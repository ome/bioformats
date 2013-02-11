/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
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
public class CoreMetadata implements Cloneable {

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

  public int resolutionCount = 1;

  // -- Constructors --

  public CoreMetadata() {
    seriesMetadata = new Hashtable<String, Object>();
  }

  public CoreMetadata(IFormatReader r, int coreIndex) {
    int currentIndex = r.getCoreIndex();
    r.setCoreIndex(coreIndex);

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
    resolutionCount = r.getResolutionCount();

    r.setCoreIndex(currentIndex);
  }

  public CoreMetadata(CoreMetadata c) {
    sizeX = c.sizeX;
    sizeY = c.sizeY;
    sizeZ = c.sizeZ;
    sizeC = c.sizeC;
    sizeT = c.sizeT;
    thumbSizeX = c.thumbSizeX;
    thumbSizeY = c.thumbSizeY;
    pixelType = c.pixelType;
    bitsPerPixel = c.bitsPerPixel;
    imageCount = c.imageCount;
    cLengths = c.cLengths;
    cTypes = c.cTypes;
    dimensionOrder = c.dimensionOrder;
    orderCertain = c.orderCertain;
    rgb = c.rgb;
    littleEndian = c.littleEndian;
    interleaved = c.interleaved;
    indexed = c.indexed;
    falseColor = c.falseColor;
    metadataComplete = c.metadataComplete;
    seriesMetadata = c.seriesMetadata;
    thumbnail = c.thumbnail;
    resolutionCount = c.resolutionCount;
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

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public CoreMetadata clone(IFormatReader r, int coreIndex) {
      return new CoreMetadata(r, coreIndex);
  }

}
