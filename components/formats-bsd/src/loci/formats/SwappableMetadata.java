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
package loci.formats;

/**
 * Extends {@link CoreMetadata} to include the notion of an "input order" for planes:
 * the input order being the order planes are stored on disk.
 * <p>
 * NB: Input order can differ from the {@link CoreMetadata#dimensionOrder} field,
 * the latter being the order dimensions are returned (and thus returned by
 * {@link IFormatReader#getDimensionOrder()}).
 * </p>
 * <p>
 * This class is primarily intended for use by {@link DimensionSwapper} and its children,
 * which introduce the API to separate input and output orders.
 * </p>
 * @author Mark Hiner
 */
public class SwappableMetadata extends CoreMetadata {
  
  // -- Fields --
  
  /**
   * Order in which dimensions are stored.  Must be one of the following:<ul>
   *  <li>XYCZT</li>
   *  <li>XYCTZ</li>
   *  <li>XYZCT</li>
   *  <li>XYZTC</li>
   *  <li>XYTCZ</li>
   *  <li>XYTZC</li>
   * </ul>
   */
  public String inputOrder;
  
  // -- Constructors --
  
  public SwappableMetadata() {
    super();
  }
  
  public SwappableMetadata(IFormatReader r, int seriesNo) {
    super(r, seriesNo);
    inputOrder = dimensionOrder;
  }

  public SwappableMetadata(SwappableMetadata c) {
    super(c);
    inputOrder = dimensionOrder;
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException {
      return super.clone();
  }

  @Override
  public CoreMetadata clone(IFormatReader r, int coreIndex) {
      return new SwappableMetadata(r, coreIndex);
  }

  // -- CoreMetadata methods --
  
}
