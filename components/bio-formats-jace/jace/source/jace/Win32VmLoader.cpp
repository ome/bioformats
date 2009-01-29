
#ifdef _WIN32

#include "jace/Win32VmLoader.h"
using ::jace::VmLoader;
using ::jace::Win32VmLoader;
using ::jace::JNIException;

#define WIN32_LEAN_AND_MEAN
#include <windows.h>

#include <string>
using std::string;

namespace {

  /**
   * Windows helper for querying the registry values related to the various JVM installations
   */
  bool getRegistryValue( const std::string& regPath, std::string& value ) {

    // Split into key and value name
    size_t lastSlash = regPath.rfind( '\\' );
    std::string keyName = regPath.substr( 0, lastSlash );
    std::string valueName = regPath.substr( lastSlash+1, regPath.length() - lastSlash );

    // Open the registry and get the data
    HKEY regKey;

    if ( RegOpenKeyEx(HKEY_LOCAL_MACHINE, keyName.c_str(), 0, KEY_READ, &regKey ) == ERROR_SUCCESS ) {                   

		  BYTE retBuf[ 1000 ];
		  DWORD retBufSize = sizeof( retBuf );
		  DWORD retType;
      LONG apiRetVal = RegQueryValueEx( regKey, valueName.c_str(), 0, &retType, retBuf, &retBufSize );

      RegCloseKey( regKey );

		  if ( apiRetVal == ERROR_SUCCESS )	{
			  if ( retType == REG_SZ ) {
				  value = ( char* ) retBuf;
				  return true;
			  }
		  }
    } 

	  return false;
  }
}


Win32VmLoader::Win32VmLoader( Win32VmLoader::JVMVendor jvmVendor, Win32VmLoader::JVMType jvmType, std::string version, jint jniVer ) : 
  path(), handle( 0 ), jniVersion( jniVer ) {

  getCreatedJavaVMsPtr = 0;
  createJavaVMPtr = 0;

  specifyVm( jvmVendor, jvmType, version );
}

Win32VmLoader::Win32VmLoader( std::string path_, jint version ) : 
  path( path_ ), handle( 0 ), jniVersion( version ) {

  getCreatedJavaVMsPtr = 0;
  createJavaVMPtr = 0;
}

void Win32VmLoader::loadVm() throw ( JNIException ) {
  loadVm( path );
}

void Win32VmLoader::unloadVm() {
  if ( handle ) {
    FreeLibrary( handle );
    handle = 0;
  }
}

