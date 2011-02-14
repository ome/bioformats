//
// OMENotesServiceImpl.java
//

/*
OME Notes library for flexible organization and presentation of OME-XML
metadata. Copyright (C) 2006-@year@ Melissa Linkert and Christopher Peterson.

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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-notes/src/loci/ome/notes/services/OMENotesServiceImpl.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-notes/src/loci/ome/notes/services/OMENotesServiceImpl.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
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
