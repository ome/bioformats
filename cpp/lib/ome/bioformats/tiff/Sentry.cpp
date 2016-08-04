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

#include <ome/internal/config.h>

#ifdef OME_HAVE_CSTDARG
# include <cstdarg>
#else
# include "stdarg.h"
#endif

#include <ome/bioformats/tiff/Sentry.h>
#include <ome/bioformats/tiff/Exception.h>

#include <tiffio.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      namespace
      {

        /// Saved libtiff global error handler.
        TIFFErrorHandler oldErrorHandler;

        /// Sentry currently holding the tiff_lock mutex.
        Sentry *currentSentry = 0;

        /**
         * Set the libtiff error handler.
         *
         * @param sentry the current Sentry.
         * @param handler the handler function to set.
         * @returns the old handler to allow later restoration.
         */
        TIFFErrorHandler
        setHandler(Sentry           *sentry,
                   TIFFErrorHandler  handler)
        {
          if (sentry)
            currentSentry = sentry;
          TIFFErrorHandler old = TIFFSetErrorHandler(handler);
          if (!sentry)
            currentSentry = sentry;
          return old;
        }

      }

      boost::recursive_mutex Sentry::tiff_mutex;

      // Visual Studio 12 and earlier don't have va_copy.
#if _MSC_VER &&_MSC_VER < 1800
#  define va_copy(dest, src) (dest = src)
#endif

      // This code deliberately formats a nonliteral format string, so
      // disable -Wformat-nonliteral for the duration.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wformat-nonliteral"
#endif

      void
      Sentry::errorHandler(const char *module,
                           const char *fmt,
                           va_list     ap)
      {
        try
          {
            va_list ap2;
            char *dest = static_cast<char *>(malloc(sizeof(char)));

            va_copy(ap2, ap);
            int length = vsnprintf(dest, 1, fmt, ap2);
            int oldlength = 0;
            while (length > 0 && length != oldlength)
              {
                va_copy(ap2, ap);
                dest = static_cast<char *>(realloc(dest, sizeof(char) * static_cast<size_t>(length+1)));
                oldlength = length;
                length = vsnprintf(dest, static_cast<size_t>(length+1), fmt, ap2);
              }
            if (length < 0)
              {
                free(dest);
                dest = 0;
              }

            std::string message(module ? module : "");
            if (!message.empty())
              message += ": ";
            if (dest)
              message += dest;
            else
              message += "Unknown error (error formatting TIFF error message)";

            free(dest);

            if (currentSentry)
              currentSentry->setMessage(message);
          }
        catch (...)
          {
            // We can't throw exceptions through C, so stop here.
            // Nothing here should ever throw, but best to be careful.
          }
      }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

      Sentry::Sentry():
        lock(tiff_mutex),
        message()
      {
        oldErrorHandler = setHandler(this, &Sentry::errorHandler);
      }

      Sentry::~Sentry()
      {
        setHandler(0, oldErrorHandler);
      }

      void
      Sentry::setMessage(std::string const& message)
      {
        this->message = message;
      }

      std::string const&
      Sentry::getMessage() const
      {
        return this->message;
      }

      void
      Sentry::error(const std::string& message) const
      {
        if (!this->message.empty())
          throw Exception(this->message);
        else
          throw Exception(message);
      }

      void
      Sentry::error() const
      {
        error("Unknown error (no TIFF error message received)");
      }

    }
  }
}
