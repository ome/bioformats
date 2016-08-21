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

#ifndef OME_XERCES_DOM_DOCUMENT_H
#define OME_XERCES_DOM_DOCUMENT_H

#include <ome/compat/config.h>

#include <cassert>
#include <string>
#include <ostream>

#include <xercesc/dom/DOMDocument.hpp>

#include <ome/xerces/dom/Element.h>
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
      class Document
      {
      public:
        /**
         * Construct a NULL Document.
         */
        Document ():
          xmldoc()
        {
        }

        /**
         * Copy construct a Document.
         *
         * @param document the Document to copy.
         */
        Document (const Document& document):
          xmldoc(document.xmldoc)
        {
        }

        /**
         * Construct a Document from a xercesc::DOMDocument *.
         *
         * @param document the Document to wrap.
         */
        Document (xercesc::DOMDocument *document):
          xmldoc(document)
        {
        }

        /// Destructor.
        ~Document ()
        {
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

          return xmldoc->createElementNS(xns, xname);
        }

        /**
         * Assign a Document.
         *
         * @param document the Document to assign.
         * @returns the Document.
         */
        Document&
        operator= (Document& document)
        {
          this->xmldoc = document.xmldoc;
          return *this;
        }

        /**
         * Assign a xercesc::DOMDocument *.
         *
         * @param document the Document to assign.
         * @returns the Document.
         */
        Document&
        operator= (xercesc::DOMDocument *document)
        {
          this->xmldoc = document;
          return *this;
        }

        /**
         * Dereference to xercesc::DOMDocument.
         *
         * @returns the wrapped xercesc::DOMDocument.
         */
        xercesc::DOMDocument&
        operator* () noexcept
        {
          assert(xmldoc != 0);
          return *xmldoc;
        }

        /**
         * Dereference to const xercesc::DOMDocument.
         *
         * @returns the wrapped xercesc::DOMDocument.
         */
        const xercesc::DOMDocument&
        operator* () const noexcept
        {
          assert(xmldoc != 0);
          return *xmldoc;
        }

        /**
         * Dereference to xercesc::DOMDocument.
         *
         * @returns the wrapped xercesc::DOMDocument.
         */
        xercesc::DOMDocument *
        operator-> () noexcept
        {
          assert(xmldoc != 0);
          return xmldoc;
        }

        /**
         * Dereference to const xercesc::DOMDocument.
         *
         * @returns the wrapped xercesc::DOMDocument.
         */
        const xercesc::DOMDocument *
        operator-> () const noexcept
        {
          assert(xmldoc != 0);
          return xmldoc;
        }

        /**
         * Check if the wrapped Document is NULL.
         *
         * @returns true if valid, false if NULL.
         */
        operator bool () const
        {
          return xmldoc != 0;
        }

      private:
        /// The wrapped xercesc::DOMDocument.
        xercesc::DOMDocument *xmldoc;
      };

    }
  }
}

#endif // OME_XERCES_DOM_DOCUMENT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
