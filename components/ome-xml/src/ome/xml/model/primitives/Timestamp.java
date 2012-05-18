//
// Timestamp.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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
  private Date asDate;

  public Timestamp(String value) {
    super(value);
    SimpleDateFormat dateFormat = new SimpleDateFormat(ISO8601_FORMAT);
    try {
      asDate = dateFormat.parse(value);
    }
    catch (ParseException e) {
      throw new RuntimeException(e);
    }
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
