//
// LogicManager.java
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

import loci.visbio.state.SaveException;
import loci.visbio.state.Saveable;
import org.w3c.dom.Element;

/**
 * LogicManager is the superclass of all VisBio logic handlers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/LogicManager.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/LogicManager.java">SVN</a></dd></dl>
 */
public class LogicManager implements Saveable {

  // -- Fields --

  /** VisBio frame. */
  protected VisBioFrame bio;

  // -- Constructor --

  /** Constructs a logic manager. */
  public LogicManager(VisBioFrame bio) { this.bio = bio; }

  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) { }

  /** Gets the VisBio frame for this logic manager. */
  public VisBioFrame getVisBio() { return bio; }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 0; }

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element. */
  public void saveState(Element el) throws SaveException { }

  /** Restores the current state from the given DOM element. */
  public void restoreState(Element el) throws SaveException { }

}
