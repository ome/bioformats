# bzip2 superbuild
set(proj bzip2)

# Set dependency list
set(bzip2_DEPENDENCIES)

if(${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  unset(bzip2_DIR CACHE)
  find_package(BZIP2 REQUIRED)
endif()

if(NOT ${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  set(EP_SOURCE_DIR ${CMAKE_BINARY_DIR}/${proj}-source)
  set(EP_BINARY_DIR ${CMAKE_BINARY_DIR}/${proj}-build)

  # Notes:
  # INSTALL_LIB_DIR overridden to use GNUInstallDirs setting

  ExternalProject_Add(${proj}
    ${BIOFORMATS_EP_COMMON_ARGS}
    URL "http://bzip.org/1.0.6/bzip2-1.0.6.tar.gz"
    URL_HASH "SHA512=00ace5438cfa0c577e5f578d8a808613187eff5217c35164ffe044fbafdfec9e98f4192c02a7d67e01e5a5ccced630583ad1003c37697219b0f147343a3fdd12"
    SOURCE_DIR "${EP_SOURCE_DIR}"
    INSTALL_DIR ""
    BUILD_IN_SOURCE 1
    PATCH_COMMAND ${CMAKE_COMMAND}
      "-DPATCH_DIR:PATH=${EP_SOURCE_DIR}"
      -P "${CMAKE_CURRENT_LIST_DIR}/External_bzip2_patch.cmake"
    CONFIGURE_COMMAND ""
    BUILD_COMMAND ${CMAKE_COMMAND}
      "-DBUILD_DIR:PATH=${EP_SOURCE_DIR}"
      "-DINSTALL_DIR:PATH=${BIOFORMATS_EP_INSTALL_DIR}"
      "-DLIB_DIR:PATH=${CMAKE_INSTALL_LIBDIR}"
      -P "${CMAKE_CURRENT_LIST_DIR}/External_bzip2_build.cmake"
    INSTALL_COMMAND ""
    DEPENDS
      ${bzip2_DEPENDENCIES}
    )
else()
  ExternalProject_Add_Empty(${proj} DEPENDS ${bzip2_DEPENDENCIES})
endif()

