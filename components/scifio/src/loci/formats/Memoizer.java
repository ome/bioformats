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

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * {@link ReaderWrapper} implementation which caches the state of the
 * delegate (including and other {@link ReaderWrapper} instances)
 * after setId has been called.
 */
public class Memoizer extends ReaderWrapper {

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

  private File realFile;

  private File memoFile;

  private boolean skipLoad = false;

  private boolean skipSave = false;

  // -- Constructors --

  /** Constructs a memoizer around a new image reader. */
  public Memoizer() { super(); }

  /** Constructs a memoizer around the given reader. */
  public Memoizer(IFormatReader r) { super(r); }

  // -- ReaderWrapper API methods --

  public void setId(String id) throws FormatException, IOException {
    StopWatch sw = stopWatch();
    try {
      realFile = new File(id); // TODO: Can likely fail.
      memoFile = getMemoFile(id);
      IFormatReader memo = loadMemo();

      if (memo == null) {
        super.setId(id);
        saveMemo();
      } else {
        reader = memo;
      }
    } finally {
        sw.stop("loci.formats.Memoizer.setId");
    }
  }

  //-- Helper methods --

  /**
   * Returns a configured {@link Kryo} instance. This method can be modified
   * by consumers. The returned instance is not thread-safe.
   *
   * @return a non-null {@link Kryo} instance.
   */
  protected Kryo getKryo() {
    Kryo kryo = new Kryo() {
        public void writeClassAndObject(Output o, Object obj) {
            LOGGER.warn("writeClassAndObject: {}", obj);
            super.writeClassAndObject(o, obj);
        }
    };
    kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    return kryo;
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

  public IFormatReader loadMemo() throws FileNotFoundException {

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

    if (memoFile.lastModified() < realFile.lastModified()) {
      LOGGER.debug("memo(lastModified={}) older than real(lastModified={})",
        memoFile.lastModified(),
        realFile.lastModified());
      return null;
    }

    StopWatch sw = stopWatch();
    IFormatReader copy = null;
    Input input = new Input(new FileInputStream(memoFile));
    try {
      Kryo kryo = getKryo();

      // VERSION
      Integer version = kryo.readObject(input, Integer.class);
      if (!VERSION.equals(version)) {
        LOGGER.info("Old version of memo file: {} not {}", version, VERSION);
        return null;
      }

      // CLASS & COPY
      Class<?> c = kryo.readObject(input, Class.class);
      copy = (IFormatReader) getKryo().readObject(input, c);

      // TODO:
      // Check flags
      // Check wrappers
      // DataV1 class?
      // Handle exceptions on read/write. possibly deleting.
      LOGGER.debug("loaded memo file: {} ({} bytes)",
        memoFile, memoFile.length());
      return copy;
    } finally {
      input.close();
      sw.stop("loci.formats.Memoizer.loadMemo");
    }
  }

  public void saveMemo() throws FileNotFoundException {

    if (skipSave) {
      LOGGER.trace("skip memo");
      return;
    }

    StopWatch sw = stopWatch();
    Output output = new Output(new FileOutputStream(memoFile));
    try {
      Kryo kryo = getKryo();
      kryo.writeObject(output, VERSION);
      kryo.writeObject(output, reader.getClass());
      kryo.writeObject(output, reader);
    } finally {
      output.close();
      sw.stop("loci.formats.Memoizer.saveMemo");
      LOGGER.debug("saved memo file: {} ({} bytes)",
        memoFile, memoFile.length());
    }
  }
}
