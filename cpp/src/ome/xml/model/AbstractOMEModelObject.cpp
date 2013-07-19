/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
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

#include <ome/xerces/dom/nodelist.h>
#include <ome/xerces/string.h>

#include <ome/xml/model/AbstractOMEModelObject.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      AbstractOMEModelObject::AbstractOMEModelObject ():
        OMEModelObject()
      {
      }

      AbstractOMEModelObject::~AbstractOMEModelObject ()
      {
      }

      xerces::dom::element&
      AbstractOMEModelObject::asXMLElementInternal (xerces::dom::document& document,
                                                    xerces::dom::element& element) const
      {
        return element;
      }

      void
      AbstractOMEModelObject::update (const xerces::dom::element& element,
                                      OMEModel& model)
      {
      }

      bool
      AbstractOMEModelObject::link (std::shared_ptr<Reference>& reference,
                                    std::shared_ptr<OMEModelObject>& o)
      {
        return false;
      }

      std::vector<xerces::dom::element>
      AbstractOMEModelObject::getChildrenByTagName (const xerces::dom::element& parent,
                                                    const std::string& name)
      {
        // TODO: May need to be a shared_ptr<element> if element is not refcounting.
        std::vector<xerces::dom::element> ret;

        xerces::dom::nodelist children(parent->getChildNodes());
        // TODO: correct type for iteration.
        for (xerces::dom::nodelist::iterator pos = children.begin();
             pos != children.end();
             ++pos)
          {
            // Note that this will be null if not an element node.
            xerces::dom::element child(*pos);
            if (child && name == stripNamespacePrefix(xerces::string(child->getNodeName()))) {
              xerces::dom::element c2(child);
              ret.push_back(child);
            }
          }
        return ret;
      }

      std::string
      AbstractOMEModelObject::stripNamespacePrefix (const std::string& v) {
        std::string ret;
        std::string::size_type i = v.find_last_of(':');
        if (i != std::string::npos)
          {
              ++i;
              if (i < v.size())
                ret = v.substr(i);
          }
        return v;
      }

    }
  }
}
