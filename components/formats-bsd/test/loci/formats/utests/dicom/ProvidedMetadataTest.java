/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2023 Open Microscopy Environment:
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
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import loci.common.Constants;
import loci.formats.FormatException;
import loci.formats.MetadataTools;
import loci.formats.dicom.DicomAttribute;
import loci.formats.dicom.DicomTag;
import loci.formats.dicom.DicomVR;
import loci.formats.in.DicomReader;
import loci.formats.in.FakeReader;
import loci.formats.meta.IMetadata;
import loci.formats.out.DicomWriter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 */
public class ProvidedMetadataTest {

  public Path doConversion(String jsonMetadata) throws FormatException, IOException {
    Path jsonFile = Files.createTempFile("dicom", ".json");
    Path dicomFile = Files.createTempFile("metadata-test", ".dcm");
    FakeReader reader = new FakeReader();
    DicomWriter writer = new DicomWriter();
    try {
      Files.write(jsonFile, jsonMetadata.getBytes(Constants.ENCODING));

      IMetadata meta = MetadataTools.createOMEXMLMetadata();
      reader.setMetadataStore(meta);
      reader.setId("test.fake");

      writer.setExtraMetadata(jsonFile.toString());
      writer.setMetadataRetrieve(meta);
      writer.setId(dicomFile.toString());
      writer.saveBytes(0, reader.openBytes(0));
    }
    catch (Throwable e) {
      Files.delete(dicomFile);
      throw e;
    }
    finally {
      reader.close();
      writer.close();
      Files.delete(jsonFile);
    }

    return dicomFile;
  }

  public List<DicomTag> getTags(Path dicomFile) throws FormatException, IOException {
    DicomReader reader = new DicomReader();
    reader.setId(dicomFile.toString());
    List<DicomTag> tags = reader.getTags();
    reader.close();
    return tags;
  }

  public DicomTag lookup(List<DicomTag> tags, DicomAttribute attr) {
    for (DicomTag t : tags) {
      if (attr.equals(t.attribute)) {
        return t;
      }
    }
    return null;
  }

  @Test
  public void testSingleTagWithDefaults() throws FormatException, IOException {
    Path dicomFile = null;
    try {
      String json = "{" +
       "\"BodyPartExamined\": {" +
       "\"Value\": \"BRAIN\"" +
       "}}";

      dicomFile = doConversion(json);
      List<DicomTag> tags = getTags(dicomFile);
      DicomTag found = lookup(tags, DicomAttribute.BODY_PART_EXAMINED);
      assertNotNull(found);

      // trailing space is not a typo
      // the writer pads string values to an even number of characters
      assertEquals(found.value, "BRAIN ");
      assertEquals(found.vr, DicomVR.CS);
    }
    finally {
      Files.delete(dicomFile);
    }
  }

