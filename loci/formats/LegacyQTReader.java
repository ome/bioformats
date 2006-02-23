//
// LegacyQTReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;

/**
 * LegacyQTReader is the old file format reader for QuickTime movie files.
 * To use it, QuickTime for Java must be installed.
 *
 * Much of this reader's code was adapted from Wayne Rasband's
 * QuickTime Movie Opener plugin for ImageJ
 * (available at http://rsb.info.nih.gov/ij/).
 *
 * TODO -- address efficiency issues; currently 200-550ms are required to
 *         process each plane, with an additional 2500+ ms of overhead
 */
public class LegacyQTReader extends FormatReader {

  // -- Constants --

  public static final String NO_QT_MSG = "You need to install " +
    "QuickTime for Java from http://www.apple.com/quicktime/";

  public static final String EXPIRED_QT_MSG = "Your version of " +
    "QuickTime for Java has expired";

  protected static final String[] SUFFIXES = { "mov" };

  protected static final boolean MAC_OS_X =
    System.getProperty("os.name").equals("Mac OS X");

  // -- Static fields --

  /**
   * This custom class loader searches additional paths for the QTJava.zip
   * library. Java has a restriction where only one class loader can have a
   * native library loaded within a JVM. So the class loader must be static,
   * shared by all QTForms, or else an UnsatisfiedLinkError is thrown when
   * attempting to initialize QTJava within multiple QTForms.
   */
  protected static final ClassLoader LOADER = constructLoader();

  protected static ClassLoader constructLoader() {
    // set up additional QuickTime for Java paths
    URL[] paths = null;
    try {
      paths = new URL[] {
        // Windows
        new URL("file:/WinNT/System32/QTJava.zip"),
        new URL("file:/Program Files/QuickTime/QTSystem/QTJava.zip"),
        new URL("file:/Windows/System32/QTJava.zip"),
        new URL("file:/Windows/System/QTJava.zip"),
        // Mac OS X
        new URL("file:/System/Library/Java/Extensions/QTJava.zip")
      };
    }
    catch (MalformedURLException exc) { }
    return paths == null ? null : new URLClassLoader(paths);
  }


  // -- Fields --

  /** Flag indicating this reader has been initialized. */
  protected boolean initialized = false;

  /** Flag indicating QuickTime for Java is not installed. */
  protected boolean noQT = false;

  /** Flag indicating QuickTime for Java has expired. */
  protected boolean expiredQT = false;

  /** Reflection tool for QuickTime for Java calls. */
  protected ReflectedUniverse r;

  /** Number of images in current QuickTime movie. */
  protected int numImages;

  /** Time increment between frames. */
  protected int timeStep;

  /** Flag indicating QuickTime frame needs to be redrawn. */
  protected boolean needsRedrawing;

  /** Image containing current frame. */
  protected Image image;


  // -- Constructor --

  /** Constructs a new QT reader. */
  public LegacyQTReader() { super("QuickTime", "mov"); }


  // -- LegacyQTReader API methods --

  /** Initializes the QuickTime reader. */
  protected void initReader() {
    if (initialized) return;
    boolean needClose = false;
    r = new ReflectedUniverse(LOADER);
    try {
      r.exec("import quicktime.QTSession");
      r.exec("QTSession.open()");
      needClose = true;
      if (MAC_OS_X) {
        r.exec("import quicktime.app.view.QTImageProducer");
        r.exec("import quicktime.app.view.MoviePlayer");
        r.exec("import quicktime.std.movies.TimeInfo");
      }
      else {
        r.exec("import quicktime.app.display.QTCanvas");
        r.exec("import quicktime.app.image.ImageUtil");
        r.exec("import quicktime.app.image.JImagePainter");
        r.exec("import quicktime.app.image.QTImageDrawer");
        r.exec("import quicktime.app.image.QTImageProducer");
        r.exec("import quicktime.app.image.Redrawable");
        r.exec("import quicktime.app.players.MoviePlayer");
      }
      r.exec("import quicktime.io.OpenMovieFile");
      r.exec("import quicktime.io.QTFile");
      r.exec("import quicktime.qd.Pict");
      r.exec("import quicktime.qd.QDDimension");
      r.exec("import quicktime.qd.QDGraphics");
      r.exec("import quicktime.qd.QDRect");
      r.exec("import quicktime.std.StdQTConstants");
      r.exec("import quicktime.std.image.CodecComponent");
      r.exec("import quicktime.std.image.CompressedFrameInfo");
      r.exec("import quicktime.std.image.CSequence");
      r.exec("import quicktime.std.image.ImageDescription");
      r.exec("import quicktime.std.image.QTImage");
      r.exec("import quicktime.std.movies.Movie");
      r.exec("import quicktime.std.movies.Track");
      r.exec("import quicktime.std.movies.media.VideoMedia");
      r.exec("import quicktime.util.QTHandle");
      r.exec("import quicktime.util.RawEncodedImage");
    }
    catch (ExceptionInInitializerError err) {
      noQT = true;
      Throwable t = err.getException();
      if (t instanceof SecurityException) {
        SecurityException exc = (SecurityException) t;
        if (exc.getMessage().indexOf("expired") >= 0) expiredQT = true;
      }
    }
    catch (Throwable t) { noQT = true; }
    finally {
      if (needClose) {
        try { r.exec("QTSession.close()"); }
        catch (Throwable t) { }
      }
      initialized = true;
    }
  }

