# Generic cmake configuration
include("${EP_SCRIPT_CONFIG}")
include("${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake")

message(STATUS "Running \"${CMAKE_COMMAND}\"
                          -G \"${CMAKE_GENERATOR}\"
                          \"-DCMAKE_INSTALL_PREFIX=${CMAKE_INSTALL_PREFIX}\"
                          \"-DCMAKE_PREFIX_PATH=${CMAKE_PREFIX_PATH}\"
                          ${CONFIGURE_OPTIONS}
                          \"${SOURCE_DIR}\"")

  execute_process(COMMAND "${CMAKE_COMMAND}"
                          -G "${CMAKE_GENERATOR}"
                          "-DCMAKE_INSTALL_PREFIX=${CMAKE_INSTALL_PREFIX}"
                          "-DCMAKE_PREFIX_PATH=${CMAKE_PREFIX_PATH}"
                          ${CONFIGURE_OPTIONS}
                          "${SOURCE_DIR}"
                  WORKING_DIRECTORY "${BUILD_DIR}"
                  RESULT_VARIABLE configure_result)

if(configure_result)
  message(FATAL_ERROR "cmake: Configure failed")
endif()
