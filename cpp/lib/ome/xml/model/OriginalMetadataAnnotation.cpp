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

#include <iostream>

#include <ome/internal/version.h>

#include <boost/format.hpp>

#include <boost/format.hpp>

#include <ome/common/xml/Platform.h>

#include <ome/xml/model/ModelException.h>
#include <ome/xml/model/OMEModel.h>
#include <ome/xml/model/OriginalMetadataAnnotation.h>

using boost::format;

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

      OriginalMetadataAnnotation::OriginalMetadataAnnotation ():
        ::ome::xml::model::OMEModelObject(),
        XMLAnnotation(),
        metadata()
      {
#ifdef OME_HAVE_BOOST_LOG
        logger.add_attribute("ClassName", logging::attributes::constant<std::string>("OriginalMetadataAnnotation"));
#else // ! OME_HAVE_BOOST_LOG
        logger.className("OriginalMetadataAnnotation");
#endif // OME_HAVE_BOOST_LOG
      }

      OriginalMetadataAnnotation::OriginalMetadataAnnotation (const OriginalMetadataAnnotation& copy):
        ::ome::xml::model::OMEModelObject(),
        XMLAnnotation(copy),
        metadata(copy.metadata)
      {
      }

      OriginalMetadataAnnotation::OriginalMetadataAnnotation (const metadata_type& metadata):
        ::ome::xml::model::OMEModelObject(),
        XMLAnnotation(),
        metadata(metadata)
      {
      }

      OriginalMetadataAnnotation::OriginalMetadataAnnotation (common::xml::dom::Element&        element,
                          ::ome::xml::model::OMEModel& model):
        XMLAnnotation(),
        metadata()
      {
        update(element, model);
      }

      OriginalMetadataAnnotation::~OriginalMetadataAnnotation ()
      {
      }

      const std::string&
      OriginalMetadataAnnotation::elementName() const
      {
        static const std::string type("OriginalMetadataAnnotation");
        return type;
      }

      bool
      OriginalMetadataAnnotation::validElementName(const std::string& name) const
      {
        static const std::string expectedTagName("OriginalMetadataAnnotation");

        return expectedTagName == name || XMLAnnotation::validElementName(name);
      }

      void
      OriginalMetadataAnnotation::update(const common::xml::dom::Element&  element,
                                         ::ome::xml::model::OMEModel& model)
      {
        // Delegate checking of element type.
        XMLAnnotation::update(element, model);

        std::vector<common::xml::dom::Element> XMLValue_nodeList = getChildrenByTagName(element, "Value");
        if (XMLValue_nodeList.size() > 1)
          {
            format fmt("Value node list size %1% != 1");
            fmt % XMLValue_nodeList.size();
            throw ModelException(fmt.str());
          }
        else if (XMLValue_nodeList.size() != 0)
          {
            std::vector<common::xml::dom::Element> OriginalMetadataValue_nodeList = getChildrenByTagName(XMLValue_nodeList.at(0), "OriginalMetadata");
            if (OriginalMetadataValue_nodeList.size() > 1)
              {
                format fmt("OriginalMetadata node list size %1% != 1");
                fmt % OriginalMetadataValue_nodeList.size();
                throw ModelException(fmt.str());
              }
            else if (OriginalMetadataValue_nodeList.size() != 0)
              {
                std::vector<common::xml::dom::Element> Key_nodeList = getChildrenByTagName(OriginalMetadataValue_nodeList.at(0), "Key");
                if (Key_nodeList.size() > 1)
                  {
                    format fmt("OriginalMetadata Key node list size %1% != 1");
                    fmt % Key_nodeList.size();
                    throw ModelException(fmt.str());
                  }
                else if (Key_nodeList.size() != 0)
                  {
                    metadata.first = Key_nodeList.at(0).getTextContent();
                  }
                std::vector<common::xml::dom::Element> Value_nodeList = getChildrenByTagName(OriginalMetadataValue_nodeList.at(0), "Value");
                if (Value_nodeList.size() > 1)
                  {
                    format fmt("OriginalMetadata Value node list size %1% != 1");
                    fmt % Value_nodeList.size();
                    throw ModelException(fmt.str());
                  }
                else if (Value_nodeList.size() != 0)
                  {
                    metadata.second = Value_nodeList.at(0).getTextContent();
                  }
              }
          }
    }

      bool
      OriginalMetadataAnnotation::link (ome::compat::shared_ptr<Reference>&                          reference,
                                        ome::compat::shared_ptr< ::ome::xml::model::OMEModelObject>& object)
      {
        if (XMLAnnotation::link(reference, object))
          {
            return true;
          }
        BOOST_LOG_SEV(logger, ome::logging::trivial::warning)
          << "Unable to handle reference of type: "
          << typeid(reference).name();
        return false;
      }

      common::xml::dom::Element
      OriginalMetadataAnnotation::asXMLElement (common::xml::dom::Document& document) const
      {
        common::xml::dom::Element nullelem;
        return asXMLElementInternal(document, nullelem);
      }

      common::xml::dom::Element
      OriginalMetadataAnnotation::asXMLElementInternal (common::xml::dom::Document& document,
                                                        common::xml::dom::Element&  element) const
      {

        return XMLAnnotation::asXMLElementInternal(document, element);
      }

      OriginalMetadataAnnotation::metadata_type&
      OriginalMetadataAnnotation::getMetadata ()
      {
        return metadata;
      }

      const OriginalMetadataAnnotation::metadata_type&
      OriginalMetadataAnnotation::getMetadata () const
      {
        return metadata;
      }

      void
      OriginalMetadataAnnotation::setMetadata (const metadata_type& metadata)
      {
        this->metadata = metadata;

        common::xml::Platform xmlplat;

        common::xml::dom::Document Value_document(ome::common::xml::dom::createEmptyDocument("wrapped"));
        common::xml::dom::Element Value_root = Value_document.getDocumentElement();

        common::xml::dom::Element keyElement =
          Value_document.createElement("Key");
        keyElement.setTextContent(metadata.first);
        common::xml::dom::Element valueElement =
          Value_document.createElement("Value");
        valueElement.setTextContent(metadata.second);

        common::xml::dom::Element originalMetadata =
          Value_document.createElement("OriginalMetadata");
        originalMetadata.appendChild(keyElement);
        originalMetadata.appendChild(valueElement);
        Value_root.appendChild(originalMetadata);

        std::string textvalue;
        ome::common::xml::dom::writeNode(originalMetadata, textvalue);
        setValue(textvalue);

      }

    }
  }
}
