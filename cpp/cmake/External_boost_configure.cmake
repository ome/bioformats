# Configure (bootstrap) boost

if(WIN32) # bootstrap.bat has no options, the options are given to ./b2 when building

  message(STATUS "Bootstrapping boost (Windows)")
  execute_process(COMMAND bootstrap.bat
                  WORKING_DIRECTORY ${BUILD_DIR}
                  RESULT_VARIABLE boostrap_result)

else(WIN32)

  message(STATUS "Bootstrapping boost (Unix)")

  set(ENV{CXX} "${BOOST_CXX}")

  execute_process(COMMAND ./bootstrap.sh
                          --prefix=${BOOST_INSTALL_DIR}
                          --libdir=${BOOST_INSTALL_DIR}/${LIB_DIR}
                          --without-libraries=python
                          --with-toolset=${BOOST_TOOLSET}
                  WORKING_DIRECTORY ${BUILD_DIR}
                  RESULT_VARIABLE boostrap_result)

endif(WIN32)

if (bootstrap_result)
  message(FATAL_ERROR "boost: Configure (bootstrap) failed")
endif()
