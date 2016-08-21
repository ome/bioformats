/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.in;

import loci.formats.DelegateReader;
import loci.formats.FormatTools;

/**
 * ND2Reader is the file format reader for Nikon ND2 files.
 * It does not read files directly, but chooses which ND2 reader is
 * more appropriate.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ND2Reader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ND2Reader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see NativeND2Reader
 * @see LegacyND2Reader
 */
public class ND2Reader extends DelegateReader {

  // -- Constructor --

  /** Constructs a new ND2 reader. */
  public ND2Reader() {
    super("Nikon ND2", "nd2");
    nativeReader = new NativeND2Reader();
    legacyReader = new LegacyND2Reader();
    nativeReaderInitialized = false;
    legacyReaderInitialized = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

}
