/*
 * #%L
 * OME-COMMON C++ library for C++ compatibility/portability
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

#ifndef OME_COMMON_BOOLEAN_H
#define OME_COMMON_BOOLEAN_H

#include <istream>
#include <limits>
#include <ostream>

#include <ome/compat/cstdint.h>

namespace ome
{
  namespace common
  {

    /**
     * Boolean type with guaranteed size, alignment and storage
     * values.
     *
     * The standard @c bool type has an implementation-defined size,
     * and as such this makes it unsuitable for interoperability with
     * some APIs and libraries when used in arrays and matrices where
     * the size and alignment are required to meet specific criteria.
     * The special-casing of @c bool for some standard library
     * containers is an additional complication where direct
     * addressing and safe iteration and modification are required.
     *
     * This type is compatible with @c bool, but is defined in terms
     * of @c uint8_t, and so is 8 bits in size.  It should have the
     * same alignment requirements (i.e. on byte boundaries as for @c
     * char) and so should permit tight packing and unaligned access.
     * Its value is all bits zero (false, 0x00) or all bits one (true,
     * 0xFF), making it suitable for direct logical masking operations
     * with 8-bit data or as a normalized GL texture, for example.
     */
    class boolean
    {
    public:
      /// Value type for Boolean values.
      typedef uint8_t value_type;

      /**
       * Default construct.
       *
       * The default value is @c false.
       */
      boolean():
        value(std::numeric_limits<uint8_t>::min()) // "false"
      {}

      /**
       * Construct with initial value.
       *
       * @param value the initial value.
       */
      boolean(bool value):
        value(value ? std::numeric_limits<uint8_t>::max() : std::numeric_limits<uint8_t>::min())
      {}

      /**
       * Copy construct.
       *
       * @param value the value to copy.
       */
      boolean(const boolean& value):
        value(value.value)
      {}

      /**
       * Cast to @c bool.
       *
       * @returns the @c bool value.
       */
      operator bool() const
      {
        return value != std::numeric_limits<uint8_t>::min();
      }

      /**
       * Assign value.
       *
       * @param rhs the value to assign.
       * @returns the new value.
       */
      boolean&
      operator=(bool rhs)
      {
        this->value = (rhs ? std::numeric_limits<uint8_t>::max() : std::numeric_limits<uint8_t>::min());
        return *this;
      }

      /**
       * Assign value.
       *
       * @param rhs the value to assign.
       * @returns the new value.
       */
      boolean&
      operator=(const boolean& rhs)
      {
        this->value = rhs.value;
        return *this;
      }

      /**
       * Not operator.
       *
       * @returns the inverted value.
       */
      boolean
      operator!() const
      {
        return !static_cast<bool>(*this);
      }

    private:
      /// The boolean value.
      uint8_t value;
    };

    /**
     * Compare boolean with @c bool for equality.
     *
     * @param lhs the first value to compare.
     * @param rhs the second value to compare.
     * @returns @c true if equal, @c false if not equal.
     */
    inline bool
    operator==(const boolean& lhs,
               bool rhs)
    {
      return static_cast<bool>(lhs) == rhs;
    }

    /**
     * Compare @c bool with boolean for equality.
     *
     * @param lhs the first value to compare.
     * @param rhs the second value to compare.
     * @returns @c true if equal, @c false if not equal.
     */
    inline bool
    operator==(bool lhs,
               const boolean& rhs)
    {
      return lhs == static_cast<bool>(rhs);
    }

    /**
     * Compare boolean with boolean for equality.
     *
     * @param lhs the first value to compare.
     * @param rhs the second value to compare.
     * @returns @c true if equal, @c false if not equal.
     */
    inline bool
    operator==(const boolean& lhs,
               const boolean& rhs)
    {
      return static_cast<bool>(lhs) == static_cast<bool>(rhs);
    }

    /**
     * Compare boolean with @c bool for inequality.
     *
     * @param lhs the first value to compare.
     * @param rhs the second value to compare.
     * @returns @c true if not equal, @c false if equal.
     */
    inline bool
    operator!=(const boolean& lhs,
               bool rhs)
    {
      return static_cast<bool>(lhs) != rhs;
    }

    /**
     * Compare @c bool with boolean for inequality.
     *
     * @param lhs the first value to compare.
     * @param rhs the second value to compare.
     * @returns @c true if not equal, @c false if equal.
     */
    inline bool
    operator!=(bool lhs,
               const boolean& rhs)
    {
      return lhs != static_cast<bool>(rhs);
    }

    /**
     * Compare @c bool with boolean for inequality.
     *
     * @param lhs the first value to compare.
     * @param rhs the second value to compare.
     * @returns @c true if not equal, @c false if equal.
     */
    inline bool
    operator!=(const boolean& lhs,
               const boolean& rhs)
    {
      return static_cast<bool>(lhs) != static_cast<bool>(rhs);
    }

    /**
     * Output boolean to output stream.
     *
     * @param os the output stream.
     * @param rhs the boolean to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
    inline std::basic_ostream<charT,traits>&
    operator<< (std::basic_ostream<charT,traits>& os,
                const boolean& rhs)
    {
      return os << static_cast<bool>(rhs);
    }

    /**
     * Set boolean from input stream.
     *
     * @param is the input stream.
     * @param rhs the boolean to set.
     * @returns the input stream.
     */
    template<class charT, class traits>
    inline std::basic_istream<charT,traits>&
    operator>> (std::basic_istream<charT,traits>& is,
                boolean& rhs)
    {

      bool b;
      if (is >> b)
        rhs = b;
      return is;
    }

  }
}

#endif // OME_COMMON_BOOLEAN_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
