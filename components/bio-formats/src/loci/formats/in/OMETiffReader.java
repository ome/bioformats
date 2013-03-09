//
// OMETiffReader.java
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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

/**
 * OMETiffReader is the file format reader for
 * <a href="http://www.loci.wisc.edu/ome/ome-tiff-spec.html">OME-TIFF</a>
 * files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/OMETiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/OMETiffReader.java">SVN</a></dd></dl>
 */
public class OMETiffReader extends FormatReader {

  // -- Fields --

  /** Mapping from series and plane numbers to files and IFD entries. */
  protected OMETiffPlane[][] info; // dimensioned [numSeries][numPlanes]

  /** List of used files. */
  protected String[] used;

  private int lastPlane;
  private boolean hasSPW;

  // -- Constructor --

  /** Constructs a new OME-TIFF reader. */
  public OMETiffReader() {
    super("OME-TIFF", new String[] {"ome.tif", "ome.tiff"});
    suffixNecessary = false;
    suffixSufficient = false;
    domains = FormatTools.ALL_DOMAINS;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    // parse and populate OME-XML metadata
    String fileName = new Location(id).getAbsoluteFile().getAbsolutePath();
    RandomAccessInputStream ras = new RandomAccessInputStream(fileName);
    TiffParser tp = new TiffParser(ras);
    IFDList ifds = tp.getIFDs();
    ras.close();
    String xml = ifds.get(0).getComment();
    IMetadata meta = MetadataTools.createOMEXMLMetadata(xml);

    if (meta == null) {
      throw new FormatException("ome-xml.jar is required to read OME-TIFF " +
        "files.  Please download it from " +
        "http://loci.wisc.edu/ome/formats-library.html");
    }

    if (meta.getRoot() == null) {
      throw new FormatException("Could not parse OME-XML from TIFF comment");
    }

    int nImages = 0;
    for (int i=0; i<meta.getImageCount(); i++) {
      int nChannels = meta.getLogicalChannelCount(i);
      if (nChannels == 0) nChannels = 1;
      for (int p=0; p<meta.getPixelsCount(i); p++) {
        int z = meta.getPixelsSizeZ(i, p).intValue();
        int t = meta.getPixelsSizeT(i, p).intValue();
        nImages += z * t * nChannels;
      }
    }
    return nImages <= ifds.size();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    boolean validHeader = tp.isValidHeader();
    if (!validHeader) return false;
    // look for OME-XML in first IFD's comment
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String comment = ifd.getComment();
    if (comment == null) return false;
    return comment.trim().endsWith("</OME>");
  }

