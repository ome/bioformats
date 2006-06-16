//
// LegacyQTReader.java
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

/**
 * LegacyQTReader is a file format reader for QuickTime movie files.
 * To use it, QuickTime for Java must be installed.
 *
 * Much of this code was based on the QuickTime Movie Opener for ImageJ
 * (available at http://rsb.info.nih.gov/ij/plugins/movie-opener.html).
 */
public class LegacyQTReader extends FormatReader {

  // -- Fields --

  /** Instance of LegacyQTTools to handle QuickTime for Java detection. */
  protected LegacyQTTools tools;

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

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a QuickTime file. */
  public boolean isThisType(byte[] block) { return false; }

  /** Determines the number of images in the given QuickTime file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return (!separated) ? numImages : 3*numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return true;
  }

  /** Obtains the specified image from the given QuickTime file. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    BufferedImage img = openImage(id, no);
    if (!separated) {
      byte[][] p = ImageTools.getBytes(img);
      byte[] rtn = new byte[p.length * p[0].length];
      for (int i=0; i<p.length; i++) {
        System.arraycopy(p[i], 0, rtn, i*p[0].length, p[i].length);
      }
      return rtn;
    }
    else {
      return ImageTools.getBytes(img)[0];
    }
  }

  /** Obtains the specified image from the given QuickTime file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (tools.isQTExpired()) {
      throw new FormatException(LegacyQTTools.EXPIRED_QT_MSG);
    }
    if (!tools.canDoQT()) throw new FormatException(LegacyQTTools.NO_QT_MSG);

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

    if (!separated) {
      return ImageTools.makeBuffered(image);
    }
    else {
      return ImageTools.splitChannels(ImageTools.makeBuffered(image))[no % 3];
    }
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (currentId == null) return;

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
    if (tools == null) {
      tools = new LegacyQTTools();
      r = tools.getUniverse();
    }
    if (tools.isQTExpired()) {
      throw new FormatException(LegacyQTTools.EXPIRED_QT_MSG);
    }
    if (!tools.canDoQT()) throw new FormatException(LegacyQTTools.NO_QT_MSG);

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

//      if (LegacyQTTools.MAC_OS_X) {
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
//      }
//      else {
//        r.exec("seq = ImageUtil.createSequence(imageTrack)");
//        numImages = ((Integer) r.exec("seq.size()")).intValue();
//      }

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
