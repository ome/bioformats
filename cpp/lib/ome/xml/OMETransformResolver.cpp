/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2016 Open Microscopy Environment:
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

#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/dijkstra_shortest_paths.hpp>
#include <boost/graph/labeled_graph.hpp>
#include <boost/graph/graphviz.hpp>
#include <boost/tuple/tuple.hpp> // For tie

#include <ome/common/module.h>
#include <ome/xml/module.h>

#include <ome/xml/OMETransformResolver.h>

namespace
{

  using ome::xml::OMETransformResolver;

  /*
   * Get schema source and target version from filename.
   *
   * Requires the file to be named "${source}-to-${target}.xsl".
   *
   *
   * @returns a pair containing the source and target version.
   * @throws std::runtime_error if the filename is not valid.
   */
  std::pair<std::string, std::string>
  source_and_target_version(const boost::filesystem::path& file)
  {
    std::string transformname = file.filename().stem().string();

    if (!file.empty())
      {
        const std::string sep("-to-");
        std::string::size_type pos = transformname.find(sep);
        if (pos == std::string::npos)
          throw std::runtime_error("Not a valid XSL transform name");

        return std::make_pair(transformname.substr(0, pos),
                              transformname.substr(pos + sep.size()));
      }
    else
      {
        return std::pair<std::string, std::string>();
      }
  }

  /*
   * Get the weight value associated with a Quality enumeration.
   */
  uint32_t
  quality_weight(OMETransformResolver::Quality quality)
  {
    return static_cast<uint32_t>(quality);
  }

  /*
   * Get the Quality enumeration for a given quality metric.
   *
   * Returns the enumeration with the nearest lower value to the
   * metric.
   *
   * @returns the Quality enumeration.
   */
  OMETransformResolver::Quality
  weight_quality(uint32_t weight)
  {
    OMETransformResolver::Quality quality;

    if (weight < quality_weight(OMETransformResolver::GOOD))
      quality = OMETransformResolver::EXCELLENT;
    else if (weight < quality_weight(OMETransformResolver::FAIR))
      quality = OMETransformResolver::GOOD;
    else if (weight < quality_weight(OMETransformResolver::POOR))
      quality = OMETransformResolver::FAIR;
    else
      quality = OMETransformResolver::POOR;

    return quality;
  }

  /*
   * Get the Quality of a given transform.
   *
   * This matches the historic behaviour of the Python script to
   * generate ome-transforms.xsl, and the previous hand-created
   * versions of the same file.
   *
   * - upgrades are always of excellent quality
   * - downgrades are of good quality
   * - if the target is before 2008 then the quality is fair
   * - if the target is before 2010-06 then the quality is poor
   *
   * @param schemas the source and target schema versions to use.
   * @returns the Quality enumeration for these versions.
   */
  static OMETransformResolver::Quality
  determine_quality(const std::pair<std::string, std::string>& schemas)
  {
    const std::string& source = schemas.first;
    const std::string& target = schemas.second;

    OMETransformResolver::Quality quality = OMETransformResolver::GOOD;

    if (source < target)
      quality = OMETransformResolver::EXCELLENT;
    else if (target < "2008")
      quality = OMETransformResolver::POOR;
    else if (target < "2010-06")
      quality = OMETransformResolver::FAIR;

    return quality;
  }

}

namespace ome
{
  namespace xml
  {

    std::string
    OMETransformResolver::quality_name(OMETransformResolver::Quality quality)
    {
      std::string name;

      switch(quality)
        {
        case OMETransformResolver::POOR:
          name = "poor";
          break;
        case OMETransformResolver::FAIR:
          name = "fair";
          break;
        case OMETransformResolver::GOOD:
          name = "good";
          break;
        case OMETransformResolver::EXCELLENT:
          name = "excellent";
          break;
        default:
          break;
        };

      return name;
    }

    OMETransformResolver::Transform::Transform():
      file(boost::filesystem::path()),
      schemas(source_and_target_version(boost::filesystem::path())),
      weight(quality_weight(EXCELLENT))
    {
    }

    OMETransformResolver::Transform::Transform(const boost::filesystem::path& file):
      file(file),
      schemas(source_and_target_version(file)),
      weight(quality_weight(determine_quality(source_and_target_version(file))))
    {
    }

    OMETransformResolver::Transform::Transform(const boost::filesystem::path& file,
                                               Quality                        quality):
      file(file),
      schemas(source_and_target_version(file)),
      weight(quality_weight(quality))
    {
    }

    /**
     * Private implementation details of OMETransformResolver.
     *
     * Contains a graph of the schemas and transforms.  This is
     * private to keep Boost.Graph out of the public interface.
     */
    class OMETransformResolverImpl
    {
    public:
      /// Graph type; using our Schema and Transform types as bundled
      /// properties, with labelled vertices for ease of access.
      typedef boost::labeled_graph<boost::adjacency_list<boost::vecS, boost::vecS, boost::directedS, OMETransformResolver::Schema, OMETransformResolver::Transform>, std::string> Graph;
      /// Identifier type for vertices.
      typedef boost::graph_traits<Graph>::vertex_descriptor vertex_descriptor;
      /// Identifier type for edges.
      typedef boost::graph_traits<Graph>::edge_descriptor edge_descriptor;
      /// Iterator type for vertices.
      typedef boost::graph_traits<Graph>::vertex_iterator vertex_iter;

      /// Graph storing schemas as vertices and transforms as weighted edges.
      Graph g;

