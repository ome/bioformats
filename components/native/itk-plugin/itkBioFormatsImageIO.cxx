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

namespace itk
{

  BioFormatsImageIO::BioFormatsImageIO()
  {
    DebugOn(); // NB: For debugging.
    itkDebugMacro(<<"BioFormatsImageIO constuctor");
    m_FileType = Binary;

    // initialize the Java virtual machine
    itkDebugMacro(<<"Creating JVM...");
    StaticVmLoader loader(JNI_VERSION_1_4);
    OptionList list;
    list.push_back(jace::ClassPath(
      "jace-runtime.jar:bio-formats.jar:loci_tools.jar"
    ));
    list.push_back(jace::CustomOption("-Xcheck:jni"));
    list.push_back(jace::CustomOption("-Xmx256m"));
    //list.push_back(jace::CustomOption("-verbose"));
    //list.push_back(jace::CustomOption("-verbose:jni"));
    jace::helper::createVm(loader, list, false);
    itkDebugMacro(<<"JVM created.");

    itkDebugMacro(<<"Creating Bio-Formats objects...");
    reader = new ChannelFiller;
    writer = new ImageWriter;
    itkDebugMacro(<<"Created reader and writer.");
  }

  BioFormatsImageIO::~BioFormatsImageIO()
  {
    delete reader;
    delete writer;
  }

  bool
  BioFormatsImageIO::CanReadFile(const char* FileNameToRead)
  {
    itkDebugMacro(<<"BioFormatsImageIO::CanReadFile: FileNameToRead=" << FileNameToRead);
    std::string filename(FileNameToRead);

    if ( filename == "" )
    {
      itkExceptionMacro(<<"A FileName must be specified.");
      return false;
    }

    // call Bio-Formats to check file type
    bool isType = reader->isThisType(filename);
    itkDebugMacro(<<"BioFormatsImageIO::CanReadFile: isType=" << isType);
    return isType;
  }

  void
  BioFormatsImageIO::ReadImageInformation()
  {
    itkDebugMacro(<<"BioFormatsImageIO::ReadImageInformation: m_FileName=" << m_FileName);

    // attach OME metadata object
    IMetadata omeMeta = MetadataTools::createOMEXMLMetadata();
    reader->setMetadataStore(omeMeta);

    // initialize dataset
    itkDebugMacro(<<"Initializing...");
    reader->setId(m_FileName);
    itkDebugMacro(<<"Initialized.");

    int seriesCount = reader->getSeriesCount();
    itkDebugMacro(<<"\tSeriesCount = " << seriesCount);

    // set ITK byte order
    bool little = reader->isLittleEndian();
    if (little) SetByteOrderToLittleEndian(); // m_ByteOrder
    else SetByteOrderToBigEndian(); // m_ByteOrder

    // set ITK component type
    int pixelType = reader->getPixelType();
    int bpp = FormatTools::getBytesPerPixel(pixelType);
    itkDebugMacro(<<"\tBytes per pixel = " << bpp);
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
      itkExceptionMacro(<<"Unknown pixel type: " << pixelType);
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
      <<"\tSizeX = " << sizeX << std::endl
      <<"\tSizeY = " << sizeY << std::endl
      <<"\tSizeZ = " << sizeZ << std::endl
      <<"\tSizeC = " << sizeC << std::endl
      <<"\tSizeT = " << sizeT << std::endl
      <<"\tRGB Channel Count = " << rgbChannelCount << std::endl
      <<"\tEffective SizeC = " << rgbChannelCount << std::endl
      <<"\tImage Count = " << imageCount);

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

    // NB: Jace interface proxies do not inherit from superinterfaces.
    //     E.g., IMetadata does not possess methods from MetadataRetrieve.
    //     Need to find a way around this, or improve Jace.
    //float physX = omeMeta.getDimensionsPhysicalSizeX(0, 0);
    //float physY = omeMeta.getDimensionsPhysicalSizeY(0, 0);
    //m_Spacing[0] = physX;
    //m_Spacing[1] = physY;
    //if (imageCount > 1) m_Spacing[2] = 1;

    //itkDebugMacro(<<"\tPhysicalSizeX = " << physX);
    //itkDebugMacro(<<"\tPhysicalSizeY = " << physY);
  }

  void
  BioFormatsImageIO::Read(void* pData)
  {
    char* data = (char*) pData;
    itkDebugMacro(<<"BioFormatsImageIO::Read");

    typedef JArray<JByte> ByteArray;

    int pixelType = reader->getPixelType();
    int bpp = FormatTools::getBytesPerPixel(pixelType);
    int rgbChannelCount = reader->getRGBChannelCount();

    itkDebugMacro(<<"Pixel type code = " << pixelType);
    itkDebugMacro(<<"Bytes per pixel = " << bpp);

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
    int bytesPerSubPlane = xCount * yCount * bpp * rgbChannelCount;

    itkDebugMacro("Region extents:" << std::endl
      <<"\tRegion dimension = " << regionDim << std::endl
      <<"\tX index = " << xIndex << std::endl
      <<"\tX count = " << xCount << std::endl
      <<"\tY index = " << yIndex << std::endl
      <<"\tY count = " << yCount << std::endl
      <<"\tPlane index = " << pIndex << std::endl
      <<"\tPlane count = " << pCount << std::endl
      <<"\tBytes per plane = " << bytesPerSubPlane);

    int p = 0;
    ByteArray buf(bytesPerSubPlane); // pre-allocate buffer
    for (int no=pIndex; no<pIndex+pCount; no++)
    {
      int imageCount = reader->getImageCount();
      itkDebugMacro(<<"Reading image plane " << no <<
        " (" << (no - pIndex + 1) << "/" << pCount <<
        " of " << imageCount << " available planes)");
      reader->openBytes(no, buf, xIndex, yIndex, xCount, yCount);
      for (int i=0; i<bytesPerSubPlane; i++) data[p++] = buf[i];
    }

    reader->close();
    itkDebugMacro(<<"Done.");
  } // end Read function

  bool
  BioFormatsImageIO::CanWriteFile(const char* name)
  {
    itkDebugMacro(<<"BioFormatsImageIO::CanWriteFile: name=" << name);
    std::string filename(name);

    if ( filename == "" )
    {
      itkExceptionMacro(<<"A FileName must be specified.");
      return false;
    }

    // call Bio-Formats to check file type
    ImageWriter writer;
    bool isType = writer.isThisType(filename);
    itkDebugMacro(<<"BioFormatsImageIO::CanWriteFile: isType=" << isType);

    return isType;
  }

  void
  BioFormatsImageIO::WriteImageInformation()
  {
    itkDebugMacro(<<"BioFormatsImageIO::WriteImageInformation");
    // NB: Nothing to do.
  }

  void
  BioFormatsImageIO::Write(const void* buffer)
  {
    itkDebugMacro(<<"BioFormatsImageIO::Write");
    // CTR TODO - implement Write function
  } // end Write function

} // end NAMESPACE ITK
