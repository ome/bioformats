# Generic cmake installation
include("${EP_SCRIPT_CONFIG}")
include("${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake")

if(CMAKE_GENERATOR MATCHES "Unix Makefiles")

  execute_process(COMMAND ${CMAKE_MAKE_PROGRAM} install "DESTDIR=${BIOFORMATS_EP_INSTALL_DIR}"
                  WORKING_DIRECTORY "${EP_BUILD_DIR}"
                  RESULT_VARIABLE install_result)

else()

  execute_process(COMMAND "${CMAKE_COMMAND}" --build .
                                             --target install
                                             --config "${CONFIG}"
                                             -- ${MAKE_VERBOSE}
                  WORKING_DIRECTORY "${EP_BUILD_DIR}"
                  RESULT_VARIABLE install_result)

endif(WIN32)

if(install_result)
  message(FATAL_ERROR "cmake: Install failed")
endif()
