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

#include <cassert>

#include <boost/format.hpp>
#include <boost/range/size.hpp>
#include <boost/iostreams/device/file_descriptor.hpp>
#include <boost/iostreams/stream.hpp>
#include <boost/uuid/uuid.hpp>
#include <boost/uuid/uuid_io.hpp>
#include <boost/uuid/uuid_generators.hpp>

#include <ome/bioformats/FormatException.h>
#include <ome/bioformats/FormatTools.h>
#include <ome/bioformats/MetadataTools.h>
#include <ome/bioformats/out/OMETIFFWriter.h>
#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/Util.h>

#include <ome/common/endian.h>
#include <ome/common/filesystem.h>

#include <ome/internal/config.h>

#include <ome/xml/meta/Convert.h>

#include <tiffio.h>

using boost::filesystem::path;

using ome::bioformats::getOMEXML;
using ome::bioformats::detail::WriterProperties;
using ome::bioformats::tiff::TIFF;
using ome::bioformats::tiff::IFD;
using ome::bioformats::tiff::enableBigTIFF;

using ome::common::make_relative;

using ome::xml::model::enums::DimensionOrder;
using ome::xml::model::enums::PixelType;
using ome::xml::meta::convert;
using ome::xml::meta::MetadataRetrieve;
using ome::xml::meta::OMEXMLMetadata;

namespace ome
{
  namespace bioformats
  {
    namespace out
    {

      namespace
      {

        // Note that tf2, tf8 and btf are all extensions for "bigTIFF"
        // (2nd generation TIFF, TIFF with 8-byte offsets and big TIFF
        // respectively).
        const char *suffixes[] = {"ome.tif", "ome.tiff", "ome.tf2", "ome.tf8", "ome.btf"};
        const char *companion_suffixes_array[] = {"companion.ome"};

        WriterProperties
        tiff_properties()
        {
          WriterProperties p("OME-TIFF",
                             "Open Microscopy Environment TIFF");

          p.suffixes = std::vector<boost::filesystem::path>(suffixes,
                                                            suffixes + boost::size(suffixes));


          const PixelType::value_map_type& pv = PixelType::values();
          std::set<ome::xml::model::enums::PixelType> pixeltypes;
          for (PixelType::value_map_type::const_iterator i = pv.begin();
               i != pv.end();
               ++i)
            {
              pixeltypes.insert(i->first);
            }
          p.codec_pixel_types.insert(WriterProperties::codec_pixel_type_map::value_type("default", pixeltypes));

          return p;
        }

        const WriterProperties props(tiff_properties());

        std::vector<path> companion_suffixes(companion_suffixes_array,
                                             companion_suffixes_array + boost::size(companion_suffixes_array));

        const std::string default_description("OME-TIFF");

        /**
         * @todo Move these stream helpers to a proper location,
         * i.e. to replicate the equivalent Java helpers.
         */

        // No switch default to avoid -Wunreachable-code errors.
        // However, this then makes -Wswitch-default complain.  Disable
        // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

        template<typename T,
                 typename B,
                 typename L,
                 typename N>
        T
        read_raw(std::istream&  in,
                 EndianType     endian)
        {
          T ret;

          switch(endian)
            {
            case ENDIAN_BIG:
              {
                B big_val;
                in.read(reinterpret_cast<char *>(&big_val), sizeof(big_val));
                ret = big_val;
                break;
              }
            case ENDIAN_LITTLE:
              {
                L little_val;
                in.read(reinterpret_cast<char *>(&little_val), sizeof(little_val));
                ret = little_val;
                break;
              }
            case ENDIAN_NATIVE:
              {
                N native_val;
                in.read(reinterpret_cast<char *>(&native_val), sizeof(native_val));
                ret = native_val;
                break;
              }
            }

          if (!in)
            throw std::runtime_error("Failed to read value from stream");

          return ret;
        }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

        template<typename T,
                 typename B,
                 typename L,
                 typename N>
        T
        read_raw(std::istream&  in,
                 std::streamoff off,
                 EndianType     endian)
        {
          if (in)
            {
              in.seekg(off, std::ios::beg);
              if (in)
                return read_raw<T, B, L, N>(in, endian);
              else
                throw std::runtime_error("Bad istream offset");
            }
          else
            throw std::runtime_error("Bad istream");
        }

