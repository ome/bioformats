/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Arrays;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.DummyMetadata;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.AffineTransform;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.Compression;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.ExperimentType;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.FillRule;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.FontFamily;
import ome.xml.model.enums.FontStyle;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Marker;
import ome.xml.model.enums.Medium;
import ome.xml.model.enums.MicrobeamManipulationType;
import ome.xml.model.enums.MicroscopeType;
import ome.xml.model.enums.NamingConvention;
import ome.xml.model.enums.PixelType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.enums.handlers.AcquisitionModeEnumHandler;
import ome.xml.model.enums.handlers.ArcTypeEnumHandler;
import ome.xml.model.enums.handlers.BinningEnumHandler;
import ome.xml.model.enums.handlers.CompressionEnumHandler;
import ome.xml.model.enums.handlers.ContrastMethodEnumHandler;
import ome.xml.model.enums.handlers.CorrectionEnumHandler;
import ome.xml.model.enums.handlers.DetectorTypeEnumHandler;
import ome.xml.model.enums.handlers.DimensionOrderEnumHandler;
import ome.xml.model.enums.handlers.ExperimentTypeEnumHandler;
import ome.xml.model.enums.handlers.FilamentTypeEnumHandler;
import ome.xml.model.enums.handlers.FillRuleEnumHandler;
import ome.xml.model.enums.handlers.FilterTypeEnumHandler;
import ome.xml.model.enums.handlers.FontFamilyEnumHandler;
import ome.xml.model.enums.handlers.FontStyleEnumHandler;
import ome.xml.model.enums.handlers.IlluminationTypeEnumHandler;
import ome.xml.model.enums.handlers.ImmersionEnumHandler;
import ome.xml.model.enums.handlers.LaserMediumEnumHandler;
import ome.xml.model.enums.handlers.LaserTypeEnumHandler;
import ome.xml.model.enums.handlers.MarkerEnumHandler;
import ome.xml.model.enums.handlers.MediumEnumHandler;
import ome.xml.model.enums.handlers.MicrobeamManipulationTypeEnumHandler;
import ome.xml.model.enums.handlers.MicroscopeTypeEnumHandler;
import ome.xml.model.enums.handlers.NamingConventionEnumHandler;
import ome.xml.model.enums.handlers.PixelTypeEnumHandler;
import ome.xml.model.enums.handlers.PulseEnumHandler;

/**
 * Abstract superclass of all biological file format readers.
 */
