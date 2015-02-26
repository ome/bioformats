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

#include <algorithm>
#include <iterator>
#include <map>
#include <set>

#include <boost/range/size.hpp>

#include <ome/bioformats/FormatException.h>
#include <ome/bioformats/FormatTools.h>
#include <ome/bioformats/MetadataTools.h>
#include <ome/bioformats/in/OMETIFFReader.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/Field.h>

#include <ome/xml/meta/OMEXMLMetadata.h>
#include <ome/xml/meta/BaseMetadata.h>
#include <ome/xml/meta/Convert.h>

namespace fs = boost::filesystem;
using boost::filesystem::path;
using ome::compat::canonical;

using ome::bioformats::detail::ReaderProperties;
using ome::bioformats::tiff::TIFF;
using ome::bioformats::tiff::IFD;

typedef ome::xml::meta::BaseMetadata::index_type index_type;
using namespace ome::xml::model::primitives;
using namespace ome::xml::model::enums;

namespace
{

  struct get_file : public std::unary_function<std::map<std::string, path>::value_type, path>
  {
    path
    operator() (const std::map<std::string, path>::value_type& value) const
    {
      return value.second;
    }
  };

}

namespace ome
{
  namespace bioformats
  {
    namespace in
    {

      namespace
      {

        const char *suffixes[] = {"ome.tif", "ome.tiff", };
        const char *companion_suffixes_array[] = {"companion.ome"};

        ReaderProperties
        tiff_properties()
        {
          ReaderProperties p("OME-TIFF",
                             "Open Microscopy Environment TIFF");

          p.suffixes = std::vector<boost::filesystem::path>(suffixes,
                                                            suffixes + boost::size(suffixes));
          p.metadata_levels.insert(MetadataOptions::METADATA_MINIMUM);
          p.metadata_levels.insert(MetadataOptions::METADATA_NO_OVERLAYS);
          p.metadata_levels.insert(MetadataOptions::METADATA_ALL);

          return p;
        }

        const ReaderProperties props(tiff_properties());

        std::vector<path> companion_suffixes(companion_suffixes_array,
                                             companion_suffixes_array + boost::size(companion_suffixes_array));

        void
        getComment(const TIFF&  tiff,
                   std::string& omexml)
        {
          try
            {
              ome::compat::shared_ptr<tiff::IFD> ifd (tiff.getDirectoryByIndex(0));
              if (ifd)
                ifd->getField(ome::bioformats::tiff::IMAGEDESCRIPTION).get(omexml);
              else
                throw tiff::Exception("No TIFF IFDs found");
            }
          catch (const tiff::Exception& /* e */)
            {
              throw FormatException("No TIFF ImageDescription found");
            }
        }

        class OMETIFFPlane
        {
        public:
          /// Status of the file associated with this plane.
          enum Status
            {
              UNKNOWN, ///< Not known.
              PRESENT, ///< File exists.
              ABSENT   ///< File is missing.
            };

          /// File containing this plane.
          path id;
          /// IFD index.
          dimension_size_type ifd;
          /// Certainty flag, for dealing with unspecified NumPlanes.
          bool certain;
          /// File status.
          Status status;

          OMETIFFPlane():
            id(),
            ifd(),
            certain(false),
            status(UNKNOWN)
          {
          }

          OMETIFFPlane(const std::string& id):
            id(id),
            ifd(),
            certain(false),
            status(UNKNOWN)
          {
          }
        };

        class OMETIFFMetadata : public CoreMetadata
        {
        public:
          /// Tile width.
          dimension_size_type tileWidth;
          /// Tile width.
          dimension_size_type tileHeight;
          /// Per-plane data.
          std::vector<OMETIFFPlane> tiffPlanes;

          OMETIFFMetadata():
            CoreMetadata(),
            tileWidth(),
            tileHeight(),
            tiffPlanes()
          {}

          OMETIFFMetadata(const OMETIFFMetadata& copy):
            CoreMetadata(copy),
            tileWidth(copy.tileWidth),
            tileHeight(copy.tileHeight),
            tiffPlanes(copy.tiffPlanes)
          {}

        };

      }

      OMETIFFReader::OMETIFFReader():
        detail::FormatReader(props),
        files(),
        tiffs(),
        metadataFile(),
        usedFiles(),
        hasSPW(false)
      {
        this->suffixNecessary = false;
        this->suffixSufficient = false;
        this->domains = getDomainCollection(NON_GRAPHICS_DOMAINS);
        this->companionFiles = true;
        this->datasetDescription = "One or more .ome.tiff files";
      }

      OMETIFFReader::~OMETIFFReader()
      {
      }

      void
      OMETIFFReader::close(bool fileOnly)
      {
        /// @todo

        if (!fileOnly)
          {
            files.clear();
            hasSPW = false;
            usedFiles.clear();
            metadataFile.clear();
          }
        tiffs.clear(); // Closes all open TIFFs.

        detail::FormatReader::close(fileOnly);
      }

