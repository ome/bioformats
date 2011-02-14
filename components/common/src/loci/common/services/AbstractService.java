//
// AbstractService.java
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

package loci.common.services;


/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/services/AbstractService.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/services/AbstractService.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public abstract class AbstractService implements Service {

  /**
   * Checks a given class dependency at runtime to ensure that a given class
   * will be available. This method is expected to be called at least once by
   * all service implementations.
   * @param klass A class that this service depends upon.
   */
  protected void checkClassDependency(Class<? extends Object> klass) {
    // Just need *something* here to trigger a ClassNotFoundException if the
    // class isn't on the classpath.
    klass.getName();
  }

}
