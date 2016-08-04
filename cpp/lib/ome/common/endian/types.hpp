//  boost/endian/types.hpp  ------------------------------------------------------------//

//  (C) Copyright Darin Adler 2000
//  (C) Copyright Beman Dawes 2006, 2009

//  Distributed under the Boost Software License, Version 1.0.
//  See http://www.boost.org/LICENSE_1_0.txt

//  See library home page at http://www.boost.org/libs/endian

//--------------------------------------------------------------------------------------//

//  Original design developed by Darin Adler based on classes developed by Mark
//  Borgerding. Four original class templates were combined into a single endian
//  class template by Beman Dawes, who also added the unrolled_byte_loops sign
//  partial specialization to correctly extend the sign when cover integer size
//  differs from endian representation size.

// TODO: When a compiler supporting constexpr becomes available, try possible uses.

#ifndef OME_COMMON_ENDIAN_HPP
#define OME_COMMON_ENDIAN_HPP

#if defined(_MSC_VER)  
# pragma warning(push)  
# pragma warning(disable:4365)  // conversion ... signed/unsigned mismatch
#endif

#ifdef BOOST_ENDIAN_LOG
# include <iostream>
#endif

#if defined(__BORLANDC__) || defined( __CODEGEARC__)
# pragma pack(push, 1)
#endif

#include <boost/config.hpp>
#include <boost/detail/endian.hpp>
#include <ome/common/endian/conversion.hpp>
#define BOOST_MINIMAL_INTEGER_COVER_OPERATORS
#define BOOST_NO_IO_COVER_OPERATORS
#include <ome/common/endian/detail/cover_operators.hpp>
#undef  BOOST_NO_IO_COVER_OPERATORS
#undef  BOOST_MINIMAL_INTEGER_COVER_OPERATORS
#include <boost/type_traits/is_signed.hpp>
#include <boost/cstdint.hpp>
#include <boost/static_assert.hpp>
#include <boost/detail/scoped_enum_emulation.hpp>
#include <iosfwd>
#include <climits>

# if CHAR_BIT != 8
#   error Platforms with CHAR_BIT != 8 are not supported
# endif

# ifdef BOOST_NO_DEFAULTED_FUNCTIONS
#   define BOOST_ENDIAN_DEFAULT_CONSTRUCT {}          // C++03
# else
#   define BOOST_ENDIAN_DEFAULT_CONSTRUCT = default;  // C++0x
# endif

# if defined(BOOST_NO_DEFAULTED_FUNCTIONS) && defined(BOOST_ENDIAN_FORCE_PODNESS)
#   define BOOST_ENDIAN_NO_CTORS
# endif

# ifndef BOOST_ENDIAN_EXPLICIT_CTORS
#   define BOOST_ENDIAN_EXPLICIT_OPT
# else
#   define BOOST_ENDIAN_EXPLICIT_OPT explicit
# endif

//----------------------------------  synopsis  ----------------------------------------//

namespace boost
{
namespace endian
{

#ifndef BOOST_ENDIAN_ORDER_ENUM_DEFINED
  BOOST_SCOPED_ENUM_START(order)
  {
    big, little,
# ifdef  BOOST_BIG_ENDIAN
    native = big
# else
    native = little
# endif
  }; BOOST_SCOPED_ENUM_END
# define BOOST_ENDIAN_ORDER_ENUM_DEFINED
#endif
  BOOST_SCOPED_ENUM_START(align) {no, yes}; BOOST_SCOPED_ENUM_END

  template <BOOST_SCOPED_ENUM(order) Order, typename T, std::size_t n_bits,
    BOOST_SCOPED_ENUM(align) A = align::no>
      class endian;

  // aligned big endian floating point types
  typedef endian<order::big, float, 32, align::yes>       big_align_float32_t;
  typedef endian<order::big, double, 64, align::yes>      big_align_float64_t;

  // aligned little endian floating point types
  typedef endian<order::little, float, 32, align::yes>    little_align_float32_t;
  typedef endian<order::little, double, 64, align::yes>   little_align_float64_t;

  // unaligned big endian floating point types
  typedef endian<order::big, float, 32, align::no>        big_float32_t;
  typedef endian<order::big, double, 64, align::no>       big_float64_t;

