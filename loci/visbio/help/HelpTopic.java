//
// HelpTopic.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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

package loci.visbio.help;

import javax.swing.tree.DefaultMutableTreeNode;

/** HelpTopic is a tree node representing a help topic. */
public class HelpTopic extends DefaultMutableTreeNode {

  // -- Fields --

  /** Content source for this help topic. */
  private String source;


  // -- Constructor --

  /** Creates a VisBio help topic. */
  public HelpTopic(String name, String source) {
    super(name);
    this.source = source;
  }


  // -- HelpWindow API methods --

  /** Gets the name associated with this help topic. */
  public String getName() { return (String) getUserObject(); }

  /** Gets the content source for this help topic. */
  public String getSource() { return source; }

}
