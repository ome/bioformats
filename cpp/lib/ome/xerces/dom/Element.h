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

#ifndef OME_XERCES_DOM_ELEMENT_H
#define OME_XERCES_DOM_ELEMENT_H

#include <ome/compat/config.h>

#include <cassert>
#include <string>
#include <ostream>

#include <xercesc/dom/DOMElement.hpp>

#include <ome/xerces/dom/Node.h>
#include <ome/xerces/String.h>

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
      class Element : public Node
      {
      public:
        /**
         * Construct a NULL Element.
         */
        Element ():
          xmlelem()
        {
        }

        /**
         * Copy construct an Element.
         *
         * @param element the Element to copy.
         */
        Element (const Element& element):
          xmlelem(element.xmlelem)
        {
        }

        /**
         * Construct an Element from a node.
         *
         * @param node the node to copy.
         */
        Element (Node& node):
          xmlelem()
        {
          *this = node;
        }

        /**
         * Construct an Element from a xercesc::DOMElement *.
         *
         * @param element the Element to wrap.
         */
        Element (xercesc::DOMElement *element):
          xmlelem(element)
        {
        }

        /**
         * Construct an Element from a xercesc::DOMNode *.
         *
         * @param node the node to wrap.
         */
        Element (xercesc::DOMNode *node):
          xmlelem()
        {
          *this = node;
        }

        /// Destructor.
        ~Element()
        {
        }

        /**
         * Get Element tag name.
         *
         * @returns the tag name.
         */
        String
        getTagName () const
        {
          return xmlelem->getTagName();
        }

        /**
         * Check if the Element has the specified attribute.
         *
         * @param attr the attribute to check.
         * @returns true if the Element has the attribute, otherwise
         * false.
         */
        bool
        hasAttribute (const std::string& attr) const
        {
          return xmlelem->hasAttribute(xerces::String(attr));
        }

        /**
         * Get the specified attribute value.
         *
         * @param attr the attribute to get.
         * @returns the attribute value.
         */
        String
        getAttribute (const std::string& attr) const
        {
          return xmlelem->getAttribute(xerces::String(attr));
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
          return xmlelem->setAttribute(xerces::String(attr),
                                       xerces::String(val));
        }

        /**
         * Get Element text content.
         *
         * @returns the text content.
         */
        String
        getTextContent () const
        {
          return xmlelem->getTextContent();
        }

        /**
         * Set Element text content.
         *
         * @param val the text content to set.
         */
        void
        setTextContent (const std::string& val)
        {
          return xmlelem->setTextContent(xerces::String(val));
        }

        /**
         * Assign an Element.
         *
         * @param element the Element to assign.
         * @returns the Element.
         */
        Element&
        operator= (Element& element)
        {
          this->xmlelem = element.xmlelem;
          return *this;
        }

        /**
         * Assign a Node.
         *
         * @param node the Node to assign.
         * @returns the Element.
         */
        Element&
        operator= (Node& node)
        {
          xercesc::DOMNode *xnode = static_cast<xercesc::DOMNode *>(node);
          this->xmlelem = dynamic_cast<xercesc::DOMElement*>(xnode);
          return *this;
        }

        /**
         * Assign a xercesc::DOMElement *.
         *
         * @param element the Element to assign.
         * @returns the Element.
         */
        Element&
        operator= (xercesc::DOMElement *element)
        {
          this->xmlelem = element;
          return *this;
        }

        /**
         * Assign a xercesc::DOMNode *.
         *
         * @param node the node to assign.
         * @returns the Element.
         */
        Element&
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
         * Check if the wrapped Element is NULL.
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