  /** Whether QuickTime is available to this JVM. */
  public boolean canDoQT() {
    if (!initialized) initReader();
    return !noQT;
  }

  /** Whether QuickTime for Java has expired. */
  public boolean isQTExpired() {
    if (!initialized) initReader();
    return expiredQT;
  }

  /** Gets QuickTime for Java reflected universe. */
  public ReflectedUniverse getUniverse() {
    if (!initialized) initReader();
    return r;
  }

  /** Gets width and height for the given PICT bytes. */
  public Dimension getPictDimensions(byte[] bytes)
    throws FormatException, ReflectException
  {
    if (!initialized) initReader();
    if (expiredQT) throw new FormatException(EXPIRED_QT_MSG);
    if (noQT) throw new FormatException(NO_QT_MSG);

    try {
      r.exec("QTSession.open()");
      r.setVar("bytes", bytes);
      r.exec("pict = new Pict(bytes)");
      r.exec("box = pict.getPictFrame()");
      int width = ((Integer) r.exec("box.getWidth()")).intValue();
      int height = ((Integer) r.exec("box.getHeight()")).intValue();
      r.exec("QTSession.close()");
      return new Dimension(width, height);
    }
    catch (Exception e) {
      r.exec("QTSession.close()");
      throw new FormatException("PICT height determination failed", e);
    }
  }

