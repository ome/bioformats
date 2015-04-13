/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2015 Open Microscopy Environment:
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

package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import loci.common.DateTools;

/**
 * Unit tests for {@link loci.common.DateTools}.
 */
public class DateToolsTest {

  String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @DataProvider(name = "times_no_ms")
  public Object[][] createTimes() {
    return new Object[][] {
      {"1970-01-01 00:00:01", 1000L},
      {"1970-01-01 00:01:00", 60000L},
      {"1970-01-01 01:00:00", 3600000L},
      {"1970-01-02 00:00:00", 86400000L},
    };
  }

  @DataProvider(name = "times_with_ms")
  public Object[][] createTimesMs() {
    return new Object[][] {
      {"1970-01-01 00:00:00:1", 1L, 100L},
      {"1970-01-01 00:00:00:10", 10L, 100L},
      {"1970-01-01 00:00:00:100", 100L, 100L},
      {"1970-01-01 00:00:00:010", 10L, 10L},
      {"1970-01-01 00:00:00:001", 1L, 1L},
    };
  }

  @DataProvider(name = "times_ms_separators")
  public Object[][] createTimesSeparators() {
    return new Object[][] {
      {"1970-01-01 00:00:00:10", 10L, ":"},
      {"1970-01-01 00:00:00.10", 10L, "."},
      {"1970-01-01 00:00:00-10", 10L, "-"},
    };
  }

  @Test(dataProvider = "times_no_ms")
  public void testGetTime(String date, long ms) {
    assertEquals(ms, DateTools.getTime(date, DATE_FORMAT));
  }

  @Test()
  public void testGetTimeInvalid() {
    assertEquals(-1, DateTools.getTime("wrongdate", DATE_FORMAT));
  }

  @Test(dataProvider = "times_with_ms")
  public void testGetTimeMs(String date, long ms1, long ms2) {
    assertEquals(ms1, DateTools.getTime(date, DATE_FORMAT, ":"));
    assertEquals(ms2, DateTools.getTime(date, DATE_FORMAT + ":SSS"));
  }

  @Test(dataProvider = "times_ms_separators")
  public void testGetTimeSeparators(String date, long ms, String separator) {
    assertEquals(ms, DateTools.getTime(date, DATE_FORMAT, separator));
  }

  @Test
  public void testGetTimeSeparatorNoMs() {
    assertEquals(0L, DateTools.getTime(
      "1970-01-01 00:00:00", DATE_FORMAT, "."));
  }

  @Test
  public void testGetTimeConflictingSeparator() {
    assertEquals(-1, DateTools.getTime(
      "1970-01-01 00:00:00", DATE_FORMAT, ":"));
  }
}