        uint16_t
        read_raw_uint16(std::istream&  in,
                        EndianType     endian)
        {
          return read_raw<uint16_t,
                          boost::endian::big_uint16_t,
                          boost::endian::little_uint16_t,
                          boost::endian::native_uint16_t>(in, endian);
        }

        uint32_t
        read_raw_uint32(std::istream&  in,
                        EndianType     endian)
        {
          return read_raw<uint32_t,
                          boost::endian::big_uint32_t,
                          boost::endian::little_uint32_t,
                          boost::endian::native_uint32_t>(in, endian);
        }

        uint64_t
        read_raw_uint64(std::istream&  in,
                        EndianType     endian)
        {
          return read_raw<uint64_t,
                          boost::endian::big_uint64_t,
                          boost::endian::little_uint64_t,
                          boost::endian::native_uint64_t>(in, endian);
        }

        uint16_t
        read_raw_uint16(std::istream&  in,
                        std::streamoff off,
                        EndianType     endian)
        {
          return read_raw<uint16_t,
                          boost::endian::big_uint16_t,
                          boost::endian::little_uint16_t,
                          boost::endian::native_uint16_t>(in, off, endian);
        }

        uint32_t
        read_raw_uint32(std::istream&  in,
                        std::streamoff off,
                        EndianType     endian)
        {
          return read_raw<uint32_t,
                          boost::endian::big_uint32_t,
                          boost::endian::little_uint32_t,
                          boost::endian::native_uint32_t>(in, off, endian);
        }

        uint64_t
        read_raw_uint64(std::istream&  in,
                        std::streamoff off,
                        EndianType     endian)
        {
          return read_raw<uint64_t,
                          boost::endian::big_uint64_t,
                          boost::endian::little_uint64_t,
                          boost::endian::native_uint64_t>(in, off, endian);
        }

        // No switch default to avoid -Wunreachable-code errors.
        // However, this then makes -Wswitch-default complain.  Disable
        // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

        template<typename T,
                 typename B,
                 typename L,
                 typename N>
        void
        write_raw(std::ostream& in,
                  EndianType    endian,
                  const T&      value)
        {
          switch(endian)
            {
            case ENDIAN_BIG:
              {
                B big_val(value);
                in.write(reinterpret_cast<char *>(&big_val), sizeof(big_val));
                break;
              }
            case ENDIAN_LITTLE:
              {
                L little_val(value);
                in.write(reinterpret_cast<char *>(&little_val), sizeof(little_val));
                break;
              }
            case ENDIAN_NATIVE:
              {
                N native_val(value);
                in.write(reinterpret_cast<char *>(&native_val), sizeof(native_val));
                break;
              }
            }

          if (!in)
            throw std::runtime_error("Failed to write value to stream");
        }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

        template<typename T,
                 typename B,
                 typename L,
                 typename N>
        void
        write_raw(std::ostream&  in,
                  std::streamoff off,
                  EndianType     endian,
                  const T&       value)
        {
          if (in)
            {
              in.seekp(off, std::ios::beg);
              if (in)
                write_raw<T, B, L, N>(in, endian, value);
              else
                throw std::runtime_error("Bad ostream offset");
            }
          else
            throw std::runtime_error("Bad ostream");
        }

        void
        write_raw_uint16(std::ostream& in,
                         EndianType    endian,
                         uint16_t      value)
        {
          return write_raw<uint16_t,
                           boost::endian::big_uint16_t,
                           boost::endian::little_uint16_t,
                           boost::endian::native_uint16_t>(in, endian, value);
        }

        void
        write_raw_uint32(std::ostream& in,
                         EndianType    endian,
                         uint32_t      value)
        {
          return write_raw<uint32_t,
                           boost::endian::big_uint32_t,
                           boost::endian::little_uint32_t,
                           boost::endian::native_uint32_t>(in, endian, value);
        }

        void
        write_raw_uint64(std::ostream& in,
                         EndianType    endian,
                         uint64_t      value)
        {
          return write_raw<uint64_t,
                           boost::endian::big_uint64_t,
                           boost::endian::little_uint64_t,
                           boost::endian::native_uint64_t>(in, endian, value);
        }

        void
        write_raw_uint16(std::ostream&  in,
                         std::streamoff off,
                         EndianType     endian,
                         uint16_t       value)
        {
          write_raw<uint16_t,
                    boost::endian::big_uint16_t,
                    boost::endian::little_uint16_t,
                    boost::endian::native_uint16_t>(in, off, endian, value);
        }

