# Build boost

if(WIN32)

  execute_process(COMMAND nmake -f makefile.msc
                  WORKING_DIRECTORY ${BUILD_DIR} RESULT_VARIABLE build_result)

else(WIN32)

  if(APPLE)
    set(SOEXT "SOEXT=dylib")
  endif()
  execute_process(COMMAND make ${SOEXT}
                  COMMAND make PREFIX="${INSTALL_DIR}" ${SOEXT} install
                  WORKING_DIRECTORY ${BUILD_DIR} RESULT_VARIABLE build_result)

endif(WIN32)

if (build_result)
  message(FATAL_ERROR "bzip2: Build failed")
endif()

if(NOT WIN32)
  if (NOT "${LIB_DIR}" STREQUAL "lib")
    file(GLOB lib_files "${INSTALL_DIR}/lib/libbz2*")
    file(MAKE_DIRECTORY "${INSTALL_DIR}/${LIB_DIR}")
    foreach(lib ${lib_files})
      file(COPY ${lib} DESTINATION "${INSTALL_DIR}/${LIB_DIR}")
      file(REMOVE ${lib})
    endforeach()
  endif()
endif()
