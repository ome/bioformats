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

package loci.common.services;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A legacy wrapper class for ome.scifio.services.DependencyException.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/services/DependencyException.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/services/DependencyException.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class DependencyException extends Exception
{
  /** Serial for this version. */
  private static final long serialVersionUID = -7836244849086491562L;
  private ome.scifio.services.DependencyException e;

  /**
   * Default constructor.
   * @param message Error message.
   */
  public DependencyException(String message)
  {
    e = new ome.scifio.services.DependencyException(message);
  }

  /**
   * Default constructor.
   * @param message Error message. 
   * @param klass Failed instantiation class.
   */
  public DependencyException(String message, Class<? extends Service> klass)
  {
    e = new ome.scifio.services.DependencyException(message, klass);
  }

  /**
   * Default constructor.
   * @param message Error message. 
   * @param klass Failed instantiation class.
   * @param cause Upstream exception.
   */
  public DependencyException(String message, Class<? extends Service> klass,
      Throwable cause)
  {
    e = new ome.scifio.services.DependencyException(message, klass, cause);
  }

  /**
   * Default constructor.
   * @param cause Upstream exception.
   */
  public DependencyException(Throwable cause)
  {
    e = new ome.scifio.services.DependencyException(cause);
  }
  
  /**
   * Direct wrapping constructor 
   * @param ex
   */
  public DependencyException(ome.scifio.services.DependencyException ex) {
    e = ex;
  }

  /**
   * Returns the class that was used during a failed instantiation.
   * @return See above.
   */
  @SuppressWarnings("unchecked")
  public Class<? extends Service> getFailureClass()
  {
    return (Class<? extends Service>) e.getFailureClass();
  }

  // -- Delegators --

  @Override
  public boolean equals(Object obj) {
    return e.equals(obj);
  }
  
  @Override
  public int hashCode() {
    return e.hashCode();
  }
  
  @Override
  public String toString() {
    return e.toString();
  }

  public Throwable fillInStackTrace() {
    return (e == null) ? null : e.fillInStackTrace();
  }

  public Throwable getCause() {
    return e.getCause();
  }

  public String getLocalizedMessage() {
    return e.getLocalizedMessage();
  }

  public String getMessage() {
    return e.getMessage();
  }

  public StackTraceElement[] getStackTrace() {
    return e.getStackTrace();
  }

  public Throwable initCause(Throwable arg0) {
    return e.initCause(arg0);
  }

  public void printStackTrace() {
    e.printStackTrace();
  }

  public void printStackTrace(PrintStream arg0) {
    e.printStackTrace(arg0);
  }

  public void printStackTrace(PrintWriter arg0) {
    e.printStackTrace(arg0);
  }

  public void setStackTrace(StackTraceElement[] arg0) {
    e.setStackTrace(arg0);
  }
}
