package com.sun.jimi.core.util.x11;

import java.io.*;
import java.util.*;
import java.awt.Color;

import java.util.Hashtable;

/**
 * X11 bitmap (xpm) parser
 *
 * Parse files of the form:
 * <pre>
 *  static char * fee_m_pm[] = {
 *  "8 8 2 1 -1 -1",
 *  "       s iconColor5    m black c blue",
 *  ".      s none  m none  c none",
 *  "      ..",
 *  "    ....",
 *  "    ....",
 *  "     ...",
 *  " ..   ..",
 *  " ...   .",
 *  ".....   ",
 *  "......  "};
 * </pre>
 *
 * @version	1.2 96/02/20
 * @author 	Jan Andersson, Torpa Konsult AB. (janne@torpa.se)
 */
public class XpmParser {
   private DataInputStream input;
   private int lineNo = 0;
   private int width = 0;
   private int height = 0;
   private int nColors = 0;
   private int charsPerPixel = 0;
   
   private Hashtable colors;
   
   private Color[] colorTable;
   
   private byte[] pixmap = null;
   private String line;
   
   /**
    * Construct and XpmParser from an InputStream
    * @param is the imput stream to parse
    */
   public XpmParser(InputStream is) {
      input = new DataInputStream(is);
      colors = new Hashtable();
   }

   /**
    * Parse input stream.
    * @return true on success.
    */
   public boolean parse() {
      try {
	 parseInput();
	 return true;
      }
      catch (Exception e) {
	 return false;
      }
   }

   /**
    * Get image width.
    * @return width in pixels.
    */
   public int getWidth() {
      return width;
   }
   
   /**
    * Get image height.
    * @return height in pixels.
    */
   public int getHeight() {
      return height;
   }
   
   /**
    * Get pixmap.
    * @return pixmap array.
    */
   public byte[] getPixmap() {
      return pixmap;
   }

   /**
    * Get color table.
    * @return array of used colors.
    */
   public Color[] getColorTable() {
      return colorTable;
   }
   
   /**
    * Parse input stream.
    * @exception Exception on input errors.
    */
   private void parseInput() throws Exception {
      checkForHeader();
      skipLineStartingWith("static char");
      readHintsLine();
      
      colorTable = new Color[nColors];
      
      readColorTable();
      readPixels();
   }

   /**
    * Read and check XPM header comment
    */
   private void checkForHeader() throws Exception {
      readLine();
      if (line != null && line.startsWith("/*") && line.endsWith("*/")) {
	 String tmp = line.substring(2);
	 tmp = tmp.trim();
	 // read next line
	 readLine();
      }
   }
   
   /**
    * Skip line starting with specified string.
    */
   private void skipLineStartingWith(String skip) throws Exception {
      if (line == null)
	 throw(new Exception("Invalid Xpm format, line: " + lineNo));
      if (line.startsWith(skip)) {
	 // ignore this line; read next
	 readLine();
      }
   }

   /**
    * Read XPM hints line. I.e width, height, number of colors and
    * chars per pixel.
    */
   private void readHintsLine() throws Exception {
      // skip comment line
      if (line != null && line.startsWith("/*") && line.endsWith("*/"))
	 readLine();
      if (line == null)
	 throw(new Exception("Invalid Xpm format: unexpected EOF, line: " +
			     lineNo));
      int start = line.indexOf('"');
      int end = line.lastIndexOf('"');
      if (start < 0 || end < 0 || end <= start)
	 throw(new Exception("Invalid Xpm format: hints line: " + lineNo));
      String tmp = line.substring(start+1, end);
      StringTokenizer st = new StringTokenizer(tmp);
      if (st.countTokens() < 4)
	 throw(new Exception("Invalid Xpm format: hints line: " + lineNo));
      try {
	 width = Integer.parseInt(st.nextToken());
	 height = Integer.parseInt(st.nextToken());
	 nColors = Integer.parseInt(st.nextToken());
	 charsPerPixel = Integer.parseInt(st.nextToken());
      }
      catch (Exception e) {
	 throw(new Exception("Invalid Xpm format: hints line: " + lineNo));
      }
      if (charsPerPixel > 3) {
	 throw(new Exception("Invalid Xpm format: "+
			    "Can only handle up to 3 chars per pixels"));
      }
      
   }

