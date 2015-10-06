/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
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

#include <algorithm>
#include <cmath>

#include <boost/range/size.hpp>

#include <ome/bioformats/Dimension.h>

#include <ome/test/test.h>

using ome::bioformats::Dimension;
using ome::bioformats::DimensionSet;
using ome::bioformats::IndexedDimensionStorage;
using ome::bioformats::NamedDimensionStorage;
using ome::bioformats::IndexedDimensionSubrange;
using ome::bioformats::NamedDimensionSubrange;

namespace
{

  // Print a 2D array.
  // planecoord is the index into the higher dimension (>=2) indices;
  // the lowest two dimensions will be rendered in a 2D grid.
  void dump_array(const DimensionSet& set,
                  DimensionSet::coord_type planecoord,
                  uint32_t index_size,
                  DimensionSet::index_type (DimensionSet::*get_index)(const DimensionSet::coord_type& coord) const)
  {
    if (set.size() == 0)
      return;

    uint32_t ylabsize = 1;
    uint32_t yvalsize = 1;
    uint32_t nx = 1;
    uint32_t ny = 1;

    const DimensionSet::logical_order_type& logical(set.logical_order());
    nx = logical.at(0).size();
    if (set.size() > 1)
      {
        ylabsize = logical.at(1).name.size();
        ny = logical.at(1).size();

        yvalsize = static_cast<uint32_t>(std::floor(std::log10(static_cast<float>(logical.at(1).size()-1)))) + 1;
      }

    // Header
    std::string sepy;
    std::string sepy2;
    if (set.size() > 1)
      {
        sepy = std::string(ylabsize + yvalsize + 2, ' ');
        sepy2 = std::string(ylabsize + 1, ' ');
      }
    std::string xthickline;
    std::string xthinline;
    std::string ythickline;
    std::string ythinline;
    for (uint32_t i = 0; i < index_size; ++i)
      {
        xthickline += "━";
        xthinline += "─";
      }
    for (uint32_t i = 0; i < yvalsize; ++i)
      {
        ythickline += "━";
        ythinline += "─";
      }

    // Column label
    std::cout << sepy << logical.at(0).name << '\n';

    // Top line
    std::cout << sepy << "┏";
    for (uint32_t i = 0; i < nx; ++i)
      {
        std::cout << xthickline;
        if (i + 1 < nx)
          std::cout << "┯";
      }
    std::cout << "┓\n";

    // Column values
    std::cout << sepy << "┃";
    for (uint32_t i = 0; i < nx; ++i)
      {
        std::cout << std::setw(index_size) << std::right << i;
        if (i + 1 < nx)
          std::cout << "│";
      }
    std::cout << "┃\n";

    // Mid line
    if (set.size() > 1)
      {
        std::cout << logical.at(1).name << ' ' << "┏";
        for (uint32_t i = 0; i < 1; ++i)
          {
            std::cout << ythickline;
            if (i + 1 < 1)
              std::cout << "┯";
          }
        std::cout << "╋";
        for (uint32_t i = 0; i < nx; ++i)
          {
            std::cout << xthickline;
            if (i + 1 < nx)
              std::cout << "┿";
          }
        std::cout << "┫\n";
      }
    else
      {
        std::cout << sepy << "┣";
        for (uint32_t i = 0; i < nx; ++i)
          {
            std::cout << xthickline;
            if (i + 1 < nx)
              std::cout << "┿";
          }
        std::cout << "┫\n";
      }

    DimensionSet::coord_type index;
    index.push_back(0);
    if (set.size() > 1)
      index.push_back(0);
    std::copy(planecoord.begin(), planecoord.end(),
              std::back_inserter(index));

    // Loop over rows.
    if (set.size() > 1)
      {
        for (uint32_t y = 0; y < ny; ++y)
          {
            if (set.size() > 1)
              index.at(1) = y;

            // Row values
            if (set.size() > 1)
              {
                std::cout << sepy2 << "┃";
                for (uint32_t i = 0; i < 1; ++i)
                  {
                    std::cout << std::setw(yvalsize) << std::right << y;
                    if (i + 1 < 1)
                      std::cout << "│";
                  }
                std::cout << "┃";
                for (uint32_t x = 0; x < nx; ++x)
                  {
                    index.at(0) = x;
                    std::cout << std::setw(index_size) << (set.*get_index)(index);
                    if (x + 1 < nx)
                      std::cout << "│";
                  }
                std::cout << "┃\n";
              }

            // Row line
            if (y + 1 < ny && set.size() > 1)
              {
                std::cout << sepy2 << "┠";
                for (uint32_t i = 0; i < 1; ++i)
                  {
                    std::cout << ythinline;
                    if (i + 1 < 1)
                      std::cout << "┼";
                  }
                std::cout << "╂";
                for (uint32_t i = 0; i < nx; ++i)
                  {
                    std::cout << xthinline;
                    if (i + 1 < nx)
                      std::cout << "┼";
                  }
                std::cout << "┨\n";
              }
          }
      }
    else
      {
        std::cout << sepy << "┃";
        for (uint32_t x = 0; x < nx; ++x)
          {
            index.at(0) = x;
            std::cout << std::setw(index_size) << (set.*get_index)(index);
            if (x + 1 < nx)
              std::cout << "│";
          }
        std::cout << "┃\n";
      }

    // Bottom line
    if (set.size() > 1)
      {
        std::cout << sepy2 << "┗";
        for (uint32_t i = 0; i < 1; ++i)
          {
            std::cout << ythickline;
            if (i + 1 < 1)
              std::cout << "┷";
          }
        std::cout << "┻";
        for (uint32_t i = 0; i < nx; ++i)
          {
            std::cout << xthickline;
            if (i + 1 < nx)
              std::cout << "┷";
          }
        std::cout << "┛\n";
      }
    else
      {
        std::cout << sepy << "┗";
        for (uint32_t i = 0; i < nx; ++i)
          {
            std::cout << xthickline;
            if (i + 1 < nx)
              std::cout << "┷";
          }
        std::cout << "┛\n";
      }
  }

