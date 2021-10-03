/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

/*
 * https://trac.openmicroscopy.org/ome/ticket/4151
 *
 * AxioVision TIFF format documentation:
 *
 * XML: Based on two z plane image.  /Volumes/ome/data_repo/easy-formats/zeiss-axiovision
 * <ROOT>
 *    <Tags>
 *       <Count> Number of following tag blocks </Count>
 *       Each tag block is three elements: <Vn>, <In>, and <An>.  Counts from zero.
 *       V is the tag value, I is the identifier, A is unused.
 *       @see ZeissTIFFHandler
 *    </Tags>
 *    <Scaling>
 *      Information about scaling in XYZT, but not needed since the scaling is already included in the main tags.
 *      Appears to duplicate some of the information in <Tags> and also add additional scaling tags.
 *    </Scaling>
 *    <Layers>
 *      This block of tags is for ROIs, which are collections of shapes in layers.
 *      <Key>
 *      <AttributeShape>
 *      <Count>
 *      <Itemn> [n==layer number]
 *        <Key>
 *        <Flags>
 *        <Shapes>
 *          <Key>
 *          <Count>
 *          <Itemm> [m==shape number, from 0]
 *             <Key>
 *                Shape number (Itemm + 1)
 *             <ShapeAttributes>
 *                 RBG may be RGBA (1 byte padding).  Flags may be 32 bits (4 byte offsets).
 *               [48-50] fill colour R/G/B
 *               [52-54] text colour R/G/B
 *               [56-58] draw colour R/G/B 0-255 each
 *               [60] line width (pixels)
 *               [64] draw style (line type)
 *               [68] fill style
 *               [76] 0=Regular, 1=Strikeout [font]
 *               [80] 44=Regular, 188=Bold [font]
 *               [81] 1=Regular, 2=Bold [font]
 *               [84] font size (points) [font]
 *               [88] 0=Regular, 1=Italic [font]
 *               [92] 0=Regular, 1=Underline [font]
 *               [96] ?
 *               [104] 18=white, 19=black?
 *               [136] 0=none 5= arrows, 6=end ticks (scale),  8 = filled arrows
 *               [144] 0=none 1=left 2=right 3=both (arrow or end ticks) (scale)
 *               [148,149] Both set to 255 when including tag name
 *               [152] Charset.  These are constants directly used from the Windows headers...
 *                 0=Western, 177=Hebrew, 178=Arabic, 161=Greek, 162=Turkish, 186=Baltic, 238=Central European, 204=Cyrillic, 222=Thai, 163=Vietnamese
 *             <DrawFeatures>
 *             <Text> xx µm (scale) Tag value if displaying tag.
 *             <InputMethod>
 *             <SourceTagId> Tag Id if displaying a tag value.  Needs converting to human-readable text.
 *             <SourceName>
 *             <PredefinedStrings>
 *             <FontName>
 *             <Name>
 *             <Dummy> Not used (scale bar)
 *             <HandleSize2> 4 (scale bar)
 *             <PointCount> 2 (scale bar)
 *             <Points> Note all values are 0-255; larger values are probably split across multiple elements...
 *                32 digits (scale bar) = 2 xy pairs at 8 bytes/pair
 *                  [5+6] x+y origin (scale)
 *                  [13+14,16-22,29-30] xy (scale bar)
 *                  []
 *                80 digits (text) = 5xy pairs at 8 bytes/pair
 *             <Features> Not used (scale bar)
 *    </Layers>
 *    <*>
 *      Numbered z planes.
 *      Contains only a <Tags> block, with the same Count, V/I/A elements as above.
 *    </*>
 *
 *    Metadata within the scaled down preview tiff: No (other than basic dimensions and image planes)
 *    Metadata within the separate TIFF z plane images: No (other than basic dimensions and image planes)
 */

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.Memoizer;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;

/**
 * ZeissTIFFReader is the file format reader for Zeiss AxioVision TIFF
 * files and their companion XML file.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com and Roger Leigh r.leigh at dundee.ac.uk
 */
public class ZeissTIFFReader extends BaseZeissReader {

  // -- Constants --

  public static final String[] TIFF_SUFFIXES = {"tif", "xml"};
  public static final String XML_NAME = "_meta.xml";

  // -- Fields --

  TIFFInfo tiffInfo;

  /** Image planes */
  ArrayList<Plane> planes;

  /** Helper reader for TIFF files. */
  private IFormatReader tiffReader;

  private transient HashMap<String, String[]> directoryCache =
    new HashMap<String, String[]>();

  // -- Constructor --

