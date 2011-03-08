//
// itkBioFormatsImageIO.h
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

#ifndef H_ITK_IMAGE_IO_H
#define H_ITK_IMAGE_IO_H

// for Bio-Formats C++ bindings
#include "loci-common.h"
using jace::JNIException;
using jace::proxy::java::lang::Exception;
using jace::proxy::loci::common::DebugTools;
#include "bio-formats.h"
using jace::proxy::loci::formats::ChannelFiller;
using jace::proxy::loci::formats::ChannelMerger;
using jace::proxy::loci::formats::ChannelSeparator;
using jace::proxy::loci::formats::FormatTools;
using jace::proxy::loci::formats::IFormatHandler;
using jace::proxy::loci::formats::IFormatReader;
using jace::proxy::loci::formats::ImageReader;
using jace::proxy::loci::formats::ImageWriter;
using jace::proxy::loci::formats::MetadataTools;
using jace::proxy::loci::formats::meta::IMetadata;
using jace::proxy::loci::formats::meta::MetadataRetrieve;

#undef Byte

// STL includes

// ITK includes
#include "itkImageIOBase.h"
#include "itkMatrix.h"

#include <itk_zlib.h>

//#include "itkBioFormatsIOWin32Header.h"

namespace itk
{

  class  BioFormatsImageIO : public ImageIOBase
  {
  public:
    typedef BioFormatsImageIO       Self;
    typedef ImageIOBase             Superclass;
    typedef SmartPointer<Self>      Pointer;

    /** Method for creation through the object factory **/
    itkNewMacro(Self);
    /** RTTI (and related methods) **/
    itkTypeMacro(BioFormatsImageIO, Superclass);

    /**--------------- Read the data----------------- **/
    virtual bool CanReadFile(const char* FileNameToRead);
    /* Set the spacing and dimension information for the set file name */
    virtual void ReadImageInformation();
    /* Read the data from the disk into provided memory buffer */
    virtual void Read(void* buffer);

    /**---------------Write the data------------------**/
    virtual bool CanWriteFile(const char* FileNameToWrite);
    /* Set the spacing and dimension information for the set file name */
    virtual void WriteImageInformation();
    /* Write the data to the disk from the provided memory buffer */
    virtual void Write(const void* buffer);

  protected:
    BioFormatsImageIO();
    ~BioFormatsImageIO();

  private:
    IFormatReader* reader;
    ImageReader* imageReader;
    ChannelFiller* channelFiller;
    ChannelSeparator* channelSeparator;
    ChannelMerger* channelMerger;
    ImageWriter* writer;
  };

}

#endif // H_ITK_IMAGE_IO_H
