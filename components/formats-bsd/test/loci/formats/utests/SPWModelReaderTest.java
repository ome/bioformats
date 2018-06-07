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

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import loci.formats.ChannelFiller;
import loci.formats.ChannelSeparator;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MinMaxCalculator;
import loci.formats.meta.IMetadata;
import loci.formats.ome.OMEXMLMetadataImpl;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class SPWModelReaderTest {

  private SPWModelMock mock;
  
  private SPWModelMock mockWithNoLightSources;

  private File temporaryFile;

  private File temporaryFileWithNoLightSources;
  
  private IFormatReader reader;

  private IFormatReader readerWithNoLightSources;

  private IMetadata metadata;

  private IMetadata metadataWithNoLightSources;

  @BeforeClass
  public void setUp() throws Exception {
    mock = new SPWModelMock(true);
    mockWithNoLightSources = new SPWModelMock(false);
    temporaryFile = File.createTempFile(this.getClass().getName(), ".ome");
    temporaryFileWithNoLightSources = 
      File.createTempFile(this.getClass().getName(), ".ome");
    writeMockToFile(mock, temporaryFile, true);
    writeMockToFile(mockWithNoLightSources, temporaryFileWithNoLightSources,
                    true);
  }

  /**
   * Writes a model mock to a file as XML.
   * @param mock Mock to build a DOM tree of and serialize to XML.
   * @param file File to write serialized XML to.
   * @param withBinData Whether or not to do BinData post processing.
   * @throws Exception If there is an error writing the XML to the file.
   */
  public static void writeMockToFile(ModelMock mock, File file,
  boolean withBinData) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    Document document = parser.newDocument();
    // Produce a valid OME DOM element hierarchy
    Element root = mock.getRoot().asXMLElement(document);
    SPWModelMock.postProcess(root, document, withBinData);
    // Write the OME DOM to the requested file
    OutputStream stream = new FileOutputStream(file);
    stream.write(SPWModelMock.asString(document).getBytes());
  }

  @AfterClass
  public void tearDown() throws Exception {
    temporaryFile.delete();
    temporaryFileWithNoLightSources.delete();
  }

  @Test
  public void testSetId() throws Exception {
    reader = new MinMaxCalculator(new ChannelSeparator(
        new ChannelFiller(new ImageReader())));
    metadata = new OMEXMLMetadataImpl();
    reader.setMetadataStore(metadata);
    reader.setId(temporaryFile.getAbsolutePath());
  }

  @Test
  public void testSetIdWithNoLightSources() throws Exception {
    readerWithNoLightSources = new MinMaxCalculator(new ChannelSeparator(
        new ChannelFiller(new ImageReader())));
    metadataWithNoLightSources = new OMEXMLMetadataImpl();
    readerWithNoLightSources.setMetadataStore(metadataWithNoLightSources);
    readerWithNoLightSources.setId(
      temporaryFileWithNoLightSources.getAbsolutePath());
  }

  @Test(dependsOnMethods={"testSetId"})
  public void testSeriesCount() {
    assertEquals(384, reader.getSeriesCount());
  }

  @Test(dependsOnMethods={"testSetId"})
  public void testCanReadEveryPlane() throws Exception {
    assertTrue(canReadEveryPlane(reader));
  }

  @Test(dependsOnMethods={"testSetIdWithNoLightSources"})
  public void testCanReadEveryPlaneWithNoLightSources() throws Exception {
    assertTrue(canReadEveryPlane(readerWithNoLightSources));
  }

  /**
   * Checks to see if every plane of an initialized reader can be read.
   * @param reader Reader to read all planes from.
   * @return <code>true</code> if all planes can be read, <code>false</code>
   * otherwise.
   * @throws Exception If there is an error reading data.
   */
  public static boolean canReadEveryPlane(IFormatReader reader)
  throws Exception {
    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    int pixelType = reader.getPixelType();
    int bytesPerPixel = getBytesPerPixel(pixelType);
    byte[] buf = new byte[sizeX * sizeY * bytesPerPixel];
    for (int i = 0; i < reader.getSeriesCount(); i++)
    {
      reader.setSeries(i);
      for (int j = 0; j < reader.getImageCount(); j++)
      {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    "Required SHA-1 message digest algorithm unavailable.");
        }
        buf = reader.openBytes(j, buf);
        try {
          md.update(buf);
        } catch (Exception e) {
          // This better not happen. :)
          throw new RuntimeException(e);
        }
      }
    }
    return true;
  }

  @Test(dependsOnMethods={"testSetId"})
  public void testHasLightSources() {
    assertEquals(1, metadata.getInstrumentCount());
    assertEquals(5, metadata.getLightSourceCount(0));
  }

  @Test(dependsOnMethods={"testSetIdWithNoLightSources"})
  public void testHasNoLightSources() {
    assertEquals(1, metadataWithNoLightSources.getInstrumentCount());
    assertEquals(0, metadataWithNoLightSources.getLightSourceCount(0));
  }

  /**
   * Retrieves how many bytes per pixel the current plane or section has.
   * @return the number of bytes per pixel.
   */
  public static int getBytesPerPixel(int type) {
    switch(type) {
    case 0:
    case 1:
      return 1;  // INT8 or UINT8
    case 2:
    case 3:
      return 2;  // INT16 or UINT16
    case 4:
    case 5:
    case 6:
      return 4;  // INT32, UINT32 or FLOAT
    case 7:
      return 8;  // DOUBLE
    }
    throw new RuntimeException("Unknown type with id: '" + type + "'");
  }

}
