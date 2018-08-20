/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2018 Open Microscopy Environment:
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

import ome.xml.model.primitives.PositiveInteger;

/**
 * Interface for defining pyramid resolutions.
 */
public interface IPyramidStore {

  /**
   * Define the image width in pixels of the given pyramid resolution.
   * Both width and height must be defined for each desired resolution.
   *
   * @param sizeX the image width in pixels
   * @param image the Image (series) index
   * @param resolution the resolution index; expected to be greater than 0
   *    as the largest resolution is defined by the Image itself.
   *
   * @see #setResolutionSizeY(PositiveInteger, int, int)
   */
  void setResolutionSizeX(PositiveInteger sizeX, int image, int resolution);

  /**
   * Define the image height in pixels of the given pyramid resolution.
   * Both width and height must be defined for each desired resolution.
   *
   * @param sizeY the image height in pixels
   * @param image the Image (series) index
   * @param resolution the resolution index; expected to be greater than 0
   *    as the largest resolution is defined by the Image itself.
   *
   * @see #setResolutionSizeX(PositiveInteger, int, int)
   */
  void setResolutionSizeY(PositiveInteger sizeY, int image, int resolution);

  /**
   * Retrieve the number of pyramid resolutions defined for the given Image.
   *
   * @param image the Image (series) index
   * @return the number of valid pyramid resolutions, including the largest
   *    resolution; expected to be positive if the Image index is valid.
   */
  int getResolutionCount(int image);

  /**
   * Retrieve the image width in pixels of the given pyramid resolution.
   *
   * @param image the Image (series) index
   * @param resolution the resolution index; expected to be greater than 0
   *    as the largest resolution is defined by the Image itself.
   * @return the width in pixels, or null if either index is invalid
   */
  PositiveInteger getResolutionSizeX(int image, int resolution);

  /**
   * Retrieve the image height in pixels of the given pyramid resolution.
   *
   * @param image the Image (series) index
   * @param resolution the resolution index; expected to be greater than 0
   *    as the largest resolution is defined by the Image itself.
   * @return the height in pixels, or null if either index is invalid
   */
  PositiveInteger getResolutionSizeY(int image, int resolution);

}
