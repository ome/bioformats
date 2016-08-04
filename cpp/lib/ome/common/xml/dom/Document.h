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

#ifndef OME_COMMON_XML_DOM_DOCUMENT_H
#define OME_COMMON_XML_DOM_DOCUMENT_H

#include <ome/common/config.h>

#include <cassert>
#include <istream>
#include <string>
#include <ostream>

#include <xercesc/dom/DOMComment.hpp>
#include <xercesc/dom/DOMDocument.hpp>
#include <xercesc/dom/DOMNode.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>

#include <ome/common/filesystem.h>

#include <ome/compat/memory.h>

#include <ome/common/xml/dom/Element.h>
#include <ome/common/xml/dom/NodeList.h>
#include <ome/common/xml/dom/Wrapper.h>
#include <ome/common/xml/EntityResolver.h>
#include <ome/common/xml/String.h>

namespace ome
{
  namespace common
  {
    namespace xml
    {
      /**
       * Domain object model.
       */
      namespace dom
      {

        /**
         * DOM Document wrapper.  The wrapper behaves as though is the
         * wrapped DOMDocument; it can be dereferenced using the "*" or
         * "->" operators to obtain a reference or pointer to the
         * wrapped object.  It can also be cast to a pointer to the
         * wrapped object, so can substitute for it directly.
         */
        class Document : public Wrapper<xercesc::DOMDocument, Node>
        {
        public:
          /**
           * Construct a NULL Document.
           */
          Document ():
            Wrapper<xercesc::DOMDocument, Node>()
          {
          }

          /**
           * Copy construct a Document.
           *
           * @param document the Document to copy.
           */
          Document (const Document& document):
            Wrapper<xercesc::DOMDocument, Node>(document)
          {
          }

          /**
           * Copy construct a Document.
           *
           * @param base the base type to copy (must be a Document).
           */
          Document (const Wrapper<xercesc::DOMDocument, Node>::base_type& base):
            Wrapper<xercesc::DOMDocument, Node>(base)
          {
          }

          /**
           * Construct a Document from a xercesc::DOMDocument *.
           *
           * @param document the Document to wrap.
           * @param managed is the value to be managed?
           */
          Document (Wrapper<xercesc::DOMDocument, Node>::element_type *document,
                    bool                                               managed):
            Wrapper<xercesc::DOMDocument, Node>(managed ?
                                                Wrapper<xercesc::DOMDocument, Node>(document, std::mem_fun(&base_element_type::release)) :
                                                Wrapper<xercesc::DOMDocument, Node>(document, &ome::common::xml::dom::detail::unmanaged<base_element_type>))
          {
          }

          /**
           * Construct a Document from a xercesc::DOMNode *.
           *
           * @param base the DOMNode to wrap.
           * @param managed is the value to be managed?
           */
          Document (Wrapper<xercesc::DOMDocument, Node>::base_element_type *base,
                    bool                                                    managed):
            Wrapper<xercesc::DOMDocument, Node>(managed ?
                                                Wrapper<xercesc::DOMDocument, Node>(base, std::mem_fun(&base_element_type::release)) :
                                                Wrapper<xercesc::DOMDocument, Node>(base, &ome::common::xml::dom::detail::unmanaged<base_element_type>))
          {
          }

          /// Destructor.
          ~Document ()
          {
          }

          /**
           * Assign a Document.
           *
           * @param wrapped the Document to assign.
           * @returns the Document.
           */
          Document&
          operator= (const Document& wrapped)
          {
            Wrapper<xercesc::DOMDocument, Node>::operator=(wrapped);
            return *this;
          }

          /**
           * Create Element with namespace.
           *
           * @param ns the namespace.
           * @param name the element name.
           * @returns the created Element.
           */
          Element
          createElementNS(const std::string& ns,
                          const std::string& name)
          {
            common::xml::String xns(ns);
            common::xml::String xname(name);

            return Element((*this)->createElementNS(xns, xname), false);
          }

          /**
           * Create Comment.
           *
           * @param comment the comment text.
           * @returns the created Node.
           */
          Node
          createComment(const std::string& comment)
          {
            common::xml::String text(comment);

            xercesc::DOMNode *node = dynamic_cast<xercesc::DOMNode *>((*this)->createComment(text));
            return Node(node, false);
          }

