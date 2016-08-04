/*
 * #%L
 * OME-COMMON C++ library for C++ compatibility/portability
 * %%
 * Copyright © 2006 - 2015 Open Microscopy Environment:
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

#include <boost/format.hpp>
#include <boost/range/size.hpp>

#include <ome/common/filesystem.h>
#include <ome/common/module.h>

#include <ome/internal/config.h>

#include <cstring>
#include <map>
#include <stdexcept>

namespace fs = boost::filesystem;

#ifdef OME_HAVE_DLADDR
#ifndef _GNU_SOURCE
# define _GNU_SOURCE 1
#endif
#include <dlfcn.h>
#include <stdio.h>
#endif // OME_HAVE_DLADDR

namespace
{

#ifdef OME_HAVE_DLADDR
  Dl_info this_module;

  __attribute__((constructor))
  void
  find_module(void)
  {
    dladdr(reinterpret_cast<void *>(find_module), &this_module);
  }
#endif // OME_HAVE_DLADDR

  bool
  validate_root_path(const fs::path& path)
  {
    fs::path schema(path / fs::path(OME_BIOFORMATS_INSTALL_SCHEMADIR));
    return (fs::exists(path) && fs::is_directory(path) &&
            fs::exists(schema) && fs::is_directory(schema));
  }

  bool
  validate_path(const fs::path& path)
  {
    return (fs::exists(path) && fs::is_directory(path));
  }

  struct internalpath
  {
    std::string envvar;
    boost::filesystem::path abspath;
    boost::filesystem::path relpath;

    internalpath(const char *const envvar,
                 const char *const abspath,
                 const char *const relpath):
      envvar(envvar),
      abspath(std::string(abspath)),
      relpath(std::string(relpath))
    {}
  };

  typedef std::map<std::string, internalpath> path_map;
  typedef path_map::value_type pm;
  typedef boost::filesystem::path path;

  const path_map&
  internalpaths()
  {
    static const pm paths[] =
      {
        // Standard GNU paths.
        pm("bin", internalpath("BIOFORMATS_BINDIR", INSTALL_FULL_BINDIR, INSTALL_BINDIR)),
        pm("sbin", internalpath("BIOFORMATS_SBINDIR", INSTALL_FULL_SBINDIR, INSTALL_SBINDIR)),
        // Note envvar SYS prefix to avoid clash with package path.
        pm("libexec", internalpath("BIOFORMATS_SYSLIBEXECDIR", INSTALL_FULL_LIBEXECDIR, INSTALL_LIBEXECDIR)),
        pm("sysconf", internalpath("BIOFORMATS_SYSCONFDIR", INSTALL_FULL_SYSCONFDIR, INSTALL_SYSCONFDIR)),
        pm("sharedstate", internalpath("BIOFORMATS_SHAREDSTATEDIR", INSTALL_FULL_SHAREDSTATEDIR, INSTALL_SHAREDSTATEDIR)),
        pm("localstate", internalpath("BIOFORMATS_LOCALSTATEDIR", INSTALL_FULL_LOCALSTATEDIR, INSTALL_LOCALSTATEDIR)),
        pm("lib", internalpath("BIOFORMATS_LIBDIR", INSTALL_FULL_LIBDIR, INSTALL_LIBDIR)),
        pm("include", internalpath("BIOFORMATS_INCLUDEDIR", INSTALL_FULL_INCLUDEDIR, INSTALL_INCLUDEDIR)),
        pm("oldinclude", internalpath("BIOFORMATS_OLDINCLUDEDIR", INSTALL_FULL_OLDINCLUDEDIR, INSTALL_OLDINCLUDEDIR)),
        pm("dataroot", internalpath("BIOFORMATS_DATAROOTDIR", INSTALL_FULL_DATAROOTDIR, INSTALL_DATAROOTDIR)),
        // Note envvar SYS prefix to avoid clash with package path.
        pm("data", internalpath("BIOFORMATS_SYSDATADIR", INSTALL_FULL_DATADIR, INSTALL_DATADIR)),
        pm("info", internalpath("BIOFORMATS_INFODIR", INSTALL_FULL_INFODIR, INSTALL_INFODIR)),
        pm("locale", internalpath("BIOFORMATS_LOCALEDIR", INSTALL_FULL_LOCALEDIR, INSTALL_LOCALEDIR)),
        pm("man", internalpath("BIOFORMATS_MANDIR", INSTALL_FULL_MANDIR, INSTALL_MANDIR)),
        pm("doc", internalpath("BIOFORMATS_DOCDIR", INSTALL_FULL_DOCDIR, INSTALL_DOCDIR)),

        // Bio-Formats package-specific paths.
        pm("bf-root", internalpath("BIOFORMATS_HOME", INSTALL_PREFIX, "")),
        pm("bf-data", internalpath("BIOFORMATS_DATADIR", OME_BIOFORMATS_INSTALL_FULL_DATADIR, OME_BIOFORMATS_INSTALL_DATADIR)),
        pm("bf-icon", internalpath("BIOFORMATS_ICONDIR", OME_BIOFORMATS_INSTALL_FULL_ICONDIR, OME_BIOFORMATS_INSTALL_ICONDIR)),
        pm("bf-libexec", internalpath("BIOFORMATS_LIBEXECDIR", OME_BIOFORMATS_INSTALL_FULL_LIBEXECDIR, OME_BIOFORMATS_INSTALL_LIBEXECDIR)),
        pm("bf-schema", internalpath("BIOFORMATS_SCHEMADIR", OME_BIOFORMATS_INSTALL_FULL_SCHEMADIR, OME_BIOFORMATS_INSTALL_SCHEMADIR)),
        pm("bf-transform", internalpath("BIOFORMATS_TRANSFORMDIR", OME_BIOFORMATS_INSTALL_FULL_TRANSFORMDIR, OME_BIOFORMATS_INSTALL_TRANSFORMDIR))
      };

    static path_map pmap(paths,
                         paths + boost::size(paths));

    return pmap;
  }
}


namespace ome
{
  namespace common
  {

    /* TESTING NOTE
     * ────────────
     *
     * This code can't be unit tested since it is used after
     * installation.  This section documents the expected paths for
     * different configurations.
     *
     * CMAKE_INSTALL_PREFIX=$install_path
     * - will fail unless "make install" has run and the install tree
     *   is present.
     * - will work in the install tree and build tree if "make"
     *   install has run.
     * - BIOFORMATS_HOME can override the hardcoded install prefix,
     *   but only if the new path contains an install tree.
     *
     * CMAKE_INSTALL_PREFIX=  [no install prefix for self-contained distributions]
     * - used for prepackaged zips
     * - will fail in the build tree since there is no valid install
     * - will work in the install tree since it will introspect the
     *   correct path
     * - BIOFORMATS_HOME can override the hardcoded install prefix,
     *   but only if the new path contains an install tree.
     *
     * Testing:
     * - With and without CMAKE_INSTALL_PREFIX set [default is /usr/local]
     * - In the install and build trees
     * - With and without BIOFORMATS_HOME
     * - With and without BIOFORMATS_HOME set to a valid path
     *
     * Testing in the build tree verifies it fails correctly.
     *
     * The sequence of checking dtype "foo" is:
     * - BIOFORMATS_$FOO env var
     * - BIOFORMATS_HOME env var [if set] + FOO_RELATIVE_PATH
     * - FOO_ABSOLUTE_PATH
     * - INSTALL_PREFIX [if set] + FOO_RELATIVE_PATH
     * - introspection [if possible] + FOO_RELATIVE_PATH
     * - throw exception
     */

    fs::path
    module_runtime_path(const std::string& dtype)
    {
      const path_map& paths(internalpaths());
      path_map::const_iterator ipath(paths.find(dtype));

      // Is this a valid dtype?
      if (ipath == paths.end())
        {
          boost::format fmt("Invalid runtime path type “%1%”");
          fmt % dtype;
          throw std::logic_error(fmt.str());
        }

      // dtype set explicitly in environment.
      if (getenv(ipath->second.envvar.c_str()))
        {
          fs::path dir(getenv(ipath->second.envvar.c_str()));
          if (validate_path(dir))
            return ome::common::canonical(dir);
        }

      // Full root path in environment + relative component
      if (getenv("BIOFORMATS_HOME"))
        {
          fs::path home(getenv("BIOFORMATS_HOME"));
          if (validate_root_path(home))
            {
              home /= ipath->second.relpath;
              if (validate_path(home))
                return ome::common::canonical(home);
            }
        }

      // Full prefix is available only when configured explicitly.
      if (strlen(INSTALL_PREFIX) > 0)
        {
          // Full specific path.
          if (validate_path(ipath->second.abspath))
            return ome::common::canonical(ipath->second.abspath);

          // Full root path + relative component
          fs::path home(INSTALL_PREFIX);
          if (validate_root_path(home))
            {
              home /= ipath->second.relpath;
              if (validate_path(home))
                return ome::common::canonical(home);
            }
        }
      else
        {
#ifdef OME_HAVE_DLADDR
          // Introspect root with dladdr(3) + relative component
          fs::path module(canonical(fs::path(this_module.dli_fname)));
          fs::path moduledir(module.parent_path());

          bool match = true;
          fs::path libdir(INSTALL_LIBDIR);

          while(!libdir.empty())
            {
              if (libdir.filename() == moduledir.filename())
                {
                  libdir = libdir.parent_path();
                  moduledir = moduledir.parent_path();
                }
              else
                {
                  match = false;
                  break;
                }
            }
          if (match && validate_path(moduledir))
            {
              moduledir /= ipath->second.relpath;
              if (validate_path(moduledir))
                return ome::common::canonical(moduledir);
            }
#endif // OME_HAVE_DLADDR
        }
      boost::format fmt("Could not determine Bio-Formats runtime path for “%1%” directory");
      fmt % dtype;
      throw std::runtime_error(fmt.str());
    }

  }
}
