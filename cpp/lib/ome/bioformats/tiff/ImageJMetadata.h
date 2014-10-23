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

#ifndef OME_BIOFORMATS_TIFF_IMAGEJMETADATA_H
#define OME_BIOFORMATS_TIFF_IMAGEJMETADATA_H

#include <map>
#include <vector>
#include <sstream>
#include <string>

#include <ome/bioformats/Types.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      class IFD;

      /**
       * ImageJ metadata from IFD tags.
       */
      struct ImageJMetadata
      {
        /// Map of key-value pairs from ImageDescription field.
        std::map<std::string, std::string> map;
        /// Content of ImageJMetaDataByteCounts field.
        std::vector<uint32_t> counts;
        /// Content of ImageJMetaData field.
        std::vector<uint8_t> data;
        /// Total number of images.
        dimension_size_type images;
        /// Slice count.
        dimension_size_type slices;
        /// Frame count.
        dimension_size_type frames;
        /// Channel count.
        dimension_size_type channels;
        /// Length unit name.
        std::string unit;
        /// Z spacing distance.
        double spacing;
        /// Frame interval time.
        double finterval;
        /// X origin.
        dimension_size_type xorigin;
        /// Y origin.
        dimension_size_type yorigin;
        /// Color mode.
        std::string mode;
        /// Loop animation(?).
        bool loop;

        /**
         * Construct from an IFD.
         *
         * @param ifd the IFD to parse the metadata from.
         * @throws an exception on parse errors.
         */
        ImageJMetadata(const IFD& ifd);

        /**
         * Parse the ImageDescription field content.
         *
         * Split into separate lines, then split on the first @c =
         * symbol into key-value pairs.
         *
         * @returns a map of all the key-value pairs.
         */
        static
        std::map<std::string,std::string>
        parse_imagedescription(const std::string& description);

      protected:
        /**
         * Parse a key's value into a given type.
         *
         * The string value of the given key will be parsed into the
         * corresponding value type.
         *
         * @param key the key name.
         * @param value the value to store the key's parsed string value.
         * @throws @c std::ios_base::failure on stream parse errors.
         */
        template<typename T>
        void
        parse_value(const std::string& key,
                    T&                 value)
        {
          std::map<std::string,std::string>::const_iterator i = map.find(key);
          if (i != map.end())
            {
              std::istringstream is(i->second);
              is.imbue(std::locale::classic());
              is.exceptions(std::ios::failbit);
              is >> value;
            }
        }

        /**
         * Parse a key's value into a bool.
         *
         * The string value of the given key will be parsed into a
         * bool type.
         *
         * @param key the key name.
         * @param value the value to store the key's parsed string value.
         * @throws @c std::runtime_error on parse errors.
         */
        void
        parse_value(const std::string& key,
                    bool&              value);

        /**
         * Parse a key's value into a string.
         *
         * The string value of the given key will be parsed into a
         * string type.
         *
         * @param key the key name.
         * @param value the value to store the key's parsed string value.
         * @throws @c std::runtime_error on parse errors.
         */
        void
        parse_value(const std::string& key,
                    std::string&       value);

      };

    }
  }
}

#endif // OME_BIOFORMATS_TIFF_IMAGEJMETADATA_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
