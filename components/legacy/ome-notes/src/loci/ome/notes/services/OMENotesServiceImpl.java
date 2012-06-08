/*
 * #%L
 * OME Notes library for flexible organization and presentation of OME-XML
 * metadata.
 * %%
 * Copyright (C) 2007 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
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