      bool
      OMETIFFReader::isSingleFile(const boost::filesystem::path& id) const
      {
        if (checkSuffix(id, companion_suffixes))
          return false;

        ome::compat::shared_ptr<tiff::TIFF> tiff = TIFF::open(id, "r");

        if (!tiff)
          {
            boost::format fmt("Failed to open ‘%1%’");
            fmt % id.native();
            throw FormatException(fmt.str());
          }

        std::string omexml;
        getComment(*tiff, omexml);

        ome::compat::shared_ptr< ::ome::xml::meta::Metadata> meta(createOMEXMLMetadata(omexml));

        dimension_size_type nImages = 0U;
        for (dimension_size_type i = 0U;
             i < meta->getImageCount();
             ++i)
          {
            dimension_size_type nChannels = meta->getChannelCount(i);
            if (!nChannels)
              nChannels = 1;
            ome::xml::model::primitives::PositiveInteger z(meta->getPixelsSizeZ(i));
            ome::xml::model::primitives::PositiveInteger t(meta->getPixelsSizeT(i));

            nImages += static_cast<dimension_size_type>(z) * static_cast<dimension_size_type>(t) * nChannels;
          }

        dimension_size_type nIFD = tiff->directoryCount();

        return nImages > 0 && nImages <= nIFD;
      }

      bool
      OMETIFFReader::isThisType(const boost::filesystem::path& name,
                                bool                           open) const
      {
        if (checkSuffix(name, companion_suffixes))
          return true;

        return detail::FormatReader::isThisType(name, open);
      }

      bool
      OMETIFFReader::isFilenameThisTypeImpl(const boost::filesystem::path& name) const
      {
        ome::compat::shared_ptr<tiff::TIFF> tiff = TIFF::open(name, "r");

        if (!tiff)
          {
            boost::format fmt("Failed to open ‘%1%’");
            fmt % name.native();
            throw FormatException(fmt.str());
          }

        std::string omexml;
        getComment(*tiff, omexml);

        // Basic sanity check before parsing.
        if (omexml.size() == 0 || omexml[0] != '<' || omexml[omexml.size()-1] != '>')
          return false;

        try
          {
            ome::compat::shared_ptr< ::ome::xml::meta::Metadata> meta(createOMEXMLMetadata(omexml));

            std::string metadataFile = meta->getBinaryOnlyMetadataFile();
            if (!metadataFile.empty())
              return true;

            for (::ome::xml::meta::Metadata::index_type i = 0;
                 i < meta->getImageCount();
                 ++i)
              {
                verifyMinimum(*meta, i);
              }
            return meta->getImageCount() > 0;
          }
        catch (const std::exception& /* e */)
          {
            return false;
          }
      }

      const ome::compat::shared_ptr<const tiff::IFD>
      OMETIFFReader::ifdAtIndex(dimension_size_type no) const
      {
        ome::compat::shared_ptr<const IFD> ifd;

        const OMETIFFMetadata& ometa(dynamic_cast<const OMETIFFMetadata&>(getCoreMetadata(getCoreIndex())));

        if (no < ometa.tiffPlanes.size())
          {
            const OMETIFFPlane& plane(ometa.tiffPlanes.at(no));
            const ome::compat::shared_ptr<const TIFF> tiff(getTIFF(plane.id));
            if (tiff)
              ifd = ome::compat::shared_ptr<const IFD>(tiff->getDirectoryByIndex(plane.ifd));
          }

        if (!ifd)
          {
            boost::format fmt("Failed to open IFD ‘%1%’");
            fmt % no;
            throw FormatException(fmt.str());
          }

        return ifd;
      }

      const std::vector<std::string>&
      OMETIFFReader::getDomains() const
      {
        assertId(currentId, true);
        return getDomainCollection(hasSPW ? HCS_ONLY_DOMAINS : NON_GRAPHICS_DOMAINS); 
      }

      const std::vector<boost::filesystem::path>
      OMETIFFReader::getSeriesUsedFiles(bool noPixels) const
      {
        assertId(currentId, true);

        std::set<boost::filesystem::path> fileSet;

        if (!noPixels)
          {
            if (!metadataFile.empty())
              fileSet.insert(metadataFile);

            const OMETIFFMetadata& ometa(dynamic_cast<const OMETIFFMetadata&>(getCoreMetadata(getCoreIndex())));

            for(std::vector<OMETIFFPlane>::const_iterator i = ometa.tiffPlanes.begin();
                i != ometa.tiffPlanes.end();
                ++i)
              {
                if (!i->id.empty())
                  fileSet.insert(i->id);
              }
          }

        return std::vector<boost::filesystem::path>(fileSet.begin(), fileSet.end());
      }

      FormatReader::FileGroupOption
      OMETIFFReader::fileGroupOption(const std::string& id)
      {
        FileGroupOption group = CAN_GROUP;

        try
          {
            if (!isSingleFile(id))
              group = MUST_GROUP;
          }
        catch (const std::exception& /* e */)
          {
          }

        return group;
      }

      dimension_size_type
      OMETIFFReader::getOptimalTileWidth() const
      {
        assertId(currentId, true);

        const OMETIFFMetadata& ometa(dynamic_cast<const OMETIFFMetadata&>(getCoreMetadata(getCoreIndex())));

        return ometa.tileWidth;
      }

      dimension_size_type
      OMETIFFReader::getOptimalTileHeight() const
      {
        assertId(currentId, true);

        const OMETIFFMetadata& ometa(dynamic_cast<const OMETIFFMetadata&>(getCoreMetadata(getCoreIndex())));

        return ometa.tileHeight;
      }

