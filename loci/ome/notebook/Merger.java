//
// Merger.java
//

/*
OME Metadata Notebook application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006-@year@ Christopher Peterson.

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

package loci.ome.notebook;

import org.openmicroscopy.xml.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.io.FileInputStream;

public class Merger {
  // --Constants--

  /** Factory for generating document builders. */
  public static final DocumentBuilderFactory DOC_FACT =
    DocumentBuilderFactory.newInstance();
    
  /** Different mode constants.*/
  public static final int ALL_ORIGINAL = 0x01; // 0001
  public static final int ALL_COMPANION = 0x02; // 0010
  public static final int ORIGINAL_OVER = 0x04; // 0100
  public static final int COMPANION_OVER = 0x08; // 1000

  // --Members--

  protected OMENode ome, compOme, finalOme;
  protected int mode;
  private JComponent comp;

  public Merger(OMENode originalOme, File compFile, JComponent c) {
    ome = originalOme;
    comp = c;
    
    try {
      compOme = new OMENode(compFile);
    }
    catch (Exception exc) { exc.printStackTrace();}
    
    prompt();
    merge();
  }
  
  public OMENode getRoot() {
    return finalOme;
  }
  
  private void merge() {
    if(mode == ALL_ORIGINAL) finalOme = ome;
    else if(mode == ALL_COMPANION) finalOme = compOme;
    else if(mode == ORIGINAL_OVER) {
      finalOme = merge(ome,compOme);
    }
    else if(mode == COMPANION_OVER) {
      finalOme = merge(compOme,ome);
    }
  }
  
  public static OMENode merge(OMENode over, OMENode under) {
    return over;
  }
  
  private void prompt() {
    Object[] possibilities = {"Just use the original file",
      "Just use the companion file", "Merge, original file takes precedence",
      "Merge, companion file takes precedence"};
    String s = (String)JOptionPane.showInputDialog(
      comp.getTopLevelAncestor(),
      "How would you like to merge the companion file "
        + "with the original file?\nIf you choose to merge, "
        + "you must specify which file takes\nprecedence should "
        + "a conflict in the metadata arise.",
      "Merge Mode Selection",
      JOptionPane.QUESTION_MESSAGE,
      (javax.swing.Icon)null,
      possibilities,
      possibilities[3]);
    if ((s != null) && (s.length() > 0)) { 
      if(s.equals(possibilities[0])) mode = ALL_ORIGINAL;
      else if (s.equals(possibilities[1])) mode = ALL_COMPANION;
      else if (s.equals(possibilities[2])) mode = ORIGINAL_OVER;
      else if (s.equals(possibilities[3])) mode = COMPANION_OVER;
    }
    else mode = ALL_ORIGINAL;
  }
}