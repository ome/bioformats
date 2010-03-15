//
// IceServer.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.ice.formats;

import Ice.Communicator;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Ice server for remote Bio-Formats functionality.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats-ice/src/loci/ice/formats/IceServer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats-ice/src/loci/ice/formats/IceServer.java">SVN</a></dd></dl>
 */
public class IceServer {

  public static boolean debug = false;

  public static void main(String[] args) {
    if (args.length > 0 && "-debug".equals(args[0])) debug = true;
    Logger root = Logger.getRootLogger();
    root.setLevel(debug ? Level.DEBUG : Level.INFO);
    root.addAppender(new ConsoleAppender(new PatternLayout("%m%n")));

    int status = 0;
    Communicator ic = null;
    try {
      debug("Starting Bio-Formats server...");
      ic = Util.initialize(args);
      ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints(
        "BioFormatsAdapter", "default -p 43704");
      Object readerObject = new IFormatReaderI();
      adapter.add(readerObject, Util.stringToIdentity("IFormatReader"));
      Object writerObject = new IFormatWriterI();
      adapter.add(writerObject, Util.stringToIdentity("IFormatWriter"));
      Object metaObject = new IMetadataI();
      adapter.add(metaObject, Util.stringToIdentity("IMetadata"));
      adapter.activate();
      debug("Bio-Formats server is active.");
      ic.waitForShutdown();
    }
    catch (Ice.LocalException e) {
      e.printStackTrace();
      status = 1;
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      status = 1;
    }
    if (ic != null) {
      debug("Bio-Formats server shutting down...");
      // Clean up
      try {
        ic.destroy();
      }
      catch (Exception e) {
        System.err.println(e.getMessage());
        status = 1;
      }
    }
    System.exit(status);
  }

  public static void debug(String message) {
    if (debug) System.out.println(message);
  }

}
