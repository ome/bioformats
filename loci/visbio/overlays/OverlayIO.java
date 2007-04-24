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
import loci.formats.FormatTools;
import loci.visbio.state.BooleanOption;
import loci.visbio.state.OptionManager;
import loci.visbio.util.*;
import loci.visbio.VisBio;
import loci.visbio.VisBioFrame;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.Region;

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
    Vector loadedNodedObjects = new Vector();
    boolean nodesChanged = false; // used in event INIT, state NODES
    int numberOfNodedObjectsRead = 0;
    int numNodedObjectsRestored = 0;

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
          loadedOverlays = new Vector[FormatTools.getRasterLength(lengths)];
          for (int i=0; i<loadedOverlays.length; i++) {
            loadedOverlays[i] = new Vector();
          }

        } else if (state == NODES) {
          if (numberOfNodedObjectsRead == loadedNodedObjects.size()) {
            String s = "more \"Noded Object\" (Freeforms, Polylines) node"
              + " lists " + "than Noded Objects (" + numberOfNodedObjectsRead
              + ") specified in table";
            displayErrorMsg(owner, lineNum, s);
            return null;
          }
          // store nodes of previously read freeform
          if (nodesChanged) {
            OverlayNodedObject ono = (OverlayNodedObject)
              loadedNodedObjects.elementAt(numNodedObjectsRestored++);
            float[][] temp = new float[2][numNodes];
            for (int i=0; i<2; i++) System.arraycopy(nodes[i], 0, temp[i], 0,
                numNodes);
            ono.setNodes(temp);
            nodes = new float[2][50];
            numNodes = 0;
          }
          numberOfNodedObjectsRead++;
          nodesChanged = true;
        }

      } else if (event == PARSE) {
        if (state == TABLE) {
          StringTokenizer st = new StringTokenizer("#" + line + "#", "\t");
          int count = st.countTokens();
          String type = st.nextToken().substring(1); // remove initial #
          int tok = 0;

          if (count != lengths.length + 10) { 
            // 10 == number of non-dim. fields in the overlay description
            String s = "line in data table has an insufficient number of " +
              "fields (" + count + " instead of " + (lengths.length + 10) + ")";
            displayErrorMsg(owner, lineNum, s);
            return null;
          }

          int[] pos = new int[lengths.length];
          for (int i=0; i<pos.length; i++) {
            try {
              int p = Integer.parseInt(st.nextToken());
              if (p > 0 && p <= lengths[i]) { 
                // Is coordinate within range?
                // Remember, in the overlay.txt files dimensional position
                // coordinates are indexed from 1; inside visbio, 
                // they're indexed from 0.
                pos[i] = p-1; // shift from external to internal indexing 
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
            displayErrorMsg(owner, lineNum, 
                "line has an invalid dimensional position");
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
            displayErrorMsg(owner, lineNum, 
                "line has invalid coordinate values");
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

          if (obj instanceof OverlayNodedObject) loadedNodedObjects.add(obj);

          int r = FormatTools.positionToRaster(lengths, pos);
          //System.out.print("["); // TEMP
          //for (int i=0; i< pos.length; i++) System.out.print(i + " "); // TEMP
          //System.out.println("]"); // TEMP
          //System.out.println("r = " + r); // TEMP
          // this error should never fire--will be caught above ("is coordinate
          // w/in range?")
          /*
          if (r < 0 || r >= loadedOverlays.length) {
            displayErrorMsg(owner, lineNum, "could not reconstruct overlay:
            invalid dimensional position");
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
          obj.drawing = false;
          obj.selected = false;

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
            // this error message won't fire: covered by regular expressions in
            // getEventAndState
            displayErrorMsg(owner, lineNum, "error parsing node coordinates");
            return null;
          }

          numNodes++;
          nodes[0][numNodes-1] = x;
          nodes[1][numNodes-1] = y;

          if (numNodes == nodes[0].length) {
            float[][] temp = new float[2][numNodes*2];
            for (int i=0; i<2; i++) 
              System.arraycopy(nodes[i], 0, temp[i], 0, numNodes);
            nodes = temp;
          }
        }
      } // end (event == PARSE)
    } // end while(true)

    // after parsing all lines:
    if (!foundOverlays) {
      displayErrorMsg(owner, lineNum, "no overlays found");
      return null;
    } 
    else if (loadedNodedObjects.size() > 0) {
      if (numNodedObjectsRestored + 1 < loadedNodedObjects.size()) {
        displayErrorMsg(owner, lineNum, 
            "missing node lists for one or more Freeforms");
        return null;
      } 
      else {
        // store last freeform read
        OverlayNodedObject ono = (OverlayNodedObject)
          loadedNodedObjects.elementAt(numNodedObjectsRestored++);
        float[][] temp = new float[2][numNodes];
        for (int i=0; i<2; i++) System.arraycopy(nodes[i], 0, temp[i], 0,
            numNodes);
        ono.setNodes(temp);
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
    int freeformCount = 0;
    int polylineCount = 0;

    Vector lines = new Vector();
    Vector markers = new Vector();
    Vector freeforms = new Vector();
    Vector texts = new Vector();
    Vector ovals = new Vector();
    Vector boxes = new Vector();
    Vector arrows = new Vector();
    Vector polylines = new Vector();

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
      int[] pos = FormatTools.rasterToPosition(lengths, i);
      StringBuffer sb = new StringBuffer();
      // add 1 to shift indices for humans
      for (int p=0; p<pos.length; p++) sb.append((pos[p]+1) + "\t");
      String posString = sb.toString();
      for (int j=0; j<overlays[i].size(); j++) {
        OverlayObject obj = (OverlayObject) overlays[i].elementAt(j);
        
        if (obj instanceof OverlayLine) lines.add(obj);
        if (obj instanceof OverlayFreeform) freeforms.add(obj);
        if (obj instanceof OverlayMarker) markers.add(obj);
        if (obj instanceof OverlayText) texts.add(obj);
        if (obj instanceof OverlayOval) ovals.add(obj);
        if (obj instanceof OverlayBox) boxes.add(obj);
        if (obj instanceof OverlayArrow) arrows.add(obj);
        if (obj instanceof OverlayPolyline) polylines.add(obj);

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

    // print stats by object type
    Vector[] vectors = {lines, freeforms, markers, texts, ovals, boxes, 
      arrows, polylines};
    String[] titles = OverlayStat.getOverlayTypes();
    for (int v=0; v<vectors.length; v++) {
      if (vectors[v].size() > 0) {
        out.println(); // Throw in a blank
        out.println("# " + titles[v] + " Statistics");
        for (int i=0; i<vectors[v].size(); i++) {
          OverlayObject obj = (OverlayObject) vectors[v].get(i);
          int index = i + 1;
          out.println("# " + titles[v] + " " + index);
          String[] stats = OverlayStat.getStatTypes(titles[v]);
          OptionManager om = (OptionManager)
            VisBioFrame.getVisBio().getManager(OptionManager.class);
          for (int j=0; j<stats.length; j++) {
            String name = titles[v] + "." + stats[j];
            BooleanOption option = (BooleanOption) om.getOption(name);
            // print if option is selected
            if (option.getValue()) {
              out.println("#\t" + stats[j] + "\t" +
                obj.getStat(stats[j]));
            }
          }
        }
      }
    }
   
    // nodes of noded objects, one node per line
    for (int i=0; i<freeforms.size() + polylines.size(); i++) {
      // get the noded object from the appropriate Vector
      OverlayNodedObject ono;
      if (i < freeforms.size()) {
        ono = (OverlayNodedObject) freeforms.get(i);
      }
      else {
        ono = (OverlayNodedObject) polylines.get(i - freeforms.size());
      }

      out.println();
      float xx1, xx2, yy1, yy2;
      xx1 = ono.getX();
      yy1 = ono.getY();
      xx2 = ono.getX2();
      yy2 = ono.getY2();
      
      // nodes header
      int k = 0;
      if (ono instanceof OverlayFreeform) k = ++freeformCount;
      else if (ono instanceof OverlayPolyline) k = ++polylineCount;

      out.println("# " + ono + " " + k + " nodes:");

      out.println("X\tY");
      // print the nodes themselves
      for (int j=0; j<ono.getNumNodes(); j++) {
        float[] c = ono.getNodeCoords(j);
        out.println(c[0]+"\t"+c[1]);
      }
    }
  }

  /** Saves overlays to a .xls workbook */
  public static HSSFWorkbook exportOverlays (OverlayTransform overlay) {
    String[] dims = overlay.getDimTypes();
    int[] lengths = overlay.getLengths();

    Vector[] overlays = overlay.overlays;

		Vector lines = new Vector();
		Vector markers = new Vector();
		Vector freeforms = new Vector();
		Vector texts = new Vector();
		Vector ovals = new Vector();
		Vector boxes = new Vector();
		Vector arrows = new Vector();
		Vector polylines = new Vector();

    // initialize worksheet
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet s = wb.createSheet();
    HSSFRow r = null;
    HSSFCell c = null;

    // create cell styles 
    HSSFCellStyle text = wb.createCellStyle();
    text.setDataFormat(HSSFDataFormat.getBuiltinFormat("text")); 

    HSSFCellStyle integer = wb.createCellStyle();
    integer.setDataFormat((short) 1);

    HSSFCellStyle flt = wb.createCellStyle();
    flt.setDataFormat((short) 0); // "general" format
    
    HSSFCellStyle hex = wb.createCellStyle();
    hex.setDataFormat((short) 0); // "general" format

    // write file header
    int rownum = 0;

    String header = VisBio.TITLE + " " + VisBio.VERSION +
      " overlay file written " + new Date();

    // try to estimate number of cells to merge
    // short width = s.getDefaultColumnWidth();
    // int numChars = header.length();
    // short numColsToMerge = (short) Math.ceil((float) numChars/(float)
    // (width));
    
    short numColsToMerge = 12; // number of columns in overlay table header

    Region mergedCells = new Region (0, (short) 0, 0, numColsToMerge);
    s.addMergedRegion(mergedCells);

    r = s.createRow(rownum);
    c = r.createCell((short) 0);

    c.setCellStyle(text);
    c.setCellValue(new HSSFRichTextString(header));

    // write table header
  
    short cellnum = 0;
    r = s.createRow(++rownum);
    c = r.createCell(cellnum);

    c.setCellStyle(text);
    c.setCellValue(new HSSFRichTextString("Overlay"));

    cellnum = 1;
    for (int i = 0; i<lengths.length; i++) {
      c = r.createCell(cellnum++);
      c.setCellStyle(text);
      c.setCellValue(new HSSFRichTextString(dims[i] + " (" + lengths[i] + ")"));
    }

    String[] colHeaders = {"x1", "y1", "x2", "y2", "text", "color", "filled",
      "group", "notes"};
    for (int i=0; i<colHeaders.length; i++) {
      c = r.createCell(cellnum++);
      c.setCellStyle(text);
      c.setCellValue(new HSSFRichTextString(colHeaders[i]));
    }

    // overlays table
    for (int i=0; i<overlays.length; i++) {
      int[] pos = FormatTools.rasterToPosition(lengths, i);
      
      for (int j=0; j<overlays[i].size(); j++) {
        cellnum = 0;
        // make new row
        r = s.createRow(++rownum);
        
        OverlayObject obj = (OverlayObject) overlays[i].elementAt(j);

        // a 'rider' to this loop: keep track of noded objects
        if (obj instanceof OverlayLine) lines.add(obj);
        if (obj instanceof OverlayFreeform) freeforms.add(obj);
        if (obj instanceof OverlayMarker) markers.add(obj);
        if (obj instanceof OverlayText) texts.add(obj);
        if (obj instanceof OverlayOval) ovals.add(obj);
        if (obj instanceof OverlayBox) boxes.add(obj);
        if (obj instanceof OverlayArrow) arrows.add(obj);
        if (obj instanceof OverlayPolyline) polylines.add(obj);

        // overlay object type
        c = r.createCell(cellnum++);
        c.setCellStyle(text);
        c.setCellValue(new HSSFRichTextString(obj.toString()));

        // object dimensional position
        for (int p=0; p<pos.length; p++) {
          c = r.createCell(cellnum++);
          c.setCellStyle(integer);
          c.setCellValue(pos[p] + 1); // add 1 to shift indices for humans
        }

        // x1
        c = r.createCell(cellnum++);
        if (obj.hasEndpoint()) { 
          c.setCellStyle(flt);
          c.setCellValue(obj.x1);
        }
        else {
          c.setCellStyle(text);
          c.setCellValue(new HSSFRichTextString(NOT_APPLICABLE));
        }
        
        // y1 
        c = r.createCell(cellnum++);
        if (obj.hasEndpoint()) { 
          c.setCellStyle(flt);
          c.setCellValue(obj.y1);
        }
        else {
          c.setCellStyle(text);
          c.setCellValue(new HSSFRichTextString(NOT_APPLICABLE));
        }

        // x2
        c = r.createCell(cellnum++);
        if (obj.hasEndpoint2()) { 
          c.setCellStyle(flt);
          c.setCellValue(obj.x2);
        }
        else {
          c.setCellStyle(text);
          c.setCellValue(new HSSFRichTextString(NOT_APPLICABLE));
        }

        // y2 
        c = r.createCell(cellnum++);
        if (obj.hasEndpoint2()) { 
          c.setCellStyle(flt);
          c.setCellValue(obj.y2);
        }
        else {
          c.setCellStyle(text);
          c.setCellValue(new HSSFRichTextString(NOT_APPLICABLE));
        }

        // object text
        c = r.createCell(cellnum++);
        c.setCellStyle(text);
        c.setCellValue(new HSSFRichTextString(
              obj.hasText() ? obj.text : NOT_APPLICABLE));

        // object color
        c = r.createCell(cellnum++);
        c.setCellStyle(hex);
        c.setCellValue(new HSSFRichTextString(ColorUtil.colorToHex(obj.color)));

        // object filled
        c = r.createCell(cellnum++);
        c.setCellStyle(text);
        c.setCellValue(new HSSFRichTextString(
              obj.canBeFilled() ? "" + 
              (obj.filled ? "true" : "false") : NOT_APPLICABLE));
        
        // object group
        c = r.createCell(cellnum++);
        c.setCellStyle(text);
        c.setCellValue(new HSSFRichTextString(obj.group.replaceAll("\t", " ")));

        // object notes
        c = r.createCell(cellnum++);
        c.setCellStyle(text);
        c.setCellValue(new HSSFRichTextString(obj.notes.replaceAll("\t", " ")));
      }
    }
    
    // write overlay statistics
    Vector[] vectors = {lines, freeforms, markers, texts, ovals, boxes, 
      arrows, polylines};
    String[] titles = OverlayStat.getOverlayTypes(); 
    for (int v=0; v<vectors.length ; v++) {
      if (vectors[v].size() > 0) {
        rownum += 2;
        r = s.createRow(rownum);
        cellnum = 0;
        c = r.createCell(cellnum++);
        c.setCellStyle(text);
        c.setCellValue(new HSSFRichTextString(titles[v] + " Statistics"));
                  
        for (int i=0; i<vectors[v].size(); i++) {
          OverlayObject obj = (OverlayObject) vectors[v].get(i);
          int index = i + 1; // index from 1 for readability
          
          cellnum = 0;
          r = s.createRow(++rownum);
          c = r.createCell(cellnum++);
          c.setCellStyle(text);
          c.setCellValue(new HSSFRichTextString(titles[v] + " " + index));
          
          String[] statTypes = OverlayStat.getStatTypes(titles[v]);
          OptionManager om = (OptionManager)
            VisBioFrame.getVisBio().getManager(OptionManager.class);

          for (int j=0; j<statTypes.length; j++) {
            String name = titles[v] + "." + statTypes[j];
            BooleanOption option = (BooleanOption) om.getOption(name);
            // print if option is selected
            if (option.getValue()) {
              r = s.createRow(++rownum);
              cellnum = 1; // indent one column
              c = r.createCell(cellnum++);
              c.setCellStyle(text);
              c.setCellValue(new HSSFRichTextString(statTypes[j]));
              
              c = r.createCell(cellnum++);
              c.setCellStyle(text);
              c.setCellValue(new HSSFRichTextString(obj.getStat(statTypes[j])));
            }
          } 
        }
      }
    } 

    // write nodes of noded objects
    int freeformCount = 0;
    int polylineCount = 0;
    
    rownum += 2; // skip a row
    for (int i = 0; i<freeforms.size() + polylines.size(); i++) {
      OverlayNodedObject ono;
      int k = 0;
      if (i < freeforms.size()){
        ono = (OverlayNodedObject) freeforms.get(i);
        k = ++freeformCount;
      }
      else {
        ono = (OverlayNodedObject) polylines.get(i - freeforms.size());
        k = ++polylineCount;
      }

      // write nodes header
      int numNodes = ono.getNumNodes();
      r = s.createRow(rownum++);
      c = r.createCell((short) 0);
      c.setCellStyle(text);

      float xx1, xx2, yy1, yy2;
      xx1 = ono.getX();
      yy1 = ono.getY();
      xx2 = ono.getX2();
      yy2 = ono.getY2();
            
      String hdr = ono + " " + k;
      c.setCellValue(new HSSFRichTextString(hdr));
      r = s.createRow(rownum++);
      c = r.createCell((short) 0);
      c.setCellStyle(text);
      c.setCellValue(new HSSFRichTextString("X"));
      c = r.createCell((short) 1);
      c.setCellStyle(text);
      c.setCellValue(new HSSFRichTextString("Y"));

      // write the nodes themselves
      for (int j = 0; j<numNodes; j++) {
        float[] node = ono.getNodeCoords(j);
        r = s.createRow(rownum++);
        c = r.createCell((short) 0);
        c.setCellStyle(flt);
        c.setCellValue(node[0]);

        c = r.createCell((short) 1);
        c.setCellStyle(flt);
        c.setCellValue(node[1]);
      }
      rownum++; // skip a row
    }

    return wb;
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
    // at each input (a line of text), the machine changes state and spits out
    // an event telling the method loadOverlays to parse the line, display an
    // error message, etc.
    // This method getEventTypeAndNewState describes the state machine's
    // behavior: each top-level if/elseif clause corresponds to a different
    // state, and the interior if/elseif/else clauses describe possible
    // transitions from that state.
    //
    // As a result of using the state machine approach, I've managed to put
    // most the error messages for unexpected lines in one place (if (event ==
    // BARF) under loadOverlays); however there are still many cases which
    // generate errors elsewhere in loadOverlays.
    // Adding more states to the machine and/or more rigorous checking for
    // acceptable line formats in this machine would help reduce the number of
    // exceptional cases not handled here.  
    
    int state = WAIT, event = BARF;
    if (current == WAIT) {
      if (input.matches("^\\s*$") || input.startsWith("#")) {
        state = WAIT; event = IGNORE;
      }
      else if (input.startsWith("Overlay")) {
        state = TABLE; event = INIT;
      }
      else {
        state = WAIT; event = BARF;
      }
    } else if (current == TABLE) {
      if (input.equals("")) {
        state = TABLE; event = IGNORE;
      }
      else if (input.matches("# Freeform \\d+ nodes:")
          || input.matches("# Polyline \\d+ nodes:"))
      {
        state = NODES; event = INIT;
      }
      else if (input.startsWith("Line") || input.startsWith("Freeform")
          || input.startsWith("Marker") || input.startsWith("Text") ||
          input.startsWith("Oval") || input.startsWith("Box") ||
          input.startsWith("Arrow") || input.startsWith("Polyline")) {
        state = TABLE; event = PARSE;
      }
      else if (input.startsWith("#")) {state = TABLE; event = IGNORE;} 
      // must check for freeform header first
      else {
        event = BARF; state = TABLE;
      }
    } else if (current == NODES) {
      if (input.equals("")) {
        state = NODES; event = IGNORE;
      }
      else if (input.matches("# Freeform \\d+ nodes:") || 
          input.matches("# Polyline \\d+ nodes:")) {
        state = NODES; event = INIT;
      }
      else if (input.startsWith("#") || input.matches("^[Xx]\t[Yy]")) {
        state = NODES; event = IGNORE;
      }
      else if (input.matches("^[0-9]+\\.[0-9]+\\s[0-9]+\\.[0-9]+$")) {
        state = NODES; event = PARSE;
      }
      else {
        state = NODES; event = BARF;
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
