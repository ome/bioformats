/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2013 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_FORMATHANDLER_H
#define OME_BIOFORMATS_FORMATHANDLER_H

#include <algorithm>
#include <iterator>
#include <stdexcept>
#include <string>
#include <vector>

#include <boost/format.hpp>
#include <boost/optional.hpp>

namespace ome
{
  namespace bioformats
  {

    /**
     * Interface for all biological file format readers and writers.
     *
     * @note The Java implementation includes a getNativeDataType
     * method; this is not included in this implementation.
     */
    class FormatHandler
    {
    public:
      /// Constructor.
      FormatHandler()
      {}

      /// Destructor.
      virtual
      ~FormatHandler()
      {}

      /**
       * Check if the given file is a valid instance of this file format.
       *
       * @param name the file to open for checking.
       * @param open If @c true, and the file extension is
       *   insufficient to determine the file type, the file may be
       *   opened for further analysis, or other relatively expensive
       *   file system operations (such as file existence tests and
       *   directory listings) may be performed.  If @c false, file
       *   system access is not allowed.
       * @returns @c true if the file is valid, @c false otherwise.
       *
       * @todo Could this method be static and/or const?
       */
      virtual
      bool
      isThisType(const std::string& name,
                 bool               open) = 0;

      /**
       * Get the name of this file format.
       *
       * @returns the file format name.
       */
      virtual
      const std::string&
      getFormat() const = 0;

      /**
       * Get the description of this file format.
       *
       * @returns the file format description.
       */
      virtual
      const std::string&
      getFormatDescription() const = 0;

      /**
       * Get the default file suffixes for this file format.
       *
       * @returns a list of file suffixes.
       */
      virtual
      const std::vector<std::string>&
      getSuffixes() const = 0;

      /**
       * Get the default compression suffixes for this file format.
       *
       * @returns a list of file suffixes.
       */
      virtual
      const std::vector<std::string>&
      getCompressionSuffixes() const = 0;

      /**
       * Set the current file name.
       *
       * Note this will throw FormatException if there are problems
       * opening the file.
       *
       * @param id the filename to open.
       */
      virtual void
      setId(const std::string& id) = 0;

      /**
       * Close the currently open file.
       *
       * @param fileOnly close the open file only if @c true, or else
       * free all internal state if @c false.
       */
      virtual
      void
      close(bool fileOnly = false) = 0;

      // -- Utility methods --

      /**
       * Perform suffix matching for the given filename.
       *
       * @param name the name to check.
       * @param suffix the suffix to match.
       *
       * @returns @c true if the suffix is suppored, @c false otherwise.
       */
      static bool
      checkSuffix(const std::string& name,
                  const std::string& suffix)
      {
        std::vector<std::string> suffixes;
        std::vector<std::string> compression_suffixes;
        suffixes.push_back(suffix);

        return checkSuffix(name, suffixes, compression_suffixes);
      }

      /**
       * Perform suffix matching for the given filename.
       *
       * @param name the name to check.
       * @param suffixes the suffixes to match.
       * @param compression_suffixes the compression suffixes to match.
       *
       * @returns @c true if the suffix is suppored, @c false otherwise.
       */
      static bool
      checkSuffix(const std::string&              name,
                  const std::vector<std::string>& suffixes,
                  const std::vector<std::string>& compression_suffixes)
      {
        std::string lname;
        std::transform(name.begin(), name.end(),
                       std::back_inserter(lname),
                       static_cast<int (*)(int)>(std::tolower));

        for (std::vector<std::string>::const_iterator si = suffixes.begin();
             si != suffixes.end();
             ++si)
          {
            std::string suffix(".");
            suffix += *si;
            if (name >= suffix &&
                name.compare(name.size()-suffix.size(), suffix.size(), suffix) == 0)
              return true;

            for (std::vector<std::string>::const_iterator csi = compression_suffixes.begin();
                 csi != compression_suffixes.end();
                 ++csi)
              {
                std::string csuffix(suffix);
                csuffix += "." + *csi;

                if (name >= csuffix &&
                    name.compare(name.size()-csuffix.size(), csuffix.size(), csuffix) == 0)
                  return false;
                /**
                 * @todo Should return true when compression suffixes are supported.
                 */
              }
          }
        return false;
      }

      /**
       * Assert that the current file is valid.
       *
       * Assert if the current file is null, or not, according to the
       * given flag. If the assertion fails, an exception is thrown.
       *
       * @param id Filename to test.
       * @param notNull true if @c id should be non-null, @c false if @c
       * id should be null.
       * @throws std::logic_error if the assertion fails.
       */
      static void
      assertId(const boost::optional<std::string>& id,
               bool                                notNull = true)
      {
        if (!id && notNull)
          {
            throw std::logic_error("Current file should not be null; call setId(String) first");
          }
        else if (id && !notNull)
          {
            boost::format fmt("Current file should be null, but is '%1%'; call close() first");
            fmt % id;
            throw std::logic_error(fmt.str());
          }
      }
    };


  }
}

#endif // OME_BIOFORMATS_FORMATHANDLER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

