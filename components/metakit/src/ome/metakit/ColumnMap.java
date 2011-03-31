
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
    String type = col.getTypeString();
    return !type.equals("S") && !type.equals("B");
  }

  // -- Helper methods --

  private void setup() throws IOException {
    /* debug */ System.out.println("reading ColumnMap from " + stream.getFilePointer());
    if (isFixedMap()) {
      // read a single IVecRef
      int ivecSize = MetakitTools.readBpInt(stream) / 2; // in nybbles?
      /* debug */ System.out.println("  fixed, with size = " + ivecSize + " bytes");
      if (ivecSize > 0) {
        int ivecLocation = MetakitTools.readBpInt(stream);
        long fp = stream.getFilePointer();
        /* debug */ System.out.println("  pointer = " + ivecLocation);
        stream.seek(ivecLocation - 6);

        for (int i=0; i<rowCount; i++) {
          values.add(readElement(ivecSize));
        }
        stream.seek(fp);
      }
    }
    else {
      // IVecRef -> IVecBinaryData
      // IVecRef -> IVecIntData (item sizes for IVecBinaryData)
      // IVecRef -> IVecCatalogData
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
        }
    }

    return null;
  }

}
