/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
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

#ifndef OME_XML_MODEL_PRIMITIVES_COLOR_H
#define OME_XML_MODEL_PRIMITIVES_COLOR_H

#include <limits>
#include <string>

#include <ome/compat/cstdint.h>

namespace ome
{
  namespace xml
  {
    namespace model
    {
      /**
       * Model primitive data types.  All the classes in this
       * namespace are fundamental data types used by the OME model
       * objects, but are not themselves model objects.  They map to
       * types defined in the OME-XML schema.
       */
      namespace primitives
      {

        /**
         * An RGBA color value.  Internally, this is representated as
         * an unsigned integer comprised of the red, green, blue and
         * alpha components.  Externally, it may be constructed from a
         * signed or unsigned integer, or all of the separate
         * components.  For compatibility reasons, it will default to
         * input and output of a signed integer as its external string
         * representation.
         *
         * Do not inherit this primitive type; it intentionally does
         * not have a virtual destructor.
         *
         * @todo: The use of a signed integer is not a good design.
         * Look at correcting the model to use an unsigned type.
         */
        class Color
        {
        public:
          /// The type of an individual color component (R, G, B, A).
          typedef uint8_t  component_type;
          /// The type of all components composed as a single RGBA value (unsigned).
          typedef uint32_t composed_type;
          /// The type of all components composed as a single RGBA value (signed).
          typedef int32_t  signed_type;

          /**
           * Construct a Color.  Defaults to 0x000000FF (black).
           */
          inline
          Color ():
            value(0x000000FFU)
          {
          }

          /**
           * Construct a Color from an unsigned integer value.
           *
           * @param value the color value.
           */
          inline
          Color (composed_type value):
            value(value)
          {
          }

          /**
           * Construct a Color from a signed integer value.
           *
           * @param value the color value.
           */
          inline
          Color (signed_type value):
            value(static_cast<composed_type>(value))
          {
          }

          /**
           * Construct a Color from separate red, green, blue and
           * alpha components.
           *
           * @param r the red component.
           * @param g the green component.
           * @param b the blue component.
           * @param a the alpha component.
           */
          inline
          Color (component_type r,
                 component_type g,
                 component_type b,
                 component_type a = std::numeric_limits<component_type>::max()):
            value(static_cast<composed_type>(r) << 24U |
                  static_cast<composed_type>(g) << 16U |
                  static_cast<composed_type>(b) <<  8U |
                  static_cast<composed_type>(a) <<  0U)
          {
          }

          /**
           * Construct a Color from a string.  By default, the string
           * contains an signed integer, thoguh this can be changed to
           * unsigned.
           *
           * @param str The string to be parsed.
           * @param sign true if a signed integer, false if an unsigned integer
           * @throws std::invalid_argument if the string is invalid.
           */
          Color (const std::string& str,
                 bool               sign = true);

          inline
          ~Color ()
          {
          }

          /**
           * Get the red component of this color.
           *
           * @returns the red component.
           */
          inline component_type
          getRed () const
          {
            return static_cast<component_type>((this->value >> 24U) & 0xffU);
          }

          /**
           * Set the red component of this color.
           *
           * @param red the value of the red component.
           */
          inline void
          setRed (component_type red)
          {
            this->value &= ~(0xFFU << 24U);
            this->value |= static_cast<composed_type>(red) << 24U;
          }

          /**
           * Get the green component of this color.
           *
           * @returns the green component.
           */
          inline component_type
          getGreen () const
          {
            return static_cast<component_type>((this->value >> 16U) & 0xffU);
          }

          /**
           * Set the green component of this color.
           *
           * @param green the value of the green component.
           */
          inline void
          setGreen (component_type green)
          {
            this->value &= ~(0xffU << 16U);
            this->value |= static_cast<composed_type>(green) << 16U;
          }

          /**
           * Get the blue component of this color.
           *
           * @returns the blue component.
           */
          inline component_type
          getBlue () const
          {
            return static_cast<component_type>((this->value >> 8U) & 0xffU);
          }

          /**
           * Set the blue component of this color.
           *
           * @param blue the value of the blue component.
           */
          inline void
          setBlue (component_type blue)
          {
            this->value &= ~(0xffU << 8U);
            this->value |= static_cast<composed_type>(blue) << 8U;
          }

          /**
           * Get the alpha component of this color.
           *
           * @returns the alpha component.
           */
          inline component_type
          getAlpha () const
          {
            return static_cast<component_type>((this->value >> 0) & 0xffU);
          }

          /**
           * Set the alpha component of this color.
           *
           * @param alpha the value of the alpha component.
           */
          inline void
          setAlpha (component_type alpha)
          {
            this->value &= ~(0xffU << 0);
            this->value |= static_cast<composed_type>(alpha) << 0;
          }

          /**
           * Get the *signed* integer value of this color.
           *
           * @returns the value.
           */
          inline signed_type
          getValue () const
          {
            return static_cast<signed_type>(this->value);
          }

          /**
           * Set the integer value of this color from an unsigned integer.
           *
           * @param value the value to set.
           */
          inline void
          setValue (composed_type value)
          {
            this->value = value;
          }

          /**
           * Set the integer value of this color from a *signed* integer.
           *
           * @param value the value to set.
           */
          inline void
          setValue (signed_type value)
          {
            this->value = static_cast<composed_type>(value);
          }

