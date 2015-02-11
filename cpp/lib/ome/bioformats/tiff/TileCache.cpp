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

#include <ome/bioformats/tiff/TileCache.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      TileCache::TileCache():
        cache()
      {
      }

      TileCache::~TileCache()
      {
      }

      bool
      TileCache::insert(key_type   tileindex,
                        value_type tilebuffer)
      {
        std::pair<std::map<key_type, value_type>::iterator, bool> i =
          cache.insert(std::pair<key_type, value_type>(tileindex, tilebuffer));
        return i.second;
      }

      void
      TileCache::erase(key_type tileindex)
      {
        cache.erase(tileindex);
      }

      TileCache::value_type
      TileCache::find(key_type tileindex)
      {
        std::map<key_type, value_type>::iterator i = cache.find(tileindex);
        if (i != cache.end())
          return i->second;
        else
          return value_type();
      }

      const TileCache::value_type
      TileCache::find(key_type tileindex) const
      {
        std::map<key_type, value_type>::const_iterator i = cache.find(tileindex);
        if (i != cache.end())
          return i->second;
        else
          return value_type();
      }

      dimension_size_type
      TileCache::size() const
      {
        return cache.size();
      }

      void
      TileCache::clear()
      {
        cache.clear();
      }

      TileCache::value_type&
      TileCache::operator[](key_type tileindex)
      {
        std::map<key_type, value_type>::iterator i = cache.find(tileindex);
        if (i != cache.end())
          return i->second;
        else
          {
            value_type value;
            std::pair<std::map<key_type, value_type>::iterator, bool> i =
              cache.insert(std::pair<key_type, value_type>(tileindex, value));
            return i.first->second;
          }
      }

    }
  }
}
