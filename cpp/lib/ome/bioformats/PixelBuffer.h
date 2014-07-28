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

#ifndef OME_BIOFORMATS_PIXELBUFFER_H
#define OME_BIOFORMATS_PIXELBUFFER_H

#include <istream>
#include <limits>
#include <ostream>
#include <stdexcept>
#include <string>

#include <boost/multi_array.hpp>

#include <ome/bioformats/PixelProperties.h>

#include <ome/compat/array.h>
#include <ome/compat/cstdint.h>
#include <ome/compat/memory.h>

#include <ome/xml/model/enums/DimensionOrder.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Dimensions.
     *
     * The OME data model currently supports five dimensions (XYZTC),
     * plus an implicit dimension (subchannel) and three modulo
     * dimensions which subdivide Z, T and C.  This enumeration is
     * used to refer to specific dimensions, and the value is the
     * logical dimension order used for the PixelBuffer interface.
     *
     * The current interface requires all dimensions to be present
     * even if unused.  Future model changes may remove this
     * requirement.
     */
     enum Dimensions
     {
       DIM_SPATIAL_X  = 0, ///< Spatial x dimension (X).
       DIM_SPATIAL_Y  = 1, ///< Spatial y dimension (Y).
       DIM_SPATIAL_Z  = 2, ///< Spatial z dimension (Z).
       DIM_TEMPORAL_T = 3, ///< Temporal t dimension (T).
       DIM_CHANNEL    = 4, ///< Logical channel (typically detectors of specific wavelengths) (C).
       DIM_SUBCHANNEL = 5, ///< Logical sub-channel (typically used for RGB channel sub-components) (S).
       DIM_MODULO_Z   = 6, ///< Logical subdivision of the spatial z dimension (z).
       DIM_MODULO_T   = 7, ///< Logical subdivision of the temporal t dimension (t).
       DIM_MODULO_C   = 8  ///< Logical subdivision of the logical channel dimension (c).
     };

    /**
     * Base class for all PixelBuffer types.
     *
     * Individual pixel buffer types are created from the PixelType
     * and EndianType enumerations.  However, the same underlying type
     * may be used for two or more combinations of each type,
     * depending upon the hardware and compiler implementation,
     * meaning that it isn't possible to determine the original
     * PixelType and EndianType from the language type alone.  This
     * base class stores the PixelType and EndianType in order to
     * provide reliable introspection.
     */
    class PixelBufferBase
    {
    public:
      /// Total number of supported dimensions.
      static const uint16_t dimensions = 9;

      /// Size type.
      typedef boost::multi_array_types::size_type size_type;

      /// Index type.
      typedef boost::multi_array_types::index index;

      /// Type used to index all dimensions in public interfaces.
      typedef std::array<boost::multi_array_types::index,
                         PixelBufferBase::dimensions> indices_type;

      /// Storage ordering type for controlling pixel memory layout.
      typedef boost::general_storage_order<dimensions> storage_order_type;

      /// Extent range type.
      typedef boost::detail::multi_array::extent_gen<dimensions> range_type;

    protected:
      /**
       * Constructor.
       *
       * @param pixeltype the pixel type stored in this buffer.
       * @param endiantype the endian type variant of the pixel type
       * stored in this buffer.
       */
      PixelBufferBase(::ome::xml::model::enums::PixelType pixeltype,
                      EndianType                          endiantype):
        pixeltype(pixeltype),
        endiantype(endiantype)
      {}

    public:
      /// Destructor.
      virtual ~PixelBufferBase()
      {}

      /**
       * Get the pixel type in use.
       *
       * @returns the pixel type.
       */
      ::ome::xml::model::enums::PixelType
      pixelType() const
      {
        return pixeltype;
      }

      /**
       * Get the endian type in use.
       *
       * @returns the endian type.
       */
      EndianType
      endianType() const
      {
        return endiantype;
      }

      /**
       * Generate storage ordering for a given dimension order.
       *
       * This converts the OME data model dimension ordering
       * specification to the native 9D ordering, including subchannel
       * and modulo components.
       *
       * @param order the OME data model dimension ordering
       * @param interleaved @c true if subchannels are interleaved
       * (chunky), @c false otherwise (planar).
       * @returns the storage ordering.
       */
      static storage_order_type
      make_storage_order(ome::xml::model::enums::DimensionOrder order,
                         bool                                   interleaved);

      /**
       * Generate default storage ordering.
       *
       * The default is @c XYZTC with subchannel interleaving (i.e. @c
       * SXYzZtTcC as the 9D order).
       *
       * @returns the storage ordering.
       */
      static storage_order_type
      default_storage_order();

    private:
      /// Pixel type stored in this buffer.
      const ::ome::xml::model::enums::PixelType pixeltype;

      /// Endian type stored in this buffer.
      const EndianType endiantype;
    };

    /**
     * Buffer for a specific pixel type.
     *
     * The purpose of this class is to allow transfer of pixel data of
     * specific type and endianness, and of any dimensionality.
     *
     * Internally, multi-dimensional order is achieved using
     * @c Boost.MultiArray.  The @c MultiArray data buffer is provided
     * internally by default, but may also be provided externally, for
     * example from a memory-mapped file.
     *
     * Nine dimensions are currently supported.
     *
     * @see Dimensions for the logical dimension ordering.
     *
     * The logical dimension ordering is the order used by the
     * interface.  However, the storage ordering in memory may be
     * completely different.  These may differ when:
     * - the file format uses a different ordering, including direct
     *   memory-mapping of the raw pixel data
     * - the consumer of the data requires a specific ordering, for
     *   example to ensure optimal performance of algorithms iterating
     *   over the pixel data
     *
     * If the order the data is accessed in differs from the
     * underlying storage order, there may be a significant
     * performance cost.  It is recommended that users should query
     * the storage ordering to iterate over the pixels in the optimal
     * order or require a specific ordering.  Reordering the data may
     * also bear a significant cost.
     *
     * @todo Add support for subsetting dimensions.
     */
    template<typename T>
    class PixelBuffer : public PixelBufferBase
    {
    public:
      /// Pixel value type.
      typedef T value_type;

      /**
       * Type for multi-dimensional pixel array view referencing
       * external data.  This type is always a view over a separate
       * buffer.
       */
      typedef boost::multi_array_ref<value_type, dimensions> array_ref_type;

      /**
       * Type for multi-dimensional pixel array view.  This type
       * uses an internal data buffer.
       */
      typedef boost::multi_array<value_type, dimensions> array_type;

      /**
       * Default constructor.
       *
       * This constructs a pixel buffer of size 1 in all dimensions.
       * The desired size should be set after construction.
       */
      explicit PixelBuffer():
        PixelBufferBase(::ome::xml::model::enums::PixelType::UINT8, NATIVE),
        multiarray(new array_type(boost::extents[1][1][1][1][1][1][1][1][1],
                                  PixelBufferBase::default_storage_order()))
      {}

      /**
       * Construct from extents (internal storage).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param extents the extent of each dimension.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       */
      template<class ExtentList>
      explicit
      PixelBuffer(const ExtentList&                   extents,
                  ::ome::xml::model::enums::PixelType pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                  EndianType                          endiantype = NATIVE,
                  const storage_order_type&           storage = PixelBufferBase::default_storage_order()):
        PixelBufferBase(pixeltype, endiantype),
        multiarray(new array_type(extents, storage))
      {}

      /**
       * Construct from extents (external storage).
       *
       * Storage for the buffer must be pre-allocated by the caller
       * and must exist for the lifetime of this object.
       *
       * @param pixeldata the externally-provided storage for pixel
       * data.
       * @param extents the extent of each dimension.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       */
      template<class ExtentList>
      explicit
      PixelBuffer(value_type                          *pixeldata,
                  const ExtentList&                    extents,
                  ::ome::xml::model::enums::PixelType  pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                  EndianType                           endiantype = NATIVE,
                  const storage_order_type&            storage = PixelBufferBase::default_storage_order()):
        PixelBufferBase(pixeltype, endiantype),
        multiarray(new array_ref_type(pixeldata, extents, storage))
      {}

      /**
       * Construct from ranges (internal storage).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param range the range of each dimension.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       */
      explicit
      PixelBuffer(const range_type&                   range,
                  ::ome::xml::model::enums::PixelType pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                  EndianType                          endiantype = NATIVE,
                  const storage_order_type&           storage = PixelBufferBase::default_storage_order()):
        PixelBufferBase(pixeltype, endiantype),
        multiarray(new array_type(range, storage))
      {}

      /**
       * Construct from ranges (external storage).
       *
       * Storage for the buffer must be pre-allocated by the caller
       * and must exist for the lifetime of this object.
       *
       * @param pixeldata the externally-provided storage for pixel
       * data.
       * @param range the range of each dimension.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       */
      explicit
      PixelBuffer(value_type                          *pixeldata,
                  const range_type&                    range,
                  ::ome::xml::model::enums::PixelType  pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                  EndianType                           endiantype = NATIVE,
                  const storage_order_type&            storage = PixelBufferBase::default_storage_order()):
        PixelBufferBase(pixeltype, endiantype),
        multiarray(new array_ref_type(pixeldata, range, storage))
      {}

      /**
       * Copy constructor.
       *
       * Note that due to the use of shared pointers this only
       * performs a shallow copy.
       *
       * @param buffer the buffer to copy.
       */
      explicit
      PixelBuffer(const PixelBuffer& buffer):
        PixelBufferBase(buffer),
        multiarray(buffer.multiarray)
      {}

      /// Destructor.
      virtual ~PixelBuffer()
      {}

      /**
       * Get the pixel data.
       *
       * @returns the multidimensional pixel data array; this value
       * will never be null.
       */
      std::shared_ptr<array_ref_type>
      array()
      {
        return multiarray;
      }

      /**
       * Get the pixel data.
       *
       * @returns the multidimensional pixel data array; this value
       * will never be null.
       */
      const std::shared_ptr<array_ref_type>
      array() const
      {
        return multiarray;
      }

      /**
       * Get the raw data.
       *
       * This is the buffer referenced by the multidimensional pixel
       * data array.
       *
       * @returns a pointer to the raw data buffer.
       */
      value_type *
      data()
      {
        return array()->data();
      }

      /**
       * Get the raw data.
       *
       * This is the buffer referenced by the multidimensional pixel
       * data array.
       *
       * @returns a pointer to the raw data buffer.
       */
      const value_type *
      data() const
      {
        return array()->data();
      }

      /**
       * Check the buffer validity.
       *
       * This tests if the @c MultiArray is not null, and hence safe
       * to use.  Note that this is not a guarantee of safety in the
       * case of using an externally managed data buffer, in which
       * case the external buffer must still be valid in addition.
       *
       * @returns @c true if not null, @c false if null.
       */
      bool
      valid() const
      {
        return (multiarray);
      }

      /**
       * Check if the buffer is internally managed.
       *
       * @returns @c true if the @c MultiArray data is managed
       * internally (i.e. is a @c multi_array) or @c false if not
       * managed (i.e. is a @c multi_array_ref).
       */
      bool
      managed() const
      {
        std::shared_ptr<array_type> m(std::dynamic_pointer_cast<array_type>(multiarray));
        return !m;
      }

      /**
       * Get the number of pixel elements in the multi-dimensional array.
       */
      size_type
      num_elements() const
      {
        return array()->num_elements();
      }

      /**
       * Get the number of dimensions in the multi-dimensional array.
       */
      size_type
      num_dimensions() const
      {
        return array()->num_dimensions();
      }

      /**
       * Get the shape of the multi-dimensional array.
       *
       * The shape is the extent of each array dimension.
       *
       * @returns an array of extents (size is the dimension size).
       */
      const size_type *
      shape() const
      {
        return array()->shape();
      }

      /**
       * Get the strides of the multi-dimensional array.
       *
       * The strides are the stride associated with each array dimension.
       *
       * @returns an array of strides (size is the dimension size).
       */
      const boost::multi_array_types::index *
      strides() const
      {
        return array()->strides();
      }

      /**
       * Get the index bases of the multi-dimensional array.
       *
       * The index bases are the numeric index of the first element
       * for each array dimension of the multi-dimensional array.
       *
       * @returns an array of index bases (size is the dimension size).
       */
      const boost::multi_array_types::index *
      index_bases() const
      {
        return array()->index_bases();
      }

      /**
       * Get the origin of the array.
       *
       * This is the address of the element at @c
       * [0][0][0][0][0][0][0][0][0].  Note that this is not always
       * the buffer start address, depending upon the dimension
       * ordering.
       *
       * @returns the address of the array origin.
       */
      const value_type *
      origin() const
      {
        return array()->origin();
      }

      /**
       * Get the array storage order.
       *
       * @returns the storage order.
       */
      const storage_order_type&
      storage_order() const
      {
        return array()->storage_order();
      }

      /**
       * Compare a pixel buffer for equality.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if equal, @c false if not equal.
       */
      bool
      operator == (const PixelBuffer& rhs) const
      {
        return *multiarray == *(rhs.multiarray);
      }

      /**
       * Compare a pixel buffer for equality with a multiarray.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if equal, @c false if not equal.
       */
      bool
      operator == (const array_ref_type& rhs) const
      {
        return *multiarray == rhs;
      }

      /**
       * Compare a pixel buffer for inequality.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator != (const PixelBuffer& rhs) const
      {
        return *multiarray != *(rhs.multiarray);
      }

      /**
       * Compare a pixel buffer for inequality with a multiarray.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator != (const array_ref_type& rhs) const
      {
        return *multiarray != rhs;
      }

      /**
       * Less than comparison with a pixel buffer.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator < (const PixelBuffer& rhs) const
      {
        return *multiarray < *(rhs.multiarray);
      }

      /**
       * Less than comparison with a multiarray.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator < (const array_ref_type& rhs) const
      {
        return *multiarray < rhs;
      }

      /**
       * Less than or equal comparison with a pixel buffer.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator <= (const PixelBuffer& rhs) const
      {
        return *multiarray <= *(rhs.multiarray);
      }

      /**
       * Less than or equal comparison with a multiarray.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator <= (const array_ref_type& rhs) const
      {
        return *multiarray <= rhs;
      }

      /**
       * Greater than comparison with a pixel buffer.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator > (const PixelBuffer& rhs) const
      {
        return *multiarray > *(rhs.multiarray);
      }

      /**
       * Greater than comparison with a multiarray.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator > (const array_ref_type& rhs) const
      {
        return *multiarray > rhs;
      }

      /**
       * Greater than or equal comparison with a pixel buffer.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator >= (const PixelBuffer& rhs) const
      {
        return *multiarray >= *(rhs.multiarray);
      }

      /**
       * Greater than or equal comparison with a multiarray.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator >= (const array_ref_type& rhs) const
      {
        return *multiarray >= rhs;
      }

      /**
       * Assign pixel values.
       *
       * Note that the range to assign must be equal to num_elements().
       *
       * @param begin the start of the range to assign.
       * @param end the end of the range to assign.
       */
      template <typename InputIterator>
      void
      assign(InputIterator begin,
             InputIterator end)
      {
        array()->assign(begin, end);
      }

      /**
       * Get the pixel value at an index.
       *
       * @note If the index is out of bounds, an assertion failure
       * will immediately abort the program, so take care to ensure it
       * is always valid.
       *
       * @param indices the multi-dimensional array index.
       * @returns a reference to the pixel value.
       */
      value_type&
      at(const indices_type& indices)
      {
        return (*array())(indices);
      }

      /**
       * Get the pixel value at an index.
       *
       * @note If the index is out of bounds, an assertion failure
       * will immediately abort the program, so take care to ensure it
       * is always valid.
       *
       * @param indices the multi-dimensional array index.
       * @returns a constant reference to the pixel value.
       */
      const value_type&
      at(const indices_type& indices) const
      {
        return (*array())(indices);
      }

      /**
       * Read raw pixel data from a stream in physical storage order.
       *
       * Note that the pixels will be read in the physical storage
       * order.  This will typically be a contiguous read, but this is
       * not guaranteed.  The current implementation iterates over
       * each pixel and so may be slower than strictly necessary.
       *
       * @param stream the stream to read from.
       */
      template<class charT, class traits>
      inline void
      read(std::basic_istream<charT,traits>& stream)
      {
        const size_type * extents = shape();
        const storage_order_type order = storage_order();
        indices_type idx;

        /**
         * This initial naïve implementation likely has much room for
         * optimisation and generalisation to different dimension
         * numbers for subsetting.
         */
        for (idx[order.ordering(8)] = order.ascending(8) ? 0 : extents[order.ordering(8)] - 1;
             order.ascending(8) ? idx[order.ordering(8)] < extents[order.ordering(8)] : idx[order.ordering(8)] >= 0;
             order.ascending(8) ? ++idx[order.ordering(8)] : --idx[order.ordering(8)])
          for (idx[order.ordering(7)] = order.ascending(7) ? 0 : extents[order.ordering(7)] - 1;
               order.ascending(7) ? idx[order.ordering(7)] < extents[order.ordering(7)] : idx[order.ordering(7)] >= 0;
               order.ascending(7) ? ++idx[order.ordering(7)] : --idx[order.ordering(7)])
            for (idx[order.ordering(6)] = order.ascending(6) ? 0 : extents[order.ordering(6)] - 1;
                 order.ascending(6) ? idx[order.ordering(6)] < extents[order.ordering(6)] : idx[order.ordering(6)] >= 0;
                 order.ascending(6) ? ++idx[order.ordering(6)] : --idx[order.ordering(6)])
              for (idx[order.ordering(5)] = order.ascending(5) ? 0 : extents[order.ordering(5)] - 1;
                   order.ascending(5) ? idx[order.ordering(5)] < extents[order.ordering(5)] : idx[order.ordering(5)] >= 0;
                   order.ascending(5) ? ++idx[order.ordering(5)] : --idx[order.ordering(5)])
                for (idx[order.ordering(4)] = order.ascending(4) ? 0 : extents[order.ordering(4)] - 1;
                     order.ascending(4) ? idx[order.ordering(4)] < extents[order.ordering(4)] : idx[order.ordering(4)] >= 0;
                     order.ascending(4) ? ++idx[order.ordering(4)] : --idx[order.ordering(4)])
                  for (idx[order.ordering(3)] = order.ascending(3) ? 0 : extents[order.ordering(3)] - 1;
                       order.ascending(3) ? idx[order.ordering(3)] < extents[order.ordering(3)] : idx[order.ordering(3)] >= 0;
                       order.ascending(3) ? ++idx[order.ordering(3)] : --idx[order.ordering(3)])
                    for (idx[order.ordering(2)] = order.ascending(2) ? 0 : extents[order.ordering(2)] - 1;
                         order.ascending(2) ? idx[order.ordering(2)] < extents[order.ordering(2)] : idx[order.ordering(2)] >= 0;
                         order.ascending(2) ? ++idx[order.ordering(2)] : --idx[order.ordering(2)])
                      for (idx[order.ordering(1)] = order.ascending(1) ? 0 : extents[order.ordering(1)] - 1;
                           order.ascending(1) ? idx[order.ordering(1)] < extents[order.ordering(1)] : idx[order.ordering(1)] >= 0;
                           order.ascending(1) ? ++idx[order.ordering(1)] : --idx[order.ordering(1)])
                        for (idx[order.ordering(0)] = order.ascending(0) ? 0 : extents[order.ordering(0)] - 1;
                             order.ascending(0) ? idx[order.ordering(0)] < extents[order.ordering(0)] : idx[order.ordering(0)] >= 0;
                             order.ascending(0) ? ++idx[order.ordering(0)] : --idx[order.ordering(0)])
                          {
                            stream.read(reinterpret_cast<char *>(&at(idx)), sizeof(value_type));
                          }
      }

      /**
       * Write raw pixel data to a stream in physical storage order.
       *
       * Note that the pixels will be written in the physical storage
       * order.  This will typically be a contiguous read, but this is
       * not guaranteed.  The current implementation iterates over
       * each pixel and so may be slower than strictly necessary.
       *
       * @param stream the stream to write to.
       */
      template<class charT, class traits>
      inline void
      write(std::basic_ostream<charT,traits>& stream) const
      {
        const size_type * extents = shape();
        const storage_order_type order = storage_order();
        indices_type idx;

        /**
         * This initial naïve implementation likely has much room for
         * optimisation and generalisation to different dimension
         * numbers for subsetting.
         */
        for (idx[order.ordering(8)] = order.ascending(8) ? 0 : extents[order.ordering(8)] - 1;
             order.ascending(8) ? idx[order.ordering(8)] < extents[order.ordering(8)] : idx[order.ordering(8)] >= 0;
             order.ascending(8) ? ++idx[order.ordering(8)] : --idx[order.ordering(8)])
          for (idx[order.ordering(7)] = order.ascending(7) ? 0 : extents[order.ordering(7)] - 1;
               order.ascending(7) ? idx[order.ordering(7)] < extents[order.ordering(7)] : idx[order.ordering(7)] >= 0;
               order.ascending(7) ? ++idx[order.ordering(7)] : --idx[order.ordering(7)])
            for (idx[order.ordering(6)] = order.ascending(6) ? 0 : extents[order.ordering(6)] - 1;
                 order.ascending(6) ? idx[order.ordering(6)] < extents[order.ordering(6)] : idx[order.ordering(6)] >= 0;
                 order.ascending(6) ? ++idx[order.ordering(6)] : --idx[order.ordering(6)])
              for (idx[order.ordering(5)] = order.ascending(5) ? 0 : extents[order.ordering(5)] - 1;
                   order.ascending(5) ? idx[order.ordering(5)] < extents[order.ordering(5)] : idx[order.ordering(5)] >= 0;
                   order.ascending(5) ? ++idx[order.ordering(5)] : --idx[order.ordering(5)])
                for (idx[order.ordering(4)] = order.ascending(4) ? 0 : extents[order.ordering(4)] - 1;
                     order.ascending(4) ? idx[order.ordering(4)] < extents[order.ordering(4)] : idx[order.ordering(4)] >= 0;
                     order.ascending(4) ? ++idx[order.ordering(4)] : --idx[order.ordering(4)])
                  for (idx[order.ordering(3)] = order.ascending(3) ? 0 : extents[order.ordering(3)] - 1;
                       order.ascending(3) ? idx[order.ordering(3)] < extents[order.ordering(3)] : idx[order.ordering(3)] >= 0;
                       order.ascending(3) ? ++idx[order.ordering(3)] : --idx[order.ordering(3)])
                    for (idx[order.ordering(2)] = order.ascending(2) ? 0 : extents[order.ordering(2)] - 1;
                         order.ascending(2) ? idx[order.ordering(2)] < extents[order.ordering(2)] : idx[order.ordering(2)] >= 0;
                         order.ascending(2) ? ++idx[order.ordering(2)] : --idx[order.ordering(2)])
                      for (idx[order.ordering(1)] = order.ascending(1) ? 0 : extents[order.ordering(1)] - 1;
                           order.ascending(1) ? idx[order.ordering(1)] < extents[order.ordering(1)] : idx[order.ordering(1)] >= 0;
                           order.ascending(1) ? ++idx[order.ordering(1)] : --idx[order.ordering(1)])
                        for (idx[order.ordering(0)] = order.ascending(0) ? 0 : extents[order.ordering(0)] - 1;
                             order.ascending(0) ? idx[order.ordering(0)] < extents[order.ordering(0)] : idx[order.ordering(0)] >= 0;
                             order.ascending(0) ? ++idx[order.ordering(0)] : --idx[order.ordering(0)])
                          {
                            stream.write(reinterpret_cast<const char *>(&at(idx)), sizeof(value_type));
                          }
      }

    private:
      /**
       * Multi-dimensional pixel array.  This may be a view on the
       * accompanying @c data member, or an external buffer.  If
       * using an external buffer, the lifetime of the buffer must
       * exceed that of this class instance.
       *
       * Note that a @c shared_ptr is used here in order that a @c
       * multi_array or @c multi_array_ref may be used
       * interchangeably, and also so that it is possible to resize
       * the array by replacing the existing array.  It also permits
       * efficient shallow copying in the absence of a C++11 move
       * constructor for @c MultiArray types.
       */
      std::shared_ptr<array_ref_type> multiarray;
    };

  }
}

namespace std
{

  /**
   * Set PixelBuffer from input stream.
   *
   * @param is the input stream.
   * @param buf the PixelBuffer to set.
   * @returns the input stream.
   */
  template<typename T, class charT, class traits>
  inline std::basic_istream<charT,traits>&
  operator>> (std::basic_istream<charT,traits>& is,
              ::ome::bioformats::PixelBuffer<T>& buf)
  {
    buf.read(is);
    return is;
  }

  /**
   * Output PixelBuffer to output stream.
   *
   * @param os the output stream.
   * @param buf the PixelBuffer to output.
   * @returns the output stream.
   */
  template<typename T, class charT, class traits>
  inline std::basic_ostream<charT,traits>&
  operator<< (std::basic_ostream<charT,traits>& os,
              const ::ome::bioformats::PixelBuffer<T>& buf)
  {
    buf.write(os);
    return os;
  }

}

#endif // OME_BIOFORMATS_PIXELBUFFER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