        void
        write_raw_uint32(std::ostream&  in,
                         std::streamoff off,
                         EndianType     endian,
                         uint32_t       value)
        {
          write_raw<uint32_t,
                    boost::endian::big_uint32_t,
                    boost::endian::little_uint32_t,
                    boost::endian::native_uint32_t>(in, off, endian, value);
        }

        void
        write_raw_uint64(std::ostream&  in,
                         std::streamoff off,
                         EndianType     endian,
                         uint64_t       value)
        {
          write_raw<uint64_t,
                    boost::endian::big_uint64_t,
                    boost::endian::little_uint64_t,
                    boost::endian::native_uint64_t>(in, off, endian, value);
        }

      }

      OMETIFFWriter::TIFFState::TIFFState(ome::compat::shared_ptr<ome::bioformats::tiff::TIFF>& tiff):
        uuid(boost::uuids::to_string(boost::uuids::random_generator()())),
        tiff(tiff),
        ifdCount(0U)
      {
      }

      OMETIFFWriter::TIFFState::~TIFFState()
      {
      }

      OMETIFFWriter::OMETIFFWriter():
        ome::bioformats::detail::FormatWriter(props),
        logger(ome::common::createLogger("OMETIFFWriter")),
        files(),
        tiffs(),
        currentTIFF(tiffs.end()),
        flags(),
        seriesState(),
        originalMetadataRetrieve(),
        omeMeta(),
        bigTIFF(boost::none)
      {
      }

      OMETIFFWriter::~OMETIFFWriter()
      {
        try
          {
            close();
          }
        catch (...)
          {
          }
      }

      void
      OMETIFFWriter::setId(const boost::filesystem::path& id)
      {
        // Attempt to canonicalize the path.
        path canonicalpath = id;
        try
          {
            canonicalpath = ome::common::canonical(id);
          }
        catch (const std::exception&)
          {
          }

        if (currentId && *currentId == canonicalpath)
          return;

        if (seriesState.empty()) // First call to setId.
          {
            baseDir = (canonicalpath.parent_path());

            // Create OME-XML metadata.
            originalMetadataRetrieve = metadataRetrieve;
            omeMeta = ome::compat::make_shared<OMEXMLMetadata>();
            convert(*metadataRetrieve, *omeMeta);
            omeMeta->resolveReferences();
            metadataRetrieve = omeMeta;

            // Try to fix up OME-XML metadata if inconsistent.
            if (!validateModel(*omeMeta, false))
              {
                validateModel(*omeMeta, true);
                if (validateModel(*omeMeta, false))
                  {
                    BOOST_LOG_SEV(logger, ome::logging::trivial::warning)
                      << "Correction of model SizeC/ChannelCount/SamplesPerPixel inconsistency attempted";
                  }
                else
                  {
                    BOOST_LOG_SEV(logger, ome::logging::trivial::error)
                      << "Correction of model SizeC/ChannelCount/SamplesPerPixel inconsistency attempted (but inconsistencies remain)";
                  }
              }

            // Set up initial TIFF plane state for all planes in each series.
            dimension_size_type seriesCount = metadataRetrieve->getImageCount();
            seriesState.resize(seriesCount);
            for (dimension_size_type series = 0U; series < seriesCount; ++series)
              {
                dimension_size_type sizeZ = metadataRetrieve->getPixelsSizeZ(series);
                dimension_size_type sizeT = metadataRetrieve->getPixelsSizeT(series);
                dimension_size_type effC = metadataRetrieve->getChannelCount(series);
                dimension_size_type planeCount = sizeZ * sizeT * effC;

                SeriesState& seriesMeta(seriesState.at(series));
                seriesMeta.planes.resize(planeCount);

                for (dimension_size_type plane = 0U; plane < planeCount; ++plane)
                  {
                    detail::OMETIFFPlane& planeMeta(seriesMeta.planes.at(plane));
                    planeMeta.certain = true;
                    planeMeta.status = detail::OMETIFFPlane::ABSENT; // Not written yet.
                  }
              }
          }

        if (flags.empty())
          {
            flags += 'w';

            // Get expected size of pixel data.
            ome::compat::shared_ptr<const ::ome::xml::meta::MetadataRetrieve> mr(getMetadataRetrieve());
            storage_size_type pixelSize = significantPixelSize(*mr);

            if (enableBigTIFF(bigTIFF, pixelSize, canonicalpath, logger))
              flags += '8';
          }

        tiff_map::iterator i = tiffs.find(canonicalpath);
        if (i == tiffs.end())
          {
            detail::FormatWriter::setId(canonicalpath);
            ome::compat::shared_ptr<ome::bioformats::tiff::TIFF> tiff(ome::bioformats::tiff::TIFF::open(canonicalpath, flags));
            std::pair<tiff_map::iterator,bool> result =
              tiffs.insert(tiff_map::value_type(*currentId, TIFFState(tiff)));
            if (result.second) // should always be true
              currentTIFF = result.first;
            detail::FormatWriter::setId(id);
            setupIFD();
          }
        else
          {
            detail::FormatWriter::setId(i->first);
            currentTIFF = i;
          }
      }

