//
// TaskEvent.java
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

import java.util.EventObject;

/** TaskEvent is the event generated when a BioTask is updated in some way. */
public class TaskEvent extends EventObject {

  // -- Constants --

  /** Indicates another operation has been registered as part of this task. */
  public static final int OPERATION_ADDED = 1;

  /** Indicates the task has advanced. */
  public static final int TASK_ADVANCED = 2;

  /** Indicates the task's message has changed. */
  public static final int MESSAGE_CHANGED = 3;

  /** Indicates the task has been cleared. */
  public static final int TASK_CLEARED = 4;


  // -- Fields --

  /** The type of this event. */
  private int eventType;


  // -- Constructor --

  /** Constructs a new data event. */
  public TaskEvent(Object source, int eventType) {
    super(source);
    this.eventType = eventType;
  }


  // -- TaskEvent API methods --

  /** Gets the type of this event. */
  public int getEventType() { return eventType; }

  /** Gets a string representation of this event. */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getSource().getClass().getName());
    sb.append(": ");
    if (eventType == TASK_ADVANCED) sb.append("TASK_ADVANCED");
    else if (eventType == MESSAGE_CHANGED) sb.append("MESSAGE_CHANGED");
    else if (eventType == TASK_CLEARED) sb.append("TASK_CLEARED");
    else sb.append("(" + eventType + ")");
    return sb.toString();
  }

}
