//
// WindowMonitor.java
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

import java.lang.*;
import ij.WindowManager;

/**
 * Runs a pseudo-infinite loop to monitor
 * changes in the how many files are open.
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class WindowMonitor extends Thread {

  public WindowMonitor() { super("OME-Plugin-WindowMonitor"); }

  public void run() {
    int[] idList = WindowManager.getIDList();
    if(idList == null) idList = new int[0];
    int[] newIdList;
    while (!OMESidePanel.cancelPlugin) {
      newIdList = WindowManager.getIDList();
      if(newIdList == null) newIdList = new int[0];
      if(idList.length != newIdList.length) OMESidePanel.showIt();
      else {
        // make sure that the IDs haven't changed
        for(int i=0; i<idList.length; i++) {
          if(idList[i] != newIdList[i]) OMESidePanel.showIt();
        }
      }
      idList = newIdList;
      try { Thread.sleep(100); }
      catch (InterruptedException exc) { }
    }
  }

}