  /* @see loci.formats.IFormatReader#getDomains() */
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    return hasSPW ? new String[] {FormatTools.HCS_DOMAIN} :
      FormatTools.NON_HCS_DOMAINS;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if (info[series][lastPlane] == null ||
      info[series][lastPlane].reader == null ||
      info[series][lastPlane].id == null)
    {
      return null;
    }
    info[series][lastPlane].reader.setId(info[series][lastPlane].id);
    return info[series][lastPlane].reader.get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if (info[series][lastPlane] == null ||
      info[series][lastPlane].reader == null ||
      info[series][lastPlane].id == null)
    {
      return null;
    }
    info[series][lastPlane].reader.setId(info[series][lastPlane].id);
    return info[series][lastPlane].reader.get16BitLookupTable();
  }

  /*
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    lastPlane = no;
    int i = info[series][no].ifd;
    MinimalTiffReader r = (MinimalTiffReader) info[series][no].reader;
    if (r.getCurrentFile() == null) {
      r.setId(info[series][no].id);
    }
    IFD ifd = r.getIFDs().get(i);
    RandomAccessInputStream s =
      new RandomAccessInputStream(info[series][no].id);
    TiffParser p = new TiffParser(s);
    p.getSamples(ifd, buf, x, y, w, h);
    s.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    return noPixels ? null : used;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption() */
  public int fileGroupOption(String id) {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      info = null;
      used = null;
      lastPlane = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("OMETiffReader.initFile(" + id + ")");
    // normalize file name
    super.initFile(normalizeFilename(null, id));
    id = currentId;
    String dir = new File(id).getParent();

    // parse and populate OME-XML metadata
    String fileName = new Location(id).getAbsoluteFile().getAbsolutePath();
    RandomAccessInputStream ras = new RandomAccessInputStream(fileName);
    TiffParser tp = new TiffParser(ras);
    IFD firstIFD = tp.getFirstIFD();
    ras.close();
    String xml = firstIFD.getComment();
    IMetadata meta = MetadataTools.createOMEXMLMetadata(xml);

    hasSPW = meta.getPlateCount() > 0;

    Hashtable originalMetadata = MetadataTools.getOriginalMetadata(meta);
    if (originalMetadata != null) metadata = originalMetadata;

    debug(xml, 3);

    if (meta == null) {
      throw new FormatException("ome-xml.jar is required to read OME-TIFF " +
        "files.  Please download it from " +
        "http://loci.wisc.edu/ome/formats-library.html");
    }

    if (meta.getRoot() == null) {
      throw new FormatException("Could not parse OME-XML from TIFF comment");
    }

    String currentUUID = meta.getUUID();
    MetadataTools.ensureValidOMEXML(meta);
    MetadataTools.convertMetadata(meta, metadataStore);

    // determine series count from Image and Pixels elements
    int seriesCount = 0;
    int imageCount = meta.getImageCount();
    for (int i=0; i<imageCount; i++) seriesCount += meta.getPixelsCount(i);
    core = new CoreMetadata[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      core[i] = new CoreMetadata();
    }
    info = new OMETiffPlane[seriesCount][];

    // compile list of file/UUID mappings
    Hashtable<String, String> files = new Hashtable<String, String>();
    boolean needSearch = false;
    for (int i=0; i<imageCount; i++) {
      int pixelsCount = meta.getPixelsCount(i);
      for (int p=0; p<pixelsCount; p++) {
        int tiffDataCount = meta.getTiffDataCount(i, p);
        for (int td=0; td<tiffDataCount; td++) {
          String uuid = meta.getTiffDataUUID(i, p, td);
          String filename = null;
          if (uuid == null) {
            // no UUID means that TiffData element refers to this file
            uuid = "";
            filename = id;
          }
          else {
            filename = meta.getTiffDataFileName(i, p, td);
            if (!new Location(dir, filename).exists()) filename = null;
            if (filename == null) {
              if (uuid.equals(currentUUID)) {
                // UUID references this file
                filename = id;
              }
              else {
                // will need to search for this UUID
                filename = "";
                needSearch = true;
              }
            }
            else filename = normalizeFilename(dir, filename);
          }
          String existing = files.get(uuid);
          if (existing == null) files.put(uuid, filename);
          else if (!existing.equals(filename)) {
            throw new FormatException("Inconsistent UUID filenames");
          }
        }
      }
    }

    // search for missing filenames
    if (needSearch) {
      Enumeration en = files.keys();
      while (en.hasMoreElements()) {
        String uuid = (String) en.nextElement();
        String filename = files.get(uuid);
        if (filename.equals("")) {
          // TODO search...
          // should scan only other .ome.tif files
          // to make this work with OME server may be a little tricky?
          throw new FormatException("Unmatched UUID: " + uuid);
        }
      }
    }

    // build list of used files
    Enumeration en = files.keys();
    int numUUIDs = files.size();
    HashSet fileSet = new HashSet(); // ensure no duplicate filenames
    for (int i=0; i<numUUIDs; i++) {
      String uuid = (String) en.nextElement();
      String filename = files.get(uuid);
      fileSet.add(filename);
    }
    used = new String[fileSet.size()];
    Iterator iter = fileSet.iterator();
    for (int i=0; i<used.length; i++) used[i] = (String) iter.next();

    // process TiffData elements
    Hashtable<String, IFormatReader> readers =
      new Hashtable<String, IFormatReader>();
    int s = 0;
    for (int i=0; i<imageCount; i++) {
      debug("Image[" + i + "] {");
      debug("  id = " + meta.getImageID(i));
      int pixelsCount = meta.getPixelsCount(i);
      for (int p=0; p<pixelsCount; p++, s++) {
        debug("  Pixels[" + p + "] {");
        debug("    id = " + meta.getPixelsID(i, p));
        String order = meta.getPixelsDimensionOrder(i, p);

        Integer samplesPerPixel = meta.getLogicalChannelSamplesPerPixel(i, 0);
        int samples = samplesPerPixel == null ?
          -1 : samplesPerPixel.intValue();
        int tiffSamples = firstIFD.getSamplesPerPixel();
        if (samples != tiffSamples) {
          warn("SamplesPerPixel mismatch: OME=" + samples +
            ", TIFF=" + tiffSamples);
          samples = tiffSamples;
        }

        int effSizeC = meta.getPixelsSizeC(i, p).intValue() / samples;
        if (effSizeC == 0) effSizeC = 1;
        if (effSizeC * samples != meta.getPixelsSizeC(i, p).intValue()) {
          effSizeC = meta.getPixelsSizeC(i, p).intValue();
        }
        int sizeT = meta.getPixelsSizeT(i, p).intValue();
        int sizeZ = meta.getPixelsSizeZ(i, p).intValue();
        int num = effSizeC * sizeT * sizeZ;

        OMETiffPlane[] planes = new OMETiffPlane[num];
        for (int no=0; no<num; no++) planes[no] = new OMETiffPlane();

        int tiffDataCount = meta.getTiffDataCount(i, p);
        for (int td=0; td<tiffDataCount; td++) {
          debug("    TiffData[" + td + "] {");
          // extract TiffData parameters
          String filename = meta.getTiffDataFileName(i, p, td);
          String uuid = meta.getTiffDataUUID(i, p, td);
          Integer tdIFD = meta.getTiffDataIFD(i, p, td);
          int ifd = tdIFD == null ? 0 : tdIFD.intValue();
          Integer numPlanes = meta.getTiffDataNumPlanes(i, p, td);
          Integer firstC = meta.getTiffDataFirstC(i, p, td);
          Integer firstT = meta.getTiffDataFirstT(i, p, td);
          Integer firstZ = meta.getTiffDataFirstZ(i, p, td);
          int c = firstC == null ? 0 : firstC.intValue();
          int t = firstT == null ? 0 : firstT.intValue();
          int z = firstZ == null ? 0 : firstZ.intValue();

          // NB: some writers index FirstC, FirstZ and FirstT from 1
          if (c >= effSizeC) c--;
          if (z >= sizeZ) z--;
          if (t >= sizeT) t--;

          int index = FormatTools.getIndex(order,
            sizeZ, effSizeC, sizeT, num, z, c, t);
          int count = numPlanes == null ? 1 : numPlanes.intValue();
          if (count == 0) {
            core[s] = null;
            break;
          }

          // get reader object for this filename
          if (filename == null) {
            if (uuid == null) filename = id;
            else filename = files.get(uuid);
          }
          else filename = normalizeFilename(dir, filename);
          IFormatReader r = readers.get(filename);
          if (r == null) {
            r = new MinimalTiffReader();
            readers.put(filename, r);
          }

          Location file = new Location(filename);
          if (!file.exists()) {
            // if this is an absolute file name, try using a relative name
            // old versions of OMETiffWriter wrote an absolute path to
            // UUID.FileName, which causes problems if the file is moved to
            // a different directory
            filename =
              filename.substring(filename.lastIndexOf(File.separator) + 1);
            filename = dir + File.separator + filename;

            if (!new Location(filename).exists()) {
              filename = currentId;
            }
          }

          // populate plane index -> IFD mapping
          for (int q=0; q<count; q++) {
            int no = index + q;
            planes[no].reader = r;
            planes[no].id = filename;
            planes[no].ifd = ifd + q;
            planes[no].certain = true;
            debug("      Plane[" + no + "]: file=" +
              planes[no].id + ", IFD=" + planes[no].ifd);
          }
          if (numPlanes == null) {
            // unknown number of planes; fill down
            for (int no=index+1; no<num; no++) {
              if (planes[no].certain) break;
              planes[no].reader = r;
              planes[no].id = filename;
              planes[no].ifd = planes[no - 1].ifd + 1;
              debug("      Plane[" + no + "]: FILLED");
            }
          }
          else {
            // known number of planes; clear anything subsequently filled
            for (int no=index+count; no<num; no++) {
              if (planes[no].certain) break;
              planes[no].reader = null;
              planes[no].id = null;
              planes[no].ifd = -1;
              debug("      Plane[" + no + "]: CLEARED");
            }
          }
          debug("    }");
        }

        if (core[s] == null) continue;

        // verify that all planes are available
        debug("    --------------------------------");
        for (int no=0; no<num; no++) {
          debug("    Plane[" + no + "]: file=" +
            planes[no].id + ", IFD=" + planes[no].ifd);
          if (planes[no].reader == null) {
            warn("Pixels ID '" + meta.getPixelsID(i, p) + "': missing plane #" +
              no + ".  Using TiffReader to determine the number of planes.");
            TiffReader r = new TiffReader();
            r.setId(currentId);
            planes = new OMETiffPlane[r.getImageCount()];
            for (int plane=0; plane<planes.length; plane++) {
              planes[plane] = new OMETiffPlane();
              planes[plane].id = currentId;
              planes[plane].reader = r;
              planes[plane].ifd = plane;
            }
            num = planes.length;
          }
        }
        debug("  }");

        // populate core metadata
        info[s] = planes;
        try {
          core[s].sizeX = meta.getPixelsSizeX(i, p).intValue();
          int tiffWidth = (int) firstIFD.getImageWidth();
          if (core[s].sizeX != tiffWidth) {
            warn("SizeX mismatch: OME=" + core[s].sizeX +
              ", TIFF=" + tiffWidth);
          }
          core[s].sizeY = meta.getPixelsSizeY(i, p).intValue();
          int tiffHeight = (int) firstIFD.getImageLength();
          if (core[s].sizeY != tiffHeight) {
            warn("SizeY mismatch: OME=" + core[s].sizeY +
              ", TIFF=" + tiffHeight);
          }
          core[s].sizeZ = meta.getPixelsSizeZ(i, p).intValue();
          core[s].sizeC = meta.getPixelsSizeC(i, p).intValue();
          core[s].sizeT = meta.getPixelsSizeT(i, p).intValue();
          core[s].pixelType = FormatTools.pixelTypeFromString(
            meta.getPixelsPixelType(i, p));
          int tiffPixelType = firstIFD.getPixelType();
          if (core[s].pixelType != tiffPixelType) {
            warn("PixelType mismatch: OME=" + core[s].pixelType +
              ", TIFF=" + tiffPixelType);
            core[s].pixelType = tiffPixelType;
          }
          core[s].imageCount = num;
          core[s].dimensionOrder = meta.getPixelsDimensionOrder(i, p);
          core[s].orderCertain = true;
          int photo = firstIFD.getPhotometricInterpretation();
          core[s].rgb = samples > 1 || photo == PhotoInterp.RGB;
          if ((samples != core[s].sizeC && (samples % core[s].sizeC) != 0 &&
            (core[s].sizeC % samples) != 0) || core[s].sizeC == 1)
          {
            core[s].sizeC *= samples;
          }

          if (core[s].sizeZ * core[s].sizeT * core[s].sizeC >
            core[s].imageCount && !core[s].rgb)
          {
            if (core[s].sizeZ == core[s].imageCount) {
              core[s].sizeT = 1;
              core[s].sizeC = 1;
            }
            else if (core[s].sizeT == core[s].imageCount) {
              core[s].sizeZ = 1;
              core[s].sizeC = 1;
            }
            else if (core[s].sizeC == core[s].imageCount) {
              core[s].sizeT = 1;
              core[s].sizeZ = 1;
            }
          }

          core[s].littleEndian = !meta.getPixelsBigEndian(i, p).booleanValue();
          boolean tiffLittleEndian = firstIFD.isLittleEndian();
          if (core[s].littleEndian != tiffLittleEndian) {
            warn("BigEndian mismatch: OME=" + !core[s].littleEndian +
              ", TIFF=" + !tiffLittleEndian);
          }
          core[s].interleaved = false;
          core[s].indexed = photo == PhotoInterp.RGB_PALETTE &&
            firstIFD.getIFDValue(IFD.COLOR_MAP) != null;
          if (core[s].indexed) {
            core[s].rgb = false;
          }
          core[s].falseColor = false;
          core[s].metadataComplete = true;
        }
        catch (NullPointerException exc) {
          throw new FormatException("Incomplete Pixels metadata", exc);
        }
      }
      debug("}");
    }

    // remove null CoreMetadata entries

    Vector<CoreMetadata> series = new Vector<CoreMetadata>();
    Vector<OMETiffPlane[]> planeInfo = new Vector<OMETiffPlane[]>();
    for (int i=0; i<core.length; i++) {
      if (core[i] != null) {
        series.add(core[i]);
        planeInfo.add(info[i]);
      }
    }
    core = series.toArray(new CoreMetadata[series.size()]);
    info = planeInfo.toArray(new OMETiffPlane[0][0]);
  }

  // -- Helper methods --

  private String normalizeFilename(String dir, String name) {
     File file = new File(dir, name);
     if (file.exists()) return file.getAbsolutePath();
     return new Location(name).getAbsolutePath();
  }

  // -- Helper classes --

  /** Structure containing details on where to find a particular image plane. */
  private class OMETiffPlane {
    /** Reader to use for accessing this plane. */
    public IFormatReader reader;
    /** File containing this plane. */
    public String id;
    /** IFD number of this plane. */
    public int ifd = -1;
    /** Certainty flag, for dealing with unspecified NumPlanes. */
    public boolean certain = false;
  }

}
