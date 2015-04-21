/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#include <stdexcept>

#include <boost/format.hpp>
#include <boost/optional.hpp>
#include <boost/range/size.hpp>

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
        throw std::logic_error(fmt.str());
      }

      void
      throw_exception(char dim,
                      const std::string& what,
                      dimension_size_type value1,
                      dimension_size_type value2)
      {
        boost::format fmt("Invalid %1% %2%: %3%/%4%");
        fmt % dim % what % value1 % value2;
        throw std::logic_error(fmt.str());
      }

      void
      validate_dimensions(const std::string&   order,
                          dimension_size_type  zSize,
                          dimension_size_type  cSize,
                          dimension_size_type  tSize,
                          dimension_size_type  num,
                          dimension_size_type& iz,
                          dimension_size_type& ic,
                          dimension_size_type& it)
      {
        if (!(order.size() >= 2 && order.substr(0,2) == "XY"))
          {
            boost::format fmt("Invalid dimension order: %1%");
            fmt % order;
            throw std::logic_error(fmt.str());
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
            throw std::logic_error(fmt.str());
          }

        iz = sz - 2;
        it = st - 2;
        ic = sc - 2;

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
            throw std::logic_error(fmt.str());
          }

        if (num != (zSize * cSize * tSize))
          {
            // If this happens, there is probably a bug in the
            // metadata ZCT sizes, or the total number of images, or
            // else the input file is invalid
            boost::format fmt("ZCT/image count mismatch (sizeZ=%1%, sizeT=%2%, sizeC=%3%, total=%4%");
            fmt % zSize % tSize % cSize % num;
            throw std::logic_error(fmt.str());
          }
      }

      template<typename I>
      inline std::vector<std::string>
      domain_strings(I begin,
                     I end)
      {
        std::vector<std::string> ret;

        for (I i = begin; i < end; ++i)
          ret.push_back(getDomain(*i));

        return ret;
      }

    }

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

    const std::string&
    getDomain(Domain domain)
    {
      static const std::string unk("Unknown");

      switch(domain)
        {
        case UNKNOWN_DOMAIN:
          {
            return unk;
          }
          break;
        case HCS_DOMAIN:
          {
            static const std::string hcs("High-Content Screening (HCS)");
            return hcs;
          }
          break;
        case LM_DOMAIN:
          {
            static const std::string lm("Light Microscopy (LM)");
            return lm;
          }
          break;
        case EM_DOMAIN:
          {
            static const std::string em("Electron Microscopy (EM)");
            return em;
          }
          break;
        case SPM_DOMAIN:
          {
            static const std::string spm("Scanning Probe Microscopy (SPM)");
            return spm;
          }
          break;
        case SEM_DOMAIN:
          {
            static const std::string sem("Scanning Electron Microscopy (SEM)");
            return sem;
          }
          break;
        case FLIM_DOMAIN:
          {
            static const std::string flim("Fluorescence-Lifetime Imaging (FLIM)");
            return flim;
          }
          break;
        case MEDICAL_DOMAIN:
          {
            static const std::string mi("Medical Imaging");
            return mi;
          }
          break;
        case HISTOLOGY_DOMAIN:
          {
            static const std::string hs("Histology");
            return hs;
          }
          break;
        case GEL_DOMAIN:
          {
            static const std::string gel("Gel/Blot Imaging");
            return gel;
          }
          break;
        case ASTRONOMY_DOMAIN:
          {
            static const std::string astronomy("Astronomy");
            return astronomy;
          }
          break;
        case GRAPHICS_DOMAIN:
          {
            static const std::string graphics("Graphics");
            return graphics;
          }
          break;
        }

      // Fallback if enum is unknown.
      return unk;
    }

    const std::vector<std::string>&
    getDomainCollection(DomainCollection domains)
    {
      switch(domains)
        {
        case NON_GRAPHICS_DOMAINS:
          {
            const Domain non_graphics_enums[] =
              {
                UNKNOWN_DOMAIN,
                HCS_DOMAIN,
                LM_DOMAIN,
                EM_DOMAIN,
                SPM_DOMAIN,
                SEM_DOMAIN,
                FLIM_DOMAIN,
                MEDICAL_DOMAIN,
                HISTOLOGY_DOMAIN,
                GEL_DOMAIN,
                ASTRONOMY_DOMAIN
              };
            static const std::vector<std::string> non_graphics_domains
              (domain_strings(non_graphics_enums,
                              non_graphics_enums + boost::size(non_graphics_enums)));
            return non_graphics_domains;
          }
          break;
        case NON_HCS_DOMAINS:
          {
            const Domain non_hcs_enums[] =
              {
                UNKNOWN_DOMAIN,
                LM_DOMAIN,
                EM_DOMAIN,
                SPM_DOMAIN,
                SEM_DOMAIN,
                FLIM_DOMAIN,
                MEDICAL_DOMAIN,
                HISTOLOGY_DOMAIN,
                GEL_DOMAIN,
                ASTRONOMY_DOMAIN
              };
            static const std::vector<std::string> non_hcs_domains
              (domain_strings(non_hcs_enums,
                              non_hcs_enums + boost::size(non_hcs_enums)));
            return non_hcs_domains;
          }
          break;
        case NON_SPECIAL_DOMAINS:
          {
            const Domain non_special_enums[] =
              {
                UNKNOWN_DOMAIN,
                LM_DOMAIN,
                EM_DOMAIN,
                SPM_DOMAIN,
                SEM_DOMAIN,
                FLIM_DOMAIN,
                MEDICAL_DOMAIN,
                HISTOLOGY_DOMAIN,
                GEL_DOMAIN,
                ASTRONOMY_DOMAIN
              };
            static const std::vector<std::string> non_special_domains
              (domain_strings(non_special_enums,
                              non_special_enums + boost::size(non_special_enums)));
            return non_special_domains;
          }
          break;
        case ALL_DOMAINS:
          {
            const Domain all_enums[] =
              {
                UNKNOWN_DOMAIN,
                HCS_DOMAIN,
                LM_DOMAIN,
                EM_DOMAIN,
                SPM_DOMAIN,
                SEM_DOMAIN,
                FLIM_DOMAIN,
                MEDICAL_DOMAIN,
                HISTOLOGY_DOMAIN,
                GEL_DOMAIN,
                ASTRONOMY_DOMAIN,
                GRAPHICS_DOMAIN
              };
            static const std::vector<std::string> all_domains
              (domain_strings(all_enums,
                              all_enums + boost::size(all_enums)));
            return all_domains;
          }
          break;
        case HCS_ONLY_DOMAINS:
          {
            const Domain hcs_only_enums[] =
              {
                HCS_DOMAIN
              };
            static const std::vector<std::string> hcs_only_domains
              (domain_strings(hcs_only_enums,
                              hcs_only_enums + boost::size(hcs_only_enums)));
            return hcs_only_domains;
          }
          break;
        }

      // Fallback if enum is unknown.
      static const std::vector<std::string> unk;
      return unk;
    }


