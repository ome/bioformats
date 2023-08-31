/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2023 Open Microscopy Environment:
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

package loci.formats.dicom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.Constants;
import loci.common.DataTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide DICOM tags from a file containing JSON.
 * Formal JSON schema yet to be determined, but the idea is to accept a hierarchy
 * of tags of the form:
 *
 * {
 *   "BodyPartExamined": {
 *     "Value": "BRAIN",
 *     "VR": "CS",
 *     "Tag": "(0018,0015)"
 *   }
 *   "ContributingEquipmentSequence": {
 *     "VR": "SQ",
 *     "Tag": "(0018,a001)",
 *     "Sequence": {
 *       "Manufacturer": {
 *         "Value": "PixelMed",
 *         "VR": "LO",
 *         "Tag": "(0008,0070)"
 *       },
 *       "ContributionDateTime": {
 *         "Value": "20210710234601.105+0000",
 *         "VR": "DT",
 *         "Tag": "(0018,a002)"
 *       }
 *     }
 *   }
 * }
 *
 * This is similar to JSON examples in https://github.com/QIICR/dcmqi/tree/master/doc/examples,
 * but allows for the VR (type) and tag to be explicitly stated, in addition to
 * the more human-readable description.
 */
public class DicomJSONProvider implements ITagProvider {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(DicomJSONProvider.class);

  private List<DicomTag> tags = new ArrayList<DicomTag>();

  @Override
  public void readTagSource(String location) throws IOException {
    String rawJSON = DataTools.readFile(location);
    try {
      JSONObject root = new JSONObject(rawJSON);

      for (String tagKey : root.keySet()) {
        JSONObject tag = root.getJSONObject(tagKey);
        String value = tag.has("Value") ? tag.getString("Value") : null;

        Integer intTagCode = null;

        if (tag.has("Tag")) {
          String[] tagCode = tag.getString("Tag").replaceAll("[()]", "").split(",");

          int tagUpper = Integer.parseInt(tagCode[0], 16);
          int tagLower = Integer.parseInt(tagCode[1], 16);

          intTagCode = tagUpper << 16 | tagLower;
        }
        else {
          intTagCode = DicomAttribute.getTag(tagKey);

          if (intTagCode == null) {
            throw new IllegalArgumentException(
              "Tag not defined and could not be determined from description '" +
              tagKey + "'");
          }
        }

        DicomVR vrEnum = DicomAttribute.getDefaultVR(intTagCode);
        if (tag.has("VR")) {
          DicomVR userEnum = DicomVR.valueOf(DicomVR.class, tag.getString("VR"));
          if (!vrEnum.equals(userEnum)) {
            LOGGER.warn("User-defined VR ({}) for {} does not match expected VR ({})",
              userEnum, DicomAttribute.formatTag(intTagCode), vrEnum);
            if (userEnum != null) {
              vrEnum = userEnum;
            }
          }
        }

        DicomTag dicomTag = new DicomTag(intTagCode, vrEnum);
        dicomTag.value = value;
        LOGGER.debug("Adding tag: {}, VR: {}, value: {}",
          DicomAttribute.formatTag(intTagCode), vrEnum, value);
        dicomTag.validateValue();

        if (vrEnum == DicomVR.SQ && tag.has("Sequence")) {
          readSequence(tag, dicomTag);
        }

        ResolutionStrategy rs = vrEnum == DicomVR.SQ ? ResolutionStrategy.APPEND : ResolutionStrategy.REPLACE;
        if (tag.has("ResolutionStrategy")) {
          String strategy = tag.getString("ResolutionStrategy");
          rs = Enum.valueOf(ResolutionStrategy.class, strategy);
        }
        dicomTag.strategy = rs;

        tags.add(dicomTag);
      }
    }
    catch (JSONException e) {
      throw new IOException("Could not parse JSON", e);
    }
  }

  @Override
  public List<DicomTag> getTags() {
    return tags;
  }

  private void readSequence(JSONObject rootTag, DicomTag parent) {
    JSONObject sequence = rootTag.getJSONObject("Sequence");
    for (String key : sequence.keySet()) {
      JSONObject tag = sequence.getJSONObject(key);
      String vr = tag.getString("VR");
      String[] tagCode = tag.getString("Tag").replaceAll("[()]", "").split(",");

      int tagUpper = Integer.parseInt(tagCode[0], 16);
      int tagLower = Integer.parseInt(tagCode[1], 16);

      int intTagCode = tagUpper << 16 | tagLower;
      DicomVR vrEnum = DicomVR.valueOf(DicomVR.class, vr);
      DicomTag dicomTag = new DicomTag(intTagCode, vrEnum);

      if (tag.has("Value")) {
        dicomTag.value = tag.get("Value");
      }

      LOGGER.debug("Adding tag: {}, VR: {}, value: {}", intTagCode, vrEnum, dicomTag.value);
      dicomTag.validateValue();

      dicomTag.parent = parent;
      parent.children.add(dicomTag);

      if (vrEnum == DicomVR.SQ && tag.get("Sequence") != null) {
        readSequence(tag, dicomTag);
      }
    }
    parent.children.sort(null);
  }

}
