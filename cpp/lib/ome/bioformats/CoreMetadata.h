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

#ifndef OME_BIOFORMATS_COREMETADATA_H
#define OME_BIOFORMATS_COREMETADATA_H

#include <map>
#include <numeric>
#include <string>
#include <vector>

#include <ome/compat/cstdint.h>

#include <ome/bioformats/MetadataMap.h>
#include <ome/bioformats/Modulo.h>
#include <ome/bioformats/Types.h>

#include <ome/xml/meta/BaseMetadata.h>
#include <ome/xml/model/enums/DimensionOrder.h>
#include <ome/xml/model/enums/PixelType.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Core metadata values.
     *
     * @note The Java implementation had a constructor to construct by
     * copying the core metadata from a series of an existing reader.
     * This is not duplicated here.  Use this instead:
     *
     * `CoreMetadata copy(reader.getCoreMetadataList().at(index));`
     *
     * Where @c reader is a FormatReader and @c index is the core index.
     *
     * @todo We may want to consider refactoring the FormatReader
     * getter methods that fill in missing CoreMetadata fields on the
     * fly (getThumbSizeX, getThumbSizeY) to avoid doing so -- one
     * alternate approach would be to have this class use getter
     * methods instead of public fields.
     */
    class CoreMetadata
    {
    public:
      /// Width (in pixels) of images in this series.
      dimension_size_type sizeX;

      /// Height (in pixels) of images in this series.
      dimension_size_type sizeY;

      /// Number of Z sections.
      dimension_size_type sizeZ;

      /// Number of channels.
      std::vector<dimension_size_type> sizeC;

      /// Number of timepoints.
      dimension_size_type sizeT;

      /// Width (in pixels) of thumbnail images in this series.
      dimension_size_type thumbSizeX;

      /// Height (in pixels) of thumbnail images in this series.
      dimension_size_type thumbSizeY;

      /// Number of bytes per pixel.
      ome::xml::model::enums::PixelType pixelType;

      /// Number of valid bits per pixel. */
      pixel_size_type bitsPerPixel;

      /// Total number of images.
      dimension_size_type imageCount;

      /// Modulo Z dimension.
      Modulo moduloZ;

      /// Modulo T dimension.
      Modulo moduloT;

      /// Modulo C dimension.
      Modulo moduloC;

      /// Order in which dimensions are stored.
      ome::xml::model::enums::DimensionOrder dimensionOrder;

      /// Are we confident that the dimension order is correct?
      bool orderCertain;

      /// Is the pixel byte order little endian?
      bool littleEndian;

      /**
       * @c true if channels are stored RGBRGBRGB...; @c false if channels are stored
       * RRR...GGG...BBB...
       */
      bool interleaved;

      /// Are images are stored as indexed color?
      bool indexed;

      /// Can the color map (if present) be ignored?
      bool falseColor;

      /// Is all of the metadata stored within the file parsed correctly?
      bool metadataComplete;

      /// Non-core metadata associated with this series.
      MetadataMap seriesMetadata;

      /// Is this series is a lower-resolution copy of another series?
      bool thumbnail;

      /**
       *  Sub-resolution count.  The number of images following this
       *  image which are sub-sampled lower resolution copies of this
       *  image.  The count includes this image, so an image with two
       *  following sub-resolution images will have a count of @c 3 .
       */
      dimension_size_type resolutionCount;

      /// Constructor.
      CoreMetadata();

      /**
       * Copy constructor.
       *
       * @param copy the CoreMetadata to copy.
       */
      CoreMetadata(const CoreMetadata& copy);

      /// Destructor.
      virtual
      ~CoreMetadata();
    };

    /**
     * Output CoreMetadata to output stream.
     *
     * @param os the output stream.
     * @param core the CoreMetadata to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
    inline std::basic_ostream<charT,traits>&
    operator<< (std::basic_ostream<charT,traits>& os,
                const CoreMetadata& core)
    {
      os << "sizeX = " << core.sizeX << '\n'
         << "sizeY = " << core.sizeY << '\n'
         << "sizeZ = " << core.sizeZ << '\n'
         << "sizeC = " << std::accumulate(core.sizeC.begin(), core.sizeC.end(), dimension_size_type(0));
      if (core.sizeC.size() > 1U)
        {
          os << " [";
          for (std::vector<dimension_size_type>::const_iterator i = core.sizeC.begin();
               i != core.sizeC.end();
               ++i)
            {
              os << *i;
              if (i + 1 != core.sizeC.end())
                os << ", ";
            }
          os << ']';
        }
      os << "\nsizeT = " << core.sizeT << '\n'
         << "thumbSizeX = " << core.thumbSizeX << '\n'
         << "thumbSizeY = " << core.thumbSizeY << '\n'
         << "pixelType = " << core.pixelType << '\n'
         << "bitsPerPixel = " << core.bitsPerPixel << '\n'
         << "imageCount = " << core.imageCount << '\n'
         << "moduloZ = {\n" << core.moduloZ
         << "}\nmoduloT = {\n" << core.moduloT
         << "}\nmoduloC = {\n" << core.moduloC
         << "}\ndimensionOrder = " << core.dimensionOrder << '\n'
         << "orderCertain = " << core.orderCertain << '\n';
      os << "rgb = [";
      for (std::vector<dimension_size_type>::const_iterator i = core.sizeC.begin();
           i != core.sizeC.end();
           ++i)
        {
          os << (*i > 1);
          if (i + 1 != core.sizeC.end())
            os << ", ";
        }
      os << ']';
      os << "\nlittleEndian = " << core.littleEndian << '\n'
         << "interleaved = " << core.interleaved << '\n'
         << "indexed = " << core.indexed << '\n'
         << "falseColor = " << core.falseColor << '\n'
         << "metadataComplete = " << core.metadataComplete << '\n'
         << "seriesMetadata = " << core.seriesMetadata.size() << " keys" << '\n'
         << "thumbnail = " << core.thumbnail << '\n'
         << "resolutionCount = " << core.resolutionCount << '\n';
      return os;
    }

  }
}

#endif // OME_BIOFORMATS_COREMETADATA_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
