# Configure icu
include(${EP_SCRIPT_CONFIG})
include(${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake)

if(WIN32) # Use appropriate MSVC solution

  message(STATUS "Bootstrapping icu (Windows)")

else(WIN32)

  message(STATUS "Bootstrapping icu (Unix)")

  set(ENV{CC} "${ICU_C}")
  set(ENV{CXX} "${ICU_CXX}")

  execute_process(COMMAND "${SOURCE_DIR}/source/configure"
                          "--prefix=/"
                          "--libdir=/${CMAKE_INSTALL_LIBDIR}"
                  WORKING_DIRECTORY ${BUILD_DIR}
                  RESULT_VARIABLE configure_result)

endif(WIN32)

if (configure_result)
  message(FATAL_ERROR "icu: Configure failed")
endif()
