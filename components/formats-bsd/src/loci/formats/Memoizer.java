/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import loci.common.Constants;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * {@link ReaderWrapper} implementation which caches the state of the
 * delegate (including and other {@link ReaderWrapper} instances)
 * after setId has been called.
 */
public class Memoizer extends ReaderWrapper {

  public interface Deser {

    void loadStart(File memoFile) throws IOException;

    Integer loadVersion() throws IOException;

    String loadReleaseVersion() throws IOException;

    String loadRevision() throws IOException;

    IFormatReader loadReader() throws IOException, ClassNotFoundException;

    void loadStop() throws IOException;

    void saveStart(File tempFile) throws IOException;

    void saveVersion(Integer version) throws IOException;

    void saveReleaseVersion(String version) throws IOException;

    void saveRevision(String revision) throws IOException;

    void saveReader(IFormatReader reader) throws IOException;

    void saveStop() throws IOException;

    void close();
  }

  public static class KryoDeser implements Deser {

    final public Kryo kryo = new Kryo();
    {
      kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    FileInputStream fis;
    FileOutputStream fos;
    Input input;
    Output output;

    public void close() {
      loadStop();
      saveStop();
      kryo.reset();
    }

    public void loadStart(File memoFile) throws FileNotFoundException {
        fis = new FileInputStream(memoFile);
        input = new Input(fis);
    }

    public Integer loadVersion() {
        return kryo.readObject(input, Integer.class);
    }

    public String loadReleaseVersion() {
        return kryo.readObject(input, String.class);
    }

    public String loadRevision() {
        return kryo.readObject(input, String.class);
    }

    public IFormatReader loadReader() {
        Class<?> c = kryo.readObject(input, Class.class);
        return (IFormatReader) kryo.readObject(input, c);
    }

    public void loadStop() {
      if (input != null) {
        input.close();
        input = null;
      }
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          LOGGER.error("failed to close KryoDeser.fis", e);
        }
        fis = null;
      }
    }

    public void saveStart(File tempFile) throws FileNotFoundException {
      fos = new FileOutputStream(tempFile);
      output = new Output(fos);
    }

    public void saveVersion(Integer version) {
      kryo.writeObject(output, version);
    }

    public void saveReleaseVersion(String version) {
      kryo.writeObject(output, version);
    }

    public void saveRevision(String revision) {
      kryo.writeObject(output, revision);
    }

   public void saveReader(IFormatReader reader) {
     kryo.writeObject(output, reader.getClass());
     kryo.writeObject(output, reader);
   }

