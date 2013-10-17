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

#include <iostream>

#include <ome/xerces/ErrorReporter.h>
#include <ome/xerces/String.h>

#include <xercesc/sax/SAXParseException.hpp>

namespace ome
{
  namespace xerces
  {

    ErrorReporter::ErrorReporter(std::ostream& stream):
      stream(stream),
      saw_error(false)
    {
    }

    ErrorReporter::~ErrorReporter()
    {
    }

    void
    ErrorReporter::warning(const xercesc::SAXParseException& e)
    {
      stream << "Error at file \"" << String(e.getSystemId())
	     << "\", line " << e.getLineNumber()
	     << ", column " << e.getColumnNumber()
	     << "\n   Message: " << String(e.getMessage()) << std::endl;
    }

    void
    ErrorReporter::error(const xercesc::SAXParseException& e)
    {
      saw_error = true;
      stream << "Error at file \"" << String(e.getSystemId())
	     << "\", line " << e.getLineNumber()
	     << ", column " << e.getColumnNumber()
	     << "\n   Message: " << String(e.getMessage()) << std::endl;
    }

    void
    ErrorReporter::fatalError(const xercesc::SAXParseException& e)
    {
      saw_error = true;
      std::cerr << "Fatal Error at file \"" << String(e.getSystemId())
	     << "\", line " << e.getLineNumber()
	     << ", column " << e.getColumnNumber()
	     << "\n   Message: " << String(e.getMessage()) << std::endl;
    }

    void
    ErrorReporter::resetErrors()
    {
      saw_error = false;
    }

  }
}
