/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2015 - 2017 Open Microscopy Environment:
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

package loci.formats.services;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import loci.common.RandomAccessInputStream;
import loci.common.services.AbstractService;
import loci.common.services.ServiceException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

/**
 *
 */
public class EXIFServiceImpl extends AbstractService implements EXIFService {

  private ExifSubIFDDirectory directory;

  // -- Constructor --

  public EXIFServiceImpl() {
    // check for metadata-extractor.jar
    checkClassDependency(ImageMetadataReader.class);
    // check for xmpcore.jar
    checkClassDependency(com.adobe.xmp.XMPMeta.class);
  }

  // -- EXIFService API methods --

  @Override
  public void initialize(String file) throws ServiceException, IOException {
    try (RandomAccessInputStream jpegFile = new RandomAccessInputStream(file)) {
      try {
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
      }
      catch (Throwable e) {
        throw new ServiceException("Could not read EXIF data", e);
      }
    }
  }

  @Override
  public HashMap<String, String> getTags() {
    HashMap<String, String> tagMap = new HashMap<String, String>();
    if (directory != null) {
      for (Tag tag : directory.getTags()) {
        tagMap.put(tag.getTagName(), tag.getDescription());
      }
    }

    return tagMap;
  }

  @Override
  public Date getCreationDate() {
    if (directory != null) {
      return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
    }
    return null;
  }

  @Override
  public void close() throws IOException {
    directory = null;
  }

}
