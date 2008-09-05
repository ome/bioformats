//
// SlimTypes.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.slim;

import visad.*;

/**
 * Data structure encapsulating shared VisAD math types and domain sets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/SlimTypes.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/SlimTypes.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SlimTypes {

  // -- Fields - RealTypes --

  // element
  public final RealType xType;

  // line
  public final RealType yType;

  // bin
  public final RealType bType;

  // channel
  public final RealType cType;

  // count
  public final RealType vType;

  // value
  public final RealType vType2;

  // value_fit
  public final RealType vTypeFit;

  // value_res
  public final RealType vTypeRes;

  // -- Fields - Units --

  public final Unit[] bcUnits;

  // -- Fields - Tuples --

  // (element, line)
  public final RealTupleType xy;

  // (bin, channel)
  public final RealTupleType bc;

  // (bin, count)
  public final RealTupleType bv;

  // (bin, channel, count)
  public final RealTupleType bcv;

  // (count, value)
  public final RealTupleType vv;

  // -- Fields - Sets --

  // set: (0, 0) - (width - 1, height - 1)
  public final Integer2DSet xySet;

  // set: minWave - maxWave
  public final Linear1DSet cSet;

  // -- Fields - Functions --

  // ((element, line) -> count
  public final FunctionType xyvFunc;

  // (channel -> ((element, line) -> count))
  public final FunctionType cxyvFunc;

  // ((bin, channel) -> (count, value))
  public final FunctionType bcvFunc;

  // ((bin, channel) -> value_fit)
  public final FunctionType bcvFuncFit;

  // ((bin, channel) -> value_res)
  public final FunctionType bcvFuncRes;

  public SlimTypes(int width, int height, int channels,
    double minWave, double maxWave) throws VisADException
  {
    xType = RealType.getRealType("element");
    yType = RealType.getRealType("line");
    ScaledUnit ns = new ScaledUnit(1e-9, SI.second, "ns");
    ScaledUnit nm = new ScaledUnit(1e-9, SI.meter, "nm");
    bcUnits = new Unit[] {ns, nm};
    bType = RealType.getRealType("bin", ns);
    cType = RealType.getRealType("channel", nm);
    vType = RealType.getRealType("count");
    xy = new RealTupleType(xType, yType);
    xySet = new Integer2DSet(xy, width, height);
    xyvFunc = new FunctionType(xy, vType);
    cxyvFunc = new FunctionType(cType, xyvFunc);
    cSet = new Linear1DSet(cType,
      minWave, maxWave, channels, null, new Unit[] {nm}, null);
    bc = new RealTupleType(bType, cType);
    bv = new RealTupleType(bType, vType);
    bcv = new RealTupleType(bType, cType, vType);
    vType2 = RealType.getRealType("value");
    vv = new RealTupleType(vType, vType2);
    bcvFunc = new FunctionType(bc, vv);
    vTypeFit = RealType.getRealType("value_fit");
    bcvFuncFit = new FunctionType(bc, vTypeFit);
    vTypeRes = RealType.getRealType("value_res");
    bcvFuncRes = new FunctionType(bc, vTypeRes);
  }

}
