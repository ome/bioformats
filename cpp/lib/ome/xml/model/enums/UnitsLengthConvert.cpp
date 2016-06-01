/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2016 Open Microscopy Environment:
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

#include <boost/preprocessor.hpp>

#include <ome/common/units/length.h>

#include <ome/xml/model/enums/UnitsLength.h>
#include <ome/xml/model/primitives/Quantity.h>

using ome::xml::model::enums::UnitsLength;

namespace
{

  using namespace ome::common::units;

  // For future use if portable (to replace the static property structs); tuples are enum name, unit quantity type and unit system ID
#define LENGTH_PROPERTY_LIST                                            \
  ((YOTTAMETER)(yottameter_quantity)(0))                                \
  ((ZETTAMETER)(zettameter_quantity)(0))                                \
  ((EXAMETER)(exameter_quantity)(0))                                    \
  ((PETAMETER)(petameter_quantity)(0))                                  \
  ((TERAMETER)(terameter_quantity)(0))                                  \
  ((GIGAMETER)(gigameter_quantity)(0))                                  \
  ((MEGAMETER)(megameter_quantity)(0))                                  \
  ((KILOMETER)(kilometer_quantity)(0))                                  \
  ((HECTOMETER)(hectometer_quantity)(0))                                \
  ((DECAMETER)(decameter_quantity)(0))                                  \
  ((METER)(meter_quantity)(0))                                          \
  ((DECIMETER)(decimeter_quantity)(0))                                  \
  ((CENTIMETER)(centimeter_quantity)(0))                                \
  ((MILLIMETER)(millimeter_quantity)(0))                                \
  ((MICROMETER)(micrometer_quantity)(0))                                \
  ((NANOMETER)(nanometer_quantity)(0))                                  \
  ((PICOMETER)(picometer_quantity)(0))                                  \
  ((FEMTOMETER)(femtometer_quantity)(0))                                \
  ((ATTOMETER)(attometer_quantity)(0))                                  \
  ((ZEPTOMETER)(zeptometer_quantity)(0))                                \
  ((YOCTOMETER)(yoctometer_quantity)(0))                                \
  ((ANGSTROM)(angstrom_quantity)(0))                                    \
  ((THOU)(thou_quantity)(0))                                            \
  ((LINE)(line_quantity)(0))                                            \
  ((INCH)(inch_quantity)(0))                                            \
  ((FOOT)(foot_quantity)(0))                                            \
  ((YARD)(yard_quantity)(0))                                            \
  ((MILE)(mile_quantity)(0))                                            \
  ((ASTRONOMICALUNIT)(astronomical_unit_quantity)(0))                   \
  ((LIGHTYEAR)(light_year_quantity)(0))                                 \
  ((PARSEC)(parsec_quantity)(0))                                        \
  ((POINT)(point_quantity)(0))                                          \
  ((PIXEL)(pixel_quantity)(1))                                          \
  ((REFERENCEFRAME)(reference_frame_quantity)(2))

  /**
   * Map a given UnitsLength enum to the corresponding language types.
   */
  template<int>
  struct LengthProperties;
  
#define LENGTH_UNIT_CASE(maR, maProperty, maType)                       \
  template<>                                                            \
  struct LengthProperties<UnitsLength::BOOST_PP_SEQ_ELEM(0, maType)>    \
  {                                                                     \
    typedef BOOST_PP_SEQ_ELEM(1, maType) quantity_type;                 \
    static const int system = BOOST_PP_SEQ_ELEM(2, maType);             \
  };

  BOOST_PP_SEQ_FOR_EACH(LENGTH_UNIT_CASE, %%, LENGTH_PROPERTY_LIST)

  /**
   * Test if a given conversion is valid.
   */
  template<int U1, int U2>
  struct LengthConversion
  {
    static const bool valid = LengthProperties<U1>::system == LengthProperties<U2>::system;
  };

  // Convert two units
  template<typename Q, int Src, int Dest>
  inline
  typename boost::enable_if_c<
    LengthConversion<Src, Dest>::valid,
    Q
    >::type
  convert_src_dest(typename Q::value_type v,
                   typename Q::unit_type dest)
  {
    typename LengthProperties<Dest>::quantity_type d(LengthProperties<Src>::quantity_type::from_value(v));
    return Q(quantity_cast<typename Q::value_type>(d), dest);
  }

  // Fail conversion: incompatible unit systems
  template<typename Q, int Src, int Dest>
  inline
  typename boost::disable_if_c<
    LengthConversion<Src, Dest>::valid,
    Q
    >::type
  convert_src_dest(typename Q::value_type /* v */,
                   typename Q::unit_type  /*dest */)
  {
    throw std::logic_error("Unit conversion failed: incompatible unit systems");
  }

// No switch default to avoid -Wunreachable-code errors.
// However, this then makes -Wswitch-default complain.  Disable
// temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

#define DEST_UNIT_CASE(maR, maProperty, maType)                         \
  case UnitsLength::maType:                                             \
  {                                                                     \
    maProperty = convert_src_dest<Q, Src, UnitsLength::maType>(value, dest); \
  }                                                                     \
  break;

  template<typename Q, int Src>
  Q
  convert_dest(typename Q::value_type value,
               typename Q::unit_type dest)
  {
    Q q;

    switch(dest)
      {
        BOOST_PP_SEQ_FOR_EACH(DEST_UNIT_CASE, q, OME_XML_MODEL_ENUMS_UNITSLENGTH_VALUES);
      }

    return q;
  }

#undef DEST_UNIT_CASE

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

}

namespace ome
{
  namespace xml
  {
    namespace model
    {
      namespace primitives
      {
        
        // No switch default to avoid -Wunreachable-code errors.
        // However, this then makes -Wswitch-default complain.  Disable
        // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

#define SRC_UNIT_CASE(maR, maProperty, maType)                                \
        case UnitsLength::maType:                                             \
        maProperty = convert_dest<Quantity<UnitsLength>, UnitsLength::maType>(quantity.getValue(), unit); \
          break;

        Quantity<UnitsLength>
        convert(const Quantity<UnitsLength>&     quantity,
                Quantity<UnitsLength>::unit_type unit)
        {
          Quantity<UnitsLength> q;

          switch(quantity.getUnit())
            {
              BOOST_PP_SEQ_FOR_EACH(SRC_UNIT_CASE, q, OME_XML_MODEL_ENUMS_UNITSLENGTH_VALUES);
            }

          return q;
        }

#undef SRC_UNIT_CASE

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

      }
    }
  }
}
