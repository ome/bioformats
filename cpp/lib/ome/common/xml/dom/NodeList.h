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

#ifndef OME_COMMON_XML_DOM_NODELIST_H
#define OME_COMMON_XML_DOM_NODELIST_H

#include <ome/common/config.h>

#include <cassert>

#include <ome/common/xml/dom/Base.h>
#include <ome/common/xml/dom/Wrapper.h>

#include <xercesc/dom/DOMNodeList.hpp>

namespace ome
{
  namespace common
  {
    namespace xml
    {
      namespace dom
      {

        class Node;

        /**
         * DOM NodeList wrapper.  The wrapper behaves as though is the
         * wrapped DOMNodeList; it can be dereferenced using the "*" or
         * "->" operators to obtain a reference or pointer to the
         * wrapped object.  It can also be cast to a pointer to the
         * wrapped object, so can substitute for it directly.
         */
        class NodeList : public Wrapper<xercesc::DOMNodeList, Base<xercesc::DOMNodeList> >
        {
        public:
          /// The NodeList size type.
          typedef XMLSize_t size_type;

          /**
           * Iterator for a NodeList.
           */
          class iterator
          {
          private:
            /**
             * Construct a null iterator.  This is used to refer to
             * invalid positions such as past-the-end.
             */
            iterator();

            /**
             * Construct an iterator at the specified position for the
             * specified list.
             *
             * @param xmlnodelist the NodeList to iterate over.
             * @param index the index into the NodeList.
             */
            iterator(xercesc::DOMNodeList *xmlnodelist,
                     size_type             index);

          public:
            /**
             * Copy construct an iterator.
             *
             * @param rhs the iterator to copy.
             */
            iterator(const iterator& rhs);

            /// Destructor.
            ~iterator();

            /**
             * Dereference the iterator.
             *
             * @return a reference to the Node at this position.
             */
            Node&
            operator* () noexcept;

            /**
             * Dereference the iterator.
             *
             * @return a pointer to the Node at this position.
             */
            Node *
            operator-> () noexcept;

            /**
             * Move the iterator backward one element.
             *
             * @returns the iterator at the new position.
             */
            iterator&
            operator-- ();

            /**
             * Move the iterator forward one element.
             *
             * @returns the iterator at the new position.
             */
            iterator&
            operator++ ();

            /**
             * Check the equality of two iterators.
             *
             * @param rhs the iterator to compare with.
             * @returns true if equal, otherwise false.
             */
            bool
            operator == (const iterator& rhs) const noexcept;

            /**
             * Check the non-equality of two iterators.
             *
             * @param rhs the iterator to compare with.
             * @returns true if not equal, otherwise false.
             */
            bool
            operator != (const iterator& rhs) const noexcept
            {
              return !(*this == rhs);
            }

            friend class NodeList;

          private:
            /// Index into the list.
            size_type index;
            /// List being iterated over.
            xercesc::DOMNodeList *xmlnodelist;
            /// Node at current position.
            ome::compat::shared_ptr<Node> xmlnode;
          };

          /**
           * Construct a NULL NodeList.
           */
          NodeList ():
            Wrapper<xercesc::DOMNodeList, Base<xercesc::DOMNodeList> >()
          {
          }

          /**
           * Copy construct a NodeList.
           *
           * @param nodelist the NodeList to copy.
           */
          NodeList (const NodeList& nodelist):
            Wrapper<xercesc::DOMNodeList, Base<xercesc::DOMNodeList> >(nodelist)
          {
          }

          /**
           * Construct a NodeList from a xercesc::DOMNodeList * (unmanaged).
           *
           * @param nodelist the NodeList to wrap.
           */
          NodeList (xercesc::DOMNodeList *nodelist):
            Wrapper<xercesc::DOMNodeList, Base<xercesc::DOMNodeList> >(static_cast<base_element_type *>(nodelist))
          {
          }

          /// Destructor.
          ~NodeList ()
          {
          }

          /**
           * Get the size (length) of the NodeList.
           *
           * @returns the list size.
           */
          size_type
          size() const
          {
            size_type ret = 0;

            if (this->get())
              return (*this)->getLength();

            return ret;
          }

          /**
           * Check if the NodeList is empty.
           *
           * @returns @c true if empty, @c false if not empty.
           */
          bool
          empty() const
          {
            return size() == 0;
          }

          /**
           * Get an iterator pointing to the first element in the NodeList.
           *
           * @returns an iterator pointing to the first element in the NodeList.
           */
          iterator
          begin()
          {
            return iterator(this->get(), 0);
          }

          /**
           * Get an iterator pointing to the past-the-end element in the NodeList.
           *
           * @returns an iterator pointing to the past-the-end element in the NodeList.
           */
          iterator
          end()
          {
            return iterator();
          }

          /**
           * Get the element at a particular index.
           *
           * @param index the index of the element.
           * @returns the Node at the specified index.
           */
          Node
          at(size_type index);
        };

      }
    }
  }
}

#endif // OME_COMMON_XML_DOM_NODELIST_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
