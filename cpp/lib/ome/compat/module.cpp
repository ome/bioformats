/*
 * #%L
 * OME-COMPAT C++ library for C++ compatibility/portability
 * %%
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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

#include <ome/compat/filesystem.h>
#include <ome/compat/module.h>

#include <ome/internal/config.h>

#include <cstring>
#include <stdexcept>

namespace fs = boost::filesystem;

#ifdef OME_HAVE_DLADDR
#ifndef _GNU_SOURCE
# define _GNU_SOURCE 1
#endif
#include <dlfcn.h>
#include <stdio.h>

namespace
{

  Dl_info this_module;

  __attribute__((constructor))
  void
  find_module(void)
  {
    dladdr(reinterpret_cast<void *>(find_module), &this_module);
  }

  bool
  validate_path(const fs::path& path)
  {
    fs::path schema(path / fs::path(OME_BIOFORMATS_INSTALL_SCHEMADIR));
    return (fs::exists(path) && fs::is_directory(path) &&
            fs::exists(schema) && fs::is_directory(schema));
  }

}

#endif // OME_HAVE_DLADDR

namespace ome
{
  namespace compat
  {

    fs::path
    module_runtime_prefix()
    {
      if (getenv("BIOFORMATS_HOME"))
        {
          fs::path home(getenv("BIOFORMATS_HOME"));
          if (validate_path(home))
            return home;
        }
      else if (strlen(INSTALL_PREFIX) > 0)
        {
          fs::path prefix(INSTALL_PREFIX);
          if (validate_path(prefix))
            return prefix;
        }
#ifdef OME_HAVE_DLADDR
      else
        {
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
            return moduledir;
        }
#endif // OME_HAVE_DLADDR
      throw std::runtime_error("Could not determine Bio-Formats runtime path prefix");
    }

  }
}
