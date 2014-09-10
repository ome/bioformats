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

#ifndef OME_BIOFORMATS_FORMATWRITER_H
#define OME_BIOFORMATS_FORMATWRITER_H

#include <string>
#include <vector>
#include <map>

#include <boost/optional.hpp>

#include <ome/bioformats/CoreMetadata.h>
#include <ome/bioformats/FileInfo.h>
#include <ome/bioformats/FormatHandler.h>
#include <ome/bioformats/MetadataConfigurable.h>
#include <ome/bioformats/MetadataMap.h>
#include <ome/bioformats/Types.h>

#include <ome/compat/array.h>

#include <ome/xml/meta/MetadataRetrieve.h>

namespace ome
{
  namespace bioformats
  {

    class VariantPixelBuffer;

    /**
     * Interface for all biological file format writers.
     *
     * @note No IOException in C++.
     *
     * @note getColorModel and setColorModel not implemented; it is
     * Java-specific and if needed will require reimplementing in C++.
     *
     * @note getCodecOptions and setCodec options not implemented;
     * these rely on the Java-specific ColorModel and its design
     * reflects that of Java c javax.imageio which we can't use here,
     * though bits of it might require reimplementing.
     *
     * @todo Implement saveBytes methods using 2D @c Region with
     * and/or in terms of Boost.MultiArray index ranges.
     */
    class FormatWriter : virtual public FormatHandler
    {
    public:
      /// Frame rate type.
      typedef uint16_t frame_rate_type;

    protected:
      /**
       * Sentry for saving and restoring writer series state.
       *
       * For any FormatWriter method or subclass method which needs to
       * set and later restore the series/coreIndex/resolution as part
       * of its operation, this class exists to manage the safe
       * restoration of the state.  Create an instance of this class
       * with the writer set to @c *this.  When the instance goes out
       * of scope, e.g. at the end of a block or method, or when an
       * exception is thrown, the saved state will be transparently
       * restored.
       */
      class SaveSeries
      {
      private:
        /// Writer for which the state will be saved and restored.
        const FormatWriter& writer;
        /// Saved state.
        dimension_size_type series;

      public:
        /**
         * Constructor.
         *
         * @param writer the writer to manage.
         */
        SaveSeries(const FormatWriter& writer):
          writer(writer),
          series(writer.getSeries())
        {}

        /**
         * Destructor.
         *
         * Saved state will be restored when run.
         */
        ~SaveSeries()
        {
          try
            {
              if (series != writer.getSeries())
                writer.setSeries(series);
            }
          catch (...)
            {
              // We can't throw in a destructor.
            }
        }
      };

      /// Constructor.
      FormatWriter()
      {}

    public:
      /// Destructor.
      virtual
      ~FormatWriter()
      {}

      /**
       * Set the 8-bit color lookup table associated with the current
       * series.
       *
       * If the pixel type of the current series is anything other
       * than ome::xml::model::enums::PixelType::UINT8 or
       * ome::xml::model::enums::PixelType::INT8 this method will
       * throw an exception.
       *
       * @param buf the source pixel buffer.
       */
      virtual
      void
      set8BitLookupTable(const VariantPixelBuffer& buf) = 0;

      /**
       * Get the 16-bit color lookup table associated with the current
       * series.
       *
       * If the pixel type of the current series is anything other
       * than ome::xml::model::enums::PixelType::UINT16 or
       * ome::xml::model::enums::PixelType::INT16 this method will
       * throw an exception.
       *
       * @param buf the source pixel buffer.
       */
      virtual
      void
      set16BitLookupTable(const VariantPixelBuffer& buf) = 0;

      /**
       * Save an image plane.
       *
       * Write an image plane from a VariantPixelBuffer
       * of size
       *
       * \code{.cpp}
       * getSizeX * getSizeY * bytesPerPixel * getRGBChannelCount()
       * \endcode
       *
       * to the current series in the current file.
       *
       * @param no the image index within the file.
       * @param buf the source pixel buffer.
       * @throws FormatException if any of the parameters are invalid.
       */
      virtual
      void
      saveBytes(dimension_size_type no,
                VariantPixelBuffer& buf) = 0;

      /**
       * Save an image plane.
       *
       * Write an image plane from a VariantPixelBuffer
       * of size
       *
       * \code{.cpp}
       * getSizeX * getSizeY * bytesPerPixel * getRGBChannelCount()
       * \endcode
       *
       * to the current series in the current file.
       *
       * @param no the image index within the file.
       * @param buf the source pixel buffer.
       * @param x the @c X coordinate of the upper-left corner of the sub-image.
       * @param y the @c Y coordinate of the upper-left corner of the sub-image.
       * @param w the width of the sub-image.
       * @param h the height of the sub-image.
       * @throws FormatException if any of the parameters are invalid.
       */
      virtual
      void
      saveBytes(dimension_size_type no,
                VariantPixelBuffer& buf,
                dimension_size_type x,
                dimension_size_type y,
                dimension_size_type w,
                dimension_size_type h) = 0;

