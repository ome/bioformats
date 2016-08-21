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

#ifndef OME_XERCES_PLATFORM_H
#define OME_XERCES_PLATFORM_H

#include <xercesc/util/PlatformUtils.hpp>

namespace ome
{
  /**
   * Xerces-C modern C++ wrapper.  All classes in this namespace wrap
   * the Xerces-C classes and functions to provide RAII and
   * exception-safe equivalents, and which also handle memory
   * management transparently.
   */
  namespace xerces
  {

    /**
     * XML Platform.  This class wraps calls to the
     * xercesc::XMLPlatformUtils Initialize() and Terminate()
     * functions, to allow their use in an exception-safe manner.
     * Create an instance of this class prior to performing any work
     * with Xerces, and ensure it will remain in scope for all work to
     * complete.  When the scope is exited, or an exception is thrown,
     * Xerces will be automatically terminated.  Any number of
     * instances of this class may be created; Xerces will only be
     * terminated when the last instance is destroyed.
     */
    class Platform
    {
    public:
      inline
      /**
       * Construct a Platform.  Calls xercesc::XMLPlatformUtils::Initialize().
       */
      Platform()
      {
        xercesc::XMLPlatformUtils::Initialize();
      }

      /**
       * Destructor. Calls xercesc::XMLPlatformUtils::Terminate().
       */
      inline
      ~Platform()
      {
        xercesc::XMLPlatformUtils::Terminate();
      }
    };

  }
}

#endif // OME_XERCES_PLATFORM_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
