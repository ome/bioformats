//
// ByteVector.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.codec;

/**
 * A growable array of bytes.
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/ByteVector.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/ByteVector.java">SVN</a></dd></dl>
 *
 * @author Wayne Rasband wsr at nih.gov
 */
public class ByteVector {
  private byte[] data;
  private int size;

  public ByteVector() {
    data = new byte[10];
    size = 0;
  }

  public ByteVector(int initialSize) {
    data = new byte[initialSize];
    size = 0;
  }

  public ByteVector(byte[] byteBuffer) {
    data = byteBuffer;
    size = 0;
  }

  public void add(byte x) {
    while (size >= data.length) doubleCapacity();
    data[size++] = x;
  }

  public int size() {
    return size;
  }

  public void add(byte[] array) { add(array, 0, array.length); }

  public void add(byte[] array, int off, int len) {
    while (data.length < size + len) doubleCapacity();
    if (len == 1) data[size] = array[off];
    else if (len < 35) {
      // for loop is faster for small number of elements
      for (int i=0; i<len; i++) data[size + i] = array[off + i];
    }
    else System.arraycopy(array, off, data, size, len);
    size += len;
  }

  void doubleCapacity() {
    byte[] tmp = new byte[data.length*2 + 1];
    System.arraycopy(data, 0, tmp, 0, data.length);
    data = tmp;
  }

  public void clear() {
    size = 0;
  }

  public byte[] toByteArray() {
    byte[] bytes = new byte[size];
    System.arraycopy(data, 0, bytes, 0, size);
    return bytes;
  }

}
