//
// TimestampTest.java
//

/*
 * ome.xml.utests
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007-2008 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.utests;

import java.util.Calendar;
import java.util.Date;

import ome.xml.model.primitives.Timestamp;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/TimestampTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/TimestampTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class TimestampTest {

  public static final String TIMESTAMP = "2011-10-20T15:07:14";

  private final Timestamp a = new Timestamp(TIMESTAMP);

  private void assertYMDHMS(Calendar calendar) {
    Assert.assertEquals(calendar.get(Calendar.YEAR), 2011);
    Assert.assertEquals(calendar.get(Calendar.MONTH), 9);  // Zero based
    Assert.assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 20);
    Assert.assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 15);
    Assert.assertEquals(calendar.get(Calendar.MINUTE), 7);
    Assert.assertEquals(calendar.get(Calendar.SECOND), 14);
  }

  private Calendar createCalendar(Date date) {
    Assert.assertNotNull(date);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  @Test
  public void testAsDate() {
    Date date = a.asDate();
    Calendar calendar = createCalendar(date);
    assertYMDHMS(calendar);
  }

  @Test
  public void testAsCalendar() {
    assertYMDHMS(a.asCalendar());
  }

  @Test
  public void testAsSqlDate() {
    java.sql.Date date = a.asSqlDate();
    Calendar calendar = createCalendar(date);
    assertYMDHMS(calendar);
  }

  @Test
  public void testDateConstructor() {
    Timestamp b = new Timestamp(a.asDate());
    Assert.assertEquals(b, a);
    assertYMDHMS(b.asCalendar());
  }

  @Test
  public void testCalendarConstructor() {
    Timestamp b = new Timestamp(a.asCalendar());
    Assert.assertEquals(b, a);
    assertYMDHMS(b.asCalendar());
  }
}
