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

#ifndef OME_XML_MODEL_DETAIL_PARSE_H
#define OME_XML_MODEL_DETAIL_PARSE_H

#include <ome/xml/model/OMEModelObject.h>

#include <boost/mpl/bool.hpp>
#include <boost/type_traits.hpp>

namespace ome
{
  namespace xml
  {
    namespace model
    {
      namespace detail
      {

        /**
         * Throw a model exception with an informative message.
         *
         * @param text the property text value which failed to parse.
         * @param klass the class owning the property.
         * @param property the property name.
         * @throws a ModelException.
         */
        void
        parse_value_fail(const std::string& text,
                         const std::string& klass,
                         const std::string& property);

        /// Type trait for shared_ptr.
        template <class T>
        struct is_shared_ptr
          : boost::false_type {};

        /// Type trait for shared_ptr.
        template <class T>
        struct is_shared_ptr<ome::compat::shared_ptr<T> >
          : boost::true_type {};

        /**
         * Parse an arbitrary value.
         *
         * @param text the text to parse.
         * @param value the variable to store the parsed value.
         * @param klass the class owning the property.
         * @param property the property name.
         * @throws a ModelException on failure.
         */
        template<typename T>
        inline void
        parse_value(const std::string& text,
                    T&                 value,
                    const std::string& klass,
                    const std::string& property)
        {
          std::istringstream is(text);
          is.imbue(std::locale::classic());

          if (!(is >> value))
            parse_value_fail(text, klass, property);
        }

        /**
         * Parse a Boolean value.
         *
         * @param text the text to parse.
         * @param value the variable to store the parsed value.
         * @param klass the class owning the property.
         * @param property the property name.
         * @throws a ModelException on failure.
         */
        template<>
        inline void
        parse_value<bool>(const std::string& text,
                          bool&              value,
                          const std::string& klass,
                          const std::string& property)
        {
          std::istringstream is(text);
          is.imbue(std::locale::classic());

          if (!(is >> std::boolalpha >> value))
            parse_value_fail(text, klass, property);
        }

    // Disable -Wunused-parameter temporarily.  We can't comment out
    // the names since they are needed for docstrings when used
    // inline.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wunused-parameter"
#endif

        /**
         * Parse a string value.
         *
         * @param text the text to parse.
         * @param value the variable to store the parsed value.
         * @param klass the class owning the property.
         * @param property the property name.
         * @throws a ModelException on failure.
         */
        template<>
        inline void
        parse_value<std::string>(const std::string& text,
                                 std::string&       value,
                                 const std::string& klass,
                                 const std::string& property)
        {
          value = text;
        }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

        /**
         * Parse an arbitrary value.
         *
         * @param text the text to parse.
         * @param klass the class owning the property.
         * @param property the property name.
         * @returns the parsed value.
         * @throws a ModelException on failure.
         */
        template<typename T>
        inline typename boost::disable_if_c<
          is_shared_ptr<T>::value,
          typename boost::remove_const<typename boost::remove_reference<T>::type>::type
          >::type
        parse_value(const std::string& text,
                    const std::string& klass,
                    const std::string& property)
        {
          typedef typename boost::remove_const<typename boost::remove_reference<T>::type>::type raw_type;

          raw_type attr;
          parse_value(text, attr, klass, property);
          return attr;
        }

        /**
         * Parse an arbitrary shared_ptr value.
         *
         * @param text the text to parse.
         * @param klass the class owning the property.
         * @param property the property name.
         * @returns the parsed value.
         * @throws a ModelException on failure.
         */
        template<typename T>
        inline typename boost::enable_if_c<
          is_shared_ptr<T>::value,
          typename boost::remove_const<typename boost::remove_reference<T>::type>::type
          >::type
        parse_value(const std::string& text,
                    const std::string& klass,
                    const std::string& property)
        {
          typedef typename boost::remove_const<typename boost::remove_reference<T>::type>::type raw_type;

          raw_type attr(ome::compat::make_shared<typename raw_type::element_type>());
          parse_value(text, *attr, klass, property);
          return attr;
        }

        /**
         * Set an arbitrary value.
         *
         * The type to set will be obtained from the setter argument
         * type.
         *
         * @param text the text to parse.
         * @param object on which to set the parsed value.
         * @param setter the class method to call to set the parsed
         * value.
         * @param klass the class owning the property.
         * @param property the property name.
         * @throws a ModelException on failure.
         */
        template<class C, typename T>
        inline void
        set_value(const std::string&       text,
                  C&                       object,
                  void               (C::* setter)(T value),
                  const std::string&       klass,
                  const std::string&       property)
        {
          typedef typename boost::remove_const<typename boost::remove_reference<T>::type>::type raw_type;

          raw_type attr(parse_value<raw_type>(text, klass, property));

          (object.*setter)(attr);
        }

        /**
         * Parse an arbitrary quantity.
         *
         * @param text the text to parse.
         * @param unit the unit symbol name to parse.
         * @param klass the class owning the property.
         * @param property the property name.
         * @returns the parsed quantity.
         * @throws a ModelException on failure.
         */
        template<typename T>
        inline typename boost::disable_if_c<
          is_shared_ptr<T>::value,
          typename boost::remove_const<typename boost::remove_reference<T>::type>::type
          >::type
        parse_quantity(const std::string& text,
                       const std::string& unit,
                       const std::string& klass,
                       const std::string& property)
        {
          typedef typename boost::remove_const<typename boost::remove_reference<T>::type>::type raw_type;

          typename raw_type::element_type::value_type v;
          parse_value(text, v, klass, property);
          typename raw_type::element_type::unit_type u(unit);
          raw_type attr(v, u);
          return attr;
        }

        /**
         * Parse an arbitrary shared_ptr quantity.
         *
         * @param text the text to parse.
         * @param unit the unit symbol name to parse.
         * @param klass the class owning the property.
         * @param property the property name.
         * @returns the parsed quantity.
         * @throws a ModelException on failure.
         */
        template<typename T>
        inline typename boost::enable_if_c<
          is_shared_ptr<T>::value,
          typename boost::remove_const<typename boost::remove_reference<T>::type>::type
          >::type
        parse_quantity(const std::string& text,
                       const std::string& unit,
                       const std::string& klass,
                       const std::string& property)
        {
          typedef typename boost::remove_const<typename boost::remove_reference<T>::type>::type raw_type;

          typename raw_type::element_type::value_type v;
          parse_value(text, v, klass, property);
          typename raw_type::element_type::unit_type u(unit);
          raw_type attr(ome::compat::make_shared<typename raw_type::element_type>(v, u));
          return attr;
        }

        /**
         * Set an arbitrary quantity.
         *
         * The type to set will be obtained from the setter argument
         * type.
         *
         * @param text the text to parse.
         * @param unit the unit to use.
         * @param object on which to set the parsed value.
         * @param setter the class method to call to set the parsed
         * value.
         * @param klass the class owning the property.
         * @param property the property name.
         * @throws a ModelException on failure.
         */
        template<class C, typename T>
        inline void
        set_quantity(const std::string&       text,
                     const std::string&       unit,
                     C&                       object,
                     void               (C::* setter)(T value),
                     const std::string&       klass,
                     const std::string&       property)
        {
          typedef typename boost::remove_const<typename boost::remove_reference<T>::type>::type raw_type;

          raw_type attr(parse_quantity<raw_type>(text, unit, klass, property));

          (object.*setter)(attr);
        }

      }
    }
  }
}

#endif // OME_XML_MODEL_DETAIL_PARSE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
