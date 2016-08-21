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

#ifndef OME_XERCES_DOM_NODELIST_H
#define OME_XERCES_DOM_NODELIST_H

#include <ome/compat/config.h>

#include <cassert>
#include <string>
#include <ostream>

#include <xercesc/dom/DOMNodeList.hpp>

#include <ome/xerces/dom/Node.h>

namespace ome
{
  namespace xerces
  {
    namespace dom
    {

      /**
       * DOM NodeList wrapper.  The wrapper behaves as though is the
       * wrapped DOMNodeList; it can be dereferenced using the "*" or
       * "->" operators to obtain a reference or pointer to the
       * wrapped object.  It can also be cast to a pointer to the
       * wrapped object, so can substitute for it directly.
       */
      class NodeList
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
          iterator ():
            index(0),
            xmlnodelist(0),
            xmlnode(static_cast<xercesc::DOMNode *>(0))
          {}

          /**
           * Construct an iterator at the specified position for the
           * specified list.
           *
           * @param xmlnodelist the NodeList to iterate over.
           * @param index the index into the NodeList.
           */
          iterator (xercesc::DOMNodeList *xmlnodelist,
                    size_type             index):
            index(index),
            xmlnodelist(xmlnodelist),
            xmlnode(xmlnodelist->item(index))
          {}

        public:
          /**
           * Copy construct an iterator.
           *
           * @param rhs the iterator to copy.
           */
          iterator (const iterator& rhs):
            index(rhs.index),
            xmlnodelist(rhs.xmlnodelist),
            xmlnode(rhs.xmlnode)
          {}

          /// Destructor.
          ~iterator ()
          {}

          /**
           * Dereference the iterator.
           *
           * @return a reference to the Node at this position.
           */
          Node&
          operator* () noexcept
          {
            assert(xmlnode);
            return xmlnode;
          }

          /**
           * Dereference the iterator.
           *
           * @return a pointer to the Node at this position.
           */
          Node *
          operator-> () noexcept
          {
            assert(xmlnode);
            return &xmlnode;
          }

          /**
           * Move the iterator backward one element.
           *
           * @returns the iterator at the new position.
           */
          iterator&
          operator-- ()
          {
            if (xmlnode)
              {
                if (index != 0)
                  {
                    --index;
                    xmlnode = xmlnodelist->item(index);
                  }
                else
                  xmlnode = 0;
              }
            return *this;
          }

          /**
           * Move the iterator forward one element.
           *
           * @returns the iterator at the new position.
           */
          iterator&
          operator++ ()
          {
            if (xmlnode)
              {
                ++index;
                xmlnode = xmlnodelist->item(index);
              }
            return *this;
          }

          /**
           * Check the equality of two iterators.
           *
           * @param rhs the iterator to compare with.
           * @returns true if equal, otherwise false.
           */
          bool
          operator == (const iterator& rhs) const noexcept
          {
            if (!xmlnode && !rhs.xmlnode)
              return true;
            if (xmlnode || rhs.xmlnode)
              return false;
            return index == rhs.index;
          }

          /**
           * Check the non-equality of two iterators.
           *
           * @param rhs the iterator to compare with.
           * @returns true if not equal, otherwise false.
           */
          bool
          operator != (const iterator& rhs) const noexcept
          {
            return *this != rhs;
          }

          friend class NodeList;

        private:
          /// Index into the list.
          size_type index;
          /// List being iterated over.
          xercesc::DOMNodeList *xmlnodelist;
          /// Node at current position.
          Node xmlnode;
        };

        /**
         * Construct a NULL NodeList.
         */
        NodeList ():
          xmlnodelist()
        {
        }

        /**
         * Copy construct a NodeList.
         *
         * @param nodelist the NodeList to copy.
         */
        NodeList (const NodeList& nodelist):
          xmlnodelist(nodelist.xmlnodelist)
        {
        }

        /**
         * Construct a NodeList from a xercesc::DOMNodeList *.
         *
         * @param nodelist the NodeList to wrap.
         */
        NodeList (xercesc::DOMNodeList *nodelist):
          xmlnodelist(nodelist)
        {
        }

        /// Destructor.
        ~NodeList ()
        {
        }

        /**
         * Assign a NodeList.
         *
         * @param nodelist the NodeList to assign.
         * @returns the NodeList.
         */
        NodeList&
        operator= (NodeList& nodelist)
        {
          this->xmlnodelist = nodelist.xmlnodelist;
          return *this;
        }

        /**
         * Assign a xercesc::DOMNodeList *.
         *
         * @param nodelist the NodeList to assign.
         * @returns the NodeList.
         */
        NodeList&
        operator= (xercesc::DOMNodeList *nodelist)
        {
          this->xmlnodelist = nodelist;
          return *this;
        }

        /**
         * Dereference to xercesc::DOMNodeList.
         *
         * @returns the wrapped xercesc::DOMNodeList.
         */
        xercesc::DOMNodeList&
        operator* () noexcept
        {
          assert(xmlnodelist != 0);
          return *xmlnodelist;
        }

        /**
         * Dereference to const xercesc::DOMNodeList.
         *
         * @returns the wrapped xercesc::DOMNodeList.
         */
        const xercesc::DOMNodeList&
        operator* () const noexcept
        {
          assert(xmlnodelist != 0);
          return *xmlnodelist;
        }

        /**
         * Dereference to xercesc::DOMNodeList.
         *
         * @returns the wrapped xercesc::DOMNodeList.
         */
        xercesc::DOMNodeList *
        operator-> () noexcept
        {
          assert(xmlnodelist != 0);
          return xmlnodelist;
        }

        /**
         * Dereference to const xercesc::DOMNodeList.
         *
         * @returns the wrapped xercesc::DOMNodeList.
         */
        const xercesc::DOMNodeList *
        operator-> () const noexcept
        {
          assert(xmlnodelist != 0);
          return xmlnodelist;
        }

        /**
         * Check if the wrapped NodeList is NULL.
         *
         * @returns true if valid, false if NULL.
         */
        operator bool () const
        {
          return xmlnodelist != 0;
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

          if (xmlnodelist)
            ret = xmlnodelist->getLength();

          return ret;
        }

        /**
         * Get an iterator pointing to the first element in the NodeList.
         *
         * @returns an iterator pointing to the first element in the NodeList.
         */
        iterator
        begin()
        {
          return iterator(xmlnodelist, 0);
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

      private:
        /// The wrapped xercesc::DOMNodeList.
        xercesc::DOMNodeList *xmlnodelist;
      };

    }
  }
}

#endif // OME_XERCES_DOM_NODELIST_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
