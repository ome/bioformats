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

#ifdef WIN32
#include <io.h>
#include <fcntl.h>
#include <process.h>
#include <math.h>

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

  template <typename T>
  T GetTypedMetaData ( MetaDataDictionary dict, std::string key )
  {
    std::string tmp;
    ExposeMetaData<std::string>(dict, key, tmp);
    return valueOfString<T>(tmp);
  }

  template <>
  bool valueOfString <bool> ( const std::string &s )
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
  DebugOn(); // NB: For debugging.
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

  

#ifdef WIN32
  std::string javaCommand = "java";
#else
  std::string javaCommand = "/usr/bin/java"; // todo: let the user choose the java executable
#endif
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

#ifdef WIN32
   SECURITY_ATTRIBUTES saAttr; 
   saAttr.nLength = sizeof(SECURITY_ATTRIBUTES); 
   saAttr.bInheritHandle = TRUE; 
   saAttr.lpSecurityDescriptor = NULL;
  
  if( !CreatePipe( &(m_Pipe[0]), &(m_Pipe[1]), &saAttr, 0) )
	itkExceptionMacro(<<"createpipe() failed");
  if ( ! SetHandleInformation(m_Pipe[1], HANDLE_FLAG_INHERIT, 0) )
    itkExceptionMacro(<<"set inherited failed");
#else
  pipe( m_Pipe );
#endif

  m_Process = itksysProcess_New();
  itksysProcess_SetCommand( m_Process, m_Argv );
  itksysProcess_SetPipeNative( m_Process, itksysProcess_Pipe_STDIN, m_Pipe);
  
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

#ifdef WIN32
  CloseHandle( m_Pipe[1] );
#else
  close( m_Pipe[1] );
#endif
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

  
 
#ifdef WIN32
  DWORD bytesWritten;
  bool r = WriteFile( m_Pipe[1], command.c_str(), command.size(), &bytesWritten, NULL );
#else
  write( m_Pipe[1], command.c_str(), command.size() );
#endif

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
#ifdef WIN32
      if( imgInfo.size() >= 4 && imgInfo.substr( imgInfo.size()-4, 4 ) == "\r\n\r\n" )
#else
	  if( imgInfo.size() >= 2 && imgInfo.substr( imgInfo.size()-2, 2 ) == "\n\n" )
#endif
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

  

#ifdef WIN32
  DWORD bytesWritten;
  WriteFile( m_Pipe[1], command.c_str(), command.size(), &bytesWritten, NULL );
#else
  write( m_Pipe[1], command.c_str(), command.size() );
