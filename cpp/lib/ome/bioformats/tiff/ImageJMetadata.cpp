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

#include <boost/format.hpp>

#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/ImageJMetadata.h>
#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/Exception.h>

#include <sstream>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      namespace
      {
	const int MAGIC_NUMBER = 0x494a494a;  // "IJIJ"
	const int INFO =         0x696e666f;  // "info" (Info image property)
	const int LABELS =       0x6c61626c;  // "labl" (slice labels)
	const int RANGES =       0x72616e67;  // "rang" (display ranges)
	const int LUTS =         0x6c757473;  // "luts" (channel LUTs)
      }

      ImageJMetadata::ImageJMetadata(const IFD& ifd)
      {
        ifd.getField(IMAGEJ_META_DATA_BYTE_COUNTS).get(counts);
        ifd.getField(IMAGEJ_META_DATA).get(data);
        std::string desc;
        ifd.getField(IMAGEDESCRIPTION).get(desc);
        map = parse_imagedescription(desc);

        parse_value("images", images);
        parse_value("channels", channels);
        parse_value("slices", slices);
        parse_value("frames", frames);
        parse_value("unit", unit);
        parse_value("spacing", spacing);
        parse_value("finterval", finterval);
        parse_value("xorigin", xorigin);
        parse_value("yorigin", yorigin);
        parse_value("mode", mode);
        parse_value("loop", loop);
      }

      std::map<std::string,std::string>
      ImageJMetadata::parse_imagedescription(const std::string& description)
      {
        std::map<std::string,std::string> ret;

        std::string::size_type start = 0;
        std::string::size_type end = 0;
        std::string::size_type sep = 0;

        const std::string::size_type size = description.size();

        while (start != std::string::npos && start < size)
          {
            std::string::size_type next = start;
            if (start)
              ++next; // skip last found newline
            if (next < size)
              {
                end = description.find_first_of('\n', next);
                sep = description.find_first_of('=', next);

                if (sep != std::string::npos &&
                    (end == std::string::npos || sep < end))
                  {
                    std::string key(description.substr(start, sep - start));
                    std::string value;
                    ++sep;

                    if (sep < size)
                      {
                        if (end != std::string::npos)
                          value = description.substr(sep, end - sep);
                        else
                          value = description.substr(sep);
                      }

                    ret.insert(std::make_pair(key, value));
                  }
              }
            else
              end = sep = std::string::npos;

            start = end;
            if (start != std::string::npos)
              ++start;
          }

        return ret;
      }

      void
      ImageJMetadata::parse_value_error(const std::string& key,
                                        const std::string& value)
      {
        boost::format fmt("Failed to parse value ‘%1%’ in ImageJ TIFF metadata for key ‘%2%’");
        fmt % value % key;
        throw std::runtime_error(fmt.str());
      }

      void
      ImageJMetadata::parse_value(const std::string& key,
                                  bool&              value)
      {
        std::map<std::string,std::string>::const_iterator i = map.find(key);
        if (i != map.end())
          {
            if (i->second == "true" || i->second == "yes" || i->second == "1")
              value = true;
            else if (i->second == "false" || i->second == "no" || i->second == "0")
              value = false;
            else
              parse_value_error(key, i->second);
          }
      }

      void
      ImageJMetadata::parse_value(const std::string& key,
                                  std::string&       value)
      {
        std::map<std::string,std::string>::const_iterator i = map.find(key);
        if (i != map.end())
          value = i->second;
      }

    }
  }
}
