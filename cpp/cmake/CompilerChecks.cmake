check_cxx_compiler_flag(-std=c++11 CXX_FLAG_CXX11)
if (CXX_FLAG_CXX11)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
else(CXX_FLAG_CXX11)
  check_cxx_compiler_flag(-std=c++03 CXX_FLAG_CXX03)
  if (CXX_FLAG_CXX03)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++03")
  else(CXX_FLAG_CXX03)
    check_cxx_compiler_flag(-std=c++98 CXX_FLAG_CXX98)
    if (CXX_FLAG_CXX98)
      set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++98")
    else(CXX_FLAG_CXX98)
    endif(CXX_FLAG_CXX98)
  endif(CXX_FLAG_CXX03)
endif(CXX_FLAG_CXX11)

set(test_flags
    -invalid-flag -pedantic -Wall -Wcast-align -Wwrite-strings
    -Wswitch-default -Wcast-qual -Wunused-variable -Wredundant-decls
    -Wctor-dtor-privacy -Wnon-virtual-dtor -Wreorder -Wold-style-cast
    -Woverloaded-virtual -fstrict-aliasing)

foreach(flag ${test_flags})
  set(test_cxx_flag "CXX_FLAG${flag}")
  CHECK_CXX_COMPILER_FLAG(${flag} "${test_cxx_flag}")
  if (${test_cxx_flag})
     set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${flag}")
  endif (${test_cxx_flag})
endforeach(flag ${test_flags})

check_include_file_cxx(tuple OME_HAVE_TUPLE)
check_include_file_cxx(tr1/tuple OME_HAVE_TR1_TUPLE)
check_include_file_cxx(cstdint OME_HAVE_CSTDINT)

check_cxx_source_compiles("
#include <memory>
struct foo : public std::enable_shared_from_this<foo>
{
        foo() {}
};
int main() { std::shared_ptr<foo> f(new foo()); }
" OME_HAVE_MEMORY)

check_cxx_source_compiles("
void foo() noexcept{}
int main() { foo(); }
" OME_HAVE_NOEXCEPT)
