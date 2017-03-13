/*
 * #%L
 * Top-level reader and writer APIs
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

package loci.formats;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A utility class for working with metadata objects,
 * including {@link MetadataStore}, {@link MetadataRetrieve},
 * and OME-XML strings.
 * Most of the methods require the optional {@link loci.formats.ome}
 * package, and optional ome-xml.jar library, to be present at runtime.
 */
public final class MetadataTools {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MetadataTools.class);

  // -- Static fields --

  private static boolean defaultDateEnabled = false;

  // -- Constructor --

  private MetadataTools() { }

  // -- Utility methods - OME-XML --

  /**
   * Populates the Pixels element of the given metadata store, using core
   * metadata from the given reader.
   *
   * Delegate to
   * {@link #populatePixels(MetadataStore, IFormatReader, boolean, boolean)}
   * with {@code doPlane} set to {@code false} and {@code doImageName} set to
   * {@code true}.
   *
   * @param store The metadata store whose Pixels should be populated
   * @param r     The format reader whose core metadata should be used
   */
  public static void populatePixels(MetadataStore store, IFormatReader r) {
    populatePixels(store, r, false, true);
  }

  /**
   * Populates the Pixels element of the given metadata store, using core
   * metadata from the given reader.  If the {@code doPlane} flag is set,
   * then the Plane elements will be populated as well.
   *
   * Delegates to
   * {@link #populatePixels(MetadataStore, IFormatReader, boolean, boolean)}
   * with {@code doImageName} set to {@code true}.
   *
   * @param store   The metadata store whose Pixels should be populated
   * @param r       The format reader whose core metadata should be used
   * @param doPlane Specifies whether Plane elements should be populated
   */
  public static void populatePixels(MetadataStore store, IFormatReader r,
    boolean doPlane)
  {
    populatePixels(store, r, doPlane, true);
  }

  /**
   * Populates the Pixels element of the given metadata store, using core
   * metadata from the given reader.  If the {@code doPlane} flag is set,
   * then the Plane elements will be populated as well. If the
   * {@code doImageName} flag is set, then the image name will be populated as
   * well.
   *
   * @param store       The metadata store whose Pixels should be populated
   * @param r           The format reader whose core metadata should be used
   * @param doPlane     Specifies whether Plane elements should be populated
   * @param doImageName Specifies whether the Image name should be populated
   */
  public static void populatePixels(MetadataStore store, IFormatReader r,
    boolean doPlane, boolean doImageName)
  {
    if (store == null || r == null) return;
    int oldSeries = r.getSeries();

    for (int i=0; i<r.getSeriesCount(); i++) {
      r.setSeries(i);

      String imageName = null;
      if (doImageName) {
        Location f = new Location(r.getCurrentFile());
        imageName = f.getName();

        if (r.getSeriesCount() > 1) {
          imageName += " #" + (i + 1);
        }
      }
      String pixelType = FormatTools.getPixelTypeString(r.getPixelType());

      populateMetadata(store, r.getCurrentFile(), i, imageName,
        r.isLittleEndian(), r.getDimensionOrder(), pixelType, r.getSizeX(),
        r.getSizeY(), r.getSizeZ(), r.getSizeC(), r.getSizeT(),
        r.getRGBChannelCount());

      store.setPixelsInterleaved(r.isInterleaved(), i);
      store.setPixelsSignificantBits(
        new PositiveInteger(r.getBitsPerPixel()), i);

      try {
        OMEXMLService service =
          new ServiceFactory().getInstance(OMEXMLService.class);
        if (service.isOMEXMLRoot(store.getRoot())) {
          MetadataStore baseStore = r.getMetadataStore();
          if (service.isOMEXMLMetadata(baseStore)) {
            OMEXMLMetadata omeMeta;
            try {
              omeMeta = service.getOMEMetadata(service.asRetrieve(baseStore));
              if (omeMeta.getTiffDataCount(i) == 0 && omeMeta.getPixelsBinDataCount(i) == 0) {
                service.addMetadataOnly(omeMeta, i, i == 0);
              }
            }
            catch (ServiceException e) {
              LOGGER.warn("Failed to add MetadataOnly", e);
            }
          }
        }
      }
      catch (DependencyException exc) {
        LOGGER.warn("Failed to add MetadataOnly", exc);
      }

      if (doPlane) {
        for (int q=0; q<r.getImageCount(); q++) {
          int[] coords = r.getZCTCoords(q);
          store.setPlaneTheZ(new NonNegativeInteger(coords[0]), i, q);
          store.setPlaneTheC(new NonNegativeInteger(coords[1]), i, q);
          store.setPlaneTheT(new NonNegativeInteger(coords[2]), i, q);
        }
      }
    }
    r.setSeries(oldSeries);
  }

  /**
   * Populates the given {@link MetadataStore}, for the specified series, using
   * the values from the provided {@link CoreMetadata}.
   * <p>
   * After calling this method, the metadata store will be sufficiently
   * populated for use with an {@link IFormatWriter} (assuming it is also a
   * {@link MetadataRetrieve}).
   * </p>
   */
  public static void populateMetadata(MetadataStore store, int series,
    String imageName, CoreMetadata coreMeta)
  {
    final String pixelType = FormatTools.getPixelTypeString(coreMeta.pixelType);
    final int effSizeC = coreMeta.imageCount / coreMeta.sizeZ / coreMeta.sizeT;
    final int samplesPerPixel = coreMeta.sizeC / effSizeC;
    populateMetadata(store, null, series, imageName, coreMeta.littleEndian,
      coreMeta.dimensionOrder, pixelType, coreMeta.sizeX, coreMeta.sizeY,
      coreMeta.sizeZ, coreMeta.sizeC, coreMeta.sizeT, samplesPerPixel);
  }

  /**
   * Populates the given {@link MetadataStore}, for the specified series, using
   * the provided values.
   * <p>
   * After calling this method, the metadata store will be sufficiently
   * populated for use with an {@link IFormatWriter} (assuming it is also a
   * {@link MetadataRetrieve}).
   * </p>
   */
  public static void populateMetadata(MetadataStore store, int series,
    String imageName, boolean littleEndian, String dimensionOrder,
    String pixelType, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT,
    int samplesPerPixel)
  {
    populateMetadata(store, null, series, imageName, littleEndian,
      dimensionOrder, pixelType, sizeX, sizeY, sizeZ, sizeC, sizeT,
      samplesPerPixel);
  }

  /**
   * Populates the given {@link MetadataStore}, for the specified series, using
   * the provided values.
   * <p>
   * After calling this method, the metadata store will be sufficiently
   * populated for use with an {@link IFormatWriter} (assuming it is also a
   * {@link MetadataRetrieve}).
   * </p>
   */
  public static void populateMetadata(MetadataStore store, String file,
    int series, String imageName, boolean littleEndian, String dimensionOrder,
    String pixelType, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT,
    int samplesPerPixel)
  {
    store.setImageID(createLSID("Image", series), series);
    setDefaultCreationDate(store, file, series);
    if (imageName != null) store.setImageName(imageName, series);
    populatePixelsOnly(store, series, littleEndian, dimensionOrder, pixelType,
      sizeX, sizeY, sizeZ, sizeC, sizeT, samplesPerPixel);
  }

  public static void populatePixelsOnly(MetadataStore store, IFormatReader r) {
    int oldSeries = r.getSeries();
    for (int i=0; i<r.getSeriesCount(); i++) {
      r.setSeries(i);

      String pixelType = FormatTools.getPixelTypeString(r.getPixelType());

      populatePixelsOnly(store, i, r.isLittleEndian(), r.getDimensionOrder(),
        pixelType, r.getSizeX(), r.getSizeY(), r.getSizeZ(), r.getSizeC(),
        r.getSizeT(), r.getRGBChannelCount());
    }
    r.setSeries(oldSeries);
  }

  public static void populatePixelsOnly(MetadataStore store, int series,
    boolean littleEndian, String dimensionOrder, String pixelType, int sizeX,
    int sizeY, int sizeZ, int sizeC, int sizeT, int samplesPerPixel)
  {
    store.setPixelsID(createLSID("Pixels", series), series);
    store.setPixelsBigEndian(!littleEndian, series);
    try {
      store.setPixelsDimensionOrder(
        DimensionOrder.fromString(dimensionOrder), series);
    }
    catch (EnumerationException e) {
      LOGGER.warn("Invalid dimension order: " + dimensionOrder, e);
    }
    try {
      store.setPixelsType(PixelType.fromString(pixelType), series);
    }
    catch (EnumerationException e) {
      LOGGER.warn("Invalid pixel type: " + pixelType, e);
    }
    store.setPixelsSizeX(new PositiveInteger(sizeX), series);
    store.setPixelsSizeY(new PositiveInteger(sizeY), series);
    store.setPixelsSizeZ(new PositiveInteger(sizeZ), series);
    store.setPixelsSizeC(new PositiveInteger(sizeC), series);
    store.setPixelsSizeT(new PositiveInteger(sizeT), series);
    int effSizeC = sizeC / samplesPerPixel;
    for (int i=0; i<effSizeC; i++) {
      store.setChannelID(createLSID("Channel", series, i),
        series, i);
      store.setChannelSamplesPerPixel(new PositiveInteger(samplesPerPixel),
        series, i);
    }
  }

  /**
   * Constructs an LSID, given the object type and indices.
   * For example, if the arguments are "Detector", 1, and 0, the LSID will
   * be "Detector:1:0".
   */
  public static String createLSID(String type, int... indices) {
    StringBuffer lsid = new StringBuffer(type);
    for (int index : indices) {
      lsid.append(":");
      lsid.append(index);
    }
    return lsid.toString();
  }

  /**
   * Checks whether the given metadata object has the minimum metadata
   * populated to successfully describe an Image.
   *
   * @throws FormatException if there is a missing metadata field,
   *   or the metadata object is uninitialized
   */
  public static void verifyMinimumPopulated(MetadataRetrieve src)
    throws FormatException
  {
    verifyMinimumPopulated(src, 0);
  }

  /**
   * Checks whether the given metadata object has the minimum metadata
   * populated to successfully describe the nth Image.
   *
   * @throws FormatException if there is a missing metadata field,
   *   or the metadata object is uninitialized
   */
  public static void verifyMinimumPopulated(MetadataRetrieve src, int n)
    throws FormatException
  {
    if (src == null) {
      throw new FormatException("Metadata object is null; " +
          "call IFormatWriter.setMetadataRetrieve() first");
    }
    if (src instanceof MetadataStore
        && ((MetadataStore) src).getRoot() == null) {
      throw new FormatException("Metadata object has null root; " +
        "call IMetadata.createRoot() first");
    }
    if (src.getImageID(n) == null) {
      throw new FormatException("Image ID #" + n + " is null");
    }
    if (src.getPixelsID(n) == null) {
      throw new FormatException("Pixels ID #" + n + " is null");
    }
    for (int i=0; i<src.getChannelCount(n); i++) {
      if (src.getChannelID(n, i) == null) {
        throw new FormatException("Channel ID #" + i + " in Image #" + n +
          " is null");
      }
    }
    if (src.getPixelsBigEndian(n) == null)
    {
      if (src.getPixelsBinDataCount(n) == 0 || src.getPixelsBinDataBigEndian(n, 0) == null) {
        throw new FormatException("BigEndian #" + n + " is null");
      }
    }
    if (src.getPixelsDimensionOrder(n) == null) {
      throw new FormatException("DimensionOrder #" + n + " is null");
    }
    if (src.getPixelsType(n) == null) {
      throw new FormatException("PixelType #" + n + " is null");
    }
    if (src.getPixelsSizeC(n) == null) {
      throw new FormatException("SizeC #" + n + " is null");
    }
    if (src.getPixelsSizeT(n) == null) {
      throw new FormatException("SizeT #" + n + " is null");
    }
    if (src.getPixelsSizeX(n) == null) {
      throw new FormatException("SizeX #" + n + " is null");
    }
    if (src.getPixelsSizeY(n) == null) {
      throw new FormatException("SizeY #" + n + " is null");
    }
    if (src.getPixelsSizeZ(n) == null) {
      throw new FormatException("SizeZ #" + n + " is null");
    }
  }

  /**
   * Disables the setting of a default creation date.
   *
   * By default, missing creation dates will be replaced with the corresponding
   * file's last modification date, or the current time if the modification
   * date is not available.
   *
   * Calling this method with the 'enabled' parameter set to 'false' causes
   * missing creation dates to be left as null.
   *
   * @param enabled See above.
   * @see #setDefaultCreationDate(MetadataStore, String, int)
   */
  public static void setDefaultDateEnabled(boolean enabled) {
    defaultDateEnabled = enabled;
  }

  /**
   * Sets a default creation date.  If the named file exists, then the creation
   * date is set to the file's last modification date.  Otherwise, it is set
   * to the current date.
   *
   * @see #setDefaultDateEnabled(boolean)
   */
  public static void setDefaultCreationDate(MetadataStore store, String id,
    int series)
  {
    if (!defaultDateEnabled) {
      return;
    }
    Location file = id == null ? null : new Location(id).getAbsoluteFile();
    long time = System.currentTimeMillis();
    if (file != null && file.exists()) time = file.lastModified();
    store.setImageAcquisitionDate(new Timestamp(DateTools.convertDate(
        time, DateTools.UNIX)), series);
  }

  /**
   * Adjusts the given dimension order as needed so that it contains exactly
   * one of each of the following characters: 'X', 'Y', 'Z', 'C', 'T'.
   */
  public static String makeSaneDimensionOrder(String dimensionOrder) {
    String order = dimensionOrder.toUpperCase();
    order = order.replaceAll("[^XYZCT]", "");
    String[] axes = new String[] {"X", "Y", "C", "Z", "T"};
    for (String axis : axes) {
      if (order.indexOf(axis) == -1) order += axis;
      while (order.indexOf(axis) != order.lastIndexOf(axis)) {
        order = order.replaceFirst(axis, "");
      }
    }
    return order;
  }

  // -- Utility methods - original metadata --

  /** Gets a sorted list of keys from the given hashtable. */
  public static String[] keys(Hashtable<String, Object> meta) {
    String[] keys = new String[meta.size()];
    meta.keySet().toArray(keys);
    Arrays.sort(keys);
    return keys;
  }

  /**
   * Merges the given lists of metadata, prepending the
   * specified prefix for the destination keys.
   */
  public static void merge(Map<String, Object> src, Map<String, Object> dest,
    String prefix)
  {
    for (String key : src.keySet()) {
      dest.put(prefix + key, src.get(key));
    }
  }

  /**
   * Creates an OME-XML metadata object using reflection, to avoid
   * direct dependencies on the optional {@link loci.formats.ome} package.
   * @return A new instance of {@link loci.formats.ome.AbstractOMEXMLMetadata},
   *   or null if one cannot be created.
   */
  public static IMetadata createOMEXMLMetadata() {
    try {
      final OMEXMLService service =
        new ServiceFactory().getInstance(OMEXMLService.class);
      if (service == null) return null;
      return service.createOMEXMLMetadata();
    }
    catch (DependencyException exc) {
      return null;
    }
    catch (ServiceException exc) {
      return null;
    }
  }

}
