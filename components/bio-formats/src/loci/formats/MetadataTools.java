//
// MetadataTools.java
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

import loci.common.*;
import loci.formats.meta.*;

/**
 * A utility class for working with metadata objects,
 * including {@link MetadataStore}, {@link MetadataRetrieve},
 * and OME-XML strings.
 * Most of the methods require the optional {@link loci.formats.ome}
 * package, and optional ome-xml.jar library, to be present at runtime.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/MetadataTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/MetadataTools.java">SVN</a></dd></dl>
 */
public final class MetadataTools {

  // -- Constructor --

  private MetadataTools() { }

  // -- Utility methods - OME-XML --

  /**
   * Creates an OME-XML metadata object using reflection, to avoid
   * direct dependencies on the optional {@link loci.formats.ome} package.
   * @return A new instance of {@link loci.formats.ome.OMEXMLMetadata},
   *   or null if the class is not available.
   */
  public static IMetadata createOMEXMLMetadata() {
    return createOMEXMLMetadata(null);
  }

  /**
   * Creates an OME-XML metadata object using reflection, to avoid
   * direct dependencies on the optional {@link loci.formats.ome} package,
   * wrapping a DOM representation of the given OME-XML string.
   * @return A new instance of {@link loci.formats.ome.OMEXMLMetadata},
   *   or null if the class is not available.
   */
  public static IMetadata createOMEXMLMetadata(String xml) {
    return createOMEXMLMetadata(xml, null);
  }

