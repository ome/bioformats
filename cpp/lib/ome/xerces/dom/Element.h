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

#ifndef OME_XERCES_DOM_ELEMENT_H
#define OME_XERCES_DOM_ELEMENT_H

#include <string>

#include <xercesc/dom/DOMElement.hpp>

#include <ome/xerces/String.h>
#include <ome/xerces/dom/Node.h>
#include <ome/xerces/dom/Wrapper.h>

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
      template<int S>
      class ElementWrapper : public Wrapper<xercesc::DOMElement, NodeWrapper<S> >
      {
      public:
        /**
         * Construct a NULL Element.
         */
        ElementWrapper ():
          Wrapper<xercesc::DOMElement, NodeWrapper<S> >()
        {
        }

        /**
         * Copy construct an Element.
         *
         * @param element the Element to copy.
         */
        ElementWrapper (const ElementWrapper& element):
          Wrapper<xercesc::DOMElement, NodeWrapper<S> >(element)
        {
        }

        /**
         * Copy construct an Element.
         *
         * @param base the base type to copy (must be an Element).
         */
        ElementWrapper (const typename Wrapper<xercesc::DOMElement, NodeWrapper<S> >::base_type& base):
          Wrapper<xercesc::DOMElement, NodeWrapper<S> >(base)
        {
        }

        /**
         * Construct an Element from a xercesc::DOMElement *.
         *
         * @param element the Element to wrap.
         */
        ElementWrapper (xercesc::DOMElement *element):
          Wrapper<xercesc::DOMElement, NodeWrapper<S> >(element)
        {
        }

        /**
         * Construct an Element from a xercesc::DOMNode *.
         *
         * @param base the DOMNode to wrap.
         */
        ElementWrapper (typename Wrapper<xercesc::DOMElement, NodeWrapper<S> >::base_element_type *base):
          Wrapper<xercesc::DOMElement, NodeWrapper<S> >(base)
        {
        }

        /// Destructor.
        ~ElementWrapper()
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
          return (*this)->getTagName();
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
          return (*this)->hasAttribute(xerces::String(attr));
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
          return (*this)->getAttribute(xerces::String(attr));
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
          return (*this)->setAttribute(xerces::String(attr),
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
          return (*this)->getTextContent();
        }

        /**
         * Set Element text content.
         *
         * @param val the text content to set.
         */
        void
        setTextContent (const std::string& val)
        {
          return (*this)->setTextContent(xerces::String(val));
        }
      };

      /// Managed Element.
      typedef ElementWrapper<MANAGED> ManagedElement;
      /// Unmanaged Element.
      typedef ElementWrapper<UNMANAGED> UnmanagedElement;
      /// Default Element.
      typedef UnmanagedElement Element;

    }
  }
}

#endif // OME_XERCES_DOM_ELEMENT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
