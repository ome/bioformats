/*
 * #%L
 * OME database I/O package for communicating with OME and OMERO servers.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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

package loci.ome.io.services;

import loci.common.services.Service;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-io/src/loci/ome/io/services/OMEReaderWriterService.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-io/src/loci/ome/io/services/OMEReaderWriterService.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public interface OMEReaderWriterService extends Service {

  /**
   * Creates a new OME IFormatReader implementation.
   * @return See above.
   */
  public IFormatReader newOMEReader();

  /**
   * Creates a new OMERO IFormatReader implementation.
   * @return See above.
   */
  public IFormatReader newOMEROReader();

  /**
   * Creates a new OMERO IFormatWriter implementation.
   * @return See above.
   */
  public IFormatWriter newOMEWriter();

  /**
   * Creates a new OMERO IFormatWriter implementation.
   * @return See above.
   */
  public IFormatWriter newOMEROWriter();

}