#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

    dimension_size_type
    getIndex(const std::string& order,
             dimension_size_type zSize,
             dimension_size_type cSize,
             dimension_size_type tSize,
             dimension_size_type num,
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

    dimension_size_type
    getIndex(const std::string& order,
             dimension_size_type zSize,
             dimension_size_type cSize,
             dimension_size_type tSize,
             dimension_size_type moduloZSize,
             dimension_size_type moduloCSize,
             dimension_size_type moduloTSize,
             dimension_size_type num,
             dimension_size_type z,
             dimension_size_type c,
             dimension_size_type t,
             dimension_size_type moduloZ,
             dimension_size_type moduloC,
             dimension_size_type moduloT)
    {
      return getIndex(order,
                      zSize,
                      cSize,
                      tSize,
                      num,
                      (z * moduloZSize) + moduloZ,
                      (c * moduloCSize) + moduloC,
                      (t * moduloTSize) + moduloT);

    }

    ome::compat::array<dimension_size_type, 3>
    getZCTCoords(const std::string& order,
                 dimension_size_type zSize,
                 dimension_size_type cSize,
                 dimension_size_type tSize,
                 dimension_size_type num,
                 dimension_size_type index)
    {
      dimension_size_type iz = 0, it = 0, ic = 0;
      validate_dimensions(order, zSize, cSize, tSize, num, iz, ic, it);

      // assign rasterization order
      dimension_size_type len0 = iz == 0 ? zSize : (ic == 0 ? cSize : tSize);
      dimension_size_type len1 = iz == 1 ? zSize : (ic == 1 ? cSize : tSize);
      dimension_size_type v0 = index % len0;
      dimension_size_type v1 = index / len0 % len1;
      dimension_size_type v2 = index / len0 / len1;

      ome::compat::array<dimension_size_type, 3> ret;
      ret[0] = iz == 0 ? v0 : (iz == 1 ? v1 : v2); // z
      ret[1] = ic == 0 ? v0 : (ic == 1 ? v1 : v2); // c
      ret[2] = it == 0 ? v0 : (it == 1 ? v1 : v2); // t

      return ret;
    }

    ome::compat::array<dimension_size_type, 6>
    getZCTCoords(const std::string& order,
                 dimension_size_type zSize,
                 dimension_size_type cSize,
                 dimension_size_type tSize,
                 dimension_size_type moduloZSize,
                 dimension_size_type moduloCSize,
                 dimension_size_type moduloTSize,
                 dimension_size_type num,
                 dimension_size_type index)
    {
      ome::compat::array<dimension_size_type, 3> coords
        (getZCTCoords(order,
                      zSize,
                      cSize,
                      tSize,
                      num,
                      index));

      ome::compat::array<dimension_size_type, 6> ret;
      ret[0] = coords[0] / moduloZSize;
      ret[1] = coords[1] / moduloCSize;
      ret[2] = coords[2] / moduloTSize;
      ret[3] = coords[0] % moduloZSize;
      ret[4] = coords[1] % moduloCSize;
      ret[5] = coords[2] % moduloTSize;

      return ret;
    }

  }
}
