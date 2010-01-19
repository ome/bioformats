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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import loci.common.Location;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
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
    "http://loci.wisc.edu/ome/ome-tiff.html. -->";

  // -- Static fields --

  private static HashMap<String, String> uuids = new HashMap<String, String>();

  // -- Fields --

  private ArrayList<Integer> seriesMap;
  private boolean wroteLast;

  // -- Constructor --

  public OMETiffWriter() {
    super("OME-TIFF", new String[] {"ome.tif", "ome.tiff"});
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    if (currentId != null) {
      if (!wroteLast) {
        // FIXME
        throw new IOException(
          "Sorry, closing OME-TIFF files early is not yet supported.");
      }

      // extract OME-XML string from metadata object
      MetadataRetrieve retrieve = getMetadataRetrieve();
      IMetadata omeMeta = MetadataTools.getOMEMetadata(retrieve);

      // generate UUID and add to OME element
      String filename = new Location(currentId).getName();
      String uuid = "urn:uuid:" + getUUID(filename);
      omeMeta.setUUID(uuid);

      for (int series=0; series<omeMeta.getImageCount(); series++) {
        String dimensionOrder = omeMeta.getPixelsDimensionOrder(series, 0);
        int sizeZ = omeMeta.getPixelsSizeZ(series, 0).intValue();
        int sizeC = omeMeta.getPixelsSizeC(series, 0).intValue();
        int sizeT = omeMeta.getPixelsSizeT(series, 0).intValue();

        int imageCount = 0;
        int ifdCount = seriesMap.size();
        for (int q=0; q<ifdCount; q++) {
          if (seriesMap.get(q).intValue() == series) imageCount++;
        }

        if (imageCount == 0) {
          omeMeta.setTiffDataNumPlanes(new Integer(0), series, 0, 0);
          continue;
        }

        // if RGB planes were written, adjust sizeC
        if (imageCount < sizeZ * sizeC * sizeT) {
          sizeC = imageCount / (sizeZ * sizeT);
        }

        Integer samplesPerPixel =
          new Integer((sizeZ * sizeC * sizeT) / imageCount);
        for (int c=0; c<omeMeta.getLogicalChannelCount(series); c++) {
          omeMeta.setLogicalChannelSamplesPerPixel(samplesPerPixel, series, c);
        }

        int ifd = 0, plane = 0;
        while (plane < imageCount) {
          // skip past IFDs from other series
          while (seriesMap.get(ifd).intValue() != series) {
            ifd++;
          }
          // determine number of sequential IFDs
          int end = ifd;
          while (end < ifdCount && seriesMap.get(end).intValue() == series) {
            end++;
          }
          int num = end - ifd;

          // fill in filename and UUID values
          int[] zct = FormatTools.getZCTCoords(dimensionOrder,
            sizeZ, sizeC, sizeT, imageCount, plane);
          omeMeta.setTiffDataFileName(filename, series, 0, plane);
          omeMeta.setTiffDataUUID(uuid, series, 0, plane);
          // fill in any non-default TiffData attributes
          if (zct[0] > 0) {
            omeMeta.setTiffDataFirstZ(new Integer(zct[0]), series, 0, plane);
          }
          if (zct[1] > 0) {
            omeMeta.setTiffDataFirstC(new Integer(zct[1]), series, 0, plane);
          }
          if (zct[2] > 0) {
            omeMeta.setTiffDataFirstT(new Integer(zct[2]), series, 0, plane);
          }
          if (ifd > 0) {
            omeMeta.setTiffDataIFD(new Integer(ifd), series, 0, plane);
          }
          if (num != ifdCount) {
            omeMeta.setTiffDataNumPlanes(new Integer(num), series, 0, plane);
          }
          plane += num;
          ifd = end;
        }
      }

      String xml = MetadataTools.getOMEXML(omeMeta);

      // insert warning comment
      String prefix = xml.substring(0, xml.indexOf(">") + 1);
      String suffix = xml.substring(xml.indexOf(">") + 1);
      xml = prefix + WARNING_COMMENT + suffix;

      // write OME-XML to the first IFD's comment
      try {
        TiffSaver.overwriteComment(currentId, xml);
      }
      catch (FormatException exc) {
        IOException io = new IOException("Unable to append OME-XML comment");
        io.initCause(exc);
        throw io;
      }
    }
    super.close();
    seriesMap = null;
    wroteLast = false;
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveBytes(byte[], int, boolean, boolean) */
  public void saveBytes(byte[] buf, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    if (seriesMap == null) seriesMap = new ArrayList<Integer>();
    seriesMap.add(new Integer(series));
    if (last) wroteLast = true;
    super.saveBytes(buf, series, lastInSeries, last);
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    if (id.equals(currentId)) return;
    Location file = new Location(id);
    if (file.exists() && file.length() > 0) {
      // FIXME
      throw new FormatException(
        "Sorry, appending to existing OME-TIFF files is not yet supported.");
    }
    super.setId(id);
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
