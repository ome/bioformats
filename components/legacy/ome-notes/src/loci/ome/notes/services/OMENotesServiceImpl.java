//
// OMENotesServiceImpl.java
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

package loci.ome.notes.services;

import loci.common.services.AbstractService;
import loci.common.services.OMENotesService;
import loci.ome.notes.Notes;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/legacy/ome-notes/src/loci/ome/notes/services/OMENotesServiceImpl.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/legacy/ome-notes/src/loci/ome/notes/services/OMENotesServiceImpl.java">SVN</a></dd></dl>
 */
public class OMENotesServiceImpl extends AbstractService
  implements OMENotesService {

  /**
   * Default constructor.
   */
  public OMENotesServiceImpl() {
    checkClassDependency(Notes.class);
  }

  /* (non-Javadoc)
   * @see loci.formats.dependency.OMENotesService#newNotes()
   */
  public void newNotes(String filename) {
    new Notes(null, filename);
  }

}
