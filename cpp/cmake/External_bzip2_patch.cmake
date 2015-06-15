# Build boost

if(WIN32)

  # No patch command, and the patch is mainly for shared libs on Unix.

else(WIN32)

  # Apply reworked Debian patch (20-legacy) to build a versioned
  # shared library; adapted to build a .dylib on MacOS X.

  execute_process(COMMAND patch -p1
                  INPUT_FILE "${CMAKE_CURRENT_LIST_DIR}/External_bzip2_legacy.patch"
                  WORKING_DIRECTORY "${BUILD_DIR}"
                  RESULT_VARIABLE patch_result)

endif(WIN32)

if (patch_result)
  message(FATAL_ERROR "bzip2: Patching failed")
endif()
