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

#include <cctype>

#include <ome/bioformats/XMLTools.h>

#include <ome/common/xml/ErrorReporter.h>
#include <ome/common/xml/Platform.h>
#include <ome/common/xml/String.h>

#include <ome/xml/Document.h>

namespace xml = ome::common::xml;

namespace
{

  const std::string xsi_ns("http://www.w3.org/2001/XMLSchema-instance");
  const std::string xml_schema_path("http://www.w3.org/2001/XMLSchema");

}

namespace ome
{
  namespace bioformats
  {

    std::string
    escapeXML(const std::string& s)
    {
      std::string ret;
      ret.reserve(s.size());

      for(std::string::const_iterator i = s.begin();
          i != s.end();
          ++i)
        {
          switch(*i)
            {
            case '<':
              ret += "&lt;";
              break;
            case '>':
              ret += "&gt;";
              break;
            case '&':
              ret += "&amp;";
              break;
            case '"':
              ret += "&quot;";
              break;
            case '\'':
              ret += "&apos;";
              break;
            default:
              ret += *i;
              break;
            }
        }
      return ret;
    }

    std::string
    sanitizeXML(const std::string& s)
    {
      std::string ret;
      ret.reserve(s.size());

      for(std::string::const_iterator i = s.begin();
          i != s.end();
          ++i)
        {
          char v = *i;
          // Remove all control characters except for newline, tab and
          // cr.  Note that the java code also removes codepoints
          // which are not defined in unicode, but we don't have any
          // means of doing that with just the standard library, so
          // undefined characters are currently passed through.
          if (std::iscntrl(*i) && '\n' != *i && '\t' != *i && '\r' != *i)
            v = 0;

          // Eliminate invalid "&#" sequences
          if (i != s.begin() && '&' == *(i-1) && '#' == *i)
            {
              ret.resize(ret.size() - 1);
              ret += "&amp;";
            }

          if (v)
            ret += v;
        }

      return ret;
    }

    bool
    validateXML(const std::string& s,
                const std::string& /* loc */)
    {
      bool valid = true;

      try
        {
          ome::common::xml::Platform xmlplat;
          ome::xml::createDocument(s);
        }
      catch (const std::runtime_error&)
        {
          valid = false;
        }

      return valid;
    }

  }
}
