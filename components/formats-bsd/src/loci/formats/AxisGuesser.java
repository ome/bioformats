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

import java.io.IOException;
import java.math.BigInteger;

import loci.common.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AxisGuesser guesses which blocks in a file pattern correspond to which
 * dimensional axes (Z, T or C), potentially recommending an adjustment in
 * dimension order within the files, depending on the confidence of each guess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/AxisGuesser.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/AxisGuesser.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class AxisGuesser {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(AxisGuesser.class);

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

  /** Prefix endings indicating space dimension. */
  protected static final String[] Z = {
    "fp", "sec", "z", "zs", "focal", "focalplane"
  };

  /** Prefix endings indicating time dimension. */
  protected static final String[] T = {"t", "tl", "tp", "time"};

  /** Prefix endings indicating channel dimension. */
  protected static final String[] C = {"c", "ch", "w", "wavelength"};

  /** Prefix endings indicating series dimension. */
  protected static final String[] S = {"s", "series", "sp"};

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

      // check against known Z prefixes
      for (int j=0; j<Z.length; j++) {
        if (p.equals(Z[j])) {
          axisTypes[i] = Z_AXIS;
          foundZ = true;
          break;
        }
      }
      if (axisTypes[i] != UNKNOWN_AXIS) continue;

      // check against known T prefixes
      for (int j=0; j<T.length; j++) {
        if (p.equals(T[j])) {
          axisTypes[i] = T_AXIS;
          foundT = true;
          break;
        }
      }
      if (axisTypes[i] != UNKNOWN_AXIS) continue;

      // check against known C prefixes
      for (int j=0; j<C.length; j++) {
        if (p.equals(C[j])) {
          axisTypes[i] = C_AXIS;
          foundC = true;
          break;
        }
      }
      if (axisTypes[i] != UNKNOWN_AXIS) continue;

      // check against known series prefixes
      for (int j=0; j<S.length; j++) {
        if (p.equals(S[j])) {
          axisTypes[i] = S_AXIS;
          break;
        }
      }
      if (axisTypes[i] != UNKNOWN_AXIS) continue;

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

        if ((first == 'r' || second == 'r' || third == 'r') &&
          (first == 'g' || second == 'g' || third == 'g') &&
          (first == 'b' || second == 'b' || third == 'b'))
        {
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

  /** Returns a best guess of the given label's axis type. */
  public static int getAxisType(String label) {
    String lowerLabel = label.toLowerCase();
    for (String p : Z) {
      if (p.equals(lowerLabel) || lowerLabel.endsWith(p)) return Z_AXIS;
    }
    for (String p : C) {
      if (p.equals(lowerLabel) || lowerLabel.endsWith(p)) return C_AXIS;
    }
    for (String p : T) {
      if (p.equals(lowerLabel) || lowerLabel.endsWith(p)) return T_AXIS;
    }
    for (String p : S) {
      if (p.equals(lowerLabel) || lowerLabel.endsWith(p)) return S_AXIS;
    }
    return UNKNOWN_AXIS;
  }

  // -- Main method --

  /** Method for testing pattern guessing logic. */
  public static void main(String[] args) throws FormatException, IOException {
    Location file = args.length < 1 ?
      new Location(System.getProperty("user.dir")).listFiles()[0] :
      new Location(args[0]);
    LOGGER.info("File = {}", file.getAbsoluteFile());
    String pat = FilePattern.findPattern(file);
    if (pat == null) LOGGER.info("No pattern found.");
    else {
      LOGGER.info("Pattern = {}", pat);
      FilePattern fp = new FilePattern(pat);
      if (fp.isValid()) {
        LOGGER.info("Pattern is valid.");
        String id = fp.getFiles()[0];
        if (!new Location(id).exists()) {
          LOGGER.info("File '{}' does not exist.", id);
        }
        else {
          // read dimensional information from first file
          LOGGER.info("Reading first file ");
          ImageReader reader = new ImageReader();
          reader.setId(id);
          String dimOrder = reader.getDimensionOrder();
          int sizeZ = reader.getSizeZ();
          int sizeT = reader.getSizeT();
          int sizeC = reader.getSizeC();
          boolean certain = reader.isOrderCertain();
          reader.close();
          LOGGER.info("[done]");
          LOGGER.info("\tdimOrder = {} ({})",
            dimOrder, certain ? "certain" : "uncertain");
          LOGGER.info("\tsizeZ = {}", sizeZ);
          LOGGER.info("\tsizeT = {}", sizeT);
          LOGGER.info("\tsizeC = {}", sizeC);

          // guess axes
          AxisGuesser ag = new AxisGuesser(fp,
            dimOrder, sizeZ, sizeT, sizeC, certain);

          // output results
          String[] blocks = fp.getBlocks();
          String[] prefixes = fp.getPrefixes();
          int[] axes = ag.getAxisTypes();
          String newOrder = ag.getAdjustedOrder();
          boolean isCertain = ag.isCertain();
          LOGGER.info("Axis types:");
          for (int i=0; i<blocks.length; i++) {
            String axis;
            switch (axes[i]) {
              case Z_AXIS:
                axis = "Z";
                break;
              case T_AXIS:
                axis = "T";
                break;
              case C_AXIS:
                axis = "C";
                break;
              default:
                axis = "?";
            }
            LOGGER.info("\t{}\t{} (prefix = {})",
              new Object[] {blocks[i], axis, prefixes[i]});
          }
          if (!dimOrder.equals(newOrder)) {
            LOGGER.info("Adjusted dimension order = {} ({})",
              newOrder, isCertain ? "certain" : "uncertain");
          }
        }
      }
      else LOGGER.info("Pattern is invalid: {}", fp.getErrorMessage());
    }
  }

}

// -- Notes --

// INPUTS: file pattern, dimOrder, sizeZ, sizeT, sizeC, isCertain
//
// 1) Fill in all "known" dimensional axes based on known patterns and
//    conventions
//      * known internal axes (ZCT) have isCertain == true
//      * known dimensional axes have a known pattern or convention
//    After that, we are left with only unknown slots, which we must guess.
//
// 2) First, we decide whether we really "believe" the reader. There is a
//    special case where we may decide that it got Z and T mixed up:
//      * if a Z block was found, but not a T block:
//          if !isOrderCertain, and sizeZ > 1, and sizeT == 1, swap 'em
//      * else if a T block was found, but not a Z block:
//          if !isOrderCertain and sizeT > 1, and sizeZ == 1, swap 'em
//    At this point, we can (have to) trust the internal ordering, and use it
//    to decide how to fill in the remaining dimensional blocks.
//
// 3) Set canBeZ to true iff no Z block is assigned and sizeZ == 1.
//    Set canBeT to true iff no T block is assigned and sizeT == 1.
//    Go through the blocks in order from left to right:
//      * If canBeZ, assign Z and set canBeZ to false.
//      * If canBeT, assign T and set canBeT to false.
//      * Otherwise, assign C.
//
// OUTPUTS: list of axis assignments, new dimOrder
