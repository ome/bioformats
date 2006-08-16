//
// ColorUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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

import java.awt.Color;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Arrays;
import visad.*;
import loci.visbio.util.ObjectUtil;

/** ColorUtil contains useful color table functions. */
public abstract class ColorUtil {

  // -- Preset color tables --

  /** Resolution of color tables. */
  public static final int COLOR_DETAIL = 256;

  /** Maximum alpha exponent for polynomial transparency curve. */
  public static final int MAX_POWER = 8;

  /** Zeroed out color table componented used in several presets. */
  protected static final float[] ZEROED = makeZeroed();

  /** Linear color table component used in several presets. */
  protected static final float[] LINEAR = makeLinear();

  /** Constructs zero color table component. */
  private static float[] makeZeroed() {
    float[] wedge = new float[COLOR_DETAIL];
    Arrays.fill(wedge, 0f);
    return wedge;
  }

  /** Constructs linear color table component. */
  private static float[] makeLinear() {
    float[] wedge = new float[COLOR_DETAIL];
    float max = COLOR_DETAIL - 1;
    for (int i=0; i<wedge.length; i++) wedge[i] = i / max;
    return wedge;
  }

  /** Grayscale color look-up table. */
  public static final float[][] LUT_GRAY = {LINEAR, LINEAR, LINEAR};

  /** Red color look-up table. */
  public static final float[][] LUT_RED = {LINEAR, ZEROED, ZEROED};

  /** Green color look-up table. */
  public static final float[][] LUT_GREEN = {ZEROED, LINEAR, ZEROED};

  /** Blue color look-up table. */
  public static final float[][] LUT_BLUE = {ZEROED, ZEROED, LINEAR};

  /** Cyan color look-up table. */
  public static final float[][] LUT_CYAN = {ZEROED, LINEAR, LINEAR};

  /** Magenta color look-up table. */
  public static final float[][] LUT_MAGENTA = {LINEAR, ZEROED, LINEAR};

  /** Yellow color look-up table. */
  public static final float[][] LUT_YELLOW = {LINEAR, LINEAR, ZEROED};

  /** HSV color look-up table. */
  public static final float[][] LUT_HSV =
    ColorControl.initTableHSV(new float[3][COLOR_DETAIL]);

  /** RGB (pseudocolor) color look-up table. */
  public static final float[][] LUT_RGB =
    ColorControl.initTableVis5D(new float[3][COLOR_DETAIL]);

