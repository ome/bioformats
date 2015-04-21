/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#ifndef OME_BIOFORMATS_FILEINFO_H
#define OME_BIOFORMATS_FILEINFO_H

#include <string>
#include <ostream>

#include <ome/common/filesystem.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Basic metadata for a file.
     */
    struct FileInfo
    {
      /// Absolute path to this file.
      boost::filesystem::path filename;

      /**
       * FormatReader implementation used to read this file.
       *
       * @note In the Java implementation this returned a class.  In
       * the C++ implementation this is the name of the reader which
       * may be used to construct a reader via a factory.
       * @todo Document factory function to create reader.
       */
      std::string reader;

      /**
       * @c true if this file can be passed to the appropriate reader's
       * FormatReader::setId() method; @c false if otherwise.
       */
      bool usedToInitialize;
    };

    /**
     * Output FileInfo to output stream.
     *
     * @param os the output stream.
     * @param info the FileInfo to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
    inline std::basic_ostream<charT,traits>&
    operator<< (std::basic_ostream<charT,traits>& os,
                const FileInfo& info)
    {
      return os << "filename = " << info.filename
                << "\nreader = " << info.reader
                << "\nused to initialize = " << info.usedToInitialize;
    }

  }
}

#endif // OME_BIOFORMATS_FILEINFO_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

