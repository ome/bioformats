//
// itkBioFormatsImageIO.cxx
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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

See slicer-license.txt for licensing information.

For more information about the ITK Plugin IO mechanism, see:
http://www.itk.org/Wiki/Plugin_IO_mechanisms
*/

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

namespace itk
{

  BioFormatsImageIO::BioFormatsImageIO()
  {
    PRINT("BioFormatsImageIO constuctor");
    m_PixelType = SCALAR;
    m_FileType = Binary;
    m_NumberOfComponents = 1; // NB: Always split channels for now.

    // initialize the Java virtual machine
    PRINT("Creating JVM...");
    StaticVmLoader loader(JNI_VERSION_1_4);
    OptionList list;
    // NB: Use full path for now, to ensure Java libraries can be found.
    std::string jarPath =
      "/home/curtis/src/itk/InsightToolkit-3.10.2/build/bin";
    list.push_back(jace::ClassPath(
      jarPath + "/jace-runtime.jar:" +
      jarPath + "/bio-formats.jar:" +
      jarPath + "/loci_tools.jar"
    ));
    list.push_back(jace::CustomOption("-Xcheck:jni"));
    list.push_back(jace::CustomOption("-Xmx256m"));
    //list.push_back(jace::CustomOption("-verbose:jni"));
    jace::helper::createVm(loader, list, false);
    PRINT("JVM created.");

    PRINT("Creating Bio-Formats objects...");
    reader = new ChannelSeparator;
    writer = new ImageWriter;
    PRINT("Created reader and writer.");
  }

  BioFormatsImageIO::~BioFormatsImageIO()
  {
    delete reader;
    delete writer;
  }

  bool
  BioFormatsImageIO::CanReadFile(const char* FileNameToRead)
  {
    PRINT("BioFormatsImageIO::CanReadFile: FileNameToRead=" << FileNameToRead);
    std::string filename(FileNameToRead);

    if ( filename == "" )
    {
      itkExceptionMacro(<<"A FileName must be specified.");
      return false;
    }

    // call Bio-Formats to check file type

    // NB: Calling reader->isThisType() causes a symbol lookup error on:
    //     _ZNK4jace5proxy5types8JBooleancvaEv
    /*
    bool isType = reader->isThisType(filename);
    PRINT("BioFormatsImageIO::CanReadFile: isType=" << isType);
    return isType;
    */

    return true;
  }

  void
  BioFormatsImageIO::ReadImageInformation()
  {
    PRINT("BioFormatsImageIO::ReadImageInformation: m_FileName=" << m_FileName);

    // attach OME metadata object
    IMetadata omeMeta = MetadataTools::createOMEXMLMetadata();
    reader->setMetadataStore(omeMeta);

    // initialize dataset
    PRINT("Initializing...");
    reader->setId(m_FileName);
    PRINT("Initialized.");

    int seriesCount = reader->getSeriesCount();
    PRINT("\tSeriesCount = " << seriesCount);

    // get byte order

    // NB: Calling reader->isLittleEndian() causes a symbol lookup error on:
    //     _ZNK4jace5proxy5types8JBooleancvaEv
    /*
    bool little = reader->isLittleEndian();
    if (little) SetByteOrderToLittleEndian();
    else SetByteOrderToBigEndian();
    */
    SetByteOrderToBigEndian(); // m_ByteOrder

    // get component type

    // NB: Calling FormatTools::UINT8() causes a symbol lookup error on:
    //     _ZN4jace6helper15deleteGlobalRefEP10_Jv_JNIEnvP9__jobject
    int pixelType = reader->getPixelType();
    int bpp = FormatTools::getBytesPerPixel(pixelType);
    PRINT("\tBytes per pixel = " << bpp);
    /*
    IOComponentType componentType;
    if (pixelType == FormatTools::UINT8())
      componentType = UCHAR;
    else if (pixelType == FormatTools::INT8())
      componentType = CHAR;
    if (pixelType == FormatTools::UINT16())
      componentType = USHORT;
    else if (pixelType == FormatTools::INT16())
      componentType = SHORT;
    if (pixelType == FormatTools::UINT32())
      componentType = UINT;
    else if (pixelType == FormatTools::INT32())
      componentType = INT;
    if (pixelType == FormatTools::FLOAT())
      componentType = FLOAT;
    else if (pixelType == FormatTools::DOUBLE())
      componentType = DOUBLE;
    else
      componentType = UNKNOWNCOMPONENTTYPE;
    SetComponentType(componentType); // m_ComponentType
    if (componentType == UNKNOWNCOMPONENTTYPE)
    {
      itkExceptionMacro(<<"Unknown pixel type: " << pixelType);
    }
    */

    // TEMP - for now we assume 8-bit unsigned integer data
    SetComponentType(UCHAR);

    // get pixel resolution and dimensional extents
    int sizeX = reader->getSizeX();
    int sizeY = reader->getSizeY();
    int sizeZ = reader->getSizeZ();
    int sizeC = reader->getSizeC();
    int sizeT = reader->getSizeT();

    // NB: ITK does not seem to provide a facility for multidimensional
    //     data beyond multichannel 3D? Need to investigate further.

    int imageCount = reader->getImageCount();
    SetNumberOfDimensions(imageCount > 1 ? 3 : 2);
    m_Dimensions[0] = sizeX;
    m_Dimensions[1] = sizeY;
    if (imageCount > 1) m_Dimensions[2] = imageCount;

    PRINT("\tSizeX = " << sizeX);
    PRINT("\tSizeY = " << sizeY);
    PRINT("\tSizeZ = " << sizeZ);
    PRINT("\tSizeC = " << sizeC);
    PRINT("\tSizeT = " << sizeT);
    PRINT("\tImage Count = " << imageCount);

    // get physical resolution

    // NB: Jace interface proxies do not inherit from superinterfaces.
    //     E.g., IMetadata does not possess methods from MetadataRetrieve.
    //     Need to find a way around this, or improve Jace.
    //float physX = omeMeta.getDimensionsPhysicalSizeX(0, 0);
    //float physY = omeMeta.getDimensionsPhysicalSizeY(0, 0);
    //m_Spacing[0] = physX;
    //m_Spacing[1] = physY;
    //if (imageCount > 1) m_Spacing[2] = 1;

    //PRINT("\tPhysicalSizeX = " << physX);
    //PRINT("\tPhysicalSizeY = " << physY);
  }

