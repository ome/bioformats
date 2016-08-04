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

#include <ome/compat/memory.h>

#include <ome/common/filesystem.h>
#include <ome/common/xml/Platform.h>
#include <ome/common/xml/dom/Document.h>

#include <ome/xml/Document.h>
#include <ome/xml/meta/Convert.h>
#include <ome/xml/meta/OMEXMLMetadata.h>

#include <ome/bioformats/FormatTools.h>
#include <ome/bioformats/MetadataTools.h>

using boost::filesystem::path;
using ome::compat::array;
using ome::compat::make_shared;
using ome::compat::shared_ptr;
using ome::bioformats::dimension_size_type;
using ome::bioformats::addMetadataOnly;
using ome::bioformats::createOMEXMLMetadata;
using ome::bioformats::createID;
using ome::bioformats::getZCTCoords;
using ome::bioformats::getOMEXML;
namespace xml = ome::common::xml;
namespace meta = ome::xml::meta;
namespace model = ome::xml::model;
typedef meta::OMEXMLMetadata::index_type index_type;

namespace
{

  shared_ptr<meta::OMEXMLMetadata>
  readMetadata(const path& filename)
  {
    /* read-file-example-start */
    // Create metadata directly from file
    shared_ptr<meta::OMEXMLMetadata> filemeta(createOMEXMLMetadata(filename));
    /* read-file-example-end */

    // Alternatively, create metadata from XML DOM tree:

    /* read-dom-example-start */
    // XML platform (required by Xerces)
    xml::Platform xmlplat;
    // XML DOM tree containing parsed file content
    xml::dom::Document inputdoc(ome::xml::createDocument(filename));
    // Create metadata from DOM document
    shared_ptr<meta::OMEXMLMetadata> dommeta(createOMEXMLMetadata(inputdoc));
    /* read-dom-example-end */

    return filemeta;
  }

  /* query-example-start */
  void
  queryMetadata(const meta::MetadataRetrieve& meta,
                const std::string&            state,
                std::ostream&                 stream)
  {
    // Get total number of images (series)
    index_type ic = meta.getImageCount();
    stream << "Image count: " << ic << '\n';

    // Loop over images
    for (index_type i = 0 ; i < ic; ++i)
      {
        // Print image dimensions (for this image index)
        stream << "Dimensions for Image " << i << ' ' << state << ':'
               << "\n\tX = " << meta.getPixelsSizeX(i)
               << "\n\tY = " << meta.getPixelsSizeY(i)
               << "\n\tZ = " << meta.getPixelsSizeZ(i)
               << "\n\tT = " << meta.getPixelsSizeT(i)
               << "\n\tC = " << meta.getPixelsSizeC(i)
               << '\n';

        // Get total number of planes (for this image index)
        index_type pc = meta.getPlaneCount(i);
        stream << "\tPlane count: " << pc << '\n';

        // Loop over planes (for this image index)
        for (index_type p = 0 ; p < pc; ++p)
          {
            // Print plane position (for this image index and plane
            // index)
            stream << "\tPosition of Plane " << p << ':'
                   << "\n\t\tTheZ = " << meta.getPlaneTheZ(i, p)
                   << "\n\t\tTheT = " << meta.getPlaneTheT(i, p)
                   << "\n\t\tTheC = " << meta.getPlaneTheC(i, p)
                   << '\n';
          }
      }
  }
  /* query-example-end */

  /* update-example-start */
  void
  updateMetadata(meta::Metadata& meta)
  {
    // Get total number of images (series)
    index_type ic = meta.getImageCount();

    // Loop over images
    for (index_type i = 0 ; i < ic; ++i)
      {
        // Change image dimensions (for this image index)
        meta.setPixelsSizeX(12, i);
        meta.setPixelsSizeY(24, i);
        meta.setPixelsSizeZ(6, i);
        meta.setPixelsSizeT(30, i);
        meta.setPixelsSizeC(4, i);
      }
  }
  /* update-example-end */

  /* add-example-start */
  void
  addMetadata(meta::Metadata& meta)
  {
    // Get total number of images (series)
    index_type i = meta.getImageCount();

    // Size of Z, T and C dimensions
    index_type nz = 3;
    index_type nt = 1;
    index_type nc = 4;

    // Create new image; the image index is the same as the image
    // count, i.e. one past the end of the current limit; createID
    // creates a unique identifier for the image
    meta.setImageID(createID("Image", i), i);
    // Set Pixels identifier using createID and the same image index
    meta.setPixelsID(createID("Pixels", i), i);
    // Now set the dimension order, pixel type and dimension sizes for
    // this image, using the same image index
    meta.setPixelsDimensionOrder(model::enums::DimensionOrder::XYZTC, i);
    meta.setPixelsType(model::enums::PixelType::UINT8, i);
    meta.setPixelsSizeX(256, i);
    meta.setPixelsSizeY(256, i);
    meta.setPixelsSizeZ(nz, i);
    meta.setPixelsSizeT(nt, i);
    meta.setPixelsSizeC(nc, i);

    // Plane count
    index_type pc = nz * nc * nt;

    // Loop over planes
    for(index_type p = 0; p < pc; ++p)
      {
        // Get the Z, T and C coordinate for this plane index
        array<dimension_size_type, 3> coord =
          getZCTCoords("XYZTC", nz, nc, nt, pc, p);

        // Set the plane position using the image index and plane
        // index to reference the correct plane
        meta.setPlaneTheZ(coord[0], i, p);
        meta.setPlaneTheT(coord[2], i, p);
        meta.setPlaneTheC(coord[1], i, p);
      }

    // Add MetadataOnly to Pixels since this is an example without
    // TiffData or BinData
    meta::OMEXMLMetadata *omexmlmeta = dynamic_cast<meta::OMEXMLMetadata *>(&meta);
    if (omexmlmeta)
      addMetadataOnly(*omexmlmeta, i);
  }
  /* add-example-end */

  void
  writeMetadata(meta::MetadataRetrieve& meta,
                std::ostream&           stream)
  {
    /* write-example-start */
    meta::OMEXMLMetadata *omexmlmeta = dynamic_cast<meta::OMEXMLMetadata *>(&meta);
    shared_ptr<meta::OMEXMLMetadata> convertmeta;
    if (!omexmlmeta)
      {
        convertmeta = make_shared<meta::OMEXMLMetadata>();
        meta::convert(meta, *convertmeta);
        omexmlmeta = &*convertmeta;
      }
    // Get OME-XML text from metadata store (and validate it)
    std::string omexml(getOMEXML(*omexmlmeta, true));
    /* write-example-end */

    // Dump OME-XML text to stream
    stream << omexml << '\n';
  }

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

          // Read XML file content into OME-XML metadata store
          shared_ptr<meta::OMEXMLMetadata> meta(readMetadata(filename));

          // Get size information from the metadata store
          queryMetadata(*meta, "before update", std::cout);

          // Update the size information and query again
          updateMetadata(*meta);
          queryMetadata(*meta, "after update", std::cout);

          // Add new image and query again
          addMetadata(*meta);
          queryMetadata(*meta, "after image insertion", std::cout);

          // Write XML content from OME-XML metadata store to stream
          writeMetadata(*meta, std::cout);
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
