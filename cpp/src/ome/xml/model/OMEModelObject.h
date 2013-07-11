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

#ifndef OME_XML_MODEL_OMEMODELOBJECT_H
#define OME_XML_MODEL_OMEMODELOBJECT_H

#include <map>
#include <memory>
#include <string>

#include <ome/xerces/dom/element.h>
#include <ome/xerces/dom/document.h>

// #include <ome/xml/model/Reference.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      class OMEModel;
      class Reference;

      // TODO: constness and ref type for params/return types

      class OMEModelObject
      {
      public:
	typedef std::shared_ptr<OMEModelObject> shared_ptr;
	typedef const std::shared_ptr<OMEModelObject> const_shared_ptr;
	typedef std::weak_ptr<OMEModelObject> weak_ptr;
	typedef const std::weak_ptr<OMEModelObject> const_weak_ptr;

        inline
	OMEModelObject ()
	{}

        inline virtual
        ~OMEModelObject ()
	{}

	/**
	 * Takes the entire object hierarchy and produces an XML DOM tree.
	 * @param document Destination document for element creation, etc.
	 * @return XML DOM tree root element for this model object.
	 */
	virtual xerces::dom::element&
	asXMLElement (xerces::dom::document& document) const = 0;

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
	update (xerces::dom::element& element,
		OMEModel& model) = 0;

	/**
	 * Link a given OME model object to this model object.
	 * @param reference The <i>type</i> qualifier for the reference. This should
	 * be the corresponding reference type for <code>o</code>. If, for example,
	 * <code>o</code> is of type <code>Image</code>, <code>reference</code>
	 * <b>MUST</b> be of type <code>ImageRef</code>.
	 * @param o Model object to link to.
	 * @return <code>true</code> if this model object was able to handle the
	 * reference, <code>false</code> otherwise.
	 */
	virtual bool
	link (std::shared_ptr<Reference>& reference,
	      OMEModelObject::shared_ptr& o) = 0;

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
