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

#ifndef OME_BIOFORMATS_FORMATTOOLS_H
#define OME_BIOFORMATS_FORMATTOOLS_H

#include <string>

#include <ome/compat/array.h>

#include <ome/bioformats/Types.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Imaging domain.
     */
    enum Domain
      {
        UNKNOWN,   ///< Unknown
        HCS,       ///< High-Content Screening (HCS)
        LM,        ///< Light Microscopy (LM)
        EM,        ///< Electron Microscopy (EM)
        SPM,       ///< Scanning Probe Microscopy (SPM)
        SEM,       ///< Scanning Electron Microscopy (SEM)
        FLIM,      ///< Fluorescence-Lifetime Imaging (FLIM)
        MEDICAL,   ///< Medical Imaging
        HISTOLOGY, ///< Histology
        GEL,       ///< Gel/Blot Imaging
        ASTRONOMY, ///< Astronomy
        GRAPHICS   ///< Graphics
      };

    /**
     * Get the string corresponding to a particular Domain.
     *
     * @param domain the Domain to use.
     * @returns the string description of the Domain.
     */
    const std::string&
    getDomain(Domain domain);

    /**
     * Get the rasterized index corresponding to the given @c Z, @c C
     * and @c T coordinates.
     *
     * @param order dimension order.
     * @param zSize total number of focal planes.
     * @param cSize total number of channels.
     * @param tSize total number of time points.
     * @param num total number of image planes (zSize * cSize * tSize),
     *   specified as a consistency check.
     * @param z the @c Z coordinate of ZCT coordinate triple to convert to 1D index.
     * @param c the @c C coordinate of ZCT coordinate triple to convert to 1D index.
     * @param t the @c T coordinate of ZCT coordinate triple to convert to 1D index.
     * @returns the 1D index.
     */
    dimension_size_type
    getIndex(const std::string& order,
             dimension_size_type zSize,
             dimension_size_type cSize,
             dimension_size_type tSize,
             dimension_size_type num,
             dimension_size_type z,
             dimension_size_type c,
             dimension_size_type t);

    /**
     * Get the rasterized index corresponding to the given @c Z, @c C,
     * @c T, @c ModuloZ, @c ModuloC and @c ModuloT coordinates.
     *
     * @param order dimension order.
     * @param zSize total number of focal planes.
     * @param cSize total number of channels.
     * @param tSize total number of time points.
     * @param moduloZSize total number of ModuloZ planes.
     * @param moduloCSize total number of ModuloC channels.
     * @param moduloTSize total number of ModuloT time points.
     * @param num total number of image planes (zSize * cSize * tSize),
     *   specified as a consistency check.
     * @param z the @c Z coordinate of ZCTmZmCmT coordinate sextuple to convert to 1D index.
     * @param c the @c C coordinate of ZCTmZmCmT coordinate sextuple to convert to 1D index.
     * @param t the @c T coordinate of ZCTmZmCmT coordinate sextuple to convert to 1D index.
     * @param moduloZ the @c ModuloZ coordinate of ZCTmZmCmT coordinate sextuple to convert to 1D index.
     * @param moduloC the @c ModuloC coordinate of ZCTmZmCmT coordinate sextuple to convert to 1D index.
     * @param moduloT the @c ModuloT coordinate of ZCTmZmCmT coordinate sextuple to convert to 1D index.
     * @returns the 1D index.
     */
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
             dimension_size_type moduloT);

    /**
     * Get the @c Z, @c C and @c T coordinates corresponding to the
     * given rasterized index value.
     *
     * @param order dimension order.
     * @param zSize total number of focal planes.
     * @param cSize total number of channels.
     * @param tSize total number of time points.
     * @param num total number of image planes (zSize * cSize * tSize),
     *   specified as a consistency check.
     * @param index 1D (rasterized) index to convert to ZCT coordinates.
     * @returns an array containing the ZCT coordinates (in that order).
     */
    std::array<dimension_size_type, 3>
    getZCTCoords(const std::string& order,
                 dimension_size_type zSize,
                 dimension_size_type cSize,
                 dimension_size_type tSize,
                 dimension_size_type num,
                 dimension_size_type index);

    /**
     * Get the @c Z, @c C, @c T, @c ModuloZ, @c ModuloC and @c ModuloT
     * coordinates corresponding to the given rasterized index value.
     *
     * @param order dimension order.
     * @param zSize total number of focal planes.
     * @param cSize total number of channels.
     * @param tSize total number of time points.
     * @param moduloZSize total number of ModuloZ planes.
     * @param moduloCSize total number of ModuloC channels.
     * @param moduloTSize total number of ModuloT time points.
     * @param num total number of image planes (zSize * cSize * tSize),
     *   specified as a consistency check.
     * @param index 1D (rasterized) index to convert to ZCTmZmCmT
     * coordinates.
     * @returns an array containing the ZCTmZmCmT coordinates (in that
     * order).
     */
    std::array<dimension_size_type, 6>
    getZCTCoords(const std::string& order,
                 dimension_size_type zSize,
                 dimension_size_type cSize,
                 dimension_size_type tSize,
                 dimension_size_type moduloZSize,
                 dimension_size_type moduloCSize,
                 dimension_size_type moduloTSize,
                 dimension_size_type num,
                 dimension_size_type index);

  }
}

#endif // OME_BIOFORMATS_FORMATTOOLS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

