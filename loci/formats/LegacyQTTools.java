//
// LegacyQTTools.java
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

import java.net.*;
import javax.swing.JOptionPane;

/**
 * Utility class for working with QuickTime for Java.
 */
public class LegacyQTTools {

  // -- Constants --

  public static final String NO_QT_MSG = "You need to install " +
    "QuickTime for Java from http://www.apple.com/quicktime/";

  public static final String EXPIRED_QT_MSG = "Your version of " +
    "QuickTime for Java has expired";

  protected static final String[] SUFFIXES = {"mov", "qt"};

  protected static final boolean MAC_OS_X =
    System.getProperty("os.name").equals("Mac OS X");

  // supported codecs for writing

  public static final String[] CODECS = {"Motion JPEG-B", "Cinepak",
    "Animation", "H.263", "Sorenson", "Sorenson 3", "MPEG-4", "Raw"};

  public static final int[] CODEC_TYPES = {1835692130, 1668704612, 1919706400,
    1748121139, 1398165809, 0x53565133, 0x6d703476, 0};

  public static final String[] QUALITY_STRINGS = {"Low", "Normal", "High",
    "Maximum"};

  public static final int[] QUALITY_CONSTANTS = {256, 512, 768, 1023};

  // -- Static fields --

  /**
   * This custom class loader searches additional paths for the QTJava.zip
   * library. Java has a restriction where only one class loader can have a
   * native library loaded within a JVM. So the class loader must be static,
   * shared by all QTForms, or else an UnsatisfiedLinkError is thrown when
   * attempting to initialize QTJava multiple times.
   */
  protected static final ClassLoader LOADER = constructLoader();

  protected static ClassLoader constructLoader() {
    // set up additional QuickTime for Java paths
    URL[] paths = null;

    // should work for Windows
    String windir = System.getProperty("user.home");

    try {
      paths = new URL[] {
        // Windows
        new URL("file:/WinNT/System32/QTJava.zip"),
        new URL("file:/Program Files/QuickTime/QTSystem/QTJava.zip"),
        new URL("file:/Windows/System32/QTJava.zip"),
        new URL("file:/Windows/System/QTJava.zip"),
        new URL(windir + "/System/QTJava.zip"),
        new URL(windir + "/System32/QTJava.zip"),
        // Mac OS X
        new URL("file:/System/Library/Java/Extensions/QTJava.zip")
      };
    }
    catch (MalformedURLException exc) { }
    return paths == null ? null : new URLClassLoader(paths);
  }

  // -- Fields --

  /** Flag indicating this class has been initialized. */
  protected boolean initialized = false;

  /** Flag indicating QuickTime for Java is not installed. */
  protected boolean noQT = false;

  /** Flag indicating QuickTime for Java has expired. */
  protected boolean expiredQT = false;

  /** Reflection tool for QuickTime for Java calls. */
  protected ReflectedUniverse r;


  // -- LegacyQTTools API methods --

  /** Initializes the class. */
  protected void initClass() {
    if (initialized) return;
    boolean needClose = false;
    r = new ReflectedUniverse(LOADER);
    try {
      r.exec("import java.lang.Class");
      r.exec("import java.lang.reflect.Field");
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
      r.exec("import quicktime.std.qtcomponents.SpatialSettings");
      r.exec("import quicktime.util.EndianOrder");
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
    if (!initialized) initClass();
    return !noQT;
  }

  /** Whether QuickTime for Java has expired. */
  public boolean isQTExpired() {
    if (!initialized) initClass();
    return expiredQT;
  }

  /** Gets QuickTime for Java reflected universe. */
  public ReflectedUniverse getUniverse() {
    if (!initialized) initClass();
    return r;
  }

  /** Display a dialog box allowing the user to choose a video codec. */
  public static int getCodec() {
    String codec = (String) JOptionPane.showInputDialog(null,
      "Choose a video codec", "Input", JOptionPane.INFORMATION_MESSAGE, null,
      CODECS, CODECS[0]);

    int codecId = 0;
    for (int i=0; i<CODECS.length; i++) {
      if (CODECS[i].equals(codec)) {
        codecId = CODEC_TYPES[i];
      }
    }
    return codecId;
  }

}
