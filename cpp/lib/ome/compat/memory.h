/*
 * #%L
 * OME-COMPAT C++ library for C++ compatibility/portability
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

/**
 * @file ome/compat/memory.h Memory type substitution.
 *
 * This header substitutes Boost types for the same types in the std
 * namespace when not using a conforming C++11 compiler.  This permits
 * all code to use the C++11 standard types irrespective of the
 * compiler being used.
 */

#ifndef OME_COMPAT_MEMORY_H
# define OME_COMPAT_MEMORY_H

# include <ome/common/config.h>

# ifdef OME_HAVE_MEMORY
#  include <memory>
# elif OME_HAVE_BOOST_SHARED_PTR
#  include <boost/enable_shared_from_this.hpp>
#  include <boost/make_shared.hpp>
#  include <boost/shared_ptr.hpp>
# ifdef OME_HAVE_BOOST_OWNER_LESS
#  include <boost/smart_ptr/owner_less.hpp>
# else
#  include <functional>
// From Boost 1.56 <boost/smart_ptr/owner_less.hpp> for older boost
// versions; owner_before replaced with direct operator< usage which
// older shared_ptr/weak_ptr implementations do not provide.  shared
// and weak are not directly comparable, so convert to weak for these
// comparisons.
namespace boost
{
  template<typename T> class shared_ptr;
  template<typename T> class weak_ptr;

  namespace detail
  {
    template<typename T, typename U>
    struct generic_owner_less : public std::binary_function<T, T, bool>
    {
      bool operator()(const T &lhs, const T &rhs) const
      {
        return lhs < rhs;
      }
      bool operator()(const T &lhs, const U &rhs) const
      {
        return weak_ptr<typename T::element_type>(lhs) < weak_ptr<typename T::element_type>(rhs);
      }
      bool operator()(const U &lhs, const T &rhs) const
      {
        return weak_ptr<typename T::element_type>(lhs) < weak_ptr<typename T::element_type>(rhs);
      }
    };
  } // namespace detail

  template<typename T> struct owner_less;

  template<typename T>
  struct owner_less<shared_ptr<T> >:
    public detail::generic_owner_less<shared_ptr<T>, weak_ptr<T> >
  {};

  template<typename T>
  struct owner_less<weak_ptr<T> >:
    public detail::generic_owner_less<weak_ptr<T>, shared_ptr<T> >
  {};

} // namespace boost
# endif
namespace ome
{
  namespace compat
  {
    using boost::shared_ptr;
    using boost::weak_ptr;
    using boost::static_pointer_cast;
    using boost::const_pointer_cast;
    using boost::dynamic_pointer_cast;
    using boost::enable_shared_from_this;
    using boost::make_shared;
    using boost::owner_less;
  }
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
