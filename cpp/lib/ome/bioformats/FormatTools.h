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
     * Get the rasterized index corresponding to the given Z, C and T
     * coordinates.
     *
     * @param order dimension order.
     * @param zSize total number of focal planes.
     * @param cSize total number of channels.
     * @param tSize total number of time points.
     * @param num total number of image planes (zSize * cSize * tSize),
     *   specified as a consistency check.
     * @param z Z coordinate of ZCT coordinate triple to convert to 1D index.
     * @param c C coordinate of ZCT coordinate triple to convert to 1D index.
     * @param t T coordinate of ZCT coordinate triple to convert to 1D index.
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
     * Get the Z, C and T coordinates corresponding to the given rasterized
     * index value.
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

  }
}

#endif // OME_BIOFORMATS_FORMATTOOLS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

