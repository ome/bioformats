/*
 * #%L
 * Bio-Formats plugin for the Insight Toolkit.
 * %%
 * Copyright (C) 2009 - 2012 Open Microscopy Environment:
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
using jace::proxy::loci::formats::meta::MetadataRetrieve;
using jace::proxy::loci::formats::meta::IMetadata;
#include "ome-xml.h"
using jace::proxy::ome::xml::model::enums::DimensionOrder;
using jace::proxy::ome::xml::model::enums::PixelType;
using jace::proxy::ome::xml::model::primitives::PositiveInteger;
using jace::proxy::ome::xml::model::primitives::PositiveFloat;
using jace::proxy::types::JInt;
using jace::proxy::java::lang::Boolean;
using jace::proxy::java::lang::Integer;
using jace::proxy::java::lang::Double;
using jace::proxy::java::lang::String;

#undef Byte

// STL includes

// ITK includes
#include "itkImageIOBase.h"
#include "itkMatrix.h"
#include "itkIndent.h"

#include <itk_zlib.h>

#include "itkBioFormatsIOWin32Header.h"

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

    virtual double decode(PositiveFloat pf);
    virtual double decode(Double h);
  };

}

#endif // H_ITK_IMAGE_IO_H
