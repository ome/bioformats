//
// itkBioFormatsImageIO.cxx
//

/*
OME Bio-Formats ITK plugin for calling Bio-Formats from the Insight Toolkit.
Copyright (c) 2008-@year@, UW-Madison LOCI.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the UW-Madison LOCI nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY UW-MADISON LOCI ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL UW-MADISON LOCI BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
IMPORTANT NOTE: Although this software is distributed according to a
"BSD-style" license, it requires the OME Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.
*/

/*
Adapted from the Slicer3 project: http://www.slicer.org/
http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/

See slicer-license.txt for Slicer3's licensing information.

For more information about the ITK Plugin IO mechanism, see:
http://www.itk.org/Wiki/Plugin_IO_mechanisms
*/

// Special thanks to Alex Gouaillard, Sebastien Barre,
// Luis Ibanez and Jim Miller for fixes and suggestions.

#include <fstream>

#include "itkBioFormatsImageIO.h"
#include "itkIOCommon.h"
#include "itkExceptionObject.h"
#include "itkByteSwapper.h"
#include "itkMetaDataObject.h"

#include <vnl/vnl_matrix.h>
#include <vnl/vnl_vector.h>
#include <vnl/vnl_cross.h>

#include <cmath>

#include <stdio.h>
#include <stdlib.h>

#if defined (_WIN32)
#define PATHSTEP ';'
#define SLASH '\\'
#else
#define PATHSTEP ':'
#define SLASH '/'
#endif

//--------------------------------------
//
// BioFormatsImageIO
//

namespace itk {

