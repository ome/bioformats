/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2015 Open Microscopy Environment:
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

#include <ome/bioformats/VariantPixelBuffer.h>
#include <ome/bioformats/in/TIFFReader.h>

#include <ome/compat/memory.h>

#include <ome/common/filesystem.h>

using boost::filesystem::path;
using ome::compat::make_shared;
using ome::compat::shared_ptr;
using ome::bioformats::dimension_size_type;
using ome::bioformats::FormatReader;
using ome::bioformats::MetadataMap;
using ome::bioformats::in::TIFFReader;
using ome::bioformats::VariantPixelBuffer;

namespace
{

  /* read-example-start */
  void
  readMetadata(const FormatReader& reader,
               std::ostream&       stream)
  {
    // Get total number of images (series)
    dimension_size_type ic = reader.getSeriesCount();
    stream << "Image count: " << ic << '\n';

    // Loop over images
    for (dimension_size_type i = 0 ; i < ic; ++i)
      {
        // Change the current series to this index
        reader.setSeries(i);

        // Print image dimensions (for this image index)
        stream << "Dimensions for Image " << i << ':'
               << "\n\tX = " << reader.getSizeX()
               << "\n\tY = " << reader.getSizeY()
               << "\n\tZ = " << reader.getSizeZ()
               << "\n\tT = " << reader.getSizeT()
               << "\n\tC = " << reader.getSizeC()
               << "\n\tEffectiveC = " << reader.getEffectiveSizeC();
        for (dimension_size_type channel = 0;
             channel < reader.getEffectiveSizeC();
             ++channel)
          {
            stream << "\n\tChannel " << channel << ':'
                   << "\n\t\tRGB = " << (reader.isRGB(channel) ? "true" : "false")
                   << "\n\t\tRGBC = " << reader.getRGBChannelCount(channel);
          }
        stream << '\n';

        // Get total number of planes (for this image index)
        dimension_size_type pc = reader.getImageCount();
        stream << "\tPlane count: " << pc << '\n';

        // Loop over planes (for this image index)
        for (dimension_size_type p = 0 ; p < pc; ++p)
          {
            // Print plane position (for this image index and plane
            // index)
            ome::compat::array<dimension_size_type, 3> coords =
              reader.getZCTCoords(p);
            stream << "\tPosition of Plane " << p << ':'
                   << "\n\t\tTheZ = " << coords[0]
                   << "\n\t\tTheT = " << coords[2]
                   << "\n\t\tTheC = " << coords[1]
                   << '\n';
          }
      }
  }
  /* read-example-end */

  /* original-example-start */
  void
  readOriginalMetadata(const FormatReader& reader,
                       std::ostream&       stream)
  {
    // Get total number of images (series)
    dimension_size_type ic = reader.getSeriesCount();
    stream << "Image count: " << ic << '\n';

    // Get global metadata
    const MetadataMap& global = reader.getGlobalMetadata();

    // Print global metadata
    stream << "Global metadata:\n" << global << '\n';

    // Loop over images
    for (dimension_size_type i = 0 ; i < ic; ++i)
      {
        // Change the current series to this index
        reader.setSeries(i);

        // Print series metadata
        const MetadataMap& series = reader.getSeriesMetadata();

        // Print image dimensions (for this image index)
        stream << "Metadata for Image " << i << ":\n"
               << series
               << '\n';
      }
  }
  /* original-example-end */

  /* pixel-example-start */
  void
  readPixelData(const FormatReader& reader,
                std::ostream&       stream)
  {
    // Get total number of images (series)
    dimension_size_type ic = reader.getSeriesCount();
    stream << "Image count: " << ic << '\n';

    // Loop over images
    for (dimension_size_type i = 0 ; i < ic; ++i)
      {
        // Change the current series to this index
        reader.setSeries(i);

        // Get total number of planes (for this image index)
        dimension_size_type pc = reader.getImageCount();
        stream << "\tPlane count: " << pc << '\n';

        // Pixel buffer
        VariantPixelBuffer buf;

        // Loop over planes (for this image index)
        for (dimension_size_type p = 0 ; p < pc; ++p)
          {
            // Read the entire plane into the pixel buffer.
            reader.openBytes(p, buf);

            // If this wasn't an example, we would do something
            // exciting with the pixel data here.
            stream << "Pixel data for Image " << i
                   << " Plane " << p << " contains "
                   << buf.num_elements() << " pixels\n";
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

          /* reader-example-start */
          // Create TIFF reader
          shared_ptr<FormatReader> reader(make_shared<TIFFReader>());

          // Set reader options before opening a file
          reader->setMetadataFiltered(false);
          reader->setGroupFiles(true);

          // Open the file
          reader->setId(filename);

          // Display series core metadata
          readMetadata(*reader, std::cout);

          // Display global and series original metadata
          readOriginalMetadata(*reader, std::cout);

          // Read pixel data
          readPixelData(*reader, std::cout);

          // Explicitly close reader
          reader->close();
          /* reader-example-end */
        }
      else
        {
          std::cerr << "Usage: " << argv[0] << " ome-xml.xml\n";
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
