//
// SaveException.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2006 Curtis Rueden.

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

/** Exception indicating a problem with saving or restoring an object state. */
public class SaveException extends Exception {

  // -- Fields --

  /** The exception upon which the save exception is based. */
  private Exception exc;


  // -- Constructors --

  /** Constructs a save exception with the given message. */
  public SaveException(String msg) { super(msg); }

  /** Constructs a save exception based on the given exception. */
  public SaveException(Exception exc) { this.exc = exc; }


  // -- SaveException API methods --

  /** Gets the exception upon which this save exception is based. */
  public Exception getException() { return exc; }


  // -- Exception API methods --

  /** Prints a stack trace. */
  public void printStackTrace() {
    if (exc == null) super.printStackTrace();
    else exc.printStackTrace();
  }

}
