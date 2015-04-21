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

#ifndef OME_BIOFORMATS_TIFF_FIELD_H
#define OME_BIOFORMATS_TIFF_FIELD_H

#include <ome/compat/memory.h>

#include <string>

#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/Exception.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      class IFD;

      /**
       * Common functionality for fields of all types.
       */
      class FieldBase
      {
      protected:
        /**
         * Constructor.
         *
         * @param ifd the directory the field belongs to.
         * @param tag the tag identifying this field.
         */
        FieldBase(ome::compat::shared_ptr<IFD> ifd,
                  tag_type                     tag);

      public:
        /// Destructor.
        virtual
        ~FieldBase();

        /**
         * Get field data type.
         *
         * @returns the data type used by this field.
         * @see TIFFFieldDataType().
         */
        Type
        type() const;

        /**
         * Get field count requirement.
         *
         * @returns @c true if a count parameter is needed, @c false
         * otherwise.
         * @see TIFFFieldPassCount().
         */
        bool
        passCount() const;

        /**
         * Get field read count requirement.
         *
         * @todo: Check TIFF_VARIABLE/TIFF_VARIABLE2/TIFF_SPP and
         * adjust the return value accordingly.
         *
         * @returns the number of values to read.
         * @see TIFFFieldReadCount().
         */
        int
        readCount() const;

        /**
         * Get field write count requirement.
         *
         * @todo: Check TIFF_VARIABLE/TIFF_VARIABLE2/TIFF_SPP and
         * adjust the return value accordingly.
         *
         * @returns the number of values to write.
         * @see TIFFFieldWriteCount().
         */
        int
        writeCount() const;

        /**
         * Get field tag number.
         *
         * @returns the tag number.
         * @see TIFFFieldTag().
         */
        tag_type
        tagNumber() const;

        /**
         * Get field tag name.
         *
         * @returns the registered name of the field, or @c Unknown if
         * not a registered tag.
         * @see TIFFFieldName().
         */
        std::string
        name() const;

        /**
         * Get the directory this field belongs to.
         *
         * @returns the directory.
         */
        ome::compat::shared_ptr<IFD>
        getIFD() const;

      protected:
        class Impl;
        /// Private implementation details.
        ome::compat::shared_ptr<Impl> impl;
      };

      /**
       * Field representing a tag value.
       */
      template<typename Tag>
      class Field : public FieldBase
      {
      public:
        /// The tag type (C++ enum type).
        typedef Tag tag_category;
        /// The tag value type (C++ type).
        typedef typename ::ome::bioformats::detail::tiff::TagProperties<tag_category>::value_type value_type;

        friend class IFD;

      protected:
        /**
         * Constructor.
         *
         * @param ifd the directory the field belongs to.
         * @param tag the tag identifying this field.
         */
        Field(ome::compat::shared_ptr<IFD> ifd,
              tag_category                 tag):
          FieldBase(ifd, getWrappedTag(tag)),
          tag(tag)
        {}


      public:
        /// Destructor.
        virtual ~Field()
        {}

        /**
         * Get the value for this field.
         *
         * @param value the variable in which the field value will be stored.
         */
        void
        get(value_type& value) const;

        /**
         * Set the value for this field.
         *
         * @param value the value to set.
         */
        void
        set(const value_type& value);

        /**
         * Assign a field value.
         *
         * @param field the field to assign from.
         * @returns the Field.
         */
        Field&
        operator=(const Field& field)
        {
          set(field);
          return *this;
        }

        /**
         * Cast operator for getting the value of this field.
         */
        operator value_type()
        {
          value_type v;
          get(v);
          return v;
        }

      protected:
        /// The tag identifying this field.
        tag_category tag;
      };

      /**
       * Proxy for getting and setting a Field value.
       *
       * This wraps a value to allow it to be assigned a value from a
       * Field as the left hand side of an expression.
       *
       * @note the lifetime of this wrapper may not exceed that of the
       * wrapped value since it is stored by reference.
       */
      template<typename V>
      class ValueProxy
      {
      public:
        /// The value type being wrapped.
        typedef V value_type;

      private:
        /// The wrapped value.
        V& value;

      public:
        /**
         * Construct from value.
         *
         * @param value the value to wrap.
         */
        explicit
        ValueProxy(value_type& value):
          value(value)
        {}

        /// Destructor.
        ~ValueProxy()
        {}

        /**
         * Assign from a ValueProxy.
         *
         * @param value the value to assign.
         * @returns the ValueProxy.
         */
        ValueProxy&
        operator= (const ValueProxy& value)
        { this->value = value.value; }

        /**
         * Assign from value_type.
         *
         * @param value the value to assign.
         * @returns the ValueProxy.
         */
        ValueProxy&
        operator= (const value_type& value)
        {
          this->value = value;
        }

        /**
         * Assign from Field.
         *
         * @param field the field to assign.
         * @returns the ValueProxy.
         */
        template<typename F>
        ValueProxy&
        operator= (const Field<F>& field)
        {
          field.get(value);
          return *this;
        }

        /**
         * Get the wrapped value.
         *
         * @returns the wrapped value.
         */
        value_type&
        get()
        {
          return value;
        }

        /**
         * Get the wrapped value.
         *
         * @returns the wrapped value.
         */
        const value_type&
        get() const
        {
          return value;
        }
      };

      /**
       * Field value.
       *
       * This contains a value to allow it to be assigned from a Field
       * as the left hand side of an expression.
       */
      template<typename V>
      class Value
      {
      public:
        /// The value type being wrapped.
        typedef V value_type;

      private:
        /// The value.
        V value;

      public:
        /// Constructor.
        explicit
        Value():
          value()
        {}

        /// Destructor.
        ~Value()
        {}

        /**
         * Assign from a Value.
         *
         * @param value the value to assign.
         * @returns the Value.
         */
        Value&
        operator= (const Value& value)
        { this->value = value.value; }

        /**
         * Assign from value_type.
         *
         * @param value the value to assign.
         * @returns the Value.
         */
        Value&
        operator= (const value_type& value)
        {
          this->value = value;
        }

        /**
         * Assign from Field.
         *
         * @param field the field to assign.
         * @returns the Value.
         */
        template<typename F>
        Value&
        operator= (const Field<F>& field)
        {
          field.get(value);
          return *this;
        }

        /**
         * Get the value.
         *
         * @returns the wrapped value.
         */
        value_type&
        get()
        {
          return value;
        }

        /**
         * Get the value.
         *
         * @returns the wrapped value.
         */
        const value_type&
        get() const
        {
          return value;
        }
      };

    }
  }
}

#endif // OME_BIOFORMATS_TIFF_FIELD_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
