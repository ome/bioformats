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

#ifndef OME_XML_MODEL_OMEMODELOBJECT_H
#define OME_XML_MODEL_OMEMODELOBJECT_H

#include <algorithm>
#include <map>
#include <string>
#include <vector>

#include <boost/multi_index_container.hpp>
#include <boost/multi_index/identity.hpp>
#include <boost/multi_index/indexed_by.hpp>
#include <boost/multi_index/ordered_index.hpp>
#include <boost/multi_index/random_access_index.hpp>

#include <ome/compat/memory.h>

#include <ome/common/xml/dom/Element.h>
#include <ome/common/xml/dom/Document.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      class OMEModel;
      class Reference;

      /**
       * OME model object interface.
       *
       * @todo Check constness and reference type for params/return types.
       * @todo Consider dropping redundant parts of typenames which
       * are duplicated in the namespace.  For example, OMEModelObject
       * could just be Object, since it's really an
       * ome::xml::model::Object.
       */
      class OMEModelObject : public ome::compat::enable_shared_from_this<OMEModelObject>
      {
      protected:
        /**
         * Multi-index container for efficient ordered insertion and
         * deletion of model object references.
         */
        template<typename T, template <typename ElementType> class Ptr>
        struct indexed_container
        {
          /// Multi-index container type.
          typedef boost::multi_index_container<
            Ptr<T>, // value type
            boost::multi_index::indexed_by<
              boost::multi_index::random_access<>, // insertion order
              boost::multi_index::ordered_unique<boost::multi_index::identity<Ptr<T> >, ome::compat::owner_less<Ptr<T> > > // sorted order
              >
            > type;
        };

        /// Constructor.
        OMEModelObject ()
        {}

      public:
        /// Destructor.
        virtual
        ~OMEModelObject ()
        {}

        /**
         * Get the element name of this model object.
         *
         * This will be the most-derived class name.
         *
         * @returns the element type.
         */
        virtual const std::string&
        elementName() const = 0;

        /**
         * Check if a given element name is valid for processing by
         * this model object.
         *
         * Used for processing nodes when interitance is involved.
         *
         * @param name the element name to check.
         *
         * @returns @c true if valid, @c false if invalid.
         */
        virtual bool
        validElementName(const std::string& name) const = 0;

      private:
        /// Copy constructor (deleted).
        OMEModelObject (const OMEModelObject&);

        /// Assignment operator (deleted).
        OMEModelObject&
        operator= (const OMEModelObject&);

      public:
        /**
         * Transform the object hierarchy rooted at this element to
         * XML.
         *
         * @param document document for element creation
         * @returns an XML DOM tree root element for this model object.
         */
        virtual common::xml::dom::Element
        asXMLElement (common::xml::dom::Document& document) const = 0;

        /**
         * Update the object hierarchy recursively from an XML DOM tree.
         *
         * @note No properties are removed, only added or updated.
         *
         * @param element root of the XML DOM tree to from which to
         * construct the model object graph.
         * @param model handler for the OME model used to track
         * instances and references seen during the update.
         * @throws EnumerationException if there is an error
         * instantiating an enumeration during model object creation,
         * or ModelException if there are any consistency or validity
         * errors found during processing.
         */
        virtual void
        update (const common::xml::dom::Element& element,
                OMEModel&                   model) = 0;

        /**
         * Link a given OME model object to this model object.
         *
         * @param reference type qualifier for the reference. This
         * should be the corresponding reference type for @a
         * object. If, for example, @a object is of type Image,
         * @a reference must be of type ImageRef.
         *
         * @param object Model object to link to.
         * @returns @c true if this model object was able to handle the
         * reference, otherwise @c false.
         *
         * @todo the use of @a reference to provide type information
         * for the type of @a object is unconventional and quite
         * possibly unnecessary--a simple string or type_info would
         * suffice for what it's being used for, if it's needed at
         * all.  Is this true for all cases?  The implementation also
         * needs to do strict checking of the @a object type; it's not
         * currently failing if it's of the wrong type.  This applies
         * to all generated model objects implementing this interface.
         */
        virtual bool
        link (ome::compat::shared_ptr<Reference>&      reference,
              ome::compat::shared_ptr<OMEModelObject>& object) = 0;

        /**
         * Get the XML namespace for this model object.
         *
         * @returns the XML namespace.
         */
        virtual const std::string&
        getXMLNamespace() const = 0;
      };

    }
  }
}

#endif // OME_XML_MODEL_OMEMODELOBJECT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