  /**
   * Creates an OME-XML metadata object using reflection, to avoid
   * direct dependencies on the optional {@link loci.formats.ome} package,
   * wrapping a DOM representation of the given OME-XML string.
   *
   * @param xml The OME-XML string to use for initial population of the
   *   metadata object.
   * @param version The OME-XML version to use (e.g., "2003-FC" or "2007-06").
   *   If the xml and version parameters are both null, the newest version is
   *   used.
   * @return A new instance of {@link loci.formats.ome.OMEXMLMetadata},
   *   or null if the class is not available.
   */
  public static IMetadata createOMEXMLMetadata(String xml, String version) {
    Object ome = null;
    IMetadata meta = null;
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      ome = createOMEXMLRoot(xml);
      if (version == null) {
        if (ome == null) {
          // default to newest schema version
          r.exec("import ome.xml.OMEXMLFactory");
          version = (String) r.exec("OMEXMLFactory.LATEST_VERSION");
        }
        else {
          // extract schema version from OME root node
          r.setVar("ome", ome);
          version = getOMEXMLVersion(ome);
        }
      }

      // create metadata object of the appropriate schema version
      String metaClass = "OMEXML" +
        version.replaceAll("[^\\w]", "") + "Metadata";
      r.exec("import loci.formats.ome." + metaClass);
      meta = (IMetadata) r.exec("new " + metaClass + "()");

      // attach OME root node to metadata object
      if (ome != null) meta.setRoot(ome);
    }
    catch (ReflectException exc) {
      if (FormatHandler.debug) LogTools.trace(exc);
    }
    return meta;
  }

  /**
   * Constructs an OME root node (ome.xml.OMEXMLNode subclass)
   * of the appropriate schema version from the given XML string.
   */
  public static Object createOMEXMLRoot(String xml) {
    if (xml == null) return null;
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import ome.xml.OMEXMLFactory");
      r.setVar("xml", xml);
      return r.exec("OMEXMLFactory.newOMENodeFromSource(xml)");
    }
    catch (ReflectException exc) {
      if (FormatHandler.debug) LogTools.trace(exc);
    }
    return null;
  }

  /**
   * Checks whether the given object is an OME-XML metadata object.
   * @return True iff the object is an instance of
   *   {@link loci.formats.ome.OMEXMLMetadata}.
   */
  public static boolean isOMEXMLMetadata(Object o) {
    return isInstance(o, "loci.formats.ome", "OMEXMLMetadata");
  }

  /**
   * Checks whether the given object is an OME-XML root object.
   * @return True iff the object is an instance of ome.xml.OMEXMLNode.
   */
  public static boolean isOMEXMLRoot(Object o) {
    return isInstance(o, "ome.xml", "OMEXMLNode");
  }

  /**
   * Gets the schema version for the given OME-XML metadata or root object
   * (e.g., "2007-06" or "2003-FC").
   * @return OME-XML schema version, or null if the object is not an instance
   *   of {@link loci.formats.ome.OMEXMLMetadata} or ome.xml.OMEXMLNode.
   */
  public static String getOMEXMLVersion(Object o) {
    if (o == null) return null;
    String name = o.getClass().getName();
    if (isOMEXMLMetadata(o)) {
      final String prefix = "loci.formats.ome.OMEXML";
      final String suffix = "Metadata";
      if (name.startsWith(prefix) && name.endsWith(suffix)) {
        String numbers =
          name.substring(prefix.length(), name.length() - suffix.length());
        if (numbers.length() == 6) {
          return numbers.substring(0, 4) + "-" +
            numbers.substring(4, 6).toUpperCase();
        }
      }
    }
    else if (isOMEXMLRoot(o)) {
      ReflectedUniverse r = new ReflectedUniverse();
      r.setVar("ome", o);
      try {
        return (String) r.exec("ome.getVersion()");
      }
      catch (ReflectException exc) {
        if (FormatHandler.debug) LogTools.trace(exc);
      }
    }
    return null;
  }

  public static IMetadata getOMEMetadata(MetadataRetrieve src) {
    // check if the metadata is already an OME-XML metadata object
    if (isOMEXMLMetadata(src)) return (IMetadata) src;

    // populate a new OME-XML metadata object with metadata
    // converted from the non-OME-XML metadata object
    IMetadata omexmlMeta = createOMEXMLMetadata();
    convertMetadata(src, omexmlMeta);
    return omexmlMeta;
  }

  /**
   * Extracts an OME-XML metadata string from the given metadata object,
   * by converting to an OME-XML metadata object if necessary.
   */
  public static String getOMEXML(MetadataRetrieve src) {
    IMetadata omexmlMeta = getOMEMetadata(src);
    ReflectedUniverse r = new ReflectedUniverse();
    r.setVar("omexmlMeta", omexmlMeta);
    try {
      return (String) r.exec("omexmlMeta.dumpXML()");
    }
    catch (ReflectException exc) {
      if (FormatHandler.debug) LogTools.trace(exc);
    }
    return null;
  }

  /**
   * Attempts to validate the given OME-XML string using
   * Java's XML validation facility. Requires Java 1.5+.
   */
  public static void validateOMEXML(String xml) {
    XMLTools.validateXML(xml, "OME-XML");
  }

  /**
   * Populates the 'pixels' element of the given metadata store, using core
   * metadata from the given reader.
   */
  public static void populatePixels(MetadataStore store, IFormatReader r) {
    populatePixels(store, r, false);
  }

  /**
   * Populates the 'pixels' element of the given metadata store, using core
   * metadata from the given reader.  If the 'doPlane' flag is set,
   * then the 'plane' elements will be populated as well.
   */
  public static void populatePixels(MetadataStore store, IFormatReader r,
    boolean doPlane)
  {
    if (store == null || r == null) return;
    int oldSeries = r.getSeries();
    for (int i=0; i<r.getSeriesCount(); i++) {
      r.setSeries(i);
      store.setPixelsID("Pixels:" + i, i, 0);
      store.setImageDefaultPixels("Pixels:" + i, i);
      store.setPixelsSizeX(new Integer(r.getSizeX()), i, 0);
      store.setPixelsSizeY(new Integer(r.getSizeY()), i, 0);
      store.setPixelsSizeZ(new Integer(r.getSizeZ()), i, 0);
      store.setPixelsSizeC(new Integer(r.getSizeC()), i, 0);
      store.setPixelsSizeT(new Integer(r.getSizeT()), i, 0);
      store.setPixelsPixelType(
        FormatTools.getPixelTypeString(r.getPixelType()), i, 0);
      store.setPixelsBigEndian(new Boolean(!r.isLittleEndian()), i, 0);
      store.setPixelsDimensionOrder(r.getDimensionOrder(), i, 0);
      store.setDisplayOptionsDisplay(r.isRGB() ? "RGB" : "Grey", i);
      if (r.getSizeC() > 0) {
        Integer sampleCount = new Integer(r.getRGBChannelCount());
        for (int c=0; c<r.getEffectiveSizeC(); c++) {
          store.setLogicalChannelSamplesPerPixel(sampleCount, i, c);
          for (int rgb=0; rgb<r.getRGBChannelCount(); rgb++) {
            store.setChannelComponentIndex(
              new Integer(c * r.getRGBChannelCount() + rgb), i, c, rgb);
            store.setChannelComponentPixels("Pixels:" + i, i, c, rgb);
          }
        }
      }
      if (doPlane) {
        for (int q=0; q<r.getImageCount(); q++) {
          int[] coords = r.getZCTCoords(q);
          store.setPlaneTheZ(new Integer(coords[0]), i, 0, q);
          store.setPlaneTheC(new Integer(coords[1]), i, 0, q);
          store.setPlaneTheT(new Integer(coords[2]), i, 0, q);
        }
      }
    }
    r.setSeries(oldSeries);
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
    if (src instanceof MetadataStore &&
      ((MetadataStore) src).getRoot() == null)
    {
      throw new FormatException("Metadata object has null root; " +
        "call IMetadata.createRoot() first");
    }
    if (src.getPixelsBigEndian(n, 0) == null) {
      throw new FormatException("BigEndian #" + n + " is null");
    }
    if (src.getPixelsDimensionOrder(n, 0) == null) {
      throw new FormatException("DimensionOrder #" + n + " is null");
    }
    if (src.getPixelsPixelType(n, 0) == null) {
      throw new FormatException("PixelType #" + n + " is null");
    }
    if (src.getPixelsSizeC(n, 0) == null) {
      throw new FormatException("SizeC #" + n + " is null");
    }
    if (src.getPixelsSizeT(n, 0) == null) {
      throw new FormatException("SizeT #" + n + " is null");
    }
    if (src.getPixelsSizeX(n, 0) == null) {
      throw new FormatException("SizeX #" + n + " is null");
    }
    if (src.getPixelsSizeY(n, 0) == null) {
      throw new FormatException("SizeY #" + n + " is null");
    }
    if (src.getPixelsSizeZ(n, 0) == null) {
      throw new FormatException("SizeZ #" + n + " is null");
    }
  }

  /**
   * Sets a default creation date.  If the named file exists, then the creation
   * date is set to the file's last modification date.  Otherwise, it is set
   * to the current date.
   */
  public static void setDefaultCreationDate(MetadataStore store, String id,
    int series)
  {
    Location file = new Location(id).getAbsoluteFile();
    long time = System.currentTimeMillis();
    if (file.exists()) time = file.lastModified();
    store.setImageCreationDate(DataTools.convertDate(time, DataTools.UNIX),
      series);
  }

  /**
   * Adds the specified key/value pair as a new OriginalMetadata node
   * to the given OME-XML metadata object.
   * Does nothing unless the given object is an OME-XML metadata object.
   * @param omexmlMeta An object of type
   *   {@link loci.formats.ome.OMEXMLMetadata}.
   * @param key Metadata key to populate.
   * @param value Metadata value corresponding to the specified key.
   */
  public static void populateOriginalMetadata(Object omexmlMeta,
    String key, String value)
  {
    ReflectedUniverse r = new ReflectedUniverse();
    r.setVar("omexmlMeta", omexmlMeta);
    r.setVar("key", key);
    r.setVar("value", value);
    try {
      r.exec("omexmlMeta.setOriginalMetadata(key, value)");
    }
    catch (ReflectException exc) {
      if (FormatHandler.debug) LogTools.trace(exc);
    }
  }

  /**
   * Gets the value for the specified OriginalMetadata key
   * from the given OME-XML metadata object.
   * Does nothing unless the given object is an OME-XML metadata object.
   * @param omexmlMeta An object of type
   *   {@link loci.formats.ome.OMEXMLMetadata}.
   * @param key Metadata key whose value is needed.
   * @return Metadata value corresponding to the specified key.
   */
  public static String getOriginalMetadata(Object omexmlMeta, String key) {
    ReflectedUniverse r = new ReflectedUniverse();
    r.setVar("omexmlMeta", omexmlMeta);
    r.setVar("key", key);
    try {
      return (String) r.exec("omexmlMeta.getOriginalMetadata(key)");
    }
    catch (ReflectException exc) {
      if (FormatHandler.debug) LogTools.trace(exc);
    }
    return null;
  }

  // -- Utility methods -- metadata conversion --

  /**
   * Converts information from an OME-XML string (source)
   * into a metadata store (destination).
   */
  public static void convertMetadata(String xml, MetadataStore dest) {
    Object ome = createOMEXMLRoot(xml);
    String rootVersion = getOMEXMLVersion(ome);
    String storeVersion = getOMEXMLVersion(dest);
    if (rootVersion.equals(storeVersion)) {
      // metadata store is already an OME-XML metadata object of the
      // correct schema version; populate OME-XML string directly
      ReflectedUniverse r = new ReflectedUniverse();
      try {
        r.setVar("xml", xml);
        r.setVar("omexmlMeta", dest);
        r.exec("omexmlMeta.createRoot(xml)");
      }
      catch (ReflectException exc) {
        if (FormatHandler.debug) LogTools.trace(exc);
      }
    }
    else {
      // metadata store is incompatible; create an OME-XML
      // metadata object and copy it into the destination
      IMetadata src = createOMEXMLMetadata(xml);
      convertMetadata(src, dest);
    }
  }

  /**
   * Copies information from a metadata retrieval object
   * (source) into a metadata store (destination).
   */
  public static void convertMetadata(MetadataRetrieve src, MetadataStore dest) {
    MetadataConverter.convertMetadata(src, dest);
  }

  // -- Helper methods --

  /**
   * Checks whether the given object is an instance of the specified class.
   * @return True iff the object is an instance of the given class
   *   from the specified package.
   */
  private static boolean isInstance(Object o,
    String packageName, String className)
  {
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import " + packageName + "." + className);
      Class c = (Class) r.getVar(className);
      return c.isInstance(o);
    }
    catch (ReflectException exc) {
      if (FormatHandler.debug) LogTools.trace(exc);
    }
    return false;
  }

}
