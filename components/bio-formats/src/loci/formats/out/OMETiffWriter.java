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
import java.util.UUID;

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
import loci.formats.services.OMEXMLService;
import loci.formats.tiff.TiffSaver;

/**
 * OMETiffWriter is the file format writer for OME-TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/OMETiffWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/OMETiffWriter.java">SVN</a></dd></dl>
 */
public class OMETiffWriter extends TiffWriter {

  // -- Constants --

  private static final String WARNING_COMMENT =
    "<!-- Warning: this comment is an OME-XML metadata block, which " +
    "contains crucial dimensional parameters and other important metadata. " +
    "Please edit cautiously (if at all), and back up the original data " +
    "before doing so. For more information, see the OME-TIFF web site: " +
    FormatTools.URL_OME_TIFF + ". -->";

  // -- Static fields --

  private static HashMap<String, String> uuids = new HashMap<String, String>();

  // -- Fields --

  private ArrayList<Integer> seriesMap;
  private boolean wroteLast;
  private String[][] imageLocations;
  private int totalPlanes = 0;

  // -- Constructor --

  public OMETiffWriter() {
    super("OME-TIFF", new String[] {"ome.tif", "ome.tiff"});
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (currentId != null) {
      if (!wroteLast) {
        super.close();
        return;
      }

      // extract OME-XML string from metadata object
      MetadataRetrieve retrieve = getMetadataRetrieve();

      OMEXMLMetadata omeMeta;
      OMEXMLService service;
      try {
        ServiceFactory factory = new ServiceFactory();
        service = factory.getInstance(OMEXMLService.class);
        omeMeta = service.getOMEMetadata(retrieve);
      }
      catch (DependencyException de) {
        // TODO : Modify close() signature to include FormatException?
        // throw new MissingLibraryException(OMETiffReader.NO_OME_XML_JAR, de);
        throw new RuntimeException(de);
      }
      catch (ServiceException se) {
        // TODO : Modify close() signature to include FormatException?
        // throw new FormatException(se);
        throw new RuntimeException(se);
      }

      for (int series=0; series<omeMeta.getImageCount(); series++) {
        String dimensionOrder =
          omeMeta.getPixelsDimensionOrder(series).toString();
        int sizeZ = omeMeta.getPixelsSizeZ(series).getValue().intValue();
        int sizeC = omeMeta.getPixelsSizeC(series).getValue().intValue();
        int sizeT = omeMeta.getPixelsSizeT(series).getValue().intValue();

        int imageCount = getPlaneCount();
        int ifdCount = seriesMap.size();

        if (imageCount == 0) {
          omeMeta.setTiffDataPlaneCount(new Integer(0), series, 0);
          continue;
        }

        Integer samplesPerPixel =
          new Integer((sizeZ * sizeC * sizeT) / imageCount);
        for (int c=0; c<omeMeta.getChannelCount(series); c++) {
          omeMeta.setChannelSamplesPerPixel(samplesPerPixel, series, c);
        }

        HashMap<String, Integer> ifdCounts = new HashMap<String, Integer>();

        for (int plane=0; plane<imageCount; plane++) {
          int[] zct = FormatTools.getZCTCoords(dimensionOrder,
            sizeZ, sizeC, sizeT, imageCount, plane);
          String filename =
            new Location(imageLocations[series][plane]).getName();

          Integer ifdIndex = ifdCounts.get(filename);
          int ifd = ifdIndex == null ? 0 : ifdIndex.intValue();

          omeMeta.setUUIDFileName(filename, series, plane);
          String uuid = "urn:uuid:" + getUUID(filename);
          omeMeta.setUUIDValue(uuid, series, plane);
          // fill in any non-default TiffData attributes
          omeMeta.setTiffDataFirstZ(zct[0], series, plane);
          omeMeta.setTiffDataFirstC(zct[1], series, plane);
          omeMeta.setTiffDataFirstT(new Integer(zct[2]), series, plane);
          omeMeta.setTiffDataIFD(ifd, series, plane);
          omeMeta.setTiffDataPlaneCount(1, series, plane);

          ifdCounts.put(filename, ifd + 1);
        }
      }

      ArrayList<String> files = new ArrayList<String>();
      for (String[] s : imageLocations) {
        for (String f : s) {
          if (!files.contains(f)) files.add(f);
        }
      }

      for (String file : files) {
        // generate UUID and add to OME element
        String uuid = "urn:uuid:" + getUUID(new Location(file).getName());
        omeMeta.setUUID(uuid);

        String xml;
        try {
          xml = service.getOMEXML(omeMeta);
        }
        catch (ServiceException se) {
          // FIXME: Modify close() signature to include FormatException?
          // throw new FormatException(se);
          throw new RuntimeException(se);
        }

        // insert warning comment
        String prefix = xml.substring(0, xml.indexOf(">") + 1);
        String suffix = xml.substring(xml.indexOf(">") + 1);
        xml = prefix + WARNING_COMMENT + suffix;

        if (out != null) out.close();
        out = new RandomAccessOutputStream(file);

        // write OME-XML to the first IFD's comment
        try {
          TiffSaver saver = new TiffSaver(out);
          RandomAccessInputStream in = new RandomAccessInputStream(file);
          saver.overwriteLastIFDOffset(in);
          saver.overwriteComment(in, xml);
          in.close();
        }
        catch (FormatException exc) {
          IOException io = new IOException("Unable to append OME-XML comment");
          io.initCause(exc);
          throw io;
        }
      }
    }
    super.close();
    seriesMap = null;
    imageLocations = null;
    wroteLast = false;
    totalPlanes = 0;
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (seriesMap == null) seriesMap = new ArrayList<Integer>();
    seriesMap.add(new Integer(series));

    MetadataRetrieve r = getMetadataRetrieve();

    super.saveBytes(no, buf, x, y, w, h);

    int index = totalPlanes;
    int currentSeries = series;
    for (int s=0; s<currentSeries; s++) {
      setSeries(s);
      index -= getPlaneCount();
    }
    setSeries(currentSeries);

    imageLocations[series][index] = currentId;
    totalPlanes++;

    wroteLast = series == r.getImageCount() - 1 && index == getPlaneCount() - 1;
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
        imageLocations[i] = new String[getPlaneCount()];
      }
      setSeries(0);
    }
  }

  // -- Helper methods --

  /** Gets the UUID corresponding to the given filename. */
  private static String getUUID(String filename) {
    String uuid;
    synchronized (uuids) {
      uuid = uuids.get(filename);
      if (uuid == null) {
        uuid = UUID.randomUUID().toString();
        uuids.put(filename, uuid);
      }
    }
    return uuid;
  }

}
