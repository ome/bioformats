/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package ome.xml.model.primitives;

import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * Primitive type that represents an RGBA color.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/src/ome/xml/model/primitives/Color.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/src/ome/xml/model/primitives/Color.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Color {

  private float red;
  private float green;
  private float blue;
  private float alpha;

  public Color() {
    this(1.0f, 1.0f, 1.0f, 1.0f);
  }

  public Color(float red, float green, float blue, float alpha) {
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;

    if (this.red < 0.0f) this.red = 0.0f;
    if (this.red > 1.0f) this.red = 1.0f;
    if (this.green < 0.0f) this.green = 0.0f;
    if (this.green > 1.0f) this.green = 1.0f;
    if (this.blue < 0.0f) this.blue = 0.0f;
    if (this.blue > 1.0f) this.blue = 1.0f;
    if (this.alpha < 0.0f) this.alpha = 0.0f;
    if (this.alpha > 1.0f) this.alpha = 1.0f;
  }

  public Color(float red, float green, float blue) {
    this(red, green, blue, 1.0f);
  }

  public Color(int red, int green, int blue) {
    this(red, green, blue, 255);
  }

  public Color(int red, int green, int blue, int alpha) {
    this(red/255.0f, green/255.0f, blue/255.0f, alpha/255.0f);
  }

  /**
   * Returns an <code>Color</code> object holding the value of
   * the specified string.
   * @param s The string to be parsed.
   * @return See above.
   */
  public static Color valueOf(String s) {
    Scanner scanner = new Scanner(s);
    float[] f = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    try {
      for (int i = 0; i < 4; i++) {
        if (scanner.hasNext())
          f[i] = scanner.nextFloat();
        else
          break;
      }
    }
    catch (InputMismatchException e) {}

    return new Color(f[0], f[1], f[2], f[3]);
  }

  /**
   * Returns the red component of this color.
   *
   * @return See above.
   */
  public float getRed() {
    return red;
  }

  /**
   * Returns the green component of this color.
   *
   * @return See above.
   */
  public float getGreen() {
    return green;
  }

  /**
   * Returns the blue component of this color.
   *
   * @return See above.
   */
  public float getBlue() {
    return blue;
  }

  /**
   * Returns the alpha component of this color.
   *
   * @return See above.
   */
  public float getAlpha() {
    return alpha;
  }

  /**
   * Returns the red component of this color as an 8-bit color.
   *
   * @return See above.
   */
  public int getRed8Bit() {
    return (int) Math.round(red * 255.0f);
  }

  /**
   * Returns the red component of this color as a 16-bit color.
   *
   * @return See above.
   */
  public int getRed16Bit() {
    return (int) Math.round(red * 65535.0f);
  }

  /**
   * Returns the green component of this color as an 8-bit color.
   *
   * @return See above.
   */
  public int getGreen8Bit() {
    return (int) Math.round(green * 255.0f);
  }

  /**
   * Returns the green component of this color as a 16-bit color.
   *
   * @return See above.
   */
  public int getGreen16Bit() {
    return (int) Math.round(green * 65535.0f);
  }

  /**
   * Returns the blue component of this color as an 8-bit color.
   *
   * @return See above.
   */
  public int getBlue8Bit() {
    return (int) Math.round(blue * 255.0f);
  }

  /**
   * Returns the blue component of this color as a 16-bit color.
   *
   * @return See above.
   */
  public int getBlue16Bit() {
    return (int) Math.round(blue * 65535.0f);
  }

  /**
   * Returns the alpha component of this color as an 8-bit color.
   *
   * @return See above.
   */
  public int getAlpha8Bit() {
    return (int) Math.round(alpha * 255.0f);
  }

  /**
   * Returns the alpha component of this color as a 16-bit color.
   *
   * @return See above.
   */
  public int getAlpha16Bit() {
    return (int) Math.round(alpha * 65535.0f);
  }


}
