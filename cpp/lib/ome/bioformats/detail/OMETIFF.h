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

#ifndef OME_BIOFORMATS_DETAIL_OMETIFF_H
#define OME_BIOFORMATS_DETAIL_OMETIFF_H

#include <ome/bioformats/Types.h>

#include <ome/common/filesystem.h>

namespace ome
{
  namespace bioformats
  {
    namespace detail
    {

      /**
       * Metadata for a single plane within an OME-TIFF file set.
       */
      class OMETIFFPlane
      {
      public:
        /// Status of the file associated with this plane.
        enum Status
          {
            UNKNOWN, ///< Not known.
            PRESENT, ///< File exists.
            ABSENT   ///< File is missing.
          };

        /// File containing this plane.
        boost::filesystem::path id;
        /// IFD index.
        dimension_size_type ifd;
        /// Certainty flag, for dealing with unspecified NumPlanes.
        bool certain;
        /// File status.
        Status status;

        /**
         * Default constructor.
         *
         * File and IFD are default constructed; order is uncertain;
         * status is unknown.
         */
        OMETIFFPlane():
          id(),
          ifd(),
          certain(false),
          status(UNKNOWN)
        {
        }

        /**
         * Construct with filename.
         *
         * @param id the TIFF file containing this plane.
         *
         * IFD is default constructed; order is uncertain; status is
         * unknown.
         */
        OMETIFFPlane(const boost::filesystem::path& id):
          id(id),
          ifd(),
          certain(false),
          status(UNKNOWN)
        {
        }
      };

    }
  }
}

#endif // OME_BIOFORMATS_DETAIL_OMETIFF_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
