//
// SpreadsheetOptionStrategy.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

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

package loci.visbio.state;

/**
 * Strings related to the SpreadsheetLaunchOption option.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/state/SpreadsheetOptionStrategy.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/state/SpreadsheetOptionStrategy.java">SVN</a></dd></dl>
 */
public final class SpreadsheetOptionStrategy {

  private SpreadsheetOptionStrategy() {}

  /** Returns the text for the spreadsheet option. */
  public static String getText() {
    return "Automatically launch spreadsheet application " +
      "when exporting overlays";
  }

  /** Returns the text tooltip for the spreadsheet option. */
  public static String getTextTip() {
    return "The path to the spreadsheet application to launch";
  }

  /** Returns the checkbox tool tip for the spreadsheet option. */
  public static String getBoxTip() {
    return  "Toggles whether spreadsheet application is automatically" +
      " launched when overlays are exported";
  }

  /** Returns the label for the spreadsheet option. */
  public static String getLabel() {
    return "Path to Spreadsheet Application:";
  }

  /** Returns the button tip for the spreadsheet option. */
  public static String getButtonTip() {
    return "Restore the default predicted path to spreadsheet application";
  }
}

