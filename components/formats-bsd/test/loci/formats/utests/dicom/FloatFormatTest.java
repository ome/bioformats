/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2026 Open Microscopy Environment:
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

package loci.formats.utests.dicom;

import static org.testng.AssertJUnit.assertEquals;

import loci.formats.out.DicomWriter;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 */
public class FloatFormatTest {

  @DataProvider(name = "values")
  public Object[][] values() {
    double[] d = getDoubleValues();
    String[] s = getStringValues();
    Object[][] rtn = new Object[d.length][2];
    for (int i=0; i<rtn.length; i++) {
      rtn[i][0] = d[i];
      rtn[i][1] = s[i];
    }
    return rtn;
  }

  private double[] getDoubleValues() {
    return new double[] {
      0,
      1.1,
      0.11,
      0.1133408781152648,
      -0.1133408781152648,
      0.01133408781152648,
      -0.01133408781152648,
      0.001133408781152648,
      -0.001133408781152648,
      0.0001133408781152648,
      -0.0001133408781152648,
      0.00001133408781152648,
      -0.00001133408781152648,
      0.000001133408781152648,
      -0.000001133408781152648,
      0.000000000001133408781152648,
      -0.000000000001133408781152648,
      113340878115264.8,
      -113340878115264.8,
      1133408781152648.0,
      -1133408781152648.0,
      .012624143592677,
      99999.999,
      -99999.999,
      99999.999999999999999,
      -99999.999999999999999,
      Double.NEGATIVE_INFINITY,
      Double.POSITIVE_INFINITY,
      Double.NaN,
      Double.MAX_VALUE,
      Double.MIN_VALUE,
      Float.MAX_VALUE,
      Float.MIN_VALUE,
      Long.MAX_VALUE,
      Long.MIN_VALUE,
      Integer.MAX_VALUE,
      Integer.MIN_VALUE,
      Short.MAX_VALUE,
      Short.MIN_VALUE,
      // see https://github.com/ome/bioformats/issues/4376
      0.45641259698767683
    };
  }

  private String[] getStringValues() {
    return new String[] {
      "0",
      "1.1",
      ".11",
      ".113340878115265",
      "-.11334087811526",
      ".011334087811526",
      "-.01133408781153",
      ".001133408781153",
      "-.00113340878115",
      ".000113340878115",
      "-.00011334087812",
      ".000011334087812",
      "-.00001133408781",
      ".113340878115E-5",
      "-.11334087812E-5",
      ".11334087812E-11",
      "-.1133408781E-11",
      "113340878115265",
      "-113340878115265",
      "1133408781152648",
      "-.11334087812E16",
      ".012624143592677",
      "99999.999",
      "-99999.999",
      "100000",
      "-100000",
      "-Infinity",
      "+Infinity",
      "NaN",
      ".17976931349E309",
      ".49E-323",
      ".340282346639E39",
      ".14012984643E-44",
      ".922337203685E19",
      "-.92233720369E19",
      "2147483647",
      "-2147483648",
      "32767",
      "-32768",
      ".456412596987677"
    };
  }

  @Test(dataProvider = "values")
  public void testFormat(double v, String expected) {
    assertEquals(DicomWriter.formatFixedWidth(v, 16), expected);
  }

}
