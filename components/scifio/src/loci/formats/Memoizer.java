/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy;
import com.romix.quickser.Serialization;

/**
 * {@link ReaderWrapper} implementation which caches the state of the
 * delegate (including and other {@link ReaderWrapper} instances)
 * after setId has been called.
 */
public class Memoizer extends ReaderWrapper {

  public interface Deser {

    void loadStart(File memoFile) throws IOException;

    Integer loadVersion() throws IOException;

    IFormatReader loadReader() throws IOException, ClassNotFoundException;

    void loadStop() throws IOException;

    void saveStart(File tempFile) throws IOException;

    void saveVersion(Integer version) throws IOException;

    void saveReader(IFormatReader reader) throws IOException;

    void saveStop() throws IOException;

  }

  private static class KryoDeser implements Deser {

    final Kryo kryo = new Kryo();
    {
      kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    Input input;
    Output output;

    public void loadStart(File memoFile) throws FileNotFoundException {
        input = new Input(new FileInputStream(memoFile));
    }

    public Integer loadVersion() {
        return kryo.readObject(input, Integer.class);
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
    }

    public void saveStart(File tempFile) throws FileNotFoundException {
      output = new Output(new FileOutputStream(tempFile));
    }

    public void saveVersion(Integer vERSION) {
      kryo.writeObject(output, VERSION);
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
    }

  }

  private static class QuickserDeser extends RandomAccessDeser {

    final Serialization serialization = new Serialization();

    @Override
    protected IFormatReader readerFromBytes(byte[] rArr) throws IOException, ClassNotFoundException {
      return (IFormatReader) serialization.deserialize(rArr);
    }

    @Override
    protected byte[] bytesFromReader(IFormatReader reader) throws IOException {
        return serialization.serialize(reader);
    }

  }

  private static abstract class RandomAccessDeser implements Deser {

    RandomAccessInputStream loadStream;

    RandomAccessOutputStream saveStream;

    public void loadStart(File memoFile) throws IOException {
        this.loadStream = new RandomAccessInputStream(memoFile.getAbsolutePath());
    }

    public Integer loadVersion() throws IOException {
        return loadStream.readInt();
    }

    public IFormatReader loadReader() throws IOException, ClassNotFoundException {
        int rSize = loadStream.readInt();
        byte[] rArr = new byte[rSize];
        loadStream.readFully(rArr);
        return readerFromBytes(rArr);
    }

    protected abstract IFormatReader readerFromBytes(byte[] rArr) throws IOException, ClassNotFoundException;

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

    public void saveReader(IFormatReader reader) throws IOException {
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
  public static Integer VERSION = 1;

  private static final Logger LOGGER =
    LoggerFactory.getLogger(Memoizer.class);

  // -- Fields --

  private transient Deser ser;

  private transient OMEXMLService service;

  private File realFile;

  private File memoFile;

  private File tempFile;

  private boolean skipLoad = false;

  private boolean skipSave = false;

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
  public Memoizer() { super(); }

  /** Constructs a memoizer around the given reader. */
  public Memoizer(IFormatReader r) { super(r); }

  public boolean isLoadedFromMemo() {
    return loadedFromMemo;
  }

  public boolean isSavedToMemo() {
    return savedToMemo;
  }

  // -- ReaderWrapper API methods --

  public void setId(String id) throws FormatException, IOException {
    StopWatch sw = stopWatch();
    try {
      realFile = new File(id); // TODO: Can likely fail.
      memoFile = getMemoFile(id);
      IFormatReader memo = loadMemo(); // Should never throw.

      if (memo == null) {
        OMEXMLService service = getService();
        super.setMetadataStore(service.createOMEXMLMetadata());
        super.setId(id);
        handleMetadataStore(null); // Between setId and saveMemo
        loadedFromMemo = false;
        savedToMemo = saveMemo(); // Should never throw.
      } else {
        // loadMemo has already called handleMetadataStore with non-null
        loadedFromMemo = true;
        reader = memo;
      }
    } catch (ServiceException e) {
      LOGGER.debug("Could not create OMEXMLMetadata", e);
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
      ser = new QuickserDeser();
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
    File f = new File(id);
    String p = f.getParent();
    String n = f.getName();
    return new File(p, "." + n + ".bfmemo");
  }

  public IFormatReader loadMemo() throws IOException, MissingLibraryException {

    if (skipLoad) {
      LOGGER.trace("skip load");
      return null;
    }

    if (memoFile == null) {
      LOGGER.warn("No memo file set: {}", memoFile);
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

      // CLASS & COPY
      try {
        copy = ser.loadReader();
      } catch (ClassNotFoundException e) {
        LOGGER.debug("unknown reader type: {}", e);
        return null;
      }

      if (!FormatTools.equalReaders(reader, copy)) {
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
    try {

      // Create temporary location for output
      tempFile = File.createTempFile(
        memoFile.getName(), "", memoFile.getParentFile());

      ser.saveStart(tempFile);

      // Save to temporary location.
      ser.saveVersion(VERSION);
      ser.saveReader(reader);
      ser.saveStop();
      LOGGER.debug("saved to temp file: {}", tempFile);

      // Rename temporary file. Any failures will have to be ignored.
      if (!tempFile.renameTo(memoFile)) {
        LOGGER.debug("temp file rename returned false: {}", tempFile);
      }

      LOGGER.debug("saved memo file: {} ({} bytes)",
        memoFile, memoFile.length());
      return true;

    } catch (Throwable t) {

      // Any exception should be ignored, and false returned.
      LOGGER.debug(String.format("failed to save memo file: %s", memoFile), t);
      return false;

    } finally {

      // Close the output stream quietly.
      try {
        ser.saveStop();
        sw.stop("loci.formats.Memoizer.saveMemo");
      } catch (Throwable t) {
        LOGGER.debug("output close failed", t);
      }

      // Delete the tempFile quietly.
      try {
        if (tempFile != null) {
          tempFile.delete();
          tempFile = null;
        }
      } catch (Throwable t) {
        LOGGER.debug("temp file deletion faled", t);   
      }
    }
  }


  /**
:
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


}