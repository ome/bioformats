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

package loci.formats.cache;

/**
 * A crosshair strategy caches planes extending from the the current
 * dimensional position along each individual axis, but not combinations of
 * axes. For example, if the current position is Z5-C2-T18, the strategy will
 * preload the next and previous focal planes (Z6-C2-T18 and Z4-C2-T18),
 * the next and previous channels (Z5-C3-T18 and Z5-C1-T18),
 * and the next and previous time points (Z5-C2-T19 and Z5-C2-T17),
 * but nothing diverging on multiple axes (e.g., Z6-C3-T19 or Z4-C1-T17).
 * <p>
 * Planes closest to the current position are loaded first, with axes
 * prioritized according to the cache strategy's priority settings.
 * <p>
 * To illustrate the crosshair strategy, here is a diagram showing a case
 * in 2D with 35 dimensional positions (5Z x 7T). For both Z and T, order is
 * centered, range is 2, and priority is normal.
 * The numbers indicate the order planes will be cached, with "0"
 * corresponding to the current dimensional position (Z2-3T).
 * <pre>
 *      T  0  1  2  3  4  5  6
 *    Z /---------------------
 *    0 |           6
 *    1 |           2
 *    2 |     8  4  0  3  7
 *    3 |           1
 *    4 |           5
 * </pre>
 */
public class CrosshairStrategy extends CacheStrategy {

  // -- Constructor --

  /** Constructs a crosshair strategy. */
  public CrosshairStrategy(int[] lengths) { super(lengths); }

  // -- CacheStrategy API methods --

  /* @see CacheStrategy#getPossiblePositions() */
  @Override
  protected int[][] getPossiblePositions() {
    // only positions diverging along a single axis can ever be cached
    int len = 1;
    for (int i=0; i<lengths.length; i++) len += lengths[i] - 1;
    int[][] p = new int[len][lengths.length];
    for (int i=0, c=0; i<lengths.length; i++) {
      for (int j=1; j<lengths[i]; j++) p[++c][i] = j;
    }
    return p;
  }

}
