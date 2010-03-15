//
// DependencyException.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.common.services;


/**
 * Exception thrown when there is an object instantiation error or error
 * processing dependencies.
 * 
 * @author Chris Allan <callan at blackcat dot ca>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/DependencyException.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/DependencyException.java">SVN</a></dd></dl>
 */
public class DependencyException extends Exception
{
  /** Serial for this version. */
  private static final long serialVersionUID = -7836244849086491562L;
  
  /** The class that was used in a failed instantiation. */
  private Class<? extends Service> failureClass;

  /**
   * Default constructor.
   * @param message Error message.
   */
  public DependencyException(String message)
  {
    super(message);
  }

  /**
   * Default constructor.
   * @param message Error message. 
   * @param klass Failed instantiation class.
   */
  public DependencyException(String message, Class<? extends Service> klass)
  {
    super(message);
    this.failureClass = klass;
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
    super(message, cause);
    this.failureClass = klass;
  }

  /**
   * Default constructor.
   * @param cause Upstream exception.
   */
  public DependencyException(Throwable cause)
  {
    super(cause);
  }

  /**
   * Returns the class that was used during a failed instantiation.
   * @return See above.
   */
  public Class<? extends Service> getFailureClass()
  {
    return failureClass;
  }

  @Override
  public String toString()
  {
    if (failureClass == null)
    {
      return getMessage();
    }
    return getMessage() + " for " + failureClass;
  }
}