  /** Fire color look-up table. */
  public static final float[][] LUT_FIRE = {
    // fire red component
    {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.004f,
     0.016f, 0.027f, 0.039f, 0.051f, 0.063f, 0.075f, 0.086f, 0.098f, 0.11f,
     0.122f, 0.133f, 0.145f, 0.157f, 0.169f, 0.18f, 0.192f, 0.204f, 0.216f,
     0.227f, 0.239f, 0.251f, 0.263f, 0.275f, 0.286f, 0.298f, 0.31f, 0.322f,
     0.333f, 0.345f, 0.357f, 0.369f, 0.384f, 0.396f, 0.408f, 0.42f, 0.431f,
     0.443f, 0.455f, 0.467f, 0.478f, 0.49f, 0.502f, 0.514f, 0.525f, 0.537f,
     0.549f, 0.561f, 0.573f, 0.58f, 0.588f, 0.596f, 0.604f, 0.612f, 0.62f,
     0.627f, 0.635f, 0.639f, 0.643f, 0.651f, 0.655f, 0.659f, 0.667f, 0.671f,
     0.678f, 0.682f, 0.686f, 0.694f, 0.698f, 0.702f, 0.71f, 0.714f, 0.722f,
     0.725f, 0.729f, 0.737f, 0.741f, 0.745f, 0.753f, 0.757f, 0.765f, 0.769f,
     0.776f, 0.78f, 0.788f, 0.792f, 0.80f, 0.804f, 0.812f, 0.816f, 0.82f,
     0.824f, 0.831f, 0.835f, 0.839f, 0.843f, 0.851f, 0.855f, 0.863f, 0.867f,
     0.875f, 0.878f, 0.886f, 0.89f, 0.898f, 0.902f, 0.906f, 0.914f, 0.918f,
     0.922f, 0.929f, 0.933f, 0.941f, 0.945f, 0.953f, 0.957f, 0.965f, 0.969f,
     0.976f, 0.98f, 0.988f, 0.988f, 0.988f, 0.992f, 0.992f, 0.992f, 0.996f,
     0.996f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
     1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
     1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
     1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
     1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
     1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
     1f, 1f, 1f, 1f, 1f, 1f},
    // fire green component
    {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.004f, 0.012f, 0.02f, 0.027f, 0.031f, 0.039f,
     0.047f, 0.055f, 0.063f, 0.075f, 0.082f, 0.094f, 0.106f, 0.114f, 0.125f,
     0.137f, 0.145f, 0.157f, 0.169f, 0.18f, 0.188f, 0.2f, 0.212f, 0.224f,
     0.231f, 0.243f, 0.255f, 0.267f, 0.275f, 0.286f, 0.298f, 0.31f, 0.318f,
     0.329f, 0.341f, 0.353f, 0.361f, 0.373f, 0.384f, 0.396f, 0.404f, 0.412f,
     0.42f, 0.427f, 0.435f, 0.443f, 0.451f, 0.459f, 0.467f, 0.475f, 0.482f,
     0.49f, 0.498f, 0.506f, 0.514f, 0.522f, 0.525f, 0.533f, 0.541f, 0.549f,
     0.553f, 0.561f, 0.569f, 0.576f, 0.58f, 0.588f, 0.596f, 0.604f, 0.608f,
     0.616f, 0.624f, 0.631f, 0.635f, 0.643f, 0.651f, 0.659f, 0.663f, 0.671f,
     0.678f, 0.686f, 0.69f, 0.698f, 0.706f, 0.714f, 0.722f, 0.729f, 0.737f,
     0.745f, 0.749f, 0.757f, 0.765f, 0.773f, 0.78f, 0.788f, 0.796f, 0.804f,
     0.808f, 0.816f, 0.824f, 0.831f, 0.835f, 0.843f, 0.851f, 0.859f, 0.863f,
     0.871f, 0.878f, 0.886f, 0.894f, 0.902f, 0.91f, 0.918f, 0.922f, 0.929f,
     0.937f, 0.945f, 0.949f, 0.957f, 0.965f, 0.973f, 0.973f, 0.976f, 0.98f,
     0.984f, 0.988f, 0.992f, 0.996f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
     1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
     1f, 1f, 1f, 1f},
    // fire blue component
    {0.122f, 0.133f, 0.149f, 0.165f, 0.18f, 0.192f, 0.208f, 0.224f, 0.239f,
     0.255f, 0.271f, 0.29f, 0.306f, 0.322f, 0.341f, 0.357f, 0.376f, 0.392f,
     0.408f, 0.424f, 0.443f, 0.459f, 0.475f, 0.49f, 0.51f, 0.525f, 0.541f,
     0.561f, 0.576f, 0.592f, 0.612f, 0.627f, 0.647f, 0.659f, 0.671f, 0.686f,
     0.698f, 0.71f, 0.725f, 0.737f, 0.753f, 0.765f, 0.78f, 0.792f, 0.808f,
     0.82f, 0.835f, 0.847f, 0.863f, 0.863f, 0.867f, 0.871f, 0.875f, 0.878f,
     0.882f, 0.886f, 0.89f, 0.878f, 0.871f, 0.863f, 0.855f, 0.847f, 0.839f,
     0.831f, 0.824f, 0.808f, 0.792f, 0.78f, 0.765f, 0.749f, 0.737f, 0.722f,
     0.71f, 0.694f, 0.678f, 0.663f, 0.651f, 0.635f, 0.62f, 0.604f, 0.592f,
     0.576f, 0.561f, 0.549f, 0.533f, 0.518f, 0.506f, 0.49f, 0.478f, 0.463f,
     0.447f, 0.435f, 0.42f, 0.404f, 0.392f, 0.376f, 0.365f, 0.349f, 0.333f,
     0.322f, 0.306f, 0.29f, 0.278f, 0.263f, 0.251f, 0.235f, 0.22f, 0.208f,
     0.192f, 0.176f, 0.165f, 0.149f, 0.137f, 0.122f, 0.106f, 0.09f, 0.078f,
     0.063f, 0.047f, 0.031f, 0.02f, 0.016f, 0.012f, 0.012f, 0.008f, 0.004f,
     0.004f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.016f, 0.031f, 0.051f,
     0.067f, 0.082f, 0.102f, 0.118f, 0.137f, 0.165f, 0.196f, 0.227f, 0.259f,
     0.29f, 0.322f, 0.353f, 0.384f, 0.412f, 0.443f, 0.475f, 0.506f, 0.533f,
     0.565f, 0.596f, 0.627f, 0.655f, 0.686f, 0.718f, 0.749f, 0.78f, 0.812f,
     0.843f, 0.875f, 0.89f, 0.906f, 0.922f, 0.937f, 0.953f, 0.969f, 0.984f, 1f,
     1f, 1f, 1f, 1f, 1f, 1f, 1f}
  };

