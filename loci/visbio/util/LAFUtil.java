//
// LAFUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

package loci.visbio.util;

import com.jgoodies.plaf.LookUtils;
import com.jgoodies.plaf.Options;
import com.jgoodies.plaf.FontSizeHints;

import java.awt.Dimension;

import javax.swing.UIManager;

/** LAFUtil contains useful functions relating to Look and Feel. */
public abstract class LAFUtil {

  /** Initializes the look and feel to a reasonable default. */
  public static void setLookAndFeel() {
    UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
    Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
    Options.setDefaultIconSize(new Dimension(18, 18));

    UIManager.installLookAndFeel("JGoodies ExtWindows",
      "com.jgoodies.plaf.windows.ExtWindowsLookAndFeel");
    UIManager.installLookAndFeel("JGoodies Plastic",
      "com.jgoodies.plaf.plastic.PlasticLookAndFeel");
    UIManager.installLookAndFeel("JGoodies Plastic3D",
      "com.jgoodies.plaf.plastic.Plastic3DLookAndFeel");
    UIManager.installLookAndFeel("JGoodies PlasticXP",
      "com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");

    /*
    UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
    for (int i=0; i<lafs.length; i++) {
      System.out.println(lafs[i].getName() +
        " (" + lafs[i].getClassName() + ")");
    }
    */

    String lafName = LookUtils.IS_OS_WINDOWS_XP
      ? Options.getCrossPlatformLookAndFeelClassName()
      : Options.getSystemLookAndFeelClassName();

    try { UIManager.setLookAndFeel(lafName); }
    catch (Exception exc) { exc.printStackTrace(); }
  }

  /** Gets the name of the current look and feel. */
  public static String getLookAndFeel() {
    return UIManager.getLookAndFeel().getName();
  }

}
