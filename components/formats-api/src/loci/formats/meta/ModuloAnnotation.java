/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
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

package loci.formats.meta;

import loci.common.xml.XMLTools;
import loci.formats.Modulo;
import loci.formats.ome.OMEXMLMetadata;
import ome.xml.model.XMLAnnotation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ModuloAnnotation extends XMLAnnotation {

  public static final String MODULO_NS;
  static {
    MODULO_NS = "openmicroscopy.org/omero/dimension/modulo";
  }

  private Modulo modulo;

  /**
   * Set the value of this annotation using the given Modulo object
   * @param meta the associated OMEXMLMetadata
   * @param m the Modulo object from which to retrieve dimension information
   */
  public void setModulo(OMEXMLMetadata meta, Modulo m) {
    modulo = m;
    setNamespace(MODULO_NS);
    Document doc = XMLTools.createDocument();
    Element r = makeModuloElement(doc);
    setValue(XMLTools.dumpXML(null, doc, r, false));
  }

  /**
   * Construct a DOM element for this annotation using
   * the given Document as the root.
   * @param document the root document node
   */
  protected Element makeModuloElement(Document document) {
    Element mtop = document.createElement("Modulo");
    mtop.setAttribute("namespace", "http://www.openmicroscopy.org/Schemas/Additions/2011-09");
    // TODO: the above should likely NOT be hard-coded
    Element m = document.createElement("ModuloAlong" + modulo.parentDimension);
    mtop.appendChild(m);

    String type = modulo.type;
    String typeDescription = modulo.typeDescription;
    if (type != null) {
      type = type.toLowerCase();
    }
    // Handle CZI files for the moment.
    // TODO: see https://trac.openmicroscopy.org/ome/ticket/11720
    if (type.equals("rotation")) {
      type = "angle";
    }
    if (type == null || (!type.equals("angle") && !type.equals("phase") &&
      !type.equals("tile") && !type.equals("lifetime") &&
      !type.equals("lambda")))
    {
      if (typeDescription == null) {
        typeDescription = type;
      }
      type = "other";
    }

    m.setAttribute("Type", type);
    m.setAttribute("TypeDescription", typeDescription);
    if (modulo.unit != null) {
      m.setAttribute("Unit", modulo.unit);
    }
    if (modulo.end > modulo.start) {
      m.setAttribute("Start", String.valueOf(modulo.start));
      m.setAttribute("Step", String.valueOf(modulo.step));
      m.setAttribute("End", String.valueOf(modulo.end));
    }
    if (modulo.labels != null) {
      for (String label : modulo.labels) {
        Element labelNode = document.createElement("Label");
        labelNode.setTextContent(label);
        m.appendChild(labelNode);
      }
    }
    return mtop;
  }
}