      void
      OMETIFFReader::initFile(const boost::filesystem::path& id)
      {
        detail::FormatReader::initFile(id);
        path dir(id.parent_path());

        if (checkSuffix(id, companion_suffixes))
          {
            // This is a companion file.  Read the metadata, get the
            // TIFF for the TiffData for the first image, and then
            // recurse with this file as the id.
            ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata> meta(createOMEXMLMetadata(id));
            path firstTIFF(path(meta->getUUIDFileName(0, 0)));
            initFile(canonical(firstTIFF, dir));
            return;
          }

        // Cache and use this TIFF.
        addTIFF(id);
        const ome::compat::shared_ptr<const TIFF> tiff(getTIFF(id));

        // Get the OME-XML from the first TIFF, and create OME-XML
        // metadata from it.
        std::string omexml;
        getComment(*tiff, omexml);
        ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata> meta(createOMEXMLMetadata(omexml));

        // Is there an associated binary-only metadata file?
        try
          {
            metadataFile = canonical(path(meta->getBinaryOnlyMetadataFile()), dir);
            if (!metadataFile.empty() && boost::filesystem::exists(metadataFile))
              meta = createOMEXMLMetadata(metadataFile);
          }
        catch (const std::exception& /* e */)
          {
            /// @todo Log.
            metadataFile.clear();
          }

        // Is this a screen/plate?
        try
          {
            this->hasSPW = meta->getPlateCount() > 0U;
          }
        catch (const std::exception& /* e */)
          {
          }

        // Clean up any invalid metadata.
        cleanMetadata(*meta);

        // Retrieve original metadata.
        metadata = getOriginalMetadata(*meta);

        if (!meta->getRoot())
          throw FormatException("Could not parse OME-XML from TIFF ImageDescription");

        // Save image timestamps for later use.
        std::vector<boost::optional<Timestamp> > acquiredDates(meta->getImageCount());
        getAcquisitionDates(*meta, acquiredDates);

        // Get UUID for the first file.
        boost::optional<std::string> currentUUID;
        try
          {
            currentUUID = meta->getUUID();
          }
        catch (const std::exception& /* e */)
          {
            // null UUID.
          }

        // Transfer OME-XML metadata to metadata store for reader.
        convert(*meta, *metadataStore, true);

        // Create CoreMetadata for each image.
        index_type seriesCount = meta->getImageCount();
        core.clear();
        core.reserve(seriesCount);
        for (index_type i = 0; i < seriesCount; ++i)
          core.push_back(ome::compat::make_shared<OMETIFFMetadata>());

        // UUID → file mapping and used files.
        findUsedFiles(*meta, id, dir, currentUUID);

        // Process TiffData elements.
        for (index_type series = 0; series < seriesCount; ++series)
          {
            ome::compat::shared_ptr<OMETIFFMetadata> coreMeta(ome::compat::dynamic_pointer_cast<OMETIFFMetadata>(core.at(series)));
            assert(coreMeta); // Should never be null.
            std::clog << "Image[" << series << "] {\n  id = " << meta->getImageID(series) << '\n';

            DimensionOrder order(meta->getPixelsDimensionOrder(series));

            boost::optional<dimension_size_type> samples;
            if (meta->getChannelCount(series) > 0)
              {
                try
                  {
                    PositiveInteger samplesPerPixel = meta->getChannelSamplesPerPixel(series, 0);
                    samples = samplesPerPixel;
                  }
                catch (const std::exception& /* e */)
                  {
                  }
              }
            dimension_size_type tiffSamples = seriesFileSamplesPerPixel(*meta, series);

            bool adjustedSamples = false;
            if (!samples || *samples != tiffSamples)
              {
                boost::format fmt("SamplesPerPixel mismatch: OME=%1%, TIFF=%2%");
                fmt % (samples ? *samples : 0) % tiffSamples;
                std::clog << fmt.str() << '\n';

                samples = tiffSamples;
                adjustedSamples = true;
              }

            if (adjustedSamples && meta->getChannelCount(series) <= 1)
              adjustedSamples = false;

            PositiveInteger effSizeC = meta->getPixelsSizeC(series);
            if (!adjustedSamples && !(effSizeC % *samples))
              effSizeC /= static_cast<PositiveInteger::value_type>(*samples);

            PositiveInteger sizeT = meta->getPixelsSizeT(series);
            PositiveInteger sizeZ = meta->getPixelsSizeZ(series);
            PositiveInteger num = effSizeC * sizeT * sizeZ;

            coreMeta->tiffPlanes.resize(num);
            index_type tiffDataCount = meta->getTiffDataCount(series);
            boost::optional<NonNegativeInteger> zIndexStart;
            boost::optional<NonNegativeInteger> tIndexStart;
            boost::optional<NonNegativeInteger> cIndexStart;

            seriesIndexStart(*meta, series,
                             zIndexStart, tIndexStart, cIndexStart);

            for (index_type td = 0; td < tiffDataCount; ++td)
              {
                std::clog << "  TiffData[" << td << "] {\n";

                boost::optional<NonNegativeInteger> tdIFD;
                NonNegativeInteger numPlanes = 0;
                NonNegativeInteger firstZ = 0;
                NonNegativeInteger firstT = 0;
                NonNegativeInteger firstC = 0;

                if (!getTiffDataValues(*meta, series, td,
                                       tdIFD, numPlanes,
                                       firstZ, firstT, firstC))
                  break;

                // Note: some writers index FirstC, FirstZ, and FirstT from 1.
                // Subtract index start to correct for this.
                if (cIndexStart && firstC >= *cIndexStart)
                  firstC -= *cIndexStart;
                if (zIndexStart && firstZ >= *zIndexStart)
                  firstZ -= *zIndexStart;
                if (tIndexStart && firstT >= *tIndexStart)
                  firstT -= *tIndexStart;

                if (firstZ >= static_cast<PositiveInteger::value_type>(sizeZ) ||
                    firstC >= static_cast<PositiveInteger::value_type>(effSizeC) ||
                    firstT >= static_cast<PositiveInteger::value_type>(sizeT))
                  {
                    boost::format fmt("Found invalid TiffData: Z=%1%, C=%2%, T=%3%");
                    fmt % firstZ % firstC % firstT;
                    std::clog << fmt.str() << '\n';
                    break;
                  }

                dimension_size_type index = ome::bioformats::getIndex(order,
                                                                      sizeZ, effSizeC, sizeT,
                                                                      num,
                                                                      firstZ, firstC, firstT);

                // get reader object for this filename.
                boost::optional<path> filename;
                boost::optional<std::string> uuid;
                try
                  {
                    filename = path(meta->getUUIDFileName(series, td));
                  }
                catch (const std::exception& e)
                  {
                    std::clog << "Ignoring null UUID object when retrieving filename.\n";
                  }
                try
                  {
                    uuid = meta->getUUIDValue(series, td);
                  }
                catch (const std::exception& e)
                  {
                    std::clog << "Ignoring null UUID object when retrieving value.\n";
                  }

                if (!filename)
                  {
                    if (!uuid)
                      {
                        filename = id;
                      }
                    else
                      {
                        std::map<std::string, path>::const_iterator i(files.find(*uuid));
                        if (i != files.end())
                          filename = i->second;
                      }
                  }
                else
                  {
                    // All the other cases will already have a canonical path.
                    filename = canonical(*filename, dir);
                  }

                addTIFF(*filename);

                bool exists = true;
                if (!fs::exists(*filename))
                  {
                    // If an absolute filename, try using a relative
                    // name.  Old versions of the Java OMETiffWriter
                    // wrote an absolute path to UUID.FileName, which
                    // causes problems if the file is moved to a
                    // different directory.
                    path relative(dir / (*filename).filename());
                    if (fs::exists(relative))
                      {
                        filename = relative;
                      }
                    else
                      {
                        filename = id;
                        exists = usedFiles.size() == 1;
                      }
                  }

                // Fill plane index → IFD mapping
                for (dimension_size_type q = 0;
                     q < static_cast<dimension_size_type>(numPlanes);
                     ++q)
                  {
                    dimension_size_type no = index + q;
                    OMETIFFPlane& plane(coreMeta->tiffPlanes.at(no));
                    plane.id = *filename;
                    plane.ifd = static_cast<dimension_size_type>(*tdIFD) + q;
                    plane.certain = true;
                    plane.status = exists ? OMETIFFPlane::PRESENT : OMETIFFPlane::ABSENT;

                    std::clog << "    Plane[" << no
                              << "]: file=" << plane.id.native()
                              << ", IFD=" << plane.ifd << '\n';
                  }
                if (numPlanes == 0)
                  {
                    // Unknown number of planes (default value); fill down
                    for (dimension_size_type no = index + 1;
                         no < static_cast<dimension_size_type>(num);
                         ++no)
                      {
                        OMETIFFPlane& plane(coreMeta->tiffPlanes.at(no));
                        if (plane.certain)
                          break;
                        OMETIFFPlane& previousPlane(coreMeta->tiffPlanes.at(no - 1));
                        plane.id = *filename;
                        plane.ifd = previousPlane.ifd + 1;
                        plane.status = exists ? OMETIFFPlane::PRESENT : OMETIFFPlane::ABSENT;

                        std::clog << "    Plane[" << no
                                  << "]: FILLED\n";
                      }
                  }
                std::clog << "  }\n";
              }

            // Clear any unset planes.
            for (std::vector<OMETIFFPlane>::iterator plane = coreMeta->tiffPlanes.begin();
                 plane != coreMeta->tiffPlanes.end();
                 ++plane)
              {
                if (plane->status != OMETIFFPlane::UNKNOWN)
                  continue;
                plane->id.clear();
                plane->ifd = 0;

                std::clog << "    Plane[" << plane - coreMeta->tiffPlanes.begin()
                          << "]: CLEARED\n";
              }

            if (!core.at(series))
              continue;

            // Verify all planes are available.
            for (dimension_size_type no = 0;
                 no < static_cast<dimension_size_type>(num);
                 ++no)
              {
                OMETIFFPlane& plane(coreMeta->tiffPlanes.at(no));

                std::clog << "  Verify Plane[" << no
                          << "]: file=" << plane.id.native()
                          << ", IFD=" << plane.ifd << '\n';

                if (plane.id.empty())
                  {
                    std::clog << "Image ID: " << meta->getImageID(series)
                              << " missing plane #" << no << '\n';

                    // Fallback if broken.
                    dimension_size_type nIFD = tiff->directoryCount();

                    coreMeta->tiffPlanes.clear();
                    coreMeta->tiffPlanes.resize(nIFD);
                    for (dimension_size_type p = 0; p < nIFD; ++p)
                      {
                        OMETIFFPlane& plane(coreMeta->tiffPlanes.at(p));
                        plane.id = id;
                        plane.ifd = p;
                      }
                    break;
                  }
              }

            std::clog << "}\n";

            // Fill CoreMetadata.
            try
              {

                for (std::vector<OMETIFFPlane>::iterator i = coreMeta->tiffPlanes.begin();
                     i != coreMeta->tiffPlanes.end();
                     ++i)
                  {
                    // What exactly are we checking for here?  That
                    // it's a valid TIFF?  Why the fallback to currentId?
                  }

                const OMETIFFPlane& plane(coreMeta->tiffPlanes.at(0));
                const ome::compat::shared_ptr<const tiff::TIFF> ptiff(getTIFF(plane.id));
                const ome::compat::shared_ptr<const tiff::IFD> pifd(ptiff->getDirectoryByIndex(plane.ifd));
                const tiff::TileInfo tinfo(pifd->getTileInfo());

                uint32_t tiffWidth = pifd->getImageWidth();
                uint32_t tiffHeight = pifd->getImageHeight();
                ome::xml::model::enums::PixelType tiffPixelType = pifd->getPixelType();
                tiff::PhotometricInterpretation photometric = pifd->getPhotometricInterpretation();

                coreMeta->tileWidth = tinfo.tileWidth();
                coreMeta->tileHeight = tinfo.tileHeight();
                coreMeta->sizeX = meta->getPixelsSizeX(series);
                coreMeta->sizeY = meta->getPixelsSizeY(series);
                coreMeta->sizeZ = meta->getPixelsSizeZ(series);
                coreMeta->sizeT = meta->getPixelsSizeT(series);
                coreMeta->sizeC = meta->getPixelsSizeC(series);
                coreMeta->pixelType = meta->getPixelsType(series);
                coreMeta->imageCount = num;
                coreMeta->dimensionOrder = meta->getPixelsDimensionOrder(series);
                coreMeta->orderCertain = true;
                // libtiff converts to the native endianess transparently
#ifdef BOOST_BIG_ENDIAN
                coreMeta->littleEndian = false;
#else // Little endian
                coreMeta->littleEndian = true;
#endif
                coreMeta->interleaved = false;
                coreMeta->indexed = false;
                if (photometric == tiff::PALETTE)
                  {
                    try
                      {
                        std::array<std::vector<uint16_t>, 3> cmap;
                        pifd->getField(ome::bioformats::tiff::COLORMAP).get(cmap);
                        coreMeta->indexed = true;
                      }
                    catch (const tiff::Exception&)
                      {
                      }
                  }
                coreMeta->metadataComplete = true;
                coreMeta->bitsPerPixel = bitsPerPixel(coreMeta->pixelType);
                try
                  {
                    pixel_size_type bpp =
                      static_cast<pixel_size_type>(meta->getPixelsSignificantBits(series));
                    if (bpp <= coreMeta->bitsPerPixel)
                      {
                        coreMeta->bitsPerPixel = bpp;
                      }
                    else
                      {
                        boost::format fmt("BitsPerPixel out of range: OME=%1%, MAX=%2%");
                        fmt % bpp % coreMeta->bitsPerPixel;
                        std::clog << fmt.str() << '\n';
                      }
                  }
                catch (const std::exception&)
                  {
                  }
                coreMeta->rgb = (*samples > 1) || photometric == tiff::RGB;
                if ((*samples != coreMeta->sizeC &&
                     (*samples % coreMeta->sizeC) != 0U &&
                     (coreMeta->sizeC % *samples) != 0U) ||
                    coreMeta->sizeC == 1U ||
                    adjustedSamples)
                  coreMeta->sizeC *= *samples;

                if (coreMeta->sizeX != tiffWidth)
                  {
                    boost::format fmt("SizeX mismatch: OME=%1%, TIFF=%2%");
                    fmt % coreMeta->sizeX % tiffWidth;
                    std::clog << fmt.str() << '\n';
                  }
                if (coreMeta->sizeY != tiffHeight)
                  {
                    boost::format fmt("SizeY mismatch: OME=%1%, TIFF=%2%");
                    fmt % coreMeta->sizeY % tiffHeight;
                    std::clog << fmt.str() << '\n';
                  }
                if (coreMeta->pixelType != tiffPixelType)
                  {
                    boost::format fmt("PixelType mismatch: OME=%1%, TIFF=%2%");
                    fmt % coreMeta->pixelType % tiffPixelType;
                    std::clog << fmt.str() << '\n';
                  }
                if (meta->getPixelsBinDataCount(series) > 1U)
                  {
                    std::clog << "Ignoring invalid BinData elements in OME-TIFF Pixels element\n";
                  }

                fixOMEROMetadata(*meta, series);
                fixDimensions(series);
              }
            catch (const std::exception& e)
              {
                boost::format fmt("Incomplete Pixels metadata: %1%");
                fmt % e.what();
                throw FormatException(fmt.str());
              }
          }

        for (coremetadata_list_type::iterator i = core.begin();
             i != core.end();
             ++i)
          {
            try
              {
                (*i)->moduloZ = getModuloAlongZ(*meta, std::distance(core.begin(), i));
              }
            catch (const std::exception&)
              {
              }
            try
              {
                (*i)->moduloT = getModuloAlongT(*meta, std::distance(core.begin(), i));
              }
            catch (const std::exception&)
              {
              }
            try
              {
                (*i)->moduloC = getModuloAlongC(*meta, std::distance(core.begin(), i));
              }
            catch (const std::exception&)
              {
              }
          }

        // Remove null CoreMetadata entries.
        std::remove(core.begin(), core.end(), ome::compat::shared_ptr<OMETIFFMetadata>());

        if (getImageCount() == 1U)
          {
            ome::compat::shared_ptr<CoreMetadata>& ms0 = core.at(0);
            ms0->sizeZ = 1U;
            if (!ms0->rgb)
              ms0->sizeC = 1U;
            ms0->sizeT = 1U;
          }

        fillMetadata(*metadataStore, *this, false, false);

        for (std::vector<boost::optional<Timestamp> >::const_iterator ts = acquiredDates.begin();
             ts != acquiredDates.end();
             ++ts)
          {
            index_type series = std::distance<std::vector<boost::optional<Timestamp> >::const_iterator>(acquiredDates.begin(), ts);
            if (*ts)
              {
                try
                  {
                    metadataStore->setImageAcquisitionDate(**ts, series);
                  }
                catch (const std::exception& e)
                  {
                    boost::format fmt("Failed to set Image AcquisitionDate for series %1%: %2%");
                    fmt % series % e.what();
                    std::clog << fmt.str();
                  }
              }
          }

        metadataStore = getMetadataStoreForConversion();
      }

