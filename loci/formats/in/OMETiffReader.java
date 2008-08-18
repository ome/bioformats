//
// OMETiffReader.java
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

import java.io.File;
import java.io.IOException;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;

/**
 * OMETiffReader is the file format reader for
 * <a href="http://www.loci.wisc.edu/ome/ome-tiff-spec.html">OME-TIFF</a>
 * files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/OMETiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/OMETiffReader.java">SVN</a></dd></dl>
 */
public class OMETiffReader extends FormatReader {

  // -- Fields --

  /** Mapping from series and plane numbers to files and IFD entries. */
  protected OMETiffPlane[][] info; // dimensioned [numSeries][numPlanes]

  /** List of used files. */
  protected String[] used;

  // -- Constructor --

  /** Constructs a new OME-TIFF reader. */
  public OMETiffReader() {
    super("OME-TIFF", new String[] {"ome.tif", "ome.tiff"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return TiffTools.isValidHeader(stream);
  }

  /*
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y,
    int width, int height) throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    IFormatReader r = info[series][no].reader;
    r.setId(info[series][no].id);
    return r.openBytes(info[series][no].ifd, buf, x, y, width, height);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return used;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption() */
  public int fileGroupOption(String id) {
    return FormatTools.MUST_GROUP;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OMETiffReader.initFile(" + id + ")");
    // normalize file name
    super.initFile(normalizeFilename(null, id));
    id = currentId;
    String dir = new File(id).getParent();

    // parse and populate OME-XML metadata
    String fileName = new Location(id).getAbsoluteFile().getAbsolutePath();
    RandomAccessStream ras = new RandomAccessStream(fileName);
    Hashtable firstIFD = TiffTools.getFirstIFD(ras);
    ras.close();
    String xml = TiffTools.getComment(firstIFD);
    MetadataRetrieve meta = (MetadataRetrieve)
      MetadataTools.createOMEXMLMetadata(xml);
    String currentUUID = meta.getUUID();
    MetadataTools.convertMetadata(meta, metadataStore);

    // determine series count from Image and Pixels elements
    int seriesCount = 0;
    int imageCount = meta.getImageCount();
    for (int i=0; i<imageCount; i++) seriesCount += meta.getPixelsCount(i);
    info = new OMETiffPlane[seriesCount][];

    // compile list of file/UUID mappings
    Hashtable files = new Hashtable();
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
          String existing = (String) files.get(uuid);
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
        String filename = (String) files.get(uuid);
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
      String filename = (String) files.get(uuid);
      fileSet.add(filename);
    }
    used = new String[fileSet.size()];
    Iterator iter = fileSet.iterator();
    for (int i=0; i<used.length; i++) used[i] = (String) iter.next();

    // HACK - for efficiency, assume all IFDs of all
    // constituent files have the same samples per pixel
    int samples = TiffTools.getSamplesPerPixel(firstIFD);

    // process TiffData elements
    Hashtable readers = new Hashtable();
    int s = 0;
    for (int i=0; i<imageCount; i++) {
      if (debug) {
        debug("Image[" + i + "] {");
        debug("  id = " + meta.getImageID(i));
      }
      int pixelsCount = meta.getPixelsCount(i);
      for (int p=0; p<pixelsCount; p++) {
        if (debug) {
          debug("  Pixels[" + p + "] {");
          debug("    id = " + meta.getPixelsID(i, p));
        }
        String order = meta.getPixelsDimensionOrder(i, p);
        int effSizeC = meta.getPixelsSizeC(i, p).intValue() / samples;
        int sizeT = meta.getPixelsSizeT(i, p).intValue();
        int sizeZ = meta.getPixelsSizeZ(i, p).intValue();
        int num = effSizeC * sizeT * sizeZ;

        OMETiffPlane[] planes = new OMETiffPlane[num];
        for (int no=0; no<num; no++) planes[no] = new OMETiffPlane();

        int tiffDataCount = meta.getTiffDataCount(i, p);
        for (int td=0; td<tiffDataCount; td++) {
          if (debug) debug("    TiffData[" + td + "] {");
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
          int index = FormatTools.getIndex(order,
            sizeZ, effSizeC, sizeT, num, z, c, t);
          int count = numPlanes == null ? 1 : numPlanes.intValue();

          // get reader object for this filename
          if (filename == null) {
            if (uuid == null) filename = id;
            else filename = (String) files.get(uuid);
          }
          else filename = normalizeFilename(dir, filename);
          IFormatReader r = (IFormatReader) readers.get(filename);
          if (r == null) {
            r = new TiffReader();
            readers.put(filename, r);
          }

          // populate plane index -> IFD mapping
          for (int q=0; q<count; q++) {
            int no = index + q;
            planes[no].reader = r;
            planes[no].id = filename;
            planes[no].ifd = ifd + q;
            planes[no].certain = true;
            if (debug) {
              debug("      Plane[" + no + "]: file=" +
                planes[no].id + ", IFD=" + planes[no].ifd);
            }
          }
          if (numPlanes == null) {
            // unknown number of planes; fill down
            for (int no=index+1; no<num; no++) {
              if (planes[no].certain) break;
              planes[no].reader = r;
              planes[no].id = filename;
              planes[no].ifd = planes[no - 1].ifd + 1;
              if (debug) debug("      Plane[" + no + "]: FILLED");
            }
          }
          else {
            // known number of planes; clear anything subsequently filled
            for (int no=index+count; no<num; no++) {
              if (planes[no].certain) break;
              planes[no].reader = null;
              planes[no].id = null;
              planes[no].ifd = -1;
              if (debug) debug("      Plane[" + no + "]: CLEARED");
            }
          }
          if (debug) debug("    }");
        }

        // verify that all planes are available
        if (debug) debug("    --------------------------------");
        for (int no=0; no<num; no++) {
          if (debug) {
            debug("    Plane[" + no + "]: file=" +
              planes[no].id + ", IFD=" + planes[no].ifd);
          }
          if (planes[no].reader == null) {
            throw new FormatException("Pixels ID '" +
              meta.getPixelsID(i, p) + "': missing plane #" + no);
          }
        }
        if (debug) debug("  }");

        // populate core metadata
        info[s] = planes;
        try {
          core.sizeX[s] = meta.getPixelsSizeX(i, p).intValue();
          core.sizeY[s] = meta.getPixelsSizeY(i, p).intValue();
          core.sizeZ[s] = meta.getPixelsSizeZ(i, p).intValue();
          core.sizeC[s] = meta.getPixelsSizeC(i, p).intValue();
          core.sizeT[s] = meta.getPixelsSizeT(i, p).intValue();
          core.pixelType[s] = FormatTools.pixelTypeFromString(
            meta.getPixelsPixelType(i, p));
          core.imageCount[s] = num;
          core.currentOrder[s] = meta.getPixelsDimensionOrder(i, p);
          core.orderCertain[s] = true;
          int photo = TiffTools.getPhotometricInterpretation(firstIFD);
          core.rgb[s] = samples > 1 || photo == TiffTools.RGB;
          core.littleEndian[s] = !meta.getPixelsBigEndian(i, p).booleanValue();
          core.interleaved[s] = false;
          core.indexed[s] = photo == TiffTools.RGB_PALETTE &&
            TiffTools.getIFDValue(firstIFD, TiffTools.COLOR_MAP) != null;
          if (core.indexed[s]) {
            core.sizeC[0] = 1;
            core.rgb[0] = false;
          }
          core.falseColor[s] = false;
          core.metadataComplete[s] = true;
        }
        catch (NullPointerException exc) {
          throw new FormatException("Incomplete Pixels metadata", exc);
        }
      }
      if (debug) debug("}");
    }
  }

  // -- Helper methods --

  private IFormatReader getReader(int no) throws FormatException, IOException {
    FormatTools.checkPlaneNumber(this, no);
    IFormatReader r = info[series][no].reader;
    r.setId(info[series][no].id);
    return r;
  }

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
