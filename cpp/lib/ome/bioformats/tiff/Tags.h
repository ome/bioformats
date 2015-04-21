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

#ifndef OME_BIOFORMATS_TIFF_TAGS_H
#define OME_BIOFORMATS_TIFF_TAGS_H

#include <string>
#include <vector>

#include <ome/bioformats/tiff/Types.h>

#include <ome/compat/memory.h>

#include <ome/compat/array.h>
#include <ome/compat/cstdint.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      class TIFF;

      /// String fields.
      enum StringTag1
        {
          ARTIST,           ///< Person who created the image.
          COPYRIGHT,        ///< Copyright notice.
          DATETIME,         ///< Date and time of image creation.
          DOCUMENTNAME,     ///< Name of the document from which the image originated.
          HOSTCOMPUTER,     ///< Computer and/or operating system creating the image.
          IMAGEDESCRIPTION, ///< Description of the image.
          MAKE,             ///< Scanner manufacturer.
          MODEL,            ///< Scanner model name or number.
          PAGENAME,         ///< Name of the page from which the image originated.
          SOFTWARE,         ///< Name and version of the software creating the image.
          TARGETPRINTER     ///< Description of the printing environment.
        };

      /// String array fields.
      enum StringTagArray1
        {
          INKNAMES ///< Names of each ink used in a separated image.
        };

      /// Unsigned 16-bit integer fields.
      enum UInt16Tag1
        {
          BITSPERSAMPLE,    ///< Number of bits per component.
          CELLWIDTH,        ///< Width of dithering or halftoning matrix for bilevel data.
          CELLLENGTH,       ///< Height of dithering or halftoning matrix for bilevel data.
          CLEANFAXDATA,     ///< How bad scanlines were handled.
          DATATYPE,         ///< Use SAMPLEFORMAT [obsolete].
          GRAYRESPONSEUNIT, ///< Precision of GRAYRESPONSECURVE.
          INDEXED,          ///< Image uses indexed color in any color space.
          INKSET,           ///< Inkset used in a separated image.
          MATTEING,         ///< Use EXTRASAMPLES [obsolete].
          MAXSAMPLEVALUE,   ///< Maximum component value.
          MINSAMPLEVALUE,   ///< Minimum component value.
          NUMBEROFINKS,     ///< Number of inks.
          RESOLUTIONUNIT,   ///< Unit of measurement for XRESOLUTION and YRESOLUTION.
          SAMPLESPERPIXEL   ///< Number of components per pixel.
        };

      /// Unsigned 16-bit integer array fields.
      enum UInt16TagArray1
        {
          GRAYRESPONSECURVE ///< Optical density of greyscale pixel values.
        };

      /// Compression enum fields.
      enum UInt16Compression1
        {
          COMPRESSION ///< Compression scheme in use on the image data.
        };

      /// Fill order enum fields.
      enum UInt16FillOrder1
        {
          FILLORDER ///< Logical order of bits within a byte.
        };

      /// Orientation enum fields.
      enum UInt16Orientation1
        {
          ORIENTATION ///< Image orientation.
        };

      /// Photometric interpretation enum fields.
      enum UInt16PhotometricInterpretation1
        {
          PHOTOMETRIC ///< Photometric interpretation; colour space of image data.
        };

      /// PlanarConfiguration enum fields.
      enum UInt16PlanarConfiguration1
        {
          PLANARCONFIG ///< How components of each pixel are stored.
        };

      /// Predictor enum fields.
      enum UInt16Predictor1
        {
          PREDICTOR ///< Mathematical operation applied before encoding.
        };

      /// Sample format enum fields.
      enum UInt16SampleFormat1
        {
          SAMPLEFORMAT ///< How to interpret each data sample in a pixel.
        };

      /// Threshholding enum fields.
      enum UInt16Threshholding1
        {
          THRESHHOLDING ///< Method used to convert to black and white pixels.
        };

      /// YCbCrPosition enum fields.
      enum UInt16YCbCrPosition1
        {
          YCBCRPOSITIONING ///< Positioning of subsampled chrominance components relative to luminance samples.
        };

      /// Unsigned 16-bit integer (×2) fields.
      enum UInt16Tag2
        {
          DOTRANGE,        ///< Component values corresponding to a 0% dot and 100% dot.
          HALFTONEHINTS,   ///< Range of detail (low-high) for which tonal detail should be retained.
          PAGENUMBER,      ///< Number of the page from which the image originated.
          YCBCRSUBSAMPLING ///< Subsampling factors used for YCbCr chrominance components.
        };

      /// Unsigned 16-bit integer (×6) fields.
      enum UInt16Tag6
        {
          TRANSFERRANGE ///< Expand range of TransferFunction.
        };

      /// Unsigned 16-bit integer array fields.
      enum UInt16ExtraSamplesArray1
        {
          EXTRASAMPLES ///< Description of extra components.
        };

      /// Unsigned 16-bit integer array (×3) fields.
      enum UInt16TagArray3
        {
          COLORMAP,        ///< Colour map for palette colour images.
          TRANSFERFUNCTION ///< Transfer function for the image.
        };

      /// Unsigned 32-bit integer fields.
      enum UInt32Tag1
        {
          BADFAXLINES,            ///< Number of bad scan lines encountered.
          CONSECUTIVEBADFAXLINES, ///< Maximum number of consecutive bad scan lines encountered.
          GROUP3OPTIONS,          ///< Options for Group 3 Fax compression (T4Options).
          GROUP4OPTIONS,          ///< Options for Group 4 Fax compression (T6Options).
          IMAGEDEPTH,             ///< Number of z planes in the image.
          IMAGELENGTH,            ///< Number of rows in the image (pixels per column).
          IMAGEWIDTH,             ///< Number of columns in the image (pixels per row).
          ROWSPERSTRIP,           ///< Number of rows per strip.
          SUBFILETYPE,            ///< Type of data in this subfile [new tag].
          T4OPTIONS,              ///< Options for Group3 fax compression.
          T6OPTIONS,              ///< Options for Group4 fax compression.
          TILEDEPTH,              ///< Tile depth in pixels (z planes).
          TILELENGTH,             ///< Tile height in pixels (rows).
          TILEWIDTH               ///< Tile width in pixels (columns).
        };

      /// Unsigned 32-bit integer array fields.
      enum UInt32TagArray1
        {
          IMAGEJ_META_DATA_BYTE_COUNTS, ///< Private tag for ImageJ metadata byte counts.
          RICHTIFFIPTC                  ///< IPTC (International Press Telecommunications Council) metadata.
        };

      /// Unsigned 64-bit integer fields.
      enum UInt64TagArray1
        {
          FREEOFFSETS,     ///< Offsets of free bytes [unused].
          FREEBYTECOUNTS,  ///< Sizes of free bytes [unused].
          SUBIFD,          ///< Offsets of child IFDs.
          STRIPBYTECOUNTS, ///< Number of bytes in each strip (after compression).
          STRIPOFFSETS,    ///< Byte offset of each strip.
          TILEBYTECOUNTS,  ///< Number of bytes in each tile (after compression).
          TILEOFFSETS      ///< Byte offset of each tile.
        };

      /// Byte (Unsigned 8-bit integer) fields.
      enum RawDataTag1
        {
          ICCPROFILE,      ///< ICC profile data.
          JPEGTABLES,      ///< JPEG quantization and/or Huffman tables (JPEG "abbreviated table specification" datastream).
          PHOTOSHOP,       ///< Photoshop "Image Resource Blocks".
          XMLPACKET,       ///< XMP metadata.
          IMAGEJ_META_DATA ///< Private tag for ImageJ metadata.
        };

      /// Floating point fields.
      enum FloatTag1
        {
          XRESOLUTION, ///< Number of pixels per resolution unit along the image width.
          YRESOLUTION, ///< Number of pixels per resolution unit along the image height.
          XPOSITION,   ///< The x position of the image in RESOLUTIONUNITs.
          YPOSITION    ///< THe y position of the image in RESOLUTIONUNITs.
        };

      /// Floating point (×2) fields.
      enum FloatTag2
        {
          WHITEPOINT ///< Chromaticity of the white point of the image as 1931 CIE (xy).
        };

      /// Floating point (×3) fields.
      enum FloatTag3
        {
          YCBCRCOEFFICIENTS ///< Coefficients describing the RGB to YCbCr transform.
        };

      /// Floating point (×6) fields.
      enum FloatTag6
        {
          PRIMARYCHROMATICITIES, ///< Chromaticities of primary colours as 1931 CIE R(xy) G(xy) B(xy).
          REFERENCEBLACKWHITE    ///< Reference black and white pairs for RGB or YCbCr images.
        };

      /*
       * Tags which are known but not currently wrapped.
       *
       * OSUBFILETYPE,                ///< Type of data in this subfile [old tag].
       * COLORRESPONSEUNIT,           ///<
       * SMINSAMPLEVALUE,             ///<
       * SMAXSAMPLEVALUE,             ///<
       * CLIPPATH,                    ///<
       * XCLIPPATHUNITS,              ///<
       * YCLIPPATHUNITS,              ///<
       * OPIPROXY,                    ///<
       * GLOBALPARAMETERSIFD,         ///<
       * PROFILETYPE,                 ///<
       * FAXPROFILE,                  ///<
       * CODINGMETHODS,               ///<
       * VERSIONYEAR,                 ///<
       * MODENUMBER,                  ///<
       * DECODE,                      ///<
       * IMAGEBASECOLOR,              ///<
       * T82OPTIONS,                  ///<
       * JPEGPROC,                    ///<
       * JPEGIFOFFSET,                ///<
       * JPEGIFBYTECOUNT,             ///<
       * JPEGRESTARTINTERVAL,         ///<
       * JPEGLOSSLESSPREDICTORS,      ///<
       * JPEGPOINTTRANSFORM,          ///<
       * JPEGQTABLES,                 ///<
       * JPEGDCTABLES,                ///<
       * JPEGACTABLES,                ///<
       * STRIPROWCOUNTS,              ///<
       * OPIIMAGEID,                  ///< Full pathname or identifier of original image (OPI).
       * REFPTS,                      ///<
       * REGIONTACKPOINT,             ///<
       * REGIONWARPCORNERS,           ///<
       * REGIONAFFINE,                ///<
       * PIXAR_IMAGEFULLLENGTH,       ///<
       * PIXAR_IMAGEFULLWIDTH,        ///<
       * PIXAR_TEXTUREFORMAT,         ///<
       * PIXAR_WRAPMODES,             ///<
       * PIXAR_FOVCOT,                ///<
       * PIXAR_MATRIX_WORLDTOSCREEN,  ///<
       * PIXAR_MATRIX_WORLDTOCAMERA,  ///<
       * WRITERSERIALNUMBER,          ///<
       * IT8SITE,                     ///<
       * IT8COLORSEQUENCE,            ///<
       * IT8HEADER,                   ///<
       * IT8RASTERPADDING,            ///<
       * IT8BITSPERRUNLENGTH,         ///<
       * IT8BITSPEREXTENDEDRUNLENGTH, ///<
       * IT8COLORTABLE,               ///<
       * IT8IMAGECOLORINDICATOR,      ///<
       * IT8BKGCOLORINDICATOR,        ///<
       * IT8IMAGECOLORVALUE,          ///<
       * IT8BKGCOLORVALUE,            ///<
       * IT8PIXELINTENSITYRANGE,      ///<
       * IT8TRANSPARENCYINDICATOR,    ///<
       * IT8COLORCHARACTERIZATION,    ///<
       * IT8HCUSAGE,                  ///<
       * IT8TRAPINDICATOR,            ///<
       * IT8CMYKEQUIVALENT,           ///<
       * FRAMECOUNT,                  ///<
       * EXIFIFD,                     ///<
       * IMAGELAYER,                  ///< Background, mask and foreground layers from RFC2301.
       * JBIGOPTIONS,                 ///<
       * GPSIFD,                      ///<
       * FAXRECVPARAMS,               ///<
       * FAXSUBADDRESS,               ///<
       * FAXRECVTIME,                 ///<
       * FAXDCS,                      ///<
       * STONITS,                     ///<
       * FEDEX_EDR,                   ///<
       * INTEROPERABILITYIFD,         ///<
       * DNGVERSION,                  ///<
       * DNGBACKWARDVERSION,          ///<
       * UNIQUECAMERAMODEL,           ///<
       * LOCALIZEDCAMERAMODEL,        ///<
       * CFAPLANECOLOR,               ///<
       * CFALAYOUT,                   ///<
       * LINEARIZATIONTABLE,          ///<
       * BLACKLEVELREPEATDIM,         ///<
       * BLACKLEVEL,                  ///<
       * BLACKLEVELDELTAH,            ///<
       * BLACKLEVELDELTAV,            ///<
       * WHITELEVEL,                  ///<
       * DEFAULTSCALE,                ///<
       * DEFAULTCROPORIGIN,           ///<
       * DEFAULTCROPSIZE,             ///<
       * COLORMATRIX1,                ///<
       * COLORMATRIX2,                ///<
       * CAMERACALIBRATION1,          ///<
       * CAMERACALIBRATION2,          ///<
       * REDUCTIONMATRIX1,            ///<
       * REDUCTIONMATRIX2,            ///<
       * ANALOGBALANCE,               ///<
       * ASSHOTNEUTRAL,               ///<
       * ASSHOTWHITEXY,               ///<
       * BASELINEEXPOSURE,            ///<
       * BASELINENOISE,               ///<
       * BASELINESHARPNESS,           ///<
       * BAYERGREENSPLIT,             ///<
       * LINEARRESPONSELIMIT,         ///<
       * CAMERASERIALNUMBER,          ///<
       * LENSINFO,                    ///<
       * CHROMABLURRADIUS,            ///<
       * ANTIALIASSTRENGTH,           ///<
       * SHADOWSCALE,                 ///<
       * DNGPRIVATEDATA,              ///<
       * MAKERNOTESAFETY,             ///<
       * CALIBRATIONILLUMINANT1,      ///<
       * CALIBRATIONILLUMINANT2,      ///<
       * BESTQUALITYSCALE,            ///<
       * RAWDATAUNIQUEID,             ///<
       * ORIGINALRAWFILENAME,         ///<
       * ORIGINALRAWFILEDATA,         ///<
       * ACTIVEAREA,                  ///<
       * MASKEDAREAS,                 ///<
       * ASSHOTICCPROFILE,            ///<
       * ASSHOTPREPROFILEMATRIX,      ///<
       * CURRENTICCPROFILE,           ///<
       * CURRENTPREPROFILEMATRIX,     ///<
       * DCSHUESHIFTVALUES,           ///<
       * FAXMODE,                     ///<
       * JPEGQUALITY,                 ///<
       * JPEGCOLORMODE,               ///<
       * JPEGTABLESMODE,              ///<
       * FAXFILLFUNC,                 ///<
       * PIXARLOGDATAFMT,             ///<
       * DCSIMAGERTYPE,               ///<
       * DCSINTERPMODE,               ///<
       * DCSBALANCEARRAY,             ///<
       * DCSCORRECTMATRIX,            ///<
       * DCSGAMMA,                    ///<
       * DCSTOESHOULDERPTS,           ///<
       * DCSCALIBRATIONFD,            ///<
       * ZIPQUALITY,                  ///<
       * PIXARLOGQUALITY,             ///<
       * DCSCLIPRECTANGLE,            ///<
       * SGILOGDATAFMT,               ///<
       * SGILOGENCODE,                ///<
       * LZMAPRESET,                  ///<
       * PERSAMPLE                    ///<
       */

      /**
       * Get the TIFF tag number for the specified tag.
       *
       * @param tag the tag to use.
       * @returns the tag number, or zero if not implemented.
       */
      tag_type
      getWrappedTag(StringTag1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(StringTagArray1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16Tag1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16TagArray1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16Compression1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16FillOrder1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16Orientation1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16PhotometricInterpretation1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16PlanarConfiguration1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16Predictor1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16SampleFormat1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16Threshholding1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16YCbCrPosition1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16Tag2 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16Tag6 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16ExtraSamplesArray1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt16TagArray3 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt32Tag1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt32TagArray1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(UInt64TagArray1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(RawDataTag1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(FloatTag1 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(FloatTag2 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(FloatTag3 tag);

      /// @copydoc getWrappedTag(StringTag1)
      tag_type
      getWrappedTag(FloatTag6 tag);

    }

    namespace detail
    {
      /**
       * TIFF implementation details.
       */
      namespace tiff
      {

        /**
         * Map a given tag category enum type to the corresponding
         * language value type.
         */
        template<typename TagCategory>
        struct TagProperties;

        /// Properties of StringTag1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::StringTag1>
        {
          /// string type.
          typedef std::string value_type;
        };

        /// Properties of StringTagArray1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::StringTagArray1>
        {
          /// string type.
          typedef std::vector<std::string> value_type;
        };

        /// Properties of UInt16Tag1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16Tag1>
        {
          /// uint16_t type.
          typedef uint16_t value_type;
        };

        /// Properties of UInt16TagArray1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16TagArray1>
        {
          /// uint16_t vector type.
          typedef std::vector<uint16_t> value_type;
        };

        /// Properties of UInt16Compression1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16Compression1>
        {
          /// uint16_t type.
          typedef ::ome::bioformats::tiff::Compression value_type;
        };

        /// Properties of UInt16FillOrder1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16FillOrder1>
        {
          /// uint16_t type.
          typedef ::ome::bioformats::tiff::FillOrder value_type;
        };

        /// Properties of UInt16Orientation1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16Orientation1>
        {
          /// uint16_t type.
          typedef ::ome::bioformats::tiff::Orientation value_type;
        };

        /// Properties of UInt16PhotometricInterpretation1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16PhotometricInterpretation1>
        {
          /// uint16_t type.
          typedef ::ome::bioformats::tiff::PhotometricInterpretation value_type;
        };

        /// Properties of UInt16PlanarConfiguration1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16PlanarConfiguration1>
        {
          /// uint16_t type.
          typedef ::ome::bioformats::tiff::PlanarConfiguration value_type;
        };

        /// Properties of UInt16Predictor1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16Predictor1>
        {
          /// uint16_t type.
          typedef ::ome::bioformats::tiff::Predictor value_type;
        };

        /// Properties of UInt16SampleFormat1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16SampleFormat1>
        {
          /// uint16_t type.
          typedef ::ome::bioformats::tiff::SampleFormat value_type;
        };

        /// Properties of UInt16Threshholding1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16Threshholding1>
        {
          /// uint16_t type.
          typedef ::ome::bioformats::tiff::Threshholding value_type;
        };

        /// Properties of UInt16YCbCrPosition1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16YCbCrPosition1>
        {
          /// uint16_t type.
          typedef ::ome::bioformats::tiff::YCbCrPosition value_type;
        };

        /// Properties of UInt16Tag2 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16Tag2>
        {
          /// uint16_t array type.
          typedef ome::compat::array<uint16_t, 2> value_type;
        };

        /// Properties of UInt16Tag6 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16Tag6>
        {
          /// uint16 array type.
          typedef ome::compat::array<uint16_t, 6> value_type;
        };

        /// Properties of UInt16ExtraSamplesArray1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16ExtraSamplesArray1>
        {
          /// uint16_t vector type.
          typedef std::vector< ::ome::bioformats::tiff::ExtraSamples> value_type;
        };

        /// Properties of UInt16TagArray3 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt16TagArray3>
        {
          /// uint16_t array type.
          typedef ome::compat::array<std::vector<uint16_t>, 3> value_type;
        };

        /// Properties of UInt32Tag1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt32Tag1>
        {
          /// uint32_t type.
          typedef uint32_t value_type;
        };

        /// Properties of UInt32TagArray1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt32TagArray1>
        {
          /// uint32_t vector type.
          typedef std::vector<uint32_t> value_type;
        };

        /// Properties of UInt64TagArray1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::UInt64TagArray1>
        {
          /// uint64_t vector type.
          typedef std::vector<uint64_t> value_type;
        };

        /// Properties of RawDataTag1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::RawDataTag1>
        {
          /// uint32_t vector type.
          typedef std::vector<uint8_t> value_type;
        };

        /// Properties of FloatTag1 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::FloatTag1>
        {
          /// float type.
          typedef float value_type;
        };

        /// Properties of FloatTag2 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::FloatTag2>
        {
          /// float array type.
          typedef ome::compat::array<float, 2> value_type;
        };

        /// Properties of FloatTag3 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::FloatTag3>
        {
          /// float array type.
          typedef ome::compat::array<float, 3> value_type;
        };

        /// Properties of FloatTag6 tags.
        template<>
        struct TagProperties< ::ome::bioformats::tiff::FloatTag6>
        {
          /// float array type.
          typedef ome::compat::array<float, 6> value_type;
        };

      }
    }
  }
}

#endif // OME_BIOFORMATS_TIFF_TAGS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
