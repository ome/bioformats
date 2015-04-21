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

#include <ome/internal/version.h>

#include <ome/xml/model/OME.h>
#include <ome/xml/model/MapPairs.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      namespace
      {
        const std::string MAP_NAMESPACE("http://www.openmicroscopy.org/Schemas/SA/" OME_MODEL_VERSION);
        const std::string PAIRS_NAMESPACE("http://www.openmicroscopy.org/Schemas/OME/" OME_MODEL_VERSION);
      }

      MapPairs::MapPairs ():
        ::ome::xml::model::OMEModelObject(),
        detail::OMEModelObject(),
        map()
      {
#ifdef OME_HAVE_BOOST_LOG
        logger.add_attribute("ClassName", logging::attributes::constant<std::string>("MapPairs"));
#else // ! OME_HAVE_BOOST_LOG
        logger.className("MapPairs");
#endif // OME_HAVE_BOOST_LOG
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
#ifdef OME_HAVE_BOOST_LOG
        logger.add_attribute("ClassName", logging::attributes::constant<std::string>("MapPairs"));
#else // ! OME_HAVE_BOOST_LOG
        logger.className("MapPairs");
#endif // OME_HAVE_BOOST_LOG
      }

      MapPairs::MapPairs (common::xml::dom::Element&        element,
                          ::ome::xml::model::OMEModel& model):
        detail::OMEModelObject(),
        map()
      {
        update(element, model);
      }

      MapPairs::~MapPairs ()
      {
      }

      const std::string&
      MapPairs::elementName() const
      {
        static const std::string type("MapPairs");
        return type;
      }

      bool
      MapPairs::validElementName(const std::string& name) const
      {
        static const std::string expectedTagName("MapPairs");

        return expectedTagName == name || detail::OMEModelObject::validElementName(name);
      }

      void
      MapPairs::update(const common::xml::dom::Element&  element,
                       ::ome::xml::model::OMEModel& model)
      {
        detail::OMEModelObject::update(element, model);
        std::string tagName(element.getTagName());
        //+        if (!("Map".equals(tagName) || "Value".equals(tagName))) {
        if (tagName != "Map" && tagName != "Value")
          {
            BOOST_LOG_SEV(logger, ome::logging::trivial::warning)
              << "Expecting node name of Map or Value, got " << tagName;
          }

        std::vector<common::xml::dom::Element> M_nodeList(getChildrenByTagName(element, "M"));
        for (std::vector<common::xml::dom::Element>::iterator elem = M_nodeList.begin();
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
                BOOST_LOG_SEV(logger, ome::logging::trivial::warning)
                  << "MapPairs entry M does not contain key attribute K";
              }
          }
      }

      bool
      MapPairs::link (ome::compat::shared_ptr<Reference>&                          reference,
                      ome::compat::shared_ptr< ::ome::xml::model::OMEModelObject>& object)
      {
        if (detail::OMEModelObject::link(reference, object))
          {
            return true;
          }
        BOOST_LOG_SEV(logger, ome::logging::trivial::warning)
          << "Unable to handle reference of type: "
          << typeid(reference).name();
        return false;
      }

      common::xml::dom::Element
      MapPairs::asXMLElement (common::xml::dom::Document& document) const
      {
        common::xml::dom::Element nullelem;
        return asXMLElementInternal(document, nullelem);
      }

      common::xml::dom::Element
      MapPairs::asXMLElementInternal (common::xml::dom::Document& document,
                                      common::xml::dom::Element&  element) const
      {
        // Creating XML block for Line

        if (!element)
          {
            // A node named "Map" is only desired if we are working
            // with an instance of Map (a subclass of MapPairs), in
            // which case it is the subclass' responsibility to ensure
            // that 'pairs' is a node of the correct name and type.
            common::xml::dom::Element newElement = document.createElementNS(MAP_NAMESPACE, "Line");
            element = newElement;
          }

        for (map_type::const_iterator i = map.begin();
             i != map.end();
             ++i)
          {
            common::xml::dom::Element pair = document.createElementNS(PAIRS_NAMESPACE, "M");
            pair.setAttribute("K", i->first);
            pair.setTextContent(i->second);
            element.appendChild(pair);
          }

        return detail::OMEModelObject::asXMLElementInternal(document, element);
      }

      MapPairs::map_type&
      MapPairs::getMap ()
      {
        return map;
      }

      const MapPairs::map_type&
      MapPairs::getMap () const
      {
        return map;
      }

      void
      MapPairs::setMap (const map_type& map)
      {
        this->map = map;
      }

      const std::string&
      MapPairs::getXMLNamespace() const
      {
        // This is a hack; we don't have direct access to the
        // namespace at present since it's emitted by the code
        // generator.  Copy the namespace from the generated OME
        // class.
        OME ome;
        return ome.getXMLNamespace();
      }

    }
  }
}
