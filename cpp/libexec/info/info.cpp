/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2014 - 2015 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

#include <iostream>

#include <boost/format.hpp>

#include <ome/internal/version.h>

#include <ome/common/log.h>
#include <ome/common/module.h>

#include <info/options.h>
#include <info/ImageInfo.h>

#ifdef _MSC_VER
#  include <windows.h>
#else
#  include <unistd.h>
#endif

using boost::format;
using namespace info;

namespace
{

  void
  print_version(std::ostream& stream)
  {
    format fmtr("%1% (%2%) %3% (%4%)");
    fmtr % "bf-test info" % "OME Bio-Formats"
      % OME_VERSION_MAJOR_S "." OME_VERSION_MINOR_S "." OME_VERSION_PATCH_S OME_VERSION_EXTRA_S
      % OME_VCS_DATE_S;

    format fmtc("Copyright © %1%–%2% Open Microscopy Environment");
    fmtc % "2006" % "2014";

    stream << fmtr << '\n'
           << fmtc << '\n' << '\n'
           << "This is free software; see the source for copying conditions.  There is NO\n"
      "warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\n"
           << std::flush;
  }

  void
  print_help(std::ostream& stream,
             const options& opts)
  {
    stream << "Usage:\n  bf-test info  [OPTION…] [FILE] — display and validate image metadata\n"
           << opts.get_visible_options()
           << std::flush;
  }

  void
  display_manpage(const std::string& name,
                  const std::string& section)
  {
#ifdef _MSC_VER
    boost::filesystem::path docpath(ome::common::module_runtime_path("doc"));
    docpath = docpath / "manual" / "html" / "developers" / "cpp" / "commands";
    std::string htmlpage = "bf-test-";
    htmlpage += name;
    htmlpage += ".html";
    docpath /= htmlpage;
    CoInitializeEx(NULL, COINIT_APARTMENTTHREADED | COINIT_DISABLE_OLE1DDE);
    ShellExecute(NULL, "open", htmlpage.c_str(),
		 NULL, NULL, SW_SHOWNORMAL);
    std::exit(EXIT_FAILURE);
#else
    boost::filesystem::path mandir(ome::common::module_runtime_path("man"));
    execlp("man", "man", "-M", mandir.generic_string().c_str(), section.c_str(), name.c_str(), static_cast<char *>(0));
    std::cerr << "E: Failed to run man to view " << name << '.' << section << std::endl;
    std::exit(EXIT_FAILURE);
#endif
  }

  void
  print_metadata(std::ostream& stream,
                 const options& opts)
  {
    for (std::vector<std::string>::const_iterator i = opts.files.begin();
         i != opts.files.end();
         ++i)
      {
        stream << "Image: " << *i << '\n';
        ImageInfo info(*i, opts);
        info.testRead(stream);
      }
  }

}

int
main(int argc, char *argv[])
{
  int status = 0;

  try
    {
      options opts;
      opts.parse(argc, argv);

      ome::logging::trivial::severity_level logLevel;

      switch (opts.verbosity)
        {
        case options::MSG_QUIET:
          logLevel = ome::logging::trivial::fatal;
          break;
        case options::MSG_NORMAL:
          logLevel = ome::logging::trivial::warning;
          break;
        case options::MSG_VERBOSE:
          logLevel = ome::logging::trivial::info;
          break;
        case options::MSG_DEBUG:
          logLevel = ome::logging::trivial::debug;
          break;
        default:
          break;
        }

      ome::common::setLogLevel(logLevel);

      switch (opts.action)
        {
        case options::ACTION_VERSION:
          print_version(std::cout);
          break;
        case options::ACTION_USAGE:
          print_help(std::cout, opts);
          break;
        case options::ACTION_HELP:
          display_manpage("bf-test-info", "1");
          break;
        case options::ACTION_METADATA:
          print_metadata(std::cout, opts);
          break;
        default:
          print_help(std::cout, opts);
          break;
        }
    }
  catch (const std::exception& e)
    {
      status = 1;
      std::cerr << "E: " << e.what() << std::endl;
    }

  return status;
}
