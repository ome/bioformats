//
// TCSReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.*;
import java.text.*;
import java.util.*;
import loci.formats.*;
import loci.formats.in.TiffReader;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * TCSReader is the file format reader for Leica TCS TIFF files and their
 * companion XML file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/TCSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/TCSReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class TCSReader extends FormatReader {

  // -- Constants --

  public static final String[] XML_SUFFIX = {"xml"};

  // -- Fields --

  /** List of TIFF files. */
  private Vector tiffs;

  /** Helper readers. */
  private TiffReader[] tiffReaders;

  private Vector seriesNames;
  private Vector containerNames;
  private Vector containerCounts;
  private Vector xcal, ycal, zcal;
  private Vector x, y, z, c, t, bits;

  // -- Constructor --

  public TCSReader() {
    super("Leica TCS TIFF", new String[] {"tif", "tiff", "xml"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!open) return false; // not allowed to touch the file system

    // check that there is no LEI file
    String prefix = name;
    if (prefix.indexOf(".") != -1) {
      prefix = prefix.substring(0, prefix.lastIndexOf("."));
    }
    Location lei = new Location(prefix + ".lei");
    if (!lei.exists()) {
      lei = new Location(prefix + ".LEI");
      while (!lei.exists() && prefix.indexOf("_") != -1) {
        prefix = prefix.substring(0, prefix.lastIndexOf("_"));
        lei = new Location(prefix + ".lei");
        if (!lei.exists()) lei = new Location(prefix + ".LEI");
      }
    }
    if (lei.exists()) return false;
    try {
      return isThisType(new RandomAccessStream(name));
    }
    catch (IOException e) {
      if (debug) LogTools.trace(e);
      return false;
    }
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    // check for Leica TCS IFD directory entries
    Hashtable ifd = TiffTools.getFirstIFD(stream);

    if (ifd == null) return false;
    String document = (String) ifd.get(new Integer(TiffTools.DOCUMENT_NAME));
    if (document == null) document = "";
    Object s = ifd.get(new Integer(TiffTools.SOFTWARE));
    String software = null;
    if (s instanceof String) software = (String) s;
    else if (s instanceof String[]) {
      String[] ss = (String[]) s;
      if (ss.length > 0) software = ss[0];
    }
    if (software == null) software = "";
    return document.startsWith("CHANNEL") || software.trim().equals("TCSNTV");
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[0].get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[0].get16BitLookupTable();
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

    int n = no;
    for (int i=0; i<series; i++) {
      n += core[i].imageCount;
    }

    if (tiffReaders.length == 1) {
      return tiffReaders[0].openBytes(n, buf, x, y, w, h);
    }
    return tiffReaders[n].openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    Vector v = new Vector();
    for (int i=0; i<tiffs.size(); i++) {
      v.add(tiffs.get(i));
    }
    if (!v.contains(currentId)) v.add(currentId);
    return (String[]) v.toArray(new String[0]);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    tiffs = null;
    tiffReaders = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("TCSReader.initFile(" + id + ")");

    if (checkSuffix(id, TiffReader.TIFF_SUFFIXES)) {
      // find the associated XML file, if it exists
      Location l = new Location(id).getAbsoluteFile();
      Location parent = l.getParentFile();
      String[] list = parent.list();
      for (int i=0; i<list.length; i++) {
        if (checkSuffix(list[i], XML_SUFFIX)) {
          initFile(new Location(parent, list[i]).getAbsolutePath());
          return;
        }
      }
    }

    super.initFile(id);

    if (checkSuffix(id, XML_SUFFIX)) {
      in = new RandomAccessStream(id);
      MetadataStore store =
        new FilterMetadata(getMetadataStore(), isMetadataFiltered());

      // parse XML metadata

      LeicaHandler handler = new LeicaHandler(store);
      String prefix = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><LEICA>";
      String suffix = "</LEICA>";
      String xml = prefix + in.readString((int) in.length()) + suffix;

      for (int i=0; i<xml.length(); i++) {
        char ch = xml.charAt(i);
        if (Character.isISOControl(ch) || !Character.isDefined(ch)) {
          xml = xml.replace(ch, ' ');
        }
      }

      DataTools.parseXML(xml, handler);

      xcal = handler.getXCal();
      ycal = handler.getYCal();
      zcal = handler.getZCal();
      seriesNames = handler.getSeriesNames();
      containerNames = handler.getContainerNames();
      containerCounts = handler.getContainerCounts();
      x = handler.getWidths();
      y = handler.getHeights();
      z = handler.getZs();
      c = handler.getChannels();
      t = handler.getTs();
      bits = handler.getBits();
      metadata = handler.getMetadata();

      // look for associated TIFF files

      tiffs = new Vector();

      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] list = parent.list();
      Arrays.sort(list);
      for (int i=0; i<list.length; i++) {
        if (checkSuffix(list[i], TiffReader.TIFF_SUFFIXES)) {
          String file = new Location(parent, list[i]).getAbsolutePath();
          Hashtable ifd = TiffTools.getIFDs(new RandomAccessStream(file))[0];
          String software =
            (String) TiffTools.getIFDValue(ifd, TiffTools.SOFTWARE);
          if (software != null && software.trim().equals("TCSNTV")) {
            tiffs.add(file);
          }
        }
      }

      tiffReaders = new TiffReader[tiffs.size()];

      for (int i=0; i<tiffReaders.length; i++) {
        tiffReaders[i] = new TiffReader();
        tiffReaders[i].setId((String) tiffs.get(i));
      }

      core = new CoreMetadata[x.size()];

      for (int i=0; i<x.size(); i++) {
        core[i] = new CoreMetadata();
        core[i].sizeX = ((Integer) x.get(i)).intValue();
        core[i].sizeY = ((Integer) y.get(i)).intValue();
        if (z.size() > 0) core[i].sizeZ = ((Integer) z.get(i)).intValue();
        else core[i].sizeZ = 1;
        if (c.size() > 0) core[i].sizeC = ((Integer) c.get(i)).intValue();
        else core[i].sizeC = 1;
        if (t.size() > 0) core[i].sizeT = ((Integer) t.get(i)).intValue();
        else core[i].sizeT = 1;
        core[i].imageCount = core[i].sizeZ * core[i].sizeC * core[i].sizeT;
        int bpp = ((Integer) bits.get(i)).intValue();
        while (bpp % 8 != 0) bpp++;
        switch (bpp) {
          case 8:
            core[i].pixelType = FormatTools.UINT8;
            break;
          case 16:
            core[i].pixelType = FormatTools.UINT16;
            break;
          case 32:
            core[i].pixelType = FormatTools.FLOAT;
            break;
        }
        if (tiffs.size() < core[i].imageCount) {
          int div = core[i].imageCount / core[i].sizeC;
          core[i].imageCount = tiffs.size();
          if (div >= core[i].sizeZ) core[i].sizeZ /= div;
          else if (div >= core[i].sizeT) core[i].sizeT /= div;
        }
        core[i].dimensionOrder = getSizeZ() > getSizeT() ? "XYCZT" : "XYCTZ";
        core[i].metadataComplete = true;
        core[i].rgb = false;
        core[i].interleaved = false;
        core[i].indexed = tiffReaders[0].isIndexed();
        core[i].falseColor = true;
      }

      for (int i=0; i<x.size(); i++) {
        store.setImageName((String) seriesNames.get(i), i);
        MetadataTools.setDefaultCreationDate(store, id, i);
      }

      MetadataTools.populatePixels(store, this);

      for (int i=0; i<x.size(); i++) {
        store.setDimensionsPhysicalSizeX((Float) xcal.get(i), i, 0);
        store.setDimensionsPhysicalSizeY((Float) ycal.get(i), i, 0);
        store.setDimensionsPhysicalSizeZ((Float) zcal.get(i), i, 0);
      }
    }
    else {
      tiffs = new Vector();
      tiffs.add(id);
      tiffReaders = new TiffReader[1];
      tiffReaders[0] = new TiffReader();
      tiffReaders[0].setMetadataStore(getMetadataStore());
      tiffReaders[0].setId(id);
      in = new RandomAccessStream(id);

      Hashtable[] ifds = TiffTools.getIFDs(in);

      int[] ch = new int[ifds.length];
      int[] idx = new int[ifds.length];
      long[] stamp = new long[ifds.length];

      int channelCount = 0;
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");

      for (int i=0; i<ifds.length; i++) {
        String document =
          (String) ifds[i].get(new Integer(TiffTools.DOCUMENT_NAME));
        if (document == null) continue;

        int index = document.indexOf("INDEX");
        String s = document.substring(8, index).trim();
        ch[i] = Integer.parseInt(s);
        if (ch[i] > channelCount) channelCount = ch[i];

        String n = document.substring(index + 6,
          document.indexOf(" ", index + 6)).trim();
        idx[i] = Integer.parseInt(n);

        String date = document.substring(document.indexOf(" ", index + 6),
          document.indexOf("FORMAT")).trim();
        stamp[i] = fmt.parse(date, new ParsePosition(0)).getTime();
      }

      core[0].sizeT = 0;
      core[0].dimensionOrder = isRGB() ? "XYC" : "XY";

      // determine the axis sizes and ordering
      boolean unique = true;
      for (int i=0; i<stamp.length; i++) {
        for (int j=i+1; j<stamp.length; j++) {
          if (stamp[j] == stamp[i]) {
            unique = false;
            break;
          }
        }
        if (unique) {
          core[0].sizeT++;
          if (getDimensionOrder().indexOf("T") < 0) {
            core[0].dimensionOrder += "T";
          }
        }
        else if (i > 0) {
          if ((ch[i] != ch[i - 1]) && getDimensionOrder().indexOf("C") < 0) {
            core[0].dimensionOrder += "C";
          }
          else if (getDimensionOrder().indexOf("Z") < 0) {
            core[0].dimensionOrder += "Z";
          }
        }
        unique = true;
      }

      if (getDimensionOrder().indexOf("Z") < 0) core[0].dimensionOrder += "Z";
      if (getDimensionOrder().indexOf("C") < 0) core[0].dimensionOrder += "C";
      if (getDimensionOrder().indexOf("T") < 0) core[0].dimensionOrder += "T";

      if (getSizeT() == 0) core[0].sizeT = 1;
      if (channelCount == 0) channelCount = 1;
      core[0].sizeZ = ifds.length / (getSizeT() * channelCount);
      core[0].sizeC *= channelCount;
      core[0].imageCount = getSizeZ() * getSizeT() * channelCount;

      // cut up comment

      String comment = TiffTools.getComment(ifds[0]);
      if (comment != null && comment.startsWith("[")) {
        StringTokenizer st = new StringTokenizer(comment, "\n");
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          if (!token.startsWith("[")) {
            int eq = token.indexOf("=");
            String key = token.substring(0, eq);
            String value = token.substring(eq + 1);
            addMeta(key, value);
          }
        }
        metadata.remove("Comment");
      }
      core = tiffReaders[0].getCoreMetadata();
    }
  }

}
