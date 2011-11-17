//
// UnknownFormatException.java
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

package loci.formats;

/**
 * UnknownFormatException is the exception thrown when the format of a
 * particular data stream or filename cannot be recognized or is unsupported.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/UnknownFormatException.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/UnknownFormatException.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class UnknownFormatException extends FormatException {

  public UnknownFormatException() { super(); }
  public UnknownFormatException(String s) { super(s); }
  public UnknownFormatException(String s, Throwable cause) { super(s, cause); }
  public UnknownFormatException(Throwable cause) { super(cause); }

}
