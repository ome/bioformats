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

#include <ome/common/xml/dom/NodeList.h>
#include <ome/common/xml/dom/Node.h>

namespace ome
{
  namespace common
  {
    namespace xml
    {
      namespace dom
      {

        NodeList::iterator::iterator ():
          index(0),
          xmlnodelist(0),
          xmlnode()
        {}

        NodeList::iterator::iterator (xercesc::DOMNodeList *xmlnodelist,
                                      size_type             index):
          index(index),
          xmlnodelist(xmlnodelist),
          xmlnode(ome::compat::shared_ptr<Node>(new Node(xmlnodelist->item(index), false)))
        {}

        NodeList::iterator::iterator (const iterator& rhs):
          index(rhs.index),
          xmlnodelist(rhs.xmlnodelist),
          xmlnode(rhs.xmlnode)
        {}

        NodeList::iterator::~iterator ()
        {}

        Node&
        NodeList::iterator::operator* () noexcept
        {
          assert(xmlnode && *xmlnode);
          return *xmlnode;
        }

        Node *
        NodeList::iterator::operator-> () noexcept
        {
          assert(xmlnode && *xmlnode);
          return &*xmlnode;
        }

        NodeList::iterator&
        NodeList::iterator::operator-- ()
        {
          if (xmlnode && *xmlnode)
            {
              if (index != 0)
                {
                  --index;
                  *xmlnode = Node(xmlnodelist->item(index), false);
                }
              else
                *xmlnode = Node(static_cast<xercesc::DOMNode *>(0), false);
            }
          return *this;
        }

        NodeList::iterator&
        NodeList::iterator::operator++ ()
        {
          if (xmlnode && *xmlnode)
            {
              ++index;
              *xmlnode = Node(xmlnodelist->item(index), false);
            }
          return *this;
        }

        bool
        NodeList::iterator::operator == (const iterator& rhs) const noexcept
        {
          // both null
          if ((!xmlnode || !(*xmlnode)) && ((!rhs.xmlnode) || !(*rhs.xmlnode)))
            return true;
          // one null
          else if ((xmlnode && *xmlnode) || (rhs.xmlnode && *rhs.xmlnode))
            return false;
          else return index == rhs.index;
        }

        Node
        NodeList::at(size_type index)
        {
          return Node((*this)->item(index), false);
        }

      }
    }
  }
}
