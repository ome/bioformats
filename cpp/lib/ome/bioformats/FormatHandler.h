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

#include <string>
#include <vector>

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
       * Check if the given string is a valid filename for this file format.
       *
       * @param name the filename to check.
       * @returns @c true if valid, @c false otherwise.
       */
      virtual
      bool
      isThisType(const std::string& name);

      /**
       * Get the name of this file format.
       *
       * @returns the file format name.
       */
      virtual
      const std::string&
      getFormat() const;

      /**
       * Get the description of this file format.
       *
       * @returns the file format description.
       */
      virtual
      const std::string&
      getFormatDescription() const;

      /**
       * Get the default file suffixes for this file format.
       *
       * @returns a list of file suffixes.
       */
      virtual
      const std::vector<std::string>&
      getSuffixes() const;

      /**
       * Get the default compression suffixes for this file format.
       *
       * @returns a list of file suffixes.
       */
      virtual
      const std::vector<std::string>&
      getCompressionSuffixes() const;

      /**
       * Set the current file name.
       *
       * Note this will throw FormatException if there are problems
       * opening the file.
       *
       * @param id the filename to open.
       */
      void
      setId(const std::string& id);

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
         */
        static bool
        checkSuffix(const std::string& name,
                    const std::string& suffix)
        {
          std::vector<std::string> suffixes;
          suffixes.push_back(suffix);

          return checkSuffix(name, suffixes);
        }

        /** Performs suffix matching for the given filename. */
        static boolean checkSuffix(const std::string&              name,
                                   const std::vector<std::string>& suffixes)
        {
          std::string lname;
          std::transform(name.begin(), name.end(), std::back_inserter(lname), std::tolower);

          for (std::vector<std::string>::const_iterator si = detail.suffixes.begin();
               si != detail.suffixes.end();
               ++si)
            {
              std::string suffix(".");
              suffix += *si;
              if (name >= suffix &&
                  name.compare(name.size()-suffix.size(), suffix.size(), suffix) == 0)
                return true;

              for (std::vector<std::string>::const_iterator csi = detail.compression_suffixes.begin();
                   csi != detail.compression_suffixes.end();
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
    };

  }
}

#endif // OME_BIOFORMATS_FORMATHANDLER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