  /** Ice color look-up table. */
  public static final float[][] LUT_ICE = {
    // ice red component
    {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
     0f, 0f, 0f, 0f, 0f, 0.008f, 0.016f, 0.027f, 0.035f, 0.043f, 0.055f,
     0.063f, 0.075f, 0.078f, 0.082f, 0.086f, 0.094f, 0.098f, 0.102f, 0.106f,
     0.114f, 0.122f, 0.133f, 0.141f, 0.153f, 0.165f, 0.173f, 0.184f, 0.196f,
     0.192f, 0.192f, 0.192f, 0.192f, 0.188f, 0.188f, 0.188f, 0.188f, 0.2f,
     0.216f, 0.231f, 0.247f, 0.263f, 0.278f, 0.294f, 0.31f, 0.325f, 0.341f,
     0.357f, 0.373f, 0.388f, 0.404f, 0.42f, 0.439f, 0.447f, 0.459f, 0.471f,
     0.482f, 0.49f, 0.502f, 0.514f, 0.525f, 0.537f, 0.549f, 0.561f, 0.573f,
     0.584f, 0.596f, 0.608f, 0.62f, 0.631f, 0.647f, 0.659f, 0.675f, 0.686f,
     0.702f, 0.714f, 0.729f, 0.733f, 0.741f, 0.749f, 0.757f, 0.765f, 0.773f,
     0.78f, 0.788f, 0.796f, 0.804f, 0.812f, 0.82f, 0.827f, 0.835f, 0.843f,
     0.851f, 0.855f, 0.863f, 0.867f, 0.875f, 0.878f, 0.886f, 0.89f, 0.898f,
     0.902f, 0.91f, 0.914f, 0.922f, 0.929f, 0.933f, 0.941f, 0.949f, 0.953f,
     0.957f, 0.961f, 0.965f, 0.969f, 0.973f, 0.976f, 0.98f, 0.98f, 0.98f,
     0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f,
     0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f,
     0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.984f,
     0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f,
     0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f,
     0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f,
     0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.984f,
     0.984f, 0.984f, 0.984f, 0.984f, 0.984f, 0.984f, 0.984f, 0.984f, 0.98f,
     0.976f, 0.973f, 0.969f, 0.965f, 0.961f, 0.957f, 0.953f, 0.945f, 0.937f,
     0.933f, 0.925f, 0.918f, 0.914f, 0.906f, 0.902f, 0.902f, 0.902f, 0.902f,
     0.902f, 0.902f, 0.902f, 0.902f},
    // ice green component
    {0.612f, 0.616f, 0.62f, 0.624f, 0.627f, 0.631f, 0.635f, 0.639f, 0.647f,
     0.651f, 0.655f, 0.663f, 0.667f, 0.671f, 0.678f, 0.682f, 0.69f, 0.694f,
     0.698f, 0.702f, 0.706f, 0.71f, 0.714f, 0.718f, 0.722f, 0.722f, 0.725f,
     0.729f, 0.733f, 0.733f, 0.737f, 0.741f, 0.745f, 0.745f, 0.749f, 0.753f,
     0.757f, 0.757f, 0.761f, 0.765f, 0.769f, 0.765f, 0.765f, 0.761f, 0.761f,
     0.761f, 0.757f, 0.757f, 0.757f, 0.749f, 0.745f, 0.741f, 0.737f, 0.733f,
     0.729f, 0.725f, 0.722f, 0.714f, 0.706f, 0.702f, 0.694f, 0.686f, 0.682f,
     0.675f, 0.671f, 0.663f, 0.659f, 0.655f, 0.651f, 0.647f, 0.643f, 0.639f,
     0.635f, 0.627f, 0.62f, 0.612f, 0.604f, 0.596f, 0.588f, 0.58f, 0.573f,
     0.561f, 0.549f, 0.541f, 0.529f, 0.518f, 0.51f, 0.498f, 0.49f, 0.478f,
     0.471f, 0.463f, 0.455f, 0.443f, 0.435f, 0.427f, 0.42f, 0.412f, 0.404f,
     0.396f, 0.392f, 0.384f, 0.376f, 0.369f, 0.365f, 0.357f, 0.353f, 0.345f,
     0.341f, 0.333f, 0.329f, 0.322f, 0.318f, 0.318f, 0.322f, 0.325f, 0.329f,
     0.329f, 0.333f, 0.337f, 0.341f, 0.341f, 0.345f, 0.345f, 0.349f, 0.353f,
     0.353f, 0.357f, 0.361f, 0.361f, 0.365f, 0.365f, 0.369f, 0.373f, 0.373f,
     0.376f, 0.38f, 0.376f, 0.376f, 0.376f, 0.376f, 0.373f, 0.373f, 0.373f,
     0.373f, 0.369f, 0.369f, 0.369f, 0.369f, 0.365f, 0.365f, 0.365f, 0.365f,
     0.365f, 0.365f, 0.365f, 0.365f, 0.365f, 0.365f, 0.365f, 0.365f, 0.361f,
     0.361f, 0.357f, 0.357f, 0.357f, 0.353f, 0.353f, 0.353f, 0.349f, 0.345f,
     0.345f, 0.341f, 0.337f, 0.337f, 0.333f, 0.333f, 0.325f, 0.318f, 0.31f,
     0.302f, 0.294f, 0.286f, 0.278f, 0.271f, 0.267f, 0.263f, 0.263f, 0.259f,
     0.255f, 0.255f, 0.251f, 0.251f, 0.243f, 0.239f, 0.235f, 0.231f, 0.224f,
     0.22f, 0.216f, 0.212f, 0.208f, 0.204f, 0.2f, 0.196f, 0.192f, 0.188f,
     0.184f, 0.184f, 0.176f, 0.173f, 0.165f, 0.161f, 0.153f, 0.149f, 0.141f,
     0.137f, 0.129f, 0.122f, 0.114f, 0.106f, 0.098f, 0.09f, 0.082f, 0.075f,
     0.063f, 0.055f, 0.043f, 0.035f, 0.027f, 0.016f, 0.008f, 0f, 0f, 0.004f,
     0.004f, 0.008f, 0.008f, 0.012f, 0.012f, 0.016f, 0.012f, 0.012f, 0.008f,
     0.008f, 0.004f, 0.004f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f},
    // ice blue component
    {0.549f, 0.549f, 0.553f, 0.557f, 0.561f, 0.565f, 0.569f, 0.573f, 0.576f,
     0.58f, 0.584f, 0.592f, 0.596f, 0.6f, 0.608f, 0.612f, 0.62f, 0.624f,
     0.627f, 0.631f, 0.635f, 0.639f, 0.643f, 0.647f, 0.651f, 0.651f, 0.655f,
     0.655f, 0.659f, 0.659f, 0.663f, 0.663f, 0.667f, 0.667f, 0.671f, 0.675f,
     0.678f, 0.678f, 0.682f, 0.686f, 0.69f, 0.706f, 0.722f, 0.737f, 0.753f,
     0.769f, 0.784f, 0.8f, 0.82f, 0.824f, 0.827f, 0.835f, 0.839f, 0.843f,
     0.851f, 0.855f, 0.863f, 0.867f, 0.875f, 0.882f, 0.89f, 0.894f, 0.902f,
     0.91f, 0.918f, 0.91f, 0.906f, 0.902f, 0.898f, 0.894f, 0.89f, 0.886f,
     0.882f, 0.886f, 0.89f, 0.898f, 0.902f, 0.906f, 0.914f, 0.918f, 0.925f,
     0.929f, 0.933f, 0.937f, 0.945f, 0.949f, 0.953f, 0.957f, 0.965f, 0.965f,
     0.969f, 0.969f, 0.973f, 0.973f, 0.976f, 0.976f, 0.98f, 0.98f, 0.98f,
     0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.984f, 0.98f, 0.98f, 0.98f, 0.98f,
     0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f, 0.98f,
     0.98f, 0.98f, 0.976f, 0.973f, 0.973f, 0.969f, 0.965f, 0.965f, 0.961f,
     0.961f, 0.953f, 0.945f, 0.937f, 0.929f, 0.922f, 0.914f, 0.906f, 0.902f,
     0.902f, 0.902f, 0.902f, 0.902f, 0.902f, 0.902f, 0.902f, 0.902f, 0.898f,
     0.894f, 0.89f, 0.886f, 0.882f, 0.878f, 0.875f, 0.871f, 0.859f, 0.851f,
     0.839f, 0.831f, 0.82f, 0.812f, 0.8f, 0.792f, 0.78f, 0.769f, 0.757f,
     0.749f, 0.737f, 0.725f, 0.714f, 0.706f, 0.694f, 0.686f, 0.678f, 0.671f,
     0.663f, 0.655f, 0.647f, 0.639f, 0.627f, 0.616f, 0.608f, 0.596f, 0.584f,
     0.576f, 0.565f, 0.557f, 0.545f, 0.537f, 0.525f, 0.518f, 0.51f, 0.498f,
     0.49f, 0.482f, 0.475f, 0.471f, 0.467f, 0.463f, 0.459f, 0.455f, 0.451f,
     0.447f, 0.443f, 0.439f, 0.435f, 0.431f, 0.427f, 0.424f, 0.42f, 0.416f,
     0.408f, 0.404f, 0.396f, 0.392f, 0.384f, 0.38f, 0.373f, 0.369f, 0.361f,
     0.357f, 0.353f, 0.349f, 0.341f, 0.337f, 0.333f, 0.329f, 0.318f, 0.31f,
     0.298f, 0.29f, 0.278f, 0.271f, 0.259f, 0.251f, 0.231f, 0.212f, 0.192f,
     0.176f, 0.157f, 0.137f, 0.118f, 0.102f, 0.102f, 0.102f, 0.102f, 0.102f,
     0.102f, 0.102f, 0.102f, 0.106f, 0.106f, 0.106f, 0.106f, 0.106f, 0.106f,
     0.106f, 0.106f}
  };


