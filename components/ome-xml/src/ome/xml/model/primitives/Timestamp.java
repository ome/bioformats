/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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
 * #L%
 */

package ome.xml.model.primitives;

import org.joda.time.Instant;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Primitive type that represents an ISO 8601 timestamp.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/src/ome/xml/model/primitives/Timestamp.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/src/ome/xml/model/primitives/Timestamp.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Timestamp extends PrimitiveType<String> {

  /** ISO 8601 date output format with milliseconds. */
  public static final String ISO8601_FORMAT_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS";

  /** ISO 8601 date output format without milliseconds. */
  public static final String ISO8601_FORMAT_S = "yyyy-MM-dd'T'HH:mm:ss";

  /** ISO 8601 date input formatter. */
  public static final DateTimeFormatter ISO8601_PARSER = ISODateTimeFormat.dateTimeParser().withZone(DateTimeZone.UTC);

  /** ISO 8601 date output formatter with milliseconds. */
  public static final DateTimeFormatter ISO8601_FORMATTER_MS = DateTimeFormat.forPattern(ISO8601_FORMAT_MS);

  /** ISO 8601 date output formatter without milliseconds. */
  public static final DateTimeFormatter ISO8601_FORMATTER_S = DateTimeFormat.forPattern(ISO8601_FORMAT_S);

  /** Logger for this class. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(Timestamp.class);

  final Instant timestamp;

  public Timestamp(final String value) throws IllegalArgumentException, UnsupportedOperationException {
    this.timestamp = Instant.parse(value, ISO8601_PARSER);
    this.value = toString();
  }

  public Timestamp(final Instant instant) {
    this.timestamp = new Instant(instant);
    this.value = toString();
  }

  public Timestamp(final DateTime datetime) {
    this.timestamp = datetime.toInstant();
    this.value = toString();
  }

  /**
   * Returns a <code>Timestamp</code> object holding the value of
   * the specified string, or null if parsing failed.
   * @param s The string to be parsed.
   * @return See above.
   */
  public static Timestamp valueOf(String value) {
    if (value == null)
      return null;
    Timestamp t = null;
    try {
      t = new Timestamp(value);
    }
    catch (IllegalArgumentException e) {
        LOGGER.debug("Invalid timestamp '{}'", value);
    }
    catch (UnsupportedOperationException e) {
        LOGGER.debug("Error parsing timestamp '{}'", value, e);
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
   * @param zone the DateTime instance uses the specified timezone, or the default zone if null.
   * @return See above.
   */
  public DateTime asDateTime(DateTimeZone zone) {
      return new DateTime(timestamp, zone);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (timestamp == null)
        return "";
    if ((timestamp.getMillis() % 1000) != 0)
        return ISO8601_FORMATTER_MS.print(timestamp);
    else
        return ISO8601_FORMATTER_S.print(timestamp);
  }

}