          /**
           * Create Element without namespace.
           *
           * @param name the element name.
           * @returns the created Element.
           */
          Element
          createElement(const std::string& name)
          {
            common::xml::String xname(name);

            return Element((*this)->createElement(xname), false);
          }

          /**
           * Get the root element of this document.
           *
           * @returns the root element.
           */
          Element
          getDocumentElement()
          {
            return Element((*this)->getDocumentElement(), false);
          }

          /**
           * Get child elements with a given tag name.
           *
           * @param name the element name to use.
           * @returns the child nodes (if any).
           */
          NodeList
          getElementsByTagName(const std::string& name)
          {
            return (*this)->getElementsByTagName(String(name));
          }
        };

        /**
         * Parameters controlling DOM writing.
         *
         * The DOMSerializer provides for some control over the process
         * via DOMConfiguration.  They are settable here to allow their
         * use at a high level without the need to have access to the
         * internals of the Xerces-C writing process.  Simply create an
         * instance of this class, adjust the parameters as needed, and
         * then pass to a method call which uses WriteParameters as an
         * optional argument.
         *
         * If more precise control of the process is required, use the
         * Xerces-C classes directly.  These are simply a convienience
         * for the common case and will not suit every situation.
         */
        struct ParseParameters
        {
          /// Validation scheme.
          xercesc::XercesDOMParser::ValSchemes validationScheme;
          /// Use namespaces?
          bool doNamespaces;
          /// Use schemas?
          bool doSchema;
          /// Handle multiple imports?
          bool handleMultipleImports;
          /// Do full checking during validation?
          bool validationSchemaFullChecking;
          /// Create entity reference nodes?
          bool createEntityReferenceNodes;

          /// Constructor.
          ParseParameters():
            validationScheme(xercesc::XercesDOMParser::Val_Auto),
            doNamespaces(true),
            doSchema(true),
            handleMultipleImports(true),
            validationSchemaFullChecking(true),
            createEntityReferenceNodes(true)
          {
          }
        };

        /**
         * Construct an empty Document.
         *
         * @param qualifiedName the qualified name of the document type.
         * @returns the new Document.
         */
        Document
        createEmptyDocument(const std::string& qualifiedName);

        /**
         * Construct an empty Document.
         *
         * @param namespaceURI the namespace URI of the root document
         * element.
         * @param qualifiedName the qualified name of the document type.
         * @returns the new Document.
         */
        Document
        createEmptyDocument(const std::string& namespaceURI,
                            const std::string& qualifiedName);

        /**
         * Construct a Document from the content of a file.
         *
         * @param file the file to read.
         * @param resolver the EntityResolver to use.
         * @param params XML parser parameters.
         * @returns the new Document.
         */
        Document
        createDocument(const boost::filesystem::path& file,
                       EntityResolver&                resolver,
                       const ParseParameters&         params = ParseParameters());

        /**
         * Construct a Document from the content of a string.
         *
         * @param text the string to use.
         * @param resolver the EntityResolver to use.
         * @param params XML parser parameters.
         * @param id document filename (for error reporting only).
         * @returns the new Document.
         */
        Document
        createDocument(const std::string&     text,
                       EntityResolver&        resolver,
                       const ParseParameters& params = ParseParameters(),
                       const std::string&     id = "membuf");

        /**
         * Construct a Document from the content of an input stream.
         *
         * @param stream the stream to read.
         * @param resolver the EntityResolver to use.
         * @param params XML parser parameters.
         * @param id document filename (for error reporting only).
         * @returns the new Document.
         */
        Document
        createDocument(std::istream&          stream,
                       EntityResolver&        resolver,
                       const ParseParameters& params = ParseParameters(),
                       const std::string&     id = "streambuf");

