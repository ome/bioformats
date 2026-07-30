/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2026 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.out;

/**
 * The single definition of every private TIFF tag number the NDPI writer
 * emits. Standard TIFF tags come from loci.formats.tiff.IFD.
 */
final class NDPITags {

  // -- Constants --

  /** NDPI format version; always 1. */
  static final int VERSION = 65420;
  /** Objective magnification scaled to the level, or -1/-2 for macro/map. */
  static final int SOURCE_LENS = 65421;
  /** Image-center X position in nanometers. */
  static final int X_POSITION = 65422;
  /** Image-center Y position in nanometers. */
  static final int Y_POSITION = 65423;
  /** Image-center Z position in nanometers. */
  static final int Z_POSITION = 65424;
  /** Index of the tissue region this image belongs to. */
  static final int TISSUE_INDEX = 65425;
  /** Low words of the restart-segment offsets into the JPEG stream. */
  static final int MCU_STARTS = 65426;
  /** Free-text slide reference. */
  static final int REFERENCE = 65427;
  /** Checksum over sampled entropy bytes of selected restart segments. */
  static final int AUTH_CODE = 65428;
  /** High words of the restart-segment offsets into the JPEG stream. */
  static final int MCU_STARTS_HIGH_BYTES = 65432;
  /** Exposure ratio. */
  static final int EXPOSURE_RATIO = 65435;
  /** Red gain multiplier. */
  static final int RED_MULTIPLIER = 65436;
  /** Green gain multiplier. */
  static final int GREEN_MULTIPLIER = 65437;
  /** Blue gain multiplier. */
  static final int BLUE_MULTIPLIER = 65438;
  /** Signed X/Y/Z focus-point triplets. */
  static final int FOCUS_POINTS = 65439;
  /** Signed focus-point region mappings. */
  static final int FOCUS_POINT_REGIONS = 65440;
  /** Capture mode; the RGB writer only emits brightfield (0). */
  static final int CAPTURE_MODE = 65441;
  /** Scanner serial number. */
  static final int SERIAL_NUMBER = 65442;
  /** JPEG quality as a percentage. */
  static final int JPEG_QUALITY = 65444;
  /** Refocus interval in minutes. */
  static final int REFOCUS_INTERVAL = 65445;
  /** Focus offset in nanometers. */
  static final int FOCUS_OFFSET = 65446;
  /** Scanner firmware version. */
  static final int FIRMWARE_VERSION = 65448;
  /** CRLF-separated scanner calibration property map. */
  static final int CALIBRATION = 65449;
  /** Macro-only flag indicating whether the label region is obscured. */
  static final int LABEL_OBSCURED = 65450;
  /** Emission wavelength in nanometers. */
  static final int WAVELENGTH = 65451;
  /** Lamp age in hours. */
  static final int LAMP_AGE = 65453;
  /** Exposure time in microseconds. */
  static final int EXPOSURE_TIME = 65454;
  /** Focus duration in seconds. */
  static final int FOCUS_TIME = 65455;
  /** Scan duration in seconds. */
  static final int SCAN_TIME = 65456;
  /** File-write duration in seconds. */
  static final int WRITE_TIME = 65457;
  /** Fully automatic focus flag. */
  static final int FULLY_AUTO_FOCUS = 65458;
  /** First of the eight consecutive barcode tags. */
  static final int FIRST_BARCODE = 65468;
  /** Last of the eight consecutive barcode tags. */
  static final int LAST_BARCODE = 65475;
  /** Medical-regulation string. */
  static final int MEDICAL_REGULATION = 65476;

  // -- Constructor --

  private NDPITags() { }

}
