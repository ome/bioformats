/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
 * %%
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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

#include <ome/test/config.h>

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

#include <ome/test/test.h>

#include <stdexcept>

namespace xml = ome::xerces;

TEST(Xerces, ValidateOMEXML)
{
  const char *filename = PROJECT_SOURCE_DIR "/components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome";

  xml::Platform xmlplat;

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

  parser.parse(filename);

  if (er)
    throw std::runtime_error("Parse error");
}
