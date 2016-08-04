/*
 * #%L
 * OME-COMMON C++ library for C++ compatibility/portability
 * %%
 * Copyright © 2014 - 2015 Open Microscopy Environment:
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
 * @file endian.h Endian-specific integer types.  This header uses the
 * proposed Boost.Endian headers, which are not yet an official part of
 * any Boost release.
 *
 * @note Boost.Endian was imported from https://github.com/Beman/endian.git
 * commit d339470e6.  It should be replaced with the real Boost implementation
 * once it is included.  This is only included here as a workaround until
 * then.
 */

#ifndef OME_COMMON_ENDIAN_H
# define OME_COMMON_ENDIAN_H

#include <ome/common/config.h>

#ifndef BOOST_NOEXCEPT
# ifdef OME_HAVE_NOEXCEPT
/// Work around missing BOOST_NOEXCEPT in older Boost versions (e.g. 1.46)
#  define BOOST_NOEXCEPT noexcept
# else
/// Work around missing BOOST_NOEXCEPT in older Boost versions (e.g. 1.46)
#  define BOOST_NOEXCEPT
# endif
#endif

#include <ome/common/endian/types.hpp>

namespace ome
{

  // Import all endian types into the ome namespace.
  using namespace boost::endian;

}

#endif /* OME_COMMON_ENDIAN_H */

/*
 * Local Variables:
 * mode:C++
 * End:
 */
