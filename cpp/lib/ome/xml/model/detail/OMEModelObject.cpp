/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2006 - 2016 Open Microscopy Environment:
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
#include <ome/common/xml/String.h>

#include <ome/xml/model/detail/OMEModelObject.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {
      namespace detail
      {

        OMEModelObject::OMEModelObject (const std::string& objectType):
          ::ome::xml::model::OMEModelObject(),
          logger(ome::common::createLogger(objectType))
        {
        }

        OMEModelObject::~OMEModelObject ()
        {
        }

        OMEModelObject::OMEModelObject (const OMEModelObject& copy):
          ::ome::xml::model::OMEModelObject(),
          logger(copy.logger)
        {
          // Nothing to copy.
        }

        bool
        OMEModelObject::validElementName(const std::string& /* name */) const
        {
          return false;
        }

        common::xml::dom::Element
        OMEModelObject::asXMLElementInternal (common::xml::dom::Document& /* document */,
                                              common::xml::dom::Element&  element) const
        {
          return element;
        }

        void
        OMEModelObject::update (const common::xml::dom::Element&  /* element */,
                                ::ome::xml::model::OMEModel& /* model */)
        {
        }

        bool
        OMEModelObject::link (ome::compat::shared_ptr<Reference>&                          /* reference */,
                              ome::compat::shared_ptr< ::ome::xml::model::OMEModelObject>& /* object */)
        {
          return false;
        }

        std::vector<common::xml::dom::Element>
        OMEModelObject::getChildrenByTagName (const common::xml::dom::Element& parent,
                                              const std::string&          name)
        {
          // TODO: May need to be a shared_ptr<element> if element is not refcounting.
          std::vector<common::xml::dom::Element> ret;

          common::xml::dom::NodeList children(parent->getChildNodes());
          // TODO: correct type for iteration.
          for (common::xml::dom::NodeList::iterator pos = children.begin();
               pos != children.end();
               ++pos)
            {
              try
                {
                  // This pointer check is unnecessary--the Element
                  // class would throw; but this avoids the need to
                  // throw and catch many std::logic_error exceptions
                  // during document processing.
                  if (dynamic_cast<const xercesc::DOMElement *>(pos->get()))
                    {
                      common::xml::dom::Element child(pos->get(), false);
                      if (child && name == stripNamespacePrefix(common::xml::String(child->getNodeName())))
                        {
                          ret.push_back(child);
                        }
                    }
                }
              catch (std::logic_error&)
                {
                  // Not an Element.
                }
            }
          return ret;
        }

        std::string
        OMEModelObject::stripNamespacePrefix (const std::string& value) {
          std::string ret;
          std::string::size_type i = value.find_last_of(':');
          if (i != std::string::npos && ++i < value.size())
            ret = value.substr(i);
          else
            ret = value;
          return ret;
        }

      }
    }
  }
}
