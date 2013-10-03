/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
 * %%
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

#ifndef OME_XERCES_DOM_DOCUMENT_H
#define OME_XERCES_DOM_DOCUMENT_H

#include <ome/compat/config.h>

#include <cassert>
#include <string>
#include <ostream>

#include <xercesc/dom/DOMDocument.hpp>

#include <ome/xerces/dom/element.h>
#include <ome/xerces/string.h>

namespace ome
{
  namespace xerces
  {
    namespace dom
    {

      class document
      {
      public:
        document ():
          xmldoc()
        {
        }

        document (const document& document):
          xmldoc(document.xmldoc)
        {
        }

        document (xercesc::DOMDocument *document):
          xmldoc(document)
        {
        }

        ~document ()
        {
        }

        element
        createElementNS(const std::string& ns,
                        const std::string& name)
        {
          xerces::string xns(ns);
          xerces::string xname(name);

          return xmldoc->createElementNS(xns, xname);
        }

        document&
        operator= (document& document)
        {
          this->xmldoc = document.xmldoc;
          return *this;
        }

        document&
        operator= (xercesc::DOMDocument *document)
        {
          this->xmldoc = document;
          return *this;
        }

        xercesc::DOMDocument&
        operator* () noexcept
        {
          assert(xmldoc != 0);
          return *xmldoc;
        }

        const xercesc::DOMDocument&
        operator* () const noexcept
        {
          assert(xmldoc != 0);
          return *xmldoc;
        }

        xercesc::DOMDocument *
        operator-> () noexcept
        {
          assert(xmldoc != 0);
          return xmldoc;
        }

        const xercesc::DOMDocument *
        operator-> () const noexcept
        {
          assert(xmldoc != 0);
          return xmldoc;
        }

        operator bool () const
        {
          return xmldoc != 0;
        }

      private:
        xercesc::DOMDocument *xmldoc;
      };

    }
  }
}

#endif // OME_XERCES_DOM_DOCUMENT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
