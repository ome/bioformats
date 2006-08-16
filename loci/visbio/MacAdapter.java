//
// MacAdapter.java
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

package loci.visbio;

import com.apple.eawt.*;
import loci.visbio.VisBioFrame;
import loci.visbio.ExitManager;
import loci.visbio.help.HelpManager;
import loci.visbio.state.OptionManager;

/** An adapter for handling the Mac OS X application menu items. */
public class MacAdapter extends ApplicationAdapter {

  // NB: This class will only compile on Macintosh systems. However, since
  // no other classes depend on it directly (i.e., they reference it via
  // reflection), the rest of VisBio should still compile on non-Macintoshes.

  // Alternately, the code can be compiled on other platforms with the
  // "AppleJavaExtensions" stub jar available from:
  //    http://developer.apple.com/samplecode/Sample_Code/Java.htm

  // -- Fields --

  /** Linked VisBio frame. */
  private VisBioFrame bio;


  // -- Constructor --

  /** Constructs a Mac OS X adapter. */
  public MacAdapter(VisBioFrame bio) { this.bio = bio; }


  // -- MacAdapter API methods --

  /** Associates the VisBio frame with a Mac OS X adapter. */
  public static void link(VisBioFrame bio) {
    Application app = new Application();
    app.setEnabledPreferencesMenu(true);
    app.addApplicationListener(new MacAdapter(bio));
  }


  // -- ApplicationAdapter API methods --

  /** Handles the About menu item. */
  public void handleAbout(ApplicationEvent evt) {
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    if (hm == null) evt.setHandled(false);
    else {
      evt.setHandled(true);
      hm.helpAbout();
    }
  }

  /** Handles the Preferences menu item. */
  public void handlePreferences(ApplicationEvent evt) {
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    if (om == null) evt.setHandled(false);
    else {
      evt.setHandled(true);
      om.fileOptions();
    }
  }

  /** Handles the Quit menu item. */
  public void handleQuit(ApplicationEvent evt) {
    ExitManager em = (ExitManager) bio.getManager(ExitManager.class);
    if (em == null) evt.setHandled(true);
    else {
      evt.setHandled(false);
      em.fileExit();
    }
  }

}
