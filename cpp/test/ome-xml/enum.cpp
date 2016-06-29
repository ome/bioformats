#include <ome/xml/model/enums/EnumerationException.h>
#include <ome/xml/model/enums/LaserType.h>
#include <ome/xml/model/enums/PixelType.h>

#include <ome/test/test.h>

#include <sstream>

#include <boost/preprocessor.hpp>

using ome::xml::model::enums::LaserType;
using ome::xml::model::enums::PixelType;
using ome::xml::model::enums::EnumerationException;

TEST(Enum, LaserTypeCreateMetalVapor)
{
  // Construction by value.
  ASSERT_NO_THROW(LaserType lt(LaserType::METALVAPOR));
}

TEST(Enum, LaserTypeCreateExcimer)
{
  // Construction by value.
  ASSERT_NO_THROW(LaserType lt(LaserType::EXCIMER));
}

TEST(Enum, LaserTypeCompareMetalVapor)
{
  LaserType lv1(LaserType::METALVAPOR);
  LaserType lv2(LaserType::METALVAPOR);
  ASSERT_EQ(lv1, lv2);
}

TEST(Enum, LaserTypeCompareMetalVaporExcimer)
{
  LaserType lv1(LaserType::METALVAPOR);
  LaserType lv2(LaserType::EXCIMER);
  ASSERT_NE(lv1, lv2);
}

TEST(Enum, LaserTypeAssign)
{
  LaserType lv1(LaserType::METALVAPOR);
  LaserType lv2(LaserType::EXCIMER);
  LaserType lv3(LaserType::EXCIMER);

  ASSERT_NE(lv1, lv2);
  ASSERT_NE(lv1, lv3);
  ASSERT_EQ(lv2, lv3);

  lv3=lv1;

  ASSERT_NE(lv1, lv2);
  ASSERT_EQ(lv1, lv3);
  ASSERT_NE(lv2, lv3);
}

TEST(Enum, LaserStrings)
{
  const LaserType::string_map_type& strings = LaserType::strings();
  for(LaserType::string_map_type::const_iterator i = strings.begin();
      i != strings.end();
      ++i)
    {
      LaserType l1(i->first);
      LaserType l2(i->second);
      ASSERT_EQ(l1, l2);
      ASSERT_EQ(l1, i->first);
      ASSERT_EQ(l1, i->second);
      ASSERT_EQ(l2, i->first);
      ASSERT_EQ(l2, i->second);
    }
}

TEST(Enum, LaserValues)
{
  const LaserType::value_map_type& values = LaserType::values();
  for(LaserType::value_map_type::const_iterator i = values.begin();
      i != values.end();
      ++i)
    {
      LaserType l1(i->first);
      LaserType l2(i->second);
      ASSERT_EQ(l1, l2);
      ASSERT_EQ(l1, i->first);
      ASSERT_EQ(l1, i->second);
      ASSERT_EQ(l2, i->first);
      ASSERT_EQ(l2, i->second);
    }
}

class EnumStringParameters
{
public:
  std::string name;
  bool        valid;
  bool        insensitive;
  bool        fallback;

  EnumStringParameters(std::string const& name,
                       bool               valid,
                       bool               insensitive,
                       bool               fallback):
    name(name),
    valid(valid),
    insensitive(insensitive),
    fallback(fallback)
 {}
};

class EnumString : public ::testing::TestWithParam<EnumStringParameters>
{
};

TEST_P(EnumString, Construct)
{
  const EnumStringParameters& params = GetParam();

  if (params.valid)
    {
      ASSERT_NO_THROW(LaserType(params.name));
    }
  else if (params.insensitive || params.fallback)
    {
      ASSERT_THROW(LaserType(params.name), EnumerationException);

      if (params.insensitive)
        {
          ASSERT_THROW(LaserType(params.name, true), EnumerationException);
          ASSERT_NO_THROW(LaserType(params.name, false));
        }
      else
        {
          ASSERT_THROW(LaserType(params.name, true), EnumerationException);
          // Classes with an "Other" enumeration will not throw.
          ASSERT_NO_THROW(LaserType(params.name, false));
        }
    }
  else
    {
      ASSERT_THROW(LaserType(params.name, true), EnumerationException);
    }
}

