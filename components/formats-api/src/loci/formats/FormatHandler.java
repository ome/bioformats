/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.formats.in.DefaultMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;

/**
 * Abstract superclass of all biological file format readers and writers.
 */
public abstract class FormatHandler implements IFormatHandler {

  // -- Constants --

  protected static final Logger LOGGER =
    LoggerFactory.getLogger(FormatHandler.class);

  /** Suffixes for supported compression types. */
  public static final String[] COMPRESSION_SUFFIXES = {"bz2", "gz"};

  // -- Fields --

  /** Name of this file format. */
  protected String format;

  /** Valid suffixes for this file format. */
  protected String[] suffixes;

  /** Name of current file. */
  protected String currentId;
  
  /** Metadata parsing options. */
  protected MetadataOptions metadataOptions = new DefaultMetadataOptions();

  // -- Constructors --

  /** Constructs a format handler with the given name and default suffix. */
  public FormatHandler(String format, String suffix) {
    this(format, suffix == null ? null : new String[] {suffix});
  }

  /** Constructs a format handler with the given name and default suffixes. */
  public FormatHandler(String format, String[] suffixes) {
    this.format = format;
    this.suffixes = suffixes == null ? new String[0] : suffixes;
  }
  
  // -- IMetadataConfigurable API methods --

  /* (non-Javadoc)
   * @see loci.formats.IMetadataConfigurable#getSupportedMetadataLevels()
   */
  @Override
  public Set<MetadataLevel> getSupportedMetadataLevels() {
    Set<MetadataLevel> supportedLevels = new HashSet<MetadataLevel>();
    supportedLevels.add(MetadataLevel.ALL);
    supportedLevels.add(MetadataLevel.NO_OVERLAYS);
    supportedLevels.add(MetadataLevel.MINIMUM);
    return supportedLevels;
  }

  /* (non-Javadoc)
   * @see loci.formats.IMetadataConfigurable#getMetadataOptions()
   */
  @Override
  public MetadataOptions getMetadataOptions() {
    return metadataOptions;
  }

  /* (non-Javadoc)
   * @see loci.formats.IMetadataConfigurable#setMetadataOptions(loci.formats.in.MetadataOptions)
   */
  @Override
  public void setMetadataOptions(MetadataOptions options) {
    this.metadataOptions = options;
  }

  // -- IFormatHandler API methods --

  /**
   * Checks if a file matches the type of this format handler.
   * The default implementation checks filename suffixes against
   * those known for this format.
   */
  @Override
  public boolean isThisType(String name) {
    return checkSuffix(name, suffixes);
  }

  /* @see IFormatHandler#getFormat() */
  @Override
  public String getFormat() { return format; }

  /* @see IFormatHandler#getSuffixes() */
  @Override
  public String[] getSuffixes() { return suffixes; }

  /* @see IFormatHandler#getNativeDataType() */
  @Override
  public Class<?> getNativeDataType() {
    // NB: Handlers use byte arrays by default as the native type.
    return byte[].class;
  }

  // -- Utility methods --

  /** Performs suffix matching for the given filename. */
  public static boolean checkSuffix(String name, String suffix) {
    return checkSuffix(name, new String[] {suffix});
  }

  /** Performs suffix matching for the given filename. */
  public static boolean checkSuffix(String name, String[] suffixList) {
    String lname = name.toLowerCase();
    for (int i=0; i<suffixList.length; i++) {
      String s = "." + suffixList[i];
      if (lname.endsWith(s)) return true;
      for (int j=0; j<COMPRESSION_SUFFIXES.length; j++) {
        if (lname.endsWith(s + "." + COMPRESSION_SUFFIXES[j])) return true;
      }
    }
    return false;
  }

}
