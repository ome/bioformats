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
