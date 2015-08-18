/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#include <string>

#include <ome/bioformats/FormatReader.h>
#include <ome/bioformats/MetadataMap.h>
#include <ome/bioformats/Types.h>

#include <ome/common/filesystem.h>

#include <ome/xml/meta/Metadata.h>
#include <ome/xml/meta/MetadataRoot.h>
#include <ome/xml/meta/OMEXMLMetadata.h>

#include <ome/xml/model/enums/DimensionOrder.h>

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
     * Create OME-XML metadata from DOM Document.
     *
     * @param document the XML document.
     * @returns the OME-XML metadata.
     */
    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(ome::common::xml::dom::Document& document);

    /**
     * Create OME-XML metadata from XML file.
     *
     * @param file the XML file.
     * @returns the OME-XML metadata.
     */
    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(const boost::filesystem::path& file);

    /**
     * Create OME-XML metadata from XML string.
     *
     * @param text the XML string.
     * @returns the OME-XML metadata.
     */
    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(const std::string& text);

    /**
     * Create OME-XML metadata from XML input stream.
     *
     * @param stream the XML input stream.
     * @returns the OME-XML metadata.
     */
    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(std::istream& stream);

    /**
     * Create OME-XML metadata from reader core metadata.
     *
     * @param reader the reader to use.
     * @param doPlane create Plane elements if @c true.
     * @param doImageName set image name if @c true.
     * @returns the OME-XML metadata.
     */
    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(const FormatReader& reader,
                         bool                doPlane = false,
                         bool                doImageName = true);


    /**
     * Create OME-XML metadata root from XML document.
     *
     * @param document the XML document source.
     * @returns the OME-XML metadata root.
     */
    ome::compat::shared_ptr< ::ome::xml::meta::MetadataRoot>
    createOMEXMLRoot(const std::string& document);

    /**
     * Get OME-XML metadata from metadata.
     *
     * This will convert the metadata to OME-XML metadata if required.
     *
     * @param retrieve the metadata to use.
     * @returns the OME-XML metadata.
     */
    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    getOMEXMLMetadata(ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve>& retrieve);

    /**
     * Get OME-XML document from OME-XML metadata.
     *
     * This will convert the OME-XML metadata to an XML document
     * string.
     *
     * @param omexml the OME-XML metadata store.
     * @param validate @c true to validate the OME-XML, @c false to
     * skip validation.
     * @returns the OME-XML metadata as an XML document string.
     */
    std::string
    getOMEXML(::ome::xml::meta::OMEXMLMetadata& omexml,
              bool                              validate = true);

    /**
     * Validate an OME-XML document.
     *
     * @param document the XML document source.
     * @returns @c true if valid, @c false if invalid.
     */
    bool
    validateOMEXML(const std::string& document);

    /**
     * Validate a metadata store.
     *
     * Note that unlike validateOMEXML(const std::string&) this does
     * not perform XML validation.  It will look for missing or
     * inconsistent metadata and (if specified) attempt to correct the
     * metadata to make it valid.
     *
     * @param meta the metadata store.
     * @param correct @c true to attempt correction, @c false to leave
     * unmodified.
     * @returns @c true if valid, @c false if invalid.
     * @throws FormatException if correction of invalid metadata fails.
     */
    bool
    validateModel(::ome::xml::meta::Metadata& meta,
                  bool                        correct);

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
     * Fill OME-XML metadata store from core metadata.
     *
     * The metadata store is expected to be empty.
     *
     * @param store the OME-XML metadata store.
     * @param seriesList the core metadata to use.
     * @param doPlane create Plane elements if @c true.
     */
    void
    fillMetadata(::ome::xml::meta::MetadataStore&                          store,
                 const std::vector<ome::compat::shared_ptr<CoreMetadata> > seriesList,
                 bool                                                      doPlane = false);

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
     * Fill an OME-XML metadata store Pixels element from core metadata.
     *
     * Set Pixels metadata for the the specified series.
     *
     * @param store the OME-XML metadata store.
     * @param seriesMetadata the seriesMetadata the metadata to use.
     * @param series the series to set.
     */
    void
    fillPixels(::ome::xml::meta::MetadataStore& store,
               const CoreMetadata&              seriesMetadata,
               dimension_size_type              series);

    /**
     * Add a MetadataOnly element to Pixels for the specified series.
     *
     * @param omexml the OME-XML metadata store.
     * @param series the series containing the Pixels element to add
     * MetadataOnly to.
     * @param resolve @c true to resolve references, @c false to skip
     * resolving references
     */
    void
    addMetadataOnly(::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type               series,
                    bool                              resolve = true);

    /**
     * Get ModuloAlongZ annotation from OME-XML metadata.
     *
     * @param omexml the OME-XML metadata store.
     * @param image the image index.
     * @returns the Modulo annotation.
     */
    Modulo
    getModuloAlongZ(const ::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type                     image);

    /**
     * Get ModuloAlongT annotation from OME-XML metadata.
     *
     * @param omexml the OME-XML metadata store.
     * @param image the image index.
     * @returns the Modulo annotation.
     */
    Modulo
    getModuloAlongT(const ::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type                     image);

    /**
     * Get ModuloAlongC annotation from OME-XML metadata.
     *
     * @param omexml the OME-XML metadata store.
     * @param image the image index.
     * @returns the Modulo annotation.
     */
    Modulo
    getModuloAlongC(const ::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type                     image);

    /**
     * Get Modulo annotation from OME-XML metadata.
     *
     * @param omexml the OME-XML metadata store.
     * @param tag the Modulo annotation XML tag name.
     * @param image the image index.
     * @returns the Modulo annotation.
     */
    Modulo
    getModulo(const ::ome::xml::meta::OMEXMLMetadata& omexml,
              const std::string&                      tag,
              dimension_size_type                     image);

    /**
     * Verify correctness of minimal amount of metadata in a series.
     *
     * @param retrieve the OME-XML metadata store.
     * @param series the image series to verify.
     * @throws FormatException if any metadata is missing.
     */
    void
    verifyMinimum(::ome::xml::meta::MetadataRetrieve& retrieve,
                  dimension_size_type                 series = 0U);

    /**
     * Remove all BinData elements from OME-XML metadata.
     *
     * @param omexml the OME-XML metadata store.
     */
    void
    removeBinData(::ome::xml::meta::OMEXMLMetadata& omexml);

    /**
     * Remove all but the specified number of valid Channel elements
     * from OME-XML metadata.
     *
     * @param omexml the OME-XML metadata store.
     * @param image the image index.
     * @param sizeC the number of channels to retain.
     */
    void
    removeChannels(::ome::xml::meta::OMEXMLMetadata& omexml,
                   dimension_size_type               image,
                   dimension_size_type               sizeC);

    /**
     * Get original metadata from OME-XML metadata StructuredAnnotations.
     *
     * @param omexml the OME-XML metadata store.
     * @returns a map of the original metadata annotations.
     */
    MetadataMap
    getOriginalMetadata(::ome::xml::meta::OMEXMLMetadata& omexml);

    /**
     * Create OriginalMetadataAnnotations from MetadataMap.
     *
     * @param omexml the OME-XML metadata store.
     * @param metadata the original metadata.
     */
    void
    fillOriginalMetadata(::ome::xml::meta::OMEXMLMetadata& omexml,
                         const MetadataMap&                metadata);

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
     * @param document the OME-XML document.
     * @returns the model version.
     */
    std::string
    getModelVersion(ome::common::xml::dom::Document& document);

    /**
     * Get the model version used by an OME-XML document
     *
     * @param document the OME-XML document.
     * @returns the model version.
     */
    std::string
    getModelVersion(const std::string& document);

    /**
     * Transform an OME-XML document to the latest model version
     *
     * @param document the OME-XML document.
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

    /**
     * Create a valid DimensionOrder from string.
     *
     * Any duplicate dimension will have all duplicates following the
     * initial instance removed.  Any missing dimension will be
     * suffixed to the resulting dimension order; the order of these
     * dimensions is unspecified (since they weren't provided, the
     * expectation is that the caller did not care).
     *
     * @param order the string dimension order.
     * @returns the dimension order.
     * @throws std::logic_error if the provided order is invalid.
     */
    ome::xml::model::enums::DimensionOrder
    createDimensionOrder(const std::string& order);

    /**
     * Get the total size of pixel data in a series.
     *
     * The size for the pixel type is rounded up to the nearest byte
     * before multiplying by the dimension sizes.
     *
     * @param meta the metadata to use.
     * @param series the image series to use.
     * @returns the size (in bytes).
     */
    storage_size_type
    pixelSize(const ::ome::xml::meta::MetadataRetrieve& meta,
              dimension_size_type                       series);

    /**
     * Get the total size of pixel data for all series.
     *
     * The size for the pixel type is rounded up to the nearest byte
     * before multiplying by the dimension sizes.
     *
     * @param meta the metadata to use.
     * @returns the size (in bytes).
     */
    storage_size_type
    pixelSize(const ::ome::xml::meta::MetadataRetrieve& meta);


    /**
     * Get the total significant size of pixel data in a series.
     *
     * The significant size for the pixel type (in bits) is multiplied
     * by the dimension sizes before converting to bytes.
     *
     * @param meta the metadata to use.
     * @param series the image series to use.
     * @returns the size (in bytes).
     */
    storage_size_type
    significantPixelSize(const ::ome::xml::meta::MetadataRetrieve& meta,
                         dimension_size_type                       series);

    /**
     * Get the total significant size of pixel data for all series.
     *
     * The significant size for the pixel type (in bits) is multiplied
     * by the dimension sizes before converting to bytes.
     *
     * @param meta the metadata to use.
     * @returns the size (in bytes).
     */
    storage_size_type
    significantPixelSize(const ::ome::xml::meta::MetadataRetrieve& meta);

  }
}

#endif // OME_BIOFORMATS_METADATATOOLS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
