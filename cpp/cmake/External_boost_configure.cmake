# Configure (bootstrap) boost
include(${EP_SCRIPT_CONFIG})
include(${CMAKE_CURRENT_LIST_DIR}/ExternalProjectEnvironment.cmake)

if(WIN32) # bootstrap.bat has no options, the options are given to ./b2 when building

  message(STATUS "Bootstrapping boost (Windows)")
  execute_process(COMMAND bootstrap.bat
                  WORKING_DIRECTORY ${EP_SOURCE_DIR}
                  RESULT_VARIABLE boostrap_result)

else(WIN32)

  message(STATUS "Bootstrapping boost (Unix)")

  set(ENV{CXX} "${BOOST_CXX}")
  file(WRITE "user-config.jam" "using ${BOOST_TOOLSET} : : ${BOOST_CXX}
")

  execute_process(COMMAND ./bootstrap.sh
                          --prefix=${BOOST_INSTALL_DIR}
                          --libdir=${BOOST_INSTALL_DIR}/${CMAKE_INSTALL_LIBDIR}
                          --without-libraries=python
                          --with-toolset=${BOOST_TOOLSET}
                  WORKING_DIRECTORY ${EP_SOURCE_DIR}
                  RESULT_VARIABLE bootstrap_result)

endif(WIN32)

if (bootstrap_result)
  message(FATAL_ERROR "boost: Configure (bootstrap) failed")
endif()
