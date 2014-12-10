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

#ifndef OME_XERCES_DOM_DOCUMENT_H
#define OME_XERCES_DOM_DOCUMENT_H

#include <ome/compat/config.h>
#include <ome/compat/memory.h>

#include <cassert>
#include <istream>
#include <string>
#include <ostream>

#include <xercesc/dom/DOMDocument.hpp>

#include <ome/compat/filesystem.h>

#include <ome/xerces/dom/Element.h>
#include <ome/xerces/dom/NodeList.h>
#include <ome/xerces/dom/Wrapper.h>
#include <ome/xerces/String.h>

namespace ome
{
  namespace xerces
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
      template<int S>
      class DocumentWrapper : public Wrapper<xercesc::DOMDocument, NodeWrapper<S> >
      {
      public:
        /**
         * Construct a NULL Document.
         */
        DocumentWrapper ():
          Wrapper<xercesc::DOMDocument, NodeWrapper<S> >()
        {
        }

        /**
         * Copy construct a Document.
         *
         * @param document the Document to copy.
         */
        DocumentWrapper (const DocumentWrapper& document):
          Wrapper<xercesc::DOMDocument, NodeWrapper<S> >(document)
        {
        }

        /**
         * Copy construct a Document.
         *
         * @param base the base type to copy (must be a Document).
         */
        DocumentWrapper (const typename Wrapper<xercesc::DOMDocument, NodeWrapper<S> >::base_type& base):
          Wrapper<xercesc::DOMDocument, NodeWrapper<S> >(base)
        {
        }

        /**
         * Construct a Document from a xercesc::DOMDocument *.
         *
         * @param document the Document to wrap.
         */
        DocumentWrapper (typename Wrapper<xercesc::DOMDocument, NodeWrapper<S> >::element_type *document):
          Wrapper<xercesc::DOMDocument, NodeWrapper<S> >(document)
        {
        }

        /**
         * Construct a Document from a xercesc::DOMNode *.
         *
         * @param base the DOMNode to wrap.
         */
        DocumentWrapper (typename Wrapper<xercesc::DOMDocument, NodeWrapper<S> >::base_element_type *base):
          Wrapper<xercesc::DOMDocument, NodeWrapper<S> >(base)
        {
        }

        /// Destructor.
        ~DocumentWrapper ()
        {
        }

        /**
         * Assign a Document.
         *
         * @param wrapped the Document to assign.
         * @returns the Document.
         */
        DocumentWrapper&
        operator= (const DocumentWrapper& wrapped)
        {
          Wrapper<xercesc::DOMDocument, NodeWrapper<S> >::operator=(wrapped);
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
          xerces::String xns(ns);
          xerces::String xname(name);

          return (*this)->createElementNS(xns, xname);
        }

        /**
         * Get the root element of this document.
         *
         * @returns the root element.
         */
        Element
        getDocumentElement()
        {
          return (*this)->getDocumentElement();
        }

        /**
         * Get child nodes.
         *
         * @returns the child nodes (if any).
         */
        NodeList
        getChildNodes()
        {
          return (*this)->getChildNodes();
        }
      };

      /// Managed Document.
      typedef DocumentWrapper<MANAGED> ManagedDocument;
      /// Unmanaged Document.
      typedef DocumentWrapper<UNMANAGED> UnmanagedDocument;
      /// Default Document.
      typedef ManagedDocument Document;

      /**
       * Construct an empty Document.
       *
       * @param qualifiedName the qualified name of the document type.
       * @returns the new Document.
       */
      Document
      createEmptyDocument(const std::string& qualifiedName);

      /**
       * Construct a Document from the content of a file.
       *
       * @param file the file to read.
       * @returns the new Document.
       */
      Document
      createDocument(const boost::filesystem::path& file);

      /**
       * Construct a Document from the content of a string.
       *
       * @param text the string to use.
       * @returns the new Document.
       */
      Document
      createDocument(const std::string& text);

      /**
       * Construct a Document from the content of an input stream.
       *
       * @param stream the stream to read.
       * @returns the new Document.
       */
      Document
      createDocument(std::istream& stream);

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
       * Write a Document to a stream.
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

#endif // OME_XERCES_DOM_DOCUMENT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
