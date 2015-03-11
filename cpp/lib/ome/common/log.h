/*
 * #%L
 * OME-COMMON C++ library for C++ compatibility/portability
 * %%
 * Copyright © 2015 Open Microscopy Environment:
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
 * @file ome/common/log.h Boost.Log compatibility.
 *
 * This header provides additional functionality on top of the
 * Boost.Log library.
 */

#ifndef OME_COMMON_LOG_H
#define OME_COMMON_LOG_H

#include <ostream>
#include <string>

#include <ome/common/config.h>

#ifdef OME_HAVE_BOOST_LOG
#define BOOST_LOG_DYN_LINK
#include <boost/log/core.hpp>
#include <boost/log/trivial.hpp>
#include <boost/log/expressions.hpp>
#include <boost/log/sources/severity_logger.hpp>
#include <boost/log/sources/severity_feature.hpp>
#include <boost/log/attributes/constant.hpp>
#else // ! OME_HAVE_BOOST_LOG
// For std::clog
#include <iostream>
#endif // OME_HAVE_BOOST_LOG

namespace ome
{

#ifdef OME_HAVE_BOOST_LOG
  namespace logging = boost::log;
#else
  namespace logging
  {
    namespace trivial
    {

      /// Trivial severity levels
      enum severity_level
        {
          trace,   ///< Trace
          debug,   ///< Debug
          info,    ///< Information
          warning, ///< Warning
          error,   ///< Error
          fatal    ///< Fatal error
        };

    }
  }
#endif // OME_HAVE_BOOST_LOG

  namespace common
  {

#ifdef OME_HAVE_BOOST_LOG
    /// Message logger.
    typedef logging::sources::severity_logger_mt<logging::trivial::severity_level> Logger;

    /**
     * Create a message logger for a class.
     *
     * @param className the class name owning the logger.
     * @returns a message logger.
     */
    inline
    Logger
    createLogger(const std::string& className)
    {
      Logger logger;
      logger.add_attribute("ClassName", logging::attributes::constant<std::string>(className));
      return logger;
    }
#else // ! OME_HAVE_BOOST_LOG
    class Logger
    {
    private:
      /// Class name.
      std::string klass;

    public:
      /**
       * Constructor.
       *
       * @param className the class name.
       */
      Logger(const std::string& className):
        klass(className)
      {}

      void
      className(const std::string& className)
      {
        this->klass = className;
      }

      const std::string&
      className() const
      {
        return this->klass;
      }
    };

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

    class LogMessage
    {
    private:
      std::ostream& ostream;
      logging::trivial::severity_level severity;

    public:
      LogMessage(std::ostream& ostream,
                 logging::trivial::severity_level severity):
        ostream(ostream),
        severity(severity)
      {
        const char * sevstr;
        switch(severity)
          {
          case logging::trivial:: trace:
            sevstr = "trace";
            break;
          case logging::trivial:: debug:
            sevstr = "debug";
            break;
          case logging::trivial:: info:
            sevstr = "info";
            break;
          case logging::trivial:: warning:
            sevstr = "warning";
            break;
          case logging::trivial:: error:
            sevstr = "error";
            break;
          case logging::trivial:: fatal:
            sevstr = "fatal";
            break;
          }

        ostream << '[' << sevstr << "] ";
      }

      ~LogMessage()
      {
        try
          {
            ostream << '\n';
          }
        catch (...)
          {
          }
      }

      std::ostream&
      stream()
      {
        return ostream;
      }
    };

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

    inline
    Logger
    createLogger(const std::string& className)
    {
      Logger logger(className);
      return logger;
    }

#endif // OME_HAVE_BOOST_LOG

    /**
     * Set global logging level.
     *
     * Log messages will be filtered such that messages with a
     * priority greater or equal to the specified severity will be
     * logged; messages with a lower priority will be discarded.
     *
     * If using Boost.Log for logging, this is used to set the logging
     * core filter.
     *
     * @param severity the log severity.
     */
    void
    setLogLevel(logging::trivial::severity_level severity);

    /**
     * Get global logging level.
     *
     * @returns the log severity.
     */
    logging::trivial::severity_level
    getLogLevel();

  }
}

/// Fallback if Boost.Log is missing.
#ifndef OME_HAVE_BOOST_LOG
#define BOOST_LOG_SEV(logger, severity)\
  if (severity >= ome::common::getLogLevel())\
    ome::common::LogMessage(std::clog, severity).stream() << logger.className() << ": "
#endif // !OME_HAVE_BOOST_LOG

#endif // OME_COMMON_LOG_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
