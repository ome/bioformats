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

#ifndef OME_XERCES_DOM_NODELIST_H
#define OME_XERCES_DOM_NODELIST_H

#include <ome/compat/config.h>

#include <cassert>
#include <string>
#include <ostream>

#include <xercesc/dom/DOMNodeList.hpp>

#include <ome/xerces/dom/node.h>

namespace ome
{
  namespace xerces
  {
    namespace dom
    {

      class nodelist
      {
      public:
        typedef XMLSize_t size_type;

        class iterator
        {
        private:
          iterator ():
            index(0),
            xmlnodelist(0),
            xmlnode(static_cast<xercesc::DOMNode *>(0))
          {}

          iterator (xercesc::DOMNodeList *xmlnodelist,
                    size_type             index):
            index(index),
            xmlnodelist(xmlnodelist),
            xmlnode(xmlnodelist->item(index))
          {}

        public:
          iterator (const iterator& rhs):
            index(rhs.index),
            xmlnodelist(rhs.xmlnodelist),
            xmlnode(rhs.xmlnode)
          {}

          ~iterator ()
          {}

          node&
          operator* () noexcept
          {
            assert(xmlnode);
            return xmlnode;
          }

          node *
          operator-> () noexcept
          {
            assert(xmlnode);
            return &xmlnode;
          }

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

          bool
          operator == (const iterator& rhs) const noexcept
          {
            if (!xmlnode && !rhs.xmlnode)
              return true;
            if (xmlnode || rhs.xmlnode)
              return false;
            return index == rhs.index;
          }

          bool
          operator != (const iterator& rhs) const noexcept
          {
            return *this != rhs;
          }

          friend class nodelist;

        private:
          size_type index;
          xercesc::DOMNodeList *xmlnodelist;
          node xmlnode;
        };

        nodelist ():
          xmlnodelist()
        {
        }

        nodelist (const nodelist& nodelist):
          xmlnodelist(nodelist.xmlnodelist)
        {
        }

        nodelist (xercesc::DOMNodeList *nodelist):
          xmlnodelist(nodelist)
        {
        }

        ~nodelist ()
        {
        }

        nodelist&
        operator= (nodelist& nodelist)
        {
          this->xmlnodelist = nodelist.xmlnodelist;
          return *this;
        }

        nodelist&
        operator= (xercesc::DOMNodeList *nodelist)
        {
          this->xmlnodelist = nodelist;
          return *this;
        }

        xercesc::DOMNodeList&
        operator* () noexcept
        {
          assert(xmlnodelist != 0);
          return *xmlnodelist;
        }

        const xercesc::DOMNodeList&
        operator* () const noexcept
        {
          assert(xmlnodelist != 0);
          return *xmlnodelist;
        }

        xercesc::DOMNodeList *
        operator-> () noexcept
        {
          assert(xmlnodelist != 0);
          return xmlnodelist;
        }

        const xercesc::DOMNodeList *
        operator-> () const noexcept
        {
          assert(xmlnodelist != 0);
          return xmlnodelist;
        }

        operator bool () const
        {
          return xmlnodelist != 0;
        }

        size_type
        size() const
        {
          size_type ret = 0;

          if (xmlnodelist)
            ret = xmlnodelist->getLength();

          return ret;
        }

        iterator
        begin()
        {
          return iterator(xmlnodelist, 0);
        }

        iterator
        end()
        {
          return iterator();
        }

      private:
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
