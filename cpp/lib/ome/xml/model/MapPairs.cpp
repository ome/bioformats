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

#include <iostream>

#include <ome/internal/version.h>

#include <ome/xml/model/MapPairs.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      namespace
      {
        const std::string NAMESPACE("http://www.openmicroscopy.org/Schemas/ROI/" OME_MODEL_VERSION);
      }

      MapPairs::MapPairs ():
        ::ome::xml::model::OMEModelObject(),
        detail::OMEModelObject(),
        map()
      {
      }

      MapPairs::MapPairs (const MapPairs& copy):
        ::ome::xml::model::OMEModelObject(),
        detail::OMEModelObject(copy),
        map(copy.map)
      {
      }

      MapPairs::MapPairs (const map_type& map):
        ::ome::xml::model::OMEModelObject(),
        detail::OMEModelObject(),
        map(map)
      {
      }

      MapPairs::MapPairs (xerces::dom::Element&        element,
                          ::ome::xml::model::OMEModel& model):
        detail::OMEModelObject(),
        map()
      {
        update(element, model);
      }

      MapPairs::~MapPairs ()
      {
      }

      void
      MapPairs::update(const xerces::dom::Element&  element,
                       ::ome::xml::model::OMEModel& model)
      {
        detail::OMEModelObject::update(element, model);
        std::string tagName(element.getTagName());
        if (tagName != "MapPairs")
          {
            std::clog << "Expecting node name of MapPairs got " << tagName << std::endl;
          }

        std::vector<xerces::dom::Element> M_nodeList(getChildrenByTagName(element, "M"));
        for (std::vector<xerces::dom::Element>::iterator elem = M_nodeList.begin();
             elem != M_nodeList.end();
             ++elem)
          {
            if (elem->hasAttribute("K"))
              {
                std::string key(elem->getAttribute("K"));
                std::string value(elem->getTextContent());
                map.insert(std::make_pair(key, value));
              }
            else
              {
                std::clog << "MapPairs entry M does not contain key attribute K";
              }
          }
      }

      bool
      MapPairs::link (std::shared_ptr<Reference>&                          reference,
                      std::shared_ptr< ::ome::xml::model::OMEModelObject>& object)
      {
        if (detail::OMEModelObject::link(reference, object))
          {
            return true;
          }
        std::clog << "Unable to handle reference of type: " << typeid(reference).name() << std::endl;
        return false;
      }

      xerces::dom::Element&
      MapPairs::asXMLElement (xerces::dom::Document& document) const
      {
        xerces::dom::Element nullelem;
        return asXMLElementInternal(document, nullelem);
      }

      xerces::dom::Element&
      MapPairs::asXMLElementInternal (xerces::dom::Document& document,
                                      xerces::dom::Element&  element) const
      {
        // Creating XML block for Line

        if (!element)
          {
            xerces::dom::Element newElement = document.createElementNS(NAMESPACE, "Line");
            element = newElement;
          }

        for (map_type::const_iterator i = map.begin();
             i != map.end();
             ++i)
          {
            xerces::dom::Element pair = document.createElementNS(NAMESPACE, "M");
            pair.setAttribute("K", i->first);
            pair.setTextContent(i->second);
            element.appendChild(pair);
          }

        return detail::OMEModelObject::asXMLElementInternal(document, element);
      }

    }
  }
}
