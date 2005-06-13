//
// Dynamic.java
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

package loci.visbio.state;

/**
 * The Dynamic interface provides methods for testing whether two objects have
 * the same state, and for synchronizing states if they do not. The reason for
 * such an interface is to assist with state salvation and restoration (see the
 * Saveable interface).
 *
 * When we read a new list of Dynamic objects from a state file, we need a way
 * to determine how closely the list in memory matches the one just read, as
 * well as an efficient way to modify the existing list to conform to the one
 * just read without necessarily recreating all those objects from scratch
 * (which could be quite expensive).
 *
 * Thus, these "dynamic" objects implement the matches method to test if a
 * freshly read dynamic object is in the same state as any of the current
 * objects.
 *
 * If so, there is no need to reinitialize that object&mdash;we can just use
 * the existing one.
 *
 * If not, we call the initState method to force the original object's state to
 * conform to the one just read, without needing to recreate the object itself
 * or relink it into data structures.
 *
 * The {@link loci.visbio.state.StateManager} class contains the mergeStates
 * static method, which performs this procedure on two lists of dynamic
 * objects.
 */
public interface Dynamic {

  /** Tests whether two dynamic objects are equivalent. */
  boolean matches(Dynamic dyn);

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  boolean isCompatible(Dynamic dyn);

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  void initState(Dynamic dyn);

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  void discard();

}
