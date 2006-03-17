//
// OMEController.java
//

package loci.ome.ij;

import ij.*;
import ij.io.*;
import java.util.*;
import loci.ome.*;

/**
 * OMEController keeps track of all open images, their names, IDs, and 
 * associated XML nodes for use with OMESidePanel.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OMEController {

  /** Current (trimmed) ID list. */
  private static int[] current;

  /** The name of each open image. */
  private static String[] names;

  /** The description of each open image. */
  private static String[] descriptions;

  /** The number of open images. */
  private static int nImages;

  /** IDs of images that had a "partner" image that was killed. */
  private static Vector partners = new Vector();
  
  /** 
   * True if this is the first time findOpenImages has been called since
   * OMESidePanel.showIt() was called.
   */
  private static boolean calledOnce = true;
  
  public static int[] getCurrent() { return current; }

  public static String[] getNames() { return names; }

  public static String[] getDescrs() { return descriptions; }
  
  /**
   * Retrieves a list of open images from ImageJ, then trims the list
   * appropriately. 
   */
  public static void findOpenImages() {
    current = WindowManager.getIDList();
    
    if (current == null) current = new int[0];
    nImages = current.length;
   
    names = new String[nImages];
    descriptions = new String[nImages];
    for (int i=0; i<nImages; i++) {
      ImagePlus img = WindowManager.getImage(current[i]);      
      names[i] = img.getTitle();
      try {
        FileInfo fi = img.getOriginalFileInfo();
        if (fi == null) fi = img.getFileInfo();
        descriptions[i] = fi.description; 
      }
      catch (NullPointerException n) {
        descriptions[i] = null;
      }        
    }

    Vector toRemove = new Vector(); // list of indices to remove
    
    // first iterate through the list and look for any IDs that
    // originally had a partner image, but where the partner no
    // longer exists; anything we find should be killed

    for (int i=0; i<nImages; i++) {
      if (partners.contains(new Integer(current[i]))) {
        if ((i+1) < nImages) {
          if (current[i] != current[i+1]) {
            if ((i != 0) && (current[i] != current[i-1])) {      
              toRemove.add(new Integer(i));
              partners.remove(new Integer(current[i]));
            }
            else if (i == 0) {
              toRemove.add(new Integer(i));
              partners.remove(new Integer(current[i]));
            }        
          }
        }
        else if (((i != 0) && (current[i] != current[i-1])) || (nImages == 1)) {
          toRemove.add(new Integer(i));
          partners.remove(new Integer(current[i]));
        }        
      }
    }
   
    cleanup(toRemove, true); 
    
    
    // iterate through the list and look for duplicates
    // if we find a pair of duplicates, remove the first from the list
    // and kill it in ImageJ (latter may or may not work)
   
    toRemove.clear();
    
    for (int i=0; i<nImages; i++) {
      if ((i + 1) < nImages) {      
        if (current[i] == current[i+1]) {
          toRemove.add(new Integer(i));
          partners.add(new Integer(current[i]));
        } 
      }  
    }
 
    cleanup(toRemove, false);
    
    // need to make sure we caught everything, so call ourselves again

    if (calledOnce) {
      calledOnce = false;
      findOpenImages();      
    }
    else {
      calledOnce = true;
    }        
  }   

  /**
   * Kills any dead windows and makes sure that the ID, name and description
   * lists are accurate.
   */
  private static void cleanup(Vector toRemove, boolean canKill) {
    
    if (canKill) {      
      // first kill in ImageJ
      // the necessity of this step is debatable, but I like having it here
            
      for (int i=0; i<toRemove.size(); i++) {
        int kill = current[((Integer) toRemove.get(i)).intValue()];
        try {
          WindowManager.getImage(kill).getWindow().close();
        }
        catch (Exception e) { }
      }        
    }
    
    // now clean up ID list
   
    if (toRemove.size() > 0) {
      int[] temp = new int[nImages - toRemove.size()];
      String[] nameTemp = new String[nImages - toRemove.size()];
      String[] descrTemp = new String[nImages - toRemove.size()];
      int pt = 0;
   
      for (int i=0; i<toRemove.size(); i++) {
        int start = (i == 0) ? 0 : ((Integer) toRemove.get(i-1)).intValue() + 1;
        int end = ((Integer) toRemove.get(i)).intValue();
           
        System.arraycopy(current, start, temp, pt, end - start);
        System.arraycopy(names, start, nameTemp, pt, end - start);
        System.arraycopy(descriptions, start, descrTemp, pt, end - start);
        pt += (end - start);
      }

      if (pt < temp.length) {
        int start = ((Integer) toRemove.get(toRemove.size() - 1)).intValue()+1;
        System.arraycopy(current, start, temp, pt, temp.length - pt);
        System.arraycopy(names, start, nameTemp, pt, temp.length - pt);
        System.arraycopy(descriptions, start, descrTemp, pt, temp.length - pt);
              
      }        
   
      current = temp;
      names = nameTemp;
      descriptions = descrTemp;
      nImages = current.length;

      toRemove.clear();
    }
  }
      
  /**
   * Checks the Hashtable of OME-XML nodes in OMESidePanel to make sure
   * that every open image has accurate metadata.  Performs corrective
   * steps if this is not the case.
   */
  public static void checkMetadata() {
    Hashtable metadata = OMESidePanel.getTable();

    // step 1 is to check that every open image has metadata in the hashtable

    for (int i=0; i<nImages; i++) {
      Object[] o = (Object[]) metadata.get(new Integer(current[i]));
      if (o == null) {
        o = new Object[2];
        o[0] = new Integer(current[i]);
        o[1] = MetaPanel.exportMeta(descriptions[i], current[i]);
        if (o[1] == null) {
          try {
            // succeeds if this is a database image      
            OMEMetaDataHandler.exportMeta(descriptions[i], current[i]);
          }  
          catch (ArrayIndexOutOfBoundsException e) {
            // fails if this is a disk image opened with the LOCI data browser
            metadata.put(new Integer(current[i]), o);      
          }        
        } 
        else {
          metadata.put(new Integer(current[i]), o);
        }  
      }        
    }
   
    // step 2 is to check that there are no extra IDs in the hashtable
   
    if (metadata.size() > nImages) {
      Enumeration keys = metadata.keys();
      while (keys.hasMoreElements()) {
        int key = ((Integer) keys.nextElement()).intValue();
        boolean found = false;
        for (int j=0; j<nImages; j++) {
          if (current[j] == key) found = true;
        }       

        if (!found) {
          metadata.remove(new Integer(key));
        }        
      }
    }

    OMESidePanel.setTable(metadata);
  }

}
