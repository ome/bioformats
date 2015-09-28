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
#include <utility>

#include <boost/format.hpp>

#include <ome/common/filesystem.h>

#include <ome/common/xml/EntityResolver.h>
#include <ome/common/xml/String.h>

#include <ome/common/xml/Platform.h>
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
        xercesc::XMLEntityResolver(),
        logger(ome::common::createLogger("EntityResolver")),
        entity_path_map(),
        entity_data_map()
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

        entity_path_map_type::const_iterator i = entity_path_map.find(resource);

        if (i != entity_path_map.end())
          {
            entity_data_map_type::iterator d = entity_data_map.find(resource);

            if (d == entity_data_map.end()) // No cached data
              {
                const boost::filesystem::path& file(i->second);

                if (boost::filesystem::exists(file))
                  {
                    std::ifstream in(file.string().c_str());
                    if (in)
                      {
                        std::string data;
                        std::ios::pos_type pos = in.tellg();
                        in.seekg(0, std::ios::end);
                        std::ios::pos_type len = in.tellg() - pos;
                        if (len)
                          data.reserve(static_cast<std::string::size_type>(len));
                        in.seekg(0, std::ios::beg);

                        data.assign(std::istreambuf_iterator<char>(in),
                                    std::istreambuf_iterator<char>());

                        BOOST_LOG_SEV(logger, ome::logging::trivial::debug)
                          << "Registering resource data " << resource
                          << " (" << i->second << ")\n" << data;

                        std::pair<entity_data_map_type::iterator,bool> valid =
                          entity_data_map.insert(std::make_pair(resource, data));
                        if (valid.second)
                          d = valid.first;
                      }
                    else
                      {
                        boost::format fmt("Failed to load XML schema id ‘%1%’ from file ‘%2%’");
                        fmt % resource % file.string();
                        std::cerr << fmt.str() << '\n';
                      }
                  }
              }

            if (d != entity_data_map.end()) // Cached data
              {
                const std::string&data(d->second);

                BOOST_LOG_SEV(logger, ome::logging::trivial::trace)
                  << "Returning resource " << resource
                  << " (" << i->second << ")\n" << data;

                ret = new xercesc::MemBufInputSource(reinterpret_cast<const XMLByte *>(data.c_str()),
                                                     static_cast<XMLSize_t>(data.size()),
                                                     String(i->second.string()));
              }
          }

        return ret;
      }

      void
      EntityResolver::registerEntity(const std::string&             id,
                                     const boost::filesystem::path& file)
      {
        entity_path_map_type::iterator i = entity_path_map.find(id);

        if (i == entity_path_map.end())
          {
            // Insert new entry.
            entity_path_map.insert(std::make_pair(id, canonical(file)));
          }
        else
          {
            if(canonical(file) != i->second)
              {
                boost::format fmt("Mismatch registering entity id ‘%1%’: File ‘%2%’ does not match existing cached file ‘%3%’");
                fmt % id % i->second % file;
                std::cerr << fmt.str() << std::endl;
                throw std::runtime_error(fmt.str());
              }
          }
      }

      void
      EntityResolver::registerCatalog(const boost::filesystem::path& catalog)
      {
        std::set<boost::filesystem::path> visited;
        std::deque<boost::filesystem::path> pending;

	ome::common::xml::Platform platform;

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
                EntityResolver r; // Does nothing.
                dom::Document doc(dom::createDocument(in, r));
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

                                    BOOST_LOG_SEV(logger, ome::logging::trivial::debug)

                                      << "Registering " << static_cast<std::string>(e.getAttribute("name"))
                                      << " as " << ome::common::canonical(newid);
                                    registerEntity(static_cast<std::string>(e.getAttribute("name")),
                                                   ome::common::canonical(newid));
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

    }
  }
}
