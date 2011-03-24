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
#include "itksys/Process.h"

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
    std::stringstream ss;
    ss << s;
    ReturnType res;
    ss >> res;
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
  m_ClassPath = classpath;

  m_JavaCommand = "/usr/bin/java"; // todo: let the user choose the java executable

  itkDebugMacro("BioFormatsImageIO base command: "+m_JavaCommand+" -cp "+classpath);
}

BioFormatsImageIO::~BioFormatsImageIO()
{
}

bool BioFormatsImageIO::CanReadFile( const char* FileNameToRead )
{
  itkDebugMacro( "BioFormatsImageIO::CanReadFile: FileNameToRead = " << FileNameToRead);

  std::vector< std::string > args;
  args.push_back( m_JavaCommand );
  args.push_back( "-cp" );
  args.push_back( m_ClassPath );
  args.push_back( "loci.formats.itk.ITKBridgePipes" );
  args.push_back( FileNameToRead );
  args.push_back( "canRead" );
  // convert to something usable by itksys
  char ** argv = toCArray(args);

  std::string imgInfo;
  std::string errorMessage;
  char * pipedata;
  int pipedatalength = 1000;

  itksysProcess *process = itksysProcess_New();
  itksysProcess_SetCommand(process, argv);
  itksysProcess_Execute(process);

  int retcode = -1;
  while( retcode = itksysProcess_WaitForData(process, &pipedata, &pipedatalength, NULL) )
    {
    // itkDebugMacro( "BioFormatsImageIO::ReadImageInformation: reading " << pipedatalength << " bytes.");
    if( retcode == itksysProcess_Pipe_STDOUT )
      {
      // append that data to our img information to parse it much easily later
      imgInfo += std::string( pipedata, pipedatalength );
      }
    else
      {
      errorMessage += std::string( pipedata, pipedatalength );
      }
    }

  itksysProcess_WaitForExit(process, NULL);

  // we don't need that anymore
  delete[] argv;

  int state = itksysProcess_GetState(process);
  switch( state )
    {
    case itksysProcess_State_Exited:
      {
      int retCode = itksysProcess_GetExitValue(process);
      itksysProcess_Delete(process);
      itkDebugMacro("BioFormatsImageIO::ReadImageInformation: retCode = " << retCode);
      if ( retCode != 0 )
        {
        itkExceptionMacro(<<"BioFormatsImageIO: ITKReadImageInformation exited with return value: " << retCode << std::endl
                          << errorMessage);
        }
      break;
      }
    case itksysProcess_State_Error:
      {
      std::string msg = itksysProcess_GetErrorString(process);
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: ITKReadImageInformation error:" << std::endl << msg);
      break;
      }
    case itksysProcess_State_Exception:
      {
      std::string msg = itksysProcess_GetExceptionString(process);
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: ITKReadImageInformation exception:" << std::endl << msg);
      break;
      }
    case itksysProcess_State_Executing:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation is still running.");
      break;
      }
    case itksysProcess_State_Expired:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation expired.");
      break;
      }
    case itksysProcess_State_Killed:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation killed.");
      break;
      }
    case itksysProcess_State_Disowned:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation disowned.");
      break;
      }
//     case kwsysProcess_State_Starting:
//       {
//       break;
//       }
    default:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation is in unknown state.");
      break;
      }
    }

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

  std::vector< std::string > args;
  args.push_back( m_JavaCommand );
  args.push_back( "-cp" );
  args.push_back( m_ClassPath );
  args.push_back( "loci.formats.itk.ITKBridgePipes" );
  args.push_back( m_FileName );
  args.push_back( "info" );
  // convert to something usable by itksys
  char **argv = toCArray(args);

  std::string imgInfo;
  std::string errorMessage;
  char * pipedata;
  int pipedatalength = 1000;

  itksysProcess *process = itksysProcess_New();
  itksysProcess_SetCommand(process, argv);
  itksysProcess_Execute(process);

  while( int retcode = itksysProcess_WaitForData(process, &pipedata, &pipedatalength, NULL) )
    {
    // itkDebugMacro( "BioFormatsImageIO::ReadImageInformation: reading " << pipedatalength << " bytes.");
    if( retcode == itksysProcess_Pipe_STDOUT )
      {
      // append that data to our img information to parse it much easily later
      imgInfo += std::string( pipedata, pipedatalength );
      }
    else
      {
      errorMessage += std::string( pipedata, pipedatalength );
      }
    }

  itksysProcess_WaitForExit(process, NULL);

  // we don't need that anymore
  delete[] argv;

  int state = itksysProcess_GetState(process);
  switch( state )
    {
    case itksysProcess_State_Exited:
      {
      int retCode = itksysProcess_GetExitValue(process);
      itksysProcess_Delete(process);
      itkDebugMacro("BioFormatsImageIO::ReadImageInformation: retCode = " << retCode);
      if ( retCode != 0 )
        {
        itkExceptionMacro(<<"BioFormatsImageIO: ITKReadImageInformation exited with return value: " << retCode << std::endl
                          << errorMessage);
        }
      break;
      }
    case itksysProcess_State_Error:
      {
      std::string msg = itksysProcess_GetErrorString(process);
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: ITKReadImageInformation error:" << std::endl << msg);
      break;
      }
    case itksysProcess_State_Exception:
      {
      std::string msg = itksysProcess_GetExceptionString(process);
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: ITKReadImageInformation exception:" << std::endl << msg);
      break;
      }
    case itksysProcess_State_Executing:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation is still running.");
      break;
      }
    case itksysProcess_State_Expired:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation expired.");
      break;
      }
    case itksysProcess_State_Killed:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation killed.");
      break;
      }
    case itksysProcess_State_Disowned:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation disowned.");
      break;
      }
