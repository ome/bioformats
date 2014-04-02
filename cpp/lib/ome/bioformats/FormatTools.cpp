/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#include <stdexcept>

#include <boost/format.hpp>
#include <boost/optional.hpp>

#include <ome/bioformats/FormatTools.h>

namespace ome
{
  namespace bioformats
  {

    namespace
    {

      void
      throw_exception(char dim,
                      const std::string& what,
                      dimension_size_type value)
      {
        boost::format fmt("Invalid %1% %2%: %3%");
        fmt % dim % what % value;
        throw new std::logic_error(fmt.str());
      }

      void
      throw_exception(char dim,
                      const std::string& what,
                      dimension_size_type value1,
                      dimension_size_type value2)
      {
        boost::format fmt("Invalid %1% %2%: %3%/%4%");
        fmt % dim % what % value1 % value2;
        throw new std::logic_error(fmt.str());
      }

      void
      validate_dimensions(const std::string&   order,
                          dimension_size_type  zSize,
                          dimension_size_type  cSize,
                          dimension_size_type  tSize,
                          image_size_type      num,
                          dimension_size_type& iz,
                          dimension_size_type& ic,
                          dimension_size_type& it)
      {
        if (!(order.size() >= 2 && order.substr(0,2) == "XY"))
          {
            boost::format fmt("Invalid dimension order: %1%");
            fmt % order;
            throw new std::logic_error(fmt.str());
          }

        std::string::size_type sz, st, sc;

        sz = order.find_first_of('Z', 2);
        st = order.find_first_of('T', 2);
        sc = order.find_first_of('C', 2);
        if (sz == std::string::npos ||
            st == std::string::npos ||
            sc == std::string::npos)
          {
            boost::format fmt("Invalid dimension order: %1%");
            fmt % order;
            throw new std::logic_error(fmt.str());
          }

        iz = sz;
        it = st;
        ic = sc;

        // check SizeZ
        if (!zSize)
          throw_exception('Z', "size", zSize);

        // check SizeT
        if (!tSize)
          throw_exception('T', "size", tSize);

        // check SizeC
        if (!cSize)
          throw_exception('C', "size", cSize);

        // check image count
        if (!num)
          {
            boost::format fmt("Invalid image count: %1%");
            fmt % num;
            throw new std::logic_error(fmt.str());
          }

        if (num != (zSize * cSize * tSize))
          {
            // If this happens, there is probably a bug in the
            // metadata ZCT sizes, or the total number of images, or
            // else the input file is invalid
            boost::format fmt("ZCT/image count mismatch (sizeZ=%1%, sizeT=%2%, sizeC=%3%, total=%4%");
            fmt % zSize % tSize % cSize % num;
            throw new std::logic_error(fmt.str());
          }
      }

    }

    dimension_size_type
    getIndex(const std::string& order,
             dimension_size_type zSize,
             dimension_size_type cSize,
             dimension_size_type tSize,
             image_size_type     num,
             dimension_size_type z,
             dimension_size_type c,
             dimension_size_type t)
    {
      dimension_size_type iz, it, ic;
      validate_dimensions(order, zSize, cSize, tSize, num, iz, ic, it);

      if (z >= zSize)
        throw_exception('Z', "index", z, zSize);
      if (t >= tSize)
        throw_exception('T', "index", t, tSize);
      if (c >= cSize)
        throw_exception('C', "index", c, cSize);

      // assign rasterization order
      dimension_size_type v0 = iz == 0 ? z : (ic == 0 ? c : t);
      dimension_size_type v1 = iz == 1 ? z : (ic == 1 ? c : t);
      dimension_size_type v2 = iz == 2 ? z : (ic == 2 ? c : t);
      dimension_size_type len0 = iz == 0 ? zSize : (ic == 0 ? cSize : tSize);
      dimension_size_type len1 = iz == 1 ? zSize : (ic == 1 ? cSize : tSize);

      return v0 + (v1 * len0) + (v2 * len0 * len1);
    }

    std::array<dimension_size_type, 3>
    getZCTCoords(const std::string& order,
                 dimension_size_type zSize,
                 dimension_size_type cSize,
                 dimension_size_type tSize,
                 image_size_type     num,
                 dimension_size_type index)
    {
      dimension_size_type iz, it, ic;
      validate_dimensions(order, zSize, cSize, tSize, num, iz, ic, it);

      // assign rasterization order
      dimension_size_type len0 = iz == 0 ? zSize : (ic == 0 ? cSize : tSize);
      dimension_size_type len1 = iz == 1 ? zSize : (ic == 1 ? cSize : tSize);
      dimension_size_type v0 = index % len0;
      dimension_size_type v1 = index / len0 % len1;
      dimension_size_type v2 = index / len0 / len1;

      std::array<dimension_size_type, 3> ret;
      ret[0] = iz == 0 ? v0 : (iz == 1 ? v1 : v2); // z
      ret[1] = ic == 0 ? v0 : (ic == 1 ? v1 : v2); // c
      ret[2] = it == 0 ? v0 : (it == 1 ? v1 : v2); // t

      return ret;
    }

  }
}
