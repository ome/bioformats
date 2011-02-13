//
// CoreMetadata.java
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

package loci.formats;

import java.util.Hashtable;

/**
 * Encompasses core metadata values.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/CoreMetadata.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/CoreMetadata.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CoreMetadata {

  // -- Fields --

  // TODO: We may want to consider refactoring the FormatReader getter methods
  // that populate missing CoreMetadata fields on the fly
  // (getChannelDimLengths, getChannelDimTypes, getThumbSizeX, getThumbSizeY)
  // to avoid doing so -- one alternate approach would be to have this class
  // use getter methods instead of public fields.

  /** Width (in pixels) of images in this series. */
  public int sizeX;

  /** Height (in pixels) of images in this series. */
  public int sizeY;

  /** Number of Z sections. */
  public int sizeZ;

  /** Number of channels. */
  public int sizeC;

  /** Number of timepoints. */
  public int sizeT;

  /** Width (in pixels) of thumbnail images in this series. */
  public int thumbSizeX;

  /** Height (in pixels) of thumbnail images in this series. */
  public int thumbSizeY;

  /**
   * Describes the number of bytes per pixel.  Must be one of the <i>static</i>
   * pixel types (e.g. <code>INT8</code>) in {@link loci.formats.FormatTools}.
   */
  public int pixelType;

  /** Number of valid bits per pixel. */
  public int bitsPerPixel;

  /** Total number of images. */
  public int imageCount;

  /** Length of each subdimension of C. */
  public int[] cLengths;

  /** Name of each subdimension of C. */
  public String[] cTypes;

  /**
   * Order in which dimensions are stored.  Must be one of the following:<ul>
   *  <li>XYCZT</li>
   *  <li>XYCTZ</li>
   *  <li>XYZCT</li>
   *  <li>XYZTC</li>
   *  <li>XYTCZ</li>
   *  <li>XYTZC</li>
   * </ul>
   */
  public String dimensionOrder;

  /**
   * Indicates whether or not we are confident that the
   * dimension order is correct.
   */
  public boolean orderCertain;

  /**
   * Indicates whether or not the images are stored as RGB
   * (multiple channels per plane).
   */
  public boolean rgb;

  /** Indicates whether or not each pixel's bytes are in little endian order. */
  public boolean littleEndian;

  /**
   * True if channels are stored RGBRGBRGB...; false if channels are stored
   * RRR...GGG...BBB...
   */
  public boolean interleaved;

  /** Indicates whether or not the images are stored as indexed color. */
  public boolean indexed;

  /** Indicates whether or not we can ignore the color map (if present). */
  public boolean falseColor = true;

  /**
   * Indicates whether or not we are confident that all of the metadata stored
   * within the file has been parsed.
   */
  public boolean metadataComplete;

  /** Non-core metadata associated with this series. */
  public Hashtable<String, Object> seriesMetadata;

  /**
   * Indicates whether or not this series is a lower-resolution copy of
   * another series.
   */
  public boolean thumbnail;

  // -- Constructors --

  public CoreMetadata() {
    seriesMetadata = new Hashtable<String, Object>();
  }

  public CoreMetadata(IFormatReader r, int seriesNo) {
    int series = r.getSeries();
    r.setSeries(seriesNo);
    sizeX = r.getSizeX();
    sizeY = r.getSizeY();
    sizeZ = r.getSizeZ();
    sizeC = r.getSizeC();
    sizeT = r.getSizeT();
    thumbSizeX = r.getThumbSizeX();
    thumbSizeY = r.getThumbSizeY();
    pixelType = r.getPixelType();
    bitsPerPixel = r.getBitsPerPixel();
    imageCount = r.getImageCount();
    cLengths = r.getChannelDimLengths();
    cTypes = r.getChannelDimTypes();
    dimensionOrder = r.getDimensionOrder();
    orderCertain = r.isOrderCertain();
    rgb = r.isRGB();
    littleEndian = r.isLittleEndian();
    interleaved = r.isInterleaved();
    indexed = r.isIndexed();
    falseColor = r.isFalseColor();
    metadataComplete = r.isMetadataComplete();
    seriesMetadata = r.getSeriesMetadata();
    thumbnail = r.isThumbnailSeries();
    r.setSeries(series);
  }

  // -- Object methods --

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(super.toString() + ":");
    sb.append("\n\tsizeX = " + sizeX);
    sb.append("\n\tsizeY = " + sizeY);
    sb.append("\n\tsizeZ = " + sizeZ);
    sb.append("\n\tsizeC = " + sizeC);
    sb.append("\n\tsizeT = " + sizeT);
    sb.append("\n\tthumbSizeX = " + thumbSizeX);
    sb.append("\n\tthumbSizeY = " + thumbSizeY);
    sb.append("\n\tpixelType = " + FormatTools.getPixelTypeString(pixelType));
    sb.append("\n\tbitsPerPixel = " + bitsPerPixel);
    sb.append("\n\timageCount = " + imageCount);
    sb.append("\n\tcLengths =");
    if (cLengths == null) sb.append(" null");
    else for (int i=0; i<cLengths.length; i++) sb.append(" " + cLengths[i]);
    sb.append("\n\tcTypes =");
    if (cTypes == null) sb.append(" null");
    else for (int i=0; i<cTypes.length; i++) sb.append(" " + cTypes[i]);
    sb.append("\n\tdimensionOrder = " + dimensionOrder);
    sb.append("\n\torderCertain = " + orderCertain);
    sb.append("\n\trgb = " + rgb);
    sb.append("\n\tlittleEndian = " + littleEndian);
    sb.append("\n\tinterleaved = " + interleaved);
    sb.append("\n\tindexed = " + indexed);
    sb.append("\n\tfalseColor = " + falseColor);
    sb.append("\n\tmetadataComplete = " + metadataComplete);
    sb.append("\n\tseriesMetadata = " + seriesMetadata.size() + " keys");
    sb.append("\n\tthumbnail = " + thumbnail);
    return sb.toString();
  }

}
