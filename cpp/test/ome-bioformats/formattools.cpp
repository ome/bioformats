/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
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

#include <stdexcept>
#include <vector>

#include <ome/bioformats/FormatTools.h>

#include <ome/xml/model/enums/DimensionOrder.h>

#include <ome/test/test.h>

using ome::bioformats::dimension_size_type;
using ome::bioformats::getIndex;
using ome::bioformats::getZCTCoords;
using ome::xml::model::enums::DimensionOrder;

typedef std::array<dimension_size_type, 3> dims;

namespace std
{
  template<class charT, class traits>
  inline std::basic_ostream<charT,traits>&
  operator<< (std::basic_ostream<charT,traits>& os,
              const dims& d)
  {
    return os << '(' << d[0] << ',' << d[1] << ',' << d[2] << ')';
  }
}

class DimensionTestParameters
{
public:

  std::string order;
  dims sizes;
  dimension_size_type totalsize;
  dims coords;
  dimension_size_type index;

  DimensionTestParameters(const std::string& order,
                          const dims& sizes,
                          dimension_size_type totalsize,
                          const dims& coords,
                          dimension_size_type index):
    order(order),
    sizes(sizes),
    totalsize(totalsize),
    coords(coords),
    index(index)
  {}
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const DimensionTestParameters& tp)
{
  os << tp.order << " dimsizes=(" << tp.sizes[0] << ',' << tp.sizes[1] << ',' << tp.sizes[2]
     << ") coords=(" << tp.coords[0] << ',' << tp.coords[1] << ',' << tp.coords[2] << ") index= " << tp.index;

  return os;
}

class DimensionTest : public ::testing::TestWithParam<DimensionTestParameters>
{
};

TEST_P(DimensionTest, GetCoords)
{
  const DimensionTestParameters& params = GetParam();

  ASSERT_EQ(params.coords,
            getZCTCoords(params.order,
                         params.sizes[0],
                         params.sizes[1],
                         params.sizes[2],
                         params.totalsize,
                         params.index));
}

TEST_P(DimensionTest, GetIndex)
{
  const DimensionTestParameters& params = GetParam();

  ASSERT_EQ(params.index,
            getIndex(params.order,
                     params.sizes[0],
                     params.sizes[1],
                     params.sizes[2],
                     params.totalsize,
                     params.coords[0],
                     params.coords[1],
                     params.coords[2]));
}

namespace
{

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

  std::vector<DimensionTestParameters>
  init_params()
  {
    std::vector<DimensionTestParameters> params;

    const DimensionOrder::value_map_type& orders = DimensionOrder::values();

    dims sizes;
    sizes[0] = 7;
    sizes[1] = 3;
    sizes[2] = 5;
    dimension_size_type totalsize = sizes[0] * sizes[1] * sizes[2];

    for (DimensionOrder::value_map_type::const_iterator order = orders.begin();
         order != orders.end();
         ++order)
      {
        dimension_size_type d0size, d1size, d2size;

        switch(order->first)
          {
          case DimensionOrder::XYZCT:
            d0size = sizes[0];
            d1size = sizes[1];
            d2size = sizes[2];
            break;
          case DimensionOrder::XYZTC:
            d0size = sizes[0];
            d1size = sizes[2];
            d2size = sizes[1];
            break;
          case DimensionOrder::XYCTZ:
            d0size = sizes[1];
            d1size = sizes[2];
            d2size = sizes[0];
            break;
          case DimensionOrder::XYCZT:
            d0size = sizes[1];
            d1size = sizes[0];
            d2size = sizes[2];
            break;
          case DimensionOrder::XYTCZ:
            d0size = sizes[2];
            d1size = sizes[1];
            d2size = sizes[0];
            break;
          case DimensionOrder::XYTZC:
            d0size = sizes[2];
            d1size = sizes[0];
            d2size = sizes[1];
            break;
          }

        dimension_size_type index = 0;
        for (dimension_size_type d2 = 0; d2 < d2size; ++d2)
          for (dimension_size_type d1 = 0; d1 < d1size; ++d1)
            for (dimension_size_type d0 = 0; d0 < d0size; ++d0)
              {
                dims coords;

                switch(order->first)
                  {
                  case DimensionOrder::XYZCT:
                    coords[0] = d0;
                    coords[1] = d1;
                    coords[2] = d2;
                    break;
                  case DimensionOrder::XYZTC:
                    coords[0] = d0;
                    coords[1] = d2;
                    coords[2] = d1;
                    break;
                  case DimensionOrder::XYCTZ:
                    coords[0] = d2;
                    coords[1] = d0;
                    coords[2] = d1;
                    break;
                  case DimensionOrder::XYCZT:
                    coords[0] = d1;
                    coords[1] = d0;
                    coords[2] = d2;
                    break;
                  case DimensionOrder::XYTCZ:
                    coords[0] = d2;
                    coords[1] = d1;
                    coords[2] = d0;
                    break;
                  case DimensionOrder::XYTZC:
                    coords[0] = d1;
                    coords[1] = d2;
                    coords[2] = d0;
                    break;
                  }

                std::cerr << "DO=" << order->second << "(" << coords[0] << ","<<coords[1]<<","<<coords[2]<<")\n";

                params.push_back(DimensionTestParameters(order->second, sizes, totalsize, coords, index));
                ++index;
              }
      }

    return params;
  }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

}

std::vector<DimensionTestParameters> params(init_params());

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(DimensionVariants, DimensionTest, ::testing::ValuesIn(params));
