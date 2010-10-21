//
// OMEModel.java
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

package ome.xml.model;

import java.util.List;
import java.util.Map;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/OMEModelObject.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/OMEModelObject.java">SVN</a></dd></dl>
 */
public interface OMEModel {

  OMEModelObject addModelObject(String id, OMEModelObject object);

  OMEModelObject removeModelObject(String id);

  OMEModelObject getModelObject(String id);

  Map<String, OMEModelObject> getModelObjects();

  boolean addReference(OMEModelObject a, Reference b);

  Map<OMEModelObject, List<Reference>> getReferences();

  int resolveReferences();
}
