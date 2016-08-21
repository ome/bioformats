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

#include <ome/xerces/String.h>
#include <ome/xerces/Platform.h>
#include <ome/xerces/ErrorReporter.h>

// #include <ome/xerces/dom/Document.h>
// #include <ome/xerces/dom/Node.h>
// #include <ome/xerces/dom/Element.h>
// #include <ome/xerces/dom/NodeList.h>

#include <xercesc/dom/DOM.hpp>

#include <xercesc/framework/StdOutFormatTarget.hpp>
#include <xercesc/framework/LocalFileFormatTarget.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/util/OutOfMemoryException.hpp>
#include <xercesc/util/XMLException.hpp>

#include <iostream>
#include <cstring>
#include <cstdlib>
#include <stdexcept>


void fatal(const char *err, const char *reason = 0)
{
  std::cerr << "E: " << err;
  if (reason)
    std::cerr << ": " << reason;
  std::cerr << std::endl;
  std::exit(1);
}

namespace xml = ome::xerces;


int main (int argc, char *argv[])
{
  if (argc != 2)
    fatal("Usage: testx file.xml");

  const char *filename = argv[1];

  try
    {
      xml::Platform xmlplat;

      try
        {
          xercesc::XercesDOMParser::ValSchemes vscheme = xercesc::XercesDOMParser::Val_Auto;  // Val_Always;
          bool do_ns = true;
          bool do_schema = true;
          //bool do_valid = false;
          bool do_fullcheck = true;
          bool do_create = true;

          xercesc::XercesDOMParser parser;
          parser.setValidationScheme(vscheme);
          parser.setDoNamespaces(do_ns);
          parser.setDoSchema(do_schema);
          parser.setHandleMultipleImports (true);
          parser.setValidationSchemaFullChecking(do_fullcheck);
          parser.setCreateEntityReferenceNodes(do_create);

          xml::ErrorReporter er;
          parser.setErrorHandler(&er);

          std::cerr << "Set up parser\n";

          parser.parse(filename);

          if (er)
            throw std::runtime_error("Parse error");

          std::cerr << "Parsed " << filename << "\n";

          return 0;
        }
      catch (const xercesc::XMLException &e)
        {
          fatal("XML parse error", xml::String(e.getMessage()).str().c_str());
        }
      catch (const xercesc::DOMException &e)
        {
          const unsigned int maxc = 2047;
          XMLCh error[maxc + 1];

          if (xercesc::DOMImplementation::loadDOMExceptionMsg(e.code, error, maxc))
            fatal("XML DOM parse error", xml::String(error).str().c_str());
          else
            fatal("XML DOM parse error (no error message)");
        }
      catch (const xercesc::OutOfMemoryException&)
        {
          fatal("Out of memory");
        }
      catch (const std::exception&e)
        {
          fatal(e.what());
        }
      catch (...)
        {
          fatal("Unknown exception");
        }
    }
  catch (const xercesc::XMLException &e)
    {
      fatal("XML parse error", xml::String(e.getMessage()).str().c_str());
    }
  return 1;
}