#endif

  // fflush( m_Pipe[1] );
  std::string imgInfo;
  std::string errorMessage;
  char * pipedata;
  int pipedatalength = 1000;

  bool keepReading = true;
  while( keepReading )
    {
    int retcode = itksysProcess_WaitForData( m_Process, &pipedata, &pipedatalength, NULL );
    //itkDebugMacro( "BioFormatsImageIO::ReadImageInformation: reading " << pipedatalength << " bytes.");

	
    if( retcode == itksysProcess_Pipe_STDOUT )
      {
      imgInfo += std::string( pipedata, pipedatalength );
      // if the two last char are "\n\n", then we're done
#ifdef WIN32
		if( imgInfo.size() >= 4 && imgInfo.substr( imgInfo.size()-4, 4 ) == "\r\n\r\n" )
#else
        if( imgInfo.size() >= 2 && imgInfo.substr( imgInfo.size()-2, 2 ) == "\n\n" )
#endif
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

  // we have one thing per two lines
  size_t p0 = 0;
  size_t p1 = 0;
  std::string line;

  while( p0 < imgInfo.size() )
    {
	
    // get the key line
#ifdef WIN32
    p1 = imgInfo.find("\r\n", p0);
#else
    p1 = imgInfo.find("\n", p0);
#endif

    line = imgInfo.substr( p0, p1-p0 );

    // ignore the empty lines
    if( line == "" )
      {
      // go to the next line
#ifdef WIN32
      p0 = p1+2;
#else
	  p0 = p1+1;
#endif
      continue;
      }

    std::string key = line;
    // go to the next line
#ifdef WIN32
      p0 = p1+2;
#else
      p0 = p1+1;
#endif

    // get the value line
#ifdef WIN32
    p1 = imgInfo.find("\r\n", p0);
#else
    p1 = imgInfo.find("\n", p0);
#endif

    line = imgInfo.substr( p0, p1-p0 );
	
    // ignore the empty lines
#ifdef WIN32
    if( line == "\r" )
#else
    if( line == "" )
#endif
      {
      // go to the next line
#ifdef WIN32
      p0 = p1+2;
#else
      p0 = p1+1;
#endif
      continue;
      }

    std::string value = line;
    //itkDebugMacro("=== " << key << " = " << value << " ===");
    
    // store the values in the dictionary
    if( dict.HasKey(key) )
      {
      itkDebugMacro("BioFormatsImageIO::ReadImageInformation metadata " << key << " = " << value << " ignored because the key is already defined.");
      }
    else
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
        itkDebugMacro("Storing metadata: " << key << " ---> " << tmp);
        EncapsulateMetaData< std::string >( dict, key, tmp );
      }

    // go to the next line
#ifdef WIN32
      p0 = p1+2;
#else
      p0 = p1+1;
#endif
	
    }
  // set the values needed by the reader
  std::string s;
  bool b;
  long i;
  double r;

  // is interleaved?
  b = GetTypedMetaData<bool>(dict, "Interleaved");
  if( b )
    {
    itkDebugMacro("Interleaved ---> True");
    }
  else
    {
    itkDebugMacro("Interleaved ---> False");
    }

  // is little endian?
  b = GetTypedMetaData<bool>(dict, "LittleEndian");
  if( b )
    {
    itkDebugMacro("Setting LittleEndian ---> True");
    SetByteOrderToLittleEndian();
    }
  else
    {
    itkDebugMacro("Setting LittleEndian ---> False");
    SetByteOrderToBigEndian();
    }

  // component type
  itkAssertOrThrowMacro( dict.HasKey("PixelType"), "PixelType is not in the metadata dictionary!");
  i = GetTypedMetaData<long>(dict, "PixelType");
  if( i == UNKNOWNCOMPONENTTYPE)
    {
    itkExceptionMacro("Unknown pixel type: "<< i);
    }
  itkDebugMacro("Setting PixelType: " << i);
  SetComponentType( (itk::ImageIOBase::IOComponentType)i );

  // x, y, z, t, c
  i = GetTypedMetaData<long>(dict, "SizeX");
  itkDebugMacro("Setting SizeX: " << i);
  this->SetDimensions( 0, i );
  i = GetTypedMetaData<long>(dict, "SizeY");
  itkDebugMacro("Setting SizeY: " << i);
  this->SetDimensions( 1, i );
  i = GetTypedMetaData<long>(dict, "SizeZ");
  itkDebugMacro("Setting SizeZ: " << i);
  this->SetDimensions( 2, i );
  i = GetTypedMetaData<long>(dict, "SizeT");
  itkDebugMacro("Setting SizeT: " << i);
  this->SetDimensions( 3, i );
  i = GetTypedMetaData<long>(dict, "SizeC");
  itkDebugMacro("Setting SizeC: " << i);
  this->SetDimensions( 4, i );

  // number of components
  i = GetTypedMetaData<long>(dict, "RGBChannelCount");
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
  r = GetTypedMetaData<double>(dict, "PixelsPhysicalSizeX");
  itkDebugMacro("Setting PixelsPhysicalSizeX: " << r);
  this->SetSpacing( 0, r );
  r = GetTypedMetaData<double>(dict, "PixelsPhysicalSizeY");
  itkDebugMacro("Setting PixelsPhysicalSizeY: " << r);
  this->SetSpacing( 1, r );
  r = GetTypedMetaData<double>(dict, "PixelsPhysicalSizeZ");
  itkDebugMacro("Setting PixelsPhysicalSizeZ: " << r);
  this->SetSpacing( 2, r );
  r = GetTypedMetaData<double>(dict, "PixelsPhysicalSizeT");
  itkDebugMacro("Setting PixelsPhysicalSizeT: " << r);
  this->SetSpacing( 3, r );
  r = GetTypedMetaData<double>(dict, "PixelsPhysicalSizeC");
  itkDebugMacro("Setting PixelsPhysicalSizeC: " << r);
  this->SetSpacing( 4, r );
}

