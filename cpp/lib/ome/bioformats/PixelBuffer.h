/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#ifndef OME_BIOFORMATS_PIXELBUFFER_H
#define OME_BIOFORMATS_PIXELBUFFER_H

#include <string>
#include <vector>

#include <boost/multi_array.hpp>

#include <ome/compat/cstdint.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Base class for all pixel buffer types.
     *
     * This class exists solely to allow pixel buffers to be exchanged
     * without regard to their specific type.
     */
    class PixelBufferBase
    {
    protected:
      /// Constructor.
      PixelBufferBase()
      {}

      /// Destructor.
      virtual
      ~PixelBufferBase()
      {}
    };

    /**
     * A byte buffer with no specified structure.
     */
    class PixelBufferRaw : public PixelBufferBase
    {
    public:
      /// Type of underlying pixels.
      typedef uint8_t pixel_type;
      /// Type of backing buffer.
      typedef std::vector<pixel_type> storage_type;
      /// Size type for sizing and indexing.
      typedef storage_type::size_type size_type;

      /**
       * Construct with initial buffer size.
       *
       * @param size the buffer size to allocate.
       */
      PixelBufferRaw(size_type size):
        PixelBufferBase(),
        bytes(size)
      {}

      /**
       * Construct from iterator range.
       *
       * @param first start of range.
       * @param last end of range.
       */
      template <class InputIterator>
      PixelBufferRaw(InputIterator first,
                     InputIterator last):
        PixelBufferBase(),
        bytes(first, last)
      {}

      /**
       * Copy constructor.
       *
       * @param copy the pixel buffer to copy.
       */
      PixelBufferRaw(PixelBufferRaw& copy):
        PixelBufferBase(copy),
        bytes(copy.bytes)
      {}

      /// Destructor.
      ~PixelBufferRaw()
      {}

      /**
       * Compare a pixel buffer for equality.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if equal, @c false if not equal.
       */
      bool
      operator == (const PixelBufferRaw& rhs) const
      {
        return bytes == rhs.bytes;
      }

      /**
       * Compare a pixel buffer for inequality.
       *
       * @param rhs the pixel buffer to compare with.
       * @returns @c true if not equal, @c false if equal.
       */
      bool
      operator != (const PixelBufferRaw& rhs) const
      {
        return bytes != rhs.bytes;
      }

      /**
       * Get a pixel by its index.
       *
       * Note that this method does not perform bounds checking.
       *
       * @param index the index to get.
       * @returns the pixel value.
       */
      const pixel_type&
      operator [] (size_type index) const
      {
        return bytes[index];
      }

      /**
       * Get a pixel by its index.
       *
       * Note that this method does not perform bounds checking.
       *
       * @param index the index to get.
       * @returns the pixel value.
       */
      pixel_type&
      operator [] (size_type index)
      {
        return bytes[index];
      }

      /**
       * Get a pixel by its index.
       *
       * @param index the index to get.
       * @returns the pixel value.
       */
      const pixel_type&
      at(size_type index) const
      {
        return bytes.at(index);
      }

      /**
       * Get a pixel by its index.
       *
       * @param index the index to get.
       * @returns the pixel value.
       */
      pixel_type&
      at(size_type index)
      {
        return bytes.at(index);
      }

      /**
       * Get buffer size.
       *
       * @returns the buffer size in bytes.
       */
      size_type
      size() const
      {
        return bytes.size();
      }

      /**
       * Get buffer location.
       *
       * @returns a pointer to the buffer start address.
       */
      uint8_t *
      buffer()
      {
        if (bytes.empty())
          return 0;
        return &bytes.front();
      }

      /**
       * Get buffer location.
       *
       * @returns a pointer to the buffer start address.
       */
      const uint8_t *
      buffer() const
      {
        if (bytes.empty())
          return 0;
        return &bytes.front();
      }

    private:
      /// Backing storage.
      std::vector<uint8_t> bytes;
    };

    /**
     * Output PixelBufferRaw to output stream.
     *
     * @param os the output stream.
     * @param buf the PixelBuffer to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
    inline std::basic_ostream<charT,traits>&
    operator<< (std::basic_ostream<charT,traits>& os,
                const PixelBufferRaw& buf)
    {
      return os << "PixelBufferRaw size = " << buf.size();
    }

        /**
         * Set PixelBufferRaw from input stream.
         *
         * @param is the input stream.
         * @param buf the PixelBufferRaw to set.
         * @returns the input stream.
         */
        template<class charT, class traits>
        inline std::basic_istream<charT,traits>&
        operator>> (std::basic_istream<charT,traits>& is,
                    PixelBufferRaw& buf)
        {
          return is.read(reinterpret_cast<char *>(buf.buffer()), buf.size());
        }

  }
}

#endif // OME_BIOFORMATS_PIXELBUFFER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
