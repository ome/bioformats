# png superbuild
set(proj png)

# Set dependency list
set(png_DEPENDENCIES zlib)

if(${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  unset(png_DIR CACHE)
  find_package(PNG REQUIRED)
endif()

if(NOT ${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  set(EP_SOURCE_DIR ${CMAKE_BINARY_DIR}/${proj}-source)
  set(EP_BINARY_DIR ${CMAKE_BINARY_DIR}/${proj}-build)

  # Notes:
  # INSTALL_LIB_DIR overridden to use GNUInstallDirs setting
  # Installs cmake settings into lib/libpng; could be deleted

  ExternalProject_Add(${proj}
    ${BIOFORMATS_EP_COMMON_ARGS}
    URL "ftp://ftp.heanet.ie/mirrors/download.sourceforge.net/pub/sourceforge/l/li/libpng/libpng16/1.6.17/libpng-1.6.17.tar.xz"
    URL_HASH "SHA512=f22a48b355adea197a2d79f90ccc6b3edef2b5e8f6fb17319bd38652959126bbecb9442fd95e5147a894484446e87e535667fbfcf3b1e901b8375e5bb00a3bf3"
    SOURCE_DIR "${EP_SOURCE_DIR}"
    BINARY_DIR "${EP_BINARY_DIR}"
    INSTALL_DIR ""
    INSTALL_COMMAND "make;install;DESTDIR=${BIOFORMATS_EP_INSTALL_DIR}"
    ${cmakeversion_external_update} "${cmakeversion_external_update_value}"
    CMAKE_ARGS
      -Wno-dev --no-warn-unused-cli
      "-DCMAKE_INSTALL_LIBDIR=/${CMAKE_INSTALL_LIBDIR}"
    DEPENDS
      ${png_DEPENDENCIES}
    )
else()
  ExternalProject_Add_Empty(${proj} DEPENDS ${png_DEPENDENCIES})
endif()
