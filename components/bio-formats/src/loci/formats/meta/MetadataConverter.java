//
// MetadataConverter.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2010 UW-Madison LOCI and Glencoe Software, Inc.

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

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via MetadataAutogen on May 4, 2010 5:36:22 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import ome.xml.r201004.enums.*;
import ome.xml.r201004.primitives.*;

/**
 * A utility class containing a method for piping a source
 * {@link MetadataRetrieve} object into a destination {@link MetadataStore}.
 *
 * <p>This technique allows conversion between two different storage media.
 * For example, it can be used to convert an <code>OMEROMetadataStore</code>
 * (OMERO's metadata store implementation) into an
 * {@link loci.formats.ome.OMEXMLMetadata}, thus generating OME-XML from
 * information in an OMERO database.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/meta/MetadataConverter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/meta/MetadataConverter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public final class MetadataConverter {

  // -- Constructor --

  private MetadataConverter() { }

  // -- MetadataConverter API methods --

  /**
   * Copies information from a metadata retrieval object
   * (source) into a metadata store (destination).
   */
  public static void convertMetadata(MetadataRetrieve src, MetadataStore dest) {
    // TODO
  }
}
