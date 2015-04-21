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

#ifndef OME_COMMON_XML_DOM_NAMEDNODEMAP_H
#define OME_COMMON_XML_DOM_NAMEDNODEMAP_H

#include <ome/common/config.h>

#include <cassert>

#include <ome/common/xml/dom/Base.h>
#include <ome/common/xml/dom/Wrapper.h>

#include <xercesc/dom/DOMNamedNodeMap.hpp>

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
         * DOM NamedNodeMap wrapper.  The wrapper behaves as though is
         * the wrapped DOMNamedNodeMap; it can be dereferenced using the
         * "*" or "->" operators to obtain a reference or pointer to the
         * wrapped object.  It can also be cast to a pointer to the
         * wrapped object, so can substitute for it directly.
         */
        class NamedNodeMap : public Wrapper<xercesc::DOMNamedNodeMap, Base<xercesc::DOMNamedNodeMap> >
        {
        public:
          /**
           * Construct a NULL NamedNodeMap.
           */
          NamedNodeMap ():
            Wrapper<xercesc::DOMNamedNodeMap, Base<xercesc::DOMNamedNodeMap> >()
          {
          }

          /**
           * Copy construct a NamedNodeMap.
           *
           * @param nodelist the NamedNodeMap to copy.
           */
          NamedNodeMap (const NamedNodeMap& nodelist):
            Wrapper<xercesc::DOMNamedNodeMap, Base<xercesc::DOMNamedNodeMap> >(nodelist)
          {
          }

          /**
           * Construct a NamedNodeMap from a xercesc::DOMNamedNodeMap * (unmanaged).
           *
           * @param nodelist the NamedNodeMap to wrap.
           */
          NamedNodeMap (xercesc::DOMNamedNodeMap *nodelist):
            Wrapper<xercesc::DOMNamedNodeMap, Base<xercesc::DOMNamedNodeMap> >(static_cast<base_element_type *>(nodelist))
          {
          }

          /// Destructor.
          ~NamedNodeMap ()
          {
          }

          /**
           * Get an item by name.
           *
           * @param name the name of the item
           * @returns the item, which will be null if not found.
           */
          Node
          getNamedItem(const std::string& name);
        };

      }
    }
  }
}

#endif // OME_COMMON_XML_DOM_NAMEDNODEMAP_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