  // -- Color table manipulation --

  /** RGB color space model. */
  public static final int RGB_MODEL = 0;

  /** HSV color space model. */
  public static final int HSV_MODEL = 1;

  /** Composite RGB color space model. */
  public static final int COMPOSITE_MODEL = 2;

  /** Constant alpha value transparency model. */
  public static final int CONSTANT_ALPHA = 0;

  /** Polynomial alpha curve transparency model. */
  public static final int CURVED_ALPHA = 1;

  /** RealType for indicating nothing mapped to a color component. */
  public static final RealType CLEAR = RealType.getRealType("bio_clear");

  /** RealType for indicating uniformly solid color component. */
  public static final RealType SOLID = RealType.getRealType("bio_solid");

  /**
   * Computes color tables from the given color settings.
   *
   * @param maps ScalarMaps for which to generate color tables
   * @param brightness Brightness value from 0 to 255.
   * @param contrast Contrast value from 0 to 255.
   * @param model Color space model: RGB_MODEL, HSV_MODEL or COMPOSITE_MODEL
   * @param red RealType to map to red color component
   * @param green RealType to map to green color component
   * @param blue RealType to map to blue color component
   */
  public static float[][][] computeColorTables(ScalarMap[] maps,
    int brightness, int contrast, int model,
    RealType red, RealType green, RealType blue)
  {
    // compute center and slope from brightness and contrast
    double mid = COLOR_DETAIL / 2.0;
    double slope;
    if (contrast <= mid) slope = contrast / mid;
    else slope = mid / (COLOR_DETAIL - contrast);
    if (slope == Double.POSITIVE_INFINITY) slope = Double.MAX_VALUE;
    double center = (slope + 1) * brightness / COLOR_DETAIL - 0.5 * slope;

    // compute color channel table values from center and slope
    float[] vals = new float[COLOR_DETAIL];
    float[] rvals, gvals, bvals;
    if (model == COMPOSITE_MODEL) {
      rvals = new float[COLOR_DETAIL];
      gvals = new float[COLOR_DETAIL];
      bvals = new float[COLOR_DETAIL];
      float[][] comp = LUT_RGB;
      for (int i=0; i<COLOR_DETAIL; i++) {
        rvals[i] = (float) (slope * (comp[0][i] - 0.5) + center);
        gvals[i] = (float) (slope * (comp[1][i] - 0.5) + center);
        bvals[i] = (float) (slope * (comp[2][i] - 0.5) + center);
        if (rvals[i] > 1) rvals[i] = 1;
        if (rvals[i] < 0) rvals[i] = 0;
        if (gvals[i] > 1) gvals[i] = 1;
        if (gvals[i] < 0) gvals[i] = 0;
        if (bvals[i] > 1) bvals[i] = 1;
        if (bvals[i] < 0) bvals[i] = 0;
      }
    }
    else {
      for (int i=0; i<COLOR_DETAIL; i++) {
        vals[i] = (float) (0.5 * slope * (i / mid - 1.0) + center);
      }
      rvals = gvals = bvals = vals;
    }
    for (int i=0; i<COLOR_DETAIL; i++) {
      if (vals[i] < 0.0f) vals[i] = 0.0f;
      else if (vals[i] > 1.0f) vals[i] = 1.0f;
    }

    // update color tables and map ranges
    boolean rSolid = red == SOLID;
    boolean gSolid = green == SOLID;
    boolean bSolid = blue == SOLID;
    float[][][] colorTables = new float[maps.length][][];
    for (int j=0; j<maps.length; j++) {
      RealType rt = (RealType) maps[j].getScalar();

      // fill in color table elements
      float[][] t;
      if (model == COMPOSITE_MODEL) t = new float[][] {rvals, gvals, bvals};
      else {
        t = new float[][] {
          rt.equals(red) ? rvals : new float[COLOR_DETAIL],
          rt.equals(green) ? gvals : new float[COLOR_DETAIL],
          rt.equals(blue) ? bvals : new float[COLOR_DETAIL]
        };
        if (rSolid) Arrays.fill(t[0], 1.0f);
        if (gSolid) Arrays.fill(t[1], 1.0f);
        if (bSolid) Arrays.fill(t[2], 1.0f);

        // convert color table to HSV color model if necessary
        if (model == HSV_MODEL) {
          float[][] newt = new float[3][COLOR_DETAIL];
          for (int i=0; i<COLOR_DETAIL; i++) {
            Color c = new Color(Color.HSBtoRGB(t[0][i], t[1][i], t[2][i]));
            newt[0][i] = c.getRed() / 255f;
            newt[1][i] = c.getGreen() / 255f;
            newt[2][i] = c.getBlue() / 255f;
          }
          t = newt;
        }
      }
      colorTables[j] = t;
    }
    return colorTables;
  }