      void
      OMETIFFReader::findUsedFiles(const ome::xml::meta::OMEXMLMetadata& meta,
                                   const boost::filesystem::path&        currentId,
                                   const boost::filesystem::path&        currentDir,
                                   const boost::optional<std::string>&   currentUUID)
      {
        index_type seriesCount = meta.getImageCount();
        for (index_type series = 0; series < seriesCount; ++series)
          {
            index_type tiffDataCount = meta.getTiffDataCount(series);
            for (index_type td = 0; td < tiffDataCount; ++td)
              {
                std::string uuid;
                path filename;
                try
                  {
                    uuid = meta.getUUIDValue(series, td);
                  }
                catch (const std::exception& /* e */)
                  {
                  }
                if (uuid.empty())
                  {
                    // No UUID means that TiffData element refers to this
                    // file.
                    filename = currentId;
                  }
                else
                  {
                    path uuidFilename;
                    try
                      {
                        uuidFilename = meta.getUUIDFileName(series, td);
                      }
                    catch (const std::exception& /* e */)
                      {
                      }
                    if (fs::exists(uuidFilename))
                      {
                        filename = canonical(uuidFilename, currentDir);
                      }
                    else
                      {
                        if (currentUUID && (uuid == *currentUUID || (*currentUUID).empty()))
                          {
                            // UUID references this file
                            filename = currentId;
                          }
                        else
                          {
                            boost::format fmt("Unmatched filename for UUID ‘%1%’");
                            fmt % uuid;
                            throw FormatException(fmt.str());
                          }
                      }
                  }

                std::map<std::string, path>::const_iterator existing = files.find(uuid);
                if (existing == files.end())
                  files.insert(std::make_pair(uuid, filename));
                else if (existing->second != filename)
                  {
                    boost::format fmt("Inconsistent UUID filenames ‘%1%’ and ‘%2%’");
                    fmt % existing->second.native() % filename.native();
                    throw FormatException(fmt.str());
                  }
              }
          }

        // Build list of used files.
        {
          std::set<path> fileSet;
          std::transform(files.begin(), files.end(),
                         std::inserter(fileSet, fileSet.begin()), get_file());
          usedFiles.assign(fileSet.begin(), fileSet.end());
        }
      }

