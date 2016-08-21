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

#include <ome/compat/memory.h>

#include <ome/xerces/dom/Element.h>
#include <ome/xerces/dom/Document.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      class OMEModel;
      class Reference;

      /**
       * OME model object.
       *
       * @todo Check constness and reference type for params/return types.
       * @todo Consider dropping redundant parts of typenames which
       * are duplicated in the namespace.  For example, OMEModelObject
       * could just be Object, since it's really an
       * ome::xml::model::Object.
       */
      class OMEModelObject : public std::enable_shared_from_this<OMEModelObject>
      {
      public:
        /// Constructor.
        OMEModelObject ();

        /// Destructor.
        virtual
        ~OMEModelObject ();

        /**
         * Transform the object hierarchy rooted at this element to
         * XML.
         *
         * @param document document for element creation
         * @returns an XML DOM tree root element for this model object.
         */
        virtual xerces::dom::Element&
        asXMLElement (xerces::dom::Document& document) const = 0;

      protected:
        /**
         * Transform the object hierarchy rooted at this element to
         * XML.  This internal implementation of asXMLelement also
         * requires an XML element, which may be null, or may be
         * instantiated and passed from superclasses.
         *
         * @param document XML document for element creation.
         * @param element XML element for setting model data.
         * @returns an XML DOM tree root element for this model object.
         */
        virtual xerces::dom::Element&
        asXMLElementInternal (xerces::dom::Document& document,
                              xerces::dom::Element&  element) const = 0;

      public:
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
         * instantiating an enumeration during model object creation.
         */
        virtual void
        update (const xerces::dom::Element& element,
                OMEModel&                   model);

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
        link (std::shared_ptr<Reference>&      reference,
              std::shared_ptr<OMEModelObject>& object);

        /**
         * Retrieves all the children of an element that have a given tag name. If a
         * tag has a namespace prefix it will be stripped prior to attempting a
         * name match.
         * @param parent DOM element to retrieve tags based upon.
         * @param name Name of the tags to retrieve.
         * @return List of elements which have the tag <code>name</code>.
         */
        static std::vector<xerces::dom::Element>
        getChildrenByTagName (const xerces::dom::Element& parent,
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
         * @todo: Use of strict const since this is nonmodifying.
         */
        template<typename T>
        class compare_element
        {
        private:
          /// The element to compare other elements with.
          std::shared_ptr<const T> cmp;

        public:
          /**
           * Constructor.
           *
           * @param cmp the element to compare other elements with.
           */
          compare_element(const std::shared_ptr<const T>& cmp):
            cmp(cmp)
          {}

          /**
           * Compare element with another element.
           *
           * @note This is a shared_ptr comparison, not a value
           * comparison.
           *
           * @param element the element to compare the original element with.
           * @returns @c true if the elements are the same, otherwise @c false.
           */
          bool
          operator () (std::weak_ptr<const T> element)
          {
            std::shared_ptr<const T> shared_element(element);
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
                 const std::shared_ptr<T>& element)
        {
          return (std::find_if(container.begin(),
                               container.end(),
                               compare_element<T>(element)) != container.end());
        }

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
