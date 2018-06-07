/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.util.Vector;

import loci.common.Location;
import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.LegacyQTTools;
import loci.formats.meta.MetadataStore;

/**
 * LegacyQTReader is a file format reader for QuickTime movie files.
 * To use it, QuickTime for Java must be installed.
 *
 * Much of this code was based on the QuickTime Movie Opener for ImageJ
 * (available at http://rsb.info.nih.gov/ij/plugins/movie-opener.html).
 */
public class LegacyQTReader extends BIFormatReader {

  // -- Fields --

  /** Instance of LegacyQTTools to handle QuickTime for Java detection. */
  protected LegacyQTTools tools;

  /** Reflection tool for QuickTime for Java calls. */
  protected ReflectedUniverse r;

  /** Time offset for each frame. */
  protected int[] times;

  /** Image containing current frame. */
  protected Image image;

  // -- Constructor --

  /** Constructs a new QT reader. */
  public LegacyQTReader() {
    super("QuickTime", "mov");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#openPlane(int, int, int, int, int int) */
  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, -1, x, y, w, h);

    // paint frame into image
    try {
      r.setVar("time", times[no]);
      r.exec("moviePlayer.setTime(time)");
      r.exec("qtip.redraw(null)");
      r.exec("qtip.updateConsumers(null)");
    }
    catch (ReflectException re) {
      throw new FormatException("Open movie failed", re);
    }
    return AWTImageTools.getSubimage(AWTImageTools.makeBuffered(image),
      isLittleEndian(), x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    try {
      if (r != null && r.getVar("openMovieFile") != null) {
        r.exec("openMovieFile.close()");
        if (!fileOnly) {
          r.exec("m.disposeQTObject()");
          r.exec("imageTrack.disposeQTObject()");
          r.exec("QTSession.close()");
        }
      }
    }
    catch (ReflectException e) {
      LOGGER.debug("Failed to close QuickTime session", e);
    }
    if (!fileOnly) {
      currentId = null;
      times = null;
      image = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    LOGGER.info("Checking for QuickTime Java");

    if (tools == null) {
      tools = new LegacyQTTools();
      r = tools.getUniverse();
    }
    tools.checkQTLibrary();

    super.initFile(id);

    LOGGER.info("Reading movie dimensions");
    try {
      r.exec("QTSession.open()");

      // open movie file
      Location file = new Location(id);
      r.setVar("path", file.getAbsolutePath());
      r.exec("qtf = new QTFile(path)");
      r.exec("openMovieFile = OpenMovieFile.asRead(qtf)");
      r.exec("m = Movie.fromFile(openMovieFile)");

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

      r.exec("moviePlayer = new MoviePlayer(m)");
      r.setVar("dim", new Dimension(w.intValue(), h.intValue()));
      ImageProducer qtip = (ImageProducer)
        r.exec("qtip = new QTImageProducer(moviePlayer, dim)");
      image = Toolkit.getDefaultToolkit().createImage(qtip);

      r.setVar("zero", 0);
      r.setVar("one", 1f);
      r.exec("timeInfo = new TimeInfo(zero, zero)");
      r.exec("moviePlayer.setTime(zero)");
      Vector<Integer> v = new Vector<Integer>();
      int time = 0;
      Integer q = new Integer(time);
      do {
        v.add(q);
        r.exec("timeInfo = imageTrack.getNextInterestingTime(" +
          "StdQTConstants.nextTimeMediaSample, timeInfo.time, one)");
        q = (Integer) r.getVar("timeInfo.time");
        time = q.intValue();
      }
      while (time >= 0);

      CoreMetadata m = core.get(0);
      m.imageCount = v.size();
      times = new int[getImageCount()];
      for (int i=0; i<times.length; i++) {
        q = v.elementAt(i);
        times[i] = q.intValue();
      }

      LOGGER.info("Populating metadata");

      BufferedImage img = AWTImageTools.makeBuffered(image);

      m.sizeX = img.getWidth();
      m.sizeY = img.getHeight();
      m.sizeZ = 1;
      m.sizeC = img.getRaster().getNumBands();
      m.sizeT = getImageCount();
      m.pixelType = AWTImageTools.getPixelType(img);
      m.dimensionOrder = "XYCTZ";
      m.rgb = true;
      m.interleaved = false;
      m.littleEndian = false;
      m.indexed = false;
      m.falseColor = false;

      MetadataStore store = makeFilterMetadata();
      MetadataTools.populatePixels(store, this);
    }
    catch (ReflectException e) {
      throw new FormatException("Open movie failed", e);
    }
  }

}
