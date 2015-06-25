# Build xerces

if(WIN32)

  # msbuild

else(WIN32)

  execute_process(COMMAND ${CMAKE_MAKE_PROGRAM} install "DESTDIR=${XERCES_INSTALL_DIR}"
                  WORKING_DIRECTORY ${BUILD_DIR}
                  RESULT_VARIABLE build_result)

endif(WIN32)

if (build_result)
  message(FATAL_ERROR "xerces: Build failed")
endif()
