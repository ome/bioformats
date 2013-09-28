/**
 * @file sbuild-tr1types.h Memory type substitution.  This header
 * substitutes Boost types for the same types in the std namespace
 * when not using a conforming C++11.  This permits all code to use
 * the C++11 standard types irrespective of the compiler being used.
 */

#ifndef OME_COMPAT_MEMORY_H
# define OME_COMPAT_MEMORY_H

# include <ome/compat/config.h>

# ifdef OME_HAVE_MEMORY
#  include <memory>
# elif OME_HAVE_BOOST_SHARED_PTR
#  include <boost/enable_shared_from_this.hpp>
#  include <boost/shared_ptr.hpp>
namespace std {
    using boost::shared_ptr;
    using boost::weak_ptr;
    using boost::static_pointer_cast;
    using boost::const_pointer_cast;
    using boost::dynamic_pointer_cast;
    using boost::enable_shared_from_this;
}
# else
#  error A shared_ptr implementation is not available
# endif

#endif // OME_COMPAT_MEMORY_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
