/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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

import java.util.Calendar;
import java.util.Date;

import org.joda.time.Instant;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Primitive type that represents an ISO 8601 timestamp.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/src/ome/xml/model/primitives/Timestamp.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/src/ome/xml/model/primitives/Timestamp.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Timestamp extends PrimitiveType<String> {

  /** ISO 8601 date input formatter. */
  public static final DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser().withZone(DateTimeZone.UTC);

  /** ISO 8601 date output formatter with milliseconds. */
  public static final DateTimeFormatter formatms = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

  /** ISO 8601 date output formatter without milliseconds. */
  public static final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

  final Instant timestamp;

  public Timestamp(final String value) throws IllegalArgumentException, UnsupportedOperationException {
    super(null);
    this.timestamp = Instant.parse(value, parser);
    this.value = toString();
  }

  public Timestamp(final Instant instant) {
    super(null);
    this.timestamp = new Instant(instant);
    this.value = toString();
  }

  public Timestamp(final DateTime datetime) {
    super(null);
    this.timestamp = datetime.toInstant();
    this.value = toString();
  }

  public Timestamp(final Date date) {
    super(null);
    this.timestamp = new Instant(date);
    this.value = toString();
  }

  public Timestamp(final Calendar calendar) {
    super(null);
    this.timestamp = new Instant(calendar);
    this.value = toString();
  }

  /**
   * Returns a <code>Timestamp</code> object holding the value of
   * the specified string, or null if parsing failed.
   * @param s The string to be parsed.
   * @return See above.
   */
  public static Timestamp valueOf(String value) {
    Timestamp t = null;
    try {
      t = new Timestamp(value);
    }
    catch (IllegalArgumentException e) {
    }
    catch (UnsupportedOperationException e) {
    }
    return t;
  }

  /**
   * Returns the timestamp as a Joda {@link org.joda.time.DateTime} type.
   * @return See above.
   */
  public Instant asInstant() {
    return timestamp;
  }

  /**
   * Returns the timestamp as a Joda {@link org.joda.time.DateTime} type.
   * @return See above.
   */
  public DateTime asDateTime() {
    return new DateTime(timestamp);
  }

  /**
   * Returns the timestamp as a Java {@link java.util.Date} date type.
   * @return See above.
   */
  public Date asDate() {
    return new DateTime(timestamp).toDate();
  }

  /**
   * Returns the timestamp as a Java {@link java.util.Calendar} date type.
   * @return See above.
   */
  public Calendar asCalendar() {
    return new DateTime(timestamp).toGregorianCalendar();
  }

  /**
   * Returns the timesamp as a Java SQL {@link java.sql.Date} date type.
   * @return See above.
   */
  public java.sql.Date asSqlDate() {
    return new java.sql.Date(new DateTime(timestamp).getMillis());
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (timestamp == null)
        return "";
    if ((timestamp.getMillis() % 1000) != 0)
        return formatms.print(timestamp);
    else
        return format.print(timestamp);
  }

}