  /*
  @Test(expectedExceptions={ IllegalArgumentException.class })
  public void testSingleInvalidTagName() throws FormatException, IOException {
    String json = "{" +
     "\"not a valid tag\": {" +
     "\"Value\": \"x\"" +
     "}}";

    doConversion(json);
  }

  @Test(expectedExceptions={ IllegalArgumentException.class })
  public void testSingleTagWeirdNameWithWhitespace() throws FormatException, IOException {
    String json = "{" +
     "\"bOdy PaRt examiNeD \": {" +
     "\"Value\": \"x\"" +
     "}}";

    doConversion(json);
  }

  @Test
  public void testSingleTagWeirdName() throws FormatException, IOException {
    Path dicomFile = null;
    try {
      String json = "{" +
       "\"bOdyPaRtexamiNeD\": {" +
       "\"Value\": \"BRAIN\"" +
       "}}";

      dicomFile = doConversion(json);
      List<DicomTag> tags = getTags(dicomFile);
      DicomTag found = lookup(tags, DicomAttribute.BODY_PART_EXAMINED);
      assertNotNull(found);

      // trailing space is not a typo
      // the writer pads string values to an even number of characters
      assertEquals(found.value, "BRAIN ");
      assertEquals(found.vr, DicomVR.CS);
    }
    finally {
      Files.delete(dicomFile);
    }
  }

  @Test
  public void testSingleTagCustomVR() throws FormatException, IOException {
    Path dicomFile = null;
    try {
      String json = "{" +
       "\"BodyPartExamined\": {" +
       "\"Value\": \"0\"," +
       "\"VR\": \"SH\"" +
       "}}";

      dicomFile = doConversion(json);
      List<DicomTag> tags = getTags(dicomFile);
      DicomTag found = lookup(tags, DicomAttribute.BODY_PART_EXAMINED);
      assertNotNull(found);

      assertEquals(found.value, "0 ");
      assertEquals(found.vr, DicomVR.SH);
    }
    finally {
      Files.delete(dicomFile);
    }
  }

  @Test
  public void testReplaceExistingTag() throws FormatException, IOException {
    Path dicomFile = null;
    try {
      String json = "{" +
       "\"SpecimenLabelInImage\": {" +
       "\"Value\": \"NO\"" +
       "}}";

      dicomFile = doConversion(json);
      List<DicomTag> tags = getTags(dicomFile);
      DicomTag found = lookup(tags, DicomAttribute.SPECIMEN_LABEL_IN_IMAGE);
      assertNotNull(found);

      assertEquals(found.value, "NO");
      assertEquals(found.vr, DicomVR.CS);
    }
    finally {
      Files.delete(dicomFile);
    }
  }

  @Test
  public void testIgnoreExistingTag() throws FormatException, IOException {
    Path dicomFile = null;
    try {
      String json = "{" +
       "\"SpecimenLabelInImage\": {" +
       "\"Value\": \"NO\"," +
       "\"ResolutionStrategy\": \"IGNORE\"" +
       "}}";

      dicomFile = doConversion(json);
      List<DicomTag> tags = getTags(dicomFile);
      DicomTag found = lookup(tags, DicomAttribute.SPECIMEN_LABEL_IN_IMAGE);
      assertNotNull(found);

      assertEquals(found.value, "YES ");
      assertEquals(found.vr, DicomVR.CS);
    }
    finally {
      Files.delete(dicomFile);
    }
  }

  @Test
  public void testReplaceOpticalPath() throws FormatException, IOException {
    Path dicomFile = null;
    try {
      String json = "{" +
      "\"OpticalPathSequence\": {" +
      "\"Sequence\": {" +
      "\"IlluminationTypeCodeSequence\": {" +
      "\"Sequence\": {" +
      "\"CodeValue\": {" +
      "\"VR\": \"SH\"," +
      "\"Value\": \"111743\"" +
      "}," +
      "\"CodingSchemeDesignator\": {" +
      "\"VR\": \"SH\"," +
      "\"Value\": \"DCM\"" +
      "}," +
      "\"CodeMeaning\": {" +
      "\"VR\": \"LO\"," +
      "\"Tag\": \"(0008,0104)\"," +
      "\"Value\": \"Epifluorescence illumination\"" +
      "}}}," +
      "\"IlluminationWaveLength\": {" +
      "\"Value\": \"488.0\"" +
      "}," +
      "\"OpticalPathIdentifier\": {" +
      "\"Value\": \"1\"" +
      "}," +
      "\"OpticalPathDescription\": {" +
      "\"Value\": \"replacement channel\" " +
      "}}," +
      "\"ResolutionStrategy\": \"REPLACE\"" +
      "}}";

      dicomFile = doConversion(json);
      List<DicomTag> tags = getTags(dicomFile);

      DicomTag found = lookup(tags, DicomAttribute.OPTICAL_PATH_SEQUENCE);
      assertNotNull(found);

      assertEquals(found.children.size(), 4);
      assertEquals(found.vr, DicomVR.SQ);

      assertEquals(found.children.get(0).attribute, DicomAttribute.ILLUMINATION_TYPE_CODE_SEQUENCE);

      assertEquals(found.children.get(1).attribute, DicomAttribute.ILLUMINATION_WAVELENGTH);
      assertEquals(found.children.get(1).value, 488f);

      assertEquals(found.children.get(2).attribute, DicomAttribute.OPTICAL_PATH_ID);
      assertEquals(found.children.get(2).value, "1 ");

      assertEquals(found.children.get(3).attribute, DicomAttribute.OPTICAL_PATH_DESCRIPTION);
      assertEquals(found.children.get(3).value, "replacement channel ");
    }
    finally {
      Files.delete(dicomFile);
    }
  }

  @Test
  public void testAppendOpticalPath() throws FormatException, IOException {
    Path dicomFile = null;
    try {
      String json = "{" +
      "\"OpticalPathSequence\": {" +
      "\"Sequence\": {" +
      "\"IlluminationTypeCodeSequence\": {" +
      "\"Sequence\": {" +
      "\"CodeValue\": {" +
      "\"VR\": \"SH\"," +
      "\"Value\": \"111743\"" +
      "}," +
      "\"CodingSchemeDesignator\": {" +
      "\"VR\": \"SH\"," +
      "\"Value\": \"DCM\"" +
      "}," +
      "\"CodeMeaning\": {" +
      "\"VR\": \"LO\"," +
      "\"Tag\": \"(0008,0104)\"," +
      "\"Value\": \"Epifluorescence illumination\"" +
      "}}}," +
      "\"IlluminationWaveLength\": {" +
      "\"Value\": \"488.0\"" +
      "}," +
      "\"OpticalPathIdentifier\": {" +
      "\"Value\": \"1\"" +
      "}," +
      "\"OpticalPathDescription\": {" +
      "\"Value\": \"replacement channel\" " +
      "}}," +
      "\"ResolutionStrategy\": \"APPEND\"" +
      "}}";

      dicomFile = doConversion(json);
      List<DicomTag> tags = getTags(dicomFile);

      DicomTag found = lookup(tags, DicomAttribute.OPTICAL_PATH_SEQUENCE);
      assertNotNull(found);

      assertEquals(found.children.size(), 8);
      assertEquals(found.vr, DicomVR.SQ);

      assertEquals(found.children.get(0).attribute, DicomAttribute.ILLUMINATION_TYPE_CODE_SEQUENCE);

      assertEquals(found.children.get(1).attribute, DicomAttribute.ILLUMINATION_WAVELENGTH);
      assertEquals(found.children.get(1).value, 1f);

      assertEquals(found.children.get(2).attribute, DicomAttribute.OPTICAL_PATH_ID);
      assertEquals(found.children.get(2).value, "0 ");

      assertEquals(found.children.get(3).attribute, DicomAttribute.OPTICAL_PATH_DESCRIPTION);
      assertEquals(found.children.get(3).value, "");

      assertEquals(found.children.get(4).attribute, DicomAttribute.ILLUMINATION_TYPE_CODE_SEQUENCE);

      assertEquals(found.children.get(5).attribute, DicomAttribute.ILLUMINATION_WAVELENGTH);
      assertEquals(found.children.get(5).value, 488f);

      assertEquals(found.children.get(6).attribute, DicomAttribute.OPTICAL_PATH_ID);
      assertEquals(found.children.get(6).value, "1 ");

      assertEquals(found.children.get(7).attribute, DicomAttribute.OPTICAL_PATH_DESCRIPTION);
      assertEquals(found.children.get(7).value, "replacement channel ");
    }
    finally {
      Files.delete(dicomFile);
    }
  }

  @Test
  public void testIgnoreOpticalPath() throws FormatException, IOException {
    Path dicomFile = null;
    try {
      String json = "{" +
      "\"OpticalPathSequence\": {" +
      "\"Sequence\": {" +
      "\"IlluminationTypeCodeSequence\": {" +
      "\"Sequence\": {" +
      "\"CodeValue\": {" +
      "\"VR\": \"SH\"," +
      "\"Value\": \"111743\"" +
      "}," +
      "\"CodingSchemeDesignator\": {" +
      "\"VR\": \"SH\"," +
      "\"Value\": \"DCM\"" +
      "}," +
      "\"CodeMeaning\": {" +
      "\"VR\": \"LO\"," +
      "\"Tag\": \"(0008,0104)\"," +
      "\"Value\": \"Epifluorescence illumination\"" +
      "}}}," +
      "\"IlluminationWaveLength\": {" +
      "\"Value\": \"488.0\"" +
      "}," +
      "\"OpticalPathIdentifier\": {" +
      "\"Value\": \"1\"" +
      "}," +
      "\"OpticalPathDescription\": {" +
      "\"Value\": \"replacement channel\" " +
      "}}," +
      "\"ResolutionStrategy\": \"IGNORE\"" +
      "}}";

      dicomFile = doConversion(json);
      List<DicomTag> tags = getTags(dicomFile);

      DicomTag found = lookup(tags, DicomAttribute.OPTICAL_PATH_SEQUENCE);
      assertNotNull(found);

      assertEquals(found.children.size(), 4);
      assertEquals(found.vr, DicomVR.SQ);

      assertEquals(found.children.get(0).attribute, DicomAttribute.ILLUMINATION_TYPE_CODE_SEQUENCE);

      assertEquals(found.children.get(1).attribute, DicomAttribute.ILLUMINATION_WAVELENGTH);
      assertEquals(found.children.get(1).value, 1f);

      assertEquals(found.children.get(2).attribute, DicomAttribute.OPTICAL_PATH_ID);
      assertEquals(found.children.get(2).value, "0 ");

      assertEquals(found.children.get(3).attribute, DicomAttribute.OPTICAL_PATH_DESCRIPTION);
      assertEquals(found.children.get(3).value, "");
    }
    finally {
      Files.delete(dicomFile);
    }
  }

  @Test
  public void testIgnoreInvalidJSON() throws FormatException, IOException {
    Path dicomFile = null;
    try {
      String json = "{" +
       "\"BodyPartExamined\": {" +
       "\"Value\": \"BRAIN\"" +
       "}";

      dicomFile = doConversion(json);
      List<DicomTag> tags = getTags(dicomFile);
      DicomTag found = lookup(tags, DicomAttribute.BODY_PART_EXAMINED);
      assertEquals(found, null);
    }
    finally {
      Files.delete(dicomFile);
    }
  }
  */

}
