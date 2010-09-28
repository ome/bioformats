//
// OMEModelImpl.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/OMEModelObject.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/OMEModelObject.java">SVN</a></dd></dl>
 */
public class OMEModelImpl implements OMEModel {

  private Map<String, OMEModelObject> modelObjects = 
    new HashMap<String, OMEModelObject>();

  private Map<OMEModelObject, List<Reference>> references =
    new HashMap<OMEModelObject, List<Reference>>();

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(OMEModelImpl.class);

  /* (non-Javadoc)
   * @see ome.xml.model.OMEModel#removeModelObject(java.lang.String)
   */
  public OMEModelObject removeModelObject(String id) {
    return modelObjects.remove(id);
  }
  
  /* (non-Javadoc)
   * @see ome.xml.model.OMEModel#addModelObject(java.lang.String, ome.xml.model.OMEModelObject)
   */
  public OMEModelObject addModelObject(String id, OMEModelObject object) {
    return modelObjects.put(id, object);
  }

  /* (non-Javadoc)
   * @see ome.xml.model.OMEModel#getModelObject(java.lang.String)
   */
  public OMEModelObject getModelObject(String id) {
    return modelObjects.get(id);
  }

  /* (non-Javadoc)
   * @see ome.xml.model.OMEModel#getModelObjects()
   */
  public Map<String, OMEModelObject> getModelObjects() {
    return modelObjects;
  }

  /* (non-Javadoc)
   * @see ome.xml.model.OMEModel#addReference(java.lang.String, ome.xml.model.Reference)
   */
  public boolean addReference(OMEModelObject a, Reference b) {
    List<Reference> bList = references.get(a);
    if (bList == null) {
      bList = new ArrayList<Reference>();
      references.put(a, bList);
    }
    return bList.add(b);
  }

  /* (non-Javadoc)
   * @see ome.xml.model.OMEModel#getReferences()
   */
  public Map<OMEModelObject, List<Reference>> getReferences() {
    return references;
  }

  /* (non-Javadoc)
   * @see ome.xml.model.OMEModel#resolveReferences()
   */
  public int resolveReferences() {
    int unhandledReferences = 0;
    for (Entry<OMEModelObject, List<Reference>> entry : references.entrySet())
    {
      OMEModelObject a = entry.getKey();
      if (a == null) {
        List<Reference> references = entry.getValue();
        if (references == null) {
          LOGGER.error("Null reference to null object, continuing.");
          continue;
        }
        LOGGER.error("Null reference to {} objects, continuing.",
                     references.size());
        unhandledReferences += references.size();
        continue;
      }
      for (Reference reference : entry.getValue()) {
        String referenceID = reference.getID();
        OMEModelObject b = getModelObject(referenceID);
        if (b == null) {
          LOGGER.warn("{} reference to {} missing from object hierarchy.",
                      a, referenceID);
          unhandledReferences++;
          continue;
        }
        a.link(reference, b);
      }
    }
    return unhandledReferences;
  }

}
