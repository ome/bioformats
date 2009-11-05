//
// itkBioFormatsImageIO.cxx
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*
Adapted from the Slicer3 project: http://www.slicer.org/
http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/

See slicer-license.txt for Slicer3's licensing information.

For more information about the ITK Plugin IO mechanism, see:
http://www.itk.org/Wiki/Plugin_IO_mechanisms
*/

// Special thanks to Alex Gouaillard, Sebastien Barre, Luis Ibanez
// and Jim Miller for fixes and suggestions.

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
      // initialize the Java virtual machine
      itkDebugMacro("Creating JVM...");
      jace::StaticVmLoader loader(JNI_VERSION_1_4);
      jace::OptionList list;
      list.push_back(jace::ClassPath(
        "jace-runtime.jar:bio-formats.jar:loci_tools.jar"
      ));
      list.push_back(jace::CustomOption("-Xcheck:jni"));
      list.push_back(jace::CustomOption("-Xmx256m"));
      list.push_back(jace::CustomOption("-Djava.awt.headless=true"));
      //list.push_back(jace::CustomOption("-verbose"));
      //list.push_back(jace::CustomOption("-verbose:jni"));
      jace::helper::createVm(loader, list, false);
      itkDebugMacro("JVM created.");
    }
    catch (JNIException& jniException) {
      itkDebugMacro("Exception creating JVM: " << jniException.what());
    }

    try {
      itkDebugMacro("Creating Bio-Formats objects...");
      reader = new ChannelFiller;
      writer = new ImageWriter;
      itkDebugMacro("Created reader and writer.");
    }
    catch (Exception& e) {
      itkDebugMacro("A Java error occurred: " << Log::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro("A JNI error occurred. " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred. " << e.what());
    }
  } // end constructor

  BioFormatsImageIO::~BioFormatsImageIO() {
    delete reader;
    delete writer;
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
      isType = reader->isThisType(filename);
      itkDebugMacro("isType = " << isType);
    }
    catch (Exception& e) {
      itkDebugMacro("A Java error occurred: " << Log::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro("A JNI error occurred. " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred. " << e.what());
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
        << "\tEffective SizeC = " << rgbChannelCount << std::endl
        << "\tImage Count = " << imageCount);

      int numDims = 2; // X and Y
      if (sizeZ > 1) numDims++; // multiple focal planes
      if (sizeT > 1) numDims++; // multiple time points
      if (effSizeC > 1) numDims++; // multiple independent channels
      SetNumberOfDimensions(numDims);
      m_Dimensions[0] = sizeX;
      m_Dimensions[1] = sizeY;
      int dim = 2;
      if (sizeZ > 1) m_Dimensions[dim++] = sizeZ;
      if (sizeT > 1) m_Dimensions[dim++] = sizeT;
      if (effSizeC > 1) m_Dimensions[dim++] = effSizeC;

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
      MetadataRetrieve retrieve = MetadataTools::asRetrieve(omeMeta);
      double physX = retrieve.getDimensionsPhysicalSizeX(0, 0).doubleValue();
      double physY = retrieve.getDimensionsPhysicalSizeY(0, 0).doubleValue();
      m_Spacing[0] = physX;
      m_Spacing[1] = physY;
      if (imageCount > 1) m_Spacing[2] = 1;

      itkDebugMacro("Physical resolution = " << physX << " x " << physY);
    }
    catch (Exception& e) {
      itkDebugMacro("A Java error occurred: " << Log::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro("A JNI error occurred. " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred. " << e.what());
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

      int sizeZ = reader->getSizeZ();
      int sizeT = reader->getSizeT();
      int effSizeC = reader->getEffectiveSizeC();

      int xIndex = 0, yIndex = 1, zIndex = 2, tIndex = 3, cIndex = 4;
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

      itkDebugMacro("Region extents:" << std::endl
        << "\tRegion dimension = " << regionDim << std::endl
        << "\tX: start = " << xStart << ", count = " << xCount << std::endl
        << "\tY: start = " << yStart << ", count = " << yCount << std::endl
        << "\tZ: start = " << zStart << ", count = " << zCount << std::endl
        << "\tT: start = " << tStart << ", count = " << tCount << std::endl
        << "\tC: start = " << cStart << ", count = " << cCount << std::endl
        << "\tBytes per plane = " << bytesPerPlane);

      int imageCount = reader->getImageCount();

      jbyte* jData = (jbyte*) pData;
      ByteArray buf(bytesPerPlane); // pre-allocate buffer
      for (int c=cStart; c<cCount; c++) {
        for (int t=tStart; t<tCount; t++) {
          for (int z=zStart; z<zCount; z++) {
            int no = reader->getIndex(z, c, t);
            itkDebugMacro("Reading image plane " << no
              << " (Z=" << z << ", T=" << t << ", C=" << c << ")"
              << " of " << imageCount << " available planes)");
            reader->openBytes(no, buf, xStart, yStart, xCount, yCount);

            // copy raw byte array
            JNIEnv* env = jace::helper::attach();
            jbyteArray jArray = static_cast<jbyteArray>(buf.getJavaJniArray());
            env->GetByteArrayRegion(jArray, 0, bytesPerPlane, jData);
            jData += bytesPerPlane;
          }
        }
      }

      reader->close();
    }
    catch (Exception& e) {
      itkDebugMacro("A Java error occurred: " << Log::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro(
        "A JNI error occurred. " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred. " << e.what());
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
      itkDebugMacro("A Java error occurred: " << Log::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro("A JNI error occurred. " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred. " << e.what());
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
