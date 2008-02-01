//
// MetadataTools.java
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

package loci.formats;

import loci.formats.meta.MetadataConverter;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

/**
 * A utility class for working with metadata objects,
 * including {@link MetadataStore}, {@link MetadataRetrieve},
 * and OME-XML strings.
 * Most of the methods require the optional {@link loci.formats.ome}
 * package, and optional ome-java.jar library, to be present at runtime.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/MetadataTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/MetadataTools.java">SVN</a></dd></dl>
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
  public static MetadataStore createOMEXMLMetadata() {
    return createOMEXMLMetadata(null);
  }

  /**
   * Creates an OME-XML metadata object using reflection, to avoid
   * direct dependencies on the optional {@link loci.formats.ome} package,
   * wrapping a DOM representation of the given OME-XML string.
   * @return A new instance of {@link loci.formats.ome.OMEXMLMetadata},
   *   or null if the class is not available.
   */
  public static MetadataStore createOMEXMLMetadata(String xml) {
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
  public static MetadataStore createOMEXMLMetadata(String xml, String version) {
    Object ome = null;
    MetadataStore store = null;
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      ome = createOMEXMLRoot(xml);
      if (version == null) {
        if (ome == null) {
          // default to newest schema version
          r.exec("import ome.xml.OMEXMLFactory");
          //version = (String) r.exec("OMEXMLFactory.LATEST_VERSION");
          version = "2003-FC";
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
      store = (MetadataStore) r.exec("new " + metaClass + "()");

      // attach OME root node to metadata object
      if (ome != null) store.setRoot(ome);
    }
    catch (ReflectException exc) {
      if (FormatHandler.debug) LogTools.trace(exc);
    }
    return store;
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
   * Gets the schema version for the given OME-XML metadata or root object.
   * @return OME-XML schema version, or null if the object is not an instance
   *   of {@link loci.formats.ome.OMEXMLMetadata} or ome.xml.OMEXMLNode.
   */
  public static String getOMEXMLVersion(Object o) {
    String name = o.getClass().getName();
    if (isOMEXMLMetadata(o)) {
      final String prefix = "loci.formats.ome.OMEXML";
      final String suffix = "Metadata";
      if (name.startsWith(prefix) && name.endsWith(suffix)) {
        String numbers =
          name.substring(prefix.length(), name.length() - suffix.length());
        if (numbers.length() == 6) {
          return numbers.substring(0, 4) + "-" + numbers.substring(4, 6);
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

  /**
   * Extracts an OME-XML metadata string from the given metadata object,
   * by converting to an OME-XML metadata object if necessary.
   */
  public static String getOMEXML(MetadataRetrieve src) {
    MetadataStore omexmlMeta;
    if (isOMEXMLMetadata(src)) {
      // the metadata is already an OME-XML metadata object
      omexmlMeta = (MetadataStore) src;
    }
    else {
      // populate a new OME-XML metadata object with metadata
      // converted from the non-OME-XML metadata object
      omexmlMeta = createOMEXMLMetadata();
      MetadataTools.convertMetadata(src, omexmlMeta);
    }
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
    int oldSeries = r.getSeries();
    for (int i=0; i<r.getSeriesCount(); i++) {
      r.setSeries(i);
      store.setPixelsSizeX(new Integer(r.getSizeX()), i, 0);
      store.setPixelsSizeY(new Integer(r.getSizeY()), i, 0);
      store.setPixelsSizeZ(new Integer(r.getSizeZ()), i, 0);
      store.setPixelsSizeC(new Integer(r.getSizeC()), i, 0);
      store.setPixelsSizeT(new Integer(r.getSizeT()), i, 0);
      store.setPixelsPixelType(
        FormatTools.getPixelTypeString(r.getPixelType()), i, 0);
      store.setPixelsBigEndian(new Boolean(!r.isLittleEndian()), i, 0);
      store.setPixelsDimensionOrder(r.getDimensionOrder(), i, 0);
    }
    r.setSeries(oldSeries);
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
      r.exec("omexmlMeta.populateOriginalMetadata(key, value)");
    }
    catch (ReflectException exc) {
      if (FormatHandler.debug) LogTools.trace(exc);
    }
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
      MetadataRetrieve src = (MetadataRetrieve) createOMEXMLMetadata(xml);
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
