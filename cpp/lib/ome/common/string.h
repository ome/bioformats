/*
 * #%L
 * OME-COMMON C++ library for C++ compatibility/portability
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

/**
 * @file ome/common/string.h String utility methods.
 */

#ifndef OME_COMMON_STRING_H
# define OME_COMMON_STRING_H

# include <ome/common/config.h>

#include <string>
#include <cstdarg>
#include <cstring>

namespace ome
{
  namespace common
  {

    /**
     * Trim leading whitespace from a string.
     *
     * Space, newline, carriage return and horizontal and vertical tabs
     * are removed from the left-hand side of the string.
     *
     * @param str the string to trim.
     * @returns the left-trimmed string.
     */
    inline std::string
    ltrim(const std::string& str)
    {
      std::string::size_type pos = str.find_first_not_of(" \r\n\t\v");
      if (pos == std::string::npos)
        return std::string();

      return str.substr(pos, std::string::npos);
    }

    /**
     * Trim trailing whitespace from a string.
     *
     * Space, newline, carriage return and horizontal and vertical tabs
     * are removed from the right-hand side of the string.
     *
     * @param str the string to trim.
     * @returns the right-trimmed string.
     */
    inline std::string
    rtrim(const std::string& str)
    {
      std::string::size_type pos = str.find_last_not_of(" \r\n\t\v");
      if (pos == std::string::npos)
        return std::string();

      return str.substr(0, pos + 1);
    }

    /**
     * Trim leading and trailing whitespace from a string.
     *
     * Space, newline, carriage return and horizontal and vertical tabs
     * are removed from the left- and right-hand sides of the string.
     *
     * @param str the string to trim.
     * @returns the trimmed string.
     */
    inline std::string
    trim(const std::string& str)
    {
      std::string::size_type fpos = str.find_first_not_of(" \r\n\t\v");
      if (fpos == std::string::npos)
        return std::string();

      std::string::size_type lpos = str.find_last_not_of(" \r\n\t\v");
      if (lpos == std::string::npos)
        return std::string();

      return str.substr(fpos, lpos - fpos + 1);
    }

    // C99/C++11 compatibility for MSVC users.  MSVC does not have a
    // current C99 or C++ standard library, so these functions are
    // missing.  Thanks to Valentin Milea.
    // http://stackoverflow.com/questions/2915672/snprintf-and-visual-studio-2010

#if defined(_MSC_VER) && !defined(OME_HAVE_SNPRINTF)

# define snprintf c99_snprintf
# define vsnprintf c99_vsnprintf

# include <stdio.h>
# include <stdarg.h>

    inline int
    c99_vsnprintf(char* str, size_t size, const char* format, va_list ap)
    {
      int count = -1;

      if (size != 0)
	count = _vsnprintf_s(str, size, _TRUNCATE, format, ap);
      if (count == -1)
	count = _vscprintf(format, ap);

      return count;
    }

    inline int
    c99_snprintf(char* str, size_t size, const char* format, ...)
    {
      int count;
      va_list ap;

      va_start(ap, format);
      count = vsnprintf(str, size, format, ap);
      va_end(ap);

      return count;
    }

#endif // _MSC_VER && !OME_HAVE_SNPRINTF

  }
}

#endif // OME_COMMON_STRING_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

