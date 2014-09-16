/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#ifndef OME_BIOFORMATS_TIFF_IFD_H
#define OME_BIOFORMATS_TIFF_IFD_H

#include <string>

#include <ome/compat/memory.h>

#include <ome/bioformats/tiff/Types.h>

#include <ome/xml/model/enums/PixelType.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      class TIFF;

      /// Forward declaration of Field<Tag>.
      template<typename Tag>
      class Field;

      /**
       * Image File Directory (IFD).
       *
       * An IFD represents a subfile within a TIFF.
       */
      class IFD : public std::enable_shared_from_this<IFD>
      {
      private:
        class Impl;
        /// Private implementation details.
        std::shared_ptr<Impl> impl;

      protected:
        /// Constructor (not public).
        IFD(std::shared_ptr<TIFF>& tiff,
            offset_type            offset);

      private:
        /// Copy constructor (deleted).
        IFD (const IFD&);

        /// Assignment operator (deleted).
        IFD&
        operator= (const IFD&);

      public:
        /// Destructor.
        virtual ~IFD();

        /**
         * Open an IFD by index.
         *
         * @param tiff the source TIFF.
         * @param index the directory index.
         * @returns the open IFD.
         */
        static std::shared_ptr<IFD>
        openIndex(std::shared_ptr<TIFF>& tiff,
                  directory_index_type   index);

        /**
         * Open an IFD.
         *
         * @param tiff the source TIFF.
         * @param offset the directory offset.
         * @returns the open IFD.
         */
        static std::shared_ptr<IFD>
        openOffset(std::shared_ptr<TIFF>& tiff,
                   offset_type            offset);

        /**
         * Get the source TIFF this descriptor belongs to.
         *
         * @returns the source TIFF.
         */
        std::shared_ptr<TIFF>&
        getTIFF() const;

        /**
         * Make this IFD the current directory.
         *
         * Internally this is simply a call to TIFFSetDirectory.
         */
        void
        makeCurrent() const;

        /**
         * Get the directory offset.
         *
         * Internally this is simply a call to TIFFCurrentDirOffset.
         *
         * @returns the directory offset.
         */
        offset_type
        getOffset() const;

        /**
         * Get a field by its tag number.
         *
         * @note This should not be used except internally.  Use
         * getField(TagCategory) instead which offers a type-safe
         * interface on top of this lower-level TIFFGetField wrapper.
         *
         * @param tag the tag number.
         * @param ... pointers to variables to store the value(s) in.
         */
        void
        getRawField(tag_type tag,
                    ...) const;

        /**
         * Set a field by its tag number.
         *
         * @note This should not be used except internally.  Use
         * getField(TagCategory) instead which offers a type-safe
         * interface on top of this lower-level TIFFSetField wrapper.
         *
         * @param tag the tag number.
         * @param ... variables containing the value(s) to set.
         */
        void
        setRawField(tag_type tag,
                    ...);

        /**
         * Get a Field by its tag enumeration.
         *
         * @param tag the field identifier.
         * @returns the Field corresponding to the tag.
         */
        template<typename TagCategory>
        Field<TagCategory>
        getField(TagCategory tag)
        {
          return Field<TagCategory>(this->shared_from_this(), tag);
        }

        /**
         * Get a Field by its tag enumeration.
         *
         * @param tag the field identifier.
         * @returns the Field corresponding to the tag.
         */
        template<typename TagCategory>
        const Field<TagCategory>
        getField(TagCategory tag) const
        {
          return Field<TagCategory>(const_cast<IFD *>(this)->shared_from_this(), tag);
        }

        /**
         * Get the OME data model PixelType.
         *
         * This is computed based upon the SampleFormat and
         * BitsPerSample tags for this IFD.
         *
         * @returns the PixelType.
         * @throws an Exception if there is no corresponding PixelType
         * for the SampleFormat and BitsPerSample in use.
         */
        ::ome::xml::model::enums::PixelType
        getPixelType() const;

        /**
         * Set the OME data model PixelType.
         *
         * This sets the SampleFormat and BitsPerSample tags for this
         * IFD which correspond to the PixelType in use.
         *
         * @param type the PixelType to set.
         * @throws an Exception if the PixelType is invalid.
         */
        void
        setPixelType(::ome::xml::model::enums::PixelType type);

        /**
         * Get next directory.
         *
         * @returns the next directory, or null if this is the last directory.
         */
        std::shared_ptr<IFD>
        next() const;

        /**
         * Check if this is the last directory.
         *
         * @returns @c true if last, @c false otherwise.
         */
        bool
        last() const;
      };

    }
  }
}

#endif // OME_BIOFORMATS_TIFF_IFD_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
