//
// ProgramState.java
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

import loci.ome.xml.OMEElement;

/** A VisBio state wrapper. */
public class ProgramState {

  // -- Fields --

  /** Message to be displayed as part of "Undo" and "Redo" menu items. */
  public String msg;

  /** State object (XML) for use in state restoration. */
  public OMEElement state;


  // -- Constructor --

  /** Constructs a VisBio state wrapper. */
  public ProgramState(String msg, OMEElement state) {
    this.msg = msg;
    this.state = state;
  }

}