//     case kwsysProcess_State_Starting:
//       {
//       break;
//       }
    default:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKReadImageInformation is in unknown state.");
      break;
      }
    }

  this->SetNumberOfDimensions(5);

  // we have one thing per line
  int p0 = 0;
  int p1 = 0;
  std::string line;
  // is little endian?
  p1 = imgInfo.find("\n", p0);
  line = imgInfo.substr( p0, p1 );
  if( valueOfString<bool>(line) )
    {
    SetByteOrderToLittleEndian(); // m_ByteOrder
    }
  else
    {
    SetByteOrderToBigEndian(); // m_ByteOrder
    }
  // component type
  p0 = p1+1;
  p1 = imgInfo.find("\n", p0);
  line = imgInfo.substr( p0, p1 );
  int componentType = valueOfString<int>(line);
  if( componentType == UNKNOWNCOMPONENTTYPE)
    {
    itkExceptionMacro("Unknown pixel type:");
    }
  SetComponentType( (itk::ImageIOBase::IOComponentType)componentType );
  // x, y, z, t, c
  for( int i=0; i<5; i++ )
    {
    p0 = p1+1;
    p1 = imgInfo.find("\n", p0);
    line = imgInfo.substr( p0, p1 );
    this->SetDimensions( i, valueOfString<int>(line) );
    }
  // number of components
  p0 = p1+1;
  p1 = imgInfo.find("\n", p0);
  line = imgInfo.substr( p0, p1 );
  int nbOfComponents = valueOfString<int>(line);
  if( nbOfComponents == 1 )
    {
    SetPixelType( SCALAR );
    }
  else if( nbOfComponents == 3 )
    {
    SetPixelType( RGB );
    }
  else
    {
    SetPixelType( VECTOR );
    }
  SetNumberOfComponents( nbOfComponents ); // m_NumberOfComponents
  // spacing
  for( int i=0; i<5; i++ )
    {
    p0 = p1+1;
    p1 = imgInfo.find("\n", p0);
    line = imgInfo.substr( p0, p1 );
    double s = valueOfString<double>(line);
    if( s == 0.0 )
      {
      s = 1.0;
      }
    this->SetSpacing( i, s );
    }
}

void BioFormatsImageIO::Read(void* pData)
{
  itkDebugMacro("BioFormatsImageIO::Read");

  std::vector< std::string > args;
  args.push_back( m_JavaCommand );
  args.push_back( "-cp" );
  args.push_back( m_ClassPath );
  args.push_back( "loci.formats.itk.ITKBridgePipes" );
  args.push_back( m_FileName );
  args.push_back( "read" );
  // convert to something usable by itksys
  char **argv = toCArray(args);

  char * data = (char *)pData;
  long pos = 0;
  std::string errorMessage;
  char * pipedata;
  int pipedatalength;

  itksysProcess *process = itksysProcess_New();
  itksysProcess_SetPipeShared(process, itksysProcess_Pipe_STDIN, 0);
//  itksysProcess_SetPipeShared(process, itksysProcess_Pipe_STDERR, 1);
  itksysProcess_SetCommand(process, argv);
  itksysProcess_Execute(process);

  while( int retcode = itksysProcess_WaitForData(process, &pipedata, &pipedatalength, NULL) )
    {
    if( retcode == itksysProcess_Pipe_STDOUT )
      {
      // std::cout << "pos: " << pos << "  reading: " << pipedatalength << std::endl;
      for( long i=0; i<pipedatalength; i++, pos++ )
        {
        data[pos] = pipedata[i];
        }
      }
    else if( retcode == itksysProcess_Pipe_STDERR )
      {
      std::string error = std::string( pipedata, pipedatalength );
      itkDebugMacro("BioFormatsImageIO::Read error = " << error);
      errorMessage += error;
      }
    }

  itksysProcess_WaitForExit(process, NULL);

  // we don't need that anymore
  delete[] argv;

  int state = itksysProcess_GetState(process);
  switch( state )
    {
    case itksysProcess_State_Exited:
      {
      int retCode = itksysProcess_GetExitValue(process);
      itksysProcess_Delete(process);
      itkDebugMacro("BioFormatsImageIO::Read: retCode = " << retCode);
      if ( retCode != 0 )
        {
        itkExceptionMacro(<<"BioFormatsImageIO: ITKRead exited with return value: " << retCode << std::endl
                          << errorMessage);
        }
      break;
      }
    case itksysProcess_State_Error:
      {
      std::string msg = itksysProcess_GetErrorString(process);
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: ITKRead error:" << std::endl << msg);
      break;
      }
    case itksysProcess_State_Exception:
      {
      std::string msg = itksysProcess_GetExceptionString(process);
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: ITKRead exception:" << std::endl << msg);
      break;
      }
    case itksysProcess_State_Executing:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKRead is still running.");
      break;
      }
    case itksysProcess_State_Expired:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKRead expired.");
      break;
      }
    case itksysProcess_State_Killed:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKRead killed.");
      break;
      }
    case itksysProcess_State_Disowned:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKRead disowned.");
      break;
      }
//     case kwsysProcess_State_Starting:
//       {
//       break;
//       }
    default:
      {
      itksysProcess_Delete(process);
      itkExceptionMacro(<<"BioFormatsImageIO: internal error: ITKRead is in unknown state.");
      break;
      }
    }

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
