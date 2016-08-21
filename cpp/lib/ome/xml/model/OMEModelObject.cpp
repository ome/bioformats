/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
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

#include <ome/xerces/dom/NodeList.h>
#include <ome/xerces/String.h>

#include <ome/xml/model/OMEModelObject.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      OMEModelObject::OMEModelObject ():
        std::enable_shared_from_this<OMEModelObject>()
      {
      }

      OMEModelObject::~OMEModelObject ()
      {
      }

      xerces::dom::Element&
      OMEModelObject::asXMLElementInternal (xerces::dom::Document& document,
                                            xerces::dom::Element&  element) const
      {
        return element;
      }

      void
      OMEModelObject::update (const xerces::dom::Element& element,
                              OMEModel&                   model)
      {
      }

      bool
      OMEModelObject::link (std::shared_ptr<Reference>&      reference,
                            std::shared_ptr<OMEModelObject>& object)
      {
        return false;
      }

      std::vector<xerces::dom::Element>
      OMEModelObject::getChildrenByTagName (const xerces::dom::Element& parent,
                                            const std::string&          name)
      {
        // TODO: May need to be a shared_ptr<element> if element is not refcounting.
        std::vector<xerces::dom::Element> ret;

        xerces::dom::NodeList children(parent->getChildNodes());
        // TODO: correct type for iteration.
        for (xerces::dom::NodeList::iterator pos = children.begin();
             pos != children.end();
             ++pos)
          {
            // Note that this will be null if not an element node.
            xerces::dom::Element child(*pos);
            if (child && name == stripNamespacePrefix(xerces::String(child->getNodeName()))) {
              xerces::dom::Element c2(child);
              ret.push_back(child);
            }
          }
        return ret;
      }

      std::string
      OMEModelObject::stripNamespacePrefix (const std::string& value) {
        std::string ret;
        std::string::size_type i = value.find_last_of(':');
        if (i != std::string::npos)
          {
              ++i;
              if (i < value.size())
                ret = value.substr(i);
          }
        return value;
      }

    }
  }
}
