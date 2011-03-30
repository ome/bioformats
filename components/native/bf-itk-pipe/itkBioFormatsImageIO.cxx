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
/*
OME Bio-Formats ITK plugin for calling Bio-Formats from the Insight Toolkit.
Copyright (c) 2008, UW-Madison LOCI.
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
IMPORTANT NOTE: Although this specific file is distributed according to a
"BSD-style" license and the Apache 2 license, it requires to be linked to the
OME Bio-Formats Java library at run-time to do anything useful. The OME
Bio-Formats Java library is licensed under the GPL v2 or later.  Therefore, if
you wish to distribute this software in binary form with Bio-Formats itself,
your combined binary work must be distributed under the terms of the GPL v2
or later license.
*/

/*
Adapted from the Slicer3 project: http://www.slicer.org/
http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/

See slicer-license.txt for Slicer3's licensing information.
*/

// Special thanks to Alex Gouaillard, Sebastien Barre,
// Luis Ibanez and Jim Miller for fixes and suggestions.

#include <fstream>

#include "itkBioFormatsImageIO.h"
#include "itkIOCommon.h"
#include "itkExceptionObject.h"
#include "itkMetaDataObject.h"

#include <cmath>

#include <stdio.h>
#include <stdlib.h>

#if defined (_WIN32)
#define PATHSTEP ';'
#define SLASH '\\'
#else
#define PATHSTEP ':'
#define SLASH '/'
#endif

//--------------------------------------
//
// BioFormatsImageIO
//

namespace itk {
 
  template <typename ReturnType>
  ReturnType valueOfString( const std::string &s )
  {
    ReturnType res;
    if( !(std::istringstream(s) >> res) )
      {
      itkGenericExceptionMacro(<<"BioFormatsImageIO: error while converting: " << s );
      }
    return res;
  }

  template<>
  bool valueOfString<bool>( const std::string &s )
  {
    std::stringstream ss;
    ss << s;
    bool res = false;
    ss >> res;
    if( ss.fail() )
    {
      ss.clear();
      ss >> std::boolalpha >> res;
    }
    return res;
  }