  // unaligned little endian floating point types
  typedef endian<order::little, float, 32, align::no>     little_float32_t;
  typedef endian<order::little, double, 64, align::no>    little_float64_t;

  // aligned big endian signed integer types
  typedef endian<order::big, int16_t, 16, align::yes>      big_align_int16_t;
  typedef endian<order::big, int32_t, 32, align::yes>      big_align_int32_t;
  typedef endian<order::big, int64_t, 64, align::yes>      big_align_int64_t;

  // aligned big endian unsigned integer types
  typedef endian<order::big, uint16_t, 16, align::yes>     big_align_uint16_t;
  typedef endian<order::big, uint32_t, 32, align::yes>     big_align_uint32_t;
  typedef endian<order::big, uint64_t, 64, align::yes>     big_align_uint64_t;

  // aligned little endian signed integer types
  typedef endian<order::little, int16_t, 16, align::yes>   little_align_int16_t;
  typedef endian<order::little, int32_t, 32, align::yes>   little_align_int32_t;
  typedef endian<order::little, int64_t, 64, align::yes>   little_align_int64_t;

  // aligned little endian unsigned integer types
  typedef endian<order::little, uint16_t, 16, align::yes>  little_align_uint16_t;
  typedef endian<order::little, uint32_t, 32, align::yes>  little_align_uint32_t;
  typedef endian<order::little, uint64_t, 64, align::yes>  little_align_uint64_t;

  // aligned native endian typedefs are not provided because
  // <cstdint> types are superior for this use case

  // unaligned big endian signed integer types
  typedef endian<order::big, int_least8_t, 8>        big_int8_t;
  typedef endian<order::big, int_least16_t, 16>      big_int16_t;
  typedef endian<order::big, int_least32_t, 24>      big_int24_t;
  typedef endian<order::big, int_least32_t, 32>      big_int32_t;
  typedef endian<order::big, int_least64_t, 40>      big_int40_t;
  typedef endian<order::big, int_least64_t, 48>      big_int48_t;
  typedef endian<order::big, int_least64_t, 56>      big_int56_t;
  typedef endian<order::big, int_least64_t, 64>      big_int64_t;

  // unaligned big endian unsigned integer types
  typedef endian<order::big, uint_least8_t, 8>       big_uint8_t;
  typedef endian<order::big, uint_least16_t, 16>     big_uint16_t;
  typedef endian<order::big, uint_least32_t, 24>     big_uint24_t;
  typedef endian<order::big, uint_least32_t, 32>     big_uint32_t;
  typedef endian<order::big, uint_least64_t, 40>     big_uint40_t;
  typedef endian<order::big, uint_least64_t, 48>     big_uint48_t;
  typedef endian<order::big, uint_least64_t, 56>     big_uint56_t;
  typedef endian<order::big, uint_least64_t, 64>     big_uint64_t;

  // unaligned little endian signed integer types
  typedef endian<order::little, int_least8_t, 8>     little_int8_t;
  typedef endian<order::little, int_least16_t, 16>   little_int16_t;
  typedef endian<order::little, int_least32_t, 24>   little_int24_t;
  typedef endian<order::little, int_least32_t, 32>   little_int32_t;
  typedef endian<order::little, int_least64_t, 40>   little_int40_t;
  typedef endian<order::little, int_least64_t, 48>   little_int48_t;
  typedef endian<order::little, int_least64_t, 56>   little_int56_t;
  typedef endian<order::little, int_least64_t, 64>   little_int64_t;

  // unaligned little endian unsigned integer types
  typedef endian<order::little, uint_least8_t, 8>    little_uint8_t;
  typedef endian<order::little, uint_least16_t, 16>  little_uint16_t;
  typedef endian<order::little, uint_least32_t, 24>  little_uint24_t;
  typedef endian<order::little, uint_least32_t, 32>  little_uint32_t;
  typedef endian<order::little, uint_least64_t, 40>  little_uint40_t;
  typedef endian<order::little, uint_least64_t, 48>  little_uint48_t;
  typedef endian<order::little, uint_least64_t, 56>  little_uint56_t;
  typedef endian<order::little, uint_least64_t, 64>  little_uint64_t;

# ifdef BOOST_BIG_ENDIAN
  // unaligned native endian signed integer types
  typedef big_int8_t   native_int8_t;
  typedef big_int16_t  native_int16_t;
  typedef big_int24_t  native_int24_t;
  typedef big_int32_t  native_int32_t;
  typedef big_int40_t  native_int40_t;
  typedef big_int48_t  native_int48_t;
  typedef big_int56_t  native_int56_t;
  typedef big_int64_t  native_int64_t;

