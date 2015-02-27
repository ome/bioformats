/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.common;

import java.text.FieldPosition;
import java.text.ParsePosition;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalInstantException;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class with convenience methods for working with dates.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/DateTools.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/DateTools.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public final class DateTools {

  // -- Constants --

  /** Timestamp formats. */
  public static final int UNIX = 0;      // January 1, 1970
  public static final int COBOL = 1;     // January 1, 1601
  public static final int MICROSOFT = 2; // December 30, 1899
  public static final int ZVI = 3;
  public static final int ALT_ZVI = 4;

  /** Milliseconds until UNIX epoch. */
  public static final long UNIX_EPOCH = 0;
  public static final long COBOL_EPOCH = 11644473600000L;
  public static final long MICROSOFT_EPOCH = 2209143600000L;
  public static final long ZVI_EPOCH = 2921084975759000L;
  public static final long ALT_ZVI_EPOCH = 2921084284761000L;

  /** ISO 8601 date output formatter with milliseconds. */
  public static final String ISO8601_FORMAT_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS";

  /** ISO 8601 date output formatter without milliseconds. */
  public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  /** Human readable timestamp string */
  public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /** Human readable timestamp filename string */
  public static final String FILENAME_FORMAT = "yyyy-MM-dd_HH-mm-ss";

  /** ISO 8601 date formatter with milliseconds. */
  private static final DateTimeFormatter ISO8601_FORMATTER_MS =
    DateTimeFormat.forPattern(ISO8601_FORMAT_MS);

  /** ISO 8601 date formatter without milliseconds. */
  private static final DateTimeFormatter ISO8601_FORMATTER =
    DateTimeFormat.forPattern(ISO8601_FORMAT);

  /** Human readable timestamp formatter. */
  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
    DateTimeFormat.forPattern(TIMESTAMP_FORMAT);

  /** Human readable timestamp filename formatter. */
  private static final DateTimeFormatter FILENAME_FORMATTER =
    DateTimeFormat.forPattern(FILENAME_FORMAT);

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DateTools.class);

  // -- Constructor --

  private DateTools() { }

  // -- Date handling --

  /**
   * Converts from two-word tick representation to milliseconds.
   * Mainly useful in conjunction with COBOL date conversion.
   */
  public static long getMillisFromTicks(long hi, long lo) {
    long ticks = ((hi << 32) | lo);
    return ticks / 10000; // 100 ns = 0.0001 ms
  }

  /** Converts the given timestamp into an ISO8601 date. */
  public static String convertDate(long stamp, int format) {
    return convertDate(stamp, format, ISO8601_FORMAT);
  }

  /** Converts the given timestamp into a date string with the given format. */
  public static String convertDate(long stamp, int format, String outputFormat)
  {
    return convertDate(stamp, format, outputFormat, false);
  }

  /**
   * Converts the given timestamp into a date string with the given format.
   *
   * If correctTimeZoneForGMT is set, then the timestamp will be interpreted
   * as being relative to GMT and not the local time zone.
   */
  public static String convertDate(long stamp, int format, String outputFormat,
    boolean correctTimeZoneForGMT)
  {
    // see http://www.merlyn.demon.co.uk/critdate.htm for more information on
    // dates than you will ever need (or want)

    long ms = stamp;

    switch (format) {
      case UNIX:
        ms -= UNIX_EPOCH;
        break;
      case COBOL:
        ms -= COBOL_EPOCH;
        break;
      case MICROSOFT:
        ms -= MICROSOFT_EPOCH;
        break;
      case ZVI:
        ms -= ZVI_EPOCH;
        break;
      case ALT_ZVI:
        ms -= ALT_ZVI_EPOCH;
        break;
    }

    final DateTimeFormatter fmt = DateTimeFormat.forPattern(outputFormat);

    try {
      if (correctTimeZoneForGMT) {
        DateTimeZone tz = DateTimeZone.getDefault();
        ms = tz.convertLocalToUTC(ms, false);
      }
    }
    catch (ArithmeticException e) {}
    catch (IllegalInstantException e) {}

    DateTime d = new DateTime(ms, DateTimeZone.UTC);

    return fmt.print(d);
  }

  /**
   * Formats the given date as an ISO 8601 date.
   * Delegates to {@link #formatDate(String, String, boolean)}, with the
   * 'lenient' flag set to false.
   *
   * @param date The date to format as ISO 8601.
   * @param format The date's input format.
   */
  public static String formatDate(String date, String format) {
    return formatDate(date, format, false);
  }

  /**
   * Formats the given date as an ISO 8601 date.
   *
   * @param date The date to format as ISO 8601.
   * @param format The date's input format.
   * @param lenient Whether or not to leniently parse the date.
   */
  public static String formatDate(String date, String format, boolean lenient) {
    if (date == null) return null;
    final DateTimeFormatter parser =
      DateTimeFormat.forPattern(format).withZone(DateTimeZone.UTC);
    Instant timestamp = null;

    try {
      timestamp = Instant.parse(date, parser);
    }
    catch (IllegalArgumentException e) {
      LOGGER.debug("Invalid timestamp '{}'", date);
    }
    catch (UnsupportedOperationException e) {
      LOGGER.debug("Error parsing timestamp '{}'", date, e);
    }

    if (timestamp == null) {
      return null;
    }

    final DateTimeFormatter isoformat;
    if ((timestamp.getMillis() % 1000) != 0) {
      isoformat = ISO8601_FORMATTER_MS;
    }
    else {
      isoformat = ISO8601_FORMATTER;
    }

    return isoformat.print(timestamp);
  }

  /**
   * Formats the given date as an ISO 8601 date.
   * Delegates to {@link #formatDate(String, String[], boolean)}, with the
   * 'lenient' flag set to false.
   *
   * @param date The date to format as ISO 8601.
   * @param formats The date's possible input formats.
   */
  public static String formatDate(String date, String[] formats) {
    return formatDate(date, formats, false);
  }

  /**
   * Formats the given date as an ISO 8601 date.
   *
   * @param date The date to format as ISO 8601.
   * @param formats The date's possible input formats.
   * @param lenient Whether or not to leniently parse the date.
   */
  public static String formatDate(String date, String[] formats,
    boolean lenient)
  {
    for (int i=0; i<formats.length; i++) {
      String result = formatDate(date, formats[i], lenient);
      if (result != null) return result;
    }
    return null;
  }

  /**
   * Converts a string date in the given format to a long timestamp
   * (in Unix format: milliseconds since January 1, 1970).
   */
  public static long getTime(String date, String format) {
    final DateTimeFormatter parser =
      DateTimeFormat.forPattern(format).withZone(DateTimeZone.UTC);
    Instant timestamp = null;
    try {
      timestamp = Instant.parse(date, parser);
    }
    catch (IllegalArgumentException e) {
      LOGGER.debug("Invalid timestamp '{}'", date);
    }
    catch (UnsupportedOperationException e) {
      LOGGER.debug("Error parsing timestamp '{}'", date, e);
    }
    if (timestamp == null) return -1;
    return timestamp.getMillis();
  }

  /**
   * Returns a timestamp for the current timezone in a
   * human-readable locale-independent format ("YYYY-MM-DD HH:MM:SS")
   */
  public static String getTimestamp() {
    return TIMESTAMP_FORMATTER.print(new DateTime());
  }

  /**
   * Returns a timestamp for the current timezone in a format suitable
   * for a filename in a locale-independent format
   * ("YYYY-MM-DD_HH-MM-SS")
   */
  public static String getFileTimestamp() {
    return FILENAME_FORMATTER.print(new DateTime());
  }

}
