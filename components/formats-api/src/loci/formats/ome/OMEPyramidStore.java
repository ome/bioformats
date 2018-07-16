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

package loci.formats.ome;

import java.util.ArrayList;
import java.util.List;

import loci.formats.meta.IPyramidStore;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Extension of OMEXMLMetadataImpl that allows for pyramid resolution storage and retrieval.
 */
public class OMEPyramidStore extends OMEXMLMetadataImpl implements IPyramidStore {

  private List<List<Resolution>> resolutions = new ArrayList<List<Resolution>>();

  /* @see IPyramidStore#setResolutionSizeX(PositiveInteger, int, int) */
  public void setResolutionSizeX(PositiveInteger sizeX, int image, int resolution) {
    checkImageIndex(image);
    Resolution r = lookupResolution(image, resolution, true);
    r.x = sizeX;
  }

  /* @see IPyramidStore#setResolutionSizeY(PositiveInteger, int, int) */
  public void setResolutionSizeY(PositiveInteger sizeY, int image, int resolution) {
    checkImageIndex(image);
    Resolution r = lookupResolution(image, resolution, true);
    r.y = sizeY;
  }

  /* @see IPyramidStore#getResolutionCount(int) */
  public int getResolutionCount(int image) {
    checkImageIndex(image);
    return image < resolutions.size() ? resolutions.get(image).size() : 1;
  }

  /* @see IPyramidStore#getResolutionSizeX(int, int) */
  public PositiveInteger getResolutionSizeX(int image, int resolution) {
    checkResolutionIndex(image, resolution);
    Resolution r = lookupResolution(image, resolution);
    return r == null ? null : r.x;
  }

  /* @see IPyramidStore#getResolutionSizeY(int, int) */
  public PositiveInteger getResolutionSizeY(int image, int resolution) {
    checkResolutionIndex(image, resolution);
    Resolution r = lookupResolution(image, resolution);
    return r == null ? null : r.y;
  }

  // -- Helper methods --

  /**
   * Verify that the given index is non-negative and less than the Image count.
   *
   * @param image the index to check
   * @throws IllegalArgumentException if the index is invalid
   */
  private void checkImageIndex(int image) {
    if (image < 0 || image >= getImageCount()) {
      throw new IllegalArgumentException("Invalid image index: " + image);
    }
  }

  /**
   * Verify that the given image and resolution indices are valid.
   *
   * @param image the Image index to check
   * @param res the resolution index to check
   * @throws IllegalArgumentException if either index is invalid
   *
   * @see #checkImageIndex(int)
   */
  private void checkResolutionIndex(int image, int res) {
    checkImageIndex(image);
    if (res == 0) {
      throw new IllegalArgumentException("Use {get,set}PixelsSize{X,Y} " +
        "to work with the largest resolution");
    }
    else if (res < 0 || res >= getResolutionCount(image)) {
      throw new IllegalArgumentException(
        "Invalid resolution index for image #" + image + ": " + res);
    }
  }

  /**
   * Find the Resolution object for the given indices.
   *
   * @param image the Image index
   * @param res the resolution index
   * @return the corresponding Resolution object, or null if one does not exist
   * @see #lookupResolution(int, int, boolean)
   */
  private Resolution lookupResolution(int image, int res) {
    return lookupResolution(image, res, false);
  }

  /**
   * Find the Resolution object for the given indices.
   * If 'insert' is true, then a Resolution will be created
   * if one does not exist.
   *
   * @param image the Image index
   * @param res the resolution index
   * @param insert true if a Resolution should be created
   * @return the corresponding Resolution object, or null if one
   *    does not exist and 'insert' is false
   */
  private Resolution lookupResolution(int image, int res, boolean insert) {
    if (image < 0 || image >= resolutions.size()) {
      if (insert) {
        insertResolution(new Resolution(), image, res);
        return lookupResolution(image, res);
      }
      return null;
    }
    List<Resolution> currentResolutions = resolutions.get(image);
    if (res < 0 || res >= currentResolutions.size()) {
      if (insert) {
        insertResolution(new Resolution(), image, res);
        return lookupResolution(image, res);
      }
      return null;
    }
    return currentResolutions.get(res);
  }

  /**
   * Store the given Resolution object at the given indices.
   *
   * @param r the Resolution object to store
   * @param image the Image index
   * @param res the resolution index
   */
  private void insertResolution(Resolution r, int image, int res) {
    while (image >= resolutions.size()) {
      resolutions.add(new ArrayList<Resolution>());
    }
    List<Resolution> currentResolutions = resolutions.get(image);
    while (res > currentResolutions.size()) {
      currentResolutions.add(new Resolution());
    }
    currentResolutions.add(r);
  }

  class Resolution {
    public PositiveInteger x, y;
    public int index;
    public int parentImage = -1;
  }

}
