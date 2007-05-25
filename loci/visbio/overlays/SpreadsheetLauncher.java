//
// SpreadsheetLauncher.java
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

import com.jgoodies.looks.LookUtils;
import java.io.File;
import java.io.IOException;

public class SpreadsheetLauncher {

  // -- Constants -- 

  protected static final String WIN_PATH = "C:\\Program Files\\Microsoft Office\\excel.exe";

  protected static final String LIN_PATH = "/usr/bin/oocalc";

  protected static final String MAC_PATH = "/Applications/Microsoft Office 2004/Microsoft Excel";

  // --  Fields --
  
  /** Path to spreadsheet executable */
  protected String path;

  // -- Constructor --

  /** Constructs a spreadsheet launcher */
  public SpreadsheetLauncher() {
    // determine OS
    if (LookUtils.IS_OS_WINDOWS) path = WIN_PATH;
    if (LookUtils.IS_OS_LINUX) path = LIN_PATH; 
    if (LookUtils.IS_OS_MAC) path = MAC_PATH;
  }

  // -- SpreadsheetLauncher API methods --
   
  /** Tries to launch the appropriate spreadsheet application */
  public void launchSpreadsheet(File file) throws SpreadsheetLaunchException {
    if (file.exists()) {
      String command = path + " " + file.getAbsolutePath();
      try {
        Runtime.getRuntime().exec(command);
      }
      catch (IOException ex) { 
        throw new SpreadsheetLaunchException(makeCommandErrorMessage(command));
      }
    }
    else {
      throw new SpreadsheetLaunchException(makeFileErrorMessage(file));
    }
  }

  /** Makes an error message from the given command */
  protected String makeCommandErrorMessage(String command) {
    return "Could not launch spreadsheet using the following command:\n\t" + command;
  }

  /** makes an error message from the given file */
  protected String makeFileErrorMessage(File file) {
    return "Could not launch spreadsheet.  File does not exist:\n\t" + 
      file.getAbsolutePath();
  }

  /** For testing */
  public static void main (String[] args) {
    SpreadsheetLauncher s = new SpreadsheetLauncher();
  }
}
