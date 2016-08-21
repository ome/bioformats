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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Metadata structure for Prairie Technologies' TIFF-based format.
 * 
 * @author Curtis Rueden
 */
public class PrairieMetadata {

  /** {@code <Sequence>} elements, keyed on each sequence's {@code cycle}. */
  private final HashMap<Integer, Sequence> sequences =
    new HashMap<Integer, Sequence>();

  /** Table of key/value pairs at the top level. */
  private final ValueTable scanValues = new ValueTable();

  /** The first actual {@code <Sequence>} element. */
  private Sequence firstSequence;

  /** Minimum cycle value. */
  private int cycleMin = Integer.MAX_VALUE;

  /** Maximum cycle value. */
  private int cycleMax = Integer.MIN_VALUE;

  /** The date of the acquisition. */
  private String date;

  /** The wait time of the acquisition. */
  private Double waitTime;

  /** Set of active channel indices. */
  private final HashSet<Integer> activeChannels = new HashSet<Integer>();

  /** Key/value pairs from CFG and/or ENV files. */
  private final ValueTable config = new ValueTable();

  /**
   * Creates a new Prairie metadata by parsing the given XML, CFG and/or ENV
   * documents.
   * 
   * @param xml The XML document to parse, or null if none available.
   * @param cfg The CFG document to parse, or null if none available.
   * @param env The ENV document to parse, or null if none available.
   */
  public PrairieMetadata(final Document xml, final Document cfg,
    final Document env)
  {
    if (xml != null) parseXML(xml);
    if (cfg != null) parseCFG(cfg);
    if (env != null) parseENV(env);
    parseChannels();
  }

  // -- PrairieMetadata methods --

  /** Gets the {@code waitTime} recorded in the configuration. */
  public Double getWaitTime() {
    return waitTime;
  }

  /**
   * Gets the list of active channel indices, in sorted order.
   * <p>
   * These indices correspond to the configuration's {@code channel} keys
   * flagged as {@code True}.
   * </p>
   */
  public int[] getActiveChannels() {
    final int[] result = new int[activeChannels.size()];
    int i = 0;
    for (int channelIndex : activeChannels) {
      result[i++] = channelIndex;
    }
    Arrays.sort(result);
    return result;
  }

  /**
   * Gets whether the stage position X coordinates are inverted (i.e.,
   * left-to-right).
   */
  public boolean isInvertX() {
    //return b(getConfig("xYStageXPositionIncreasesLeftToRight"));
    // HACK: It appears that this flag is not actually respected
    // in the tile layout, so we behave as though it is never set!
    return false;
  }

  /**
   * Gets whether the stage position Y coordinates are inverted (i.e.,
   * bottom-to-top).
   */
  public boolean isInvertY() {
    return b(value(getConfig("xYStageYPositionIncreasesBottomToTop")));
  }

  /** Gets the {@code bitDepth} recorded in the configuration. */
  public Integer getBitDepth() {
    return i(value(getConfig("bitDepth")));
  }

  /** Gets the first {@code laserPower} recorded in the configuration. */
  public Double getLaserPower() {
    return d(value(getConfig("laserPower"), 0));
  }

  /** Gets the {@code value} of the given configuration {@code key}. */
  public Value getConfig(final String key) {
    return config.get(key);
  }

  /** Gets the map of configuration key/value pairs. */
  public ValueTable getConfig() {
    return config;
  }

  /** Gets the date of the acquisition. */
  public String getDate() {
    return date;
  }

  /**
   * Gets the minimum cycle value. Matches the smallest {@code cycle} attribute
   * found, and hence will not necessarily equal {@code 1} (though in practice
   * it usually does).
   */
  public int getCycleMin() {
    return cycleMin;
  }

  /**
   * Gets the maximum cycle value. Matches the largest {@code cycle} attribute
   * found, and hence will not necessarily equal {@code sequences#size()}
   * (though in practice it usually does).
   */
  public int getCycleMax() {
    return cycleMax;
  }