          /**
           * Cast the color to its value as an unsigned integer.
           *
           * @returns the color value.
           */
          inline
          operator composed_type () const
          {
            return this->value;
          }

          /**
           * Cast the color to its value as a signed integer.
           *
           * @returns the color value.
           */
          inline
          operator signed_type () const
          {
            return static_cast<signed_type>(this->value);
          }

        private:
          /// The color value.
          composed_type value;
        };

        /**
         * Compare two Color objects for equality.
         *
         * @param lhs a Color object.
         * @param rhs a Color object.
         * @returns true if the colors are the same, otherwise false.
         */
        inline bool
        operator== (const Color& lhs,
                    const Color& rhs)
        {
          return static_cast<Color::composed_type>(lhs) == static_cast<Color::composed_type>(rhs);
        }

        /**
         * Compare a Color object with a composed color value for equality.
         *
         * @param lhs a Color object.
         * @param rhs a composed color value.
         * @returns true if the Color object is the same as the color value, otherwise false.
         */
        inline bool
        operator== (const Color&         lhs,
                    Color::composed_type rhs)
        {
          return static_cast<Color::composed_type>(lhs) == rhs;
        }

        /**
         * Compare a composed color value with a Color object for equality.
         *
         * @param lhs a composed color value.
         * @param rhs a Color object.
         * @returns true if the Color object is the same as the color value, otherwise false.
         */
        inline bool
        operator== (Color::composed_type lhs,
                    const Color&         rhs)
        {
          return lhs == static_cast<Color::composed_type>(rhs);
        }

        /**
         * Compare a Color object with a signed composed color value for equality.
         *
         * @param lhs a Color object.
         * @param rhs a signed composed color value.
         * @returns true if the Color object is the same as the color value, otherwise false.
         */
        inline bool
        operator== (const Color&       lhs,
                    Color::signed_type rhs)
        {
          return static_cast<Color::signed_type>(lhs) == rhs;
        }

        /**
         * Compare a signed composed color value with a Color object for equality.
         *
         * @param lhs a signed composed color value.
         * @param rhs a Color object.
         * @returns true if the Color object is the same as the color value, otherwise false.
         */
        inline bool
        operator== (Color::signed_type lhs,
                    const Color&       rhs)
        {
          return lhs == static_cast<Color::signed_type>(rhs);
        }

        /**
         * Compare two Color objects for non-equality.
         *
         * @param lhs a Color object.
         * @param rhs a Color object.
         * @returns true if the colors are not the same, otherwise false.
         */
        inline bool
        operator!= (const Color& lhs,
                    const Color& rhs)
        {
          return static_cast<Color::composed_type>(lhs) != static_cast<Color::composed_type>(rhs);
        }

        /**
         * Compare a Color object with a composed color value for non-equality.
         *
         * @param lhs a Color object.
         * @param rhs a composed color value.
         * @returns true if the Color object is not the same as the color value, otherwise false.
         */
        inline bool
        operator!= (const Color&         lhs,
                    Color::composed_type rhs)
        {
          return static_cast<Color::composed_type>(lhs) != rhs;
        }

        /**
         * Compare a composed color value with a Color object for non-equality.
         *
         * @param lhs a composed color value.
         * @param rhs a Color object.
         * @returns true if the Color object is not the same as the color value, otherwise false.
         */
        inline bool
        operator!= (Color::composed_type lhs,
                    const Color&         rhs)
        {
          return lhs != static_cast<Color::composed_type>(rhs);
        }

        /**
         * Compare a Color object with a signed composed color value for non-equality.
         *
         * @param lhs a Color object.
         * @param rhs a signed composed color value.
         * @returns true if the Color object is not the same as the color value, otherwise false.
         */
        inline bool
        operator!= (const Color&       lhs,
                    Color::signed_type rhs)
        {
          return static_cast<Color::signed_type>(lhs) != rhs;
        }

        /**
         * Compare a signed composed color value with a Color object for non-equality.
         *
         * @param lhs a signed composed color value.
         * @param rhs a Color object.
         * @returns true if the Color object is not the same as the color value, otherwise false.
         */
        inline bool
        operator!= (Color::signed_type lhs,
                    const Color&       rhs)
        {
          return lhs != static_cast<Color::signed_type>(rhs);
        }

        /**
         * Output Color to output stream.
         *
         * @param os the output stream.
         * @param color the Color to output.
         * @returns the output stream.
         */
        template<class charT, class traits>
        inline std::basic_ostream<charT,traits>&
        operator<< (std::basic_ostream<charT,traits>& os,
                    const Color& color)
        {
          return os << static_cast<Color::signed_type>(color);
        }

        /**
         * Set Color from input stream.
         *
         * @param is the input stream.
         * @param color the Color to set.
         * @returns the input stream.
         */
        template<class charT, class traits>
        inline std::basic_istream<charT,traits>&
        operator>> (std::basic_istream<charT,traits>& is,
                    Color& color)
        {
          Color::signed_type c;
          if (is >> c)
            color.setValue(c);
          return is;
        }

      }
    }
  }
}

#endif // OME_XML_MODEL_PRIMITIVES_COLOR_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
