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

#include <fstream>

#include <ome/xerces/EntityResolver.h>
#include <ome/xerces/String.h>

#include <xercesc/sax/InputSource.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

namespace ome
{
  namespace xerces
  {

    EntityResolver::EntityResolver():
      xercesc::XMLEntityResolver()
    {
    }

    EntityResolver::~EntityResolver()
    {
    }

    xercesc::InputSource *
    EntityResolver::resolveEntity(xercesc::XMLResourceIdentifier* resource)
    {
      xercesc::InputSource *ret = 0;

      if (resource)
        {
          switch(resource->getResourceIdentifierType())
            {
            case xercesc::XMLResourceIdentifier::SchemaGrammar:
            case xercesc::XMLResourceIdentifier::SchemaImport:
              {
                entity_map_type& cache(entities());
                entity_map_type::const_iterator i = cache.find(String(resource->getSchemaLocation()));
                const std::string& data(i->second);
                if (i != cache.end())
                  {
                    ret = new xercesc::MemBufInputSource(reinterpret_cast<const XMLByte *>(data.c_str()),
                                                         static_cast<XMLSize_t>(data.size()),
                                                         resource->getSchemaLocation());
                  }
              }
              break;
            case xercesc::XMLResourceIdentifier::ExternalEntity:
              {
                entity_map_type& cache(entities());
                entity_map_type::const_iterator i = cache.find(String(resource->getSystemId()));
                const std::string& data(i->second);
                if (i != cache.end())
                  {
                    ret = new xercesc::MemBufInputSource(reinterpret_cast<const XMLByte *>(data.c_str()),
                                                         static_cast<XMLSize_t>(data.size()),
                                                         resource->getSystemId());
                  }
              }
              break;
            default:
              break;
            }
        }

      return ret;
    }

    EntityResolver::entity_map_type&
    EntityResolver::entities()
    {
      static entity_map_type entity_map;
      return entity_map;
    }

    EntityResolver::AutoRegisterEntity::AutoRegisterEntity(const std::string& id,
                                                           const std::string& data):
      id(id)
    {
      EntityResolver::entities().insert(std::make_pair(id, data));
    }

    EntityResolver::AutoRegisterEntity::AutoRegisterEntity(const std::string&             id,
                                                           const boost::filesystem::path& file):
      id(id)
    {
      std::string data;

      std::ifstream in(file.generic_string().c_str());
      in.seekg(0, std::ios::end);
      data.reserve(in.tellg());
      in.seekg(0, std::ios::beg);

      data.assign(std::istreambuf_iterator<char>(in),
                  std::istreambuf_iterator<char>());

      EntityResolver::entities().insert(std::make_pair(id, data));
    }

    EntityResolver::AutoRegisterEntity::~AutoRegisterEntity()
    {
      EntityResolver::entities().erase(id);
    }

    EntityResolver::RegisterEntity::RegisterEntity(const std::string& id,
                                                   const std::string& data):
      registration(new AutoRegisterEntity(id, data))
    {
      EntityResolver::entities().insert(std::make_pair(id, data));
    }

    EntityResolver::RegisterEntity::RegisterEntity(const std::string&             id,
                                                   const boost::filesystem::path& file):
      registration(new AutoRegisterEntity(id, file))
    {
    }

    EntityResolver::RegisterEntity::~RegisterEntity()
    {
    }

  }
}
