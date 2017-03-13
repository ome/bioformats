/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2016 Open Microscopy Environment:
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

package loci.formats.utests;

import loci.formats.ImageReader;
import loci.formats.in.MetadataOptions;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataLevel;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class ImageReaderTest {

  public static final String KEY = "test.option";
  public static final String VALUE = "foo";

  @DataProvider(name = "levels")
  public Object[][] createLevels() {
    return new Object[][] {
      {MetadataLevel.MINIMUM},
      {MetadataLevel.NO_OVERLAYS},
      {MetadataLevel.ALL}
    };
  }

  @Test
  public void testOptionsExplicit() throws Exception {
    DynamicMetadataOptions opt = new DynamicMetadataOptions();
    opt.set(KEY, VALUE);
    ImageReader reader = new ImageReader();
    reader.setMetadataOptions(opt);
    reader.setId("test.fake");
    MetadataOptions rOpt = reader.getReader().getMetadataOptions();
    assertTrue(rOpt instanceof DynamicMetadataOptions);
    String v = ((DynamicMetadataOptions) rOpt).get(KEY);
    assertNotNull(v);
    assertEquals(v, VALUE);
    reader.close();
  }

  @Test(dataProvider = "levels")
  public void testOptionsImplicit(MetadataLevel level) throws Exception {
    ImageReader reader = new ImageReader();
    reader.getMetadataOptions().setMetadataLevel(level);
    reader.setId("test.fake");
    MetadataLevel rLevel =
      reader.getReader().getMetadataOptions().getMetadataLevel();
    assertEquals(rLevel, level);
    reader.close();
  }

}
