/*
* #%L
* OME-BIOFORMATS C++ library for image IO.
* Copyright (C) 2015 - 2016 Open Microscopy Environment:
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

#include <iostream>

#include <ome/bioformats/CoreMetadata.h>
#include <ome/bioformats/MetadataTools.h>
#include <ome/bioformats/VariantPixelBuffer.h>
#include <ome/bioformats/out/OMETIFFWriter.h>
#include <ome/xml/meta/OMEXMLMetadata.h>

#include <ome/compat/memory.h>

#include <ome/common/filesystem.h>

using boost::filesystem::path;
using ome::compat::make_shared;
using ome::compat::shared_ptr;
using ome::bioformats::dimension_size_type;
using ome::bioformats::fillMetadata;
using ome::bioformats::CoreMetadata;
using ome::bioformats::DIM_SPATIAL_X;
using ome::bioformats::DIM_SPATIAL_Y;
using ome::bioformats::DIM_CHANNEL;
using ome::bioformats::FormatWriter;
using ome::bioformats::MetadataMap;
using ome::bioformats::out::OMETIFFWriter;
using ome::bioformats::PixelBuffer;
using ome::bioformats::PixelBufferBase;
using ome::bioformats::PixelProperties;
using ome::bioformats::VariantPixelBuffer;
using ome::xml::model::enums::PixelType;
using ome::xml::model::enums::DimensionOrder;

namespace
{

  /* write-example-start */
  shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
  createMetadata()
  {
    // OME-XML metadata store.
    shared_ptr< ::ome::xml::meta::OMEXMLMetadata> meta(make_shared< ::ome::xml::meta::OMEXMLMetadata>());

    // Create simple CoreMetadata and use this to set up the OME-XML
    // metadata.  This is purely for convenience in this example; a
    // real writer would typically set up the OME-XML metadata from an
    // existing MetadataRetrieve instance or by hand.
    std::vector<shared_ptr<CoreMetadata> > seriesList;
    shared_ptr<CoreMetadata> core(make_shared<CoreMetadata>());
    core->sizeX = 512U;
    core->sizeY = 512U;
    core->sizeC.clear(); // defaults to 1 channel with 1 subchannel; clear this
    core->sizeC.push_back(1);
    core->sizeC.push_back(1);
    core->sizeC.push_back(1);
    core->pixelType = ome::xml::model::enums::PixelType::UINT16;
    core->interleaved = false;
    core->bitsPerPixel = 12U;
    core->dimensionOrder = DimensionOrder::XYZTC;
    seriesList.push_back(core);
    seriesList.push_back(core); // add two identical series

    fillMetadata(*meta, seriesList);

    return meta;
  }
  /* write-example-end */

  /* pixel-example-start */
  void
  writePixelData(FormatWriter& writer,
                 std::ostream& stream)
  {
    // Total number of images (series)
    dimension_size_type ic = writer.getMetadataRetrieve()->getImageCount();
    stream << "Image count: " << ic << '\n';

    // Loop over images
    for (dimension_size_type i = 0 ; i < ic; ++i)
      {
        // Change the current series to this index
        writer.setSeries(i);

        // Total number of planes.
        dimension_size_type pc = 1U;
        pc *= writer.getMetadataRetrieve()->getPixelsSizeZ(i);
        pc *= writer.getMetadataRetrieve()->getPixelsSizeT(i);
        pc *= writer.getMetadataRetrieve()->getChannelCount(i);
        stream << "\tPlane count: " << pc << '\n';

        // Loop over planes (for this image index)
        for (dimension_size_type p = 0 ; p < pc; ++p)
          {
            // Change the current plane to this index.
            writer.setPlane(p);

            // Pixel buffer; size 512 × 512 with 3 channels of type
            // uint16_t.  It uses the native endianness and has a
            // storage order of XYZTC without interleaving
            // (subchannels are planar).
            shared_ptr<PixelBuffer<PixelProperties<PixelType::UINT16>::std_type> >
              buffer(make_shared<PixelBuffer<PixelProperties<PixelType::UINT16>::std_type> >
                     (boost::extents[512][512][1][1][1][1][1][1][1],
                      PixelType::UINT16, ome::bioformats::ENDIAN_NATIVE,
                      PixelBufferBase::make_storage_order(DimensionOrder::XYZTC, false)));

            // Fill each subchannel with a different intensity ramp in
            // the 12-bit range.  In a real program, the pixel data
            // would typically be obtained from data acquisition or
            // another image.
            for (dimension_size_type x = 0; x < 512; ++x)
              for (dimension_size_type y = 0; y < 512; ++y)
                {
                  PixelBufferBase::indices_type idx;
                  std::fill(idx.begin(), idx.end(), 0);
                  idx[DIM_SPATIAL_X] = x;
                  idx[DIM_SPATIAL_Y] = y;

                  idx[DIM_CHANNEL] = 0;

                  switch(p)
                    {
                    case 0:
                      buffer->at(idx) = (static_cast<float>(x) / 512.0f) * 4096.0f;
                      break;
                    case 1:
                      buffer->at(idx) = (static_cast<float>(y) / 512.0f) * 4096.0f;
                      break;
                    case 2:
                      buffer->at(idx) = (static_cast<float>(x+y) / 1024.0f) * 4096.0f;
                      break;
                    default:
                      break;
                    }
                }

            VariantPixelBuffer vbuffer(buffer);
            stream << "PixelBuffer PixelType is " << buffer->pixelType() << '\n';
            stream << "VariantPixelBuffer PixelType is " << vbuffer.pixelType() << '\n';
            stream << std::flush;

            // Write the the entire pixel buffer to the plane.
            writer.saveBytes(p, vbuffer);

            stream << "Wrote " << buffer->num_elements() << ' ' << buffer->pixelType() << " pixels\n";
          }
      }
  }
  /* pixel-example-end */

}

int
main(int argc, char *argv[])
{
  try
    {
      if (argc > 1)
        {
          // Portable path
          path filename(argv[1]);

          /* writer-example-start */
          // Create metadata for the file to be written.
          shared_ptr< ::ome::xml::meta::MetadataRetrieve> meta(createMetadata());

          // Create TIFF writer
          shared_ptr<FormatWriter> writer(make_shared<OMETIFFWriter>());

          // Set writer options before opening a file
          writer->setMetadataRetrieve(meta);
          writer->setInterleaved(false);

          // Open the file
          writer->setId(filename);

          // Write pixel data
          writePixelData(*writer, std::cout);

          // Explicitly close writer
          writer->close();
          /* writer-example-end */
        }
      else
        {
          std::cerr << "Usage: " << argv[0] << " ome-xml.ome.tiff\n";
          std::exit(1);
        }
    }
  catch (const std::exception& e)
    {
      std::cerr << "Caught exception: " << e.what() << '\n';
      std::exit(1);
    }
  catch (...)
    {
      std::cerr << "Caught unknown exception\n";
      std::exit(1);
    }
}
