/*
 * #%L
 * OME-COMPAT C++ library for C++ compatibility/portability
 * %%
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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
 * @file ome/compat/variant.h Variant type limit workaround.
 *
 * This header increases the Boost MPL size limits, if required.  Some
 * older versions of Boost.Variant throw runtime exceptions when using
 * Variant and MPL with a number of types over a compile-time limit.
 */

#ifndef OME_COMPAT_VARIANT_H
# define OME_COMPAT_VARIANT_H

# include <ome/compat/config.h>

#ifndef OME_VARIANT_LIMIT
# ifndef BOOST_MPL_CFG_NO_PREPROCESSED_HEADERS
/// Disable MPL header preprocessing (to allow the following macros to be modified).
#  define BOOST_MPL_CFG_NO_PREPROCESSED_HEADERS
# endif
# ifndef BOOST_MPL_LIMIT_VECTOR_SIZE
/// MPL vector size limit increase.
#  define BOOST_MPL_LIMIT_VECTOR_SIZE 40
# endif
# ifndef BOOST_MPL_LIMIT_LIST_SIZE
/// MPL list size limit increase.
#  define BOOST_MPL_LIMIT_LIST_SIZE 40
# endif
#endif

#include <boost/mpl/insert_range.hpp>
#include <boost/mpl/joint_view.hpp>
#include <boost/mpl/transform_view.hpp>
#include <boost/mpl/vector.hpp>

#include <boost/variant/apply_visitor.hpp>
//#include <boost/variant/multivisitors.hpp>
#include <boost/variant/get.hpp>
#include <boost/variant/variant.hpp>

#endif // OME_COMPAT_VARIANT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
