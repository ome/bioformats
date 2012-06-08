/*
 * #%L
 * Bio-Formats plugin for the Insight Toolkit.
 * %%
 * Copyright (C) 2010 - 2012 Insight Software Consortium, and Open Microscopy
 * Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
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
    std::string tmp;
    itk::ExposeMetaData<std::string>( imgMetaDictionary, *itKey, tmp );
    std::cout << *itKey << " ---> " << tmp << std::endl;
    //std::cout << "Metadata: " << notes1 << std::endl;
    metaString = METADATA_NOT_FOUND;
  }

  // Print out the metadata naturally contained within itkImageIOBase
  itk::ImageIORegion region = reader->GetImageIO()->GetIORegion();
  int regionDim = region.GetImageDimension();

  std::cout << "Metadata Key ---> Value pairs, from ImageIOBase:" << std::endl;

  for(int i = 0; i < regionDim; i++)
  {
    std::cout << "Dimension " << i + 1 << " Size: " << region.GetSize(i) << std::endl;
  }
  for(int i = 0; i < regionDim; i++)
  {
    std::cout << "Spacing " << i + 1 << ": " << reader->GetImageIO()->GetSpacing(i) << std::endl;
  }
  std::cout << "Byte Order: " << reader->GetImageIO()->GetByteOrderAsString(reader->GetImageIO()->GetByteOrder()) << std::endl;
  std::cout << "Pixel Stride: " << reader->GetImageIO()->GetPixelStride() << std::endl;
  std::cout << "Pixel Type: " << reader->GetImageIO()->GetPixelTypeAsString(reader->GetImageIO()->GetPixelType()) << std::endl;
  std::cout << "Image Size (in pixels): " << reader->GetImageIO()->GetImageSizeInPixels() << std::endl;
  std::cout << "Pixel Type: " << reader->GetImageIO()->GetComponentTypeAsString(reader->GetImageIO()->GetComponentType()) << std::endl;
  std::cout << "RGB Channel Count: " << reader->GetImageIO()->GetNumberOfComponents() << std::endl;
  std::cout << "Number of Dimensions: " << reader->GetImageIO()->GetNumberOfDimensions() << std::endl;
}