  // Print an nD array.
  void
  dump_array(const DimensionSet& set,
             bool                storage = true)
  {
    DimensionSet::index_type (DimensionSet::*get_index)(const DimensionSet::coord_type& coord) const = &DimensionSet::storage_index;
    if (!storage)
      get_index = &DimensionSet::logical_index;

    // Maximum length of index for display.
    uint32_t index_size = 1;
    {
      DimensionSet::coord_type idx;
      for(Dimension::index_type i = 0; i < set.num_elements(); ++i)
        {
          set.logical_coord(i, idx);
          index_size = std::max(index_size,
                                static_cast<uint32_t>(std::floor(std::log10(static_cast<float>((set.*get_index)(idx))))) + 1);
        }
    }

    if (set.size() < 3) // Render as 1D or 2D plane
      {
        DimensionSet::coord_type planecoord; // Intentionally empty.
        dump_array(set, planecoord, index_size, get_index);
      }
    else // Render as multiple 2D planes
      {
        const DimensionSet::logical_order_type& logical(set.logical_order());

        std::vector<Dimension> dims;
        for (uint32_t i = 2; i < logical.size(); ++i)
          dims.push_back(logical.at(i));
        DimensionSet set2(dims);

        DimensionSet::coord_type coord;
        for(Dimension::index_type i = 0; i < set2.num_elements(); ++i)
          {
            set2.logical_coord(i, coord);
            for (uint32_t j = 0; j < coord.size(); ++j)
              {
                const Dimension& dim(logical.at(j + 2));
                std::cout << dim.name << '=' << coord.at(j);
                if (j + 1 < coord.size())
                  std::cout << ", ";
              }
            std::cout << '\n';
            dump_array(set, coord, index_size, get_index);
            std::cout << '\n';
          }
      }
  }

  // Print a vector-type object with comma separators.
  template<typename T>
  struct dump_vector
  {
    const T& v;

    dump_vector(const T& v):
      v(v)
    {}
  };

