/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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

#include <limits>
#include <string>
#include <sstream>

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
	 */
	class Timestamp {
	public:
	  typedef boost::posix_time::ptime value_type;

          /**
           * Default construct a timestamp.
           */
          Timestamp():
            value()
          {
          }

	  /**
	   * Construct a timestamp from an ISO-8601-formatted string.
	   */
	  Timestamp(const std::string& value)
	  {
	    std::locale iso8601_loc(std::locale::classic(),
				    new boost::posix_time::time_input_facet("%Y-%m-%dT%H:%M:%SZ%z"));

	    std::istringstream is(value);
	    is.exceptions(std::ios_base::failbit);
	    is.imbue(iso8601_loc);
	    is >> this->value;
	  }

	  Timestamp(value_type value):
	    value(value)
	  {
	  }

          inline
          operator value_type () const
          {
            return this->value;
          }

	private:
	  value_type value;
	};

        template<class charT, class traits>
        inline std::basic_ostream<charT,traits>&
        operator<< (std::basic_ostream<charT,traits>& os,
                    const Timestamp& timestamp)
        {
          return os << static_cast<Timestamp::value_type>(timestamp);
        }

        template<class charT, class traits>
        inline std::basic_istream<charT,traits>&
        operator>> (std::basic_istream<charT,traits>& is,
                    Timestamp& timestamp)
        {
          std::locale iso8601_loc(std::locale::classic(),
                                  new boost::posix_time::time_input_facet("%Y-%m-%dT%H:%M:%SZ%z"));

          is.exceptions(std::ios_base::failbit);
          is.imbue(iso8601_loc);

          Timestamp::value_type value;
          is >> value;
          if (is)
            timestamp = Timestamp(value);

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
