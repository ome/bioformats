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

#ifndef OME_XERCES_DOM_ELEMENT_H
#define OME_XERCES_DOM_ELEMENT_H

#include <ome/compat/config.h>

#include <cassert>
#include <string>
#include <ostream>

#include <xercesc/dom/DOMElement.hpp>

#include <ome/xerces/dom/node.h>
#include <ome/xerces/string.h>

namespace ome
{
  namespace xerces
  {
    namespace dom
    {

      /**
       * DOM Element wrapper.  The wrapper behaves as though is the
       * wrapped DOMElement; it can be dereferenced using the "*" or
       * "->" operators to obtain a reference or pointer to the
       * wrapped object.  It can also be cast to a pointer to the
       * wrapped object, so can substitute for it directly.
       */
      class element : public node
      {
      public:
        /**
         * Construct a NULL element.
         */
        element ():
          xmlelem()
        {
        }

        /**
         * Copy construct an element.
         *
         * @param element the element to copy.
         */
        element (const element& element):
          xmlelem(element.xmlelem)
        {
        }

        /**
         * Construct an element from a node.
         *
         * @param node the node to copy.
         */
        element (node& node):
          xmlelem()
        {
          *this = node;
        }

        /**
         * Construct an element from a xercesc::DOMElement *.
         *
         * @param element the element to wrap.
         */
        element (xercesc::DOMElement *element):
          xmlelem(element)
        {
        }

        /**
         * Construct an element from a xercesc::DOMNode *.
         *
         * @param node the node to wrap.
         */
        element (xercesc::DOMNode *node):
          xmlelem()
        {
          *this = node;
        }

        /// Destructor.
        ~element()
        {
        }

        /**
         * Get element tag name.
         *
         * @returns the tag name.
         */
        string
        getTagName () const
        {
          return xmlelem->getTagName();
        }

        /**
         * Check if the element has the specified attribute.
         *
         * @param attr the attribute to check.
         * @returns true if the element has the attribute, otherwise
         * false.
         */
        bool
        hasAttribute (const std::string& attr) const
        {
          return xmlelem->hasAttribute(xerces::string(attr));
        }

        /**
         * Get the specified attribute value.
         *
         * @param attr the attribute to get.
         * @returns the attribute value.
         */
        string
        getAttribute (const std::string& attr) const
        {
          return xmlelem->getAttribute(xerces::string(attr));
        }

        /**
         * Set the specified attribute value.
         *
         * @param attr the attribute to set.
         * @param val the value to set.
         */
        void
        setAttribute (const std::string& attr,
                      const std::string& val)
        {
          return xmlelem->setAttribute(xerces::string(attr),
                                       xerces::string(val));
        }

        /**
         * Get element text content.
         *
         * @returns the text content.
         */
        string
        getTextContent () const
        {
          return xmlelem->getTextContent();
        }

        /**
         * Set element text content.
         *
         * @param val the text content to set.
         */
        void
        setTextContent (const std::string& val)
        {
          return xmlelem->setTextContent(xerces::string(val));
        }

        /**
         * Assign an element.
         *
         * @param element the element to assign.
         * @returns the element.
         */
        element&
        operator= (element& element)
        {
          this->xmlelem = element.xmlelem;
          return *this;
        }

        /**
         * Assign a node.
         *
         * @param node the node to assign.
         * @returns the element.
         */
        element&
        operator= (node& node)
        {
          xercesc::DOMNode *xnode = static_cast<xercesc::DOMNode *>(node);
          this->xmlelem = dynamic_cast<xercesc::DOMElement*>(xnode);
          return *this;
        }

        /**
         * Assign a xercesc::DOMElement *.
         *
         * @param element the element to assign.
         * @returns the element.
         */
        element&
        operator= (xercesc::DOMElement *element)
        {
          this->xmlelem = element;
          return *this;
        }

        /**
         * Assign a xercesc::DOMNode *.
         *
         * @param node the node to assign.
         * @returns the element.
         */
        element&
        operator= (xercesc::DOMNode *node)
        {
          this->xmlelem = dynamic_cast<xercesc::DOMElement *>(node);
          return *this;
        }

        /**
         * Dereference to xercesc::DOMElement.
         *
         * @returns the wrapped xercesc::DOMElement.
         */
        xercesc::DOMElement&
        operator* () noexcept
        {
          assert(xmlelem != 0);
          return *xmlelem;
        }

        /**
         * Dereference to const xercesc::DOMElement.
         *
         * @returns the wrapped xercesc::DOMElement.
         */
        const xercesc::DOMElement&
        operator* () const noexcept
        {
          assert(xmlelem != 0);
          return *xmlelem;
        }

        /**
         * Dereference to xercesc::DOMElement.
         *
         * @returns the wrapped xercesc::DOMElement.
         */
        xercesc::DOMElement *
        operator-> () noexcept
        {
          assert(xmlelem != 0);
          return xmlelem;
        }

        /**
         * Dereference to const xercesc::DOMElement.
         *
         * @returns the wrapped xercesc::DOMElement.
         */
        const xercesc::DOMElement *
        operator-> () const noexcept
        {
          assert(xmlelem != 0);
          return xmlelem;
        }

        /**
         * Check if the wrapped element is NULL.
         *
         * @returns true if valid, false if NULL.
         */
        operator bool () const
        {
          return xmlelem != 0;
        }

      private:
        /// The wrapped xercesc::DOMElement.
        xercesc::DOMElement *xmlelem;
      };

    }
  }
}

#endif // OME_XERCES_DOM_ELEMENT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
