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
#include <ome/compat/variant.h>

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
      typedef std::array<boost::multi_array_types::index, PixelBufferBase::dimensions> indices_type;

      /// Storage ordering type for controlling pixel memory layout.
      typedef boost::general_storage_order<dimensions> storage_order_type;

      /// Extent range type.
      typedef boost::detail::multi_array::extent_gen<dimensions> range_type;

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

      static storage_order_type
      make_storage_order(ome::xml::model::enums::DimensionOrder order,
                         bool                                   interleaved);

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
       * Construct from extents (internal storage).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param extents the extent of each dimension.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       */
      template<class ExtentList>
      explicit
      PixelBuffer(const ExtentList&                   extents,
                  const storage_order_type&           storage,
                  ::ome::xml::model::enums::PixelType pixeltype,
                  EndianType                          endiantype):
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
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       */
      template<class ExtentList>
      explicit
      PixelBuffer(value_type                          *pixeldata,
                  const ExtentList&                    extents,
                  const storage_order_type&            storage,
                  ::ome::xml::model::enums::PixelType  pixeltype,
                  EndianType                           endiantype):
        PixelBufferBase(pixeltype, endiantype),
        multiarray(new array_ref_type(pixeldata, extents, storage))
      {}

      /**
       * Construct from ranges (internal storage).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param range the range of each dimension.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       */
      explicit
      PixelBuffer(const range_type&                   range,
                  const storage_order_type&           storage,
                  ::ome::xml::model::enums::PixelType pixeltype,
                  EndianType                          endiantype):
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
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       */
      explicit
      PixelBuffer(value_type                          *pixeldata,
                  const range_type&                    range,
                  const storage_order_type&            storage,
                  ::ome::xml::model::enums::PixelType  pixeltype,
                  EndianType                           endiantype):
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

      /**
       * Get the pixel data.
       *
       * @returns the multidimensional pixel data array.
       */
      std::shared_ptr<array_ref_type>
      array()
      {
        return multiarray;
      }

      /**
       * Get the pixel data.
       *
       * @returns the multidimensional pixel data array.
       */
      const std::shared_ptr<array_ref_type>
      array() const
      {
        return multiarray;
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
      boost::multi_array_types::size_type
      num_elements() const
      {
        return array()->num_elements();
      }

      /**
       * Get the number of dimensions in the multi-dimensional array.
       */
      boost::multi_array_types::size_type
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
      const boost::multi_array_types::size_type *
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

      const value_type *
      origin() const
      {
        return array()->origin();
      }

      const storage_order_type&
      storage_order() const
      {
        return array()->storage_order();
      }

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
       */
      template<class charT, class traits>
      inline void
      read(std::basic_istream<charT,traits>& stream)
      {
        const boost::multi_array_types::size_type * extents = shape();
        const storage_order_type order = storage_order();
        indices_type idx;

        /**
         * This initial naïve implementation likely has much room for
         * optimisation and generalisation to different dimension
         * numbers for subsetting.
         */
        for (idx[0] = order.ascending(0) ? 0 : extents[order.ordering(0)] - 1;
             order.ascending(0) ? idx[0] < extents[order.ordering(0)] : idx[0] >= 0;
             order.ascending(0) ? ++idx[0] : --idx[0])
          for (idx[1] = order.ascending(1) ? 0 : extents[order.ordering(1)] - 1;
               order.ascending(1) ? idx[1] < extents[order.ordering(1)] : idx[1] >= 0;
               order.ascending(1) ? ++idx[1] : --idx[1])
            for (idx[2] = order.ascending(2) ? 0 : extents[order.ordering(2)] - 1;
                 order.ascending(2) ? idx[2] < extents[order.ordering(2)] : idx[2] >= 0;
                 order.ascending(2) ? ++idx[2] : --idx[2])
              for (idx[3] = order.ascending(3) ? 0 : extents[order.ordering(3)] - 1;
                   order.ascending(3) ? idx[3] < extents[order.ordering(3)] : idx[3] >= 0;
                   order.ascending(3) ? ++idx[3] : --idx[3])
                for (idx[4] = order.ascending(4) ? 0 : extents[order.ordering(4)] - 1;
                     order.ascending(4) ? idx[4] < extents[order.ordering(4)] : idx[4] >= 0;
                     order.ascending(4) ? ++idx[4] : --idx[4])
                  for (idx[5] = order.ascending(5) ? 0 : extents[order.ordering(5)] - 1;
                       order.ascending(5) ? idx[5] < extents[order.ordering(5)] : idx[5] >= 0;
                       order.ascending(5) ? ++idx[5] : --idx[5])
                    for (idx[6] = order.ascending(6) ? 0 : extents[order.ordering(6)] - 1;
                         order.ascending(6) ? idx[6] < extents[order.ordering(6)] : idx[6] >= 0;
                         order.ascending(6) ? ++idx[6] : --idx[6])
                      for (idx[7] = order.ascending(7) ? 0 : extents[order.ordering(7)] - 1;
                           order.ascending(7) ? idx[7] < extents[order.ordering(7)] : idx[7] >= 0;
                           order.ascending(7) ? ++idx[7] : --idx[7])
                        for (idx[8] = order.ascending(8) ? 0 : extents[order.ordering(8)] - 1;
                             order.ascending(8) ? idx[8] < extents[order.ordering(8)] : idx[8] >= 0;
                             order.ascending(8) ? ++idx[8] : --idx[8])
                          {
                            stream.read(reinterpret_cast<char *>(&at(idx)), sizeof(value_type));
                          }
      }

      template<class charT, class traits>
      inline void
      write(std::basic_ostream<charT,traits>& stream) const
      {
        const boost::multi_array_types::size_type * extents = shape();
        const storage_order_type order = storage_order();
        indices_type idx;

        /**
         * This initial naïve implementation likely has much room for
         * optimisation and generalisation to different dimension
         * numbers for subsetting.
         */
        for (idx[0] = order.ascending(0) ? 0 : extents[order.ordering(0)] - 1;
             order.ascending(0) ? idx[0] < extents[order.ordering(0)] : idx[0] >= 0;
             order.ascending(0) ? ++idx[0] : --idx[0])
          for (idx[1] = order.ascending(1) ? 0 : extents[order.ordering(1)] - 1;
               order.ascending(1) ? idx[1] < extents[order.ordering(1)] : idx[1] >= 0;
               order.ascending(1) ? ++idx[1] : --idx[1])
            for (idx[2] = order.ascending(2) ? 0 : extents[order.ordering(2)] - 1;
                 order.ascending(2) ? idx[2] < extents[order.ordering(2)] : idx[2] >= 0;
                 order.ascending(2) ? ++idx[2] : --idx[2])
              for (idx[3] = order.ascending(3) ? 0 : extents[order.ordering(3)] - 1;
                   order.ascending(3) ? idx[3] < extents[order.ordering(3)] : idx[3] >= 0;
                   order.ascending(3) ? ++idx[3] : --idx[3])
                for (idx[4] = order.ascending(4) ? 0 : extents[order.ordering(4)] - 1;
                     order.ascending(4) ? idx[4] < extents[order.ordering(4)] : idx[4] >= 0;
                     order.ascending(4) ? ++idx[4] : --idx[4])
                  for (idx[5] = order.ascending(5) ? 0 : extents[order.ordering(5)] - 1;
                       order.ascending(5) ? idx[5] < extents[order.ordering(5)] : idx[5] >= 0;
                       order.ascending(5) ? ++idx[5] : --idx[5])
                    for (idx[6] = order.ascending(6) ? 0 : extents[order.ordering(6)] - 1;
                         order.ascending(6) ? idx[6] < extents[order.ordering(6)] : idx[6] >= 0;
                         order.ascending(6) ? ++idx[6] : --idx[6])
                      for (idx[7] = order.ascending(7) ? 0 : extents[order.ordering(7)] - 1;
                           order.ascending(7) ? idx[7] < extents[order.ordering(7)] : idx[7] >= 0;
                           order.ascending(7) ? ++idx[7] : --idx[7])
                        for (idx[8] = order.ascending(8) ? 0 : extents[order.ordering(8)] - 1;
                             order.ascending(8) ? idx[8] < extents[order.ordering(8)] : idx[8] >= 0;
                             order.ascending(8) ? ++idx[8] : --idx[8])
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

    /**
     * Buffer for all pixel types.
     *
     * The purpose of this class is to allow transfer of pixel data of
     * any type and endianness, and of any dimensionality.
     *
     * This class uses Boost.Variant to support specialisations of
     * PixelBuffer for all combinations of pixel type and endianness.
     *
     * While direct access to the pixel data is possible using this
     * class, please be aware that this will not provide high
     * performance.  For high performance access to the pixel data,
     * use of a @c boost::static_visitor is recommended.  This has the
     * benefit of generalising the algorithm to operate on all
     * PixelBuffer types, as well as allowing special casing for
     * particular types (e.g. integer vs. float, signed vs. unsigned,
     * simple vs. complex, or any other distinction which affects the
     * algorithm).  This will also allow subsetting of the data if
     * required, again for all pixel types with special casing being
     * possible.
     */
    class VariantPixelBuffer
    {
    private:
      /*
       * The following series of typedefs may appear a little
       * complicated, and perhaps unnecessary, but they do have a
       * purpose.  They exist to work around pre-C++11 compiler
       * limitations (lack of variadic templates), primarily a limit
       * to the maximum number of types which may be used with
       * boost::variant.  To exceed this limit (minimum guaranteed is
       * 10), boost::mpl sequences are used to define the variant
       * types.  These also have length limits, so the type list is
       * built up by defining separate type sequences, then
       * concatenating them, and transforming them to provide list
       * variants.  Note that none of this is code per se; it's all
       * compile-time template expansion which evaluates to a list of
       * permitted types.
       */

      /// Integer pixel types.
      typedef boost::mpl::vector<PixelProperties< ::ome::xml::model::enums::PixelType::INT8>,
                                 PixelProperties< ::ome::xml::model::enums::PixelType::INT16>,
                                 PixelProperties< ::ome::xml::model::enums::PixelType::INT32>,
                                 PixelProperties< ::ome::xml::model::enums::PixelType::UINT8>,
                                 PixelProperties< ::ome::xml::model::enums::PixelType::UINT16>,
                                 PixelProperties< ::ome::xml::model::enums::PixelType::UINT32>,
                                 PixelProperties< ::ome::xml::model::enums::PixelType::BIT> > integer_pixel_types;

      /// Floating-point pixel types.
      typedef boost::mpl::vector< PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT>,
                                  PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>,
                                  PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>,
                                  PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX> > float_pixel_types;

      /// Aggregate view of all numeric types.
      typedef boost::mpl::joint_view<integer_pixel_types,
                                     float_pixel_types>::type basic_pixel_types_view;

      /// Convert T into a big-endian buffer.
      template<typename T>
      struct make_big_endian_buffer
      {
        /// Buffer type.
        typedef std::shared_ptr<PixelBuffer<typename T::big_type> > type;
      };

      /// Convert T into a little-endian buffer.
      template<typename T>
      struct make_little_endian_buffer
      {
        /// Buffer type.
        typedef std::shared_ptr<PixelBuffer<typename T::little_type> > type;
      };

      /// Convert T into a native-endian buffer.
      template<typename T>
      struct make_native_endian_buffer
      {
        /// Buffer type.
        typedef std::shared_ptr<PixelBuffer<typename T::native_type> > type;
      };

      /// Aggregate view of all big-endian buffer types.
      typedef boost::mpl::transform_view<basic_pixel_types_view, make_big_endian_buffer<boost::mpl::_1> >::type big_endian_pixel_buffer_types_view;

      /// Aggregate view of all little-endian buffer types.
      typedef boost::mpl::transform_view<basic_pixel_types_view, make_little_endian_buffer<boost::mpl::_1> >::type little_endian_pixel_buffer_types_view;

      /// Aggregate view of all native-endian buffer types.
      typedef boost::mpl::transform_view<basic_pixel_types_view, make_native_endian_buffer<boost::mpl::_1> >::type native_endian_pixel_buffer_types_view;

      /// Aggregate view of all big-endian and little-endian buffer types.
      typedef boost::mpl::joint_view<big_endian_pixel_buffer_types_view, little_endian_pixel_buffer_types_view> big_little_endian_pixel_buffer_types_view;

      /// Aggregate view of all buffer types.
      typedef boost::mpl::joint_view<native_endian_pixel_buffer_types_view, big_little_endian_pixel_buffer_types_view> all_endian_pixel_buffer_types_view;

      /// Empty vector placeholder.
      typedef boost::mpl::vector<> empty_types;

      /// List of all pixel buffer types (big, little and native endian).
      typedef boost::mpl::insert_range<empty_types, boost::mpl::end<empty_types>::type, all_endian_pixel_buffer_types_view>::type pixel_buffer_types;

    public:
      /// Buffer type, allowing assignment of all buffer types.
      typedef boost::make_variant_over<pixel_buffer_types>::type variant_buffer_type;

      /// Raw pixel type used in public interfaces.
      typedef PixelProperties< ::ome::xml::model::enums::PixelType::UINT8>::native_type raw_type;

      /// Type used to index all dimensions in public interfaces.
      typedef std::array<boost::multi_array_types::index, PixelBufferBase::dimensions> indices_type;

      /// Storage ordering type for controlling pixel memory layout.
      typedef PixelBufferBase::storage_order_type storage_order_type;

      /// Extent range type.
      typedef PixelBufferBase::range_type range_type;

    public:
      /**
       * Default constructor.
       *
       * This constructs a pixel buffer of unspecified type, of size 1
       * in all dimensions.  The desired type and size should be set
       * after construction.
       */
      explicit
      VariantPixelBuffer():
        buffer(createBuffer(boost::extents[1][1][1][1][1][1][1][1][1]))
      {
      }

      /**
       * Construct from extents (internal storage).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param extents the extent of each dimension.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       */
      template<class ExtentList>
      explicit
      VariantPixelBuffer(const ExtentList&                   extents,
                         ::ome::xml::model::enums::PixelType pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                         EndianType                          endiantype = NATIVE,
                         const storage_order_type&           storage = PixelBufferBase::default_storage_order()):
        buffer(createBuffer(extents, pixeltype, endiantype, storage))
      {
      }

      /**
       * Construct from ranges (internal storage).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param range the range of each dimension.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       */
      explicit
      VariantPixelBuffer(const range_type&                   range,
                         ::ome::xml::model::enums::PixelType pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                         EndianType                          endiantype = NATIVE,
                         const storage_order_type&           storage = PixelBufferBase::default_storage_order()):
        buffer(createBuffer(range, pixeltype, endiantype, storage))
      {
      }

      /**
       * Copy constructor.
       *
       * Note that due to the use of shared pointers this only
       * performs a shallow copy.
       *
       * @param buffer the buffer to copy.
       */
       explicit
      VariantPixelBuffer(const VariantPixelBuffer& buffer);

      /**
       * Construct from existing pixel buffer.  Use for referencing external data.
       *
       * @param buffer the buffer to contain.
       */
      template<typename T>
      explicit
      VariantPixelBuffer(const PixelBuffer<T>& buffer):
        buffer(buffer)
      {
      }

      /// Destructor.
      virtual
      ~VariantPixelBuffer()
      {}

      /**
       * Get a reference to the variant buffer.
       *
       * @returns a reference to the buffer.
       */
      variant_buffer_type&
      vbuffer()
      {
        return buffer;
      }

      /**
       * Get a reference to the variant buffer.
       *
       * @returns a reference to the buffer.
       */
      const variant_buffer_type&
      vbuffer() const
      {
        return buffer;
      }

    protected:
      /**
       * Create buffer from extents (helper).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param extents the extent of each dimension.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @returns the new buffer contained in a variant.
       */
      template<class T, class ExtentList>
      static variant_buffer_type
      makeBuffer(const ExtentList&                   extents,
                 const storage_order_type&           storage,
                 ::ome::xml::model::enums::PixelType pixeltype,
                 EndianType                          endiantype)
      {
        return std::shared_ptr<PixelBuffer<T> >(new PixelBuffer<T>(extents, storage, pixeltype, endiantype));
      }

      /**
       * Create buffer from ranges (helper).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param range the range of each dimension.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @returns the new buffer contained in a variant.
       */
      template<class T>
      static variant_buffer_type
      makeBuffer(const range_type&                   range,
                 const storage_order_type&           storage,
                 ::ome::xml::model::enums::PixelType pixeltype,
                 EndianType                          endiantype)
      {
        return std::shared_ptr<PixelBuffer<T> >(new PixelBuffer<T>(range, storage, pixeltype, endiantype));
      }

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

      /**
       * Create buffer from extents (internal storage).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param extents the extent of each dimension.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @returns the new buffer contained in a variant.
       */
      template<class ExtentList>
      static variant_buffer_type
      createBuffer(const ExtentList&                   extents,
                   ::ome::xml::model::enums::PixelType pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                   EndianType                          endiantype = NATIVE,
                   const storage_order_type&           storage = PixelBufferBase::default_storage_order())
      {
        variant_buffer_type buf;

        switch(pixeltype)
          {
          case ::ome::xml::model::enums::PixelType::INT8:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT8, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT8, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT8, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::INT16:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT16, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT16, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT16, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::INT32:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT32, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT32, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT32, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::UINT8:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT8, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT8, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT8, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::UINT16:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT16, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT16, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT16, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case :: ome::xml::model::enums::PixelType::UINT32:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT32, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT32, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT32, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::FLOAT:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::FLOAT, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::FLOAT, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::FLOAT, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::DOUBLE:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLE, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLE, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLE, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::BIT:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::BIT, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::BIT, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::BIT, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::COMPLEX:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX, BIG>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX, LITTLE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX, NATIVE>::type> >(extents, storage, pixeltype, endiantype);
                break;
              }
            break;
          }

        return buf;
      }

      /**
       * Create buffer from ranges (helper).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param range the range of each dimension.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       * @returns the new buffer contained in a variant.
       */
      static variant_buffer_type
      createBuffer(const range_type&                   range,
                   ::ome::xml::model::enums::PixelType pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                   EndianType                          endiantype = NATIVE,
                   const storage_order_type&           storage = PixelBufferBase::default_storage_order())
      {
        variant_buffer_type buf;

        switch(pixeltype)
          {
          case ::ome::xml::model::enums::PixelType::INT8:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT8, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT8, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT8, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::INT16:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT16, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT16, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT16, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::INT32:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT32, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT32, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT32, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::UINT8:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT8, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT8, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT8, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::UINT16:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT16, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT16, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT16, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case :: ome::xml::model::enums::PixelType::UINT32:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT32, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT32, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT32, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::FLOAT:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::FLOAT, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::FLOAT, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::FLOAT, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::DOUBLE:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLE, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLE, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLE, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::BIT:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::BIT, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::BIT, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::BIT, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::COMPLEX:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
            switch(endiantype)
              {
              case BIG:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX, BIG>::type>(range, storage, pixeltype, endiantype);
                break;
              case LITTLE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX, LITTLE>::type>(range, storage, pixeltype, endiantype);
                break;
              case NATIVE:
                buf = makeBuffer<PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX, NATIVE>::type>(range, storage, pixeltype, endiantype);
                break;
              }
            break;
          }

        return buf;
      }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

    public:
      /**
       * Set the buffer from extents (helper).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param extents the extent of each dimension.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       */
      template<class T, class ExtentList>
      void
      setBuffer(const ExtentList&                   extents,
                ::ome::xml::model::enums::PixelType pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                EndianType                          endiantype = NATIVE,
                const storage_order_type&           storage = PixelBufferBase::default_storage_order())
      {
        buffer = createBuffer(extents, pixeltype, endiantype, storage);
      }

      /**
       * Set the buffer from ranges (internal storage).
       *
       * Storage for the buffer will be allocated internally.
       *
       * @param range the range of each dimension.
       * @param pixeltype the pixel type to store.
       * @param endiantype the required endianness of the pixel type.
       * @param storage the storage ordering, defaulting to C array
       * storage ordering.
       */
      void
      setBuffer(const range_type&                   range,
                ::ome::xml::model::enums::PixelType pixeltype = ::ome::xml::model::enums::PixelType::UINT8,
                EndianType                          endiantype = NATIVE,
                const storage_order_type&           storage = PixelBufferBase::default_storage_order())
      {
        buffer = createBuffer(range, pixeltype, endiantype, storage);
      }

      /**
       * Get the number of pixel elements in the multi-dimensional array.
       */
      boost::multi_array_types::size_type
      num_elements() const;

      /**
       * Get the number of dimensions in the multi-dimensional array.
       */
      boost::multi_array_types::size_type
      num_dimensions() const;

      /**
       * Get the shape of the multi-dimensional array.
       *
       * The shape is the extent of each array dimension.
       *
       * @returns an array of extents (size is the dimension size).
       */
      const boost::multi_array_types::size_type *
      shape() const;

      /**
       * Get the strides of the multi-dimensional array.
       *
       * The strides are the stride associated with each array dimension.
       *
       * @returns an array of strides (size is the dimension size).
       */
      const boost::multi_array_types::index *
      strides() const;

      /**
       * Get the index bases of the multi-dimensional array.
       *
       * The index bases are the numeric index of the first element
       * for each array dimension of the multi-dimensional array.
       *
       * @returns an array of index bases (size is the dimension size).
       */
      const boost::multi_array_types::index *
      index_bases() const;

      template <typename T>
      const T *
      origin() const;

      const storage_order_type&
      storage_order() const;

      /**
       * Get the type of pixels stored in the buffer.
       */
      ::ome::xml::model::enums::PixelType
      pixelType() const;

      /**
       * Get the endianness of the pixel type stored in the buffer.
       */
      EndianType
      endianType() const;

      /**
       * Get raw buffered data.
       *
       * @returns a pointer to the buffer start address.
       */
      raw_type *
      data();

      /**
       * Get raw buffered data.
       *
       * @returns a pointer to the buffer start address.
       */
      template<typename T>
      T *
      data();

      /**
       * Compare a pixel buffer for equality.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if equal, @c false if not equal.
       */
      bool
      operator == (const VariantPixelBuffer& rhs) const;

      /**
       * Compare a pixel buffer for inequality.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator != (const VariantPixelBuffer& rhs) const;

      template <typename InputIterator>
      void
      assign(InputIterator begin,
             InputIterator end);

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
      template<typename T>
      T&
      at(const indices_type& indices)
      {
        std::shared_ptr<PixelBuffer<T> >& r
          = boost::get<std::shared_ptr<PixelBuffer<T> > >(buffer);
        if (!r)
          throw std::runtime_error("Null pixel type");
        return (*r->array())(indices);
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
      template<typename T>
      const T&
      at(const indices_type& indices) const
      {
        const std::shared_ptr<PixelBuffer<T> >& r
          = boost::get<std::shared_ptr<PixelBuffer<T> > >(buffer);
        if (!r)
          throw std::runtime_error("Null pixel type");
        return (*r->array())(indices);
      }

    template<class charT, class traits>
    inline void
    read(std::basic_istream<charT,traits>& stream);

    template<class charT, class traits>
    inline void
    write(std::basic_ostream<charT,traits>& stream) const;

    protected:
      /// Pixel storage.
      variant_buffer_type buffer;
    };

    namespace detail
    {

      /// Find a PixelBuffer data array of a specific pixel type.
      template<typename T>
      struct PixelBufferArrayVisitor : public boost::static_visitor<>
      {
        /**
         * PixelBuffer of correct type.
         *
         * @param v the value to find.
         * @returns a pointer to the data array.
         * @throws if the PixelBuffer is null.
         */
        T *
        operator() (T& v)
        {
          if (!v)
            throw std::runtime_error("Null pixel type");
          return v->array.data();
        }

        /**
         * PixelBuffer of incorrect type.
         *
         * @throws if used.
         * @returns a pointer to the data array.
         */
        template <typename U>
        T *
        operator() (U& /* v */) const
        {
          throw std::runtime_error("Unsupported pixel type conversion for buffer");
        }
      };

      /// Assign a PixelBuffer from an input iterator.
      template <typename InputIterator>
      struct PixelBufferAssignVisitor : public boost::static_visitor<>
      {
        /// Input start.
        InputIterator begin;
        /// Input end.
        InputIterator end;

        /**
         * Constructor.
         *
         * @param begin the start of input.
         * @param end the end of input.
         */
        PixelBufferAssignVisitor(InputIterator begin, InputIterator end):
          begin(begin), end(end)
        {}

        /**
         * PixelBuffer of correct type.
         *
         * @param v the PixelBuffer to assign to.
         * @throws if the PixelBuffer is null or the PixelBuffer's data array is null.
         */
        void
        operator() (std::shared_ptr<PixelBuffer<typename std::iterator_traits<InputIterator>::value_type> >& v) const
        {
          if (!v || !v->array())
            throw std::runtime_error("Null pixel type");
          v->array()->assign(begin, end);
        }

        /**
         * PixelBuffer of incorrect type.
         *
         * @throws if used.
         */
        template <typename T>
        void
        operator() (T& /* v */) const
        {
          throw std::runtime_error("Unsupported pixel type conversion for assignment");
        }
      };

      /// Obtain the origin of a PixelBuffer.
      template <typename T>
      struct PixelBufferOriginVisitor : public boost::static_visitor<T *>
      {
        /**
         * PixelBuffer of correct type.
         *
         * @param v the PixelBuffer from which to obtain the origin.
         * @throws if the PixelBuffer is null or the PixelBuffer's data array is null.
         */
        void
        operator() (std::shared_ptr<PixelBuffer<T> >& v) const
        {
          if (!v || !v->array())
            throw std::runtime_error("Null pixel type");
          return v->array()->origin();
        }

        /**
         * PixelBuffer of incorrect type.
         *
         * @throws if used.
         */
        template <typename U>
        void
        operator() (U& /* v */) const
        {
          throw std::runtime_error("Unsupported pixel type conversion for origin");
        }
      };

      /// Read data into a PixelBuffer.
      template<class charT, class traits>
      struct PixelBufferReadVisitor : public boost::static_visitor<>
      {
        /// The input stream.
        std::basic_istream<charT,traits>& stream;

        /**
         * Constructor.
         *
         * @param stream the input stream.
         */
        PixelBufferReadVisitor(std::basic_istream<charT,traits>& stream):
          stream(stream)
        {}

        /**
         * Read data into specific PixelBuffer type.
         *
         * @param v the PixelBuffer to use.
         */
        template <typename T>
        void
        operator() (T& v) const
        {
          if (!v || !v->array())
            throw std::runtime_error("Null pixel type");
          v->read(stream);
        }
      };

      /// Write data from a PixelBuffer.
      template<class charT, class traits>
      struct PixelBufferWriteVisitor : public boost::static_visitor<>
      {
        /// The output stream.
        std::basic_ostream<charT,traits>& stream;

        /**
         * Constructor.
         *
         * @param stream the output stream.
         */
        PixelBufferWriteVisitor(std::basic_ostream<charT,traits>& stream):
          stream(stream)
        {}

        /**
         * Write data from specific PixelBuffer type.
         *
         * @param v the PixelBuffer to use.
         */
        template <typename T>
        void
        operator() (const T& v) const
        {
          if (!v || !v->array())
            throw std::runtime_error("Null pixel type");
          v->write(stream);
        }
      };

    }

    /**
     * Get a pointer to the underlying raw data.
     *
     * @returns a pointer to the data.
     * @throws if the PixelBuffer is null.
     */
    template<typename T>
    inline T *
    VariantPixelBuffer::data()
    {
      detail::PixelBufferArrayVisitor<T> v;
      return boost::apply_visitor(v, buffer);
    }

    template <typename T>
    inline const T *
    VariantPixelBuffer::origin() const
    {
      detail::PixelBufferOriginVisitor<T> v;
      return boost::apply_visitor(v, buffer);
    }

    /**
     * Assign all pixel values from an input iterator.
     *
     * @param begin the start of input.
     * @param end the end of input.
     * @throws if the PixelBuffer is null or the PixelBuffer's data array is null.
     */
    template <typename InputIterator>
    inline void
    VariantPixelBuffer::assign(InputIterator begin,
                               InputIterator end)
    {
      detail::PixelBufferAssignVisitor<InputIterator> v(begin, end);
      boost::apply_visitor(v, buffer);
    }

    template<class charT, class traits>
    inline void
    VariantPixelBuffer::read(std::basic_istream<charT,traits>& stream)
    {
      detail::PixelBufferReadVisitor<charT, traits> v(stream);
      boost::apply_visitor(v, buffer);
    }

    template<class charT, class traits>
    inline void
    VariantPixelBuffer::write(std::basic_ostream<charT,traits>& stream) const
    {
      detail::PixelBufferWriteVisitor<charT, traits> v(stream);
      boost::apply_visitor(v, buffer);
    }

  }
}

namespace std
{

    /**
     * Set VariantPixelBuffer from input stream.
     *
     * @param is the input stream.
     * @param buf the VariantPixelBuffer to set.
     * @returns the input stream.
     */
    template<class charT, class traits>
    inline std::basic_istream<charT,traits>&
    operator>> (std::basic_istream<charT,traits>& is,
                ::ome::bioformats::VariantPixelBuffer& buf)
    {
      buf.read(is);
      return is;
    }

    /**
     * Output VariantPixelBuffer to output stream.
     *
     * @param os the output stream.
     * @param buf the VariantPixelBuffer to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
    inline std::basic_ostream<charT,traits>&
    operator<< (std::basic_ostream<charT,traits>& os,
                const ::ome::bioformats::VariantPixelBuffer& buf)
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
