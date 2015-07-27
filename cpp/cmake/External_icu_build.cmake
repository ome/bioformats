# Build icu
include(${EP_SCRIPT_CONFIG})
include(${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake)

if(WIN32)

  message(STATUS "Building icu (Windows)")

  execute_process(COMMAND msbuild "source\\allinone\\allinone.sln"
                          "/p:Configuration=${CONFIG}"
                          "/p:Platform=${CMAKE_VS_PLATFORM_NAME}"
                  WORKING_DIRECTORY ${SOURCE_DIR}
                  RESULT_VARIABLE build_result)

else()

  message(STATUS "Building icu (Unix)")

  execute_process(COMMAND ${CMAKE_MAKE_PROGRAM}
                  WORKING_DIRECTORY ${BUILD_DIR}
                  RESULT_VARIABLE build_result)

endif()

if (build_result)
  message(FATAL_ERROR "icu: Build failed")
endif()
