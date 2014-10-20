/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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

package ome.xml.model.primitives;

/**
 * A primitive number from an XSD definition with a given set of constraints.
 *
 * @author callan
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PrimitiveType.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PrimitiveType.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public abstract class PrimitiveNumber<T extends Number> extends Number implements PrimitiveInterface<T>{

  /** The delegate value. */
  T value;

  /**
   * Default constructor.
   * @param value The delegate value to use.
   */
  PrimitiveNumber(T value) {
    this.value = value;
  }

  /**
   * Default constructor.
   * @param value The delegate value to use.
   */
  PrimitiveNumber() {
  }

  /**
   * Retrieves the concrete delegate value.
   * @return See above.
   */
  public T getValue() {
    return value;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return value.toString();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PrimitiveNumber<?>) {
      return value.equals(((PrimitiveNumber<?>) obj).getValue());
    }
    return value.equals(obj);
  }
  
  /**
   * Retrieves the value as a double.
   * This function is required by Number and may not make sence for
   * the ome type.
   * @return See above.
   */
  public double doubleValue() {
    return value.doubleValue();
  }

  /**
   * Retrieves the value as a float.
   * This function is required by Number and may not make sence for
   * the ome type.
   * @return See above.
   */
  public float floatValue() {
    return value.floatValue();
  }

  /**
   * Retrieves the value as a long.
   * This function is required by Number and may not make sence for
   * the ome type.
   * @return See above.
   */
  public long longValue() {
    return value.longValue();
  }

  /**
   * Retrieves the value as an int.
   * This function is required by Number and may not make sence for
   * the ome type.
   * @return See above.
   */
  public int intValue() {
    return value.intValue();
  }

}
