# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2014 Open Microscopy Environment:
#   - Massachusetts Institute of Technology
#   - National Institutes of Health
#   - University of Dundee
#   - Board of Regents of the University of Wisconsin-Madison
#   - Glencoe Software, Inc.
# %%
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# The views and conclusions contained in the software and documentation are
# those of the authors and should not be interpreted as representing official
# policies, either expressed or implied, of any organization.
# #L%

function(cxx_std_check flag var)
  check_cxx_compiler_flag("${flag}" ${var})
  set(CMAKE_CXX_FLAGS_SAVE "${CMAKE_CXX_FLAGS}")
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${flag}")

  check_cxx_source_compiles("#include <cstdarg>

void format(const char *fmt, va_list ap)
{
  va_list ap2;
  va_copy(ap2, ap);
}

int main() {
}"
"${var}_CSTDARG")

  check_cxx_source_compiles("#include <stdarg.h>

void format(const char *fmt, va_list ap)
{
  va_list ap2;
  va_copy(ap2, ap);
}

int main() {
}"
"${var}_STDARG")

  if("${var}_CSTDARG" OR "${var}_STDARG")
    set(${var} ${${var}} PARENT_SCOPE)
  else("${var}_CSTDARG" OR "${var}_STDARG")
    set(${var} FALSE PARENT_SCOPE)
  endif("${var}_CSTDARG" OR "${var}_STDARG")


  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS_SAVE}")
endfunction(cxx_std_check)


# Try to put the compiler into the most recent standard mode.  This
# will generally have the most features, and will remove the need for
# Boost fallbacks if native implementations are available.
option(cxxstd-autodetect "Enable C++11 features if possible, otherwise fall back to C++03 and C++98" ON)
if (cxxstd-autodetect)
  cxx_std_check(-std=c++11 CXX_FLAG_CXX11)
  if (CXX_FLAG_CXX11)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
  else(CXX_FLAG_CXX11)
    cxx_std_check(-std=c++03 CXX_FLAG_CXX03)
    if (CXX_FLAG_CXX03)
      set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++03")
    else(CXX_FLAG_CXX03)
      cxx_std_check(-std=c++98 CXX_FLAG_CXX98)
      if (CXX_FLAG_CXX98)
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++98")
      else(CXX_FLAG_CXX98)
        cxx_std_check("" CXX_FLAG_NONE)
        if (NOT CXX_FLAG_NONE)
          message(ERROR "Failed to detect compiler options for Standard C++")
        endif (NOT CXX_FLAG_NONE)
      endif(CXX_FLAG_CXX98)
    endif(CXX_FLAG_CXX03)
  endif(CXX_FLAG_CXX11)
endif (cxxstd-autodetect)

# Try to enable the -pedantic flag.  This one needs special casing
# since it may break building with older compilers where int64_t (long
# long) isn't available in pedantic mode because it's not part of the
# C++98 standard.  Newer compilers support long long properly.
set(flag -pedantic)
set(test_cxx_flag "CXX_FLAG${flag}")
CHECK_CXX_COMPILER_FLAG(${flag} "${test_cxx_flag}")
if (${test_cxx_flag})
  SET(CMAKE_CXX_FLAGS_SAVE ${CMAKE_CXX_FLAGS})
  SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${flag}")
  check_cxx_source_compiles(
"int main() {
  long long l;
}"
CXX_PEDANTIC_LONG_LONG)
  SET(CMAKE_CXX_FLAGS ${CMAKE_CXX_FLAGS_SAVE})
  if (${CXX_PEDANTIC_LONG_LONG})
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${flag}")
  endif (${CXX_PEDANTIC_LONG_LONG})
endif (${test_cxx_flag})

# Check if the compiler supports each of the following additional
# warning flags, and enable them if supported.  This greatly improves
# the quality of the build by checking for a number of common
# problems, some of which are quite serious.
set(test_flags
    -Wall
    -Wcast-align
    -Wcast-qual
    -Wctor-dtor-privacy
    -Wextra
    -Wformat=2
    -Wimplicit-atomic-properties
    -Wmissing-declarations
    -Wnon-virtual-dtor
    -Wold-style-cast
    -Woverlength-strings
    -Woverloaded-virtual
    -Wredundant-decls
    -Wreorder
    -Wswitch-default
    -Wunused-variable
    -Wwrite-strings
    -fstrict-aliasing)

# These are annoyingly verbose, produce false positives or don't work
# nicely with all supported compiler versions, so are disabled unless
# explicitly enabled.
option(extra-warnings "Enable extra compiler warnings" OFF)
if (extra-warnings)
list(APPEND test_flags
    -Wconversion
    -Wdocumentation
    -Wfloat-equal
    -Wmissing-prototypes
    -Wunreachable-code)
endif (extra-warnings)


foreach(flag ${test_flags})
  set(test_cxx_flag "CXX_FLAG${flag}")
  CHECK_CXX_COMPILER_FLAG(${flag} "${test_cxx_flag}")
  if (${test_cxx_flag})
     set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${flag}")
  endif (${test_cxx_flag})
endforeach(flag ${test_flags})

check_include_file_cxx(tuple OME_HAVE_TUPLE)
check_include_file_cxx(tr1/tuple OME_HAVE_TR1_TUPLE)

check_cxx_source_compiles("
#include <cstdint>
int main() { uint16_t test(134); }
" OME_HAVE_CSTDINT)

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

check_cxx_source_compiles("
#include <array>
int main() { std::array<int,3> a; a[0] = 5; }
" OME_HAVE_ARRAY)

check_cxx_source_compiles("
#include <cstdarg>

void print(const char *fmt, ...)
{
  va_list va1, va2;
  va_start(va1, fmt);
  va_copy(va2, va1);
  va_end(va1);
}

int main() { print(\"%d %s\", 43, \"test\"); }
" OME_HAVE_CSTDARG)
