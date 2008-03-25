//
// OMETiffWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.out;

import java.awt.Image;
import java.io.IOException;
import java.util.Vector;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;

/**
 * OMETiffWriter is the file format writer for OME-TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/out/OMETiffWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/out/OMETiffWriter.java">SVN</a></dd></dl>
 */
public class OMETiffWriter extends TiffWriter {

  // -- Constants --

  private static final String WARNING_COMMENT =
    "<!-- Warning: this comment is an OME-XML metadata block, which " +
    "contains crucial dimensional parameters and other important metadata. " +
    "Please edit cautiously (if at all), and back up the original data " +
    "before doing so.  For more information, see the OME-TIFF web site: " +
    "http://loci.wisc.edu/ome/ome-tiff.html. -->";

  // -- Fields --

  private Vector seriesMap;

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
      // extract OME-XML string from metadata object
      MetadataRetrieve retrieve = getMetadataRetrieve();
      String xml = MetadataTools.getOMEXML(retrieve);

      // insert warning comment

      String prefix = xml.substring(0, xml.indexOf(">") + 1);
      String suffix = xml.substring(xml.indexOf(">") + 1);
      xml = prefix + WARNING_COMMENT + suffix;

      int previousPixelsIndex = 0;

      for (int series=0; series<retrieve.getImageCount(); series++) {
        String dimensionOrder = retrieve.getPixelsDimensionOrder(series, 0);
        int sizeZ = retrieve.getPixelsSizeZ(series, 0).intValue();
        int sizeC = retrieve.getPixelsSizeC(series, 0).intValue();
        int sizeT = retrieve.getPixelsSizeT(series, 0).intValue();

        int imageCount = 0;
        for (int q=0; q<seriesMap.size(); q++) {
          if ((((Integer) seriesMap.get(q))).intValue() == series) imageCount++;
        }

        StringBuffer tiffData = new StringBuffer();
        tiffData.append(">");
        int num = 0;
        for (int q=0; q<imageCount; q++) {
          while (((Integer) seriesMap.get(num)).intValue() != series) {
            num++;
          }
          int[] coordinates = FormatTools.getZCTCoords(dimensionOrder,
            sizeZ, sizeC, sizeT, imageCount, q);
          tiffData.append("<TiffData IFD=\"");
          tiffData.append(num);
          tiffData.append("\" FirstZ=\"");
          tiffData.append(coordinates[0]);
          tiffData.append("\" FirstC=\"");
          tiffData.append(coordinates[1]);
          tiffData.append("\" FirstT=\"");
          tiffData.append(coordinates[2]);
          tiffData.append("\" />");
          num++;
        }
        tiffData.append("</Pixels>");

        // insert TiffData element
        int pix = xml.indexOf("<Pixels ", previousPixelsIndex);
        int end = xml.indexOf("</Pixels", pix);
        int len = 9;
        if (end == -1) {
          end = xml.indexOf("/>", pix);
          len = 2;
        }

        prefix = xml.substring(0, end);
        suffix = xml.substring(end + len);
        xml = prefix + tiffData.toString() + suffix;
        previousPixelsIndex = pix + 8;
      }

      // write OME-XML to the first IFD's comment
      try {
        TiffTools.overwriteComment(currentId, xml);
      }
      catch (FormatException exc) {
        IOException io =
          new IOException("Unable to append OME-XML comment");
        io.initCause(exc);
        throw io;
      }
    }
    super.close();
    seriesMap = null;
  }

  // -- IFormatWriter API methods --

  public void saveImage(Image image, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    if (seriesMap == null) seriesMap = new Vector();
    seriesMap.add(new Integer(series));
    super.saveImage(image, series, lastInSeries, last);
  }

}
