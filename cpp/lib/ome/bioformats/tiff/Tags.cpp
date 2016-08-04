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

#include <ome/bioformats/tiff/Sentry.h>
#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/TIFF.h>

#include <tiffio.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      namespace
      {
        const tag_type TIFFTAG_IMAGEJ_META_DATA_BYTE_COUNTS = 50838;
        const tag_type TIFFTAG_IMAGEJ_META_DATA = 50839;
      }

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

      tag_type
      getWrappedTag(StringTag1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case ARTIST:
#ifdef TIFFTAG_ARTIST
            ret = TIFFTAG_ARTIST;
#endif
            break;
          case COPYRIGHT:
#ifdef TIFFTAG_COPYRIGHT
            ret = TIFFTAG_COPYRIGHT;
#endif
            break;
          case DATETIME:
#ifdef TIFFTAG_DATETIME
            ret = TIFFTAG_DATETIME;
#endif
            break;
          case DOCUMENTNAME:
#ifdef TIFFTAG_DOCUMENTNAME
            ret = TIFFTAG_DOCUMENTNAME;
#endif
            break;
          case HOSTCOMPUTER:
#ifdef TIFFTAG_HOSTCOMPUTER
            ret = TIFFTAG_HOSTCOMPUTER;
#endif
            break;
          case IMAGEDESCRIPTION:
#ifdef TIFFTAG_IMAGEDESCRIPTION
            ret = TIFFTAG_IMAGEDESCRIPTION;
#endif
            break;
          case MAKE:
#ifdef TIFFTAG_MAKE
            ret = TIFFTAG_MAKE;
#endif
            break;
          case MODEL:
#ifdef TIFFTAG_MODEL
            ret = TIFFTAG_MODEL;
#endif
            break;
          case PAGENAME:
#ifdef TIFFTAG_PAGENAME
            ret = TIFFTAG_PAGENAME;
#endif
            break;
          case SOFTWARE:
#ifdef TIFFTAG_SOFTWARE
            ret = TIFFTAG_SOFTWARE;
#endif
            break;
          case TARGETPRINTER:
#ifdef TIFFTAG_TARGETPRINTER
            ret = TIFFTAG_TARGETPRINTER;
#endif
            break;
          }

        return ret;
      }

      tag_type
      getWrappedTag(StringTagArray1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case INKNAMES:
#ifdef TIFFTAG_INKNAMES
            ret = TIFFTAG_INKNAMES;
#endif
            break;
          }

        return ret;
      }

      tag_type
      getWrappedTag(UInt16Tag1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case BITSPERSAMPLE:
#ifdef TIFFTAG_BITSPERSAMPLE
            ret = TIFFTAG_BITSPERSAMPLE;
#endif
            break;
          case CELLWIDTH:
#ifdef TIFFTAG_CELLWIDTH
            ret = TIFFTAG_CELLWIDTH;
#endif
            break;
          case CELLLENGTH:
#ifdef TIFFTAG_CELLLENGTH
            ret = TIFFTAG_CELLLENGTH;
#endif
            break;
          case CLEANFAXDATA:
#ifdef TIFFTAG_CLEANFAXDATA
            ret = TIFFTAG_CLEANFAXDATA;
#endif
            break;
          case DATATYPE:
#ifdef TIFFTAG_DATATYPE
            ret = TIFFTAG_DATATYPE;
#endif
            break;
          case GRAYRESPONSEUNIT:
#ifdef TIFFTAG_GRAYRESPONSEUNIT
            ret = TIFFTAG_GRAYRESPONSEUNIT;
#endif
            break;
          case INDEXED:
#ifdef TIFFTAG_INDEXED
            ret = TIFFTAG_INDEXED;
#endif
            break;
          case INKSET:
#ifdef TIFFTAG_INKSET
            ret = TIFFTAG_INKSET;
#endif
            break;
          case MATTEING:
#ifdef TIFFTAG_MATTEING
            ret = TIFFTAG_MATTEING;
#endif
            break;
          case MAXSAMPLEVALUE:
#ifdef TIFFTAG_MAXSAMPLEVALUE
            ret = TIFFTAG_MAXSAMPLEVALUE;
#endif
            break;
          case MINSAMPLEVALUE:
#ifdef TIFFTAG_MINSAMPLEVALUE
            ret = TIFFTAG_MINSAMPLEVALUE;
#endif
            break;
          case NUMBEROFINKS:
#ifdef TIFFTAG_NUMBEROFINKS
            ret = TIFFTAG_NUMBEROFINKS;
#endif
            break;
          case RESOLUTIONUNIT:
#ifdef TIFFTAG_RESOLUTIONUNIT
            ret = TIFFTAG_RESOLUTIONUNIT;
#endif
            break;
          case SAMPLESPERPIXEL:
#ifdef TIFFTAG_SAMPLESPERPIXEL
            ret = TIFFTAG_SAMPLESPERPIXEL;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16TagArray1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case GRAYRESPONSECURVE:
#ifdef TIFFTAG_GRAYRESPONSECURVE
            ret = TIFFTAG_GRAYRESPONSECURVE;
#endif
            break;
          };

        return ret;
      }

      tag_type
      getWrappedTag(UInt16Compression1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case COMPRESSION:
#ifdef TIFFTAG_COMPRESSION
            ret = TIFFTAG_COMPRESSION;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16FillOrder1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case FILLORDER:
#ifdef TIFFTAG_FILLORDER
            ret = TIFFTAG_FILLORDER;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16Orientation1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case ORIENTATION:
#ifdef TIFFTAG_ORIENTATION
            ret = TIFFTAG_ORIENTATION;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16PhotometricInterpretation1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case PHOTOMETRIC:
#ifdef TIFFTAG_PHOTOMETRIC
            ret = TIFFTAG_PHOTOMETRIC;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16PlanarConfiguration1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case PLANARCONFIG:
#ifdef TIFFTAG_PLANARCONFIG
            ret = TIFFTAG_PLANARCONFIG;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16Predictor1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case PREDICTOR:
#ifdef TIFFTAG_PREDICTOR
            ret = TIFFTAG_PREDICTOR;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16SampleFormat1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case SAMPLEFORMAT:
#ifdef TIFFTAG_SAMPLEFORMAT
            ret = TIFFTAG_SAMPLEFORMAT;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16Threshholding1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case THRESHHOLDING:
#ifdef TIFFTAG_THRESHHOLDING
            ret = TIFFTAG_THRESHHOLDING;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16YCbCrPosition1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case YCBCRPOSITIONING:
#ifdef TIFFTAG_YCBCRPOSITIONING
            ret = TIFFTAG_YCBCRPOSITIONING;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16Tag2 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case DOTRANGE:
#ifdef TIFFTAG_DOTRANGE
            ret = TIFFTAG_DOTRANGE;
#endif
            break;
          case HALFTONEHINTS:
#ifdef TIFFTAG_HALFTONEHINTS
            ret = TIFFTAG_HALFTONEHINTS;
#endif
            break;
          case PAGENUMBER:
#ifdef TIFFTAG_PAGENUMBER
            ret = TIFFTAG_PAGENUMBER;
#endif
            break;
          case YCBCRSUBSAMPLING:
#ifdef TIFFTAG_YCBCRSUBSAMPLING
            ret = TIFFTAG_YCBCRSUBSAMPLING;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16Tag6 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case TRANSFERRANGE:
            ret = 342;
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16ExtraSamplesArray1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case EXTRASAMPLES:
#ifdef TIFFTAG_EXTRASAMPLES
            ret = TIFFTAG_EXTRASAMPLES;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt16TagArray3 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case COLORMAP:
#ifdef TIFFTAG_COLORMAP
            ret = TIFFTAG_COLORMAP;
#endif
            break;
          case TRANSFERFUNCTION:
#ifdef TIFFTAG_TRANSFERFUNCTION
            ret = TIFFTAG_TRANSFERFUNCTION;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt32Tag1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case BADFAXLINES:
#ifdef TIFFTAG_BADFAXLINES
            ret = TIFFTAG_BADFAXLINES;
#endif
            break;
          case CONSECUTIVEBADFAXLINES:
#ifdef TIFFTAG_CONSECUTIVEBADFAXLINES
            ret = TIFFTAG_CONSECUTIVEBADFAXLINES;
#endif
            break;
          case GROUP3OPTIONS:
#ifdef TIFFTAG_GROUP3OPTIONS
            ret = TIFFTAG_GROUP3OPTIONS;
#endif
            break;
          case GROUP4OPTIONS:
#ifdef TIFFTAG_GROUP4OPTIONS
            ret = TIFFTAG_GROUP4OPTIONS;
#endif
            break;
          case IMAGEDEPTH:
#ifdef TIFFTAG_IMAGEDEPTH
            ret = TIFFTAG_IMAGEDEPTH;
#endif
            break;
          case IMAGELENGTH:
#ifdef TIFFTAG_IMAGELENGTH
            ret = TIFFTAG_IMAGELENGTH;
#endif
            break;
          case IMAGEWIDTH:
#ifdef TIFFTAG_IMAGEWIDTH
            ret = TIFFTAG_IMAGEWIDTH;
#endif
            break;
          case ROWSPERSTRIP:
#ifdef TIFFTAG_ROWSPERSTRIP
            ret = TIFFTAG_ROWSPERSTRIP;
#endif
            break;
          case SUBFILETYPE:
#ifdef TIFFTAG_SUBFILETYPE
            ret = TIFFTAG_SUBFILETYPE;
#endif
            break;
          case T4OPTIONS:
#ifdef TIFFTAG_T4OPTIONS
            ret = TIFFTAG_T4OPTIONS;
#endif
            break;
          case T6OPTIONS:
#ifdef TIFFTAG_T6OPTIONS
            ret = TIFFTAG_T6OPTIONS;
#endif
            break;
          case TILEDEPTH:
#ifdef TIFFTAG_TILEDEPTH
            ret = TIFFTAG_TILEDEPTH;
#endif
            break;
          case TILELENGTH:
#ifdef TIFFTAG_TILELENGTH
            ret = TIFFTAG_TILELENGTH;
#endif
            break;
          case TILEWIDTH:
#ifdef TIFFTAG_TILEWIDTH
            ret = TIFFTAG_TILEWIDTH;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(UInt32TagArray1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case IMAGEJ_META_DATA_BYTE_COUNTS:
            ret = TIFFTAG_IMAGEJ_META_DATA_BYTE_COUNTS;
            break;
          case RICHTIFFIPTC:
#ifdef TIFFTAG_RICHTIFFIPTC
            ret = TIFFTAG_RICHTIFFIPTC;
#endif
            break;
          };

        return ret;
      }

      tag_type
      getWrappedTag(UInt64TagArray1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case FREEOFFSETS:
#ifdef TIFFTAG_FREEOFFSETS
            ret = TIFFTAG_FREEOFFSETS;
#endif
            break;
          case FREEBYTECOUNTS:
#ifdef TIFFTAG_FREEBYTECOUNTS
            ret = TIFFTAG_FREEBYTECOUNTS;
#endif
            break;
          case STRIPBYTECOUNTS:
#ifdef TIFFTAG_STRIPBYTECOUNTS
            ret = TIFFTAG_STRIPBYTECOUNTS;
#endif
            break;
          case STRIPOFFSETS:
#ifdef TIFFTAG_STRIPOFFSETS
            ret = TIFFTAG_STRIPOFFSETS;
#endif
            break;
          case SUBIFD:
#ifdef TIFFTAG_SUBIFD
            ret = TIFFTAG_SUBIFD;
#endif
            break;
          case TILEBYTECOUNTS:
#ifdef TIFFTAG_TILEBYTECOUNTS
            ret = TIFFTAG_TILEBYTECOUNTS;
#endif
            break;
          case TILEOFFSETS:
#ifdef TIFFTAG_TILEOFFSETS
            ret = TIFFTAG_TILEOFFSETS;
#endif
            break;
          };

        return ret;
      }

      tag_type
      getWrappedTag(RawDataTag1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case ICCPROFILE:
#ifdef TIFFTAG_ICCPROFILE
            ret = TIFFTAG_ICCPROFILE;
#endif
            break;
          case JPEGTABLES:
#ifdef TIFFTAG_JPEGTABLES
            ret = TIFFTAG_JPEGTABLES;
#endif
            break;
          case PHOTOSHOP:
#ifdef TIFFTAG_PHOTOSHOP
            ret = TIFFTAG_PHOTOSHOP;
#endif
            break;
          case XMLPACKET:
#ifdef TIFFTAG_XMLPACKET
            ret = TIFFTAG_XMLPACKET;
#endif
            break;
          case IMAGEJ_META_DATA:
            ret = TIFFTAG_IMAGEJ_META_DATA;
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(FloatTag1 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case XRESOLUTION:
#ifdef TIFFTAG_XRESOLUTION
            ret = TIFFTAG_XRESOLUTION;
#endif
            break;
          case YRESOLUTION:
#ifdef TIFFTAG_YRESOLUTION
            ret = TIFFTAG_YRESOLUTION;
#endif
            break;
          case XPOSITION:
#ifdef TIFFTAG_XPOSITION
            ret = TIFFTAG_XPOSITION;
#endif
            break;
          case YPOSITION:
#ifdef TIFFTAG_YPOSITION
            ret = TIFFTAG_YPOSITION;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(FloatTag2 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case WHITEPOINT:
#ifdef TIFFTAG_WHITEPOINT
            ret = TIFFTAG_WHITEPOINT;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(FloatTag3 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case YCBCRCOEFFICIENTS:
#ifdef TIFFTAG_YCBCRCOEFFICIENTS
            ret = TIFFTAG_YCBCRCOEFFICIENTS;
#endif
            break;
          };
        return ret;
      }

      tag_type
      getWrappedTag(FloatTag6 tag)
      {
        tag_type ret = 0;

        switch(tag)
          {
          case PRIMARYCHROMATICITIES:
#ifdef TIFFTAG_PRIMARYCHROMATICITIES
            ret = TIFFTAG_PRIMARYCHROMATICITIES;
#endif
            break;
          case REFERENCEBLACKWHITE:
#ifdef TIFFTAG_REFERENCEBLACKWHITE
            ret = TIFFTAG_REFERENCEBLACKWHITE;
#endif
            break;
          };
        return ret;
      }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

    }
  }
}
