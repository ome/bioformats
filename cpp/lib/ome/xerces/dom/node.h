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

      class node
      {
      public:
        node ():
          xmlnode()
        {
        }

        node (const node& node):
          xmlnode(node.xmlnode)
        {
        }

        node (xercesc::DOMNode *node):
          xmlnode(node)
        {
        }

        ~node ()
        {
        }

        node
        appendChild (const node& node)
        {
          // TODO: Catch and rethrow xerces exceptions with the xerces
          // errors converted to sane descriptions.  And additionally
          // for all other xerces methods which throw.
          return this->xmlnode->appendChild(node.xmlnode);
        }

        node&
        operator= (node& node)
        {
          this->xmlnode = node.xmlnode;
          return *this;
        }

        node&
        operator= (xercesc::DOMNode *node)
        {
          this->xmlnode = node;
          return *this;
        }

        xercesc::DOMNode&
        operator* () noexcept
        {
          assert(xmlnode != 0);
          return *xmlnode;
        }

        const xercesc::DOMNode&
        operator* () const noexcept
        {
          assert(xmlnode != 0);
          return *xmlnode;
        }

        xercesc::DOMNode *
        operator-> () noexcept
        {
          assert(xmlnode != 0);
          return xmlnode;
        }

        const xercesc::DOMNode *
        operator-> () const noexcept
        {
          assert(xmlnode != 0);
          return xmlnode;
        }

        inline
        operator xercesc::DOMNode* ()
        {
          return *this;
        }

        inline
        operator const xercesc::DOMNode* () const
        {
          return *this;
        }

        operator bool () const
        {
          return xmlnode != 0;
        }

      private:
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
