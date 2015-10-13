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

#include <set>

#include <ome/bioformats/Dimension.h>

namespace ome
{
  namespace bioformats
  {

    void
    Dimension::check(size_type previous_begin,
                     size_type previous_end)
    {
      if (extent == 0)
        {
          boost::format fmt("Dimension %1% extent is of zero length");
          fmt % name;
          throw std::logic_error(fmt.str());
        }

      if (begin >= extent ||
          end > extent ||
          begin > end)
        {
          boost::format fmt("Dimension %1% subrange %2%–%3% outside valid range %4%–%5%");
          fmt % name % begin % end % 0U % extent;
          throw std::logic_error(fmt.str());
        }

      if (begin < previous_begin ||
          end > previous_end)
        {
          boost::format fmt("Dimension %1% subrange %2%–%3% outside valid subrange %4%–%5%");
          fmt % name % begin % end % previous_begin % previous_end;
          throw std::logic_error(fmt.str());
        }
    }

    DimensionSpace::DimensionSpace(const logical_order_type& dimensions):
      _logical_order(dimensions),
      _storage_order(),
      _detail(dimensions.size()),
      _base(0U)
    {
      init();
    }

    DimensionSpace::DimensionSpace(const logical_order_type&         dimensions,
                                   const indexed_storage_order_type& order):
      _logical_order(dimensions),
      _storage_order(),
      _detail(dimensions.size()),
      _base(0U)
    {
      init();
      storage_order(order);
    }

    DimensionSpace::DimensionSpace(const logical_order_type&       dimensions,
                                   const named_storage_order_type& order):
      _logical_order(dimensions),
      _storage_order(),
      _detail(dimensions.size()),
      _base(0U)
    {
      init();
      storage_order(order);
    }

    void
    DimensionSpace::init()
    {
      _storage_order.reserve(_logical_order.size());
      _detail.resize(_logical_order.size());

      std::set<std::string> used;

      for (uint32_t i = 0; i < _logical_order.size(); ++i)
        {
          // Check for duplicate dimensions
          if (used.find(_logical_order[i].name) != used.end())
            {
              boost::format fmt("Dimension %1% used multiple times");
              fmt % _logical_order[i].name;
              throw std::logic_error(fmt.str());
            }
          used.insert(_logical_order[i].name);

          // Set default storage order (matches logical order).
          _storage_order.push_back(IndexedDimensionStorage(i, Dimension::ASCENDING));
        }

      // Set strides and offset.
      compute_logical_strides();
      compute_storage_strides();
      compute_storage_offset();
    }

    void
    DimensionSpace::storage_order(const indexed_storage_order_type& order)
    {
      if (order.size() != size())
        {
          boost::format fmt("Storage order dimension count %1% for order does not match space dimension count %2%");
          fmt % order.size() % size();
          throw std::logic_error(fmt.str());
        }

      // Check for duplicates using set of indices.
      std::set<index_type> used;
      for (index_type i = 0; i < order.size(); ++i)
        {
          const indexed_storage_order_type::value_type& o(order.at(i));
          // Check dimension is valid.
          if (o.dimension >= size())
            {
              boost::format fmt("Storage order dimension %1% index %2% out of valid range %3%–%4%");
              fmt % i % o.dimension % 0 % (size() -1);
              throw std::logic_error(fmt.str());
            }
          // Check for duplicate dimensions.
          if (used.find(o.dimension) != used.end())
            {
              boost::format fmt("Dimension %1% used multiple times");
              fmt % o.dimension;
              throw std::logic_error(fmt.str());
            }
          used.insert(o.dimension);
        }
      _storage_order = order;

      // Recompute strides and offset.
      compute_storage_strides();
      compute_storage_offset();
    }

    void
    DimensionSpace::storage_order(const named_storage_order_type& order)
    {
      std::map<std::string, index_type> nmap(name_map(_logical_order));
      indexed_storage_order_type iorder;

      // Convert named order to indexed order.
      for (named_storage_order_type::const_iterator o = order.begin();
           o != order.end();
           ++o)
        {
          std::map<std::string, index_type>::const_iterator i(nmap.find(o->dimension));
          if (i == nmap.end())
            {
              boost::format fmt("Dimension name ‘%1%’ invalid");
              fmt % o->dimension;
              throw std::logic_error(fmt.str());
            }
          iorder.push_back(indexed_storage_order_type::value_type(i->second, o->direction));
        }

      storage_order(iorder);
    }

