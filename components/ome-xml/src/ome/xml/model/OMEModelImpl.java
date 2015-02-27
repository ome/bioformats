/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/OMEModelObject.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/OMEModelObject.java;hb=HEAD">Gitweb</a></dd></dl>
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
    if (Reference.class.isAssignableFrom(object.getClass())) {
      return object;
    }
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