      void
      OMETIFFReader::getAcquisitionDates(const ome::xml::meta::OMEXMLMetadata&                                  meta,
                                         std::vector<boost::optional<ome::xml::model::primitives::Timestamp> >& timestamps)
      {
        for (index_type i = 0; i < meta.getImageCount(); ++i)
          {
            boost::optional<Timestamp> ts;
            try
              {
                meta.getImageAcquisitionDate(i);
              }
            catch (const std::exception& /* e */)
              {
                // null timestamp.
              }
            timestamps.push_back(ts);
          }
      }

      void
      OMETIFFReader::cleanMetadata(ome::xml::meta::OMEXMLMetadata& meta)
      {
        index_type imageCount = meta.getImageCount();
        for (index_type i = 0; i < imageCount; ++i)
          {
            PositiveInteger sizeC = meta.getPixelsSizeC(i);
            removeChannels(meta, i, sizeC);
          }
      }


      dimension_size_type
      OMETIFFReader::seriesFileSamplesPerPixel(const ome::xml::meta::OMEXMLMetadata&    meta,
                                               ome::xml::meta::BaseMetadata::index_type series)
      {
        index_type tiffDataCount = meta.getTiffDataCount(series);
        index_type td = 0;
        boost::optional<NonNegativeInteger> ifdIndex = 0;
        for (td = 0; td < tiffDataCount; ++td)
          {
            boost::optional<NonNegativeInteger> tdIFD;
            try
              {
                ifdIndex = meta.getTiffDataIFD(series, td);
              }
            catch (const std::exception& e)
              {
              }
            if (ifdIndex)
              break; // Found an IFD.
          }
        if (!ifdIndex)
          {
            // Fallback to use the first IFD
            ifdIndex = 0;
            td = 0;
          }

        std::string uuid;
        try
          {
            uuid = meta.getUUIDValue(series, td);
          }
        catch (const std::exception& /* e */)
          {
          }

        path filename;
        file_map::const_iterator i = files.find(uuid);
        if (i != files.end())
          filename = i->second;
        else
          filename = *currentId;

        const ome::compat::shared_ptr<const tiff::TIFF> tiff(getTIFF(filename));
        const ome::compat::shared_ptr<const tiff::IFD> ifd(tiff->getDirectoryByIndex(*ifdIndex));

        return ifd->getSamplesPerPixel();
      }