      void
      OMETIFFWriter::close(bool fileOnly)
      {
        if (currentId)
          {
            // Flush last IFD.
            nextIFD();

            // Remove any BinData elements.
            removeBinData(*omeMeta);
            // Create UUID and TiffData elements for each series.
            fillMetadata();

            for (tiff_map::const_iterator t = tiffs.begin();
                 t != tiffs.end();
                 ++t)
              {
                // Get OME-XML for this TIFF file.
                std::string xml = getOMEXML(t->first);
                // Make sure file is closed before we modify it outside libtiff.
                t->second.tiff->close();

                // Save OME-XML in the TIFF.
                saveComment(t->first, xml);
              }
          }

        // Close any open TIFFs.
        for (tiff_map::const_iterator t = tiffs.begin();
             t != tiffs.end();
             ++t)
          t->second.tiff->close();
        if (!fileOnly)
          {
            files.clear();
            tiffs.clear();
            currentTIFF = tiffs.end();
            flags.clear();
            seriesState.clear();
            originalMetadataRetrieve.reset();
            omeMeta.reset();
            bigTIFF = boost::none;
          }

        ome::bioformats::detail::FormatWriter::close(fileOnly);
      }

      void
      OMETIFFWriter::setSeries(dimension_size_type series) const
      {
        const dimension_size_type currentSeries = getSeries();
        detail::FormatWriter::setSeries(series);

        if (currentSeries != series)
          {
            nextIFD();
            setupIFD();
          }
      }

      void
      OMETIFFWriter::setPlane(dimension_size_type plane) const
      {
        const dimension_size_type currentPlane = getPlane();
        detail::FormatWriter::setPlane(plane);

        if (currentPlane != plane)
          {
            nextIFD();
            setupIFD();
          }
      }

      void
      OMETIFFWriter::nextIFD() const
      {
        currentTIFF->second.tiff->writeCurrentDirectory();
        ++currentTIFF->second.ifdCount;
      }

      void
      OMETIFFWriter::setupIFD() const
      {
        // Get current IFD.
        ome::compat::shared_ptr<tiff::IFD> ifd (currentTIFF->second.tiff->getCurrentDirectory());

        // Default to single strips for now.
        ifd->setImageWidth(getSizeX());
        ifd->setImageHeight(getSizeY());

        ifd->setTileType(tiff::STRIP);
        ifd->setTileWidth(getSizeX());
        ifd->setTileHeight(1U);

        ome::compat::array<dimension_size_type, 3> coords = getZCTCoords(getPlane());

        dimension_size_type channel = coords[1];

        ifd->setPixelType(getPixelType());
        ifd->setBitsPerSample(bitsPerPixel(getPixelType()));
        ifd->setSamplesPerPixel(getRGBChannelCount(channel));

        const boost::optional<bool> interleaved(getInterleaved());
        if (isRGB(channel) && interleaved && *interleaved)
          ifd->setPlanarConfiguration(tiff::CONTIG);
        else
          ifd->setPlanarConfiguration(tiff::SEPARATE);

        // This isn't necessarily always true; we might want to use a
        // photometric interpretation other than RGB with three
        // subchannels.
        if (isRGB(channel) && getRGBChannelCount(channel) == 3)
          ifd->setPhotometricInterpretation(tiff::RGB);
        else
          ifd->setPhotometricInterpretation(tiff::MIN_IS_BLACK);

        if (currentTIFF->second.ifdCount == 0)
          ifd->getField(ome::bioformats::tiff::IMAGEDESCRIPTION).set(default_description);
      }

