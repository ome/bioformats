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

#ifndef ABSTRACT_OME_XML_MODEL_OMEMODELOBJECT_H
#define ABSTRACT_OME_XML_MODEL_OMEMODELOBJECT_H

#include <map>
#include <memory>
#include <string>
#include <vector>

#include <ome/xerces/dom/element.h>
#include <ome/xerces/dom/document.h>

#include <ome/xml/model/OMEModelObject.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      // TODO: constness and ref type for params/return types

      class AbstractOMEModelObject : public OMEModelObject
      {
      public:
	typedef std::shared_ptr<AbstractOMEModelObject> shared_ptr;
	typedef const std::shared_ptr<AbstractOMEModelObject> const_shared_ptr;
	typedef std::weak_ptr<AbstractOMEModelObject> weak_ptr;
	typedef const std::weak_ptr<AbstractOMEModelObject> const_weak_ptr;

	AbstractOMEModelObject ();

        virtual
        ~AbstractOMEModelObject ();

	virtual xerces::dom::element&
	asXMLElement (xerces::dom::document& document) const = 0;

      protected:
	virtual xerces::dom::element&
	asXMLElementInternal (xerces::dom::document& document,
                              xerces::dom::element& element) const = 0;

      public:
	/** 
	 * Updates the object hierarchy recursively from an XML DOM tree.
	 * <b>NOTE:</b> No properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	virtual void
	update (const xerces::dom::element& element,
		OMEModel& model);

	virtual bool
	link (std::shared_ptr<Reference>& reference,
	      OMEModelObject::shared_ptr& o);

        /**
         * Retrieves all the children of an element that have a given tag name. If a
         * tag has a namespace prefix it will be stripped prior to attempting a
         * name match.
         * @param parent DOM element to retrieve tags based upon.
         * @param name Name of the tags to retrieve.
         * @return List of elements which have the tag <code>name</code>.
         */
        static std::vector<xerces::dom::element>
        getChildrenByTagName (xerces::dom::element& parent,
                              const std::string& name);

        /**
         * Strips the namespace prefix off of a given tag name.
         * @param v Tag name to strip the prefix from if it has one.
         * @return <code>v</code> with the namespace prefix stripped or <code>v</code>
         * if it has none.
         */
        static std::string
        stripNamespacePrefix (const std::string& v);

      };

    }
  }
}

#endif // ABSTRACT_OME_XML_MODEL_OMEMODELOBJECT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
