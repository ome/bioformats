set(test_main_sources main.cpp)

add_library(testmain STATIC ${test_main_sources})

set(TEST_LIBS testmain ${GTEST_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})

function(headers_test component path)
  set(headerdir ${PROJECT_BINARY_DIR}/test/${component}/headers)
  file(MAKE_DIRECTORY ${headerdir})
  set(dirs ${PROJECT_SOURCE_DIR}/lib ${PROJECT_BINARY_DIR}/lib)
  foreach(dir ${dirs})
    file(GLOB_RECURSE headers RELATIVE
         "${dir}"
         "${dir}/${path}/*.h")

  foreach(header ${headers})
    string(REPLACE "/" "_" genheader ${header})
    string(REPLACE "${PROJECT_SOURCE_DIR}/src/" "" include ${header})
    string(REPLACE "${headerdir}/" "" include ${include})
    string(REGEX REPLACE "\\.h$" ".cpp" genheader ${genheader})
    string(REGEX REPLACE "[/.]" "_" safeheader ${include})
    string(CONFIGURE "#include <@include@>

#include <gtest/gtest.h>

TEST(Header, ${safeheader})
{
}
" src)
    file(WRITE "${headerdir}/${genheader}" "${src}")
    list(APPEND test_headers_SOURCES "${headerdir}/${genheader}")
  endforeach(header)
  endforeach(dir)

  add_executable(${component}-headers ${test_headers_SOURCES})
  target_link_libraries(${component}-headers ${TEST_LIBS})
endfunction(headers_test)

add_subdirectory(ome-compat)
add_subdirectory(ome-xerces)
add_subdirectory(ome-xml)
