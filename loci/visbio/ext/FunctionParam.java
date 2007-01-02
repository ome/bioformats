//
// FunctionParam.java
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

package loci.visbio.ext;

/** FunctionParam is a name/value pair for a function parameter. */
public class FunctionParam {

  // -- Fields --

  /** Name of this parameter. */
  protected String name;

  /** Default value of this parameter. */
  protected String value;

  // -- Constructor --

  /**
   * Constructs a new function parameter with
   * the given name and default value.
   */
  public FunctionParam(String name, String value) {
    this.name = name;
    this.value = value;
  }

  // -- FunctionParam API methods --

  /** Gets the name of this parameter. */
  public String getName() { return name; }

  /** Gets the default value of this parameter. */
  public String getValue() { return value; }

}