      void
      OMETIFFReader::seriesIndexStart(const ome::xml::meta::OMEXMLMetadata&                             meta,
                                      ome::xml::meta::BaseMetadata::index_type                          series,
                                      boost::optional<ome::xml::model::primitives::NonNegativeInteger>& zIndexStart,
                                      boost::optional<ome::xml::model::primitives::NonNegativeInteger>& tIndexStart,
                                      boost::optional<ome::xml::model::primitives::NonNegativeInteger>& cIndexStart)
      {
        // Pre-scan TiffData indices to see if any are indexed from 1.
        index_type tiffDataCount = meta.getTiffDataCount(series);
        for (index_type td = 0; td < tiffDataCount; ++td)
          {
            NonNegativeInteger firstC = 0;
            try
              {
                firstC = meta.getTiffDataFirstC(series, td);
              }
            catch (const std::exception& e)
              {
              }
            if (!cIndexStart)
              cIndexStart = firstC;
            else
              cIndexStart = std::min(*cIndexStart, firstC);

            NonNegativeInteger firstZ = 0;
            try
              {
                firstZ = meta.getTiffDataFirstC(series, td);
              }
            catch (const std::exception& e)
              {
              }
            if (!zIndexStart)
              zIndexStart = firstZ;
            else
              zIndexStart = std::min(*zIndexStart, firstZ);

            NonNegativeInteger firstT = 0;
            try
              {
                firstT = meta.getTiffDataFirstT(series, td);
              }
            catch (const std::exception& e)
              {
              }
            if (!tIndexStart)
              tIndexStart = firstT;
            else
              tIndexStart = std::min(*tIndexStart, firstT);
          }
      }

