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

#ifndef OME_XML_MODEL_PRIMITIVES_QUANTITY_H
#define OME_XML_MODEL_PRIMITIVES_QUANTITY_H

#include <boost/operators.hpp>

namespace ome
{
  namespace xml
  {
    namespace model
    {
      namespace primitives
      {

        /**
         * A quantity of a defined unit.
         */
        template<class Unit, typename Value = double>
        class Quantity : private boost::partially_ordered<Quantity<Unit, Value>,
                                 boost::equality_comparable<Quantity<Unit, Value>,
                                 boost::addable<Quantity<Unit, Value>,
                                 boost::subtractable<Quantity<Unit, Value>,
                                 boost::dividable2<Quantity<Unit, Value>, Value,
                                 boost::multipliable2<Quantity<Unit, Value>, Value,
                                 boost::incrementable<Quantity<Unit, Value>,
                                 boost::decrementable<Quantity<Unit, Value> > > > > > > > >
        {
        public:
          /// The type of a unit.
          typedef Unit unit_type;
          /// The type of a value.
          typedef Value value_type;

          /**
           * Default construct a Quantity.
           */
          inline
          Quantity ():
            value(),
            unit(typename unit_type::enum_value(0))
          {
          }

          /**
           * Construct a Quantity from a value and unit.
           *
           * @param value the quantity value.
           * @param unit the quantity unit.
           */
          inline
          Quantity (value_type value,
                    unit_type  unit):
            value(value),
            unit(unit)
          {
          }

          /**
           * Copy construct a Quantity.
           *
           * @param quantity the quantity to copy.
           */
          inline
          Quantity(const Quantity& quantity):
            value(quantity.value),
            unit(quantity.unit)
          {
          }

          /// Destructor.
          inline
          ~Quantity ()
          {
          }

          /**
           * Get the value for this quantity.
           *
           * @returns the value.
           */
          value_type
          getValue() const
          {
            return value;
          }

          /**
           * Get the unit for this quantity.
           *
           * @returns the unit.
           */
          unit_type
          getUnit() const
          {
            return unit;
          }

          /**
           * Assign the quantity from a quantity.
           *
           * @param quantity the quantity to assign.
           * @returns the new quantity.
           */
          inline Quantity&
          operator= (const Quantity& quantity)
          {
            this->value = quantity.value;
            this->unit = quantity.unit;
            return *this;
          }

          /**
           * Add a quantity to the quantity.
           *
           * @param quantity the quantity to add.
           * @returns the new quantity.
           */
          inline Quantity&
          operator+= (const Quantity& quantity)
          {
            this->value += convert_value(quantity, this->unit);
            return *this;
          }

          /**
           * Subtract a quantity from the quantity.
           *
           * @param quantity the quantity to subtract.
           * @returns the new quantity.
           */
          inline Quantity&
          operator-= (const Quantity& quantity)
          {
            this->value -= convert_value(quantity, this->unit);
            return *this;
          }

          /**
           * Multiply the quantity by a value.
           *
           * @param value the value to multiply by.
           * @returns the new quantity.
           */
          inline Quantity&
          operator*= (const value_type& value)
          {
            this->value *= value;
            return *this;
          }

          /**
           * Divide the quantity by a value.
           *
           * @param value the value to divide by.
           * @returns the new quantity.
           */
          inline Quantity&
          operator/= (const value_type& value)
          {
            this->value /= value;
            return *this;
          }

          /**
           * Modulo of the quantity by a value.
           *
           * @param value the value to compute the modulus with.
           * @returns the new quantity.
           */
          inline Quantity&
          operator%= (const value_type& value)
          {
            this->value %= value;
            return *this;
          }

          /**
           * Increment the quantity by one.
           *
           * @returns the new value.
           */
          inline Quantity&
          operator++ ()
          {
            ++this->value;
            return *this;
          }

          /**
           * Decrement the quantity by one.
           *
           * @returns the new value.
           */
          inline Quantity&
          operator-- ()
          {
            --this->value;
            return *this;
          }

          /**
           * Check if the quantity is less than a quantity.
           *
           * @param quantity the quantity to compare with.
           * @returns true if the condition is true, else false.
           */
          inline bool
          operator< (const Quantity& quantity) const
          {
            return this->value < convert_value(quantity, this->unit);
          }

          // Note operator> (const Quantity& value) const is
          // provided by Boost.Operators.

          /**
           * Check if the quantity is equal to a quantity.
           *
           * @param quantity the quantity to compare with.
           * @returns true if the condition is true, else false.
           */
          inline bool
          operator== (const Quantity& quantity) const
          {
            return this->value == convert_value(quantity, this->unit);
          }

        private:
          /**
           * Convert a quantity to a specific unit.
           *
           * @param quantity the quantity to convert.
           * @param unit the unit to which to convert the quantity.
           * @returns the converted quantity.
           */
          value_type
          convert_value(const Quantity& quantity,
                        unit_type       unit) const;

          /// Quantity value.
          value_type value;
          /// Quantity unit.
          unit_type unit;
        };

        /**
         * Convert quantity to another unit.
         *
         * @param quantity the quantity to convert.
         * @param unit the unit to which to convert the quantity.
         * @returns the converted quantity.
         * @throws std::logic error if the unit systems are
         * incompatible making conversion impossible.
         */
        template<typename T>
        Quantity<T>
        convert(const Quantity<T>&              quantity,
                typename Quantity<T>::unit_type unit);

        template<class Unit, typename Value>
        inline typename Quantity<Unit, Value>::value_type
        Quantity<Unit, Value>::convert_value(const Quantity<Unit, Value>&              quantity,
                                             typename Quantity<Unit, Value>::unit_type unit) const
        {
          return convert<Quantity<Unit, Value> >(quantity, unit).getValue();
        }

        /**
         * Output Quantity to output stream.
         *
         * @param os the output stream.
         * @param quantity the Quantity to output.
         * @returns the output stream.
         */
        template<class charT, class traits, typename Unit, typename Value>
        inline std::basic_ostream<charT,traits>&
        operator<< (std::basic_ostream<charT,traits>& os,
                    const Quantity<Unit, Value>& quantity)
        {
          return os << quantity.getValue() << ' ' << quantity.getUnit();
        }

        /**
         * Set Quantity from input stream.
         *
         * @param is the input stream.
         * @param quantity the Quantity to set.
         * @returns the input stream.
         */
        template<class charT, class traits, typename Unit, typename Value>
        inline std::basic_istream<charT,traits>&
        operator>> (std::basic_istream<charT,traits>& is,
                    Quantity<Unit, Value>& quantity)
        {
          Value v;
          Unit u(typename Unit::enum_value(0));
          if (is >> v >> u)
            {
              quantity = Quantity<Unit, Value>(v, u);
            }
          return is;
        }

      }
    }
  }
}

#endif // OME_XML_MODEL_PRIMITIVES_QUANTITY_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