TEST_P(EnumString, Compare)
{
  const EnumStringParameters& params = GetParam();

  if (!params.valid && !params.insensitive && !params.fallback)
    return;

  LaserType lv1(LaserType::EXCIMER);
  LaserType lv2(LaserType::SOLIDSTATE);
  LaserType lv3(LaserType::OTHER);


  LaserType ln(params.name, !(params.insensitive || params.fallback));

  if (params.valid || params.insensitive)
    {

      ASSERT_NE(lv1, lv2);
      ASSERT_NE(lv1, lv3);
      ASSERT_NE(lv1, ln);
      ASSERT_EQ(lv2, ln);
      ASSERT_NE(lv3, ln);

    }
  else if (params.fallback)
    {
      ASSERT_NE(lv1, lv2);
      ASSERT_NE(lv1, lv3);
      ASSERT_NE(lv1, ln);
      ASSERT_NE(lv2, ln);
      ASSERT_EQ(lv3, ln);
    }
}

EnumStringParameters string_params[] =
  {
    EnumStringParameters("SolidState",             true,  false, false),
    EnumStringParameters("solidstate",             false, true,  false),
    EnumStringParameters("soLidsTaTe",             false, true,  false),
    EnumStringParameters("    \tsolidstate",       false, true,  false),
    EnumStringParameters("SolidState      \n",     false, true,  false),
    EnumStringParameters("\f\f  solidstate    \v", false, true,  false),
    EnumStringParameters("InvalidName",            false, false, false),
    EnumStringParameters("--invalid--",            false, false, true),
    EnumStringParameters("invalid3",               false, false, true)
  };


// This enum test is intentionally setting an invalid value, so don't
// warn about it.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wconversion"
#endif
TEST(Enum, LaserTypeCreateInvalidValue)
{
  ASSERT_THROW(LaserType(static_cast<LaserType::enum_value>(50)),
               EnumerationException);
}
#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

template<typename E>
class EnumValueParameters
{
public:
  typedef E enum_type;

  enum_type                      value;
  typename enum_type::enum_value valuepos;
  typename enum_type::enum_value valueneg;
  std::string                    namepos;
  std::string                    nameneg;

  EnumValueParameters(const enum_type&               value,
                      typename enum_type::enum_value valuepos,
                      typename enum_type::enum_value valueneg,
                      const std::string&             namepos,
                      const std::string&             nameneg):
    value(value),
    valuepos(valuepos),
    valueneg(valueneg),
    namepos(namepos),
    nameneg(nameneg)
 {}
};

class LaserTypeValue : public ::testing::TestWithParam<EnumValueParameters<LaserType> >
{
public:
  typedef LaserType enum_type;
  typedef EnumValueParameters<enum_type> param_type;
};

TEST_P(LaserTypeValue, ConstructValue)
{
  const param_type& params = GetParam();

  ASSERT_NO_THROW(enum_type(params.valuepos));
}

TEST_P(LaserTypeValue, ConstructString)
{
  const param_type& params = GetParam();

  ASSERT_NO_THROW(enum_type(params.namepos));
}

TEST_P(LaserTypeValue, CastValue)
{
  const param_type& params = GetParam();

  ASSERT_EQ(static_cast<enum_type::enum_value>(params.value), params.valuepos);
  ASSERT_NE(static_cast<enum_type::enum_value>(params.value), params.valueneg);
}

TEST_P(LaserTypeValue, CastName)
{
  const param_type& params = GetParam();

  ASSERT_EQ(params.namepos, static_cast<const std::string&>(params.value));
  ASSERT_NE(params.nameneg, static_cast<const std::string&>(params.value));
}

TEST_P(LaserTypeValue, Value)
{
  const param_type& params = GetParam();

  enum_type::enum_value etv = params.value;
  ASSERT_EQ(etv, static_cast<enum_type::enum_value>(params.value));
  ASSERT_EQ(etv, params.valuepos);
  ASSERT_NE(etv, params.valueneg);
}

TEST_P(LaserTypeValue, Name)
{
  const param_type& params = GetParam();

  std::string ets = params.value;
  ASSERT_EQ(ets, static_cast<const std::string&>(params.value));
  ASSERT_EQ(ets, params.namepos);
  ASSERT_NE(ets, params.nameneg);
}

TEST_P(LaserTypeValue, CompareValue)
{
  const param_type& params = GetParam();

  ASSERT_EQ(params.value, params.valuepos);
  ASSERT_NE(params.value, params.valueneg);
  ASSERT_EQ(params.valuepos, params.value);
  ASSERT_NE(params.valueneg, params.value);
}

TEST_P(LaserTypeValue, CompareName)
{
  const param_type& params = GetParam();

  ASSERT_EQ(params.value, params.namepos);
  ASSERT_NE(params.value, params.nameneg);
  ASSERT_EQ(params.namepos, params.value);
  ASSERT_NE(params.nameneg, params.value);
}

