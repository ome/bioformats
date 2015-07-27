# Configure xerces
include(${EP_SCRIPT_CONFIG})
include(${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake)

if(WIN32) # Use appropriate MSVC solution

  message(STATUS "Bootstrapping xerces (Windows)")

else(WIN32)

  message(STATUS "Bootstrapping xerces (Unix)")

  set(ENV{CXX} "${XERCES_CXX}")

  execute_process(COMMAND "${SOURCE_DIR}/configure"
                          "--prefix=/"
                          "--libdir=/${CMAKE_INSTALL_LIBDIR}"
                          "--with-pkgconfigdir=${BIOFORMATS_EP_INSTALL_DIR}/${CMAKE_INSTALL_LIBDIR}/pkgconfig"
                  WORKING_DIRECTORY ${BUILD_DIR}
                  RESULT_VARIABLE configure_result)

endif(WIN32)

if (configure_result)
  message(FATAL_ERROR "xerces: Configure failed")
endif()
