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

#include <cassert>
#include <deque>
#include <fstream>
#include <iostream>
#include <set>

#include <boost/format.hpp>

#include <ome/common/filesystem.h>

#include <ome/common/xml/EntityResolver.h>
#include <ome/common/xml/String.h>

#include <ome/common/xml/dom/Document.h>
#include <ome/common/xml/dom/Element.h>
#include <ome/common/xml/dom/NodeList.h>

#include <xercesc/sax/InputSource.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

namespace ome
{
  namespace common
  {
    namespace xml
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
                  ret = getSource(String(resource->getSchemaLocation()));
                }
                break;
              case xercesc::XMLResourceIdentifier::ExternalEntity:
                {
                  ret = getSource(String(resource->getSystemId()));
                }
                break;
              default:
                break;
              }
          }

        return ret;
      }

      xercesc::InputSource *
      EntityResolver::getSource(const std::string& resource)
      {
        xercesc::InputSource *ret = 0;

        entity_map_type& cache(entities());
        entity_map_type::iterator i = cache.find(resource);

        entity_ref ref;
        if (i != cache.end())
          ref = entity_ref(i->second.lock());

        if (ref) // Value is both cached and valid
          {
            std::string& data(ref->data);

            if (data.empty())
              {
                const boost::filesystem::path& file(ref->path);

                if (boost::filesystem::exists(file))
                  {
                    std::ifstream in(file.string().c_str());
                    if (in)
                      {
                        std::ios::pos_type pos = in.tellg();
                        in.seekg(0, std::ios::end);
                        std::ios::pos_type len = in.tellg() - pos;
                        if (len)
                          data.reserve(static_cast<std::string::size_type>(len));
                        in.seekg(0, std::ios::beg);

                        data.assign(std::istreambuf_iterator<char>(in),
                                    std::istreambuf_iterator<char>());

                      }
                    else
                      {
                        boost::format fmt("Failed to load XML schema id ‘%1%’ from file ‘%2%’");
                        fmt % resource % file.string();
                        std::cerr << fmt.str() << '\n';
                      }
                  }
              }

            if (!data.empty())
              {
                ret = new xercesc::MemBufInputSource(reinterpret_cast<const XMLByte *>(data.c_str()),
                                                     static_cast<XMLSize_t>(data.size()),
                                                     String(ref->path.string()));
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

      EntityResolver::entity_ref
      EntityResolver::registerEntity(const std::string&             id,
                                     const boost::filesystem::path& file)
      {
        entity_map_type& cache(entities());
        entity_map_type::iterator i = cache.find(id);

        entity_ref ref;
        if (i != cache.end())
          ref = entity_ref(i->second.lock());

        entity_ref ret;

        if (i == cache.end() || !ref)
          {
            // Remove existing invalid entry.
            if (!ref)
              cache.erase(id);

            // Insert new entry.
            ret = ome::compat::make_shared<entity_data>(file);
            entity_cache cacheval(ret);
            cache.insert(std::make_pair(id, cacheval));
          }
        else
          {
            // Return existing entry.  But only if the canonical path
            // of the file is the same as the (already canonical)
            // cached file.
            ret = ref;

            if(canonical(file) != ret->path)
              {
                boost::format fmt("Mismatch registering entity id ‘%1%’: File ‘%2%’ does not match existing cached file ‘%3%’");
                fmt % id % ret->path % file;
                throw std::runtime_error(fmt.str());
              }
          }

        assert(ret);
        return ret;
      }

      void
      EntityResolver::unregisterEntity(const std::string& id)
      {
        entity_map_type& cache(entities());
        entity_map_type::iterator i = cache.find(id);

        if (i != cache.end())
          {
            // Remove id from cache if the weak_ptr is no longer valid.
            entity_ref ref(i->second.lock());
            if(!ref)
              cache.erase(id);
          }
      }

      EntityResolver::RegisterEntity::RegisterEntity(const std::string&             id,
                                                     const boost::filesystem::path& file):
        id(id),
        ref(EntityResolver::registerEntity(id, file))
      {
      }

      EntityResolver::RegisterEntity::~RegisterEntity()
      {
        ref.reset();
        EntityResolver::unregisterEntity(id);
      }

      EntityResolver::RegisterCatalog::RegisterCatalog(const boost::filesystem::path& catalog):
        refs()
      {
        std::set<boost::filesystem::path> visited;
        std::deque<boost::filesystem::path> pending;

        pending.push_back(ome::common::canonical(catalog));

        while(!pending.empty())
          {
            boost::filesystem::path current(pending.front());
            assert(!current.empty());
            pending.pop_front();

            if (visited.find(current) != visited.end())
              {
                boost::format fmt("XML catalog ‘%1%’ contains a recursive reference");
                fmt % current.string();
                std::cerr << fmt.str() << '\n';
                continue; // This has already been processed; break loop
              }

            boost::filesystem::path currentdir(current.parent_path());
            assert(!currentdir.empty());

            std::ifstream in(current.string().c_str());
            if (in)
              {
                dom::Document doc(dom::createDocument(in));
                dom::Element root(doc.getDocumentElement());
                dom::NodeList nodes(root.getChildNodes());
                for (dom::NodeList::iterator i = nodes.begin();
                     i != nodes.end();
                     ++i)
                  {
                    if (i->getNodeType() == xercesc::DOMNode::ELEMENT_NODE)
                      {
                        dom::Element e(i->get(), false);
                        if (e)
                          {
                            if (e.getTagName() == "uri")
                              {
                                if (e.hasAttribute("uri") && e.hasAttribute("name"))
                                  {
                                    boost::filesystem::path newid(currentdir / static_cast<std::string>(e.getAttribute("uri")));
                                    refs.push_back(RegisterEntity(static_cast<std::string>(e.getAttribute("name")),
                                                                  ome::common::canonical(newid)));
                                  }
                              }
                            if (e.getTagName() == "nextCatalog")
                              {
                                if (e.hasAttribute("catalog"))
                                  {
                                    boost::filesystem::path newcatalog(currentdir / static_cast<std::string>(e.getAttribute("catalog")));
                                    pending.push_back(ome::common::canonical(newcatalog));
                                  }
                              }
                          }
                      }
                  }
              }
#ifndef NDEBUG
            // Don't make failure hard in release builds; just skip.
            else
              {
                boost::format fmt("Failed to load XML catalog from file ‘%1%’");
                fmt % catalog.string();
                throw std::runtime_error(fmt.str());
              }
#endif

            // Mark this file visited.
            visited.insert(current);
          }
      }

      EntityResolver::RegisterCatalog::~RegisterCatalog()
      {
      }

    }
  }
}