public abstract class FormatReader extends FormatHandler
  implements IFormatReader
{

  // -- Constants --

  /** Default thumbnail width and height. */
  protected static final int THUMBNAIL_DIMENSION = 128;

  // -- Fields --

  /** Current file. */
  protected transient RandomAccessInputStream in;

  /** Hashtable containing metadata key/value pairs. */
  protected Hashtable<String, Object> metadata;

  /** The number of the current series. */
  protected int coreIndex = 0;

  /** The number of the current series (non flat). */
  protected int series = 0;

  /** Core metadata values. */
  protected List<CoreMetadata> core;

  /** The number of the current resolution. */
  protected int resolution = 0;

  /** Whether or not resolutions are flattened. */
  protected boolean flattenedResolutions = true;

  /**
   * Whether the file extension matching one of the reader's suffixes
   * is necessary to identify the file as an instance of this format.
   */
  protected boolean suffixNecessary = true;

  /**
   * Whether the file extension matching one of the reader's suffixes
   * is sufficient to identify the file as an instance of this format.
   */
  protected boolean suffixSufficient = true;

  /** Whether this format supports multi-file datasets. */
  protected boolean hasCompanionFiles = false;

  /** Short description of the structure of the dataset. */
  protected String datasetDescription = "Single file";

  /** Whether or not to normalize float data. */
  protected boolean normalizeData;

  /** Whether or not to filter out invalid metadata. */
  protected boolean filterMetadata;

  /** Whether or not to save proprietary metadata in the MetadataStore. */
  protected boolean saveOriginalMetadata = false;

  /** Whether or not MetadataStore sets C = 3 for indexed color images. */
  protected boolean indexedAsRGB = false;

  /** Whether or not to group multi-file formats. */
  protected boolean group = true;

  /** List of domains in which this format is used. */
  protected String[] domains = new String[0];

  /**
   * Current metadata store. Should never be accessed directly as the
   * semantics of {@link #getMetadataStore()} prevent "null" access.
   */
  protected MetadataStore metadataStore = new DummyMetadata();

  private ServiceFactory factory;
  private OMEXMLService service;

  // -- Constructors --

  /** Constructs a format reader with the given name and default suffix. */
  public FormatReader(String format, String suffix) { super(format, suffix); }

  /** Constructs a format reader with the given name and default suffixes. */
  public FormatReader(String format, String[] suffixes) {
    super(format, suffixes);
  }

  // -- Internal FormatReader API methods --

  /* @see IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    if (in != null) {
      in.close();
    }
    in = new RandomAccessInputStream(currentId);
    in.order(isLittleEndian());
  }

  /**
   * Initializes the given file (parsing header information, etc.).
   * Most subclasses should override this method to perform
   * initialization operations such as parsing metadata.
   *
   * @throws FormatException if a parsing error occurs processing the file.
   * @throws IOException if an I/O error occurs processing the file
   */
  protected void initFile(String id) throws FormatException, IOException {
    LOGGER.debug("{}.initFile({})", this.getClass().getName(), id);
    if (currentId != null) {
      String[] s = getUsedFiles();
      for (int i=0; i<s.length; i++) {
        if (id.equals(s[i])) return;
      }
    }

    coreIndex = 0;
    series = 0;
    close();
    currentId = id;
    metadata = new Hashtable<String, Object>();

    core = new ArrayList<CoreMetadata>();
    CoreMetadata core0 = new CoreMetadata();
    core.add(core0);
    core0.orderCertain = true;

    // reinitialize the MetadataStore
    // NB: critical for metadata conversion to work properly!
    getMetadataStore().createRoot();

    String optionsFile = DynamicMetadataOptions.getMetadataOptionsFile(id);
    if (optionsFile != null) {
      MetadataOptions options = getMetadataOptions();
      if (options instanceof DynamicMetadataOptions) {
        ((DynamicMetadataOptions) options).loadOptions(optionsFile, getAvailableOptions());
      }
    }
  }

  /** Returns the list of available metadata options. */
  protected ArrayList<String> getAvailableOptions() {
    ArrayList<String> optionsList = new ArrayList<String>();
    optionsList.add(DynamicMetadataOptions.METADATA_LEVEL_KEY);
    optionsList.add(DynamicMetadataOptions.READER_VALIDATE_KEY);
    return optionsList;
  }
  
  /** Returns true if the given file name is in the used files list. */
  protected boolean isUsedFile(String file) {
    String[] usedFiles = getUsedFiles();
    for (String used : usedFiles) {
      if (used.equals(file)) return true;
      String path = new Location(file).getAbsolutePath();
      if (used.equals(path)) return true;
    }
    return false;
  }

  /** Adds an entry to the specified Hashtable. */
  protected void addMeta(String key, Object value,
    Hashtable<String, Object> meta)
  {
    if (key == null || value == null ||
      getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM)
    {
      return;
    }

    key = key.trim();

    boolean string = value instanceof String || value instanceof Character;

    // string value, if passed in value is a string
    String val = string ? String.valueOf(value) : null;

    if (filterMetadata ||
      (saveOriginalMetadata && (getMetadataStore() instanceof OMEXMLMetadata)))
    {
      // filter out complex data types
      boolean simple = string ||
        value instanceof Number ||
        value instanceof Boolean;
      if (!simple) return;

      // verify key & value are reasonable length
      int maxLen = 8192;
      if (key.length() > maxLen) return;
      if (string && val.length() > maxLen) return;

      // remove all non-printable characters
      key = DataTools.sanitize(key);
      if (string) val = DataTools.sanitize(val);

      // verify key contains at least one alphabetic character
      if (!key.matches(".*[a-zA-Z].*")) return;

      // remove &lt;, &gt; and &amp; to prevent XML parsing errors
      String[] invalidSequences = new String[] {
        "&lt;", "&gt;", "&amp;", "<", ">", "&"
      };
      for (int i=0; i<invalidSequences.length; i++) {
        if (key.indexOf(invalidSequences[i]) >= 0) {
          key = key.replaceAll(invalidSequences[i], "");
        }
        if (string && val.indexOf(invalidSequences[i]) >= 0) {
          val = val.replaceAll(invalidSequences[i], "");
        }
      }

      // verify key & value are not empty
      if (key.length() == 0) return;
      if (string && val.trim().length() == 0) return;

      if (string) value = val;
    }

    meta.put(key, val == null ? value : val);
  }

  /** Adds an entry to the global metadata table. */
  protected void addGlobalMeta(String key, Object value) {
    addMeta(key, value, metadata);
  }

  /** Adds an entry to the global metadata table. */
  protected void addGlobalMeta(String key, boolean value) {
    addGlobalMeta(key, Boolean.valueOf(value));
  }

  /** Adds an entry to the global metadata table. */
  protected void addGlobalMeta(String key, byte value) {
    addGlobalMeta(key, Byte.valueOf(value));
  }

  /** Adds an entry to the global metadata table. */
  protected void addGlobalMeta(String key, short value) {
    addGlobalMeta(key, Short.valueOf(value));
  }

  /** Adds an entry to the global metadata table. */
  protected void addGlobalMeta(String key, int value) {
    addGlobalMeta(key, Integer.valueOf(value));
  }

  /** Adds an entry to the global metadata table. */
  protected void addGlobalMeta(String key, long value) {
    addGlobalMeta(key, Long.valueOf(value));
  }

  /** Adds an entry to the global metadata table. */
  protected void addGlobalMeta(String key, float value) {
    addGlobalMeta(key, Float.valueOf(value));
  }

  /** Adds an entry to the global metadata table. */
  protected void addGlobalMeta(String key, double value) {
    addGlobalMeta(key, Double.valueOf(value));
  }

  /** Adds an entry to the global metadata table. */
  protected void addGlobalMeta(String key, char value) {
    addGlobalMeta(key, Character.valueOf(value));
  }

  /** Gets a value from the global metadata table. */
  protected Object getGlobalMeta(String key) {
    return metadata.get(key);
  }

  /**
   * Add the given key/value pair to the given hashtable.
   * If the key already exists, a list will be stored in the hashtable
   * and the value will be appended to the list.
   * @param key the key to store in the hashtable
   * @param value the value to store in the hashtable or list
   * @param meta the hashtable in which to store the key/value
   */
  protected void addMetaList(String key, Object value,
    Hashtable<String, Object> meta)
  {
    Vector list = (Vector) meta.remove(key);
    addMeta(key, value, meta);
    Object newValue = meta.remove(key);
    if (newValue != null) {
      if (list == null) {
        list = new Vector();
      }

      list.add(newValue);
      meta.put(key, list);
    }
    else if (list != null) {
      meta.put(key, list);
    }
  }

  /**
   * Add the given key/value pair to the global metadata hashtable.
   * If the key already exists, a list will be stored in the hashtable
   * and the value will be appended to the list.
   * @param key the key to store in the hashtable
   * @param value the value to store in the hashtable or list
   */
  protected void addGlobalMetaList(String key, Object value) {
    addMetaList(key, value, metadata);
  }

  /**
   * Add the given key/value pair to the current series metadata hashtable.
   * If the key already exists, a list will be stored in the hashtable
   * and the value will be appended to the list.
   */
  protected void addSeriesMetaList(String key, Object value) {
    addMetaList(key, value, getCurrentCore().seriesMetadata);
  }

  /**
   * Call {@link #updateMetadataLists(Hashtable)} on
   * all metadata hashtables.
   */
  protected void flattenHashtables() {
    updateMetadataLists(metadata);

    for (int s=0; s<core.size(); s++) {
      if (core.get(s).seriesMetadata.size() > 0) {
        updateMetadataLists(core.get(s).seriesMetadata);
      }
    }
  }

  /**
   * For the given metadata hashtable, replace any value that is
   * a list with one key/value pair per list entry.  The new keys
   * will be the original key with the list index appended.
   * @param meta the hashtable from which to remove lists
   */
  protected void updateMetadataLists(Hashtable<String, Object> meta) {
    String[] keys = meta.keySet().toArray(new String[meta.size()]);
    for (String key : keys) {
      Object v = meta.get(key);
      if (v instanceof Vector) {
        Vector list = (Vector) v;
        int digits = String.valueOf(list.size()).length();

        for (int i=0; i<list.size(); i++) {
          String index = String.valueOf(i + 1);
          while (index.length() < digits) {
            index = "0" + index;
          }
          meta.put(key + " #" + index, list.get(i));
        }

        meta.remove(key);
      }
    }
  }

  /** Adds an entry to the metadata table for the current series. */
  protected void addSeriesMeta(String key, Object value) {
    addMeta(key, value, getCurrentCore().seriesMetadata);
  }

  /** Adds an entry to the metadata table for the current series. */
  protected void addSeriesMeta(String key, boolean value) {
    addSeriesMeta(key, Boolean.valueOf(value));
  }

  /** Adds an entry to the metadata table for the current series. */
  protected void addSeriesMeta(String key, byte value) {
    addSeriesMeta(key, Byte.valueOf(value));
  }

  /** Adds an entry to the metadata table for the current series. */
  protected void addSeriesMeta(String key, short value) {
    addSeriesMeta(key, Short.valueOf(value));
  }

  /** Adds an entry to the metadata table for the current series. */
  protected void addSeriesMeta(String key, int value) {
    addSeriesMeta(key, Integer.valueOf(value));
  }

  /** Adds an entry to the metadata table for the current series. */
  protected void addSeriesMeta(String key, long value) {
    addSeriesMeta(key, Long.valueOf(value));
  }

  /** Adds an entry to the metadata table for the current series. */
  protected void addSeriesMeta(String key, float value) {
    addSeriesMeta(key, Float.valueOf(value));
  }

  /** Adds an entry to the metadata table for the current series. */
  protected void addSeriesMeta(String key, double value) {
    addSeriesMeta(key, Double.valueOf(value));
  }

  /** Adds an entry to the metadata table for the current series. */
  protected void addSeriesMeta(String key, char value) {
    addSeriesMeta(key, Character.valueOf(value));
  }

  /** Gets an entry from the metadata table for the current series. */
  protected Object getSeriesMeta(String key) {
    return getCurrentCore().seriesMetadata.get(key);
  }

  /** Reads a raw plane from disk. */
  protected byte[] readPlane(RandomAccessInputStream s, int x, int y,
    int w, int h, byte[] buf) throws IOException
  {
    return readPlane(s, x, y, w, h, 0, buf);
  }

  /** Reads a raw plane from disk. */
  protected byte[] readPlane(RandomAccessInputStream s, int x, int y,
    int w, int h, int scanlinePad, byte[] buf) throws IOException
  {
    int c = getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    if (x == 0 && y == 0 && w == getSizeX() && h == getSizeY() &&
      scanlinePad == 0)
    {
      s.read(buf);
    }
    else if (x == 0 && w == getSizeX() && scanlinePad == 0) {
      if (isInterleaved()) {
        s.skipBytes((long) y * w * bpp * c);
        s.read(buf, 0, h * w * bpp * c);
      }
      else {
        int rowLen = w * bpp;
        for (int channel=0; channel<c; channel++) {
          s.skipBytes((long) y * rowLen);
          s.read(buf, channel * h * rowLen, h * rowLen);
          if (channel < c - 1) {
            // no need to skip bytes after reading final channel
            s.skipBytes((long) (getSizeY() - y - h) * rowLen);
          }
        }
      }
    }
    else {
      long scanlineWidth = getSizeX() + scanlinePad;
      if (isInterleaved()) {
        s.skipBytes(y * scanlineWidth * bpp * c);
        for (int row=0; row<h; row++) {
          s.skipBytes(x * bpp * c);
          s.read(buf, row * w * bpp * c, w * bpp * c);
          if (row < h - 1) {
            // no need to skip bytes after reading final row
            s.skipBytes(bpp * c * (scanlineWidth - w - x));
          }
        }
      }
      else {
        for (int channel=0; channel<c; channel++) {
          s.skipBytes(y * scanlineWidth * bpp);
          for (int row=0; row<h; row++) {
            s.skipBytes(x * bpp);
            s.read(buf, channel * w * h * bpp + row * w * bpp, w * bpp);
            if (row < h - 1 || channel < c - 1) {
              // no need to skip bytes after reading final row of final channel
              s.skipBytes(bpp * (scanlineWidth - w - x));
            }
          }
          if (channel < c - 1) {
            // no need to skip bytes after reading final channel
            s.skipBytes(scanlineWidth * bpp * (getSizeY() - y - h));
          }
        }
      }
    }
    return buf;
  }

  /** Return a properly configured loci.formats.meta.FilterMetadata. */
  protected MetadataStore makeFilterMetadata() {
    return new FilterMetadata(getMetadataStore(), isMetadataFiltered());
  }

  // -- IFormatReader API methods --

  /**
   * Checks if a file matches the type of this format reader.
   * Checks filename suffixes against those known for this format.
   * If the suffix check is inconclusive and the open parameter is true,
   * the file is opened and tested with
   * {@link #isThisType(RandomAccessInputStream)}.
   *
   * @param open If true, and the file extension is insufficient to determine
   *   the file type, the (existing) file is opened for further analysis.
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    // if file extension ID is insufficient and we can't open the file, give up
    if (!suffixSufficient && !open) return false;

    if (suffixNecessary || suffixSufficient) {
      // it's worth checking the file extension
      boolean suffixMatch = super.isThisType(name);

      // if suffix match is required but it doesn't match, failure
      if (suffixNecessary && !suffixMatch) return false;

      // if suffix matches and that's all we need, green light it
      if (suffixMatch && suffixSufficient) return true;
    }

    // suffix matching was inconclusive; we need to analyze the file contents
    if (!open) return false; // not allowed to open any files
    try (RandomAccessInputStream stream = new RandomAccessInputStream(name)) {
      return isThisType(stream);
    }
    catch (IOException exc) {
      LOGGER.debug("", exc);
      return false;
    }
  }

  /* @see IFormatReader#isThisType(byte[]) */
  @Override
  public boolean isThisType(byte[] block) {
    try (RandomAccessInputStream stream = new RandomAccessInputStream(block)) {
      return isThisType(stream);
    }
    catch (IOException e) {
      LOGGER.debug("", e);
    }
    return false;
  }

  /* @see IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return false;
  }

  /* @see IFormatReader#getImageCount() */
  @Override
  public int getImageCount() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().imageCount;
  }

  /* @see IFormatReader#isRGB() */
  @Override
  public boolean isRGB() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().rgb;
  }

  /* @see IFormatReader#getSizeX() */
  @Override
  public int getSizeX() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().sizeX;
  }

  /* @see IFormatReader#getSizeY() */
  @Override
  public int getSizeY() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().sizeY;
  }

  /* @see IFormatReader#getSizeZ() */
  @Override
  public int getSizeZ() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().sizeZ;
  }

  /* @see IFormatReader#getSizeC() */
  @Override
  public int getSizeC() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().sizeC;
  }

  /* @see IFormatReader#getSizeT() */
  @Override
  public int getSizeT() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().sizeT;
  }

  /* @see IFormatReader#getPixelType() */
  @Override
  public int getPixelType() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().pixelType;
  }

  /* @see IFormatReader#getBitsPerPixel() */
  @Override
  public int getBitsPerPixel() {
    FormatTools.assertId(currentId, true, 1);
    if (getCurrentCore().bitsPerPixel == 0) {
      getCurrentCore().bitsPerPixel =
        FormatTools.getBytesPerPixel(getPixelType()) * 8;
    }
    return getCurrentCore().bitsPerPixel;
  }

  /* @see IFormatReader#getEffectiveSizeC() */
  @Override
  public int getEffectiveSizeC() {
    // NB: by definition, imageCount == effectiveSizeC * sizeZ * sizeT
    int sizeZT = getSizeZ() * getSizeT();
    if (sizeZT == 0) return 0;
    return getImageCount() / sizeZT;
  }

  /* @see IFormatReader#getRGBChannelCount() */
  @Override
  public int getRGBChannelCount() {
    int effSizeC = getEffectiveSizeC();
    if (effSizeC == 0) return 0;
    return getSizeC() / effSizeC;
  }

  /* @see IFormatReader#isIndexed() */
  @Override
  public boolean isIndexed() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().indexed;
  }

  /* @see IFormatReader#isFalseColor() */
  @Override
  public boolean isFalseColor() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().falseColor;
  }

  /* @see IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return null;
  }

  /* @see IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return null;
  }

  /* @see IFormatReader#getModuloZ() */
  @Override
  public Modulo getModuloZ() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().moduloZ;
  }

  /* @see IFormatReader#getModuloC() */
  @Override
  public Modulo getModuloC() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().moduloC;
  }

  /* @see IFormatReader#getModuloT() */
  @Override
  public Modulo getModuloT() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().moduloT;
  }

  /* @see IFormatReader#getThumbSizeX() */
  @Override
  public int getThumbSizeX() {
    FormatTools.assertId(currentId, true, 1);
    if (getCurrentCore().thumbSizeX == 0) {
      int sx = getSizeX();
      int sy = getSizeY();
      int thumbSizeX = 0;
      if (sx < THUMBNAIL_DIMENSION && sy < THUMBNAIL_DIMENSION)
        thumbSizeX = sx;
      else if (sx > sy) thumbSizeX = THUMBNAIL_DIMENSION;
      else if (sy > 0) thumbSizeX = sx * THUMBNAIL_DIMENSION / sy;
      if (thumbSizeX == 0) thumbSizeX = 1;
      return thumbSizeX;
    }
    return getCurrentCore().thumbSizeX;
  }

  /* @see IFormatReader#getThumbSizeY() */
  @Override
  public int getThumbSizeY() {
    FormatTools.assertId(currentId, true, 1);
    if (getCurrentCore().thumbSizeY == 0) {
      int sx = getSizeX();
      int sy = getSizeY();
      int thumbSizeY = 1;
      if (sx < THUMBNAIL_DIMENSION && sy < THUMBNAIL_DIMENSION)
        thumbSizeY = sy;
      else if (sy > sx) thumbSizeY = THUMBNAIL_DIMENSION;
      else if (sx > 0) thumbSizeY = sy * THUMBNAIL_DIMENSION / sx;
      if (thumbSizeY == 0) thumbSizeY = 1;
      return thumbSizeY;
    }
    return getCurrentCore().thumbSizeY;
  }

  /* @see IFormatReader.isLittleEndian() */
  @Override
  public boolean isLittleEndian() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().littleEndian;
  }

  /* @see IFormatReader#getDimensionOrder() */
  @Override
  public String getDimensionOrder() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().dimensionOrder;
  }

  /* @see IFormatReader#isOrderCertain() */
  @Override
  public boolean isOrderCertain() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().orderCertain;
  }

  /* @see IFormatReader#isThumbnailSeries() */
  @Override
  public boolean isThumbnailSeries() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().thumbnail;
  }

  /* @see IFormatReader#isInterleaved() */
  @Override
  public boolean isInterleaved() {
    return isInterleaved(0);
  }

  /* @see IFormatReader#isInterleaved(int) */
  @Override
  public boolean isInterleaved(int subC) {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().interleaved;
  }

  /* @see IFormatReader#openBytes(int) */
  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return openBytes(no, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(no, buf, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    int ch = getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    byte[] newBuffer;
    try {
      newBuffer = DataTools.allocate(w, h, ch, bpp);
    }
    catch (IllegalArgumentException e) {
      throw new FormatException("Image plane too large. Only 2GB of data can " +
        "be extracted at one time. You can work around the problem by opening " +
        "the plane in tiles; for further details, see: " +
        "https://docs.openmicroscopy.org/bio-formats/" + FormatTools.VERSION +
        "/about/bug-reporting.html#common-issues-to-check", e);
    }
    return openBytes(no, newBuffer, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public abstract byte[] openBytes(int no, byte[] buf, int x, int y,
    int w, int h) throws FormatException, IOException;

  /* @see IFormatReader#openPlane(int, int, int, int, int int) */
  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    // NB: Readers use byte arrays by default as the native type.
    return openBytes(no, x, y, w, h);
  }

  /* @see IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return FormatTools.openThumbBytes(this, no);
  }

  /* @see IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    if (in != null) in.close();
    if (!fileOnly) {
      in = null;
      currentId = null;
      resolution = 0;
      core = null;
    }
  }

  /* @see IFormatReader#getSeriesCount() */
  @Override
  public int getSeriesCount() {
    FormatTools.assertId(currentId, true, 1);
    if (hasFlattenedResolutions()) {
      return core.size();
    }

    return coreIndexToSeries(core.size() - 1) + 1;
  }

  /* @see IFormatReader#setSeries(int) */
  @Override
  public void setSeries(int no) {
    coreIndex = seriesToCoreIndex(no);
    series = no;
    resolution = 0;
  }

  /* @see IFormatReader#getSeries() */
  @Override
  public int getSeries() {
    return series;
  }

  /* @see IFormatReader#setGroupFiles(boolean) */
  @Override
  public void setGroupFiles(boolean groupFiles) {
    FormatTools.assertId(currentId, false, 1);
    group = groupFiles;
  }

  /* @see IFormatReader#isGroupFiles() */
  @Override
  public boolean isGroupFiles() {
    return group;
  }

  /* @see IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id)
    throws FormatException, IOException
  {
    return FormatTools.CANNOT_GROUP;
  }

  /* @see IFormatReader#isMetadataComplete() */
  @Override
  public boolean isMetadataComplete() {
    FormatTools.assertId(currentId, true, 1);
    return getCurrentCore().metadataComplete;
  }

  /* @see IFormatReader#setNormalized(boolean) */
  @Override
  public void setNormalized(boolean normalize) {
    FormatTools.assertId(currentId, false, 1);
    normalizeData = normalize;
  }

  /* @see IFormatReader#isNormalized() */
  @Override
  public boolean isNormalized() {
    return normalizeData;
  }

  /* @see IFormatReader#setOriginalMetadataPopulated(boolean) */
  @Override
  public void setOriginalMetadataPopulated(boolean populate) {
    FormatTools.assertId(currentId, false, 1);
    saveOriginalMetadata = populate;
  }

  /* @see IFormatReader#isOriginalMetadataPopulated() */
  @Override
  public boolean isOriginalMetadataPopulated() {
    return saveOriginalMetadata;
  }

  /* @see IFormatReader#getUsedFiles() */
  @Override
  public String[] getUsedFiles() {
    return getUsedFiles(false);
  }

  /* @see IFormatReader#getUsedFiles() */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    String[] seriesUsedFiles;    
    Set<String> files = new LinkedHashSet<String>();

    int seriesCount = getSeriesCount();
    if (seriesCount == 1) {
      seriesUsedFiles = getSeriesUsedFiles(noPixels);
      if (null == seriesUsedFiles) {
        seriesUsedFiles = new String[] {};
      }
      files.addAll(Arrays.asList(seriesUsedFiles));
    }
    else {
      int oldSeries = getSeries();
  
      for (int i = 0; i < seriesCount; i++) {
        setSeries(i);
        seriesUsedFiles = getSeriesUsedFiles(noPixels);
        if (seriesUsedFiles != null) {
          files.addAll(Arrays.asList(seriesUsedFiles));
        }
      }
      setSeries(oldSeries);
    }

    String optionsFile = DynamicMetadataOptions.getMetadataOptionsFile(currentId);
    if (optionsFile != null && new Location(optionsFile).exists()) {
      files.add(optionsFile);
    }

    return files.toArray(new String[files.size()]);
  }

  /* @see IFormatReader#getSeriesUsedFiles() */
  @Override
  public String[] getSeriesUsedFiles() {
    return getSeriesUsedFiles(false);
  }

  /* @see IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    return noPixels ? null : new String[] {currentId};
  }

  /* @see IFormatReader#getAdvancedUsedFiles(boolean) */
  @Override
  public FileInfo[] getAdvancedUsedFiles(boolean noPixels) {
    String[] files = getUsedFiles(noPixels);
    if (files == null) return null;
    FileInfo[] infos = new FileInfo[files.length];
    for (int i=0; i<infos.length; i++) {
      infos[i] = new FileInfo();
      infos[i].filename = files[i];
      infos[i].reader = this.getClass();
      infos[i].usedToInitialize = files[i].endsWith(getCurrentFile());
    }
    return infos;
  }

  /* @see IFormatReader#getAdvancedSeriesUsedFiles(boolean) */
  @Override
  public FileInfo[] getAdvancedSeriesUsedFiles(boolean noPixels) {
    String[] files = getSeriesUsedFiles(noPixels);
    if (files == null) return null;
    FileInfo[] infos = new FileInfo[files.length];
    for (int i=0; i<infos.length; i++) {
      infos[i] = new FileInfo();
      infos[i].filename = files[i];
      infos[i].reader = this.getClass();
      infos[i].usedToInitialize = files[i].endsWith(getCurrentFile());
    }
    return infos;
  }

  /* @see IFormatReader#getCurrentFile() */
  @Override
  public String getCurrentFile() {
    return currentId;
  }

  /* @see IFormatReader#getIndex(int, int, int) */
  @Override
  public int getIndex(int z, int c, int t) {
    FormatTools.assertId(currentId, true, 1);
    return FormatTools.getIndex(this, z, c, t);
  }

  /* @see IFormatReader#getIndex(int, int, int, int, int, int) */
  public int getIndex(int z, int c, int t, int moduloZ, int moduloC, int moduloT) {
    FormatTools.assertId(currentId, true, 1);
    return FormatTools.getIndex(this, z, c, t, moduloZ, moduloC, moduloT);
  }

  /* @see IFormatReader#getZCTCoords(int) */
  @Override
  public int[] getZCTCoords(int index) {
    FormatTools.assertId(currentId, true, 1);
    return FormatTools.getZCTCoords(this, index);
  }

  /* @see IFormatReader#getZCTModuloCoords(int) */
  public int[] getZCTModuloCoords(int index) {
    FormatTools.assertId(currentId, true, 1);
    return FormatTools.getZCTModuloCoords(this, index);
  }

  /* @see IFormatReader#getMetadataValue(String) */
  @Override
  public Object getMetadataValue(String field) {
    FormatTools.assertId(currentId, true, 1);
    flattenHashtables();
    return getGlobalMeta(field);
  }

  /* @see IFormatReader#getSeriesMetadataValue(String) */
  @Override
  public Object getSeriesMetadataValue(String field) {
    FormatTools.assertId(currentId, true, 1);
    flattenHashtables();
    return getSeriesMeta(field);
  }

  /* @see IFormatReader#getGlobalMetadata() */
  @Override
  public Hashtable<String, Object> getGlobalMetadata() {
    FormatTools.assertId(currentId, true, 1);
    flattenHashtables();
    return metadata;
  }

  /* @see IFormatReader#getSeriesMetadata() */
  @Override
  public Hashtable<String, Object> getSeriesMetadata() {
    FormatTools.assertId(currentId, true, 1);
    if (getCurrentCore().seriesMetadata.size() > 0) {
      flattenHashtables();
    }
    return getCurrentCore().seriesMetadata;
  }

  /* @see IFormatReader#getCoreMetadataList() */
  @Override
  public List<CoreMetadata> getCoreMetadataList() {
    FormatTools.assertId(currentId, true, 1);
    return core;
  }

  /* @see IFormatReader#setMetadataFiltered(boolean) */
  @Override
  public void setMetadataFiltered(boolean filter) {
    FormatTools.assertId(currentId, false, 1);
    filterMetadata = filter;
  }

  /* @see IFormatReader#isMetadataFiltered() */
  @Override
  public boolean isMetadataFiltered() {
    return filterMetadata;
  }

  /* @see IFormatReader#setMetadataStore(MetadataStore) */
  @Override
  public void setMetadataStore(MetadataStore store) {
    FormatTools.assertId(currentId, false, 1);
    if (store == null) {
      throw new IllegalArgumentException("Metadata object cannot be null; " +
        "use loci.formats.meta.DummyMetadata instead");
    }
    metadataStore = store;
  }

  /* @see IFormatReader#getMetadataStore() */
  @Override
  public MetadataStore getMetadataStore() {
    return metadataStore;
  }

  /* @see IFormatReader#getMetadataStoreRoot() */
  @Override
  public Object getMetadataStoreRoot() {
    FormatTools.assertId(currentId, true, 1);
    return getMetadataStore().getRoot();
  }

  /* @see IFormatReader#getUnderlyingReaders() */
  @Override
  public IFormatReader[] getUnderlyingReaders() {
    return null;
  }

  /* @see IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return true;
  }

  /* @see IFormatReader#getRequiredDirectories(String[]) */
  @Override
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return 0;
  }

  /* @see IFormatReader#getDatasetStructureDescription() */
  @Override
  public String getDatasetStructureDescription() {
    return datasetDescription;
  }

  /* @see IFormatReader#hasCompanionFiles() */
  @Override
  public boolean hasCompanionFiles() {
    return hasCompanionFiles;
  }

  /* @see IFormatReader#getPossibleDomains(String) */
  @Override
  public String[] getPossibleDomains(String id)
    throws FormatException, IOException
  {
    return domains;
  }

  /* @see IFormatReader#getDomains() */
  @Override
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    return domains;
  }

  /* @see IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeX();
  }

  /* @see IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
     int bpp = FormatTools.getBytesPerPixel(getPixelType());
     int maxHeight = (1024 * 1024) / (getSizeX() * getRGBChannelCount() * bpp);
     return (int) Math.min(maxHeight, getSizeY());
  }

  // -- Sub-resolution API methods --

  @Override
  public int seriesToCoreIndex(int series)
  {
    if (hasFlattenedResolutions()) {
      // coreIndex and series are identical
      if (series < 0 || series >= core.size()) {
        throw new IllegalArgumentException("Invalid series: " + series);
      }
      return series;
    }

    // Use corresponding coreIndex
    if (this.series == series) {
      return coreIndex - resolution;
    }

    int index = 0;

    for (int i = 0; i < series && index < core.size(); i++) {
      if (core.get(i) != null)
        index += core.get(index).resolutionCount;
      else
        throw new IllegalArgumentException("Invalid series (null core["+i+"]: " + series);
    }

    if (index < 0 || index >= core.size()) {
      throw new IllegalArgumentException("Invalid series: " + series + "  index="+index);
    }

    return index;
  }

  @Override
  public int coreIndexToSeries(int index)
  {
    if (index < 0 || index >= core.size()) {
      throw new IllegalArgumentException("Invalid index: " + index);
    }

    if (hasFlattenedResolutions()) {
      // coreIndex and series are identical
      return index;
    }

    // Use corresponding series
    if (coreIndex == index) {
      return series;
    }

    // Convert from non-flattened coreIndex to flattened series
    int series = 0;
    for (int i=0; i<index;) {
      if (core.get(i) != null) {
        int nextSeries = i + core.get(i).resolutionCount;
      if (index < nextSeries)
        break;
      i = nextSeries;
      } else {
        throw new IllegalArgumentException("Invalid coreIndex (null core["+i+"]: " + index);
      }
      series++;
    }
    return series;
  }

  /* @see IFormatReader#getResolutionCount() */
  @Override
  public int getResolutionCount() {
    FormatTools.assertId(currentId, true, 1);

    if (hasFlattenedResolutions()) {
      return 1;
    }

    return core.get(seriesToCoreIndex(getSeries())).resolutionCount;
  }

  /* @see IFormatReader#setResolution(int) */
  @Override
  public void setResolution(int no) {
    if (no < 0 || no >= getResolutionCount()) {
      throw new IllegalArgumentException("Invalid resolution: " + no);
    }
    coreIndex = seriesToCoreIndex(getSeries()) + no;
    resolution = no;
  }

  /* @see IFormatReader#getResolution() */
  @Override
  public int getResolution() {
    return resolution;
  }

  /* @see IFormatReader#hasFlattenedResolutions */
  @Override
  public boolean hasFlattenedResolutions() {
    return flattenedResolutions;
  }

  /* @see IFormatReader#setFlattenedResolutions(boolean) */
  @Override
  public void setFlattenedResolutions(boolean flattened) {
    FormatTools.assertId(currentId, false, 1);
    flattenedResolutions = flattened;
  }

  @Override
  public int getCoreIndex() {
    return coreIndex;
  }

  /* @see IFormatHandler#setCoreIndex(int) */
  @Override
  public void setCoreIndex(int no) {
    if (no < 0 || no >= core.size()) {
      throw new IllegalArgumentException("Invalid series: " + no);
    }
    series = coreIndexToSeries(no);
    coreIndex = no;
    resolution = no - seriesToCoreIndex(series);
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#isThisType(String) */
  @Override
  public boolean isThisType(String name) {
    // if necessary, open the file for further analysis
    return isThisType(name, true);
  }

  /**
   * Initialize a reader from the input file name.
   *
   * Call {@link #initFile(String id)} to initialize the input file, read
   * all of the metadata and set the reader up for reading planes.
   * The performance of this method depends on the format and can be up to
   * several minutes for large file sets.
   *
   *  @param id a {@link String} specifying the path to the file
   */
  @Override
  public void setId(String id) throws FormatException, IOException {
    if (!isOmero(id)) {
      LOGGER.debug("{} initializing {}", this.getClass().getSimpleName(), id);
    }
    

    if (currentId == null || !new Location(id).getAbsolutePath().equals(
      new Location(currentId).getAbsolutePath()))
    {
      initFile(id);

      MetadataStore store = getMetadataStore();
      if (saveOriginalMetadata) {
        if (store instanceof OMEXMLMetadata) {
          setupService();
          Hashtable<String, Object> allMetadata =
            new Hashtable<>(metadata);

          for (int series=0; series<getSeriesCount(); series++) {
            String name = "Series " + series;
            try {
              String realName = ((IMetadata) store).getImageName(series);
              if (realName != null && realName.trim().length() != 0) {
                name = realName;
              }
            }
            catch (Exception e) { }
            setSeries(series);
            MetadataTools.merge(getSeriesMetadata(), allMetadata, name + " ");
          }
          setSeries(0);

          service.populateOriginalMetadata((OMEXMLMetadata) store, allMetadata);
        }
      }

      if (store instanceof OMEXMLMetadata) {
        ((OMEXMLMetadata) store).resolveReferences();
        setupService();

        if (getMetadataOptions().isValidate()) {
          try {
            String omexml = service.getOMEXML((MetadataRetrieve)store);
            service.validateOMEXML(omexml);
          } catch (ServiceException | NullPointerException e) {
            LOGGER.warn("OMEXMLService unable to create OME-XML metadata object.", e);
          }
        }

        for (int series=0; series<getSeriesCount(); series++) {
          setSeries(series);

          if (getModuloZ().length() > 1 || getModuloC().length() > 1 ||
            getModuloT().length() > 1)
          {
            service.addModuloAlong(
              (OMEXMLMetadata) store, getCurrentCore(), series);
          }
        }
        setSeries(0);
      }
    }
  }

  /** Initialize the OMEXMLService needed by {@link #setId(String)} */
  private void setupService() {
    try {
      if (factory == null) factory = new ServiceFactory();
      if (service == null) {
        service = factory.getInstance(OMEXMLService.class);
      }
    }
    catch (DependencyException e) {
      LOGGER.warn("OMEXMLService not available.", e);
    }
  }

  /* @see IFormatHandler#close() */
  @Override
  public void close() throws IOException {
    close(false);
  }

  /**
   * Get the CoreMetadata corresponding to the current series and resolution
   *
   * @return the CoreMetadata
   */
  protected CoreMetadata getCurrentCore() {
    return core.get(getCoreIndex());
  }

  // -- Metadata enumeration convenience methods --

  /**
   * Retrieves an {@link ome.xml.model.enums.AcquisitionMode} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getAcquisitionMode(String)}.
   */
  @Deprecated
  protected AcquisitionMode getAcquisitionMode(String value)
    throws FormatException {
    return MetadataTools.getAcquisitionMode(value);
  }

  /**
   * Retrieves an {@link ome.xml.model.enums.ArcType} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getArcType(String)}.
   */
  @Deprecated
  protected ArcType getArcType(String value) throws FormatException {
    return MetadataTools.getArcType(value);
  }

  /**
   * Retrieves an {@link ome.xml.model.enums.Binning} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getBinning(String)}.
   */
  @Deprecated
  protected Binning getBinning(String value) throws FormatException {
    return MetadataTools.getBinning(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.Compression} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getCompression(String)}.
   */
  @Deprecated
  protected Compression getCompression(String value) throws FormatException {
    return MetadataTools.getCompression(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.ContrastMethod} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getContrastMethod(String)}.
   */
  @Deprecated
  protected ContrastMethod getContrastMethod(String value)
    throws FormatException {
    return MetadataTools.getContrastMethod(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.Correction} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getCorrection(String)}.
   */
  @Deprecated
  protected Correction getCorrection(String value) throws FormatException {
    return MetadataTools.getCorrection(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.DetectorType} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getDetectorType(String)}.
   */
  @Deprecated
  protected DetectorType getDetectorType(String value) throws FormatException {
    return MetadataTools.getDetectorType(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.DimensionOrder} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getDimensionOrder(String)}.
   */
  @Deprecated
  protected DimensionOrder getDimensionOrder(String value)
    throws FormatException {
    return MetadataTools.getDimensionOrder(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.ExperimentType} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getExperimentType(String)}.
   */
  @Deprecated
  protected ExperimentType getExperimentType(String value)
    throws FormatException {
    return MetadataTools.getExperimentType(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.FilamentType} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getFilamentType(String)}.
   */
  @Deprecated
  protected FilamentType getFilamentType(String value) throws FormatException {
    return MetadataTools.getFilamentType(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.FillRule} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getFillRule(String)}.
   */
  @Deprecated
  protected FillRule getFillRule(String value) throws FormatException {
    return MetadataTools.getFillRule(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.FilterType} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getFilterType(String)}.
   */
  @Deprecated
  protected FilterType getFilterType(String value) throws FormatException {
    return MetadataTools.getFilterType(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.FontFamily} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getFontFamily(String)}.
   */
  @Deprecated
  protected FontFamily getFontFamily(String value) throws FormatException {
    return MetadataTools.getFontFamily(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.FontStyle} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getFontStyle(String)}.
   */
  @Deprecated
  protected FontStyle getFontStyle(String value) throws FormatException {
    return MetadataTools.getFontStyle(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.IlluminationType} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getIlluminationType(String)}.
   */
  @Deprecated
  protected IlluminationType getIlluminationType(String value)
    throws FormatException {
    return MetadataTools.getIlluminationType(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.Immersion} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getImmersion(String)}.
   */
  @Deprecated
  protected Immersion getImmersion(String value) throws FormatException {
    return MetadataTools.getImmersion(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.LaserMedium} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getLaserMedium(String)}.
   */
  @Deprecated
  protected LaserMedium getLaserMedium(String value) throws FormatException {
    return MetadataTools.getLaserMedium(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.LaserType} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getLaserType(String)}.
   */
  @Deprecated
  protected LaserType getLaserType(String value) throws FormatException {
    return MetadataTools.getLaserType(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.Marker} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getMarker(String)}.
   */
  @Deprecated
  protected Marker getMarker(String value) throws FormatException {
    return MetadataTools.getMarker(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.Medium} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getMedium(String)}.
   */
  @Deprecated
  protected Medium getMedium(String value) throws FormatException {
    return MetadataTools.getMedium(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.MicrobeamManipulationType}
   * enumeration value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getMicrobeamManipulationType(String)}.
   */
  @Deprecated
  protected MicrobeamManipulationType getMicrobeamManipulationType(String value)
    throws FormatException {
    return MetadataTools.getMicrobeamManipulationType(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.MicroscopeType} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getMicroscopeType(String)}.
   */
  @Deprecated
  protected MicroscopeType getMicroscopeType(String value)
    throws FormatException {
    return MetadataTools.getMicroscopeType(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.NamingConvention} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getNamingConvention(String)}.
   */
  @Deprecated
  protected NamingConvention getNamingConvention(String value)
    throws FormatException {
    return MetadataTools.getNamingConvention(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.PixelType} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getPixelType(String)}.
   */
  @Deprecated
  protected PixelType getPixelType(String value) throws FormatException {
    return MetadataTools.getPixelType(value);
  }
  /**
   * Retrieves an {@link ome.xml.model.enums.Pulse} enumeration
   * value for the given String.
   *
   * @throws ome.xml.model.enums.EnumerationException if an appropriate
   *  enumeration value is not found.
   * @deprecated Use {@link MetadataTools#getPulse(String)}.
   */
  @Deprecated
  protected Pulse getPulse(String value) throws FormatException {
    return MetadataTools.getPulse(value);
  }

  /**
   * Construct an {@link ome.xml.model.AffineTransform} corresponding to
   * the given angle.
   * @param theta the angle of rotation in radians
   */
  protected AffineTransform getRotationTransform(double theta) {
    AffineTransform transform = new AffineTransform();
    transform.setA02(0.0);
    transform.setA12(0.0);
    transform.setA00(Math.cos(theta));
    transform.setA11(Math.cos(theta));
    transform.setA01(Math.sin(theta));
    transform.setA10(-1 * Math.sin(theta));
    return transform;
  }

  private boolean isOmero(String id) {
    return id != null && id.toLowerCase().startsWith("omero:") &&
    id.indexOf("\n") > 0;
  }

}
