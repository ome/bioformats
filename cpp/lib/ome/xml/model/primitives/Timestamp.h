/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
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

#ifndef OME_XML_MODEL_PRIMITIVES_TIMESTAMP_H
#define OME_XML_MODEL_PRIMITIVES_TIMESTAMP_H

#include <string>
#include <sstream>
#include <stdexcept>

#include <boost/date_time/posix_time/posix_time.hpp>

#include <ome/compat/cstdint.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {
      namespace primitives
      {

        /**
         * An ISO-8601 timestamp.
         *
         * The timestamp will have at least microsecond precision and,
         * on systems where the Boost.Date_Time module has been
         * compiled with support enabled, nanosecond precision.
         */
        class Timestamp {
        public:
          /// POSIX time is the underlying time representation.
          typedef boost::posix_time::ptime value_type;

          /**
           * Construct a Timestamp (defaults to current UTC time).
           */
          Timestamp();

          /**
           * Construct a Timestamp from an ISO-8601 date string.
           *
           * @param value an ISO-8601-formatted string.
           */
          Timestamp(const std::string& value);

          /**
           * Construct a Timestamp from POSIX time.
           *
           * @param value the POSIX time.
           */
          Timestamp(value_type value);

          /**
           * Get the wrapped POSIX time value.
           *
           * Returns the POSIX time.
           */
          inline
          operator value_type () const
          {
            return this->value;
          }

        private:
          /// The POSIX time (at least microsecond precision).
          value_type value;
        };

        /**
         * Output Timestamp to output stream.
         *
         * @param os the output stream.
         * @param timestamp the timestamp to output.
         * @returns the output stream.
         */
        template<class charT, class traits>
        inline std::basic_ostream<charT,traits>&
        operator<< (std::basic_ostream<charT,traits>& os,
                    const Timestamp& timestamp)
        {
          return os << boost::posix_time::to_iso_extended_string(static_cast<Timestamp::value_type>(timestamp))
                    << 'Z';
        }

        /**
         * Set Timestamp from input stream.
         *
         * @param is the input stream.
         * @param timestamp the Timestamp to set.
         * @returns the input stream.
         */
        template<class charT, class traits>
        inline std::basic_istream<charT,traits>&
        operator>> (std::basic_istream<charT,traits>& is,
                    Timestamp& timestamp)
        {
          Timestamp::value_type value;

          // Save locale.
          std::locale savedlocale = is.getloc();

          try
            {
              boost::posix_time::time_input_facet *input_facet =
                new boost::posix_time::time_input_facet();
              input_facet->set_iso_extended_format();
              std::locale iso8601_loc(std::locale::classic(), input_facet);

              is.imbue(iso8601_loc);
              is >> value;

              if (is)
                {
                  // Check for zone offset
                  char tztype = is.peek();
                  if(tztype != std::char_traits<char>::eof())
                    {
                      if (tztype == 'Z')
                        {
                          is.ignore(); // Drop above from istream
                          // If Z, we're already using UTC, so don't apply numeric offsets
                        }
                      else if (tztype == '-' || tztype == '+')
                        {
                          is.ignore(); // Drop above from istream
                          if (is.rdbuf()->in_avail() >= 4) // Need 4 numeric chars
                            {
                              // Check that the next 4 characters are only numeric
                              char inchars[4];
                              is.read(&inchars[0], 4);
                              for (int i=0; i < 4; ++i)
                                if (inchars[i] < '0' || inchars[i] > '9')
                                  is.setstate(std::ios::failbit);

                              if (is)
                                {
                                  // Get offset value
                                  int offset;
                                  std::istringstream valueis(inchars);
                                  valueis >> offset;
                                  if (valueis)
                                    {
                                      if (tztype == '+')
                                        offset = -offset;
                                      // Offset in                       hours,      minutes,    seconds
                                      boost::posix_time::time_duration d(offset/100, offset%100, 0);
                                      // Apply offset
                                      value += d;
                                    }
                                  else
                                    is.setstate(std::ios::failbit);
                                }
                            }
                          else
                            {
                              is.setstate(std::ios::failbit);
                            }
                        }
                    }
                }

              if (is)
                timestamp = Timestamp(value);
              else
                throw std::runtime_error("Failed to parse timestamp");
            }
          catch (const std::exception& e)
            {
              is.imbue(savedlocale);
              throw;
            }

          is.imbue(savedlocale);
          return is;
        }

      }
    }
  }
}

#endif // OME_XML_MODEL_PRIMITIVES_TIMESTAMP_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
