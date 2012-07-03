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
