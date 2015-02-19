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

#ifndef OME_XML_MODEL_ORIGINALMETADATAANNOTATION_H
#define OME_XML_MODEL_ORIGINALMETADATAANNOTATION_H

#include <string>
#include <utility>

#include <ome/compat/log.h>
#include <ome/compat/memory.h>

#include <ome/xerces/dom/Document.h>
#include <ome/xerces/dom/Element.h>
#include <ome/xerces/dom/Node.h>
#include <ome/xerces/dom/NodeList.h>

#include <ome/xml/model/XMLAnnotation.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      // Forward declarations.
      class OMEModel;

      /**
       * OriginalMetadataAnnotation model object.
       */
      class OriginalMetadataAnnotation : public XMLAnnotation
      {
      public:
        /// Type of original annotation.
        typedef std::pair<std::string, std::string> metadata_type;

      private:
        /// Message logger.
        static ome::compat::Logger logger;
        /// Original metadata key-value pair.
        metadata_type metadata;

      public:
        /// Default constructor.
        OriginalMetadataAnnotation ();

        /**
         * Copy constructor.
         *
         * @param copy the object to copy.
         */
        OriginalMetadataAnnotation (const OriginalMetadataAnnotation& copy);

        /**
         * Construct from original metadata key and value.
         *
         * @param map the map to copy.
         */
        OriginalMetadataAnnotation (const metadata_type& map);

        /**
         * Construct a OriginalMetadataAnnotation recursively from an XML DOM tree.
         *
         * @param element root of the XML DOM tree to from which to
         * construct the model object graph.
         * @param model handler for the OME model used to track
         * instances and references seen during the update.
         * @throws EnumerationException if there is an error
         * instantiating an enumeration during model object creation.
         */
        OriginalMetadataAnnotation (xerces::dom::Element& element, ::ome::xml::model::OMEModel& model);

        /// Destructor.
        virtual
        ~OriginalMetadataAnnotation ();

        // Documented in superclass.
        const std::string&
        elementName() const;

        // Documented in superclass.
        bool
        validElementName(const std::string& name) const;

        /// @copydoc ome::xml::model::OMEModelObject::update
        virtual void
        update(const xerces::dom::Element&  element,
               ::ome::xml::model::OMEModel& model);

      public:
        /// @copydoc ome::xml::model::OMEModelObject::link
        bool
        link (std::shared_ptr<Reference>&                          reference,
              std::shared_ptr< ::ome::xml::model::OMEModelObject>& object);

        /**
         * Get the key-value pair mappings.
         *
         * @returns the map.
         */
        metadata_type&
        getMetadata ();

        /**
         * Get the key-value pair mappings.
         *
         * @returns the map.
         */
        const metadata_type&
        getMetadata () const;

        /**
         * Set the key-value pair mappings.
         *
         * @param map the map to set.
         */
        void
        setMetadata (const metadata_type& map);

        /// @copydoc ome::xml::model::OMEModelObject::asXMLElement
        virtual xerces::dom::Element
        asXMLElement (xerces::dom::Document& document) const;

      protected:
        // Documented in base class.
        virtual xerces::dom::Element
        asXMLElementInternal (xerces::dom::Document& document,
                              xerces::dom::Element&  element) const;
      };

    }
  }
}

#endif // OME_XML_MODEL_ORIGINALMETADATAANNOTATION_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
