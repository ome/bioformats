# Build xerces
include(${EP_SCRIPT_CONFIG})
include(${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake)
include(${CMAKE_CURRENT_LIST_DIR}/External_xerces_common.cmake)

if(WIN32)

  message(STATUS "Building xerces (Windows)")

  execute_process(COMMAND msbuild "projects\\Win32\\${XERCES_SOLUTION}\\xerces-all\\xerces-all.sln"
                          "/p:Configuration=${XERCES_CONFIG}"
                          "/p:Platform=${XERCES_PLATFORM}"
                          "/p:useenv=true" "/v:d"
                  WORKING_DIRECTORY ${SOURCE_DIR}
                  RESULT_VARIABLE build_result)

else(WIN32)

  message(STATUS "Building xerces (Unix)")

  execute_process(COMMAND ${CMAKE_MAKE_PROGRAM}
                  WORKING_DIRECTORY ${BUILD_DIR}
                  RESULT_VARIABLE build_result)

endif(WIN32)

if (build_result)
  message(FATAL_ERROR "xerces: Build failed")
endif()