  template<typename T>
  std::ostream&
  operator << (std::ostream& os, const dump_vector<T>& vec)
  {
    for (typename T::const_iterator i = vec.v.begin();
         i != vec.v.end();
         ++i)
      {
        os << *i;
        if (i + 1 != vec.v.end())
          os << ',';
      }
    return os;
  }

  // Compute all direction permutations for a given number of
  // dimensions.
  std::vector<std::vector<Dimension::direction> >
  direction_permutations(Dimension::index_type count)
  {
    // Combinations of dimension progression directions.
    std::vector<std::vector<Dimension::direction> > directions;
    {
      for (Dimension::index_type i = 0U; i < (1U << count); ++i)
        {
          std::vector<Dimension::direction> direction;
          for (Dimension::index_type j = 0U; j < count; ++j)
            {
              if (!(i & (1 << j)))
                direction.push_back(Dimension::ASCENDING);
              else
                direction.push_back(Dimension::DESCENDING);
            }
          directions.push_back(direction);
        }
    }

    return directions;
  }

  // Compute all order permutations (by index) for a given set of
  // dimensions.
  std::vector<std::vector<IndexedDimensionStorage> >
  indexed_order_permutations(const std::vector<Dimension>& dims)
  {
    Dimension::index_type count = dims.size();

    // Combinations of dimension progression directions.
    std::vector<std::vector<Dimension::direction> > directions(direction_permutations(count));

    // Combinations of dimension storage ordering.
    std::vector<std::vector<Dimension::index_type> > orders;
    {
      std::vector<Dimension::index_type> order;
      for(Dimension::index_type i = 0; i < count; ++i)
        order.push_back(i);

      do
        {
          orders.push_back(order);
        }
      while(std::next_permutation(order.begin(), order.end()));
    }

    std::vector<std::vector<IndexedDimensionStorage> > ret;

    for(std::vector<std::vector<Dimension::index_type> >::const_iterator i = orders.begin();
        i != orders.end();
        ++i)
      {
        for(std::vector<std::vector<Dimension::direction> >::const_iterator j = directions.begin();
            j != directions.end();
            ++j)
          {
            std::vector<IndexedDimensionStorage> storage;
            for (Dimension::index_type k = 0; k < count; ++k)
              storage.push_back(IndexedDimensionStorage(i->at(k), j->at(k)));
            ret.push_back(storage);
          }
      }

    return ret;
  }

  // Compute all order permutations (by name) for a given set of
  // dimensions.
  std::vector<std::vector<NamedDimensionStorage> >
  named_order_permutations(const std::vector<Dimension>& dims)
  {
    Dimension::index_type count = dims.size();

    // Combinations of dimension progression directions.
    std::vector<std::vector<Dimension::direction> > directions(direction_permutations(count));

    // Combinations of dimension storage ordering.
    std::vector<std::vector<std::string> > orders;
    {
      std::vector<std::string> order;
      for(Dimension::index_type i = 0; i < count; ++i)
        order.push_back(dims.at(i).name);
      std::sort(order.begin(), order.end());

      do
        {
          orders.push_back(order);
        }
      while(std::next_permutation(order.begin(), order.end()));
    }

    std::vector<std::vector<NamedDimensionStorage> > ret;

    for(std::vector<std::vector<std::string> >::const_iterator i = orders.begin();
        i != orders.end();
        ++i)
      {
        for(std::vector<std::vector<Dimension::direction> >::const_iterator j = directions.begin();
            j != directions.end();
            ++j)
          {
            std::vector<NamedDimensionStorage> storage;
            for (Dimension::index_type k = 0; k < count; ++k)
              storage.push_back(NamedDimensionStorage(i->at(k), j->at(k)));
            ret.push_back(storage);
          }
      }

    return ret;
  }

