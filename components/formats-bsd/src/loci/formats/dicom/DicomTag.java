/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2021 Open Microscopy Environment:
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
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;

import static loci.formats.dicom.DicomAttribute.*;
import static loci.formats.dicom.DicomVR.*;

/**
 * Represents a complete DICOM tag, including the dictionary attribute,
 * actual VR, value, and any "child" tags (in the case of a sequence).
 */
public class DicomTag implements Comparable<DicomTag> {
  public DicomTag parent = null;
  public List<DicomTag> children = new ArrayList<DicomTag>();

  public int tag;

  public DicomAttribute attribute = null;
  public DicomVR vr = null;
  public String key = null;

  public Object value = null;

  public int elementLength = 0;
  private long start = 0;

  private boolean bigEndianTransferSyntax = false;
  private boolean oddLocations = false;

  // optional indicator of how to handle this tag when merging
  // into an existing tag list
  public ResolutionStrategy strategy = null;

  public DicomTag(DicomAttribute attribute) {
    this(attribute, null);
  }

  public DicomTag(DicomAttribute attribute, DicomVR vr) {
    this.attribute = attribute;
    if (vr != null) {
      this.vr = vr;
    }
    else {
      this.vr = attribute.getDefaultVR();
    }
    this.tag = attribute.getTag();
  }

  public DicomTag(int tag, DicomVR vr) {
    this.tag = tag;
    this.attribute = DicomAttribute.get(tag);
    if (vr != null) {
      this.vr = vr; 
    }
    else if (attribute != null) {
      this.vr = attribute.getDefaultVR();
    }
  }

  /**
   * Read a complete tag and value from the given input stream.
   */
  public DicomTag(RandomAccessInputStream in, boolean bigEndian,
    long location, boolean oddLocations)
    throws FormatException, IOException
  {
    this(in, bigEndian, location, oddLocations, true);
  }

