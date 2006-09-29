//
// OMEPlugin.java
//

/*
OME Plugin for ImageJ plugin for transferring images to and from an OME
database. Copyright (C) 2004-@year@ Philip Huettl and Melissa Linkert.

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

package loci.plugins.ome;

import ij.IJ;
import ij.plugin.PlugIn;
import loci.plugins.Util;

/**
 * OMEPlugin is the ImageJ Plugin that allows image import and exports from
 * the OME database. It also views OME-XML metadata present in OME-TIFF files.
 *
 * @author Philip Huettl pmhuettl at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OMEPlugin implements PlugIn {
  private static OMESidePanel omeSidePanel;

  /** shows and retrieves info from the SidePanel */
  public void run(String arg) {
    if (!Util.checkVersion()) return;
    if (!Util.checkLibraries(true, true, true, true)) return;
    if (omeSidePanel == null) omeSidePanel = new OMESidePanel(IJ.getInstance());
    WindowMonitor monitor = new WindowMonitor();
    monitor.start();
    OMESidePanel.showIt();
  }

}