    void
    DimensionSpace::compute_logical_strides()
    {
      difference_type stride = 1;

      // For each dimension in logical order, assign stride from
      // previous dimension and multiply stride by dimension size.
      // Note this is the subrange size, not the extent size.  Also
      // note there is no logical offset since the logical dimensions
      // are all in an ascending direction, making the base zero.
      for (logical_order_type::iterator dim = _logical_order.begin();
           dim != _logical_order.end();
           ++dim)
        {
          dim->stride = stride;
          stride *= dim->size();
        }
    }

    void
    DimensionSpace::compute_storage_strides()
    {
      difference_type stride = 1;

      // For each dimension in storage order, assign stride from
      // previous dimension and multiply stride by dimension size.
      // Note this is the extent size, not the subrange size.  If the
      // direction of the dimension is descending, then the stride is
      // negative to cause backward traversal.
      for (indexed_storage_order_type::const_iterator dim = _storage_order.begin();
           dim != _storage_order.end();
           ++dim)
        {
          difference_type sign = +1;
          if (dim->direction == Dimension::DESCENDING)
            sign = -1;

          _detail.at(dim->dimension).stride = stride * sign;
          stride *= dimension(dim->dimension).extent;
        }
    }

    void
    DimensionSpace::compute_storage_offset()
    {
      // Descending dimension offset.
      difference_type descending_offset = 0U;

      // If all dimensions are ascending, then the offset is zero.
      bool all_ascending = true;
      for (indexed_storage_order_type::const_iterator dim = _storage_order.begin();
           dim != _storage_order.end();
           ++dim)
        {
          if (dim->direction == Dimension::DESCENDING)
            all_ascending = false;
        }
      if (!all_ascending)
        {
          // For each dimension in storage order where the dimension
          // is descending, compute the offset as the (extent size -1)
          // multiplied by the stride for this dimenesion.  The base
          // offset is the sum of the offsets for all dimensions.
          for (indexed_storage_order_type::const_iterator dim = _storage_order.begin();
               dim != _storage_order.end();
               ++dim)
            {
              if (dim->direction == Dimension::DESCENDING)
                {
                  DimensionStorageDetail& detail = _detail.at(dim->dimension);
                  detail.descending_offset = (dimension(dim->dimension).extent - 1) *
                    detail.stride;
                  // Subtract because the offset is negative due to the negative stride.
                  descending_offset -= detail.descending_offset;
                }
            }
        }

      _base = descending_offset;
    }

    DimensionSpace
    DimensionSpace::subrange(const indexed_subrange_type& subrange) const
    {
      // Check for duplicates using set of indices.
      std::set<index_type> used;

      DimensionSpace ret(*this);

      for(indexed_subrange_type::const_iterator sub = subrange.begin();
          sub != subrange.end();
          ++sub)
        {
          Dimension& dim(ret._logical_order.at(sub->dimension));
          // Range is relative to existing subrange; add to existing
          // range start to make absolute.
          dim = Dimension(dim, dim.begin + sub->begin, dim.begin + sub->end);

          // Check for duplicate dimensions.
          if (used.find(sub->dimension) != used.end())
            {
              boost::format fmt("Dimension %1% used multiple times in subrange");
              fmt % sub->dimension;
              throw std::logic_error(fmt.str());
            }
          used.insert(sub->dimension);

          if(sub->end < sub->begin || sub->end == sub->begin ||
             sub->end > dim.extent)
            {
              boost::format fmt("Dimension %1% subrange %2%–%3% invalid");
              fmt % sub->dimension % sub->begin % sub->end;
              throw std::logic_error(fmt.str());
            }
        }

      // Recompute logical strides.
      ret.compute_logical_strides();

      return ret;
    }

    DimensionSpace
    DimensionSpace::subrange(const named_subrange_type& subrange) const
    {
      std::map<std::string, index_type> nmap(name_map(_logical_order));
      indexed_subrange_type isubrange;

      // Convert named subrange to indexed subrange.
      for (named_subrange_type::const_iterator s = subrange.begin();
           s != subrange.end();
           ++s)
        {
          std::map<std::string, index_type>::const_iterator i(nmap.find(s->dimension));
          if (i == nmap.end())
            {
              boost::format fmt("Dimension name ‘%1%’ invalid");
              fmt % s->dimension;
              throw std::logic_error(fmt.str());
            }
          isubrange.push_back(indexed_subrange_type::value_type(i->second, s->begin, s->end));
        }

      // Note that duplicate dimensions will be caught here.
      return this->subrange(isubrange);
    }

  }
}
