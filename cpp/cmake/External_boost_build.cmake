# Build boost

if(WIN32)

  execute_process(COMMAND ./b2 install --prefix=${BOOST_INSTALL_DIR}
                  --without-python
                  WORKING_DIRECTORY ${BUILD_DIR} RESULT_VARIABLE build_result)

else(WIN32)

  execute_process(COMMAND ./b2 install cxxflags=${CXXFLAGS} linkflags=${LDFLAGS}
                  WORKING_DIRECTORY ${BUILD_DIR} RESULT_VARIABLE build_result)

endif(WIN32)

if (build_result)
  message(FATAL_ERROR "boost: Build failed")
endif()
