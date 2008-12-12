
#include "jace/JNIHelper.h"

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif


#ifdef _WIN32

#ifndef VC_EXTRALEAN
#define VC_EXTRALEAN		// Exclude rarely-used stuff from Windows headers
#endif
#include <windows.h>

#include "iostream"
using std::cerr;
using std::endl;

//
// Used to initialize thread-local variables
//
BOOL WINAPI DllMain(HMODULE hModule, DWORD fdwReason, LPVOID lpReserved)
{
	switch (fdwReason)
	{
		case DLL_PROCESS_ATTACH:
			// Initialize once for each new process.
			// Return FALSE to fail DLL load.
			try
			{
				jace::helper::onProcessCreation();
			}
			catch (std::exception& e)
			{
				cerr << e.what() << endl;
			}
			break;

		case DLL_THREAD_ATTACH:
			// Do thread-specific initialization.
			try
			{
				jace::helper::onThreadCreation();
			}
			catch (std::exception& e)
			{
				cerr << e.what() << endl;
			}
			break;

		case DLL_THREAD_DETACH:
			// Do thread-specific cleanup.
			try
			{
				jace::helper::onThreadDestruction();
			}
			catch (std::exception& e)
			{
				cerr << e.what() << endl;
			}
			break;

		case DLL_PROCESS_DETACH:
			// Perform any necessary cleanup.
			try
			{
				jace::helper::onProcessDestruction();
			}
			catch (std::exception& e)
			{
				cerr << e.what() << endl;
			}
			break;
		}
	return TRUE;  // Successful DLL_PROCESS_ATTACH.
}

#endif //_WIN32
