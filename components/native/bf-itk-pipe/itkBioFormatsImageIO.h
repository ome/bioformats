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

#ifndef __itkBioFormatsImageIO_h
#define __itkBioFormatsImageIO_h

// ITK includes
#include "itkImageIOBase.h"
#include "itkStreamingImageIOBase.h"
#include "itkBioFormatsIOWin32Header.h"
#include <sstream>
#include <iostream>
#include "itksys/Process.h"

namespace itk
{

/** \class BioFormatsImageIO
 *
 * \brief Interface to the Bio-Formats Java Library.
 *
 * This class provides an adaptor gate to use all the file formats supported by
 * the Bio-Formats Java library.
 *
 * \warning Note that the Bio-Format Java library is distributed under a GPLv2
 * license. For details, see http://www.loci.wisc.edu/software/bio-formats
 */
class BioFormatsImageIO_EXPORT BioFormatsImageIO : public StreamingImageIOBase
{
public:
  typedef BioFormatsImageIO           Self;
  typedef ImageIOBase                 Superclass;
  typedef SmartPointer<Self>          Pointer;
  typedef SmartPointer<const Self>    ConstPointer;
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

  void CreateJavaProcess();
  void DestroyJavaProcess();

protected:
  BioFormatsImageIO();
  ~BioFormatsImageIO();

  virtual SizeType GetHeaderSize() const { return 0; }

private:
  typedef itk::ImageIOBase::IOComponentType ITKComponent;

  char ** toCArray( std::vector< std::string > & args )
  {
    char **argv = new char *[args.size() + 1];
    for( int i = 0; i < static_cast< int >( args.size() ); i++ )
      {
      itkDebugMacro( "BioFormatsImageIO::toCArray::args["<<i<<"] = " << args[i]);
      argv[i] = (char*)args[i].c_str();
      }
    argv[args.size()] = NULL;
    return argv;
  }

  ITKComponent bfToTIKComponentType( int pixelType ) {
    switch ( pixelType ) {
      case 0:
        return CHAR;
      case 1:
        return UCHAR;
      case 2:
        return INT;
      case 3:
        return UINT;
      case 4:
        return LONG;
      case 5:
         return ULONG;
      case 6:
        return FLOAT;
      default:
        return DOUBLE;
    }
  }

  int itkToBFPixelType( ITKComponent cmp )
  {
    switch ( cmp )
    {
    case CHAR:
      return 0;
    case UCHAR:
      return 1;
    case SHORT:
    case INT:
      return 2;
    case USHORT:
    case UINT:
      return 3;
    case LONG:
      return 4;
    case ULONG:
      return 5;
    case FLOAT:
      return 6;
    default:
      return 7;
    }
  }

  std::vector< std::string >   m_Args;
  char **                      m_Argv;
  itksysProcess_Pipe_Handle    m_Pipe[2];
  itksysProcess *              m_Process;
};

}

#endif
