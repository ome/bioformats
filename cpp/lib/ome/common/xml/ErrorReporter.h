/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
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

#ifndef OME_COMMON_XML_ERRORREPORTER_H
#define OME_COMMON_XML_ERRORREPORTER_H

#include <iostream>
#include <ostream>

#include <xercesc/sax/ErrorHandler.hpp>

namespace ome
{
  namespace common
  {
    namespace xml
    {

      /**
       * Xerces error handler reporting errors to an ostream.
       * Encountered Xerces xercesc::SAXParseException exceptions are
       * logged to the specified ostream.  Xerces exceptions don't
       * derive from any of the standard exception classes, and don't
       * use standard strings, making them difficult to catch and
       * process.  If an error is encountered, this class will evaluate
       * to true.
       */
      class ErrorReporter : public xercesc::ErrorHandler
      {
      public:
        /**
         * Construct an ErrorReporter.
         *
         * @param stream the stream to output exception details to.
         */
        ErrorReporter(std::ostream& stream = std::cerr);

        /// The destructor.
        ~ErrorReporter();

        /**
         * Log a warning.
         *
         * @param toCatch the exception to log.
         */
        void warning(const xercesc::SAXParseException& toCatch);

        /**
         * Log an error.
         *
         * @param toCatch the exception to log.
         */
        void error(const xercesc::SAXParseException& toCatch);

        /**
         * Log a fatal error.
         *
         * @param toCatch the exception to log.
         */
        void fatalError(const xercesc::SAXParseException& toCatch);

        /**
         * Reset error status.  Forget any errors which have been
         * previously encountered.  The class will subsequently evaluate
         * to true.
         */
        void resetErrors();

      private:
        /// The output stream to use.
        std::ostream& stream;
        /// Has an error been encountered?
        bool saw_error;

      public:
        /**
         * Has an error been encountered?
         *
         * @returns true if an error has been encountered, otherwise false.
         */
        operator bool() const
        {
          return saw_error;
        }
      };

    }
  }
}

#endif // OME_COMMON_XML_ERRORREPORTER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

