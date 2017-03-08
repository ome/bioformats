/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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
/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2014 - 2016 Open Microscopy Environment. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;

import loci.common.services.ServiceFactory;
import loci.formats.ImageReader;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.out.OMETiffWriter;
import loci.formats.services.OMEXMLService;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

//Java imports

/**
 * Tests that the correct reader is used to read an OME-TIFF file with unicode
 * characters.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @since 5.0
 */
public class OMETiffWriterUnicodeTest {

    public static final int SIZE_X = 100;

    public static final int SIZE_Y = 4;

    public static final int SIZE_Z = 100;

    public static final int SIZE_C = 1;

    public static final int SIZE_T = 20;

    private static final byte[] buf = new byte[] {
      0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
      0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15
    };

    private File target;

    private OMEXMLMetadata ms;

    @BeforeClass
    public void setUp() throws Exception {
      target = File.createTempFile("OMETiffWriterUnicodeTest", ".ome.tiff");

      ServiceFactory sf = new ServiceFactory();
      OMEXMLService service = sf.getInstance(OMEXMLService.class);
      ms = service.createOMEXMLMetadata();
      ms.setImageID("Image:1", 0);
      ms.setPixelsID("Pixels:1", 0);
      ms.setPixelsDimensionOrder(DimensionOrder.XYZCT, 0);
      ms.setPixelsSizeX(new PositiveInteger(SIZE_X), 0);
      ms.setPixelsSizeY(new PositiveInteger(SIZE_Y), 0);
      ms.setPixelsSizeZ(new PositiveInteger(SIZE_Z), 0);
      ms.setPixelsSizeC(new PositiveInteger(SIZE_C), 0);
      ms.setPixelsSizeT(new PositiveInteger(SIZE_T), 0);
      ms.setPixelsPhysicalSizeX(new Length(10, UNITS.MICROMETER), 0);
      ms.setPixelsPhysicalSizeX(new Length(10, UNITS.MICROMETER), 0);
      ms.setPixelsPhysicalSizeX(new Length(10, UNITS.MICROMETER), 0);
      ms.setPixelsType(PixelType.UINT8, 0);
      ms.setPixelsBinDataBigEndian(true, 0, 0);
      ms.setChannelID("Channel:1", 0, 0);
      ms.setChannelSamplesPerPixel(new PositiveInteger(1), 0, 0);
    }

    @AfterClass
    public void tearDown() throws Exception {
      target.delete();
    }

    @Test
    public void testImageWidthWrittenCorrectly() throws Exception {
      OMETiffWriter writer = new OMETiffWriter();
      writer.setMetadataRetrieve(ms);
      writer.setId(target.getAbsolutePath());
      writer.saveBytes(0, buf, 0, 0, buf.length, 1);
      writer.close();
      ImageReader reader = new ImageReader();
      reader.setId(target.getAbsolutePath());
      assertEquals(reader.getFormat(), "OME-TIFF");
      reader.close();
    }
}
