/*
 * Copyright (c) 1996 by Jan Andersson, Torpa Konsult AB.
 *
 * Permission to use, copy, and distribute this software for
 * NON-COMMERCIAL purposes and without fee is hereby granted
 * provided that this copyright notice appears in all copies.
 *
 */

package com.sun.jimi.core.util.x11;

import java.util.Hashtable;

/**
 * Simple class to convert from X11 symbolic color names to RGB
 * value.
 *
 * Note: Not even close to support all X11 color names.
 *
 * @version	1.1 96/02/20
 * @author 	Jan Andersson, Torpa Konsult AB. (janne@torpa.se)
 */
public class XColorNames {
   public static final int NOT_FOUND = Integer.MIN_VALUE;
   static Hashtable table;
   static {
      table = new Hashtable(100);
      // Color table, from Anthony Thyssen's Icon Library
      table.put("black", new Integer(0x000000));
      table.put("dark slate gray", new Integer(0x2F4F4F));
      table.put("slate gray", new Integer(0x708090));
      table.put("gray", new Integer(0xBEBEBE));

	  // Added shades of gray, /Karl

      table.put("gray20", new Integer(0x333333));
      table.put("gray40", new Integer(0x666666));
      table.put("gray60", new Integer(0x999999));
      table.put("gray80", new Integer(0xcccccc));
      table.put("gray100", new Integer(0xffffff));

      table.put("gainsboro", new Integer(0xDCDCDC));
      table.put("white", new Integer(0xFFFFFF));
      table.put("purple", new Integer(0xA020F0));
      table.put("magenta", new Integer(0xFF00FF));
      table.put("violet", new Integer(0xEE82EE));
      table.put("firebrick", new Integer(0xB22222));
      table.put("red", new Integer(0xFF0000));
      table.put("tomato", new Integer(0xFF6347));
      table.put("orange", new Integer(0xFFA500));
      table.put("gold", new Integer(0xFFD700));
      table.put("yellow", new Integer(0xFFFF00));
      table.put("sienna", new Integer(0xA0522D));
      table.put("peru", new Integer(0xCD853F));
      table.put("tan", new Integer(0xD2B4C8));
      table.put("wheat", new Integer(0xF5DeB3));
      table.put("lemon chiffon", new Integer(0xFFFACD));
      table.put("sea green", new Integer(0x2E8B57));
      table.put("lime green", new Integer(0x32CD32));
      table.put("green", new Integer(0x00FF00));
      table.put("pale green", new Integer(0x98FB98));
      table.put("navy", new Integer(0x000080));
      table.put("blue", new Integer(0x0000FF));
      table.put("dodger blue", new Integer(0x1E90FF));
      table.put("sky blue", new Integer(0x87CEEB));
      table.put("lavender", new Integer(0xE6E6FA));
      table.put("tan", new Integer(0xD2B48C));
      table.put("cyan", new Integer(0x00FFFF));
   }

   /**
    * Get RGB value for symbolic color name
    * @param colorName name of color to look up.
    * @returns RGB value or null if color unknown
    */
   public static int getRgb(String name) {
      Object tmp = table.get(name);
      if (tmp == null)
	 return NOT_FOUND;
      return ((Integer)tmp).intValue();
   }
   
}
