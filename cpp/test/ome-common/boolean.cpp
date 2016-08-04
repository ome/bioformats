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

#include <ome/common/boolean.h>

#include <ome/test/test.h>

using ome::common::boolean;

// Verify underlying bit pattern is 0xFF
void
verify_true(const boolean& value)
{
  const uint8_t& raw(*reinterpret_cast<const uint8_t *>(&value));
  ASSERT_EQ(std::numeric_limits<uint8_t>::max(), raw);
}

// Verify underlying bit pattern is 0x00
void
verify_false(const boolean& value)
{
  const uint8_t& raw(*reinterpret_cast<const uint8_t *>(&value));
  ASSERT_EQ(std::numeric_limits<uint8_t>::min(), raw);
}

TEST(Boolean, NumericLimits)
{
  ASSERT_EQ(0, std::numeric_limits<uint8_t>::min());
  ASSERT_EQ(255, std::numeric_limits<uint8_t>::max());
}

TEST(Boolean, DefaultConstruct)
{
  boolean b;
  ASSERT_FALSE(b);
}

TEST(Boolean, ValueConstruct)
{
  boolean t(true);
  boolean f(false);
  boolean tv = true;
  boolean fv = false;
  ASSERT_TRUE(t);
  ASSERT_FALSE(f);
  ASSERT_TRUE(tv);
  ASSERT_FALSE(fv);
  verify_true(t);
  verify_false(f);
  verify_true(tv);
  verify_false(fv);
}

TEST(Boolean, CopyConstruct)
{
  boolean t(true);
  boolean f(false);
  ASSERT_TRUE(t);
  ASSERT_FALSE(f);
  verify_true(t);
  verify_false(f);
  boolean tc(t);
  boolean fc(f);
  ASSERT_TRUE(tc);
  ASSERT_FALSE(fc);
  verify_true(tc);
  verify_false(fc);
}

TEST(Boolean, BoolCast)
{
  boolean d;
  boolean t(true);
  boolean f(false);
  ASSERT_TRUE(static_cast<bool>(d) == false);
  ASSERT_TRUE(static_cast<bool>(t) == true);
  ASSERT_TRUE(static_cast<bool>(f) == false);
}

TEST(Boolean, BoolAssign)
{
  boolean b;
  ASSERT_FALSE(b);
  verify_false(b);
  b = true;
  ASSERT_TRUE(b);
  verify_true(b);
  b = false;
  ASSERT_FALSE(b);
  verify_false(b);
}

TEST(Boolean, BooleanAssign)
{
  const boolean t(true);
  const boolean f(false);
  boolean b;
  ASSERT_FALSE(b);
  verify_false(b);
  b = t;
  ASSERT_TRUE(b);
  verify_true(b);
  b = f;
  ASSERT_FALSE(b);
  verify_false(b);
}

TEST(Boolean, BoolEquals)
{
  boolean b;
  ASSERT_TRUE(b == false);
  ASSERT_TRUE(false == b);
  b = true;
  ASSERT_TRUE(b == true);
  ASSERT_TRUE(true == b);
  b = false;
  ASSERT_TRUE(b == false);
  ASSERT_TRUE(false == b);
}

TEST(Boolean, BooleanEquals)
{
  const boolean t(true);
  const boolean f(false);
  boolean b;
  ASSERT_EQ(f, b);
  ASSERT_EQ(b, f);
  b = true;
  ASSERT_EQ(t, b);
  ASSERT_EQ(b, t);
  b = false;
  ASSERT_EQ(f, b);
  ASSERT_EQ(b, f);
}

TEST(Boolean, BoolNotEquals)
{
  boolean b;
  ASSERT_TRUE(b != true);
  ASSERT_TRUE(true != b);
  b = true;
  ASSERT_TRUE(b != false);
  ASSERT_TRUE(false != b);
  b = false;
  ASSERT_TRUE(b != true);
  ASSERT_TRUE(true != b);
}

TEST(Boolean, BooleanNotEquals)
{
  const boolean t(true);
  const boolean f(false);
  boolean b;
  ASSERT_NE(t, b);
  ASSERT_NE(b, t);
  b = true;
  ASSERT_NE(f, b);
  ASSERT_NE(b, f);
  b = false;
  ASSERT_NE(t, b);
  ASSERT_NE(b, t);
}

TEST(Boolean, BooleanNot)
{
  boolean b;
  ASSERT_TRUE(!b);
  b = true;
  ASSERT_FALSE(!b);
  b = false;
  ASSERT_TRUE(!b);
}

TEST(Boolean, StorageSize)
{
  ASSERT_EQ(sizeof(boolean::value_type), sizeof(boolean));
}

TEST(Boolean, Packing)
{
  boolean::value_type ia[16] = {0};
  boolean ca[16] = {0};
  ASSERT_EQ(sizeof(ia), sizeof(ca));
}

TEST(Boolean, StorageBits)
{
  const boolean t(true);
  const boolean f(false);

  ASSERT_EQ(std::numeric_limits<uint8_t>::min(),
            *reinterpret_cast<const uint8_t *>(&f));
  ASSERT_EQ(std::numeric_limits<uint8_t>::max(),
            *reinterpret_cast<const uint8_t *>(&t));
}

TEST(Boolean, StreamOutput)
{
  std::ostringstream bs;
  bs << bool() << '/' << false << '/' << true << '\n';

  std::ostringstream bcs;
  bcs << boolean() << '/' << boolean(false) << '/' << boolean(true) << '\n';

  ASSERT_EQ(bs.str(), bcs.str());
}

TEST(Boolean, StreamInput)
{
  std::string s("0/1");
  bool bt;
  bool bf;
  char bslash;
  std::istringstream bi(s);
  bi >> bf >> bslash >> bt;

  boolean bct;
  boolean bcf;
  char bcslash;
  std::istringstream bci(s);
  bci >> bcf >> bcslash >> bct;

  ASSERT_EQ(bf, bcf);
  ASSERT_FALSE(bf);
  ASSERT_FALSE(bcf);
  verify_false(bf);
  verify_false(bcf);

  ASSERT_EQ(bt, bct);
  ASSERT_TRUE(bt);
  ASSERT_TRUE(bct);
  verify_true(bt);
  verify_true(bct);

  ASSERT_EQ(bslash, bcslash);
  ASSERT_EQ('/', bslash);
  ASSERT_EQ('/', bcslash);
}