  void
  BioFormatsImageIO::Read(void* pData)
  {
    char* data = (char*) pData;
    PRINT("BioFormatsImageIO::Read");

    typedef JArray<JByte> ByteArray;

    int pixelType = reader->getPixelType();
    int bpp = FormatTools::getBytesPerPixel(pixelType);

    // check IO region to identify the planar extents desired
    ImageIORegion region = GetIORegion();
    int regionDim = region.GetImageDimension();
    int xIndex = region.GetIndex(0);
    int xCount = region.GetSize(0);
    int yIndex = region.GetIndex(1);
    int yCount = region.GetSize(1);
    int pIndex = 0, pCount = 1;
    if (regionDim > 2) {
      pIndex = region.GetIndex(2);
      pCount = region.GetSize(2);
    }
    int bytesPerSubPlane = xCount * yCount * bpp;

    PRINT("\tRegion dimension = " << regionDim);
    PRINT("\tX index = " << xIndex);
    PRINT("\tX count = " << xCount);
    PRINT("\tY index = " << yIndex);
    PRINT("\tY count = " << yCount);
    PRINT("\tPlane index = " << pIndex);
    PRINT("\tPlane count = " << pCount);
    PRINT("\tBytes per plane = " << bytesPerSubPlane);

    int p = 0;
    for (int no=pIndex; no<pIndex+pCount; no++)
    {
      PRINT("Reading image plane " <<
        (no + 1) << "/" << reader->getImageCount());
      ByteArray buf = reader->openBytes(no, xIndex, xCount, yIndex, yCount);

      // NB: Using brackets with a JArray causes a symbol lookup error on:
      //     _ZN4jace6helper12newGlobalRefEP10_Jv_JNIEnvP9__jobject
      //for (int i=0; i<bytesPerSubPlane; i++) data[p++] = buf[i];

      // TEMP - for now we populate the buffer with dummy data
      for (int i=0; i<bytesPerSubPlane; i++) data[p++] = 255 - no;
    }

    reader->close();
    PRINT("Done.");
  } // end Read function

  bool
  BioFormatsImageIO::CanWriteFile(const char* name)
  {
    PRINT("BioFormatsImageIO::CanWriteFile: name=" << name);
    std::string filename(name);

    if ( filename == "" )
    {
      itkExceptionMacro(<<"A FileName must be specified.");
      return false;
    }

    // call Bio-Formats to check file type
    ImageWriter writer;
    bool isType = writer.isThisType(filename);
    PRINT("BioFormatsImageIO::CanWriteFile: isType=" << isType);

    return isType;
  }

  void
  BioFormatsImageIO::WriteImageInformation()
  {
    PRINT("BioFormatsImageIO::WriteImageInformation");
  }

  void
  BioFormatsImageIO::Write(const void* buffer)
  {
    PRINT("BioFormatsImageIO::Write");
    // CTR TODO - implmeent Write function
  } // end Write function

} // end NAMESPACE ITK
