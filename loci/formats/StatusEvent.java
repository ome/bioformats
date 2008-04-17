//
// StatusEvent.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

/**
 * A event indicating a status update.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/StatusEvent.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/StatusEvent.java">SVN</a></dd></dl>
 */
public class StatusEvent {

  // -- Fields --

  /** Current progress value. */
  protected int progress;

  /** Current progress maximum. */
  protected int maximum;

  /** Current status message. */
  protected String status;

  // -- Constructor --

  /** Constructs a status event. */
  public StatusEvent(String message) {
    this(-1, -1, message);
  }

  /** Constructs a status event. */
  public StatusEvent(int progress, int maximum, String message) {
    this.progress = progress;
    this.maximum = maximum;
    status = message;
  }

  // -- StatusEvent API methods --

  /** Gets progress value. Returns -1 if progress is unknown. */
  public int getProgressValue() { return progress; }

  /** Gets progress maximum. Returns -1 if progress is unknown. */
  public int getProgressMaximum() { return maximum; }

  /** Gets status message. */
  public String getStatusMessage() { return status; }

}
