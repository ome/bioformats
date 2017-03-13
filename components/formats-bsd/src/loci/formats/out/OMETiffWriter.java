/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.out;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;

import loci.common.Location;
import loci.common.Constants;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.ome.OMEXMLMetadataImpl;
import loci.formats.services.OMEXMLService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffSaver;
import loci.formats.in.MetadataOptions;
import loci.formats.in.DynamicMetadataOptions;

/**
 * OMETiffWriter is the file format writer for OME-TIFF files.
 */
public class OMETiffWriter extends TiffWriter {

  // -- Constants --

  private static final String WARNING_COMMENT =
    "<!-- Warning: this comment is an OME-XML metadata block, which " +
    "contains crucial dimensional parameters and other important metadata. " +
    "Please edit cautiously (if at all), and back up the original data " +
    "before doing so. For more information, see the OME-TIFF web site: " +
    FormatTools.URL_OME_TIFF + ". -->";

  public static final String COMPANION_KEY = "ometiff.companion";

  // -- Fields --

  private String[][] imageLocations;
  private OMEXMLMetadata omeMeta;
  private OMEXMLService service;
  private Map<String, Integer> ifdCounts = new HashMap<String, Integer>();

  private Map<String, String> uuids = new HashMap<String, String>();

  // -- Constructor --

  public OMETiffWriter() {
    super("OME-TIFF",
      new String[] {"ome.tif", "ome.tiff", "ome.tf2", "ome.tf8", "ome.btf"});
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  @Override
  public void close() throws IOException {
    try {
      if (currentId != null) {
        setupServiceAndMetadata();

        // remove any BinData and old TiffData elements from the OME-XML
        service.removeBinData(omeMeta);
        service.removeTiffData(omeMeta);

        for (int series=0; series<omeMeta.getImageCount(); series++) {
          setSeries(series);
          populateImage(omeMeta, series);
        }

        String companion = getCompanion();
        String companionUUID = null;
        if (null != companion) {
          String companionXML = getOMEXML(companion);
          PrintWriter out = new PrintWriter(companion, Constants.ENCODING);
          out.println(XMLTools.indentXML(companionXML, true));
          out.close();
          companionUUID = "urn:uuid:" + getUUID(
              new Location(companion).getName());
        }

        List<String> files = new ArrayList<String>();
        for (String[] s : imageLocations) {
          for (String f : s) {
            if (!files.contains(f) && f != null) {
              files.add(f);

              String xml = null;
              if (null != companion) {
                xml = getBinaryOnlyOMEXML(f, companion, companionUUID);
              } else {
                xml = getOMEXML(f);
              }
              xml = insertWarningComment(xml);
              if (getMetadataOptions().isValidate()) {
                service.validateOMEXML(xml);
              }

              // write OME-XML to the first IFD's comment
              saveComment(f, xml);
            }
          }
        }
      }
    }
    catch (DependencyException de) {
      throw new RuntimeException(de);
    }
    catch (ServiceException se) {
      throw new RuntimeException(se);
    }
    catch (FormatException fe) {
      throw new RuntimeException(fe);
    }
    catch (IllegalArgumentException iae) {
      throw new RuntimeException(iae);
    }
    finally {
      super.close();

      boolean canReallyClose =
        omeMeta == null || ifdCounts.size() == omeMeta.getImageCount();

      if (omeMeta != null && canReallyClose) {
        int omePlaneCount = 0;
        for (int i=0; i<omeMeta.getImageCount(); i++) {
          int sizeZ = omeMeta.getPixelsSizeZ(i).getValue();
          int sizeC = omeMeta.getPixelsSizeC(i).getValue();
          int sizeT = omeMeta.getPixelsSizeT(i).getValue();

          omePlaneCount += sizeZ * sizeC * sizeT;
        }

        int ifdCount = 0;
        for (String key : ifdCounts.keySet()) {
          ifdCount += ifdCounts.get(key);
        }

        canReallyClose = omePlaneCount == ifdCount;
      }

      if (canReallyClose) {
        imageLocations = null;
        omeMeta = null;
        service = null;
        ifdCounts.clear();
      }
      else {
        for(String k : ifdCounts.keySet())
        ifdCounts.put(k, 0);
      }
    }
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    saveBytes(no, buf, null, x, y, w, h);
  }

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, IFD ifd, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    super.saveBytes(no, buf, ifd, x, y, w, h);

    int index = no;
    while (imageLocations[series][index] != null) {
      if (index < imageLocations[series].length - 1) {
        index++;
      }
      else {
        break;
      }
    }
    imageLocations[series][index] = currentId;
  }

  // -- FormatWriter API methods --

