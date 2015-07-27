/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2015 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_DIMENSION_H
#define OME_BIOFORMATS_DIMENSION_H

#include <algorithm>
#include <cstddef>
#include <stdexcept>
#include <string>
#include <vector>
#include <map>
#include <iostream>

#include <boost/format.hpp>

#include <ome/compat/cstdint.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Dimension specification.
     *
     * A description of a single dimension, at a minimum the name of
     * the dimension and its extent (size).  It may also include a
     * subrange within the extent, used as a window to address a
     * selected subrange of the full range.
     *
     * Type definitions are for use in related classes and functions.
     */
    struct Dimension
    {
      /// Size type for dimension extents.
      typedef uint32_t size_type;
      /// Index type for dimension indices.
      typedef uint32_t index_type;
      /// Signed difference type for difference between dimension indices.
      typedef int32_t difference_type;
      /// Direction of dimension progression.
      enum direction
        {
          ASCENDING, ///< Ascending progression.
          DESCENDING ///< Descending progression.
        };

      /// Dimension name.
      std::string name;
      /// Dimension size.
      size_type extent;
      /// Start index.
      size_type begin;
      /// End index.
      size_type end;
      /// Logical stride.
      size_type stride;

      /**
       * Constructor (extent).
       *
       * @param name the dimension name.
       * @param extent the dimension size (extent).
       */
      Dimension(const std::string& name,
                size_type          extent = 1U):
        name(name),
        extent(extent),
        begin(0U),
        end(extent),
        stride(1)
      {
        check(begin, end);
      }

      /**
       * Constructor (range).
       *
       * If the begin and end indices are both the same value, this
       * dimension exists logically fixed at the specified index, but
       * has no storage.
       *
       * @param name the dimension name.
       * @param extent the dimension size (extent).
       * @param begin the starting index.
       * @param end the ending index + 1.
       */
      Dimension(const std::string& name,
                size_type          extent,
                size_type          begin,
                size_type          end):
        name(name),
        extent(extent),
        begin(begin),
        end(end),
        stride(1)
      {
        check(begin, end);
      }

      /**
       * Copy constructor (range).
       *
       * If the begin and end indices are both the same value, this
       * dimension exists logically fixed at the specified index, but
       * has no storage.
       *
       * @param dim the dimension to copy.
       * @param begin the starting index.
       * @param end the ending index + 1.
       */
      Dimension(const Dimension& dim,
                size_type        begin,
                size_type        end):
        name(dim.name),
        extent(dim.extent),
        begin(begin),
        end(end),
        stride(1)
      {
        check(dim.begin, dim.end);
      }

      /**
       * Check dimension extent and range.
       *
       * The extent must be greater than zero.
       *
       * The begin and end values marking the usable range within the
       * total extent of the dimension must lie within zero and the
       * extent size.  If this is a subrange of an existing subrange,
       * it must lie within the existing subrange; i.e. it is not
       * possible to expand a subrange, only to reduce it further.
       *
       * @param previous_begin the previous begin value (or else zero).
       * @param previous_end the previous end value (or else current extent size).
       * @throws std::logic_error if the subrange is invalid.
       */
      void
      check(size_type previous_begin,
            size_type previous_end);

      /**
       * Get usable size.
       *
       * This is determined by the begin and end values defining the
       * usable range.
       *
       * @note Use extent for the full range.
       *
       * @returns the usable size.
       */
      size_type
      size() const
      {
        return end - begin;
      }

      /**
       * Compare dimensions by name.
       *
       * @param rhs the Dimension to compare.
       * @returns @c true if lexicographically less than @c rhs.
       */
      bool
      operator< (const Dimension& rhs)
      {
        return name < rhs.name;
      }

      /**
       * Check validity of dimension.
       * @returns @c true if invalid, @c false if valid.
       */
      bool
      operator! () const
      {
        return name.empty() || extent == 0;
      }
    };

    /**
     * Dimension storage specification (by index).
     *
     * Specify the storage order of a single dimension in terms of the
     * logical dimension index.
     *
     * @note Intended for use in an array where the array order
     * denotes the storage order.
     */
    struct IndexedDimensionStorage
    {
      /// Logical dimension index.
      Dimension::index_type dimension;
      /// Direction of dimension progression (forward or backward).
      Dimension::direction direction;

      /**
       * Constructor.
       *
       * @param dimension the logical dimension index.
       * @param direction the direction of dimension progression.
       */
      IndexedDimensionStorage(Dimension::index_type dimension,
                              Dimension::direction  direction):
        dimension(dimension),
        direction(direction)
      {}
    };

    /**
     * Dimension storage specification (by name).
     *
     * Specify the storage order of a single dimension in terms of the
     * logical dimension name.
     *
     * @note Intended for use in an array where the array order
     * denotes the storage order.
     */
    struct NamedDimensionStorage
    {
      /// Logical dimension name.
      std::string dimension;
      /// Direction of dimension progression (forward or backward).
      Dimension::direction direction;

      /**
       * Constructor.
       *
       * @param dimension the logical dimension name.
       * @param direction the direction of dimension progression.
       */
      NamedDimensionStorage(const std::string&   dimension,
                            Dimension::direction direction):
        dimension(dimension),
        direction(direction)
      {}
    };

    /**
     * Dimension subrange (by index).
     *
     * Restrict the range of a dimension to a subrange of its full
     * extent, as a half-open range of logical indices.
     */
    struct IndexedDimensionSubrange
    {
      /// Logical dimension index.
      Dimension::index_type dimension;
      /// Starting index in this dimension.
      Dimension::index_type begin;
      /// End index in this dimension.
      Dimension::index_type end;

      /**
       * Constructor.
       *
       * @param dimension the logical dimension index.
       * @param begin the starting index in this dimension.
       * @param end the ending index +1 in this dimension.
       */
      IndexedDimensionSubrange(Dimension::index_type dimension,
                               Dimension::index_type begin,
                               Dimension::index_type end):
        dimension(dimension),
        begin(begin),
        end(end)
      {}
    };

    /**
     * Dimension subrange (by name).
     *
     * Restrict the range of a dimension to a subrange of its full
     * extent, as a half-open range of logical indices.
     */
    struct NamedDimensionSubrange
    {
      /// Logical dimension name.
      std::string dimension;
      /// Starting index in this dimension.
      Dimension::index_type begin;
      /// End index in this dimension.
      Dimension::index_type end;

      /**
       * Constructor.
       *
       * @param dimension the logical dimension name.
       * @param begin the starting index in this dimension.
       * @param end the ending index +1 in this dimension.
       */
      NamedDimensionSubrange(const std::string&    dimension,
                             Dimension::index_type begin,
                             Dimension::index_type end):
        dimension(dimension),
        begin(begin),
        end(end)
      {}
    };

    /**
     * Dimension storage details (in logical order).
     */
    struct DimensionStorageDetail
    {
      /// Element stride to increment index forward by one.
      Dimension::difference_type stride;
      /// Descending offset (nonzero for descending dimensions).
      Dimension::difference_type descending_offset;

      /// Constructor.
      DimensionStorageDetail():
        stride(1),
        descending_offset(0)
      {}
    };

    /**
     * Collection of dimensions.
     *
     * This class implements the calculations needed to convert
     * between a logical linear array index and logical coordinates in
     * each dimension, and between logical coordinates and linear
     * storage array index.
     *
     * The concept implemented is that a set of dimensions are
     * referred to in a defined logical order.  This logical order is
     * the order in which the end user or programmer will index
     * dimensions, for example the coordinates to refer to a specific
     * location in (x,y,z) might be (0,4,2).  The logical order
     * however, need not be the same as the storage order (the
     * physical layout in memory).  The storage order may be specified
     * independently, and allows for the logical dimensions to be
     * stored in an arbitrary order, each of which may progress in a
     * forward or reverse direction.
     *
     * For the logical order and storage order, the stride for each
     * dimension and base index are computed, to allow for fast
     * translation between linear logical and storage indices and
     * logical coordinates as an intermediate representation between
     * the two.
     *
     * This class does not implement any storage; it is purely for
     * implementing the calculations needed by a multi-dimensional
     * array class or for any other calulation involving multiple
     * dimensions.
     *
     * @see Boost.MultiArray for a compile-time templated alternative
     * which will likely have higher performance, with the caveat that
     * it requires the number of dimensions to be fixed at compile
     * time.  This class trades off performance for run-time
     * flexibility.
     */
    class DimensionSet
    {
    public:
      /// @copydoc Dimension::size_type
      typedef Dimension::size_type size_type;
      /// @copydoc Dimension::index_type
      typedef Dimension::index_type index_type;
      /// @copydoc Dimension::difference_type
      typedef Dimension::difference_type difference_type;

      /// Index type for coordinate within a dimension.
      typedef std::vector<index_type> coord_type;
      /// Signed difference type for difference between coordinates.
      typedef std::vector<difference_type> coord_difference_type;

      /// Dimensions in logical order.
      typedef std::vector<Dimension>                logical_order_type;
      /// Dimensions in storage order (by index).
      typedef std::vector<IndexedDimensionStorage>  indexed_storage_order_type;
      /// Dimensions in storage order (by name).
      typedef std::vector<NamedDimensionStorage>    named_storage_order_type;
      /// Storage order details.
      typedef std::vector<DimensionStorageDetail>   detail_type;
      /// Dimension extents subrange (by index).
      typedef std::vector<IndexedDimensionSubrange> indexed_subrange_type;
      /// Dimension extents subrange (by name).
      typedef std::vector<NamedDimensionSubrange>   named_subrange_type;

    private:
      /// Dimensions in logical order (user-addressable order).
      std::vector<Dimension> _logical_order;
      /// Dimensions in indexed storage order (describing physical memory layout).
      indexed_storage_order_type _storage_order;
      /// Dimension details in logical order (describing physical strides and offsets).
      detail_type _detail;
      /// Base offset.
      index_type _base;

    public:
      /**
       * Construct with default storage order.
       *
       * The logical dimension order and storage order may not be
       * modified after construction.  The storage order (physical
       * memory layout) defaults to the logical dimension order, with
       * each dimension having ascending progression.
       *
       * @param dimensions the dimensions contained in this set, in
       * logical order.  Dimension names must be unique within the set,
       * and not repeated.
       * @throws std::logic_error if the unique constraint is violated.
       */
      DimensionSet(const logical_order_type& dimensions);

      /**
       * Construct with indexed storage order.
       *
       * The logical dimension order and storage order may not be
       * modified after construction.  The storage order is specified
       * in terms of the index each dimension in the @c dimensions
       * parameter.
       *
       * @param dimensions the dimensions contained in this set, in
       * logical order.  Dimension names must be unique within the set,
       * and not repeated.
       * @param order the storage order (by dimension index in @c
       * dimensions).  Dimension indices must not be repeated.
       * @throws std::logic_error if the unique constraint is violated.
       */
      DimensionSet(const logical_order_type&         dimensions,
                   const indexed_storage_order_type& order);

      /**
       * Construct with named storage order.
       *
       * The logical dimension order and storage order may not be
       * modified after construction.  The storage order is specified
       * in terms of the name of each dimension in the @c dimensions
       * parameter.
       *
       * @param dimensions the dimensions contained in this set, in
       * logical order.  Dimension names must be unique within the set,
       * and not repeated.
       * @param order the storage order (by dimension name in @c
       * dimensions).  Dimension indices must not be repeated.
       * @throws std::logic_error if the unique constraint is violated.
       */
      DimensionSet(const logical_order_type&       dimensions,
                   const named_storage_order_type& order);

    private:
      /**
       * Initialisation common to all constructor variants.
       *
       * @note A workaround for lack of delegating constructors pre-C++11.
       */
      void
      init();

      /**
       * Set storage order (by index).
       *
       * The dimension list must not contain duplicate dimension indices.
       *
       * @param order the storage order.
       */
      void
      storage_order(const indexed_storage_order_type& order);

      /**
       * Set storage order (by name).
       *
       * The dimension list must not contain duplicate dimension names.
       *
       * @param order the storage order.
       */
      void
      storage_order(const named_storage_order_type& order);

      /**
       * Get mapping between dimension name and index for a logical dimension ordering.
       *
       * @param order the logical dimension ordering for which to create the mapping.
       * @returns the mapping.
       */
      static
      std::map<std::string, index_type>
      name_map(const logical_order_type& order)
      {
        std::map<std::string, index_type> ret;

        for (index_type i = 0; i < order.size(); ++i)
          {
            const Dimension& dim(order[i]);
            ret.insert(std::make_pair(dim.name, i));
          }

        return ret;
      }

      /**
       * Get mapping between dimension index and name for a logical dimension ordering.
       *
       * @param order the logical dimension ordering for which to create the mapping.
       * @returns the mapping.
       */
      static
      std::map<index_type, std::string>
      index_map(const logical_order_type& order)
      {
        std::map<index_type, std::string> ret;

        for (index_type i = 0; i < order.size(); ++i)
          {
            const Dimension& dim(order[i]);
            ret.insert(std::make_pair(i, dim.name));
          }

        return ret;
      }

      /**
       * Compute logical strides for each dimension.
       *
       * This is computed for each dimension in progessing logical
       * order.
       *
       * @note The strides are not recomputed after any subsetting.
       */
      void
      compute_logical_strides();

      /**
       * Compute storage strides for each dimension.
       *
       * This is computed for each dimension in progessing storage
       * order.
       *
       * @note The strides are not recomputed after any subsetting.
       */
      void
      compute_storage_strides();

      /**
       * Compute storage origin offset.
       *
       * This is computed as the sum of the calculated offsets for
       * each descending dimension.
       *
       * @note The origin offset is not recomputed after any
       * subsetting.
       */
      void
      compute_storage_offset();

    public:
      /**
       * The number of dimensions in the set.
       *
       * @returns the number of dimensions.
       */
      size_type
      size() const
      {
        return _logical_order.size();
      }

      /**
       * The number of elements (product of all dimension sizes).
       *
       * @note This is the product of the subrange size of each
       * dimension, not the full range.  That is to say, it is the
       * total number of accessible elements, which might be less than
       * the total number of elements.
       *
       * @returns the number of elements.
       */
      size_type
      num_elements() const
      {
        size_type n = 1;

        for (std::vector<Dimension>::const_iterator dim = _logical_order.begin();
             dim != _logical_order.end();
             ++dim)
          n *= dim->size();

        return n;
      }

      /**
       * Get the logical order of dimensions.
       *
       * @returns the logical order of dimensions.
       */
      const logical_order_type&
      logical_order() const
      {
        return _logical_order;
      }

      /**
       * Get the storage order of dimensions.
       *
       * @note the list is in storage order; to get the dimension
       * details, look up the dimension in its logical order by the
       * dimension index.
       *
       * @returns the storage order of dimensions.
       */
      const indexed_storage_order_type&
      storage_order() const
      {
        return _storage_order;
      }

      /**
       * Get a dimension by its index.
       *
       * @param dimension_index the logical index.
       * @returns a reference to the dimension.
       * @throws std::logic_error if the index is invalid.
       */
      const Dimension&
      dimension(index_type dimension_index) const
      {
        if (dimension_index >= size())
          {
            boost::format fmt("Dimension index %1% out of valid range %2%–%3%");
            fmt % dimension_index % 0 % (size() - 1);
            throw std::logic_error(fmt.str());
          }

        return _logical_order[dimension_index];
      }

      /**
       * Compute the logical index from logical coordinate.
       *
       * The logical coordinate values must be within the permitted
       * subrange defined for each dimension (indexed relative from
       * zero within the subrange).  If the indices fall outside the
       * permitted subranges, the result is undefined.
       *
       * @param coord the logical coordinate.
       * @returns the logical index corresponding to the specified
       * coordinate.
       */
      index_type
      logical_index(const coord_type& coord) const
      {
        DimensionSet::difference_type ret = 0;

        // For each dimension in logical order, multiply the
        // coordinate value by the stride for the dimension and return
        // the sum for all dimensions.
        for(size_type d = 0; d < size(); ++d)
          ret += coord.at(d) * dimension(d).stride;

        return ret;
      }

      /**
       * Compute the logical coordinate from logical index.
       *
       * The logical index must correspond to a valid coordinate
       * within the permitted subrange defined for each dimension,
       * otherwise the result is undefined.
       *
       * @param index the logical index.
       * @param coord the logical coordinate corresponding to the
       * specified index.
       */
      void
      logical_coord(index_type  index,
                    coord_type& coord) const
      {
        // Note a coord reference is used rather than returning it for
        // efficiency--it avoids allocating storage for a vector which
        // is not ideal when called repeatedly.
        coord.resize(size(), 0U);

        // For each dimension in reverse logical order, compute the
        // coordinate value by dividing by the stride for the
        // dimension, then repeat using the remainder.
        for(size_type d = 0; d < size(); ++d)
          {
            size_type rd = size() - d;
            --rd;

            coord.at(rd) = index / dimension(rd).stride;
            index %= dimension(rd).stride;
          }
      }

      /**
       * Compute the storage index from logical coordinate.
       *
       * The logical coordinate values must be within the permitted
       * subrange defined for each dimension (indexed relative from
       * zero within the subrange).  If the indices fall outside the
       * permitted subranges, the result is undefined.
       *
       * @param coord the logical coordinate.
       * @returns the storage index corresponding to the specified
       * coordinate.
       */
      index_type
      storage_index(const coord_type& coord) const
      {
        if (_logical_order.size() != coord.size())
          {
            boost::format fmt("Coordinate dimension count %1% does not match set dimension count %2%");
            fmt % coord.size() % size();
            throw std::logic_error(fmt.str());
          }

        difference_type pos = _base;

        // For each dimension in logical order, multiply the
        // coordinate value by the stride for the dimension and return
        // the sum for all dimensions.  When using subranges, the
        // subrange start is added to the coordinate to ensure that
        // the calculation is using coordinates in the full range,
        // rather than within the subrange window.
        for (std::vector<Dimension>::size_type d = 0; d < size(); ++d)
          {
            const Dimension& dim(dimension(d));
            const DimensionStorageDetail& detail = _detail.at(d);
            pos += detail.stride * (coord.at(d) + dim.begin);
          }

        return pos;
      }

      /**
       * Compute the logical coordinate from storage index.
       *
       * The storage index must correspond to a valid coordinate
       * within the permitted subrange defined for each dimension,
       * otherwise the result is undefined.
       *
       * @param index the storage index.
       * @param coord the logical coordinate corresponding to the
       * specified index.
       */
      void
      storage_coord(index_type  index,
                    coord_type& coord) const
      {
        // Note a coord reference is used rather than returning it for
        // efficiency--it avoids allocating storage for a vector which
        // is not ideal when called repeatedly.
        coord.resize(size(), 0U);

        // For each dimension in reverse logical order, compute the
        // coordinate value by dividing by the stride for the
        // dimension, then repeat using the remainder.  When using
        // subranges, the subrange start is subtracted from the
        // coordinate to ensure that the calculation is using
        // coordinates in the full range, but returning the coordinate
        // within the subrange window.
        difference_type remainder = index;
        for (std::vector<Dimension>::size_type d = 0; d < size(); ++d)
          {
            index_type id = size() - d - 1;
            const IndexedDimensionStorage& sdim(_storage_order.at(id));
            const Dimension& dim(dimension(sdim.dimension));
            const DimensionStorageDetail& dimdetail(_detail.at(sdim.dimension));

            if (dimdetail.stride < 0)
              {
                difference_type v = dim.extent * abs(dimdetail.stride);
                v -= remainder;
                v -= 1;
                coord.at(sdim.dimension) = v / abs(dimdetail.stride);
              }
            else
              {
                coord.at(sdim.dimension) = remainder / dimdetail.stride;
              }
            coord.at(sdim.dimension) -= dim.begin;
            remainder %= abs(dimdetail.stride);
          }
      }

      /**
       * Create a subrange from this dimension set (by index).
       *
       * The subrange must be equal to or less than any subrange
       * already set; it is not possible to expand the range.
       *
       * @param subrange the dimensions to reduce in permitted range.
       * @returns a new dimension set using the specified subrange.
       */
      DimensionSet
      subrange(const indexed_subrange_type& subrange) const;

      /**
       * Create a subrange from this dimension set (by name).
       *
       * The subrange must be equal to or less than any subrange
       * already set; it is not possible to expand the range.
       *
       * @param subrange the dimensions to reduce in permitted range.
       * @returns a new dimension set using the specified subrange.
       */
      DimensionSet
      subrange(const named_subrange_type& subrange) const;

      template<class charT, class traits>
      friend std::basic_ostream<charT,traits>&
      operator<< (std::basic_ostream<charT,traits>& os,
                  const DimensionSet& set);
    };

    /**
     * Output DimensionSet to output stream.
     *
     * @param os the output stream.
     * @param set the DimensionSet to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
    inline std::basic_ostream<charT,traits>&
    operator<< (std::basic_ostream<charT,traits>& os,
                const DimensionSet& set)
    {
      os << "Logical dimensions:      (";
      for (std::vector<Dimension>::const_iterator dim = set._logical_order.begin();
           dim != set._logical_order.end();
           ++dim)
        {
          os << dim->name;
          if (dim + 1 < set._logical_order.end())
            os << ',';
        }
      os << ")\nLogical extents:         (";
      for (std::vector<Dimension>::const_iterator dim = set._logical_order.begin();
           dim != set._logical_order.end();
           ++dim)
        {
          os << dim->extent;
          if (dim + 1 < set._logical_order.end())
            os << ',';
        }
      os << ")\nLogical ranges:          (";
      for (std::vector<Dimension>::const_iterator dim = set._logical_order.begin();
           dim != set._logical_order.end();
           ++dim)
        {
          os << '[' << dim->begin << ',' << dim->end << ')';
          if (dim + 1 < set._logical_order.end())
            os << ',';
        }
      os << ")\nLogical sizes:           (";
      for (std::vector<Dimension>::const_iterator dim = set._logical_order.begin();
           dim != set._logical_order.end();
           ++dim)
        {
          os << dim->size();
          if (dim + 1 < set._logical_order.end())
            os << ',';
        }
      os << ")\nLogical strides:         (";
      for (std::vector<Dimension>::const_iterator dim = set._logical_order.begin();
           dim != set._logical_order.end();
           ++dim)
        {
          os << dim->stride;
          if (dim + 1 < set._logical_order.end())
            os << ',';
        }
      os << ")\nSize:                    " << set.size()
         << "\nElements:                " << set.num_elements()
         << "\nStorage dimensions:      (";
      for (DimensionSet::indexed_storage_order_type::const_iterator dim = set._storage_order.begin();
           dim != set._storage_order.end();
           ++dim)
        {
          const Dimension& ldim(set.dimension(dim->dimension));
          os << ldim.name;
          if (dim + 1 < set._storage_order.end())
            os << ',';
        }
      os << ")\nStorage extents:         (";
      for (DimensionSet::indexed_storage_order_type::const_iterator dim = set._storage_order.begin();
           dim != set._storage_order.end();
           ++dim)
        {
          const Dimension& ldim(set.dimension(dim->dimension));
          os << (dim->direction == Dimension::ASCENDING ? '+' : '-') << ldim.extent;
          if (dim + 1 < set._storage_order.end())
            os << ',';
        }
      os << ")\nBase:                    " << set._base
         << "\nStrides:                 (";
      for (std::vector<DimensionStorageDetail>::const_iterator detail = set._detail.begin();
           detail != set._detail.end();
           ++detail)
        {
          os << detail->stride;
          if (detail + 1 < set._detail.end())
            os << ',';
        }
      os << ")\nDescending offsets:      (";
      for (std::vector<DimensionStorageDetail>::const_iterator detail = set._detail.begin();
           detail != set._detail.end();
           ++detail)
        {
          os << detail->descending_offset;
          if (detail + 1 < set._detail.end())
            os << ',';
        }
      os << ")\n";
      return os;
    }

  }
}

#endif // OME_BIOFORMATS_DIMENSION_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
