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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.AxisGuesser;
import loci.formats.CoreMetadata;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffParser;

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
  public static final String PREFIX =
    "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><LEICA>";
  public static final String SUFFIX = "</LEICA>";

  public static final String[] XML_SUFFIX = {"xml"};

  // -- Fields --

  /** List of TIFF files. */
  private Vector<String> tiffs;

  /** Helper readers. */
  private TiffReader[] tiffReaders;

  private TiffParser tiffParser;

  private long datestamp;
  private String xmlFile;

  // -- Constructor --

  public TCSReader() {
    super("Leica TCS TIFF", new String[] {"tif", "tiff", "xml"});
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    if (checkSuffix(id, "xml")) return false;
    Location file = new Location(id);
    String[] list = file.getParentFile().list();
    for (String f : list) {
      if (checkSuffix(f, "xml") && DataTools.samePrefix(file.getName(), f)) {
        return false;
      }
    }
    return true;
  }

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
      traceDebug(e);
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
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String document = (String) ifd.get(new Integer(IFD.DOCUMENT_NAME));
    if (document == null) document = "";
    Object s = ifd.get(new Integer(IFD.SOFTWARE));
    String software = null;
    if (s instanceof String) software = (String) s;
    else if (s instanceof String[]) {
      String[] ss = (String[]) s;
      if (ss.length > 0) software = ss[0];
    }
    if (software == null) software = "";
    software = software.trim();
    return document.startsWith("CHANNEL") || software.startsWith("TCS");
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

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      String name = currentId.toLowerCase();
      if (checkSuffix(name, TiffReader.TIFF_SUFFIXES)) return null;
      return new String[] {currentId};
    }
    Vector<String> v = new Vector<String>();
    v.addAll(tiffs);

    String absoluteId = new Location(currentId).getAbsolutePath();
    if (!v.contains(absoluteId)) v.add(absoluteId);
    return v.toArray(new String[v.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      tiffs = null;
      tiffReaders = null;
      tiffParser = null;
      datestamp = 0;
      xmlFile = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("TCSReader.initFile(" + id + ")");

    Location l = new Location(id).getAbsoluteFile();
    Location parent = l.getParentFile();
    String[] list = parent.list();

    boolean isXML = checkSuffix(id, XML_SUFFIX);

    if (list != null) {
      for (String file : list) {
        if (checkSuffix(file, XML_SUFFIX) && !isXML) {
          xmlFile = new Location(parent, file).getAbsolutePath();
          break;
        }
        else if (checkSuffix(file, TiffReader.TIFF_SUFFIXES) && isXML) {
          initFile(new Location(parent, file).getAbsolutePath());
          return;
        }
      }
    }

    super.initFile(id);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    in = new RandomAccessInputStream(id);
    tiffParser = new TiffParser(in);
    tiffs = new Vector<String>();

    IFDList ifds = tiffParser.getIFDs();
    String date = ifds.get(0).getIFDStringValue(IFD.DATE_TIME, false);
    datestamp = DateTools.getTime(date, "yyyy:MM:dd HH:mm:ss");

    groupFiles();
    tiffReaders = new TiffReader[tiffs.size()];

    for (int i=0; i<tiffReaders.length; i++) {
      tiffReaders[i] = new TiffReader();
      tiffReaders[i].setId(tiffs.get(i));
    }

    int[] ch = new int[ifds.size()];
    int[] idx = new int[ifds.size()];
    long[] stamp = new long[ifds.size()];

    int channelCount = 0;

    FilePattern fp = new FilePattern(new Location(currentId).getAbsoluteFile());
    AxisGuesser guesser = new AxisGuesser(fp, "XYTZC", 1, ifds.size(), 1, true);

    core[0].sizeZ = 1;
    core[0].sizeC = tiffReaders[0].getSizeC();

    core[0].dimensionOrder = isRGB() ? "XYC" : "XY";

    if (isGroupFiles()) {
      int[] axisTypes = guesser.getAxisTypes();
      int[] count = fp.getCount();

      for (int i=axisTypes.length-1; i>=0; i--) {
        if (axisTypes[i] == AxisGuesser.Z_AXIS) {
          if (getDimensionOrder().indexOf("Z") == -1) {
            core[0].dimensionOrder += "Z";
          }
          core[0].sizeZ *= count[i];
        }
        else if (axisTypes[i] == AxisGuesser.C_AXIS) {
          if (getDimensionOrder().indexOf("C") == -1) {
            core[0].dimensionOrder += "C";
          }
          core[0].sizeC *= count[i];
        }
      }
    }

    for (int i=0; i<ifds.size(); i++) {
      String document = ifds.get(i).getIFDStringValue(IFD.DOCUMENT_NAME, false);
      if (document == null) continue;

      int index = document.indexOf("INDEX");
      String s = document.substring(8, index).trim();
      ch[i] = Integer.parseInt(s);
      if (ch[i] > channelCount) channelCount = ch[i];

      String n = document.substring(index + 6,
        document.indexOf(" ", index + 6)).trim();
      idx[i] = Integer.parseInt(n);

      date = document.substring(document.indexOf(" ", index + 6),
        document.indexOf("FORMAT")).trim();
      stamp[i] = DateTools.getTime(date, DATE_FORMAT);
    }

    core[0].sizeT = 0;

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
    if (getSizeZ() <= 1) {
      core[0].sizeZ = ifds.size() / (getSizeT() * channelCount);
    }
    core[0].sizeC *= channelCount;
    core[0].imageCount = getSizeZ() * getSizeT() * getSizeC();

    // cut up comment

    String comment = ifds.get(0).getComment();
    if (comment != null && comment.startsWith("[")) {
      String[] lines = comment.split("\n");
      for (String line : lines) {
        if (!line.startsWith("[")) {
          int eq = line.indexOf("=");
          String key = line.substring(0, eq).trim();
          String value = line.substring(eq + 1).trim();
          addGlobalMeta(key, value);
        }
      }
      metadata.remove("Comment");
    }
    core[0].sizeX = tiffReaders[0].getSizeX();
    core[0].sizeY = tiffReaders[0].getSizeY();
    core[0].rgb = tiffReaders[0].isRGB();
    core[0].pixelType = tiffReaders[0].getPixelType();
    core[0].littleEndian = tiffReaders[0].isLittleEndian();
    core[0].interleaved = tiffReaders[0].isInterleaved();
    core[0].falseColor = true;
    core[0].indexed = tiffReaders[0].isIndexed();

    if (isRGB()) core[0].imageCount /= (getSizeC() / channelCount);
    if (getSizeZ() * getSizeT() * getEffectiveSizeC() !=
      (ifds.size() * tiffReaders.length))
    {
      core[0].sizeZ = 1;
      int c = getEffectiveSizeC();
      if (c == 0) c = 1;
      core[0].sizeT = (ifds.size() * tiffReaders.length) / c;
      core[0].imageCount = getSizeT() * c;
    }

    MetadataTools.populatePixels(store, this, true);

    if (xmlFile != null) {
      // parse XML metadata

      String xml = DataTools.readFile(xmlFile);
      xml = XMLTools.sanitizeXML(PREFIX + xml + SUFFIX);

      LeicaHandler handler = new LeicaHandler(store);
      XMLTools.parseXML(xml, handler);

      metadata = handler.getGlobalMetadata();

      core = handler.getCoreMetadata().toArray(new CoreMetadata[0]);

      for (int i=0; i<getSeriesCount(); i++) {
        if (tiffs.size() < core[i].imageCount) {
          int div = core[i].imageCount / core[i].sizeC;
          core[i].imageCount = tiffs.size();
          if (div >= core[i].sizeZ) core[i].sizeZ /= div;
          else if (div >= core[i].sizeT) core[i].sizeT /= div;
        }
        core[i].dimensionOrder = getSizeZ() > getSizeT() ? "XYCZT" : "XYCTZ";
        core[i].rgb = false;
        core[i].interleaved = false;
        core[i].indexed = tiffReaders[0].isIndexed();
      }

      MetadataTools.populatePixels(store, this, true);

      for (int i=0; i<getSeriesCount(); i++) {
        MetadataTools.setDefaultCreationDate(store, id, i);
      }
    }
  }

  // -- Helper methods --

  private void groupFiles() throws FormatException, IOException {
    // look for associated TIFF files

    // we assume that two files are grouped if all of the following are true:
    //
    //  * the files are in the same directory
    //  * the file names are the same length
    //  * the time stamps are less than 60 seconds apart
    //  * the files have the same number of bytes

    Location current = new Location(currentId).getAbsoluteFile();
    Location parent = current.getParentFile();

    String[] list = parent.list();
    Arrays.sort(list);

    HashMap<String, Long> timestamps = new HashMap<String, Long>();

    for (String file : list) {
      if (checkSuffix(file, TiffReader.TIFF_SUFFIXES)) {
        file = new Location(parent, file).getAbsolutePath();
        if (file.length() != current.getAbsolutePath().length()) continue;

        RandomAccessInputStream rais = new RandomAccessInputStream(file);
        if (Math.abs(rais.length() - in.length()) > 16) continue;
        TiffParser tp = new TiffParser(rais);
        IFD ifd = tp.getIFDs().get(0);

        String date = ifd.getIFDStringValue(IFD.DATE_TIME, false);
        long stamp = DateTools.getTime(date, "yyyy:MM:dd HH:mm:ss");

        rais.close();
        String software = ifd.getIFDStringValue(IFD.SOFTWARE, false);
        if (software != null && software.trim().startsWith("TCS")) {
          timestamps.put(file, new Long(stamp));
        }
      }
    }

    String[] files = timestamps.keySet().toArray(new String[timestamps.size()]);
    Arrays.sort(files);

    if (checkSuffix(currentId, TiffReader.TIFF_SUFFIXES)) {
      tiffs.add(current.getAbsolutePath());
    }

    for (String file : files) {
      long thisStamp = timestamps.get(file).longValue();
      boolean match = false;
      for (String tiff : tiffs) {
        RandomAccessInputStream s = new RandomAccessInputStream(tiff);
        TiffParser parser = new TiffParser(s);
        IFD ifd = parser.getIFDs().get(0);
        s.close();

        String date = ifd.getIFDStringValue(IFD.DATE_TIME, false);
        long nextStamp = DateTools.getTime(date, "yyyy:MM:dd HH:mm:ss");
        if (Math.abs(thisStamp - nextStamp) < 60000) {
          match = true;
          break;
        }
      }
      if (match && !tiffs.contains(file)) tiffs.add(file);
    }
  }

}
