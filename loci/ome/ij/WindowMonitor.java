package loci.ome.ij;

//
// WindowMonitor.java
//

import java.lang.*;
import ij.WindowManager;

/**
 * Runs a pseudo-infinite loop to monitor changes in the how many files
 * are open.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */

public class WindowMonitor extends Thread {

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