  /**
   * Computes alpha table component from the given transparency settings.
   *
   * @param opacity Level of solidity, from 0 to COLOR_DETAIL
   * @param model Transparency model: CONSTANT_ALPHA or CURVED_ALPHA
   */
  public static float[] computeAlphaTable(int opacity, int model) {
    // compute transparency values
    float[] avals = new float[COLOR_DETAIL];
    if (model == CURVED_ALPHA) {
      double value = 1 - (double) opacity / COLOR_DETAIL;
      boolean invert = value < 0.5;
      if (invert) value = 1 - value;
      // 0.50 -> 1
      // 0.75 -> ~4
      // 1.00 -> ~1000
      double pow = Math.pow(4, Math.pow(4 * value - 2, 2.32));
      if (invert) pow = 1 / pow;
      for (int i=0; i<COLOR_DETAIL; i++) {
        double inc = (double) i / (COLOR_DETAIL - 1);
        avals[i] = (float) Math.pow(inc, pow);
      }
    }
    else Arrays.fill(avals, (float) opacity / COLOR_DETAIL);
    return avals;
  }

  /** Assigns the given color range to the specified display and mapping. */
  public static void setColorRange(DisplayImpl display,
    ScalarMap map, double lo, double hi, boolean fixed)
  {
    if (hi <= lo) hi = lo + 1;

    if (fixed) {
      double[] range = map.getRange();
      if (map.isAutoScale() || range[0] != lo || range[1] != hi) {
        try { map.setRange(lo, hi); }
        catch (VisADException exc) { exc.printStackTrace(); }
        catch (RemoteException exc) { exc.printStackTrace(); }
      }
    }
    else if (!map.isAutoScale()) reAutoScale(display, map);
  }