      bool
      OMETIFFReader::getTiffDataValues(const ome::xml::meta::OMEXMLMetadata&                             meta,
                                       ome::xml::meta::BaseMetadata::index_type                          series,
                                       ome::xml::meta::BaseMetadata::index_type                          tiffData,
                                       boost::optional<ome::xml::model::primitives::NonNegativeInteger>& tdIFD,
                                       ome::xml::model::primitives::NonNegativeInteger&                  numPlanes,
                                       ome::xml::model::primitives::NonNegativeInteger&                  firstZ,
                                       ome::xml::model::primitives::NonNegativeInteger&                  firstT,
                                       ome::xml::model::primitives::NonNegativeInteger&                  firstC)
      {
        bool valid = true;

        try
          {
            tdIFD = meta.getTiffDataIFD(series, tiffData);
          }
        catch (const std::exception& e)
          {
          }

        try
          {
            numPlanes = meta.getTiffDataPlaneCount(series, tiffData);
          }
        catch (const std::exception& e)
          {
            if (tdIFD)
              numPlanes = 1;
          }

        if (numPlanes == 0)
          {
            core.at(series) = ome::compat::shared_ptr<OMETIFFMetadata>();
            valid = false;
          }

        if (!tdIFD)
          tdIFD = 0; // Start at first IFD in file if unspecified.

        try
          {
            firstC = meta.getTiffDataFirstC(series, tiffData);
          }
        catch (const std::exception& e)
          {
          }

        try
          {
            firstT = meta.getTiffDataFirstT(series, tiffData);
          }
        catch (const std::exception& e)
          {
          }

        try
          {
            firstZ = meta.getTiffDataFirstZ(series, tiffData);
          }
        catch (const std::exception& e)
          {
          }

        return valid;
      }

