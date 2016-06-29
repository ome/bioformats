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

#ifndef OME_XML_MODEL_OMEMODEL_H
#define OME_XML_MODEL_OMEMODEL_H

#include <map>
#include <string>
#include <vector>

#include <ome/compat/memory.h>

#include <ome/xml/model/OMEModelObject.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {

      class Reference;

      /**
       * OME model interface (abstract top-level container)
       */
      class OMEModel
      {
      public:
        /// A list of Reference objects.
        typedef std::vector<ome::compat::shared_ptr<Reference> > reference_list_type;
        /// A map of string model object identifiers to model objects.
        typedef std::map<std::string, ome::compat::shared_ptr<OMEModelObject> > object_map_type;
        /// A map of model objects to list of Reference objects.
        typedef std::map<ome::compat::shared_ptr<OMEModelObject>, reference_list_type> reference_map_type;
        /// Size type for reference map.
        typedef reference_map_type::size_type size_type;

      protected:
        /// Constructor.
        OMEModel ()
        {}

      public:
        /// Destructor.
        virtual
        ~OMEModel ()
        {}

      private:
        /// Copy constructor (deleted).
        OMEModel (const OMEModel&);

        /// Assignment operator (deleted).
        OMEModel&
        operator= (const OMEModel&);

      public:
        /**
         * Add a model object to the model.  Note that the concrete
         * implementation will not add types derived from Reference.
         *
         * @param id the model object identifier.
         * @param object the model object to add.
         * @returns the model object.
         *
         * @todo What is the point of returning the object?  The
         * object is returned under all conditions, even when adding
         * the object failed (e.g. if the object was a Reference).
         * Should it be possible to insert null objects?
         */
        virtual
        ome::compat::shared_ptr<OMEModelObject>
        addModelObject (const std::string&                       id,
                        ome::compat::shared_ptr<OMEModelObject>& object) = 0;

        /**
         * Remove a model object from the model.
         *
         * @param id the model object identifier of the model object
         * to remove.
         * @returns the model object or null if the identifier does
         * not exist.
         */
        virtual
        ome::compat::shared_ptr<OMEModelObject>
        removeModelObject (const std::string& id) = 0;

        /**
         * Retrieve a model object from the model.
         *
         * @param id the model object identifier of the model object
         * to retrieve.
         * @returns the model object or null if the identifier does
         * not exist.
         *
         * @todo: Would a const reference be better for the return?
         */
        virtual
        ome::compat::shared_ptr<OMEModelObject>
        getModelObject (const std::string& id) const = 0;

        /**
         * Retrieve all model objects from the model.
         *
         * @returns a reference to the object model map.
         */
        virtual
        const object_map_type&
        getModelObjects () const = 0;

        /**
         * Add a reference to a model object.
         *
         * @param a the model object to reference.
         * @param b the reference to add.
         * @returns true
         *
         * @todo There is no validation of whether the model object
         * was valid for this model (as for the java implementation).
         * The return type is an artifact of the java map
         * implementation.  Consider removing the return type and
         * throwing if the model object is not already present in the
         * object map; or adding the object to the object map
         * implicitly and not throwing.
         */
        virtual
        bool
        addReference (ome::compat::shared_ptr<OMEModelObject>& a,
                      ome::compat::shared_ptr<Reference>&      b) = 0;

        /**
         * Retrieve all references from the model.
         *
         * @returns a reference to the reference map.
         */
        virtual
        const reference_map_type&
        getReferences () const = 0;

        /**
         * Resolve all references.  Check for invalid references and
         * null model objects.
         *
         * @returns the number of unhandled references.
         *
         * @todo This method is a bit of a wart in the design.  The
         * model should not allow invalid references or insertion of
         * null model objects to begin with.
         */
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
