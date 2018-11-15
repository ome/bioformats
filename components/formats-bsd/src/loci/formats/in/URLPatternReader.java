/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
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

package loci.formats.in;

import loci.common.DataTools;
import loci.common.Location;
import loci.formats.ClassList;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ReaderWrapper;
import loci.formats.WrappedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A reader designed for use with opaque remote resources that should not be permanently fetched
 * The urlpattern file represents the fileset, individual files are not available to clients
 */
public class URLPatternReader extends WrappedReader {

  // -- Fields --
  private static final Logger LOGGER = LoggerFactory.getLogger(URLPatternReader.class);
  private ReaderWrapper helper;

  protected final static Pattern IS_ABSOLUTE_URL = Pattern.compile("([\\p{Alnum}\\+]+)://[^/].*");

  // -- Constructor --

  /** Constructs a new pattern reader. */
  public URLPatternReader() {
    super("URL File pattern", new String[]{"urlpattern"});
    this.initHelper(new ClassList<>(IFormatReader.class));
  }

  /** Initialise the helper with a list of readers */
  protected void initHelper(ClassList<IFormatReader> newClasses) {
    class RemoteReader extends ReaderWrapper {
      public RemoteReader(IFormatReader r) {
        super(r);
      }
    };

    for (Class<? extends IFormatReader> c : newClasses.getClasses()) {
      LOGGER.error("urlpattern helper: {}", c);
    }
    helper = new RemoteReader(new ImageReader(newClasses));
  }

  // -- WrappedReader methods --

  protected ReaderWrapper getHelper() {
    return helper;
  }

  // -- IFormatReader methods --

  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if (getCurrentFile() == null) {
      return null;
    }
    return helper.get8BitLookupTable();
  }

  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if (getCurrentFile() == null) {
      return null;
    }
    return helper.get16BitLookupTable();
  }

  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (noPixels) {
      return new String[] {currentId};
    }
    String[] helperFiles = helper.getSeriesUsedFiles(noPixels);
    String[] allFiles = new String[helperFiles.length + 1];
    allFiles[0] = currentId;
    System.arraycopy(helperFiles, 0, allFiles, 1, helperFiles.length);
    return allFiles;
  }

  @Override
  public String[] getUsedFiles(boolean noPixels) {
    return new String[] {currentId};
  }

  @Override
  public List<CoreMetadata> getCoreMetadataList() {
    // Only used for determining the object type.
    List<CoreMetadata> oldcore = helper.getCoreMetadataList();
    List<CoreMetadata> newcore = new ArrayList<CoreMetadata>();

    for (int s=0; s<oldcore.size(); s++) {
      CoreMetadata newMeta = oldcore.get(s).clone(this, s);
      newMeta.resolutionCount = oldcore.get(s).resolutionCount;
      newcore.add(newMeta);
    }

    return newcore;
  }

  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  @Override
  public boolean hasCompanionFiles() {
    return true;
  }

  // -- Internal FormatReader methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    // read the pattern from the file
    // the file should just contain:
    // - a single line with the absolute URL file pattern
    // - optionally the reader name on the second line

    currentId = new Location(id).getAbsolutePath();
    LOGGER.trace("urlpattern input file: {}", currentId);
    String[] input = DataTools.readFile(id).trim().split("\\R");
    String pattern = input[0];
    LOGGER.trace("urlpattern pattern: {}", pattern);

    if (!IS_ABSOLUTE_URL.matcher(pattern).matches()) {
      throw new FormatException("Expected absolute URL:" + pattern);
    }
//    if (input.length > 2) {
//      throw new FormatException("Expected maximum of two lines:" + input);
//    }

    ClassList newClasses = new ClassList<>(IFormatReader.class);
    if (input.length > 1) {
      for (int i = 1; i < input.length; ++i) {
        String reader = input[1];
        LOGGER.trace("urlpattern reader: {}", reader);
        newClasses.parseLine(reader);
      }
    } else {
      ClassList<IFormatReader> classes = ImageReader.getDefaultReaderClasses();
      for (Class<? extends IFormatReader> c : classes.getClasses()) {
        if(!WrappedReader.class.isAssignableFrom(c)) {
          newClasses.addClass(c);
        }
      }
    }
    initHelper(newClasses);

    helper.setId(pattern);
    core = helper.getCoreMetadataList();
  }

}
