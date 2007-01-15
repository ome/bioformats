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

  // file parsing state machine states and events
  // states
  protected static final int WAIT = 0;
  protected static final int TABLE = 1;
  protected static final int NODES = 2;

  // events
  protected static final int BARF = -1;
  protected static final int IGNORE = 0;
  protected static final int PARSE = 1;
  protected static final int INIT = 2;

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

    // stores all overlays
    Vector[] loadedOverlays = null;
    boolean foundOverlays = false;

    // tracks addresses of stored freeforms
    Vector loadedFreeforms = new Vector();
    boolean nodesChanged = false; // used in event INIT, state NODES
    int numberOfFreeformsRead = 0;
    int numFreeformsRestored = 0;

    // tracks line number for error messages
    int lineNum = 0;

    // stores freeform nodes as they're read from file
    float[][] nodes = new float[2][50];
    int numNodes = 0;

    int state = WAIT;

    // read file line by line
    // actions are grouped by event type, then by current state
    while (true) {
      lineNum++;

      String line = in.readLine();
      if (line == null) break;
      String trim = line.trim();

      int[] eventState = getEventTypeAndNewState(trim, state);
      int event = eventState[0];
      state = eventState[1];

      if (event == IGNORE) continue;

      else if (event == BARF) {
        // barf appropriately based on state
        String s = "";
        if (state == TABLE) s = "invalid line in overlay table";
        else if (state == NODES) s = "invalid line in freeform node lists";
        else if (state == WAIT) s = "invalid line before overlay data";

        displayErrorMsg(owner, lineNum, s);
        return null;

      } else if (event == INIT) {
        if (state == TABLE) {
          StringTokenizer st = new StringTokenizer("#" + line + "#", "\t");
          int count = st.countTokens();
          // parse table header from first valid line
          // 12: number of non-dim. fields in overlay description
          int numDims = count - 10;

          if (numDims < 0) {
            displayErrorMsg(owner, lineNum, "insufficient column headings");
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
            displayErrorMsg(owner, lineNum,
              "dimensional axis types do not match");
            return null;
          }
          if (!ObjectUtil.arraysEqual(lengths, theLengths)) {
            displayErrorMsg(owner, lineNum,
              "dimensional axis lengths do not match");
            return null;
          }

          // initialize replacement overlay lists
          loadedOverlays = new Vector[MathUtil.getRasterLength(lengths)];
          for (int i=0; i<loadedOverlays.length; i++) {
            loadedOverlays[i] = new Vector();
          }

        } else if (state == NODES) {
          if (numberOfFreeformsRead == loadedFreeforms.size()) {
            String s = "more Freeform node lists than Freeforms (" + numberOfFreeformsRead
              + ") specified in table";
            displayErrorMsg(owner, lineNum, s);
            return null;
          }

          // store nodes of previously read freeform
          if (nodesChanged) {
            OverlayFreeform of = (OverlayFreeform) loadedFreeforms.elementAt(numFreeformsRestored++);
            float[][] temp = new float[2][numNodes];
            for (int i=0; i<2; i++) System.arraycopy(nodes[i], 0, temp[i], 0, numNodes);
            of.setNodes(temp);
            nodes = new float[2][50];
            numNodes = 0;
          }
          numberOfFreeformsRead++;
          nodesChanged = true;
        }

      } else if (event == PARSE) {
        if (state == TABLE) {
          StringTokenizer st = new StringTokenizer("#" + line + "#", "\t");
          int count = st.countTokens();
          String type = st.nextToken().substring(1); // remove initial #
          int tok = 0;

          if (count != lengths.length + 10) { // 10 == number of non-dim. fields in the overlay description
            String s = "line in data table has an insufficient number of fields (" + count + " instead of "
              + (lengths.length + 10) + ")";
            displayErrorMsg(owner, lineNum, s);
            return null;
          }

          int[] pos = new int[lengths.length];
          for (int i=0; i<pos.length; i++) {
            try {
              int p = Integer.parseInt(st.nextToken());
              if (p >= 0 && p < lengths[i]) { // is coordinate within range?
                pos[i] = p;
                tok++;
              } else {
                pos = null;
                break;
              }
            }
            catch (NumberFormatException exc) {
              pos = null;
              break;
            }
          }

          if (pos == null) {
            displayErrorMsg(owner, lineNum, "line has an invalid dimensional position");
            return null;
          }

          String sx1, sx2, sy1, sy2;

          sx1 = st.nextToken();
          sy1 = st.nextToken();
          sx2 = st.nextToken();
          sy2 = st.nextToken();

          float x1, y1, x2, y2;

          try {
            x1 = sx1.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sx1);
            y1 = sy1.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sy1);
            x2 = sx2.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sx2);
            y2 = sy2.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sy2);
          }

          catch (NumberFormatException exc) {
            displayErrorMsg(owner, lineNum, "line has invalid coordinate values");
            return null;
          }

          String text = st.nextToken();

          Color color;
          try {
            color = ColorUtil.hexToColor(st.nextToken());
          } catch (NumberFormatException exc) {
            displayErrorMsg(owner, lineNum, "line has invalid color value");
            continue;
          }

          boolean filled = false;
          filled = st.nextToken().equalsIgnoreCase("true");

          String group = st.nextToken();

          String notes = "";
          notes = st.nextToken();
          notes = notes.substring(0, notes.length() - 1); // remove trailing #

          String className = "loci.visbio.overlays.Overlay" + type;
          OverlayObject obj = createOverlay(className, trans, lineNum);
          if (obj == null) continue;

          boolean hasNodes = false;
          if (obj instanceof OverlayFreeform) {
            loadedFreeforms.add(obj);
            hasNodes = true;
          }

          int r = MathUtil.positionToRaster(lengths, pos);
          // this error should never fire--will be caught above ("is coordinate w/in range?")
          /*
          if (r < 0 || r >= loadedOverlays.length) {
            displayErrorMsg(owner, lineNum, "could not reconstruct overlay: invalid dimensional position");
            return null;
          }
          */

          // assign overlay parameters
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
          foundOverlays = true;
        } else if (state == NODES) {

          String[] toks = trim.split("\\s");// split on whitespace

          float x, y;
          try {
            x = Float.parseFloat(toks[0]);
            y = Float.parseFloat(toks[1]);
          }
          catch (NumberFormatException exc) {
            // this error message won't fire: covered by regular expressions in getEventAndState
            displayErrorMsg(owner, lineNum, "error parsing node coordinates");
            return null;
          }

          numNodes++;
          nodes[0][numNodes-1] = x;
          nodes[1][numNodes-1] = y;

          if (numNodes == nodes[0].length) {
            float[][] temp = new float[2][numNodes*2];
            for (int i=0; i<2; i++) System.arraycopy(nodes[i], 0, temp[i], 0, numNodes);
            nodes = temp;
          }
        }
      } // end (event == PARSE)
    } // end while(true)

    // after parsing all lines:
    if (!foundOverlays) {
      displayErrorMsg(owner, lineNum, "no overlays found");
      return null;
    } else if (loadedFreeforms != null) {
      if (numFreeformsRestored + 1 < loadedFreeforms.size()) {
        displayErrorMsg(owner, lineNum, "missing node lists for one or more Freeforms");
        return null;
      } else {
        // store last freeform read
        OverlayFreeform of = (OverlayFreeform) loadedFreeforms.elementAt(numFreeformsRestored++);
        float[][] temp = new float[2][numNodes];
        for (int i=0; i<2; i++) System.arraycopy(nodes[i], 0, temp[i], 0, numNodes);
        of.setNodes(temp);
      }
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

    // nodes of freeforms, one node per line
    for (int i=0; i<savedFreeforms.size(); i++) {
      OverlayFreeform of = (OverlayFreeform) savedFreeforms.get(i);
      out.println();
      float xx1, xx2, yy1, yy2;
      xx1 = of.getX();
      yy1 = of.getY();
      xx2 = of.getX2();
      yy2 = of.getY2();
      // nodes header
      out.println("# Freeform line " + i + " (" + xx1 + "," + yy1 + ")(" + xx2 + "," + yy2 + ")");
      out.println("X\tY");
      float[][] nodes = of.getNodes();
      for (int j=0; j<nodes[0].length; j++) {
        out.println(nodes[0][j]+"\t"+nodes[1][j]);
      }
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

  // -- Private helper methods --

  /** Method for determining how to handle next line read from input file */
  private static int[] getEventTypeAndNewState(String input, int current) {
    // logic for parsing overlays file --ACS 12/06
    //
    // I visualized the process of parsing an overlay file as a 'state machine':
    // at each input (a line of text), the machine changes state and spits out an event
    // telling the method loadOverlays to parse the line, display an error message, etc.
    // This method getEventTypeAndNewState describes the state machine's behavior: each
    // top-level if/elseif clause corresponds to a different state, and the interior
    // if/elseif/else clauses describe possible transitions from that state.
    //
    // As a result of the state machine, I've managed to put most the error messages for
    // unexpected lines in one place (if (event == BARF) under loadOverlays); however
    // there are still many cases which generate errors elsewhere in loadOverlays.
    // Adding more states to the machine and/or more rigorous checking for acceptable line formats
    // in this machine would help reduce the number of exceptional cases not handled here.

    int state = WAIT, event = BARF;
    if (current == WAIT) {
      if (input.matches("^\\s*$") || input.startsWith("#")) {state = WAIT; event = IGNORE;}
      else if (input.startsWith("Overlay")) {state = TABLE; event = INIT;}
      else {state = WAIT; event = BARF;}
    } else if (current == TABLE) {
      if (input.equals("")) {state = TABLE; event = IGNORE;}
      else if (input.matches("^\\s*#\\s*[Ff][Rr][Ee][Ee][Ff][Oo][Rr][Mm].*")) {state = NODES; event = INIT;}
      else if (input.startsWith("Line") || input.startsWith("Freeform")
          || input.startsWith("Marker") || input.startsWith("Text") || input.startsWith("Oval")
          || input.startsWith("Box") || input.startsWith("Arrow")) {
        state = TABLE;
        event = PARSE;
      }
      else if (input.startsWith("#")) {state = TABLE; event = IGNORE;} // must check for freeform header first
      else {
        event = BARF;
        state = TABLE;
      }
    } else if (current == NODES) {
      if (input.equals("")) {state = NODES; event = IGNORE;}
      else if (input.matches("^\\s*#\\s*[Ff][Rr][Ee][Ee][Ff][Oo][Rr][Mm].*")) {state = NODES; event = INIT;}
      else if (input.startsWith("#") || input.matches("^[Xx]\t[Yy]")) {state = NODES; event = IGNORE;}
      else if (input.matches("^[0-9]+\\.[0-9]+\\s[0-9]+\\.[0-9]+$")) {state = NODES; event = PARSE;}
      else {
        state = NODES;
        event = BARF;
      }
    }

    int[] retvals = {event, state};
    return retvals;
  }

  /** Displays an alarm box */
  private static void displayErrorMsg(JComponent owner, int line, String msg) {
    JOptionPane.showMessageDialog(owner, "Invalid overlay file: "
      + msg + "\n" + "Error in line " + line ,
      "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
  }

}
