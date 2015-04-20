/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
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

#include "tiffsamples.h"

using namespace boost::filesystem;

std::vector<TileTestParameters>
find_tile_tests()
{
  std::vector<TileTestParameters> params;

  path dir(PROJECT_BINARY_DIR "/cpp/test/ome-bioformats/data");
  if (exists(dir) && is_directory(dir))
    {
      for(directory_iterator i(dir); i != directory_iterator(); ++i)
        {
          static ome::compat::regex tile_match(".*/data-layout-([[:digit:]]+)x([[:digit:]]+)-([[:alpha:]]+)-tiles-([[:digit:]]+)x([[:digit:]]+)\\.tiff");
          static ome::compat::regex strip_match(".*/data-layout-([[:digit:]]+)x([[:digit:]]+)-([[:alpha:]]+)-strips-([[:digit:]]+)\\.tiff");

          ome::compat::smatch found;
          std::string file(i->path().string());
          path wpath(i->path().parent_path());
          wpath /= std::string("w-") + i->path().filename().string();
          std::string wfile(wpath.string());
          if (ome::compat::regex_match(file, found, tile_match))
            {
              TileTestParameters p;
              p.tile = true;
              p.file = file;
              p.wfile = wfile;

              std::istringstream iwid(found[1]);
              if (!(iwid >> p.imagewidth))
                continue;

              std::istringstream iht(found[2]);
              if (!(iht >> p.imagelength))
                continue;

              p.imageplanar = false;
              if (found[3] == "planar")
                p.imageplanar = true;

              std::istringstream twid(found[4]);
              if (!(twid >> p.tilewidth))
                continue;

              std::istringstream tht(found[5]);
              if (!(tht >> p.tilelength))
                continue;

              params.push_back(p);
            }
          else if (ome::compat::regex_match(file, found, strip_match))
            {
              TileTestParameters p;
              p.tile = false;
              p.file = file;
              p.wfile = wfile;

              std::istringstream iwid(found[1]);
              if (!(iwid >> p.imagewidth))
                continue;

              std::istringstream iht(found[2]);
              if (!(iht >> p.imagelength))
                continue;

              p.imageplanar = false;
              if (found[3] == "planar")
                p.imageplanar = true;

              p.tilewidth = p.imagewidth;

              std::istringstream srow(found[4]);
              if (!(srow >> p.tilelength))
                continue;

              params.push_back(p);
            }
        }
    }

  return params;
}