  /**
   * Gets the number of recorded cycles. This value is equal to
   * {@link #getCycleMax()} - {@link #getCycleMin()} + 1.
   */
  public int getCycleCount() {
    return cycleMax - cycleMin + 1;
  }

  /** Gets the first {@code Sequence}. */
  public Sequence getFirstSequence() {
    return firstSequence;
  }

  /** Gets the {@code Sequence} at the given {@code cycle}. */
  public Sequence getSequence(final int cycle) {
    return sequences.get(cycle);
  }

  /** Gets all {@code Sequences}, ordered by {@code cycle}. */
  public ArrayList<Sequence> getSequences() {
    return valuesByKey(sequences);
  }

  /** Gets the {@code Frame} at the given ({@code cycle} and {@code index}). */
  public Frame getFrame(final int cycle, final int index) {
    final Sequence sequence = getSequence(cycle);
    if (sequence == null) return null;
    return sequence.getFrame(index);
  }

  /**
   * Gets the {@code Frame} at the given ({@code cycle}, {@code index},
   * {@code channel}).
   */
  public PFile getFile(final int cycle, final int index, final int channel) {
    final Frame frame = getFrame(cycle, index);
    if (frame == null) return null;
    return frame.getFile(channel);
  }

  /**
   * Gets the {@code value} of the given {@code key}, at the top-level
   * {@code <PVScan>} element.
   */
  public Value getValue(final String key) {
    return scanValues.get(key);
  }

  /** Gets the table of {@code PVScan} key/value pairs. */
  public ValueTable getValues() {
    return scanValues;
  }

  // -- Helper methods --

  /** Parses metadata from Prairie XML file. */
  private void parseXML(final Document doc) {
    final Element pvScan = doc.getDocumentElement();
    checkElement(pvScan, "PVScan");

    // parse <PVStateShard> key/value block
    parsePVStateShard(pvScan, scanValues);

    // parse acquisition date
    date = attr(pvScan, "date");

    // iterate over all Sequence elements
    final NodeList sequenceNodes = doc.getElementsByTagName("Sequence");
    for (int s = 0; s < sequenceNodes.getLength(); s++) {
      final Element sequenceElement = el(sequenceNodes, s);
      if (sequenceElement == null) continue;

      final Sequence sequence = new Sequence(sequenceElement);
      if (firstSequence == null) firstSequence = sequence;

      final int cycle = sequence.getCycle();
      if (cycle < cycleMin) cycleMin = cycle;
      if (cycle > cycleMax) cycleMax = cycle;

      sequences.put(cycle, sequence);
    }
  }

  /**
   * Parses metadata from Prairie CFG file. This file is only present for
   * Prairie datasets recorded prior to version 5.2.
   */
  private void parseCFG(final Document doc) {
    checkElement(doc.getDocumentElement(), "PVConfig");

    final NodeList waitNodes = doc.getElementsByTagName("PVTSeriesElementWait");
    if (waitNodes.getLength() > 0) {
      final Element waitElement = el(waitNodes, 0);
      waitTime = d(attr(waitElement, "waitTime"));
    }

    parseKeys(doc.getDocumentElement(), config);
  }

  /**
   * Parses metadata from Prairie ENV file. This file is only present for
   * Prairie datasets recorded with version 5.2 or later.
   */
  private void parseENV(final Document doc) {
    checkElement(doc.getDocumentElement(), "Environment");

    parsePVStateShard(doc.getDocumentElement(), config);
  }

