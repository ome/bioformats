//
// BioOption.java
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

import java.awt.Component;

/** BioOption represents an option in the VisBio Options dialog. */
public abstract class BioOption implements Saveable {

  // -- Fields --

  /** String identifying this option. */
  protected String text;


  // -- Constructor --

  /** Constructs a new option. */
  public BioOption(String text) { this.text = text; }


  // -- BioOption API methods --

  /** Gets text identifying this option. */
  public String getText() { return text; }

  /** Gets a GUI component representing this option. */
  public abstract Component getComponent();

  /** Sets the GUI component to reflect the given value. */
  public abstract void setValue(String value);


  // -- Saveable API methods --

  /** Writes the current state to the given OME-CA XML object. */
  public void saveState(OMEElement ome) throws SaveException { }

  /** Restores the current state from the given OME-CA XML object. */
  public void restoreState(OMEElement ome) throws SaveException { }

}