  /** Assigns the given color ranges to the specified display and mappings. */
  public static void setColorRanges(DisplayImpl display,
    ScalarMap[] maps, double[] lo, double[] hi, boolean[] fixed)
  {
    if (lo.length != maps.length || hi.length != maps.length ||
      fixed.length != maps.length)
    {
      return;
    }
    for (int i=0; i<maps.length; i++) {
      setColorRange(display, maps[i], lo[i], hi[i], fixed[i]);
    }
  }

  /** Recomputes autoscaled color bounds for the given color map. */
  public static void reAutoScale(DisplayImpl display, ScalarMap map) {
    map.resetAutoScale();
    display.reAutoScale();

    // HACK - stupid trick to force immediate rescale
    BaseColorControl cc = (BaseColorControl) map.getControl();
    try { cc.setTable(cc.getTable()); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }

    // HACK - check for bogus (-0.5 - 0.5) range and reassign to (0.0 - 1.0)
    double[] range = map.getRange();
    if (range[0] == -0.5 && range[1] == 0.5) {
      try { map.setRange(0, 1); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  /** Assigns the given color tables to the specified mappings. */
  public static void setColorTables(ScalarMap[] maps, float[][][] tables) {
    if (tables.length != maps.length) return;

    for (int i=0; i<maps.length; i++) {
      boolean doAlpha = maps[i].getDisplayScalar().equals(Display.RGBA);

      BaseColorControl cc = (BaseColorControl) maps[i].getControl();
      float[][] oldTable = cc.getTable();

      float[][] t = ColorUtil.adjustColorTable(tables[i],
        oldTable.length > 3 ? oldTable[3] : null, doAlpha);
      if (ObjectUtil.arraysEqual(oldTable, t)) continue;

      try { cc.setTable(t); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  /** Assigns the given alpha channel to the specified mappings. */
  public static void setAlphaTable(ScalarMap[] maps, float[] alpha) {
    for (int i=0; i<maps.length; i++) {
      if (!maps[i].getDisplayScalar().equals(Display.RGBA)) continue;

      BaseColorControl cc = (BaseColorControl) maps[i].getControl();
      float[][] oldTable = cc.getTable();
      if (oldTable.length > 3 && ObjectUtil.arraysEqual(oldTable[3], alpha)) {
        continue;
      }
      float[][] t = new float[][] {
        oldTable[0], oldTable[1], oldTable[2], alpha
      };

      try { cc.setTable(t); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  /**
   * Ensures the color table is of the proper type (RGB or RGBA).
   *
   * If the alpha is not required but the table has an alpha channel,
   * a new table is returned with the alpha channel stripped out.
   *
   * If alpha is required but the table does not have an alpha channel,
   * a new table is returned with an alpha channel matching the provided
   * one (or all 1s if the provided alpha channel is null or invalid).
   */
  public static float[][] adjustColorTable(float[][] table,
    float[] alpha, boolean doAlpha)
  {
    if (table == null || table.length == 0 || table[0] == null) return null;
    if (table.length == 3) {
      if (!doAlpha) return table;
      int len = table[0].length;
      if (alpha == null || alpha.length != len) {
        alpha = new float[len];
        Arrays.fill(alpha, 1.0f);
      }
      return new float[][] {table[0], table[1], table[2], alpha};
    }
    else { // table.length == 4
      if (doAlpha) return table;
      return new float[][] {table[0], table[1], table[2]};
    }
  }

  /**
   * Sets the given display's color mode to use either averaging or sums,
   * depending on the specified color model.
   */
  public static void setColorMode(DisplayImpl display, int model) {
    try {
      display.getGraphicsModeControl().setColorMode(model == COMPOSITE_MODEL ?
        GraphicsModeControl.AVERAGE_COLOR_MODE :
        GraphicsModeControl.SUM_COLOR_MODE);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }


  // -- Color table I/O --

  /** Color table file length. */
  protected static final int LUT_LEN = 768;

  /** Loads the color table from the given file. */
  public static float[][] loadColorTable(File file) {
    if (file == null || !file.exists()) return null;
    long fileLen = file.length();
    if (fileLen > Integer.MAX_VALUE) return null;
    int offset = (int) fileLen - LUT_LEN;
    if (offset < 0) return null;

    // read in LUT data
    int[] b = new int[LUT_LEN];
    try {
      DataInputStream fin = new DataInputStream(new FileInputStream(file));
      int skipped = 0;
      while (skipped < offset) skipped += fin.skipBytes(offset - skipped);
      for (int i=0; i<LUT_LEN; i++) b[i] = fin.readUnsignedByte();
      fin.close();
    }
    catch (IOException exc) { return null; }

    // convert data to VisAD format
    int len = LUT_LEN / 3;
    int count = 0;
    float[][] table = new float[3][len];
    for (int i=0; i<3; i++) {
      for (int j=0; j<len; j++) table[i][j] = b[count++] / 255f;
    }
    return table;
  }

  /**
   * Saves the given color table into the specified file.
   * @return true if file was saved successfully
   */
  public static boolean saveColorTable(float[][] table, File file) {
    if (file == null || table == null || table.length != 3) return false;

    // convert LUT data to binary format
    int len = LUT_LEN / 3;
    int[] b = new int[LUT_LEN];
    int count = 0;
    for (int i=0; i<3; i++) {
      if (table[i].length != len) return false;
      for (int j=0; j<len; j++) b[count++] = (int) (255 * table[i][j]);
    }

    // write out LUT data
    try {
      DataOutputStream fout = new DataOutputStream(new FileOutputStream(file));
      for (int i=0; i<LUT_LEN; i++) fout.writeByte(b[i]);
      fout.close();
    }
    catch (IOException exc) { return false; }
    return true;
  }

  /** Gets a hexidecimal representation for this color. */
  public static String colorToHex(Color color) {
    String red = Integer.toHexString(color.getRed());
    String green = Integer.toHexString(color.getGreen());
    String blue = Integer.toHexString(color.getBlue());

    // ensure all hex strings are the same length
    int len = red.length();
    if (green.length() > len) len = green.length();
    if (blue.length() > len) len = blue.length();
    while (red.length() < len) red = "0" + red;
    while (green.length() < len) green = "0" + green;
    while (blue.length() < len) blue = "0" + blue;

    return red + green + blue;
  }

  /** Gets the corresponding Color object for this hexidecimal string. */
  public static Color hexToColor(String hex) {
    if (hex == null) return null;
    int len = hex.length();
    if (len == 0 || len % 3 != 0) return null;
    int len3 = len / 3;
    float max = (float) Math.pow(16, len3) - 1;
    Color color = null;
    try {
      // divide hex string into three equal parts, normalizing to (0.0 - 1.0)
      float r = Integer.parseInt(hex.substring(0, len3), 16) / max;
      float g = Integer.parseInt(hex.substring(len3, 2 * len3), 16) / max;
      float b = Integer.parseInt(hex.substring(2 * len3), 16) / max;
      color = new Color(r, g, b);
    }
    catch (NumberFormatException exc) { }
    return color;
  }

}
