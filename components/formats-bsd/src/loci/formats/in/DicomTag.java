package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;

import static loci.formats.in.DicomAttribute.*;
import static loci.formats.in.DicomVR.*;

public class DicomTag {
  public DicomTag parent = null;
  public List<DicomTag> children = new ArrayList<DicomTag>();

  public DicomAttribute attribute = null;
  public DicomVR vr = null;
  public String key = null;

  public Object value = null;

  private int elementLength = 0;
  private long start = 0;

  private boolean bigEndianTransferSyntax = false;
  private boolean oddLocations = false;

  public DicomTag(RandomAccessInputStream in, boolean bigEndian,
    long location, boolean oddLocations)
    throws FormatException, IOException
  {
    bigEndianTransferSyntax = bigEndian;
    this.oddLocations = oddLocations;

    int tag = getNextTag(in);
    attribute = DicomAttribute.get(tag);
    start = in.getFilePointer();
    if (attribute != null) {
      key = attribute.getDescription();
    }

    String id = null;

    if (attribute != null) {
      /*
      if (attribute == ITEM_DELIMITATION_ITEM || attribute == SEQUENCE_DELIMITATION_ITEM) {
        inSequence = false;
      }
      */

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

    if (attribute == ITEM) {
      value = id;
    }
    if (value == null) {
      boolean skip = vr == null;
      if (vr == IMPLICIT && attribute != null) {
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
            value = in.readFloat();
            break;
          case FD:
            value = in.readDouble();
            break;
          case OB:
            value = new byte[elementLength];
            in.read((byte[]) value);
            break;
          case SL:
            value = in.readInt();
            break;
          case SS:
            value = in.readShort();
            break;
          case SV:
            value = in.readLong();
            break;
          case UL:
            value = in.readInt() & 0xffffffffL;
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
            while (in.getFilePointer() < stop) {
              DicomTag child = new DicomTag(in, bigEndian, location, oddLocations);
              child.parent = this;
              children.add(child);
            }
            in.seek(stop);
            break;
          default:
            skip = true;
        }
      }
      if (skip) {
        long skipCount = (long) elementLength;
        if (in.getFilePointer() + skipCount <= in.length()) {
          in.skipBytes(skipCount);
        }
        location += elementLength;
        value = "";
      }
    }
  }

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
    else if (groupWord == 0xfeff || groupWord == 0xfffe) {
      in.skipBytes(6);
      return getNextTag(in);
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

    if (elementLength < 0 && groupWord == 0x7fe0) {
      in.skipBytes(12);
      elementLength = in.readInt();
      if (elementLength < 0) elementLength = in.readInt();
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

    // "Undefined" element length.
    // This is a sort of bracket that encloses a sequence of elements.
    if (elementLength < 0) {
      elementLength = 0;
    }
    return tag;
  }

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
    ///* debug */ System.out.println("@ 241 vr = " + vr);

    if (vr == null) {
      vr = IMPLICIT;
    ///* debug */ System.out.println("@ 245 vr = " + vr);
      int len = DataTools.bytesToInt(b, in.isLittleEndian());
      if (len + in.getFilePointer() > in.length() || len < 0) {
        len = DataTools.bytesToInt(b, 2, 2, in.isLittleEndian());
        len &= 0xffff;
      }
      return len;
    }

    switch (vr) {
      case OB:
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
        vr = IMPLICIT;
        return 8;
      default:
        throw new IllegalArgumentException(vr.toString());
    }
  }

  public String getStringValue() {
    return value == null ? null : value.toString().trim();
  }

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

  public long getValueStartPointer() {
    return start;
  }

  public long getEndPointer() {
    return start + elementLength;
  }

  public DicomTag lookupChild(DicomAttribute attr) {
    for (DicomTag child : children) {
      if (child.attribute == attr) {
        return child;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return key + " = " + value;
  }

}
