/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2014 - 2015 Open Microscopy Environment:
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

#include <ome/bioformats/in/TIFFReader.h>
#include <ome/bioformats/in/OMETIFFReader.h>

#include <ome/common/xml/Platform.h>
#include <ome/common/xml/dom/Document.h>

#include <ome/xml/Document.h>

#include <ome/xml/meta/MetadataStore.h>
#include <ome/xml/meta/MetadataRetrieve.h>
#include <ome/xml/meta/OMEXMLMetadata.h>
#include <ome/xml/model/primitives/Timestamp.h>

#include <info/ImageInfo.h>

using namespace ome::bioformats;
using ome::bioformats::dimension_size_type;
using ome::xml::model::primitives::Timestamp;

namespace
{

  const char * const stars = "************";

}

namespace info
{

  ImageInfo::ImageInfo (const std::string &file,
                        const options&     opts):
    logger(ome::common::createLogger("ImageInfo")),
    file(file),
    opts(opts),
    reader()
  {
  }

  ImageInfo::~ImageInfo ()
  {
  }

  void
  ImageInfo::setReader(ome::compat::shared_ptr<FormatReader>& reader)
  {
    this->reader = reader;
  }

  void
  ImageInfo::testRead(std::ostream& stream)
  {
    if (!reader)
      reader = ome::compat::make_shared<in::OMETIFFReader>();

    preInit(stream);

    Timestamp t1;
    reader->setId(file);
    Timestamp t2;
    stream << "Reader setup took "
           << static_cast<Timestamp::value_type>(t2) - static_cast<Timestamp::value_type>(t1)
           << "\n\n";

    postInit(stream);
    checkWarnings(stream);

    if (opts.stitch)
      stream << "File pattern = " << file << '\n';
    else
      stream << "Filename = "
             << (reader->getCurrentFile() ? *reader->getCurrentFile() : "null")
             << '\n';

    /// @todo Log mapped filename (if any)

    if (opts.showused)
      readUsedFiles(stream);
    stream << '\n';
    if (opts.showcore)
      readCoreMetadata(stream);
    if (opts.showorig)
      readOriginalMetadata(stream);
    if (opts.showomexml)
      readOMEXMLMetadata(stream);
  }

  void
  ImageInfo::preInit(std::ostream& stream)
  {
    if (opts.showomexml)
      {
        reader->setOriginalMetadataPopulated(opts.showsa);
        ome::compat::shared_ptr<ome::xml::meta::MetadataStore> store(ome::compat::make_shared<ome::xml::meta::OMEXMLMetadata>());
        reader->setMetadataStore(store);
      }

    /// @todo ImageReader format detection.
    ome::compat::shared_ptr<ome::bioformats::detail::FormatReader> detail = ome::compat::dynamic_pointer_cast<ome::bioformats::detail::FormatReader>(reader);
    if (detail)
      stream << "Using reader: " << detail->getFormat()
             << " (" << detail->getFormatDescription() << ")\n";
    else
      stream << "Unknown reader\n";

    if (opts.stitch)
      {
        /// @todo Stitching
        stream << "Stiching not implemented\n";
      }
    /// @todo ChannelFiller
    /// @todo ChannelSeparator
    /// @todo ChannelMerger
    /// @todo MinMaxCalc
    /// @todo BufferedImageReader

    reader->close();
    reader->setMetadataFiltered(opts.filter);
    reader->setGroupFiles(opts.group);
    MetadataOptions mopts(opts.showcore ? MetadataOptions::METADATA_ALL : MetadataOptions::METADATA_MINIMUM);
    reader->setMetadataOptions(mopts);
    reader->setFlattenedResolutions(opts.flat);
  }

  void
  ImageInfo::postInit(std::ostream& /* stream */)
  {
  }

  void
  ImageInfo::checkWarnings(std::ostream& /* stream */)
  {
  }

  void
  ImageInfo::readUsedFiles(std::ostream& stream)
  {
    const std::vector<boost::filesystem::path> used(reader->getUsedFiles());
    if (used.empty())
      {
        stream << "Used files = []";
      }
    else if (used.size() == 1)
      {
        stream << "Used files = [" << used.at(0) << "]\n";
      }
    else
      {
        stream << "Used files\n";
        for (std::vector<boost::filesystem::path>::const_iterator i = used.begin();
             i != used.end();
             ++i)
          {
            stream << '\t' << *i << '\n';
          }
      }
  }