  // Get all storage order permutations from a dimension list and list
  // of indexed or named storage orders, realised in the form of
  // constructed DimensionSet objects.
  template<typename Storage>
  std::vector<DimensionSet>
  order_permutations(std::vector<Dimension>             dims,
                     std::vector<std::vector<Storage> > orders)
  {
    {
      DimensionSet setdef(dims);

      std::cout << "DimensionSet default parameters:\n"
                << setdef
                << "\nDimensionSet logical layout:\n\n";
      dump_array(setdef, false);
      std::cout << "\nDimensionSet default storage layout:\n\n";
      dump_array(setdef, true);
      std::cout << '\n';
    }

    std::vector<DimensionSet> ret;

    typedef typename std::vector<std::vector<Storage> > storage_type;
    for(typename storage_type::const_iterator order = orders.begin();
        order != orders.end();
        ++order)
      {
        ret.push_back(DimensionSet(dims, *order));
      }

    return ret;
  }

  // Test all DimensionSet permutations provided.  This will ensure
  // round-trip from logical coordinate to storage index and back is
  // working, but does not validate the correctness of the
  // algorithm--it only checks it's behaviour is consistent.  It will
  // also print the array structure and (for subrange sets) the
  // subrange structure in addition to the full range.
  void
  test_orders(std::vector<DimensionSet> dims)
  {
    for(std::vector<DimensionSet>::const_iterator dim = dims.begin();
        dim != dims.end();
        ++dim)
      {
        // Check if subranges are in use, and if so, print the original
        // set as well as the reduced set for visual comparison.
        const DimensionSet& set(*dim);

        DimensionSet::logical_order_type logical(set.logical_order());
        DimensionSet::logical_order_type logical_full;
        const DimensionSet::indexed_storage_order_type& storage(set.storage_order());
        bool subrange = false;
        for (DimensionSet::logical_order_type::const_iterator i = logical.begin();
             i != logical.end();
             ++i)
          {
            if (i->extent != i->size())
              subrange = true;
            logical_full.push_back(Dimension(i->name, i->extent));
          }
        DimensionSet full_set(logical_full, storage);

        std::cout << "DimensionSet with storage order:\n"
                  << set;
        if (subrange)
          {
            std::cout << "\nDimensionSet storage layout (original):\n\n";
            dump_array(full_set);
            std::cout << "\nDimensionSet storage layout (subrange):\n\n";
          }
        else
          {
            std::cout << "\nDimensionSet storage layout:\n\n";
          }
        dump_array(set);
        std::cout << '\n';

        DimensionSet::coord_type pos1, pos3;
        for(Dimension::index_type i = 0; i < set.num_elements(); ++i)
          {
            set.logical_coord(i, pos1);

            uint32_t pos2 =  set.storage_index(pos1);
            set.storage_coord(pos2, pos3);
            std::cout << dump_vector<DimensionSet::coord_type>(pos1)
                      << " → " << std::setw(2) << std::right << pos2 << " → "
                      << dump_vector<DimensionSet::coord_type>(pos3) << '\n';
            // Check round trip results in same indices.
            ASSERT_TRUE(pos1 == pos3);

            // Check the subrange (which may be the full range)
            {
              DimensionSet::coord_type full_index = pos1;
              for (std::vector<Dimension>::size_type d = 0; d < set.size(); ++d)
                {
                  full_index[d] += logical[d].begin;
                }
              ASSERT_TRUE(full_set.storage_index(full_index) == pos2);
            }
          }
        std::cout << '\n';
      }
  }

}

// Dimension construction and failure cases.
TEST(Dimension, Construct)
{
  Dimension x("X");
  ASSERT_EQ(std::string("X"), x.name);
  ASSERT_EQ(1U, x.extent);

  Dimension t("T", 43U);

  ASSERT_EQ(std::string("T"), t.name);
  ASSERT_EQ(43U, t.extent);
  ASSERT_EQ(0U, t.begin);
  ASSERT_EQ(43U, t.end);

  // Zero extent size is invalid.
  ASSERT_THROW(Dimension("InvalidSize", 0), std::logic_error);
}

