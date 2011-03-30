/*=========================================================================
 *
 *  Copyright Insight Software Consortium
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *=========================================================================*/
#if defined(_MSC_VER)
#pragma warning ( disable : 4786 )
#endif

#include <iostream>
#include "itkBioFormatsImageIO.h"
#include "itkImageFileReader.h"
#include "itkImageFileWriter.h"
#include "itkImage.h"
#include "itkMetaDataObject.h"
#include "itkStreamingImageFilter.h"

#if defined(ITK_USE_MODULAR_BUILD)
  #define SPECIFIC_IMAGEIO_MODULE_TEST
#endif

int main( int argc, char * argv [] )
{
  if( argc < 3)
    {
    std::cerr << "Usage: " << argv[0] << " input output\n";
    return EXIT_FAILURE;
    }

  typedef unsigned char       PixelType;
  const unsigned int          Dimension = 3;

  typedef itk::Image< PixelType, Dimension >   ImageType;

  typedef itk::ImageFileReader<ImageType> ReaderType;

  itk::BioFormatsImageIO::Pointer io = itk::BioFormatsImageIO::New();

  io->DebugOn();

  ReaderType::Pointer reader = ReaderType::New();
  std::cout << "reader->GetUseStreaming(): " << reader->GetUseStreaming() << std::endl;

  reader->SetFileName("fileNotFound");
  reader->SetImageIO(io);

  // should get an exception
  bool catched = false;
  try
    {
    reader->Update();
    }
  catch (itk::ExceptionObject &e)
    {
    std::cerr << e << std::endl;
    catched = true;
    }
  if( !catched )
    {
    std::cerr << "exception not catched for wrong path" << std::endl;
    return EXIT_FAILURE;
    }

  reader->SetFileName(argv[1]);

  typedef itk::StreamingImageFilter<ImageType, ImageType> StreamingFilter;
  StreamingFilter::Pointer streamer = StreamingFilter::New();
  streamer->SetInput( reader->GetOutput() );
  streamer->SetNumberOfStreamDivisions( atoi(argv[3]) );

  itk::ImageFileWriter<ImageType>::Pointer writer;

  //
  // Use the generic writers to write the image.
  //
  writer = itk::ImageFileWriter<ImageType>::New();
  writer->SetInput(streamer->GetOutput());
  writer->SetFileName(argv[2]);

  try
    {
    streamer->Update();
    writer->Update();
    }
  catch (itk::ExceptionObject &e)
    {
    std::cerr << e << std::endl;
    return EXIT_FAILURE;
    }

  std::string notes;
  itk::ExposeMetaData<std::string>( reader->GetMetaDataDictionary(), "Recording #1 Notes", notes );
  std::cout << "Notes: " << notes << std::endl;

  return EXIT_SUCCESS;
}
