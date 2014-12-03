/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
 * %%
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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

#ifndef OME_XERCES_ENTITYRESOLVER_H
#define OME_XERCES_ENTITYRESOLVER_H

#include <map>
#include <string>

#include <ome/compat/filesystem.h>
#include <ome/compat/memory.h>

#include <xercesc/util/XMLEntityResolver.hpp>

namespace ome
{
  namespace xerces
  {

    /**
     * Xerces entity resolver
     *
     * This resolver allows replacement of URLs with local files or
     * in-memory copies of XML schemas.  This permits efficient
     * validation without network access for commonly-used schemas.
     */
    class EntityResolver : public xercesc::XMLEntityResolver
    {
    public:
      /// Constructor.
      EntityResolver();

      /// Destructor.
      ~EntityResolver();

      /**
       * Resolve an entity.
       *
       * @param resource the resource to resolve.
       * @returns an input source containing the cached content, or
       * null if the resource was not cached.
       */
      xercesc::InputSource *
      resolveEntity(xercesc::XMLResourceIdentifier* resource);

    private:
      /// Cached entity (path and content).
      typedef std::pair<boost::filesystem::path, std::string> entity_cache;
      /// Entity mapping type.
      typedef std::map<std::string, entity_cache> entity_map_type;

      /**
       * Get input source from file.
       *
       * Open and read the contents of the file, then return this as
       * an InputSource.  Use cached content if possible.
       *
       * @param resource the resource to resolve.
       * @returns the input source for the file, or null on failure.
       */
      xercesc::InputSource *
      getSource(const std::string& resource);

      /**
       * Get entity mappings.
       *
       * @returns a reference to the current entity map.
       */
      static
      entity_map_type&
      entities();

      /**
       * Automatically register and unregister an entity with the entity resolver.
       */
      class AutoRegisterEntity
      {
      public:
        /**
         * Register in-memory text data with the entity resolver.
         *
         * @param id the XML system ID of the entity.
         * @param data the text data of the entity.
         */
        AutoRegisterEntity(const std::string& id,
                           const std::string& data);

        /**
         * Register a file with the entity resolver.
         *
         * @param id the XML system ID of the entity.
         * @param file the filename of the entity.
         */
        AutoRegisterEntity(const std::string&             id,
                           const boost::filesystem::path& file);

        /**
         * Destructor.
         *
         * The entity will be unregistered with the entity resolver.
         */
        ~AutoRegisterEntity();

      private:
        /// Copy constructor (deleted).
        AutoRegisterEntity (const AutoRegisterEntity&);

        /// Assignment operator (deleted).
        AutoRegisterEntity&
        operator= (const AutoRegisterEntity&);

        /// XML system ID.
        std::string id;
      };

      /**
       * Automatically register and unregister an XML catalog with the entity resolver.
       */
      class AutoRegisterCatalog
      {
      public:
        /**
         * Register a catalog file with the entity resolver.
         *
         * The catalog will be read, and any entities in it will be
         * registered.  This will be done recursively for all
         * referenced catalogs.
         *
         * @param catalog the filename of the catalog.
         */
        AutoRegisterCatalog(const boost::filesystem::path& catalog);

        /**
         * Destructor.
         *
         * The entity will be unregistered with the entity resolver.
         */
        ~AutoRegisterCatalog();

      private:
        /// Copy constructor (deleted).
        AutoRegisterCatalog (const AutoRegisterCatalog&);

        /// Assignment operator (deleted).
        AutoRegisterCatalog&
        operator= (const AutoRegisterCatalog&);

        /// Registered entities from this catalog.
        std::vector<std::shared_ptr<AutoRegisterEntity> > registration;
      };

    public:
      /**
       * Register an entity with the entity resolver.
       */
      class RegisterEntity
      {
      public:
        /**
         * Register in-memory text data with the entity resolver.
         *
         * @param id the XML system ID of the entity.
         * @param data the text data of the entity.
         */
        RegisterEntity(const std::string& id,
                       const std::string& data);

        /**
         * Register a file with the entity resolver.
         *
         * @param id the XML system ID of the entity.
         * @param file the filename of the entity.
         */
        RegisterEntity(const std::string&             id,
                       const boost::filesystem::path& file);

        /// Destructor.
        ~RegisterEntity();

      private:
        /// Automatic registration.
        std::shared_ptr<AutoRegisterEntity> registration;        
      };

      /**
       * Register an XML catalog with the entity resolver.
       */
      class RegisterCatalog
      {
      public:
        /**
         * Register a catalog file with the entity resolver.
         *
         * The catalog will be read, and any entities in it will be
         * registered.  This will be done recursively for all
         * referenced catalogs.
         *
         * @param catalog the filename of the catalog.
         */
        RegisterCatalog(const boost::filesystem::path& catalog);

        /// Destructor.
        ~RegisterCatalog();

      private:
        /// Automatic registration.
        std::shared_ptr<AutoRegisterCatalog> registration;
      };

      friend class RegisterEntity;
      friend class RegisterCatalog;
    };

  }
}

#endif // OME_XERCES_ENTITYRESOLVER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

