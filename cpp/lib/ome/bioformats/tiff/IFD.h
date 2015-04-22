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

#ifndef OME_BIOFORMATS_TIFF_IFD_H
#define OME_BIOFORMATS_TIFF_IFD_H

#include <string>

#include <ome/compat/memory.h>

#include <ome/bioformats/CoreMetadata.h>
#include <ome/bioformats/TileCoverage.h>
#include <ome/bioformats/tiff/TileInfo.h>
#include <ome/bioformats/tiff/Types.h>
#include <ome/bioformats/VariantPixelBuffer.h>

#include <ome/xml/model/enums/PixelType.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      class TIFF;

      /// Forward declaration of Field<Tag>.
      template<typename Tag>
      class Field;

      /**
       * Image File Directory (IFD).
       *
       * An IFD represents a subfile within a TIFF.
       */
      class IFD : public ome::compat::enable_shared_from_this<IFD>
      {
      private:
        class Impl;
        /// Private implementation details.
        ome::compat::shared_ptr<Impl> impl;

      protected:
        /// Constructor (not public).
        IFD(ome::compat::shared_ptr<TIFF>& tiff,
            offset_type                    offset);

        /// Constructor (not public).
        IFD(ome::compat::shared_ptr<TIFF>& tiff);

      private:
        /// Copy constructor (deleted).
        IFD (const IFD&);

        /// Assignment operator (deleted).
        IFD&
        operator= (const IFD&);

      public:
        /// Destructor.
        virtual ~IFD();

        /**
         * Open an IFD by index.
         *
         * @param tiff the source TIFF.
         * @param index the directory index.
         * @returns the open IFD.
         */
        static ome::compat::shared_ptr<IFD>
        openIndex(ome::compat::shared_ptr<TIFF>& tiff,
                  directory_index_type           index);

        /**
         * Open an IFD.
         *
         * @param tiff the source TIFF.
         * @param offset the directory offset.
         * @returns the open IFD.
         */
        static ome::compat::shared_ptr<IFD>
        openOffset(ome::compat::shared_ptr<TIFF>& tiff,
                   offset_type                    offset);

        /**
         * Get the current IFD.
         *
         * @param tiff the source TIFF.
         * @returns the open IFD.
         */
        static ome::compat::shared_ptr<IFD>
        current(ome::compat::shared_ptr<TIFF>& tiff);

        /**
         * Get the source TIFF this descriptor belongs to.
         *
         * @returns the source TIFF.
         */
        ome::compat::shared_ptr<TIFF>&
        getTIFF() const;

        /**
         * Make this IFD the current directory.
         *
         * Internally this is simply a call to TIFFSetDirectory.
         */
        void
        makeCurrent() const;

        /**
         * Get the directory offset.
         *
         * Internally this is simply a call to TIFFCurrentDirOffset.
         *
         * @returns the directory offset.
         */
        offset_type
        getOffset() const;

        /**
         * Get a field by its tag number.
         *
         * @note This should not be used except internally.  Use
         * getField(TagCategory) instead which offers a type-safe
         * interface on top of this lower-level TIFFGetField wrapper.
         *
         * @param tag the tag number.
         * @param ... pointers to variables to store the value(s) in.
         */
        void
        getRawField(tag_type tag,
                    ...) const;

        /**
         * Set a field by its tag number.
         *
         * @note This should not be used except internally.  Use
         * getField(TagCategory) instead which offers a type-safe
         * interface on top of this lower-level TIFFSetField wrapper.
         *
         * @param tag the tag number.
         * @param ... variables containing the value(s) to set.
         */
        void
        setRawField(tag_type tag,
                    ...);

        /**
         * Get a Field by its tag enumeration.
         *
         * @param tag the field identifier.
         * @returns the Field corresponding to the tag.
         */
        template<typename TagCategory>
        Field<TagCategory>
        getField(TagCategory tag)
        {
          return Field<TagCategory>(this->shared_from_this(), tag);
        }

        /**
         * Get a Field by its tag enumeration.
         *
         * @param tag the field identifier.
         * @returns the Field corresponding to the tag.
         */
        template<typename TagCategory>
        const Field<TagCategory>
        getField(TagCategory tag) const
        {
          return Field<TagCategory>(const_cast<IFD *>(this)->shared_from_this(), tag);
        }

        /**
         * Get the tile type.
         *
         * @returns the tile type.
         */
        TileType
        getTileType() const;

        /**
         * Set the tile type.
         *
         * @param type the tile type.
         */
        void
        setTileType(TileType type);

        /**
         * Get the current tile being written.
         *
         * This is the tile currently being modified pending flush.
         *
         * @returns the current tile.
         */
        dimension_size_type
        getCurrentTile() const;

        /**
         * Set the current tile being written.
         *
         * This is the tile currently being modified pending flush.
         *
         * @note This should not be set by hand; it will be updated by
         * the code writing out tile data called internally by
         * writeImage().
         *
         * @param tile the current tile.
         */
        void
        setCurrentTile(dimension_size_type tile);

        /**
         * Get tiling metadata.
         *
         * @returns the TileInfo metadata for this IFD.
         * @throws an Exception if tiles are not supported.
         */
        TileInfo
        getTileInfo();

        /**
         * Get tiling metadata.
         *
         * @returns the TileInfo metadata for this IFD.
         * @throws an Exception if tiles are not supported.
         */
        const TileInfo
        getTileInfo() const;

        /**
         * Get tile coverage cache.
         *
         * @returns the TileCoverage cache for this IFD.
         */
        std::vector<TileCoverage>&
        getTileCoverage();

        /**
         * Get tile coverage cache.
         *
         * @returns the TileCoverage cache for this IFD.
         */
        const std::vector<TileCoverage>&
        getTileCoverage() const;

        /**
         * Get the image width.
         *
         * @returns the image width.
         */
        uint32_t
        getImageWidth() const;

        /**
         * Set the image width.
         *
         * @param width the image width.
         */
        void
        setImageWidth(uint32_t width);

        /**
         * Get the image height.
         *
         * @returns the image height.
         */
        uint32_t
        getImageHeight() const;

        /**
         * Set the image height.
         *
         * @param height the image height.
         */
        void
        setImageHeight(uint32_t height);

        /**
         * Get the tile width.
         *
         * @returns the tile width.
         */
        uint32_t
        getTileWidth() const;

        /**
         * Set the tile width.
         *
         * @param width the tile width.
         */
        void
        setTileWidth(uint32_t width);

        /**
         * Get the tile height.
         *
         * @returns the tile height.
         */
        uint32_t
        getTileHeight() const;

        /**
         * Set the tile height.
         *
         * @param height the tile height.
         */
        void
        setTileHeight(uint32_t height);

        /**
         * Get the OME data model PixelType.
         *
         * This is computed based upon the SampleFormat and
         * BitsPerSample tags for this IFD.
         *
         * @returns the PixelType.
         * @throws an Exception if there is no corresponding PixelType
         * for the SampleFormat and BitsPerSample in use.
         */
        ::ome::xml::model::enums::PixelType
        getPixelType() const;

        /**
         * Set the OME data model PixelType.
         *
         * This sets the SampleFormat and BitsPerSample tags for this
         * IFD which correspond to the PixelType in use.
         *
         * @param type the PixelType to set.
         * @throws an Exception if the PixelType is invalid.
         */
        void
        setPixelType(::ome::xml::model::enums::PixelType type);

        /**
         * Get bits per sample.
         *
         * @returns the number of bits per sample.
         */
        uint16_t
        getBitsPerSample() const;

        /**
         * Set bits per sample.
         *
         * @param samples the number of bits per sample.
         */
        void
        setBitsPerSample(uint16_t samples);

        /**
         * Get samples per pixel.
         *
         * @returns the number of samples per pixel.
         */
        uint16_t
        getSamplesPerPixel() const;

        /**
         * Set samples per pixel.
         *
         * @param samples the number of samples per pixel.
         */
        void
        setSamplesPerPixel(uint16_t samples);

        /**
         * Get planar configuration.
         *
         * @returns the number of planar configuration.
         */
        PlanarConfiguration
        getPlanarConfiguration() const;

        /**
         * Set planar configuration.
         *
         * @param planarconfig the number of planar configuration.
         */
        void
        setPlanarConfiguration(PlanarConfiguration planarconfig);

        /**
         * Get photometric interpretation.
         *
         * @returns the photometric interpretation of sample values.
         */
        PhotometricInterpretation
        getPhotometricInterpretation() const;

        /**
         * Set photometric interpretation.
         *
         * @param photometric the photometric interpretation of sample values.
         */
        void
        setPhotometricInterpretation(PhotometricInterpretation photometric);

        /**
         * Read a whole image plane into a pixel buffer.
         *
         * @param buf the destination pixel buffer.
         */
        void
        readImage(VariantPixelBuffer& buf) const;

        /**
         * @copydoc IFD::readImage(VariantPixelBuffer&) const
         * @param subC the subchannel to read.
         */
        void
        readImage(VariantPixelBuffer& buf,
                  dimension_size_type subC) const;

        /**
         * Read a region of an image plane into a pixel buffer.
         *
         * If the destination pixel buffer is of a different size to
         * the region being read, or is of the incorrect pixel type,
         * or has a different storage order, it will be resized using
         * the correct pixel type and storage order.
         *
         * @param dest the destination pixel buffer.
         * @param x the @c X coordinate of the upper-left corner of the sub-image.
         * @param y the @c Y coordinate of the upper-left corner of the sub-image.
         * @param w the width of the sub-image.
         * @param h the height of the sub-image.
         */
        void
        readImage(VariantPixelBuffer& dest,
                  dimension_size_type x,
                  dimension_size_type y,
                  dimension_size_type w,
                  dimension_size_type h) const;

        /**
         * @copydoc IFD::readImage(VariantPixelBuffer&,dimension_size_type,dimension_size_type,dimension_size_type,dimension_size_type) const
         * @param subC the subchannel to read.
         */
        void
        readImage(VariantPixelBuffer& dest,
                  dimension_size_type x,
                  dimension_size_type y,
                  dimension_size_type w,
                  dimension_size_type h,
                  dimension_size_type subC) const;

        /**
         * Read a lookup table into a pixel buffer.
         *
         * @param buf the destination pixel buffer.
         */
        void
        readLookupTable(VariantPixelBuffer& buf) const;

        /**
         * Write a whole image plane from a pixel buffer.
         *
         * @param buf the source pixel buffer.
         */
        void
        writeImage(const VariantPixelBuffer& buf);

        /**
         * @copydoc IFD::writeImage(const VariantPixelBuffer&)
         * @param subC the subchannel to write.
         */
        void
        writeImage(const VariantPixelBuffer& buf,
                   dimension_size_type       subC);

        /**
         * Write a whole image plane from a pixel buffer.
         *
         * The source pixel buffer must match the size of the region
         * being written, and must also the same pixel type and
         * storage ordering as the TIFF image.
         *
         * @param source the source pixel buffer.
         * @param x the @c X coordinate of the upper-left corner of the sub-image.
         * @param y the @c Y coordinate of the upper-left corner of the sub-image.
         * @param w the width of the sub-image.
         * @param h the height of the sub-image.
         */
        void
        writeImage(const VariantPixelBuffer& source,
                   dimension_size_type       x,
                   dimension_size_type       y,
                   dimension_size_type       w,
                   dimension_size_type       h);

        /**
         * @copydoc IFD::writeImage(const VariantPixelBuffer&,dimension_size_type,dimension_size_type,dimension_size_type,dimension_size_type)
         * @param subC the subchannel to write.
         */
        void
        writeImage(const VariantPixelBuffer& source,
                   dimension_size_type       x,
                   dimension_size_type       y,
                   dimension_size_type       w,
                   dimension_size_type       h,
                   dimension_size_type       subC);

        /**
         * Get next directory.
         *
         * @returns the next directory, or null if this is the last directory.
         */
        ome::compat::shared_ptr<IFD>
        next() const;

        /**
         * Check if this is the last directory.
         *
         * @returns @c true if last, @c false otherwise.
         */
        bool
        last() const;
      };

    }
  }
}

#endif // OME_BIOFORMATS_TIFF_IFD_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
