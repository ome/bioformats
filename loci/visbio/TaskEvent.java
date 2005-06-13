//
// TaskEvent.java
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

package loci.visbio;

/** A event indicating a task update. */
public class TaskEvent {

  // -- Fields --

  /** Current progress value. */
  protected int progress;

  /** Current progress maximum. */
  protected int maximum;

  /** Current status message. */
  protected String status;


  // -- Constructor --

  /** Constructs a task event. */
  public TaskEvent(int progress, int maximum, String message) {
    this.progress = progress;
    this.maximum = maximum;
    status = message;
  }


  // -- TaskEvent API methods --

  /** Gets progress value. */
  public int getProgressValue() { return progress; }

  /** Gets progress maximum. */
  public int getProgressMaximum() { return maximum; }

  /** Gets status message. */
  public String getStatusMessage() { return status; }

}