  void
  ImageInfo::readCoreMetadata(std::ostream& stream)
  {
    stream << "Reading core metadata\n";
    dimension_size_type seriesCount(reader->getSeriesCount());
    stream << "Series count = " << seriesCount << '\n' << '\n';

    for (dimension_size_type s = 0; s < seriesCount; ++s)
      {
        reader->setSeries(s);

        /// @todo Get image name from metadata store
        //          std::string imageName(mr ? mr->getImageName(s) : "");
        stream << "Series #" << s << ":\n";
        // << (imageName.empty() ? "" : " -- ")
        // << imageName
        // << ':' << '\n';

        dimension_size_type rc = reader->getResolutionCount();
        if (!opts.flat && rc > 1)
          {
            stream << "\tResolutions = " << rc << '\n';
            for (dimension_size_type r = 0; r < rc; ++r)
              {
                reader->setResolution(r);
                stream << "\t\t#" << r << " = " << reader->getSizeX() << " × " << reader->getSizeY() << '\n';
              }
            reader->setResolution(0);
          }

        stream << "\tImage count = " << reader->getImageCount() << '\n'
               << "\tRGB = [";
        dimension_size_type effC = reader->getEffectiveSizeC();
        for (dimension_size_type c = 0; c < effC; ++c)
          {
            stream << (reader->isRGB(c) ? "true" : "false");
            if (c + 1 != effC)
              stream << ", ";
          }
        stream << "] ([";
        for (dimension_size_type c = 0; c < effC; ++c)
          {
            stream << reader->getRGBChannelCount(c);
            if (c + 1 != effC)
              stream << ", ";
          }
        stream << "]) "
               << (opts.merge ? "merged" : opts.separate ? "separated" : "") << '\n'
               << "\tInterleaved = " << (reader->isInterleaved() ? "true" : "false") << '\n'
               << "\tIndexed = " << (reader->isIndexed() ? "true" : "false") << '\n'
               << "\tWidth = " << reader->getSizeX() << '\n'
               << "\tHeight = " << reader->getSizeY() << '\n';
        printDimension(stream, "SizeZ", reader->getSizeZ(), reader->getSizeZ(), reader->getModuloZ());
        printDimension(stream, "SizeT", reader->getSizeT(), reader->getSizeT(), reader->getModuloT());
        printDimension(stream, "SizeC", reader->getSizeC(), reader->getEffectiveSizeC(), reader->getModuloC());
        stream << "\tThumbnail size = " << reader->getThumbSizeX() << " × " << reader->getThumbSizeY() << '\n'
               << "\tEndianness = " << (reader->isLittleEndian() ? "little" : "big") << '\n'
               << "\tDimensionOrder = " << reader->getDimensionOrder() << " (" << (reader->isOrderCertain() ? "certain" : "not certain") << ")\n"
               << "\tPixelType = " << reader->getPixelType() << '\n'
               << "\tBits per Pixel = " << reader->getBitsPerPixel() << '\n'
               << "\tMetadataComplete = " << (reader->isMetadataComplete() ? "true" : "false") << '\n'
               << "\tThumbnailSeries = " << (reader->isThumbnailSeries() ? "true" : "false") << '\n'
               << '\n';
      }
  }

  void
  ImageInfo::readOriginalMetadata(std::ostream& stream)
  {
    const ome::bioformats::MetadataMap global = reader->getGlobalMetadata().flatten();
    if (global.empty())
      {
        stream << "No global metadata\n\n";
      }
    else
      {
        stream << "Global metadata:\n";
        for (ome::bioformats::MetadataMap::const_iterator i = global.begin();
             i != global.end();
             ++i)
          {
            stream << '\t' << i->first << ": " << i->second << '\n';
          }
        stream << '\n';
      }

    dimension_size_type seriesCount(reader->getSeriesCount());
    for (dimension_size_type s = 0; s < seriesCount; ++s)
      {
        reader->setSeries(s);

        const ome::bioformats::MetadataMap series = reader->getSeriesMetadata().flatten();
        if (!series.empty())
          {
            stream << "Series #" << s << " metadata:\n";
            for (ome::bioformats::MetadataMap::const_iterator i = series.begin();
                 i != series.end();
                 ++i)
              {
                stream << '\t' << i->first << ": " << i->second << '\n';
              }
            stream << '\n';
          }
      }
  }

  void
  ImageInfo::readOMEXMLMetadata(std::ostream& stream)
  {
    try
      {
        ome::compat::shared_ptr<ome::xml::meta::MetadataStore> ms(reader->getMetadataStore());
        ome::compat::shared_ptr<ome::xml::meta::MetadataRetrieve> mr(ome::compat::dynamic_pointer_cast<ome::xml::meta::MetadataRetrieve>(ms));
      }
    catch (const std::exception& e)
      {
        std::cerr << "Failed to get metadata: " << e.what() << '\n';
      }

    ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadata> omemeta(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadata>(reader->getMetadataStore()));
    if (omemeta)
      {
        ome::common::xml::Platform xmlplat;

        std::string omexml;
        bool omexml_dumped = false;
        try
          {
            omexml = omemeta->dumpXML();
            stream << "OME-XML metadata:\n" << omexml << '\n';
            omexml_dumped = true;
          }
        catch (const std::exception& e)
          {
            stream << "Failed to get OME-XML metadata: " << e.what() << '\n';
          }

        if (omexml_dumped && opts.validate)
          {
            try
              {
                ome::common::xml::dom::Document doc(ome::xml::createDocument(omexml));
                stream << "OME-XML validation successful\n";
              }
            catch (const std::exception& e)
              {
                stream << "Failed to validate OME-XML metadata: " << e.what() << '\n';
              }
          }
      }
  }

  void
  ImageInfo::printDimension(std::ostream&                        stream,
                            const std::string&                   dim,
                            ome::bioformats::dimension_size_type size,
                            ome::bioformats::dimension_size_type effectiveSize,
                            const ome::bioformats::Modulo&       modulo)
  {
    stream << '\t' << dim << " = " << size;
    if (effectiveSize)
      stream << " (effectively " << effectiveSize << ')';

    dimension_size_type product = 1;

    if (modulo.size() == 1)
      {
        product = size;
      }
    else
      {
        stream << " (" << size / modulo.size()
               << " " << modulo.parentType
               << " × " << modulo.size()
               << " " << modulo.type
               << ')';
      }
    stream << '\n';

    if (product != size)
      std::cerr << "\t" << stars << ' ' << dim << " dimension mismatch " << stars << '\n';
  }

}