TEST_P(LaserTypeValue, StreamOutput)
{
  const param_type& params = GetParam();

  std::ostringstream os;
  os << params.value;
  ASSERT_EQ(os.str(), params.namepos);
  ASSERT_NE(os.str(), params.nameneg);
}

TEST_P(LaserTypeValue, StreamInput)
{
  const param_type& params = GetParam();

  std::istringstream is(params.namepos);
  enum_type e(params.nameneg);
  is >> e;
  ASSERT_TRUE(!!is);
  ASSERT_EQ(params.valuepos, e);
  ASSERT_NE(params.valueneg, e);
}

TEST_P(LaserTypeValue, StreamInputFail)
{
  const param_type& params = GetParam();

  std::istringstream is("INVALID_ENUM_VALUE__");
  enum_type e(params.value);
  is >> e;
  ASSERT_TRUE(!!is);
  ASSERT_EQ(LaserType::OTHER, e); // Unchanged.
  ASSERT_NE(params.valueneg, e);
}

typedef EnumValueParameters<LaserType> lts_param;
lts_param lt_value_params[] =
  {
    lts_param(LaserType(LaserType::METALVAPOR),
              LaserType::METALVAPOR, LaserType::EXCIMER,
              "MetalVapor", "Excimer"),
    lts_param(LaserType(LaserType::EXCIMER),
              LaserType::EXCIMER, LaserType::GAS,
              "Excimer", "Gas"),
    lts_param(LaserType(LaserType::SOLIDSTATE),
              LaserType::SOLIDSTATE, LaserType::FREEELECTRON,
              "SolidState", "FreeElectron"),
    lts_param(LaserType(LaserType::OTHER),
              LaserType::OTHER, LaserType::FREEELECTRON,
              "Other", "FreeElectron"),
    lts_param(LaserType("SolidState"),
              LaserType::SOLIDSTATE, LaserType::FREEELECTRON,
              "SolidState", "FreeElectron"),
    lts_param(LaserType("SolidState", true),
              LaserType::SOLIDSTATE, LaserType::FREEELECTRON,
              "SolidState", "FreeElectron"),
    lts_param(LaserType("\tSOLIDStaTe ", false),
              LaserType::SOLIDSTATE, LaserType::FREEELECTRON,
              "SolidState", "FreeElectron"),
  };


template<typename E>
void
verify (E&                     et,
        typename E::enum_value valuepos,
        typename E::enum_value valueneg,
        const std::string&     namepos,
        const std::string&     nameneg)
{
  ASSERT_EQ(static_cast<typename E::enum_value>(et), valuepos);
  ASSERT_NE(static_cast<typename E::enum_value>(et), valueneg);

  ASSERT_EQ(namepos, static_cast<const std::string&>(et));
  ASSERT_NE(nameneg, static_cast<const std::string&>(et));

  typename E::enum_value etv = et;
  ASSERT_EQ(etv, static_cast<typename E::enum_value>(et));
  ASSERT_EQ(etv, valuepos);
  ASSERT_NE(etv, valueneg);

  std::string ets = et;
  ASSERT_EQ(ets, static_cast<const std::string&>(et));
  ASSERT_EQ(ets, namepos);
  ASSERT_NE(ets, nameneg);

  ASSERT_EQ(et, valuepos);
  ASSERT_NE(et, valueneg);
  ASSERT_EQ(valuepos, et);
  ASSERT_NE(valueneg, et);

  ASSERT_EQ(et, namepos);
  ASSERT_NE(et, nameneg);
  ASSERT_EQ(namepos, et);
  ASSERT_NE(nameneg, et);

  std::ostringstream os;
  os << et;
  ASSERT_EQ(os.str(), namepos);
  ASSERT_NE(os.str(), nameneg);
}

TEST(Enum, PixelType)
{
  PixelType p1(PixelType::UINT16);
  verify(p1, PixelType::UINT16, PixelType::DOUBLE, "uint16", "double");

  PixelType p2(PixelType::INT32);
  ASSERT_NE(p2, p1);
  verify(p2, PixelType::INT32, PixelType::DOUBLE, "int32", "double");

  PixelType p3("uint16");
  ASSERT_EQ(p1, p3);
  ASSERT_EQ(p1, p3);
  ASSERT_NE(p2, p3);
  verify(p3, PixelType::UINT16, PixelType::DOUBLE, "uint16", "double");

  ASSERT_THROW(PixelType p4("UInt16"), EnumerationException);
  ASSERT_THROW(PixelType p4("UInt16", true), EnumerationException);
  ASSERT_NO_THROW(PixelType p4("UInt16", false));

  PixelType p4("UInt16", false);
  ASSERT_EQ(p1, p4);
  ASSERT_EQ(p3, p4);
  verify(p4, PixelType::UINT16, PixelType::DOUBLE, "uint16", "double");
}