  // unaligned native endian unsigned integer types
  typedef big_uint8_t   native_uint8_t;
  typedef big_uint16_t  native_uint16_t;
  typedef big_uint24_t  native_uint24_t;
  typedef big_uint32_t  native_uint32_t;
  typedef big_uint40_t  native_uint40_t;
  typedef big_uint48_t  native_uint48_t;
  typedef big_uint56_t  native_uint56_t;
  typedef big_uint64_t  native_uint64_t;

  // native endian floating point types
  typedef big_float32_t native_float32_t;
  typedef big_float64_t native_float64_t;
  typedef big_align_float32_t native_align_float32_t;
  typedef big_align_float64_t native_align_float64_t;
# else
  // unaligned native endian signed integer types
  typedef little_int8_t   native_int8_t;
  typedef little_int16_t  native_int16_t;
  typedef little_int24_t  native_int24_t;
  typedef little_int32_t  native_int32_t;
  typedef little_int40_t  native_int40_t;
  typedef little_int48_t  native_int48_t;
  typedef little_int56_t  native_int56_t;
  typedef little_int64_t  native_int64_t;

  // unaligned native endian unsigned integer types
  typedef little_uint8_t   native_uint8_t;
  typedef little_uint16_t  native_uint16_t;
  typedef little_uint24_t  native_uint24_t;
  typedef little_uint32_t  native_uint32_t;
  typedef little_uint40_t  native_uint40_t;
  typedef little_uint48_t  native_uint48_t;
  typedef little_uint56_t  native_uint56_t;
  typedef little_uint64_t  native_uint64_t;

  // native endian floating point types
  typedef little_float32_t native_float32_t;
  typedef little_float64_t native_float64_t;
  typedef little_align_float32_t native_align_float32_t;
  typedef little_align_float64_t native_align_float64_t;
# endif
}  // namespace boost
}  // namespace endian

//----------------------------------  end synopsis  ------------------------------------//

namespace boost
{
namespace endian
{
  namespace detail
  {

    // Unrolled loops for loading and storing streams of bytes.

    template <typename T, std::size_t n_bytes,
      bool sign=boost::is_signed<T>::value >
    struct unrolled_byte_loops
    {
      typedef unrolled_byte_loops<T, n_bytes - 1, sign> next;

      static T load_big(const unsigned char* bytes) BOOST_NOEXCEPT
        { return *(bytes - 1) | (next::load_big(bytes - 1) << 8); }
      static T load_little(const unsigned char* bytes) BOOST_NOEXCEPT
        { return *bytes | (next::load_little(bytes + 1) << 8); }

      static void store_big(char* bytes, T value) BOOST_NOEXCEPT
        {
          *(bytes - 1) = static_cast<char>(value);
          next::store_big(bytes - 1, value >> 8);
        }
      static void store_little(char* bytes, T value) BOOST_NOEXCEPT
        {
          *bytes = static_cast<char>(value);
          next::store_little(bytes + 1, value >> 8);
        }
    };

    template <typename T>
    struct unrolled_byte_loops<T, 1, false>
    {
      static T load_big(const unsigned char* bytes) BOOST_NOEXCEPT
        { return *(bytes - 1); }
      static T load_little(const unsigned char* bytes) BOOST_NOEXCEPT
        { return *bytes; }
      static void store_big(char* bytes, T value) BOOST_NOEXCEPT
        { *(bytes - 1) = static_cast<char>(value); }
      static void store_little(char* bytes, T value) BOOST_NOEXCEPT
        { *bytes = static_cast<char>(value); }

    };