      void
      OMETIFFReader::fixOMEROMetadata(ome::xml::meta::OMEXMLMetadata&          meta,
                                      ome::xml::meta::BaseMetadata::index_type series)
      {
        // Hackish workaround for files exported by OMERO
        // having an incorrect dimension order.
        {
          std::string uuidFileName;
          try
            {
              if (meta.getTiffDataCount(series) > 0)
                uuidFileName = meta.getUUIDFileName(series, 0);
            }
          catch (const std::exception& e)
            {
            }
          if (meta.getChannelCount(series) > 0)
            {
              try
                {
                  // Will throw if null.
                  std::string channelName(meta.getChannelName(series, 0));
                  ome::compat::shared_ptr<CoreMetadata> coreMeta(core.at(series));
                  if (meta.getTiffDataCount(series) > 0 &&
                      files.find("__omero_export") != files.end() &&
                      coreMeta)
                    coreMeta->dimensionOrder = ome::xml::model::enums::DimensionOrder("XYZCT");
                }
              catch (const std::exception& e)
                {
                }
            }
        }
      }

      void
      OMETIFFReader::fixDimensions(ome::xml::meta::BaseMetadata::index_type series)
      {
        ome::compat::shared_ptr<CoreMetadata> coreMeta(core.at(series));
        if (coreMeta)
          {

            if (coreMeta->sizeZ * coreMeta->sizeT * coreMeta->sizeC > coreMeta->imageCount &&
                !coreMeta->rgb)
              {
                if (coreMeta->sizeZ == coreMeta->imageCount)
                  {
                    coreMeta->sizeT = 1U;
                    coreMeta->sizeC = 1U;
                  }
                else if (coreMeta->sizeT == coreMeta->imageCount)
                  {
                    coreMeta->sizeZ = 1U;
                    coreMeta->sizeC = 1U;
                  }
                else if (coreMeta->sizeC == coreMeta->imageCount)
                  {
                    coreMeta->sizeZ = 1U;
                    coreMeta->sizeT = 1U;
                  }
                else
                  {
                    coreMeta->sizeZ = 1U;
                    coreMeta->sizeT = coreMeta->imageCount;
                    coreMeta->sizeC = 1U;
                  }
              }
          }
      }

      void
      OMETIFFReader::getLookupTable(VariantPixelBuffer& buf,
                                    dimension_size_type no) const
      {
        assertId(currentId, true);

        const ome::compat::shared_ptr<const IFD>& ifd(ifdAtIndex(no));

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
      OMETIFFReader::openBytesImpl(dimension_size_type no,
                                   VariantPixelBuffer& buf,
                                   dimension_size_type x,
                                   dimension_size_type y,
                                   dimension_size_type w,
                                   dimension_size_type h) const
      {
        assertId(currentId, true);

        const ome::compat::shared_ptr<const IFD>& ifd(ifdAtIndex(no));

        if (isRGB())
          {
            // Copy the desired subchannel into the destination buffer.
            VariantPixelBuffer tmp;
            ifd->readImage(tmp, x, y, w, h);

            dimension_size_type S = no % getSizeC();
            detail::CopySubchannelVisitor v(buf, S);
            boost::apply_visitor(v, tmp.vbuffer());
          }
        else
          ifd->readImage(buf, x, y, w, h);
      }

      void
      OMETIFFReader::addTIFF(const boost::filesystem::path& tiff)
      {
        tiffs.insert(std::make_pair(tiff, ome::compat::shared_ptr<tiff::TIFF>()));
      }

      const ome::compat::shared_ptr<const ome::bioformats::tiff::TIFF>
      OMETIFFReader::getTIFF(const boost::filesystem::path& tiff) const
      {
        tiff_map::iterator i = tiffs.find(tiff);
        if (!i->second)
          i->second = tiff::TIFF::open(i->first, "r");

        if (!i->second)
          {
            boost::format fmt("Failed to open ‘%1%’");
            fmt % i->first.native();
            throw FormatException(fmt.str());
          }

        return i->second;
      }

      void
      OMETIFFReader::closeTIFF(const boost::filesystem::path& tiff)
      {
        tiff_map::iterator i = tiffs.find(tiff);
        if (i->second)
          {
            i->second->close();
            i->second = ome::compat::shared_ptr<ome::bioformats::tiff::TIFF>();
          }
      }

      ome::compat::shared_ptr<ome::xml::meta::MetadataStore>
      OMETIFFReader::getMetadataStoreForConversion()
      {
        SaveSeries sentry(*this);

        ome::compat::shared_ptr<ome::xml::meta::MetadataStore> store = getMetadataStore();

        if (store)
          {
            for (dimension_size_type i = 0; i < getSeriesCount(); ++i)
              {
                setSeries(i);
                store->setPixelsBinDataBigEndian(!isLittleEndian(), i, 0);
              }
          }

        return store;
      }

      ome::compat::shared_ptr<ome::xml::meta::MetadataStore>
      OMETIFFReader::getMetadataStoreForDisplay()
      {
        ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadata> omexml;
        ome::compat::shared_ptr<ome::xml::meta::MetadataStore> store = getMetadataStore();

        if (store)
          {
            omexml = ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadata>(store);
            if (omexml)
              {
                removeBinData(*omexml);
                for (dimension_size_type i = 0;
                     i < getSeriesCount();
                     ++i)
                  {
                    try
                      {
                        if (omexml->getTiffDataCount(i) == 0)
                          addMetadataOnly(*omexml, i);
                      }
                    catch (const std::exception& e)
                      {
                        boost::format fmt("Failed to add MetadataOnly for series %1%: %2%");
                        fmt % i % e.what();
                        std::clog << fmt.str();
                      }
                  }
              }
          }

        return omexml;
      }

    }
  }
}
