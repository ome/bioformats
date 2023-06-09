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

import loci.common.DataTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide DICOM tags from a file containing the output of dcdump.
 */
public class DCDumpProvider implements ITagProvider {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(DCDumpProvider.class);

  private List<DicomTag> tags = new ArrayList<DicomTag>();

  @Override
  public void readTagSource(String location) throws IOException {
    String[] lines = DataTools.readFile(location).split("[\r\n]");

    LOGGER.debug("Attempting to parse {} tags", lines.length);
    DicomTag parentTag = null;
    for (String line : lines) {
      line = line.trim();

      if (line.isEmpty() && parentTag != null) {
        // indicates end of sequence
        parentTag = parentTag.parent;
        continue;
      }

      LOGGER.debug("Parsing line: {}", line);
      boolean sequenceElement = line.startsWith(">");
      if (sequenceElement) {
        line = line.substring(1).trim();
        if (parentTag == null) {
          parentTag = tags.get(tags.size() - 1);
        }
      }

      String[] tokens = line.split(" ");
      if (tokens.length <= 1) {
        continue;
      }
      String[] tag = tokens[0].replaceAll("[()]", "").split(",");

      String vr = tokens[1].trim();
      if (vr.isEmpty()) {
        LOGGER.debug("VR not found for tag {}", tokens[0]);
        continue;
      }

      String length = null;
      String value = "";
      for (int i=2; i<tokens.length; i++) {
        if (tokens[i].startsWith("VL")) {
          length = tokens[i];
        }
        else if (tokens[i].startsWith("<")) {
          value += tokens[i];
          while (!value.endsWith(">")) {
            i++;
            value += tokens[i];
          }
          value = value.substring(1, value.length() - 1);
        }
        else if (tokens[i].startsWith("[")) {
          value += tokens[i];
          while (!value.endsWith("]")) {
            i++;
            value += tokens[i];
          }
          value = value.substring(1, value.length() - 1);
        }
      }

      int tagUpper = Integer.decode(tag[0]);
      int tagLower = Integer.decode(tag[1]);

      DicomVR vrEnum = DicomVR.valueOf(DicomVR.class, vr);
      int tagCode = tagUpper << 16 | tagLower;
      DicomTag t = new DicomTag(tagCode, vrEnum);
      if (vrEnum != DicomVR.SQ) {
        t.value = value;
        t.validateValue();
      }
      LOGGER.debug("Adding tag: {}, VR: {}, value: {}", tagCode, vrEnum, value);
      if (parentTag == null) {
        tags.add(t);
      }
      else {
        t.parent = parentTag;
        parentTag.children.add(t);
      }
      if (vrEnum == DicomVR.SQ) {
        parentTag = t;
      }
    }
  }

  @Override
  public List<DicomTag> getTags() {
    return tags;
  }

}
