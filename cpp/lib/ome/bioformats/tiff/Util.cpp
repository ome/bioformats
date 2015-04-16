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

#include <ome/internal/config.h>

#include <ome/bioformats/CoreMetadata.h>
#include <ome/bioformats/FormatException.h>
#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/Util.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      namespace
      {

        // Scalar
        template<typename T>
        void
        setMetadata(CoreMetadata&      core,
                    const std::string& key,
                    const T&           value)
        {
          core.seriesMetadata.set(key, value);
        }

        // Vector
        template <typename T>
        void
        setMetadata(CoreMetadata&         core,
                    const std::string&    key,
                    const std::vector<T>& value)
        {
          std::ostringstream os;
          for (typename std::vector<T>::const_iterator i = value.begin();
               i != value.end();
               ++i)
            {
              os << *i;
              if (i + 1 != value.end())
                os << ", ";
            }
          core.seriesMetadata.set(key, os.str());
        }

        // Array
        template <template <typename, std::size_t> class C,
                  typename T,
                  std::size_t S>
        void
        setMetadata(CoreMetadata&      core,
                    const std::string& key,
                    const C<T, S>&     value)
        {
          std::ostringstream os;
          for (typename C<T, S>::const_iterator i = value.begin();
               i != value.end();
               ++i)
            {
              os << *i;
              if (i + 1 != value.end())
                os << ", ";
            }
          core.seriesMetadata.set(key, os.str());
        }

        template<typename TagCategory>
        bool
        setMetadata(const IFD&         ifd,
                    CoreMetadata&      core,
                    const std::string& key,
                    TagCategory        tag)
        {
          bool set = false;

          typedef typename ::ome::bioformats::detail::tiff::TagProperties<TagCategory>::value_type value_type;

          try
            {
              value_type v;
              ifd.getField(tag).get(v);
              setMetadata(core, key, v);
              set = true;
            }
          catch (...)
            {
            }

          return set;
        }

      }

      ome::compat::shared_ptr<CoreMetadata>
      makeCoreMetadata(const IFD& ifd)
      {
        ome::compat::shared_ptr<CoreMetadata> m(ome::compat::make_shared<CoreMetadata>());
        getCoreMetadata(ifd, *m);
        return m;
      }

      void
      getCoreMetadata(const IFD&    ifd,
                      CoreMetadata& core)
      {
        core.dimensionOrder = ome::xml::model::enums::DimensionOrder::XYCZT;
        core.sizeX = ifd.getImageWidth();
        core.sizeY = ifd.getImageHeight();
        core.pixelType = ifd.getPixelType();

        core.bitsPerPixel = significantBitsPerPixel(core.pixelType);
        pixel_size_type psize = ifd.getBitsPerSample();
        if (psize < core.bitsPerPixel)
          core.bitsPerPixel = psize;

        uint16_t samples = ifd.getSamplesPerPixel();
        tiff::PhotometricInterpretation photometric = ifd.getPhotometricInterpretation();

        // Note that RGB does not mean photometric interpretation is
        // RGB.  It's a way to force the subchannels into sizeC as
        // addressable channels in the absence of an nD API.
        core.rgb = false;
        core.sizeC = 1U;
        if (samples > 1 || photometric == tiff::RGB)
          {
            core.rgb = true;
            core.sizeC = samples;
          }
        core.sizeZ = core.sizeT = core.imageCount = 1U;

        // libtiff does any needed endian conversion
        // automatically, so the data is always in the native
        // byte order.
#ifdef BOOST_BIG_ENDIAN
        core.littleEndian = false;
#else // ! BOOST_BIG_ENDIAN
        core.littleEndian = true;
#endif // BOOST_BIG_ENDIAN

        // This doesn't match the reality, but since subchannels are
        // addressed as planes this is needed.
        core.interleaved = false;

        // Indexed samples.
        if (samples == 1 && photometric == tiff::PALETTE)
          {
            try
              {
                ome::compat::array<std::vector<uint16_t>, 3> cmap;
                ifd.getField(tiff::COLORMAP).get(cmap);
                core.indexed = true;
                core.rgb = false;
              }
            catch (...)
              {
              }
          }
        // Indexed samples for different photometric interpretations;
        // not currently supported fully.
        else
          {
            try
              {
                uint16_t indexed;
                ifd.getField(tiff::INDEXED).get(indexed);
                if (indexed)
                  {
                    core.indexed = true;
                    core.rgb = false;
                  }
              }
            catch (...)
              {
              }
          }

        // Add series metadata from tags.
        setMetadata(ifd, core, "PageName #", PAGENAME);
        setMetadata(ifd, core, "ImageWidth", IMAGEWIDTH);
        setMetadata(ifd, core, "ImageLength", IMAGELENGTH);
        setMetadata(ifd, core, "BitsPerSample", BITSPERSAMPLE);

        /// @todo EXIF IFDs

        setMetadata(ifd, core, "PhotometricInterpretation", PHOTOMETRIC);

        /// @todo Text stream output for Tag enums.
        /// @todo Metadata type for PhotometricInterpretation.

        try
          {
            setMetadata(ifd, core, "Artist", ARTIST);
            Threshholding th;
            ifd.getField(THRESHHOLDING).get(th);
            core.seriesMetadata.set("Threshholding", th);
            if (th == HALFTONE)
              {
                setMetadata(ifd, core, "CellWidth", CELLWIDTH);
                setMetadata(ifd, core, "CellLength", CELLLENGTH);
              }
          }
        catch (...)
          {
          }

        setMetadata(ifd, core, "Orientation", ORIENTATION);

        /// @todo Image orientation (storage order and direction) from
        /// ORIENTATION; fix up width and length from orientation.

        setMetadata(ifd, core, "SamplesPerPixel", SAMPLESPERPIXEL);
        setMetadata(ifd, core, "Software", SOFTWARE);
        setMetadata(ifd, core, "Instrument Make", MAKE);
        setMetadata(ifd, core, "Instrument Model", MODEL);
        setMetadata(ifd, core, "Make", MAKE);
        setMetadata(ifd, core, "Model", MODEL);
        setMetadata(ifd, core, "Document Name", DOCUMENTNAME);
        setMetadata(ifd, core, "Date Time", DATETIME);
        setMetadata(ifd, core, "Artist", ARTIST);

        setMetadata(ifd, core, "Host Computer", HOSTCOMPUTER);
        setMetadata(ifd, core, "Copyright", COPYRIGHT);

        setMetadata(ifd, core, "Subfile Type", SUBFILETYPE);
        setMetadata(ifd, core, "Fill Order", FILLORDER);

        setMetadata(ifd, core, "Min Sample Value", MINSAMPLEVALUE);
        setMetadata(ifd, core, "Max Sample Value", MAXSAMPLEVALUE);

        setMetadata(ifd, core, "XResolution", XRESOLUTION);
        setMetadata(ifd, core, "YResolution", YRESOLUTION);

        setMetadata(ifd, core, "Planar Configuration", PLANARCONFIG);

        setMetadata(ifd, core, "XPosition", XPOSITION);
        setMetadata(ifd, core, "YPosition", YPOSITION);

        /// @todo Only set if debugging/verbose.
        // setMetadata(ifd, core, "FreeOffsets", FREEOFFSETS);
        // setMetadata(ifd, core, "FreeByteCounts", FREEBYTECOUNTS);

        setMetadata(ifd, core, "GrayResponseUnit", GRAYRESPONSEUNIT);
        setMetadata(ifd, core, "GrayResponseCurve", GRAYRESPONSECURVE);

        try
          {
            Compression cmpr;
            ifd.getField(COMPRESSION).get(cmpr);
            core.seriesMetadata.set("Compression", cmpr);
            if (cmpr == COMPRESSION_CCITT_T4)
              setMetadata(ifd, core, "T4Options", T4OPTIONS);
            else if (cmpr == COMPRESSION_CCITT_T6)
              setMetadata(ifd, core, "T6Options", T6OPTIONS);
            else if (cmpr == COMPRESSION_LZW)
              setMetadata(ifd, core, "Predictor", PREDICTOR);
          }
        catch (...)
          {
          }

        setMetadata(ifd, core, "ResolutionUnit", RESOLUTIONUNIT);

        setMetadata(ifd, core, "PageNumber", PAGENUMBER);

        // TransferRange only valid if TransferFunction set.
        if (setMetadata(ifd, core, "TransferFunction", TRANSFERFUNCTION))
          setMetadata(ifd, core, "TransferRange", TRANSFERRANGE);

        setMetadata(ifd, core, "WhitePoint", WHITEPOINT);
        setMetadata(ifd, core, "PrimaryChromacities", PRIMARYCHROMATICITIES);
        setMetadata(ifd, core, "HalftoneHints", HALFTONEHINTS);

        setMetadata(ifd, core, "TileWidth", TILEWIDTH);
        setMetadata(ifd, core, "TileLength", TILELENGTH);

        /// @todo Only set if debugging/verbose.
        // setMetadata(ifd, core, "TileOffsets", TILEOFFSETS);
        // setMetadata(ifd, core, "TileByteCounts", TILEBYTECOUNTS);

        setMetadata(ifd, core, "InkSet", INKSET);
        setMetadata(ifd, core, "InkNames", INKNAMES);
        setMetadata(ifd, core, "NumberOfInks", NUMBEROFINKS);
        setMetadata(ifd, core, "DotRange", DOTRANGE);
        setMetadata(ifd, core, "TargetPrinter", TARGETPRINTER);
        setMetadata(ifd, core, "ExtraSamples", EXTRASAMPLES);

        setMetadata(ifd, core, "SampleFormat", SAMPLEFORMAT);

        /// @todo sminsamplevalue
        /// @todo smaxsamplevalue

        /// @todo Only set if debugging/verbose.
        // setMetadata(ifd, core, "StripOffsets", STRIPOFFSETS);
        // setMetadata(ifd, core, "StripByteCounts", STRIPBYTECOUNTS);

        /// @todo JPEG tags

        setMetadata(ifd, core, "YCbCrCoefficients", YCBCRCOEFFICIENTS);
        setMetadata(ifd, core, "YCbCrSubSampling", YCBCRSUBSAMPLING);
        setMetadata(ifd, core, "YCbCrPositioning", YCBCRPOSITIONING);
        setMetadata(ifd, core, "ReferenceBlackWhite", REFERENCEBLACKWHITE);

        try
          {
            uint16_t samples;
            ifd.getField(SAMPLESPERPIXEL).get(samples);
            PhotometricInterpretation photometric;
            ifd.getField(PHOTOMETRIC).get(photometric);
            if (photometric == RGB ||
                photometric == CFA_ARRAY)
              samples = 3;

            try
              {
                std::vector<ExtraSamples> extra;
                ifd.getField(EXTRASAMPLES).get(extra);
                samples += static_cast<uint16_t>(extra.size());
              }
            catch (...)
              {
              }

            core.seriesMetadata.set("NumberOfChannels", samples);
          }
        catch (...)
          {
          }

        core.seriesMetadata.set("BitsPerSample", bitsPerPixel(ifd.getPixelType()));
      }

      void
      setCoreMetadata(IFD&                ifd,
                      const CoreMetadata& core)
      {
        ifd.setImageWidth(core.sizeX);
        ifd.setImageHeight(core.sizeY);
        ifd.setPixelType(core.pixelType);

        pixel_size_type psize = significantBitsPerPixel(core.pixelType);
        if (core.bitsPerPixel < psize)
          psize = core.bitsPerPixel;
        ifd.setBitsPerSample(psize);

        // Note that RGB does not mean photometric interpretation is
        // RGB.  It's a way to force the subchannels into sizeC as
        // addressable channels in the absence of an nD API.
        uint16_t samples = 1;
        if (core.rgb)
          samples = core.sizeC;

        tiff::PhotometricInterpretation photometric = tiff::MIN_IS_BLACK;
        if (core.rgb && core.sizeC == 3)
          photometric = tiff::RGB;
        else if (!core.rgb && core.indexed && samples == 1)
          photometric = tiff::PALETTE;
        ifd.setPhotometricInterpretation(photometric);

        // libtiff does any needed endian conversion automatically, so
        // the data is always in the native byte order; don't set
        // anything here.

        // @todo Consider adding getMetadata equivalent to
        // setMetadata, to allow setting of Baseline TIFF tags from
        // original metadata, allowing round-trip of Baseline TIFF
        // tags through Bio-Formats.
      }

      dimension_size_type
      ifdIndex(const SeriesIFDRange& seriesIFDRange,
               dimension_size_type   series,
               dimension_size_type   plane,
               dimension_size_type   sizeC,
               bool                  isRGB)
      {
        if (series >= seriesIFDRange.size())
          {
            boost::format fmt("Invalid series number ‘%1%’");
            fmt % series;
            throw FormatException(fmt.str());
          }
        const IFDRange range(seriesIFDRange.at(series));

        // Compute timepoint and subchannel from plane number.
        dimension_size_type realplane = plane;
        if (isRGB)
          {
            realplane = plane / sizeC;
          }
        dimension_size_type ifdidx = range.begin + realplane;
        assert(range.begin <= realplane && realplane < range.end);

        if (realplane >= (range.end - range.begin))
          {
            boost::format fmt("Invalid plane number ‘%1%’ for series ‘%2%’");
            fmt % realplane % series;
            throw FormatException(fmt.str());
          }

        return ifdidx;
      }

    }
  }
}