  BioFormatsImageIO::BioFormatsImageIO() {
    DebugOn(); // NB: For debugging.
    itkDebugMacro("BioFormatsImageIO constructor");
    m_FileType = Binary;

    try {
      jace::StaticVmLoader* tmpLoader = (jace::StaticVmLoader*)jace::helper::getVmLoader();
      if(tmpLoader == NULL) {

        // initialize the Java virtual machine
        itkDebugMacro("Creating JVM...");
        jace::OptionList list;
        jace::StaticVmLoader loader(JNI_VERSION_1_4);

        const char name[] = "ITK_AUTOLOAD_PATH";
        const char* namePtr;
        namePtr = name;
        char* path;
        path = getenv(name);
        std::string dir("");
        if( path != NULL) {
          dir.assign(path);
        }

        if( dir.at(dir.length() - 1) != SLASH ) {
          dir.append(1,SLASH);
        } 

        list.push_back(jace::ClassPath(
        dir+"jace-runtime.jar"+PATHSTEP+dir+"bio-formats.jar"+PATHSTEP+dir+"loci_tools.jar"
        ));
        list.push_back(jace::CustomOption("-Xcheck:jni"));
        list.push_back(jace::CustomOption("-Xmx256m"));
        list.push_back(jace::CustomOption("-Djava.awt.headless=true"));
        list.push_back(jace::CustomOption("-Djava.library.path=" + dir));
        //list.push_back(jace::CustomOption("-verbose"));
        //list.push_back(jace::CustomOption("-verbose:jni"));
        jace::helper::createVm(loader, list, false);
        itkDebugMacro("JVM created.");
      }
    }
    catch (JNIException& jniException) {
      itkDebugMacro("Exception creating JVM: " << jniException.what());
    }

    try {
      itkDebugMacro("Creating Bio-Formats objects...");
      reader = imageReader = new ImageReader;
      reader = channelFiller = new ChannelFiller(*reader);
      //reader = channelSeparator = new ChannelSeparator(reader);
      channelSeparator = NULL;
      reader = channelMerger = new ChannelMerger(*reader);
      writer = new ImageWriter;
      itkDebugMacro("Created reader and writer.");
    }
    catch (Exception& e) {
     itkDebugMacro("A Java error occurred: " << DebugTools::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro("A JNI error occurred: " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred: " << e.what());
    }
  } // end constructor

  BioFormatsImageIO::~BioFormatsImageIO() {
    if (imageReader != NULL) delete imageReader;
    imageReader = NULL;
    if (channelFiller != NULL) delete channelFiller;
    channelFiller = NULL;
    if (channelSeparator != NULL) delete channelSeparator;
    channelSeparator = NULL;
    if (channelMerger != NULL) delete channelMerger;
    channelMerger = NULL;
    if (writer != NULL) delete writer;
    writer = NULL;
  } // end destructor

  bool BioFormatsImageIO::CanReadFile(const char* FileNameToRead) {
    itkDebugMacro(
      "BioFormatsImageIO::CanReadFile: FileNameToRead = " << FileNameToRead);
    std::string filename(FileNameToRead);

    if (filename == "") {
      itkDebugMacro("A file name must be specified.");
      return false;
    }

    bool isType = 0;
    try {
      // call Bio-Formats to check file type
      isType = ((IFormatHandler*) reader)->isThisType(filename);
      itkDebugMacro("isType = " << isType);
    }
    catch (Exception& e) {
     itkDebugMacro("A Java error occurred: " << DebugTools::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro("A JNI error occurred: " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred: " << e.what());
    }
    return isType;
  } // end CanReadFile function

  void BioFormatsImageIO::ReadImageInformation() {
    itkDebugMacro(
      "BioFormatsImageIO::ReadImageInformation: m_FileName = " << m_FileName);

    try {
      // attach OME metadata object
      IMetadata omeMeta = MetadataTools::createOMEXMLMetadata();
      reader->setMetadataStore(omeMeta);

      // initialize dataset
      itkDebugMacro("Initializing...");
      reader->setId(m_FileName);
      itkDebugMacro("Initialized.");

      int seriesCount = reader->getSeriesCount();
      itkDebugMacro("Series count = " << seriesCount);

      // set ITK byte order
      bool little = reader->isLittleEndian();
      if (little) SetByteOrderToLittleEndian(); // m_ByteOrder
      else SetByteOrderToBigEndian(); // m_ByteOrder

      // set ITK component type
      int pixelType = reader->getPixelType();
      int bpp = FormatTools::getBytesPerPixel(pixelType);
      itkDebugMacro("Bytes per pixel = " << bpp);
      IOComponentType itkComponentType;
      if (pixelType == FormatTools::UINT8())
        itkComponentType = UCHAR;
      else if (pixelType == FormatTools::INT8())
        itkComponentType = CHAR;
      else if (pixelType == FormatTools::UINT16())
        itkComponentType = USHORT;
      else if (pixelType == FormatTools::INT16())
        itkComponentType = SHORT;
      else if (pixelType == FormatTools::UINT32())
        itkComponentType = UINT;
      else if (pixelType == FormatTools::INT32())
        itkComponentType = INT;
      else if (pixelType == FormatTools::FLOAT())
        itkComponentType = FLOAT;
      else if (pixelType == FormatTools::DOUBLE())
        itkComponentType = DOUBLE;
      else
        itkComponentType = UNKNOWNCOMPONENTTYPE;
      SetComponentType(itkComponentType); // m_ComponentType
      if (itkComponentType == UNKNOWNCOMPONENTTYPE)
      {
        itkDebugMacro("Unknown pixel type: " << pixelType);
      }

      // get pixel resolution and dimensional extents
      int sizeX = reader->getSizeX();
      int sizeY = reader->getSizeY();
      int sizeZ = reader->getSizeZ();
      int sizeC = reader->getSizeC();
      int sizeT = reader->getSizeT();
      int effSizeC = reader->getEffectiveSizeC();
      int rgbChannelCount = reader->getRGBChannelCount();
      int imageCount = reader->getImageCount();

      itkDebugMacro("Dimensional extents:" << std::endl
        << "\tSizeX = " << sizeX << std::endl
        << "\tSizeY = " << sizeY << std::endl
        << "\tSizeZ = " << sizeZ << std::endl
        << "\tSizeC = " << sizeC << std::endl
        << "\tSizeT = " << sizeT << std::endl
        << "\tRGB Channel Count = " << rgbChannelCount << std::endl
        << "\tEffective SizeC = " << effSizeC << std::endl
        << "\tImage Count = " << imageCount);

      // NB: Always return 5D, to be unambiguous.
      int numDims = 5;
      /*
      int numDims = 2; // X and Y
      if (sizeZ > 1) numDims++; // multiple focal planes
      if (sizeT > 1) numDims++; // multiple time points
      if (effSizeC > 1) numDims++; // multiple independent channels
      */

      SetNumberOfDimensions(numDims);
      m_Dimensions[0] = sizeX;
      m_Dimensions[1] = sizeY;
      /*
      int dim = 2;
      if (sizeZ > 1) m_Dimensions[dim++] = sizeZ;
      if (sizeT > 1) m_Dimensions[dim++] = sizeT;
      if (effSizeC > 1) m_Dimensions[dim++] = effSizeC;
      */
      m_Dimensions[2] = sizeZ;
      m_Dimensions[3] = sizeT;
      m_Dimensions[4] = effSizeC;

      // set ITK pixel type
      IOPixelType itkPixelType;
      if (rgbChannelCount == 1)
        itkPixelType = SCALAR;
      else if (rgbChannelCount == 3)
        itkPixelType = RGB;
      else
        itkPixelType = VECTOR;
      SetPixelType(itkPixelType); // m_PixelType
      SetNumberOfComponents(rgbChannelCount); // m_NumberOfComponents

      // get physical resolution
      double physX = 1, physY = 1, physZ = 1, timeIncrement = 1;
      // CTR - avoid invalid memory access error on some systems (OS X 10.5)
      //MetadataRetrieve retrieve = MetadataTools::asRetrieve(omeMeta);
      //physX = retrieve.getPixelsPhysicalSizeX(0).doubleValue();
      //physY = retrieve.getPixelsPhysicalSizeY(0).doubleValue();
      //physZ = retrieve.getPixelsPhysicalSizeZ(0).doubleValue();
      //timeIncrement = retrieve.getPixelsTimeIncrement(0).doubleValue();
      m_Spacing[0] = physX;
      m_Spacing[1] = physY;
      // TODO: verify m_Spacing.length > 2
      if (imageCount > 1) m_Spacing[2] = physZ;
      m_Spacing[3] = timeIncrement;

      itkDebugMacro("Physical resolution = " << physX << " x " << physY
        << " x " << physZ << " x " << timeIncrement);
    }
    catch (Exception& e) {
      itkDebugMacro("A Java error occurred: " << DebugTools::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro("A JNI error occurred: " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred: " << e.what());
    }
  } // end ReadImageInformation function

  void BioFormatsImageIO::Read(void* pData) {
    itkDebugMacro("BioFormatsImageIO::Read");

    try {
      int pixelType = reader->getPixelType();
      int bpp = FormatTools::getBytesPerPixel(pixelType);
      int rgbChannelCount = reader->getRGBChannelCount();

      itkDebugMacro("Pixel type:" << std::endl
        << "Pixel type code = " << pixelType << std::endl
        << "Bytes per pixel = " << bpp << std::endl
        << "RGB channel count = " << rgbChannelCount);

      // check IO region to identify the planar extents desired
      ImageIORegion region = GetIORegion();
      int regionDim = region.GetImageDimension();
      int xStart = 0, xCount = 1;
      int yStart = 0, yCount = 1;
      int zStart = 0, zCount = 1;
      int tStart = 0, tCount = 1;
      int cStart = 0, cCount = 1;

      //int sizeZ = reader->getSizeZ();
      //int sizeT = reader->getSizeT();
      //int effSizeC = reader->getEffectiveSizeC();

      int xIndex = 0, yIndex = 1, zIndex = 2, tIndex = 3, cIndex = 4;
      /*  Currently unnecessary, as images are assumed to be 5D
      if (sizeZ == 1) {
        zIndex = -1;
        tIndex--;
        cIndex--;
      }
      if (sizeT == 1) {
        tIndex = -1;
        cIndex--;
      }
      if (effSizeC == 1) {
        cIndex = -1;
      }
      */
      for (int dim = 0; dim < regionDim; dim++) {
        int index = region.GetIndex(dim);
        int size = region.GetSize(dim);
        if (dim == xIndex) {
          xStart = index;
          xCount = size;
        }
        else if (dim == yIndex) {
          yStart = index;
          yCount = size;
        }
        else if (dim == zIndex) {
          zStart = index;
          zCount = size;
        }
        else if (dim == tIndex) {
          tStart = index;
          tCount = size;
        }
        else if (dim == cIndex) {
          cStart = index;
          cCount = size;
        }
      }
      int bytesPerPlane = xCount * yCount * bpp * rgbChannelCount;
      bool isInterleaved = reader->isInterleaved();

      itkDebugMacro("Region extents:" << std::endl
        << "\tRegion dimension = " << regionDim << std::endl
        << "\tX: start = " << xStart << ", count = " << xCount << std::endl
        << "\tY: start = " << yStart << ", count = " << yCount << std::endl
        << "\tZ: start = " << zStart << ", count = " << zCount << std::endl
        << "\tT: start = " << tStart << ", count = " << tCount << std::endl
        << "\tC: start = " << cStart << ", count = " << cCount << std::endl
        << "\tBytes per plane = " << bytesPerPlane << std::endl
        << "\tIsInterleaved = " << isInterleaved);


      int imageCount = reader->getImageCount();

      // allocate temporary array
      bool canDoDirect = (rgbChannelCount == 1 || isInterleaved);
      jbyte* tmpData = NULL;
      if (!canDoDirect) tmpData = new jbyte[bytesPerPlane];

      jbyte* jData = (jbyte*) pData;
      ByteArray buf(bytesPerPlane); // pre-allocate buffer
      for (int c=cStart; c<cStart+cCount; c++) {
        for (int t=tStart; t<tStart+tCount; t++) {
          for (int z=zStart; z<zStart+zCount; z++) {
            int no = reader->getIndex(z, c, t);
            itkDebugMacro("Reading image plane " << no + 1
              << " (Z=" << z << ", T=" << t << ", C=" << c << ")"
              << " of " << imageCount << " available planes)");
            reader->openBytes(no, buf, xStart, yStart, xCount, yCount);

            JNIEnv* env = jace::helper::attach();
            jbyteArray jArray = static_cast<jbyteArray>(buf.getJavaJniArray());
            if (canDoDirect) {
              env->GetByteArrayRegion(jArray, 0, bytesPerPlane, jData);
            }
            else {
              // need to reorganize byte array after copy
              env->GetByteArrayRegion(jArray, 0, bytesPerPlane, tmpData);

              // reorganize elements
              int pos = 0;
              for (int x=0; x<xCount; x++) {
                for (int y=0; y<yCount; y++) {
                  for (int i=0; i<rgbChannelCount; i++) {
                    for (int b=0; b<bpp; b++) {
                      int index = yCount * (xCount * (rgbChannelCount * b + i) + x) + y;
                      jData[pos++] = tmpData[index];
                    }
                  }
                }
              }
            }
            jData += bytesPerPlane;
          }
        }
      }

      // delete temporary array
      if (tmpData != NULL) {
        delete tmpData;
        tmpData = NULL;
      }

      ((IFormatHandler*)reader)->close();
    }
    catch (Exception& e) {
      itkDebugMacro("A Java error occurred: " << DebugTools::getStackTrace(e));
    }	
    catch (JNIException& jniException) {
      itkDebugMacro(
        "A JNI error occurred: " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred: " << e.what());
    }
    itkDebugMacro("Done.");
  } // end Read function

  bool BioFormatsImageIO::CanWriteFile(const char* name) {
    itkDebugMacro("BioFormatsImageIO::CanWriteFile: name = " << name);
    std::string filename(name);

    if (filename == "") {
      itkDebugMacro("A FileName must be specified.");
      return false;
    }

    bool isType = 0;
    try {
      // call Bio-Formats to check file type
      ImageWriter writer;
      isType = writer.isThisType(filename);
      itkDebugMacro("isType = " << isType);
    }
    catch (Exception& e) {
      itkDebugMacro("A Java error occurred: " << DebugTools::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro("A JNI error occurred: " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred: " << e.what());
    }
    return isType;
  } // end CanWriteFile function

  void BioFormatsImageIO::WriteImageInformation() {
    itkDebugMacro("BioFormatsImageIO::WriteImageInformation");
    // NB: Nothing to do.
  } // end WriteImageInformation function

  void BioFormatsImageIO::Write(const void* buffer) {
    itkDebugMacro("BioFormatsImageIO::Write");
    // CTR TODO - implement Write function
  } // end Write function

} // end namespace itk