  public ZeissTIFFReader() {
    super("Zeiss AxioVision TIFF", TIFF_SUFFIXES);
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  protected String getPrefix(String name) {
    String ret = name;
    if (checkSuffix(name, new String[] {"tif", "zvi"})) {
      ret = name.substring(0, name.lastIndexOf("."));
    }
    return ret;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (!checkSuffix(name, TIFF_SUFFIXES) || !open) {
      return false;
    }

    try {
      TIFFInfo info = evalFile(name);
    }
    catch (Exception e) {
      LOGGER.trace("Type checking failed for " + name, e);
      return false;
    }

    return true;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    if (imageFiles[no] != null && new Location(imageFiles[no]).exists()) {
      Plane p = planes.get(no);
      tiffReader.setId(p.filename);
      tiffReader.openBytes(0, buf, x, y, w, h);
      tiffReader.close();
    } else {
      LOGGER.warn("File for image #{} ({}) is missing.", no, imageFiles[no]);
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    ArrayList<String> files = new ArrayList<String>();

    Location xml = new Location(tiffInfo.xmlname);
    if (xml.exists()) {
      files.add(xml.getAbsolutePath());
    }
    Location origname = new Location(tiffInfo.origname);
    if (!noPixels && tiffInfo.origname != null && origname.exists()) {
      files.add(origname.getAbsolutePath());
    }
    if (!noPixels) {
      for (String tiff : imageFiles) {
        Location tiffLocation = new Location(tiff);
        String path = tiffLocation.getAbsolutePath();
        if (tiffLocation.exists() && !files.contains(path)) {
          files.add(path);
        }
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (tiffReader != null) tiffReader.close(fileOnly);
    if (!fileOnly) {
      tiffInfo = null;
      tiffReader = null;
      planes = null;
      directoryCache.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /**
   * Evaluate the metadata of a TIFF/XML file collection, and return this to the user.  This is done by filling a TIFFInfo object containing all of the file and directory paths, plus the parsed metadata.
   * @param id the filename to evaluate.  This may be the top-level or per-plane TIFF, or the XML metadata.
   * @return an TIFFInfo object.
   * @throws FormatException
   * @throws IOException
   */
  protected TIFFInfo evalFile(String id) throws FormatException, IOException {
    // If this is an XML file, or one of the per-plane TIFFs, we must find the real basename now.
    // If it's a tiff, check for foo_meta.xml or foo_files/_meta.xml or _meta.xml.  For the latter, work out the basename from the XML itself.  Note that it might be missing, so should be optional.

    // The initial file is either:
    // Single plane
    // · A top-level TIFF (plus ${file}_meta.xml)
    // · A top-level ${file}_meta.xml (plus ${file} TIFF)
    // Multiple planes
    // · A top-level TIFF thumbnail (plus subdir with _meta.xml and per-plane TIFFs)
    // · A subdirectory _meta.xml (plus per-plane TIFFs and possible top-level TIFF thumbnail)
    // · A subdirectory per-plane TIFF (plus additional per-plane TIFFs, _meta.xml and possible top-level TIFF thumbnail)
    //
    // Start by trying to find the XML.  Then get the Filename from the XML, and use that to try to find the other files.  If there's no Filename tag, try to use the original filename.

    TIFFInfo info = new TIFFInfo();

    Location lxml = null;
    Location l = new Location(id).getAbsoluteFile();
    String name = l.getAbsolutePath();
    // This "original" name is only tentative; it might be set to to eiher the top-level image or thumbnail (if it's the XML file, and the top-level file exists, or the XML file if it does not exist)
    if (name.endsWith(".tif")) {
      // Now iterate through the various XML locations
      info.xmlname = name + XML_NAME;
      // If the XML file isn't present, check we're not in a subdirectory.
      lxml = new Location(info.xmlname);
      if (lxml.exists()) { // Simple single-plane case
        info.origname = name;
        info.basedir = null; // Always null for single files.
        info.multifile = false;
      } else { // Multiple planes
        Location lb = new Location(name + "_files");
        if (!lb.exists()) {
          lb = new Location(name + "_Files");
        }
        lxml = new Location(lb, XML_NAME);
        if (lb.exists() && lxml.exists()) { // Planes in subdirectory
          info.xmlname = lxml.getAbsolutePath();
          info.origname = name;
          info.basedir = lb.getAbsolutePath(); // Multifile
          info.multifile = true;
        } else { // Planes in this directory
          lb = new Location(l.getParent());
          lxml = new Location(lb, XML_NAME);
          if (lb.exists() && lxml.exists()) {
            info.xmlname = lxml.getAbsolutePath();
            info.origname = info.xmlname; // May be updated later
            info.basedir = lb.getAbsolutePath(); // Multifile
            info.multifile = true;
          } else {
            throw new FormatException("XML metadata not found");
          }
        }
      }
    } else if (name.endsWith(XML_NAME)) {
      info.xmlname = name;
      lxml = new Location(info.xmlname);
      if (!lxml.exists()) {
        throw new FormatException("XML metadata not found");
      }
      info.xmlname = lxml.getAbsolutePath();
      if (lxml.getName().equals(XML_NAME)) {// Multiple files
        Location lb = new Location(lxml.getParent());
        info.origname = info.xmlname;
        info.basedir = lb.getAbsolutePath();
        info.multifile = true;
      } else {
        info.origname = info.xmlname.substring(0,info.xmlname.length()-XML_NAME.length());
        info.basedir = null; // Single file
        info.multifile = false;
        l = new Location(info.origname);
        if (!l.exists()) {
          throw new FormatException("TIFF image data not found");
        }
        info.origname = l.getAbsolutePath();
      }
    } else {
      throw new FormatException("Invalid AxioVision TIFF XML");
    }

    String xml = DataTools.readFile(info.xmlname);
    info.handler =
        new ZeissTIFFHandler(this);
    XMLTools.parseXML(xml, info.handler);

    boolean found = false;
    for (Tag t : info.handler.main_tagset.tags) {
      if (t.getKey().equals("Filename")) {
        Location n = new Location(info.basedir, t.getValue());
        info.origname = n.getName();
        found = true;
        break;
      }
    }
    if (!found) {
        // We're out of luck.  We've got a _meta.xml, but it's not been possible to determine the original image name, and hence the filename prefix etc.
        // It's possible we could guess this by looking for the common prefix in the contents of the directory.
      if (info.origname.endsWith(XML_NAME)) {
        throw new FormatException("Image name not found in XML metadata");}
    }

    info.prefix = getPrefix(info.origname);
    if (info.basedir != null) {
      l = new Location(info.basedir);
      info.basedir = l.getAbsolutePath();
    }
    l = new Location(l.getParent(), info.prefix + ".tif");
    info.origname = lookup(l.getAbsolutePath());

    return info;
  }
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    TIFFInfo info = evalFile(id);
    if (new Location(info.origname).getAbsoluteFile().exists()) {
      super.initFile(info.origname);
    }
    else {
      super.initFile(id);
    }
    this.tiffInfo = info;
    super.initFileMain(info.origname);
  }

  @Override
  protected void initVars(String id) throws FormatException, IOException {
    super.initVars(id);

    tiffReader = new MinimalTiffReader();
    planes = new ArrayList<Plane>();
  }

  @Override
  protected void fillMetadataPass1(MetadataStore store) throws FormatException, IOException {
    super.fillMetadataPass1(store);
    int nplanes = tiffInfo.handler.planes.size();

    if (rawCount == 0)
      rawCount = nplanes;
    else if (rawCount != nplanes)
      LOGGER.warn("Problem determining correct number of image planes.  Document reported {}, while {} were found", rawCount, nplanes);
    if (rawCount == 0)
      LOGGER.warn("No image planes found");

    // Determine number of separate timepoints, channels, and z slices.
    for (ZeissTIFFHandler.Plane p : tiffInfo.handler.planes) {
      Plane np = new Plane();
      boolean tag_1047_present = false;
      for (Tag t : p.tagset.tags) {
        if (t.getKeyID() == 1047)
          tag_1047_present = true;
      }
      for (Tag t : p.tagset.tags) {
         // Use 1047 in preference to 1025 since 1025 is a
         // locale-specific text date rather than a float, and it's
         // better to use the latter.
        if (t.getKeyID() != 1025 || tag_1047_present == false)
          np.tags.put(t.getKey(), t.getValue());
      }
      np.taglist = p.tagset.tags;
      // Special case: _single plane is for base image only.  Should only occur when we don't have a _files directory.
      // Other planes: _files/_meta
      if (nplanes == 1 && !tiffInfo.multifile) {
        np.filename = lookup(tiffInfo.origname);
      }
      else {
        np.filename = lookup(tiffInfo.basedir + File.separator + tiffInfo.prefix + "_" + p.basename + ".tif");
      }

      int tileid = parseInt(np.tags.get("ImageTile Index"));
      int channelid = parseInt(np.tags.get("Image Channel Index"));
      int sliceid = parseInt(np.tags.get("Image Index Z"));
      int timepointid = parseInt(np.tags.get("Image Index T"));
      int xsize = parseInt(np.tags.get("Camera Frame Width"));
      int ysize = parseInt(np.tags.get("Camera Frame Height"));

      np.site = tileid;
      tileIndices.add(tileid);
      channelIndices.add(channelid);
      zIndices.add(sliceid);
      timepointIndices.add(timepointid);
      if (getSizeX() == 0) {
        core.get(0).sizeX = xsize;
        core.get(0).sizeY = ysize;
      }

      planes.add(np);
      if (bpp == 0) {
        tiffReader.setId(np.filename);
        IFDList ifds = ((MinimalTiffReader)tiffReader).getIFDs();
        tiffReader.close();
        IFD firstIFD = ifds.get(0);
        int bits = firstIFD.getBitsPerSample()[0];
        int samples = firstIFD.getSamplesPerPixel();
        bpp = (bits / 8) * samples;
      }
    }

    // Filter out images with an incomplete number of z slices.
    // This is to work around a probable bug in AxioVision.
    int full = Collections.max(tileIndices);
    int indexCount[] = new int[full+1];
    int max = 0;
    for (Plane plane : planes) {
      indexCount[plane.site]++;
      if (indexCount[plane.site] > max)
        max = indexCount[plane.site];
    }
    for (int i = 0; i < full+1; i++) {
      if (indexCount[i] != max)
          tileIndices.remove(i);
    }

    for (Iterator<Plane> i = planes.iterator(); i.hasNext();) {
      Plane plane = i.next();
      if (!tileIndices.contains(plane.site)) {
          i.remove();
      }
    }

    countImages(); // Allocates memory for arrays below

    for (int i = 0; i < planes.size(); i++) {
      Plane plane = planes.get(i);
      int channelid = parseInt(plane.tags.get("Image Channel Index"));
      int sliceid = parseInt(plane.tags.get("Image Index Z"));
      int timepointid = parseInt(plane.tags.get("Image Index T"));

      coordinates[i][0] = sliceid;
      coordinates[i][1] = channelid;
      coordinates[i][2] = timepointid;
      imageFiles[i] = plane.filename;
    }

    int total = tileIndices.size() * channelIndices.size() * zIndices.size() * timepointIndices.size();
    if(total != planes.size())
      LOGGER.warn("Number of image planes not detected correctly: total={} planes.size={}", total, planes.size());
    
    tiffReader = Memoizer.wrap(getMetadataOptions(), tiffReader);

  }

  @Override
  protected void fillMetadataPass2(MetadataStore store) throws FormatException, IOException {
    super.fillMetadataPass2(store);

    core.get(0).interleaved = false;
  }

  @Override
  protected void fillMetadataPass5(MetadataStore store) throws FormatException, IOException {
    super.fillMetadataPass5(store);

    for (int i = 0; i < planes.size(); i++) {
      Plane plane = planes.get(i);
      parseMainTags(i, store, plane.taglist);
    }
  }

  @Override
  protected void countImages()
  {
    core.get(0).imageCount = planes.size();

    super.countImages();
  }
  // -- Helpers --

  /**
   * Translate a file path from the XML file to an actual path on disk.
   * The path stored in the XML file may not have the same case as the path on
   * disk (typically all lower case on disk and possibly mixed case in XML).
   * If a matching file cannot be found, then the path from the XML file is returned.
   *
   * Uses {@link #directoryCache} so that any given directory is only listed once
   * per initialization, not once per lookup.
   */
  private String lookup(String src) {
    Location f = new Location(src);
    String parent = f.getParent();
    String name = f.getName();
    String[] list = directoryCache.get(parent);
    if (list == null) {
      list = f.getParentFile().list();
      Arrays.sort(list);
      directoryCache.put(parent, list);
    }
    for (String s : list) {
      if (s.equalsIgnoreCase(name)) {
        return new Location(f.getParentFile(), s).getAbsolutePath();
      }
    }
    return src;
  }

  class Plane
  {
    public String filename;
    public HashMap<String,String> tags = new HashMap<String,String>();
    public ArrayList<Tag> taglist;
    public int site;

    @Override
    public String
    toString()
    {
      StringBuilder s = new StringBuilder("---Plane---\n  file=");
      s.append(filename);
      s.append("\n keys=\n");
      for (String k : tags.keySet()) {
        s.append("    ");
        s.append(k);
        s.append("=");
        s.append(tags.get(k));
        s.append("\n");
      }
      return s.toString();
    }
  }

  class TIFFInfo
  {
    // Is file composed of multiple files?
    public boolean multifile = false;
    // Original name (to pass to initFile)
    public String origname = null;
    // Name of XML metadata
    public String xmlname = null;
    // Directory containing TIFF planes and XML data if multifile
    public String basedir = null;
    // Prefix for TIFF planes
    public String prefix = null;
    // XML SAX parser.
    ZeissTIFFHandler handler = null;
  }
}
