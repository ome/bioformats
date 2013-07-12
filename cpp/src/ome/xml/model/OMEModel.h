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

#ifndef OME_XML_MODEL_OMEMODEL_H
#define OME_XML_MODEL_OMEMODEL_H

#include <map>
#include <memory>
#include <string>
#include <vector>

#include <ome/xml/model/OMEModelObject.h>
#include <ome/xml/model/Reference.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      // TODO: constness and ref type for params/return types

      class OMEModel
      {
      public:
        typedef OMEModelObject::shared_ptr object_ptr_type;
        typedef std::vector<Reference::shared_ptr> reference_list_type;
	typedef std::map<std::string, object_ptr_type> object_map_type;
	typedef std::map<object_ptr_type, reference_list_type> reference_map_type;
        typedef reference_map_type::size_type size_type;

        inline
	OMEModel ()
	{}

        inline virtual
        ~OMEModel ()
	{}

	virtual
	OMEModelObject::shared_ptr
	addModelObject(const std::string&   id,
		       OMEModelObject::shared_ptr& object) = 0;

	virtual
	OMEModelObject::shared_ptr
	removeModelObject (const std::string& id) = 0;

	virtual
	OMEModelObject::shared_ptr
	getModelObject (const std::string& id) const = 0;
	
	// TODO: Reference or value?
	virtual
	const object_map_type&
	getModelObjects() const = 0;

	virtual
	bool
	addReference (OMEModelObject::shared_ptr& a,
		      Reference::shared_ptr& b) = 0;

	virtual
	const reference_map_type&
	getReferences () const = 0;

	virtual
	size_type
	resolveReferences () = 0;

      };

    }
  }
}

#endif // OME_XML_MODEL_OMEMODEL_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
