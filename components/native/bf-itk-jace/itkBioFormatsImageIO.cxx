/*
 * #%L
 * Bio-Formats plugin for the Insight Toolkit.
 * %%
 * Copyright (C) 2009 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * 
 * ----------------------------------------------------------------
 * Adapted from the Slicer3 project: http://www.slicer.org/
 * http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/
 * 
 * See slicer-license.txt for Slicer3's licensing information.
 * 
 * For more information about the ITK Plugin IO mechanism, see:
 * http://www.itk.org/Wiki/Plugin_IO_mechanisms
 * #L%
 */

// Special thanks to Alex Gouaillard, Sebastien Barre,
// Luis Ibanez and Jim Miller for fixes and suggestions.

#include <fstream>
#include <sstream>

#include "itkBioFormatsImageIO.h"
#include "itkIOCommon.h"
#include "itkExceptionObject.h"
#include "itkByteSwapper.h"
#include "itkMetaDataObject.h"
#include "javaTools.h"

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
    const char name[] = "ITK_AUTOLOAD_PATH";
    const char* namePtr;
    namePtr = name;
    char* path;
    path = getenv(name);
    std::string dir("");
    if( path != NULL) {
      dir.assign(path);
    }
    itkDebugMacro("Creating JVM...");
    JavaTools::createJVM(std::string(dir+"loci_tools.jar"+PATHSTEP+dir+"jace-runtime.jar"+PATHSTEP+dir+"bio-formats.jar"));
    itkDebugMacro("JVM Created");

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
      if (little)
      {
        SetByteOrderToLittleEndian(); // m_ByteOrder
        itkDebugMacro("Little endian and m_ByteOrder is: " << m_ByteOrder);
      } 
      else
      {
        SetByteOrderToBigEndian(); // m_ByteOrder
        itkDebugMacro("Big endian and m_ByteOrder is: " << m_ByteOrder);
      }

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

      MetadataRetrieve retrieve = MetadataTools::asRetrieve(omeMeta);

      itkDebugMacro("Getting Physical Pixel Sizes...");
      PositiveFloat metaX = retrieve.getPixelsPhysicalSizeX(0);
      PositiveFloat metaY = retrieve.getPixelsPhysicalSizeY(0);
      PositiveFloat metaZ = retrieve.getPixelsPhysicalSizeZ(0);
      Double metaT = retrieve.getPixelsTimeIncrement(0);

      physX = decode(metaX);
      physY = decode(metaY);
      physZ = decode(metaZ);
      timeIncrement = decode(metaT);

      m_Spacing[0] = physX;
      m_Spacing[1] = physY;
      m_Spacing[2] = physZ;
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
    try{
    const int xIndex = 0, yIndex = 1, zIndex = 2, cIndex = 3, tIndex = 4;
    ImageIORegion region = GetIORegion();
    Indent ind;
    int regionDim = region.GetImageDimension();
    itkDebugMacro("dim0: " << m_Dimensions[0] << std::endl
               << "dim1: " << m_Dimensions[1] << std::endl
               << "dim2: " << m_Dimensions[2] << std::endl
               << "dim3: " << m_Dimensions[3] << std::endl
               << "dim4: " << m_Dimensions[4] << std::endl)
    IMetadata meta = MetadataTools::createOMEXMLMetadata();
    meta.createRoot();
    meta.setImageID("Image:0" , 0);
    meta.setPixelsID("Pixels:0", 0);
    jace::proxy::java::lang::Boolean* bHelp;
    if (m_ByteOrder)
      meta.setPixelsBinDataBigEndian(bHelp->valueOf("false"), 0, 0);
    else
      meta.setPixelsBinDataBigEndian(bHelp->valueOf("true"), 0, 0);
    meta.setPixelsDimensionOrder(DimensionOrder::XYZCT(), 0);
    meta.setPixelsType(PixelType::fromString(FormatTools::getPixelTypeString(m_PixelType)) , 0);
    for(int i = 0; i < regionDim; i++)
    {
      int tmp = m_Dimensions[i] > 0 ? m_Dimensions[i] : 1;
      switch(i)
      {
        case xIndex: meta.setPixelsSizeX(PositiveInteger(Integer(tmp)) , 0);
        break;
        case yIndex: meta.setPixelsSizeY(PositiveInteger(Integer(tmp)) , 0);
        break;
        case zIndex: meta.setPixelsSizeZ(PositiveInteger(Integer(tmp)) , 0);
        break;
        case cIndex: meta.setPixelsSizeC(PositiveInteger(Integer(tmp)) , 0);
        break;
        case tIndex: meta.setPixelsSizeT(PositiveInteger(Integer(tmp)) , 0);
        break;
      }
    }
    for(int i = regionDim; i < 5; i++)
    {
      switch(i)
      {
        case xIndex: meta.setPixelsSizeX(PositiveInteger(Integer(1)) , 0);
        break;
        case yIndex: meta.setPixelsSizeY(PositiveInteger(Integer(1)) , 0);
        break;
        case zIndex: meta.setPixelsSizeZ(PositiveInteger(Integer(1)) , 0);
        break;
        case cIndex: meta.setPixelsSizeC(PositiveInteger(Integer(1)) , 0);
        break;
        case tIndex: meta.setPixelsSizeT(PositiveInteger(Integer(1)) , 0);
        break;
      }
    }
    meta.setChannelID("Channel:0:0" , 0, 0);
    meta.setChannelSamplesPerPixel(PositiveInteger(Integer(m_NumberOfComponents)) , 0, 0);

    writer->setMetadataRetrieve(meta);
    itkDebugMacro("Setting id to: " << m_FileName);
    writer->setId(m_FileName);
    itkDebugMacro("Id set.");

    int bpp = FormatTools::getBytesPerPixel(m_PixelType);
    int xStart = 0, xCount = 1;
    int yStart = 0, yCount = 1;
    int zStart = 0, zCount = 1;
    int cStart = 0, cCount = 1;
    int tStart = 0, tCount = 1;

    //bool isInterleaved = writer->isInterleaved();
    int rgbChannelCount = m_NumberOfComponents;

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

    jbyte* jData = (jbyte*) buffer;

    itkDebugMacro("Writing data...");

    int no = 0;
    ByteArray buf(bytesPerPlane);
    for (int c=cStart; c<cStart+cCount; c++) {
      for (int t=tStart; t<tStart+tCount; t++) {
        for (int z=zStart; z<zStart+zCount; z++) {
          itkDebugMacro("Writing region at c: " << c << " t: " << t << " z: " << z << " ...");
          JNIEnv* env = jace::helper::attach();
          jbyteArray jArray = static_cast<jbyteArray>(buf.getJavaJniArray());
          env->SetByteArrayRegion(jArray, 0, bytesPerPlane, jData);
          writer->saveBytes(no++, jArray, xStart, yStart, xCount, yCount);
          jData += bytesPerPlane;
          itkDebugMacro("Done writing region.");
        }
      }
    }
    itkDebugMacro("Done writing data.");

    writer->close();
    }
    catch(Exception& e) {
      itkDebugMacro("A Java error occurred: " << DebugTools::getStackTrace(e));
    }
    catch (JNIException& jniException) {
      itkDebugMacro("A JNI error occurred: " << jniException.what());
    }
    catch (std::exception& e) {
      itkDebugMacro("A C++ error occurred: " << e.what());
    }
    itkDebugMacro("Done writing image.");
  } // end Write function

  double BioFormatsImageIO::decode(PositiveFloat pf) {
    if (pf.isNull()) return 1;
    jace::proxy::java::lang::Object tmpo = pf.getValue();
    jobject obj = tmpo.getJavaJniObject();
    Double val (obj);
    return decode(val);
  }

  double BioFormatsImageIO::decode(Double d) {
    if (d.isNull() || d.isNaN()) return 1;
    return d.doubleValue();
  }


} // end namespace itk
