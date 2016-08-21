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

#include <map>
#include <string>

#include <ome/compat/memory.h>

#include <ome/xml/model/OMEModelImpl.h>
#include <ome/xml/model/Reference.h>

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

      std::shared_ptr<OMEModelObject>
      OMEModelImpl::addModelObject(const std::string&               id,
                                   std::shared_ptr<OMEModelObject>& object)
      {
        // Don't store references.
        if (std::dynamic_pointer_cast<Reference>(object))
          return object;

        object_map_type::iterator i = modelObjects.find(id);
        if (i != modelObjects.end())
          i->second = object;
        else
          modelObjects.insert(std::make_pair(id, object));

        return object;
      }

      std::shared_ptr<OMEModelObject>
      OMEModelImpl::removeModelObject(const std::string& id)
      {
        std::shared_ptr<OMEModelObject> ret;

        object_map_type::iterator i = modelObjects.find(id);
        if (i != modelObjects.end())
          {
            ret = i->second;
            modelObjects.erase(i);
          }

        return ret;
      }

      std::shared_ptr<OMEModelObject>
      OMEModelImpl::getModelObject(const std::string& id) const
        {
          std::shared_ptr<OMEModelObject> ret;

          object_map_type::const_iterator i = modelObjects.find(id);
          if (i != modelObjects.end())
            ret = i->second;

          return ret;
        }

      const OMEModel::object_map_type&
      OMEModelImpl::getModelObjects () const
      {
        return modelObjects;
      }

      bool
      OMEModelImpl::addReference (std::shared_ptr<OMEModelObject>& a,
                                  std::shared_ptr<Reference>&      b)
      {
        reference_map_type::iterator i = references.find(a);

        if (i != references.end())
          {
            std::pair<reference_map_type::iterator,bool> r =
              references.insert(std::make_pair(a, reference_map_type::value_type::second_type()));
            i = r.first;
          }
        i->second.push_back(b);
        return true;
      }

      const OMEModel::reference_map_type&
      OMEModelImpl::getReferences () const
      {
        return references;
      }

      OMEModel::size_type
      OMEModelImpl::resolveReferences ()
      {
        int unhandledReferences = 0;

        for (reference_map_type::iterator i = references.begin();
             i != references.end();
             ++i)
          {
            const std::shared_ptr<const OMEModelObject>& a(i->first);

            if (!a)
              {
                const reference_list_type& references(i->second);

                if (references.empty())
                  std::clog << "No references to null object, continuing." << std::endl;
                else
                  std::clog << "Null reference to " << references.size()
                            << " objects, continuing." << std::endl;
                unhandledReferences += references.size();
              }
            else
              {
                reference_list_type& references(i->second);

                for (reference_list_type::iterator ref = references.begin();
                     ref != references.end();
                     ++ref)
                  {
                    if (!(*ref))
                      std::clog << typeid(*a).name() << "@" << a
                                << " reference to null object, continuing." << std::endl;
                    else
                      {
                        const std::string& referenceID = (*ref)->getID();

                        std::shared_ptr<OMEModelObject> b = getModelObject(referenceID);
                        if (!b)
                          {
                            std::clog << typeid(*a).name() << "@" << a
                                      << " reference to " << referenceID
                                      << " missing from object hierarchy." << std::endl;
                            unhandledReferences++;
                          }
                        else
                          {
                            std::shared_ptr<OMEModelObject> aw(std::const_pointer_cast<OMEModelObject>(a));
                            aw->link(*ref, b);
                          }
                      }
                  }
              }
          }
        return unhandledReferences;
      }

    }
  }
}
