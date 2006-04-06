//
// About.java
//

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
