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

#ifndef OME_XERCES_DOM_NODE_H
#define OME_XERCES_DOM_NODE_H

#include <ome/compat/config.h>

#include <cassert>
#include <string>
#include <ostream>

#include <xercesc/dom/DOMNode.hpp>

namespace ome
{
  namespace xerces
  {
    namespace dom
    {

      /**
       * DOM Node wrapper.  The wrapper behaves as though is the
       * wrapped DOMNode; it can be dereferenced using the "*" or "->"
       * operators to obtain a reference or pointer to the wrapped
       * object.  It can also be cast to a pointer to the wrapped
       * object, so can substitute for it directly.
       */
      class Node
      {
      public:
        /**
         * Construct a NULL Node.
         */
        Node ():
          xmlnode()
        {
        }

        /**
         * Copy construct a Node.
         *
         * @param node the Node to copy.
         */
        Node (const Node& node):
          xmlnode(node.xmlnode)
        {
        }

        /**
         * Construct a Node from a xercesc::DOMNode *.
         *
         * @param node the Node to wrap.
         */
        Node (xercesc::DOMNode *node):
          xmlnode(node)
        {
        }

        /// Destructor.
        ~Node ()
        {
        }

        /**
         * Append a child Node.
         *
         * @param node the child Node to append.
         * @returns the appended Node.
         */
        Node
        appendChild (const Node& node)
        {
          // TODO: Catch and rethrow xerces exceptions with the xerces
          // errors converted to sane descriptions.  And additionally
          // for all other xerces methods which throw.
          return this->xmlnode->appendChild(node.xmlnode);
        }

        /**
         * Assign a Node.
         *
         * @param node the Node to assign.
         * @returns the Node.
         */
        Node&
        operator= (Node& node)
        {
          this->xmlnode = node.xmlnode;
          return *this;
        }

        /**
         * Assign a xercesc::DOMNode *.
         *
         * @param node the Node to assign.
         * @returns the Node.
         */
        Node&
        operator= (xercesc::DOMNode *node)
        {
          this->xmlnode = node;
          return *this;
        }

        /**
         * Dereference to xercesc::DOMNode.
         *
         * @returns the wrapped xercesc::DOMNode.
         */
        xercesc::DOMNode&
        operator* () noexcept
        {
          assert(xmlnode != 0);
          return *xmlnode;
        }

        /**
         * Dereference to const xercesc::DOMNode.
         *
         * @returns the wrapped xercesc::DOMNode.
         */
        const xercesc::DOMNode&
        operator* () const noexcept
        {
          assert(xmlnode != 0);
          return *xmlnode;
        }

        /**
         * Dereference to xercesc::DOMNode.
         *
         * @returns the wrapped xercesc::DOMNode.
         */
        xercesc::DOMNode *
        operator-> () noexcept
        {
          assert(xmlnode != 0);
          return xmlnode;
        }

        /**
         * Dereference to const xercesc::DOMNode.
         *
         * @returns the wrapped xercesc::DOMNode.
         */
        const xercesc::DOMNode *
        operator-> () const noexcept
        {
          assert(xmlnode != 0);
          return xmlnode;
        }

        /**
         * Cast to xercesc::DOMNode *.
         *
         * @returns the wrapped xercesc::DOMNode.
         */
        inline
        operator xercesc::DOMNode* ()
        {
          return *this;
        }

        /**
         * Cast to const xercesc::DOMNode *.
         *
         * @returns the wrapped xercesc::DOMNode.
         */
        inline
        operator const xercesc::DOMNode* () const
        {
          return *this;
        }

        /**
         * Check if the wrapped Node is NULL.
         *
         * @returns true if valid, false if NULL.
         */
        operator bool () const
        {
          return xmlnode != 0;
        }

      private:
        /// The wrapped xercesc::DOMNode.
        xercesc::DOMNode *xmlnode;
      };

    }
  }
}

#endif // OME_XERCES_DOM_NODE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
