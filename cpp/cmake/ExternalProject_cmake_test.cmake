# Generic cmake testing
include("${EP_SCRIPT_CONFIG}")
include("${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake")

execute_process(COMMAND "${CMAKE_CTEST_COMMAND}" -C "${CONFIG}" -V
                WORKING_DIRECTORY "${EP_BUILD_DIR}"
                RESULT_VARIABLE test_result)

if(test_result)
  message(FATAL_ERROR "cmake: Testing failed")
endif()
