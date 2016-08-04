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

#ifndef OME_BIOFORMATS_TILEBUFFER_H
#define OME_BIOFORMATS_TILEBUFFER_H

#include <ome/bioformats/Types.h>

#include <ome/xml/model/enums/PixelType.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Tile pixel data buffer.
     *
     * Pixel data for a single tile.
     */
    class TileBuffer
    {
    public:
      /**
       * Constructor.
       *
       * @param size the buffer size (bytes).
       */
      explicit
      TileBuffer(dimension_size_type size);

      /// Destructor.
      virtual ~TileBuffer();

    private:
      // To avoid unintentional and expensive copies, copying and
      // assignment of buffers is prevented.

      /// Copy constructor (deleted).
      TileBuffer (const TileBuffer&);

      /// Assignment operator (deleted).
      TileBuffer&
      operator= (const TileBuffer&);

    public:
      /**
       * Get the buffer size.
       *
       * @returns the buffer size.
       */
      dimension_size_type
      size() const;

      /**
       * Get the buffer data.
       *
       * @returns a pointer to the data.
       */
      uint8_t *
      data();

      /**
       * Get the buffer data.
       *
       * @returns a pointer to the data.
       */
      const uint8_t *
      data() const;

    private:
      /// Buffer size (bytes).
      dimension_size_type bufsize;
      /// Raw buffer.
      uint8_t *buf;
    };

  }
}

#endif // OME_BIOFORMATS_TILEBUFFER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
