//
// ImageFilter.java
//

// Adapted from FileChooserDemo2

package loci.browser;

import java.io.File;
import javax.swing.filechooser.*;

public class ImageFilter extends FileFilter {

  /** Accepts all directories and all files types supported by ImageJ. */
  public boolean accept(File f) {
    if (f.isDirectory()) return true;

    String extension = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 &&  i < s.length() - 1) {
      extension = s.substring(i+1).toLowerCase();
    }

    if (extension != null) {
      if (extension.equalsIgnoreCase("tiff") ||
        extension.equalsIgnoreCase("tif") ||
        extension.equalsIgnoreCase("gif") ||
        extension.equalsIgnoreCase("jpeg") ||
        extension.equalsIgnoreCase("jpg") ||
        extension.equalsIgnoreCase("bmp") ||
        extension.equalsIgnoreCase("lut") ||
        extension.equalsIgnoreCase("pgm") ||
        extension.equalsIgnoreCase("pic") ||
        extension.equalsIgnoreCase("zvi") ||
        extension.equalsIgnoreCase("ipw") ||
        extension.equalsIgnoreCase("mov") ||
        extension.equalsIgnoreCase("png"))
      {
        return true;
      }
      else return false;
    }
    return false;
  }

  /** The description of this filter. */
  public String getDescription() { return "Accepted Images"; }

}
