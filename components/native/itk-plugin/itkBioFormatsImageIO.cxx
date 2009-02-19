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

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "bioformats.h"

#include <cmath>

#include <stdio.h>
#include <stdlib.h>

//-------------------------------

static
std::string
GetExtension( const std::string& filename)
{
  const std::string::size_type pos = filename.find_last_of(".");
  std::string extension(filename, pos+1, filename.length());
  return extension;
}

//--------------------------------------
//
// BioFormatsImageIO
//

namespace itk
{

  BioFormatsImageIO::BioFormatsImageIO()
  {
    this->SetNumberOfDimensions(3);
    const unsigned int uzero = 0;
    m_Dimensions[0] = uzero;
    m_Dimensions[1] = uzero;
    m_Dimensions[2] = uzero;

    if ( ByteSwapper<int>::SystemIsBigEndian())
      m_ByteOrder = BigEndian;
    else
      m_ByteOrder = LittleEndian;
  }

  BioFormatsImageIO::~BioFormatsImageIO()
  {
  }

  bool
  BioFormatsImageIO::CanReadFile(const char* FileNameToRead)
  {
    std::cout << "BioFormatsImageIO::CanReadFile: FileNameToRead=" << FileNameToRead << std::endl;//TEMP
    std::string filename(FileNameToRead);

    if ( filename == "" )
    {
      itkExceptionMacro(<<"A FileName must be specified.");
      return false;
    }

    // check if the correct extension is given by the user
    std::string extension = GetExtension(filename);
    if ( extension == std::string("zvi") || extension == std::string("lif") )
    {
      return true;
    }

    return false;
  }

  void
  BioFormatsImageIO::ReadImageInformation()
  {
    std::cout << "BioFormatsImageIO::ReadImageInformation" << std::endl;//TEMP

    // initialize the Java virtual machine
    std::cout << "Creating JVM..." << std::endl;//TEMP
    StaticVmLoader loader(JNI_VERSION_1_4);
    OptionList list;
    list.push_back(jace::ClassPath(
      "jace-runtime.jar:bio-formats.jar:loci_tools.jar"));
    list.push_back(jace::CustomOption("-Xcheck:jni"));
    list.push_back(jace::CustomOption("-Xmx256m"));
    //list.push_back(jace::CustomOption("-verbose:jni"));
    jace::helper::createVm(loader, list, false);
    std::cout << "JVM created." << std::endl;

    //ImageReader r;
    //std::cout << "Created ImageReader:" << r << std::endl;//TEMP
  }

  void
  BioFormatsImageIO::Read(void* pData)
  {
    std::cout << "BioFormatsImageIO::Read" << std::endl;//TEMP
  } // end Read function

  bool
  BioFormatsImageIO::CanWriteFile(const char* name)
  {
    std::cout << "BioFormatsImageIO::CanWriteFile: name=" << name << std::endl;//TEMP
    std::string filename(name);

    if ( filename == "" )
    {
      itkExceptionMacro(<<"A FileName must be specified.");
      return false;
    }

    std::string extension = GetExtension(filename);
    if ( extension != std::string("zvi") && extension != std::string("lif") )
      return false;

    return true;
  }

  void
  BioFormatsImageIO::WriteImageInformation()
  {
    std::cout << "BioFormatsImageIO::WriteImageInformation" << std::endl;//TEMP
  }

  void
  BioFormatsImageIO::Write(const void* buffer)
  {
    std::cout << "BioFormatsImageIO::Write" << std::endl;//TEMP
  }

} // end NAMESPACE ITK
