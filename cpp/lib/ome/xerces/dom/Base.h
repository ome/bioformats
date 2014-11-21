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

#ifndef OME_XERCES_DOM_BASE_H
#define OME_XERCES_DOM_BASE_H

#include <ome/compat/config.h>
#include <ome/compat/memory.h>

#include <stdexcept>

namespace ome
{
  namespace xerces
  {
    namespace dom
    {

      /// Lifetime and memory management strategy for wrapped types.
      enum ManagementStrategy
        {
          MANAGED,  ///< Managed by the wrapper.
          UNMANAGED ///< Managed externally.
        };

      /**
       * Manager of a wrapped resource.
       *
       * Only to be used via a shared_ptr since it's non-copyable.
       * The managed instance will be freed when this manager object
       * is destroyed.
       */
      template<typename T>
      class Manager
      {
      public:
        /// Wrapped Xerces type.
        typedef T element_type;

      private:
        /// The managed resource.
        element_type *wrapped;

      public:
        /// Constructor.
        Manager():
          wrapped()
        {}

        /**
         * Construct from pointer to resource.
         *
         * @param wrapped the resource to manage.
         */
        Manager(element_type *wrapped):
          wrapped(wrapped)
        {}

        /**
         * Destructor.
         *
         * The managed resource (if any) will be released.
         */
        ~Manager()
        {
          if (wrapped)
            wrapped->release();
        }

      private:
        /// Copy constructor (deleted).
        Manager (const Manager&);

        /// Assignment operator (deleted).
        Manager&
        operator= (const Manager&);

      public:
        /**
         * Get the managed resource.
         *
         * @returns a pointer to the managed resource.
         */
        element_type *
        get()
        { return wrapped; }

        /**
         * Get the managed resource.
         *
         * @returns a pointer to the managed resource.
         */
        const element_type *
        get() const
        { return wrapped; }

        /**
         * Release the managed resource.
         *
         * The managed resource will be null after this method
         * returns.
         *
         * @note This will not free the resource.  Only use this to
         * transfer ownership and prevent the resource being
         * destroyed when this object is destroyed.
         * @returns the managed resource.
         */
        element_type *
        release()
        {
          element_type *ret = wrapped;
          wrapped = 0;
          return ret;
        }

        /**
         * Reset the managed resource.
         *
         * The managed resource will be null after this method
         * returns.
         *
         * @note This will free the resource if it is the last
         * reference to it.
         */
        void
        reset()
        {
        }
      };

      /**
       * Base of the DOM wrapper hierarchy.
       */
      template<typename T, int S>
      class BaseElement;

      /**
       * Managed base of the DOM wrapper hierarchy.
       */
      template<typename T>
      class BaseElement<T, MANAGED>
      {
      public:
        /// Wrapped Xerces type.
        typedef T element_type;
        /// Wrapper type.
        typedef Manager<element_type> manager_type;

        /**
         * Constructor.
         *
         * A null manager will be created.
         */
        BaseElement():
          wrapped(std::shared_ptr<manager_type>(new manager_type()))
        {}

        /**
         * Constructor.
         *
         * A new manager will be created taking ownership of the
         * wrapped value.
         *
         * @param wrapped the value to wrap and take ownership of.
         */
        BaseElement(element_type *wrapped):
          wrapped(std::shared_ptr<manager_type>(new manager_type(wrapped)))
        {}

        /**
         * Get wrapped element_type.
         *
         * @note May be null.
         *
         * @returns the wrapped element_type.
         */
        element_type *
        get()
        { return wrapped->get(); }

        /**
         * Get wrapped element_type.
         *
         * @note May be null.
         *
         * @returns the wrapped element_type.
         */
        const element_type *
        get() const
        { return wrapped->get(); }

        /**
         * Set wrapped element_type.
         *
         * @note This will take ownership of the wrapped value.
         *
         * @param wrapped the new value.
         */
        void
        set(element_type *wrapped)
        { this->wrapped = std::shared_ptr<manager_type>(new manager_type(wrapped));; }

        /**
         * Release the managed resource.
         *
         * The managed resource will be null after this method
         * returns.
         *
         * @note This will not free the resource.  Only use this to
         * transfer ownership and prevent the resource being
         * destroyed when this object is destroyed.
         * @returns the managed resource.
         */
        element_type *
        release()
        {
          return this->wrapped->release();
        }

        /**
         * Free the managed resource.
         *
         * The managed resource will be freed and made null.
         */
        void
        reset()
        {
          wrapped = std::shared_ptr<manager_type>(new manager_type());
        }

      private:
        /// Managed reference.
        std::shared_ptr<manager_type> wrapped;
      };

      /**
       * Unmanaged base of the DOM wrapper hierarchy.
       */
      template<typename T>
      class BaseElement<T, UNMANAGED>
      {
      public:
        /// Wrapped Xerces type.
        typedef T element_type;

        /// Constructor.
        BaseElement():
          wrapped()
        {}

        /**
         * Constructor.
         *
         * @param wrapped the value to wrap without taking ownership.
         */
        BaseElement(element_type *wrapped):
          wrapped(wrapped)
        {}

        /**
         * Get wrapped element_type.
         *
         * @note May be null.
         *
         * @returns the wrapped element_type.
         */
        element_type *
        get()
        { return wrapped; }

        /**
         * Get wrapped element_type.
         *
         * @note May be null.
         *
         * @returns the wrapped element_type.
         */
        const element_type *
        get() const
        { return wrapped; }

        /**
         * Set wrapped element_type.
         *
         * @param wrapped the new value.
         */
        void
        set(element_type *wrapped)
        { this->wrapped = wrapped; }

        /**
         * Release the unmanaged resource.
         *
         * For this unmanaged implementation, the wrapped value will
         * null after this method returns.  No releasing occurs since
         * we don't own it.
         *
         * @returns the unmanaged resource.
         */
        element_type *
        release()
        {
          element_type *ret = this->wrapped;
          this->wrapped = 0;
          return ret;
        }

        /**
         * Free the managed resource.
         *
         * The unmanaged resource will be made null.
         */
        void
        reset()
        {
          this->wrapped = 0;
        }
      private:
        /// Unmanaged reference.
        element_type *wrapped;
      };

      /**
       * Base of the DOM wrapper hierarchy.
       *
       * This contains the managed or unmanaged base pointer to the
       * wrapped type.
       */
      template<typename T, int S>
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
         * Construct with initial wrapped value.
         *
         * @param base the value to wrap.
         */
        Base(base_element_type *base):
          base(base)
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
         * Release the managed resource.
         *
         * The managed resource will be null after this method
         * returns.
         *
         * @note This will not free the resource.  Only use this to
         * transfer ownership and prevent the resource being
         * destroyed when this object is destroyed.
         * @returns the managed resource.
         */
        T *
        release()
        {
          T *ret = base.release();
          assign(0);
          return ret;
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
          assign(0);
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
         * @param newbase the new value.
         */
        virtual
        void
        assign(base_element_type *newbase)
        {
          base.set(newbase);
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
        BaseElement<T, S> base;
      };

    }
  }
}

#endif // OME_XERCES_DOM_BASE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
