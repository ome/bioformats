//  boost/endian/conversion.hpp  -------------------------------------------------------//

//  Copyright Beman Dawes 2010, 2011

//  Distributed under the Boost Software License, Version 1.0.
//  http://www.boost.org/LICENSE_1_0.txt

#ifndef OME_COMMON_ENDIAN_CONVERTERS_HPP
#define OME_COMMON_ENDIAN_CONVERTERS_HPP

#include <boost/config.hpp>
#include <boost/detail/endian.hpp>
#include <boost/cstdint.hpp>
#include <ome/common/endian/detail/intrinsic.hpp>
#include <boost/detail/scoped_enum_emulation.hpp>
#include <boost/static_assert.hpp>
#include <algorithm>
#include <cstring>  // for memcpy

//------------------------------------- synopsis ---------------------------------------//

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

//--------------------------------------------------------------------------------------//
//                             value returning interface                                //
//                             suggested by Phil Endecott                               //
//--------------------------------------------------------------------------------------//
  
  // reverse byte order (i.e. endianness)
  //   
  inline int8_t   reverse_value(int8_t x) BOOST_NOEXCEPT;
  inline int16_t  reverse_value(int16_t x) BOOST_NOEXCEPT;
  inline int32_t  reverse_value(int32_t x) BOOST_NOEXCEPT;
  inline int64_t  reverse_value(int64_t x) BOOST_NOEXCEPT;
  inline uint8_t  reverse_value(uint8_t x) BOOST_NOEXCEPT;
  inline uint16_t reverse_value(uint16_t x) BOOST_NOEXCEPT;
  inline uint32_t reverse_value(uint32_t x) BOOST_NOEXCEPT;
  inline uint64_t reverse_value(uint64_t x) BOOST_NOEXCEPT;

  //  reverse_value overloads for floating point types as requested by Vicente
  //  Botet and others.
  //  TODO: Track progress of Floating-Point Typedefs Having Specified Widths proposal (N3626)
  inline float    reverse_value(float x) BOOST_NOEXCEPT;
  inline double   reverse_value(double x) BOOST_NOEXCEPT;   

  //  reverse bytes unless native endianness is big
  //  possible names: reverse_unless_native_big, reverse_value_unless_big, reverse_unless_big
  template <class ReversibleValue >
  inline ReversibleValue  big_endian_value(ReversibleValue  x) BOOST_NOEXCEPT;    
    //  Return: x if native endian order is big, otherwise reverse_value(x)

  //  reverse bytes unless native endianness is little
  //  possible names: reverse_unless_native_little, reverse_value_unless_little, reverse_unless_little
  template <class ReversibleValue >
  inline ReversibleValue  little_endian_value(ReversibleValue  x) BOOST_NOEXCEPT; 
    //  Return: x if native endian order is little, otherwise reverse_value(x);

  //  synonyms based on names popularized by BSD, e.g. OS X, Linux
  //  "h" stands for "host" (i.e. native), "be" for "big endian", "le" for "little endian"
  template <class T> inline T bswap(T x) BOOST_NOEXCEPT {return reverse_value(x);}
  template <class T> inline T htobe(T host) BOOST_NOEXCEPT {return big_endian_value(host);}
  template <class T> inline T htole(T host) BOOST_NOEXCEPT {return little_endian_value(host);}
  template <class T> inline T betoh(T big) BOOST_NOEXCEPT {return big_endian_value(big);}
  template <class T> inline T letoh(T little) BOOST_NOEXCEPT {return little_endian_value(little);}

  //  compile-time generic byte order conversion
  template <BOOST_SCOPED_ENUM(order) From, BOOST_SCOPED_ENUM(order) To, class ReversibleValue >
  ReversibleValue  convert_value(ReversibleValue  from) BOOST_NOEXCEPT;

  //  runtime actual byte-order determination
  inline BOOST_SCOPED_ENUM(order) effective_order(BOOST_SCOPED_ENUM(order) o) BOOST_NOEXCEPT;
    //  Return: o if o != native, otherwise big or little depending on native ordering
  
  //  runtime byte-order conversion
  template <class ReversibleValue >
  ReversibleValue  convert_value(ReversibleValue from, BOOST_SCOPED_ENUM(order) from_order,
            BOOST_SCOPED_ENUM(order) to_order) BOOST_NOEXCEPT;

