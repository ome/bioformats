/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#ifndef OME_BIOFORMATS_MODULO_H
#define OME_BIOFORMATS_MODULO_H

#include <string>
#include <vector>
#include <sstream>

#include <ome/compat/cstdint.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * A subdimension of Z, C, or T.  Needed to support Modulo
     * annotations.  See
     * http://www.openmicroscopy.org/site/support/ome-model/developers/6d-7d-and-8d-storage.html
     */
    class Modulo {
    public:
      /// Size of the subdimension.
      typedef uint32_t size_type;

      /// Parent dimension being subdivided.
      std::string parentDimension;
      /// Start value.
      double start;
      /// Step size.
      double step;
      /// End value.
      double end;
      /// Type of the parent dimension.
      std::string parentType;
      /// Type of the subdimension.
      std::string type;
      /// Type description of the subdimension.
      std::string typeDescription;
      /// Unit of the subdimension.
      std::string unit;
      /// Labels along the subdimension.
      std::vector<std::string> labels;

      /**
       * Constructor.
       *
       * @param dimension the parent dimension.
       */
      Modulo(std::string dimension);

      /**
       * Get the size of this subdimension.
       *
       * This is the number of discrete samples along this dimension,
       * between the start and end points, separated by step size
       * increments.
       *
       * @returns the subdimension size.
       */
      size_type
      size() const;

      /**
         * Convert to XML string.
         *
         * The object is serialised to an XML text representation.
         * This is suitable for embedding in an
         * ome::xml::model::XMLAnnotation, for example.
         *
         * @returns a string containing the XML.
       * @copydoc ome::OMEXMLMetadata::dumpXML()
       */
      std::string
      toXMLAnnotation() const;
    };

  }
}

#endif // OME_BIOFORMATS_MODULO_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
