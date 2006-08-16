//
// About.java
//

/*
LOCI common classes for use with VisBio, Bio-Formats, 4D Data Browser, etc.
Copyright (C) 2006 Curtis Rueden.

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

package loci.util;

import java.io.*;
import javax.swing.JOptionPane;

/**
 * About is a small program for displaying version information in a dialog box.
 * It is intended to be used as a main class for JAR libraries to easily
 * determine library version and build date.
 */
public abstract class About {

  private static String title;
  private static String msg;

  public static void show() {
    if (title == null) {
      StringBuffer sb = new StringBuffer();
      try {
        InputStream is = About.class.getResourceAsStream("about.txt");
        if (is == null) {
          title = "About";
          msg = "Error: version information not found";
        }
        else {
          BufferedReader in = new BufferedReader(new InputStreamReader(is));
          while (true) {
            String line = in.readLine();
            if (line == null) break;
            if (title == null) title = "About " + line;
            else sb.append("\n");
            sb.append(line);
          }
          in.close();
          msg = sb.toString();
        }
      }
      catch (IOException exc) {
        if (title == null) title = "About";
        msg = "Error: could not read version information";
      }
    }
    JOptionPane.showMessageDialog(null, msg,
      title, JOptionPane.INFORMATION_MESSAGE);
  }

  public static void main(String[] args) {
    show();
    System.exit(0);
  }

}
