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

#include <sstream>

#include <ome/common/xml/EntityResolver.h>
#include <ome/common/xml/ErrorReporter.h>
#include <ome/common/xml/Platform.h>
#include <ome/common/xml/String.h>
#include <ome/common/xml/dom/Document.h>

#include <xercesc/dom/DOMException.hpp>
#include <xercesc/dom/DOMImplementation.hpp>
#include <xercesc/dom/DOMImplementationRegistry.hpp>
#include <xercesc/dom/DOMLSOutput.hpp>
#include <xercesc/dom/DOMLSSerializer.hpp>
#include <xercesc/framework/LocalFileFormatTarget.hpp>
#include <xercesc/framework/LocalFileInputSource.hpp>
#include <xercesc/framework/MemBufFormatTarget.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/util/XMLException.hpp>
#include <xercesc/util/XMLUni.hpp>

namespace
{

  void
  setup_parser(xercesc::XercesDOMParser&                     parser,
               const ome::common::xml::dom::ParseParameters& params)
  {
    parser.setValidationScheme(params.validationScheme);
    parser.setDoNamespaces(params.doNamespaces);
    parser.setDoSchema(params.doSchema);
    parser.setHandleMultipleImports(params.handleMultipleImports);
    parser.setValidationSchemaFullChecking(params.validationSchemaFullChecking);
    parser.setCreateEntityReferenceNodes(params.createEntityReferenceNodes);
  }

  void
  read_source(xercesc::XercesDOMParser&         parser,
              ome::common::xml::EntityResolver& resolver,
              xercesc::InputSource&             source)
  {
    ome::common::xml::ErrorReporter er;
    parser.setErrorHandler(&er);

    parser.setXMLEntityResolver(&resolver);

    parser.parse(source);

    if (er || !parser.getDocument())
      throw std::runtime_error("Parse error");
  }

  void
  setup_writer(xercesc::DOMLSSerializer&                     writer,
               const ome::common::xml::dom::WriteParameters& params)
  {
    xercesc::DOMConfiguration *config(writer.getDomConfig());
    if (config->canSetParameter(xercesc::XMLUni::fgDOMCanonicalForm, params.canonicalForm))
      config->setParameter(xercesc::XMLUni::fgDOMCanonicalForm, params.canonicalForm);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMCDATASections, params.CDATASections))
      config->setParameter(xercesc::XMLUni::fgDOMCDATASections, params.CDATASections);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMComments, params.comments))
      config->setParameter(xercesc::XMLUni::fgDOMComments, params.comments);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMDatatypeNormalization, params.datatypeNormalization))
      config->setParameter(xercesc::XMLUni::fgDOMDatatypeNormalization, params.datatypeNormalization);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMWRTDiscardDefaultContent, params.discardDefaultContent))
      config->setParameter(xercesc::XMLUni::fgDOMWRTDiscardDefaultContent, params.discardDefaultContent);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMEntities, params.entities))
      config->setParameter(xercesc::XMLUni::fgDOMEntities, params.entities);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMNamespaces, params.namespaces))
      config->setParameter(xercesc::XMLUni::fgDOMNamespaces, params.namespaces);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMNamespaceDeclarations, params.namespaceDeclarations))
      config->setParameter(xercesc::XMLUni::fgDOMNamespaceDeclarations, params.namespaceDeclarations);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMNormalizeCharacters, params.normalizeCharacters))
      config->setParameter(xercesc::XMLUni::fgDOMNormalizeCharacters, params.normalizeCharacters);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMWRTFormatPrettyPrint, params.prettyPrint))
      config->setParameter(xercesc::XMLUni::fgDOMWRTFormatPrettyPrint, params.prettyPrint);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMSplitCDATASections, params.splitCDATASections))
      config->setParameter(xercesc::XMLUni::fgDOMSplitCDATASections, params.splitCDATASections);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMValidateIfSchema, params.validate))
      config->setParameter(xercesc::XMLUni::fgDOMValidateIfSchema, params.validate);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMWRTWhitespaceInElementContent, params.whitespace))
      config->setParameter(xercesc::XMLUni::fgDOMWRTWhitespaceInElementContent, params.whitespace);
    if (config->canSetParameter(xercesc::XMLUni::fgDOMXMLDeclaration, params.xmlDeclaration))
      config->setParameter(xercesc::XMLUni::fgDOMXMLDeclaration, params.xmlDeclaration);
  }

  void
  write_target(xercesc::DOMNode&                             node,
               xercesc::XMLFormatTarget&                     target,
               const ome::common::xml::dom::WriteParameters& params)
  {
    // To clean up properly due to the lack of Xerces
    // exception-safety, track if we need to throw an exception after
    // cleanup.
    bool ok = false;
    std::string fail_message;

    xercesc::DOMImplementation* impl = xercesc::DOMImplementationRegistry::getDOMImplementation(ome::common::xml::String("LS"));
    xercesc::DOMImplementationLS *ls(dynamic_cast<xercesc::DOMImplementationLS *>(impl));
    if (!ls)
      throw std::runtime_error("Failed to create LS DOMImplementation");

    xercesc::DOMLSSerializer* writer(0);
    xercesc::DOMLSOutput* output(0);

    try
      {
        writer = (ls->createLSSerializer());

        ome::common::xml::ErrorReporter er;

        xercesc::DOMConfiguration *config(writer->getDomConfig());
        config->setParameter(xercesc::XMLUni::fgDOMErrorHandler, &er);

        setup_writer(*writer, params);

        xercesc::DOMLSOutput* output(ls->createLSOutput());
        output->setByteStream(&target);

        writer->write(&node, output);

        ok = true;
      }
    catch (const xercesc::XMLException& toCatch)
      {
        fail_message = "XMLException during DOM XML writing: ";
        fail_message += ome::common::xml::String(toCatch.getMessage());
      }
    catch (const xercesc::DOMException& toCatch)
      {
        fail_message = "DOMException during DOM XML writing: ";
        fail_message += ome::common::xml::String(toCatch.getMessage());
      }
    catch (...)
      {
        fail_message = "Unexpected exception during DOM XML writing";
      }

    // Clean up before rethrowing any exceptions.
    if (output)
      output->release();
    if (writer)
      writer->release();

    if (!ok)
      throw std::runtime_error(fail_message);
  }

}

