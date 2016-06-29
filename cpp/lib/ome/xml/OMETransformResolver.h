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

#ifndef OME_XML_MODEL_OMETRANSFORMRESOLVER_H
#define OME_XML_MODEL_OMETRANSFORMRESOLVER_H

#include <ostream>
#include <set>
#include <string>
#include <utility>

#include <boost/filesystem.hpp>

#include <ome/compat/cstdint.h>
#include <ome/compat/memory.h>

namespace ome
{
  namespace xml
  {

    class OMETransformResolverImpl;

    /**
     * Discover and query available OME-XML transforms.
     *
     * This class will find the available XSL transforms to convert
     * between different OME-XML model versions, using either the
     * installed schemas, or schemas in a specified directory.
     *
     * In order to convert between different schema versions, one or
     * more transforms will require applying.  This class will
     * determine the optimal set of transforms to apply for a given
     * source and target model version, computed using a graph
     * representation and shortest path algorithm.
     */
    class OMETransformResolver
    {
    public:
      /**
       * Metadata for a schema version.
       *
       * Internally, this is attached to each graph vertex.
       */
      struct Schema
      {
        /// Default constructor.
        Schema():
          name()
        {}

        /**
         * Construct with name.
         *
         * @param name the name of the schema.
         */
        Schema(const std::string& name):
          name(name)
        {}

        /**
         * Compare with other Schema.
         *
         * @param rhs the Schema to compare with.
         * @returns @c true if this schema name is lexicographically
         * less, @c false otherwise.
         */
        bool
        operator< (const Schema& rhs) const
        {
          return name < rhs.name;
        }

        /// Name of the schema.
        std::string name;
      };

      /**
       * Transformation quality.
       *
       * Schema transforms may vary in quality.  Upgrades are
       * typically lossless and classed as "excellent", while
       * downgrades may lose information due to the older schemas
       * lacking the ability to represent metadata added in later
       * schema versions.  These quality levels are arbitrary
       * classifications and weightings used to match the existing
       * manually maintained transform lists used in the Java
       * implementation.
       */
      enum Quality
      {
        POOR      = 1000000LU, ///< Extreme data loss.
        FAIR      = 10000LU,   ///< Moderate data loss.
        GOOD      = 100LU,     ///< Slight data loss.
        EXCELLENT = 1LU        ///< No data loss.
      };

      /**
       * Get the name associated with a Quality enumeration.
       *
       * @param quality the Quality enumeration.
       * @returns a string representation of the Quality
       * enumeration.
       */
      static std::string
      quality_name(OMETransformResolver::Quality quality);

      /**
       * Metadata for a schema transform between two schema versions.
       *
       * Internally, this is attached to each graph edge, with the
       * transformation quality used for determining the optimal set
       * of transformations to apply.
       */
      struct Transform
      {
        /// Default constructor.
        Transform();

        /**
         * Construct transform from path.
         *
         * The quality of the transform will be determined
         * automatically from the filename.
         *
         * @param file the XSL transform.
         */
        Transform(const boost::filesystem::path& file);

        /**
         * Construct transform from path with specified quality.
         *
         * @param file the XSL transform.
         * @param quality the quality of the XSL transform.
         */
        Transform(const boost::filesystem::path& file,
                  Quality                        quality);

        /**
         * Compare with other Transform.
         *
         * @param rhs the Transform to compare with.
         * @returns @c true if the transform source and target
         * versions are lexicographically less, @c false otherwise.
         */
        bool
        operator< (const Transform& rhs) const
        {
          return schemas < rhs.schemas;
        }

        /// The XSL transform.
        boost::filesystem::path file;
        /// The source and target schema versions.
        std::pair<std::string, std::string> schemas;
        /// The quality metric for the transform (less is better).
        uint32_t weight;
      };

    public:
      /**
       * Default constructor.
       *
       * The transform directory used will be the default set at
       * install time or in the environment.
       */
      OMETransformResolver();

      /**
       * Construct with specified transform directory.
       *
       * @param transformdir the directory containing the XSL
       * transform files.
       */
      OMETransformResolver(const boost::filesystem::path& transformdir);

      /// Destructor.
      ~OMETransformResolver();

      /**
       * Get the available schema versions for transformation.
       *
       * @returns all schema versions found as a source or target for
       * a transform.
       */
      std::set<std::string>
      schema_versions() const;

      /**
       * Determine the optimal transform order between schema
       * versions.
       *
       * Run a shortest path search to determine the highest quality
       * set of transforms between the two versions.
       *
       * The returned quality metric is the sum of the quality metrics
       * for all of the transforms, rounded down to the nearest
       * quality value.
       *
       * @param source the source schema version.
       * @param target the target schema version.
       * @returns a vector of Transform objects in the order to apply,
       * and an aggregate quality metric.
       */
      std::pair<std::vector<Transform>, Quality>
      transform_order(const std::string& source,
                      const std::string& target) const;

      /**
       * Write a GraphViz dot representation of the internal graph state.
       *
       * Primarily intended for debugging and documentation.
       *
       * @param os the stream to write the dot representation to.
       */
      void
      write_graphviz(std::ostream& os);

    private:
      /// Private implementation details.
      ome::compat::shared_ptr<OMETransformResolverImpl> impl;
    };

    /**
     * Output OMETransformResolver::Quality to output stream.
     *
     * @param os the output stream.
     * @param quality the OMETransformResolver::Quality to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
      inline std::basic_ostream<charT,traits>&
      operator<< (std::basic_ostream<charT,traits>& os,
                  const OMETransformResolver::Quality& quality)
      {
        return os << OMETransformResolver::quality_name(quality);
      }

  }
}

#endif // OME_XML_MODEL_OMETRANSFORMRESOLVER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
