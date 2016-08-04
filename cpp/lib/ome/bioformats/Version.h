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

#ifndef OME_BIOFORMATS_VERSION_H
#define OME_BIOFORMATS_VERSION_H

#include <string>

#include <ome/compat/cstdint.h>
#include <ome/compat/tuple.h>

#include <ome/xml/model/primitives/Timestamp.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Release version.
     */
    struct Version
    {
      /**
       * Constructor.
       *
       * @param major the major version.
       * @param minor the minor version.
       * @param patch the patchlevel version.
       * @param extra the version suffix.
       */
      Version(uint32_t           major,
              uint32_t           minor,
              uint32_t           patch,
              const std::string& extra);

      /// Major version number.
      uint32_t major;
      /// Minor version number.
      uint32_t minor;
      /// Patchlevel version number.
      uint32_t patch;
      /// Version suffix.
      std::string extra;
    };

    /**
     * Output Version to output stream.
     *
     * @param os the output stream.
     * @param version the Version to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
    inline std::basic_ostream<charT,traits>&
    operator<< (std::basic_ostream<charT,traits>& os,
                const Version& version)
    {
      return os << version.major << '.'
                << version.minor << '.'
                << version.patch
                << version.extra;
    }

    /**
     * The release version number of the library currently being
     * linked against.
     */
    extern const Version release_version;

    /**
     * The release date of the library currently being linked against.
     */
    extern const ::ome::xml::model::primitives::Timestamp release_date;

  }
}

#endif // OME_BIOFORMATS_VERSION_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
