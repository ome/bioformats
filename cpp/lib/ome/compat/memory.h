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
 * @file memory.h Memory type substitution.  This header substitutes
 * Boost types for the same types in the std namespace when not using
 * a conforming C++11 compiler.  This permits all code to use the
 * C++11 standard types irrespective of the compiler being used.
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
