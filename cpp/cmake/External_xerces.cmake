# xerces superbuild
set(proj xerces)

# Set dependency list
set(xerces_DEPENDENCIES zlib bzip2)

if(${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  unset(xerces_DIR CACHE)
  find_package(XERCES REQUIRED)
endif()

if(NOT ${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  set(EP_SOURCE_DIR ${CMAKE_BINARY_DIR}/${proj}-source)
  set(EP_BINARY_DIR ${CMAKE_BINARY_DIR}/${proj}-build)
  set(EP_CXXFLAGS ${CMAKE_CXX_FLAGS})
  set(EP_LDFLAGS ${CMAKE_SHARED_LINKER_FLAGS})
  if(WIN32)
    # Windows compiler flags
  else()
    set(EP_CXXFLAGS ${EP_CXXFLAGS} \"-I${BIOFORMATS_EP_INCLUDE_DIR}\")
    set(EP_LDFLAGS ${EP_LDFLAGS} \"-L${BIOFORMATS_EP_LIB_DIR}\")
  endif()

  # Notes:
  # Builds xerces without Xerces.Python (not currently used by Bio-Formats)

  ExternalProject_Add(${proj}
    ${BIOFORMATS_EP_COMMON_ARGS}
    URL "http://www.apache.org/dist/xerces/c/3/sources/xerces-c-3.1.2.tar.xz"
    URL_HASH "SHA512=d4517d80f6f0ec462982a0327f10bbd96206c0f24adc6d32e4482ad07b48be7404d8923fc6c8172ebdfa8ae0c5f403eea9db4b62f8263aa017e67889b1869cd8"
    SOURCE_DIR "${EP_SOURCE_DIR}"
    BINARY_DIR "${EP_BINARY_DIR}"
    INSTALL_DIR ""
    ${cmakeversion_external_update} "${cmakeversion_external_update_value}"
    CONFIGURE_COMMAND
      ${CMAKE_COMMAND}
      "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}"
      "-DBUILD_DIR:PATH=${EP_BINARY_DIR}"
      "-DXERCES_INSTALL_DIR:PATH=${BIOFORMATS_EP_INSTALL_DIR}"
      "-DLIB_DIR:PATH=${CMAKE_INSTALL_LIBDIR}"
      "-DXERCES_CXX:PATH=${CMAKE_CXX_COMPILER}"
      "-DCXXFLAGS=${EP_CXXFLAGS}"
      "-DLDFLAGS=${EP_LDFLAGS}"
      -P ${CMAKE_CURRENT_LIST_DIR}/External_xerces_configure.cmake
    BUILD_COMMAND ${CMAKE_COMMAND}
      "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}"
      "-DBUILD_DIR:PATH=${EP_BINARY_DIR}"
      "-DXERCES_INSTALL_DIR:PATH=${BIOFORMATS_EP_INSTALL_DIR}"
      "-DLIB_DIR:PATH=${CMAKE_INSTALL_LIBDIR}"
      "-DCXXFLAGS=${EP_CXXFLAGS}"
      "-DLDFLAGS=${EP_LDFLAGS}"
      -P "${CMAKE_CURRENT_LIST_DIR}/External_xerces_build.cmake"
    INSTALL_COMMAND ""
    DEPENDS
      ${xerces_DEPENDENCIES}
    )
else()
  ExternalProject_Add_Empty(${proj} DEPENDS ${xerces_DEPENDENCIES})
endif()
