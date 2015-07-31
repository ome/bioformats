# Configure (bootstrap) boost
include(${EP_SCRIPT_CONFIG})
include(${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake)

if(WIN32) # bootstrap.bat has no options, the options are given to ./b2 when building

  message(STATUS "Bootstrapping boost (Windows)")

  execute_process(COMMAND bootstrap.bat
                  WORKING_DIRECTORY ${SOURCE_DIR}
                  RESULT_VARIABLE boostrap_result)

else()

  message(STATUS "Bootstrapping boost (Unix)")

  execute_process(COMMAND ./bootstrap.sh
                          --prefix=${BIOFORMATS_EP_INSTALL_DIR}
                          --libdir=${BIOFORMATS_EP_LIB_DIR}
                          --without-libraries=python
                          --with-toolset=${BOOST_TOOLSET}
                  WORKING_DIRECTORY ${SOURCE_DIR}
                  RESULT_VARIABLE bootstrap_result)

  file(READ "${SOURCE_DIR}/project-config.jam" PROJECT_CONFIG)
  file(WRITE "${SOURCE_DIR}/project-config.jam" "using ${BOOST_TOOLSET} : : ${CMAKE_CXX_COMPILER} ;

${PROJECT_CONFIG}")

endif()

if (bootstrap_result)
  message(FATAL_ERROR "boost: Configure (bootstrap) failed")
endif()
