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

#include <ome/bioformats/PixelBuffer.h>

namespace ome
{
  namespace bioformats
  {

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

    PixelBufferBase::storage_order_type
    PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder order,
                                        bool                                   interleaved)
    {
      size_type ordering[dimensions];
      bool ascending[dimensions] = {true, true, true, true, true, true, true, true, true};

      if (interleaved)
        {
          ordering[0] = DIM_SUBCHANNEL;
          ordering[1] = DIM_SPATIAL_X;
          ordering[2] = DIM_SPATIAL_Y;
        }
      else
        {
          ordering[0] = DIM_SPATIAL_X;
          ordering[1] = DIM_SPATIAL_Y;
          ordering[2] = DIM_SUBCHANNEL;
        }

      switch(order)
        {
        case ome::xml::model::enums::DimensionOrder::XYZTC:
          ordering[3] = DIM_MODULO_Z;
          ordering[4] = DIM_SPATIAL_Z;
          ordering[5] = DIM_MODULO_T;
          ordering[6] = DIM_TEMPORAL_T;
          ordering[7] = DIM_MODULO_C;
          ordering[8] = DIM_CHANNEL;
          break;
        case ome::xml::model::enums::DimensionOrder::XYZCT:
          ordering[3] = DIM_MODULO_Z;
          ordering[4] = DIM_SPATIAL_Z;
          ordering[5] = DIM_MODULO_C;
          ordering[6] = DIM_CHANNEL;
          ordering[7] = DIM_MODULO_T;
          ordering[8] = DIM_TEMPORAL_T;
          break;
        case ome::xml::model::enums::DimensionOrder::XYTZC:
          ordering[3] = DIM_MODULO_T;
          ordering[4] = DIM_TEMPORAL_T;
          ordering[5] = DIM_MODULO_Z;
          ordering[6] = DIM_SPATIAL_Z;
          ordering[7] = DIM_MODULO_C;
          ordering[8] = DIM_CHANNEL;
          break;
        case ome::xml::model::enums::DimensionOrder::XYTCZ:
          ordering[3] = DIM_MODULO_T;
          ordering[4] = DIM_TEMPORAL_T;
          ordering[5] = DIM_MODULO_C;
          ordering[6] = DIM_CHANNEL;
          ordering[7] = DIM_MODULO_Z;
          ordering[8] = DIM_SPATIAL_Z;
          break;
        case ome::xml::model::enums::DimensionOrder::XYCZT:
          ordering[3] = DIM_MODULO_C;
          ordering[4] = DIM_CHANNEL;
          ordering[5] = DIM_MODULO_Z;
          ordering[6] = DIM_SPATIAL_Z;
          ordering[7] = DIM_MODULO_T;
          ordering[8] = DIM_TEMPORAL_T;
          break;
        case ome::xml::model::enums::DimensionOrder::XYCTZ:
          ordering[3] = DIM_MODULO_C;
          ordering[4] = DIM_CHANNEL;
          ordering[5] = DIM_MODULO_T;
          ordering[6] = DIM_TEMPORAL_T;
          ordering[7] = DIM_MODULO_Z;
          ordering[8] = DIM_SPATIAL_Z;
          break;
        }

      return storage_order_type(ordering, ascending);
    }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

    PixelBufferBase::storage_order_type
    PixelBufferBase::default_storage_order()
    {
      return make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, true);
    }

  }
}
