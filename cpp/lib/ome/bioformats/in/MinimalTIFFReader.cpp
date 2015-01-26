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

#include <boost/format.hpp>

#include <ome/bioformats/FormatException.h>
#include <ome/bioformats/FormatTools.h>
#include <ome/bioformats/MetadataTools.h>
#include <ome/bioformats/in/MinimalTIFFReader.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/TIFF.h>

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

        const char *suffixes[] = {"tif", "tiff", "tf2", "tf8", "btf"};
        const char *companion_suffixes_array[] = {"txt", "xml"};

        ReaderProperties
        tiff_properties()
        {
          ReaderProperties p;

          p.name = "MinimalTIFF";
          p.description = "Baseline Tagged Image File Format";
          p.suffixes = std::vector<std::string>(suffixes,
                                                suffixes + (sizeof(suffixes) / sizeof(suffixes[0])));
          p.metadata_levels.insert(MetadataOptions::METADATA_MINIMUM);
          p.metadata_levels.insert(MetadataOptions::METADATA_NO_OVERLAYS);
          p.metadata_levels.insert(MetadataOptions::METADATA_ALL);

          return p;
        }

        const ReaderProperties props(tiff_properties());

        std::vector<std::string> companion_suffixes(companion_suffixes_array,
                                                    companion_suffixes_array + (sizeof(companion_suffixes_array) / sizeof(companion_suffixes_array[0])));

      }

      MinimalTIFFReader::MinimalTIFFReader():
        ::ome::bioformats::detail::FormatReader(props),
        tiff(),
        series_ifd_map()
      {
        domains.push_back(getDomain(GRAPHICS_DOMAIN));
      }

      MinimalTIFFReader::MinimalTIFFReader(const ReaderProperties& readerProperties):
        ::ome::bioformats::detail::FormatReader(readerProperties),
        tiff(),
        series_ifd_map()
      {
        domains.push_back(getDomain(GRAPHICS_DOMAIN));
      }

      MinimalTIFFReader::~MinimalTIFFReader()
      {
      }

      bool
      MinimalTIFFReader::isFilenameThisTypeImpl(const std::string& name) const
      {
        return static_cast<bool>(TIFF::open(name, "r"));
      }

      void
      MinimalTIFFReader::close(bool fileOnly)
      {
        // Drop shared reference to open TIFF.
        tiff.reset();

        ::ome::bioformats::detail::FormatReader::close(fileOnly);
      }

      void
      MinimalTIFFReader::initFile(const std::string& id)
      {
        ::ome::bioformats::detail::FormatReader::initFile(id);

        tiff = TIFF::open(id, "r");

        if (!tiff)
          {
            boost::format fmt("Failed to open ‘%1%’");
            fmt % id;
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

        std::shared_ptr<const tiff::IFD> prev_ifd;
        std::shared_ptr<CoreMetadata> prev_core;

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
                ++(series_ifd_map.back().second);
              }
            else
              {
                prev_core = makeCoreMetadata(**i);
                core.push_back(prev_core);
                series_ifd_map.push_back(std::make_pair(current_ifd, current_ifd + 1));
              }
            prev_ifd = *i;
          }
      }

      void
      MinimalTIFFReader::openBytesImpl(dimension_size_type no,
                                       VariantPixelBuffer& buf,
                                       dimension_size_type x,
                                       dimension_size_type y,
                                       dimension_size_type w,
                                       dimension_size_type h) const
      {
        assertId(currentId, true);

        dimension_size_type series = getSeries();

        if (series >= series_ifd_map.size())
          {
            boost::format fmt("Invalid series number ‘%1%’");
            fmt % series;
            throw FormatException(fmt.str());
          }
        const series_ifd_map_type::value_type range(series_ifd_map.at(series));

        // Compute timepoint and subchannel from plane number.
        dimension_size_type plane = no;
        dimension_size_type S = 0U;
        if (isRGB())
          {
            plane = no / getSizeC();
            S = no % getSizeC();
          }
        dimension_size_type ifdidx = range.first + plane;
        assert(range.first <= plane && plane < range.second);

        if (plane >= (range.second - range.first))
          {
            boost::format fmt("Invalid plane number ‘%1%’ for series ‘%2%’");
            fmt % plane % series;
            throw FormatException(fmt.str());
          }

        const std::shared_ptr<const IFD>& ifd(tiff->getDirectoryByIndex(static_cast<tiff::directory_index_type>(ifdidx)));

        if (isRGB())
          {
            // Copy the desired subchannel into the destination buffer.
            VariantPixelBuffer tmp;
            ifd->readImage(tmp, x, y, w, h);

            detail::CopySubchannelVisitor v(buf, S);
            boost::apply_visitor(v, tmp.vbuffer());
          }
        else
          ifd->readImage(buf, x, y, w, h);
      }

      std::shared_ptr<ome::bioformats::tiff::TIFF>
      MinimalTIFFReader::getTIFF()
      {
        return tiff;
      }

      const std::shared_ptr<ome::bioformats::tiff::TIFF>
      MinimalTIFFReader::getTIFF() const
      {
        return tiff;
      }

    }
  }
}
