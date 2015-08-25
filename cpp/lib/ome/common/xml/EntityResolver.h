/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
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

#ifndef OME_COMMON_XML_ENTITYRESOLVER_H
#define OME_COMMON_XML_ENTITYRESOLVER_H

#include <map>
#include <string>

#include <ome/common/filesystem.h>
#include <ome/common/log.h>

#include <ome/compat/memory.h>

#include <xercesc/util/XMLEntityResolver.hpp>

namespace ome
{
  namespace common
  {
    namespace xml
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

	/**
	 * Register a file with the entity resolver.
	 *
	 * @param id the XML system ID of the entity.
	 * @param file the filename of the entity.
	 */
        void
	registerEntity(const std::string&             id,
		       const boost::filesystem::path& file);

        /**
	 * Register a catalog with the entity resolver.
	 *
	 * @param file the filename of the catalog.
	 */
        void
	registerCatalog(const boost::filesystem::path& file);

      private:
        /// Mapping from system ID to filesystem path.
        typedef std::map<std::string, boost::filesystem::path> entity_path_map_type;
        /// Mapping from system ID to XML data.
        typedef std::map<std::string, std::string> entity_data_map_type;

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

        private:
          /// Message logger.
          ome::common::Logger logger;
          /// Map of registered system IDs to filesystem paths.
          entity_path_map_type entity_path_map;
          /// Map of system IDs to cached XML data.
          entity_data_map_type entity_data_map;
      };

    }
  }
}

#endif // OME_COMMON_XML_ENTITYRESOLVER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

