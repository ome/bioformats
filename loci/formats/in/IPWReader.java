//
// IPWReader.java
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
import java.text.*;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * IPWReader is the file format reader for Image-Pro Workspace (IPW) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/IPWReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/IPWReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class IPWReader extends BaseTiffReader {

  // -- Fields --

  private Vector imageFiles;
  private byte[] header;  // general image header data
  private byte[] tags; // tags data

  private POITools poi;

  // -- Constructor --

  /** Constructs a new IPW reader. */
  public IPWReader() { super("Image-Pro Workspace", "ipw"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    // all of our samples begin with 0xd0cf11e0
    return (block[0] == 0xd0 && block[1] == 0xcf &&
      block[2] == 0x11 && block[3] == 0xe0);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    RandomAccessStream stream =
      poi.getDocumentStream((String) imageFiles.get(0));
    ifds = TiffTools.getIFDs(stream);
    int[] bits = TiffTools.getBitsPerSample(ifds[0]);
    if (bits[0] <= 8) {
      int[] colorMap =
        (int[]) TiffTools.getIFDValue(ifds[0], TiffTools.COLOR_MAP);
      if (colorMap == null) return null;

      byte[][] table = new byte[3][colorMap.length / 3];
      int next = 0;
      for (int j=0; j<table.length; j++) {
        for (int i=0; i<table[0].length; i++) {
          table[j][i] = (byte) (colorMap[next++] >> 8);
        }
      }

      return table;
    }
    return null;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    RandomAccessStream stream =
      poi.getDocumentStream((String) imageFiles.get(no));
    ifds = TiffTools.getIFDs(stream);
    TiffTools.getSamples(ifds[0], stream, buf, x, y, w, h);
    stream.close();

    if (core.pixelType[0] == FormatTools.UINT16 ||
      core.pixelType[0] == FormatTools.INT16)
    {
      for (int i=0; i<buf.length; i+=2) {
        byte b = buf[i];
        buf[i] = buf[i + 1];
        buf[i + 1] = b;
      }
    }
    else if (core.pixelType[0] == FormatTools.UINT32 ||
      core.pixelType[0] == FormatTools.INT32)
    {
      for (int i=0; i<buf.length; i+=4) {
        byte b = buf[i];
        buf[i] = buf[i + 3];
        buf[i + 3] = b;
        b = buf[i + 1];
        buf[i + 1] = buf[i + 2];
        buf[i + 2] = b;
      }
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();

    header = null;
    tags = null;

    if (poi != null) poi.close();
    poi = null;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initMetadata() */
  public void initMetadata() throws FormatException, IOException {
    RandomAccessStream stream =
      poi.getDocumentStream((String) imageFiles.get(0));
    ifds = TiffTools.getIFDs(stream);
    stream.close();

    core.rgb[0] = (TiffTools.getIFDIntValue(ifds[0],
      TiffTools.SAMPLES_PER_PIXEL, false, 1) > 1);

    if (!core.rgb[0]) {
      core.indexed[0] = TiffTools.getIFDIntValue(ifds[0],
        TiffTools.PHOTOMETRIC_INTERPRETATION, false, 1) ==
        TiffTools.RGB_PALETTE;
    }
    if (core.indexed[0]) {
      core.sizeC[0] = 1;
      core.rgb[0] = false;
    }

    core.littleEndian[0] = TiffTools.isLittleEndian(ifds[0]);

    // parse the image description
    String description = new String(tags).trim();
    addMeta("Image Description", description);

    // default values

    core.sizeZ[0] = 1;
    core.sizeC[0] = 1;
    core.sizeT[0] = getImageCount();
    addMeta("slices", "1");
    addMeta("channels", "1");
    addMeta("frames", new Integer(getImageCount()));

    String timestamp = null;

    // parse the description to get channels/slices/times where applicable
    // basically the same as in SEQReader
    if (description != null) {
      StringTokenizer tokenizer = new StringTokenizer(description, "\n");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        String label = "Timestamp";
        String data;
        if (token.indexOf("=") != -1) {
          label = token.substring(0, token.indexOf("=")).trim();
          data = token.substring(token.indexOf("=") + 1).trim();
        }
        else {
          data = token.trim();
        }
        addMeta(label, data);
        if (label.equals("frames")) core.sizeZ[0] = Integer.parseInt(data);
        else if (label.equals("slices")) core.sizeT[0] = Integer.parseInt(data);
        else if (label.equals("channels")) {
          core.sizeC[0] = Integer.parseInt(data);
        }
        else if (label.equals("Timestamp")) timestamp = data;
      }
    }

    String version = new String(header).trim();
    addMeta("Version", version);

    Hashtable h = ifds[0];
    core.sizeX[0] = TiffTools.getIFDIntValue(h, TiffTools.IMAGE_WIDTH);
    core.sizeY[0] = TiffTools.getIFDIntValue(h, TiffTools.IMAGE_LENGTH);
    core.currentOrder[0] = core.rgb[0] ? "XYCTZ" : "XYTCZ";

    if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    if (core.sizeT[0] == 0) core.sizeT[0] = 1;

    if (core.rgb[0]) core.sizeC[0] *= 3;

    int bitsPerSample = TiffTools.getIFDIntValue(ifds[0],
      TiffTools.BITS_PER_SAMPLE);
    int bitFormat = TiffTools.getIFDIntValue(ifds[0], TiffTools.SAMPLE_FORMAT);

    while (bitsPerSample % 8 != 0) bitsPerSample++;
    if (bitsPerSample == 24 || bitsPerSample == 48) bitsPerSample /= 3;

    core.pixelType[0] = FormatTools.UINT8;

    if (bitFormat == 3) core.pixelType[0] = FormatTools.FLOAT;
    else if (bitFormat == 2) {
      switch (bitsPerSample) {
        case 8:
          core.pixelType[0] = FormatTools.INT8;
          break;
        case 16:
          core.pixelType[0] = FormatTools.INT16;
          break;
        case 32:
          core.pixelType[0] = FormatTools.INT32;
          break;
      }
    }
    else {
      switch (bitsPerSample) {
        case 8:
          core.pixelType[0] = FormatTools.UINT8;
          break;
        case 16:
          core.pixelType[0] = FormatTools.UINT16;
          break;
        case 32:
          core.pixelType[0] = FormatTools.UINT32;
          break;
      }
    }

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);

    if (timestamp != null) {
      if (timestamp.length() > 26) {
        timestamp = timestamp.substring(timestamp.length() - 26);
      }
      SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS aa");
      Date d = fmt.parse(timestamp, new ParsePosition(0));
      fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      store.setImageCreationDate(fmt.format(d), 0);
    }
    else {
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
    }

    MetadataTools.populatePixels(store, this);
    store.setImageDescription(description, 0);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("IPWReader.initFile(" + id + ")");

    currentId = id;
    metadata = new Hashtable();
    core = new CoreMetadata(1);
    Arrays.fill(core.orderCertain, true);
    getMetadataStore().createRoot();

    in = new RandomAccessStream(id);
    poi = new POITools(currentId);

    imageFiles = new Vector();

    Vector fileList = poi.getDocumentList();

    for (int i=0; i<fileList.size(); i++) {
      String name = (String) fileList.get(i);
      String relativePath =
        name.substring(name.lastIndexOf(File.separator) + 1);

      if (relativePath.equals("CONTENTS")) {
        header = poi.getDocumentBytes(name);
      }
      else if (relativePath.equals("FrameRate")) {
        byte[] b = poi.getDocumentBytes(name, 4);
        addMeta("Frame Rate", new Integer(DataTools.bytesToInt(b, true)));
      }
      else if (relativePath.equals("FrameInfo")) {
        byte[] b = poi.getDocumentBytes(name);
        for (int q=0; q<b.length/2; q++) {
          addMeta("FrameInfo " + q,
            new Short(DataTools.bytesToShort(b, q*2, 2, true)));
        }
      }
      else if (relativePath.equals("ImageInfo")) {
        tags = poi.getDocumentBytes(name);
      }
      else if (relativePath.equals("ImageTIFF")) {
        // pixel data
        String idx = "0";
        if (!name.substring(0,
          name.lastIndexOf(File.separator)).equals("Root Entry"))
        {
          idx = name.substring(21, name.indexOf(File.separator, 22));
        }

        int n = Integer.parseInt(idx);
        if (n < imageFiles.size()) imageFiles.setElementAt(name, n);
        else {
          int diff = n - imageFiles.size();
          for (int q=0; q<diff; q++) {
            imageFiles.add("");
          }
          imageFiles.add(name);
        }
        core.imageCount[0]++;
      }
    }

    status("Populating metadata");
    initMetadata();
  }

}
