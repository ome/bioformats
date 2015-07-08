# bioformats superbuild
set(proj bioformats)

# Set dependency list
set(bioformats_DEPENDENCIES zlib bzip2 png tiff icu boost xerces)
set(bioformats_ARGS)

set(EP_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR})
set(EP_BINARY_DIR ${CMAKE_BINARY_DIR}/${proj}-build)
set(EP_INSTALL_DIR ${CMAKE_BINARY_DIR}/${proj}-install)

list(APPEND CONFIGURE_OPTIONS
     ${bioformats_ARGS}
     "-Dbioformats-superbuild:BOOL=OFF")
string(REPLACE ";" "^^" BIOFORMATS_EP_ESCAPED_CMAKE_PREFIX_PATH "${CMAKE_PREFIX_PATH}")

ExternalProject_Add(${proj}
  ${BIOFORMATS_EP_COMMON_ARGS}
  DOWNLOAD_COMMAND ""
  SOURCE_DIR ${EP_SOURCE_DIR}
  BINARY_DIR ${EP_BINARY_DIR}
  CONFIGURE_COMMAND ${CMAKE_COMMAND}
    "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}"
    "-DBUILD_DIR:PATH=${EP_BINARY_DIR}"
    "-DCONFIGURE_OPTIONS=${CONFIGURE_OPTIONS}"
    "-DCONFIG:INTERNAL=$<CONFIG>"
    "-DEP_SCRIPT_CONFIG:FILEPATH=${EP_SCRIPT_CONFIG}"
    -P "${CMAKE_CURRENT_LIST_DIR}/ExternalProject_cmake_configure.cmake"
  INSTALL_DIR ""
  INSTALL_COMMAND ${CMAKE_COMMAND}
    "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}"
    "-DBUILD_DIR:PATH=${EP_BINARY_DIR}"
    "-DCONFIG:INTERNAL=$<CONFIG>"
    "-DEP_SCRIPT_CONFIG:FILEPATH=${EP_SCRIPT_CONFIG}"
    -P "${CMAKE_CURRENT_LIST_DIR}/ExternalProject_cmake_install.cmake"
  DEPENDS
    ${bioformats_DEPENDENCIES}
  )

# Force rebuilding of the main subproject every time building from super structure
ExternalProject_Add_Step(${proj} forcebuild
    COMMAND ${CMAKE_COMMAND} -E remove
    ${CMAKE_CURRENT_BUILD_DIR}/${proj}-prefix/src/${proj}-stamp/${proj}-build
    DEPENDEES configure
    DEPENDERS build
    ALWAYS 1
  )

install(DIRECTORY ${EP_INSTALL_DIR}/
        DESTINATION ${CMAKE_INSTALL_PREFIX})
