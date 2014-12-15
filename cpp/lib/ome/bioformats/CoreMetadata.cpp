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

#include <ome/bioformats/CoreMetadata.h>

namespace ome
{
  namespace bioformats
  {

    CoreMetadata::CoreMetadata():
      sizeX(1),
      sizeY(1),
      sizeZ(1),
      sizeC(1),
      sizeT(1),
      thumbSizeX(0),
      thumbSizeY(0),
      pixelType(ome::xml::model::enums::PixelType::UINT8),
      bitsPerPixel(0), // Default to full size of pixelType
      imageCount(1),
      moduloZ("Z"),
      moduloT("T"),
      moduloC("C"),
      dimensionOrder(ome::xml::model::enums::DimensionOrder::XYZTC),
      orderCertain(true),
      rgb(false),
      littleEndian(false),
      interleaved(false),
      indexed(false),
      falseColor(true),
      metadataComplete(true),
      seriesMetadata(),
      thumbnail(false),
      resolutionCount(1)
    {
    }

    CoreMetadata::CoreMetadata(const CoreMetadata &copy):
      sizeX(copy.sizeX),
      sizeY(copy.sizeY),
      sizeZ(copy.sizeZ),
      sizeC(copy.sizeC),
      sizeT(copy.sizeT),
      thumbSizeX(copy.thumbSizeX),
      thumbSizeY(copy.thumbSizeY),
      pixelType(copy.pixelType),
      bitsPerPixel(copy.bitsPerPixel),
      imageCount(copy.imageCount),
      moduloZ(copy.moduloZ),
      moduloT(copy.moduloT),
      moduloC(copy.moduloC),
      dimensionOrder(copy.dimensionOrder),
      orderCertain(copy.orderCertain),
      rgb(copy.rgb),
      littleEndian(copy.littleEndian),
      interleaved(copy.interleaved),
      indexed(copy.indexed),
      falseColor(copy.falseColor),
      metadataComplete(copy.metadataComplete),
      seriesMetadata(copy.seriesMetadata),
      thumbnail(copy.thumbnail),
      resolutionCount(copy.resolutionCount)
    {
    }

  }
}
