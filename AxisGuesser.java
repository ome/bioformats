//
// AxisGuesser.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

/**
 * AxisGuesser guesses which blocks in a file pattern correspond to which
 * dimensional axes (Z, T or C), potentially recommending an adjustment in
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

  /** Prefix endings indicating space dimension. */
  protected static final String[] Z = {
    "fp", "sec", "z", "zs", "focal", "focalplane"
  };

  /** Prefix endings indicating time dimension. */
  protected static final String[] T = {"t", "tl", "tp"};

  /** Prefix endings indicating channel dimension. */
  protected static final String[] C = {"c", "ch", "w"};

  protected static final BigInteger TWO = new BigInteger("2");
  protected static final BigInteger THREE = new BigInteger("3");

  // -- Fields --

  /** File pattern identifying dimensional axis blocks. */
  protected FilePattern fp;

  /** Original ordering of internal dimensional axes. */
  protected String dimOrder;

  /** Adjusted ordering of internal dimensional axes. */
  protected String newOrder;

  /** Guessed axis types. */
  protected int[] axes;

  // -- Constructor --

  /**
   * Guesses dimensional axis assignments corresponding to the given
   * file pattern, using the specified dimensional information from
   * within each file as a guide.
   *
   * @param fp The file pattern of the files
   * @param dimOrder The dimension order (e.g., XYZTC) within each file
   * @param sizeZ The number of Z positions within each file
   * @param sizeT The number of Z positions within each file
   * @param sizeC The number of Z positions within each file
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
    BigInteger[] first = fp.getFirst();
    BigInteger[] last = fp.getLast();
    BigInteger[] step = fp.getStep();
    int[] count = fp.getCount();
    axes = new int[count.length];
    boolean foundZ = false, foundT = false, foundC = false;

    // -- 1) fill in "known" axes based on known patterns and conventions --

    for (int i=0; i<axes.length; i++) {
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
          axes[i] = Z_AXIS;
          foundZ = true;
          break;
        }
      }
      if (axes[i] != UNKNOWN_AXIS) continue;

      // check against known T prefixes
      for (int j=0; j<T.length; j++) {
        if (p.equals(T[j])) {
          axes[i] = T_AXIS;
          foundT = true;
          break;
        }
      }
      if (axes[i] != UNKNOWN_AXIS) continue;

      // check against known C prefixes
      for (int j=0; j<C.length; j++) {
        if (p.equals(C[j])) {
          axes[i] = C_AXIS;
          foundC = true;
          break;
        }
      }
      if (axes[i] != UNKNOWN_AXIS) continue;

      // check special case: <2-3> (Bio-Rad PIC)
      if (first[i].equals(TWO) && last[i].equals(THREE) &&
        step[i].equals(BigInteger.ONE))
      {
        axes[i] = C_AXIS;
        foundC = true;
        break;
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

    for (int i=0; i<axes.length; i++) {
      if (axes[i] != UNKNOWN_AXIS) continue;

      if (canBeZ) {
        axes[i] = Z_AXIS;
        canBeZ = false;
      }
      else if (canBeT) {
        axes[i] = T_AXIS;
        canBeT = false;
      }
      else axes[i] = C_AXIS;
    }
  }

  // -- AxisGuesser API methods --

  /** Gets the file pattern. */
  public FilePattern getFilePattern() { return fp; }

  /** Gets the original dimension order. */
  public String getOriginalOrder() { return dimOrder; }

  /** Gets the adjusted dimension order. */
  public String getAdjustedOrder() { return newOrder; }

  /**
   * Gets the guessed axis type for each dimensional block.
   * @return An array containing values from the enumeration:
   *   <ul>
   *     <li>Z: focal planes</li>
   *     <li>T: time points</li>
   *     <li>C: channels</li>
   *   </ul>
   */
  public int[] getAxisTypes() { return axes; }

  // -- Main method --

  /** Method for testing pattern guessing logic. */
  public static void main(String[] args) throws FormatException, IOException {
    File file = args.length < 1 ?
      new File(System.getProperty("user.dir")).listFiles()[0] :
      new File(args[0]);
    System.out.println("File = " + file.getAbsoluteFile());
    String pat = FilePattern.findPattern(file);
    if (pat == null) System.out.println("No pattern found.");
    else {
      System.out.println("Pattern = " + pat);
      FilePattern fp = new FilePattern(pat);
      if (fp.isValid()) {
        System.out.println("Pattern is valid.");
        String id = fp.getFiles()[0];
        if (!new File(id).exists()) {
          System.out.println("File '" + id + "' does not exist.");
        }
        else {
          // read dimensional information from first file
          System.out.print("Reading first file ");
          ImageReader reader = new ImageReader();
          String dimOrder = reader.getDimensionOrder(id);
          int sizeZ = reader.getSizeZ(id);
          int sizeT = reader.getSizeT(id);
          int sizeC = reader.getSizeC(id);
          boolean certain = reader.isOrderCertain(id);
          reader.close();
          System.out.println("[done]");
          System.out.println("\tdimOrder = " + dimOrder +
            (certain ? " (certain)" : " (uncertain)"));
          System.out.println("\tsizeZ = " + sizeZ);
          System.out.println("\tsizeT = " + sizeT);
          System.out.println("\tsizeC = " + sizeC);

          // guess axes
          AxisGuesser ag = new AxisGuesser(fp,
            dimOrder, sizeZ, sizeT, sizeC, certain);

          // output results
          String[] blocks = fp.getBlocks();
          String[] prefixes = fp.getPrefixes();
          int[] axes = ag.getAxisTypes();
          String newOrder = ag.getAdjustedOrder();
          System.out.println("Axis types:");
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
            System.out.println("\t" + blocks[i] + "\t" +
              axis + " (prefix = " + prefixes[i] + ")");
          }
          if (!dimOrder.equals(newOrder)) {
            System.out.println("Adjusted dimension order = " + newOrder);
          }
        }
      }
      else System.out.println("Pattern is invalid: " + fp.getErrorMessage());
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
