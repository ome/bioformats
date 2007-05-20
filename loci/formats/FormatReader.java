//
// FormatReader.java
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/** Abstract superclass of all biological file format readers. */
public abstract class FormatReader extends FormatHandler
  implements IFormatReader
{

  // -- Constants --

  /** Default thumbnail width and height. */
  protected static final int THUMBNAIL_DIMENSION = 128;

  /** File grouping options. */
  public static final int MUST_GROUP = 0;
  public static final int CAN_GROUP = 1;
  public static final int CANNOT_GROUP = 2;

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Hashtable containing metadata key/value pairs. */
  protected Hashtable metadata;

  /** The number of the current series. */
  protected int series = 0;

  /** Core metadata values. */
  protected CoreMetadata core;

  /** Whether or not to normalize float data. */
  protected boolean normalizeData;

  /** Whether or not to filter out invalid metadata. */
  protected boolean filterMetadata;

  /** Whether or not to collect metadata. */
  protected boolean collectMetadata = true;

  /** Whether or not to group multi-file formats. */
  protected boolean group = true;

  /**
   * Current metadata store. Should <b>never</b> be accessed directly as the
   * semantics of {@link #getMetadataStore(String)} prevent "null" access.
   */
  protected MetadataStore metadataStore = new DummyMetadataStore();

  // -- Constructors --

  /** Constructs a format reader with the given name and default suffix. */
  public FormatReader(String format, String suffix) { super(format, suffix); }

  /** Constructs a format reader with the given name and default suffixes. */
  public FormatReader(String format, String[] suffixes) {
    super(format, suffixes);
  }

  // -- Internal FormatReader API methods --

  /**
   * Initializes the given file (parsing header information, etc.).
   * Most subclasses should override this method to perform
   * initialization operations such as parsing metadata.
   */
  protected void initFile(String id) throws FormatException, IOException {
    if (currentId != null) {
      String[] s = getUsedFiles();
      for (int i=0; i<s.length; i++) {
        if (id.equals(s[i])) return;
      }
    }

    close();
    currentId = id;
    metadata = new Hashtable();

    core = new CoreMetadata(1);
    Arrays.fill(core.orderCertain, true);

    // reinitialize the MetadataStore
    getMetadataStore().createRoot();
  }

  /**
   * Opens the given file, reads in the first few KB and calls
   * isThisType(byte[]) to check whether it matches this format.
   */
  protected boolean checkBytes(String name, int maxLen) {
    try {
      RandomAccessStream ras = new RandomAccessStream(name);
      long len = ras.length();
      byte[] buf = new byte[len < maxLen ? (int) len : maxLen];
      ras.readFully(buf);
      ras.close();
      return isThisType(buf);
    }
    catch (IOException exc) { return false; }
  }

  /** Returns true if the given file name is in the used files list. */
  protected boolean isUsedFile(String file) {
    String[] usedFiles = getUsedFiles();
    for (int i=0; i<usedFiles.length; i++) {
      if (usedFiles[i].equals(file) ||
        usedFiles[i].equals(new Location(file).getAbsolutePath()))
      {
        return true;
      }
    }
    return false;
  }

  /** Adds an entry to the metadata table. */
  protected void addMeta(String key, Object value) {
    if (key == null || value == null || !collectMetadata) return;
    if (filterMetadata) {
      // verify key & value are not empty
      if (key.length() == 0) return;
      String val = value.toString();
      if (val.length() == 0) return;

      // verify key & value are reasonable length
      int maxLen = 8192;
      if (key.length() > maxLen) return;
      if (val.length() > maxLen) return;

      // verify key & value start with printable characters
      if (key.charAt(0) < 32) return;
      if (val.charAt(0) < 32) return;

      // verify key contains at least one alphabetic character
      if (!key.matches(".*[a-zA-Z].*")) return;
    }
    metadata.put(key, value);

    try {
      MetadataStore store = getMetadataStore();
      if (store.getClass().getName().equals(
        "loci.formats.ome.OMEXMLMetadataStore"))
      {
        Method m = store.getClass().getMethod("setOriginalMetadata",
          new Class[] {String.class, String.class});
        m.invoke(store, new Object[] {key, value.toString()});
      }
    }
    catch (Throwable t) {
      System.out.println("Error populating OME-XML");
      t.printStackTrace();
    }
  }

  /** Gets a value from the metadata table. */
  protected Object getMeta(String key) {
    return metadata.get(key);
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#getImageCount() */
  public int getImageCount() {
    FormatTools.assertId(currentId, true, 1);
    return core.imageCount[series];
  }

  /* @see IFormatReader#isRGB() */
  public boolean isRGB() {
    FormatTools.assertId(currentId, true, 1);
    return core.rgb[series];
  }

  /* @see IFormatReader#getSizeX() */
  public int getSizeX() {
    FormatTools.assertId(currentId, true, 1);
    return core.sizeX[series];
  }

  /* @see IFormatReader#getSizeY() */
  public int getSizeY() {
    FormatTools.assertId(currentId, true, 1);
    return core.sizeY[series];
  }

  /* @see IFormatReader#getSizeZ() */
  public int getSizeZ() {
    FormatTools.assertId(currentId, true, 1);
    return core.sizeZ[series];
  }

  /* @see IFormatReader#getSizeC() */
  public int getSizeC() {
    FormatTools.assertId(currentId, true, 1);
    return core.sizeC[series];
  }

  /* @see IFormatReader#getSizeT() */
  public int getSizeT() {
    FormatTools.assertId(currentId, true, 1);
    return core.sizeT[series];
  }

  /* @see IFormatReader#getPixelType() */
  public int getPixelType() {
    FormatTools.assertId(currentId, true, 1);
    return core.pixelType[series];
  }

  /* @see IFormatReader#getEffectiveSizeC() */
  public int getEffectiveSizeC() {
    // NB: by definition, imageCount == effectiveSizeC * sizeZ * sizeT
    return getImageCount() / (getSizeZ() * getSizeT());
  }

  /* @see IFormatReader#getRGBChannelCount() */
  public int getRGBChannelCount() {
    return getSizeC() / getEffectiveSizeC();
  }

  /* @see IFormatReader#getChannelDimLengths() */
  public int[] getChannelDimLengths() {
    FormatTools.assertId(currentId, true, 1);
    if (core.cLengths[series] == null) {
      core.cLengths[series] = new int[] {core.sizeC[series]};
    }
    return core.cLengths[series];
  }

  /* @see IFormatReader#getChannelDimTypes() */
  public String[] getChannelDimTypes() {
    FormatTools.assertId(currentId, true, 1);
    if (core.cTypes[series] == null) {
      core.cTypes[series] = new String[] {FormatTools.CHANNEL};
    }
    return core.cTypes[series];
  }

  /* @see IFormatReader#getThumbSizeX() */
  public int getThumbSizeX() {
    FormatTools.assertId(currentId, true, 1);
    if (core.thumbSizeX[series] == 0) {
      int sx = getSizeX();
      int sy = getSizeY();
      core.thumbSizeX[series] =
        sx > sy ? THUMBNAIL_DIMENSION : sx * THUMBNAIL_DIMENSION / sy;
    }
    return core.thumbSizeX[series];
  }

  /* @see IFormatReader#getThumbSizeY() */
  public int getThumbSizeY() {
    FormatTools.assertId(currentId, true, 1);
    if (core.thumbSizeY[series] == 0) {
      int sx = getSizeX();
      int sy = getSizeY();
      core.thumbSizeY[series] =
        sy > sx ? THUMBNAIL_DIMENSION : sy * THUMBNAIL_DIMENSION / sx;
    }
    return core.thumbSizeY[series];
  }

  /* @see IFormatReader.isLittleEndian() */
  public boolean isLittleEndian() {
    FormatTools.assertId(currentId, true, 1);
    return core.littleEndian[series];
  }

  /* @see IFormatReader#getDimensionOrder() */
  public String getDimensionOrder() {
    FormatTools.assertId(currentId, true, 1);
    return core.currentOrder[series];
  }

  /* @see IFormatReader.isOrderCertain() */
  public boolean isOrderCertain() {
    FormatTools.assertId(currentId, true, 1);
    return core.orderCertain[series];
  }

  /* @see IFormatReader#isInterleaved() */
  public boolean isInterleaved() {
    return isInterleaved(0);
  }

  /* @see IFormatReader#isInterleaved(int) */
  public boolean isInterleaved(int subC) {
    FormatTools.assertId(currentId, true, 1);
    return core.interleaved[series];
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(no);
  }

  /* @see IFormatReader#openThumbImage(int) */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    return ImageTools.scale(openImage(no), getThumbSizeX(),
      getThumbSizeY(), false);
  }

  /* @see IFormatReader#openThumbBytes(int) */
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    BufferedImage img = openThumbImage(no);
    byte[][] bytes = ImageTools.getBytes(img);
    if (bytes.length == 1) return bytes[0];
    byte[] rtn = new byte[getRGBChannelCount() * bytes[0].length];
    for (int i=0; i<getRGBChannelCount(); i++) {
      System.arraycopy(bytes[i], 0, rtn, bytes[0].length * i, bytes[i].length);
    }
    return rtn;
  }

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (in != null) in.close();
    }
    else close();
  }

  /* @see IFormatReader#getSeriesCount() */
  public int getSeriesCount() {
    FormatTools.assertId(currentId, true, 1);
    return core.sizeX.length;
  }

  /* @see IFormatReader#setSeries(int) */
  public void setSeries(int no) {
    if (no < 0 || no >= getSeriesCount()) {
      throw new IllegalArgumentException("Invalid series: " + no);
    }
    series = no;
  }

  /* @see IFormatReader#getSeries() */
  public int getSeries() {
    return series;
  }

  /* @see IFormatReader#setGroupFiles(boolean) */
  public void setGroupFiles(boolean groupFiles) {
    FormatTools.assertId(currentId, false, 1);
    group = groupFiles;
  }

  /* @see IFormatReader#isGroupFiles() */
  public boolean isGroupFiles() {
    return group;
  }

  /* @see IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id)
    throws FormatException, IOException
  {
    return FormatTools.CANNOT_GROUP;
  }

  /* @see IFormatReader#setNormalized(boolean) */
  public void setNormalized(boolean normalize) {
    FormatTools.assertId(currentId, false, 1);
    if (currentId != null) {
      String s = "setNormalized called with open file.";
      if (debug && debugLevel >= 2) trace(s);
      else System.err.println("Warning: " + s);
    }
    normalizeData = normalize;
  }

  /* @see IFormatReader#isNormalized() */
  public boolean isNormalized() {
    return normalizeData;
  }

  /* @see IFormatReader#setMetadataCollected(boolean) */
  public void setMetadataCollected(boolean collect) {
    FormatTools.assertId(currentId, false, 1);
    if (currentId != null) {
      String s = "setMetadataCollected called with open file.";
      if (debug && debugLevel >= 2) trace(s);
      else System.err.println("Warning: " + s);
    }
    collectMetadata = collect;
  }

  /* @see IFormatReader#isMetadataCollected() */
  public boolean isMetadataCollected() {
    return collectMetadata;
  }

  /* @see IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return new String[] {currentId};
  }

  /* @see IFormatReader#getCurrentFile() */
  public String getCurrentFile() {
    return currentId == null ? "" : currentId;
  }

  /* @see IFormatReader#getIndex(int, int, int) */
  public int getIndex(int z, int c, int t) {
    FormatTools.assertId(currentId, true, 1);
    return FormatTools.getIndex(this, z, c, t);
  }

  /* @see IFormatReader#getZCTCoords(int) */
  public int[] getZCTCoords(int index) {
    FormatTools.assertId(currentId, true, 1);
    return FormatTools.getZCTCoords(this, index);
  }

  /* @see IFormatReader#getMetadataValue(String) */
  public Object getMetadataValue(String field) {
    FormatTools.assertId(currentId, true, 1);
    return getMeta(field);
  }

  /* @see IFormatReader#getMetadata() */
  public Hashtable getMetadata() {
    FormatTools.assertId(currentId, true, 1);
    return metadata;
  }

  /* @see IFormatReader#getCoreMetadata() */
  public CoreMetadata getCoreMetadata() {
    FormatTools.assertId(currentId, true, 1);
    return core;
  }

  /* @see IFormatReader#setMetadataFiltered(boolean) */
  public void setMetadataFiltered(boolean filter) {
    FormatTools.assertId(currentId, false, 1);
    if (currentId != null) {
      String s = "setMetadataFiltered called with open file.";
      if (debug && debugLevel >= 2) trace(s);
      else System.err.println("Warning: " + s);
    }
    filterMetadata = filter;
  }

  /* @see IFormatReader#isMetadataFiltered() */
  public boolean isMetadataFiltered() {
    return filterMetadata;
  }

  /* @see IFormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    FormatTools.assertId(currentId, false, 1);
    if (currentId != null) {
      String s = "setMetadataStore called with open file.";
      if (debug && debugLevel >= 2) trace(s);
      else System.err.println("Warning: " + s);
    }
    metadataStore = store;
  }

  /* @see IFormatReader#getMetadataStore() */
  public MetadataStore getMetadataStore() {
    return metadataStore;
  }

  /* @see IFormatReader#getMetadataStoreRoot() */
  public Object getMetadataStoreRoot() {
    FormatTools.assertId(currentId, true, 1);
    return getMetadataStore().getRoot();
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String, boolean) */
  public void setId(String id, boolean force)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) || force) initFile(id);
  }

  /* @see IFormatHandler#close() */
  public void close() throws IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  // -- Deprecated IFormatReader API methods --

  /** @deprecated Replaced by {@link #getImageCount()} */
  public int getImageCount(String id) throws FormatException, IOException {
    setId(id);
    return getImageCount();
  }

  /** @deprecated Replaced by {@link #isRGB()} */
  public boolean isRGB(String id) throws FormatException, IOException {
    return getRGBChannelCount(id) > 1;
  }

  /** @deprecated Replaced by {@link #getSizeX()} */
  public int getSizeX(String id) throws FormatException, IOException {
    setId(id);
    return core.sizeX[series];
  }

  /** @deprecated Replaced by {@link #getSizeY()} */
  public int getSizeY(String id) throws FormatException, IOException {
    setId(id);
    return core.sizeY[series];
  }

  /** @deprecated Replaced by {@link #getSizeZ()} */
  public int getSizeZ(String id) throws FormatException, IOException {
    setId(id);
    return core.sizeZ[series];
  }

  /** @deprecated Replaced by {@link #getSizeC()} */
  public int getSizeC(String id) throws FormatException, IOException {
    setId(id);
    return core.sizeC[series];
  }

  /** @deprecated Replaced by {@link #getSizeT()} */
  public int getSizeT(String id) throws FormatException, IOException {
    setId(id);
    return core.sizeT[series];
  }

  /** @deprecated Replaced by {@link #getPixelType()} */
  public int getPixelType(String id) throws FormatException, IOException {
    setId(id);
    return core.pixelType[series];
  }

  /** @deprecated Replaced by {@link #getEffectiveSizeC()} */
  public int getEffectiveSizeC(String id) throws FormatException, IOException {
    // NB: by definition, imageCount == effectiveSizeC * sizeZ * sizeT
    return getImageCount(id) / (getSizeZ(id) * getSizeT(id));
  }

  /** @deprecated Replaced by {@link #getRGBChannelCount()} */
  public int getRGBChannelCount(String id) throws FormatException, IOException {
    return getSizeC(id) / getEffectiveSizeC(id);
  }

  /** @deprecated Replaced by {@link #getChannelDimLengths()} */
  public int[] getChannelDimLengths(String id)
    throws FormatException, IOException
  {
    setId(id);
    if (core.cLengths[series] == null) {
      core.cLengths[series] = new int[] {core.sizeC[series]};
    }
    return core.cLengths[series];
  }

  /** @deprecated Replaced by {@link #getChannelDimTypes()} */
  public String[] getChannelDimTypes(String id)
    throws FormatException, IOException
  {
    setId(id);
    if (core.cTypes[series] == null) {
      core.cTypes[series] = new String[] {FormatTools.CHANNEL};
    }
    return core.cTypes[series];
  }

  /** @deprecated Replaced by {@link #getThumbSizeX()} */
  public int getThumbSizeX(String id) throws FormatException, IOException {
    int sx = getSizeX(id);
    int sy = getSizeY(id);
    return sx > sy ? THUMBNAIL_DIMENSION : sx * THUMBNAIL_DIMENSION / sy;
  }

  /** @deprecated Replaced by {@link #getThumbSizeY()} */
  public int getThumbSizeY(String id) throws FormatException, IOException {
    int sx = getSizeX(id);
    int sy = getSizeY(id);
    return sy > sx ? THUMBNAIL_DIMENSION : sy * THUMBNAIL_DIMENSION / sx;
  }

  /** @deprecated Replaced by {@link #isLittleEndian()} */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    setId(id);
    return isLittleEndian();
  }

  /** @deprecated Replaced by {@link #getDimensionOrder()} */
  public String getDimensionOrder(String id) throws FormatException, IOException
  {
    setId(id);
    return core.currentOrder[series];
  }

  /** @deprecated Replaced by {@link #isOrderCertain()} */
  public boolean isOrderCertain(String id) throws FormatException, IOException {
    setId(id);
    return core.orderCertain[series];
  }

  /** @deprecated Replaced by {@link #isInterleaved()} */
  public boolean isInterleaved(String id)
    throws FormatException, IOException
  {
    return isInterleaved(id, 0);
  }

  /** @deprecated Replaced by {@link #isInterleaved(int)} */
  public boolean isInterleaved(String id, int subC)
    throws FormatException, IOException
  {
    setId(id);
    return isInterleaved(subC);
  }

  /** @deprecated Replaced by {@link #openImage(int)} */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    setId(id);
    return openImage(no);
  }

  /** @deprecated Replaced by {@link #openBytes(int)} */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    setId(id);
    return openBytes(no);
  }

  /** @deprecated Replaced by {@link #openBytes(int, byte[])} */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(id, no);
  }

  /** @deprecated Replaced by {@link #openThumbImage(int)} */
  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.scale(openImage(id, no),
      getThumbSizeX(id), getThumbSizeY(id), false);
  }

  /** @deprecated Replaced by {@link #openThumbBytes(int)} */
  public byte[] openThumbBytes(String id, int no)
    throws FormatException, IOException
  {
    BufferedImage img = openThumbImage(id, no);
    byte[][] bytes = ImageTools.getBytes(img);
    if (bytes.length == 1) return bytes[0];
    byte[] rtn = new byte[getRGBChannelCount(id) * bytes[0].length];
    for (int i=0; i<getRGBChannelCount(id); i++) {
      System.arraycopy(bytes[i], 0, rtn, bytes[0].length * i, bytes[i].length);
    }
    return rtn;
  }

  /** @deprecated Replaced by {@link #getSeriesCount()} */
  public int getSeriesCount(String id) throws FormatException, IOException {
    setId(id);
    return 1;
  }

  /** @deprecated Replaced by {@link #setSeries(int)} */
  public void setSeries(String id, int no) throws FormatException, IOException {
    if (no < 0 || no >= getSeriesCount(id)) {
      throw new FormatException("Invalid series: " + no);
    }
    series = no;
  }

  /** @deprecated Replaced by {@link #getSeries()} */
  public int getSeries(String id) throws FormatException, IOException {
    setId(id);
    return series;
  }

  /** @deprecated Replaced by {@link #getUsedFiles()} */
  public String[] getUsedFiles(String id) throws FormatException, IOException {
    setId(id);
    return new String[] {id};
  }

  /** @deprecated Replaced by {@link #getIndex(int, int, int)} */
  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    setId(id);
    return FormatTools.getIndex(this, z, c, t);
  }

  /** @deprecated Replaced by {@link #getZCTCoords(int)} */
  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    setId(id);
    return FormatTools.getZCTCoords(this, index);
  }

  /** @deprecated Replaced by {@link #getMetadataValue(String)} */
  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    setId(id);
    return getMeta(field);
  }

  /** @deprecated Replaced by {@link #getMetadata()} */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    setId(id);
    return metadata;
  }

  /** @deprecated Replaced by {@link #getCoreMetadata()} */
  public CoreMetadata getCoreMetadata(String id)
    throws FormatException, IOException
  {
    setId(id);
    return core;
  }

  /** @deprecated Replaced by {@link #getMetadataStore()} */
  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    setId(id);
    return metadataStore;
  }

  /** @deprecated Replaced by {@link #getMetadataStoreRoot()} */
  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException
  {
    setId(id);
    return getMetadataStore().getRoot();
  }

}
