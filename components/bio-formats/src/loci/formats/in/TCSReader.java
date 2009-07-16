//
// TCSReader.java
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

package loci.formats.in;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.TiffTools;
import loci.formats.meta.DummyMetadata;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;

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

  public static final String DATE_FORMAT = "yyyy:MM:dd HH:mm:ss.SSS";

  public static final String[] XML_SUFFIX = {"xml"};

  // -- Fields --

  /** List of TIFF files. */
  private Vector<String> tiffs;

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
      RandomAccessInputStream s = new RandomAccessInputStream(name);
      boolean isThisType = isThisType(s);
      s.close();
      return isThisType;
    }
    catch (IOException e) {
      if (debug) trace(e);
      return false;
    }
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    // check for Leica TCS IFD directory entries
    IFD ifd = TiffTools.getFirstIFD(stream);

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
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int n = no;
    for (int i=0; i<series; i++) {
      n += core[i].imageCount;
    }

    if (tiffReaders.length == 1) {
      return tiffReaders[0].openBytes(n, buf, x, y, w, h);
    }
    return tiffReaders[n].openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      String name = currentId.toLowerCase();
      if (name.endsWith(".tif") || name.endsWith(".tiff")) return null;
      return new String[] {currentId};
    }
    Vector<String> v = new Vector<String>();
    for (String f : tiffs) {
      v.add(f);
    }
    if (!v.contains(currentId)) v.add(currentId);
    return v.toArray(new String[0]);
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
    debug("TCSReader.initFile(" + id + ")");

    if (checkSuffix(id, TiffReader.TIFF_SUFFIXES)) {
      // find the associated XML file, if it exists
      Location l = new Location(id).getAbsoluteFile();
      Location parent = l.getParentFile();
      String[] list = parent.list();
      if (list != null) {
        for (int i=0; i<list.length; i++) {
          if (checkSuffix(list[i], XML_SUFFIX)) {
            initFile(new Location(parent, list[i]).getAbsolutePath());
            return;
          }
        }
      }
    }

    super.initFile(id);

    if (checkSuffix(id, XML_SUFFIX)) {
      in = new RandomAccessInputStream(id);
      MetadataStore store = new DummyMetadata();

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

      XMLTools.parseXML(xml, handler);

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
      metadata = handler.getGlobalMetadata();

      // look for associated TIFF files

      tiffs = new Vector<String>();

      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] list = parent.list();
      Arrays.sort(list);
      for (int i=0; i<list.length; i++) {
        if (checkSuffix(list[i], TiffReader.TIFF_SUFFIXES)) {
          String file = new Location(parent, list[i]).getAbsolutePath();
          IFD ifd = TiffTools.getIFDs(new RandomAccessInputStream(file)).get(0);
          String software = (String)
            TiffTools.getIFDValue(ifd, TiffTools.SOFTWARE);
          if (software != null && software.trim().equals("TCSNTV")) {
            tiffs.add(file);
          }
        }
      }

      tiffReaders = new TiffReader[tiffs.size()];

      for (int i=0; i<tiffReaders.length; i++) {
        tiffReaders[i] = new TiffReader();
        tiffReaders[i].setId(tiffs.get(i));
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

      store = new FilterMetadata(getMetadataStore(), isMetadataFiltered());
      MetadataTools.populatePixels(store, this, true);

      store.setInstrumentID("Instrument:0", 0);

      for (int i=0; i<x.size(); i++) {
        store.setImageName((String) seriesNames.get(i), i);
        MetadataTools.setDefaultCreationDate(store, id, i);

        // link Instrument and Image
        store.setImageInstrumentRef("Instrument:0", 0);
      }

      for (int i=0; i<x.size(); i++) {
        store.setDimensionsPhysicalSizeX((Float) xcal.get(i), i, 0);
        store.setDimensionsPhysicalSizeY((Float) ycal.get(i), i, 0);
        store.setDimensionsPhysicalSizeZ((Float) zcal.get(i), i, 0);
      }
      XMLTools.parseXML(xml, new LeicaHandler(store));
    }
    else {
      tiffs = new Vector<String>();
      tiffs.add(id);
      tiffReaders = new TiffReader[1];
      tiffReaders[0] = new TiffReader();
      tiffReaders[0].setMetadataStore(getMetadataStore());
      tiffReaders[0].setId(id);
      in = new RandomAccessInputStream(id);

      IFDList ifds = TiffTools.getIFDs(in);

      int[] ch = new int[ifds.size()];
      int[] idx = new int[ifds.size()];
      long[] stamp = new long[ifds.size()];

      int channelCount = 0;
      SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);

      for (int i=0; i<ifds.size(); i++) {
        String document = (String)
          ifds.get(i).get(new Integer(TiffTools.DOCUMENT_NAME));
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
      core[0].sizeZ = ifds.size() / (getSizeT() * channelCount);
      core[0].sizeC *= channelCount;
      core[0].imageCount = getSizeZ() * getSizeT() * channelCount;

      // cut up comment

      String comment = TiffTools.getComment(ifds.get(0));
      if (comment != null && comment.startsWith("[")) {
        StringTokenizer st = new StringTokenizer(comment, "\n");
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          if (!token.startsWith("[")) {
            int eq = token.indexOf("=");
            String key = token.substring(0, eq).trim();
            String value = token.substring(eq + 1).trim();
            addGlobalMeta(key, value);
          }
        }
        metadata.remove("Comment");
      }
      core = tiffReaders[0].getCoreMetadata();

      MetadataStore store =
        new FilterMetadata(getMetadataStore(), isMetadataFiltered());
      MetadataTools.populatePixels(store, this, true);
    }
  }

}