//--------------------------------------------------------------------------------------//
//                             modify in place interface                                //
//--------------------------------------------------------------------------------------//
  
  // reverse byte order (i.e. endianness)
  //   
  template <class Value>
  inline void reverse(Value& x) BOOST_NOEXCEPT;

  //  reverse unless native endianness is big
  template <class Reversible>
  inline void big_endian(Reversible& x) BOOST_NOEXCEPT;    
    //  Effects: none if native endian order is big, otherwise reverse(x)

  //  reverse unless native endianness is little
  template <class Reversible>
  inline void little_endian(Reversible& x) BOOST_NOEXCEPT; 
    //  Effects: none if native endian order is little, otherwise reverse(x);

  //  synonyms based on names popularized by BSD, e.g. OS X, Linux.
  //  "h" stands for "host" (i.e. native), "be" for "big endian",
  //  "le" for "little endian", "m" for "modify in place"
  template <class T> inline void mbswap(T& x) BOOST_NOEXCEPT {reverse(x);}
  template <class T> inline void mhtobe(T& host) BOOST_NOEXCEPT {big_endian(host);}
  template <class T> inline void mhtole(T& host) BOOST_NOEXCEPT {little_endian(host);}
  template <class T> inline void mbetoh(T& big) BOOST_NOEXCEPT {big_endian(big);}
  template <class T> inline void mletoh(T& little) BOOST_NOEXCEPT {little_endian(little);}

  //  compile-time generic byte order conversion
  template <BOOST_SCOPED_ENUM(order) From, BOOST_SCOPED_ENUM(order) To, class Reversible>
  void convert(Reversible& x) BOOST_NOEXCEPT; 

  //  runtime byte-order conversion
  template <class Reversible>
  void convert(Reversible& x, BOOST_SCOPED_ENUM(order) from_order,
               BOOST_SCOPED_ENUM(order) to_order) BOOST_NOEXCEPT;

//----------------------------------- end synopsis -------------------------------------//

  namespace detail
  // These functions are unsafe for general use, so is placed in namespace detail.
  // Think of what happens if you reverse_value a std::pair<int16_t, int_int16_t>; the bytes
  // from first end up in second and the bytes from second end up in first. Not good! 
  {
    //  general reverse_value function template useful in testing
    template <class T>
    inline T reverse_value(T x) BOOST_NOEXCEPT;  // convert little to big or visa versa

    //  conditional unaligned reverse copy, patterned after std::reverse_copy
    template <class T>
      inline void big_reverse_copy(T from, char* to) BOOST_NOEXCEPT;
    template <class T>
      inline void big_reverse_copy(const char* from, T& to) BOOST_NOEXCEPT;
    template <class T>
      inline void little_reverse_copy(T from, char* to) BOOST_NOEXCEPT;
    template <class T>
      inline void little_reverse_copy(const char* from, T& to) BOOST_NOEXCEPT;
  }

