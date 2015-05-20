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

/**
 * @file ome/common/filesystem.h Boost.Filesystem compatibility.
 *
 * This header works around the lack of certain functions in older
 * versions of the Boost.Filesystem library.
 */

#ifndef OME_COMMON_FILESYSTEM_H
# define OME_COMMON_FILESYSTEM_H

# include <ome/common/config.h>

#include <boost/filesystem/operations.hpp>
#include <boost/filesystem/path.hpp>

// Older versions use get_generic_category() in place of the new
// generic_category()
# ifdef OME_HAVE_BOOST_FILESYSTEM_CANONICAL
#  ifdef BOOST_SYSTEM_NO_DEPRECATED
#   undef BOOST_SYSTEM_NO_DEPRECATED
#  endif // BOOST_SYSTEM_NO_DEPRECATED
#  include <boost/system/error_code.hpp>
# endif // OME_HAVE_BOOST_FILESYSTEM_CANONICAL

namespace ome
{

  /**
   * OME compatibility functions and classes.
   */
  namespace common
  {

# ifdef OME_HAVE_BOOST_FILESYSTEM_ABSOLUTE
    using boost::filesystem::absolute;
# else // OME_HAVE_BOOST_FILESYSTEM_ABSOLUTE not defined
    /**
     * Get an absolute path.
     *
     * @param p the path to make absolute.
     * @param base the base directory, defaulting to the current path.
     * @returns the absolute path.
     */
    inline
    boost::filesystem::path
    absolute(const boost::filesystem::path& p,
             const boost::filesystem::path& base = boost::filesystem::current_path())
    {
      return boost::filesystem::complete(p, base);
    }
# endif // OME_HAVE_BOOST_FILESYSTEM_ABSOLUTE

# ifdef OME_HAVE_BOOST_FILESYSTEM_CANONICAL
    using boost::filesystem::canonical;
# else // OME_HAVE_BOOST_FILESYSTEM_CANONICAL not defined
    // Implementation derived from boost 1.55.
    /**
     * Get a canonical path.
     *
     * @param p the path to make canonical.
     * @param base the base directory, defaulting to the current path.
     * @param ec pointer to storage for an error code (optional).
     * @returns the canonical path.
     */
    inline
    boost::filesystem::path
    canonical(const boost::filesystem::path& p,
              const boost::filesystem::path& base = boost::filesystem::current_path(),
              boost::system::error_code* ec = 0)
    {
      boost::filesystem::path source (absolute(p, base));
      boost::filesystem::path result;

      boost::system::error_code local_ec;
      boost::filesystem::file_status stat (status(source, local_ec));

      if (stat.type() == boost::filesystem::file_not_found)
        {
          if (ec == 0)
            throw boost::filesystem::filesystem_error
              ("boost::filesystem::canonical", source,
               boost::system::error_code(boost::system::errc::no_such_file_or_directory,
                                         boost::system::get_generic_category()));
          ec->assign(boost::system::errc::no_such_file_or_directory,
                     boost::system::get_generic_category());
          return result;
        }
      else if (local_ec)
        {
          if (ec == 0)
            throw(boost::filesystem::filesystem_error
                  ("boost::filesystem::canonical", source, local_ec));
          *ec = local_ec;
          return result;
        }
    }
# endif // OME_HAVE_BOOST_FILESYSTEM_CANONICAL

    /**
     * Make a relative path.
     *
     * @param from the start (reference) path.
     * @param to the end path (to make relative to the start path).
     * @returns the relative path.
     */
    inline
    boost::filesystem::path
    make_relative(boost::filesystem::path from,
                  boost::filesystem::path to)
    {
      from = absolute(from);
      to = absolute(to);
      boost::filesystem::path ret;
      boost::filesystem::path::const_iterator itrFrom(from.begin());
      boost::filesystem::path::const_iterator itrTo(to.begin());

      // Find common base
      for(boost::filesystem::path::const_iterator toEnd(to.end()), fromEnd(from.end());
          itrFrom != fromEnd && itrTo != toEnd && *itrFrom == *itrTo;
          ++itrFrom, ++itrTo);

      // Navigate backwards in directory to reach previously found base
      for(boost::filesystem::path::const_iterator fromEnd(from.end());
          itrFrom != fromEnd;
          ++itrFrom )
        {
          if((*itrFrom) != ".")
            ret /= "..";
        }
      // Now navigate down the directory branch
      for (boost::filesystem::path::iterator begin = itrTo;
           begin != to.end();
           ++begin)
        ret /= *begin;

      return ret;
    }

  }
}

#endif // OME_COMMON_FILESYSTEM_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