      /**
       * Set the active series.
       *
       * @param no the series to activate.
       *
       * @todo Remove use of stateful API which requires use of
       * series switching in const methods.
       */
      virtual
      void
      setSeries(dimension_size_type no) const = 0;

      /**
       * Get the active series.
       *
       * @returns the active series.
       */
      virtual
      dimension_size_type
      getSeries() const = 0;

      /**
       * Get whether or not the writer can save multiple images in a
       * single file.
       *
       * @returns @c true if the writer supports multiple images, @c
       * false otherwise.
       */
      virtual
      bool
      canDoStacks() const = 0;

      /**
       * Set the default metadata store for this writer.
       *
       * @param retrieve a metadata retrieve implementation.
       */
      virtual
      void
      setMetadataRetrieve(std::shared_ptr< ::ome::xml::meta::MetadataRetrieve>& retrieve) = 0;

      /**
       * Get the current metadata store for this writer.
       *
       * @returns the metadata store, which will never be @c null.
       */
      virtual
      const std::shared_ptr< ::ome::xml::meta::MetadataRetrieve>&
      getMetadataRetrieve() const = 0;

      /**
       * Get the current metadata store for this writer.
       *
       * @returns the metadata store, which will never be @c null.
       */
      virtual
      std::shared_ptr< ::ome::xml::meta::MetadataRetrieve>&
      getMetadataRetrieve() = 0;

      /**
       * Set the frame rate to use when writing.
       *
       * @param rate the frame rate (number of frames per second).
       */
      virtual
      void
      setFramesPerSecond(frame_rate_type rate) = 0;

      /**
       * Get the frame rate to use when writing.
       *
       * @returns the frame rate (number of frames per second).
       */
      virtual
      frame_rate_type
      getFramesPerSecond() const = 0;

      /**
       * Get supported pixel types.
       *
       * @returns the supported pixel types.
       */
      virtual
      const std::vector<ome::xml::model::enums::PixelType>&
      getPixelTypes() const = 0;

      /**
       * Get supported pixel types for the given codec.
       *
       * @param codec the codec to check.
       * @returns the supported pixel types.
       */
      virtual
      const std::vector<ome::xml::model::enums::PixelType>&
      getPixelTypes(const std::string& codec) const = 0;

      /**
       * Check if the pixel type is supported.
       *
       * @param type the pixel type to check.
       * @returns @c true if supported, @c false otherwise.
       */
      virtual
      bool
      isSupportedType(ome::xml::model::enums::PixelType type) const = 0;

      /**
       * Check if the pixel type is supported by the given codex.
       *
       * @param type the pixel type to check.
       * @param codec the codec to check.
       * @returns @c true if supported, @c false otherwise.
       */
      virtual
      bool
      isSupportedType(ome::xml::model::enums::PixelType type,
		      const std::string&                codec) const = 0;

      /**
       * Get supported compression types.
       *
       * @returns the supported compression types.
       */
      virtual
      const std::vector<std::string>&
      getCompressionTypes() const  = 0;

      /**
       * Set the compression type to use when writing.
       *
       * @param compression the compression type.
       */
      virtual
      void
      setCompression(const std::string& compression) = 0;

      /**
       * Get the compression type to use when writing.
       *
       * @returns the compression type.
       */
      virtual
      const std::string&
      getCompression() const = 0;

      /**
       * Switch the output file for the current dataset.
       *
       * @param id the new file name.
       */
      virtual
      void
      changeOutputFile(const std::string& id) = 0;

      /**
       * Write planes sequentially.
       *
       * Set if planes will be written sequentially.  If planes are
       * written sequentially and this flag is set, then performance
       * will be slightly improved.
       *
       * @param sequential @c true if sequential, @c false if not.
       */
      virtual
      void
      setWriteSequentially(bool sequential = true) = 0;

      /**
       * Get all underlying writers.
       *
       * @note If there are no underlying writers, the list will be
       * empty.
       *
       * @returns a list of writers.
       */
      virtual
      std::vector<std::shared_ptr<FormatWriter> >
      getUnderlyingWriters() const = 0;

    };

  }
}

#endif // OME_BIOFORMATS_FORMATWRITER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
