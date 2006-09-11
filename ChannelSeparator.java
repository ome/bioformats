//
// ChannelSeparator.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
import java.util.Hashtable;

/** Logic to automatically separate the channels in a file. */
public class ChannelSeparator extends FormatReader {

  // -- Fields --

  /** FormatReader used to read the file. */
  private IFormatReader reader;

  // -- Constructor --

  /** Constructs a ChannelSeparator with the given reader. */
  public ChannelSeparator(IFormatReader r) throws FormatException {
    super("any", "*");
    if (r == null) throw new FormatException("Reader cannot be null.");
    reader = r;
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for this file format. */
  public boolean isThisType(byte[] block) {
    return reader.isThisType(block);
  }

  /** Determines the number of images in the given file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.isRGB(id) ? getSizeC(id) * reader.getImageCount(id) :
      reader.getImageCount(id);
  }

  /**
   * Obtains the hashtable containing the metadata field/value pairs from
   * the given file.
   */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getMetadata(id);
  }

  /**
   * Obtains the specified metadata field's value for the given file.
   *
   * @param field the name associated with the metadata field
   * @return the value, or null if the field doesn't exit
   */
  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return reader.getMetadataValue(id, field);
  }

  /**
   * Retrieves the current metadata store for this reader. You can be
   * assured that this method will <b>never</b> return a <code>null</code>
   * metadata store.
   * @return a metadata store implementation.
   */
  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return reader.getMetadataStore(id);
  }

  /**
   * Sets the default metadata store for this reader.
   *
   * @param store a metadata store implementation.
   */
  public void setMetadataStore(MetadataStore store) {
    metadataStore = store;
    reader.setMetadataStore(store);
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) {
    return false;
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getSizeX(id);
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getSizeY(id);
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getSizeZ(id);
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getSizeC(id);
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getSizeT(id);
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.isLittleEndian(id);
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return reader.getDimensionOrder(id);
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.isInterleaved(id);
  }

  /** Return the number of series in this file. */
  public int getSeriesCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getSeriesCount(id);
  }

  /** Activates the specified series. */
  public void setSeries(String id, int no) throws FormatException, IOException {
    if (no < 0 || no >= getSeriesCount(id)) {
      throw new FormatException("Invalid series: " + no);
    }
    series = no;
    reader.setSeries(id, no);
  }

  /** Obtains the specified image from the given file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (reader.isRGB(id)) {
      int c = getSizeC(id);
      int source = no / c;
      int channel = no % c;

      BufferedImage sourceImg = reader.openImage(id, source);
      return ImageTools.splitChannels(sourceImg)[channel];
    }
    else return reader.openImage(id, no);
  }

  /** Obtains the specified image from the given file, as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (reader.isRGB(id)) {
      int c = getSizeC(id);
      int source = no / c;
      int channel = no % c;

      byte[] sourceBytes = reader.openBytes(id, source);
      return ImageTools.splitChannels(sourceBytes, c,
        false, reader.isInterleaved(id))[channel];
    }
    else return reader.openBytes(id, no);
  }

  /** Closes the currently open file. */
  public void close() throws FormatException, IOException {
    if (reader != null) reader.close();
    currentId = null;
  }

}
