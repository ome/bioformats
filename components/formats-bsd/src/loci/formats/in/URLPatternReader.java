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
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * A Pattern reader designed for use with opaque remote resources that should not be permanently fetched
 * The urlpattern file represents all images in the fileset, individual files are not available to clients
 */
public class URLPatternReader extends FilePatternReader {

  // -- Fields --

  protected final static Pattern IS_ABSOLUTE_URL = Pattern.compile("([\\p{Alnum}\\+]+)://[^/].*");

  // -- Constructor --

  /** Constructs a new pattern reader. */
  public URLPatternReader() {
    super("URL File pattern", new String[]{"urlpattern"}, false);
    ClassList<IFormatReader> classes = ImageReader.getDefaultReaderClasses();
    Class<? extends IFormatReader>[] classArray = classes.getClasses();
    ClassList<IFormatReader> newClasses = new ClassList<IFormatReader>(IFormatReader.class);
    for (Class<? extends IFormatReader> c : classArray) {
      if (!c.equals(FilePatternReader.class) && !c.equals(URLPatternReader.class)) {
        newClasses.addClass(c);
      }
    }
    initHelper(newClasses);
  }

  // -- IFormatReader methods --

  @Override
  public String[] getUsedFiles(boolean noPixels) {
    return new String[] {currentId};
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
    if (input.length > 2) {

      throw new FormatException("Expected maximum of two lines:" + input);
    }

    if (input.length > 1) {
      String reader = input[1];
      LOGGER.trace("urlpattern reader: {}", reader);
      ClassList readerClasses = new ClassList<>(IFormatReader.class);
      readerClasses.parseLine(reader);
      initHelper(readerClasses);
    }

    helper.setUsingPatternIds(true);
    helper.setCanChangePattern(false);
    helper.setId(pattern);
    core = helper.getCoreMetadataList();
  }

}
