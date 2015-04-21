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

#ifndef OME_BIOFORMATS_TIFF_SENTRY_H
#define OME_BIOFORMATS_TIFF_SENTRY_H

#include <cstdarg>
#include <string>

#include <boost/thread.hpp>

#include <ome/compat/cstdint.h>
#include <ome/compat/memory.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      /**
       * Sentry for saving and restoring libtiff state.
       *
       * This acts primarily to save the state of the global error and
       * warning handlers, which are configured to work with the
       * currently active TIFF/IFD.  The original state is restored
       * when destroyed.  Note that this exclusively locks all TIFF
       * and IFD methods calling into libtiff, so will potentially
       * block other threads.
       *
       * This class also hooks into the global libtiff error handling
       * to capture any errors which occur.  The latest error will be
       * available using getMessage().
       *
       * This class should be used at block scope so that instances
       * will only exist transiently until the block ends.
       */
      class Sentry
      {
      public:
        /// Constructor.
        Sentry();

        /// Destructor.
        ~Sentry();

      private:
        /**
         * Set the latest error message.
         *
         * Any previously stored message will be replaced.
         *
         * @param message the message to set.
         */
        void
        setMessage(std::string const& message);

      public:
        /**
         * Get the latest error message.
         *
         * If no error has occured, the message will be empty.
         *
         * @returns the message.
         */
        std::string const&
        getMessage() const;

        /**
         * Throw an Exception.
         *
         * The latest error message will be set as the message.  If no
         * error message has been set, the provided message will be
         * used.  This is to cater for the cases where libtiff fails
         * but does not provide any indication of why.
         *
         * @param message the message to set.
         * @throws an Exception.
         */
        void
        error(const std::string& message) const;

        /**
         * Throw an Exception.
         *
         * The latest error message will be set as the message.
         *
         * @throws an Exception.
         */
        void
        error() const;

      private:
        /// Mutex to lock libtiff access.
        static boost::recursive_mutex tiff_mutex;

        /// Acquired lock on tiff_lock.
        boost::lock_guard<boost::recursive_mutex> lock;

        /// Last error message.
        std::string message;

        /**
         * libtiff error handler.
         *
         * The error message received will be converted to a string
         * and saved in the current Sentry for later retrieval with
         * getMessage().
         *
         * @param module the module or file emitting the error.
         * @param fmt the format string for the error.
         * @param ap additional parameters.
         */
        static void
        errorHandler(const char *module,
                     const char *fmt,
                     va_list     ap);

      };

    }
  }
}

#endif // OME_BIOFORMATS_TIFF_SENTRY_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
