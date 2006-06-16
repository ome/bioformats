//
// LegacyQTWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.JOptionPane;

/**
 * LegacyQTWriter is a file format writer for QuickTime movies. It uses the
 * QuickTime for Java library, and allows the user to choose between a variety
 * of common video codecs.
 *
 * Much of this code was based on the QuickTime Movie Writer for ImageJ
 * (available at http://rsb.info.nih.gov/ij/plugins/movie-writer.html).
 */
public class LegacyQTWriter extends FormatWriter {

  // -- Constants --

  /** Time scale. */
  private static final int TIME_SCALE = 600;
  private static final int KEY_FRAME_RATE = 30;

  // supported codecs for writing

  public static final String[] CODECS = {"Motion JPEG-B", "Cinepak",
    "Animation", "H.263", "Sorenson", "Sorenson 3", "MPEG-4", "Raw"};

  public static final int[] CODEC_TYPES = {1835692130, 1668704612, 1919706400,
    1748121139, 1398165809, 0x53565133, 0x6d703476, 0};

  public static final String[] QUALITY_STRINGS = {"Low", "Normal", "High",
    "Maximum"};

  public static final int[] QUALITY_CONSTANTS = {256, 512, 768, 1023};

  // -- Fields --

  /** Instance of LegacyQTTools to handle QuickTime for Java detection. */
  protected LegacyQTTools tools;

  /** Reflection tool for QuickTime for Java calls. */
  protected ReflectedUniverse r;

  /** The codec to use. */
  private String codec;

  /** The quality level to use. */
  private String quality;

  /** The codec ID to use. */
  protected int codecID;

  /** The quality ID to use. */
  protected int qualityID = 512;

  /** Number of frames written. */
  private int numWritten = 0;

  /** Frame width. */
  private int width;

  /** Frame height. */
  private int height;

  private int[] pixels2 = null;

  // -- Utility methods --

  /** Display a dialog box allowing the user to choose a video codec. */
  public static int getCodec() {
    String codec = (String) JOptionPane.showInputDialog(null,
      "Choose a video codec", "Input", JOptionPane.QUESTION_MESSAGE, null,
      CODECS, CODECS[0]);

    int codecId = 0;
    for (int i=0; i<CODECS.length; i++) {
      if (CODECS[i].equals(codec)) {
        codecId = CODEC_TYPES[i];
      }
    }
    return codecId;
  }

  // -- Constructor --

  public LegacyQTWriter() {
    super("Legacy QuickTime", "mov");
  }

  // -- FormatWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * If this image is the last one in the file, the last flag must be set.
   */
  public void save(String id, Image image, boolean last)
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

    if (!id.equals(currentId)) {
      currentId = id;

      try {
        r.exec("QTSession.open()");
        BufferedImage img = ImageTools.makeBuffered(image);
        width = img.getWidth();
        height = img.getHeight();
        File f = new File(id);
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
        r.setVar("codec", codecID);
        r.setVar("quality", qualityID);

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
      catch (Exception e) {
        e.printStackTrace();
        throw new FormatException("Legacy QuickTime writer failed.");
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
            byte b = ((Byte) r.getVar("b")).byteValue();
            pixels2[offset2++] = b;
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
    catch (Exception e) {
      e.printStackTrace();
      throw new FormatException("Legacy QuickTime writer failed.");
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
      catch (Exception e) {
        e.printStackTrace();
        throw new FormatException("Legacy QuickTime writer failed.");
      }
    }
  }

  /** Reports whether the writer can save multiple images to a single file. */
  public boolean canDoStacks(String id) { return true; }

  // -- Main method --

  public static void main(String[] args) throws IOException, FormatException {
    new LegacyQTWriter().testConvert(args);
  }

}
