/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import static java.util.Collections.unmodifiableSet;


/**
 * AxisGuesser guesses which blocks in a file pattern correspond to which
 * dimensional axes (Z, T, C or S), potentially recommending an adjustment in
 * dimension order within the files, depending on the confidence of each guess.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class AxisGuesser {

  // -- Constants --

  /** Axis type for unclassified axes. */
  public static final int UNKNOWN_AXIS = 0;

  /** Axis type for focal planes. */
  public static final int Z_AXIS = 1;

  /** Axis type for time points. */
  public static final int T_AXIS = 2;

  /** Axis type for channels. */
  public static final int C_AXIS = 3;

  /** Axis type for series. */
  public static final int S_AXIS = 4;

  /** Prefixes indicating space dimension. */
  public static final Set<String> Z_PREFIXES = unmodifiableSet(
      new HashSet<String>(Arrays.asList(
          "fp", "sec", "z", "zs", "focal", "focalplane")));

  /** Prefixes indicating time dimension. */
  public static final Set<String> T_PREFIXES = unmodifiableSet(
      new HashSet<String>(Arrays.asList("t", "tl", "tp", "time")));

  /** Prefixes indicating channel dimension. */
  public static final Set<String> C_PREFIXES = unmodifiableSet(
      new HashSet<String>(Arrays.asList("c", "ch", "w", "wavelength")));

  /** Prefixes indicating series dimension. */
  public static final Set<String> S_PREFIXES = unmodifiableSet(
      new HashSet<String>(Arrays.asList("s", "series", "sp")));

  protected static final String ONE = "1";
  protected static final String TWO = "2";
  protected static final String THREE = "3";

  // -- Fields --

  /** File pattern identifying dimensional axis blocks. */
  protected FilePattern fp;

  /** Original ordering of internal dimensional axes. */
  protected String dimOrder;

  /** Adjusted ordering of internal dimensional axes. */
  protected String newOrder;

  /** Guessed axis types. */
  protected int[] axisTypes;

  /** Whether the guesser is confident that all axis types are correct. */
  protected boolean certain;

  // -- Constructor --

  /**
   * Guesses dimensional axis assignments corresponding to the given
   * file pattern, using the specified dimensional information from
   * within each file as a guide.
   * <p>
   * The algorithm first assigns pattern blocks based on known
   * prefixes. For instance, a block preceded by "time" is assigned to
   * the time points (T) axis. Blocks that don't have a known prefix
   * are checked for special cases (Bio-Rad .pic, RGB).  Remaining
   * blocks are assigned according to the given dimensional sizes:
   * dimensions with size greater than 1 are assumed to be already
   * contained within each file, while those with size equal to 1 can be
   * scattered across multiple files and thus are assigned to pattern
   * blocks.
   * <p>
   * If <code>isCertain</code> is <code>false</code>, the algorithm
   * checks for cases where the reader might have mixed up the Z and T
   * dimensions. For instance, if the pattern string is
   * <code>z&lt;*&gt;_&lt;*&gt;</code> and <code>size&lcub;Z,T,C&rcub;
   * = 2,1,1</code>, then the reader is assumed to be wrong about the
   * relative positions of Z and T in the given <code>dimOrder</code>.
   * The new suggested order (see {@link #getAdjustedOrder}) will have
   * Z and T swapped and the second block will be assigned to C (the
   * actual dimensional sizes are assumed to be <code>1,2,1</code>).
   * <p>
   * If after trying all of the above there are still unassigned
   * blocks, they will be assigned to the last dimension in the
   * (possibly adjusted) order.
   *
   * @param fp The file pattern of the files
   * @param dimOrder The dimension order (e.g., XYZTC) within each file
   * @param sizeZ The number of Z positions within each file
   * @param sizeT The number of T positions within each file
   * @param sizeC The number of C positions within each file
   * @param isCertain Whether the dimension order given is known to be good,
   *   or merely a guess
   *
   * @see FilePattern
   */
  public AxisGuesser(FilePattern fp, String dimOrder,
    int sizeZ, int sizeT, int sizeC, boolean isCertain)
  {
    this.fp = fp;
    this.dimOrder = dimOrder;

    newOrder = dimOrder;
    String[] prefixes = fp.getPrefixes();
    String suffix = fp.getSuffix();
    String[][] elements = fp.getElements();
    axisTypes = new int[elements.length];
    boolean foundZ = false, foundT = false, foundC = false;

    // -- 1) fill in "known" axes based on known patterns and conventions --

    for (int i=0; i<axisTypes.length; i++) {
      String p = prefixes[i].toLowerCase();

      // strip trailing digits and divider characters
      char[] ch = p.toCharArray();
      int l = ch.length - 1;
      while (l >= 0 && (ch[l] >= '0' && ch[l] <= '9' ||
        ch[l] == ' ' || ch[l] == '-' || ch[l] == '_' || ch[l] == '.'))
      {
        l--;
      }

      // useful prefix segment consists of trailing alphanumeric characters
      int f = l;
      while (f >= 0 && ch[f] >= 'a' && ch[f] <= 'z') f--;
      p = p.substring(f + 1, l + 1);

      // check against known prefixes
      if (Z_PREFIXES.contains(p)) {
        axisTypes[i] = Z_AXIS;
        foundZ = true;
        continue;
      }
      if (T_PREFIXES.contains(p)) {
        axisTypes[i] = T_AXIS;
        foundT = true;
        continue;
      }
      if (C_PREFIXES.contains(p)) {
        axisTypes[i] = C_AXIS;
        foundC = true;
        continue;
      }
      if (S_PREFIXES.contains(p)) {
        axisTypes[i] = S_AXIS;
        continue;
      }

      // check special case: <2-3>, <1-3> (Bio-Rad PIC)
      if (suffix.equalsIgnoreCase(".pic") && i == axisTypes.length - 1 &&
        ((elements[i].length == 2 &&
        (elements[i][0].equals(ONE) || elements[i][0].equals(TWO)) &&
        (elements[i][1].equals(TWO) || elements[i][1].equals(THREE))) ||
        (elements[i].length == 3 &&
        elements[i][0].equals(ONE) && elements[i][1].equals(TWO) &&
        elements[i][2].equals(THREE))))
      {
        axisTypes[i] = C_AXIS;
        continue;
      }
      else if (elements[i].length == 2 || elements[i].length == 3) {
        char first = elements[i][0].toLowerCase().charAt(0);
        char second = elements[i][1].toLowerCase().charAt(0);
        char third = elements[i].length == 2 ? 'b' :
          elements[i][2].toLowerCase().charAt(0);

        boolean hasRed = first == 'r' || second == 'r' || third == 'r';
        boolean hasGreen = first == 'g' || second == 'g' || third == 'g';
        boolean hasBlue = first == 'b' || second == 'b' || third == 'b';

        int rgbChannels = 0;
        if (hasRed) {
          rgbChannels++;
        }
        if (hasGreen) {
          rgbChannels++;
        }
        if (hasBlue) {
          rgbChannels++;
        }

        if (rgbChannels >= 2) {
          axisTypes[i] = C_AXIS;
          continue;
        }
      }
    }

    // -- 2) check for special cases where dimension order should be swapped --

    if (!isCertain) { // only switch if dimension order is uncertain
      if (foundZ && !foundT && sizeZ > 1 && sizeT == 1 ||
        foundT && !foundZ && sizeT > 1 && sizeZ == 1)
      {
        // swap Z and T dimensions
        int indexZ = newOrder.indexOf('Z');
        int indexT = newOrder.indexOf('T');
        char[] ch = newOrder.toCharArray();
        ch[indexZ] = 'T';
        ch[indexT] = 'Z';
        newOrder = new String(ch);
        int sz = sizeT;
        sizeT = sizeZ;
        sizeZ = sz;
      }
    }

    // -- 3) fill in remaining axis types --

    boolean canBeZ = !foundZ && sizeZ == 1;
    boolean canBeT = !foundT && sizeT == 1;
    boolean canBeC = !foundC && sizeC == 1;

    certain = isCertain;

    for (int i=0; i<axisTypes.length; i++) {
      if (axisTypes[i] != UNKNOWN_AXIS) continue;
      certain = false;

      if (canBeZ) {
        axisTypes[i] = Z_AXIS;
        canBeZ = false;
      }
      else if (canBeT) {
        axisTypes[i] = T_AXIS;
        canBeT = false;
      }
      else if (canBeC) {
        axisTypes[i] = C_AXIS;
        canBeC = false;
      }
      else {
        char lastAxis = newOrder.charAt(newOrder.length() - 1);
        if (lastAxis == 'C') {
          axisTypes[i] = C_AXIS;
        }
        else if (lastAxis == 'Z') {
          axisTypes[i] = Z_AXIS;
        }
        else axisTypes[i] = T_AXIS;
      }
    }
  }

  // -- AxisGuesser API methods --

  /** Gets the file pattern. */
  public FilePattern getFilePattern() { return fp; }

  /** Gets the original dimension order. */
  public String getOriginalOrder() { return dimOrder; }

  /** Gets the adjusted dimension order. */
  public String getAdjustedOrder() { return newOrder; }

  /** Gets whether the guesser is confident that all axes are correct. */
  public boolean isCertain() { return certain; }

  /**
   * Gets the guessed axis type for each dimensional block.
   * @return An array containing values from the enumeration:
   *   <ul>
   *     <li>Z_AXIS: focal planes</li>
   *     <li>T_AXIS: time points</li>
   *     <li>C_AXIS: channels</li>
   *     <li>S_AXIS: series</li>
   *   </ul>
   */
  public int[] getAxisTypes() { return axisTypes; }

  /**
   * Sets the axis type for each dimensional block.
   * @param axes An array containing values from the enumeration:
   *   <ul>
   *     <li>Z_AXIS: focal planes</li>
   *     <li>T_AXIS: time points</li>
   *     <li>C_AXIS: channels</li>
   *     <li>S_AXIS: series</li>
   *   </ul>
   */
  public void setAxisTypes(int[] axes) { axisTypes = axes; }

  /** Gets the number of Z axes in the pattern. */
  public int getAxisCountZ() { return getAxisCount(Z_AXIS); }

  /** Gets the number of T axes in the pattern. */
  public int getAxisCountT() { return getAxisCount(T_AXIS); }

  /** Gets the number of C axes in the pattern. */
  public int getAxisCountC() { return getAxisCount(C_AXIS); }

  /** Gets the number of S axes in the pattern. */
  public int getAxisCountS() { return getAxisCount(S_AXIS); }

  /** Gets the number of axes in the pattern of the given type.
   *  @param axisType One of:
   *   <ul>
   *     <li>Z_AXIS: focal planes</li>
   *     <li>T_AXIS: time points</li>
   *     <li>C_AXIS: channels</li>
   *     <li>S_AXIS: series</li>
   *   </ul>
   */
  public int getAxisCount(int axisType) {
    int num = 0;
    for (int i=0; i<axisTypes.length; i++) {
      if (axisTypes[i] == axisType) num++;
    }
    return num;
  }

  // -- Static API methods --

  /** Convert the given label to an axis type. If the label ends with
   * one of the known prefixes for the Z, C, T or S axis (as defined
   * in <code>Z_PREFIXES, C_PREFIXES, T_PREFIXES, S_PREFIXES</code>),
   * return the corresponding axis type; otherwise, return
   * <code>UNKNOWN_AXIS</code>. Note that the match is
   * case-insensitive.
   */
  public static int getAxisType(String label) {
    String lowerLabel = label.toLowerCase();
    for (String p : Z_PREFIXES) {
      if (p.equals(lowerLabel) || lowerLabel.endsWith(p)) return Z_AXIS;
    }
    for (String p : C_PREFIXES) {
      if (p.equals(lowerLabel) || lowerLabel.endsWith(p)) return C_AXIS;
    }
    for (String p : T_PREFIXES) {
      if (p.equals(lowerLabel) || lowerLabel.endsWith(p)) return T_AXIS;
    }
    for (String p : S_PREFIXES) {
      if (p.equals(lowerLabel) || lowerLabel.endsWith(p)) return S_AXIS;
    }
    return UNKNOWN_AXIS;
  }

}
