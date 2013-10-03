/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2006 - 2013 Open Microscopy Environment:
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

#ifndef OME_XML_MODEL_PRIMITIVES_CONSTRAINEDNUMERIC_H
#define OME_XML_MODEL_PRIMITIVES_CONSTRAINEDNUMERIC_H

#include <limits>
#include <string>
#include <sstream>

#include <boost/format.hpp>
#include <boost/operators.hpp>

#include <ome/compat/cstdint.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {
      namespace primitives
      {

        /**
         * Default error handler for ConstrainedNumeric.  This does
         * nothing but throw an invalid_argument exception.
         */
        struct ConstrainedNumericError
        {
          template<typename T>
          inline void
          operator() (const T&           value,
                      const std::string& typestr)
          {
            boost::format fmt("‘%1%’ not a supported value of %2%");
            fmt % value % typestr;
            throw std::invalid_argument(fmt.str());
          }
        };

        /**
         * A numeric type constrained to a subrange (or subranges) of
         * its range limits.  This templated class is specialised
         * using a numeric (value) type to specify the type to be
         * constrained, a constraint type to provide a constraint
         * check (a function object) and an error policy type to
         * handle errors when the constraint check fails (a function
         * object).  The default error policy is to throw an
         * std::invalid_argument exception.
         *
         * A ConstrainedNumeric instance should behave almost
         * identically to and be directly subsitutable for the
         * constrained value type.  It is constructable using the
         * value type and implicitly castable to the value type.  It
         * is also constructable from the string representation of the
         * value type, and may be serialised to and from any stream.
         * It also implements the standard numeric operators, so
         * may be used as though it were the value type.
         */
        template<typename N,
                 typename C,
                 typename E = ConstrainedNumericError>
        class ConstrainedNumeric : private boost::partially_ordered<ConstrainedNumeric<N, C, E>, N,
                                           boost::partially_ordered<ConstrainedNumeric<N, C, E>,
                                           boost::equality_comparable2<ConstrainedNumeric<N, C, E>, N,
                                           boost::equality_comparable<ConstrainedNumeric<N, C, E>,
                                           boost::addable2<ConstrainedNumeric<N, C, E>, N,
                                           boost::addable<ConstrainedNumeric<N, C, E>,
                                           boost::subtractable2<ConstrainedNumeric<N, C, E>, N,
                                           boost::subtractable<ConstrainedNumeric<N, C, E>,
                                           boost::dividable2<ConstrainedNumeric<N, C, E>, N,
                                           boost::dividable<ConstrainedNumeric<N, C, E>,
                                           boost::multipliable2<ConstrainedNumeric<N, C, E>, N,
                                           boost::multipliable<ConstrainedNumeric<N, C, E> > > > > > > > > > > > >
        {
        public:
          typedef N value_type;
          typedef C constraint_type;
          typedef E error_policy_type;

          /**
           * Construct a ConstrainedNumeric.  Note that the
           * ConstrainedNumeric value will be set to the default
           * constructed value of the constrained value type.  If this
           * value fails the constraint check, an an error will be
           * triggered according to the error policy, so not all
           * ConstrainedNumeric types are safely default
           * constructable.
           */
          ConstrainedNumeric():
            value()
          {
            check();
          }

          /**
           * Construct a ConstrainedNumeric from an unconstrained
           * value.  An exception may be thrown if the value does not
           * meet the constraints.
           *
           * @param value the unconstrained value to set.
           */
          ConstrainedNumeric(value_type value):
            value(value)
          {
            check();
          }

          /**
           * Construct a ConstrainedNumeric a string value.  An
           * exception may be thrown if the value does not meet the
           * constraints.
           *
           * @param value the string value to set.
           */
          ConstrainedNumeric(const std::string& value)
          {
            std::istringstream is(value);
            is.imbue(std::locale::classic());
            value_type nval;
            is >> nval;
            if (!is)
              {
                error_policy_type error;
                error(value, this->typestr);
              }
            this->value = nval;
            check();
          }

          /**
           * Copy constructor.
           *
           * @param value the unconstrained value to copy.
           */
          ConstrainedNumeric(const ConstrainedNumeric& value):
            value(value)
          {
            check();
          }

          inline
          operator value_type () const
          {
            return this->value;
          }

          inline ConstrainedNumeric&
          operator= (const ConstrainedNumeric& value)
          {
            this->value = value.value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator= (const value_type& value)
          {
            this->value = value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator+= (const ConstrainedNumeric& value)
          {
            this->value += value.value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator-= (const ConstrainedNumeric& value)
          {
            this->value -= value.value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator*= (const ConstrainedNumeric& value)
          {
            this->value *= value.value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator/= (const ConstrainedNumeric& value)
          {
            this->value /= value.value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator%= (const ConstrainedNumeric& value)
          {
            this->value %= value.value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator+= (const value_type& value)
          {
            this->value += value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator-= (const value_type& value)
          {
            this->value -= value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator*= (const value_type& value)
          {
            this->value *= value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator/= (const value_type& value)
          {
            this->value /= value;
            check();
            return *this;
          }

          inline ConstrainedNumeric&
          operator%= (const value_type& value)
          {
            this->value %= value;
            check();
            return *this;
          }

          inline bool
          operator< (const ConstrainedNumeric& value) const
          {
            return this->value < value.value;
          }

          inline bool
          operator< (const value_type& value) const
          {
            return this->value < value;
          }

          // Note operator> (const ConstrainedNumeric& value) const is
          // provided by Boost.Operators.

          inline bool
          operator> (const value_type& value) const
          {
            return this->value > value;
          }

          inline bool
          operator== (const ConstrainedNumeric& value) const
          {
            return this->value == value.value;
          }

          inline bool
          operator== (const value_type& value) const
          {
            return this->value == value;
          }

        private:
          /**
           * Check that the set value meets the required constraints,
           * and if it does not, handle this according to the
           * configured error policy (defaulting to throwing a
           * std::illegal_argument exception).
           */
          inline void
          check ()
          {
            constraint_type constraint;
            if (!constraint(this->value))
              {
                error_policy_type error;
                error(this->value, this->typestr);
              }
          }

          /// The value being constrained.
          value_type value;
          /// The name of the type.  Used for diagnostics only.
          static const std::string typestr;

          template<class _charT,
                   class _traits,
                   typename _N,
                   typename _C,
                   typename _E>
          friend
          std::basic_istream<_charT,_traits>&
          operator>> (std::basic_istream<_charT,_traits>& is,
                      ConstrainedNumeric<_N, _C, _E>&      value);
        };

        template<class charT,
                 class traits,
                 typename N,
                 typename C,
                 typename E>
        inline std::basic_istream<charT,traits>&
        operator>> (std::basic_istream<charT,traits>& is,
                    ConstrainedNumeric<N, C, E>&      value)
        {
          typename ConstrainedNumeric<N, C, E>::value_type nval;
          is >> nval;
          if (!is)
            {
              boost::format fmt("Failed to parse invalid input for %1%");
              fmt % ConstrainedNumeric<N, C, E>::typestr;
              throw std::invalid_argument(fmt.str());
            }

          value = nval;

          return is;
        }

        template<class charT,
                 class traits,
                 typename N,
                 typename C,
                 typename E>
        inline std::basic_ostream<charT,traits>&
        operator<< (std::basic_ostream<charT,traits>&  os,
                    const ConstrainedNumeric<N, C, E>& value)
        {
          return os << static_cast<typename ConstrainedNumeric<N, C, E>::value_type>(value);
        }

      }
    }
  }
}

#endif // OME_XML_MODEL_PRIMITIVES_CONSTRAINEDNUMERIC_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
