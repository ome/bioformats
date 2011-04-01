
package ome.metakit;

import java.io.IOException;
import java.util.ArrayList;

import loci.common.RandomAccessInputStream;

public class ColumnMap {

  // -- Fields --

  private ArrayList values = new ArrayList();
  private Column col;
  private RandomAccessInputStream stream;
  private int rowCount;

  // -- Constructors --

  public ColumnMap(Column col, RandomAccessInputStream stream, int rowCount) {
    this.col = col;
    this.stream = stream;
    this.rowCount = rowCount;
    try {
      setup();
    }
    catch (IOException e) {

    }
  }

  // -- ColumnMap API methods --

  public ArrayList getValueList() {
    return values;
  }

  public Object[] getValues() {
    return values.toArray(new Object[values.size()]);
  }

  public boolean isFixedMap() {
    char type = col.getTypeString().charAt(0);
    return type != 'S' && type != 'B';
  }

  // -- Helper methods --

  private void setup() throws IOException {
    /* debug */ System.out.println("reading ColumnMap from " + stream.getFilePointer());
    if (isFixedMap()) {
      /* debug */ System.out.println("reading fixed map");
      // read a single IVecRef

      int ivecSize = MetakitTools.readBpInt(stream);
      int ivecPointer = MetakitTools.readBpInt(stream);
      if (ivecSize > 0) {
        long fp = stream.getFilePointer();
        stream.seek(ivecPointer);

        for (int i=0; i<rowCount; i++) {
          values.add(readElement(ivecSize));
        }
        stream.seek(fp);
      }
      else {
        for (int i=0; i<rowCount; i++) {
          values.add(null);
        }
      }
    }
    else {
      /* debug */ System.out.println("reading variable map");
      int ivecSize = MetakitTools.readBpInt(stream); // total bytes
      int ivecPointer = MetakitTools.readBpInt(stream);

      int mapIvecSize = MetakitTools.readBpInt(stream);
      int mapIvecPointer = MetakitTools.readBpInt(stream);

      int catalogIvecSize = MetakitTools.readBpInt(stream);
      int catalogIvecPointer = MetakitTools.readBpInt(stream);

      long fp = stream.getFilePointer();

      stream.seek(mapIvecPointer);
      int[] byteCounts = new int[rowCount];

      for (int i=0; i<rowCount; i++) {
        byteCounts[i] = readBits(mapIvecSize, (mapIvecSize * 8) / rowCount, i);
        /* debug */ System.out.println("  byteCounts[" + i + "] = " + byteCounts[i]);
      }

      stream.seek(ivecPointer);
      for (int i=0; i<rowCount; i++) {
        byte[] buf = new byte[byteCounts[i]];
        stream.read(buf);
        char type = col.getTypeString().charAt(0);
        values.add(type == 'B' ? buf : new String(buf));
      }

      stream.seek(fp);
    }
  }

  private Object readElement(int vectorSize) throws IOException {
    char type = col.getTypeString().charAt(0);

    switch (type) {
      case 'F':
        return new Float(stream.readFloat());
      case 'D':
        return new Double(stream.readDouble());
      case 'L':
        return new Long(stream.readLong());
      case 'I':
        int bits = (vectorSize * 8) / rowCount;
        switch (bits) {
          case 1:
            break;
          case 2:
            break;
          case 4:
            break;
          case 8:
            return new Integer(stream.read());
          case 16:
            return new Integer(stream.readShort());
          case 32:
            return new Integer(stream.readInt());
          default:
            return new Integer(stream.readInt());
        }
    }

    return null;
  }

  private int readBits(int nBytes, int bits, int index) throws IOException {
    if (bits == 8) {
      return stream.read();
    }
    else if (bits == 16) {
      return stream.readShort() & 0xffff;
    }
    else if (bits == 32) {
      return stream.readInt();
    }

    /* debug */
    System.out.println("    nBytes = " + nBytes);
    System.out.println("    bits = " + bits);
    System.out.println("    index = " + index);
    System.out.println("    littleEndian = " + stream.isLittleEndian());
    /* end debug */

    long fp = stream.getFilePointer();
    int realIndex = index;
    if (stream.isLittleEndian()) {
      realIndex = ((nBytes * 8) / bits) - index - 1;
    }
    stream.skipBytes((realIndex * bits) / 8);
    int b = stream.read();
    /* debug */ System.out.println("    b = " + b);
    int mask = (int) Math.pow(2, bits) - 1;
    /* debug */ System.out.println("    mask = " + mask);

    int bitIndex = index % (8 / bits);
    /* debug */ System.out.println("    bitIndex = " + bitIndex);

    int value = b & (mask << (bitIndex * bits));

    stream.seek(fp);
    return value;
  }

}
