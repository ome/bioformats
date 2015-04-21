/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
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

#ifndef OME_COMMON_XML_STRING_H
#define OME_COMMON_XML_STRING_H

#include <cassert>
#include <string>
#include <ostream>

#include <xercesc/util/XMLString.hpp>

namespace ome
{
  namespace common
  {
    namespace xml
    {

      /**
       * Xerces string wrapper.  Xerces uses UTF-16 internally, which is
       * incompatible with the standard library string and stream
       * classes.  This class interconverts between std::string and
       * XMLCh *, as well as managing the memory of XMLCh * objects.
       * All Xerces functions and class methods which take XMLCh *
       * inputs may be transparently called with instances of this
       * class, and likewise functions and methods returning XMLCh * may
       * directly construct instances of this class using the return
       * value.
       *
       * This class does have an overhead of maintaining two copies of
       * the string (char * and XMLCh *), as well as the cost of
       * transcoding between the two forms upon construction.
       *
       * Assignment of std::string or XMLCh * is not supported.  This
       * class is only intended to transiently transcode between the two
       * types and manage the memory for this.
       */
      class String
      {
      public:
        /**
         * Construct a String from an XMLCh * string.  The string
         * content will be copied; no ownership is taken of the original
         * string.  The string will also be transcoded to a
         * NUL-terminated char * string.
         *
         * @param str an XMLCh *string.
         */
        inline
        String(const XMLCh *str):
          narrow(xercesc::XMLString::transcode(str)),
          wide(xercesc::XMLString::replicate(str))
        {
          assert(this->wide != 0);
          assert(this->narrow != 0);
        }

        /**
         * Construct a String from a NUL-terminated string.  The string
         * content will be copied into a NUL-terminated char * string.
         * The string will also be transcoded to an XMLCh * string.
         *
         * @param str a char * NUL-terminated string.
         */
        inline
        String(const char *str):
          narrow(xercesc::XMLString::replicate(str)),
          wide(xercesc::XMLString::transcode(str))
        {
          assert(this->narrow != 0);
          assert(this->wide != 0);
        }

        /**
         * Construct a String from a std::string.  The string content
         * will be copied into a NUL-terminated char * string.  The
         * string will also be transcoded to an XMLCh * string.
         *
         * @param str a std::string.
         */
        inline
        String(std::string const& str):
          narrow(xercesc::XMLString::replicate(str.c_str())),
          wide(xercesc::XMLString::transcode(str.c_str()))
        {
          assert(this->narrow != 0);
          assert(this->wide != 0);
        }

        /**
         * Destructor.  The allocated char * and XMLCh * strings will be freed.
         */
        inline
        ~String()
        {
          if (narrow)
            xercesc::XMLString::release(&narrow);
          if (wide)
            xercesc::XMLString::release(&wide);
        }

        /**
         * Cast String to XMLCh *.
         *
         * @returns a NUL-terminated XMLCh * string.
         */
        inline
        operator const XMLCh *() const
        {
          assert(this->wide != 0);

          return wide;
        }

        /**
         * Cast String to a std::string.
         *
         * @returns a std::string.
         */
        inline
        operator ::std::string() const
        {
          assert(this->narrow != 0);

          return narrow;
        }

        /**
         * Get the String content as a std::string.
         *
         * @returns a std::string containing the String content.
         */
        inline
        ::std::string
        str() const
        {
          assert(this->narrow != 0);

          return narrow;
        }

        /**
         * Compare a String for equality with a C string.
         *
         * @param rhs the string to compare.
         * @returns @c true if equal, @c false otherwise.
         */
        bool
        operator== (const char *rhs)
        {
          return this->narrow != 0 && strcmp(this->narrow, rhs) == 0;
        }

        /**
         * Compare a String for equality with a std::string.
         *
         * @param rhs the string to compare.
         * @returns @c true if equal, @c false otherwise.
         */
        bool
        operator== (const std::string& rhs)
        {
          return this->narrow != 0 && strcmp(this->narrow, rhs.c_str()) == 0;
        }

        /**
         * Compare a String for equality with a String.
         *
         * @param rhs the string to compare.
         * @returns @c true if equal, @c false otherwise.
         */
        bool
        operator== (const String& rhs)
        {
          return this->narrow != 0 && rhs.narrow != 0 &&
            strcmp(this->narrow, rhs.narrow) == 0;
        }

        /**
         * Compare a String for inequality with a C string.
         *
         * @param rhs the string to compare.
         * @returns @c true if not equal, @c false otherwise.
         */
        bool
        operator!= (const char *rhs)
        {
          return !(*this == rhs);
        }

        /**
         * Compare a String for inequality with a std::string.
         *
         * @param rhs the string to compare.
         * @returns @c true if not equal, @c false otherwise.
         */
        bool
        operator!= (const std::string& rhs)
        {
          return !(*this == rhs);
        }

        /**
         * Compare a String for inequality with a String.
         *
         * @param rhs the string to compare.
         * @returns @c true if not equal, @c false otherwise.
         */
        bool
        operator!= (const String& rhs)
        {
          return !(*this == rhs);
        }

      private:
        /// The char * string representation.
        char *narrow;
        /// The XMLCh * string representation.
        XMLCh *wide;
      };

      /**
       * Output String to output stream.
       *
       * @param os the output stream.
       * @param str the String to output.
       * @returns the output stream.
       */
      inline ::std::ostream&
      operator<< (::std::ostream& os,
                  const String&   str)
      {
        return os << static_cast<std::string>(str);
      }

    }
  }
}

#endif // OME_COMMON_XML_STRING_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
