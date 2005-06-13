//
// MatlabUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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

package loci.visbio.util;

import loci.visbio.math.*;

/** MatlabUtil contains useful MATLAB and Octave functions. */
public abstract class MatlabUtil {

  // -- MATLAB methods --

  /** The singleton MATLAB control. */
  private static MatlabControl matlab;

  /** Gets the singleton MATLAB interface object. */
  public static MatlabControl getMatlab() {
    if (matlab == null) {
      try { matlab = new MatlabControl(); }
      catch (Throwable t) { }
    }
    return matlab;
  }

  /** Gets the version of MATLAB available, or null if none. */
  public static String getMatlabVersion() {
    MatlabControl mc = getMatlab();
    if (mc == null) return null;
    Object rval = null;
    try { rval = mc.blockingFeval("version", null); }
    catch (InterruptedException exc) { }
    return rval == null ? "Installed" : rval.toString();
  }


  // -- Octave methods --

  /** Gets the singleton Octave interface object. */
  public static OctaveContext getOctave() {
    try { return OctaveContext.getInstance(); }
    catch (Throwable t) { return null; }
  }

  /** Gets the version of Octave available, or null if none. */
  public static String getOctaveVersion() {
    OctaveContext cx = getOctave();
    if (cx == null) return null;
    OctaveValueList ovl = cx.evalString("version");
    return ovl.get(0).stringValue();
  }

}
