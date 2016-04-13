/*
 * #%L
 * Java bindings and pre-built binaries for libjpeg-turbo.
 * %%
 * Copyright (C) 2013 - 2016 Open Microscopy Environment:
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
/*
 * Copyright (C)2011 D. R. Commander.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the libjpeg-turbo Project nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS",
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.libjpegturbo.turbojpeg;

/**
 * Fractional scaling factor
 */
public class TJScalingFactor {

  public TJScalingFactor(int num, int denom) throws Exception {
    if(num < 1 || denom < 1)
      throw new Exception("Numerator and denominator must be >= 1");
    this.num = num;
    this.denom = denom;
  }

  /**
   * Returns numerator
   * @return numerator
   */
  public int getNum() {
    return num;
  }

  /**
   * Returns denominator
   * @return denominator
   */
  public int getDenom() {
    return denom;
  }

  /**
   * Returns the scaled value of <code>dimension</code>.  This function
   * performs the integer equivalent of
   * <code>ceil(dimension * scalingFactor)</code>.
   * @return the scaled value of <code>dimension</code>
   */
  public int getScaled(int dimension) {
    return (dimension * num + denom - 1) / denom;
  }

  /**
   * Returns true or false, depending on whether this instance and
   * <code>other</code> have the same numerator and denominator.
   * @return true or false, depending on whether this instance and
   * <code>other</code> have the same numerator and denominator
   */
  public boolean equals(TJScalingFactor other) {
    return (this.num == other.num && this.denom == other.denom);
  }

  /**
   * Returns true or false, depending on whether this instance is equal to
   * 1/1.
   * @return true or false, depending on whether this instance is equal to
   * 1/1
   */
  public boolean isOne() {
    return (num == 1 && denom == 1);
  }

  /**
   * Numerator
   */
  private int num = 1;

  /**
   * Denominator
   */
  private int denom = 1;
};
