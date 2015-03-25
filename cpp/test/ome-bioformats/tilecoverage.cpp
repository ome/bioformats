/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
 * Copyright © 2014 - 2015 Open Microscopy Environment:
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

#include <ome/bioformats/Types.h>
#include <ome/bioformats/TileCoverage.h>

#include <ome/test/test.h>

using ome::bioformats::dimension_size_type;
using ome::bioformats::PlaneRegion;
using ome::bioformats::TileCoverage;

TEST(TileCoverage, Construct)
{
  TileCoverage c;
}

TEST(TileCoverage, InsertGrid)
{
  TileCoverage c;
  for (dimension_size_type x = 0; x < 4096; x += 32)
    for (dimension_size_type y = 0; y < 4096; y += 16)
      {
        PlaneRegion r(x, y, 32, 16);
        ASSERT_TRUE(c.insert(r));
      }
  // One region after automatic coalescing
  ASSERT_EQ(1, c.size());
}

TEST(TileCoverage, InsertGridSeparate)
{
  TileCoverage c;
  for (dimension_size_type x = 0; x < 4096; x += 32)
    for (dimension_size_type y = 0; y < 4096; y += 16)
      {
        PlaneRegion r(x, y, 32, 16);
        ASSERT_TRUE(c.insert(r, false));
      }
  // Full set of regions
  ASSERT_EQ((4096 / 32) * (4096 / 16), c.size());
}

TEST(TileCoverage, InsertFail)
{
  TileCoverage c;
  for (dimension_size_type x = 0; x < 256; x += 32)
    for (dimension_size_type y = 0; y < 256; y += 16)
      {
        PlaneRegion r(x, y, 32, 16);
        ASSERT_TRUE(c.insert(r));
      }
  PlaneRegion f(24, 21, 8, 12);
  ASSERT_FALSE(c.insert(f));
}

TEST(TileCoverage, Remove)
{
  TileCoverage c;
  for (dimension_size_type x = 0; x < 4096; x += 32)
    for (dimension_size_type y = 0; y < 4096; y += 16)
      {
        PlaneRegion r(x, y, 32, 16);
        ASSERT_TRUE(c.insert(r, false));
      }
  // Full set of regions
  ASSERT_EQ((4096 / 32) * (4096 / 16), c.size());

  PlaneRegion remove(64, 64, 32, 16);
  ASSERT_TRUE(c.remove(remove));

  // Full set of regions - 1
  ASSERT_EQ(((4096 / 32) * (4096 / 16)) - 1, c.size());
}

TEST(TileCoverage, RemoveFail)
{
  TileCoverage c;
  for (dimension_size_type x = 0; x < 4096; x += 32)
    for (dimension_size_type y = 0; y < 4096; y += 16)
      {
        PlaneRegion r(x, y, 32, 16);
        ASSERT_TRUE(c.insert(r));
      }
  ASSERT_EQ(1, c.size());

  PlaneRegion remove(64, 64, 32, 16);
  ASSERT_FALSE(c.remove(remove));

  ASSERT_EQ(1, c.size());
}

TEST(TileCoverage, Clear)
{
  TileCoverage c;
  for (dimension_size_type x = 0; x < 4096; x += 32)
    for (dimension_size_type y = 0; y < 4096; y += 16)
      {
        PlaneRegion r(x, y, 32, 16);
        ASSERT_TRUE(c.insert(r, false));
      }
  ASSERT_EQ((4096 / 32) * (4096 / 16), c.size());

  c.clear();

  ASSERT_EQ(0, c.size());
}

// Non-coalesced contiguous tiles
TEST(TileCoverage, CoverageComplete1)
{
  TileCoverage c;
  for (dimension_size_type x = 0; x < 128; x += 16)
    for (dimension_size_type y = 0; y < 128; y += 16)
      {
        PlaneRegion r(x, y, 16, 16);
        ASSERT_TRUE(c.insert(r, false));
      }

  PlaneRegion area1(32,32,32,32);
  ASSERT_EQ(32 * 32, c.coverage(area1));
  ASSERT_TRUE(c.covered(area1));

  PlaneRegion area2(54,23,21,53);
  ASSERT_EQ(21 * 53, c.coverage(area2));
  ASSERT_TRUE(c.covered(area2));
}

// Coalesced contiguous tiles
TEST(TileCoverage, CoverageComplete2)
{
  TileCoverage c;
  for (dimension_size_type x = 0; x < 128; x += 16)
    for (dimension_size_type y = 0; y < 128; y += 16)
      {
        PlaneRegion r(x, y, 16, 16);
        ASSERT_TRUE(c.insert(r, true));
      }

  PlaneRegion area1(32,32,32,32);
  ASSERT_EQ(32 * 32, c.coverage(area1));
  ASSERT_TRUE(c.covered(area1));

  PlaneRegion area2(54,23,21,53);
  ASSERT_EQ(21 * 53, c.coverage(area2));
  ASSERT_TRUE(c.covered(area2));
}

// Coalesced contiguous tiles
TEST(TileCoverage, CoverageIncomplete1)
{
  TileCoverage c;
  PlaneRegion r(16, 16, 16, 16);
  ASSERT_TRUE(c.insert(r, true));

  // No overlap
  PlaneRegion area1(32,32,32,32);
  ASSERT_EQ(0, c.coverage(area1));
  ASSERT_FALSE(c.covered(area1));

  // No overlap
  PlaneRegion area2(64,64,32,32);
  ASSERT_EQ(0, c.coverage(area2));
  ASSERT_FALSE(c.covered(area2));

  // Partial overlap
  PlaneRegion area3(20,20,43,43);
  ASSERT_EQ((32 - 20) * (32 - 20), c.coverage(area3));
  ASSERT_FALSE(c.covered(area3));

  // Complete overlap
  ASSERT_EQ(16 * 16, c.coverage(r));
  ASSERT_TRUE(c.covered(r));
}