      void
      OMETIFFWriter::saveBytes(dimension_size_type plane,
                               VariantPixelBuffer& buf,
                               dimension_size_type x,
                               dimension_size_type y,
                               dimension_size_type w,
                               dimension_size_type h)
      {
        assertId(currentId, true);

        setPlane(plane);

        // Get current IFD.
        ome::compat::shared_ptr<tiff::IFD> ifd (currentTIFF->second.tiff->getCurrentDirectory());

        // Get plane metadata.
        detail::OMETIFFPlane& planeMeta(seriesState.at(getSeries()).planes.at(plane));

        ifd->writeImage(buf, x, y, w, h);

        // Set plane metadata.
        planeMeta.id = currentTIFF->first;
        planeMeta.ifd = currentTIFF->second.ifdCount;
        planeMeta.certain = true;
        planeMeta.status = detail::OMETIFFPlane::PRESENT; // Plane now written.
      }

      void
      OMETIFFWriter::fillMetadata()
      {
        if (!omeMeta)
          throw std::logic_error("OMEXMLMetadata null");

        dimension_size_type badPlanes = 0U;
        for (series_list::const_iterator series = seriesState.begin();
             series != seriesState.end();
             ++series)
          {
            for (std::vector<detail::OMETIFFPlane>::const_iterator plane = series->planes.begin();
                 plane != series->planes.end();
                 ++plane)
              {
                if (plane->status != detail::OMETIFFPlane::PRESENT) // Plane not written.
                  ++badPlanes;
              }
          }
        if (badPlanes)
          {
            boost::format fmt
              ("Inconsistent writer state: %1% planes have not been written");
            fmt % badPlanes;
            throw FormatException(fmt.str());
          }

        dimension_size_type seriesCount = getSeriesCount();

        dimension_size_type nextPlane = 0U;
        for (dimension_size_type series = 0U; series < seriesCount; ++series)
          {
            DimensionOrder dimOrder = metadataRetrieve->getPixelsDimensionOrder(series);
            dimension_size_type sizeZ = metadataRetrieve->getPixelsSizeZ(series);
            dimension_size_type sizeT = metadataRetrieve->getPixelsSizeT(series);
            dimension_size_type effC = metadataRetrieve->getChannelCount(series);
            dimension_size_type imageCount = sizeZ * sizeT * effC;

            if (imageCount == 0)
              {
                omeMeta->setTiffDataPlaneCount(0, series, 0);
              }

            for (dimension_size_type plane = 0U; plane < imageCount; ++plane)
              {
                ome::compat::array<dimension_size_type, 3> coords =
                  ome::bioformats::getZCTCoords(dimOrder, sizeZ, effC, sizeT, imageCount, plane);
                const detail::OMETIFFPlane& planeState(seriesState.at(series).planes.at(plane));

                tiff_map::const_iterator t = tiffs.find(planeState.id);
                if (t != tiffs.end())
                  {
                    path relative(make_relative(baseDir, planeState.id));
                    std::string uuid("urn:uuid:");
                    uuid += t->second.uuid;
                    omeMeta->setUUIDFileName(relative.generic_string(), series, nextPlane);
                    omeMeta->setUUIDValue(uuid, series, nextPlane);

                    // Fill in non-default TiffData attributes.
                    omeMeta->setTiffDataFirstZ(coords[0], series, plane);
                    omeMeta->setTiffDataFirstT(coords[2], series, plane);
                    omeMeta->setTiffDataFirstC(coords[1], series, plane);
                    omeMeta->setTiffDataIFD(planeState.ifd, series, plane);
                    omeMeta->setTiffDataPlaneCount(1, series, plane);

                    // The Java writer updates the TIFF IFD count
                    // here, but not sure it's appropriate for us.
                    ++nextPlane;
                  }
                else
                  {
                    boost::format fmt
                      ("Inconsistent writer state: TIFF file %1% not registered with a UUID");
                    fmt % planeState.id;
                    throw FormatException(fmt.str());
                  }
              }
          }
      }

      std::string
      OMETIFFWriter::getOMEXML(const boost::filesystem::path& id)
      {
        tiff_map::const_iterator t = tiffs.find(id);

        if (t == tiffs.end())
          {
            boost::format fmt
              ("Inconsistent writer state: TIFF file %1% not registered with a UUID");
            fmt % id;
            throw FormatException(fmt.str());
          }

        path relative(make_relative(baseDir, id));
        std::string uuid("urn:uuid:");
        uuid += t->second.uuid;
        omeMeta->setUUID(uuid);

        return bioformats::getOMEXML(*omeMeta, true);
      }

