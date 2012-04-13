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

#include "itkImageFileReader.h"
#include "itkImageFileWriter.h"
#include "itkBioFormatsImageIO.h"
#include "itkNrrdImageIO.h"
#include "itkLSMImageIO.h"
#include "itkTimeProbe.h"
#include <vector>
#include <iomanip>

int main(int, char * argv[])
{
  itk::MultiThreader::SetGlobalMaximumNumberOfThreads(1);

  const unsigned int dim = 3;
  typedef unsigned char PType;
  typedef itk::Image< PType, dim >    IType;
  
  // read the input image
  typedef itk::ImageFileReader< IType > ReaderType;
  ReaderType::Pointer reader = ReaderType::New();
  reader->SetFileName( argv[1] );
  itk::BioFormatsImageIO::Pointer io = itk::BioFormatsImageIO::New();
  reader->SetImageIO( io );
  // update a first time to avoid the effect of the system cache
  reader->Update();
  
//   save the image so it can be used by other readers to compare
//   typedef itk::ImageFileWriter< IType > WriterType;
//   WriterType::Pointer writer = WriterType::New();
//   writer->SetInput( reader->GetOutput() );
//   writer->SetFileName( "out.tif" );
//   writer->Update();
//   writer->SetFileName( "out.nrrd" );
//   writer->Update();

  ReaderType::Pointer reader2 = ReaderType::New();
  reader2->SetFileName( argv[1] );
  itk::BioFormatsImageIO::Pointer io2 = itk::BioFormatsImageIO::New();
  reader2->SetImageIO( io2 );
  // update a first time to avoid the effect of the system cache
  reader2->Update();
  
//   ReaderType::Pointer reader3 = ReaderType::New();
//   reader3->SetFileName( "out.tif" );
//   itk::TIFFImageIO::Pointer io3 = itk::TIFFImageIO::New();
//   reader3->SetImageIO( io3 );
//   // update a first time to avoid the effect of the system cache
//   reader3->Update();
//   
//   ReaderType::Pointer reader4 = ReaderType::New();
//   reader4->SetFileName( "out.nrrd" );
//   itk::NrrdImageIO::Pointer io4 = itk::NrrdImageIO::New();
//   reader4->SetImageIO( io4 );
//   // update a first time to avoid the effect of the system cache
//   reader4->Update();
  
  std::cout << "1IO\txIO\txTIF\txNRRD" << std::endl;

  itk::TimeProbe time;
  itk::TimeProbe time2;
//   itk::TimeProbe time3;
//   itk::TimeProbe time4;
  for( int i=0; i<10; i++ )
    {
    reader->Modified();
    time.Start();
    reader->Update();
    time.Stop();
    
    io2 = itk::BioFormatsImageIO::New();
    reader2->SetImageIO( io2 );
    time2.Start();
    reader2->Update();
    time2.Stop();

//     io3 = itk::TIFFImageIO::New();
//     reader3->SetImageIO( io3 );
//     time3.Start();
//     reader3->Update();
//     time3.Stop();
//     
//     io4 = itk::NrrdImageIO::New();
//     reader4->SetImageIO( io4 );
//     time4.Start();
//     reader4->Update();
//     time4.Stop();
    
  std::cout << std::setprecision(3)
    << time.GetMeanTime() << "\t" 
    << time2.GetMeanTime() << "\t" 
//     << time3.GetMeanTime() << "\t" 
//     << time4.GetMeanTime() << "\t" 
    << std::endl;  
    }

  return 0;
}

