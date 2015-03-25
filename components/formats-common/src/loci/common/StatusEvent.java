/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
 * #L%
 */

package loci.common;

/**
 * An event indicating a status update.
 */
public class StatusEvent {

  // -- Fields --

  /** Current progress value. */
  protected int progress;

  /** Current progress maximum. */
  protected int maximum;

  /** Current status message. */
  protected String status;

  /** Whether or not this is a warning event. */
  protected boolean warning;

  // -- Constructor --

  /** Constructs a status event. */
  public StatusEvent(String message) {
    this(-1, -1, message);
  }

  /** Constructs a status event. */
  public StatusEvent(String message, boolean warn) {
    this(-1, -1, message, warn);
  }

  /** Constructs a status event. */
  public StatusEvent(int progress, int maximum, String message) {
    this(progress, maximum, message, false);
  }

  /** Constructs a status event. */
  public StatusEvent(int progress, int maximum, String message, boolean warn) {
    this.progress = progress;
    this.maximum = maximum;
    status = message;
    warning = warn;
  }

  // -- StatusEvent API methods --

  /** Gets progress value. Returns -1 if progress is unknown. */
  public int getProgressValue() {
    return progress;
  }

  /** Gets progress maximum. Returns -1 if progress is unknown. */
  public int getProgressMaximum() {
    return maximum;
  }

  /** Gets status message. */
  public String getStatusMessage() {
    return status;
  }

  /** Returns whether or not this is a warning event. */
  public boolean isWarning() {
    return warning;
  }

  // -- Object API methods --

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Status");
    sb.append(": progress=" + progress);
    sb.append(", maximum=" + maximum);
    sb.append(", warning=" + warning);
    sb.append(", status='" + status + "'");
    return sb.toString();
  }

}
