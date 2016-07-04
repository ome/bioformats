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

package loci.formats.in;

/**
 *
 * @author callan
 */
public class DefaultMetadataOptions implements MetadataOptions {

  private MetadataLevel level;
  
  private MetadataGroupFilesOptions groupFilesOptions;

  /**
   * Construct a new DefaultMetadataOptions.
   * The underlying MetadataLevel will be set to {@link loci.formats.in.MetadataLevel#ALL}.
   */
  public DefaultMetadataOptions() {
    this.level = MetadataLevel.ALL;
  }

  /**
   * Construct a new DefaultMetadataOptions.
   * @param level the MetadataLevel to use
   */
  public DefaultMetadataOptions(MetadataLevel level) {
    this.level = level;
  }

  /* (non-Javadoc)
   * @see loci.formats.in.MetadataOptions#getMetadataLevel()
   */
  @Override
  public MetadataLevel getMetadataLevel() {
    return level;
  }

  /* (non-Javadoc)
   * @see loci.formats.in.MetadataOptions#setMetadataLevel(loci.formats.in.MetadataLevel)
   */
  @Override
  public void setMetadataLevel(MetadataLevel level) {
    this.level = level;
  }
  
  /* (non-Javadoc)
   * @see loci.formats.in.MetadataOptions#getMetadataGroupFilesOptions()
   */
  @Override
  public MetadataGroupFilesOptions getMetadataGroupFilesOptions() {
    return groupFilesOptions;
  }

  /* (non-Javadoc)
   * @see loci.formats.in.MetadataOptions#setMetadataGroupFilesOptions(loci.formats.in.MetadataGroupFilesOptions)
   */
  @Override
  public void setMetadataGroupFilesOptions(MetadataGroupFilesOptions options) {
    this.groupFilesOptions = options;
  }

}
