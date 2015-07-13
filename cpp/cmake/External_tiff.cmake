# tiff superbuild
set(proj tiff)

# Set dependency list
set(tiff_DEPENDENCIES zlib)

if(${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  unset(tiff_DIR CACHE)
  find_package(TIFF REQUIRED)
endif()

if(NOT ${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  set(EP_SOURCE_DIR ${CMAKE_BINARY_DIR}/${proj}-source)
  set(EP_BINARY_DIR ${CMAKE_BINARY_DIR}/${proj}-build)

  # Notes: Using custom CMake build for tiff; this has been submitted
  # upstream and may be included in a future release.  If so, the
  # files copied in the patch step may be dropped.

  ExternalProject_Add(${proj}
    ${BIOFORMATS_EP_COMMON_ARGS}
    URL "ftp://ftp.remotesensing.org/pub/libtiff/tiff-4.0.4.tar.gz"
    URL_HASH "SHA512=4c83f8d7c10224c481c58721044813056b6fbc44c94b1b94a35d2f829bf4a89d35edd878e40e4d8579fd04b889edab946c95b0dc04b090794d1bd9120a79882b"
    SOURCE_DIR "${EP_SOURCE_DIR}"
    BINARY_DIR "${EP_BINARY_DIR}"
    INSTALL_DIR ""
    PATCH_COMMAND ${CMAKE_COMMAND} -E copy_directory
      "${CMAKE_CURRENT_LIST_DIR}/External_tiff_files"
      "${EP_SOURCE_DIR}"
    INSTALL_COMMAND ${CMAKE_COMMAND}
      "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}"
      "-DBUILD_DIR:PATH=${EP_BINARY_DIR}"
      "-DCONFIG:INTERNAL=$<CONFIG>"
      "-DEP_SCRIPT_CONFIG:FILEPATH=${EP_SCRIPT_CONFIG}"
      -P "${CMAKE_CURRENT_LIST_DIR}/ExternalProject_cmake_install.cmake"
    TEST_COMMAND ${CMAKE_COMMAND}
      "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}"
      "-DBUILD_DIR:PATH=${EP_BINARY_DIR}"
      "-DCONFIG:INTERNAL=$<CONFIG>"
      "-DEP_SCRIPT_CONFIG:FILEPATH=${EP_SCRIPT_CONFIG}"
      -P "${CMAKE_CURRENT_LIST_DIR}/ExternalProject_cmake_test.cmake"
    ${cmakeversion_external_update} "${cmakeversion_external_update_value}"
    CMAKE_ARGS
      -Wno-dev --no-warn-unused-cli
      "-DINSTALL_LIB_DIR=/${CMAKE_INSTALL_LIBDIR}"
    CMAKE_CACHE_ARGS
    DEPENDS
      ${zlib_DEPENDENCIES}
    )
else()
  ExternalProject_Add_Empty(${proj} DEPENDS ${tiff_DEPENDENCIES})
endif()

