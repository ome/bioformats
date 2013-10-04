/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
 * %%
 * Copyright © 2006 - 2013 Open Microscopy Environment:
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

#ifndef OME_XERCES_STRING_H
#define OME_XERCES_STRING_H

#include <cassert>
#include <string>
#include <ostream>

#include <xercesc/util/XMLString.hpp>

namespace ome
{
  namespace xerces
  {

    class string
    {
    public:
      inline
      string(const XMLCh *str):
      narrow(xercesc::XMLString::transcode(str)),
      wide(xercesc::XMLString::replicate(str))
      {
        assert(this->wide != 0);
        assert(this->narrow != 0);
      }

      inline
      string(const char *str):
      narrow(xercesc::XMLString::replicate(str)),
      wide(xercesc::XMLString::transcode(str))
      {
        assert(this->narrow != 0);
        assert(this->wide != 0);
      }

      inline
      string(std::string const& str):
        narrow(xercesc::XMLString::replicate(str.c_str())),
        wide(xercesc::XMLString::transcode(str.c_str()))
      {
        assert(this->narrow != 0);
        assert(this->wide != 0);
      }

      inline
      ~string()
      {
        if (narrow)
          xercesc::XMLString::release(&narrow);
        if (wide)
          xercesc::XMLString::release(&wide);
      }

      inline
      operator const XMLCh *() const
      {
        assert(this->wide != 0);

        return wide;
      }

      inline
      operator ::std::string() const
      {
        assert(this->narrow != 0);

        return narrow;
      }

      inline
      ::std::string
      str() const
      {
        assert(this->narrow != 0);

        return narrow;
      }

    private:
      char *narrow;
      XMLCh *wide;
    };

    inline ::std::ostream&
    operator<< (::std::ostream& stream,
                const string&   str)
    {
      return stream << static_cast<std::string>(str);
    }

  }
}

#endif // OME_XERCES_STRING_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
