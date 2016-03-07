/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

package ome.xml.utests;

import org.joda.time.Instant;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import ome.xml.model.primitives.Timestamp;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 */
public class TimestampTest {

  public static final String TIMESTAMP = "2011-10-20T15:07:14.732Z";

  private final Timestamp a = new Timestamp(TIMESTAMP);

  private void assertYMDHMSS(Instant i) {
    LocalDateTime lt = new LocalDateTime(i);
    Assert.assertEquals(lt.getYear(), 2011);
    Assert.assertEquals(lt.getMonthOfYear(), 10);
    Assert.assertEquals(lt.getDayOfMonth(), 20);
    Assert.assertEquals(lt.getHourOfDay(), 15);
    Assert.assertEquals(lt.getMinuteOfHour(), 7);
    Assert.assertEquals(lt.getSecondOfMinute(), 14);
    Assert.assertEquals(lt.getMillisOfSecond(), 732);
  }

  @Test
  public void testAsInstant()
  {
    Instant i = a.asInstant();
    assertYMDHMSS(i);
  }

  @Test
  public void testAsDateTime()
  {
    DateTime d = a.asDateTime(null);
    assertYMDHMSS(d.toInstant());
  }

  @Test
  public void testInstantConstructor() {
    Timestamp b = new Timestamp(a.asInstant());
    Assert.assertEquals(b, a);
    assertYMDHMSS(b.asInstant());
  }

  @Test
  public void testDateTimeConstructor() {
    Timestamp b = new Timestamp(a.asDateTime(null));
    Assert.assertEquals(b, a);
    assertYMDHMSS(b.asInstant());
  }

  @Test
  public void testString() {
    Timestamp t1 = Timestamp.valueOf("2003-08-26T19:46:38");
    Timestamp t2 = Timestamp.valueOf("2003-08-26T19:46:38.762");
    Timestamp t3 = Timestamp.valueOf("2003-08-26T19:46:38.762Z");
    Timestamp t4 = Timestamp.valueOf("2003-08-26T19:46:38.762+0400");
    Timestamp t5 = Timestamp.valueOf("invalid");
    Timestamp t6 = Timestamp.valueOf("2011-10-20T15:07:14");
    Timestamp t7 = Timestamp.valueOf("2011-10-20T15:07:14Z");
    Timestamp t8 = Timestamp.valueOf("2011-10-20T15:07:14.632Z");

    Assert.assertEquals(t1.toString(), "2003-08-26T19:46:38");
    Assert.assertEquals(t2.toString(), "2003-08-26T19:46:38.762");
    Assert.assertEquals(t3.toString(), "2003-08-26T19:46:38.762");
    Assert.assertEquals(t4.toString(), "2003-08-26T15:46:38.762");
    Assert.assertEquals(t5, null);
    Assert.assertEquals(t6.toString(), "2011-10-20T15:07:14");
    Assert.assertEquals(t7.toString(), "2011-10-20T15:07:14");
    Assert.assertEquals(t8.toString(), "2011-10-20T15:07:14.632");
  }

}
