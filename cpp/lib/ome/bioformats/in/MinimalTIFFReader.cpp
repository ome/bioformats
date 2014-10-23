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
#include <ome/bioformats/in/MinimalTIFFReader.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/TIFF.h>

using ome::bioformats::detail::ReaderProperties;
using ome::bioformats::tiff::TIFF;
using ome::bioformats::tiff::IFD;

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
        ::ome::bioformats::detail::FormatReader(props)
      {
        domains.push_back(getDomain(GRAPHICS));
      }

      MinimalTIFFReader::MinimalTIFFReader(const ReaderProperties& readerProperties):
        ::ome::bioformats::detail::FormatReader(readerProperties)
      {
        domains.push_back(getDomain(GRAPHICS));
      }

      MinimalTIFFReader::~MinimalTIFFReader()
      {
      }

      bool
      MinimalTIFFReader::isFilenameThisTypeImpl(const std::string& name) const
      {
        std::shared_ptr<ome::bioformats::tiff::TIFF> tiff = TIFF::open(name, "r");
        if (tiff)
          return true;
        else
          return false;
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
      }

      void
      MinimalTIFFReader::readIFDs()
      {
        core.clear();

        for (TIFF::const_iterator i = tiff->begin();
             i != tiff->end();
             ++i)
          {
            CoreMetadata m(tiff::makeCoreMetadata(**i));
            core.push_back(std::make_shared<CoreMetadata>(m));
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

        if (no != 0)
          {
            boost::format fmt("Invalid plane number ‘%1%’ in series ‘%2%’");
            fmt % no % series;
            throw FormatException(fmt.str());
          }

        const std::shared_ptr<IFD> ifd = tiff->getDirectoryByIndex(series);

        if (!ifd)
          {
            boost::format fmt("Invalid IFD for plane number ‘%1%’ in series ‘%2%’");
            fmt % no % series;
            throw FormatException(fmt.str());
          }

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