    template <typename T>
    struct unrolled_byte_loops<T, 1, true>
    {
      static T load_big(const unsigned char* bytes) BOOST_NOEXCEPT
        { return *reinterpret_cast<const signed char*>(bytes - 1); }
      static T load_little(const unsigned char* bytes) BOOST_NOEXCEPT
        { return *reinterpret_cast<const signed char*>(bytes); }
      static void store_big(char* bytes, T value)  BOOST_NOEXCEPT
        { *(bytes - 1) = static_cast<char>(value); }
      static void store_little(char* bytes, T value) BOOST_NOEXCEPT
        { *bytes = static_cast<char>(value); }
    };

    template <typename T, std::size_t n_bytes>
    inline
    T load_big_endian(const void* bytes) BOOST_NOEXCEPT
    {
      return unrolled_byte_loops<T, n_bytes>::load_big
        (static_cast<const unsigned char*>(bytes) + n_bytes);
    }

    template <typename T, std::size_t n_bytes>
    inline
    T load_little_endian(const void* bytes) BOOST_NOEXCEPT
    {
      return unrolled_byte_loops<T, n_bytes>::load_little
        (static_cast<const unsigned char*>(bytes));
    }

    template <typename T, std::size_t n_bytes>
    inline
    void store_big_endian(void* bytes, T value) BOOST_NOEXCEPT
    {
      unrolled_byte_loops<T, n_bytes>::store_big
        (static_cast<char*>(bytes) + n_bytes, value);
    }

    template <typename T, std::size_t n_bytes>
    inline
    void store_little_endian(void* bytes, T value) BOOST_NOEXCEPT
    {
      unrolled_byte_loops<T, n_bytes>::store_little
        (static_cast<char*>(bytes), value);
    }

  } // namespace detail

# ifdef BOOST_ENDIAN_LOG
    bool endian_log(true);
# endif

  //  endian class template specializations  -------------------------------------------//

    //  Specializations that represent unaligned bytes.
    //  Taking an integer type as a parameter provides a nice way to pass both
    //  the size and signedness of the desired integer and get the appropriate
    //  corresponding integer type for the interface.

    //  unaligned big endian specialization
    template <typename T, std::size_t n_bits>
    class endian< order::big, T, n_bits, align::no >
      : cover_operators< endian< order::big, T, n_bits >, T >
    {
        BOOST_STATIC_ASSERT( (n_bits/8)*8 == n_bits );
      public:
        typedef T value_type;
#     ifndef BOOST_ENDIAN_NO_CTORS
        endian() BOOST_ENDIAN_DEFAULT_CONSTRUCT
        BOOST_ENDIAN_EXPLICIT_OPT endian(T val) BOOST_NOEXCEPT
        { 
#       ifdef BOOST_ENDIAN_LOG
          if ( endian_log )
            std::cout << "big, unaligned, " << n_bits << "-bits, construct(" << val << ")\n";
#       endif
          detail::store_big_endian<T, n_bits/8>(m_value, val);
        }
#     endif
        endian & operator=(T val) BOOST_NOEXCEPT
          { detail::store_big_endian<T, n_bits/8>(m_value, val); return *this; }
        operator T() const BOOST_NOEXCEPT
        { 
#       ifdef BOOST_ENDIAN_LOG
          if ( endian_log )
            std::cout << "big, unaligned, " << n_bits << "-bits, convert(" << detail::load_big_endian<T, n_bits/8>(m_value) << ")\n";
#       endif
          return detail::load_big_endian<T, n_bits/8>(m_value);
        }
        const char* data() const BOOST_NOEXCEPT  { return m_value; }
      private:
        char m_value[n_bits/8];
    };
 
    //  unaligned float big endian specialization
    template <>
    class endian< order::big, float, 32, align::no >
      : cover_operators< endian< order::big, float, 32 >, float >
    {
      public:
        typedef float value_type;
#     ifndef BOOST_ENDIAN_NO_CTORS
        endian() BOOST_ENDIAN_DEFAULT_CONSTRUCT
        BOOST_ENDIAN_EXPLICIT_OPT endian(value_type val) BOOST_NOEXCEPT
          { detail::big_reverse_copy(val, m_value); }
#     endif
        endian & operator=(value_type val) BOOST_NOEXCEPT
          { detail::big_reverse_copy(val, m_value); return *this; }
        operator value_type() const BOOST_NOEXCEPT
        {
          value_type tmp;
          detail::big_reverse_copy(m_value, tmp);
          return tmp;
        }
        const char* data() const BOOST_NOEXCEPT  { return m_value; }
      private:
        char m_value[sizeof(value_type)];
    };

