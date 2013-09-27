set(TEST_MAIN ${CMAKE_CURRENT_SOURCE_DIR}/main.cpp)

function(headers_test component headers)
message("HEADERS: ${headers}")
  set(test_headers_SOURCES "")
  set(headerdir ${GEN_DIR}/test/${component}/headers)
  file(MAKE_DIRECTORY ${headerdir})
  foreach(header ${headers})
message("HEADER: ${header}")
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
  add_executable(headers ${TEST_MAIN} ${test_headers_SOURCES})
  target_link_libraries(headers ${GTEST_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})
endfunction(headers_test)

add_subdirectory(ome-xerces)
add_subdirectory(ome-xml)
