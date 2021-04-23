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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.DataTools;
import loci.common.Location;
import loci.formats.AxisGuesser;
import loci.formats.ClassList;
import loci.formats.CoreMetadata;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ReaderWrapper;
import loci.formats.WrappedReader;
import loci.formats.meta.MetadataStore;

/**
 *
 */
public class FilePatternReader extends WrappedReader {

  // -- Fields --

  private FileStitcher helper;

  // -- Constructor --

  /** Constructs a new pattern reader. */
  public FilePatternReader() {
    super("File pattern", new String[] {"pattern"});

    ClassList<IFormatReader> classes = ImageReader.getDefaultReaderClasses();
    Class<? extends IFormatReader>[] classArray = classes.getClasses();
    ClassList<IFormatReader> newClasses =
      new ClassList<IFormatReader>(IFormatReader.class);
    for (Class<? extends IFormatReader> c : classArray) {
      if (!c.equals(FilePatternReader.class)) {
        newClasses.addClass(c);
      }
    }
    helper = new FileStitcher(new ImageReader(newClasses));

    suffixSufficient = true;
  }

  // -- WrappedReader methods --

  protected ReaderWrapper getHelper() {
    return helper;
  }

  // -- FormatReader methods that are not overridden by WrappedReader --

  @Override
  public void setGroupFiles(boolean group) {
    getHelper().setGroupFiles(group);
  }

  @Override
  public boolean isGroupFiles() {
    return getHelper().isGroupFiles();
  }

  @Override
  public void setNormalized(boolean normalize) {
    getHelper().setNormalized(normalize);
  }

  @Override
  public boolean isNormalized() { return getHelper().isNormalized(); }

  @Override
  public void setOriginalMetadataPopulated(boolean populate) {
    getHelper().setOriginalMetadataPopulated(populate);
  }

  @Override
  public boolean isOriginalMetadataPopulated() {
    return getHelper().isOriginalMetadataPopulated();
  }

  @Override
  public void setMetadataFiltered(boolean filter) {
    getHelper().setMetadataFiltered(filter);
  }

  @Override
  public boolean isMetadataFiltered() { return getHelper().isMetadataFiltered(); }

  @Override
  public void setMetadataStore(MetadataStore store) {
    getHelper().setMetadataStore(store);
  }

  @Override
  public MetadataStore getMetadataStore() {
    return getHelper().getMetadataStore();
  }

  @Override
  public boolean hasFlattenedResolutions() {
    return getHelper().hasFlattenedResolutions();
  }

  @Override
  public void setFlattenedResolutions(boolean flattened) {
    getHelper().setFlattenedResolutions(flattened);
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
    if (noPixels) {
      return new String[] {currentId};
    }
    String[] helperFiles = helper.getUsedFiles(noPixels);
    String[] allFiles = new String[helperFiles.length + 1];
    allFiles[0] = currentId;
    System.arraycopy(helperFiles, 0, allFiles, 1, helperFiles.length);
    return allFiles;
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
    // the file should just contain a single line with the relative or
    // absolute file pattern

    currentId = new Location(id).getAbsolutePath();
    String pattern = DataTools.readFile(id).trim();
    String dir = new Location(id).getAbsoluteFile().getParent();
    if (new Location(pattern).getParent() == null) {
      pattern = dir + File.separator + pattern;
    }

    helper.setUsingPatternIds(true);
    helper.setCanChangePattern(false);
    helper.setId(pattern);
    core = helper.getCoreMetadataList();

    if (getEffectiveSizeC() > 1) {
      MetadataStore store = makeFilterMetadata();
      String[][] elements = helper.getFilePattern().getElements();
      int[] axisTypes = helper.getAxisTypes();
      int nextChannel = 0;
      for (int i=0; i<axisTypes.length; i++) {
        if (axisTypes[i] == AxisGuesser.C_AXIS) {
          for (int c=0; c<elements[i].length; c++) {
            if (nextChannel < getEffectiveSizeC()) {
              store.setChannelName(elements[i][c], 0, nextChannel++);
            }
          }
        }
      }
    }
  }

}