namespace ome
{
  namespace common
  {
    namespace xml
    {
      namespace dom
      {

        Document
        createEmptyDocument(const std::string& qualifiedName)
        {
          xercesc::DOMImplementation* impl = xercesc::DOMImplementationRegistry::getDOMImplementation(String("LS"));
          if (!impl)
            throw std::runtime_error("Failed to create LS DOMImplementation");

          return Document(impl->createDocument(0, String(qualifiedName), 0),
                          true);
        }

        Document
        createEmptyDocument(const std::string& namespaceURI,
                            const std::string& qualifiedName)
        {
          xercesc::DOMImplementation* impl = xercesc::DOMImplementationRegistry::getDOMImplementation(String("LS"));
          if (!impl)
            throw std::runtime_error("Failed to create LS DOMImplementation");

          return Document(impl->createDocument(String(namespaceURI),
                                               String(qualifiedName),
                                               0),
                          true);
        }

        Document
        createDocument(const boost::filesystem::path& file,
                       EntityResolver&                resolver,
                       const ParseParameters&         params)
        {
          Platform xmlplat;

          xercesc::LocalFileInputSource source(String(file.generic_string()));

          xercesc::XercesDOMParser parser;
          setup_parser(parser, params);
          read_source(parser, resolver, source);

          return Document(parser.adoptDocument(), true);
        }

        Document
        createDocument(const std::string&     text,
                       EntityResolver&        resolver,
                       const ParseParameters& params,
                       const std::string&     id)
        {
          Platform xmlplat;

          xercesc::MemBufInputSource source(reinterpret_cast<const XMLByte *>(text.c_str()),
                                            static_cast<XMLSize_t>(text.size()),
                                            String(id));

          xercesc::XercesDOMParser parser;
          setup_parser(parser, params);
          read_source(parser, resolver, source);

          return Document(parser.adoptDocument(), true);
        }

        Document
        createDocument(std::istream&          stream,
                       EntityResolver&        resolver,
                       const ParseParameters& params,
                       const std::string&     id)
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
                                            String(id));

          xercesc::XercesDOMParser parser;
          setup_parser(parser, params);
          read_source(parser, resolver, source);

          return Document(parser.adoptDocument(), true);
        }

        void
        writeNode(xercesc::DOMNode&              node,
                  const boost::filesystem::path& file,
                  const WriteParameters&         params)
        {
          Platform xmlplat;

          xercesc::LocalFileFormatTarget target(String(file.generic_string()));

          write_target(node, target, params);
        }

        void
        writeNode(xercesc::DOMNode&      node,
                  std::ostream&          stream,
                  const WriteParameters& params)
        {
          Platform xmlplat;

          xercesc::MemBufFormatTarget target(4096);

          write_target(node, target, params);

          stream.write(reinterpret_cast<const char *>(target.getRawBuffer()),
                       target.getLen());
        }

        void
        writeNode(xercesc::DOMNode&      node,
                  std::string&           text,
                  const WriteParameters& params)
        {
          Platform xmlplat;

          xercesc::MemBufFormatTarget target(4096);

          write_target(node, target, params);

          const XMLByte *buf(target.getRawBuffer());
          XMLSize_t buflen(target.getLen());
          text.assign(reinterpret_cast<const char *>(buf),
                      reinterpret_cast<const char *>(buf) + buflen);
        }

        void
        writeNode(Node&                          node,
                  const boost::filesystem::path& file,
                  const WriteParameters&         params)
        {
          writeNode(*(node.get()), file, params);
        }

        void
        writeNode(Node&                  node,
                  std::ostream&          stream,
                  const WriteParameters& params)
        {
          writeNode(*(node.get()), stream, params);
        }

        void
        writeNode(Node&                  node,
                  std::string&           text,
                  const WriteParameters& params)
        {
          writeNode(*(node.get()), text, params);
        }

        void
        writeDocument(Document&                      document,
                      const boost::filesystem::path& file,
                      const WriteParameters&         params)
        {
          writeNode(*(document.get()), file, params);
        }

        void
        writeDocument(Document&              document,
                      std::ostream&          stream,
                      const WriteParameters& params)
        {
          writeNode(*(document.get()), stream, params);
        }

        void
        writeDocument(Document&              document,
                      std::string&           text,
                      const WriteParameters& params)
        {
          writeNode(*(document.get()), text, params);
        }

      }
    }
  }
}