// Dimension subrange construction and failure cases.
TEST(Dimension, ConstructSubrange)
{
  Dimension x("X", 64);
  ASSERT_EQ(64U, x.extent);
  ASSERT_EQ(64U, x.size());

  Dimension x2(x, 16, 48);
  ASSERT_EQ(64U, x2.extent);
  ASSERT_EQ(32U, x2.size());
  ASSERT_EQ(16U, x2.begin);
  ASSERT_EQ(48U, x2.end);

  // Outside full range.
  ASSERT_THROW(Dimension("X", 64U, 32U, 128U), std::logic_error);
  // Outside subrange.
  ASSERT_THROW(Dimension(x2, 0U, 56U), std::logic_error);
}

// Dimension comparison by lexical ordering.
TEST(Dimension, Compare)
{
  Dimension x("X");
  Dimension t("T");

  ASSERT_TRUE(t < x);
  ASSERT_FALSE(x < t);

  Dimension y("Y", 32);
  Dimension c("C", 84);

  ASSERT_TRUE(c < y);
  ASSERT_FALSE(y < c);
}

// DimensionSet construction and failure cases.
TEST(DimensionSet, Construct)
{
  const Dimension static_dims[] =
    {
      Dimension("X", 3),
      Dimension("Y", 4),
      Dimension("Z", 2)
    };

  std::vector<Dimension> dims(static_dims, static_dims+boost::size(static_dims));

  DimensionSet set(dims);

  ASSERT_EQ(3U, set.size());

  // Duplicate dimension names are invalid.
  dims.push_back(Dimension("Y", 54));
  ASSERT_THROW(DimensionSet set2(dims), std::logic_error);
}

// Round-trip of logical index to logical coordinate.
TEST(DimensionSet, LogicalIndex)
{
  const Dimension static_dims[] =
    {
      Dimension("X", 3),
      Dimension("Y", 4),
      Dimension("Z", 2)
    };

  std::vector<Dimension> dims(static_dims, static_dims+boost::size(static_dims));

  DimensionSet set(dims);

  for (DimensionSet::size_type i = 0; i < set.num_elements(); ++i)
    {
      DimensionSet::coord_type coord;
      set.logical_coord(i, coord);
      DimensionSet::size_type i2 = set.logical_index(coord);

      std::cout << std::setw(2) << std::right << i << " → "
                << dump_vector<DimensionSet::coord_type>(coord)
                << " → " << std::setw(2) << std::right << i2 << '\n';

      ASSERT_EQ(i, i2);
    }
}

// Round-trip of logical coordinate to storage index.
TEST(DimensionSet, StorageIndex)
{
  const Dimension static_dims[] =
    {
      Dimension("X", 3),
      Dimension("Y", 4),
      Dimension("Z", 2)
    };

  std::vector<Dimension> dims(static_dims, static_dims+boost::size(static_dims));

  DimensionSet set(dims);

  for (DimensionSet::index_type i = 0; i < set.num_elements(); ++i)
    {
      DimensionSet::coord_type coord;
      set.logical_coord(i, coord);
      DimensionSet::index_type i2 = set.logical_index(coord);
      DimensionSet::index_type i3 = set.storage_index(coord);

      std::cout << std::setw(2) << std::right << i << " → "
                << dump_vector<DimensionSet::coord_type>(coord)
                << " → " << std::setw(2) << std::right << i3 << '\n';

      ASSERT_EQ(i, i2);
      ASSERT_EQ(i, i3);
    }

  // Failure if incorrect length.
  DimensionSet::coord_type coord;
  coord.push_back(2U);
  coord.push_back(1U);
  ASSERT_THROW(set.storage_index(coord), std::logic_error);
}

// Storage order failure cases (by index).
TEST(DimensionSet, IndexedStorageOrderFail)
{
  const Dimension static_dims[] =
    {
      Dimension("X", 3),
      Dimension("Y", 4),
    };

  std::vector<Dimension> dims(static_dims, static_dims+boost::size(static_dims));

  std::vector<IndexedDimensionStorage> order1;
  order1.push_back(IndexedDimensionStorage(0, Dimension::DESCENDING));

  // Missing dimension.
  ASSERT_THROW(DimensionSet(dims, order1), std::logic_error);

  order1.push_back(IndexedDimensionStorage(1, Dimension::ASCENDING));

  // All dimensions correct.
  ASSERT_NO_THROW(DimensionSet(dims, order1));

  order1.push_back(IndexedDimensionStorage(1, Dimension::ASCENDING));

  // Extra duplicated dimension.
  ASSERT_THROW(DimensionSet(dims, order1), std::logic_error);

  std::vector<IndexedDimensionStorage> order2;
  order2.push_back(IndexedDimensionStorage(0, Dimension::DESCENDING));
  order2.push_back(IndexedDimensionStorage(2, Dimension::DESCENDING));

  // Invalid dimension.
  ASSERT_THROW(DimensionSet(dims, order2), std::logic_error);
}