      void
      OMETIFFWriter::saveComment(const boost::filesystem::path& id,
                                 const std::string&             xml)
      {
        // Open TIFF as a raw stream.
        boost::iostreams::stream<boost::iostreams::file_descriptor> in(id);
        in.imbue(std::locale::classic());

        // Check endianness.
        EndianType endian = ENDIAN_NATIVE;
        char endianchars[2];
        in >> endianchars[0] >> endianchars[1];

        if (endianchars[0] == 'I' && endianchars[1] == 'I')
          endian = ENDIAN_LITTLE;
        else if (endianchars[0] == 'M' && endianchars[1] == 'M')
          endian = ENDIAN_BIG;
        else
          {
            boost::format fmt
              ("%1% is not a valid TIFF file: Invalid endian header \"%2%%3%\"");
            fmt % id % endianchars[0] % endianchars[1];
            throw FormatException(fmt.str());
          }

        // Check version.
        uint16_t version = read_raw_uint16(in, endian);

        bool bigOffsets;
        if (version == 0x2A)
          bigOffsets = false;
        else if (version == 0x2B)
          bigOffsets = true;
        else
          {
            boost::format fmt
              ("%1% is not a valid TIFF file: Invalid version %2%");
            fmt % id % version;
            throw FormatException(fmt.str());
          }

        // Check offset size and bail out if unusual.
        uint16_t offsetSize = bigOffsets ? read_raw_uint16(in, endian) : 4U;
        if (offsetSize != 4U && offsetSize != 8U)
          {
            boost::format fmt
              ("%1% uses a nonstandard offset size of %2% bytes");
            fmt % id % offsetSize;
            throw FormatException(fmt.str());
          }

        // Get offset of IFD 0 for later use.
        uint64_t ifd0Offset = bigOffsets ? read_raw_uint64(in, 8, endian) : read_raw_uint32(in, 4, endian);

        // Append XML text with a NUL terminator at end of file, noting the offset.
        in.seekp(0, std::ios::end);
        uint64_t descOffset = in.tellp();
        in << xml << '\0';

        // Get number of directory entries for IFD 0.
        uint64_t entries = bigOffsets ? read_raw_uint64(in, ifd0Offset, endian) : read_raw_uint16(in, ifd0Offset, endian);

        // Has ImageDescription been found?
        bool found = false;
        // Loop over directory entries to find ImageDescription.
        for (uint64_t i = 0; i < entries; ++i)
          {
            const uint64_t tagOff = bigOffsets ? ifd0Offset + 8 + (i * 20) : ifd0Offset + 2 + (i * 12);
            const uint16_t tagid = read_raw_uint16(in, tagOff + 0, endian);
            const uint16_t tagtype = read_raw_uint16(in, tagOff + 2, endian);

            if (tagid != TIFFTAG_IMAGEDESCRIPTION)
              continue;
            found = true;

            if (tagtype != TIFF_ASCII)
            {
              boost::format fmt
                ("Invalid TIFF ImageDescription type %1%");
              fmt % tagtype;
              throw FormatException(fmt.str());
            }

            uint64_t count = bigOffsets ? read_raw_uint64(in, tagOff + 4, endian) : read_raw_uint32(in, tagOff + 4, endian);
            if (count != default_description.size() + 1)
              throw FormatException("TIFF ImageDescription size is incorrect");

            // Overwrite count and offset for the ImageDescription text.
            if (bigOffsets)
              {
                write_raw_uint64(in, tagOff + 4, endian, xml.size() + 1);
                write_raw_uint64(in, tagOff + 12, endian, descOffset);
              }
            else
              {
                write_raw_uint32(in, tagOff + 4, endian, xml.size() + 1);
                write_raw_uint32(in, tagOff + 8, endian, descOffset);
              }
          }

        if (!found)
          throw FormatException("Could not find TIFF ImageDescription tag");
        if (!in)
          throw FormatException("Error writing TIFF ImageDescription tag");

        in.close();
      }

      void
      OMETIFFWriter::setBigTIFF(boost::optional<bool> big)
      {
        bigTIFF = big;
      }

      boost::optional<bool>
      OMETIFFWriter::getBigTIFF() const
      {
        return bigTIFF;
      }

    }
  }
}
