//
// OMEReaderWriterServiceImpl.java
//

/*
OME database I/O package for communicating with OME and OMERO servers.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Philip Huettl.

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

package loci.ome.io.services;

import loci.common.services.AbstractService;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.ome.io.OMEReader;
import loci.ome.io.OMEWriter;
import loci.ome.io.OmeroReader;

import org.openmicroscopy.ds.dto.Image;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/ome-io/src/loci/ome/io/services/OMEReaderWriterServiceImpl.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/ome-io/src/loci/ome/io/services/OMEReaderWriterServiceImpl.java">SVN</a></dd></dl>
 */
public class OMEReaderWriterServiceImpl extends AbstractService
  implements OMEReaderWriterService
{

  public OMEReaderWriterServiceImpl() {
    // Just being thorough with these dependencies.
    checkClassDependency(OMEReader.class);
    checkClassDependency(OMEWriter.class);
    checkClassDependency(OmeroReader.class);
    checkClassDependency(omero.model.Image.class);
    checkClassDependency(Image.class);
  }

  /* (non-Javadoc)
   * @see loci.formats.OMEReaderWriterService#newOMEROReader()
   */
  public IFormatReader newOMEROReader() {
    return new OmeroReader();
  }

  /* (non-Javadoc)
   * @see loci.formats.OMEReaderWriterService#newOMEROWriter()
   */
  public IFormatWriter newOMEROWriter() {
    throw new IllegalArgumentException("Unavailable OMERO writer.");
  }

  /* (non-Javadoc)
   * @see loci.formats.OMEReaderWriterService#newOMEReader()
   */
  public IFormatReader newOMEReader() {
    return new OMEReader();
  }

  /* (non-Javadoc)
   * @see loci.formats.OMEReaderWriterService#newOMEWriter()
   */
  public IFormatWriter newOMEWriter() {
    return new OMEWriter();
  }

}