TEST(Enum, PixelTypeInvalid)
{
  // No fallback to other.
  ASSERT_THROW(PixelType("Invalid"), EnumerationException);
}

TEST(Enum, PixelTypeStreamOutput)
{
  std::ostringstream os;
  os << PixelType("uint32");
  ASSERT_EQ(os.str(), "uint32");
}

TEST(Enum, PixelTypeStreamInput)
{
  std::istringstream is("int8");
  PixelType e("uint16");
  is >> e;
  ASSERT_TRUE(!!is);
  ASSERT_EQ(PixelType::INT8, e);
  ASSERT_NE(PixelType::UINT16, e);
}

TEST(Enum, PixelTypeStreamInputFail)
{
  std::istringstream is("INVALID_ENUM_VALUE__");
  PixelType e("uint16");
  is >> e;
  ASSERT_FALSE(!!is);
  ASSERT_EQ(PixelType::UINT16, e); // Unchanged.
}

namespace
{

  void
  check_pt(PixelType::enum_value pt_expected,
           PixelType pt)
  {
    std::cout << "Test type: " << PixelType(pt) <<'\n';
    ASSERT_EQ(pt_expected, pt);
  }

}

#define MAKE_PT(maR, maProperty, maType)        \
  {                                             \
    PixelType pt(PixelType::maType);            \
    check_pt(PixelType::maType, pt);            \
  }                                             \

TEST(Enum, PixelTypePreprocess)
{
  BOOST_PP_SEQ_FOR_EACH(MAKE_PT, %%, OME_XML_MODEL_ENUMS_PIXELTYPE_VALUES);
}

#undef MAKE_PT

// Nested lists don't work on Windows due to its broken preprocessor.
#ifndef _MSC_VER

#define PP_SEQ_FOR_EACH_R_ID() BOOST_PP_SEQ_FOR_EACH_R
#define PP_DEFER(x) x BOOST_PP_EMPTY()

namespace
{

  void check_nested(LaserType expected_a,
                    LaserType a,
                    LaserType expected_b,
                    LaserType b)
  {
    std::cout << "Test nested: first=" << a
              << " second=" << b << '\n';
    EXPECT_EQ(expected_a, a);
    EXPECT_EQ(expected_b, b);
  }

}

// No switch default to avoid -Wunreachable-code errors.
// However, this then makes -Wswitch-default complain.  Disable
// temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

#define LT_NESTED(maR, maToplevelType, maNestedType)                    \
  case LaserType::maNestedType:                                         \
  {                                                                     \
    check_nested(a, LaserType(LaserType::maToplevelType),               \
                 b, LaserType(LaserType::maNestedType));                \
  }                                                                     \
  break;

#define LT_TOPLEVEL(maR, maUnused, maType)                              \
  case LaserType::maType:                                               \
  {                                                                     \
    switch(b)                                                           \
      {                                                                 \
        PP_DEFER(PP_SEQ_FOR_EACH_R_ID)()(maR, LT_NESTED, maType,        \
                                         OME_XML_MODEL_ENUMS_LASERTYPE_VALUES); \
      }                                                                 \
  }                                                                     \
  break;

namespace
{

  void check_switch(LaserType a,
                    LaserType b)
  {
    switch(a)
      {
        BOOST_PP_EXPAND(BOOST_PP_SEQ_FOR_EACH(LT_TOPLEVEL, %%, OME_XML_MODEL_ENUMS_LASERTYPE_VALUES));
      }
  }

}

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

#define NESTED_TEST(r, product)                                      \
  check_switch(LaserType(LaserType::BOOST_PP_SEQ_ELEM(0, product)),  \
               LaserType(LaserType::BOOST_PP_SEQ_ELEM(1, product)));

TEST(Enum, PixelTypePreprocessNested)
{
  BOOST_PP_SEQ_FOR_EACH_PRODUCT(NESTED_TEST, (OME_XML_MODEL_ENUMS_LASERTYPE_VALUES)(OME_XML_MODEL_ENUMS_LASERTYPE_VALUES));
}

#undef PP_SEQ_FOR_EACH_R_ID
#undef PP_DEFER
#undef LT_NESTED
#undef LT_TOPLEVEL
#undef NESTED_TEST

#endif // !_MSC_VER

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(LaserTypeStringVariants, EnumString,
                        ::testing::ValuesIn(string_params));

INSTANTIATE_TEST_CASE_P(LaserTypeValueVariants, LaserTypeValue,
                        ::testing::ValuesIn(lt_value_params));
