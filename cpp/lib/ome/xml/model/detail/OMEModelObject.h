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

#ifndef OME_XML_MODEL_DETAIL_OMEMODELOBJECT_H
#define OME_XML_MODEL_DETAIL_OMEMODELOBJECT_H

#include <ome/common/log.h>

#include <ome/xml/model/OMEModelObject.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {
      /**
       * Implementation details.
       *
       * Default concrete implementations of interfaces in the parent
       * namespace.
       */
      namespace detail
      {

        /**
         * OME model object (concrete implementation).
         */
        class OMEModelObject : virtual public ::ome::xml::model::OMEModelObject
        {
        protected:
          /// Constructor.
          OMEModelObject (const std::string& objectType = "OMEModelObject");

        public:
          /// Destructor.
          virtual
          ~OMEModelObject ();

        protected:
          /**
           * Copy constructor.
           *
           * @param copy the OMEModelObject to copy.
           */
          OMEModelObject (const OMEModelObject& copy);

        public:
          /// @copydoc ome::xml::model::OMEModelObject::validElementName
          bool
          validElementName(const std::string& name) const = 0;

        protected:
          /**
           * Transform the object hierarchy rooted at this element to
           * XML.  This internal implementation of asXMLelement also
           * requires an XML element, which must not be null, or may
           * be instantiated and passed from superclasses.
           *
           * @param document XML document for element creation.
           * @param element XML element for setting model data.
           * @returns an XML DOM tree root element for this model object.
           */
          virtual common::xml::dom::Element
          asXMLElementInternal (common::xml::dom::Document& document,
                                common::xml::dom::Element&  element) const = 0;

        public:
          /// @copydoc ome::xml::model::OMEModelObject::update
          virtual void
          update (const common::xml::dom::Element&  element,
                  ::ome::xml::model::OMEModel& model);

          /// @copydoc ome::xml::model::OMEModelObject::link
          virtual bool
          link (ome::compat::shared_ptr<Reference>&                          reference,
                ome::compat::shared_ptr< ::ome::xml::model::OMEModelObject>& object);

          /**
           * Retrieve all the children of an element that have a given
           * tag name. If a tag has a namespace prefix it will be
           * stripped prior to attempting a name match.
           *
           * @param parent DOM element to retrieve tags based upon.
           * @param name name of the tags to retrieve.
           * @return list of elements which have the tag <code>name</code>.
           */
          static std::vector<common::xml::dom::Element>
          getChildrenByTagName (const common::xml::dom::Element& parent,
                                const std::string&          name);

          /**
           * Strip the namespace prefix from a tag name.
           *
           * @param value tag name
           * @returns @a value with the namespace prefix stripped or @a
           * value if it does not have a namespace prefix.
           */
          static std::string
          stripNamespacePrefix (const std::string& value);

        protected:
          /**
           * Comparison functor.  Compares the referenced object with
           * the object reference passed by the function operator.  All
           * objects must be shared_ptr or weak_ptr of the same type (or
           * castable to the same type).
           *
           * @note This is a shared_ptr comparison, not a value
           * comparison.
           */
          template<typename T>
          class compare_element
          {
          private:
            /// The element to compare other elements with.
            const ome::compat::shared_ptr<const T>& cmp;

          public:
            /**
             * Constructor.
             *
             * @param cmp the element to compare other elements with.
             */
            compare_element(const ome::compat::shared_ptr<const T>& cmp):
              cmp(cmp)
            {}

            /**
             * Compare element with another element.
             *
             *
             * @param element the element to compare the original element with.
             * @returns @c true if the elements are the same, otherwise @c false.
             */
            bool
            operator () (const ome::compat::shared_ptr<T>& element)
            {
              return cmp && element && cmp == element;
            }

            /**
             * Compare element with another element.
             *
             * @param element the element to compare the original element with.
             * @returns @c true if the elements are the same, otherwise @c false.
             */
            bool
            operator () (const ome::compat::shared_ptr<const T>& element)
            {
              return cmp && element && cmp == element;
            }

            /**
             * Compare element with another element.
             *
             * @param element the element to compare the original element with.
             * @returns @c true if the elements are the same, otherwise @c false.
             */
            bool
            operator () (const ome::compat::weak_ptr<T>& element)
            {
              ome::compat::shared_ptr<const T> shared_element(element);
              return cmp && shared_element && cmp == shared_element;
            }

            /**
             * Compare element with another element.
             *
             * @param element the element to compare the original element with.
             * @returns @c true if the elements are the same, otherwise @c false.
             */
            bool
            operator () (const ome::compat::weak_ptr<const T>& element)
            {
              ome::compat::shared_ptr<const T> shared_element(element);
              return cmp && shared_element && cmp == shared_element;
            }
          };

          /**
           * Check if a container contains a particular element.
           *
           * @note This is a shared_ptr comparison, not an element value
           * comparison.
           *
           * @param container the container to check.
           * @param element the element to check for.
           * @returns @c true if the element was found, otherwise @c false.
           */
          template<class C, typename T>
          bool
          contains(const C&                  container,
                   const ome::compat::shared_ptr<T>& element)
          {
            return (std::find_if(container.begin(),
                                 container.end(),
                                 compare_element<T>(element)) != container.end());
          }

          /// Message logger.
          ome::common::Logger logger;
        };

      }
    }
  }
}

#endif // OME_XML_MODEL_DETAIL_OMEMODELOBJECT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
