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
 * @file ome/compat/regex.h Regular expression type substitution.
 *
 * This header substitutes Boost types for the same types in the std
 * namespace when not using a conforming C++11 compiler.  This permits
 * all code to use the C++11 standard types irrespective of the
 * compiler being used.
 */

#ifndef OME_COMPAT_REGEX_H
# define OME_COMPAT_REGEX_H

# include <ome/common/config.h>

# ifdef OME_HAVE_REGEX
#  include <regex>
namespace ome
{
  namespace compat
  {
    using std::regex;
    using std::regex_error;
    using std::regex_match;
    using std::regex_replace;
    using std::regex_search;
    using std::cmatch;
    using std::smatch;
  }
}
# elif OME_HAVE_TR1_REGEX
#  include <tr1/regex.hpp>
namespace ome
{
  namespace compat
  {
    using std::tr1::regex;
    using std::tr1::regex_error;
    using std::tr1::regex_match;
    using std::tr1::regex_replace;
    using std::tr1::regex_search;
    using std::tr1::cmatch;
    using std::tr1::smatch;
  }
}
# elif OME_HAVE_BOOST_REGEX
#  include <boost/regex.hpp>
namespace ome
{
  namespace compat
  {
    using boost::regex;
    using boost::regex_error;
    using boost::regex_match;
    using boost::regex_replace;
    using boost::regex_search;
    using boost::cmatch;
    using boost::smatch;
  }
}
# else
#  error An regex implementation is not available
# endif

#endif // OME_COMPAT_REGEX_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