// Storage order failure cases (by name).
TEST(DimensionSet, NamedStorageOrderFail)
{
  const Dimension static_dims[] =
    {
      Dimension("X", 3),
      Dimension("Y", 4),
    };

  std::vector<Dimension> dims(static_dims, static_dims+boost::size(static_dims));

  std::vector<NamedDimensionStorage> order1;
  order1.push_back(NamedDimensionStorage("X", Dimension::DESCENDING));

  // Missing dimension.
  ASSERT_THROW(DimensionSet(dims, order1), std::logic_error);

  order1.push_back(NamedDimensionStorage("Y", Dimension::ASCENDING));

  // All dimensions correct.
  ASSERT_NO_THROW(DimensionSet(dims, order1));

  order1.push_back(NamedDimensionStorage("Y", Dimension::ASCENDING));

  // Extra duplicated dimension.
  ASSERT_THROW(DimensionSet(dims, order1), std::logic_error);

  std::vector<NamedDimensionStorage> order2;
  order2.push_back(NamedDimensionStorage("X", Dimension::DESCENDING));
  order2.push_back(NamedDimensionStorage("Invalid", Dimension::DESCENDING));

  // Invalid dimension.
  ASSERT_THROW(DimensionSet(dims, order2), std::logic_error);
}

// Storage ordering (single dimension by index).
TEST(DimensionSet, StorageOrderIndexedD1)
{
  std::vector<Dimension> dims;
  dims.push_back(Dimension("Single", 12));

  // Test all 2 combinations
  test_orders(order_permutations(dims, named_order_permutations(dims)));
}

// Storage ordering (two dimensions by index).
TEST(DimensionSet, StorageOrderIndexedD2)
{
  std::vector<Dimension> dims;
  dims.push_back(Dimension("X", 3));
  dims.push_back(Dimension("Y", 4));

  // Test all 8 combinations
  test_orders(order_permutations(dims, indexed_order_permutations(dims)));
}

// Storage ordering (two dimensions by name).
TEST(DimensionSet, StorageOrderNamedD2)
{
  std::vector<Dimension> dims;
  dims.push_back(Dimension("X", 3));
  dims.push_back(Dimension("Y", 4));

  // Test all 8 combinations
  test_orders(order_permutations(dims, named_order_permutations(dims)));
}

// Storage ordering (three dimensions by index).
TEST(DimensionSet, StorageOrderD3)
{
  static Dimension static_dims[] =
    {
      Dimension("X", 5),
      Dimension("Y", 4),
      Dimension("T", 3)
    };

  std::vector<Dimension> dims(static_dims, static_dims+boost::size(static_dims));

  // Test all 48 combinations
  test_orders(order_permutations(dims, indexed_order_permutations(dims)));
}

