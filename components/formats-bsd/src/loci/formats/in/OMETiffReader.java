/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.Modulo;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

/**
 * OMETiffReader is the file format reader for
 * <a href="http://www.openmicroscopy.org/site/support/ome-model/ome-tiff/">OME-TIFF</a>
 * files.
 */
public class OMETiffReader extends FormatReader {

  // -- Fields --

  /** Mapping from series and plane numbers to files and IFD entries. */
  protected OMETiffPlane[][] info; // dimensioned [numSeries][numPlanes]

  /** List of used files. */
  protected String[] used;

  private int lastPlane = 0;
  private boolean hasSPW;

  private int[] tileWidth;
  private int[] tileHeight;

  private OMEXMLService service;
  private transient OMEXMLMetadata meta;
  private String metaFile;

  private String metadataFile;

  // -- Constructor --

  /** Constructs a new OME-TIFF reader. */
  public OMETiffReader() {
    super("OME-TIFF", new String[] {"ome.tif", "ome.tiff", "companion.ome"});
    suffixNecessary = false;
    suffixSufficient = false;
    domains = FormatTools.NON_GRAPHICS_DOMAINS;
    hasCompanionFiles = true;
    datasetDescription = "One or more .ome.tiff files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    // companion files in a binary-only dataset should always have additional files
    if (checkSuffix(id, "companion.ome")) {
      return false;
    }

    // parse and populate OME-XML metadata
    String fileName = new Location(id).getAbsoluteFile().getAbsolutePath();
    RandomAccessInputStream ras = new RandomAccessInputStream(fileName, 16);
    TiffParser tp = new TiffParser(ras);
    IFD ifd = tp.getFirstIFD();
    long[] ifdOffsets = tp.getIFDOffsets();
    ras.close();
    String xml = ifd.getComment();

    if (service == null) setupService();
    OMEXMLMetadata meta;
    try {
      meta = service.createOMEXMLMetadata(xml);
      metaFile = new Location(id).getAbsolutePath();
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }

    if (meta.getRoot() == null) {
      throw new FormatException("Could not parse OME-XML from TIFF comment");
    }

    int nImages = 0;
    for (int i=0; i<meta.getImageCount(); i++) {
      int nChannels = meta.getChannelCount(i);
      if (nChannels == 0) nChannels = 1;
      int z = meta.getPixelsSizeZ(i).getValue().intValue();
      int t = meta.getPixelsSizeT(i).getValue().intValue();
      nImages += z * t * nChannels;
    }
    return nImages > 0 && nImages <= ifdOffsets.length;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "companion.ome")) {
      // force the reader to pick up binary-only companion files
      return true;
    }
    metaFile = new Location(name).getAbsolutePath();
    boolean valid = super.isThisType(name, open);
    if (metadataFile != null) {
      // this is a binary-only file
      // overwrite XML with what is in the companion OME-XML file
      String dir = new File(metaFile).getParent();
      Location path = new Location(dir, metadataFile);
      LOGGER.debug("Checking metadata file {}", path);
      if (!path.exists()) return false;
      metadataFile = path.getAbsolutePath();

      try {
        String xml = readMetadataFile();
        service.createOMEXMLMetadata(xml);
      } catch (ServiceException se) {
        LOGGER.debug("OME-XML parsing failed", se);
        return false;
      } catch (IOException e) {
        return false;
      } catch (NullPointerException e) {
        return false;
      }
    }
    if (valid && !isGroupFiles()) {
      try {
        return isSingleFile(metaFile);
      }
      catch (Exception e) {
        LOGGER.debug("", e);
        return false;
      }
    }
    return valid;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    tp.setDoCaching(false);
    boolean validHeader = tp.isValidHeader();
    if (!validHeader) return false;
    // look for OME-XML in first IFD's comment
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    Object description = ifd.get(IFD.IMAGE_DESCRIPTION);
    if (description == null) {
      return false;
    }
    String comment = null;
    if (description instanceof TiffIFDEntry) {
      Object value = tp.getIFDValue((TiffIFDEntry) description);
      if (value != null) {
        comment = value.toString();
      }
    }
    else if (description instanceof String) {
      comment = (String) description;
    }
    if (comment == null || comment.trim().length() == 0) return false;

    comment = comment.trim();

    // do a basic sanity check before attempting to parse the comment as XML
    // the parsing step is a bit slow, so there is no sense in trying unless
    // we are reasonably sure that the comment contains XML
    if (!comment.startsWith("<") || !comment.endsWith(">")) {
      return false;
    }

    try {
      if (service == null) setupService();
      meta = service.createOMEXMLMetadata(comment);

      try {
        metadataFile = meta.getBinaryOnlyMetadataFile();
        // check the suffix to make sure that the MetadataFile is not
        // referencing the current OME-TIFF
        if (metadataFile != null)
        {
          return true;
        }
      }
      catch (NullPointerException e) {
      }

      for (int i=0; i<meta.getImageCount(); i++) {
        meta.setPixelsBigEndian(Boolean.TRUE, i);
        if (meta.getPixelsBinDataCount(i) > 0) {
          for (int j=0; j<meta.getPixelsBinDataCount(i); j++) {
            meta.setPixelsBinDataBigEndian(Boolean.TRUE, i, j);
          }
        }
        MetadataTools.verifyMinimumPopulated(meta, i);
      }
      return meta.getImageCount() > 0;
    }
    catch (ServiceException se) {
      LOGGER.debug("OME-XML parsing failed", se);
    }
    catch (NullPointerException e) {
      LOGGER.debug("OME-XML parsing failed", e);
    }
    catch (FormatException e) {
      LOGGER.debug("OME-XML parsing failed", e);
    }
    catch (IndexOutOfBoundsException e) {
      LOGGER.debug("OME-XML parsing failed", e);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#getDomains() */
  @Override
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    return hasSPW ? new String[] {FormatTools.HCS_DOMAIN} :
      FormatTools.NON_SPECIAL_DOMAINS;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    int series = getSeries();
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
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    int series = getSeries();
    if (info[series][lastPlane] == null ||
      info[series][lastPlane].reader == null ||
      info[series][lastPlane].id == null)
    {
      return null;
    }
    info[series][lastPlane].reader.setId(info[series][lastPlane].id);
    return info[series][lastPlane].reader.get16BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    super.reopenFile();
    for (int s=0; s<info.length; s++) {
      for (int q=0; q<info[s].length; q++) {
        // only reopen readers that had previously been initialized
        if (info[s][q] != null && info[s][q].reader != null &&
          info[s][q].reader.getCurrentFile() != null)
        {
          info[s][q].reader.reopenFile();
        }
      }
    }
  }

  /*
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    int series = getSeries();
    lastPlane = no;
    int i = info[series][no].ifd;

    if (!info[series][no].exists) {
      Arrays.fill(buf, (byte) 0);
      return buf;
    }

    MinimalTiffReader r = (MinimalTiffReader) info[series][no].reader;
    if (r.getCurrentFile() == null) {
      r.setId(info[series][no].id);
    }
    r.lastPlane = i;
    IFDList ifdList = r.getIFDs();
    if (i >= ifdList.size()) {
      LOGGER.warn("Error untangling IFDs; the OME-TIFF file may be malformed (IFD #{} missing).", i);
      return buf;
    }
    IFD ifd = ifdList.get(i);
    RandomAccessInputStream s =
      new RandomAccessInputStream(info[series][no].id);
    TiffParser p = new TiffParser(s);
    p.getSamples(ifd, buf, x, y, w, h);
    s.close();

    // reasonably safe to close the reader if the entire plane or
    // lower-right-most tile from a single plane file has been read
    if (r.getImageCount() == 1 && w + x == getSizeX() && h + y == getSizeY()) {
      r.close();
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    int series = getSeries();
    if (noPixels) return null;
    Vector<String> usedFiles = new Vector<String>();
    if (metadataFile != null) {
      usedFiles.add(metadataFile);
    }
    if (info != null && info[series] != null) {
      for (int i=0; i<info[series].length; i++) {
        if (!usedFiles.contains(info[series][i].id)) {
          usedFiles.add(info[series][i].id);
        }
      }
    }
    return usedFiles.toArray(new String[usedFiles.size()]);
  }

  /* @see loci.formats.IFormatReader#fileGroupOption() */
  @Override
  public int fileGroupOption(String id) {
    try {
      boolean single = isSingleFile(id);
      return single ? FormatTools.CAN_GROUP : FormatTools.MUST_GROUP;
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    catch (IOException e) {
      LOGGER.debug("", e);
    }
    return FormatTools.CAN_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (info != null) {
      for (OMETiffPlane[] dimension : info) {
        if (dimension == null) continue;
        for (OMETiffPlane plane : dimension) {
          if (plane.reader != null) {
            try {
              plane.reader.close();
            }
            catch (Exception e) {
              LOGGER.error("Plane closure failure!", e);
            }
          }
        }
      }
    }
    if (!fileOnly) {
      info = null;
      used = null;
      lastPlane = 0;
      tileWidth = null;
      tileHeight = null;
      metadataFile = null;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return tileWidth[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return tileHeight[getSeries()];
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    // normalize file name
    super.initFile(normalizeFilename(null, id));
    id = currentId;
    String dir = new File(id).getParent();

    // parse and populate OME-XML metadata
    String fileName = new Location(id).getAbsoluteFile().getAbsolutePath();
    if (!new File(fileName).exists()) {
      fileName = currentId;
    }
    String xml;
    IFD firstIFD = null;

    boolean companion = false;
    if (checkSuffix(fileName, "companion.ome")) {
      xml = DataTools.readFile(fileName);
      companion = true;
    }
    else {
      RandomAccessInputStream ras = new RandomAccessInputStream(fileName, 16);
      try {
        TiffParser tp = new TiffParser(ras);
        firstIFD = tp.getFirstIFD();
        xml = firstIFD.getComment();
      }
      finally {
        ras.close();
      }
    }

    if (service == null) setupService();
    try {
      if (meta == null || !metaFile.equals(currentId)) {
        meta = service.createOMEXMLMetadata(xml);
        metaFile = currentId;
      }
      if (companion) {
        String firstTIFF = meta.getUUIDFileName(0, 0);
        initFile(new Location(dir, firstTIFF).getAbsolutePath());
        return;
      }
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }

    String metadataPath = null;
    try {
      metadataPath = meta.getBinaryOnlyMetadataFile();
    }
    catch (NullPointerException e) {
    }

    if (metadataPath != null) {
      // this is a binary-only file
      // overwrite XML with what is in the companion OME-XML file
      Location path = new Location(dir, metadataPath);
      if (path.exists()) {
        metadataFile = path.getAbsolutePath();
        xml = readMetadataFile();

        try {
          meta = service.createOMEXMLMetadata(xml);
        }
        catch (ServiceException se) {
          throw new FormatException(se);
        }
        catch (NullPointerException e) {
          metadataFile = null;
          metadataPath = null;
        }
      }
    }

    hasSPW = meta.getPlateCount() > 0;

    for (int i=0; i<meta.getImageCount(); i++) {
      int sizeC = meta.getPixelsSizeC(i).getValue().intValue();
      service.removeChannels(meta, i, sizeC);
    }

    Hashtable originalMetadata = service.getOriginalMetadata(meta);
    if (originalMetadata != null) metadata = originalMetadata;

    LOGGER.trace(xml);

    if (meta.getRoot() == null) {
      throw new FormatException("Could not parse OME-XML from TIFF comment");
    }

    String[] acquiredDates = new String[meta.getImageCount()];
    for (int i=0; i<acquiredDates.length; i++) {
      Timestamp acquisitionDate = meta.getImageAcquisitionDate(i);
      if (acquisitionDate != null) {
        acquiredDates[i] = acquisitionDate.getValue();
      }
    }

    String currentUUID = meta.getUUID();
    service.convertMetadata(meta, metadataStore);

    // determine series count from Image and Pixels elements
    int seriesCount = meta.getImageCount();
    core.clear();
    for (int i=0; i<seriesCount; i++) {
      core.add(new CoreMetadata());
    }
    info = new OMETiffPlane[seriesCount][];

    tileWidth = new int[seriesCount];
    tileHeight = new int[seriesCount];
    // compile list of file/UUID mappings
    Hashtable<String, String> files = new Hashtable<String, String>();
    boolean needSearch = false;
    for (int i=0; i<seriesCount; i++) {
      int tiffDataCount = meta.getTiffDataCount(i);
      for (int td=0; td<tiffDataCount; td++) {
        String uuid = null;
        try {
          uuid = meta.getUUIDValue(i, td);
        }
        catch (NullPointerException e) { }
        String filename = null;
        if (uuid == null) {
          // no UUID means that TiffData element refers to this file
          uuid = "";
          filename = id;
        }
        else {
          filename = meta.getUUIDFileName(i, td);
          if (!new Location(dir, filename).exists()) filename = null;
          if (filename == null) {
            if (uuid.equals(currentUUID) || currentUUID == null) {
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
    boolean adjustedSamples = false;
    for (int i=0; i<seriesCount; i++) {
      int s = i;
      LOGGER.debug("Image[{}] {", i);
      LOGGER.debug("  id = {}", meta.getImageID(i));

      String order = meta.getPixelsDimensionOrder(i).toString();

      PositiveInteger samplesPerPixel = null;
      if (meta.getChannelCount(i) > 0) {
        samplesPerPixel = meta.getChannelSamplesPerPixel(i, 0);
      }
      int samples = samplesPerPixel == null ?  -1 : samplesPerPixel.getValue();
      int tiffSamples = firstIFD.getSamplesPerPixel();

      if (adjustedSamples ||
        (samples != tiffSamples && (i == 0 || samples < 0)))
      {
        LOGGER.warn("SamplesPerPixel mismatch: OME={}, TIFF={}",
          samples, tiffSamples);
        samples = tiffSamples;
        adjustedSamples = true;
      }
      else {
        adjustedSamples = false;
      }

      if (adjustedSamples && meta.getChannelCount(i) <= 1) {
        adjustedSamples = false;
      }

      int effSizeC = meta.getPixelsSizeC(i).getValue().intValue();
      if (!adjustedSamples) {
        effSizeC /= samples;
      }
      if (effSizeC == 0) effSizeC = 1;
      if (effSizeC * samples != meta.getPixelsSizeC(i).getValue().intValue()) {
        effSizeC = meta.getPixelsSizeC(i).getValue().intValue();
      }
      int sizeT = meta.getPixelsSizeT(i).getValue().intValue();
      int sizeZ = meta.getPixelsSizeZ(i).getValue().intValue();
      int num = effSizeC * sizeT * sizeZ;

      OMETiffPlane[] planes = new OMETiffPlane[num];
      for (int no=0; no<num; no++) planes[no] = new OMETiffPlane();

      int tiffDataCount = meta.getTiffDataCount(i);
      Boolean zOneIndexed = null;
      Boolean cOneIndexed = null;
      Boolean tOneIndexed = null;

      // pre-scan TiffData indices to see if any of them are indexed from 1

      for (int td=0; td<tiffDataCount; td++) {
        NonNegativeInteger firstC = meta.getTiffDataFirstC(i, td);
        NonNegativeInteger firstT = meta.getTiffDataFirstT(i, td);
        NonNegativeInteger firstZ = meta.getTiffDataFirstZ(i, td);
        int c = firstC == null ? 0 : firstC.getValue();
        int t = firstT == null ? 0 : firstT.getValue();
        int z = firstZ == null ? 0 : firstZ.getValue();

        if (c >= effSizeC && cOneIndexed == null) {
          cOneIndexed = true;
        }
        else if (c == 0) {
          cOneIndexed = false;
        }
        if (z >= sizeZ && zOneIndexed == null) {
          zOneIndexed = true;
        }
        else if (z == 0) {
          zOneIndexed = false;
        }
        if (t >= sizeT && tOneIndexed == null) {
          tOneIndexed = true;
        }
        else if (t == 0) {
          tOneIndexed = false;
        }
      }

      for (int td=0; td<tiffDataCount; td++) {
        LOGGER.debug("    TiffData[{}] {", td);
        // extract TiffData parameters
        String filename = null;
        String uuid = null;
        try {
          filename = meta.getUUIDFileName(i, td);
        } catch (NullPointerException e) {
          LOGGER.debug("Ignoring null UUID object when retrieving filename.");
        }
        try {
          uuid = meta.getUUIDValue(i, td);
        } catch (NullPointerException e) {
          LOGGER.debug("Ignoring null UUID object when retrieving value.");
        }
        NonNegativeInteger tdIFD = meta.getTiffDataIFD(i, td);
        int ifd = tdIFD == null ? 0 : tdIFD.getValue();
        NonNegativeInteger numPlanes = meta.getTiffDataPlaneCount(i, td);
        NonNegativeInteger firstC = meta.getTiffDataFirstC(i, td);
        NonNegativeInteger firstT = meta.getTiffDataFirstT(i, td);
        NonNegativeInteger firstZ = meta.getTiffDataFirstZ(i, td);
        int c = firstC == null ? 0 : firstC.getValue();
        int t = firstT == null ? 0 : firstT.getValue();
        int z = firstZ == null ? 0 : firstZ.getValue();

        // NB: some writers index FirstC, FirstZ and FirstT from 1
        if (cOneIndexed != null && cOneIndexed) c--;
        if (zOneIndexed != null && zOneIndexed) z--;
        if (tOneIndexed != null && tOneIndexed) t--;

        if (z >= sizeZ || c >= effSizeC || t >= sizeT) {
          LOGGER.warn("Found invalid TiffData: Z={}, C={}, T={}",
            new Object[] {z, c, t});
          break;
        }

        int index = FormatTools.getIndex(order,
          sizeZ, effSizeC, sizeT, num, z, c, t);
        int count = numPlanes == null ? 1 : numPlanes.getValue();
        if (count == 0) {
          core.set(s, null);
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
        boolean exists = true;
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
            // if only one file is defined, we have to assume that it
            // corresponds to the current file
            exists = fileSet.size() == 1;
          }
        }

        // populate plane index -> IFD mapping
        for (int q=0; q<count; q++) {
          int no = index + q;
          planes[no].reader = r;
          planes[no].id = filename;
          planes[no].ifd = ifd + q;
          planes[no].certain = true;
          planes[no].exists = exists;
          LOGGER.debug("      Plane[{}]: file={}, IFD={}",
            new Object[] {no, planes[no].id, planes[no].ifd});
        }
        if (numPlanes == null) {
          // unknown number of planes; fill down
          for (int no=index+1; no<num; no++) {
            if (planes[no].certain) break;
            planes[no].reader = r;
            planes[no].id = filename;
            planes[no].ifd = planes[no - 1].ifd + 1;
            planes[no].exists = exists;
            LOGGER.debug("      Plane[{}]: FILLED", no);
          }
        }
        else {
          // known number of planes; clear anything subsequently filled
          for (int no=index+count; no<num; no++) {
            if (planes[no].certain) break;
            planes[no].reader = null;
            planes[no].id = null;
            planes[no].ifd = -1;
            LOGGER.debug("      Plane[{}]: CLEARED", no);
          }
        }
        LOGGER.debug("    }");
      }

      if (core.get(s) == null) continue;

      // verify that all planes are available
      LOGGER.debug("    --------------------------------");
      for (int no=0; no<num; no++) {
        LOGGER.debug("    Plane[{}]: file={}, IFD={}",
          new Object[] {no, planes[no].id, planes[no].ifd});
        if (planes[no].reader == null) {
          LOGGER.warn("Image ID '{}': missing plane #{}.  " +
            "Using TiffReader to determine the number of planes.",
            meta.getImageID(i), no);
          TiffReader r = new TiffReader();
          r.setId(currentId);
          try {
            planes = new OMETiffPlane[r.getImageCount()];
            for (int plane=0; plane<planes.length; plane++) {
              planes[plane] = new OMETiffPlane();
              planes[plane].id = currentId;
              planes[plane].reader = r;
              planes[plane].ifd = plane;
            }
            num = planes.length;
          }
          finally {
            r.close();
          }
        }
      }
      LOGGER.debug("  }");

      // populate core metadata
      CoreMetadata m = core.get(s);
      info[s] = planes;
      try {
        RandomAccessInputStream testFile = new RandomAccessInputStream(info[s][0].id);
        String firstFile = info[s][0].id;
        if (!info[s][0].reader.isThisType(testFile)) {
          LOGGER.warn("{} is not a valid OME-TIFF", info[s][0].id);
          info[s][0].id = currentId;
          info[s][0].exists = false;
        }
        testFile.close();
        for (int plane=1; plane<info[s].length; plane++) {
          if (info[s][plane].id.equals(firstFile)) {
            // don't repeat slow type checking if the files are the same
            if (!info[s][0].exists) {
              info[s][plane].id = info[s][0].id;
              info[s][plane].exists = false;
            }

            continue;
          }
          testFile = new RandomAccessInputStream(info[s][plane].id);
          if (!info[s][plane].reader.isThisType(testFile)) {
            LOGGER.warn("{} is not a valid OME-TIFF", info[s][plane].id);
            info[s][plane].id = info[s][0].id;
            info[s][plane].exists = false;
          }
          testFile.close();
        }

        info[s][0].reader.setId(info[s][0].id);
        tileWidth[s] = info[s][0].reader.getOptimalTileWidth();
        tileHeight[s] = info[s][0].reader.getOptimalTileHeight();

        m.sizeX = meta.getPixelsSizeX(i).getValue().intValue();
        int tiffWidth = (int) firstIFD.getImageWidth();
        if (m.sizeX != tiffWidth && s == 0) {
          LOGGER.warn("SizeX mismatch: OME={}, TIFF={}",
            m.sizeX, tiffWidth);
        }
        m.sizeY = meta.getPixelsSizeY(i).getValue().intValue();
        int tiffHeight = (int) firstIFD.getImageLength();
        if (m.sizeY != tiffHeight && s ==  0) {
          LOGGER.warn("SizeY mismatch: OME={}, TIFF={}",
            m.sizeY, tiffHeight);
        }
        m.sizeZ = meta.getPixelsSizeZ(i).getValue().intValue();
        m.sizeC = meta.getPixelsSizeC(i).getValue().intValue();
        m.sizeT = meta.getPixelsSizeT(i).getValue().intValue();
        m.pixelType = FormatTools.pixelTypeFromString(
          meta.getPixelsType(i).toString());
        int tiffPixelType = firstIFD.getPixelType();
        if (m.pixelType != tiffPixelType && (s == 0 || adjustedSamples)) {
          LOGGER.warn("PixelType mismatch: OME={}, TIFF={}",
            m.pixelType, tiffPixelType);
          m.pixelType = tiffPixelType;
        }
        m.imageCount = num;
        m.dimensionOrder = meta.getPixelsDimensionOrder(i).toString();

        // hackish workaround for files exported by OMERO that have an
        // incorrect dimension order
        String uuidFileName = "";
        try {
          if (meta.getTiffDataCount(i) > 0) {
            uuidFileName = meta.getUUIDFileName(i, 0);
          }
        }
        catch (NullPointerException e) { }
        if (meta.getChannelCount(i) > 0 && meta.getChannelName(i, 0) == null &&
          meta.getTiffDataCount(i) > 0 &&
          uuidFileName.indexOf("__omero_export") != -1)
        {
          m.dimensionOrder = "XYZCT";
        }

        m.orderCertain = true;
        PhotoInterp photo = firstIFD.getPhotometricInterpretation();
        m.rgb = samples > 1 || photo == PhotoInterp.RGB;
        if ((samples != m.sizeC && (samples % m.sizeC) != 0 &&
          (m.sizeC % samples) != 0) || m.sizeC == 1 ||
          adjustedSamples)
        {
          m.sizeC *= samples;
        }

        if (m.sizeZ * m.sizeT * m.sizeC >
          m.imageCount && !m.rgb)
        {
          if (m.sizeZ == m.imageCount) {
            m.sizeT = 1;
            m.sizeC = 1;
          }
          else if (m.sizeT == m.imageCount) {
            m.sizeZ = 1;
            m.sizeC = 1;
          }
          else if (m.sizeC == m.imageCount) {
            m.sizeT = 1;
            m.sizeZ = 1;
          }
        }

        if (meta.getPixelsBinDataCount(i) > 1) {
          LOGGER.warn("OME-TIFF Pixels element contains BinData elements! " +
                      "Ignoring.");
        }
        m.littleEndian = firstIFD.isLittleEndian();
        m.interleaved = false;
        m.indexed = photo == PhotoInterp.RGB_PALETTE &&
          firstIFD.getIFDValue(IFD.COLOR_MAP) != null;
        if (m.indexed) {
          m.rgb = false;
        }
        m.falseColor = true;
        m.metadataComplete = true;
        if (meta.getPixelsSignificantBits(i) != null) {
          m.bitsPerPixel = meta.getPixelsSignificantBits(i).getValue();
        }
      }
      catch (NullPointerException exc) {
        throw new FormatException("Incomplete Pixels metadata", exc);
      }
    }

    // remove null CoreMetadata entries

    ArrayList<CoreMetadata> series = new ArrayList<CoreMetadata>();
    Vector<OMETiffPlane[]> planeInfo = new Vector<OMETiffPlane[]>();
    for (int i=0; i<core.size(); i++) {
      if (core.get(i) != null) {
        series.add(core.get(i));
        planeInfo.add(info[i]);
      }
    }
    core = series;
    info = planeInfo.toArray(new OMETiffPlane[0][0]);

    if (getImageCount() == 1) {
      CoreMetadata ms0 = core.get(0);
      ms0.sizeZ = 1;
      if (!ms0.rgb) {
        ms0.sizeC = 1;
      }
      ms0.sizeT = 1;
    }

    for (int i=0; i<core.size(); i++) {
      CoreMetadata m = core.get(i);
      Modulo z = service.getModuloAlongZ(meta, i);
      if (z != null) {
        m.moduloZ = z;
      }
      Modulo c = service.getModuloAlongC(meta, i);
      if (c != null) {
        m.moduloC = c;
      }
      Modulo t = service.getModuloAlongT(meta, i);
      if (t != null) {
        m.moduloT = t;
      }
    }

    MetadataTools.populatePixels(metadataStore, this, true, false);
    for (int i=0; i<acquiredDates.length; i++) {
      if (acquiredDates[i] != null) {
        metadataStore.setImageAcquisitionDate(
            new Timestamp(acquiredDates[i]), i);
      }
    }
    metadataStore = getMetadataStoreForConversion();
  }

  // -- OMETiffReader API methods --

  /**
   * Returns a MetadataStore that is populated in such a way as to
   * produce valid OME-XML.  The returned MetadataStore cannot be used
   * by an IFormatWriter, as it will not contain the required
   * BinData.BigEndian attributes.
   */
  public MetadataStore getMetadataStoreForDisplay() {
    MetadataStore store = getMetadataStore();
    if (service.isOMEXMLMetadata(store)) {
      service.removeBinData((OMEXMLMetadata) store);
      for (int i=0; i<getSeriesCount(); i++) {
        if (((OMEXMLMetadata) store).getTiffDataCount(i) == 0) {
          service.addMetadataOnly((OMEXMLMetadata) store, i);
        }
      }
    }
    return store;
  }

  /**
   * Returns a MetadataStore that is populated in such a way as to be
   * usable by an IFormatWriter.  Any OME-XML generated from this
   * MetadataStore is <em>very unlikely</em> to be valid, as more than
   * likely both BinData and TiffData element will be present.
   */
  public MetadataStore getMetadataStoreForConversion() {
    MetadataStore store = getMetadataStore();
    int realSeries = getSeries();
    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      if (meta.getPixelsBinDataCount(i) > 0) {
        for (int j=0; j<meta.getPixelsBinDataCount(i); j++) {
          store.setPixelsBinDataBigEndian(new Boolean(!isLittleEndian()), i, j);
        }
      }
    }
    setSeries(realSeries);
    return store;
  }

  // -- Helper methods --

  private String normalizeFilename(String dir, String name) {
     File file = new File(dir, name);
     if (file.exists()) return file.getAbsolutePath();
     return name;
  }

  private void setupService() throws FormatException {
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
    }
    catch (DependencyException de) {
      throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
    }
  }

  /** Extracts the OME-XML from the current {@link #metadataFile}. */
  private String readMetadataFile() throws IOException {
    if (checkSuffix(metadataFile, "tif") || checkSuffix(metadataFile, "tiff")) {
      // metadata file is an OME-TIFF file; extract OME-XML comment
      return new TiffParser(metadataFile).getComment();
    }
    // assume metadata file is an XML file
    return DataTools.readFile(metadataFile);
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
    /**
     * Whether or not the file meant to contain this plane exists.
     * The value of 'id' may be changed to allow the tile and image dimensions
     * to be populated; this flag indicates whether the originally recorded file
     * for this plane exists.
     */
    public boolean exists = true;
  }

}
