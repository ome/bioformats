//
// DefaultMetadataOptions.java
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

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/DefaultMetadataOptions.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/DefaultMetadataOptions.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class DefaultMetadataOptions implements MetadataOptions {

  private MetadataLevel level;

  public DefaultMetadataOptions() {
    this.level = MetadataLevel.ALL;
  }

  public DefaultMetadataOptions(MetadataLevel level) {
    this.level = level;
  }

  /* (non-Javadoc)
   * @see loci.formats.in.MetadataOptions#getMetadataLevel()
   */
  public MetadataLevel getMetadataLevel() {
    return level;
  }

  /* (non-Javadoc)
   * @see loci.formats.in.MetadataOptions#setMetadataLevel(loci.formats.in.MetadataLevel)
   */
  public void setMetadataLevel(MetadataLevel level) {
    this.level = level;
  }

}