  template<typename T>
  std::string toString( const T & Value )
  {
    std::ostringstream oss;
    oss << Value;
    return oss.str();
  }



BioFormatsImageIO::BioFormatsImageIO()
{
  itkDebugMacro("BioFormatsImageIO constructor");

  this->m_FileType = Binary;

  const char name[] = "ITK_AUTOLOAD_PATH";
  const char* namePtr;
  namePtr = name;
  char * path;
  path = getenv(name);
  std::string dir("");

  if( path == NULL)
    {
    itkExceptionMacro("ITK_AUTOLOAD_PATH is not set, you must set this environment variable and point it to the directory containing the bio-formats.jar file");
    }
  dir.assign(path);
  if( dir.at(dir.length() - 1) != SLASH )
    {
    dir.append(1,SLASH);
    }
  std::string classpath = dir+"loci_tools.jar";
  classpath += PATHSTEP+dir;

  std::string javaCommand = "/usr/bin/java"; // todo: let the user choose the java executable

  itkDebugMacro("BioFormatsImageIO base command: "+javaCommand+" -Xmx256m -Djava.awt.headless=true -cp "+classpath);

  m_Args.push_back( javaCommand );
  m_Args.push_back( "-Xmx256m" );
  m_Args.push_back( "-Djava.awt.headless=true" );
  m_Args.push_back( "-cp" );
  m_Args.push_back( classpath );
  m_Args.push_back( "loci.formats.itk.ITKBridgePipes" );
  m_Args.push_back( "waitForInput" );
  // convert to something usable by itksys
  m_Argv = toCArray( m_Args );

  m_Process = NULL;
}

void BioFormatsImageIO::CreateJavaProcess()
{
  if( m_Process )
    {
    // process is still there
    if( itksysProcess_GetState( m_Process ) == itksysProcess_State_Executing )
      {
      // already created and running - just return
      return;
      }
    else
      {
      // still there but not running.
      // destroy it cleanly and continue with the creation process
      DestroyJavaProcess();
      }
    }

  pipe( m_Pipe );
  
  m_Process = itksysProcess_New();
  itksysProcess_SetCommand( m_Process, m_Argv );
  itksysProcess_SetPipeNative( m_Process, itksysProcess_Pipe_STDIN, m_Pipe );
  itksysProcess_Execute( m_Process );

  int state = itksysProcess_GetState( m_Process );
  switch( state )
    {
    case itksysProcess_State_Exited:
      {
      int retCode = itksysProcess_GetExitValue( m_Process );
      itkExceptionMacro(<<"BioFormatsImageIO: ITKReadImageInformation exited with return value: " << retCode);
      break;
      }
    case itksysProcess_State_Error:
      {
      std::string msg = itksysProcess_GetErrorString( m_Process );
      itkExceptionMacro(<<"BioFormatsImageIO: ITKReadImageInformation error:" << std::endl << msg);
      break;
      }
    case itksysProcess_State_Exception:
      {
      std::string msg = itksysProcess_GetExceptionString( m_Process );
      itkExceptionMacro(<<"BioFormatsImageIO: ITKReadImageInformation exception:" << std::endl << msg);
      break;
      }
    case itksysProcess_State_Executing:
      {
      // this is the expected state
      break;
      }
    case itksysProcess_State_Expired:
      {
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation expired.");
      break;
      }
    case itksysProcess_State_Killed:
      {
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation killed.");
      break;
      }
    case itksysProcess_State_Disowned:
      {
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation disowned.");
      break;
      }
//     case kwsysProcess_State_Starting:
//       {
//       break;
//       }
    default:
      {
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation is in unknown state.");
      break;
      }
    }
}

BioFormatsImageIO::~BioFormatsImageIO()
{
  itkDebugMacro( "BioFormatsImageIO::~BioFormatsImageIO");
  DestroyJavaProcess();
  delete m_Argv;
}

void BioFormatsImageIO::DestroyJavaProcess()
{
  if( m_Process == NULL )
    {
    // nothing to destroy
    return;
    }

  if( itksysProcess_GetState( m_Process ) == itksysProcess_State_Executing )
    {
    itkDebugMacro("BioFormatsImageIO::DestroyJavaProcess killing java process");
    itksysProcess_Kill( m_Process );
    itksysProcess_WaitForExit( m_Process, NULL );
    }

  itkDebugMacro("BioFormatsImageIO::DestroyJavaProcess destroying java process");
  itksysProcess_Delete( m_Process );
  m_Process = NULL;
  close( m_Pipe[1] );
}

bool BioFormatsImageIO::CanReadFile( const char* FileNameToRead )
{
  itkDebugMacro( "BioFormatsImageIO::CanReadFile: FileNameToRead = " << FileNameToRead);

  CreateJavaProcess();

  // send the command to the java process
  std::string command = "canRead\t";
  command += FileNameToRead;
  command += "\n";
  itkDebugMacro("BioFormatsImageIO::CanRead command: " << command);
  write( m_Pipe[1], command.c_str(), command.size() );
  // fflush( m_Pipe[1] );

  // and read its reply
  std::string imgInfo;
  std::string errorMessage;
  char * pipedata;
  int pipedatalength = 1000;

  bool keepReading = true;
  while( keepReading )
    {
    int retcode = itksysProcess_WaitForData( m_Process, &pipedata, &pipedatalength, NULL );
    // itkDebugMacro( "BioFormatsImageIO::ReadImageInformation: reading " << pipedatalength << " bytes.");
    if( retcode == itksysProcess_Pipe_STDOUT )
      {
      imgInfo += std::string( pipedata, pipedatalength );
      // if the two last char are "\n\n", then we're done
      if( imgInfo.size() >= 2 && imgInfo.substr( imgInfo.size()-2, 2 ) == "\n\n" )
        {
        keepReading = false;
        }
      }
    else if( retcode == itksysProcess_Pipe_STDERR )
      {
      errorMessage += std::string( pipedata, pipedatalength );
      }
    else
      {
      DestroyJavaProcess();
      itkExceptionMacro(<<"BioFormatsImageIO: 'ITKBridgePipe canRead' exited abnormally. " << errorMessage);
      }
    }

  itkDebugMacro("BioFormatsImageIO::CanRead error output: " << errorMessage);

  // we have one thing per line
  int p0 = 0;
  int p1 = 0;
  std::string canRead;
  // can read?
  p1 = imgInfo.find("\n", p0);
  canRead = imgInfo.substr( p0, p1 );

  return valueOfString<bool>(canRead);
}

void BioFormatsImageIO::ReadImageInformation()
{
  itkDebugMacro( "BioFormatsImageIO::ReadImageInformation: m_FileName = " << m_FileName);

  CreateJavaProcess();

  // send the command to the java process
  std::string command = "info\t";
  command += m_FileName;
  command += "\n";
  itkDebugMacro("BioFormatsImageIO::ReadImageInformation command: " << command);
  write( m_Pipe[1], command.c_str(), command.size() );
  // fflush( m_Pipe[1] );

  std::string imgInfo;
  std::string errorMessage;
  char * pipedata;
  int pipedatalength = 1000;

  bool keepReading = true;
  while( keepReading )
    {
    int retcode = itksysProcess_WaitForData( m_Process, &pipedata, &pipedatalength, NULL );
    // itkDebugMacro( "BioFormatsImageIO::ReadImageInformation: reading " << pipedatalength << " bytes.");
    if( retcode == itksysProcess_Pipe_STDOUT )
      {
      imgInfo += std::string( pipedata, pipedatalength );
      // if the two last char are "\n\n", then we're done
      if( imgInfo.size() >= 2 && imgInfo.substr( imgInfo.size()-2, 2 ) == "\n\n" )
        {
        keepReading = false;
        }
      }
    else if( retcode == itksysProcess_Pipe_STDERR )
      {
      errorMessage += std::string( pipedata, pipedatalength );
      }
    else
      {
      DestroyJavaProcess();
      itkExceptionMacro(<<"BioFormatsImageIO: 'ITKBridgePipe info' exited abnormally. " << errorMessage);
      }
    }

  itkDebugMacro("BioFormatsImageIO::ReadImageInformation error output: " << errorMessage);

  this->SetNumberOfDimensions(5);

  // fill the metadata dictionary
  MetaDataDictionary & dict = this->GetMetaDataDictionary();

  // we have one thing per line
  size_t p0 = 0;
  size_t p1 = 0;
  std::string line;
  while( p0 < imgInfo.size() )
    {
    // get the current line
    p1 = imgInfo.find("\n", p0);
    line = imgInfo.substr( p0, p1-p0 );

    // ignore the empty lines
    if( line == "" )
      {
      // go to the next line
      p0 = p1+1;
      continue;
      }

    // get the 3 parts of the line
    int sep1 = line.find("(");
    int sep2 = line.find("):");
    std::string key = line.substr( 0, sep1 );
    std::string type = line.substr( sep1+1, sep2-sep1-1 );
    std::string value = line.substr( sep2+3, line.size()-sep2-1 );
    // std::cout << "===" << name << "=" << type << "=" << value << "===" << std::endl;

    // store the values in the dictionary
    if( dict.HasKey(key) )
      {
      itkDebugMacro("BioFormatsImageIO::ReadImageInformation metadata " << key << " = " << value << " ignored because the key is already defined.");
      }
    else
      {
      if( type == "string" )
        {
        std::string tmp;
        // we have to unescape \\ and \n
        size_t lp0 = 0;
        size_t lp1 = 0;
        while( lp0 < value.size() )
          {
          lp1 = value.find( "\\", lp0 );
          if( lp1 == std::string::npos )
            {
            tmp += value.substr( lp0, value.size()-lp0 );
            lp0 = value.size();
            }
          else
            {
            tmp += value.substr( lp0, lp1-lp0 );
            if( lp1 < value.size() - 1 )
              {
              if( value[lp1+1] == '\\' )
                {
                tmp += '\\';
                }
              else if( value[lp1+1] == 'n' )
                {
                tmp += '\n';
                }
              }
            lp0 = lp1 + 2;
            }
          }
        EncapsulateMetaData< std::string >( dict, key, tmp );
        }
      else if( type == "bool" )
        {
        EncapsulateMetaData< bool >( dict, key, valueOfString<bool>(value) );
        }
      else if( type == "int" )
        {
        EncapsulateMetaData< long >( dict, key, valueOfString<long>(value) );
        }
      else if( type == "real" )
        {
        EncapsulateMetaData< double >( dict, key, valueOfString<double>(value) );
        }
      else if( type == "enum" )
        {
//         itkDebugMacro("BioFormatsImageIO::ReadImageInformation adding enum metadata " << key << " = " << value);
        EncapsulateMetaData< long >( dict, key, valueOfString<long>(value) );
        }
      }

    // go to the next line
    p0 = p1+1;
    }

  // set the values needed by the reader
  std::string s;
  bool b;
  long i;
  double r;

  // is little endian?
  ExposeMetaData<bool>( dict, "LittleEndian", b );
  if( b )
    {
    SetByteOrderToLittleEndian();
    }
  else
    {
    SetByteOrderToBigEndian();
    }

  // component type
  itkAssertOrThrowMacro( dict.HasKey("PixelType"), "PixelType is not in the metadata dictionary!");
  ExposeMetaData<long>( dict, "PixelType", i );
  if( i == UNKNOWNCOMPONENTTYPE)
    {
    itkExceptionMacro("Unknown pixel type: "<< i);
    }
  SetComponentType( (itk::ImageIOBase::IOComponentType)i );

  // x, y, z, t, c
  ExposeMetaData<long>( dict, "SizeX", i );
  this->SetDimensions( 0, i );
  ExposeMetaData<long>( dict, "SizeY", i );
  this->SetDimensions( 1, i );
  ExposeMetaData<long>( dict, "SizeZ", i );
  this->SetDimensions( 2, i );
  ExposeMetaData<long>( dict, "SizeT", i );
  this->SetDimensions( 3, i );
  ExposeMetaData<long>( dict, "SizeC", i );
  this->SetDimensions( 4, i );

  // number of components
  ExposeMetaData<long>( dict, "RGBChannelCount", i );
  if( i == 1 )
    {
    this->SetPixelType( SCALAR );
    }
  else if( i == 3 )
    {
    this->SetPixelType( RGB );
    }
  else
    {
    this->SetPixelType( VECTOR );
    }
  this->SetNumberOfComponents( i );

  // spacing
  ExposeMetaData<double>( dict, "PixelsPhysicalSizeX", r );
  this->SetSpacing( 0, r );
  ExposeMetaData<double>( dict, "PixelsPhysicalSizeY", r );
  this->SetSpacing( 1, r );
  ExposeMetaData<double>( dict, "PixelsPhysicalSizeZ", r );
  this->SetSpacing( 2, r );
  ExposeMetaData<double>( dict, "PixelsPhysicalSizeT", r );
  this->SetSpacing( 3, r );
  ExposeMetaData<double>( dict, "PixelsPhysicalSizeC", r );
  this->SetSpacing( 4, r );

}

void BioFormatsImageIO::Read(void* pData)
{
  itkDebugMacro("BioFormatsImageIO::Read");
  const ImageIORegion & region = this->GetIORegion();

  CreateJavaProcess();

  // send the command to the java process
  std::string command = "read\t";
  command += m_FileName;
  for( unsigned int d=0; d<region.GetImageDimension(); d++ )
    {
    command += "\t";
    command += toString(region.GetIndex(d));
    command += "\t";
    command += toString(region.GetSize(d));
    }
  for( int d=region.GetImageDimension(); d<5; d++ )
    {
    command += "\t0\t1";
    }
  command += "\n";
  itkDebugMacro("BioFormatsImageIO::Read command: " << command);
  write( m_Pipe[1], command.c_str(), command.size() );
  // fflush( m_Pipe[1] );

  // and read the image
  char * data = (char *)pData;
  size_t pos = 0;
  std::string errorMessage;
  char * pipedata;
  int pipedatalength;

  size_t byteCount = this->GetPixelSize() * region.GetNumberOfPixels();
  while( pos < byteCount )
    {
    int retcode = itksysProcess_WaitForData( m_Process, &pipedata, &pipedatalength, NULL );
    // itkDebugMacro( "BioFormatsImageIO::ReadImageInformation: reading " << pipedatalength << " bytes.");
    if( retcode == itksysProcess_Pipe_STDOUT )
      {
      // std::cout << "pos: " << pos << "  reading: " << pipedatalength << std::endl;
      memcpy( data + pos, pipedata, pipedatalength );
      pos += pipedatalength;
      }
    else if( retcode == itksysProcess_Pipe_STDERR )
      {
      errorMessage += std::string( pipedata, pipedatalength );
      }
    else
      {
      DestroyJavaProcess();
      itkExceptionMacro(<<"BioFormatsImageIO: 'ITKBridgePipe read' exited abnormally. " << errorMessage);
      }
    }

  itkDebugMacro("BioFormatsImageIO::Read error output: " << errorMessage);
}

bool BioFormatsImageIO::CanWriteFile(const char* name)
{
  itkDebugMacro("BioFormatsImageIO::CanWriteFile: name = " << name);
  return false;
}

void BioFormatsImageIO::WriteImageInformation()
{
  itkDebugMacro("BioFormatsImageIO::WriteImageInformation");
  // NB: Nothing to do.
}

void BioFormatsImageIO::Write(const void * itkNotUsed(buffer) )
{
  itkDebugMacro("BioFormatsImageIO::Write");
  // CTR TODO - implement Write function
}

} // end namespace itk
