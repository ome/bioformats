/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#ifndef TEST_TIFFSAMPLES_H
#define TEST_TIFFSAMPLES_H

#include <boost/filesystem.hpp>

#include <ome/bioformats/Types.h>

#include <ome/compat/regex.h>

#include <ome/internal/config.h>

#include <ome/test/config.h>
#include <ome/test/test.h>

#include <vector>

struct TileTestParameters
{
  bool tile;
  std::string file;
  std::string wfile;
  bool imageplanar;
  ome::bioformats::dimension_size_type imagewidth;
  ome::bioformats::dimension_size_type imagelength;
  ome::bioformats::dimension_size_type tilewidth;
  ome::bioformats::dimension_size_type tilelength;
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const TileTestParameters& p)
{
  return os << p.file << " [" << p.wfile << "] ("
            << p.imagewidth << "x" << p.imagelength
            << (p.imageplanar ? " planar" : " chunky")
            << (p.tile ? " tiled " : " strips ")
            << p.tilewidth << "x" << p.tilelength
            << ")";
}

extern std::vector<TileTestParameters>
find_tile_tests();

#endif // TEST_TIFFSAMPLES_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
