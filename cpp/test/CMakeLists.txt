set(TEST_MAIN ${CMAKE_CURRENT_SOURCE_DIR}/main.cpp)

function(headers_test component path)
  set(headerdir ${GEN_DIR}/test/${component}/headers)
  file(MAKE_DIRECTORY ${headerdir})
  set(dirs ${PROJECT_SOURCE_DIR}/src ${PROJECT_BINARY_DIR}/src ${PROJECT_BINARY_DIR}/gen)
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

  add_executable(${component}-headers ${TEST_MAIN} ${test_headers_SOURCES})
  target_link_libraries(${component}-headers ${GTEST_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})
endfunction(headers_test)

add_subdirectory(ome-compat)
add_subdirectory(ome-xerces)
add_subdirectory(ome-xml)
