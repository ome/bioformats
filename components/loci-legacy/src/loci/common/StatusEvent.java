/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.common;

/**
 * A legacy delegator class for ome.scifio.common.StatusEvent.
 * 
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/StatusEvent.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/StatusEvent.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class StatusEvent {

  // -- Fields --

  private ome.scifio.common.StatusEvent event;

  // -- Constructor --

  /** Constructs a status event. */
  public StatusEvent(String message) {
    event = new ome.scifio.common.StatusEvent(message);
  }

  /** Constructs a status event. */
  public StatusEvent(String message, boolean warn) {
    event = new ome.scifio.common.StatusEvent(message, warn);
  }

  /** Constructs a status event. */
  public StatusEvent(int progress, int maximum, String message) {
    event = new ome.scifio.common.StatusEvent(progress, maximum, message);
  }

  /** Constructs a status event. */
  public StatusEvent(int progress, int maximum, String message, boolean warn) {
    event = new ome.scifio.common.StatusEvent(progress, maximum, message, warn);
  }
  
  /** Constructs a loci.common.StatusEvent from an ome.scifio.common.StatusEvent */
  public StatusEvent(ome.scifio.common.StatusEvent e) {
    this.event = e;
  }

  // -- StatusEvent API methods --

  /** Gets progress value. Returns -1 if progress is unknown. */
  public int getProgressValue() { 
    return event.getProgressValue();
  }

  /** Gets progress maximum. Returns -1 if progress is unknown. */
  public int getProgressMaximum() { 
    return event.getProgressMaximum();
  }

  /** Gets status message. */
  public String getStatusMessage() {
    return event.getStatusMessage();
  }

  /** Returns whether or not this is a warning event. */
  public boolean isWarning() { 
    return event.isWarning();
  }

  // -- Object API methods --

  @Override
  public String toString() {
    return event.toString();
  }
  
  // -- Delegate Getter-
  
  public ome.scifio.common.StatusEvent getEvent() {
    return event;
  }

}
