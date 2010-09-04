//
// ValidationErrorHandler.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Chris Allan.

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

package loci.common.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Used by validateXML to handle XML validation errors.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/xml/ValidationErrorHandler.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/common/src/loci/common/xml/ValidationErrorHandler.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ValidationErrorHandler implements ErrorHandler {

  private int errors = 0;

  public boolean ok() { return errors == 0; }

  public int getErrorCount() { return errors; }

  public void error(SAXParseException e) {
    XMLTools.LOGGER.error(e.getMessage());
    errors++;
  }

  public void fatalError(SAXParseException e) {
    XMLTools.LOGGER.error(e.getMessage());
    errors++;
  }

  public void warning(SAXParseException e) {
    XMLTools.LOGGER.warn(e.getMessage());
    errors++;
  }

}
