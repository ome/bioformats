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

#include <ome/xerces/EntityResolver.h>
#include <ome/xerces/ErrorReporter.h>
#include <ome/xerces/Platform.h>
#include <ome/xerces/String.h>
#include <ome/xerces/dom/Document.h>

#include <xercesc/dom/DOMImplementation.hpp>
#include <xercesc/dom/DOMImplementationRegistry.hpp>
#include <xercesc/framework/LocalFileInputSource.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>

namespace
{

  void
  setup_parser(xercesc::XercesDOMParser& parser)
  {
    xercesc::XercesDOMParser::ValSchemes vscheme = xercesc::XercesDOMParser::Val_Auto;  // Val_Always;
    bool do_ns = true;
    bool do_schema = true;
    //bool do_valid = false;
    bool do_fullcheck = true;
    bool do_create = true;

    parser.setValidationScheme(vscheme);
    parser.setDoNamespaces(do_ns);
    parser.setDoSchema(do_schema);
    parser.setHandleMultipleImports(true);
    parser.setValidationSchemaFullChecking(do_fullcheck);
    parser.setCreateEntityReferenceNodes(do_create);
  }

  void
  read_source(xercesc::XercesDOMParser& parser,
              xercesc::InputSource&     source)
  {
    ome::xerces::ErrorReporter er;
    parser.setErrorHandler(&er);

    ome::xerces::EntityResolver res;
    parser.setXMLEntityResolver(&res);

    parser.parse(source);

    if (er)
      throw std::runtime_error("Parse error");
  }

}

namespace ome
{
  namespace xerces
  {
    namespace dom
    {

      Document
      createEmptyDocument(const std::string& qualifiedName)
      {

        xercesc::DOMImplementation* impl = xercesc::DOMImplementationRegistry::getDOMImplementation(String("LS"));

        return impl->createDocument(0, String(qualifiedName), 0);

      }

      Document
      createDocument(const boost::filesystem::path& file)
      {
        Platform xmlplat;

        xercesc::LocalFileInputSource source(String(file.generic_string()));

        xercesc::XercesDOMParser parser;
        setup_parser(parser);
        read_source(parser, source);

        return parser.adoptDocument();
      }

      Document
      createDocument(const std::string& text)
      {
        Platform xmlplat;

 	xercesc::MemBufInputSource source(reinterpret_cast<const XMLByte *>(text.c_str()),
                                          static_cast<XMLSize_t>(text.size()),
                                          String("membuf"));

        xercesc::XercesDOMParser parser;
        setup_parser(parser);
        read_source(parser, source);

        return parser.adoptDocument();
      }

      Document
      createDocument(std::istream& stream)
      {
        Platform xmlplat;

        std::string data;

        // Try to intelligently size the read buffer based upon the
        // stream length.
        std::streampos pos = stream.tellg();
        stream.seekg(0, std::ios::end);
        std::streampos len = stream.tellg() - pos;
        if (len > 0)
          data.reserve(static_cast<std::string::size_type>(len));
        stream.seekg(pos);

        data.assign(std::istreambuf_iterator<char>(stream),
                    std::istreambuf_iterator<char>());

 	xercesc::MemBufInputSource source(reinterpret_cast<const XMLByte *>(data.c_str()),
                                          static_cast<XMLSize_t>(data.size()),
                                          String("membuf"));

        xercesc::XercesDOMParser parser;
        setup_parser(parser);
        read_source(parser, source);

        return parser.adoptDocument();
      }

    }
  }
}
