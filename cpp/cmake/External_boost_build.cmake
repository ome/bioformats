# Build boost
include(${EP_SCRIPT_CONFIG})
include(${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake)

if(WIN32)

  execute_process(COMMAND ./b2 install
                               --prefix=${BIOFORMATS_EP_INSTALL_DIR}
                               --without-python
                  WORKING_DIRECTORY "${EP_SOURCE_DIR}"
                  RESULT_VARIABLE build_result)

else(WIN32)

  message(STATUS "Building boost (Unix) with toolset=${BOOST_TOOLSET} cxxflags=${CMAKE_CXX_FLAGS} linkflags=${CMAKE_SHARED_LINKER_FLAGS}")

  set(ENV{CXX} "${BOOST_CXX}")

  execute_process(COMMAND ./b2 install
                               "cxxflags=${CMAKE_CXX_FLAGS}"
                               "linkflags=${CMAKE_SHARED_LINKER_FLAGS}"
                               toolset=${BOOST_TOOLSET}
                  WORKING_DIRECTORY "${EP_SOURCE_DIR}"
                  RESULT_VARIABLE build_result)

endif(WIN32)

if (build_result)
  message(FATAL_ERROR "boost: Build failed")
endif()
