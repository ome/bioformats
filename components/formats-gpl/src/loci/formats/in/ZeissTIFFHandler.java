/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import loci.formats.in.BaseZeissReader;
import loci.formats.in.BaseZeissReader.Charset;
import loci.formats.in.BaseZeissReader.FeatureType;
import loci.common.DataTools;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler for parsing XML in Zeiss TIFF files.
 *
 * @author Roger Leigh <r.leigh at dundee.ac.uk>
 */
public class ZeissTIFFHandler extends DefaultHandler {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ZeissTIFFHandler.class);

  // -- Fields --

  // Reader
  BaseZeissReader reader;

  // Stack of XML elements to keep track of placement in the tree.
  private Stack<String> nameStack = new Stack<String>();
  // CDATA text stored while parsing.  Note that this is limited to a
  // single span between two tags, and CDATA with embedded elements is
  // not supported (and not present in the Zeiss TIFF format).
  private String cdata = new String();

  // Main metadata tags for the image.
  public TagSet main_tagset = new TagSet();
  // Per-plane metadata for the image.
  public ArrayList<Plane> planes = new ArrayList<Plane>();
  // Scaling metadata for the image.
  public ArrayList<Scaling> scalings = new ArrayList<Scaling>();
  // Layer annotations (contain Shapes).
  public ArrayList<BaseZeissReader.Layer> layers = new ArrayList<BaseZeissReader.Layer>();

  // Current tags (during parsing).
  private TagSet current_tagset;
  // Current tag (during parsing).
  private BaseZeissReader.Tag current_tag;
  // Current scaling (during parsing).
  private Scaling current_scaling;
  // Current layer (during parsing).
  private BaseZeissReader.Layer current_layer;
  // Current shape (during parsing).
  private BaseZeissReader.Shape current_shape;
  // Found planes (during parsing).
  private Set<String> planeNames = new HashSet<String>();
  // Number of tags in current Tags block
  int tag_count;
  // Number of layers in current Layers block
  int layer_count;
  // Number of shapes in current Shapes block
  int shape_count;

  // -- ZeissTIFFHandler API methods --

  ZeissTIFFHandler(ZeissTIFFReader reader) {
    this.reader = reader;
  }

  @Override
  public String toString()
  {
    String s = new String("TIFF-XML parsing\n");
    s += main_tagset;
    s += '\n';
    for (Scaling sc : scalings ) {
      s += sc;
      s += '\n';
    }
    for (Plane p : planes ) {
      s += p;
      s += '\n';
    }
    return s;
  }

  // -- DefaultHandler API methods --

  @Override
  public void endElement(String uri,
      String localName,
      String qName) {
    if (!nameStack.empty() && nameStack.peek().equals(qName))
      nameStack.pop();

    if (qName.equals("ROOT")) {
      // Finalise data.
    }
    else if (qName.equals("Layers")) {
      // No nothing; already handled.
    }
    else if (qName.equals("Shapes")) {
      // No nothing; already handled.
    }
    else if (qName.startsWith("Item")) {
      // Inside a Layer or Shape annotation.  Determine which it is.
      if (nameStack.peek().equals("Layers")) {
        if (current_layer != null)
          layers.add(current_layer);
        current_layer = null;
      }
      else if (nameStack.peek().equals("Shapes")) {
        if (current_shape != null)
          current_layer.shapes.add(current_shape);
        current_shape = null;
      }
      else
        LOGGER.info("Parse error: tag found out of place: {}", qName);
    }
    else if (qName.equals("Scaling")) {
      // Scaling metadata.  __Version, Key, Category, Factor_n,
      // Type_n, Unit_n, Origin_n, Angle_n and Matrix_n tags are
      // valid here.
      if (current_scaling != null)
        scalings.add(current_scaling);
      current_scaling = null;
    }
    else if (qName.equals("Key")) {
      if (current_scaling != null) {
        if (current_scaling.key == null)
          current_scaling.key = cdata;
        else
          LOGGER.debug("Key already set");
      }
    }
    else if (qName.equals("Category")) {
      if (current_scaling != null) {
        if (current_scaling.category == null)
          current_scaling.category = Integer.parseInt(cdata);
        else
          LOGGER.debug("Category already set");
      }
    }
    else if (qName.startsWith("Factor_")) {
      Scaling.Dimension d = current_scaling.getDimension(qName);
      d.factor = parseDouble(cdata);
    }
    else if (qName.startsWith("Type_")) {
      Scaling.Dimension d = current_scaling.getDimension(qName);
      d.type = Integer.parseInt(cdata);
    }
    else if (qName.startsWith("Unit_")) {
      Scaling.Dimension d = current_scaling.getDimension(qName);
      try {
        d.unit = Integer.parseInt(cdata);
      }
      catch (Exception e) {
      }
    }
    else if (qName.startsWith("Origin_")) {
      Scaling.Dimension d = current_scaling.getDimension(qName);
      d.origin = parseDouble(cdata);
    }
    else if (qName.startsWith("Angle_")) {
      Scaling.Dimension d = current_scaling.getDimension(qName);
      d.angle = parseDouble(cdata);
    }
    else if (qName.startsWith("Matrix_")) {
      String value = qName.substring(qName.indexOf("_") + 1);
      Integer index = Integer.parseInt(value);
      Double mval = parseDouble(cdata);
      current_scaling.matrix.put(index, mval);
    }
    else if (qName.equals("Tags")) {
      // Save tags.
      if (!nameStack.empty())
      {
        String parent = nameStack.peek();
        if (parent.equals("ROOT"))
        {
          main_tagset = current_tagset;
          current_tagset = null;
        }
        else if (parent.equals("Scaling"))
        {
          current_scaling.tagset = current_tagset;
          current_tagset = null;
        }
        else if (nameStack.size() == 2) // Plane at top-level
        {
          Plane plane = new Plane(parent, current_tagset);
          current_tagset = null;
          planes.add(plane);
          planeNames.add(plane.basename);
        }
      }
    }
    else if (qName.equals("__Version")) {
      // Do nothing.
    }
    else if (qName.equals("AttributeShape")) {
      // Found in Layers.  Unknown purpose (empty in samples).  Do nothing.
    }
    else if (qName.equals("SourceName")) {
      // Found in Shapes Itemm.  Unknown purpose (empty in samples).  Do nothing.
    }
    else if (qName.equals("PredefinedStrings")) {
      // Found in Shapes Itemm.  Unknown purpose (empty in samples).  Do nothing.
    }
    else if (qName.equals("Dummy")) {
      // Found in Shapes Itemm.  Unknown purpose (empty in samples).  Do nothing.
    }
    else if (qName.equals("Features")) {
      // Found in Shapes Itemm.  Measurement features to record in the analysis results data table.  Do nothing.  Probably should be preserved in metadata.
    }
    else if (qName.equals("DrawFeatures")) {
      // Found in Shapes Itemm.  This is a measurement feature to display as part of the measurement overlay (in the Text element).  Do nothing.  Probably should be preserved in metadata.
      // Measurements only.
    }
    else if (qName.equals("InputMethod")) {
      // Found in Shapes Itemm.  Unknown purpose (empty in samples).  Do nothing.
    }
    else if (qName.equals("HandleSize2")) {
      // Found in Shapes Itemm.  Unknown purpose; probably UI hint, but no observed effect.
      current_shape.handleSize = Integer.parseInt(cdata);
    }
    else if (qName.equals("Text")) {
      current_shape.text = cdata;
      //String t = new String("Test: (µm), text=" + cdata);
      //System.out.println(t);
    }
    else if (qName.equals("PointCount")) {
      current_shape.pointCount = Integer.parseInt(cdata);
    }
    else if (qName.equals("Points")) {
      cdata = cdata.replaceAll("\\p{Cntrl}|\\p{Space}", "");
      String[] numbers = cdata.split(",");
      byte[] raw = new byte[numbers.length];
      for (int i = 0; i < raw.length; i++) {
        raw[i] = (byte) Integer.parseInt(numbers[i]);
      }

      current_shape.points = new double[current_shape.pointCount*2];
      for (int i = 0; i < current_shape.pointCount; i++) {
        current_shape.points[(i*2)] = DataTools.bytesToDouble(raw, (i*2)*8, true);
        current_shape.points[(i*2)+1] = DataTools.bytesToDouble(raw, ((i*2)+1)*8, true);
      }
    }
    else if (qName.equals("FontName")) {
      current_shape.fontName = cdata;
    }
    else if (qName.equals("Name")) {
      current_shape.name = cdata;
    }
    else if (qName.equals("SourceTagId")) {
      int value = Integer.parseInt(cdata);
      current_shape.tagID = reader.new Tag(value, BaseZeissReader.Context.MAIN);
    }
    else if (qName.equals("ShapeAttributes")) {
      // Found in Shapes Itemm.  This appears to be a horror of the first
      // order, a comma separated list of 8-bit numbers, which appears to
      // be a dump of a raw structure including doubles, uint32 integers
      // and enums, plus padding.  Attempt to convert the "serialised"
      // data back into something usable.  Initially remove all
      // non-printing characters from the cdata.
      cdata = cdata.replaceAll("\\p{Cntrl}|\\p{Space}", "");
      //cdata.replaceAll("[[:cntrl:]]", "");
      String[] numbers = cdata.split(",");
      byte[] raw = new byte[numbers.length];
      for (int i = 0; i < raw.length; i++) {
        raw[i] = (byte) Integer.parseInt(numbers[i]);
      }

      if (raw.length >= 152) {
        // Note that all coordinates have the origin in the upper left corner.
        // We only have examples of packed structures of 156 bytes.
        int isize = DataTools.bytesToInt(raw, 0, true);
        if (raw.length < isize)
          LOGGER.info("ShapeAttributes length ({}) is less than internal size ({})!  Trying to continue...", raw.length, isize);
        int type = DataTools.bytesToInt(raw, 4, true);
        // Annotation feature type.
        current_shape.type = FeatureType.get(type);
        current_shape.unknown2 = DataTools.bytesToInt(raw, 8, true);
        current_shape.unknown3 = DataTools.bytesToInt(raw, 12, true);
        current_shape.x1 = DataTools.bytesToInt(raw, 16, true);
        current_shape.y1 = DataTools.bytesToInt(raw, 20, true);
        current_shape.x2 = DataTools.bytesToInt(raw, 24, true);
        current_shape.y2 = DataTools.bytesToInt(raw, 28, true);
        current_shape.width = current_shape.x2 - current_shape.x1;
        current_shape.height = current_shape.y2 - current_shape.y1;
        current_shape.unknown4 = DataTools.bytesToInt(raw, 32, true);
        current_shape.unknown5 = DataTools.bytesToInt(raw, 36, true);
        current_shape.unknown6 = parseColor(raw[40], raw[41], raw[42]);
        current_shape.unknown7 = DataTools.bytesToInt(raw, 44, true);
        current_shape.fillColour = parseColor(raw[48], raw[49], raw[50]); // We don't include alpha from the file.
        current_shape.textColour = parseColor(raw[52], raw[53], raw[54]); // It's not clear that it's set to a
        current_shape.drawColour = parseColor(raw[56], raw[57], raw[58]); // sensible value.  (Not exposed in software.)
        current_shape.lineWidth = DataTools.bytesToInt(raw, 60, true);
        current_shape.drawStyle = BaseZeissReader.DrawStyle.get(DataTools.bytesToInt(raw, 64, true));
        current_shape.fillStyle = BaseZeissReader.FillStyle.get(DataTools.bytesToInt(raw, 68, true));
        current_shape.unknown8 = DataTools.bytesToInt(raw, 72, true);
        current_shape.strikeout = (DataTools.bytesToInt(raw, 76, true) != 0);
        // Windows TrueType font weighting.
        current_shape.fontWeight = DataTools.bytesToInt(raw, 80, true);
        current_shape.bold = (current_shape.fontWeight >= 600);
        current_shape.fontSize = DataTools.bytesToInt(raw, 84, true);
        current_shape.italic = (DataTools.bytesToInt(raw, 88, true) != 0);
        current_shape.underline = (DataTools.bytesToInt(raw, 92, true) != 0);
        current_shape.textAlignment = BaseZeissReader.TextAlignment.get(DataTools.bytesToInt(raw, 96, true));
        current_shape.unknown10 = DataTools.bytesToInt(raw, 100, true);
        current_shape.unknown11 = DataTools.bytesToInt(raw, 104, true);
        current_shape.unknown12 = DataTools.bytesToInt(raw, 108, true);
        current_shape.unknown13 = DataTools.bytesToInt(raw, 112, true);
        current_shape.unknown14 = DataTools.bytesToInt(raw, 116, true);
        current_shape.unknown15 = DataTools.bytesToInt(raw, 120, true);
        current_shape.unknown16 = DataTools.bytesToInt(raw, 124, true);
        current_shape.unknown17 = DataTools.bytesToInt(raw, 128, true);
        current_shape.unknown18 = DataTools.bytesToInt(raw, 132, true);
        current_shape.displayTag = (DataTools.bytesToInt(raw, 148, true) != 0); // FF00==1 0000==0.

        //current_shape.zpos = (DataTools.bytesToInt(raw, 104, true));
        current_shape.lineEndStyle = BaseZeissReader.LineEndStyle.get(DataTools.bytesToInt(raw, 136, true));
        current_shape.pointStyle = BaseZeissReader.PointStyle.get(DataTools.bytesToInt(raw, 136, true));
        current_shape.lineEndSize = DataTools.bytesToInt(raw, 140, true);
        current_shape.lineEndPositions = BaseZeissReader.LineEndPositions.get(DataTools.bytesToInt(raw, 144, true));
        if (isize >= 156)
          current_shape.charset = Charset.get(DataTools.bytesToInt(raw, 152, true));
      }
    }
    else if (qName.equals("Flags")) {
      if (qName.startsWith("Item")) {
        // Found in Itemn of Layers.  Unknown purpose (set to 1 in samples).  Do nothing.
      }
    }
    else if (qName.equals("Count")) {
      // Inside a Tag, Layer or Shape annotation.  Determine which it is.
      if (nameStack.peek().equals("Tags")) {
        this.tag_count = Integer.parseInt(cdata);
        current_tagset.count = Integer.parseInt(cdata); // TODO: Remove
      }
      else if (nameStack.peek().equals("Layers"))
        this.layer_count = Integer.parseInt(cdata);
      else if (nameStack.peek().equals("Shapes"))
        this.shape_count = Integer.parseInt(cdata);
      else
        LOGGER.info("Parse error: tag found out of place: {}", qName);
    }
    else if (qName.equals("Key")) {
      // Inside a Layers or Itemn (Layer) or Itemm (Shape) annotation.  Determine which it is.
      if (nameStack.peek().equals("Layers")) {
        // Unknown purpose.  Empty in sample files.
      }
      if (nameStack.peek().equals("Shapes")) {
        // Unknown purpose.  Set to "Default" in sample files.
      }
      else if (nameStack.peek().startsWith("Item")) {
        // See if we're in a Layers or a Shapes container
        int stackSize = nameStack.size();
        if (stackSize >= 2) {
          if (nameStack.get(stackSize -2).equals("Layers"))
            current_layer.key = Integer.parseInt(cdata);
          else if (nameStack.get(stackSize -2).equals("Shapes"))
            current_layer.key = Integer.parseInt(cdata);
        }
      }
      else
        LOGGER.info("Parse error: tag found out of place: {}", qName);
    }
    else if (qName.equals("Class")) {
      // AxioVision-specific CLSID?  Ignore.
    }
    else if (!nameStack.empty() && nameStack.peek().equals("Tags"))
    {
      String type = qName.substring(0,1);
      // Only process Ann, Inn and Vnn elements.  No other known
      // elements are found in a Tags block other than Count, so
      // ignore them.
      if (type.equals("A") || type.equals("I") || type.equals("V"))
      {
        String value = qName.substring(1);
        int index = Integer.parseInt(value);

        // A tags block should always be at least one element deep, so we should have a minimum of two elements on the stack.
        int stackSize = nameStack.size();
        BaseZeissReader.Context context = BaseZeissReader.Context.PLANE;
        if (stackSize >= 2)
        {
          if (nameStack.get(stackSize - 2).equals("ROOT"))
            context = BaseZeissReader.Context.MAIN;
          else if (nameStack.get(stackSize - 2).equals("Scaling"))
            context = BaseZeissReader.Context.SCALING;
        }

        // This checks and copes with index mismatches if the XML is bad.
        if (current_tag == null || current_tag.getIndex() != index)
          current_tag = reader.new Tag(index, context);

        // If the index is out of range, ignore the tag.  It will remain invalid.
        // Note that the index counts from zero, while the total is a count.
        if (current_tagset.found >= current_tagset.count)
          LOGGER.info("Found more tags then declared");
        if (type.equals("V")) // Set to null if empty/unset?
          current_tag.setValue(cdata);
        else if (type.equals("I")) // Skip if unset.
          current_tag.setKey(Integer.parseInt(cdata));
        else if (type.equals("A"))  // Set to 0 if unset...
          current_tag.setCategory(Integer.parseInt(cdata));
        else
          LOGGER.info("Unknown tag: {}", qName);
        if (current_tag.valid()) {
          current_tagset.tags.add(current_tag);
          current_tagset.found++;
        }
      }
      else
      {
        LOGGER.info("Unknown tag: {}", qName);
      }
    }
    else
    {
      // Presumably, this is a plane.  To verify that, we need to
      // check for the presence of an embedded Tags block, and then
      // for the presence of the appropriate plane-specific tags.
      // And, additionally, that the tag-based filename prefix
      // matches an existing file on disc.
      if (!planeNames.contains(qName))
        LOGGER.info("Unknown tag: {}", qName);
    }
    cdata = "";

  }

  @Override
  public void characters(char[] ch,
      int start,
      int length)
  {
    String s = new String(ch, start, length);
    cdata += s;
  }

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes)
  {
    cdata = "";


    if (qName.equals("Scaling")) {
      // Scaling metadata.  __Version, Key, Category, Factor_n,
      // Type_n, Unit_n, Origin_n, Angle_n and Matrix_n tags are
      // valid here.
      current_scaling = new Scaling();
    }
    else if (qName.equals("Tags")) {
      // Start of metadata block.  __Version, Count, Vn, In and An
      // tags are valid here.
      current_tagset = new TagSet();
    }
    else if (qName.startsWith("Item")) {
      // Start of a Layer or Shape annotation.  Determine which it is.
      if (nameStack.peek().equals("Layers"))
        current_layer = reader.new Layer();
      else if (nameStack.peek().equals("Shapes"))
        current_shape = reader.new Shape();
      else
        LOGGER.info("Parse error: tag found out of place: {}", qName);
    }
    else if (qName.equals("Class")) {
      // AxioVision-specific CLSID.  Ignore.
    }
    else {
      // Other or unknown tag; will be handled by endElement.
    }

    nameStack.push(qName);
  }

  // -- Helper classes and functions --

  /**
   * Parse a Double from a String.  Unlike the standard method, this one replaces commas with decimal points (if present).
   * @param number the number to parse
   * @return a Double.  0 if number was null.
   */
  private static double parseDouble(String number) {
    if (number != null) {
      number = number.replaceAll(",", ".");
      return Double.parseDouble(number);
    }
    return 0;
  }

  /**
   * Content of a single tag from a Tags block.
   */
  class Tag {
    // index number of the tag in the XML.  Not useful except for parsing validation.
    public int index;
    // key number of the tag (I element).  Needs mapping to a descriptive name.
    public int key;
    // value of the tag (V element).
    public String value;
    // category (presumed) of the tag (A element).
    public int category;

    /**
     * Constructor.
     * All variables are initialised to be invalid to permit later validation of correct parsing.
     * @param index the index number of the tag.
     */
    Tag (int index)
    {
      this.index = index;
      key = -1;
      value = null;
      category = -1;
    }

    /**
     * Check if the tag is valid (key, value and category have been set).
     * @return true if valid, otherwise false.
     */
    public boolean valid () {
      return key != -1 && value != null && category != -1;
    }

    @Override
    public String toString() {
      return new String("      T: K=" + key +
          " V=" + value + " C=" + category +
          " I=" + index);
    }
  }

  /**
   * A collection of tags from a single Tags block.
   */
  class TagSet
  {
    // Number of tags.  Used only for validation
    int count = 0;
    int found = 0;
    // Mapping between tag key number and tag.
    public ArrayList<BaseZeissReader.Tag> tags = new ArrayList<BaseZeissReader.Tag>();

    @Override
    public String toString() {
      String s = new String("  Tags(" + count + "):\n");
      for (BaseZeissReader.Tag t : tags) {
        s += t;
        s += '\n';
      }
      return s;
    }
  }

  /**
   * Metadata for a single image plane.
   */
  public class Plane
  {
    // Name of the plane.
    public String basename;
    // Tags associated with the plane.
    public TagSet tagset;

    /**
     * Constructor
     * @param basename the name of the plane.
     * @param tagset the tags for the plane.
     */
    Plane(String basename, TagSet tagset) {
      this.basename = basename;
      this.tagset = tagset;
    }

    @Override
    public String toString() {
      String s = new String("  Plane: " + basename + '\n');
      s += tagset;
      s += '\n';
      return s;
    }
  }

  /**
   * Scaling metadata.
   */
  class Scaling
  {
    // Key name for this scaling.
    String key;
    // Category for this scaling (numeric).
    Integer category;
    // Mapping between dimension number and dimension object.
    Map<Integer, Dimension> dims = new HashMap<Integer, Dimension>();
    // Matrix.  Purpose unknown.
    Map<Integer,Double> matrix = new HashMap<Integer,Double>();
    // Metadata associated with this scaling.
    TagSet tagset;

    /**
     * Get a dimension by its index.  If the dimension does not yet
     * exist, a new one will be created.
     * @param key An XML element name.  The index number will be parsed
     * from this name.
     * @return A Dimension object.
     */
    Dimension
    getDimension(String key)
    {
      String value = key.substring(key.indexOf("_") + 1);
      Integer index = Integer.parseInt(value);
      Dimension d = dims.get(index);
      if (d == null)
      {
        d = new Dimension(index);
        dims.put(index, d);
      }
      return d;
    }

    @Override
    public String toString() {
      String s = new String("Scaling\n");
      s += "  Key=" + key + "\n";
      s += "  Cat=" + category + "\n";

      List<Integer> dimarray = new LinkedList<Integer>(dims.keySet());
      Collections.sort(dimarray);
      for (Integer dim : dimarray)
      {
        Dimension d = dims.get(dim);
        s += "  Dim" + dim + "=\n"
            + "    Ftr=" + d.factor + "\n"
            + "    Typ=" + d.type + "\n"
            + "    Unt=" + d.unit + "\n"
            + "    Org=" + d.origin + "\n"
            + "    Ang=" + d.angle + "\n";

      }
      s += tagset;
      s+= '\n';

      return s;
    }

    /**
     * Scaling information for a single dimension.
     */
    class Dimension
    {
      // Dimension number (012) == (xyz)?
      Integer dimension;
      // Scale factor.
      Double factor;
      // ?
      Integer type;
      // Unit type.
      // 0 = No scale (pixel)
      // 72 = meter, 72=decimeter (spec bogus--duplicate value), micrometer=76, nanometer=77.
      // inch=81; mil=84 (documented as micrometres, but a mil is 1/1000")
      // TIME:
      // second=136, millisecond=139, microsecond=140, minute=145, hour=146,
      Integer unit;
      // Origin (of scale bar or measurement?)
      Double origin;
      // Angle (of scale bar or measurement?)
      Double angle;

      /**
       * Constructor.
       * @param dimension The dimension number.
       */
      Dimension(Integer dimension)
      {
        this.dimension = dimension;
      }
    }
  }

  // TODO: Replace me with a proper Color class when available.
  protected static int parseColor(byte r, byte g, byte b) {
    return ((r&0xFF) << 24) | ((g&0xFF) << 16) | ((b&0xFF) << 8);
  }

}
