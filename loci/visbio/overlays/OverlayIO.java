//
// OverlayIO.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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

package loci.visbio.overlays;

import java.awt.Color;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import loci.visbio.VisBio;
import loci.visbio.util.*;

/** Utility methods for saving and loading overlays to and from disk. */
public final class OverlayIO {

  // -- Constants --

  /** String indicating a given field is not applicable to an overlay. */
  protected static final String NOT_APPLICABLE = "N/A";

  // -- Constructor --

  private OverlayIO() { }

  // -- OverlayIO API methods --

  /** Reads the overlays from the given reader. */
  public static Vector[] loadOverlays(BufferedReader in,
    OverlayTransform trans) throws IOException
  {
    String[] dims = trans.getDimTypes();
    int[] lengths = trans.getLengths();
    JComponent owner = trans.getControls();

    Vector[] loadedOverlays = null;
    Vector loadedFreeforms = new Vector();
    int nextFreeformToRestore = 0;
    boolean foundHeader = false;
    int lineNum = 0;
    while (true) {
      lineNum++;
      String line = in.readLine();
      if (line == null) break;
      String trim = line.trim();
      if (trim.startsWith("#")) continue; // skip comments
      if (trim.equals("")) continue; // skip blank lines

      StringTokenizer st = new StringTokenizer("#" + line + "#", "\t");
      int count = st.countTokens();
      if (!foundHeader) {
        // parse table header from first valid line
       int numDims = count - 10; // why 10?
        if (numDims < 0) {
          JOptionPane.showMessageDialog(owner,
            "Invalid table header: insufficient column headings.",
            "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
          return null;
        }
        st.nextToken(); // skip "Overlay" column heading

        // verify lengths and dims match the parent transform
        int[] theLengths = new int[numDims];
        String[] theDims = new String[numDims];
        for (int i=0; i<numDims; i++) {
          String s = st.nextToken();
          int left = s.lastIndexOf(" (");
          int right = s.lastIndexOf(")");
          try {
            theLengths[i] = Integer.parseInt(s.substring(left + 2, right));
            theDims[i] = s.substring(0, left);
          }
          catch (IndexOutOfBoundsException exc) { }
          catch (NumberFormatException exc) { }
        }
        if (!ObjectUtil.arraysEqual(dims, theDims)) {
          JOptionPane.showMessageDialog(owner,
            "Invalid table header: dimensional axis types do not match.",
            "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
          return null;
        }
        if (!ObjectUtil.arraysEqual(lengths, theLengths)) {
          JOptionPane.showMessageDialog(owner,
            "Invalid table header: dimensional axis lengths do not match.",
            "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
          return null;
        }
        // initialize replacement overlay lists
        loadedOverlays = new Vector[MathUtil.getRasterLength(lengths)];
        for (int i=0; i<loadedOverlays.length; i++) {
          loadedOverlays[i] = new Vector();
        }
        foundHeader = true;
      } else {
        // parse table entry
        String type = st.nextToken().substring(1); // remove initial #
        if (type.indexOf(',') == -1) { // if not a line of freeform nodes 
          if (count != lengths.length + 10) {
            System.err.println("Warning: line " + lineNum +
              " has the incorrect number of columns (" + count +
              " instead of " + (lengths.length + 10) + ") and will be ignored.");
            continue;
          }

          int[] pos = new int[lengths.length];
          for (int i=0; i<pos.length; i++) {
            try { pos[i] = Integer.parseInt(st.nextToken()); }
            catch (NumberFormatException exc) {
              pos = null;
              break;
            }
          }
          if (pos == null) {
            System.err.println("Warning: line " + lineNum +
              " has an invalid dimensional position and will be ignored.");
            continue;
          }
          String sx1 = st.nextToken();
          String sy1 = st.nextToken();
          String sx2 = st.nextToken();
          String sy2 = st.nextToken();
          float x1, y1, x2, y2;
          try {
            x1 = sx1.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sx1);
            y1 = sy1.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sy1);
            x2 = sx2.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sx2);
            y2 = sy2.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sy2);
          }
          catch (NumberFormatException exc) {
            System.err.println("Warning: line " + lineNum +
              " has invalid coordinate values and will be ignored.");
            continue;
          }
          String text = st.nextToken();
          Color color = ColorUtil.hexToColor(st.nextToken());
          if (color == null) {
            System.err.println("Warning: line " + lineNum +
              " has an invalid color value and will be ignored.");
            continue;
          }
          boolean filled = st.nextToken().equalsIgnoreCase("true");
          String group = st.nextToken();
          String notes = st.nextToken();
          notes = notes.substring(0, notes.length() - 1); // remove trailing #

          String className = "loci.visbio.overlays.Overlay" + type;
          OverlayObject obj = createOverlay(className, trans, lineNum);
          if (obj == null) continue;
          
          boolean hasNodes = false;
          if (obj instanceof OverlayFreeform) {
            loadedFreeforms.add(obj);
            hasNodes = true;
          }

          // assign overlay parameters
          int r = MathUtil.positionToRaster(lengths, pos);
          if (r < 0 || r >= loadedOverlays.length) {
            System.err.println("Warning: could not reconstruct " + type +
              "overlay defined on line " + lineNum +
              ": invalid dimensional position.");
            continue;
          }
          
          obj.x1 = x1;
          obj.y1 = y1;
          obj.x2 = x2;
          obj.y2 = y2;
          obj.text = text;
          obj.color = color;
          obj.filled = filled;
          obj.group = group;
          obj.notes = notes;
          obj.hasNodes = hasNodes; // necessary to distinguish freeforms
          obj.drawing = false;
          obj.selected = false;
          obj.computeGridParameters();

          // add overlay to list
          loadedOverlays[r].add(obj);
        } else {
          // parse freeform nodes
          String nodeList = type.substring (0, type.indexOf('#'));
          StringTokenizer st2 = new StringTokenizer (nodeList, " ");
          int numNodes = st2.countTokens();
          float[][] nodes = new float[2][numNodes];

          int i = 0;
          while (st2.hasMoreTokens()) {
            String node = st2.nextToken();
            int comma = node.indexOf(',');
            String x, y;
            try {
              x = node.substring(1, comma);
              y = node.substring(comma + 1, node.length()-1);
              nodes[0][i] = Float.parseFloat(x);        
              nodes[1][i] = Float.parseFloat(y);
              i++;
            } catch (StringIndexOutOfBoundsException exc) { // if no comma, extra spaces
              JOptionPane.showMessageDialog(owner, "Invalid overlay file: malformatted Freeform node list.\n" 
                  + "Error found near following string: " + node + "", "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
              return null;
            } catch (NumberFormatException exc) { // if parseFloat fails
              JOptionPane.showMessageDialog(owner, "Invalid overlay file: malformatted Freeform node list.\n" 
                  + "Error found near following string: " + node + "", "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
              return null;
            }
          }

          // set nodes of corresponding freeform 
          OverlayFreeform f =  (OverlayFreeform) loadedFreeforms.elementAt(nextFreeformToRestore++);
          f.setNodes(nodes);
        }
      }
    }

    if (nextFreeformToRestore < loadedFreeforms.size()) {
      JOptionPane.showMessageDialog(owner, "Invalid overlay file: missing node lists for one or more Freeforms.", 
          "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
      return null;
    }
    
    if (loadedOverlays == null) {
      JOptionPane.showMessageDialog(owner,
        "Invalid overlay file: no table header found.",
        "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
      return null;
    }
    trans.setTextDrawn(true);
    return loadedOverlays;
  }

  /** Writes the overlays to the given writer. */
  public static void saveOverlays(PrintWriter out, OverlayTransform trans) {
    String[] dims = trans.getDimTypes();
    int[] lengths = trans.getLengths();
    Vector[] overlays = trans.overlays;
    Vector savedFreeforms = new Vector();

    // file header
    out.println("# " + VisBio.TITLE + " " + VisBio.VERSION +
      " overlay file written " + new Date());
    out.println();

    // table header
    out.print("Overlay\t");
    for (int p=0; p<lengths.length; p++) {
      out.print(dims[p] + " (" + lengths[p] + ")\t");
    }
    out.println("x1\ty1\tx2\ty2\ttext\tcolor\tfilled\tgroup\tnotes");

    // overlays table
    for (int i=0; i<overlays.length; i++) {
      int[] pos = MathUtil.rasterToPosition(lengths, i);
      StringBuffer sb = new StringBuffer();
      for (int p=0; p<pos.length; p++) sb.append(pos[p] + "\t");
      String posString = sb.toString();
      for (int j=0; j<overlays[i].size(); j++) {
        OverlayObject obj = (OverlayObject) overlays[i].elementAt(j);
        if (obj instanceof OverlayFreeform) savedFreeforms.add(obj);
        out.print(obj.toString());
        out.print("\t");
        out.print(posString);
        out.print(obj.hasEndpoint() ? "" + obj.x1 : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.hasEndpoint() ? "" + obj.y1 : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.hasEndpoint2() ? "" + obj.x2 : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.hasEndpoint2() ? "" + obj.y2 : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.hasText() ? obj.text : NOT_APPLICABLE);
        out.print("\t");
        out.print(ColorUtil.colorToHex(obj.color));
        out.print("\t");
        out.print(obj.canBeFilled() ? "" + obj.filled : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.group.replaceAll("\t", " "));
        out.print("\t");
        out.println(obj.notes.replaceAll("\t", " "));
      }
    }

    // nodes of freeforms, one freeform per line
    for (int i=0; i<savedFreeforms.size(); i++) {
      OverlayFreeform of = (OverlayFreeform) savedFreeforms.get(i);
      float[][] nodes = of.getNodes();
      for (int j=0; j<nodes[0].length; j++) {
        out.print("("+nodes[0][j]+","+nodes[1][j]+") ");
      }
      out.println();
    } 
  }

  /** Instantiates an overlay object of the given class. */
  public static OverlayObject createOverlay(String className,
    OverlayTransform trans)
  {
    return createOverlay(className, trans, 0);
  }

  /** Instantiates an overlay object of the given class. */
  public static OverlayObject createOverlay(String className,
    OverlayTransform trans, int lineNum)
  {
    String classError = "Warning: could not reconstruct overlay " +
      "of class " + className;
    if (lineNum > 0) classError += " defined on line " + lineNum;
    classError += ": ";
    OverlayObject obj = null;
    try {
      Class c = Class.forName(className);
      Constructor con = c.getConstructor(
        new Class[] {OverlayTransform.class});
      obj = (OverlayObject) con.newInstance(new Object[] {trans});
      if (obj == null) {
        System.err.println(classError +
          "constructor for class " + className + " returned null object.");
      }
    }
    catch (ClassCastException exc) {
      System.err.println(classError +
        "class " + className + " does not extend OverlayObject.");
    }
    catch (ClassNotFoundException exc) {
      System.err.println(classError +
        "class " + className + " not found.");
    }
    catch (IllegalAccessException exc) {
      System.err.println(classError +
        "cannot access constructor for class " + className + ".");
    }
    catch (InstantiationException exc) {
      System.err.println(classError +
        "cannot instantiate class " + className + ".");
    }
    catch (InvocationTargetException exc) {
      System.err.println(classError +
        "error invoking constructor for class " + className + ".");
    }
    catch (NoSuchMethodException exc) {
      System.err.println(classError +
        "no appropriate constructor for class " + className + ".");
    }
    return obj;
  }

}
