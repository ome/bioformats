//
// LegacyQTWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.out;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import loci.formats.*;

/**
 * LegacyQTWriter is a file format writer for QuickTime movies. It uses the
 * QuickTime for Java library, and allows the user to choose between a variety
 * of common video codecs.
 *
 * Much of this code was based on the QuickTime Movie Writer for ImageJ
 * (available at http://rsb.info.nih.gov/ij/plugins/movie-writer.html).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/out/LegacyQTWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/out/LegacyQTWriter.java">SVN</a></dd></dl>
 */
public class LegacyQTWriter extends FormatWriter {

  // -- Constants --

  /** Time scale. */
  private static final int TIME_SCALE = 600;

  // -- Fields --

  /** Instance of LegacyQTTools to handle QuickTime for Java detection. */
  protected LegacyQTTools tools;

  /** Reflection tool for QuickTime for Java calls. */
  protected ReflectedUniverse r;

  /** The codec to use. */
  protected int codec = QTWriter.CODEC_RAW;

  /** The quality to use. */
  protected int quality = QTWriter.QUALITY_NORMAL;

  /** Number of frames written. */
  private int numWritten = 0;

  /** Frame width. */
  private int width;

  /** Frame height. */
  private int height;

  private int[] pixels2 = null;

  // -- Constructor --

  public LegacyQTWriter() {
    super("Legacy QuickTime", "mov");
  }

  // -- LegacyQTWriter API methods --

  /**
   * Sets the encoded movie's codec.
   * @param codec Codec value:<ul>
   *   <li>QTWriter.CODEC_CINEPAK</li>
   *   <li>QTWriter.CODEC_ANIMATION</li>
   *   <li>QTWriter.CODEC_H_263</li>
   *   <li>QTWriter.CODEC_SORENSON</li>
   *   <li>QTWriter.CODEC_SORENSON_3</li>
   *   <li>QTWriter.CODEC_MPEG_4</li>
   *   <li>QTWriter.CODEC_RAW</li>
   * </ul>
   */
  public void setCodec(int codec) { this.codec = codec; }

