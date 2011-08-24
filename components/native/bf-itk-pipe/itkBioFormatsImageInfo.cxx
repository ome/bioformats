//
// itkBioFormatsImageInfo.cxx
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

#include <iostream>
#include <vector>
#include <string>
#include "itkImageFileReader.h"
#include "itkImageFileWriter.h"
#include "itkImage.h"
#include "itkMetaDataObject.h"
#include "itkMetaDataDictionary.h"
#include "itkImageIOBase.h"
//#include "itkBioFormatsImageIO.h"

#define METADATA_NOT_FOUND "No value for this key."

int main( int argc, char * argv[] )
{
  if( argc < 2)
  {
    std::cerr << "Usage: " << argv[0] << " input\n";
    return EXIT_FAILURE;
  }

  typedef unsigned char     PixelType;
  const unsigned int        Dimension = 3;

  typedef itk::Image< PixelType, Dimension >  ImageType;

  typedef itk::ImageFileReader<ImageType> ReaderType;

  ReaderType::Pointer reader = ReaderType::New();
  ImageType::Pointer img;

  reader->SetFileName(argv[1]);
  reader->Update();
  img = reader->GetOutput();

  std::string metaString (METADATA_NOT_FOUND);
  itk::MetaDataDictionary imgMetaDictionary = img->GetMetaDataDictionary();
  std::vector<std::string> imgMetaKeys = imgMetaDictionary.GetKeys();
  std::vector<std::string>::const_iterator itKey = imgMetaKeys.begin();

  // Iterate through the keys and print their paired values
  std::cout << "Metadata Key ---> Value pairs, from dictionary:" << std::endl;
  for(; itKey != imgMetaKeys.end(); ++itKey)
  {
    //TODO: Need to look up, in the MetaDataDictionary, the value paired to each key (*itKey)
    std::string tmp;
    itk::ExposeMetaData<std::string>( imgMetaDictionary, *itKey, tmp );
    std::cout << *itKey << " ---> " << tmp << std::endl;
    //std::cout << "Metadata: " << notes1 << std::endl;
    metaString = METADATA_NOT_FOUND;
  }

  // Print out the metadata naturally contained within itkImageIOBase
  std::cout << "Metadata Key ---> Value pairs, from ImageIOBase:" << std::endl;
  std::cout << "Spacing: " << reader->GetImageIO()->GetSpacing(0) << std::endl;
  std::cout << "Byte Order: " << reader->GetImageIO()->GetByteOrderAsString(reader->GetImageIO()->GetByteOrder()) << std::endl;
  std::cout << "Pixel Stride: " << reader->GetImageIO()->GetPixelStride() << std::endl;
  std::cout << "Pixel Type: " << reader->GetImageIO()->GetPixelTypeAsString(reader->GetImageIO()->GetPixelType()) << std::endl;
  std::cout << "Image Size (in pixels): " << reader->GetImageIO()->GetImageSizeInPixels() << std::endl;
  std::cout << "Component Type: " << reader->GetImageIO()->GetComponentTypeAsString(reader->GetImageIO()->GetComponentType()) << std::endl;
  std::cout << "Number of Components: " << reader->GetImageIO()->GetNumberOfComponents() << std::endl;
  std::cout << "Number of Dimensions: " << reader->GetImageIO()->GetNumberOfDimensions() << std::endl;
}