    //  unaligned double big endian specialization
    template <>
    class endian< order::big, double, 64, align::no >
      : cover_operators< endian< order::big, double, 64 >, double >
    {
      public:
        typedef double value_type;
#     ifndef BOOST_ENDIAN_NO_CTORS
        endian() BOOST_ENDIAN_DEFAULT_CONSTRUCT
        BOOST_ENDIAN_EXPLICIT_OPT endian(value_type val) BOOST_NOEXCEPT
          { detail::big_reverse_copy(val, m_value); }
#     endif
        endian & operator=(value_type val) BOOST_NOEXCEPT
          { detail::big_reverse_copy(val, m_value); return *this; }
        operator value_type() const BOOST_NOEXCEPT
        {
          value_type tmp;
          detail::big_reverse_copy(m_value, tmp);
          return tmp;
        }
        const char* data() const BOOST_NOEXCEPT  { return m_value; }
      private:
        char m_value[sizeof(value_type)];
    };
 
    //  unaligned float little endian specialization
    template <>
    class endian< order::little, float, 32, align::no >
      : cover_operators< endian< order::little, float, 32 >, float >
    {
      public:
        typedef float value_type;
#     ifndef BOOST_ENDIAN_NO_CTORS
        endian() BOOST_ENDIAN_DEFAULT_CONSTRUCT
        BOOST_ENDIAN_EXPLICIT_OPT endian(value_type val) BOOST_NOEXCEPT
          { detail::little_reverse_copy(val, m_value); }
#     endif
        endian & operator=(value_type val) BOOST_NOEXCEPT
          { detail::little_reverse_copy(val, m_value); return *this; }
        operator value_type() const BOOST_NOEXCEPT
        {
          value_type tmp;
          detail::little_reverse_copy(m_value, tmp);
          return tmp;
        }
        const char* data() const BOOST_NOEXCEPT  { return m_value; }
      private:
        char m_value[sizeof(value_type)];
    };

    //  unaligned double little endian specialization
    template <>
    class endian< order::little, double, 64, align::no >
      : cover_operators< endian< order::little, double, 64 >, double >
    {
      public:
        typedef double value_type;
#     ifndef BOOST_ENDIAN_NO_CTORS
        endian() BOOST_ENDIAN_DEFAULT_CONSTRUCT
        BOOST_ENDIAN_EXPLICIT_OPT endian(value_type val) BOOST_NOEXCEPT
          { detail::little_reverse_copy(val, m_value); }
#     endif
        endian & operator=(value_type val) BOOST_NOEXCEPT
          { detail::little_reverse_copy(val, m_value); return *this; }
        operator value_type() const BOOST_NOEXCEPT
        {
          value_type tmp;
          detail::little_reverse_copy(m_value, tmp);
          return tmp;
        }
        const char* data() const BOOST_NOEXCEPT  { return m_value; }
      private:
        char m_value[sizeof(value_type)];
    };

    //  unaligned little endian specialization
    template <typename T, std::size_t n_bits>
    class endian< order::little, T, n_bits, align::no >
      : cover_operators< endian< order::little, T, n_bits >, T >
    {
        BOOST_STATIC_ASSERT( (n_bits/8)*8 == n_bits );
      public:
        typedef T value_type;
#     ifndef BOOST_ENDIAN_NO_CTORS
        endian() BOOST_ENDIAN_DEFAULT_CONSTRUCT
        BOOST_ENDIAN_EXPLICIT_OPT endian(T val) BOOST_NOEXCEPT
        { 
#       ifdef BOOST_ENDIAN_LOG
          if ( endian_log )
            std::cout << "little, unaligned, " << n_bits << "-bits, construct(" << val << ")\n";
#       endif
          detail::store_little_endian<T, n_bits/8>(m_value, val);
        }
#     endif
        endian & operator=(T val) BOOST_NOEXCEPT
          { detail::store_little_endian<T, n_bits/8>(m_value, val); return *this; }
        operator T() const BOOST_NOEXCEPT
        { 
#       ifdef BOOST_ENDIAN_LOG
          if ( endian_log )
            std::cout << "little, unaligned, " << n_bits << "-bits, convert(" << detail::load_little_endian<T, n_bits/8>(m_value) << ")\n";
#       endif
          return detail::load_little_endian<T, n_bits/8>(m_value);
        }
        const char* data() const BOOST_NOEXCEPT  { return m_value; }
      private:
        char m_value[n_bits/8];
    };

