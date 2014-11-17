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

#include <ome/compat/filesystem.h>

#include <ome/xml/meta/Metadata.h>
#include <ome/xml/meta/MetadataRoot.h>
#include <ome/xml/meta/OMEXMLMetadata.h>

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

    /**
     * Create OME-XML metadata from XML document.
     *
     * @param document the XML document source.
     * @returns the OME-XML metadata.
     */
    std::shared_ptr< ::ome::xml::meta::Metadata>
    createOMEXMLMetadata(const std::string& document);

    /**
     * Create OME-XML metadata from reader core metadata.
     *
     * @param reader the reader to use.
     * @param doPlane create Plane elements if @c true.
     * @param doImageName set image name if @c true.
     * @returns the OME-XML metadata.
     */
    std::shared_ptr< ::ome::xml::meta::Metadata>
    createOMEXMLMetadata(const FormatReader& reader,
                         bool                doPlane = false,
                         bool                doImageName = true);


    /**
     * Create OME-XML metadata root from XML document.
     *
     * @param document the XML document source.
     * @returns the OME-XML metadata root.
     */
    std::shared_ptr< ::ome::xml::meta::MetadataRoot>
    createOMEXMLRoot(const std::string& document);

    /**
     * Get OME-XML metadata from metadata.
     *
     * This will convert the metadata to OME-XML metadata if required.
     *
     * @param retrieve the metadata to use.
     * @returns the OME-XML metadata.
     */
    std::shared_ptr< ::ome::xml::meta::Metadata>
    getOMEXMLMetadata(std::shared_ptr<::ome::xml::meta::MetadataRetrieve>& retrieve);

    /**
     * Get OME-XML document from OME-XML metadata.
     *
     * This will convert the OME-XML metadata to an XML document
     * string.
     *
     * @param omexml the OME-XML metadata store.
     * @returns the OME-XML metadata as an XML document string.
     */
    std::string
    getOMEXML(::ome::xml::meta::OMEXMLMetadata& omexml);

    /**
     * Validate an OME-XML document.
     *
     * @param document the XML document source.
     * @returns @c true if valid, @c false if invalid.
     */
    bool
    validateOMEXML(const std::string& document);

    /**
     * Fill OME-XML metadata store from reader core metadata.
     *
     * The metadata store is expected to be empty.
     *
     * @param store the OME-XML metadata store.
     * @param reader the reader to use.
     * @param doPlane create Plane elements if @c true.
     * @param doImageName set image name if @c true.
     */
    void
    fillMetadata(::ome::xml::meta::MetadataStore& store,
                 const FormatReader&              reader,
                 bool                             doPlane = false,
                 bool                             doImageName = true);

    /**
     * Fill all OME-XML metadata store Pixels elements from reader core metadata.
     *
     * Set Pixels metadata for all series.
     *
     * @param store the OME-XML metadata store.
     * @param reader the reader to use.
     */
    void
    fillAllPixels(::ome::xml::meta::MetadataStore& store,
                  const FormatReader&              reader);

    /**
     * Fill an OME-XML metadata store Pixels element from reader core metadata.
     *
     * Set Pixels metadata for the reader's current series.
     *
     * @param store the OME-XML metadata store.
     * @param reader the reader to use.
     */
    void
    fillPixels(::ome::xml::meta::MetadataStore& store,
               const FormatReader&              reader);

    /**
     * Add a MetadataOnly element to Pixels for the specified series.
     *
     * @param omexml the OME-XML metadata store.
     * @param series the series containing the Pixels element to add MetadataOnly to.
     */
    void
    addMetadataOnly(::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type               series);

    /**
     * Verify correctness of minimal amount of metadata in a series.
     *
     * @param retrieve the OME-XML metadata store.
     * @param series the image series to verify.
     * @throws FormatException if any metadata is missing.
     */
    void
    verifyMinimum(::ome::xml::meta::MetadataRetrieve& retrieve,
                    dimension_size_type               series = 0U);

    /**
     * Check if default creation date is enabled.
     *
     * @returns @c true if enabled, @c false otherwise.
     */
    bool
    defaultCreationDateEnabled();


    /**
     * Get the currently-supported OME Data Model version.
     *
     * @returns the model version.
     */
    std::string
    getModelVersion();

    /**
     * Get the model version used by an OME-XML document
     *
     * @returns the model version.
     */
    std::string
    getModelVersion(const std::string& document)

    /**
     * Transform an OME-XML document to the latest model version
     *
     * @param string the OME-XML document.
     * @returns the transformed OME-XML document.
     */
    std::string
    transformToLatestModelVersion(const std::string& document);

    /**
     * Enable or disable default creation date.
     *
     * This setting enables or disables the replacement of missing
     * creation dates.
     *
     * @see setDefaultCreationDate().
     *
     * @param enabled @c true to enable, @c false to disable.
     */
    void
    defaultCreationDateEnabled(bool enabled);

    /**
     * Set the creation data for a series.
     *
     * If the specified file exists, the modification time of this
     * file will be used as the creation date.  If it does not exist,
     * the current system time will be used as a fallback.
     *
     * This function will do nothing unless
     * defaultCreationDateEnabled(bool) is enabled.
     *
     * @param store the OME-XML metadata store.
     * @param series the series for which to set the creation date.
     * @param id the filename for the series.
     */
    void
    setDefaultCreationDate(::ome::xml::meta::MetadataStore& store,
                           dimension_size_type              series,
                           const boost::filesystem::path&   id);

  }
}

#endif // OME_BIOFORMATS_METADATATOOLS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