  /* @see FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    if (id.equals(currentId)) return;
    super.setId(id);
    if (imageLocations == null) {
      MetadataRetrieve r = getMetadataRetrieve();
      imageLocations = new String[r.getImageCount()][];
      for (int i=0; i<imageLocations.length; i++) {
        setSeries(i);
        imageLocations[i] = new String[planeCount()];
      }
      setSeries(0);
    }
  }

  // -- OMETiff-specific methods --
  public String getCompanion() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).get(COMPANION_KEY);
    }
    return null;
  }

  // -- Helper methods --

  /** Gets the UUID corresponding to the given filename. */
  private String getUUID(String filename) {
    String uuid = uuids.get(filename);
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
      uuids.put(filename, uuid);
    }
    return uuid;
  }

  private void setupServiceAndMetadata()
    throws DependencyException, ServiceException
  {
    // extract OME-XML string from metadata object
    MetadataRetrieve retrieve = getMetadataRetrieve();

    ServiceFactory factory = new ServiceFactory();
    service = factory.getInstance(OMEXMLService.class);
    OMEXMLMetadata originalOMEMeta = service.getOMEMetadata(retrieve);
    originalOMEMeta.resolveReferences();

    String omexml = service.getOMEXML(originalOMEMeta);
    omeMeta = service.createOMEXMLMetadata(omexml);
  }

  private String insertWarningComment(String xml) {
    String prefix = xml.substring(0, xml.indexOf('>') + 1);
    String suffix = xml.substring(xml.indexOf('>') + 1);
    return prefix + WARNING_COMMENT + suffix;
  }

  private String getOMEXML(String file) throws FormatException, IOException {
    // generate UUID and add to OME element
    String uuid = "urn:uuid:" + getUUID(new Location(file).getName());
    omeMeta.setUUID(uuid);

    OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) omeMeta.getRoot();
    root.setCreator(FormatTools.CREATOR);

    String xml;
    try {
      xml = service.getOMEXML(omeMeta);
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }
    return xml;
  }

  private String getBinaryOnlyOMEXML(
      String file, String companion, String companionUUID) throws
        FormatException, IOException, DependencyException, ServiceException {
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    OMEXMLMetadata meta = service.createOMEXMLMetadata();
    String uuid = "urn:uuid:" + getUUID(new Location(file).getName());
    meta.setUUID(uuid);
    meta.setBinaryOnlyMetadataFile(new Location(companion).getName());
    meta.setBinaryOnlyUUID(companionUUID);
    OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) meta.getRoot();
    root.setCreator(FormatTools.CREATOR);
    return service.getOMEXML(meta);
  }

  private void saveComment(String file, String xml) throws IOException {
    if (out != null) out.close();
    out = new RandomAccessOutputStream(file);
    RandomAccessInputStream in = null;
    try {
      TiffSaver saver = new TiffSaver(out, file);
      saver.setBigTiff(isBigTiff);
      in = new RandomAccessInputStream(file);
      saver.overwriteLastIFDOffset(in);
      saver.overwriteComment(in, xml);
    }
    catch (FormatException exc) {
      IOException io = new IOException("Unable to append OME-XML comment");
      io.initCause(exc);
      throw io;
    }
    finally {
      if (out != null) out.close();
      if (in != null) in.close();
    }
  }

  private void populateTiffData(OMEXMLMetadata omeMeta, int[] zct,
    int ifd, int series, int plane)
  {
    omeMeta.setTiffDataFirstZ(new NonNegativeInteger(zct[0]), series, plane);
    omeMeta.setTiffDataFirstC(new NonNegativeInteger(zct[1]), series, plane);
    omeMeta.setTiffDataFirstT(new NonNegativeInteger(zct[2]), series, plane);
    omeMeta.setTiffDataIFD(new NonNegativeInteger(ifd), series, plane);
    omeMeta.setTiffDataPlaneCount(new NonNegativeInteger(1), series, plane);
  }

  private void populateImage(OMEXMLMetadata omeMeta, int series) {
    String dimensionOrder = omeMeta.getPixelsDimensionOrder(series).toString();
    int sizeZ = omeMeta.getPixelsSizeZ(series).getValue().intValue();
    int sizeC = omeMeta.getPixelsSizeC(series).getValue().intValue();
    int sizeT = omeMeta.getPixelsSizeT(series).getValue().intValue();

    int imageCount = getPlaneCount();

    if (imageCount == 0) {
      omeMeta.setTiffDataPlaneCount(new NonNegativeInteger(0), series, 0);
      return;
    }

    PositiveInteger samplesPerPixel =
      new PositiveInteger((sizeZ * sizeC * sizeT) / imageCount);
    for (int c=0; c<omeMeta.getChannelCount(series); c++) {
      omeMeta.setChannelSamplesPerPixel(samplesPerPixel, series, c);
    }
    sizeC /= samplesPerPixel.getValue();

    int nextPlane = 0;
    for (int plane=0; plane<imageCount; plane++) {
      int[] zct = FormatTools.getZCTCoords(dimensionOrder,
        sizeZ, sizeC, sizeT, imageCount, plane);

      int planeIndex = plane;
      if (imageLocations[series].length < imageCount) {
        planeIndex /= (imageCount / imageLocations[series].length);
      }

      String filename = imageLocations[series][planeIndex];
      if (filename != null) {
        filename = new Location(filename).getName();

        Integer ifdIndex = ifdCounts.get(filename);
        int ifd = ifdIndex == null ? 0 : ifdIndex.intValue();

        omeMeta.setUUIDFileName(filename, series, nextPlane);
        String uuid = "urn:uuid:" + getUUID(filename);
        omeMeta.setUUIDValue(uuid, series, nextPlane);

        // fill in any non-default TiffData attributes
        populateTiffData(omeMeta, zct, ifd, series, nextPlane);
        ifdCounts.put(filename, ifd + 1);
        nextPlane++;
      }
    }
  }

  private int planeCount() {
    MetadataRetrieve r = getMetadataRetrieve();
    int z = r.getPixelsSizeZ(series).getValue().intValue();
    int t = r.getPixelsSizeT(series).getValue().intValue();
    int c = r.getChannelCount(series);
    String pixelType = r.getPixelsType(series).getValue();
    int bytes = FormatTools.getBytesPerPixel(pixelType);

    if (bytes > 1 && c == 1) {
      c = r.getChannelSamplesPerPixel(series, 0).getValue();
    }

    return z * c * t;
  }

}
