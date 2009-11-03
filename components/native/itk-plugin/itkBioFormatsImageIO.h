//
// itkBioFormatsImageIO.h
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

#ifndef H_ITK_IMAGE_IO_H
#define H_ITK_IMAGE_IO_H

// for Bio-Formats C++ bindings
#include "bio-formats.h"
using jace::JNIException;
using jace::proxy::java::io::IOException;
using jace::proxy::loci::formats::ChannelFiller;
using jace::proxy::loci::formats::FormatException;
using jace::proxy::loci::formats::FormatTools;
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
    ChannelFiller* reader;
    ImageWriter* writer;
  };

}

#endif // H_ITK_IMAGE_IO_H
