package com.sun.jimi.core.util.x11;

import java.io.*;

/**
 * X11 bitmap (xbm) parser class.
 *
 * Parse files of the form:
 * <pre>
 * #define foo_width w
 * #define foo_height h
 * static char foo_bits[] = {
 * 0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,
 * 0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,
 * 0xnn,0xnn,0xnn,0xnn};
 * </pre>
 *
 * @version	1.2 96/02/20
 * @author 	Jan Andersson, Torpa Konsult AB. (janne@torpa.se)
 */
public class XbmParser {
   private static final int GET_WIDTH = 0;
   private static final int GET_HEIGHT = 1;
   private static final int GET_START = 2;
   private static final int GET_BYTES = 3;

   private StreamTokenizer tokenizer;
   private int width = 0;
   private int height = 0;
   private int length = 0;
   private int[] bitmap = null;

   /**
    * Construct and XpmParser from an InputStream
    * @param is the imput stream to parse
    */
   public XbmParser(InputStream is) {
      tokenizer = new StreamTokenizer(is);
      tokenizer.slashStarComments(true);
      tokenizer.ordinaryChar('/');
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
   public int[] getBitmap() {
      return bitmap;
   }

   /**
    * Parse input stream.
    * @exception Exception on input errors.
    */
   private void parseInput() throws Exception {
      int state = GET_WIDTH;
      int token = StreamTokenizer.TT_EOF;
      int index = 0;

      token = tokenizer.nextToken();
      while (token != StreamTokenizer.TT_EOF) {
	 switch(state) {
	    case GET_WIDTH:
	       if (token == StreamTokenizer.TT_NUMBER) {
		  width = (int) tokenizer.nval;
		  state = GET_HEIGHT;
	       }
	       break;
	    case GET_HEIGHT:
	       if (token == StreamTokenizer.TT_NUMBER) {
		  height = (int) tokenizer.nval;
		  state = GET_START;
	       }
	       break;
	    case GET_START:
	       if (token == '{') {
		  // reset tokinizer to handle bytes
		  resetTokenizer();
		  // allocate bitmap
		  width = ((width+7)/8)*8;  // add the padding to the width
		  length = width/8 * height;
		  bitmap = new int[length];
		  state = GET_BYTES;
	       }
	       break;
	    case GET_BYTES:
	       // waiting for 0xnn, or '}'
	       if (token == StreamTokenizer.TT_WORD) {
		  if (tokenizer.sval.length() > 2 &&
		      tokenizer.sval.charAt(0) == '0' &&
		      (tokenizer.sval.charAt(1) == 'x' ||
		       tokenizer.sval.charAt(1) == 'x')) {
		     try {
			int val = Integer.parseInt(
			   tokenizer.sval.substring(2), 16);
			bitmap[index++] = val;
		     }
		     catch (Exception e) {

    			token = StreamTokenizer.TT_EOF;
    			index--;
    			continue;
		     }
		  }
	       }
	       else if (token == '}') {
		  // done!
		  token = StreamTokenizer.TT_EOF;
	       }
	 }
	 token = tokenizer.nextToken();

      }
      if (width == 0 || height == 0 || index != length)
	 throw(new Exception());



   }

   /**
    * Reset input tokenizer to handle hexadecimal values
    */
   private void resetTokenizer()
   {
      tokenizer.resetSyntax();
      tokenizer.wordChars('a', 'f');
      tokenizer.wordChars('A', 'F');
      tokenizer.wordChars('0', '9');
      tokenizer.wordChars('x', 'x');
      tokenizer.wordChars('X', 'X');
      tokenizer.whitespaceChars(0, ' ');
      tokenizer.slashStarComments(true);
   }


}