    public void saveStop() {
      if (output != null) {
        output.close();
        output = null;
      }
      if (fos != null) {
        try {
          fos.close();
          fos = null;
        } catch (IOException e) {
          LOGGER.error("failed to close KryoDeser.fis", e);
        }
      }
    }

  }

  /**
   * Helper implementation that can be used to implement {@link Deser}
   * classes for libraries working solely with byte arrays.
   */
  private static abstract class RandomAccessDeser implements Deser {

    RandomAccessInputStream loadStream;

    RandomAccessOutputStream saveStream;

    public void loadStart(File memoFile) throws IOException {
        this.loadStream = new RandomAccessInputStream(memoFile.getAbsolutePath());
    }

    public Integer loadVersion() throws IOException {
        return loadStream.readInt();
    }

    public String loadReleaseVersion() throws IOException {
        int length = loadStream.readInt();
        return loadStream.readString(length);
    }

    public String loadRevision() throws IOException {
        int length = loadStream.readInt();
        return loadStream.readString(length);
    }

    public IFormatReader loadReader() throws IOException, ClassNotFoundException {
      int cSize = loadStream.readInt();
      byte[] cArr = new byte[cSize];
      loadStream.readFully(cArr);
      // Assuming proper encoding.
      @SuppressWarnings("unchecked")
      Class<IFormatReader> c = (Class<IFormatReader>) Class.forName(
              new String(cArr, Constants.ENCODING));
      int rSize = loadStream.readInt();
      byte[] rArr = new byte[rSize];
      loadStream.readFully(rArr);
      return readerFromBytes(c, rArr);
    }

    protected abstract IFormatReader readerFromBytes(Class<IFormatReader> c,
      byte[] rArr) throws IOException, ClassNotFoundException;

    public void loadStop() throws IOException {
        if (loadStream != null) {
          loadStream.close();
          loadStream = null;
        }
    }

    public void saveStart(File tempFile) throws IOException {
      this.saveStream = new RandomAccessOutputStream(tempFile.getAbsolutePath());
    }

    public void saveVersion(Integer version) throws IOException {
      saveStream.writeInt(version);
    }

    public void saveReleaseVersion(String version) throws IOException {
      saveStream.writeInt(version.length());
      saveStream.writeBytes(version);
    }

    public void saveRevision(String revision) throws IOException {
      saveStream.writeInt(revision.length());
      saveStream.writeBytes(revision);
    }

    public void saveReader(IFormatReader reader) throws IOException {
      byte[] cArr = reader.getClass().getName().getBytes(Constants.ENCODING);
      saveStream.write(cArr.length);
      saveStream.write(cArr);
      byte[] rArr = bytesFromReader(reader);
      saveStream.write(rArr.length);
      saveStream.write(rArr);
    }

    protected abstract byte[] bytesFromReader(IFormatReader reader) throws IOException;

    public void saveStop() throws IOException {
      if (saveStream != null) {
        saveStream.close();
        saveStream = null;
      }
    }

  }

  // -- Constants --

  /**
   * Defines the file format. Bumping this number will invalidate all other
   * cached items. This should happen when the order and type of objects stored
   * in the memo file changes.
   */
  public static final Integer VERSION = 3;

  /**
   * Default value for {@link #minimumElapsed} if none is provided in the
   * constructor.
   */
  public static final long DEFAULT_MINIMUM_ELAPSED = 100;

  private static final Logger LOGGER =
    LoggerFactory.getLogger(Memoizer.class);

  // -- Fields --

  /**
   * Minimum number of milliseconds which must elapse during the call to
   * super.setId() before a memo file will be created.
   */
  private final long minimumElapsed;

  /**
   * Directory where all memoization files should be created. If this value is
   * non-null, then all files will be created under it rather than sitting
   * beside the setId file.
   */
  private final File directory;

  protected transient Deser ser;

  private transient OMEXMLService service;

  private Location realFile;

  private File memoFile;

  private File tempFile;

  private boolean skipLoad = false;

  private boolean skipSave = false;

  /**
   * Whether to invalidate the memo file based upon mismatched major/minor
   * version numbers. By default, the Git commit hash is used to invalidate
   * the memo file.
   */
  private boolean versionChecking = false;

  /**
   * Whether the {@link #reader} instance currently active was loaded from
   * the memo file during {@link #setId(String)}.
   */
  private boolean loadedFromMemo = false;

  /**
   * Whether the {@link #reader} instance was saved to a memo file on
   * {@link #setId(String)}.
   */
  private boolean savedToMemo = false;

  /**
   * {@link MetadataStore} set by the caller. This value will be held locally
   * and <em>not</em> set on the {@link #reader} delegate until the execution
   * of {@link #setId(String)}. If no value has been set by the caller, then
   * no special actions are taken during {@link #setId(String)}. If a value
   * is set, however, we must be careful with attempting to serialize it
   *
   * @see {@link #handleMetadataStore()}
   */
  private MetadataStore userMetadataStore = null;

  /**
   * {@link MetadataStore} created internally.
   *
   * @see {@link #handleMetadataStore()}
   */
  private MetadataStore replacementMetadataStore = null;

  // -- Constructors --

  /** Constructs a memoizer around a new image reader. */
  public Memoizer() {
    this(DEFAULT_MINIMUM_ELAPSED);
  }

  /** Constructs a memoizer around a new image reader. */
  public Memoizer(long minimumElapsed) {
    this(minimumElapsed, null);
  }

  public Memoizer(long minimumElapsed, File directory) {
    super();
    this.minimumElapsed = minimumElapsed;
    this.directory = directory;
  }

  /** Constructs a memoizer around the given reader. */
  public Memoizer(IFormatReader r) {
    this(r, DEFAULT_MINIMUM_ELAPSED);
  }

  /** Constructs a memoizer around the given reader. */
  public Memoizer(IFormatReader r, long minimumElapsed) {
    this(r, minimumElapsed, null);
  }

  public Memoizer(IFormatReader r, long minimumElapsed, File directory) {
    super(r);
    this.minimumElapsed = minimumElapsed;
    this.directory = directory;
  }

  public boolean isLoadedFromMemo() {
    return loadedFromMemo;
  }

  public boolean isSavedToMemo() {
    return savedToMemo;
  }

  /**
   * Returns whether or not version checking is done based upon major/minor
   * version numbers.
   */
  public boolean isVersionChecking() {
    return versionChecking;
  }

  /**
   * Returns true if the version of the memo file as returned by
   * {@link Deser#loadReleaseVersion()} and {@link Deser#loadRevision()}
   * do not match the current version as specified by {@link FormatTools#VERSION}
   * and {@link FormatTools#VCS_REVISION}, respectively.
   */
  public boolean versionMismatch() throws IOException {

      final String releaseVersion = ser.loadReleaseVersion();
      final String revision = ser.loadRevision();

      if (!isVersionChecking()) {
        return false;
      }

      String minor = releaseVersion;
      int firstDot = minor.indexOf(".");
      if (firstDot >= 0) {
        int secondDot = minor.indexOf(".", firstDot + 1);
        if (secondDot >= 0) {
          minor = minor.substring(0, secondDot);
        }
      }

      String currentMinor = FormatTools.VERSION.substring(0,
        FormatTools.VERSION.indexOf(".", FormatTools.VERSION.indexOf(".") + 1));
      if (!currentMinor.equals(minor)) {
        LOGGER.info("Different release version: {} not {}",
          releaseVersion, FormatTools.VERSION);
        return true;
      }

      // REVISION NUMBER
      if (!versionChecking && !FormatTools.VCS_REVISION.equals(revision)) {
        LOGGER.info("Different Git version: {} not {}",
          revision, FormatTools.VCS_REVISION);
        return true;
      }

      return false;
  }

  /**
   * Set whether version checking is done based upon major/minor version
   * numbers.
   * If 'true' is passed in, then a mismatch between the major/minor version
   * of the calling code (e.g. 4.4) and the major/minor version saved in the
   * memo file (e.g. 5.0) will result in the memo file being invalidated.
   * By default, a mismatch in the Git commit hashes will invalidate the memo
   * file; this method allows for less strict version checking.
   */
  public void setVersionChecking(boolean version) {
    this.versionChecking = version;
  }

  protected void cleanup() {
    if (ser != null) {
      ser.close();
      ser = null;
    }
  }

  public void close() throws IOException {
    try {
      cleanup();
    } finally {
      super.close();
    }
  }

  public void close(boolean fileOnly) throws IOException {
    try {
      cleanup();
    } finally {
      super.close(fileOnly);
    }
  }

  // -- ReaderWrapper API methods --

  public void setId(String id) throws FormatException, IOException {
    StopWatch sw = stopWatch();
    try {
      realFile = new Location(id);
      memoFile = getMemoFile(id);

      if (memoFile == null) {
        // Memoization disabled.
        if (userMetadataStore != null) {
          reader.setMetadataStore(userMetadataStore);
        }
        super.setId(id); // EARLY EXIT
        return;
      }

      IFormatReader memo = loadMemo(); // Should never throw.

      loadedFromMemo = false;
      savedToMemo = false;

      if (memo == null) {
        OMEXMLService service = getService();
        super.setMetadataStore(service.createOMEXMLMetadata());
        long start = System.currentTimeMillis();
        super.setId(id);
        long elapsed = System.currentTimeMillis() - start;
        handleMetadataStore(null); // Between setId and saveMemo
        if (elapsed < minimumElapsed) {
          LOGGER.debug("skipping save memo. elapsed millis: {}", elapsed);
          return; // EARLY EXIT!
        }
        savedToMemo = saveMemo(); // Should never throw.
      } else {
        // loadMemo has already called handleMetadataStore with non-null
        loadedFromMemo = true;
        reader = memo;
      }
    } catch (ServiceException e) {
      LOGGER.error("Could not create OMEXMLMetadata", e);
    } finally {
      sw.stop("loci.formats.Memoizer.setId");
    }
  }

  @Override
  public void setMetadataStore(MetadataStore store) {
    this.userMetadataStore = store;
  }

  @Override
  public MetadataStore getMetadataStore() {
    if (this.userMetadataStore != null) {
      return this.userMetadataStore;
    }
    return reader.getMetadataStore();
  }

  //-- Helper methods --

  /**
   * Returns a configured {@link Kryo} instance. This method can be modified
   * by consumers. The returned instance is not thread-safe.
   *
   * @return a non-null {@link Kryo} instance.
   */
  protected Deser getDeser() {
    if (ser == null) {
      ser = new KryoDeser();
    }
    return ser;
  }

  // Copied from OMETiffReader.
  protected OMEXMLService getService() throws MissingLibraryException {
    if (service == null) {
      try {
        ServiceFactory factory = new ServiceFactory();
        service = factory.getInstance(OMEXMLService.class);
      } catch (DependencyException de) {
        throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
      }
    }
    return service;
  }
  protected Slf4JStopWatch stopWatch() {
      return new Slf4JStopWatch(LOGGER, Slf4JStopWatch.DEBUG_LEVEL);
  }

  /**
   * Constructs a {@link File} object from setId string. This method can be
   * modified by consumers, but then existing memo files will not be found.
   *
   * @param id the path passed to setId
   * @return a filename with
   */
  public File getMemoFile(String id) {
    File f = null;
    if (directory == null) {
        // Disabling memoization unless specific directory is provided.
        // This prevents random cache files from being unknowingly written.
        LOGGER.debug("skipping memo: no directory given");
        return null;
    } else {
      if (!directory.exists() || !directory.canWrite()) {
        LOGGER.warn("skipping memo: directory not writeable - {}", directory);
        return null;
      }

      // this serves to strip off the drive letter on Windows
      // since we're using the absolute path, 'id' will either start with
      // File.separator (as on UNIX), or a drive letter (as on Windows)
      id = new File(id).getAbsolutePath();
      id = id.substring(id.indexOf(File.separator) + 1);

      f = new File(directory, id);
      f.getParentFile().mkdirs();
    }
    String p = f.getParent();
    String n = f.getName();
    return new File(p, "." + n + ".bfmemo");
  }

  public IFormatReader loadMemo() throws IOException, FormatException {

    if (skipLoad) {
      LOGGER.trace("skip load");
      return null;
    }

    if (!memoFile.exists()) {
      LOGGER.trace("Memo file doesn't exist: {}", memoFile);
      return null;
    }

    if(!memoFile.canRead()) {
      LOGGER.trace("Can't read memo file: {}", memoFile);
      return null;
    }

    long memoLast = memoFile.lastModified();
    long realLast = realFile.lastModified();
    if (memoLast < realLast) {
      LOGGER.debug("memo(lastModified={}) older than real(lastModified={})",
        memoLast, realLast);
      return null;
    }

    final Deser ser = getDeser();
    final StopWatch sw = stopWatch();
    IFormatReader copy = null;
    ser.loadStart(memoFile);
    try {

      // VERSION
      Integer version = ser.loadVersion();
      if (!VERSION.equals(version)) {
        LOGGER.info("Old version of memo file: {} not {}", version, VERSION);
        return null;
      }

      // RELEASE VERSION NUMBER
       if (versionMismatch()) {
         // Logging done in versionMismatch
         return null;
       }

      // CLASS & COPY
      try {
        copy = ser.loadReader();
      } catch (ClassNotFoundException e) {
        LOGGER.warn("unknown reader type: {}", e);
        return null;
      }

      boolean equal = false;
      try {
        equal = FormatTools.equalReaders(reader, copy);
      } catch (RuntimeException rt) {
        copy.close();
        throw rt;
      } catch (Error err) {
        copy.close();
        throw err;
      }

      if (!equal) {
        copy.close();
        return null;
      }

      copy = handleMetadataStore(copy);
      if (copy == null) {
          LOGGER.debug("metadata store invalidated cache: {}", memoFile);
      }

      // TODO:
      // Check flags
      // DataV1 class?
      // Handle exceptions on read/write. possibly deleting.
      LOGGER.debug("loaded memo file: {} ({} bytes)",
        memoFile, memoFile.length());
      return copy;
    } catch (KryoException e) {
      memoFile.delete();
      LOGGER.warn("deleted invalid memo file: {}", memoFile, e);
      return null;
    } finally {
      ser.loadStop();
      sw.stop("loci.formats.Memoizer.loadMemo");
    }
  }

  public boolean saveMemo() {

    if (skipSave) {
      LOGGER.trace("skip memo");
      return false;
    }

    final Deser ser = getDeser();
    final StopWatch sw = stopWatch();
    boolean rv = true;
    try {
      // Create temporary location for output
      // Note: can't rename tempfile until resources are closed.
      tempFile = File.createTempFile(
        memoFile.getName(), "", memoFile.getParentFile());

      ser.saveStart(tempFile);

      // Save to temporary location.
      ser.saveVersion(VERSION);
      ser.saveReleaseVersion(FormatTools.VERSION);
      ser.saveRevision(FormatTools.VCS_REVISION);
      ser.saveReader(reader);
      ser.saveStop();
      LOGGER.debug("saved to temp file: {}", tempFile);

    } catch (Throwable t) {

      // Any exception should be ignored, and false returned.
      LOGGER.warn(String.format("failed to save memo file: %s", memoFile), t);
      rv = false;

    } finally {

      // Close the output stream quietly regardless.
      try {
        ser.saveStop();
        sw.stop("loci.formats.Memoizer.saveMemo");
      } catch (Throwable t) {
        LOGGER.error("output close failed", t);
      }

      // Rename temporary file if successful.
      // Any failures will have to be ignored.
      // Note: renaming the tempfile with open
      // resources can lead to segfaults
      if (rv) {
        if (!tempFile.renameTo(memoFile)) {
          LOGGER.error("temp file rename returned false: {}", tempFile);
        } else {
          LOGGER.debug("saved memo file: {} ({} bytes)",
            memoFile, memoFile.length());
        }
      }

      // Delete the tempFile quietly.
      try {
        if (tempFile != null && tempFile.exists()) {
          tempFile.delete();
          tempFile = null;
        }
      } catch (Throwable t) {
        LOGGER.error("temp file deletion faled", t);
      }
    }
    return rv;
  }


  /**
   * Return the {@link IFormatReader} instance that is passed in or null if
   * it has been invalidated, which will include the instance being closed.
   *
   * <ul>
   *  <li><em>Serialization:</em> If an unknown {@link MetadataStore}
   *  implementation is passed in when no memo file exists, then a replacement
   *  {@link MetadataStore} will be created and set on the {@link #reader}
   *  delegate before calling {@link ReaderWrapper#setId(String)}. This stack
   *  will then be serialized, before any values are copied into
   *  {@link #userMetadataStore}.
   *  </li>
   *  <li><em>Deserialization<em>: If an unknown {@link MetadataStore}
   *  implementation is set before calling {@link #setId(String)} then ...
   *  </li>
   * </ul>
   */
  protected IFormatReader handleMetadataStore(IFormatReader memo) throws MissingLibraryException {

    // Then nothing has been requested of us
    // and we can exit safely.
    if (userMetadataStore == null) {
      return memo; // EARLY EXIT. Possibly null.
    }

    // If we've been passed a memo object, then it's just been loaded.
    final boolean onLoad = (memo != null);

    // TODO: What to do if the contained store is a Dummy?
    // TODO: Which stores should we handle regularly?

    if (onLoad) {
      MetadataStore filledStore = memo.getMetadataStore();
      // Return value is important.
      if (filledStore == null) {
          // TODO: what now?
      } else if (!(filledStore instanceof MetadataRetrieve)) {
          LOGGER.error("Found non-MetadataRetrieve: {}" + filledStore.getClass());
      } else {
        OMEXMLService service = getService();
        service.convertMetadata((MetadataRetrieve) filledStore, userMetadataStore);
      }
    } else {
      // on save; we've just called super.setId()
      // Then pull out the reader and replace it.
      // Return value is unimportant.
      MetadataStore filledStore = reader.getMetadataStore();
      if (reader.getMetadataStore() == null) {
        // TODO: what now?
        LOGGER.error("Found null-MetadataRetrieve: {}" + filledStore.getClass());
      } else if (!(filledStore instanceof MetadataRetrieve)) {
        LOGGER.error("Found non-MetadataRetrieve: {}" + filledStore.getClass());
      } else {
        OMEXMLService service = getService();
        service.convertMetadata((MetadataRetrieve) filledStore, userMetadataStore);
      }

    }
    return memo;
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0 || args.length > 2) {
      System.err.println("Usage: memoizer file [tmpdir]");
      System.exit(2);
    }

    File tmp = new File(System.getProperty("java.io.tmpdir"));
    if (args.length == 2) {
      tmp = new File(args[1]);
    }

    System.out.println("First load of " + args[0]);
    load(args[0], tmp, true); // initial
    System.out.println("Second load of " + args[0]);
    load(args[0], tmp, false); // reload
  }

  private static void load(String id, File tmp, boolean delete) throws Exception {
    Memoizer m = new Memoizer(0L, tmp);

    File memo = m.getMemoFile(id);
    if (delete && memo != null && memo.exists()) {
        System.out.println("Deleting " + memo);
        memo.delete();
    }

    m.setVersionChecking(false);
    try {
      m.setId(id);
      m.openBytes(0);
      IFormatReader r = m.getReader();
      r = ((ImageReader) r).getReader();
      System.out.println(r);
    } finally {
      m.close();
    }
  }

}
