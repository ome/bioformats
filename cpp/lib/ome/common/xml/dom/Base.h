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

#ifndef OME_COMMON_XML_DOM_BASE_H
#define OME_COMMON_XML_DOM_BASE_H

#include <ome/common/config.h>

#include <ome/compat/memory.h>

#include <iostream>
#include <stdexcept>

namespace ome
{
  namespace common
  {
    namespace xml
    {
      namespace dom
      {

        namespace detail
        {

          template<typename T>
          inline
          void
          unmanaged(T *)
          {}

        }

        /**
         * Base of the DOM wrapper hierarchy.
         *
         * This contains the managed or unmanaged base pointer to the
         * wrapped type.
         */
        template<typename T>
        class Base
        {
        public:
          /// Base type.
          typedef Base base_type;
          /// Base element type (root type of the wrapped type).
          typedef T base_element_type;

          /// Constructor.
          Base():
            base()
          {}

          /**
           * Construct with initial wrapped value (managed).
           *
           * @param wrapped the value to wrap.
           * @param del the deleter to clean up the wrapped value.
           */
          template<typename Deleter>
          explicit
          Base(base_element_type *wrapped,
               Deleter            del):
            base(wrapped ?
                 ome::compat::shared_ptr<base_element_type>(wrapped, del) :
                 ome::compat::shared_ptr<base_element_type>())
          {}

          /**
           * Construct with initial wrapped value (unmanaged).
           *
           * @param wrapped the value to wrap.
           */
          explicit
          Base(base_element_type *wrapped):
            base(wrapped ?
                 ome::compat::shared_ptr<base_element_type>(wrapped, &ome::common::xml::dom::detail::unmanaged<base_element_type>) :
                 ome::compat::shared_ptr<base_element_type>())
          {}

          /// Destructor.
          virtual
          ~Base()
          {}

          /**
           * Get wrapped base_element_type *.
           *
           * @note May be null.
           *
           * @returns the wrapped base_element_type.
           */
          base_element_type*
          get()
          {
            return base.get();
          }

          /**
           * Get wrapped base_element_type *.
           *
           * @note May be null.
           *
           * @returns the wrapped base_element_type.
           */
          const base_element_type*
          get() const
          {
            return base.get();
          }

          /**
           * Check if the wrapped type is NULL.
           *
           * @returns true if valid, false if NULL.
           */
          operator bool () const
          {
            return get() != 0;
          }

          /**
           * Free the managed resource.
           *
           * The managed resource will be freed and made null.
           */
          void
          reset()
          {
            base.reset();
            ome::compat::shared_ptr<base_element_type> n;
            assign(n);
          }

        protected:
          /**
           * Check if the wrapped type is NULL.
           *
           * @throws a @c std::logic_error if NULL.
           */
          virtual
          void
          null_check() const
          {
            if (!base.get())
              throw std::logic_error("Accessing null wrapped DOM type");
          }

          /**
           * Assign a new wrapped value.
           *
           * @param wrapped the new value.
           */
          virtual
          void
          assign(const base_type& wrapped)
          {
            // Assign base directly to refcount already wrapped objects.
            base = wrapped.base;
          }

          /**
           * Assign a new wrapped value.
           *
           * @param wrapped the new value.
           */
          virtual
          void
          assign(ome::compat::shared_ptr<base_element_type>& wrapped)
          {
            base = wrapped;
          }

          /**
           * Check that a new wrapped value is of the correct derived
           * type.
           *
           * Intended for use by derivied classes.
           *
           * @param newbase the new value.
           * @returns the value cast to the derived type.
           * @throws a @c std::logic_error if not of the specified type.
           */
          template<typename D>
          D *
          assign_check(base_element_type *newbase)
          {
            D *newderived = dynamic_cast<D *>(newbase);
            if (newbase && !newderived)
              throw std::logic_error("Failed to assign incompatible wrapped DOM type");
            return newderived;
          }

        private:
          /// Wrapped reference.
          ome::compat::shared_ptr<base_element_type> base;
        };

      }
    }
  }
}

#endif // OME_COMMON_XML_DOM_BASE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
