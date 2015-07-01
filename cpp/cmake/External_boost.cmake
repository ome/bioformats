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
    set(EP_TOOLSET msvc)
  elseif (CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    set(EP_TOOLSET clang)
  elseif (CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
    set(EP_TOOLSET gcc)
  else()
    message(FATAL_ERROR "Unsupported Boost toolset for compiler type ${CMAKE_CXX_COMPILER_ID}; please report this deficiency.")
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
      "-DEP_SOURCE_DIR:PATH=${EP_SOURCE_DIR}"
      "-DBOOST_INSTALL_DIR:PATH=${BIOFORMATS_EP_INSTALL_DIR}"
      "-DBOOST_TOOLSET=${EP_TOOLSET}"
      "-DEP_SCRIPT_CONFIG=${EP_SCRIPT_CONFIG}"
      -P ${CMAKE_CURRENT_LIST_DIR}/External_boost_configure.cmake
    BUILD_IN_SOURCE 1
    BUILD_COMMAND ${CMAKE_COMMAND}
      "-DEP_SOURCE_DIR:PATH=${EP_SOURCE_DIR}"
      "-DEP_INSTALL_DIR:PATH=${BIOFORMATS_EP_INSTALL_DIR}"
      "-DBOOST_TOOLSET=${EP_TOOLSET}"
      "-DEP_SCRIPT_CONFIG=${EP_SCRIPT_CONFIG}"
      -P "${CMAKE_CURRENT_LIST_DIR}/External_boost_build.cmake"
    INSTALL_COMMAND ""
    DEPENDS
      ${boost_DEPENDENCIES}
    )
else()
  ExternalProject_Add_Empty(${proj} DEPENDS ${boost_DEPENDENCIES})
endif()
