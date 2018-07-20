/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2018 Open Microscopy Environment:
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

package loci.formats.out;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;

import loci.common.Location;
import loci.common.Constants;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.meta.IPyramidStore;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffSaver;
import loci.formats.in.MetadataOptions;
import loci.formats.in.DynamicMetadataOptions;

/**
 * PyramidOMETiffWriter is the file format writer for pyramid OME-TIFF files.
 */
public class PyramidOMETiffWriter extends OMETiffWriter {

  // -- Constructor --

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#isThisType(String) */
  @Override
  public boolean isThisType(String name) {
    if (!super.isThisType(name)) {
      return false;
    }
    MetadataRetrieve r = getMetadataRetrieve();
    if (!(r instanceof IPyramidStore)) {
      return false;
    }
    return ((IPyramidStore) r).getResolutionCount(0) > 1;
  }

  // -- IFormatWriter API methods --

  @Override
  public void saveBytes(int no, byte[] buf, IFD ifd, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (ifd == null) {
      ifd = new IFD();
    }
    if (getResolution() > 0) {
      ifd.put(IFD.NEW_SUBFILE_TYPE, 1);
    }
    else {
      if (!ifd.containsKey(IFD.SUB_IFD)) {
        ifd.put(IFD.SUB_IFD, (long) 0);
      }
    }

    super.saveBytes(no, buf, ifd, x, y, w, h);
  }

  @Override
  public void close() throws IOException {
    String id = currentId;
    MetadataRetrieve r = getMetadataRetrieve();
    int[] planeCounts = new int[r.getImageCount()];
    for (int i=0; i<planeCounts.length; i++) {
      planeCounts[i] = getPlaneCount(i);
    }
    super.close();

    // post-processing step to fill in all SubIFD arrays

    RandomAccessInputStream in = null;
    RandomAccessOutputStream out = null;
    try {
      in = new RandomAccessInputStream(id);
      TiffParser parser = new TiffParser(in);
      long[] allOffsets = parser.getIFDOffsets();
      in.close();

      int mainIFDIndex = 0;
      for (int i=0; i<r.getImageCount(); i++) {
        setSeries(i);
        for (int p=0; p<planeCounts[i]; p++) {
          int resCount = ((IPyramidStore) r).getResolutionCount(i);
          long[] subIFDOffsets = new long[resCount - 1];
          System.arraycopy(allOffsets, mainIFDIndex + 1, subIFDOffsets, 0,
            subIFDOffsets.length);

          out = new RandomAccessOutputStream(id);
          TiffSaver saver = new TiffSaver(out, id);
          saver.setBigTiff(isBigTiff);
          in = new RandomAccessInputStream(id);
          long nextPointer = (mainIFDIndex + resCount < allOffsets.length) ?
            allOffsets[mainIFDIndex + resCount] : 0;
          saver.overwriteIFDOffset(in, allOffsets[mainIFDIndex], nextPointer);
          saver.overwriteIFDValue(in, mainIFDIndex, IFD.SUB_IFD, subIFDOffsets);
          saver.close();
          out.close();
          in.close();

          mainIFDIndex += resCount;
        }
      }
      setSeries(0);
    }
    catch (FormatException e) {
      throw new IOException("Failed to assemble SubIFD offsets", e);
    }
    finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

}
