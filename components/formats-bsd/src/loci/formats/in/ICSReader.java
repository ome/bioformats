/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
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
 * #L%
 */

package loci.formats.in;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * ICSReader is the file format reader for ICS (Image Cytometry Standard)
 * files. More information on ICS can be found at http://libics.sourceforge.net
 *
 * TODO : remove sub-C logic once N-dimensional support is in place
 *        see http://dev.loci.wisc.edu/trac/java/ticket/398
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ICSReader extends FormatReader {

  // -- Constants --

  /** Newline characters. */
  public static final String NL = "\r\n";

  public static final String[] DATE_FORMATS = {
    "EEEE, MMMM dd, yyyy HH:mm:ss",
    "EEE dd MMMM yyyy HH:mm:ss",
    "EEE MMM dd HH:mm:ss yyyy",
    "EE dd MMM yyyy HH:mm:ss z",
    "HH:mm:ss dd\\MM\\yy"
  };

  // key token value matching regexes within the "document" category.
  //
  // this table is alphabetized for legibility only.
  //
  // however it is important that the most qualified regex list goes first,
  // e.g. { "a", "b" } must precede { "a" }.
  private static final String[][] DOCUMENT_KEYS = {
    { "date" },  // the full key is "document date"
    { "document", "average" },
    { "document" },
    { "gmtdate" },
    { "label" }
  };

  // key token value matching regexes within the "history" category.
  //
  // this table is alphabetized for legibility only.
  //
  // however it is important that the most qualified regex list goes first,
  // e.g. { "a", "b" } must precede { "a" }.
  private static final String[][] HISTORY_KEYS = {
    { "a\\d" }, // the full key is "history a1", etc.
    { "acquisition", "acquire\\..*." },
    { "acquisition", "laserbox\\..*." },
    { "acquisition", "modules\\(.*." },
    { "acquisition", "objective", "position" },
    { "adc", "resolution" },
    { "atd_hardware", "ver" },
    { "atd_libraries", "ver" },
    { "atd_microscopy", "ver" },
    { "author" },
    { "averagecount" },
    { "averagequality" },
    { "beam", "zoom" },
    { "binning" },
    { "bits/pixel" },
    { "black", "level" },
    { "black", "level\\*" },
    { "black_level" },
    { "camera", "manufacturer" },
    { "camera", "model" },
    { "camera" },
    { "cfd", "holdoff" },
    { "cfd", "limit", "high" },
    { "cfd", "limit", "low" },
    { "cfd", "zc", "level" },
    { "channel\\*" },
    { "collection", "time" },
    { "cols" },
    { "company" },
    { "count", "increment" },
    { "created", "on" },
    { "creation", "date" },
    { "cube", "descriptio" }, // sic; not found in sample files
    { "cube", "description" }, // correction; not found in sample files
    { "cube", "emm", "nm" },
    { "cube", "exc", "nm" },
    { "cube" },
    { "date" },
    { "dategmt" },
    { "dead", "time", "comp" },
    { "desc", "exc", "turret" },
    { "desc", "emm", "turret" },
    { "detector", "type" },
    { "detector" },
    { "dimensions" },
    { "direct", "turret" },
    { "dither", "range" },
    { "dwell" },
    { "excitationfwhm" },
    { "experiment" },
    { "experimenter" },
    { "expon.", "order" },
    { "exposure" },
    { "exposure_time" },
    { "ext", "latch", "delay" },
    { "extents" },
    { "filterset", "dichroic", "name" },
    { "filterset", "dichroic", "nm" },
    { "filterset", "emm", "name" },
    { "filterset", "emm", "nm" },
    { "filterset", "exc", "name" },
    { "filterset", "exc", "nm" },
    { "filterset" },
    { "filter\\*" },
    { "firmware" },
    { "fret", "backgr\\d"},
    { "frametime" },
    { "gain" },
    { "gain\\d" },
    { "gain\\*" },
    { "gamma" },
    { "icsviewer", "ver" },
    { "ht\\*" },
    { "id" },
    { "illumination", "mode", "laser" },
    { "illumination", "mode" },
    { "image", "bigendian" },
    { "image", "bpp" },
    { "image", "form" }, // not found in sample files
    { "image", "physical_sizex" },
    { "image", "physical_sizey" },
    { "image", "sizex" },
    { "image", "sizey" },
    { "labels" },
    { "lamp", "manufacturer" },
    { "lamp", "model" },
    { "laser", "firmware" },
    { "laser", "manufacturer" },
    { "laser", "model" },
    { "laser", "power" },
    { "laser", "rep", "rate" },
    { "laser", "type" },
    { "laser\\d", "intensity" },
    { "laser\\d", "name" },
    { "laser\\d", "wavelength" },
    { "left" },
    { "length" },
    { "line", "compressio" }, // sic
    { "line", "compression" }, // correction; not found in sample files
    { "linetime" },
    { "magnification" },
    { "manufacturer" },
    { "max", "photon", "coun" }, // sic
    { "max", "photon", "count" }, // correction; not found in sample files
    { "memory", "bank" },
    { "metadata", "format", "ver" },
    { "microscope", "built", "on" },
    { "microscope", "name" },
    { "microscope" },
    { "mirror", "\\d" },
    { "mode" },
    { "noiseval" },
    { "no.", "frames" },
    { "objective", "detail" },
    { "objective", "immersion" },
    { "objective", "mag" },
    { "objective", "magnification" },
    { "objective", "na" },
    { "objective", "type" },
    { "objective", "workingdistance" },
    { "objective" },
    { "offsets" },
    { "other", "text" },
    { "passcount" },
    { "pinhole" },
    { "pixel", "clock" },
    { "pixel", "time" },
    { "pmt" },
    { "polarity" },
    { "region" },
    { "rep", "period" },
    { "repeat", "time" },
    { "revision" },
    { "routing", "chan", "x" },
    { "routing", "chan", "y" },
    { "rows" },
    { "scan", "borders" },
    { "scan", "flyback" },
    { "scan", "pattern" },
    { "scan", "pixels", "x" },
    { "scan", "pixels", "y" },
    { "scan", "pos", "x" },
    { "scan", "pos", "y" },
    { "scan", "resolution" },
    { "scan", "speed" },
    { "scan", "zoom" },
    { "scanner", "lag" },
    { "scanner", "pixel", "time" },
    { "scanner", "resolution" },
    { "scanner", "speed" },
    { "scanner", "xshift" },
    { "scanner", "yshift" },
    { "scanner", "zoom" },
    { "shutter\\d" },
    { "shutter", "type" },
    { "software" },
    { "spectral", "bin_definition" },
    { "spectral", "calibration", "gain", "data" },
    { "spectral", "calibration", "gain", "mode" },
    { "spectral", "calibration", "offset", "data" },
    { "spectral", "calibration", "offset", "mode" },
    { "spectral", "calibration", "sensitivity", "mode" },
    { "spectral", "central_wavelength" },
    { "spectral", "laser_shield" },
    { "spectral", "laser_shield_width" },
    { "spectral", "resolution" },
    { "stage", "controller" },
    { "stage", "firmware" },
    { "stage", "manufacturer" },
    { "stage", "model" },
    { "stage", "pos" },
    { "stage", "positionx" },
    { "stage", "positiony" },
    { "stage", "positionz" },
    { "stage_xyzum" },
    { "step\\d", "channel", "\\d" },
    { "step\\d", "gain", "\\d" },
    { "step\\d", "laser" },
    { "step\\d", "name" },
    { "step\\d", "pinhole" },
    { "step\\d", "pmt", "ch", "\\d" },
    { "step\\d", "shutter", "\\d" },
    { "step\\d" },
    { "stop", "on", "o'flow" },
    { "stop", "on", "time" },
    { "study" },
    { "sync", "freq", "div" },
    { "sync", "holdoff" },
    { "sync" },
    { "tac", "gain" },
    { "tac", "limit", "low" },
    { "tac", "offset" },
    { "tac", "range" },
    { "tau\\d" },
    { "tcspc", "adc", "res" },
    { "tcspc", "adc", "resolution" },
    { "tcspc", "approx", "adc", "rate" },
    { "tcspc", "approx", "cfd", "rate" },
    { "tcspc", "approx", "tac", "rate" },
    { "tcspc", "bh" },
    { "tcspc", "cfd", "holdoff" },
    { "tcspc", "cfd", "limit", "high" },
    { "tcspc", "cfd", "limit", "low" },
    { "tcspc", "cfd", "zc", "level" },
    { "tcspc", "clock", "polarity" },
    { "tcspc", "collection", "time" },
    { "tcspc", "count", "increment" },
    { "tcspc", "dead", "time", "enabled" },
    { "tcspc", "delay" },
    { "tcspc", "dither", "range" },
    { "tcspc", "left", "border" },
    { "tcspc", "line", "compression" },
    { "tcspc", "mem", "offset" },
    { "tcspc", "operation", "mode" },
    { "tcspc", "overflow" },
    { "tcspc", "pixel", "clk", "divider" },
    { "tcspc", "pixel", "clock" },
    { "tcspc", "routing", "x" },
    { "tcspc", "routing", "y" },
    { "tcspc", "scan", "x" },
    { "tcspc", "scan", "y" },
    { "tcspc", "sync", "divider" },
    { "tcspc", "sync", "holdoff" },
    { "tcspc", "sync", "rate" },
    { "tcspc", "sync", "threshold" },
    { "tcspc", "sync", "zc", "level" },
    { "tcspc", "tac", "gain" },
    { "tcspc", "tac", "limit", "high" },
    { "tcspc", "tac", "limit", "low" },
    { "tcspc", "tac", "offset" },
    { "tcspc", "tac", "range" },
    { "tcspc", "time", "window" },
    { "tcspc", "top", "border" },
    { "tcspc", "total", "frames" },
    { "tcspc", "total", "time" },
    { "tcspc", "trigger" },
    { "tcspc", "x", "sync", "polarity" },
    { "tcspc", "y", "sync", "polarity" },
    { "text" },
    { "time" },
    { "title" },
    { "top" },
    { "transmission" },
    { "trigger" },
    { "type" },
    { "units" },
    { "version" },
    { "wavelength\\*" },
    { "x", "amplitude" },
    { "y", "amplitude" },
    { "x", "delay" },
    { "y", "delay" },
    { "x", "offset" },
    { "y", "offset" },
    { "z", "\\(background\\)" }
  };

  // key token value matching regexes within the "layout" category.
  //
  // this table is alphabetized for legibility only.
  //
  // however it is important that the most qualified regex list goes first,
  // e.g. { "a", "b" } must precede { "a" }.
  private static final String[][] LAYOUT_KEYS = {
    { "coordinates" },  // the full key is "layout coordinates"
    { "order" },
    { "parameters" },
    { "real_significant_bits" },
    { "significant_bits" },
    { "significant_channels" },
    { "sizes" }
  };

  // key token value matching regexes within the "parameter" category.
  //
  // this table is alphabetized for legibility only.
  //
  // however it is important that the most qualified regex list goes first,
  // e.g. { "a", "b" } must precede { "a" }.
  private static final String[][] PARAMETER_KEYS = {
    { "allowedlinemodes" },  // the full key is "parameter allowedlinemodes"
    { "ch" },
    { "higher_limit" },
    { "labels" },
    { "lower_limit" },
    { "origin" },
    { "range" },
    { "sample_width", "ch" },
    { "sample_width" },
    { "scale" },
    { "units", "adc-units", "channels" },
    { "units", "adc-units", "nm" },
    { "units" }
  };

  // key token value matching regexes within the "representation" category.
  //
  // this table is alphabetized for legibility only.
  //
  // however it is important that the most qualified regex list goes first,
  // e.g. { "a", "b" } must precede { "a" }.
  private static final String[][] REPRESENTATION_KEYS = {
    { "byte_order" }, // the full key is "representation byte_order"
    { "compression" },
    { "format" },
    { "sign" }
  };

  // key token value matching regexes within the "sensor" category.
  //
  // this table is alphabetized for legibility only.
  //
  // however it is important that the most qualified regex list goes first,
  // e.g. { "a", "b" } must precede { "a" }.
  private static final String[][] SENSOR_KEYS = {
    { "model" },  // the full key is "sensor model"
    { "s_params", "channels" },
    { "s_params", "exphotoncnt" },
    { "s_params", "lambdaem" },
    { "s_params", "lambdaex" },
    { "s_params", "numaperture" },
    { "s_params", "pinholeradius" },
    { "s_params", "pinholespacing" },
    { "s_params", "refinxlensmedium" }, // sic; not found in sample files
    { "s_params", "refinxmedium" }, // sic; not found in sample files
    { "s_params", "refrinxlensmedium" },
    { "s_params", "refrinxmedium" },
    { "type" }
  };

  // key token value matching regexes within the "view" category.
  //
  // this table is alphabetized for legibility only.
  //
  // however it is important that the most qualified regex list goes first,
  // e.g. { "a", "b" } must precede { "a" }.
  private static final String[][] VIEW_KEYS = {
    { "view", "color", "lib", "lut" }, // the full key is
                                       // "view view color lib lut"
    { "view", "color", "count" },
    { "view", "color", "doc", "scale" },
    { "view", "color", "mode", "rgb", "set" },
    { "view", "color", "mode", "rgb" },
    { "view", "color", "schemes" },
    { "view", "color", "view", "active" },
    { "view", "color" },
    { "view\\d", "alpha" },
    { "view\\d", "alphastate" },
    { "view\\d", "annotation", "annellipse" },
    { "view\\d", "annotation", "annpoint" },
    { "view\\d", "autoresize" },
    { "view\\d", "axis" },
    { "view\\d", "blacklevel" },
    { "view\\d", "color" },
    { "view\\d", "cursor" },
    { "view\\d", "dimviewoption" },
    { "view\\d", "gamma" },
    { "view\\d", "ignoreaspect" },
    { "view\\d", "intzoom" },
    { "view\\d", "live" },
    { "view\\d", "order" },
    { "view\\d", "port" },
    { "view\\d", "position" },
    { "view\\d", "saturation" },
    { "view\\d", "scale" },
    { "view\\d", "showall" },
    { "view\\d", "showcursor" },
    { "view\\d", "showindex" },
    { "view\\d", "size" },
    { "view\\d", "synchronize" },
    { "view\\d", "tile" },
    { "view\\d", "useunits" },
    { "view\\d", "zoom" },
    { "view\\d" },
    { "view" }
  };

  // These strings appeared in the former metadata field categories but are not
  // found in the LOCI sample files.
  //
  // The former metadata field categories table did not save the context, i.e.
  // the first token such as "document" or "history" and other intermediate
  // tokens.  The preceding tables such as DOCUMENT_KEYS or HISTORY_KEYS use
  // this full context.
  //
  // In an effort at backward compatibility, these will be used to form key
  // value pairs if key/value pair not already assigned and they match anywhere
  // in the input line.
  //
  private static String[][] OTHER_KEYS = {
    { "cube", "descriptio" },  // sic; also listed in HISTORY_KEYS
    { "cube", "description" }, // correction; also listed in HISTORY_KEYS
    { "image", "form" },       // also listed in HISTORY_KEYS
    { "refinxlensmedium" },    // Could be a mispelling of "refrinxlensmedium";
                               // also listed in SENSOR_KEYS
    { "refinxmedium" },        // Could be a mispelling of "refinxmedium";
                               // also listed in SENSOR_KEYS
    { "scil_type" },
    { "source" }
  };

  // -- Fields --

  /** Current filename. */
  private String currentIcsId;
  private String currentIdsId;

  /** Flag indicating whether current file is v2.0. */
  private boolean versionTwo;

  /** Image data. */
  private byte[] data;

  /** Offset to pixel data. */
  private long offset;

  /** Whether or not the pixels are GZIP-compressed. */
  private boolean gzip;

  private GZIPInputStream gzipStream;

  /** Whether or not the image is inverted along the Y axis. */
  private boolean invertY;

  /** Whether or not the channels represent lifetime histogram bins. */
  private boolean lifetime;

  /** Dimensional reordering for lifetime data */
  private String labels;

  /** The length of each channel axis. */
  private Vector<Integer> channelLengths;

  /** The type of each channel axis. */
  private Vector<String> channelTypes;

  private int prevImage;
  private boolean hasInstrumentData = false;
  private boolean storedRGB = false;

  // -- Constructor --

  /** Constructs a new ICSReader. */
  public ICSReader() {
    super("Image Cytometry Standard", new String[] {"ics", "ids"});
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.FLIM_DOMAIN,
      FormatTools.UNKNOWN_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .ics and possibly one .ids with a similar name";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    // check if we have a v2 ICS file - means there is no companion IDS file
    RandomAccessInputStream f = new RandomAccessInputStream(id);
    boolean singleFile = f.readString(17).trim().equals("ics_version\t2.0");
    f.close();
    return singleFile;
  }

  /* @see loci.formats.IFormatReader#getDomains() */
  @Override
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    String[] domain = new String[] {FormatTools.GRAPHICS_DOMAIN};
    if (getModuloC().length() > 1) {
      domain[0] = FormatTools.FLIM_DOMAIN;
    }
    else if (hasInstrumentData) {
      domain[0] = FormatTools.LM_DOMAIN;
    }

    return domain;
  }

  /* @see loci.formats.IFormatReader#isInterleaved(int) */
  @Override
  public boolean isInterleaved(int subC) {
    FormatTools.assertId(currentId, true, 1);
    return subC == 0 && core.get(0).interleaved;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int len = FormatTools.getPlaneSize(this);
    int pixel = bpp * getRGBChannelCount();
    int rowLen = FormatTools.getPlaneSize(this, w, 1);

    int[] coordinates = getZCTCoords(no);
    int[] prevCoordinates = getZCTCoords(prevImage);

    if (!gzip) {
      in.seek(offset + no * (long) len);
    }
    else {
      long toSkip = (no - prevImage - 1) * (long) len;
      if (gzipStream == null || no <= prevImage) {
        FileInputStream fis = null;
        toSkip = no * (long) len;
        if (versionTwo) {
          fis = new FileInputStream(currentIcsId);
          fis.skip(offset);
        }
        else {
          fis = new FileInputStream(currentIdsId);
          toSkip += offset;
        }
        try {
          gzipStream = new GZIPInputStream(fis);
        }
        catch (IOException e) {
          // the 'gzip' flag is set erroneously
          gzip = false;
          in.seek(offset + no * (long) len);
          gzipStream = null;
        }
      }

      if (gzipStream != null) {
        while (toSkip > 0) {
          toSkip -= gzipStream.skip(toSkip);
        }

        data = new byte[len * (storedRGB ? getSizeC() : 1)];
        int toRead = data.length;
        while (toRead > 0) {
          toRead -= gzipStream.read(data, data.length - toRead, toRead);
        }
      }
    }

    int sizeC = lifetime ? 1 : getSizeC();

    if (!isRGB() && channelLengths.size() == 1 && storedRGB) {
      // channels are stored interleaved, but because there are more than we
      // can display as RGB, we need to separate them
      in.seek(offset +
        (long) len * getIndex(coordinates[0], 0, coordinates[2]));
      if (!gzip && data == null) {
        data = new byte[len * getSizeC()];
        in.read(data);
      }
      else if (!gzip && (coordinates[0] != prevCoordinates[0] ||
        coordinates[2] != prevCoordinates[2]))
      {
        in.read(data);
      }

      for (int row=y; row<h + y; row++) {
        for (int col=x; col<w + x; col++) {
          int src =
            bpp * ((no % getSizeC()) + sizeC * (row * getSizeX() + col));
          int dest = bpp * ((row - y) * w + (col - x));
          System.arraycopy(data, src, buf, dest, bpp);
        }
      }
    }
    else if (gzip) {
      RandomAccessInputStream s = new RandomAccessInputStream(data);
      readPlane(s, x, y, w, h, buf);
      s.close();
    }
    else {
      readPlane(in, x, y, w, h, buf);
    }

    if (invertY) {
      byte[] row = new byte[rowLen];
      for (int r=0; r<h/2; r++) {
        int topOffset = r * rowLen;
        int bottomOffset = (h - r - 1) * rowLen;
        System.arraycopy(buf, topOffset, row, 0, rowLen);
        System.arraycopy(buf, bottomOffset, buf, topOffset, rowLen);
        System.arraycopy(row, 0, buf, bottomOffset, rowLen);
      }
    }

    prevImage = no;

    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (versionTwo) {
      return noPixels ? null : new String[] {currentIcsId};
    }
    return noPixels ? new String[] {currentIcsId} :
      new String[] {currentIcsId, currentIdsId};
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      currentIcsId = null;
      currentIdsId = null;
      data = null;
      versionTwo = false;
      gzip = false;
      invertY = false;
      lifetime = false;
      prevImage = 0;
      hasInstrumentData = false;
      storedRGB = false;
      if (gzipStream != null) {
        gzipStream.close();
      }
      gzipStream = null;
    }
  }

  /* @see loci.formats.IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    if (in != null) {
      in.close();
    }
    if (versionTwo) {
      in = new RandomAccessInputStream(currentIcsId);
    }
    else {
      in = new RandomAccessInputStream(currentIdsId);
    }
    in.order(isLittleEndian());
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    LOGGER.info("Finding companion file");

    String icsId = id, idsId = id;
    int dot = id.lastIndexOf(".");
    String ext = dot < 0 ? "" : id.substring(dot + 1).toLowerCase();
    if (ext.equals("ics")) {
      // convert C to D regardless of case
      char[] c = idsId.toCharArray();
      c[c.length - 2]++;
      idsId = new String(c);
    }
    else if (ext.equals("ids")) {
      // convert D to C regardless of case
      char[] c = icsId.toCharArray();
      c[c.length - 2]--;
      icsId = new String(c);
    }

    if (icsId == null) throw new FormatException("No ICS file found.");
    Location icsFile = new Location(icsId);
    if (!icsFile.exists()) throw new FormatException("ICS file not found.");

    LOGGER.info("Checking file version");

    // check if we have a v2 ICS file - means there is no companion IDS file
    RandomAccessInputStream f = new RandomAccessInputStream(icsId);
    if (f.readString(17).trim().equals("ics_version\t2.0")) {
      in = new RandomAccessInputStream(icsId);
      versionTwo = true;
    }
    else {
      if (idsId == null) throw new FormatException("No IDS file found.");
      Location idsFile = new Location(idsId);
      if (!idsFile.exists()) throw new FormatException("IDS file not found.");
      currentIdsId = idsId;
      in = new RandomAccessInputStream(currentIdsId);
    }
    f.close();

    currentIcsId = icsId;

    LOGGER.info("Reading metadata");

    CoreMetadata m = core.get(0);

    Double[] scales = null;
    Double[] timestamps = null;
    String[] units = null;
    String[] axes = null;
    int[] axisLengths = null;
    String byteOrder = null, rFormat = null, compression = null;

    // parse key/value pairs from beginning of ICS file

    RandomAccessInputStream reader = new RandomAccessInputStream(icsId);
    reader.seek(0);
    reader.readString(NL);
    String line = reader.readString(NL);
    boolean signed = false;

    final StringBuilder textBlock = new StringBuilder();
    double[] sizes = null;

    Double[] emWaves = null, exWaves = null;
    Length[] stagePos = null;
    String imageName = null, date = null, description = null;
    Double magnification = null, lensNA = null, workingDistance = null;
    String objectiveModel = null, immersion = null, lastName = null;
    Hashtable<Integer, Double> gains = new Hashtable<Integer, Double>();
    Hashtable<Integer, Double> pinholes = new Hashtable<Integer, Double>();
    Hashtable<Integer, Double> wavelengths = new Hashtable<Integer, Double>();
    Hashtable<Integer, String> channelNames = new Hashtable<Integer, String>();

    String laserModel = null;
    String laserManufacturer = null;
    Double laserPower = null;
    Double laserRepetitionRate = null;
    String detectorManufacturer = null;
    String detectorModel = null;
    String microscopeModel = null;
    String microscopeManufacturer = null;
    String experimentType = null;
    Time exposureTime = null;

    String filterSetModel = null;
    String dichroicModel = null;
    String excitationModel = null;
    String emissionModel = null;

    while (line != null && !line.trim().equals("end") &&
      reader.getFilePointer() < reader.length() - 1)
    {
      line = line.trim();
      if (line.length() > 0) {

        // split the line into tokens
        String[] tokens = tokenize(line);

        String token0 = tokens[0].toLowerCase();
        String[] keyValue = null;

        // version category
        if (token0.equals("ics_version")) {
          String value = concatenateTokens(tokens, 1, tokens.length);
          addGlobalMeta(token0, value);
        }
        // filename category
        else if (token0.equals("filename")) {
          imageName = concatenateTokens(tokens, 1, tokens.length);
          addGlobalMeta(token0, imageName);
        }
        // layout category
        else if (token0.equals("layout")) {
          keyValue = findKeyValue(tokens, LAYOUT_KEYS);
          String key = keyValue[0];
          String value = keyValue[1];
          addGlobalMeta(key, value);

          if (key.equalsIgnoreCase("layout sizes")) {
            StringTokenizer t = new StringTokenizer(value);
            axisLengths = new int[t.countTokens()];
            for (int n=0; n<axisLengths.length; n++) {
              try {
                axisLengths[n] = Integer.parseInt(t.nextToken().trim());
              }
              catch (NumberFormatException e) {
                LOGGER.debug("Could not parse axis length", e);
              }
            }
          }
          else if (key.equalsIgnoreCase("layout order")) {
            StringTokenizer t = new StringTokenizer(value);
            axes = new String[t.countTokens()];
            for (int n=0; n<axes.length; n++) {
              axes[n] = t.nextToken().trim();
            }
          }
          else if (key.equalsIgnoreCase("layout significant_bits")) {
            m.bitsPerPixel = Integer.parseInt(value);
          }
        }
        // representation category
        else if (token0.equals("representation")) {
          keyValue = findKeyValue(tokens, REPRESENTATION_KEYS);
          String key = keyValue[0];
          String value = keyValue[1];
          addGlobalMeta(key, value);

          if (key.equalsIgnoreCase("representation byte_order")) {
            byteOrder = value;
          }
          else if (key.equalsIgnoreCase("representation format")) {
            rFormat = value;
          }
          else if (key.equalsIgnoreCase("representation compression")) {
            compression = value;
          }
          else if (key.equalsIgnoreCase("representation sign")) {
            signed = value.equals("signed");
          }
        }
        // parameter category
        else if (token0.equals("parameter")) {
          keyValue = findKeyValue(tokens, PARAMETER_KEYS);
          String key = keyValue[0];
          String value = keyValue[1];
          addGlobalMeta(key, value);

          if (key.equalsIgnoreCase("parameter scale")) {
            // parse physical pixel sizes and time increment
            scales = splitDoubles(value);
          }
          else if (key.equalsIgnoreCase("parameter t")) {
            // parse explicit timestamps
            timestamps = splitDoubles(value);
          }
          else if (key.equalsIgnoreCase("parameter units")) {
            // parse units for scale
            units = value.split("\\s+");
          }
          if (getMetadataOptions().getMetadataLevel() !=
              MetadataLevel.MINIMUM)
          {
            if (key.equalsIgnoreCase("parameter ch")) {
              String[] names = value.split(" ");
              for (int n=0; n<names.length; n++) {
                channelNames.put(new Integer(n), names[n].trim());
              }
            }
          }
        }
        // history category
        else if (token0.equals("history")) {
          keyValue = findKeyValue(tokens, HISTORY_KEYS);
          String key = keyValue[0];
          String value = keyValue[1];
          addGlobalMeta(key, value);

          Double doubleValue = null;
          try {
            doubleValue = new Double(value);
          }
          catch (NumberFormatException e) {
            // ARG this happens a lot; spurious error in most cases
            LOGGER.debug("Could not parse double value '{}'", value, e);
          }

          if (key.equalsIgnoreCase("history software") &&
              value.indexOf("SVI") != -1) {
            // ICS files written by SVI Huygens are inverted on the Y axis
            invertY = true;
          }
          else if (key.equalsIgnoreCase("history date") ||
                   key.equalsIgnoreCase("history created on"))
          {
            if (value.indexOf(' ') > 0) {
              date = value.substring(0, value.lastIndexOf(" "));
              date = DateTools.formatDate(date, DATE_FORMATS);
            }
          }
          else if (key.equalsIgnoreCase("history creation date")) {
            date = DateTools.formatDate(value, DATE_FORMATS);
          }
          else if (key.equalsIgnoreCase("history type")) {
            // HACK - support for Gray Institute at Oxford's ICS lifetime data
            if (value.equalsIgnoreCase("time resolved") ||
                value.equalsIgnoreCase("FluorescenceLifetime"))
            {
              lifetime = true;
            }
            experimentType = value;
          }
          else if (key.equalsIgnoreCase("history labels")) {
              // HACK - support for Gray Institute at Oxford's ICS lifetime data
              labels = value;
          }
          else if (getMetadataOptions().getMetadataLevel() !=
                     MetadataLevel.MINIMUM) {

            if (key.equalsIgnoreCase("history") ||
                key.equalsIgnoreCase("history text"))
            {
              textBlock.append(value);
              textBlock.append("\n");
              metadata.remove(key);
            }
            else if (key.startsWith("history gain")) {
              Integer n = 0;
              try {
                n = new Integer(key.substring(12).trim());
                n = new Integer(n.intValue() - 1);
              }
              catch (NumberFormatException e) { }
              if (doubleValue != null) {
                  gains.put(n, doubleValue);
              }
            }
            else if (key.startsWith("history laser") &&
                     key.endsWith("wavelength")) {
              int laser =
                Integer.parseInt(key.substring(13, key.indexOf(" ", 13))) - 1;
              value = value.replaceAll("nm", "").trim();
              try {
                wavelengths.put(new Integer(laser), new Double(value));
              }
              catch (NumberFormatException e) {
                LOGGER.debug("Could not parse wavelength", e);
              }
            }
            else if (key.equalsIgnoreCase("history Wavelength*")) {
              String[] waves = value.split(" ");
              for (int i=0; i<waves.length; i++) {
                wavelengths.put(new Integer(i), new Double(waves[i]));
              }
            }
            else if (key.equalsIgnoreCase("history laser manufacturer")) {
              laserManufacturer = value;
            }
            else if (key.equalsIgnoreCase("history laser model")) {
              laserModel = value;
            }
            else if (key.equalsIgnoreCase("history laser power")) {
              try {
                laserPower = new Double(value); //TODO ARG i.e. doubleValue
              }
              catch (NumberFormatException e) { }
            }
            else if (key.equalsIgnoreCase("history laser rep rate")) {
              String repRate = value;
              if (repRate.indexOf(' ') != -1) {
                repRate = repRate.substring(0, repRate.lastIndexOf(" "));
              }
              laserRepetitionRate = new Double(repRate);
            }
            else if (key.equalsIgnoreCase("history objective type") ||
                     key.equalsIgnoreCase("history objective"))
            {
              objectiveModel = value;
            }
            else if (key.equalsIgnoreCase("history objective immersion")) {
              immersion = value;
            }
            else if (key.equalsIgnoreCase("history objective NA")) {
              lensNA = doubleValue;
            }
            else if (key.equalsIgnoreCase
                       ("history objective WorkingDistance")) {
              workingDistance = doubleValue;
            }
            else if (key.equalsIgnoreCase("history objective magnification") ||
                     key.equalsIgnoreCase("history objective mag"))
            {
              magnification = doubleValue;
            }
            else if (key.equalsIgnoreCase("history camera manufacturer")) {
              detectorManufacturer = value;
            }
            else if (key.equalsIgnoreCase("history camera model")) {
              detectorModel = value;
            }
            else if (key.equalsIgnoreCase("history author") ||
                     key.equalsIgnoreCase("history experimenter"))
            {
              lastName = value;
            }
            else if (key.equalsIgnoreCase("history extents")) {
              String[] lengths = value.split(" ");
              sizes = new double[lengths.length];
              for (int n=0; n<sizes.length; n++) {
                try {
                  sizes[n] = Double.parseDouble(lengths[n].trim());
                }
                catch (NumberFormatException e) {
                  LOGGER.debug("Could not parse axis length", e);
                }
              }
            }
            else if (key.equalsIgnoreCase("history stage_xyzum")) {
              String[] positions = value.split(" ");
              stagePos = new Length[positions.length];
              for (int n=0; n<stagePos.length; n++) {
                try {
                  final Double number = Double.valueOf(positions[n]);
                  stagePos[n] = new Length(number, UNITS.REFERENCEFRAME);
                }
                catch (NumberFormatException e) {
                  LOGGER.debug("Could not parse stage position", e);
                }
              }
            }
            else if (key.equalsIgnoreCase("history stage positionx")) {
              if (stagePos == null) {
                stagePos = new Length[3];
              }
              final Double number = Double.valueOf(value);
              stagePos[0] = new Length(number, UNITS.REFERENCEFRAME);
            }
            else if (key.equalsIgnoreCase("history stage positiony")) {
              if (stagePos == null) {
                stagePos = new Length[3];
              }
              final Double number = Double.valueOf(value);
              stagePos[1] = new Length(number, UNITS.REFERENCEFRAME);
            }
            else if (key.equalsIgnoreCase("history stage positionz")) {
              if (stagePos == null) {
                stagePos = new Length[3];
              }
              final Double number = Double.valueOf(value);
              stagePos[2] = new Length(number, UNITS.REFERENCEFRAME);
            }
            else if (key.equalsIgnoreCase("history other text")) {
              description = value;
            }
            else if (key.startsWith("history step") && key.endsWith("name")) {
              Integer n = new Integer(key.substring(12, key.indexOf(" ", 12)));
              channelNames.put(n, value);
            }
            else if (key.equalsIgnoreCase("history cube")) {
              channelNames.put(new Integer(channelNames.size()), value);
            }
            else if (key.equalsIgnoreCase("history cube emm nm")) {
              if (emWaves == null) {
                emWaves = new Double[1];
              }
              emWaves[0] = new Double(value.split(" ")[1].trim());
            }
            else if (key.equalsIgnoreCase("history cube exc nm")) {
              if (exWaves == null) {
                exWaves = new Double[1];
              }
              exWaves[0] = new Double(value.split(" ")[1].trim());
            }
            else if (key.equalsIgnoreCase("history microscope")) {
              microscopeModel = value;
            }
            else if (key.equalsIgnoreCase("history manufacturer")) {
              microscopeManufacturer = value;
            }
            else if (key.equalsIgnoreCase("history Exposure")) {
              String expTime = value;
              if (expTime.indexOf(' ') != -1) {
                expTime = expTime.substring(0, expTime.indexOf(' '));
              }
              Double expDouble = new Double(expTime);
              if (expDouble != null) {
                exposureTime = new Time(expDouble, UNITS.SECOND);
              }
            }
            else if (key.equalsIgnoreCase("history filterset")) {
              filterSetModel = value;
            }
            else if (key.equalsIgnoreCase("history filterset dichroic name")) {
              dichroicModel = value;
            }
            else if (key.equalsIgnoreCase("history filterset exc name")) {
              excitationModel = value;
            }
            else if (key.equalsIgnoreCase("history filterset emm name")) {
              emissionModel = value;
            }
          }
        }
        // document category
        else if (token0.equals("document")) {
          keyValue = findKeyValue(tokens, DOCUMENT_KEYS);
          String key = keyValue[0];
          String value = keyValue[1];
          addGlobalMeta(key, value);

        }
        // sensor category
        else if (token0.equals("sensor")) {
          keyValue = findKeyValue(tokens, SENSOR_KEYS);
          String key = keyValue[0];
          String value = keyValue[1];
          addGlobalMeta(key, value);

          if (getMetadataOptions().getMetadataLevel() !=
              MetadataLevel.MINIMUM)
          {
            if (key.equalsIgnoreCase("sensor s_params LambdaEm")) {
              String[] waves = value.split(" ");
              emWaves = new Double[waves.length];
              for (int n=0; n<emWaves.length; n++) {
                try {
                  emWaves[n] = new Double(Double.parseDouble(waves[n]));
                }
                catch (NumberFormatException e) {
                  LOGGER.debug("Could not parse emission wavelength", e);
                }
              }
            }
            else if (key.equalsIgnoreCase("sensor s_params LambdaEx")) {
              String[] waves = value.split(" ");
              exWaves = new Double[waves.length];
              for (int n=0; n<exWaves.length; n++) {
                try {
                  exWaves[n] = new Double(Double.parseDouble(waves[n]));
                }
                catch (NumberFormatException e) {
                  LOGGER.debug("Could not parse excitation wavelength", e);
                }
              }
            }
            else if (key.equalsIgnoreCase("sensor s_params PinholeRadius")) {
              String[] pins = value.split(" ");
              int channel = 0;
              for (int n=0; n<pins.length; n++) {
                if (pins[n].trim().equals("")) continue;
                try {
                  pinholes.put(new Integer(channel++), new Double(pins[n]));
                }
                catch (NumberFormatException e) {
                  LOGGER.debug("Could not parse pinhole", e);
                }
              }
            }
          }
        }
        // view category
        else if (token0.equals("view")) {
          keyValue = findKeyValue(tokens, VIEW_KEYS);
          String key = keyValue[0];
          String value = keyValue[1];

          // handle "view view color lib lut Green Fire green", etc.
          if (key.equalsIgnoreCase("view view color lib lut")) {
            int index;
            int redIndex = value.toLowerCase().lastIndexOf("red");
            int greenIndex = value.toLowerCase().lastIndexOf("green");
            int blueIndex = value.toLowerCase().lastIndexOf("blue");
            if (redIndex > 0 && redIndex > greenIndex && redIndex > blueIndex) {
              index = redIndex + "red".length();
            }
            else if (greenIndex > 0 &&
                     greenIndex > redIndex && greenIndex > blueIndex) {
              index = greenIndex + "green".length();
            }
            else if (blueIndex > 0 &&
                     blueIndex > redIndex && blueIndex > greenIndex) {
              index = blueIndex + "blue".length();
            }
            else {
                index = value.indexOf(' ');
            }
            if (index > 0) {
              key = key + ' ' + value.substring(0, index);
              value = value.substring(index + 1);
            }
          }
          // handle "view view color mode rgb set Default Colors" and
          // "view view color mode rgb set blue-green-red", etc.
          else if (key.equalsIgnoreCase("view view color mode rgb set")) {
              int index = value.toLowerCase().lastIndexOf("colors");
              if (index > 0) {
                  index += "colors".length();
              }
              else {
                index = value.indexOf(' ');
              }
              if (index > 0) {
                key = key + ' ' + value.substring(0, index);
                value = value.substring(index + 1);
              }
          }
          addGlobalMeta(key, value);
        }
        else {
          LOGGER.debug("Unknown category " + token0);
        }
      }
      line = reader.readString(NL);
    }
    reader.close();

    hasInstrumentData = emWaves != null || exWaves != null || lensNA != null ||
      stagePos != null || magnification != null || workingDistance != null ||
      objectiveModel != null || immersion != null;

    addGlobalMeta("history text", textBlock.toString());

    LOGGER.info("Populating core metadata");

    m.rgb = false;
    m.dimensionOrder = "XY";

    // find axis sizes

    channelLengths = new Vector<Integer>();
    channelTypes = new Vector<String>();

    int bitsPerPixel = 0;
    for (int i=0; i<axes.length; i++) {
      if (i >= axisLengths.length) break;
      if (axes[i].equals("bits")) {
        bitsPerPixel = axisLengths[i];
        while (bitsPerPixel % 8 != 0) bitsPerPixel++;
        if (bitsPerPixel == 24 || bitsPerPixel == 48) bitsPerPixel /= 3;
      }
      else if (axes[i].equals("x")) {
        m.sizeX = axisLengths[i];
      }
      else if (axes[i].equals("y")) {
        m.sizeY = axisLengths[i];
      }
      else if (axes[i].equals("z")) {
        m.sizeZ = axisLengths[i];
        if (getDimensionOrder().indexOf('Z') == -1) {
          m.dimensionOrder += 'Z';
        }
      }
      else if (axes[i].equals("t")) {
        if (getSizeT() == 0) m.sizeT = axisLengths[i];
        else m.sizeT *= axisLengths[i];
        if (getDimensionOrder().indexOf('T') == -1) {
          m.dimensionOrder += 'T';
        }
      }
      else {
        if (m.sizeC == 0) m.sizeC = axisLengths[i];
        else m.sizeC *= axisLengths[i];
        channelLengths.add(new Integer(axisLengths[i]));
        storedRGB = getSizeX() == 0;
        m.rgb = getSizeX() == 0 && getSizeC() <= 4 && getSizeC() > 1;
        if (getDimensionOrder().indexOf('C') == -1) {
          m.dimensionOrder += 'C';
        }

        if (axes[i].startsWith("c")) {
          channelTypes.add(FormatTools.CHANNEL);
        }
        else if (axes[i].equals("p")) {
          channelTypes.add(FormatTools.PHASE);
        }
        else if (axes[i].equals("f")) {
          channelTypes.add(FormatTools.FREQUENCY);
        }
        else channelTypes.add("");
      }
    }

    if (channelLengths.isEmpty()) {
      channelLengths.add(1);
      channelTypes.add(FormatTools.CHANNEL);
    }

    if (isRGB() && emWaves != null && emWaves.length == getSizeC()) {
      m.rgb = false;
      storedRGB = true;
    }

    m.dimensionOrder =
      MetadataTools.makeSaneDimensionOrder(getDimensionOrder());

    if (getSizeZ() == 0) m.sizeZ = 1;
    if (getSizeC() == 0) m.sizeC = 1;
    if (getSizeT() == 0) m.sizeT = 1;

    // Set up ModuloC.  It appears that for ICS, different channels
    // can have different lengths, which isn't supported by ModuloC
    // which requires all channels have the same length.  This could
    // be rectified by setting SizeC=1 and allowing multiple Modulo
    // annotations per dimension, but would require a model change.
    // Here, ModuloC is only set if all channels are of the same
    // length and type.
    if (channelLengths.size() > 0) {
      int clen0 = channelLengths.get(0);
      String ctype0 = channelTypes.get(0);
      boolean same = true;

      for (Integer len : channelLengths) {
        if (clen0 != len) same = false;
      }
      for (String type : channelTypes) {
        if (!ctype0.equals(type)) same = false;
      }

      if (same) {
        m.moduloC.type = ctype0;
        if (FormatTools.LIFETIME.equals(ctype0)) {
          m.moduloC.parentType = FormatTools.SPECTRA;
        }
        m.moduloC.typeDescription = "TCSPC";
        m.moduloC.start = 0;
        m.moduloC.step = 1;
        m.moduloC.end = clen0 - 1;
      }
    }

    m.interleaved = isRGB();
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;
    m.littleEndian = true;

    // HACK - support for Gray Institute at Oxford's ICS lifetime data
    if (lifetime && labels != null) {
      int binCount = 0;
      String newOrder = null;

      if (labels.equalsIgnoreCase("t x y")) {
        // nominal X Y Z is actually C X Y (which is X Y C interleaved)
        newOrder = "XYCZT";
        m.interleaved = true;
        binCount = m.sizeX;
        m.sizeX = m.sizeY;
        m.sizeY = m.sizeZ;
        m.sizeZ = 1;
      }
      else if (labels.equalsIgnoreCase("x y t")) {
        // nominal X Y Z is actually X Y C
        newOrder = "XYCZT";
        binCount = m.sizeZ;
        m.sizeZ = 1;
      }
      else {
        LOGGER.debug("Lifetime data, unexpected 'history labels' " + labels);
      }

      if (newOrder != null) {
        m.dimensionOrder = newOrder;
        m.sizeC = binCount;
        m.moduloC.parentType = FormatTools.LIFETIME;
      }
    }

    // do not modify the Z, T, or channel counts after this point
    m.imageCount = getSizeZ() * getSizeT();
    if (!isRGB()) m.imageCount *= getSizeC();

    if (byteOrder != null) {
      String firstByte = byteOrder.split(" ")[0];
      int first = Integer.parseInt(firstByte);
      m.littleEndian = rFormat.equals("real") ? first == 1 : first != 1;
    }

    gzip = (compression == null) ? false : compression.equals("gzip");

    if (versionTwo) {
      String s = in.readString(NL);
      while (!s.trim().equals("end")) s = in.readString(NL);
    }

    offset = in.getFilePointer();

    int bytes = bitsPerPixel / 8;

    if (bitsPerPixel < 32) m.littleEndian = !isLittleEndian();

    boolean fp = rFormat.equals("real");
    m.pixelType = FormatTools.pixelTypeFromBytes(bytes, signed, fp);

    LOGGER.info("Populating OME metadata");

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    // populate Image data

    store.setImageName(imageName, 0);

    if (date != null) store.setImageAcquisitionDate(new Timestamp(date), 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(description, 0);

      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setMicroscopeModel(microscopeModel, 0);
      store.setMicroscopeManufacturer(microscopeManufacturer, 0);

      store.setImageInstrumentRef(instrumentID, 0);

      store.setExperimentID(MetadataTools.createLSID("Experiment", 0), 0);
      store.setExperimentType(getExperimentType(experimentType), 0);

      // populate Dimensions data

      if (scales != null) {
        if (units != null && units.length == scales.length - 1) {
          // correct for missing units
          // sometimes, the units for the C axis are missing entirely
          ArrayList<String> realUnits = new ArrayList<String>();
          int unitIndex = 0;
          for (int i=0; i<axes.length; i++) {
            if (axes[i].toLowerCase().equals("ch")) {
              realUnits.add("nm");
            }
            else {
              realUnits.add(units[unitIndex++]);
            }
          }
          units = realUnits.toArray(new String[realUnits.size()]);
        }

        for (int i=0; i<scales.length; i++) {
          Double scale = scales[i];

          if (scale == null) {
            continue;
          }

          String axis = axes != null && axes.length > i ? axes[i] : "";
          String unit = units != null && units.length > i ? units[i] : "";
          if (axis.equals("x")) {
            if (checkUnit(unit, "um", "microns", "micrometers")) {
              Length x = FormatTools.getPhysicalSizeX(scale);
              if (x != null) {
                store.setPixelsPhysicalSizeX(x, 0);
              }
            }
          }
          else if (axis.equals("y")) {
            if (checkUnit(unit, "um", "microns", "micrometers")) {
              Length y = FormatTools.getPhysicalSizeY(scale);
              if (y != null) {
                store.setPixelsPhysicalSizeY(y, 0);
              }
            }
          }
          else if (axis.equals("z")) {
            if (checkUnit(unit, "um", "microns", "micrometers")) {
              Length z = FormatTools.getPhysicalSizeZ(scale);
              if (z != null) {
                store.setPixelsPhysicalSizeZ(z, 0);
              }
            }
          }
          else if (axis.equals("t") && scale != null) {
            if (checkUnit(unit, "ms")) {
              store.setPixelsTimeIncrement(new Time(scale, UNITS.MILLISECOND), 0);
            }
            else if (checkUnit(unit, "seconds") || checkUnit(unit, "s") ) {
              store.setPixelsTimeIncrement(new Time(scale, UNITS.SECOND), 0);
            }
          }
        }
      }
      else if (sizes != null) {
        if (sizes.length > 0) {
          Length x = FormatTools.getPhysicalSizeX(sizes[0]);
          if (x != null) {
            store.setPixelsPhysicalSizeX(x, 0);
          }
        }
        if (sizes.length > 1) {
          sizes[1] /= getSizeY();
          Length y = FormatTools.getPhysicalSizeY(sizes[1]);
          if (y != null) {
            store.setPixelsPhysicalSizeY(y, 0);
          }
        }
      }

      // populate Plane data

      if (timestamps != null) {
        for (int t=0; t<timestamps.length; t++) {
          if (t >= getSizeT()) break; // ignore superfluous timestamps
          if (timestamps[t] == null) continue; // ignore missing timestamp
          Time deltaT = new Time(timestamps[t], UNITS.SECOND);
          if (Double.isNaN(deltaT.value().doubleValue())) continue; // ignore invalid timestamp
          // assign timestamp to all relevant planes
          for (int z=0; z<getSizeZ(); z++) {
            for (int c=0; c<getEffectiveSizeC(); c++) {
              int index = getIndex(z, c, t);
              store.setPlaneDeltaT(deltaT, 0, index);
            }
          }
        }
      }

      // populate LogicalChannel data

      for (int i=0; i<getEffectiveSizeC(); i++) {
        if (channelNames.containsKey(i)) {
          store.setChannelName(channelNames.get(i), 0, i);
        }
        if (pinholes.containsKey(i)) {
          store.setChannelPinholeSize(new Length(pinholes.get(i), UNITS.MICROMETER), 0, i);
        }
        if (emWaves != null && i < emWaves.length) {
          Length em = FormatTools.getEmissionWavelength(emWaves[i]);
          if (em != null) {
            store.setChannelEmissionWavelength(em, 0, i);
          }
        }
        if (exWaves != null && i < exWaves.length) {
          Length ex = FormatTools.getExcitationWavelength(exWaves[i]);
          if (ex != null) {
            store.setChannelExcitationWavelength(ex, 0, i);
          }
        }
      }

      // populate Laser data

      Integer[] lasers = wavelengths.keySet().toArray(new Integer[0]);
      Arrays.sort(lasers);
      for (int i=0; i<lasers.length; i++) {
        store.setLaserID(MetadataTools.createLSID("LightSource", 0, i), 0, i);

        Length wave =
          FormatTools.getWavelength(wavelengths.get(lasers[i]));
        if (wave != null) {
          store.setLaserWavelength(wave, 0, i);
        }
        store.setLaserType(getLaserType("Other"), 0, i);
        store.setLaserLaserMedium(getLaserMedium("Other"), 0, i);

        store.setLaserManufacturer(laserManufacturer, 0, i);
        store.setLaserModel(laserModel, 0, i);
        Power theLaserPower = FormatTools.createPower(laserPower, UNITS.MILLIWATT);
        if (theLaserPower != null) {
          store.setLaserPower(theLaserPower, 0, i);
        }
        Frequency theLaserRepetitionRate = FormatTools.createFrequency(laserRepetitionRate, UNITS.HERTZ);
        if (theLaserRepetitionRate != null) {
          store.setLaserRepetitionRate(theLaserRepetitionRate, 0, i);
        }
      }

      if (lasers.length == 0 && laserManufacturer != null) {
        store.setLaserID(MetadataTools.createLSID("LightSource", 0, 0), 0, 0);
        store.setLaserType(getLaserType("Other"), 0, 0);
        store.setLaserLaserMedium(getLaserMedium("Other"), 0, 0);
        store.setLaserManufacturer(laserManufacturer, 0, 0);
        store.setLaserModel(laserModel, 0, 0);
        Power theLaserPower = FormatTools.createPower(laserPower, UNITS.MILLIWATT);
        if (theLaserPower != null) {
          store.setLaserPower(theLaserPower, 0, 0);
        }
        Frequency theLaserRepetitionRate = FormatTools.createFrequency(laserRepetitionRate, UNITS.HERTZ);
        if (theLaserRepetitionRate != null) {
          store.setLaserRepetitionRate(theLaserRepetitionRate, 0, 0);
        }
      }

      // populate FilterSet data

      if (filterSetModel != null) {
        store.setFilterSetID(MetadataTools.createLSID("FilterSet", 0, 0), 0, 0);
        store.setFilterSetModel(filterSetModel, 0, 0);

        String dichroicID = MetadataTools.createLSID("Dichroic", 0, 0);
        String emFilterID = MetadataTools.createLSID("Filter", 0, 0);
        String exFilterID = MetadataTools.createLSID("Filter", 0, 1);

        store.setDichroicID(dichroicID, 0, 0);
        store.setDichroicModel(dichroicModel, 0, 0);
        store.setFilterSetDichroicRef(dichroicID, 0, 0);

        store.setFilterID(emFilterID, 0, 0);
        store.setFilterModel(emissionModel, 0, 0);
        store.setFilterSetEmissionFilterRef(emFilterID, 0, 0, 0);

        store.setFilterID(exFilterID, 0, 1);
        store.setFilterModel(excitationModel, 0, 1);
        store.setFilterSetExcitationFilterRef(exFilterID, 0, 0, 0);
      }

      // populate Objective data

      if (objectiveModel != null) store.setObjectiveModel(objectiveModel, 0, 0);
      if (immersion == null) immersion = "Other";
      store.setObjectiveImmersion(getImmersion(immersion), 0, 0);
      if (lensNA != null) store.setObjectiveLensNA(lensNA, 0, 0);
      if (workingDistance != null) {
        store.setObjectiveWorkingDistance(new Length(workingDistance, UNITS.MICROMETER), 0, 0);
      }
      if (magnification != null) {
        store.setObjectiveCalibratedMagnification(magnification, 0, 0);
      }
      store.setObjectiveCorrection(getCorrection("Other"), 0, 0);

      // link Objective to Image
      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      store.setObjectiveSettingsID(objectiveID, 0);

      // populate Detector data

      String detectorID = MetadataTools.createLSID("Detector", 0, 0);
      store.setDetectorID(detectorID, 0, 0);
      store.setDetectorManufacturer(detectorManufacturer, 0, 0);
      store.setDetectorModel(detectorModel, 0, 0);
      store.setDetectorType(getDetectorType("Other"), 0, 0);

      for (Integer key : gains.keySet()) {
        int index = key.intValue();
        if (index < getEffectiveSizeC()) {
          store.setDetectorSettingsGain(gains.get(key), 0, index);
          store.setDetectorSettingsID(detectorID, 0, index);
        }
      }

      // populate Experimenter data

      if (lastName != null) {
        String experimenterID = MetadataTools.createLSID("Experimenter", 0);
        store.setExperimenterID(experimenterID, 0);
        store.setExperimenterLastName(lastName, 0);
      }

      // populate StagePosition data

      if (stagePos != null) {
        for (int i=0; i<getImageCount(); i++) {
          if (stagePos.length > 0) {
            store.setPlanePositionX(stagePos[0], 0, i);
            addGlobalMeta("X position for position #1", stagePos[0]);
          }
          if (stagePos.length > 1) {
            store.setPlanePositionY(stagePos[1], 0, i);
            addGlobalMeta("Y position for position #1", stagePos[1]);
          }
          if (stagePos.length > 2) {
            store.setPlanePositionZ(stagePos[2], 0, i);
            addGlobalMeta("Z position for position #1", stagePos[2]);
          }
        }
      }

      if (exposureTime != null) {
        for (int i=0; i<getImageCount(); i++) {
          store.setPlaneExposureTime(exposureTime, 0, i);
        }
      }
    }
  }

  // -- Helper methods --

  /*
   * String tokenizer for parsing metadata. Splits on any white-space
   * characters. Tabs and spaces are often used interchangeably in real-life ICS
   * files.
   *
   * Also splits on 0x04 character which appears in "paul/csarseven.ics" and
   * "paul/gci/time resolved_1.ics".
   *
   * Also respects double quote marks, so that
   *   Modules("Confocal C1 Grabber").BarrierFilter(2)
   * is just one token.
   *
   * If not for the last requirement, the one line
   *   String[] tokens = line.split("[\\s\\x04]+");
   * would work.
   */
  private String[] tokenize(String line) {
    List<String> tokens = new ArrayList<String>();
    boolean inWhiteSpace = true;
    boolean withinQuotes = false;
    StringBuilder token = null;
    for (int i = 0; i < line.length(); ++i) {
      char c = line.charAt(i);
      if (Character.isWhitespace(c) || c == 0x04) {
        if (withinQuotes) {
          // retain white space within quotes
          token.append(c);
        }
        else if (!inWhiteSpace) {
          // put out pending token string
          inWhiteSpace = true;
          if (token.length() > 0) {
            tokens.add(token.toString());
            token = null;
          }
        }
      }
      else {
        if ('"' == c) {
          // toggle quotes
          withinQuotes = !withinQuotes;
        }
        if (inWhiteSpace) {
          inWhiteSpace = false;
          // start a new token string
          token = new StringBuilder();
        }
        // build token string
        token.append(c);
      }
    }
    // put out any pending token strings
    if (null != token && token.length() > 0) {
      tokens.add(token.toString());
    }
    return tokens.toArray(new String[0]);
  }

  /* Given a list of tokens and an array of lists of regular expressions, tries
   * to find a match.  If no match is found, looks in OTHER_KEYS.
   */
  String[] findKeyValue(String[] tokens, String[][] regexesArray) {
    String[] keyValue = findKeyValueForCategory(tokens, regexesArray);
    if (null == keyValue) {
      keyValue = findKeyValueOther(tokens, OTHER_KEYS);
    }
    if (null == keyValue) {
      String key = tokens[0];
      String value = concatenateTokens(tokens, 1, tokens.length);
      keyValue = new String[] { key, value };
    }
    return keyValue;
  }

  /*
   * Builds a string from a list of tokens.
   */
  private String concatenateTokens(String[] tokens, int start, int stop) {
    final StringBuilder returnValue = new StringBuilder();
    for (int i = start; i < tokens.length && i < stop; ++i) {
      returnValue.append(tokens[i]);
      if (i < stop - 1) {
        returnValue.append(' ');
      }
    }
    return returnValue.toString();
  }

  /*
   * Given a list of tokens and an array of lists of regular expressions, finds
   * a match.  Returns key/value pair if matched, null otherwise.
   *
   * The first element, tokens[0], has already been matched to a category, i.e.
   * 'history', and the regexesArray is category-specific.
   */
  private String[] findKeyValueForCategory(String[] tokens,
                                           String[][] regexesArray) {
    String[] keyValue = null;
    int index = 0;
    for (String[] regexes : regexesArray) {
      if (compareTokens(tokens, 1, regexes, 0)) {
        int splitIndex = 1 + regexes.length; // add one for the category
        String key = concatenateTokens(tokens, 0, splitIndex);
        String value = concatenateTokens(tokens, splitIndex, tokens.length);
        keyValue = new String[] { key, value };
        break;
      }
      ++index;
    }
    return keyValue;
  }

  /* Given a list of tokens and an array of lists of regular expressions, finds
   * a match.  Returns key/value pair if matched, null otherwise.
   *
   * The first element, tokens[0], represents a category and is skipped.  Look
   * for a match of a list of regular expressions anywhere in the list of tokens.
   */
  private String[] findKeyValueOther(String[] tokens, String[][] regexesArray) {
    String[] keyValue = null;
    for (String[] regexes : regexesArray) {
      for (int i = 1; i < tokens.length - regexes.length; ++i) {
        // does token match first regex?
        if (tokens[i].toLowerCase().matches(regexes[0])) {
          // do remaining tokens match remaining regexes?
          if (1 == regexes.length || compareTokens(tokens, i + 1, regexes, 1)) {
            // if so, return key/value
            int splitIndex = i + regexes.length;
            String key = concatenateTokens(tokens, 0, splitIndex);
            String value = concatenateTokens(tokens, splitIndex, tokens.length);
            keyValue = new String[] { key, value };
            break;
          }
        }
      }
      if (null != keyValue) {
        break;
      }
    }
    return keyValue;
  }

  /*
   * Compares a list of tokens with a list of regular expressions.
   */
  private boolean compareTokens(String[] tokens, int tokenIndex,
                                String[] regexes, int regexesIndex) {
    boolean returnValue = true;
    int i, j;
    for (i = tokenIndex, j = regexesIndex; j < regexes.length; ++i, ++j) {
      if (i >= tokens.length || !tokens[i].toLowerCase().matches(regexes[j])) {
        returnValue = false;
        break;
      }
    }
    return returnValue;
  }

  /** Splits the given string into a list of {@link Double}s. */
  private Double[] splitDoubles(String v) {
    StringTokenizer t = new StringTokenizer(v);
    Double[] values = new Double[t.countTokens()];
    for (int n=0; n<values.length; n++) {
      String token = t.nextToken().trim();
      try {
        values[n] = new Double(token);
      }
      catch (NumberFormatException e) {
        LOGGER.debug("Could not parse double value '{}'", token, e);
      }
    }
    return values;
  }

  /** Verifies that a unit matches the expected value. */
  private boolean checkUnit(String actual, String... expected) {
    if (actual == null || actual.equals("")) return true; // undefined is OK
    for (String exp : expected) {
      if (actual.equals(exp)) return true; // unit matches expected value
    }
    LOGGER.debug("Unexpected unit '{}'; expected '{}'", actual, expected);
    return false;
  }

}
