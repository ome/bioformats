/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package ome.jxr.ifd;

/**
 * Represents the native type implementation of the JPEG XR
 * {@link IFDEntryType#URATIONAL} and {@link IFDEntryType#SRATIONAL} IFD entry
 * types.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public class JXRRational {

  private int numerator;
  private int denominator;

  public JXRRational(int numerator, int denominator) {
    this.numerator = numerator;
    if (denominator == 0) {
      throw new IllegalArgumentException("Denominator cannot be 0.");
    }
    this.denominator = denominator;
  }

  public int getNumerator() {
    return numerator;
  }

  public int getDenominator() {
    return denominator;
  }

  public double getDouble() {
    return (double) numerator / denominator;
  }

}
