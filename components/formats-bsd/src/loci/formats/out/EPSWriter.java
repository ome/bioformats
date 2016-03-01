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

package loci.formats.out;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.meta.MetadataRetrieve;

/**
 * EPSWriter is the file format writer for Encapsulated PostScript (EPS) files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class EPSWriter extends FormatWriter {

  // -- Constants --

  private static final String DUMMY_PIXEL = "00";

  // -- Fields --

  private long planeOffset = 0;

  // -- Constructor --

  public EPSWriter() {
    super("Encapsulated PostScript", new String[] {"eps", "epsi"});
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);

    MetadataRetrieve meta = getMetadataRetrieve();
    int sizeX = meta.getPixelsSizeX(series).getValue().intValue();
    int nChannels = getSamplesPerPixel();

    // write pixel data
    // for simplicity, write 80 char lines

    if (!initialized[series][no]) {
      initialized[series][no] = true;

      writeHeader();

      if (!isFullPlane(x, y, w, h)) {
        // write a dummy plane that will be overwritten in sections
        int planeSize = w * h * nChannels;
        for (int i=0; i<planeSize; i++) {
          out.writeBytes(DUMMY_PIXEL);
        }
      }
    }

    int planeSize = w * h;

    StringBuffer buffer = new StringBuffer();

    int offset = y * sizeX * nChannels * 2;
    out.seek(planeOffset + offset);
    for (int row=0; row<h; row++) {
      out.skipBytes(nChannels * x * 2);
      for (int col=0; col<w*nChannels; col++) {
        int i = row * w * nChannels + col;
        int index = interleaved || nChannels == 1 ? i :
          (i % nChannels) * planeSize + (i / nChannels);
        String s = Integer.toHexString(buf[index]);
        // only want last 2 characters of s
        if (s.length() > 1) buffer.append(s.substring(s.length() - 2));
        else {
          buffer.append("0");
          buffer.append(s);
        }
      }
      out.writeBytes(buffer.toString());
      buffer.delete(0, buffer.length());
      out.skipBytes(nChannels * (sizeX - w - x) * 2);
    }

    // write footer

    out.seek(out.length());
    out.writeBytes("\nshowpage\n");
  }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.UINT8};
  }

  // -- Helper methods --

  private void writeHeader() throws IOException {
    MetadataRetrieve r = getMetadataRetrieve();
    int width = r.getPixelsSizeX(series).getValue().intValue();
    int height = r.getPixelsSizeY(series).getValue().intValue();
    int nChannels = getSamplesPerPixel();

    out.writeBytes("%!PS-Adobe-2.0 EPSF-1.2\n");
    out.writeBytes("%%Title: " + currentId + "\n");
    out.writeBytes("%%Creator: " + FormatTools.CREATOR + "\n");
    out.writeBytes("%%Pages: 1\n");
    out.writeBytes("%%BoundingBox: 0 0 " + width + " " + height + "\n");
    out.writeBytes("%%EndComments\n\n");

    out.writeBytes("/ld {load def} bind def\n");
    out.writeBytes("/s /stroke ld /f /fill ld /m /moveto ld /l " +
      "/lineto ld /c /curveto ld /rgb {255 div 3 1 roll 255 div 3 1 " +
      "roll 255 div 3 1 roll setrgbcolor} def\n");
    out.writeBytes("0 0 translate\n");
    out.writeBytes(((float) width) + " " + ((float) height) + " scale\n");
    out.writeBytes("/picstr 40 string def\n");
    out.writeBytes(width + " " + height + " 8 [" + width + " 0 0 " +
      (-1 * height) + " 0 " + height +
      "] {currentfile picstr readhexstring pop} ");
    if (nChannels == 1) {
      out.writeBytes("image\n");
    }
    else {
      out.writeBytes("false 3 colorimage\n");
    }
    planeOffset = out.getFilePointer();
  }

}