  /**
   * Sets the quality of the encoded movie.
   * @param quality Quality value:<ul>
   *   <li>QTWriter.QUALITY_LOW</li>
   *   <li>QTWriter.QUALITY_MEDIUM</li>
   *   <li>QTWriter.QUALITY_HIGH</li>
   *   <li>QTWriter.QUALITY_MAXIMUM</li>
   * </ul>
   */
  public void setQuality(int quality) { this.quality = quality; }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveImage(Image, boolean) */
  public void saveImage(Image image, boolean last)
    throws FormatException, IOException
  {
    if (tools == null) {
      tools = new LegacyQTTools();
      r = tools.getUniverse();
    }

    if (tools.isQTExpired()) {
      throw new FormatException(LegacyQTTools.EXPIRED_QT_MSG);
    }
    if (!tools.canDoQT()) throw new FormatException(LegacyQTTools.NO_QT_MSG);

    if (!initialized) {
      initialized = true;

      try {
        r.exec("QTSession.open()");
        BufferedImage img = ImageTools.makeBuffered(image);
        width = img.getWidth();
        height = img.getHeight();
        File f = new File(currentId);
        r.setVar("f", f);
        r.setVar("width", (float) width);
        r.setVar("height", (float) height);

        r.exec("movFile = new QTFile(f)");
        r.setVar("val", -2147483648 | 268435456);
        r.setVar("kMoviePlayer", 1414942532);
        r.exec("movie = Movie.createMovieFile(movFile, kMoviePlayer, val)");
        int timeScale = TIME_SCALE;
        r.setVar("timeScale", timeScale);
        r.setVar("zero", 0);
        r.setVar("zeroFloat", (float) 0);
        r.exec("videoTrack = movie.addTrack(width, height, zeroFloat)");
        r.exec("videoMedia = new VideoMedia(videoTrack, timeScale)");
        r.exec("videoMedia.beginEdits()");

        r.setVar("pixelFormat", 32);
        r.exec("imgDesc2 = new ImageDescription(pixelFormat)");
        r.setVar("width", width);
        r.setVar("height", height);
        r.exec("imgDesc2.setWidth(width)");
        r.exec("imgDesc2.setHeight(height)");

        r.exec("gw = new QDGraphics(imgDesc2, zero)");
        r.exec("bounds = new QDRect(zero, zero, width, height)");

        r.exec("pixMap = gw.getPixMap()");
        r.exec("pixSize = pixMap.getPixelSize()");
        r.setVar("codec", codec);
        r.setVar("quality", quality);

        int rawImageSize = width * height * 4;
        r.setVar("rawImageSize", rawImageSize);

        r.setVar("boolTrue", true);
        r.exec("imageHandle = new QTHandle(rawImageSize, boolTrue)");
        r.exec("imageHandle.lock()");
        r.exec("compressedImage = RawEncodedImage.fromQTHandle(imageHandle)");

        r.setVar("rate", 30);

        r.exec("seq = new CSequence(gw, bounds, pixSize, codec, " +
          "CodecComponent.bestFidelityCodec, quality, quality, rate, null, " +
          "zero)");

        r.exec("imgDesc = seq.getDescription()");
      }
      catch (ReflectException e) {
        trace(e);
        throw new FormatException("Legacy QuickTime writer failed", e);
      }
    }

    numWritten++;

    try {
      r.exec("pixMap = gw.getPixMap()");
      r.exec("pixelData = pixMap.getPixelData()");

      r.exec("intsPerRow = pixelData.getRowBytes()");
      int intsPerRow = ((Integer) r.getVar("intsPerRow")).intValue() / 4;

      byte[][] px = ImageTools.getBytes(ImageTools.makeBuffered(image));

      int[] pixels = new int[px[0].length];
      for (int i=0; i<pixels.length; i++) {
        byte[] b = new byte[4];
        for (int j=0; j<px.length; j++) {
          b[j] = px[j][i];
        }
        for (int j=px.length; j<4; j++) {
          b[j] = px[j % px.length][i];
        }
        pixels[i] = DataTools.bytesToInt(b, true);
      }

      if (pixels2 == null) pixels2 = new int[intsPerRow * height];
      r.exec("nativeLittle = EndianOrder.isNativeLittleEndian()");
      boolean nativeLittle =
        ((Boolean) r.getVar("nativeLittle")).booleanValue();
      if (nativeLittle) {
        int offset1, offset2;
        for (int y=0; y<height; y++) {
          offset1 = y * width;
          offset2 = y * intsPerRow;
          for (int x=0; x<width; x++) {
            r.setVar("thisByte", pixels[offset1++]);
            r.exec("b = EndianOrder.flipBigEndianToNative32(thisByte)");
            pixels2[offset2++] = ((Integer) r.getVar("b")).intValue();
          }
        }
      }
      else {
        for (int i=0; i<height; i++) {
          System.arraycopy(pixels, i*width, pixels2, i*intsPerRow, width);
        }
      }

      r.setVar("pixels2", pixels2);
      r.setVar("len", intsPerRow * height);

      r.exec("pixelData.copyFromArray(zero, pixels2, zero, len)");
      r.setVar("four", 4);
      r.exec("cfInfo = seq.compressFrame(gw, bounds, four, compressedImage)");

      // see developer.apple.com/qa/qtmcc/qtmcc20.html
      r.exec("similarity = cfInfo.getSimilarity()");
      int sim = ((Integer) r.getVar("similarity")).intValue();
      r.setVar("syncSample", sim == 0);
      r.exec("dataSize = cfInfo.getDataSize()");
      r.setVar("fps", fps);
      r.setVar("frameRate", 600);
      r.setVar("rate", 600 / fps);
      boolean sync = ((Boolean) r.getVar("syncSample")).booleanValue();
      int syncSample = sync ? 0 : 1;

      r.setVar("sync", syncSample);
      r.setVar("one", 1);
      r.exec("videoMedia.addSample(imageHandle, zero, dataSize, " +
        "rate, imgDesc, one, sync)");
    }
    catch (ReflectException e) {
      trace(e);
      throw new FormatException("Legacy QuickTime writer failed", e);
    }
    if (last) {
      try {
        r.exec("videoMedia.endEdits()");
        r.exec("duration = videoMedia.getDuration()");
        r.setVar("floatOne", (float) 1.0);
        r.exec("videoTrack.insertMedia(zero, zero, duration, floatOne)");
        r.exec("omf = OpenMovieFile.asWrite(movFile)");
        r.exec("name = movFile.getName()");
        r.setVar("minusOne", -1);
        r.exec("movie.addResource(omf, minusOne, name)");
        r.exec("QTSession.close()");
      }
      catch (ReflectException e) {
        trace(e);
        throw new FormatException("Legacy QuickTime writer failed", e);
      }
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return true; }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    r = null;
    numWritten = 0;
    width = 0;
    height = 0;
    pixels2 = null;
    currentId = null;
    initialized = false;
  }

}
