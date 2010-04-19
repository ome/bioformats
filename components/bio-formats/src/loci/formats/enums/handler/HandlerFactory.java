//
// HandlerFactory.java
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

package loci.formats.enums.handler;

import ome.xml.r201004.enums.Enumeration;
import ome.xml.r201004.enums.EnumerationException;

/**
 * A factory for producing an IEnumerationHandler instance given an
 * enumeration class.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/enums/handler/HandlerFactory.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/enums/handler/HandlerFactory.java">SVN</a></dd></dl>
 */
public class HandlerFactory {

  /** Package containing IEnumerationHandler implementations. */
  private static final String HANDLER_PACKAGE = "loci.formats.enums.handler.";

  // -- HandlerFactory API methods --

  /**
   * Return an instance of the IEnumerationHandler corresponding to the
   * given enumeration type.
   * @throws EnumerationException if an appropriate IEnumerationHandler cannot
   *   be found or instantiated.
   */
  public static IEnumerationHandler getHandler(
    Class<? extends Enumeration> type)
    throws EnumerationException
  {
    String typeName = type.getSimpleName();
    String handlerName = HANDLER_PACKAGE + typeName + "EnumHandler";
    Exception cause = null;
    try {
      Class handlerClass = Class.forName(handlerName);
      return (IEnumerationHandler) handlerClass.newInstance();
    }
    catch (ClassNotFoundException c) { cause = c; }
    catch (ClassCastException c) { cause = c; }
    catch (InstantiationException i) { cause = i; }
    catch (IllegalAccessException i) { cause = i; }

    throw new EnumerationException("Could not find handler for enumeration " +
      type, cause);
  }

}
