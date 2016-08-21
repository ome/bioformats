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


import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ome.xml.model.BinData;
import ome.xml.model.Channel;
import ome.xml.model.Image;
import ome.xml.model.OME;
import ome.xml.model.Pixels;
import ome.xml.model.enums.Compression;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.NonNegativeLong;
import ome.xml.model.primitives.PositiveInteger;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/BaseModelMock.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/BaseModelMock.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class BaseModelMock implements ModelMock {

  private OME ome;

  public static final String GENERAL_ANNOTATION_NAMESPACE =
    "test-ome-InOut201004-namespace";

  public static final Integer SIZE_X = 24;

  public static final Integer SIZE_Y = 24;

  public static final Integer SIZE_Z = 1;

  public static final Integer SIZE_C = 3;

  public static final Integer SIZE_T = 1;

  public static final DimensionOrder DIMENSION_ORDER = DimensionOrder.XYZCT;

  public static final PixelType PIXEL_TYPE = PixelType.UINT16;

  public static final Integer BYTES_PER_PIXEL = 2;

  public static final String PLANE =
    "ZrXEfwslJ9N1nDrbtxxWh4fRHo4w8nZ2N0I74Lgj9oIKN9qrPbBK24z+w+9zYzRQ" +
    "WJXfEwwAKXgV4Z1jCPhE9woGjJaarHTsFwy21nF2IoJDkd3L/zSWMSVk508+jpxV" +
    "4t5p93HE1uE4K34WCVEeeZ1dSRli/b7/6RhF56DjdB6KboGly3zuN2/eZt9uJ2Mh" +
    "HZtktzpjFtn2mhf4i7iggpQyWx74xvFs9VxXQf1QoxN1KcTGXbdfPNoj3qmzz7Wm" +
    "8/iXXw7wpOrC2MRcbt98VH4UaQxFgu6VPer73JAS+r2Kd2C67ZFbweyR/LCoUiic" +
    "h866SrwJk3IrTD9AlnGO6SjHIz27yWVh1omr36H1qOuD4ULSknM2txm4FrB02gxH" +
    "WHbgaJWGT02eT1nwGNXygHe7gdYVP8o6Ms9sT/nBwhoMK8NuQINx7KJP/jTP0p5g" +
    "NjEHZeAN1To9Qp3AF3jaWK2671Dyy/l9BBRMhD3gEqXJ12ZXZ0par2pvqVtMcbpA" +
    "Zk96GKsSWDQP48yDkNYTG7RDBMzRJxiem7eifg1gpUP1rmmaNEu12+0wclsGBUeH" +
    "1d9HiN+rDnppycrVQIgvKbXKlUkQH230IYHDESKnlLCZALLJuRuAT5qsNri5950O" +
    "lphUxeYAnNfUkXYRUHGGnGXw58nmnBCp7iuHDC8AJdCRyK+0wk/xtt6EeADkPs9Q" +
    "q90H2kXvvGVbcL03IV1mb0PkdqWg2ovrkSLXKhLXb65ruPPz43TAT9xv4QJdmFqJ" +
    "baMHta8Wd1Fs9cffChHWJT3RS9U8VrhGlBB5+1D9PMlqLruYtp7ulUpMSJFOKkbo" +
    "yXoECSzJuzknqP2Cj1KWrNk+gSsnAlq5zko6KUyPXWMBVgPGNrXR+ivtIXmyQGu5" +
    "jSTuA+S+ogaPraRPQELmmuQ2wcoWI7O9Vpht1tFmgXkrdqCTD7+JwdXlbHSoRz3t" +
    "i9dpJY+LyKBisuKcDgdxWulwtydNliNSKKyt7qGC2B90VLo+XsYLLEYU+w95l2ZO" +
    "umqBquStdKntlReWtCDu8HfbK6AryfZXL5hqMTdqFubcXl4n5ZfBNtHaru8/LswN" +
    "VGua9VJUsvZV9rMniNwoU7Ev+oLc/0SZkJrwL/r+9Jl5k02DRymhE4XISJ3UXcnt" +
    "2K57w/OmIJK3HzznrIXgPJA9Nq7M6XjXDDXuBF08709iSEfOWWZ0Yz5ySoszOlSO" +
    "0OGoRYv8X9xUeOfWi4oizQeSOj2ZTXegqZLxj/g8Y7ykyDkG4NsMS0Kx2fZvxqKE" +
    "9EdUAXMvDN09X0fKdurqYqPBsRq79Id8YIJhamEP969OjHs9VXIETMmCkoUz2//7" +
    "BfeaCUzv5c61/asdOR6CJ4ANUX7hQA7hlTk8qllaaLIEWQyGeaDoaw9b5xq0Adhw" +
    "OZSeCKNIyQVpApdCOnXYuZVoTBNDdW7/7OPZD2uyS9gZ+7JGmuoV9/gRZT72oAQs" +
    "4++/GpC5h6uOx9Rt5265siOZjfYYX++/qUX8M5Fs9whPwL8NqrJ4qZrUbTYUzQaI";

  /** XML namespace. */
  public static final String XML_NS =
    "http://www.openmicroscopy.org/Schemas/OME/2010-06";

  /** XSI namespace. */
  public static final String XSI_NS =
    "http://www.w3.org/2001/XMLSchema-instance";

  /** XML schema location. */
  public static final String SCHEMA_LOCATION =
    "http://www.openmicroscopy.org/Schemas/OME/2010-06/ome.xsd";

  public BaseModelMock() {
    ome = new OME();
    ome.addImage(makeImage(1));
  }

  public OME getRoot() {
    return ome;
  }

  private Image makeImage(int index) {
    // Create <Image/>
    Image image = new Image();
    image.setID("Image:" + index);
    // Create <Pixels/>
    Pixels pixels = new Pixels();
    pixels.setID("Pixels:" + index);
    pixels.setSizeX(new PositiveInteger(SIZE_X));
    pixels.setSizeY(new PositiveInteger(SIZE_Y));
    pixels.setSizeZ(new PositiveInteger(SIZE_Z));
    pixels.setSizeC(new PositiveInteger(SIZE_C));
    pixels.setSizeT(new PositiveInteger(SIZE_T));
    pixels.setDimensionOrder(DIMENSION_ORDER);
    pixels.setType(PIXEL_TYPE);

    // Create <BinData/>
    for (int i = 0; i < SIZE_Z * SIZE_C * SIZE_T; i++) {
      BinData binData = new BinData();
      binData.setBigEndian(false);
      binData.setCompression(Compression.NONE);
      binData.setLength(new NonNegativeLong(
          (long) (SIZE_X * SIZE_Y * BYTES_PER_PIXEL)));
      pixels.addBinData(binData);
    }
    // Create <Channel/> under <Pixels/>
    for (int i = 0; i < SIZE_C; i++) {
      Channel channel = new Channel();
      channel.setID("Channel:" + i);
      pixels.addChannel(channel);
    }
    // Put <Pixels/> under <Image/>
    image.setPixels(pixels);
    return image;
  }

  public static void main(String[] args) throws Exception {
    BaseModelMock mock = new BaseModelMock();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    Document document = parser.newDocument();
    // Produce a valid OME DOM element hierarchy
    Element root = mock.ome.asXMLElement(document);
    SPWModelMock.postProcess(root, document, true);
    // Produce string XML
    OutputStream outputStream = new FileOutputStream(args[0]);
    outputStream.write(SPWModelMock.asString(document).getBytes());
  }

}