void BioFormatsImageIO::Read(void* pData)
{
  itkDebugMacro("BioFormatsImageIO::Read");
  const ImageIORegion & region = this->GetIORegion();

  CreateJavaProcess();

  itkDebugMacro("BioFormatsImageIO::Read region: ");


  // send the command to the java process
  std::string command = "read\t";
  command += m_FileName;
  for( unsigned int d=0; d<region.GetImageDimension(); d++ )
    {
    itkDebugMacro("region index: " << region.GetIndex(d) << "; region size: " << region.GetSize(d));
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

#ifdef WIN32
  DWORD bytesWritten;
  WriteFile( m_Pipe[1], command.c_str(), command.size(), &bytesWritten, NULL );
#else
  write( m_Pipe[1], command.c_str(), command.size() );
#endif

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
  CreateJavaProcess();

  std::string command = "canWrite\t";
  command += name;
  command += "\n";

#ifdef WIN32
 DWORD bytesWritten;
  WriteFile( m_Pipe[1], command.c_str(), command.size(), &bytesWritten, NULL );
#else
  write( m_Pipe[1], command.c_str(), command.size() );
#endif

  std::string imgInfo;
  std::string errorMessage;
  char * pipedata;
  int pipedatalength = 1000;

  bool keepReading = true;
  while( keepReading )
    {
    int retcode = itksysProcess_WaitForData( m_Process, &pipedata, &pipedatalength, NULL );
    itkDebugMacro( "BioFormatsImageIO::CanWriteFile: reading " << pipedatalength << " bytes.");
    if( retcode == itksysProcess_Pipe_STDOUT )
      {
      imgInfo += std::string( pipedata, pipedatalength );
      // if the two last char are "\n\n", then we're done
#ifdef WIN32
	  if( imgInfo.size() >= 4 && imgInfo.substr( imgInfo.size()-4, 4 ) == "\r\n\r\n" )
#else
      if( imgInfo.size() >= 2 && imgInfo.substr( imgInfo.size()-2, 2 ) == "\n\n" )
#endif
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
      itkExceptionMacro(<<"BioFormatsImageIO: 'ITKBridgePipe canWrite' exited abnormally. " << errorMessage);
      }
    }

  itkDebugMacro("BioFormatsImageIO::CanWrite error output: " << errorMessage);

  // we have one thing per line
  int p0 = 0;
  int p1 = 0;
  std::string canWrite;
  // can write?
  p1 = imgInfo.find("\n", p0);
  canWrite = imgInfo.substr( p0, p1 );
  itkDebugMacro("CanWrite result: " << canWrite);
  return valueOfString<bool>(canWrite);
}

void BioFormatsImageIO::WriteImageInformation()
{
  itkDebugMacro("BioFormatsImageIO::WriteImageInformation");
  // NB: Nothing to do.
}

void BioFormatsImageIO::Write(const void * buffer )
{
  itkDebugMacro("BioFormatsImageIO::Write");

  CreateJavaProcess();

  ImageIORegion region = GetIORegion();
  int regionDim = region.GetImageDimension();

  std::string command = "write\t";
  itkDebugMacro("File name: " << m_FileName);
  command += m_FileName;
  command += "\t";
  itkDebugMacro("Byte Order: " << GetByteOrderAsString(m_ByteOrder) << " " << m_ByteOrder);
  command += toString(m_ByteOrder);
  command += "\t";
  itkDebugMacro("Region dimensions: " << regionDim);
  command += toString(regionDim);
  command += "\t";

  for(int i = 0; i < regionDim; i++){
    itkDebugMacro("Dimension " << i << ": " << region.GetSize(i));
    command += toString(region.GetSize(i));
    command += "\t";
  }

  for(int i = regionDim; i < 5; i++) {
    itkDebugMacro("Dimension " << i << ": " << 1);
    command += toString(1);
    command += "\t";
  }

  for(int i = 0; i < regionDim; i++){
    itkDebugMacro("Phys Pixel size " << i << ": " << this->GetSpacing(i));
    command += toString(this->GetSpacing(i));
    command += "\t";
  }

  for(int i = regionDim; i < 5; i++) {
    itkDebugMacro("Phys Pixel size" << i << ": " << 1);
    command += toString(1);
    command += "\t";
  }

  itkDebugMacro("Pixel Type: " << itkToBFPixelType(GetComponentType()));
  command += toString(itkToBFPixelType(GetComponentType()));
  command += "\t";

  int rgbChannelCount = GetNumberOfComponents();

  itkDebugMacro("RGB Channels: " << rgbChannelCount);
  command += toString(rgbChannelCount);
  command += "\t";

  // int xIndex = 0, yIndex = 1
  int zIndex = 2, cIndex = 3, tIndex = 4;
  int bytesPerPlane = rgbChannelCount;
  int numPlanes = 1;

  for (int dim = 0; dim < 5; dim++)
  {
    if(dim < regionDim)
    {
      int index = region.GetIndex(dim);
      int size = region.GetSize(dim);
      itkDebugMacro("dim = " << dim << " index = " << toString(index) << " size = " << toString(size));
      command += toString(index);
      command += "\t";
      command += toString(size);
      command += "\t";

      if( dim == cIndex || dim == zIndex || dim == tIndex )
      {
        numPlanes *= size - index;
      }
    }
    else
    {
      itkDebugMacro("dim = " << dim << " index = " << 0 << " size = " << 1);
      command += toString(0);
      command += "\t";
      command += toString(1);
      command += "\t";
    }
  }

  command += "\n";

  itkDebugMacro("BioFormatsImageIO::Write command: " << command);

#ifdef WIN32
  DWORD bytesWritten;
  WriteFile( m_Pipe[1], command.c_str(), command.size(), &bytesWritten, NULL );
#else
  write( m_Pipe[1], command.c_str(), command.size() );
#endif

  // need to read back the number of planes and bytes per plane to read from buffer
  std::string imgInfo;
  std::string errorMessage;
  char * pipedata;
  int pipedatalength = 1000;

  itkDebugMacro("BioFormatsImageIO::Write reading data back ...");
  bool keepReading = true;
  while( keepReading )
    {
    int retcode = itksysProcess_WaitForData( m_Process, &pipedata, &pipedatalength, NULL );
    if( retcode == itksysProcess_Pipe_STDOUT )
      {
      imgInfo += std::string( pipedata, pipedatalength );
      // if the two last char are "\n\n", then we're done
#ifdef WIN32
      if( imgInfo.size() >= 4 && imgInfo.substr( imgInfo.size()-4, 4 ) == "\r\n\r\n" )
#else
	  if( imgInfo.size() >= 2 && imgInfo.substr( imgInfo.size()-2, 2 ) == "\n\n" )
#endif
        {
        keepReading = false;
        }
      }
    else if( retcode == itksysProcess_Pipe_STDERR )
      {
      errorMessage += std::string( pipedata, pipedatalength );
      //itkDebugMacro( "In read back loop. errorMessage: " << imgInfo);
      }
    else
      {
      DestroyJavaProcess();
      itkExceptionMacro(<<"BioFormatsImageIO: 'ITKBridgePipe Write' exited abnormally. " << errorMessage);
      }
    }

  itkDebugMacro("BioFormatsImageIO::Write error output: " << errorMessage);
  itkDebugMacro("Read imgInfo: " << imgInfo);

  // bytesPerPlane is the first line
  int p0 = 0;
  int p1 = 0;
  std::string vals;
  p1 = imgInfo.find("\n", p0);
  vals = imgInfo.substr( p0, p1 );

  bytesPerPlane = valueOfString<int>(vals);
  itkDebugMacro("BPP: " << bytesPerPlane << " numPlanes: " << numPlanes);

  typedef unsigned char BYTE;
  BYTE* data = (BYTE*)buffer;

  int pipelength = 10000;

  for (int i = 0; i < numPlanes; i++)
  {
      int bytesRead = 0;
      while(bytesRead < bytesPerPlane )
      {
        itkDebugMacro("bytesPerPlane: " << bytesPerPlane << " bytesRead: " << bytesRead << " pipelength: " << pipelength);
        int bytesToRead = ((bytesPerPlane - bytesRead) > pipelength ? pipelength : (bytesPerPlane - bytesRead));

        itkDebugMacro("Writing " << bytesToRead << " bytes to plane " << i << ".  Bytes read: " << bytesRead);

		#ifdef WIN32
		    DWORD bytesWritten;
		    WriteFile( m_Pipe[1], data, bytesToRead, &bytesWritten, NULL );
		#else
			write( m_Pipe[1], data, bytesToRead );
		#endif

        data += bytesToRead;
        bytesRead += bytesToRead;

        itkDebugMacro("Waiting for confirmation of end of plane");

        std::string bytesDone;
        keepReading = true;
        while( keepReading )
          {
          int retcode = itksysProcess_WaitForData( m_Process, &pipedata, &pipedatalength, NULL );
          if( retcode == itksysProcess_Pipe_STDOUT )
            {
            bytesDone += std::string( pipedata, pipedatalength );
            // if the two last char are "\n\n", then we're done
#ifdef WIN32
		    if( bytesDone.size() >= 4 && bytesDone.substr( bytesDone.size()-4, 4 ) == "\r\n\r\n" )
#else
            if( bytesDone.size() >= 2 && bytesDone.substr( bytesDone.size()-2, 2 ) == "\n\n" )
#endif
              {
              keepReading = false;
              }
            }
          else if( retcode == itksysProcess_Pipe_STDERR )
            {
            errorMessage += std::string( pipedata, pipedatalength );
            //itkDebugMacro( "In end of data loop. errorMessage: " << bytesDone);
            }
          else
            {
            DestroyJavaProcess();
            itkExceptionMacro(<<"BioFormatsImageIO: 'ITKBridgePipe Write' exited abnormally. " << errorMessage);
            }
          }

        itkDebugMacro("BioFormatsImageIO::Write error output: " << errorMessage);
        itkDebugMacro("Read bytesDone: " << bytesDone);
      }

      std::string planeDone;
      keepReading = true;
      while( keepReading )
        {
        int retcode = itksysProcess_WaitForData( m_Process, &pipedata, &pipedatalength, NULL );
        if( retcode == itksysProcess_Pipe_STDOUT )
          {
          planeDone += std::string( pipedata, pipedatalength );
          // if the two last char are "\n\n", then we're done
#ifdef WIN32
		  if( planeDone.size() >= 4 && planeDone.substr( planeDone.size()-4, 4 ) == "\r\n\r\n" )
#else
          if( planeDone.size() >= 2 && planeDone.substr( planeDone.size()-2, 2 ) == "\n\n" )
#endif
            {
            keepReading = false;
            }
          }
        else if( retcode == itksysProcess_Pipe_STDERR )
          {
          errorMessage += std::string( pipedata, pipedatalength );
          //itkDebugMacro( "In end of plane loop. errorMessage: " << planeDone);
          }
        else
          {
          DestroyJavaProcess();
          itkExceptionMacro(<<"BioFormatsImageIO: 'ITKBridgePipe Write' exited abnormally. " << errorMessage);
          }
        }

       itkDebugMacro("BioFormatsImageIO::Write error output: " << errorMessage);
       itkDebugMacro("Read planeDone: " << planeDone);
  }
}
} // end namespace itk