  /**
   * Read a complete tag and optionally a value from the given input stream.
   */
  public DicomTag(RandomAccessInputStream in, boolean bigEndian,
    long location, boolean oddLocations, boolean readValue)
    throws FormatException, IOException
  {
    bigEndianTransferSyntax = bigEndian;
    this.oddLocations = oddLocations;

    tag = getNextTag(in);
    attribute = DicomAttribute.get(tag);
    start = in.getFilePointer();
    if (attribute != null) {
      key = attribute.getDescription();
    }
    else {
      key = DicomAttribute.getDescription(tag);
    }

    String id = null;

    if (attribute != null) {
      id = attribute.getDescription();

      if (vr == IMPLICIT && id != null) {
        DicomVR altVR = DicomVR.get((id.charAt(0) << 8) + id.charAt(1));
        if (altVR != null) {
          vr = altVR;
        }
      }
      if (id.length() > 2) id = id.substring(2);

      if (attribute == RED_LUT_DATA || attribute == GREEN_LUT_DATA ||
        attribute == BLUE_LUT_DATA || attribute == SEGMENTED_RED_LUT_DATA ||
        attribute == SEGMENTED_GREEN_LUT_DATA || attribute == SEGMENTED_BLUE_LUT_DATA)
      {
        vr = US;
      }
    }
    else if (vr == IMPLICIT) {
      vr = DicomAttribute.getDefaultVR(tag);
    }

    if (!readValue || attribute == PIXEL_DATA) {
      long skip = elementLength & 0xffffffffL;
      if (skip > 0 && skip <= in.length() - in.getFilePointer()) {
        in.skipBytes(skip);
      }
      return;
    }

    if (attribute == ITEM) {
      value = id;
    }
    if (value == null) {
      boolean skip = vr == null;
      if ((vr == IMPLICIT || vr == RESERVED) && attribute != null) {
        vr = attribute.getDefaultVR();
      }
      if (vr != null) {
        switch (vr) {
          case AE:
          case AS:
          case CS:
          case DA:
          case DS:
          case DT:
          case IS:
          case LO:
          case LT:
          case PN:
          case SH:
          case ST:
          case TM:
          case UC:
          case UI:
          case UR:
          case UT:
            value = in.readString(elementLength);
            break;
          case AT:
            short[] pair = new short[2];
            pair[0] = in.readShort();
            pair[1] = in.readShort();
            value = pair;
            break;
          case FL:
            if (elementLength % 4 == 0 && elementLength > 4) {
              float[] f = new float[elementLength / 4];
              for (int i=0; i<f.length; i++) {
                f[i] = in.readFloat();
              }
              value = f;
            }
            else {
              value = in.readFloat();
            }
            break;
          case FD:
            if (elementLength % 8 == 0 && elementLength > 8) {
              double[] d = new double[elementLength / 8];
              for (int i=0; i<d.length; i++) {
                d[i] = in.readDouble();
              }
              value = d;
            }
            else {
              value = in.readDouble();
            }
            break;
          case OB:
            value = new byte[elementLength];
            in.read((byte[]) value);
            break;
          case SL:
            if (elementLength % 4 == 0 && elementLength > 4) {
              long[] v = new long[elementLength / 4];
              for (int i=0; i<v.length; i++) {
                v[i] = in.readInt();
              }
              value = v;
            }
            else {
              value = in.readInt();
            }
            break;
          case SS:
            if (elementLength == 2) {
              value = in.readShort();
            }
            else {
              short[] values = new short[elementLength / 2];
              for (int i=0; i<values.length; i++) {
                values[i] = in.readShort();
              }
              value = values;
            }
            break;
          case SV:
            if (elementLength % 8 == 0 && elementLength > 8) {
              long[] v = new long[elementLength / 8];
              for (int i=0; i<v.length; i++) {
                v[i] = in.readLong();
              }
              value = v;
            }
            else {
              value = in.readLong();
            }
            break;
          case UL:
            if (elementLength % 4 == 0 && elementLength > 4) {
              long[] v = new long[elementLength / 4];
              for (int i=0; i<v.length; i++) {
                v[i] = in.readInt() & 0xffffffffL;
              }
              value = v;
            }
            else {
              value = in.readInt() & 0xffffffffL;
            }
            break;
          case US:
            if (elementLength == 2) {
              value = in.readShort();
            }
            else {
              short[] values = new short[elementLength / 2];
              for (int i=0; i<values.length; i++) {
                values[i] = in.readShort();
              }
              value = values;
            }
            break;
          case IMPLICIT:
            if (elementLength == 2) {
              value = in.readShort();
            }
            else if (elementLength <= 44) {
              value = in.readString(elementLength);
            }
            else {
              short[] values = new short[elementLength / 2];
              for (int i=0; i<values.length; i++) {
                values[i] = in.readShort();
              }
              value = values;
            }
            break;
          case SQ:
            long stop = in.getFilePointer() + elementLength;
            if (elementLength == 0xffffffff) {
              // undefined length sequence, use item tags to determine when to stop
              stop = in.length();
              elementLength = 0;
            }
            while (in.getFilePointer() < stop) {
              long fp = in.getFilePointer();
              DicomTag child = new DicomTag(in, bigEndian, location, oddLocations);
              if (child.attribute == SEQUENCE_DELIMITATION_ITEM) {
                stop = in.getFilePointer();
                break;
              }
              else if (child.attribute == PIXEL_DATA) {
                child.parent = this;
                children.add(child);
                if (child.elementLength == -1) {
                  // look for end of sequence
                  long seek = fp - 2;
                  int nextTag = 0;
                  while (seek < in.length() && (nextTag != SEQUENCE_DELIMITATION_ITEM.getTag() && nextTag != ITEM_DELIMITATION_ITEM.getTag())) {
                    seek += 2;
                    in.seek(seek);
                    try {
                      nextTag = getNextTag(in);
                    }
                    catch (Exception e) {
                    }
                    if (nextTag == 0xfeffdde0 || nextTag == 0xfeff0d0e) {
                      in.order(!in.isLittleEndian());
                      break;
                    }
                  }
                }
              }
              else if (child.attribute != ITEM && child.attribute != ITEM_DELIMITATION_ITEM) {
                child.parent = this;
                children.add(child);
              }
            }
            if (elementLength <= 0) {
              elementLength = (int) (stop - start);
            }
            in.seek(stop);
            break;
          default:
            skip = true;
        }
      }
      if (skip && elementLength > 0) {
        long skipCount = (long) elementLength;
        if (in.getFilePointer() + skipCount <= in.length()) {
          in.skipBytes(skipCount);
        }
        location += elementLength;
        value = "";
      }
    }
  }

