/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * Copyright © 2006 - 2016 Open Microscopy Environment:
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

#ifndef OME_XML_META_METADATAEXCEPTION_H
#define OME_XML_META_METADATAEXCEPTION_H

#include <stdexcept>

namespace ome
{
  namespace xml
  {
    namespace meta
    {

      /**
       * Exception thrown for metadata consistency and validity errors.
       */
      class MetadataException : public std::runtime_error
      {
      public:
        /**
         * Constructor.
         *
         * @param what the exception message.
         */
        explicit
        MetadataException (const std::string& what);

        /**
         * Constructor.
         *
         * @param type the MetadataStore or Retrieve type.
         * @param method the MetadataStore or Retrieve method.
         * @param msg the exception message.
         */
        explicit
        MetadataException (const std::string& type,
                           const std::string& method,
                           const std::string& msg);

        /// Destructor.
        virtual
        ~MetadataException () throw();
      };

    }
  }
}

#endif // OME_XML_META_METADATAEXCEPTION_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