//--------------------------------------------------------------------------------------//
//                                                                                      //
//                                   implementation                                     //
//                                                                                      //
//    -- reverse_value portable approach suggested by tymofey, with avoidance of        //
//       undefined behavior as suggested by Giovanni Piero Deretta, and a further       //
//       refinement suggested by Pyry Jahkola.                                          //
//    -- reverse_value intrinsic approach suggested by reviewers, and by David Stone,   //
//       who provided his Boost licensed macro implementation (detail/intrinsic.hpp)    //
//                                                                                      //
//--------------------------------------------------------------------------------------//

  inline int8_t reverse_value(int8_t x) BOOST_NOEXCEPT
  {
    return x;
  }
                                                
  inline int16_t reverse_value(int16_t x) BOOST_NOEXCEPT
  {
# ifdef BOOST_ENDIAN_NO_INTRINSICS  
    return (static_cast<uint16_t>(x) << 8)
      | (static_cast<uint16_t>(x) >> 8);
# else
    return BOOST_ENDIAN_INTRINSIC_BYTE_SWAP_2(static_cast<uint16_t>(x));
# endif
  }

  inline int32_t reverse_value(int32_t x) BOOST_NOEXCEPT
  {
# ifdef BOOST_ENDIAN_NO_INTRINSICS  
    uint32_t step16;
    step16 = static_cast<uint32_t>(x) << 16 | static_cast<uint32_t>(x) >> 16;
    return
        ((static_cast<uint32_t>(step16) << 8) & 0xff00ff00)
      | ((static_cast<uint32_t>(step16) >> 8) & 0x00ff00ff);
# else
    return BOOST_ENDIAN_INTRINSIC_BYTE_SWAP_4(static_cast<uint32_t>(x));
# endif
  }

  inline int64_t reverse_value(int64_t x) BOOST_NOEXCEPT
  {
# ifdef BOOST_ENDIAN_NO_INTRINSICS  
    uint64_t step32, step16;
    step32 = static_cast<uint64_t>(x) << 32 | static_cast<uint64_t>(x) >> 32;
    step16 = (step32 & 0x0000FFFF0000FFFFULL) << 16
           | (step32 & 0xFFFF0000FFFF0000ULL) >> 16;
    return static_cast<int64_t>((step16 & 0x00FF00FF00FF00FFULL) << 8
           | (step16 & 0xFF00FF00FF00FF00ULL) >> 8);
# else
    return BOOST_ENDIAN_INTRINSIC_BYTE_SWAP_8(static_cast<uint64_t>(x));
# endif
  }
  
  inline uint8_t reverse_value(uint8_t x) BOOST_NOEXCEPT
  {
    return x;
  }

  inline uint16_t reverse_value(uint16_t x) BOOST_NOEXCEPT
  {
# ifdef BOOST_ENDIAN_NO_INTRINSICS  
    return (x << 8)
      | (x >> 8);
# else
    return BOOST_ENDIAN_INTRINSIC_BYTE_SWAP_2(x);
# endif
  }

  inline uint32_t reverse_value(uint32_t x) BOOST_NOEXCEPT                           
  {
# ifdef BOOST_ENDIAN_NO_INTRINSICS  
    uint32_t step16;
    step16 = x << 16 | x >> 16;
    return
        ((step16 << 8) & 0xff00ff00)
      | ((step16 >> 8) & 0x00ff00ff);
# else
    return BOOST_ENDIAN_INTRINSIC_BYTE_SWAP_4(x);
# endif
  }

  inline uint64_t reverse_value(uint64_t x) BOOST_NOEXCEPT
  {
# ifdef BOOST_ENDIAN_NO_INTRINSICS  
    uint64_t step32, step16;
    step32 = x << 32 | x >> 32;
    step16 = (step32 & 0x0000FFFF0000FFFFULL) << 16
           | (step32 & 0xFFFF0000FFFF0000ULL) >> 16;
    return (step16 & 0x00FF00FF00FF00FFULL) << 8
           | (step16 & 0xFF00FF00FF00FF00ULL) >> 8;
# else
    return BOOST_ENDIAN_INTRINSIC_BYTE_SWAP_8(x);
# endif
  }

  inline float reverse_value(float x) BOOST_NOEXCEPT
  {
    BOOST_STATIC_ASSERT_MSG(sizeof(float) == sizeof(uint32_t),
      "boost::endian only supprts sizeof(float) == 4; please report error to boost mailing list");
    return detail::reverse_value(x);
  }

  inline double reverse_value(double x) BOOST_NOEXCEPT
  {
    BOOST_STATIC_ASSERT_MSG(sizeof(double) == sizeof(uint64_t),
      "boost::endian only supprts sizeof(double) == 8; please report error to boost mailing list");
    return detail::reverse_value(x);
  }

  namespace detail
  {
    //  general reverse_value function template implementation approach using std::reverse
    //  suggested by Mathias Gaunard
    template <class T>
    inline T reverse_value(T x) BOOST_NOEXCEPT
    {
      T tmp(x);
      std::reverse(
        reinterpret_cast<char*>(&tmp),
        reinterpret_cast<char*>(&tmp) + sizeof(T));
      return tmp;
    }
    template <class T>
      inline void big_reverse_copy(T from, char* to) BOOST_NOEXCEPT
      {
#     ifdef BOOST_BIG_ENDIAN
        std::memcpy(to, reinterpret_cast<const char*>(&from), sizeof(T));
#     else
        std::reverse_copy(reinterpret_cast<const char*>(&from),
          reinterpret_cast<const char*>(&from)+sizeof(T), to);
#     endif
      }
    template <class T>
      inline void big_reverse_copy(const char* from, T& to) BOOST_NOEXCEPT
      {
#     ifdef BOOST_BIG_ENDIAN
        std::memcpy(reinterpret_cast<char*>(&to), from, sizeof(T));
#     else
        std::reverse_copy(from, from+sizeof(T), reinterpret_cast<char*>(&to));
#     endif
      }
    template <class T>
      inline void little_reverse_copy(T from, char* to) BOOST_NOEXCEPT
      {
#     ifdef BOOST_LITTLE_ENDIAN
        std::memcpy(to, reinterpret_cast<const char*>(&from), sizeof(T));
#     else
        std::reverse_copy(reinterpret_cast<const char*>(&from),
          reinterpret_cast<const char*>(&from)+sizeof(T), to);
#     endif
      }
    template <class T>
      inline void little_reverse_copy(const char* from, T& to) BOOST_NOEXCEPT
       {
#     ifdef BOOST_LITTLE_ENDIAN
        std::memcpy(reinterpret_cast<char*>(&to), from, sizeof(T));
#     else
        std::reverse_copy(from, from+sizeof(T), reinterpret_cast<char*>(&to));
#     endif
      }
 }

  template <class ReversibleValue >
  inline ReversibleValue  big_endian_value(ReversibleValue  x) BOOST_NOEXCEPT
  {
#   ifdef BOOST_BIG_ENDIAN
      return x;
#   else
      return reverse_value(x);
#   endif
  }

  template <class ReversibleValue >
  inline ReversibleValue  little_endian_value(ReversibleValue  x) BOOST_NOEXCEPT    
  {
#   ifdef BOOST_LITTLE_ENDIAN
      return x;
#   else
      return reverse_value(x);
#   endif
  }

  namespace detail
  {
    //  Primary template and specializations to support convert_value(). See rationale in convert_value() below.
    template <BOOST_SCOPED_ENUM(order) From, BOOST_SCOPED_ENUM(order) To, class Reversible>
      class value_converter ;  // primary template
    template <class T> class value_converter <order::big, order::big, T> {public: T operator()(T x) BOOST_NOEXCEPT {return x;}};
    template <class T> class value_converter <order::little, order::little, T> {public: T operator()(T x) BOOST_NOEXCEPT {return x;}};
    template <class T> class value_converter <order::big, order::little, T> {public: T operator()(T x) BOOST_NOEXCEPT {return reverse_value(x);}};
    template <class T> class value_converter <order::little, order::big, T> {public: T operator()(T x) BOOST_NOEXCEPT {return reverse_value(x);}};
  }

  //  compile-time generic convert return by value
  template <BOOST_SCOPED_ENUM(order) From, BOOST_SCOPED_ENUM(order) To, class Reversible>
  Reversible convert_value(Reversible x) BOOST_NOEXCEPT
  {
    //  work around lack of function template partial specialization by instantiating
    //  a function object of a class that is partially specialized on the two order
    //  template parameters, and then calling its operator().
    detail::value_converter <From, To, Reversible> tmp;
    return tmp(x);
  }

  inline BOOST_SCOPED_ENUM(order) effective_order(BOOST_SCOPED_ENUM(order) o) BOOST_NOEXCEPT
  {
    return o != order::native ? o :
 #   ifdef BOOST_LITTLE_ENDIAN
      order::little
#   else
      order::big
#   endif
    ;
  }

  template <class ReversibleValue >
  ReversibleValue  convert_value(ReversibleValue  from, BOOST_SCOPED_ENUM(order) from_order,
            BOOST_SCOPED_ENUM(order) to_order) BOOST_NOEXCEPT
  {
    return effective_order(from_order) == effective_order(to_order)
      ? from : reverse_value(from);
  }