  /** Converts the given byte array in PICT format to a Java image. */
  public synchronized Image pictToImage(byte[] bytes)
    throws FormatException
  {
    if (!initialized) initReader();
    if (expiredQT) throw new FormatException(EXPIRED_QT_MSG);
    if (noQT) throw new FormatException(NO_QT_MSG);

    try {
      r.exec("QTSession.open()");

      // Code adapted from:
      //   http://www.onjava.com/pub/a/onjava/2002/12/23/jmf.html?page=2
      r.setVar("bytes", bytes);
      r.exec("pict = new Pict(bytes)");
      r.exec("box = pict.getPictFrame()");
      int width = ((Integer) r.exec("box.getWidth()")).intValue();
      int height = ((Integer) r.exec("box.getHeight()")).intValue();
      // note: could get a RawEncodedImage from the Pict, but
      // apparently no way to get a PixMap from the REI
      r.exec("g = new QDGraphics(box)");
      r.exec("pict.draw(g, box)");
      // get data from the QDGraphics
      r.exec("pixMap = g.getPixMap()");
      r.exec("rei = pixMap.getPixelData()");

      // copy bytes to an array
      int rowBytes = ((Integer) r.exec("pixMap.getRowBytes()")).intValue();
      int intsPerRow = rowBytes / 4;
      int pixLen = intsPerRow * height;
      r.setVar("pixLen", pixLen);
      int[] pixels = new int[pixLen];
      r.setVar("pixels", pixels);
      r.setVar("zero", new Integer(0));
      r.exec("rei.copyToArray(zero, pixels, zero, pixLen)");

      // now coax into image, ignoring alpha for speed
      int bitsPerSample = 32;
      int redMask = 0x00ff0000;
      int greenMask = 0x0000ff00;
      int blueMask = 0x000000ff;
      int alphaMask = 0x00000000;
      DirectColorModel colorModel = new DirectColorModel(
        bitsPerSample, redMask, greenMask, blueMask, alphaMask);

      r.exec("QTSession.close()");
      return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(
        width, height, colorModel, pixels, 0, intsPerRow));
    }
    catch (Exception e) {
      try { r.exec("QTSession.close()"); }
      catch (ReflectException exc) { exc.printStackTrace(); }
      throw new FormatException("PICT extraction failed", e);
    }
  }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a QuickTime file. */
  public boolean isThisType(byte[] block) { return false; }

  /** Determines the number of images in the given QuickTime file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Obtains the specified image from the given QuickTime file. */
  public Image open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (expiredQT) throw new FormatException(EXPIRED_QT_MSG);
    if (noQT) throw new FormatException(NO_QT_MSG);

    // paint frame into image
    try {
      r.setVar("time", timeStep * no);
      r.exec("moviePlayer.setTime(time)");
      r.exec("qtip.redraw(null)");
      r.exec("qtip2 = new QTImageProducer(moviePlayer, dim)");
      ImageProducer qtip2 = (ImageProducer) r.getVar("qtip2");
      image = Toolkit.getDefaultToolkit().createImage(qtip2);
      r.exec("qtip2.redraw(null)");
    }
    catch (ReflectException re) {
      throw new FormatException("Open movie failed", re);
    }

    return image;
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (currentId == null) return;
    if (!initialized) initReader();

    try {
      r.exec("openMovieFile.close()");
      r.exec("QTSession.close()");
    }
    catch (ReflectException e) {
      throw new FormatException("Close movie failed", e);
    }
    currentId = null;
  }

  /** Initializes the given QuickTime file. */
  protected void initFile(String id)
    throws FormatException, IOException
  {
    if (!initialized) initReader();
    if (expiredQT) throw new FormatException(EXPIRED_QT_MSG);
    if (noQT) throw new FormatException(NO_QT_MSG);

    super.initFile(id);

    try {
      r.exec("QTSession.open()");

      // open movie file
      File file = new File(id);
      r.setVar("path", file.getAbsolutePath());
      r.exec("qtf = new QTFile(path)");
      r.exec("openMovieFile = OpenMovieFile.asRead(qtf)");
      r.exec("m = Movie.fromFile(openMovieFile)");

      // find first track with width != soundtrack
      int numTracks = ((Integer) r.exec("m.getTrackCount()")).intValue();
      int trackMostLikely = 0;
      int trackNum = 0;
      while (++trackNum <= numTracks && trackMostLikely == 0) {
        r.setVar("trackNum", trackNum);
        r.exec("imageTrack = m.getTrack(trackNum)");
        r.exec("d = imageTrack.getSize()");
        Integer w = (Integer) r.exec("d.getWidth()");
        if (w.intValue() > 0) trackMostLikely = trackNum;
      }

      r.setVar("trackMostLikely", trackMostLikely);
      r.exec("imageTrack = m.getTrack(trackMostLikely)");
      r.exec("d = imageTrack.getSize()");
      Integer w = (Integer) r.exec("d.getWidth()");
      Integer h = (Integer) r.exec("d.getHeight()");
      // now use controller to step movie
      r.exec("moviePlayer = new MoviePlayer(m)");
      r.setVar("dim", new Dimension(w.intValue(), h.intValue()));
      ImageProducer qtip = (ImageProducer)
        r.exec("qtip = new QTImageProducer(moviePlayer, dim)");
      image = Toolkit.getDefaultToolkit().createImage(qtip);
      needsRedrawing = ((Boolean) r.exec("qtip.isRedrawing()")).booleanValue();
      int maxTime = ((Integer) r.exec("m.getDuration()")).intValue();

      if (MAC_OS_X) {
        r.setVar("zero", 0);
        r.setVar("one", 1f);
        r.exec("timeInfo = new TimeInfo(zero, zero)");
        r.exec("moviePlayer.setTime(zero)");
        numImages = 0;
        int time = 0;
        do {
          numImages++;
          r.exec("timeInfo = imageTrack.getNextInterestingTime(" +
            "StdQTConstants.nextTimeMediaSample, timeInfo.time, one)");
          time = ((Integer) r.getVar("timeInfo.time")).intValue();
        }
        while (time >= 0);
      }
      else {
        r.exec("seq = ImageUtil.createSequence(imageTrack)");
        numImages = ((Integer) r.exec("seq.size()")).intValue();
      }

      timeStep = maxTime / numImages;
    }
    catch (Exception e) {
      throw new FormatException("Open movie failed", e);
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LegacyQTReader().testRead(args);
  }

}
