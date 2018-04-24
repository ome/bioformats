/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats;

/**
 * Represents a subdimension of Z, C, or T as needed for supporting Modulo
 * annotations.  See https://docs.openmicroscopy.org/latest/ome-model/developers/6d-7d-and-8d-storage.html
 */
public class Modulo {

  // -- Fields --

  public String parentDimension;
  public double start = 0;
  public double step = 1;
  public double end = 0;
  public String parentType;
  public String type;
  public String typeDescription;
  public String unit;
  public String[] labels;

  // -- Constructor --

  public Modulo(String dimension) {
    parentDimension = dimension;
  }

  public Modulo(Modulo m) {
    this.parentDimension = m.parentDimension;
    this.start = m.start;
    this.step = m.step;
    this.end = m.end;
    this.parentType = m.parentType;
    this.type = m.type;
    this.typeDescription = m.typeDescription;
    this.unit = m.unit;
    this.labels = m.labels;
  }

  // -- Methods --

  public int length() {
    if (labels != null) {
      return labels.length;
    }
    int len = (int) Math.rint((end - start) / step) + 1;
    return (len < 1) ? 1 : len; // Can't ever be less than 1.
  }

  public String toXMLAnnotation() {
    StringBuffer xml = new StringBuffer("<ModuloAlong");
    xml.append(parentDimension);
    xml.append(" Type=\"");

    if (type != null) {
      type = type.toLowerCase();
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
    xml.append(type);
    xml.append("\"");
    if (typeDescription != null) {
      xml.append(" TypeDescription=\"");
      xml.append(typeDescription);
      xml.append("\"");
    }
    if (unit != null) {
      xml.append(" Unit=\"");
      xml.append(unit);
      xml.append("\"");
    }
    if (end > start) {
      xml.append(" Start=\"");
      xml.append(start);
      xml.append("\" Step=\"");
      xml.append(step);
      xml.append("\" End=\"");
      xml.append(end);
      xml.append("\"");
    }
    if (labels != null) {
      xml.append(">");
      for (String label : labels) {
        xml.append("\n<Label>");
        xml.append(label);
        xml.append("</Label>");
      }
      xml.append("\n</ModuloAlong");
      xml.append(parentDimension);
      xml.append(">");
    }
    else {
      xml.append("/>");
    }

    return xml.toString();
  }

}