        /**
         * Parameters controlling DOM writing.
         *
         * The DOMSerializer provides for some control over the process
         * via DOMConfiguration.  They are settable here to allow their
         * use at a high level without the need to have access to the
         * internals of the Xerces-C writing process.  Simply create an
         * instance of this class, adjust the parameters as needed, and
         * then pass to a method call which uses WriteParameters as an
         * optional argument.
         *
         * If more precise control of the process is required, use the
         * Xerces-C classes directly.  These are simply a convienience
         * for the common case and will not suit every situation.
         */
        struct WriteParameters
        {
          /// Canonicalize document (canonical-form).
          bool canonicalForm;
          /// Retain CDATA (cdata-sections).
          bool CDATASections;
          /// Retain comments (comments).
          bool comments;
          /// Datatype normalization (datatype-normalization).
          bool datatypeNormalization;
          /// Discard defaults (discard-default-content).
          bool discardDefaultContent;
          /// Retain entities (entities).
          bool entities;
          /// Namespace processing (namespaces).
          bool namespaces;
          /// Include namespace declaration attributes (namespace-declarations).
          bool namespaceDeclarations;
          /// Normalize characters.
          bool normalizeCharacters;
          /// Pretty-print (format-pretty-print).
          bool prettyPrint;
          /// Split CDATA sections (split-cdata-sections).
          bool splitCDATASections;
          /// Validate if schema available (validate-if-schema).
          bool validate;
          /// Retain whitespace (element-content-whitespace).
          bool whitespace;
          /// Require XML declaration (xml-declaration).
          bool xmlDeclaration;

          /// Constructor.
          WriteParameters():
            canonicalForm(false),
            CDATASections(true),
            comments(true),
            datatypeNormalization(false),
            discardDefaultContent(true),
            entities(false),
            namespaces(true),
            namespaceDeclarations(true),
            normalizeCharacters(false),
            prettyPrint(false),
            splitCDATASections(true),
            validate(true),
            whitespace(false),
            xmlDeclaration(true)
          {
          }
        };

        /**
         * Write a Node to a file.
         *
         * @param node the node to use.
         * @param file the file to write.
         * @param params XML output parameters.
         */
        void
        writeNode(xercesc::DOMNode&              node,
                  const boost::filesystem::path& file,
                  const WriteParameters& params = WriteParameters());

        /**
         * Write a Node to a stream.
         *
         * @param node the node to use.
         * @param stream the stream to write to.
         * @param params XML output parameters.
         */
        void
        writeNode(xercesc::DOMNode&      node,
                  std::ostream&          stream,
                  const WriteParameters& params = WriteParameters());

        /**
         * Write a Node to a stream.
         *
         * @param node the node to use.
         * @param text the string to store the text in.
         * @param params XML output parameters.
         */
        void
        writeNode(xercesc::DOMNode&      node,
                  std::string&           text,
                  const WriteParameters& params = WriteParameters());

        /**
         * Write a Node to a file.
         *
         * @param node the node to use.
         * @param file the file to write.
         * @param params XML output parameters.
         */
        void
        writeNode(Node&                          node,
                  const boost::filesystem::path& file,
                  const WriteParameters&         params = WriteParameters());

        /**
         * Write a Node to a stream.
         *
         * @param node the node to use.
         * @param stream the stream to write to.
         * @param params XML output parameters.
         */
        void
        writeNode(Node&                  node,
                  std::ostream&          stream,
                  const WriteParameters& params = WriteParameters());

        /**
         * Write a Node to a stream.
         *
         * @param node the node to use.
         * @param text the string to store the text in.
         * @param params XML output parameters.
         */
        void
        writeNode(Node&                  node,
                  std::string&           text,
                  const WriteParameters& params = WriteParameters());

        /**
         * Write a Document to a file.
         *
         * @param document the document to use.
         * @param file the file to write.
         * @param params XML output parameters.
         */
        void
        writeDocument(Document&                      document,
                      const boost::filesystem::path& file,
                      const WriteParameters&         params = WriteParameters());

        /**
         * Write a Document to a stream.
         *
         * @param document the document to use.
         * @param stream the stream to write to.
         * @param params XML output parameters.
         */
        void
        writeDocument(Document&              document,
                      std::ostream&          stream,
                      const WriteParameters& params = WriteParameters());

        /**
         * Write a Document to a string.
         *
         * @param document the document to use.
         * @param text the string to store the text in.
         * @param params XML output parameters.
         */
        void
        writeDocument(Document&              document,
                      std::string&           text,
                      const WriteParameters& params = WriteParameters());

      }
    }
  }
}

#endif // OME_COMMON_XML_DOM_DOCUMENT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
