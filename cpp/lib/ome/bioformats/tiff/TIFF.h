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

#ifndef OME_BIOFORMATS_TIFF_TIFF_H
#define OME_BIOFORMATS_TIFF_TIFF_H

#include <string>

#include <boost/iterator/iterator_facade.hpp>

#include <ome/bioformats/tiff/Types.h>

#include <ome/common/filesystem.h>

#include <ome/compat/cstdint.h>
#include <ome/compat/memory.h>

namespace ome
{
  namespace bioformats
  {
    /**
     * TIFF file format (libtiff wrapper).
     */
    namespace tiff
    {

      class IFD;

      /**
       * Iterator for IFDs contained within a TIFF.
       */
      template<typename Value>
      class IFDIterator : public boost::iterator_facade<IFDIterator<Value>,
                                                        ome::compat::shared_ptr<Value>,
                                                        boost::forward_traversal_tag>
      {
      public:
        /**
         * Default constructor.
         *
         * The position will be null.
         */
        IFDIterator():
          pos()
        {}

        /**
         * Construct with an initial starting position.
         *
         * The position will be the provided descriptor.
         *
         * @param ifd the descriptor to point to.
         */
        IFDIterator(ome::compat::shared_ptr<IFD>& ifd):
          pos(ifd)
        {}

        /**
         * Construct from an existing iterator.
         *
         * @param rhs the iterator to copy.
         */
        template <class OtherValue>
        IFDIterator(const IFDIterator<OtherValue>& rhs)
          : pos(rhs.pos) {}

      private:
        /**
         * The current position.
         *
         * It's mutable to allow const and non-const access to the
         * underlying descriptor via const and non-const iterators.
         */
        mutable ome::compat::shared_ptr<Value> pos;

        friend class boost::iterator_core_access;
        template <class> friend class IFDIterator;

        /**
         * Increment the iterator by one position.
         *
         * @note This isn't inlined because it requires access to the
         * IFD declarations, which can't be included here to avoid
         * dependency loops.
         */
        void
        increment();

        /**
         * Check for equality.
         *
         * @param rhs the iterator to compare.
         * @returns @c true if equal, @c false otherwise.
         */
        bool
        equal(IFDIterator const& rhs) const
        {
          return this->pos == rhs.pos;
        }

        /**
         * Dereference the iterator.
         *
         * @returns a reference to currently referenced descriptor.
         */
        ome::compat::shared_ptr<Value>&
        dereference() const
        {
          return pos;
        }
      };

      /**
       * Tagged Image File Format (TIFF).
       *
       * This class is the primary class for reading and writing TIFF
       * files.  Use the static open() method to get a working
       * instance.  This instance may be used to get IFD instances and
       * then access to image metadata and pixel data.
       */
      class TIFF : public ome::compat::enable_shared_from_this<TIFF>
      {
      private:
        class Impl;
        class wrapped_type;
        /// Private implementation details.
        ome::compat::shared_ptr<Impl> impl;

      protected:
        /// Constructor (non-public).
        TIFF(const boost::filesystem::path& filename,
             const std::string&             mode);

      private:
        /// Copy constructor (deleted).
        TIFF (const TIFF&);

        /// Assignment operator (deleted).
        TIFF&
        operator= (const TIFF&);

      public:
        /// Destructor.
        ~TIFF();

        /**
         * Open a TIFF file for reading or writing.
         *
         * @note There are additional open flags, documented in
         * TIFFOpen(3).
         *
         * @param filename the file to open.
         * @param mode the file open mode (@c r to read, @c w to write
         * or @c a to append).
         * @returns the the open TIFF.
         * @throws an Exception on failure.
         */
        static ome::compat::shared_ptr<TIFF>
        open(const boost::filesystem::path& filename,
             const std::string&             mode);

        /**
         * Close the TIFF file.
         *
         * Note that this will be done automatically when the
         * destructor runs.  Any further method calls using this
         * object or any child IFD will throw an Exception.
         */
        void
        close();

        /**
         * Check if the TIFF file is valid.
         *
         * @returns @c true if the file is open and available for
         * reading and writing, or @c false if closed or invalid.
         */
        operator bool ();

        /**
         * Get the total number of IFDs.
         *
         * @returns the IFD count.
         */
        directory_index_type
        directoryCount() const;

        /**
         * Get an IFD by its index.
         *
         * @param index the directory index.
         * @returns the IFD.
         * @throws an Exception if the index is invalid or could not
         * be accessed.
         */
        ome::compat::shared_ptr<IFD>
        getDirectoryByIndex(directory_index_type index) const;

        /**
         * Get an IFD by its offset in the file.
         *
         * @param offset the directory offset.
         * @returns the IFD.
         * @throws an Exception if the offset is invalid or could not
         * be accessed.
         */
        ome::compat::shared_ptr<IFD>
        getDirectoryByOffset(offset_type offset) const;

        /**
         * Get the currently active IFD.
         *
         * @returns the IFD.
         * @throws an Exception if the IFD could not be accessed.
         */
        ome::compat::shared_ptr<IFD>
        getCurrentDirectory() const;

        /**
         * Write the currently active IFD.
         *
         * The pixel data accompanying this IFD must have been written
         * using IFD::writeImage() prior to calling this method, or
         * else the TIFF tags for strip and tile offsets will be
         * incomplete and the file will fail to read.
         */
        void
        writeCurrentDirectory();

        /**
         * Get the underlying libtiff @c \::TIFF instance.
         *
         * If there is any need to use the libtiff C interface to
         * access any functionality not exposed through these C++
         * wrapper classes, this will provide a pointer to the handle.
         *
         * @note Due to not including the libtiff C headers the @c
         * \::TIFF type isn't available here.  After including the
         * main @c <tiffio.h> header, cast the return value to the
         * correct type:
         *
         * @verbatim
         * ::TIFF *tiff = reinterpret_cast< ::TIFF *>(myfile.getWrapped());
         * @endverbatim
         *
         * @returns an opaque pointer to the wrapped @c \::TIFF
         * instance.
         */
        wrapped_type *
        getWrapped() const;

        friend class IFD;

        /// IFD iterator.
        typedef IFDIterator<IFD> iterator;
        /// const IFD iterator.
        typedef IFDIterator<const IFD> const_iterator;

        /**
         * Get the first image file directory.
         *
         * @returns an iterator referring to the first image file directory
         */
        iterator
        begin();

        /**
         * Get the first image file directory.
         *
         * @returns an iterator referring to the first image file directory
         */
        const_iterator
        begin() const;

        /**
         * Get the last+1 image file directory.
         *
         * @returns an iterator referring to the last+1 image file directory.
         */
        iterator
        end();

        /**
         * Get the last+1 image file directory.
         *
         * @returns an iterator referring to the last+1 image file directory.
         */
        const_iterator
        end() const;

      private:
        /// Register ImageJ tags with libtiff for this image.
        void
        registerImageJTags();
      };

    }
  }
}

#endif // OME_BIOFORMATS_TIFF_TIFF_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
