/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import static org.testng.AssertJUnit.*;

import java.util.ArrayList;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.out.JPEG2000Writer;
import loci.formats.services.OMEXMLService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case which outlines the problems seen in omero:#6728.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/SixteenBitLosslessJPEG2000Test.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/SixteenBitLosslessJPEG2000Test.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert <melissa at glencoesoftware.com>
 */
public class SixteenBitLosslessJPEG2000Test {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(SixteenBitLosslessJPEG2000Test.class);

  @Test
  public void testLosslessPixels() throws Exception {
    int failureCount = 0;
    for (short v=Short.MIN_VALUE; v<Short.MAX_VALUE; v++) {
      int index = v + Short.MAX_VALUE + 1;
      byte[] pixels = DataTools.shortToBytes(v, false);

      String file = index + ".jp2";
      ByteArrayHandle tmpFile = new ByteArrayHandle(1);
      Location.mapFile(file, tmpFile);

      IMetadata metadata16;

      try {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        metadata16 = service.createOMEXMLMetadata();
      }
      catch (DependencyException exc) {
        throw new FormatException("Could not create OME-XML store.", exc);
      }
      catch (ServiceException exc) {
        throw new FormatException("Could not create OME-XML store.", exc);
      }

      MetadataTools.populateMetadata(metadata16, 0, "foo", false, "XYCZT",
        "uint16", 1, 1, 1, 1, 1, 1);
      IFormatWriter writer16 = new JPEG2000Writer();
      writer16.setMetadataRetrieve(metadata16);
      writer16.setId(file);
      writer16.saveBytes(0, pixels);
      writer16.close();

      byte[] buf = tmpFile.getBytes();
      byte[] realData = new byte[(int) tmpFile.length()];
      System.arraycopy(buf, 0, realData, 0, realData.length);
      tmpFile.close();
      tmpFile = new ByteArrayHandle(realData);
      Location.mapFile(file, tmpFile);

      ImageReader reader = new ImageReader();
      reader.setId(file);
      byte[] plane = reader.openBytes(0);
      for (int q=0; q<plane.length; q++) {
        if (plane[q] != pixels[q]) {
          LOGGER.debug("FAILED on {}",
            DataTools.bytesToShort(pixels, false));
          failureCount++;
          break;
        }
      }
      reader.close();
      tmpFile.close();

      Location.mapFile(file, null);
    }
    assertEquals(failureCount, 0);
  }

}