//--------------------------------------------------------------------------------------//
//                             modify in place implementation                           //
//--------------------------------------------------------------------------------------//
  
  // reverse byte order (i.e. endianness)
  //   
  template <class Value>
  inline void reverse(Value& x) BOOST_NOEXCEPT {x = reverse_value(x);}

  //  reverse unless native endianness is big
  template <class Reversible>
  inline void big_endian(Reversible& x) BOOST_NOEXCEPT
  //  Effects: none if native endian order is big, otherwise reverse(x)
  {
#   ifndef BOOST_BIG_ENDIAN
      reverse(x);
#   endif
  }

  //  reverse bytes unless native endianness is little
  template <class Reversible>
  inline void little_endian(Reversible& x) BOOST_NOEXCEPT    
  //  Effects: none if native endian order is little, otherwise reverse(x)
  {
#   ifndef BOOST_LITTLE_ENDIAN
      reverse(x);
#   endif
  }

  namespace detail
  {
    //  Primary template and specializations to support convert(). See rationale in convert() below.
    template <BOOST_SCOPED_ENUM(order) From, BOOST_SCOPED_ENUM(order) To, class Reversible>
      class converter;  // primary template
    template <class T> class converter<order::big, order::big, T> {public: void operator()(T&) BOOST_NOEXCEPT {/*no effect*/}};
    template <class T> class converter<order::little, order::little, T> {public: void operator()(T&) BOOST_NOEXCEPT {/*no effect*/}};
    template <class T> class converter<order::big, order::little, T> {public: void operator()(T& x) BOOST_NOEXCEPT {reverse(x);}};
    template <class T> class converter<order::little, order::big, T> {public: void operator()(T& x) BOOST_NOEXCEPT {reverse(x);}};
  }

  //  compile-time generic byte-order convert in place
  template <BOOST_SCOPED_ENUM(order) From, BOOST_SCOPED_ENUM(order) To, class Reversible>
  void convert(Reversible& x) BOOST_NOEXCEPT
  {
    //  work around lack of function template partial specialization by instantiating
    //  a function object of a class that is partially specialized on the two order
    //  template parameters, and then calling its operator().
    detail::converter<From, To, Reversible> tmp;
    tmp(x);
  }

  //  runtime byte-order convert in place
  template <class Reversible>
  void convert(Reversible& x, BOOST_SCOPED_ENUM(order) from_order,
            BOOST_SCOPED_ENUM(order) to_order) BOOST_NOEXCEPT
  {
    if (effective_order(from_order) == order::big)
    {
      if (effective_order(to_order) != order::big)
        reverse(x);
    }
    else // actual from_order is little
    {
      if (effective_order(to_order) != order::little)
        reverse(x);
    }
  }

}  // namespace endian
}  // namespace boost

#endif // OME_COMMON_ENDIAN_CONVERTERS_HPP
