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

#include <cassert>

#include <boost/format.hpp>
#include <boost/range/size.hpp>

#include <ome/bioformats/FormatException.h>
#include <ome/bioformats/FormatTools.h>
#include <ome/bioformats/MetadataTools.h>
#include <ome/bioformats/in/MinimalTIFFReader.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/Util.h>

using ome::bioformats::detail::ReaderProperties;
using ome::bioformats::tiff::TIFF;
using ome::bioformats::tiff::IFD;
using ome::xml::meta::MetadataStore;

namespace ome
{
  namespace bioformats
  {
    namespace in
    {

      namespace
      {

        // Note that tf2, tf8 and btf are all extensions for "bigTIFF"
        // (2nd generation TIFF, TIFF with 8-byte offsets and big TIFF
        // respectively).
        const char *suffixes[] = {"tif", "tiff", "tf2", "tf8", "btf"};
        const char *companion_suffixes_array[] = {"txt", "xml"};

        ReaderProperties
        tiff_properties()
        {
          ReaderProperties p("MinimalTIFF",
                             "Baseline Tagged Image File Format");

          p.suffixes = std::vector<boost::filesystem::path>(suffixes,
                                                            suffixes + boost::size(suffixes));
          p.metadata_levels.insert(MetadataOptions::METADATA_MINIMUM);
          p.metadata_levels.insert(MetadataOptions::METADATA_NO_OVERLAYS);
          p.metadata_levels.insert(MetadataOptions::METADATA_ALL);

          return p;
        }

        const ReaderProperties props(tiff_properties());

        std::vector<std::string> companion_suffixes(companion_suffixes_array,
                                                    companion_suffixes_array + boost::size(companion_suffixes_array));

      }

      MinimalTIFFReader::MinimalTIFFReader():
        ::ome::bioformats::detail::FormatReader(props),
        tiff(),
        seriesIFDRange()
      {
        domains.push_back(getDomain(GRAPHICS_DOMAIN));
      }

      MinimalTIFFReader::MinimalTIFFReader(const ReaderProperties& readerProperties):
        ::ome::bioformats::detail::FormatReader(readerProperties),
        tiff(),
        seriesIFDRange()
      {
        domains.push_back(getDomain(GRAPHICS_DOMAIN));
      }

      MinimalTIFFReader::~MinimalTIFFReader()
      {
        try
          {
            close();
          }
        catch (...)
          {
          }
      }

      bool
      MinimalTIFFReader::isFilenameThisTypeImpl(const boost::filesystem::path& name) const
      {
        return static_cast<bool>(TIFF::open(name, "r"));
      }

      const ome::compat::shared_ptr<const tiff::IFD>
      MinimalTIFFReader::ifdAtIndex(dimension_size_type plane) const
      {
        dimension_size_type ifdidx = tiff::ifdIndex(seriesIFDRange, getSeries(), plane);
        const ome::compat::shared_ptr<const IFD>& ifd(tiff->getDirectoryByIndex(static_cast<tiff::directory_index_type>(ifdidx)));

        return ifd;
      }

      void
      MinimalTIFFReader::close(bool fileOnly)
      {
        // Drop shared reference to open TIFF.
        tiff.reset();

        ::ome::bioformats::detail::FormatReader::close(fileOnly);
      }

      void
      MinimalTIFFReader::initFile(const boost::filesystem::path& id)
      {
        ::ome::bioformats::detail::FormatReader::initFile(id);

        tiff = TIFF::open(id, "r");

        if (!tiff)
          {
            boost::format fmt("Failed to open ‘%1%’");
            fmt % id.string();
            throw FormatException(fmt.str());
          }

        readIFDs();

        fillMetadata(*getMetadataStore(), *this);
      }

      namespace
      {

        // Compare IFDs for equal dimensions, pixel type, photometric
        // interpretation.
        bool
        compare_ifd(const tiff::IFD& lhs,
                    const tiff::IFD& rhs)
        {
          return (lhs.getImageWidth() == rhs.getImageWidth() &&
                  lhs.getImageHeight() == rhs.getImageHeight() &&
                  lhs.getPixelType() == rhs.getPixelType() &&
                  lhs.getSamplesPerPixel() == rhs.getSamplesPerPixel() &&
                  lhs.getPlanarConfiguration() == rhs.getPlanarConfiguration() &&
                  lhs.getPhotometricInterpretation() == rhs.getPhotometricInterpretation());
        }

      }

      void
      MinimalTIFFReader::readIFDs()
      {
        core.clear();

        ome::compat::shared_ptr<const tiff::IFD> prev_ifd;
        ome::compat::shared_ptr<CoreMetadata> prev_core;

        dimension_size_type current_ifd = 0U;

        for (TIFF::const_iterator i = tiff->begin();
             i != tiff->end();
             ++i, ++current_ifd)
          {
            // The minimal TIFF reader makes the assumption that if
            // the pixel data is of the same format as the pixel data
            // in the preceding IFD, then this is a following
            // timepoint in a series.  Otherwise, a new series is
            // started.
            if (prev_core && prev_ifd && compare_ifd(*prev_ifd, **i))
              {
                ++prev_core->sizeT;
                prev_core->imageCount = prev_core->sizeT;
                ++(seriesIFDRange.back().end);
              }
            else
              {
                prev_core = makeCoreMetadata(**i);
                core.push_back(prev_core);

                tiff::IFDRange range;
                range.filename = *currentId;
                range.begin = current_ifd;
                range.end = current_ifd + 1;

                seriesIFDRange.push_back(range);
              }
            prev_ifd = *i;
          }
      }

      void
      MinimalTIFFReader::getLookupTable(dimension_size_type plane,
                                        VariantPixelBuffer& buf) const
      {
        assertId(currentId, true);

        const ome::compat::shared_ptr<const IFD>& ifd(ifdAtIndex(plane));

        try
          {
            ifd->readLookupTable(buf);
          }
        catch (const std::exception& e)
          {
            boost::format fmt("Failed to get lookup table:");
            fmt % e.what();
            throw FormatException(fmt.str());
          }
      }

      void
      MinimalTIFFReader::openBytesImpl(dimension_size_type plane,
                                       VariantPixelBuffer& buf,
                                       dimension_size_type x,
                                       dimension_size_type y,
                                       dimension_size_type w,
                                       dimension_size_type h) const
      {
        assertId(currentId, true);

        const ome::compat::shared_ptr<const IFD>& ifd(ifdAtIndex(plane));

        ifd->readImage(buf, x, y, w, h);
      }

      ome::compat::shared_ptr<ome::bioformats::tiff::TIFF>
      MinimalTIFFReader::getTIFF()
      {
        return tiff;
      }

      const ome::compat::shared_ptr<ome::bioformats::tiff::TIFF>
      MinimalTIFFReader::getTIFF() const
      {
        return tiff;
      }

    }
  }
}
