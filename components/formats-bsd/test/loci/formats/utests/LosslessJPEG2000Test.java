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

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Test case which outlines the problems seen in omero:#6728.
 *
 * @author Melissa Linkert <melissa at glencoesoftware.com>
 */
public class LosslessJPEG2000Test {

  private final List<Path> temporaryFiles = new ArrayList<Path>();

  private static final byte[] PIXELS_8 = new byte[] {119};
  private static final byte[] PIXELS_16 = new byte[] {0, 119};

  private static final String FILE_8 = "8.jp2";
  private static final String FILE_16 = "16.jp2";

  @BeforeMethod(alwaysRun = true)
  public void setUp() throws Exception {
    Path temp8Path = Files.createTempFile("test", ".jp2");
    // Retain each fixture before the remaining setup can fail.
    temporaryFiles.add(temp8Path);
    Path temp16Path = Files.createTempFile("test", ".jp2");
    temporaryFiles.add(temp16Path);
    File temp8 = temp8Path.toFile();
    File temp16 = temp16Path.toFile();
    Location.mapId(FILE_8, temp8.getAbsolutePath());
    Location.mapId(FILE_16, temp16.getAbsolutePath());

    IMetadata metadata8;

    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      metadata8 = service.createOMEXMLMetadata();
    }
    catch (DependencyException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }
    catch (ServiceException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }

    MetadataTools.populateMetadata(metadata8, 0, "foo", false, "XYCZT",
      "uint8", 1, 1, 1, 1, 1, 1);
    try (IFormatWriter writer8 = new JPEG2000Writer()) {
      writer8.setMetadataRetrieve(metadata8);
      writer8.setId(FILE_8);
      writer8.saveBytes(0, PIXELS_8);
    }

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
    try (IFormatWriter writer16 = new JPEG2000Writer()) {
      writer16.setMetadataRetrieve(metadata16);
      writer16.setId(FILE_16);
      writer16.saveBytes(0, PIXELS_16);
    }
  }

  @Test
  public void testEquivalentPixels8Bit() throws Exception {
    try (ImageReader reader = new ImageReader()) {
      reader.setId(FILE_8);
      byte[] plane = reader.openBytes(0);
      assertEquals(plane.length, PIXELS_8.length);
      for (int q=0; q<plane.length; q++) {
        assertEquals(plane[q], PIXELS_8[q]);
      }
    }
  }

  @Test
  public void testEquivalentPixels16Bit() throws Exception {
    try (ImageReader reader = new ImageReader()) {
      reader.setId(FILE_16);
      byte[] plane = reader.openBytes(0);
      assertEquals(plane.length, PIXELS_16.length);
      for (int q=0; q<plane.length; q++) {
        assertEquals(plane[q], PIXELS_16[q]);
      }
    }
  }

  @AfterMethod(alwaysRun = true)
  public void tearDown() throws IOException {
    IOException failure = null;
    Location.mapId(FILE_8, null);
    Location.mapId(FILE_16, null);
    for (Path path : temporaryFiles) {
      try {
        Files.deleteIfExists(path);
      }
      catch (IOException e) {
        if (failure == null) failure = e;
        else failure.addSuppressed(e);
      }
    }
    temporaryFiles.clear();
    if (failure != null) throw failure;
  }

}
