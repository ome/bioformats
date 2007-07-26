//
// TiffIFDEntry.java
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

/**
 * This class represents a single raw TIFF IFD entry. It does not retrieve or
 * store the values from the entry's specific offset and is based on the TIFF
 * 6.0 specification of an IFD entry.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/TiffIFDEntry.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/TiffIFDEntry.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 */
public class TiffIFDEntry {

  /** The <i>Tag</i> that identifies the field. */
  private int tag;

  /** The field <i>Type</i>. */
  private int type;

  /** The number of values, <i>Count</i> of the indicated <i>Type</i>. */
  private int valueCount;

  /**
   * The <i>Value Offset</i>, the file offset (in bytes) of the <i>Value</i>
   * for the field.
   */
  private int valueOffset;

  public TiffIFDEntry(int tag, int type, int valueCount, int valueOffset) {
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
  public int getType() { return type; }

  /**
   * Retrieves the entry's <i>ValueCount</i> value.
   * @return the entry's <i>ValueCount</i> value.
   */
  public int getValueCount() { return valueCount; }

  /**
   * Retrieves the entry's <i>ValueOffset</i> value.
   * @return the entry's <i>ValueOffset</i> value.
   */
  public int getValueOffset() { return valueOffset; }

}