  /**
   * Parses {@code <Key>} elements beneath the given element, into the specified
   * table. These {@code <Key>} elements are only present in data from
   * PrairieView versions prior to 5.2.
   */
  private void parseKeys(final Element el, final ValueTable table) {
    final NodeList keyNodes = el.getElementsByTagName("Key");
    for (int k = 0; k < keyNodes.getLength(); k++) {
      final Element keyElement = el(keyNodes, k);
      if (keyElement == null) continue;
      final String key = attr(keyElement, "key");
      final String value = attr(keyElement, "value");
      final int underscore = key.indexOf("_");
      if (underscore < 0) {
        // single key/value pair
        table.put(key, new ValueItem(value, null));
      }
      else {
        // table of key/value pairs
        final String prefix = key.substring(0, underscore);
        final String index = key.substring(underscore + 1);
        if (!table.containsKey(prefix)) {
          table.put(prefix, new ValueTable());
        }
        final ValueTable subTable = (ValueTable) table.get(prefix);
        final String[] tokens = value.split(",");
        if (tokens.length == 1) {
          // single value
          subTable.put(index, new ValueItem(value, null));
        }
        else {
          // sub-table of values
          final ValueTable subSubTable = new ValueTable();
          for (int i=0; i<tokens.length; i++) {
            subSubTable.put("" + i, new ValueItem(tokens[i], null));
          }
        }
      }
    }
  }

  /**
   * Parses the {@code <PVStateShard>} element beneath the given element, into
   * the specified table. These {@code <PVStateShard>} elements are only present
   * in data from PrairieView versions 5.2 and later.
   */
  private void parsePVStateShard(final Element el, final ValueTable table) {
    final Element pvStateShard = getFirstChild(el, "PVStateShard");
    if (pvStateShard == null) return;

    final NodeList svNodes = el.getElementsByTagName("PVStateValue");
    for (int k = 0; k < svNodes.getLength(); k++) {
      final Element keyElement = el(svNodes, k);
      if (keyElement == null) continue;
      final String key = attr(keyElement, "key");
      final String value = attr(keyElement, "value");
      if (value != null) {
        // E.g.: <PVStateValue key="linesPerFrame" value="186" />
        table.put(key, new ValueItem(value, attr(keyElement, "description")));
        continue;
      }

      // value is itself a table of values
      final ValueTable subTable = new ValueTable();
      table.put(key, subTable);

      // process <IndexedValue> elements; e.g.:
      // <IndexedValue index="0" value="605" description="Ch1 High Voltage" />
      final NodeList ivNodes = keyElement.getElementsByTagName("IndexedValue");
      for (int i = 0; i < ivNodes.getLength(); i++) {
        final Element ivElement = el(ivNodes, i);
        if (ivElement == null) continue;
        final String index = attr(ivElement, "index");
        if (index == null) continue; // invalid <IndexedValue> element
        final String iValue = attr(ivElement, "value");
        final String iDescription = attr(ivElement, "description");
        subTable.put(index, new ValueItem(iValue, iDescription));
      }

      // process <SubindexedValue> elements; e.g.:
      // <SubindexedValues index="ZAxis">
      //   <SubindexedValue subindex="0" value="-9" description="Focus" />
      //   <SubindexedValue subindex="1" value="62.45" description="Piezo" />
      // </SubindexedValues>
      final NodeList sivNodes =
        keyElement.getElementsByTagName("SubindexedValues");
      for (int i = 0; i < sivNodes.getLength(); i++) {
        final Element sivElement = el(sivNodes, i);
        if (sivElement == null) continue;
        final String index = attr(sivElement, "index");
        if (index == null) continue; // invalid <SubindexedValues> element
        final ValueTable subSubTable = new ValueTable();
        subTable.put(index, subSubTable);
        // iterate over <SubindexValue> children
        final NodeList subNodes =
          sivElement.getElementsByTagName("SubindexValue");
        for (int s = 0; s < subNodes.getLength(); s++) {
          final Element subElement = el(subNodes, i);
          final String subindex = attr(subElement, "subindex");
          if (subindex == null) continue; // invalid <SubindexedValue> element
          final String sValue = attr(subElement, "value");
          final String sDescription = attr(subElement, "description");
          subSubTable.put(index, new ValueItem(sValue, sDescription));
        }
      }
    }
  }

  /**
   * Parses details of the activated channels into the {@link #activeChannels}
   * data structure.
   */
  private void parseChannels() {
    final Value channels = config.get("channel");
    if (!(channels instanceof ValueTable)) return;
    final ValueTable channelsTable = (ValueTable) channels;
    for (final String key : channelsTable.keySet()) {
      final Value value = channelsTable.get(key);

      // verify that the channel is active
      if (!b(value(value))) continue; // channel not active

      // parse the channel index (converting to a 1-based index!)
      final int channelIndex = i(key) + 1;

      // add the channel index to the active channels list
      activeChannels.add(channelIndex);
    }
  }