   /**
    * Read XPM color table.
    */
   private void readColorTable() throws Exception {
      for (int i=0; i<nColors; i++) {
	 readLine();
	 // skip comment line
	 if (line != null && line.startsWith("/*") && line.endsWith("*/"))
	    readLine();
	 if (line == null)
	    throw(new Exception("Invalid Xpm format: unexpected EOF, line: " +
				lineNo));
	 // read the chars
	 int pos = line.indexOf('"');
	 if (pos < 0)
	    throw(new Exception("Invalid Xpm format: color table, line: "
			       + lineNo));
	 String tmp = line.substring(pos+1);
	 int colorIndex = 0;
	 for (int j=0; j<charsPerPixel; ++j)
	    colorIndex = (colorIndex << 8) + tmp.charAt(j);
	 
	 tmp = line.substring(pos+1+charsPerPixel);
	 StringTokenizer st = new StringTokenizer(tmp, " \"\t\n\r");
	 int state = 0;
	 boolean gotC = false;
	 while (st.hasMoreTokens() && state < 2) {
	    // look for 'c'
	    String token = st.nextToken();
	    switch (state) {
	       case 0:
		  // looking for 'c'
		  if (token.equals("c"))
		     state++;
		  break;
	       case 1:
		  // parse color
		  colorTable[i] = parseColor(token);
		  colors.put(new Integer(colorIndex), new Integer(i));
		  state++;
		  break;
	    }
	 }
      }
   }

   /**
    * Read the XPM pixels.
    */
   private void readPixels() throws Exception {
      readLine();
      // skip comment line
      if (line != null && line.startsWith("/*") && line.endsWith("*/"))
	 readLine();
      if (line == null)
	 throw(new Exception("Invalid Xpm format: "+
			    "EOF at line: " + lineNo));

      int datasize = width * height;
      pixmap = new byte[datasize];
      int index = 0;
      
      while (line != null) {
	 int start = line.indexOf('"');
	 int end = line.lastIndexOf('"');
	 if (start < 0 || end < 0 || end <= start)
	    throw(new Exception("Invalid Xpm format: line: " + lineNo));
	 String tmp = line.substring(start+1, end);
	 int i = 0;
	 while (i < tmp.length()) {
	    int colorIndex = 0;
	    for (int j=0; j<charsPerPixel; ++j) 
	       colorIndex = (colorIndex << 8) + tmp.charAt(i++);
	    pixmap[index++] = (byte) ((Integer) colors.get(new Integer(colorIndex))).intValue();
	 }
	 
	 if (index >= datasize)
	    return;
	 readLine();
      }
   }
   
   /**
    * Read a line from input stream.
    */
   private void readLine() {
      line = null;
      try {
	 line = input.readLine();
      }
      catch (IOException e) {
	 line = null;
      }
      if (line != null)
	 line = line.trim();
      lineNo++;
   }

   /**
    * Parse color string
    */
   private Color parseColor(String aColor) {
      Color c = null;
      if (aColor.charAt(0) == '#') {
	 // hexadecimal value
	 switch (aColor.length()) {
	    case 4:
	       // NYI
	       break;
	    case 7:
	       //Integer rgbValue = new Integer(0);
	       Integer rgbValue = Integer.valueOf(aColor.substring(1), 16);
	       c = new Color(rgbValue.intValue());
	       break;
	    case 13:
	       Integer rValue = Integer.valueOf(aColor.substring(1,3), 16);
	       Integer gValue = Integer.valueOf(aColor.substring(5,7), 16);
	       Integer bValue = Integer.valueOf(aColor.substring(9,11), 16);
	       c = new Color(rValue.intValue(), gValue.intValue(),
			     bValue.intValue());
	       break;
	 }
	 if (c == null) {
	    // unknown color; use black :-(
	    c = Color.black;
	 }
      }
      else {
	 // assume symbolic color name
	 if (aColor.equalsIgnoreCase("none")) {
	    // Use null to indicate transparent color
	    c = null;
	 }
	 else {
	    // look up color RGB value among known X11 colornames
	    int rgb = XColorNames.getRgb(aColor);
	    if (rgb != XColorNames.NOT_FOUND) 
	       c = new Color(rgb);
	    if (c == null) {
	       // unknown color; use black :-(
	       c = Color.black;
	    }
	 }
      }
      
      return c;
   }
}

   
