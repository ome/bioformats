//
// StatusEchoer.java
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

package loci.formats.tools;

import loci.common.LogTools;
import loci.formats.StatusEvent;
import loci.formats.StatusListener;

/**
 * Utility class for echoing status messages to the console.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tools/StatusEchoer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tools/StatusEchoer.java">SVN</a></dd></dl>
 */
public class StatusEchoer implements StatusListener {

  // -- Fields --

  private boolean verbose = true;
  private boolean next = true;

  // -- StatusEchoer methods --

  public void setVerbose(boolean value) { verbose = value; }
  public void setEchoNext(boolean value) { next = value; }

  // -- StatusListener methods --

  public void statusUpdated(StatusEvent e) {
    if (verbose) LogTools.println("\t" + e.getStatusMessage());
    else if (next) {
      LogTools.print(";");
      next = false;
    }
  }

}