  //  align::yes specializations; only n_bits == 16/32/64 supported

    //  aligned big endian specialization
    template <typename T, std::size_t n_bits>
    class endian<order::big, T, n_bits, align::yes>
      : cover_operators<endian<order::big, T, n_bits, align::yes>, T>
    {
        BOOST_STATIC_ASSERT( (n_bits/8)*8 == n_bits );
        BOOST_STATIC_ASSERT( sizeof(T) == n_bits/8 );
      public:
        typedef T value_type;
#     ifndef BOOST_ENDIAN_NO_CTORS
        endian() BOOST_ENDIAN_DEFAULT_CONSTRUCT
        BOOST_ENDIAN_EXPLICIT_OPT endian(T val) BOOST_NOEXCEPT
        {
#       ifdef BOOST_ENDIAN_LOG
          if ( endian_log )
            std::cout << "big, aligned, " << n_bits << "-bits, construct(" << val << ")\n";
#       endif
          m_value = ::boost::endian::big_endian_value(val);
        }

#     endif  
        endian& operator=(T val) BOOST_NOEXCEPT
        {
          m_value = ::boost::endian::big_endian_value(val);
          return *this;
        }
        operator T() const BOOST_NOEXCEPT
        {
#       ifdef BOOST_ENDIAN_LOG
          if ( endian_log )
            std::cout << "big, aligned, " << n_bits << "-bits, convert(" << ::boost::endian::big_endian_value(m_value) << ")\n";
#       endif
          return ::boost::endian::big_endian_value(m_value);
        }
        const char* data() const BOOST_NOEXCEPT  {return reinterpret_cast<const char*>(&m_value);}
      private:
        T m_value;
    };

    //  aligned little endian specialization
    template <typename T, std::size_t n_bits>
    class endian<order::little, T, n_bits, align::yes>
      : cover_operators<endian<order::little, T, n_bits, align::yes>, T>
    {
        BOOST_STATIC_ASSERT( (n_bits/8)*8 == n_bits );
        BOOST_STATIC_ASSERT( sizeof(T) == n_bits/8 );
      public:
        typedef T value_type;
#     ifndef BOOST_ENDIAN_NO_CTORS
        endian() BOOST_ENDIAN_DEFAULT_CONSTRUCT
        BOOST_ENDIAN_EXPLICIT_OPT endian(T val) BOOST_NOEXCEPT
        {
#       ifdef BOOST_ENDIAN_LOG
          if ( endian_log )
            std::cout << "little, aligned, " << n_bits << "-bits, construct(" << val << ")\n";
#       endif
          m_value = ::boost::endian::little_endian_value(val);
        }

#     endif  
        endian& operator=(T val) BOOST_NOEXCEPT
        {
          m_value = ::boost::endian::little_endian_value(val);
          return *this;
        }
        operator T() const BOOST_NOEXCEPT
        {
#       ifdef BOOST_ENDIAN_LOG
          if ( endian_log )
            std::cout << "little, aligned, " << n_bits << "-bits, convert(" << ::boost::endian::little_endian_value(m_value) << ")\n";
#       endif
          return ::boost::endian::little_endian_value(m_value);
        }
        const char* data() const BOOST_NOEXCEPT  {return reinterpret_cast<const char*>(&m_value);}
      private:
        T m_value;
    };

} // namespace endian
} // namespace boost

#if defined(__BORLANDC__) || defined( __CODEGEARC__)
# pragma pack(pop)
#endif

#if defined(_MSC_VER)  
# pragma warning(pop)  
#endif 

#endif // OME_COMMON_ENDIAN_HPP
