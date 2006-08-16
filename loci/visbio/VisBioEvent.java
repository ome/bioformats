//
// VisBioEvent.java
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

import java.util.EventObject;

/** VisBioEvent is the event generated when something in VisBio changes. */
public class VisBioEvent extends EventObject {

  // -- Constants --

  /** Indicates a new logic manager has been integrated into VisBio. */
  public static final int LOGIC_ADDED = 1;

  /** Indicates the state of one of VisBio's logic managers has changed. */
  public static final int STATE_CHANGED = 2;


  // -- Fields --

  /** The type of this event. */
  private int eventType;

  /** String message associated with this event. */
  private String message;

  /** Flag indicating whether event is undoable. */
  private boolean undoable;


  // -- Constructor --

  /** Constructs a VisBio event. */
  public VisBioEvent(Object source, int eventType,
    String message, boolean undoable)
  {
    super(source);
    this.eventType = eventType;
    this.message = message;
    this.undoable = undoable;
  }


  // -- VisBioEvent API methods --

  /** Gets the type of this event. */
  public int getEventType() { return eventType; }

  /** Gets the message for this event. */
  public String getMessage() { return message; }

  /** Gets whether the event is undoable. */
  public boolean isUndoable() { return undoable; }

  /** Gets a string representation of this event. */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (eventType == LOGIC_ADDED) sb.append("LOGIC_ADDED");
    else if (eventType == STATE_CHANGED) sb.append("STATE_CHANGED");
    else sb.append("(" + eventType + ")");
    sb.append(": ");
    String name = getSource().getClass().getName();
    if (name.startsWith("loci.visbio.")) name = name.substring(12);
    sb.append(name);
    if (message != null) {
      sb.append(": ");
      sb.append(message);
    }
    if (undoable) sb.append(" [undoable]");
    return sb.toString();
  }

}
