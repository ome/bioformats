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

package ome.xml.model.primitives;

/**
 * Primitive type that represents an RGBA color.
 */
public class Color extends PrimitiveType<Integer> {

  public Color(Integer value) {
    super(value);
  }

  public Color(int r, int g, int b, int a) {
    this(r << 24 | g << 16 | b << 8 | a);
  }

  /**
   * Returns an <code>Color</code> object holding the value of
   * the specified string.
   * @param s The string to be parsed.
   * @return See above.
   */
  public static Color valueOf(String s) {
    return new Color(Integer.valueOf(s));
  }

  /**
   * Returns the red component of this color.
   *
   * @return See above.
   */
  public int getRed() {
    return (getValue() >> 24) & 0xff;
  }

  /**
   * Returns the green component of this color.
   *
   * @return See above.
   */
  public int getGreen() {
    return (getValue() >> 16) & 0xff;
  }

  /**
   * Returns the blue component of this color.
   *
   * @return See above.
   */
  public int getBlue() {
    return (getValue() >> 8) & 0xff;
  }

  /**
   * Returns the alpha component of this color.
   *
   * @return See above.
   */
  public int getAlpha() {
    return getValue() & 0xff;
  }

}