  /**
   * Get the next 4 byte DICOM tag from the given input stream.
   */
  private int getNextTag(RandomAccessInputStream in) throws FormatException, IOException {
    long fp = in.getFilePointer();
    if (fp >= in.length() - 2) {
      return 0;
    }
    int groupWord = in.readShort() & 0xffff;
    if (groupWord == 0x0800 && bigEndianTransferSyntax) {
      groupWord = 0x0008;
      in.order(false);
    }

    int elementWord = in.readShort();
    int tag = ((groupWord << 16) & 0xffff0000) | (elementWord & 0xffff);

    elementLength = getLength(in);
    if (elementLength > in.length()) {
      in.seek(fp);
      in.order(!in.isLittleEndian());

      groupWord = in.readShort() & 0xffff;
      elementWord = in.readShort();
      tag = ((groupWord << 16) & 0xffff0000) | (elementWord & 0xffff);
      elementLength = getLength(in);

      if (elementLength > in.length()) {
        throw new FormatException("Invalid tag length " + elementLength);
      }
      return tag;
    }

    // indicates that we have found pixel data
    if (groupWord == 0x7fe0) {
      // the length may legitimately be between 2 and 4 GB
      long length = elementLength & 0xffffffffL;

      // length of 0xffffffff means undefined length, which is uncommon but allowed
      if (elementLength == -1 || (length > 0 && length < in.length())) {
        return tag;
      }
      else {
        in.skipBytes(12);
        elementLength = in.readInt();
        if (elementLength < 0) elementLength = in.readInt();
      }
    }

    if (elementLength == 0 && (groupWord == 0x7fe0 || tag == 0x291014)) {
      elementLength = getLength(in);
    }
    else if (elementLength == 0) {
      in.seek(in.getFilePointer() - 4);
      DicomVR v = DicomVR.get(in.readShort() & 0xffff);
      if (v == UT) {
        in.skipBytes(2);
        elementLength = in.readInt();
      }
      else in.skipBytes(2);
    }

    // HACK - needed to read some GE files
    // The element length must be even!
    if (!oddLocations && (elementLength % 2) == 1) elementLength++;

    return tag;
  }

