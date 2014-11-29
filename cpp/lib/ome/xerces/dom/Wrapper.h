/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
 * %%
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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

#ifndef OME_XERCES_DOM_WRAPPER_H
#define OME_XERCES_DOM_WRAPPER_H

#include <ome/compat/config.h>

#include <string>
#include <stdexcept>

namespace ome
{
  namespace xerces
  {
    namespace dom
    {

      /**
       * Xerces DOM class wrapper.  The wrapper behaves as though is
       * the wrapped DOM type; it can be dereferenced using the "*" or
       * "->" operators to obtain a reference or pointer to the
       * wrapped object.  It can also be cast to a pointer to the
       * wrapped object, so can substitute for it directly.
       *
       * The purpose of this wrapper is to manage the lifetime of DOM
       * types, which would otherwise require manual memory
       * management.
       */
      template<typename WrappedType, typename Parent>
      class Wrapper : public Parent
      {
      public:
        /// Parent type.
        typedef Parent parent_type;
        /// Base type.
        typedef typename parent_type::base_type base_type;
        /// Base element type
        typedef typename parent_type::base_element_type base_element_type;
        /// Wrapped Xerces element type.
        typedef WrappedType element_type;

        /// Constructor.
        Wrapper ():
          parent_type(),
          wrapped()
        {
        }

        /**
         * Copy construct a Wrapper.
         *
         * @param base the base type to copy (must be an element_type).
         */
        Wrapper (const base_type& base):
          parent_type(),
          wrapped()
        {
          assign(const_cast<base_element_type *>(base.get()));
        }

        /**
         * Construct a Wrapper from a base_element_type *.
         *
         * @param base the Wrapper to wrap.
         */
        Wrapper (typename parent_type::base_element_type *base):
          parent_type(),
          wrapped()
        {
          assign(base);
        }

        /// Destructor.
        ~Wrapper ()
        {
        }

        /**
         * Get wrapped element_type *.
         *
         * @note May be null.
         *
         * @returns the wrapped element_type.
         */
        element_type*
        get()
        {
          return wrapped;
        }

        /**
         * Get wrapped element_type *.
         *
         * @note May be null.
         *
         * @returns the wrapped element_type.
         */
        const element_type*
        get() const
        {
          return wrapped;
        }

      protected:
        /**
         * Assign a new reference to wrap.
         *
         * @note: If this is a managed wrapper, this will take
         * ownership of the reference.
         *
         * @param base the reference to assign.
         */
        virtual
        void
        assign(typename parent_type::base_element_type *base)
        {
          this->wrapped = this->template assign_check<element_type>(base);
          parent_type::assign(base);
        }

      public:
        /**
         * Assign a Wrapper.
         *
         * @param wrapped the Wrapper to assign.
         * @returns the Wrapper.
         */
        Wrapper&
        operator= (const Wrapper& wrapped)
        {
          assign(wrapped.wrapped);
          return *this;
        }

        /**
         * Assign a element_type *.
         *
         * @param wrapped the Wrapper to assign.
         * @returns the Wrapper.
         */
        Wrapper&
        operator= (element_type *wrapped)
        {
          assign(wrapped);
          return *this;
        }

        /**
         * Dereference to element_type.
         *
         * @returns the wrapped element_type.
         */
        element_type&
        operator* () noexcept
        {
          this->null_check();
          return *wrapped;
        }

        /**
         * Dereference to const element_type.
         *
         * @returns the wrapped element_type.
         */
        const element_type&
        operator* () const noexcept
        {
          this->null_check();
          return *wrapped;
        }

        /**
         * Dereference to element_type.
         *
         * @returns the wrapped element_type.
         */
        element_type *
        operator-> () noexcept
        {
          this->null_check();
          return wrapped;
        }

        /**
         * Dereference to const element_type.
         *
         * @returns the wrapped element_type.
         */
        const element_type *
        operator-> () const noexcept
        {
          this->null_check();
          return wrapped;
        }

      private:
        /// The wrapped type.
        element_type *wrapped;
      };

    }
  }
}

#endif // OME_XERCES_DOM_WRAPPER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
