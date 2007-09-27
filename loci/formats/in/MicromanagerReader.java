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

import java.io.*;
import java.util.*;
import loci.formats.*;

/**
 * MicromanagerReader is the file format reader for Micro-Manager files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/MicromanagerReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/MicromanagerReader.java">SVN</a></dd></dl>
 */
public class MicromanagerReader extends FormatReader {

  // -- Constants --

  /** File containing extra metadata. */
  private static final String METADATA = "metadata.txt";

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

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] b) {
    return tiffReader.isThisType(b);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    String[] s = new String[tiffs.size() + 1];
    tiffs.copyInto(s);
    s[tiffs.size()] = currentId;
    return s;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    tiffReader.setId((String) tiffs.get(no));
    return tiffReader.openBytes(0, buf);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (tiffReader != null) tiffReader.close(fileOnly);
    }
    else close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    File f = new File(name).getAbsoluteFile();
    String[] list = null;
    if (f.exists()) list = f.getParentFile().list();
    else list = (String[]) Location.getIdMap().keySet().toArray(new String[0]);

    if (list == null) return false;
    for (int i=0; i<list.length; i++) {
      if (list[i].endsWith("metadata.txt")) return super.isThisType(name, open);
    }
    return false;
  }

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (tiffReader != null) tiffReader.close();
    tiffReader = null;
    tiffs = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    tiffReader = new TiffReader();

    status("Reading metadata file");

    // find metadata.txt

    File file = new File(currentId).getAbsoluteFile();
    in = new RandomAccessStream(file.exists() ? new File(file.getParentFile(),
      METADATA).getAbsolutePath() : METADATA);
    String parent = file.exists() ? file.getParentFile().getAbsolutePath() : "";

    // usually a small file, so we can afford to read it into memory

    byte[] meta = new byte[(int) in.length()];
    in.read(meta);
    String s = new String(meta);
    meta = null;

    status("Finding image file names");

    // first find the name of each TIFF file
    tiffs = new Vector();
    int pos = 0;
    while (true) {
      pos = s.indexOf("FileName", pos);
      if (pos == -1 || pos >= in.length()) break;
      String name = s.substring(s.indexOf(":", pos), s.indexOf(",", pos));
      tiffs.add(0,
        parent + File.separator + name.substring(3, name.length() - 1));
      pos++;
    }

    // now parse the rest of the metadata

    status("Populating metadata");

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
          value = value.substring(0, value.length() - 1);
          addMeta(key, value);
          if (key.equals("Channels")) core.sizeC[0] = Integer.parseInt(value);
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
          value = value.substring(0, value.length() - 1);
          addMeta(key, value);
          if (key.equals("Channels")) core.sizeC[0] = Integer.parseInt(value);
        }
        else {
          String value =
            token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim();
          value = value.substring(0, value.length() - 1);
          addMeta(key, value);
          if (key.equals("Channels")) core.sizeC[0] = Integer.parseInt(value);
        }
      }
    }
    tiffReader.setId((String) tiffs.get(0));

    String z = (String) metadata.get("Slices");
    if (z != null) {
      core.sizeZ[0] = Integer.parseInt(z);
    }
    else core.sizeZ[0] = 1;

    String t = (String) metadata.get("Frames");
    if (t != null) {
      core.sizeT[0 ] = Integer.parseInt(t);
    }
    else core.sizeT[0] = tiffs.size() / core.sizeC[0];

    core.sizeX[0] = tiffReader.getSizeX();
    core.sizeY[0] = tiffReader.getSizeY();
    core.currentOrder[0] = "XYZCT";
    core.pixelType[0] = tiffReader.getPixelType();
    core.rgb[0] = tiffReader.isRGB();
    core.interleaved[0] = false;
    core.littleEndian[0] = tiffReader.isLittleEndian();
    core.imageCount[0] = tiffs.size();
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;

    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);

    FormatTools.populatePixels(store, this);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null);
      // TODO : retrieve min/max from the metadata
      //store.setChannelGlobalMinMax(i, getChannelGlobalMinimum(id, i),
      //  getChannelGlobalMaximum(id, i), null);
    }
  }

}