  /**
   * Get the length of the next value in the given input stream.
   */
  private int getLength(RandomAccessInputStream in) throws IOException {
    byte[] b = new byte[4];
    in.read(b);

    // We cannot know whether the VR is implicit or explicit
    // without the full DICOM Data Dictionary for public and
    // private groups.

    // We will assume the VR is explicit if the two bytes
    // match the known codes. It is possible that these two
    // bytes are part of a 32-bit length for an implicit VR.

    // see http://dicom.nema.org/medical/dicom/current/output/html/part05.html#sect_7.1.2

    vr = DicomVR.get(((b[0] & 0xff) << 8) | (b[1] & 0xff));

    if (vr == null) {
      vr = IMPLICIT;
      int len = DataTools.bytesToInt(b, in.isLittleEndian());
      if (len + in.getFilePointer() > in.length() || len < 0) {
        len = DataTools.bytesToInt(b, 2, 2, in.isLittleEndian());
        len &= 0xffff;
      }
      return len;
    }

    switch (vr) {
      case OB:
      case OV:
      case OW:
      case SQ:
      case UN:
      case UT:
      case UC:
        // Explicit VR with 32-bit length if other two bytes are zero
        if ((b[2] == 0) || (b[3] == 0)) {
          return in.readInt();
        }
        vr = IMPLICIT;
        return DataTools.bytesToInt(b, in.isLittleEndian());
      case AE:
      case AS:
      case AT:
      case CS:
      case DA:
      case DS:
      case DT:
      case FD:
      case FL:
      case IS:
      case LO:
      case LT:
      case PN:
      case SH:
      case SL:
      case SS:
      case ST:
      case TM:
      case UI:
      case UL:
      case US:
      case QQ:
        // Explicit VR with 16-bit length
        if (attribute == LUT_DATA) {
          return DataTools.bytesToInt(b, 2, 2, in.isLittleEndian());
        }
        int n1 = DataTools.bytesToShort(b, 2, 2, in.isLittleEndian());
        int n2 = DataTools.bytesToShort(b, 2, 2, !in.isLittleEndian());
        n1 &= 0xffff;
        n2 &= 0xffff;
        if (n1 < 0 || n1 + in.getFilePointer() > in.length()) return n2;
        if (n2 < 0 || n2 + in.getFilePointer() > in.length()) return n1;
        return n1;
      case RESERVED:
        int len = DataTools.bytesToInt(b, 0, 4, in.isLittleEndian());
        if (len == 0xffffffff) {
          return len;
        }
        else {
          vr = IMPLICIT;
          return 8;
        }
      default:
        throw new IllegalArgumentException(vr.toString());
    }
  }

  /**
   * Get a string representation of the current value.
   */
  public String getStringValue() {
    return value == null ? null : value.toString().trim();
  }

  /**
   * Get a number representation of the current value, or null if not a number.
   */
  public Number getNumberValue() {
    if (value == null) {
      return null;
    }
    if (value instanceof Number) {
      return (Number) value;
    }
    String v = value.toString().trim();
    try {
      return new Double(v);
    }
    catch (NumberFormatException e) {
      return DataTools.parseDouble(v);
    }
  }

  /**
   * Get the file pointer at which the value starts (useful for sequences and byte streams).
   */
  public long getValueStartPointer() {
    return start;
  }

  /**
   * Get the file pointer at which the value ends (useful for sequences and byte streams).
   */
  public long getEndPointer() {
    if (elementLength < 0) {
      return start;
    }
    return start + elementLength;
  }

  /**
   * Find the first child tag with the given attribute, or null if one does not exist.
   */
  public DicomTag lookupChild(DicomAttribute attr) {
    for (DicomTag child : children) {
      if (child.attribute == attr) {
        return child;
      }
    }
    return null;
  }

  /**
   * Check this tag against a list of existing tags.
   * If this tag is a duplicate of an existing tag or otherwise deemed invalid,
   * return false. Otherwise return true.
   */
  public boolean validate(List<DicomTag> tags) {
    for (DicomTag t : tags) {
      if (this.tag == t.tag) {
        return strategy != ResolutionStrategy.IGNORE;
      }
    }
    return true;
  }

