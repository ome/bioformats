/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

import ome.xml.model.enums.EnumerationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author callan
 *
 */
public interface OMEModelObject {

  /**
   * Takes the entire object hierarchy and produces an XML DOM tree.
   * @param document Destination document for element creation, etc.
   * @return XML DOM tree root element for this model object.
   */
  Element asXMLElement(Document document);

  /** 
   * Updates the object hierarchy recursively from an XML DOM tree.
   * <b>NOTE:</b> No properties are removed, only added or updated.
   * @param element Root of the XML DOM tree to construct a model object
   * graph from.
   * @param model Handler for the OME model which keeps track of instances
   * and references seen during object population.
   * @throws EnumerationException If there is an error instantiating an
   * enumeration during model object creation.
   */
  void update(Element element, OMEModel model) throws EnumerationException;

  /**
   * Link a given OME model object to this model object.
   * @param reference The <i>type</i> qualifier for the reference. This should
   * be the corresponding reference type for <code>o</code>. If, for example,
   * <code>o</code> is of type <code>Image</code>, <code>reference</code>
   * <b>MUST</b> be of type <code>ImageRef</code>.
   * @param o Model object to link to.
   * @return <code>true</code> if this model object was able to handle the
   * reference, <code>false</code> otherwise.
   */
  boolean link(Reference reference, OMEModelObject o);
}
