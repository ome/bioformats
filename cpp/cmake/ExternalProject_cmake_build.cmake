# Generic cmake installation
include("${EP_SCRIPT_CONFIG}")
include("${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake")

if(CMAKE_GENERATOR MATCHES "Unix Makefiles")

  execute_process(COMMAND ${CMAKE_MAKE_PROGRAM}
                  WORKING_DIRECTORY "${EP_BUILD_DIR}"
                  RESULT_VARIABLE install_result)

else()

  execute_process(COMMAND "${CMAKE_COMMAND}" --build .
                                             --config "${CONFIG}"
                  WORKING_DIRECTORY "${EP_BUILD_DIR}"
                  RESULT_VARIABLE install_result)

endif()

if(install_result)
  message(FATAL_ERROR "cmake: Install failed")
endif()
