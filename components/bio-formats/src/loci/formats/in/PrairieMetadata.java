/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PrairieMetadata.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PrairieMetadata.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PrairieMetadata {

  /** {@code <Sequence>} elements, keyed on each sequence's {@code cycle}. */
  private final HashMap<Integer, Sequence> sequences =
    new HashMap<Integer, Sequence>();

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

  /** Key/value pairs from CFG file. */
  private final HashMap<String, String> config = new HashMap<String, String>();

  /**
   * Creates a new Prairie metadata by parsing the given XML and CFG documents.
   * 
   * @param xml The XML document to parse, or null if none available.
   * @param cfg The CFG document to parse, or null if none available.
   */
  public PrairieMetadata(final Document xml, final Document cfg) {
    if (xml != null) parseXML(xml);
    if (cfg != null) parseCFG(cfg);
  }

  // -- PrairieMetadata methods --

  /** Gets the {@code waitTime} recorded in the configuration. */
  public Double getWaitTime() {
    return waitTime;
  }

  /**
   * Gets the list of active channel indices, in sorted order.
   * <p>
   * These indices correspond to the configuration's {@code channel_*} keys
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
    return b(getConfig("xYStageXPositionIncreasesLeftToRight"));
  }

  /**
   * Gets whether the stage position Y coordinates are inverted (i.e.,
   * bottom-to-top).
   */
  public boolean isInvertY() {
    return b(getConfig("xYStageYPositionIncreasesBottomToTop"));
  }

  /** Gets the {@code bitDepth} recorded in the configuration. */
  public Integer getBitDepth() {
    return i(getConfig("bitDepth"));
  }

  /** Gets the {@code laserPower_0} recorded in the configuration. */
  public Double getLaserPower() {
    return d(getConfig("laserPower_0"));
  }

  /** Gets the {@code value} of the given configuration {@code Key}. */
  public String getConfig(final String key) {
    return config.get(key);
  }

  /** Gets a read-only map of configuration key/value pairs. */
  public Map<String, String> getConfig() {
    return Collections.unmodifiableMap(config);
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

  // -- Helper methods --

  /** Parses metadata from Prairie XML file. */
  private void parseXML(final Document doc) {
    final Element pvScan = doc.getDocumentElement();
    checkElement(pvScan, "PVScan");

    // parse acquisition date
    date = pvScan.getAttribute("date");

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

  /** Parses metadata from Prairie CFG file. */
  private void parseCFG(final Document doc) {
    checkElement(doc.getDocumentElement(), "PVConfig");

    final NodeList waitNodes = doc.getElementsByTagName("PVTSeriesElementWait");
    if (waitNodes.getLength() > 0) {
      final Element waitElement = el(waitNodes, 0);
      waitTime = d(waitElement.getAttribute("waitTime"));
    }

    parseKeys(doc.getDocumentElement(), config);
    parseChannels();
  }

  /**
   * Parses {@code <Key>} elements beneath the given element, into the specified
   * map.
   */
  private void parseKeys(final Element el, final HashMap<String, String> map) {
    final NodeList keyNodes = el.getElementsByTagName("Key");
    for (int k = 0; k < keyNodes.getLength(); k++) {
      final Element keyElement = el(keyNodes, k);
      if (keyElement == null) continue;
      final String key = keyElement.getAttribute("key");
      final String value = keyElement.getAttribute("value");
      map.put(key, value);
    }
  }

  /**
   * Parses details of the activated channels into the {@link #activeChannels}
   * data structure.
   */
  private void parseChannels() {
    for (final String key : config.keySet()) {
      if (!key.matches("channel_[0-9]+")) {
        // key does not denote a channel activation
        continue;
      }

      // verify that the channel is active
      final String value = config.get(key);
      if (!b(value)) continue; // channel not active

      // parse the channel index (converting to a 1-based index!)
      final int channelIndex = i(key.substring(8)) + 1;

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

  /** Gets the {@code index}th element from the given list of nodes. */
  private Element el(final NodeList nodes, final int index) {
    final Node node = nodes.item(index);
    if (!(node instanceof Element)) return null;
    return (Element) node;
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

      type = sequenceElement.getAttribute("type");
      cycle = i(sequenceElement.getAttribute("cycle"));
      if (cycle == null) {
        throw new IllegalArgumentException("Sequence missing cycle attribute");
      }

      // iterate over all Frame elements
      final NodeList frameNodes = sequenceElement.getElementsByTagName("Frame");
      for (int f = 0; f < frameNodes.getLength(); f++) {
        final Element frameElement = el(frameNodes, f);
        if (frameElement == null) continue;

        final Frame frame = new Frame(frameElement);
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

  }

  /** A Prairie {@code <Frame>}, beneath a {@code <Sequence>}. */
  public class Frame {

    /**
     * {@code <File>} elements beneath this {@code <Frame>}, keyed on each
     * file's {@code channel}.
     */
    private final HashMap<Integer, PFile> files = new HashMap<Integer, PFile>();

    /** Table of key/value pairs for this {@code <Frame>}. */
    private final HashMap<String, String> values =
      new HashMap<String, String>();

    /** The first actual {@code <File>} element for this {@code <Frame>}. */
    private PFile firstFile;

    /** Minimum channel value. */
    private int channelMin = Integer.MAX_VALUE;

    /** Maximum channel value. */
    private int channelMax = Integer.MIN_VALUE;

    /** {@code relativeTime} attribute of this {@code <Frame>}. */
    private Double relativeTime;

    /** {@code absoluteTime} attribute of this {@code <Frame>}. */
    private Double absoluteTime;

    /** {@code index} of this {@code <Frame>}. */
    private Integer index;

    /** Creates a new frame by parsing the given {@code <Frame>} element. */
    public Frame(final Element frameElement) {
      parse(frameElement);
    }

    // -- Frame methods --

    /** Parses metadata from the given {@code Frame} element. */
    public void parse(final Element frameElement) {
      checkElement(frameElement, "Frame");

      relativeTime = d(frameElement.getAttribute("relativeTime"));
      absoluteTime = d(frameElement.getAttribute("absoluteTime"));
      index = i(frameElement.getAttribute("index"));
      if (index == null) {
        throw new IllegalArgumentException("Frame missing index attribute");
      }

      // iterate over all File elements
      final NodeList fileNodes = frameElement.getElementsByTagName("File");
      for (int f = 0; f < fileNodes.getLength(); f++) {
        final Element fileElement = el(fileNodes, f);
        if (fileElement == null) continue;

        final PFile file = new PFile(fileElement);
        if (firstFile == null) firstFile = file;

        final int channel = file.getChannel();
        if (channel < channelMin) channelMin = channel;
        if (channel > channelMax) channelMax = channel;

        files.put(channel, file);
      }

      parseKeys(frameElement, values);
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

    /**
     * Gets the minimum channel value. Matches the smallest {@code channel}
     * attribute found, and hence will not necessarily equal {@code 1} (though
     * in practice it usually does).
     */
    public int getChannelMin() {
      return channelMin;
    }

    /**
     * Gets the maximum channel value. Matches the largest {@code channel}
     * attribute found, and hence will not necessarily equal
     * {@code channels#size()} (though in practice it usually does).
     */
    public int getChannelMax() {
      return channelMax;
    }

    /**
     * Gets the number of recorded channels at this {@code Frame}. This value is
     * equal to {@link #getChannelMax()} - {@link #getChannelMin()} + 1.
     */
    public int getChannelCount() {
      return channelMax - channelMin + 1;
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
      return getValue("objectiveLens");
    }

    /** Extracts the objective manufacturer from the objective lens string. */
    public String getObjectiveManufacturer() {
      return token(getObjectiveLens(), " ", 0);
    }

    /** Extracts the magnification from the objective lens string. */
    public Integer getMagnification() {
      return i(token(getObjectiveLens(), " ", 1));
    }

    /** Extracts the immersion from the objective lens string. */
    public String getImmersion() {
      return token(getObjectiveLens(), " ", 2);
    }

    /** Gets the numerical aperture of the lens for this {@code Frame}. */
    public Double getObjectiveLensNA() {
      return d(getValue("objectiveLensNA"));
    }

    /** Gets the pixels per line for this {@code Frame}. */
    public Integer getPixelsPerLine() {
      return i(getValue("pixelsPerLine"));
    }

    /** Gets the lines per frame for this {@code Frame}. */
    public Integer getLinesPerFrame() {
      return i(getValue("linesPerFrame"));
    }

    /** Gets the X stage position associated with this {@code Frame}. */
    public Double getPositionX() {
      final Double posX = d(getValue("positionCurrent_XAxis"));
      return posX == null ? null : isInvertX() ? -posX : posX;
    }

    /** Gets the Y stage position associated with this {@code Frame}. */
    public Double getPositionY() {
      final Double posY = d(getValue("positionCurrent_YAxis"));
      return posY == null ? null : isInvertY() ? -posY : posY;
    }

    /** Gets the Z stage position associated with this {@code Frame}. */
    public Double getPositionZ() {
      return d(getValue("positionCurrent_ZAxis"));
    }

    /** Gets the optical zoom associatetd with this {@code Frame}. */
    public Double getOpticalZoom() {
      return d(getValue("opticalZoom"));
    }

    /** Gets the microns per pixel along X for this {@code Frame}. */
    public Double getMicronsPerPixelX() {
      return d(getValue("micronsPerPixel_XAxis"));
    }

    /** Gets the microns per pixel along Y for this {@code Frame}. */
    public Double getMicronsPerPixelY() {
      return d(getValue("micronsPerPixel_YAxis"));
    }

    /**
     * Gets the {@code c}th offset for this {@code Frame}.
     * 
     * @param c The 0-based(!) channel index for which to obtain the offset.
     */
    public Double getOffset(final int c) {
      return d(getValue("pmtOffset_" + c));
    }

    /**
     * Gets the {@code c}th gain for this {@code Frame}.
     * 
     * @param c The 0-based(!) channel index for which to obtain the gain.
     */
    public Double getGain(final int c) {
      return d(getValue("pmtGain_" + c));
    }

    /** Gets the imaging device associated with this {@code Frame}. */
    public String getImagingDevice() {
      return getValue("imagingDevice");
    }

    /**
     * Gets the {@code value} of the given {@code Key}, beneath this
     * {@code Frame}.
     */
    public String getValue(final String key) {
      return values.get(key);
    }

    /** Gets a read-only map of {@code Frame} key/value pairs. */
    public Map<String, String> getValues() {
      return Collections.unmodifiableMap(values);
    }

  }

  /**
   * A Prairie {@code <File>} beneath a {@code <Frame>}. It is called
   * {@code PFile} rather than {@code File} to avoid confusion with the
   * {@link java.io.File} class.
   */
  public class PFile {

    /** {@code channel} of this {@code <File>}. */
    private Integer channel;

    /** {@code channelName} attribute of this {@code <File>}. */
    private String channelName;

    /** {@code filename} attribute of this {@code <File>}. */
    private String filename;

    /** Creates a new file by parsing the given {@code <File>} element. */
    public PFile(final Element fileElement) {
      parse(fileElement);
    }

    // -- PFile methods --

    /** Parses metadata from the given {@code File} element. */
    public void parse(final Element fileElement) {
      checkElement(fileElement, "File");

      channel = i(fileElement.getAttribute("channel"));
      if (channel == null) {
        throw new IllegalArgumentException("File missing channel attribute");
      }

      channelName = fileElement.getAttribute("channelName");
      filename = fileElement.getAttribute("filename");
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

}
