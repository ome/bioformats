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

#include <ome/xml/model/primitives/Timestamp.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {
      namespace primitives
      {

        Timestamp::Timestamp():
          value(boost::posix_time::microsec_clock::universal_time())
        {
        }

        Timestamp::Timestamp(const std::string& value):
          value()//boost::posix_time::from_iso_string(value))
        {
          boost::posix_time::time_input_facet *input_facet =
            new boost::posix_time::time_input_facet();
          input_facet->set_iso_extended_format();
          std::locale iso8601_loc(std::locale::classic(), input_facet);

          std::istringstream is(value);
          //	    is.exceptions(std::ios_base::failbit);
          is.imbue(iso8601_loc);
          is >> this->value;

          char tztype;
          is.get(tztype);
          if(is)
            {
              std::cout << "TZTYPE=" << tztype << std::endl;

              if (tztype == 'Z' || tztype == '-' || tztype == '+')
                is.ignore(); // Drop above from istream

              if (tztype == '-' || tztype == '+')
                {
                  int offset;
                  is >> offset;
                  std::cout << "OFFSET=" << offset << std::endl;
                }
            }

          std::string tmp;
          is >> tmp;
          std::cout << "REM: "<< tmp << std::endl;
        }

        Timestamp::Timestamp(value_type value):
          value(value)
        {
        }

      }
    }
  }
}
