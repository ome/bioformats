//
// OMETiffWriter.java
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

package loci.formats.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.ome.OMEXMLMetadataImpl;
import loci.formats.services.OMEXMLService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffSaver;

/**
 * OMETiffWriter is the file format writer for OME-TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/out/OMETiffWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/out/OMETiffWriter.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class OMETiffWriter extends TiffWriter {

  // -- Constants --

  private static final String WARNING_COMMENT =
    "<!-- Warning: this comment is an OME-XML metadata block, which " +
    "contains crucial dimensional parameters and other important metadata. " +
    "Please edit cautiously (if at all), and back up the original data " +
    "before doing so. For more information, see the OME-TIFF web site: " +
    FormatTools.URL_OME_TIFF + ". -->";

  // -- Fields --

  private List<Integer> seriesMap;
  private String[][] imageLocations;
  private OMEXMLMetadata omeMeta;
  private OMEXMLService service;
  private Map<String, Integer> ifdCounts = new HashMap<String, Integer>();

  private Map<String, String> uuids = new HashMap<String, String>();

  // -- Constructor --

  public OMETiffWriter() {
    super("OME-TIFF", new String[] {"ome.tif", "ome.tiff"});
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    try {
      if (currentId != null) {
        setupServiceAndMetadata();

        // remove any BinData elements from the OME-XML
        service.removeBinData(omeMeta);

        for (int series=0; series<omeMeta.getImageCount(); series++) {
          setSeries(series);
          populateImage(omeMeta, series);
        }

        List<String> files = new ArrayList<String>();
        for (String[] s : imageLocations) {
          for (String f : s) {
            if (!files.contains(f) && f != null) {
              files.add(f);

              String xml = getOMEXML(f);

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
        seriesMap = null;
        imageLocations = null;
        omeMeta = null;
        service = null;
        ifdCounts.clear();
      }
    }
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    saveBytes(no, buf, null, x, y, w, h);
  }

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], IFD, int, int, int, int)
   */
  public void saveBytes(int no, byte[] buf, IFD ifd, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (seriesMap == null) seriesMap = new ArrayList<Integer>();
    if (!seriesMap.contains(series)) {
      seriesMap.add(new Integer(series));
    }

    super.saveBytes(no, buf, ifd, x, y, w, h);

    imageLocations[series][no] = currentId;
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String) */
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
    if (originalOMEMeta instanceof OMEXMLMetadataImpl) {
      ((OMEXMLMetadataImpl) originalOMEMeta).resolveReferences();

      String omexml = service.getOMEXML(originalOMEMeta);
      omeMeta = service.createOMEXMLMetadata(omexml);
    }
  }

  private String getOMEXML(String file) throws FormatException, IOException {
    // generate UUID and add to OME element
    String uuid = "urn:uuid:" + getUUID(new Location(file).getName());
    omeMeta.setUUID(uuid);

    String xml;
    try {
      xml = service.getOMEXML(omeMeta);
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }

    // insert warning comment
    String prefix = xml.substring(0, xml.indexOf(">") + 1);
    String suffix = xml.substring(xml.indexOf(">") + 1);
    return prefix + WARNING_COMMENT + suffix;
  }

  private void saveComment(String file, String xml) throws IOException {
    if (out != null) out.close();
    out = new RandomAccessOutputStream(file);
    RandomAccessInputStream in = null;
    try {
      TiffSaver saver = new TiffSaver(out, file);
      in = new RandomAccessInputStream(file);
      saver.overwriteLastIFDOffset(in);
      saver.overwriteComment(in, xml);
      in.close();
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
    int ifdCount = seriesMap.size();

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
        if ((ifd > plane || ifdCounts.size() > 1) && ifd > 0) {
          ifd--;
        }

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
