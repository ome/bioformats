/**
 * @file sbuild-tr1types.h Tuple type substitution.  This header
 * substitutes Boost types for the same types in the std namespace
 * when not using a conforming C++11.  This permits all code to use
 * the C++11 standard types irrespective of the compiler being used.
 */

#ifndef OME_COMPAT_TUPLE_H
# define OME_COMPAT_TUPLE_H

# include <ome/compat/config.h>

# ifdef OME_HAVE_TUPLE
#  include <tuple>
# elif OME_HAVE_TR1_TUPLE
#  include <tr1/tuple>
namespace std {
  using tr1::tuple;
  using tr1::get;
}
# elif OME_HAVE_BOOST_TUPLE
#  include <boost/tuple/tuple.hpp>
namespace std {
  using boost::tuple;
  using boost::get;
}
# else
#  error A tuple implementation is not available
# endif

#endif // OME_COMPAT_TUPLE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
