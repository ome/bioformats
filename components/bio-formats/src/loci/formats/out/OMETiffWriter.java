//
// OMETiffWriter.java
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

package loci.formats.out;

import java.awt.Image;
import java.io.IOException;
import java.util.Vector;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.*;

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
      IMetadata omeMeta = MetadataTools.getOMEMetadata(retrieve);

      // generate UUID and add to OME element
      // Use Java 1.5 method java.util.UUID.randomUUID() via reflection?
      // but need a separate UUID for each file's OME element
      // now all TiffData elements are identical across all files
      // the ONLY change is the UUID of the root OME element

      // main challenge is that programmer must still provide TiffData tag
      // list indicating which IFDs of which files correspond to which planes;
      // TiffData properties are part of MetadataStore now for this purpose
      //
      // if programmer indicates which TiffData blocks map to which files using
      // UUID with FileName, we have all the information we need to write each
      // plane to the correct file... assuming planes are fed to saveImage
      // according to the given dimension order
      //
      // finish thinking this through

      for (int series=0; series<omeMeta.getImageCount(); series++) {
        String dimensionOrder = omeMeta.getPixelsDimensionOrder(series, 0);
        int sizeZ = omeMeta.getPixelsSizeZ(series, 0).intValue();
        int sizeC = omeMeta.getPixelsSizeC(series, 0).intValue();
        int sizeT = omeMeta.getPixelsSizeT(series, 0).intValue();

        int imageCount = 0;
        for (int q=0; q<seriesMap.size(); q++) {
          if ((((Integer) seriesMap.get(q))).intValue() == series) imageCount++;
        }

        // if RGB planes were written, adjust sizeC
        if (imageCount < sizeZ * sizeC * sizeT) {
          sizeC = imageCount / (sizeZ * sizeT);
        }

        int num = 0;
        for (int plane=0; plane<imageCount; plane++) {
          while (((Integer) seriesMap.get(num)).intValue() != series) {
            num++;
          }
          int[] zct = FormatTools.getZCTCoords(dimensionOrder,
            sizeZ, sizeC, sizeT, imageCount, plane);
          omeMeta.setTiffDataFileName(currentId, series, 0, plane);
          omeMeta.setTiffDataFirstZ(new Integer(zct[0]), series, 0, plane);
          omeMeta.setTiffDataFirstC(new Integer(zct[1]), series, 0, plane);
          omeMeta.setTiffDataFirstT(new Integer(zct[2]), series, 0, plane);
          omeMeta.setTiffDataIFD(new Integer(num), series, 0, plane);
          omeMeta.setTiffDataNumPlanes(new Integer(1), series, 0, plane);
          num++;
        }
      }

      String xml = MetadataTools.getOMEXML(omeMeta);

      // insert warning comment
      String prefix = xml.substring(0, xml.indexOf(">") + 1);
      String suffix = xml.substring(xml.indexOf(">") + 1);
      xml = prefix + WARNING_COMMENT + suffix;

      // write OME-XML to the first IFD's comment
      try {
        TiffTools.overwriteComment(currentId, xml);
      }
      catch (FormatException exc) {
        IOException io = new IOException("Unable to append OME-XML comment");
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
