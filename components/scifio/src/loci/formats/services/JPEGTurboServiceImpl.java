/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.formats.services;

import java.io.IOException;
import java.util.ArrayList;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.Service;
import loci.common.services.ServiceException;

import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJDecompressor;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/services/JPEGTurboServiceImpl.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/services/JPEGTurboServiceImpl.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert <melissa at glencoesoftware.com>
 */
public class JPEGTurboServiceImpl implements JPEGTurboService {

  // -- Constants --

  private static final int SOF0 = 0xffc0;

  private static final int DRI = 0xffdd;
  private static final int SOS = 0xffda;

  private static final int RST0 = 0xffd0;
  private static final int RST7 = 0xffd7;

  private static final int EOI = 0xffd9;

  // -- Fields --

  private int imageWidth;
  private int imageHeight;
  private long offset;
  private RandomAccessInputStream in;

  private int restartInterval;
  private long sos;
  private long imageDimensions;

  private ArrayList<Long> restartMarkers = new ArrayList<Long>();

  // -- JPEGTurboService API methods --

  public void initialize(RandomAccessInputStream jpeg, int width, int height)
    throws ServiceException, IOException
  {
    in = jpeg;
    imageWidth = width;
    imageHeight = height;
    offset = jpeg.getFilePointer();

    in.skipBytes(2);
    int marker = in.readShort() & 0xffff;

    boolean inImage = false;
    while (marker != EOI && in.getFilePointer() < in.length()) {
      int length = in.readShort() & 0xffff;
      long end = in.getFilePointer() + length - 2;

      if (marker == DRI) {
        restartInterval = in.readShort() & 0xffff;
      }
      else if (marker == SOF0) {
        imageDimensions = in.getFilePointer() + 1;
      }
      else if (marker == SOS) {
        sos = end;
        inImage = true;
        restartMarkers.add(sos);
      }
      else if (marker >= RST0 && marker <= RST7) {
        restartMarkers.add(in.getFilePointer() - 2);
      }

      if (end < in.length() && !inImage) {
        in.seek(end);
      }

      if (inImage) {
        in.seek(in.getFilePointer() - 3);
      }
      marker = in.readShort() & 0xffff;
    }

    in.seek(offset);
  }

  public byte[] getTile(int xCoordinate, int yCoordinate, int width, int height)
    throws IOException
  {
    int tileDim = restartInterval * 8;

    ByteArrayHandle out = new ByteArrayHandle();

    out.write(getFixedHeader());

    int restartCounter = 0;

    int xTiles = imageWidth / tileDim;

    int tileX = xCoordinate / tileDim; // TODO
    int tileY = yCoordinate / tileDim; // TODO

    for (int row=0; row<restartInterval; row++) {
      int start = (xTiles * row) + tileX;
      int end = (xTiles * row) + tileX + (tileY * xTiles * restartInterval) + 1;

      if (end < restartMarkers.size()) {
        long startOffset = restartMarkers.get(start);
        long endOffset = restartMarkers.get(end);

        byte[] data = new byte[(int) (endOffset - startOffset)];
        in.seek(startOffset);
        in.read(data);

        data[data.length - 2] = (byte) 0xff;
        restartCounter %= 8;
        data[data.length - 1] = (byte) (0xd0 + restartCounter);
        restartCounter++;

        out.write(data);
      }
    }

    out.writeShort(EOI);

    byte[] tile = out.getBytes();
    out.close();

    // and here we actually decompress it...

    try {
      int pixelType = TJ.PF_RGB;
      int pixelSize = TJ.getPixelSize(pixelType);

      TJDecompressor decoder = new TJDecompressor();
      decoder.setJPEGImage(tile, tile.length);
      return decoder.decompress(width, width * pixelSize,
        height, pixelType, pixelType);
    }
    catch (Exception e) {
      throw new IOException(e);
    }
  }

  public void close() throws IOException {
    imageWidth = 0;
    imageHeight = 0;
    in = null;
    offset = 0;
    restartMarkers.clear();
  }

  // -- Helper methods --

  private byte[] getFixedHeader() throws IOException {
    in.seek(offset);

    byte[] header = new byte[(int) (sos - offset)];
    in.read(header);

    int tileDim = restartInterval * 8;

    int index = (int) (imageDimensions - offset);
    DataTools.unpackBytes(tileDim, header, index, 2, false);
    DataTools.unpackBytes(tileDim, header, index + 2, 2, false);

    return header;
  }

}
