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

#ifndef OME_BIOFORMATS_COREMETADATA_H
#define OME_BIOFORMATS_COREMETADATA_H

#include <map>
#include <string>
#include <vector>

#include <ome/compat/cstdint.h>

#include <ome/bioformats/MetadataMap.h>
#include <ome/bioformats/Modulo.h>

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
     * @todo We may want to consider refactoring the FormatReader
     * getter methods that fill in missing CoreMetadata fields on the
     * fly (getChannelDimLengths, getChannelDimTypes, getThumbSizeX,
     * getThumbSizeY) to avoid doing so -- one alternate approach
     * would be to have this class use getter methods instead of
     * public fields.
     */
    class CoreMetadata
    {
    public:
      /// Size type for image dimensions.
      typedef uint32_t dimension_size_type;
      /// Size type for image counts.
      typedef uint32_t image_size_type;
      /// Size type for pixel bit depths.
      typedef uint8_t pixel_size_type;

      /// Width (in pixels) of images in this series.
      dimension_size_type sizeX;

      /// Height (in pixels) of images in this series.
      dimension_size_type sizeY;

      /// Number of Z sections.
      dimension_size_type sizeZ;

      /// Number of channels.
      dimension_size_type sizeC;

      /// Number of timepoints.
      dimension_size_type sizeT;

      /// Width (in pixels) of thumbnail images in this series.
      dimension_size_type thumbSizeX;

      /// Height (in pixels) of thumbnail images in this series. */
      dimension_size_type thumbSizeY;

      /// Number of bytes per pixel.
      ome::xml::model::enums::PixelType pixelType;

      /// Number of valid bits per pixel. */
      pixel_size_type bitsPerPixel;

      /// Total number of images.
      image_size_type imageCount;

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

      /// Are images are stored as RGB (multiple channels per plane)?
      bool rgb;

      /// Is the pixel byte order little endian?
      bool littleEndian;

      /**
       * @c true if channels are stored RGBRGBRGB...; @c false if channels are stored
       * RRR...GGG...BBB...
       */
      bool interleaved;

      /// Are images are stored as indexed color?
      bool indexed;

      /// Can the color map (if present) be ignored? */
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
      image_size_type resolutionCount;

      /// Constructor.
      CoreMetadata();

      /* CoreMetadata(IFormatReader r, int coreIndex) { */
      /*   int currentIndex = r.getCoreIndex(); */
      /*   r.setCoreIndex(coreIndex); */

      /*   sizeX = r.getSizeX(); */
      /*   sizeY = r.getSizeY(); */
      /*   sizeZ = r.getSizeZ(); */
      /*   sizeC = r.getSizeC(); */
      /*   sizeT = r.getSizeT(); */
      /*   thumbSizeX = r.getThumbSizeX(); */
      /*   thumbSizeY = r.getThumbSizeY(); */
      /*   pixelType = r.getPixelType(); */
      /*   bitsPerPixel = r.getBitsPerPixel(); */
      /*   imageCount = r.getImageCount(); */
      /*   dimensionOrder = r.getDimensionOrder(); */
      /*   orderCertain = r.isOrderCertain(); */
      /*   rgb = r.isRGB(); */
      /*   littleEndian = r.isLittleEndian(); */
      /*   interleaved = r.isInterleaved(); */
      /*   indexed = r.isIndexed(); */
      /*   falseColor = r.isFalseColor(); */
      /*   metadataComplete = r.isMetadataComplete(); */
      /*   seriesMetadata = r.getSeriesMetadata(); */
      /*   thumbnail = r.isThumbnailSeries(); */
      /*   resolutionCount = r.getResolutionCount(); */

      /*   r.setCoreIndex(currentIndex); */
      /* } */

      /**
       * Copy constructor.
       *
       * @param copy the CoreMetadata to copy.
       */
      CoreMetadata(const CoreMetadata& copy);
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
                  const CoreMetadata& core);

  }
}

#endif // OME_BIOFORMATS_COREMETADATA_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
