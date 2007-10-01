//
// ChannelFiller.java
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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Expands indexed color images to RGB.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ChannelFiller.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ChannelFiller.java">SVN</a></dd></dl>
 */
public class ChannelFiller extends ReaderWrapper {

  // -- Constructors --

  /** Constructs a ChannelFiller around a new image reader. */
  public ChannelFiller() { super(); }

  /** Constructs a ChannelFiller with a given reader. */
  public ChannelFiller(IFormatReader r) { super(r); }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isIndexed() */
  public boolean isIndexed() {
    return false;
  }

  /* @see IFormatReader#isFalseColor() */
  public boolean isFalseColor() {
    return false;
  }

  /* @see IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    try {
      return reader.isFalseColor() ? reader.get8BitLookupTable() : null;
    }
    catch (FormatException e) { }
    catch (IOException e) { }
    return null;
  }

  /* @see IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() {
    try {
      return reader.isFalseColor() ? reader.get16BitLookupTable() : null;
    }
    catch (FormatException e) { }
    catch (IOException e) { }
    return null;
  }

  /* @see IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    if (reader.isIndexed() && !reader.isFalseColor()) {
      if (getPixelType() == FormatTools.UINT8) {
        byte[][] b = ImageTools.indexedToRGB(reader.get8BitLookupTable(),
          reader.openBytes(no));
        byte[] rtn = new byte[b.length * b[0].length];
        if (isInterleaved()) {
          int pt = 0;
          for (int i=0; i<b[0].length; i++) {
            for (int j=0; j<b.length; j++) {
              rtn[pt++] = b[j][i];
            }
          }
        }
        else {
          for (int i=0; i<b.length; i++) {
            System.arraycopy(b[i], 0, rtn, i*b[i].length, b[i].length);
          }
        }
        return rtn;
      }
      else {
        short[][] s = ImageTools.indexedToRGB(reader.get16BitLookupTable(),
          reader.openBytes(no), isLittleEndian());
        byte[] rtn = new byte[s.length * s[0].length * 2];

        if (isInterleaved()) {
          int pt = 0;
          for (int i=0; i<s[0].length; i++) {
            for (int j=0; j<s.length; j++) {
              rtn[pt++] = (byte) (isLittleEndian() ?
                (s[j][i] & 0xff) : (s[j][i] >> 8));
              rtn[pt++] = (byte) (isLittleEndian() ?
                (s[j][i] >> 8) : (s[j][i] & 0xff));
            }
          }
        }
        else {
          int pt = 0;
          for (int i=0; i<s.length; i++) {
            for (int j=0; j<s[i].length; j++) {
              rtn[pt++] = (byte) (isLittleEndian() ?
                (s[j][i] & 0xff) : (s[j][i] >> 8));
              rtn[pt++] = (byte) (isLittleEndian() ?
                (s[j][i] >> 8) : (s[j][i] & 0xff));
            }
          }
        }
        return rtn;
      }
    }
    return reader.openBytes(no);
  }

  /* @see IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    if (reader.isIndexed() && !reader.isFalseColor()) {
      return ImageTools.indexedToRGB(reader.openImage(no), isLittleEndian());
    }
    return reader.openImage(no);
  }

}
