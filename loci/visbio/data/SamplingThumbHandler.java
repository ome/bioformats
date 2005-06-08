//
// SamplingThumbHandler.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.data;

import java.rmi.RemoteException;
import loci.visbio.util.VisUtil;
import visad.FlatField;
import visad.VisADException;

/** Provides logic for handling data sampling thumbnails. */
public class SamplingThumbHandler extends ThumbnailHandler {

  // -- Constructor --

  /** Creates a thumbnail handler. */
  public SamplingThumbHandler(DataTransform data, String filename) {
    super(data, filename);
  }


  // -- Internal ThumbnailHandler API methods --

  /**
   * Computes a thumbnail for the given dimensional position.
   * This method is intelligent enough to check for parent thumbnails
   * before going to disk, and resample from them if they are available.
   */
  protected FlatField computeThumb(int[] pos) {
    DataSampling samp = (DataSampling) data;
    ThumbnailHandler th = samp.getParent().getThumbHandler();
    if (th == null) return super.computeThumb(pos);

    int[] min = samp.getMin();
    int[] step = samp.getStep();
    boolean[] range = samp.getRange();

    int[] p = new int[pos.length];
    for (int i=0; i<p.length; i++) p[i] = min[i] + step[i] * pos[i] - 1;
    FlatField ff = th.getThumb(p);
    if (ff == null) return super.computeThumb(pos);

    float[] smin = {0, 0};
    float[] smax = {samp.getImageWidth() - 1, samp.getImageHeight() - 1};
    try { return VisUtil.resample(ff, resolution, range, smin, smax); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }

}