  /**
   * Checks that the given element has the specified name.
   * 
   * @throws IllegalArgumentException if the name does not match.
   */
  private void checkElement(final Element el, final String name) {
    if (!el.getNodeName().equals(name)) {
      throw new IllegalArgumentException("Not a " + name + " element");
    }
  }

  /** Gets the first child element with the given name. */
  private Element getFirstChild(final Element el, final String name) {
    // NB: Unfortunately, the Element interface has no API method to obtain
    // _only_ direct children with a given name; the getElementsByTagName
    // method returns _all_ descendant elements with the given name.
    final NodeList nodeList = el.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      final Element child = el(nodeList, i);
      if (child == null) continue;
      if (name.equals(child.getNodeName())) return child;
    }
    return null;
  }

  /** Gets the {@code index}th element from the given list of nodes. */
  private Element el(final NodeList nodes, final int index) {
    final Node node = nodes.item(index);
    if (!(node instanceof Element)) return null;
    return (Element) node;
  }

  /** Gets the attribute value with the given name, or null if not defined. */
  private String attr(final Element el, final String name) {
    return el.hasAttribute(name) ? el.getAttribute(name) : null;
  }

  /** Returns {@code value.value()}, or null if {@code value} is null. */
  private String value(final Value value) {
    return value == null ? null : value.value();
  }

  /**
   * Returns {@code value.get(key).value()}, or null if {@code value} or
   * {@code value.get(key)} is null.
   */
  private String value(final Value value, final String key) {
    if (value == null) return null;
    final Value v = value.get(key);
    return v == null ? null : v.value();
  }

  /**
   * Returns {@code value.get(index).value()}, or null if {@code value} or
   * {@code value.get(index)} is null.
   */
  private String value(final Value value, final int index) {
    if (value == null) return null;
    final Value v = value.get(index);
    return v == null ? null : v.value();
  }

  /** Converts the given string to a {@code boolean}. */
  private boolean b(final String value) {
    return Boolean.parseBoolean(value);
  }

  /**
   * Converts the given string to a {@link Double}, or {@code null} if
   * incompatible.
   */
  private Double d(final String value) {
    if (value == null) return null;
    try {
      return new Double(value);
    }
    catch (final NumberFormatException exc) {
      // TODO: log it
      return null;
    }
  }

  /**
   * Converts the given string to an {@link Integer}, or null if incompatible.
   */
  private Integer i(final String value) {
    if (value == null) return null;
    try {
      return new Integer(value);
    }
    catch (final NumberFormatException exc) {
      // TODO: log it
      return null;
    }
  }

  /**
   * Gets the {@code i}th token of the given string, split according to the
   * specific regular expression.
   */
  private String token(final String s, final String regex, final int i) {
    if (s == null) return null;
    final String[] tokens = s.split(regex);
    return tokens.length > i ? tokens[i] : null;
  }

  /** Gets the values of the given map, sorted by key. */
  private <K extends Comparable<? super K>, V> ArrayList<V> valuesByKey(
    final Map<K, V> map)
  {
    final ArrayList<K> keys = new ArrayList<K>(map.size());
    final ArrayList<V> values = new ArrayList<V>(map.size());
    keys.addAll(map.keySet());
    Collections.sort(keys);
    for (final K key : keys) {
      values.add(map.get(key));
    }
    return values;
  }

  // -- Helper classes --

  /** A Prairie {@code <Sequence>}. */
  public class Sequence {

    /**
     * {@code <Frame>} elements beneath this {@code <Sequence>}, keyed on each
     * frame's {@code index}.
     */
    private final HashMap<Integer, Frame> frames =
      new HashMap<Integer, Frame>();

    /** Table of key/value pairs for this {@code <Sequence>}. */
    private final ValueTable sequenceValues = new ValueTable();

    /** The first actual {@code <Frame>} element for this {@code <Sequence>}. */
    private Frame firstFrame;

    /** Minimum index value. */
    private int indexMin = Integer.MAX_VALUE;

    /** Maximum index value. */
    private int indexMax = Integer.MIN_VALUE;

    /** {@code type} attribute of this {@code <Sequence>}. */
    private String type;

    /** {@code cycle} of this {@code <Sequence>}. */
    private Integer cycle;

    /**
     * Creates a new sequence by parsing the given {@code <Sequence>} element.
     */
    public Sequence(final Element sequenceElement) {
      parse(sequenceElement);
    }

    /** Parses metadata from the given {@code Sequence} element. */
    public void parse(final Element sequenceElement) {
      checkElement(sequenceElement, "Sequence");

      // parse <PVStateShard> key/value block
      parsePVStateShard(sequenceElement, sequenceValues);

      type = attr(sequenceElement, "type");
      cycle = i(attr(sequenceElement, "cycle"));
      if (cycle == null) {
        throw new IllegalArgumentException("Sequence missing cycle attribute");
      }

      // iterate over all Frame elements
      final NodeList frameNodes = sequenceElement.getElementsByTagName("Frame");
      for (int f = 0; f < frameNodes.getLength(); f++) {
        final Element frameElement = el(frameNodes, f);
        if (frameElement == null) continue;

        final Frame frame = new Frame(this, frameElement);
        if (firstFrame == null) firstFrame = frame;

        final int index = frame.getIndex();
        if (index < indexMin) indexMin = index;
        if (index > indexMax) indexMax = index;

        frames.put(index, frame);
      }
    }

    /** Gets the {@code type} associated with this {@code Sequence}. */
    public String getType() {
      return type;
    }

    /**
     * Gets whether this {@code Sequence} should be considered a time series.
     */
    public boolean isTimeSeries() {
      return "TSeries Timed Element".equals(type);
    }

    /** Gets the {@code cycle} associated with this {@code Sequence}. */
    public int getCycle() {
      return cycle;
    }

    /**
     * Gets the minimum index value. Matches the smallest {@code index}
     * attribute found, and hence will not necessarily equal {@code 1} (though
     * in practice it usually does).
     */
    public int getIndexMin() {
      return indexMin;
    }

    /**
     * Gets the maximum index value. Matches the largest {@code index} attribute
     * found, and hence will not necessarily equal {@code frames#size()} (though
     * in practice it usually does).
     */
    public int getIndexMax() {
      return indexMax;
    }

    /**
     * Gets the number of recorded indices at this {@code Sequence}. This value
     * is equal to {@link #getIndexMax()} - {@link #getIndexMin()} + 1.
     */
    public int getIndexCount() {
      return indexMax - indexMin + 1;
    }

    /** Gets the first {@code Frame} of the {@code Sequence}. */
    public Frame getFirstFrame() {
      return firstFrame;
    }

    /** Gets the {@code Frame} with the given {@code index}. */
    public Frame getFrame(final int index) {
      return frames.get(index);
    }

    /**
     * Gets the {@code Frame} at the given ({@code cycle}, {@code index},
     * {@code channel}).
     */
    public PFile getFile(final int index, final int channel) {
      final Frame frame = getFrame(index);
      if (frame == null) return null;
      return frame.getFile(channel);
    }

    /**
     * Gets the {@code value} of the given {@code key}, beneath this
     * {@code Sequence}, inferring the value from the parent {@code <PVScan>}
     * section as needed.
     */
    public Value getValue(final String key) {
      if (sequenceValues.containsKey(key)) return sequenceValues.get(key);
      return PrairieMetadata.this.getValue(key);
    }

    /** Gets the table of {@code Frame} key/value pairs. */
    public ValueTable getValues() {
      return sequenceValues;
    }

  }

  /** A Prairie {@code <Frame>}, beneath a {@code <Sequence>}. */
  public class Frame {

    /** The {@code <Sequence>} containing this {@code <Frame>}. */
    private Sequence sequence;

    /**
     * {@code <File>} elements beneath this {@code <Frame>}, keyed on each
     * file's {@code channel}.
     */
    private final HashMap<Integer, PFile> files = new HashMap<Integer, PFile>();

    /** Table of key/value pairs for this {@code <Frame>}. */
    private final ValueTable frameValues = new ValueTable();

    /** The first actual {@code <File>} element for this {@code <Frame>}. */
    private PFile firstFile;

    /** {@code relativeTime} attribute of this {@code <Frame>}. */
    private Double relativeTime;

    /** {@code absoluteTime} attribute of this {@code <Frame>}. */
    private Double absoluteTime;

    /** {@code index} of this {@code <Frame>}. */
    private Integer index;

    /** Creates a new frame by parsing the given {@code <Frame>} element. */
    public Frame(final Sequence sequence, final Element frameElement) {
      this.sequence = sequence;
      parse(frameElement);
    }

    // -- Frame methods --

    /** Gets the {@code <Sequence>} containing this {@code <Frame>}. */
    public Sequence getSequence() {
      return sequence;
    }

    /** Parses metadata from the given {@code Frame} element. */
    public void parse(final Element frameElement) {
      checkElement(frameElement, "Frame");

      // parse <PVStateShard> key/value block
      parsePVStateShard(frameElement, frameValues);

      relativeTime = d(attr(frameElement, "relativeTime"));
      absoluteTime = d(attr(frameElement, "absoluteTime"));
      index = i(attr(frameElement, "index"));
      if (index == null) {
        throw new IllegalArgumentException("Frame missing index attribute");
      }

      // iterate over all File elements
      final NodeList fileNodes = frameElement.getElementsByTagName("File");
      for (int f = 0; f < fileNodes.getLength(); f++) {
        final Element fileElement = el(fileNodes, f);
        if (fileElement == null) continue;

        final PFile file = new PFile(this, fileElement);
        if (firstFile == null) firstFile = file;

        final int channel = file.getChannel();
        files.put(channel, file);
      }

      parseKeys(frameElement, frameValues);
    }

    /** Gets the {@code relativeTime} associated with this {@code Frame}. */
    public double getRelativeTime() {
      return relativeTime;
    }

    /** Gets the {@code absoluteTime} associated with this {@code Frame}. */
    public double getAbsoluteTime() {
      return absoluteTime;
    }

    /** Gets the {@code index} associated with this {@code Frame}. */
    public int getIndex() {
      return index;
    }

    /** Gets the first {@code File} of the {@code Sequence}. */
    public PFile getFirstFile() {
      return firstFile;
    }

    /** Gets the {@code File} with the given {@code channel}. */
    public PFile getFile(final int channel) {
      return files.get(channel);
    }

    /** Gets the objective lens string for this {@code Frame}. */
    public String getObjectiveLens() {
      return value(getValue("objectiveLens"));
    }

    /** Extracts the objective manufacturer from the objective lens string. */
    public String getObjectiveManufacturer() {
      return token(getObjectiveLens(), " ", 0);
    }

    /** Extracts the magnification from the objective lens string. */
    public Double getMagnification() {
      return d(token(getObjectiveLens(), " ", 1));
    }

    /** Extracts the immersion from the objective lens string. */
    public String getImmersion() {
      return token(getObjectiveLens(), " ", 2);
    }

    /** Gets the numerical aperture of the lens for this {@code Frame}. */
    public Double getObjectiveLensNA() {
      return d(value(getValue("objectiveLensNA")));
    }

    /** Gets the pixels per line for this {@code Frame}. */
    public Integer getPixelsPerLine() {
      return i(value(getValue("pixelsPerLine")));
    }

    /** Gets the lines per frame for this {@code Frame}. */
    public Integer getLinesPerFrame() {
      return i(value(getValue("linesPerFrame")));
    }

    /** Gets the X stage position associated with this {@code Frame}. */
    public Double getPositionX() {
      final Double posX = d(value(getValue("positionCurrent"), "XAxis"));
      return posX == null ? null : isInvertX() ? -posX : posX;
    }

    /** Gets the Y stage position associated with this {@code Frame}. */
    public Double getPositionY() {
      final Double posY = d(value(getValue("positionCurrent"), "YAxis"));
      return posY == null ? null : isInvertY() ? -posY : posY;
    }

    /** Gets the Z stage position associated with this {@code Frame}. */
    public Double getPositionZ() {
      return d(value(getValue("positionCurrent"), "ZAxis"));
    }

    /** Gets the optical zoom associated with this {@code Frame}. */
    public Double getOpticalZoom() {
      return d(value(getValue("opticalZoom")));
    }

    /** Gets the microns per pixel along X for this {@code Frame}. */
    public Double getMicronsPerPixelX() {
      return d(value(getValue("micronsPerPixel"), "XAxis"));
    }

    /** Gets the microns per pixel along Y for this {@code Frame}. */
    public Double getMicronsPerPixelY() {
      return d(value(getValue("micronsPerPixel"), "YAxis"));
    }

    /**
     * Gets the {@code c}th offset for this {@code Frame}.
     * 
     * @param c The 0-based(!) channel index for which to obtain the offset.
     */
    public Double getOffset(final int c) {
      return d(value(getValue("pmtOffset"), c));
    }

    /**
     * Gets the {@code c}th gain for this {@code Frame}.
     * 
     * @param c The 0-based(!) channel index for which to obtain the gain.
     */
    public Double getGain(final int c) {
      return d(value(getValue("pmtGain"), c));
    }

    /** Gets the imaging device associated with this {@code Frame}. */
    public String getImagingDevice() {
      return value(getValue("imagingDevice"));
    }

    /**
     * Gets the {@code value} of the given {@code key}, beneath this
     * {@code Frame}, inferring the value from the parent {@code <Sequence>}
     * or grandparent {@code <PVScan>} section as needed.
     */
    public Value getValue(final String key) {
      if (frameValues.containsKey(key)) return frameValues.get(key);
      return getSequence().getValue(key);
    }

    /** Gets the table of {@code Frame} key/value pairs. */
    public ValueTable getValues() {
      return frameValues;
    }

  }

  /**
   * A Prairie {@code <File>} beneath a {@code <Frame>}. It is called
   * {@code PFile} rather than {@code File} to avoid confusion with the
   * {@link java.io.File} class.
   */
  public class PFile {

    /** The {@code <Frame>} containing this {@code <File>}. */
    private Frame frame;

    /** {@code channel} of this {@code <File>}. */
    private Integer channel;

    /** {@code channelName} attribute of this {@code <File>}. */
    private String channelName;

    /** {@code filename} attribute of this {@code <File>}. */
    private String filename;

    /** Creates a new file by parsing the given {@code <File>} element. */
    public PFile(final Frame frame, final Element fileElement) {
      this.frame = frame;
      parse(fileElement);
    }

    // -- PFile methods --

    /** Gets the {@code <Frame>} containing this {@code <File>}. */
    public Frame getFrame() {
      return frame;
    }

    /** Parses metadata from the given {@code File} element. */
    public void parse(final Element fileElement) {
      checkElement(fileElement, "File");

      channel = i(attr(fileElement, "channel"));
      if (channel == null) {
        throw new IllegalArgumentException("File missing channel attribute");
      }

      channelName = attr(fileElement, "channelName");
      filename = attr(fileElement, "filename");
    }

    /** Gets the {@code channel} associated with this {@code File}. */
    public int getChannel() {
      return channel;
    }

    /** Gets the {@code channelName} associated with this {@code File}. */
    public String getChannelName() {
      return channelName;
    }

    /** Gets the {@code filename} associated with this {@code File}. */
    public String getFilename() {
      return filename;
    }

  }

  /**
   * A value in a Prairie metadata dictionary.
   * <p>
   * Prior to PrairieView 5.2, these were expressed as {@code <Key>} elements:
   * </p>
   * 
   * <pre>
   * <Key key="linesPerFrame" permissions="Read, Write, Save" value="186" />
   * <Key key="pmtGain_0" permissions="Write, Save" value="605" />
   * <Key key="pmtGain_1" permissions="Write, Save" value="604" />
   * <Key key="pmtGain_2" permissions="Write, Save" value="0" />
   * <Key key="positionCurrent_XAxis" permissions="Write, Save" value="0.95" />
   * <Key key="positionCurrent_YAxis" permissions="Write, Save" value="-4.45" />
   * <Key key="positionCurrent_ZAxis" permissions="Write, Save" value="-9,62.45" />
   * </pre>
   * <p>
   * From 5.2 onwards, they are @{code <PVStateValue>} elements:
   * </p>
   * 
   * <pre>
   * <PVStateValue key="linesPerFrame" value="186" />
   * <PVStateValue key="pmtGain">
   *   <IndexedValue index="0" value="605" description="Ch1 High Voltage" />
   *   <IndexedValue index="1" value="604" description="Ch2 High Voltage" />
   *   <IndexedValue index="2" value="0" description="Ch3 High Voltage" />
   * </PVStateValue>
   * <PVStateValue key="positionCurrent">
   *   <SubindexedValues index="XAxis">
   *     <SubindexedValue subindex="0" value="0.95" />
   *   </SubindexedValues>
   *   <SubindexedValues index="YAxis">
   *     <SubindexedValue subindex="0" value="-4.45" />
   *   </SubindexedValues>
   *   <SubindexedValues index="ZAxis">
   *     <SubindexedValue subindex="0" value="-9" description="Focus" />
   *     <SubindexedValue subindex="1" value="62.45" description="Piezo" />
   *   </SubindexedValues>
   * </PVStateValue>
   * </pre>
   */
  public static interface Value {
    boolean isTable();
    Value get(Object key);
    Value get(int index);
    String value();
    String description();
  }

  /**
   * A leaf value with an actual {@link #value()} as well as an optional
   * {@link #description()}.
   */
  public static class ValueItem implements Value {
    private String value;
    private String description;

    public ValueItem(final String value, final String description) {
      this.value = value;
      this.description = description;
    }

    @Override
    public boolean isTable() {
      return false;
    }

    @Override
    public Value get(final Object key) {
      return null;
    }

    @Override
    public Value get(final int index) {
      return null;
    }

    @Override
    public String value() {
      return value;
    }

    @Override
    public String description() {
      return description;
    }

    @Override
    public String toString() {
      return value();
    }
  }

  /**
   * A table of values. Each value may be either a leaf item ({@link ValueItem})
   * or a sub-table ({@link ValueTable}).
   */
  public static class ValueTable extends HashMap<String, Value> implements
    Value
  {
    @Override
    public boolean isTable() {
      return true;
    }

    @Override
    public Value get(int index) {
      return get("" + index);
    }

    @Override
    public String value() {
      // NB: For tables with exactly one entry, we return the value
      // of the entry directly, when the table's value is requested.
      // This works around an ambiguity within the pre-5.2 schema,
      // which made it impossible to distinguish between two cases.
      // Consider the following pre-5.2 XML fragment:
      //
      // <Key key="positionCurrent_XAxis" value="0.95" />
      //
      // In terms of the 5.2+ schema, there are two potential ways
      // to interpret this information:
      //
      // <PVStateValue key="positionCurrent">
      //   <SubindexedValues index="XAxis">
      //     <SubindexedValue subindex="0" value="0.95" />
      //   </SubindexedValues>
      // </PVStateValue>
      //
      // And:
      //
      // <PVStateValue key="positionCurrent">
      //   <IndexedValue index="XAxis" value="0.95" />
      // </PVStateValue>
      //
      // In order to maintain consistency when consuming such fields,
      // we allow "short circuiting" single table indices. So in the
      // above case, the following statements are equivalent:
      //
      // table.get("positionCurrent").get("XAxis").get(0).value();
      // table.get("positionCurrent").get("XAxis").value();
      // table.get("positionCurrent").value();
      return size() == 1 ? values().iterator().next().value() : null;
    }

    @Override
    public String description() {
      return null;
    }
  }

}
