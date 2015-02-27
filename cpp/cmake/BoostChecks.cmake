# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2015 Open Microscopy Environment:
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

include(FindBoost)
find_package(Boost REQUIRED COMPONENTS date_time filesystem system iostreams program_options regex)

include(CheckIncludeFileCXX)
include(CheckCXXSourceCompiles)

check_include_file_cxx(boost/format.hpp OME_HAVE_BOOST_FORMAT)
check_include_file_cxx(boost/shared_ptr.hpp OME_HAVE_BOOST_SHARED_PTR)
check_include_file_cxx(boost/tuple/tuple.hpp OME_HAVE_BOOST_TUPLE)
check_include_file_cxx(boost/type_traits.hpp HAVE_BOOST_TYPE_TRAITS_HPP)

# Boost library checks could be dropped?
# boost::program_options::variables_map in -lboost_program_options
# + BOOST_PROGRAM_OPTIONS_DESCRIPTION_OLD (drop?)
SET(CMAKE_REQUIRED_LIBRARIES_SAVE ${CMAKE_REQUIRED_LIBRARIES})
SET(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES} ${Boost_PROGRAM_OPTIONS_LIBRARY_RELEASE})

check_cxx_source_compiles(
"#include <boost/program_options.hpp>

int main() {
  boost::program_options::variables_map dummy();
}"
BOOST_PROGRAM_OPTIONS_LINK)
# boost::program_options::validation_error in -lboost_program_options
# + BOOST_PROGRAM_OPTIONS_VALIDATION_ERROR_OLD (drop?)

check_cxx_source_compiles(
"#include <boost/program_options.hpp>

int main() {
  boost::program_options::validation_error
    err(boost::program_options::validation_error::invalid_option, \"error\");
}"
BOOST_PROGRAM_OPTIONS_DESCRIPTION_CURRENT_LINK)

SET(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES_SAVE})

set(BOOST_PROGRAM_OPTIONS_DESCRIPTION_OLD 0)
if (BOOST_PROGRAM_OPTIONS AND NOT BOOST_PROGRAM_OPTIONS_DESCRIPTION_CURRENT)
  set(BOOST_PROGRAM_OPTIONS_DESCRIPTION_OLD 1)
endif(BOOST_PROGRAM_OPTIONS AND NOT BOOST_PROGRAM_OPTIONS_DESCRIPTION_CURRENT)

SET(CMAKE_REQUIRED_LIBRARIES_SAVE ${CMAKE_REQUIRED_LIBRARIES})
SET(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES} ${Boost_IOSTREAMS_LIBRARY_RELEASE})
# <regex> tests; boost/regex.hpp fallback ==> HAVE_REGEX
# boost::iostreams in -lboost_iostreams
check_cxx_source_compiles(
"#include <boost/iostreams/device/file_descriptor.hpp>
#include <boost/iostreams/stream.hpp>

int main() {
  boost::iostreams::stream<boost::iostreams::file_descriptor> fdstream;
}"
BOOST_IOSTREAMS_LINK)
# boost::iostreams::file_descriptor_source in -lboost_iostreams
# + BOOST_IOSTREAMS_CLOSE_HANDLE_OLD

check_cxx_source_compiles(
"#include <boost/iostreams/device/file_descriptor.hpp>
#include <boost/iostreams/stream.hpp>
#include <unistd.h>

int main() {
boost::iostreams::file_descriptor_sink dummy(STDOUT_FILENO, boost::iostreams::close_handle);
}"
BOOST_IOSTREAMS_CLOSE_HANDLE_CURRENT_LINK)

SET(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES_SAVE})

set(BOOST_IOSTREAMS_CLOSE_HANDLE_OLD 0)
if (BOOST_IOSTREAMS AND NOT BOOST_IOSTREAMS_CLOSE_HANDLE_CURRENT)
  set(BOOST_IOSTREAMS_CLOSE_HANDLE_OLD 1)
endif(BOOST_IOSTREAMS AND NOT BOOST_IOSTREAMS_CLOSE_HANDLE_CURRENT)


SET(CMAKE_REQUIRED_LIBRARIES_SAVE ${CMAKE_REQUIRED_LIBRARIES})
SET(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES} ${Boost_FILESYSTEM_LIBRARY_RELEASE} ${Boost_SYSTEM_LIBRARY_RELEASE})
# boost::filesystem in -lboost_filesystem
check_cxx_source_compiles(
"#include <boost/filesystem.hpp>

int main() {
  boost::filesystem::is_directory(\"/\");
}"
BOOST_FILESYSTEM_LINK)
SET(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES_SAVE})
