//
// Saveable.java
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

package loci.visbio.state;

import org.w3c.dom.Element;

/**
 * The Saveable interface provides methods for saving and restoring the
 * implementing object's state to and from a data stream.
 */
public interface Saveable {

  /** Writes the current state to the given DOM element. */
  void saveState(Element el) throws SaveException;

  /** Restores the current state from the given DOM element. */
  void restoreState(Element el) throws SaveException;

}
