/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2006 - 2016 Open Microscopy Environment:
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

#include <ome/xml/model/primitives/Color.h>

#include <ome/test/test.h>

#include <sstream>
#include <stdexcept>
#include <string>

using ome::xml::model::primitives::Color;

TEST(ColorTest, Create1)
{
  Color c1(0xFE438B12U);

  EXPECT_EQ(c1.getRed(),   0xFE);
  EXPECT_EQ(c1.getGreen(), 0x43);
  EXPECT_EQ(c1.getBlue(),  0x8B);
  EXPECT_EQ(c1.getAlpha(), 0x12);
  EXPECT_EQ(c1,            0xFE438B12U);
  EXPECT_EQ(0xFE438B12U,   c1);
}

TEST(ColorTest, Create2)
{
  Color c1(0xFE438B12U);
  Color c2(43,23,53,1);

  EXPECT_NE(c1, c2);
}

TEST(ColorTest, Create3)
{
  Color c1(0xFE438B12U);
  Color c2(43,23,53,1);
  Color c3("4265839378", false);

  EXPECT_EQ(c3, 0xFE438B12U);
  EXPECT_EQ(c1, c3);
  EXPECT_NE(c2, c3);
}

TEST(ColorTest, CreateFail1)
{
  ASSERT_THROW(Color c1("4265839378"), std::invalid_argument);
}

TEST(ColorTest, CreateFail2)
{
  ASSERT_THROW(Color c1("7453894265839378"), std::invalid_argument);
}

TEST(ColorTest, CreateFail3)
{
  ASSERT_THROW(Color c1("invalid"), std::invalid_argument);
}

TEST(ColorTest, SetComponents)
{
  Color c5(0x01020304U);

  c5.setRed(0x20);
  c5.setGreen(0x21);
  c5.setBlue(0x22);
  c5.setAlpha(0x23);

  EXPECT_EQ(c5, 0x20212223U);
}

TEST(ColorTest, DefaultBlack)
{
  Color black(0, 0, 0, 255);
  Color black2;

  EXPECT_EQ(black, black2);
}

class ColorTestParameters
{
public:
  Color::component_type r;
  Color::component_type g;
  Color::component_type b;
  Color::component_type a;
  Color::composed_type  uval;
  Color::signed_type    sval;
  std::string           str;

  ColorTestParameters(Color::component_type r,
                      Color::component_type g,
                      Color::component_type b,
                      Color::component_type a,
                      Color::composed_type  uval,
                      Color::signed_type    sval,
                      std::string const&    str):
    r(r),
    g(g),
    b(b),
    a(a),
    uval(uval),
    sval(sval),
    str(str)
  {}
};

class ColorTest : public ::testing::TestWithParam<ColorTestParameters>
{
public:
  Color c;

  virtual void SetUp()
  {
    const ColorTestParameters& params = GetParam();
    c = Color(params.r, params.g, params.b, params.a);
  }
};

TEST_P(ColorTest, CompareUnsigned)
{
  const ColorTestParameters& params = GetParam();

  ASSERT_EQ(c, params.uval);
  ASSERT_EQ(params.uval, c);
}

TEST_P(ColorTest, CompareSigned)
{
  const ColorTestParameters& params = GetParam();

  ASSERT_EQ(c, params.sval);
  ASSERT_EQ(params.sval, c);
 }

TEST_P(ColorTest, GetValue)
{
  const ColorTestParameters& params = GetParam();

  ASSERT_EQ(c.getValue(), params.sval);
}

TEST_P(ColorTest, ComposedCast)
{
  const ColorTestParameters& params = GetParam();

  Color::composed_type ct = c;
  ASSERT_EQ(ct, params.uval);
}

TEST_P(ColorTest, SignedCast)
{
  const ColorTestParameters& params = GetParam();

  Color::signed_type st = c;
  ASSERT_EQ(st, params.sval);
 }

TEST_P(ColorTest, GetComponents)
{
  const ColorTestParameters& params = GetParam();

  ASSERT_EQ(c.getRed(), params.r);
  ASSERT_EQ(c.getGreen(), params.g);
  ASSERT_EQ(c.getBlue(), params.b);
  ASSERT_EQ(c.getAlpha(), params.a);
}

TEST_P(ColorTest, ConstructString)
{
  const ColorTestParameters& params = GetParam();

  Color c2(params.str);
  ASSERT_EQ(c, c2);
}

TEST_P(ColorTest, ConstructUnsigned)
{
  const ColorTestParameters& params = GetParam();

  Color c3(params.uval);
  ASSERT_EQ(c, c3);
}

TEST_P(ColorTest, ConstructSigned)
{
  const ColorTestParameters& params = GetParam();

  Color c4(params.sval);
  ASSERT_EQ(c, c4);
}

TEST_P(ColorTest, StreamInput)
{
  const ColorTestParameters& params = GetParam();

  std::istringstream is(params.str);
  is >> c;
  ASSERT_FALSE(!is);
  ASSERT_EQ(c, params.sval);
}

TEST_P(ColorTest, StreamInputFail)
{
  const ColorTestParameters& params = GetParam();

  std::istringstream is("invalid");
  is >> c;
  ASSERT_FALSE(is);
  ASSERT_EQ(c, params.sval); // Original value unchanged on failure
}

TEST_P(ColorTest, StreamOutput)
{
  const ColorTestParameters& params = GetParam();

  std::ostringstream os;
  os << c;
  ASSERT_EQ(os.str(), params.str);
}

ColorTestParameters params[] =
  {
    ColorTestParameters(255,   0,   0, 255, 0xFF0000FFU,  -16776961,  "-16776961"), // Red
    ColorTestParameters(  0, 255,   0, 255, 0x00FF00FFU,   16711935,   "16711935"), // Green
    ColorTestParameters(  0,   0, 255, 255, 0x0000FFFFU,      65535,      "65535"), // Blue
    ColorTestParameters(  0, 255, 255, 255, 0x00FFFFFFU,   16777215,   "16777215"), // Cyan
    ColorTestParameters(255,   0, 255, 255, 0xFF00FFFFU,  -16711681,  "-16711681"), // Magenta
    ColorTestParameters(255, 255,   0, 255, 0xFFFF00FFU,     -65281,     "-65281"), // Yellow
    ColorTestParameters(  0,   0,   0, 255, 0x000000FFU,        255,        "255"), // Black
    ColorTestParameters(255, 255, 255, 255, 0xFFFFFFFFU,         -1,         "-1"), // White
    ColorTestParameters(  0,   0,   0, 127, 0x0000007FU,        127,        "127"), // Transparent black
    ColorTestParameters(127, 127, 127, 127, 0x7F7F7F7FU, 2139062143, "2139062143"), // Grey
  };

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(ColorVariants, ColorTest, ::testing::ValuesIn(params));
