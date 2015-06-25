# icu superbuild
set(proj icu)

# Set dependency list
set(icu_DEPENDENCIES zlib bzip2)

if(${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  unset(icu_DIR CACHE)
  find_package(ICU REQUIRED)
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
  # Builds icu without Icu.Python (not currently used by Bio-Formats)

  ExternalProject_Add(${proj}
    ${BIOFORMATS_EP_COMMON_ARGS}
    URL "http://download.icu-project.org/files/icu4c/55.1/icu4c-55_1-src.tgz"
    URL_HASH "SHA512=21a3eb2c3678cd27b659eed073f8f1bd99c9751291d077820e9a370fd90b7d9b3bf414cc03dec4acb7fa61087e02d04f9f40e91a32c5180c718e2102fbd0cd35"
    SOURCE_DIR "${EP_SOURCE_DIR}"
    BINARY_DIR "${EP_BINARY_DIR}"
    INSTALL_DIR ""
    ${cmakeversion_external_update} "${cmakeversion_external_update_value}"
    CONFIGURE_COMMAND
      ${CMAKE_COMMAND}
      "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}/source"
      "-DBUILD_DIR:PATH=${EP_BINARY_DIR}"
      "-DICU_INSTALL_DIR:PATH=${BIOFORMATS_EP_INSTALL_DIR}"
      "-DLIB_DIR:PATH=${CMAKE_INSTALL_LIBDIR}"
      "-DICU_C:PATH=${CMAKE_C_COMPILER}"
      "-DICU_CXX:PATH=${CMAKE_CXX_COMPILER}"
      "-DCXXFLAGS=${EP_CXXFLAGS}"
      "-DLDFLAGS=${EP_LDFLAGS}"
      -P ${CMAKE_CURRENT_LIST_DIR}/External_icu_configure.cmake
    BUILD_COMMAND ${CMAKE_COMMAND}
      "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}/source"
      "-DBUILD_DIR:PATH=${EP_BINARY_DIR}"
      "-DICU_INSTALL_DIR:PATH=${BIOFORMATS_EP_INSTALL_DIR}"
      "-DLIB_DIR:PATH=${CMAKE_INSTALL_LIBDIR}"
      "-DCXXFLAGS=${EP_CXXFLAGS}"
      "-DLDFLAGS=${EP_LDFLAGS}"
      -P "${CMAKE_CURRENT_LIST_DIR}/External_icu_build.cmake"
    INSTALL_COMMAND ""
    DEPENDS
      ${icu_DEPENDENCIES}
    )
else()
  ExternalProject_Add_Empty(${proj} DEPENDS ${icu_DEPENDENCIES})
endif()