// Storage ordering (nine dimensions by index).
TEST(DimensionSet, StorageOrderD9)
{
  static Dimension static_dims[] =
    {
      Dimension("PixelX", 10),
      Dimension("PixelY", 10),
      Dimension("Z", 3),
      Dimension("T", 2),
      Dimension("C", 2),
      Dimension("Lifetime", 3),
      Dimension("Angle", 2),
      Dimension("TileX", 2),
      Dimension("TileY", 2)
    };

  std::vector<Dimension> dims(static_dims, static_dims+boost::size(static_dims));

  DimensionSet s1(dims);

  std::cout << "DimensionSet s1 default parameters:\n"
            << s1
            << "\nDimensionSet s1 default storage layout:\n\n";
  dump_array(s1);
  std::cout << '\n';

  std::cout << "s1-1: " << s1 << '\n';
  std::vector<IndexedDimensionStorage> order;
  order.push_back(IndexedDimensionStorage(0, Dimension::ASCENDING));
  order.push_back(IndexedDimensionStorage(5, Dimension::DESCENDING));
  order.push_back(IndexedDimensionStorage(1, Dimension::ASCENDING));
  order.push_back(IndexedDimensionStorage(2, Dimension::ASCENDING));
  order.push_back(IndexedDimensionStorage(3, Dimension::ASCENDING));
  order.push_back(IndexedDimensionStorage(6, Dimension::ASCENDING));
  order.push_back(IndexedDimensionStorage(8, Dimension::ASCENDING));
  order.push_back(IndexedDimensionStorage(7, Dimension::DESCENDING));
  order.push_back(IndexedDimensionStorage(4, Dimension::ASCENDING));

  DimensionSet s2(dims, order);

  std::cout << "DimensionSet s2 modified parameters:\n"
            << s2
            << "\nDimensionSet s2 modified storage layout:\n\n";
  dump_array(s2);
  std::cout << '\n';
}

// Subrange failure cases (by index).
TEST(DimensionSet, IndexedSubrangeFail)
{
  const Dimension static_dims[] =
    {
      Dimension("X", 16),
      Dimension("Y", 32),
    };

  std::vector<Dimension> dims(static_dims, static_dims+boost::size(static_dims));

  DimensionSet::indexed_subrange_type subrange1;
  subrange1.push_back(IndexedDimensionSubrange(0, 4, 12));

  // Missing dimension is OK.
  ASSERT_NO_THROW(DimensionSet(dims).subrange(subrange1));

  subrange1.push_back(IndexedDimensionSubrange(1, 24, 32));

  // All dimensions present.
  ASSERT_NO_THROW(DimensionSet(dims).subrange(subrange1));

  subrange1.push_back(IndexedDimensionSubrange(1, 28, 32));

  // Extra duplicated dimension.
  ASSERT_THROW(DimensionSet(dims).subrange(subrange1), std::logic_error);

  DimensionSet::indexed_subrange_type subrange2;
  subrange2.push_back(IndexedDimensionSubrange(0, 4, 12));
  subrange2.push_back(IndexedDimensionSubrange(2, 90, 12));

  // Invalid dimension.
  ASSERT_THROW(DimensionSet(dims).subrange(subrange2), std::logic_error);
}

// Subrange failure cases (by name).
TEST(DimensionSet, NamedSubrangeFail)
{
  const Dimension static_dims[] =
    {
      Dimension("X", 16),
      Dimension("Y", 32),
    };

  std::vector<Dimension> dims(static_dims, static_dims+boost::size(static_dims));

  DimensionSet::named_subrange_type subrange1;
  subrange1.push_back(NamedDimensionSubrange("X", 4, 12));

  // Missing dimension is OK.
  ASSERT_NO_THROW(DimensionSet(dims).subrange(subrange1));

  subrange1.push_back(NamedDimensionSubrange("Y", 24, 32));

  // All dimensions present.
  ASSERT_NO_THROW(DimensionSet(dims).subrange(subrange1));

  subrange1.push_back(NamedDimensionSubrange("Y", 28, 32));

  // Extra duplicated dimension.
  ASSERT_THROW(DimensionSet(dims).subrange(subrange1), std::logic_error);

  DimensionSet::named_subrange_type subrange2;
  subrange2.push_back(NamedDimensionSubrange("X", 4, 12));
  subrange2.push_back(NamedDimensionSubrange("Invalid", 90, 12));

  // Invalid dimension.
  ASSERT_THROW(DimensionSet(dims).subrange(subrange2), std::logic_error);
}