      /**
       * Fill the graph with transforms.
       *
       * @param transformdir the directory containing the XSL
       * transforms.
       */
      void
      fill_graph(const boost::filesystem::path& transformdir)
      {
        if (!boost::filesystem::is_directory(transformdir))
          {
            throw std::runtime_error("Transform directory does not exist");
          }

        std::set<OMETransformResolver::Schema> schemas;
        std::set<OMETransformResolver::Transform> transforms;

        boost::filesystem::directory_iterator dirent_end;
        for (boost::filesystem::directory_iterator dirent(transformdir); dirent != dirent_end; ++dirent)
          {
            if (boost::filesystem::extension(*dirent) != ".xsl")
              continue; // not a transform

            try
              {
                OMETransformResolver::Transform transform(dirent->path());

                schemas.insert(OMETransformResolver::Schema(transform.schemas.first));
                schemas.insert(OMETransformResolver::Schema(transform.schemas.second));
                transforms.insert(transform);
              }
            catch(const std::exception& /* e */)
              {
                // Not a valid transform name.
              }
          }

        // Process backward so newer schemas are at the start of the list.
        for (std::set<OMETransformResolver::Schema>::const_reverse_iterator schema = schemas.rbegin();
             schema != schemas.rend();
             ++schema)
          boost::add_vertex(schema->name, *schema, g);

        // Process backward so newer transforms are at the start of the list.
        for (std::set<OMETransformResolver::Transform>::const_reverse_iterator transform = transforms.rbegin();
             transform != transforms.rend();
             ++transform)
          {
            boost::add_edge_by_label(transform->schemas.first, transform->schemas.second,
                                     *transform, g);
          }
      }
    };

    OMETransformResolver::OMETransformResolver():
      impl(ome::compat::make_shared<OMETransformResolverImpl>())
    {
      // Hack to force module registration when static linking.
      register_module_paths();

      impl->fill_graph(ome::common::module_runtime_path("ome-xml-transform"));
    }

    OMETransformResolver::OMETransformResolver(const boost::filesystem::path& transformdir):
      impl(ome::compat::make_shared<OMETransformResolverImpl>())
    {
      impl->fill_graph(transformdir);
    }

    OMETransformResolver::~OMETransformResolver()
    {
    }

    std::set<std::string>
    OMETransformResolver::schema_versions() const
    {
      std::set<std::string> ret;

      const OMETransformResolverImpl::Graph& g = impl->g;

      OMETransformResolverImpl::vertex_iter i, end;
      for (boost::tie(i, end) = boost::vertices(g); i != end; ++i)
        ret.insert(g.graph()[*i].name);

      return ret;
    }

    std::pair<std::vector<OMETransformResolver::Transform>, OMETransformResolver::Quality>
    OMETransformResolver::transform_order(const std::string& source,
                                          const std::string& target) const
    {
      const OMETransformResolverImpl::Graph& g = impl->g;

      std::vector<Transform> order;
      uint32_t distance;

      OMETransformResolverImpl::vertex_descriptor vsource = g.vertex(source);
      OMETransformResolverImpl::vertex_descriptor vtarget  = g.vertex(target);
      if (vsource != boost::graph_traits<OMETransformResolverImpl::Graph>::null_vertex() &&
          vtarget != boost::graph_traits<OMETransformResolverImpl::Graph>::null_vertex())
        {
          std::vector<OMETransformResolverImpl::vertex_descriptor> predecessor(boost::num_vertices(g));
          std::vector<int64_t> distances(boost::num_vertices(g));

          boost::dijkstra_shortest_paths(g, vsource,
                                         boost::weight_map(boost::get(&Transform::weight, g))
                                         .distance_map(boost::make_iterator_property_map(distances.begin(), get(boost::vertex_index, g)))
                                         .predecessor_map(boost::make_iterator_property_map(predecessor.begin(), boost::get(boost::vertex_index, g))));

          distance = distances[vtarget];

          OMETransformResolverImpl::vertex_descriptor v = vtarget;
          while (v != predecessor[v])
            {
              std::pair<OMETransformResolverImpl::edge_descriptor, bool> found_edge = boost::edge(predecessor[v], v, g);
              if (!found_edge.second)
                {
                  throw std::logic_error("Missing transform in transform sequence between source and target");
                }
              const Transform& transform = g[found_edge.first];
              order.push_back(transform);
              v = predecessor[v];
            }
          if (v != vsource)
            {
              throw std::runtime_error("Impossible to transform between source and target schemas: missing transforms");
            }
        }
      else
        {
          throw std::runtime_error("Impossible to determine transform order: source or target missing");
        }

      std::reverse(order.begin(), order.end());
      return std::make_pair(order, weight_quality(distance));
    }

    namespace
    {

      // Write a GraphViz edge label and color.
      class edge_writer
      {
      public:
        // Construct from graph being written.
        edge_writer(const OMETransformResolverImpl::Graph& g):
          g(g)
        {}

        // Write edge to stream.
        template <class Edge>
        void
        operator() (std::ostream& os,
                    const Edge&   e) const
        {
          const ome::xml::OMETransformResolver::Transform& transform = g[e];
          ome::xml::OMETransformResolver::Quality quality = weight_quality(transform.weight);

          os << "[label=\"quality="
             << OMETransformResolver::quality_name(quality)
             << "\\nweight=" << transform.weight << '"';

          switch(quality)
            {
            case OMETransformResolver::GOOD:
              os << " color=blue";
              break;
            case OMETransformResolver::FAIR:
              os << " color=magenta";
              break;
            case OMETransformResolver::POOR:
              os << " color=red";
              break;
            default:
              os << " color=black";
              break;
            }

          os << ']';
        }

      private:
        const OMETransformResolverImpl::Graph& g;
      };

    }

    void
    OMETransformResolver::write_graphviz(std::ostream& os)
    {
      const OMETransformResolverImpl::Graph& g = impl->g;

      boost::write_graphviz(os, g,
                            boost::make_label_writer(boost::get(&Schema::name, g)),
                            edge_writer(g));
    }

  }
}
