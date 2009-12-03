//
// ND2Reader.java
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
 * ND2Reader is the file format reader for Nikon ND2 files.
 * It does not read files directly, but chooses which ND2 reader is
 * more appropriate.
 *
 * @see NativeND2Reader
 * @see LegacyND2Reader
 *
 * <dl><dt><b>Source code:</b</dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ND2Reader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ND2Reader.java">SVN</a></dd></dl>
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

}
