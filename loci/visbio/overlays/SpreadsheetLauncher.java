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

import com.jgoodies.plaf.LookUtils;
import java.io.File;
import java.io.IOException;

/** Launches an external spreadsheet application. */
public class SpreadsheetLauncher {

  // -- Constants --

  /** Default path to spreadsheet (Excel) on Windows computers. */
  protected static final String WIN_PATH =
    "C:\\Program Files\\Microsoft Office\\excel.exe";

  /** Default path to spreadsheet (OpenOffice) on Linux computers. */
  protected static final String LIN_PATH = "/usr/bin/oocalc";

  /** Default path to spreadsheet (Excel) on Macintosh computers. */
  protected static final String MAC_PATH =
    "/Applications/Microsoft Office 2004/Microsoft Excel";

  // --  Fields --

  /** Path to spreadsheet executable. */
  protected final String path;

  // -- Constructor --

  /** Constructs a spreadsheet launcher. */
  public SpreadsheetLauncher() throws SpreadsheetLaunchException{
    path = getDefaultApplicationPath();
  }

  // -- Static SpreadsheetLauncher API methods

  /** Returns the default spreadsheet application path for the current OS. */
  public static String getDefaultApplicationPath() throws
    SpreadsheetLaunchException
  {
    String def = "";
    if (isWindows()) def = WIN_PATH;
    else if (isLinux()) def = LIN_PATH;
    else if (isMac()) def = MAC_PATH;
    else {
      throw new SpreadsheetLaunchException(makeCantIdentifyOSMessage());
    }
    return def;
  }

  // -- SpreadsheetLauncher API methods --

  /** Tries to launch the appropriate spreadsheet application. */
  public void launchSpreadsheet(File file) throws SpreadsheetLaunchException {
    launchSpreadsheet(file, path);
  }

  /** Tries to launch the appropriate spreadsheet application. */
  public void launchSpreadsheet(File file, String appPath) throws
    SpreadsheetLaunchException
  {
    if (file.exists()) {
      String command = formatPath(appPath) + " " +
        formatPath(file.getAbsolutePath());

      try {
        Runtime.getRuntime().exec(command);
      }
      catch (IOException ex) {
        throw new SpreadsheetLaunchException(makeCommandErrorMessage(command));
      }
    }
    else {
      throw new SpreadsheetLaunchException(makeFileDoesNotExistMessage(file));
    }
  }

  // -- Helper methods --

  /** Formats path to avoid problems caused by embedded spaces. */
  protected String formatPath(String plainPath) {
    String formattedPath = plainPath;
    if (isWindows()) formattedPath = "\"" + formattedPath + "\"";
    return formattedPath;
  }

  /** Whether the OS is windows. */
  protected static boolean isWindows() {
    return LookUtils.IS_OS_WINDOWS_MODERN;
  }

  /** Whether the OS is mac. */
  protected static boolean isMac() {
    return LookUtils.IS_OS_MAC;
  }

  /** Whether OS is Linux. */
  protected static boolean isLinux() {
    return LookUtils.IS_OS_LINUX;
  }

  /** Makes an error message from the given command. */
  protected String makeCommandErrorMessage(String command) {
    String msg =
      "Could not launch spreadsheet using the following command:\n\t" +
      command + "\nYou may wish to change the spreadsheet application path" +
      " in the 'File > Options...' menu.";
    return msg;
  }

  /** Makes an error message from the given file. */
  protected String makeFileDoesNotExistMessage(File file) {
    return "Could not launch spreadsheet.  File does not exist:\n\t" +
      file.getAbsolutePath();
  }

  /** Returns an error message indicating the OS could not be identified. */
  protected static String makeCantIdentifyOSMessage() {
    return "Could not launch spreadsheet: could not identify OS.";
  }
}