// Subrange storage ordering (one dimension by index).
TEST(DimensionSet, SubrangeIndexedD1)
{
  std::vector<Dimension> dims;
  dims.push_back(Dimension("Single", 12));

  std::vector<DimensionSet> sets(order_permutations(dims, named_order_permutations(dims)));
  DimensionSet::indexed_subrange_type subrange;
  subrange.push_back(IndexedDimensionSubrange(0, 5, 9));

  std::vector<DimensionSet> subrange_sets;
  for(std::vector<DimensionSet>::const_iterator i = sets.begin();
      i != sets.end();
      ++i)
    {
      subrange_sets.push_back(i->subrange(subrange));
    }

  test_orders(subrange_sets);

  DimensionSet::indexed_subrange_type subrange2;
  subrange2.push_back(IndexedDimensionSubrange(0, 1, 3));

  std::vector<DimensionSet> subrange_sets2;
  for(std::vector<DimensionSet>::const_iterator i = subrange_sets.begin();
      i != subrange_sets.end();
      ++i)
    {
      subrange_sets2.push_back(i->subrange(subrange2));
    }

  test_orders(subrange_sets2);
}

// Subrange storage ordering (two dimensions by index).
TEST(DimensionSet, SubrangeIndexedD2)
{
  std::vector<Dimension> dims;
  dims.push_back(Dimension("X", 3));
  dims.push_back(Dimension("Y", 4));

  std::vector<DimensionSet> sets(order_permutations(dims, named_order_permutations(dims)));
  DimensionSet::indexed_subrange_type subrange;
  subrange.push_back(IndexedDimensionSubrange(0, 0, 2));
  subrange.push_back(IndexedDimensionSubrange(1, 1, 3));

  std::vector<DimensionSet> subrange_sets;
  for(std::vector<DimensionSet>::const_iterator i = sets.begin();
      i != sets.end();
      ++i)
    {
      subrange_sets.push_back(i->subrange(subrange));
    }

  test_orders(subrange_sets);
}

// Subrange storage ordering (two dimensions by name).
TEST(DimensionSet, SubrangeNamedD2)
{
  std::vector<Dimension> dims;
  dims.push_back(Dimension("X", 3));
  dims.push_back(Dimension("Y", 4));

  std::vector<DimensionSet> sets(order_permutations(dims, named_order_permutations(dims)));
  DimensionSet::named_subrange_type subrange;
  // Single dimension only; X is unchanged.
  subrange.push_back(NamedDimensionSubrange("Y", 1, 3));

  std::vector<DimensionSet> subrange_sets;
  for(std::vector<DimensionSet>::const_iterator i = sets.begin();
      i != sets.end();
      ++i)
    {
      subrange_sets.push_back(i->subrange(subrange));
    }

  test_orders(subrange_sets);
}

// Subrange storage ordering (four dimensions by index).
TEST(DimensionSet, SubrangeIndexedD4)
{
  std::vector<Dimension> dims;
  dims.push_back(Dimension("X", 8));
  dims.push_back(Dimension("Y", 8));
  dims.push_back(Dimension("Z", 3));
  dims.push_back(Dimension("C", 2));

  std::vector<DimensionSet> sets(order_permutations(dims, named_order_permutations(dims)));
  DimensionSet::indexed_subrange_type subrange;
  subrange.push_back(IndexedDimensionSubrange(0, 0, 8));
  subrange.push_back(IndexedDimensionSubrange(1, 3, 6));
  subrange.push_back(IndexedDimensionSubrange(2, 0, 2));
  subrange.push_back(IndexedDimensionSubrange(3, 1, 2));

  std::vector<DimensionSet> subrange_sets;
  for(std::vector<DimensionSet>::const_iterator i = sets.begin();
      i != sets.end();
      ++i)
    {
      subrange_sets.push_back(i->subrange(subrange));
    }

  test_orders(subrange_sets);

  DimensionSet::indexed_subrange_type subrange2;
  subrange2.push_back(IndexedDimensionSubrange(0, 4, 7));
  subrange2.push_back(IndexedDimensionSubrange(1, 1, 3));

  std::vector<DimensionSet> subrange_sets2;
  for(std::vector<DimensionSet>::const_iterator i = sets.begin();
      i != sets.end();
      ++i)
    {
      subrange_sets2.push_back(i->subrange(subrange).subrange(subrange2));
    }

  test_orders(subrange_sets2);
}
