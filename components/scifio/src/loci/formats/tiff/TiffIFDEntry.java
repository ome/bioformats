//
// TiffIFDEntry.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.tiff;

/**
 * This class represents a single raw TIFF IFD entry. It does not retrieve or
 * store the values from the entry's specific offset and is based on the TIFF
 * 6.0 specification of an IFD entry.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tiff/TiffIFDEntry.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tiff/TiffIFDEntry.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 */
public class TiffIFDEntry implements Comparable<Object> {

  /** The <i>Tag</i> that identifies the field. */
  private int tag;

  /** The field <i>Type</i>. */
  private IFDType type;

  /** The number of values, <i>Count</i> of the indicated <i>Type</i>. */
  private int valueCount;

  /**
   * The <i>Value Offset</i>, the file offset (in bytes) of the <i>Value</i>
   * for the field.
   */
  private long valueOffset;

  public TiffIFDEntry(int tag, IFDType type, int valueCount, long valueOffset) {
    this.tag = tag;
    this.type = type;
    this.valueCount = valueCount;
    this.valueOffset = valueOffset;
  }

  /**
   * Retrieves the entry's <i>Tag</i> value.
   * @return the entry's <i>Tag</i> value.
   */
  public int getTag() { return tag; }

  /**
   * Retrieves the entry's <i>Type</i> value.
   * @return the entry's <i>Type</i> value.
   */
  public IFDType getType() { return type; }

  /**
   * Retrieves the entry's <i>ValueCount</i> value.
   * @return the entry's <i>ValueCount</i> value.
   */
  public int getValueCount() { return valueCount; }

  /**
   * Retrieves the entry's <i>ValueOffset</i> value.
   * @return the entry's <i>ValueOffset</i> value.
   */
  public long getValueOffset() { return valueOffset; }

  public String toString() {
    return "tag = " + tag + ", type = " + type + ", count = " + valueCount +
      ", offset = " + valueOffset;
  }

  // -- Comparable API methods --

  public int compareTo(Object o) {
    if (!(o instanceof TiffIFDEntry)) return 1;
    long offset = ((TiffIFDEntry) o).getValueOffset();

    if (offset == getValueOffset()) return 0;
    return offset < getValueOffset() ? 1 : -1;
  }

}
