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

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.LogTools;
import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.common.XMLTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataConverter;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


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

  // -- Static fields --

  private static Templates reorderXSLT =
    XMLTools.getStylesheet("meta/reorder-2008-09.xsl", MetadataTools.class);

  /** Upgrade/downgrade stylesheets. */
  private static final Templates UPGRADE_2008_02 =
    XMLTools.getStylesheet("meta/2008-02-to-2008-09.xsl", MetadataTools.class);
  private static final Templates UPGRADE_2009_09 =
    XMLTools.getStylesheet("meta/2009-09-to-2010-04.xsl", MetadataTools.class);
  private static final Templates UPGRADE_2010_04 =
    XMLTools.getStylesheet("meta/2010-04-to-2010-06.xsl", MetadataTools.class);
  private static final Templates DOWNGRADE_2010_06 =
    XMLTools.getStylesheet("meta/2010-06-to-2008-02.xsl", MetadataTools.class);
  private static final Templates DOWNGRADE_2011_06 =
    XMLTools.getStylesheet("meta/2011-06-to-2010-06.xsl", MetadataTools.class);
  private static final Templates DOWNGRADE_2012_06 =
    XMLTools.getStylesheet("meta/2012-06-to-2011-06.xsl", MetadataTools.class);

  // -- Constructor --

  private MetadataTools() { }

  // -- Utility methods - OME-XML --

  public static String downgradeOMEXML(String xml, String version) {
    String newXML = xml;
    if (version.equals("201206")) {
      try {
        newXML = XMLTools.transformXML(newXML, DOWNGRADE_2012_06);
      }
      catch (IOException exc) {
        LogTools.traceDebug(exc);
      }
      if (newXML != null) {
        version = "201106";
      }
    }
    if (version.equals("201106")) {
      try {
        newXML = XMLTools.transformXML(newXML, DOWNGRADE_2011_06);
      }
      catch (IOException exc) {
        LogTools.traceDebug(exc);
      }
      if (newXML != null) {
        version = "201006";
      }
    }
    if (version.equals("200909")) {
      try {
        newXML = XMLTools.transformXML(newXML, UPGRADE_2009_09);
      }
      catch (IOException exc) {
        LogTools.traceDebug(exc);
      }
      if (newXML != null) {
        version = "201004";
      }
    }
    if (version.equals("201004")) {
      try {
        newXML = XMLTools.transformXML(newXML, UPGRADE_2010_04);
      }
      catch (IOException exc) {
        LogTools.traceDebug(exc);
      }
      if (newXML != null) {
        version = "201006";
      }
    }
    if (version.equals("201006")) {
      try {
        newXML = XMLTools.transformXML(newXML, DOWNGRADE_2010_06);
      }
      catch (IOException exc) {
        LogTools.traceDebug(exc);
      }
      if (newXML != null) {
        version = "200802";
      }
    }
    if (version.equals("200802")) {
      try {
        newXML = XMLTools.transformXML(newXML, UPGRADE_2008_02);
      }
      catch (IOException exc) {
        LogTools.traceDebug(exc);
      }
      if (newXML != null) {
        version = "200809";
      }
    }

    return newXML;
  }

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
      if (xml != null) {
        xml = XMLTools.sanitizeXML(xml);
      }
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
      LogTools.traceDebug(exc);
    }
    if (ome == null) meta.createRoot();
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
      LogTools.traceDebug(exc);

      try {
        r.exec("import ome.xml.DOMUtil");
        r.exec("import ome.xml.OMEXMLFactory");
        r.setVar("xml", xml);
        r.exec("rootNode = OMEXMLFactory.parseOME(xml)");
        r.exec("rootElement = rootNode.getDocumentElement()");
        r.setVar("attribute", "xmlns");
        r.exec("xmlns = DOMUtil.getAttribute(attribute, rootElement)");

        String version = r.getVar("xmlns").toString();
        version = version.substring(version.lastIndexOf("/") + 1);
        version = version.replaceAll("\\W", "");

        return createOMEXMLRoot(downgradeOMEXML(xml, version));
      }
      catch (ReflectException e) {
        LogTools.traceDebug(e);
      }
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
        LogTools.traceDebug(exc);
      }
    }
    return null;
  }

  /**
   * Returns a {@link loci.formats.ome.OMEXMLMetadata} object with the same
   * contents as the given MetadataRetrieve, converting it if necessary.
   */
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
   * If the given IMetadata is an instance of
   * {@link loci.formats.ome.OMEXMLMetadata}, the OriginalMetadata hashtable
   * is returned. Otherwise, returns null.
   */
  public static Hashtable<String, String> getOriginalMetadata(IMetadata src) {
    if (isOMEXMLMetadata(src)) {
      ReflectedUniverse r = new ReflectedUniverse();
      r.setVar("omexml", src);
      try {
        return
          (Hashtable<String, String>) r.exec("omexml.getOriginalMetadata()");
      }
      catch (ReflectException exc) {
        LogTools.traceDebug(exc);
      }
    }
    return null;
  }

  /**
   * Extracts an OME-XML metadata string from the given metadata object,
   * by converting to an OME-XML metadata object if necessary.
   */
  public static String getOMEXML(MetadataRetrieve src) {
    IMetadata omexmlMeta = getOMEMetadata(src);
    ReflectedUniverse r = new ReflectedUniverse();
    r.setVar("omexmlMeta", omexmlMeta);
    String xml = null;
    try {
      xml = (String) r.exec("omexmlMeta.dumpXML()");
    }
    catch (ReflectException exc) {
      LogTools.traceDebug(exc);
    }
    String reordered = null;
    try {
      reordered = XMLTools.transformXML(xml, reorderXSLT);
    }
    catch (IOException exc) {
      LogTools.traceDebug(exc);
    }
    return reordered == null ? xml : reordered;
  }

  /**
   * Attempts to validate the given OME-XML string using
   * Java's XML validation facility. Requires Java 1.5+.
   *
   * @param xml XML string to validate.
   * @return true if the XML successfully validates.
   */
  public static boolean validateOMEXML(String xml) {
    return validateOMEXML(xml, false);
  }

  /**
   * Attempts to validate the given OME-XML string using
   * Java's XML validation facility. Requires Java 1.5+.
   *
   * @param xml XML string to validate.
   * @param pixelsHack Whether to ignore validation errors
   *   due to childless Pixels elements
   * @return true if the XML successfully validates.
   */
  public static boolean validateOMEXML(String xml, boolean pixelsHack) {
    // HACK: Inject a TiffData element beneath any childless Pixels elements.
    if (pixelsHack) {
      // convert XML string to DOM
      Document doc = null;
      Exception exception = null;
      try {
        doc = XMLTools.parseDOM(xml);
      }
      catch (ParserConfigurationException exc) { exception = exc; }
      catch (SAXException exc) { exception = exc; }
      catch (IOException exc) { exception = exc; }
      if (exception != null) {
        LogTools.println("Malformed OME-XML:");
        LogTools.trace(exception);
        return false;
      }

      // inject TiffData elements as needed
      NodeList list = doc.getElementsByTagName("Pixels");
      for (int i=0; i<list.getLength(); i++) {
        Node node = list.item(i);
        NodeList children = node.getChildNodes();
        boolean needsTiffData = true;
        for (int j=0; j<children.getLength(); j++) {
          Node child = children.item(j);
          String name = child.getLocalName();
          if ("TiffData".equals(name) || "BinData".equals(name)) {
            needsTiffData = false;
            break;
          }
        }
        if (needsTiffData) {
          // inject TiffData element
          Node tiffData = doc.createElement("TiffData");
          node.insertBefore(tiffData, node.getFirstChild());
        }
      }

      // convert tweaked DOM back to XML string
      try {
        xml = XMLTools.getXML(doc);
      }
      catch (TransformerConfigurationException exc) { exception = exc; }
      catch (TransformerException exc) { exception = exc; }
      if (exception != null) {
        LogTools.println("Internal XML conversion error:");
        LogTools.trace(exception);
        return false;
      }
    }
    return XMLTools.validateXML(xml, "OME-XML");
  }

  /**
   * Check for various required attributes, and insert default values if they
   * are missing.
   *
   * This is intended to correct for invalidities in the original XML, not
   * problems with Bio-Formats' parsing or the upgrade/downgrade stylesheets.
   */
  public static void ensureValidOMEXML(IMetadata meta) {
    for (int i=0; i<meta.getExperimenterCount(); i++) {
      if (meta.getExperimenterID(i) == null) {
        meta.setExperimenterID("Experimenter:" + i, i);
      }

      if (meta.getExperimenterFirstName(i) == null &&
        meta.getExperimenterLastName(i) == null &&
        meta.getExperimenterEmail(i) == null &&
        meta.getExperimenterInstitution(i) == null &&
        meta.getExperimenterOMEName(i) == null)
      {
        meta.setExperimenterInstitution("Unknown", i);
      }
    }

    for (int i=0; i<meta.getInstrumentCount(); i++) {
      if (meta.getInstrumentID(i) == null) {
        meta.setInstrumentID("Instrument:" + i, i);
      }

      if (meta.getMicroscopeID(i) != null) {
        meta.setMicroscopeType("Unknown", i);
      }

      for (int q=0; q<meta.getObjectiveCount(i); q++) {
        if (meta.getObjectiveID(i, q) == null) {
          meta.setObjectiveID("Objective:" + i + ":" + q, i, q);
        }

        if (meta.getObjectiveCorrection(i, q) == null) {
          meta.setObjectiveCorrection("Other", i, q);
        }

        if (meta.getObjectiveImmersion(i, q) == null) {
          meta.setObjectiveImmersion("Other", i, q);
        }
      }

      for (int q=0; q<meta.getFilterCount(i); q++) {
        if (meta.getFilterID(i, q) == null) {
          meta.setFilterID("Filter:" + i + ":" + q, i, q);
        }
      }

      for (int q=0; q<meta.getFilterSetCount(i); q++) {
        if (meta.getFilterSetID(i, q) == null) {
          meta.setFilterID("FilterSet:" + i + ":" + q, i, q);
        }
      }

      for (int q=0; q<meta.getDetectorCount(i); q++) {
        if (meta.getDetectorID(i, q) == null) {
          meta.setDetectorID("Detector:" + i + ":" + q, i, q);
        }

        if (meta.getDetectorType(i, q) == null) {
          meta.setDetectorType("Other", i, q);
        }
      }

      for (int q=0; q<meta.getOTFCount(i); q++) {
        if (meta.getOTFID(i, q) == null) {
          meta.setOTFID("OTF:" + i + ":" + q, i, q);
        }
      }

      for (int q=0; q<meta.getDichroicCount(i); q++) {
        if (meta.getDichroicID(i, q) == null) {
          meta.setDichroicID("Dichroic:" + i + ":" + q, i, q);
        }
      }

      for (int q=0; q<meta.getLightSourceCount(i); q++) {
        if (meta.getLightSourceID(i, q) == null) {
          meta.setLightSourceID("LightSource:" + i + ":" + q, i, q);
        }

        if (meta.getLaserType(i, q) == null &&
          meta.getFilamentType(i, q) == null && meta.getArcType(i, q) == null)
        {
          meta.setLaserType("Unknown", i, q);
          meta.setLaserLaserMedium("Unknown", i, q);
        }
      }
    }

    for (int i=0; i<meta.getProjectCount(); i++) {
      if (meta.getProjectID(i) == null) {
        meta.setProjectID("Project:" + i, i);
      }
    }

    for (int i=0; i<meta.getDatasetCount(); i++) {
      if (meta.getDatasetID(i) == null) {
        meta.setDatasetID("Dataset:" + i, i);
      }
    }

    for (int i=0; i<meta.getImageCount(); i++) {
      if (meta.getImageID(i) == null) {
        meta.setImageID("Image:" + i, i);
      }

      if (meta.getImageDefaultPixels(i) == null) {
        meta.setImageDefaultPixels(meta.getPixelsID(i, 0), i);
      }

      if (meta.getStageLabelName(i) == null &&
        (meta.getStageLabelX(i) != null || meta.getStageLabelY(i) != null ||
        meta.getStageLabelZ(i) != null))
      {
        meta.setStageLabelName("unknown", i);
      }

      for (int q=0; q<meta.getPixelsCount(i); q++) {
        if (meta.getPixelsID(i, q) == null) {
          meta.setPixelsID("Pixels:" + i + ":" + q, i, q);
        }

        String order = meta.getPixelsDimensionOrder(i, q);
        int sizeZ = meta.getPixelsSizeZ(i, q);
        int sizeC = meta.getPixelsSizeC(i, q);
        int sizeT = meta.getPixelsSizeT(i, q);
        for (int p=0; p<meta.getPlaneCount(i, q); p++) {
          if (meta.getPlaneTheZ(i, q, p) == null ||
            meta.getPlaneTheC(i, q, p) == null ||
            meta.getPlaneTheT(i, q, p) == null)
          {
            int[] zct = FormatTools.getZCTCoords(order, sizeZ, sizeC, sizeT,
              meta.getPlaneCount(i, q), p);
            meta.setPlaneTheZ(zct[0], i, q, p);
            meta.setPlaneTheC(zct[1], i, q, p);
            meta.setPlaneTheT(zct[2], i, q, p);
          }
        }
      }

      for (int q=0; q<meta.getMicrobeamManipulationCount(i); q++) {
        if (meta.getMicrobeamManipulationID(i, q) == null) {
          meta.setMicrobeamManipulationID(
            "MicrobeamManipulation:" + i + ":" + q, i, q);
        }
      }

      for (int q=0; q<meta.getRegionCount(i); q++) {
        if (meta.getRegionID(i, q) == null) {
          meta.setRegionID("Region:" + i + ":" + q, i, q);
        }

        if (meta.getRegionTag(i, q) == null) {
          meta.setRegionTag("", i, q);
        }
      }

      for (int q=0; q<meta.getLogicalChannelCount(i); q++) {
        if (meta.getLogicalChannelID(i, q) == null) {
          meta.setLogicalChannelID("LogicalChannel:" + i + ":" + q, i, q);
        }
      }

      for (int q=0; q<meta.getROICount(i); q++) {
        if (meta.getROIID(i, q) == null) {
          meta.setROIID("ROI:" + i + ":" + q, i, q);
        }
      }
    }

    for (int i=0; i<meta.getExperimentCount(); i++) {
      if (meta.getExperimentID(i) == null) {
        meta.setExperimentID("Experiment:" + i, i);
      }

      if (meta.getExperimentType(i) == null) {
        meta.setExperimentType("Other", i);
      }
    }

    for (int i=0; i<meta.getGroupCount(); i++) {
      if (meta.getGroupID(i) == null) {
        meta.setGroupID("Group:" + i, i);
      }
    }
  }

  /**
   * Populates the 'pixels' element of the given metadata store, using core
   * metadata from the given reader.
   */
  public static void populatePixels(MetadataStore store, IFormatReader r) {
    populatePixels(store, r, false, true);
  }

  /**
   * Populates the 'pixels' element of the given metadata store, using core
   * metadata from the given reader.  If the 'doPlane' flag is set,
   * then the 'plane' elements will be populated as well.
   */
  public static void populatePixels(MetadataStore store, IFormatReader r,
    boolean doPlane)
  {
    populatePixels(store, r, doPlane, true);
  }

  /**
   * Populates the 'pixels' element of the given metadata store, using core
   * metadata from the given reader.  If the 'doPlane' flag is set,
   * then the 'plane' elements will be populated as well.
   * If the 'doImageName' flag is set, then the image name will be populated
   * as well.  By default, 'doImageName' is true.
   */
  public static void populatePixels(MetadataStore store, IFormatReader r,
    boolean doPlane, boolean doImageName)
  {
    if (store == null || r == null) return;
    int oldSeries = r.getSeries();
    for (int i=0; i<r.getSeriesCount(); i++) {
      r.setSeries(i);
      if (doImageName) store.setImageName(r.getCurrentFile(), i);
      String pixelsID = createLSID("Pixels", i, 0);
      store.setPixelsID(pixelsID, i, 0);
      store.setImageDefaultPixels(pixelsID, i);
      store.setPixelsSizeX(new Integer(r.getSizeX()), i, 0);
      store.setPixelsSizeY(new Integer(r.getSizeY()), i, 0);
      store.setPixelsSizeZ(new Integer(r.getSizeZ()), i, 0);
      store.setPixelsSizeC(new Integer(r.getSizeC()), i, 0);
      store.setPixelsSizeT(new Integer(r.getSizeT()), i, 0);
      store.setPixelsPixelType(
        FormatTools.getPixelTypeString(r.getPixelType()), i, 0);
      store.setPixelsBigEndian(new Boolean(!r.isLittleEndian()), i, 0);
      store.setPixelsDimensionOrder(r.getDimensionOrder(), i, 0);
      if (r.getSizeC() > 0) {
        Integer sampleCount = new Integer(r.getRGBChannelCount());
        for (int c=0; c<r.getEffectiveSizeC(); c++) {
          store.setLogicalChannelSamplesPerPixel(sampleCount, i, c);
          for (int rgb=0; rgb<r.getRGBChannelCount(); rgb++) {
            store.setChannelComponentIndex(
              new Integer(c * r.getRGBChannelCount() + rgb), i, c, rgb);
            store.setChannelComponentPixels(pixelsID, i, c, rgb);
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
    MetadataStore store = asStore(src);
    if (store != null && store.getRoot() == null) {
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
    store.setImageCreationDate(DateTools.convertDate(time, DateTools.UNIX),
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
      LogTools.traceDebug(exc);
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
      LogTools.traceDebug(exc);
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
        LogTools.traceDebug(exc);
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

  // -- Utility methods - casting --

  /**
   * Gets the given {@link MetadataRetrieve} object as a {@link MetadataStore}.
   * Returns null if the object is incompatible and cannot be casted.
   */
  public static MetadataStore asStore(MetadataRetrieve meta) {
    return meta instanceof MetadataStore ? (MetadataStore) meta : null;
  }

  /**
   * Gets the given {@link MetadataStore} object as a {@link MetadataRetrieve}.
   * Returns null if the object is incompatible and cannot be casted.
   */
  public static MetadataRetrieve asRetrieve(MetadataStore meta) {
    return meta instanceof MetadataRetrieve ? (MetadataRetrieve) meta : null;
  }

  // -- Utility methods - original metadata --

  /** Gets a sorted list of keys from the given hashtable. */
  public static String[] keys(Hashtable meta) {
    String[] keys = new String[meta.size()];
    meta.keySet().toArray(keys);
    Arrays.sort(keys);
    return keys;
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
      LogTools.traceDebug(exc);
    }
    return false;
  }

}
