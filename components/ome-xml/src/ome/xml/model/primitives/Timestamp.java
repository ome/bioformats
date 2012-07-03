/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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

package ome.xml.model.primitives;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Primitive type that represents an ISO 8601 timestamp.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/src/ome/xml/model/primitives/Timestamp.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/src/ome/xml/model/primitives/Timestamp.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Timestamp extends PrimitiveType<String> {

  /** ISO 8601 date format string. */
  public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  /** Date as a Java type. */
  private final Date asDate;

  /** ISO 8601 date format parser / printer. */
  private final SimpleDateFormat dateFormat =
      new SimpleDateFormat(ISO8601_FORMAT);

  public Timestamp(String value) {
    super(value);
    Date date = new Date();
    try {
      date = dateFormat.parse(value);
    }
    catch (ParseException e) {
    }
    asDate = date;
  }

  public Timestamp(Date date) {
    asDate = date;
    value = dateFormat.format(date);
  }

  public Timestamp(Calendar calendar) {
    asDate = calendar.getTime();
    value = dateFormat.format(asDate);
  }

  /**
   * Returns a <code>Timestamp</code> object holding the value of
   * the specified string.
   * @param s The string to be parsed.
   * @return See above.
   */
  public static Timestamp valueOf(String value) {
    return new Timestamp(value);
  }

  /**
   * Returns the timestamp as a Java {@link java.util.Date} date type.
   * @return See above.
   */
  public Date asDate() {
    return asDate;
  }

  /**
   * Returns the timestamp as a Java {@link java.util.Calendar} date type.
   * @return See above.
   */
  public Calendar asCalendar() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(asDate);
    return calendar;
  }

  /**
   * Returns the timesamp as a Java SQL {@link java.sql.Date} date type.
   * @return See above.
   */
  public java.sql.Date asSqlDate() {
    return new java.sql.Date(asDate.getTime());
  }
}