void Win32VmLoader::specifyVm( Win32VmLoader::JVMVendor jvmVendor, Win32VmLoader::JVMType jvmType, std::string version ) {

	switch ( jvmVendor )
	{
		case JVMV_SUN:
		{
			// Get current version
			if ( version.empty() )
			{
				if ( !getRegistryValue("Software\\JavaSoft\\Java Runtime Environment\\CurrentVersion", version) )
				{	
					throw JNIException("No default Sun JRE found");
				}
			}
			// Search Registry for PATH to Sun runtime
			switch ( jvmType )
			{
 				case JVMT_DEFAULT:
					if ( !getRegistryValue("Software\\JavaSoft\\Java Runtime Environment\\" + 
																 version + "\\RuntimeLib", path) )
					{
						throw JNIException("Sun JRE " + version + " not found");
					}
					break;
				case JVMT_CLASSIC:
					if ( version.find("1.3") == 0 )
					{
						if ( getRegistryValue("Software\\JavaSoft\\Java Runtime Environment\\" + 
																	version + "\\JavaHome", path) )
						{
							path += "\\bin\\classic\\jvm.dll";
						}
						else
						{
							throw JNIException("Classic Sun JRE " + version + " not found");
						}
					}
					else
					{
						throw JNIException("Classic VM not available in Sun JRE " + version);
					}
					break;
				case JVMT_DEBUG:
					throw JNIException("Debug VM not available in Sun JRE");
				case JVMT_HOTSPOT:
					if ( version.find("1.3") == 0 )
					{
						if ( getRegistryValue("Software\\JavaSoft\\Java Runtime Environment\\" + 
																	version + "\\JavaHome", path) )
						{
							path += "\\bin\\hotspot\\jvm.dll";
						}
						else
						{
							throw JNIException("Hotspot Sun JRE " + version + " not found");
						}
					}
					else
					{
						throw JNIException("Hotspot VM not available in Sun JRE " + version + "\nUse Client VM instead");
					}
					break;
				case JVMT_SERVER:
					if ( getRegistryValue("Software\\JavaSoft\\Java Development Kit\\" + 
									              version + "\\JavaHome", path) )
					{
						path += "\\jre\\bin\\server\\jvm.dll";
					}
					else
					{
						throw JNIException("Sun JDK " + version + " not found");
					}
					break;
				case JVMT_CLIENT:
					if ( version.find("1.4") == 0 )
					{
						if ( getRegistryValue("Software\\JavaSoft\\Java Runtime Environment\\" + 
										              version + "\\JavaHome", path) )
						{
							path += "\\bin\\client\\jvm.dll";
						}
						else
						{
							throw JNIException("Client Sun JRE " + version + " not found");
						}
					}
					else
					{
						throw JNIException("Client VM not available in Sun JRE " + version + 
															 "\nUse Classic or Hotspot VM instead");
					}
					break;
			}
			break;
		}
		case JVMV_IBM:
		{
			// Search Registry for PATH to IBM runtime
			switch ( jvmType )
			{
 				case JVMT_DEFAULT:
				case JVMT_CLASSIC:
					if ( !getRegistryValue("Software\\IBM\\Java2 Runtime Environment\\1.3\\RuntimeLib", path) )
					{
						throw JNIException("IBM JRE 1.3 not found");
					}
					break;

				case JVMT_DEBUG:
					if ( getRegistryValue("Software\\IBM\\Java Development Kit\\1.3\\JavaHome", path) )
					{
						path += "\\jre\\bin\\classic\\jvm_g.dll";
					}
					else
					{
						throw JNIException("IBM JDK 1.3 not found");
					}
					break;

				case JVMT_HOTSPOT:
				throw JNIException("Hotspot VM not available in IBM JRE");

				case JVMT_SERVER:
				throw JNIException("Server VM not available in IBM JRE");

				case JVMT_CLIENT:
				throw JNIException("Client VM not available in IBM JRE");
			}
		}
		break;
	}
}

void Win32VmLoader::loadVm( const std::string &jvmPath ) throw ( JNIException ) {

  /* Load the Java VM DLL */
  if ( ( handle = LoadLibrary( jvmPath.c_str() ) ) == 0 ) {
    throw JNIException( std::string( "Can't load JVM from " ) + jvmPath );
  }

  /* Now get the function addresses */
  getCreatedJavaVMsPtr = ( GetCreatedJavaVMs_t )GetProcAddress(handle, "JNI_GetCreatedJavaVMs");
	if ( ! getCreatedJavaVMsPtr ) {
		throw JNIException(std::string("Can't find JNI_GetCreatedJavaVMs in ") + jvmPath);
	}

  createJavaVMPtr = (CreateJavaVM_t)GetProcAddress(handle, "JNI_CreateJavaVM");
	if ( ! createJavaVMPtr ) {
		throw JNIException(std::string("Can't find JNI_CreateJavaVM in ") + jvmPath);
	}
}

jint Win32VmLoader::version() {
  return jniVersion;
}

jint Win32VmLoader::createJavaVM( JavaVM **pvm, void **env, void *args ) {
  return createJavaVMPtr( pvm, env, args );
}

jint Win32VmLoader::getCreatedJavaVMs( JavaVM **vmBuf, jsize bufLen, jsize *nVMs ) {
  return getCreatedJavaVMsPtr( vmBuf, bufLen, nVMs );
}

VmLoader* Win32VmLoader::clone() const {
  return new Win32VmLoader( path, jniVersion );
}

#endif // _WIN32

