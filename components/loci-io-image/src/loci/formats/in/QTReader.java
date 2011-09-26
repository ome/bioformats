//
// QTReader.java
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

package loci.formats.in;

import loci.formats.DelegateReader;
import loci.formats.FormatTools;

/**
 * QTReader is the file format reader for QuickTime movie files.
 * It does not read files directly, but chooses which QuickTime reader is
 * more appropriate.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/QTReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/QTReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see NativeQTReader
 * @see LegacyQTReader
 */
public class QTReader extends DelegateReader {

  // -- Constructor --

  /** Constructs a new QuickTime reader. */
  public QTReader() {
    super("QuickTime", "mov");
    nativeReader = new NativeQTReader();
    legacyReader = new LegacyQTReader();
    nativeReaderInitialized = false;
    legacyReaderInitialized = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

}
