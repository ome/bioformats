# boost superbuild
set(proj boost)

# Set dependency list
set(boost_DEPENDENCIES zlib bzip2 icu)

if(${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  unset(boost_DIR CACHE)
  find_package(BOOST REQUIRED)
endif()

if(NOT ${CMAKE_PROJECT_NAME}_USE_SYSTEM_${proj})
  set(EP_SOURCE_DIR ${CMAKE_BINARY_DIR}/${proj}-source)

  if (CMAKE_CXX_COMPILER_ID STREQUAL "MSVC")
    # VS 10.0
    if(NOT MSVC_VERSION VERSION_LESS 1600 AND MSVC_VERSION VERSION_LESS 1700)
      set(BOOST_TOOLSET msvc-10.0)
    # VS 11.0
    elseif(NOT MSVC_VERSION VERSION_LESS 1700 AND MSVC_VERSION VERSION_LESS 1800)
      set(BOOST_TOOLSET msvc-11.0)
    # VS 12.0
    elseif(NOT MSVC_VERSION VERSION_LESS 1800 AND MSVC_VERSION VERSION_LESS 1900)
      set(BOOST_TOOLSET msvc-12.0)
    # VS 14.0
    elseif(NOT MSVC_VERSION VERSION_LESS 1900 AND MSVC_VERSION VERSION_LESS 2000)
      set(BOOST_TOOLSET msvc-14.0)
    else()
      set(BOOST_TOOLSET msvc)
    endif()
  elseif (CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    set(BOOST_TOOLSET clang)
  elseif (CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
    set(BOOST_TOOLSET gcc)
  else()
    message(FATAL_ERROR "Unsupported Boost toolset for compiler type ${CMAKE_CXX_COMPILER_ID}.  Please report this deficiency.")
  endif()

  # Notes:
  # Builds boost without Boost.Python (not currently used by Bio-Formats)

  ExternalProject_Add(${proj}
    ${BIOFORMATS_EP_COMMON_ARGS}
    URL "http://sourceforge.net/projects/boost/files/boost/1.58.0/boost_1_58_0.tar.bz2"
    URL_HASH "SHA512=7480ec713b0aa13f0ec990603e87e3b5c8d53f4411329b10fae37fc963b90aad12dbd9290a33c3669ae801e9012a68683eadff057591e9ca2ebcd22b1a67b5d1"
    SOURCE_DIR "${EP_SOURCE_DIR}"
    INSTALL_DIR ""
    ${cmakeversion_external_update} "${cmakeversion_external_update_value}"
    CONFIGURE_COMMAND
      ${CMAKE_COMMAND}
      ${boost_CLANG_ARG}
      "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}"
      "-DBOOST_TOOLSET=${BOOST_TOOLSET}"
      "-DCONFIG:INTERNAL=$<CONFIG>"
      "-DEP_SCRIPT_CONFIG=${EP_SCRIPT_CONFIG}"
      -P ${CMAKE_CURRENT_LIST_DIR}/External_boost_configure.cmake
    BUILD_IN_SOURCE 1
    BUILD_COMMAND ${CMAKE_COMMAND}
      "-DSOURCE_DIR:PATH=${EP_SOURCE_DIR}"
      "-DEP_INSTALL_DIR:PATH=${BIOFORMATS_EP_INSTALL_DIR}"
      "-DBOOST_TOOLSET=${BOOST_TOOLSET}"
      "-DBOOST_BITS=${BOOST_BITS}"
      "-DCONFIG:INTERNAL=$<CONFIG>"
      "-DEP_SCRIPT_CONFIG=${EP_SCRIPT_CONFIG}"
      -P "${CMAKE_CURRENT_LIST_DIR}/External_boost_build.cmake"
    INSTALL_COMMAND ""
    DEPENDS
      ${boost_DEPENDENCIES}
    )
else()
  ExternalProject_Add_Empty(${proj} DEPENDS ${boost_DEPENDENCIES})
endif()
