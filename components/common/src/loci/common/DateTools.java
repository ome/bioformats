//
// DateTools.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Chris Allan.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.common;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
  public static final int UNIX = 0;  // January 1, 1970
  public static final int COBOL = 1;  // January 1, 1601
  public static final int MICROSOFT = 2; // December 30, 1899
  public static final int ZVI = 3;

  /** Milliseconds until UNIX epoch. */
  public static final long UNIX_EPOCH = 0;
  public static final long COBOL_EPOCH = 11644473600000L;
  public static final long MICROSOFT_EPOCH = 2209143600000L;
  public static final long ZVI_EPOCH = 2921084975759000L;

  /** ISO 8601 date format string. */
  public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

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
    }

    SimpleDateFormat fmt = new SimpleDateFormat(outputFormat);
    if (correctTimeZoneForGMT) {
      TimeZone tz = TimeZone.getDefault();
      ms -= tz.getOffset(ms);
    }
    StringBuffer sb = new StringBuffer();

    Date d = new Date(ms);

    fmt.format(d, sb, new FieldPosition(0));
    return sb.toString();
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
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    sdf.setLenient(lenient);
    Date d = sdf.parse(date, new ParsePosition(0));
    if (d == null) return null;
    sdf = new SimpleDateFormat(ISO8601_FORMAT);
    return sdf.format(d);
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
    SimpleDateFormat f = new SimpleDateFormat(format);
    Date d = f.parse(date, new ParsePosition(0));
    if (d == null) return -1;
    return d.getTime();
  }

}
