//
// SpawnEvent.java
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

package loci.visbio.util;

/**
 * An event indicating another application instance was spawned.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/util/SpawnEvent.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/util/SpawnEvent.java">SVN</a></dd></dl>
 */
public class SpawnEvent {

  // -- Fields --

  /** The arguments passed from the spawned application instance. */
  protected String[] args;

  // -- Constructor --

  /** Constructs an application instance spawn event. */
  public SpawnEvent(String[] args) { this.args = args; }

  // -- SpawnEvent API methods --

  /** Gets the arguments passed from the spawned application instance. */
  public String[] getArguments() { return args; }

}
