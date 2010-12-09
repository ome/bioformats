//
// IJStatusEchoer.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser and Stack Slicer. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden and Christopher Peterson.

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

package loci.plugins.util;

import loci.plugins.BF;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Echoes status messages to the ImageJ status bar.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/util/IJStatusEchoer.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/util/IJStatusEchoer.java">SVN</a></dd></dl>
 */
public class IJStatusEchoer extends AppenderSkeleton {

  // -- AppenderSkeleton API methods --

  protected void append(LoggingEvent event) {
    if (event.getLevel().isGreaterOrEqual(Level.INFO)) {
      BF.status(false, event.getMessage().toString());
    }
    else {
      BF.debug(event.getMessage().toString());
    }
  }

  public boolean requiresLayout() {
    return false;
  }

  public void close() { }

}

