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
#include <ome/bioformats/TileBuffer.h>
#include <ome/bioformats/TileCache.h>

#include <ome/test/test.h>

using ome::bioformats::dimension_size_type;
using ome::bioformats::TileCache;
using ome::bioformats::TileBuffer;

TEST(TileCache, Construct)
{
  TileCache c;
}

TEST(TileCache, Insert)
{
  TileCache c;
  const TileCache& cc(c);

  for (dimension_size_type i = 0; i < 16; ++i)
    {
      ASSERT_TRUE(c.insert(i, ome::compat::shared_ptr<TileBuffer>(new TileBuffer((8192)))));
      ASSERT_TRUE(static_cast<bool>(c.find(i)));
      ASSERT_TRUE(static_cast<bool>(cc.find(i)));
    }

  ASSERT_EQ(16, c.size());
}

TEST(TileCache, InsertFail)
{
  TileCache c;

  for (dimension_size_type i = 0; i < 16; ++i)
    {
      ASSERT_TRUE(c.insert(i, ome::compat::shared_ptr<TileBuffer>(new TileBuffer((8192)))));
    }
  for (dimension_size_type i = 0; i < 16; ++i)
    {
      ASSERT_FALSE(c.insert(i, ome::compat::shared_ptr<TileBuffer>(new TileBuffer((8192)))));
    }
}

TEST(TileCache, IndexOperator)
{
  TileCache c;
  const TileCache& cc(c);

  for (dimension_size_type i = 0; i < 16; ++i)
    {
      ASSERT_FALSE(static_cast<bool>(c[i]));
    }
  ASSERT_EQ(16, c.size());

  c.clear();
  for (dimension_size_type i = 0; i < 16; ++i)
    {
      c[i] = ome::compat::shared_ptr<TileBuffer>(new TileBuffer((8192)));
      ASSERT_TRUE(static_cast<bool>(c[i]));
    }

  ASSERT_EQ(16, c.size());
}

TEST(TileCache, Remove)
{
  TileCache c;
  const TileCache& cc(c);

  for (dimension_size_type i = 0; i < 16; ++i)
    {
      ASSERT_TRUE(c.insert(i, ome::compat::shared_ptr<TileBuffer>(new TileBuffer((8192)))));
      ASSERT_TRUE(static_cast<bool>(c.find(i)));
      ASSERT_TRUE(static_cast<bool>(cc.find(i)));
    }
  for (dimension_size_type i = 0; i < 16; ++i)
    {
      c.erase(i);
      ASSERT_FALSE(static_cast<bool>(c.find(i)));
      ASSERT_FALSE(static_cast<bool>(cc.find(i)));
    }
}

TEST(TileCache, Clear)
{
  TileCache c;

  for (dimension_size_type i = 0; i < 16; ++i)
    {
      ASSERT_TRUE(c.insert(i, ome::compat::shared_ptr<TileBuffer>(new TileBuffer((8192)))));
    }

  ASSERT_EQ(16, c.size());

  c.clear();
  ASSERT_EQ(0, c.size());
}
