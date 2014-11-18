/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#include <string>

#include <ome/bioformats/FormatReader.h>
#include <ome/bioformats/Types.h>

#include <ome/xml/meta/MetadataStore.h>

#ifndef OME_BIOFORMATS_METADATATOOLS_H
#define OME_BIOFORMATS_METADATATOOLS_H

namespace ome
{
  namespace bioformats
  {

    // Use overloaded functions for creating identifiers since
    // pre-C++11 compilers don't support the C99 stdarg interface.

    /**
     * Create an object identifier for a given object type and index.
     *
     * @param type the object type.
     * @param idx the object index.
     * @returns the identifier.
     */
    std::string
    createID(std::string const&  type,
             dimension_size_type idx);

    /**
     * Create an object identifier for a given object type and indices.
     *
     * @param type the object type.
     * @param idx1 the first object index.
     * @param idx2 the second object index.
     * @returns the identifier.
     */
    std::string
    createID(std::string const&  type,
             dimension_size_type idx1,
             dimension_size_type idx2);

    /**
     * Create an object identifier for a given object type and indices.
     *
     * @param type the object type.
     * @param idx1 the first object index.
     * @param idx2 the second object index.
     * @param idx3 the third object index.
     * @returns the identifier.
     */
    std::string
    createID(std::string const&  type,
             dimension_size_type idx1,
             dimension_size_type idx2,
             dimension_size_type idx3);

    /**
     * Create an object identifier for a given object type and indices.
     *
     * @param type the object type.
     * @param idx1 the first object index.
     * @param idx2 the second object index.
     * @param idx3 the third object index.
     * @param idx4 the fourth object index.
     * @returns the identifier.
     */
    std::string
    createID(std::string const&  type,
             dimension_size_type idx1,
             dimension_size_type idx2,
             dimension_size_type idx3,
             dimension_size_type idx4);

    void
    fillMetadata(std::shared_ptr< ::ome::xml::meta::MetadataStore>& store,
                 const FormatReader& reader);

  }
}

#endif // OME_BIOFORMATS_METADATATOOLS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
