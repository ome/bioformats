/*
 * #%L
 * Top-level reader and writer APIs
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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertEquals;

import loci.formats.in.MetadataOptions;
import loci.formats.in.DefaultMetadataOptions;
import loci.formats.in.MetadataLevel;


/**
 * Unit tests for {@link loci.formats.in.DefaultMetadataOptions}.
 */
public class DefaultMetadataOptionsTest {

  private MetadataOptions opt;

  @BeforeMethod
  public void setUp() {
    opt = new DefaultMetadataOptions();
  }

  @Test
  public void testMetadataLevel() {
    assertEquals(opt.getMetadataLevel(), MetadataLevel.ALL);
    for (MetadataLevel level: MetadataLevel.values()) {
      opt.setMetadataLevel(level);
      assertEquals(opt.getMetadataLevel(), level);
      assertEquals(
          (new DefaultMetadataOptions(level)).getMetadataLevel(), level
      );
    }
  }

  @Test
  public void testIsValidate() {
    assertFalse(opt.isValidate());
    opt.setValidate(true);
    assertTrue(opt.isValidate());
    opt.setValidate(false);
    assertFalse(opt.isValidate());
  }

}
