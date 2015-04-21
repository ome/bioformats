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

#ifndef OME_BIOFORMATS_TILECACHE_H
#define OME_BIOFORMATS_TILECACHE_H

#include <ome/bioformats/Types.h>
#include <ome/bioformats/TileBuffer.h>

#include <ome/compat/memory.h>

#include <map>

namespace ome
{
  namespace bioformats
  {

    /**
     * Tile cache.
     *
     * This is a collection of TileBuffer objects indexed by tile
     * number.
     */
    class TileCache
    {
    public:
      /// Tile index type.
      typedef dimension_size_type key_type;
      /// Tile buffer type.
      typedef ome::compat::shared_ptr<TileBuffer> value_type;

      /// Constructor.
      TileCache();

      /// Destructor.
      virtual ~TileCache();

    private:
      // To avoid unintentional and expensive copies, copying and
      // assignment of caches is prevented.

      /// Copy constructor (deleted).
      TileCache (const TileCache&);

      /// Assignment operator (deleted).
      TileCache&
      operator= (const TileCache&);

    public:
      /**
       * Insert a tile into the tile cache.
       *
       * The tile index must not already be present in the tile
       * cache.  If it is already present, the insert will fail.
       *
       * The tilebuffer must not be null.  If the tilebuffer is null
       * the insert will fail.
       *
       * @param tileindex the tile index of the tile buffer.
       * @param tilebuffer the buffered tile pixel data.
       * @returns @c true if the insert succeeded, @c false otherwise.
       */
      bool
      insert(key_type   tileindex,
             value_type tilebuffer);

      /**
       * Remove a tile from the tile cache.
       *
       * @param tileindex the tile to remove.
       */
      void
      erase(key_type tileindex);

      /**
       * Find a tile in the tile cache.
       *
       * @param tileindex the tile index to find.
       * @returns the tile buffer corresponding to the specified
       * index.  If the tile index was not found, this will be null.
       */
      value_type
      find(key_type tileindex);

      /**
       * Find a tile in the tile cache.
       *
       * @param tileindex the tile index to find.
       * @returns the tile buffer corresponding to the specified
       * tile index.  If the tile index was not found, this will be
       * null.
       */
      const value_type
      find(key_type tileindex) const;

      /**
       * Get the tile cache size.
       *
       * @returns the tile cache size.
       */
      dimension_size_type
      size() const;

      /**
       * Clear the tile cache.
       */
      void
      clear();

      /**
       * Get a tile from the tile cache.
       *
       * If the tile index is not found, it will be inserted into
       * the cache with a null tile buffer; since this is returned
       * by reference it may be assigned a new tile buffer directly.
       *
       * @param tileindex the tile index to get.
       * @returns the tile buffer corresponding to the specified
       * tile index.  If the tile index was not found, this will be
       * null.
       */
      value_type&
      operator[](key_type tileindex);

    private:
      /// Mapping of tile number to tile buffer.
      std::map<key_type, value_type> cache;
    };

  }
}

#endif // OME_BIOFORMATS_TILECACHE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
