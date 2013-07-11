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

#include <map>
#include <memory>
#include <string>

#include <ome/xml/model/OMEModelImpl.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      OMEModelImpl::OMEModelImpl ():
	modelObjects(),
	references()
      {
      }

      OMEModelImpl::~OMEModelImpl ()
      {
      }

      OMEModelObject::shared_ptr
      OMEModelImpl::removeModelObject(const std::string& id)
      {
        OMEModelObject::shared_ptr ret;

        object_map_type::iterator i = modelObjects.find(id);
        if (i != modelObjects.end())
          {
            ret = i->second;
            modelObjects.erase(i);
          }

	return ret;
      }

      OMEModelObject::shared_ptr
      OMEModelImpl::addModelObject(const std::string& id,
				   OMEModelObject::shared_ptr& object)
      {
        // Don't store references.
        if (dynamic_cast<Reference *>(&object) != 0)
	  return object;

        object_map_type::iterator i = modelObjects.find(id);
        if (i != modelObjects.end())
          i->second = object;
        else
          modelObjects.insert(std::make_pair(id, object));
	}

        return object;
      }

      OMEModelObject::shared_ptr
      OMEModelImpl::getModelObject(const std::string& id) const
	{
          std::shared_ptr ret;

          object_map_type::iterator i = modelObjects.find(id);
          if (i != modelObjects.end())
            ret = i->second;

	  return ret;
	}

      const object_map_type&
      OMEModelImpl::getModelObjects () const
      {
	return modelObjects;
      }

      bool
      OMEModelImpl::addReference (OMEModelObject::shared_ptr& a,
				  Reference::shared_ptr& b)
      {
	std::vector<Reference::shared_ptr> bList = references.get(a);
	if (bList == null) {
	  bList = new ArrayList<Reference>();
	  references.put(a, bList);
	}
	return bList.add(b);
      }

      const reference_map_type&
      OMEModelImpl::getReferences () const
      {
	return references;
      }

      // TODO: Rewrite java code.
      int
      OMEModelImpl::resolveReferences ()
      {
	int unhandledReferences = 0;
	for (Entry<OMEModelObject, List<Reference>> entry : references.entrySet())
	  {
	    OMEModelObject a = entry.getKey();
	    if (a == null) {
	      List<Reference> references = entry.getValue();
	      if (references == null) {
		LOGGER.error("Null reference to null object, continuing.");
		continue;
	      }
	      LOGGER.error("Null reference to {} objects, continuing.",
			   references.size());
	      unhandledReferences += references.size();
	      continue;
	    }
	    for (Reference reference : entry.getValue()) {
	      const std::string& referenceID = reference.getID();
	      OMEModelObject b = getModelObject(referenceID);
	      if (b == null) {
		  LOGGER.warn("{} reference to {} missing from object hierarchy.",
			      a, referenceID);
		  unhandledReferences++;
		  continue;
	      }
	      a.link(reference, b);
	    }
	  }
	return unhandledReferences;
      }

    }
  }
}