  /**
   * Compare the value type to the VR.
   * If the two types do not match, attempt to conver the value
   * into the VR's type.
   * Usually this means parsing numerical values from a String.
   */
  public void validateValue() {
    if (value == null) {
      return;
    }
    switch (vr) {
      case AE:
      case AS:
      case CS:
      case DA:
      case DS:
      case DT:
      case IS:
      case LO:
      case LT:
      case PN:
      case SH:
      case ST:
      case TM:
      case UC:
      case UI:
      case UR:
      case UT:
        value = value.toString();
        break;
      case AT:
      case SS:
      case US:
        if (value instanceof Short) {
          value = new short[] {(short) value};
        }
        else if (value instanceof String) {
          String[] values = ((String) value).split(",");
          short[] v = new short[values.length];
          for (int i=0; i<values.length; i++) {
            v[i] = Short.decode(values[i]);
          }
          value = v;
        }
        else if (!(value instanceof short[])) {
          throw new IllegalArgumentException("Incorrect value type " + value.getClass() + " for VR " + vr);
        }
        break;
      case FL:
        if (value instanceof Float) {
          value = new float[] {(float) value};
        }
        else if (value instanceof String) {
          String[] values = ((String) value).split(",");
          float[] v = new float[values.length];
          for (int i=0; i<values.length; i++) {
            v[i] = Float.parseFloat(values[i]);
          }
          value = v;
        }
        else if (!(value instanceof float[])) {
          throw new IllegalArgumentException("Incorrect value type " + value.getClass() + " for VR " + vr);
        }
        break;
      case FD:
        if (value instanceof Double) {
          value = new double[] {(double) value};
        }
        else if (value instanceof String) {
          String[] values = ((String) value).split(",");
          double[] v = new double[values.length];
          for (int i=0; i<values.length; i++) {
            v[i] = Double.parseDouble(values[i]);
          }
          value = v;
        }
        else if (!(value instanceof double[])) {
          throw new IllegalArgumentException("Incorrect value type " + value.getClass() + " for VR " + vr);
        }
        break;
      case OB:
      case IMPLICIT:
        if (value instanceof Byte) {
          value = new byte[] {(byte) value};
        }
        else if (value instanceof String) {
          String[] values = ((String) value).split(",");
          byte[] v = new byte[values.length];
          for (int i=0; i<values.length; i++) {
            v[i] = Byte.decode(values[i]);
          }
          value = v;
        }
        else if (!(value instanceof byte[])) {
          throw new IllegalArgumentException("Incorrect value type " + value.getClass() + " for VR " + vr);
        }
        break;
      case SL:
        if (value instanceof Integer) {
          value = new int[] {(int) value};
        }
        else if (value instanceof String) {
          String[] values = ((String) value).split(",");
          int[] v = new int[values.length];
          for (int i=0; i<values.length; i++) {
            v[i] = Integer.decode(values[i]);
          }
          value = v;
        }
        else if (!(value instanceof int[])) {
          throw new IllegalArgumentException("Incorrect value type " + value.getClass() + " for VR " + vr);
        }
        break;
      case SV:
      case UL:
        if (value instanceof Long) {
          value = new long[] {(long) value};
        }
        else if (value instanceof String) {
          String[] values = ((String) value).split(",");
          long[] v = new long[values.length];
          for (int i=0; i<values.length; i++) {
            v[i] = Long.decode(values[i]);
          }
          value = v;
        }
        else if (!(value instanceof long[])) {
          throw new IllegalArgumentException("Incorrect value type " + value.getClass() + " for VR " + vr);
        }
        break;
      default:
        throw new IllegalArgumentException(String.valueOf(vr.getCode()));
    }
  }

  /**
   * See https://dicom.nema.org/dicom/2013/output/chtml/part05/sect_7.8.html
   *
   * @return true if this is a private content creator tag
   */
  public boolean isPrivateContentCreator() {
    int highWord = (tag >> 16) & 0xffff;
    int lowWord = tag & 0xffff;
    return highWord % 2 == 1 && lowWord == 0x0010;
  }

  @Override
  public String toString() {
    if (key == null) {
      if (attribute != null) {
        key = attribute.getDescription();
      }
      else {
        key = DicomAttribute.getDescription(tag);
      }
    }
    return key + " = " + value;
  }

  @Override
  public int compareTo(DicomTag o) {
    int thisTag = this.attribute == null ? this.tag : this.attribute.getTag();
    int otherTag = o.attribute == null ? o.tag : o.attribute.getTag();

    return thisTag - otherTag;
  }

}
