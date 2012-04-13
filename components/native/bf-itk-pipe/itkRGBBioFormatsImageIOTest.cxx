/*
 * #%L
 * Bio-Formats plugin for the Insight Toolkit.
 * %%
 * Copyright (C) 2010 - 2012 Insight Software Consortium, Board of Regents
 * of the University of Wisconsin-Madison, Glencoe Software, Inc., and
 * University of Dundee.
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

#if defined(_MSC_VER)
#pragma warning ( disable : 4786 )
#endif

#include <iostream>
#include "itkBioFormatsImageIO.h"
#include "itkImageFileReader.h"
#include "itkImageFileWriter.h"
#include "itkImage.h"
#include "itkRGBPixel.h"
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

  typedef itk::RGBPixel<unsigned char>       PixelType;
  const unsigned int                         Dimension = 3;

  typedef itk::Image< PixelType, Dimension >   ImageType;

  typedef itk::ImageFileReader<ImageType> ReaderType;

  itk::BioFormatsImageIO::Pointer io = itk::BioFormatsImageIO::New();

  io->DebugOn();

  ReaderType::Pointer reader = ReaderType::New();
  std::cout << "reader->GetUseStreaming(): " << reader->GetUseStreaming() << std::endl;


  reader->SetFileName(argv[1]);
  reader->SetImageIO(io);

  typedef itk::StreamingImageFilter<ImageType, ImageType> StreamingFilter;
  StreamingFilter::Pointer streamer = StreamingFilter::New();
  streamer->SetInput( reader->GetOutput() );
  //this call to setnumberofstreamdivisions is causing a seg fault
  // should this be argv[2] ?
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
