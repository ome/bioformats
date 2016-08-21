/*
 * #%L
 * OME Metakit package for reading Metakit database files.
 * %%
 * Copyright (C) 2011 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package ome.metakit;

import java.io.IOException;
import java.util.ArrayList;

import loci.common.Constants;
import loci.common.RandomAccessInputStream;

/**
 * Class representating a column mapping in a Metakit database file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/metakit/src/ome/metakit/ColumnMap.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/metakit/src/ome/metakit/ColumnMap.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
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

  /** Return the list of values in this map's column. */
  public ArrayList getValueList() {
    return values;
  }

  /** Return an array of the values in this map's column. */
  public Object[] getValues() {
    return values.toArray(new Object[values.size()]);
  }

  /**
   * Return whether or not this map represents a fixed-type column.
   * Fixed-type columns have type "I", "F", "D", or "L".
   */
  public boolean isFixedMap() {
    char type = col.getTypeString().charAt(0);
    return type != 'S' && type != 'B';
  }

  // -- Helper methods --

  private void setup() throws IOException {
    if (isFixedMap()) {
      // read a single IVecRef

      int ivecSize = MetakitTools.readBpInt(stream);
      if (ivecSize > 0) {
        int ivecPointer = MetakitTools.readBpInt(stream);
        long fp = stream.getFilePointer();
        stream.seek(ivecPointer);

        for (int i=0; i<rowCount; i++) {
          values.add(readElement(ivecSize, i));
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
      int ivecSize = MetakitTools.readBpInt(stream); // total bytes
      int ivecPointer = MetakitTools.readBpInt(stream);

      int mapIvecSize = MetakitTools.readBpInt(stream);
      int mapIvecPointer = MetakitTools.readBpInt(stream);

      int catalogIvecSize = MetakitTools.readBpInt(stream);
      int catalogIvecPointer = 0;
      if (catalogIvecSize > 0) {
        catalogIvecPointer = MetakitTools.readBpInt(stream);
      }

      long fp = stream.getFilePointer();

      stream.seek(mapIvecPointer);
      int[] byteCounts = new int[rowCount];

      int bits = (mapIvecSize * 8) / rowCount;
      for (int i=0; i<rowCount; i++) {
        byteCounts[i] = readBits(mapIvecSize, bits, i);
      }

      stream.seek(ivecPointer);
      for (int i=0; i<rowCount; i++) {
        byte[] buf = new byte[byteCounts[i]];
        stream.read(buf);
        char type = col.getTypeString().charAt(0);
        values.add(type == 'B' ? buf : new String(buf, Constants.ENCODING));
      }

      stream.seek(fp);
    }
  }

  private Object readElement(int vectorSize, int index) throws IOException {
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
            return readBits(vectorSize, 1, index);
          case 2:
            return readBits(vectorSize, 2, index);
          case 4:
            return readBits(vectorSize, 4, index);
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
    else if (bits >= 32) {
      return stream.readInt();
    }

    long fp = stream.getFilePointer();
    stream.skipBytes((index * bits) / 8);
    int b = stream.read();
    int mask = (int) Math.pow(2, bits) - 1;

    int bitIndex = index % (8 / bits);
    int value = b & (mask << (bitIndex * bits));
    value >>= ((8 - (bitIndex * bits)) % 8);

    stream.seek(fp);
    return value;
  }

}
