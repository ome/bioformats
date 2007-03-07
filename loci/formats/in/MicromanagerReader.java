//
// MicromanagerReader.java
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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import loci.formats.*;

/** MicromanagerReader is the file format reader for Micro-Manager files. */
public class MicromanagerReader extends FormatReader {

  // -- Fields --

  /** Helper reader for TIFF files. */
  private TiffReader tiffReader;

  /** List of TIFF files to open. */
  private Vector tiffs;

  // -- Constructor --

  /** Constructs a new Micromanager reader. */
  public MicromanagerReader() { 
    super("Micro-Manager", new String[] {"tif", "tiff", "txt"});
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] b) {
    return tiffReader.isThisType(b);
  }

  /* @see IFormatReader#getImageCount(String) */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return tiffs.size();
  }

  /* @see IFormatReader#isLittleEndian(String) */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return tiffReader.isLittleEndian((String) tiffs.get(0));
  }

  /* @see IFormatReader#isInterleaved(String) */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /* @see IFormatReader#openBytes(String, int) */
  public byte[] openBytes(String id, int no) 
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    return tiffReader.openBytes((String) tiffs.get(no), 0);
  }

  /* @see IFormatReader#openBytes(String, int, byte[]) */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    return tiffReader.openBytes((String) tiffs.get(no), 0, buf);
  }

  /* @see IFormatReader#openImage(String, int) */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    return tiffReader.openImage((String) tiffs.get(no), 0);
  }

  /* @see IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    String min = (String) getMeta("ChContrastMin"); 
    if (min == null) return null; 
    if (sizeC[0] == 1) return new Double(min); 
    StringTokenizer st = new StringTokenizer(min, ",");
    int pos = 0;
    while (pos < theC) {
      st.nextToken();
      pos++;
    }
    min = st.nextToken();
    min.replaceAll("[", "").replaceAll("]", "").trim();
    return new Double(min); 
  }

  /* @see IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    String max = (String) getMeta("ChContrastMax"); 
    if (max == null) return null; 
    if (sizeC[0] == 1) return max == null ? null : new Double(max); 
    StringTokenizer st = new StringTokenizer(max, ",");
    int pos = 0;
    while (pos < theC) {
      st.nextToken();
      pos++;
    }
    max = st.nextToken();
    max.replaceAll("[", "").replaceAll("]", "").trim();
    return new Double(max);
  }

  /* @see IFormatReader#isMinMaxPopulated(String) */
  public boolean isMinMaxPopulated(String id) 
    throws FormatException, IOException
  {
    return getChannelGlobalMinimum(id, 0) != null &&
      getChannelGlobalMaximum(id, 0) != null; 
  }

  /* @see IFormatReader#close() */
  public void close() throws FormatException, IOException {
    if (tiffReader != null) tiffReader.close();
    tiffReader = null;
    currentId = null;
    tiffs = null;
  }
  
  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly) tiffReader.close(fileOnly);
    else close();
  }

  /* @see IFormatReader#initFile(String) */
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    tiffReader = new TiffReader();

    // find metadata.txt

    Location parent = new Location(currentId).getAbsoluteFile().getParentFile();
    RandomAccessStream ras = new RandomAccessStream(
      new Location(parent, "metadata.txt").getAbsolutePath());
  
    // usually a small file, so we can afford to read it into memory

    byte[] meta = new byte[(int) ras.length()];
    ras.read(meta);
    String s = new String(meta);
    meta = null;

    // first find the name of each TIFF file
    tiffs = new Vector();
    int pos = 0;
    while (true) {
      pos = s.indexOf("FileName", pos);
      if (pos == -1 || pos >= ras.length()) break;
      String name = s.substring(s.indexOf(":", pos), s.indexOf(",", pos));
      tiffs.add(0, name.substring(3, name.length() - 1));
      pos++; 
    }

    // now parse the rest of the metadata

    int start = s.indexOf("Summary");
    int end = s.indexOf("}", start);
    if (start != -1 && end > start) {
      s = s.substring(s.indexOf("\n", start), end).trim();
    }
 
    StringTokenizer st = new StringTokenizer(s, "\n");
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      boolean open = token.indexOf("[") != -1;
      boolean closed = token.indexOf("]") != -1; 
      if (open || (!open && !closed)) {
        int quote = token.indexOf("\"") + 1;
        String key = token.substring(quote, token.indexOf("\"", quote)); 
     
        if (!open && !closed) {
          String value = token.substring(token.indexOf(":") + 1).trim();
          addMeta(key, value.substring(0, value.length() - 1));
        }
        else if (!closed){
          StringBuffer valueBuffer = new StringBuffer();
          while (!closed) {
            token = st.nextToken();
            closed = token.indexOf("]") != -1;
            valueBuffer.append(token);
          }
          String value = valueBuffer.toString();
          value.replaceAll("\n", "").trim();
          addMeta(key, value.substring(0, value.length() - 1)); 
        }
        else {
          String value = 
            token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim();
          addMeta(key, value.substring(0, value.length() - 1));
        }
      }
    }
    sizeC[0] = Integer.parseInt((String) getMeta("Channels"));
    sizeZ[0] = 1;
    sizeT[0] = tiffs.size() / sizeC[0];
    sizeX[0] = tiffReader.getSizeX((String) tiffs.get(0));
    sizeY[0] = tiffReader.getSizeY((String) tiffs.get(0));
    currentOrder[0] = "XYCTZ";
    pixelType[0] = tiffReader.getPixelType((String) tiffs.get(0));
  
    MetadataStore store = getMetadataStore(id);

    store.setPixels(
      new Integer(sizeX[0]),
      new Integer(sizeY[0]),
      new Integer(sizeZ[0]),
      new Integer(sizeC[0]),
      new Integer(sizeT[0]),
      new Integer(pixelType[0]),
      new Boolean(isLittleEndian(id)),
      currentOrder[0],
      null, null);
    for (int i=0; i<sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
      store.setChannelGlobalMinMax(i, getChannelGlobalMinimum(id, i),
        getChannelGlobalMaximum(id, i), null);
    }
  }

  // -- FormatHandler API methods --

  /* @see FormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    Location parent = new Location(name).getAbsoluteFile().getParentFile();
    String[] list = parent.list();
    for (int i=0; i<list.length; i++) {
      if (list[i].endsWith("metadata.txt")) return super.isThisType(name, open);
    } 
    return false;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new MicromanagerReader().testRead(args);
  }

}
