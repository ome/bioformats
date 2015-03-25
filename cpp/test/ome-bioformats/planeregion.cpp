/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
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

#include <ome/bioformats/PlaneRegion.h>

#include <ome/test/test.h>

using ome::bioformats::PlaneRegion;

TEST(TIFFTest, RegionIntersection1)
{
  PlaneRegion r1(0, 0, 50, 100);
  PlaneRegion r2(25, 30, 100, 50);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(25U, r3.x);
  EXPECT_EQ(30U, r3.y);
  EXPECT_EQ(25U, r3.w);
  EXPECT_EQ(50U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(25U, r4.x);
  EXPECT_EQ(30U, r4.y);
  EXPECT_EQ(25U, r4.w);
  EXPECT_EQ(50U, r4.h);
}

TEST(TIFFTest, RegionIntersection2)
{
  PlaneRegion r1(0, 0, 100, 100);
  PlaneRegion r2(25, 25, 25, 25);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(25U, r3.x);
  EXPECT_EQ(25U, r3.y);
  EXPECT_EQ(25U, r3.w);
  EXPECT_EQ(25U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(25U, r4.x);
  EXPECT_EQ(25U, r4.y);
  EXPECT_EQ(25U, r4.w);
  EXPECT_EQ(25U, r4.h);
}

TEST(TIFFTest, RegionIntersection3)
{
  PlaneRegion r1(40, 190, 29, 18);
  PlaneRegion r2(40, 190, 29, 18);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(40U, r3.x);
  EXPECT_EQ(190U, r3.y);
  EXPECT_EQ(29U, r3.w);
  EXPECT_EQ(18U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(40U, r4.x);
  EXPECT_EQ(190U, r4.y);
  EXPECT_EQ(29U, r4.w);
  EXPECT_EQ(18U, r4.h);
}

TEST(TIFFTest, RegionIntersection4)
{
  PlaneRegion r1(20, 30, 80, 50);
  PlaneRegion r2(200, 25, 60, 20);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(0U, r3.w);
  EXPECT_EQ(0U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(0U, r4.w);
  EXPECT_EQ(0U, r4.h);
}

TEST(TIFFTest, RegionIntersection5)
{
  PlaneRegion r1(20, 400, 80, 50);
  PlaneRegion r2(30, 25, 60, 45);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(0U, r3.w);
  EXPECT_EQ(0U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(0U, r4.w);
  EXPECT_EQ(0U, r4.h);
}

TEST(TIFFTest, RegionUnion1)
{
  PlaneRegion r1(0, 0, 16, 16);
  PlaneRegion r2(16, 0, 16, 16);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(32U, r3.w);
  EXPECT_EQ(16U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(32U, r4.w);
  EXPECT_EQ(16U, r4.h);
}

TEST(TIFFTest, RegionUnion2)
{
  PlaneRegion r1(0, 0, 16, 16);
  PlaneRegion r2(0, 16, 16, 16);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(16U, r3.w);
  EXPECT_EQ(32U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(16U, r4.w);
  EXPECT_EQ(32U, r4.h);
}

TEST(TIFFTest, RegionUnion3)
{
  PlaneRegion r1(43, 23, 12, 15);
  PlaneRegion r2(55, 23, 44, 15);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(43U, r3.x);
  EXPECT_EQ(23U, r3.y);
  EXPECT_EQ(56U, r3.w);
  EXPECT_EQ(15U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(43U, r4.x);
  EXPECT_EQ(23U, r4.y);
  EXPECT_EQ(56U, r4.w);
  EXPECT_EQ(15U, r4.h);
}

TEST(TIFFTest, RegionUnion4)
{
  PlaneRegion r1(22, 19, 27, 80);
  PlaneRegion r2(22, 99, 27, 11);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(22U, r3.x);
  EXPECT_EQ(19U, r3.y);
  EXPECT_EQ(27U, r3.w);
  EXPECT_EQ(91U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(22U, r4.x);
  EXPECT_EQ(19U, r4.y);
  EXPECT_EQ(27U, r4.w);
  EXPECT_EQ(91U, r4.h);
}

TEST(TIFFTest, RegionUnion5)
{
  // No overlap or common edge
  PlaneRegion r1(43, 23, 12, 15);
  PlaneRegion r2(95, 83, 43, 15);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(0U, r3.w);
  EXPECT_EQ(0U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(0U, r4.w);
  EXPECT_EQ(0U, r4.h);
}

TEST(TIFFTest, RegionUnion6)
{
  // Overlap
  PlaneRegion r1(43, 23, 12, 15);
  PlaneRegion r2(50, 28, 12, 15);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(0U, r3.w);
  EXPECT_EQ(0U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(0U, r4.w);
  EXPECT_EQ(0U, r4.h);
}

TEST(TIFFTest, RegionValid)
{
  // Overlap
  PlaneRegion r1;
  ASSERT_TRUE(!r1.valid());
  ASSERT_FALSE(r1.valid());

  PlaneRegion r2(0, 0, 5, 2);
  ASSERT_TRUE(r2.valid());
  ASSERT_FALSE(!r2.valid());
}

TEST(TIFFTest, RegionArea)
{
  PlaneRegion r1;
  ASSERT_EQ(0U, r1.area());

  PlaneRegion r2(0, 0, 4, 2);
  ASSERT_EQ(4U*2U, r2.area());
}
