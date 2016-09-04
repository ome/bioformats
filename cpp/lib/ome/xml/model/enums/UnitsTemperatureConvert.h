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

#ifndef OME_XML_MODEL_ENUMS_UNITSTEMPERATURECONVERT_H
#define OME_XML_MODEL_ENUMS_UNITSTEMPERATURECONVERT_H

#include <boost/preprocessor.hpp>

#include <ome/common/units/temperature.h>

#include <ome/xml/model/enums/UnitsTemperature.h>
#include <ome/xml/model/primitives/Quantity.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {
      namespace primitives
      {
        namespace detail
        {

          using namespace ::ome::common::units;
          using ::ome::xml::model::enums::UnitsTemperature;

#define OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_PROPERTY_LIST              \
  ((CELSIUS)(celsius_absolute_quantity))                                \
  ((FAHRENHEIT)(fahrenheit_absolute_quantity))                          \
  ((KELVIN)(kelvin_absolute_quantity))                                  \
  ((RANKINE)(rankine_absolute_quantity))

          /**
           * Map a given UnitsTemperature enum to the corresponding language types.
           */
          template<int>
          struct TemperatureProperties;

#define OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_UNIT_CASE(maR, maProperty, maType) \
  template<>                                                            \
  struct TemperatureProperties<UnitsTemperature::BOOST_PP_SEQ_ELEM(0, maType)> \
  {                                                                     \
    typedef BOOST_PP_SEQ_ELEM(1, maType) quantity_type;                 \
  };

          BOOST_PP_SEQ_FOR_EACH(OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_UNIT_CASE, %%, OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_PROPERTY_LIST)

#undef OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_UNIT_CASE
#undef OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_PROPERTY_LIST

          // Convert two units
          template<typename Q, int Src, int Dest>
          Q
          temperature_convert_src_dest(typename Q::value_type v,
                                       typename Q::unit_type  dest)
          {
            typename TemperatureProperties<Dest>::quantity_type d(TemperatureProperties<Src>::quantity_type::from_value(v));
            return Q(quantity_cast<typename Q::value_type>(d), dest);
          }

          // No switch default to avoid -Wunreachable-code errors.
          // However, this then makes -Wswitch-default complain.  Disable
          // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

#define OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_DEST_UNIT_CASE(maR, maProperty, maType) \
  case UnitsTemperature::maType:                                        \
  {                                                                     \
    maProperty = temperature_convert_src_dest<Q, Src, UnitsTemperature::maType>(value, dest); \
  }                                                                     \
  break;

          template<typename Q, int Src>
          Q
          temperature_convert_dest(typename Q::value_type value,
                                   typename Q::unit_type  dest)
          {
            Q q;

            switch(dest)
              {
                BOOST_PP_SEQ_FOR_EACH(OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_DEST_UNIT_CASE, q, OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_VALUES);
              }

            return q;
          }

#undef OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_DEST_UNIT_CASE

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

        }

        // No switch default to avoid -Wunreachable-code errors.
        // However, this then makes -Wswitch-default complain.  Disable
        // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

#define OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_SRC_UNIT_CASE(maR, maProperty, maType) \
        case ome::xml::model::enums::UnitsTemperature::maType:          \
          maProperty = detail::temperature_convert_dest<Quantity<ome::xml::model::enums::UnitsTemperature>, \
                                    ome::xml::model::enums::UnitsTemperature::maType>(quantity.getValue(), unit); \
          break;

        /// @copydoc ome::xml::model::primitives::QuantityConverter
        template<typename Value>
        struct QuantityConverter<ome::xml::model::enums::UnitsTemperature, Value>
        {
          /// @copydoc ome::xml::model::primitives::QuantityConverter::operator()()
          inline
          Quantity<ome::xml::model::enums::UnitsTemperature, Value>
          operator() (const Quantity<ome::xml::model::enums::UnitsTemperature, Value>&              quantity,
                      typename Quantity<ome::xml::model::enums::UnitsTemperature, Value>::unit_type unit) const
          {
            Quantity<ome::xml::model::enums::UnitsTemperature, Value> q;

            switch(quantity.getUnit())
              {
                BOOST_PP_SEQ_FOR_EACH(OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_SRC_UNIT_CASE, q, OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_VALUES);
              }

            return q;
          }
        };

#undef OME_XML_MODEL_ENUMS_UNITSTEMPERATURE_SRC_UNIT_CASE

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

      }
    }
  }
}

#endif // OME_XML_MODEL_ENUMS_UNITSTEMPERATURECONVERT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
