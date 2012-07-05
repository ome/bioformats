/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.common;

/**
 * A legacy delegator class for ome.scifio.common.DateTools.
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
  public static final int UNIX = ome.scifio.common.DateTools.UNIX;
  public static final int COBOL = ome.scifio.common.DateTools.COBOL; 
  public static final int MICROSOFT = ome.scifio.common.DateTools.MICROSOFT;
  public static final int ZVI = ome.scifio.common.DateTools.ZVI;
  public static final int ALT_ZVI = ome.scifio.common.DateTools.ALT_ZVI;

  /** Milliseconds until UNIX epoch. */
  public static final long UNIX_EPOCH = ome.scifio.common.DateTools.UNIX_EPOCH;
  public static final long COBOL_EPOCH = ome.scifio.common.DateTools.COBOL_EPOCH;
  public static final long MICROSOFT_EPOCH = ome.scifio.common.DateTools.MICROSOFT_EPOCH;
  public static final long ZVI_EPOCH = ome.scifio.common.DateTools.ZVI_EPOCH;
  public static final long ALT_ZVI_EPOCH = ome.scifio.common.DateTools.ALT_ZVI_EPOCH;
  
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
    return ome.scifio.common.DateTools.getMillisFromTicks(hi, lo);
  }

  /** Converts the given timestamp into an ISO8601 date. */
  public static String convertDate(long stamp, int format) {
    return ome.scifio.common.DateTools.convertDate(stamp, format);
  }

  /** Converts the given timestamp into a date string with the given format. */
  public static String convertDate(long stamp, int format, String outputFormat)
  {
    return ome.scifio.common.DateTools.convertDate(stamp, format, outputFormat);
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
    return ome.scifio.common.DateTools.convertDate(stamp, format, outputFormat, correctTimeZoneForGMT);
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
    return ome.scifio.common.DateTools.formatDate(date, format);
  }

  /**
   * Formats the given date as an ISO 8601 date.
   *
   * @param date The date to format as ISO 8601.
   * @param format The date's input format.
   * @param lenient Whether or not to leniently parse the date.
   */
  public static String formatDate(String date, String format, boolean lenient) {
    return ome.scifio.common.DateTools.formatDate(date, format, lenient);
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
    return ome.scifio.common.DateTools.formatDate(date, formats);
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
    return ome.scifio.common.DateTools.formatDate(date, formats, lenient);
  }

  /**
   * Converts a string date in the given format to a long timestamp
   * (in Unix format: milliseconds since January 1, 1970).
   */
  public static long getTime(String date, String format) {
    return ome.scifio.common.DateTools.getTime(date, format);
  }

}
