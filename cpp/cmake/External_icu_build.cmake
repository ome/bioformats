# Build icu

if(WIN32)

  # msbuild

else(WIN32)

  execute_process(COMMAND make install "DESTDIR=${ICU_INSTALL_DIR}"
                  WORKING_DIRECTORY ${BUILD_DIR}
                  RESULT_VARIABLE build_result)

endif(WIN32)

if (build_result)
  message(FATAL_ERROR "icu: Build failed")
endif()
