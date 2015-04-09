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

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import loci.common.DataTools;
import loci.common.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FilePattern is a collection of methods for handling file patterns, a way of
 * succinctly representing a collection of files meant to be part of the same
 * data series.
 *
 * Examples:
 * <ul>
 *   <li>C:\data\BillM\sdub&lt;1-12&gt;.pic</li>
 *   <li>C:\data\Kevin\80&lt;01-59&gt;0&lt;2-3&gt;.pic</li>
 *   <li>/data/Josiah/cell-Z&lt;0-39&gt;.C&lt;0-1&gt;.tiff</li>
 * </ul>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FilePattern {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(FilePattern.class);

  // -- Fields --

  /** The file pattern string. */
  private String pattern;

  /** The validity of the file pattern. */
  private boolean valid;

  /** Error message generated during file pattern construction. */
  private String msg;

  /** Indices into the pattern indicating the start of a numerical block. */
  private int[] startIndex;

  /** Indices into the pattern indicating the end of a numerical block. */
  private int[] endIndex;

  /** List of pattern blocks for this file pattern. */
  private FilePatternBlock[] blocks;

  /** File listing for this file pattern. */
  private String[] files;

  /** Whether or not this FilePattern represents a regular expression. */
  private boolean isRegex = false;

  // -- Constructors --

  /** Creates a pattern object using the given file as a template. */
  public FilePattern(Location file) { this(FilePattern.findPattern(file)); }

  /**
   * Creates a pattern object using the given
   * filename and directory path as a template.
   */
  public FilePattern(String name, String dir) {
    this(FilePattern.findPattern(name, dir));
  }

  /** Creates a pattern object for files with the given pattern string. */
  public FilePattern(String pattern) {
    this.pattern = pattern;
    valid = false;
    if (pattern == null) {
      msg = "Null pattern string.";
      return;
    }

    // locate numerical blocks
    int len = pattern.length();
    List<Integer> lt = new ArrayList<Integer>(len);
    List<Integer> gt = new ArrayList<Integer>(len);
    int left = -1;
    while (true) {
      left = pattern.indexOf(FilePatternBlock.BLOCK_START, left + 1);
      if (left < 0) break;
      lt.add(new Integer(left));
    }
    int right = -1;
    while (true) {
      right = pattern.indexOf(FilePatternBlock.BLOCK_END, right + 1);
      if (right < 0) break;
      gt.add(new Integer(right));
    }

    // assemble numerical block indices
    int num = lt.size();
    if (num != gt.size()) {
      msg = "Mismatched numerical block markers.";
      return;
    }
    startIndex = new int[num];
    endIndex = new int[num];
    for (int i=0; i<num; i++) {
      int val = lt.get(i);
      if (i > 0 && val < endIndex[i - 1]) {
        msg = "Bad numerical block marker order.";
        return;
      }
      startIndex[i] = val;
      val = gt.get(i);
      if (val <= startIndex[i]) {
        msg = "Bad numerical block marker order.";
        return;
      }
      endIndex[i] = val + 1;
    }

    // parse numerical blocks
    blocks = new FilePatternBlock[num];
    for (int i=0; i<num; i++) {
      String block = pattern.substring(startIndex[i], endIndex[i]);
      blocks[i] = new FilePatternBlock(block);
    }


    // build file listing
    List<String> fileList = new ArrayList<String>();
    buildFiles("", num, fileList);
    files = fileList.toArray(new String[0]);

    if (files.length == 0 && new Location(pattern).exists()) {
      files = new String[] {pattern};
    }

    valid = true;
  }

  // -- FilePattern API methods --

  /** Returns whether or not this pattern is a regular expression. */
  public boolean isRegex() {
    return isRegex;
  }

  /** Gets the file pattern string. */
  public String getPattern() { return pattern; }

  /** Gets whether the file pattern string is valid. */
  public boolean isValid() { return valid; }

  /** Gets the file pattern error message, if any. */
  public String getErrorMessage() { return msg; }

  /** Gets a listing of all files matching the given file pattern. */
  public String[] getFiles() { return files; }

  public String[][] getElements() {
    String[][] elements = new String[blocks.length][];
    for (int i=0; i<elements.length; i++) {
      elements[i] = blocks[i].getElements();
    }
    return elements;
  }

  public int[] getCount() {
    int[] count = new int[blocks.length];
    for (int i=0; i<count.length; i++) {
      count[i] = blocks[i].getElements().length;
    }
    return count;
  }

  /** Gets the specified numerical block. */
  public String getBlock(int i) {
    if (i < 0 || i >= startIndex.length) return null;
    return pattern.substring(startIndex[i], endIndex[i]);
  }

  /** Gets each numerical block. */
  public String[] getBlocks() {
    String[] s = new String[startIndex.length];
    for (int i=0; i<s.length; i++) s[i] = getBlock(i);
    return s;
  }

  /** Gets the pattern's text string before any numerical ranges. */
  public String getPrefix() {
    int s = pattern.lastIndexOf(File.separator) + 1;
    int e;
    if (startIndex.length > 0) e = startIndex[0];
    else {
      int dot = pattern.lastIndexOf(".");
      e = dot < s ? pattern.length() : dot;
    }
    return s <= e ? pattern.substring(s, e) : "";
  }

  /** Gets the pattern's text string after all numerical ranges. */
  public String getSuffix() {
    return endIndex.length > 0 ?
      pattern.substring(endIndex[endIndex.length - 1]) : pattern;
  }

  /** Gets the pattern's text string before the given numerical block. */
  public String getPrefix(int i) {
    if (i < 0 || i >= startIndex.length) return null;
    int s = i > 0 ? endIndex[i - 1] :
      (pattern.lastIndexOf(File.separator) + 1);
    int e = startIndex[i];
    return s <= e ? pattern.substring(s, e) : null;
  }

  /** Gets the pattern's text string before each numerical block. */
  public String[] getPrefixes() {
    String[] s = new String[startIndex.length];
    for (int i=0; i<s.length; i++) s[i] = getPrefix(i);
    return s;
  }

  // -- Utility methods --

  /**
   * Identifies the group pattern from a given file within that group.
   * @param path The file path to use as a template for the match.
   */
  public static String findPattern(String path) {
    return findPattern(new Location(path));
  }

  /**
   * Identifies the group pattern from a given file within that group.
   * @param file The file to use as a template for the match.
   */
  public static String findPattern(Location file) {
    return findPattern(file.getName(), file.getAbsoluteFile().getParent());
  }

  /**
   * Identifies the group pattern from a given file within that group.
   * @param file The file to use as a template for the match.
   */
  public static String findPattern(File file) {
    return findPattern(file.getName(), file.getAbsoluteFile().getParent());
  }

  /**
   * Identifies the group pattern from a given file within that group.
   * @param name The filename to use as a template for the match.
   * @param dir The directory in which to search for matching files.
   */
  public static String findPattern(String name, String dir) {
    if (dir == null) dir = ""; // current directory
    else if (!dir.equals("") && !dir.endsWith(File.separator)) {
      dir += File.separator;
    }
    Location dirFile = new Location(dir.equals("") ? "." : dir);

    // list files in the given directory
    Location[] f = dirFile.listFiles();
    if (f == null) return null;
    String[] nameList = new String[f.length];
    for (int i=0; i<nameList.length; i++) nameList[i] = f[i].getName();

    return findPattern(name, dir, nameList);
  }

  /**
   * Identifies the group pattern from a given file within that group.
   * @param name The filename to use as a template for the match.
   * @param dir The directory prefix to use for matching files.
   * @param nameList The names through which to search for matching files.
   */
  public static String findPattern(String name, String dir, String[] nameList) {
    return findPattern(name, dir, nameList, null);
  }

  /**
   * Identifies the group pattern from a given file within that group.
   * @param name The filename to use as a template for the match.
   * @param dir The directory prefix to use for matching files.
   * @param nameList The names through which to search for matching files.
   * @param excludeAxes The list of axis types which should be excluded from the
   *  pattern.
   */
  public static String findPattern(String name, String dir, String[] nameList,
    int[] excludeAxes)
  {
    if (excludeAxes == null) excludeAxes = new int[0];

    if (dir == null) dir = ""; // current directory
    else if (!dir.equals("") && !dir.endsWith(File.separator)) {
      dir += File.separator;
    }

    // compile list of numerical blocks
    int len = name.length();
    int bound = (len + 1) / 2;
    int[] indexList = new int[bound];
    int[] endList = new int[bound];
    int q = 0;
    boolean num = false;
    int ndx = -1, e = 0;
    for (int i=0; i<len; i++) {
      char c = name.charAt(i);
      if (c >= '0' && c <= '9') {
        if (num) e++;
        else {
          num = true;
          ndx = i;
          e = ndx + 1;
        }
      }
      else if (num) {
        num = false;
        indexList[q] = ndx;
        endList[q] = e;
        q++;
      }
    }
    if (num) {
      indexList[q] = ndx;
      endList[q] = e;
      q++;
    }

    // analyze each block, building pattern as we go
    StringBuffer sb = new StringBuffer(dir);

    for (int i=0; i<q; i++) {
      int last = i > 0 ? endList[i - 1] : 0;
      String prefix = name.substring(last, indexList[i]);
      int axisType = AxisGuesser.getAxisType(prefix);
      if (DataTools.containsValue(excludeAxes, axisType)) {
        sb.append(name.substring(last, endList[i]));
        continue;
      }

      sb.append(prefix);
      String pre = name.substring(0, indexList[i]);
      String post = name.substring(endList[i]);

      NumberFilter filter = new NumberFilter(pre, post);
      String[] list = matchFiles(nameList, filter);
      if (list == null || list.length == 0) return null;
      if (list.length == 1) {
        // false alarm; this number block is constant
        sb.append(name.substring(indexList[i], endList[i]));
        continue;
      }
      boolean fix = true;
      for (String s : list) {
        if (s.length() != len) {
          fix = false;
          break;
        }
      }
      if (fix) {
        // tricky; this fixed-width block could represent multiple numberings
        int width = endList[i] - indexList[i];

        // check each character for duplicates
        boolean[] same = new boolean[width];
        for (int j=0; j<width; j++) {
          same[j] = true;
          int jx = indexList[i] + j;
          char c = name.charAt(jx);
          for (String s : list) {
            if (s.charAt(jx) != c) {
              same[j] = false;
              break;
            }
          }
        }

        // break down each sub-block
        int j = 0;
        while (j < width) {
          int jx = indexList[i] + j;
          if (same[j]) {
            sb.append(name.charAt(jx));
            j++;
          }
          else {
            while (j < width && !same[j]) j++;
            String p = findPattern(name, nameList, jx, indexList[i] + j, "");
            char c = indexList[i] > 0 ? name.charAt(indexList[i] - 1) : '.';
            // check if this block represents the series axis
            if (p == null && c != 'S' && c != 's' && c != 'E' && c != 'e') {
              // unable to find an appropriate breakdown of numerical blocks
              return null;
            }
            else if (p == null) {
              sb.append(name.charAt(endList[i] - 1));
            }
            else sb.append(p);
          }
        }
      }
      else {
        // assume variable-width block represents only one numbering
        BigInteger[] numbers = new BigInteger[list.length];
        for (int j=0; j<list.length; j++) {
          numbers[j] = filter.getNumber(list[j]);
        }
        Arrays.sort(numbers);
        String bounds = getBounds(numbers, false);
        if (bounds == null) return null;
        sb.append(bounds);
      }
    }
    sb.append(q > 0 ? name.substring(endList[q - 1]) : name);

    return sb.toString();
  }

  /**
   * Generate a pattern from a list of file names.
   * The pattern generated will be a regular expression.
   *
   * Currently assumes that all file names are in the same directory.
   */
  public static String findPattern(String[] names) {
    String dir =
      names[0].substring(0, names[0].lastIndexOf(File.separator) + 1);

    StringBuffer pattern = new StringBuffer();
    pattern.append(Pattern.quote(dir));

    for (int i=0; i<names.length; i++) {
      pattern.append("(?:");
      String name =
        names[i].substring(names[i].lastIndexOf(File.separator) + 1);
      pattern.append(Pattern.quote(name));
      pattern.append(")");
      if (i < names.length - 1) {
        pattern.append("|");
      }
    }
    return pattern.toString();
  }

  public static String[] findSeriesPatterns(String base) {
    Location file = new Location(base).getAbsoluteFile();
    Location parent = file.getParentFile();
    String[] list = parent.list(true);
    return findSeriesPatterns(base, parent.getAbsolutePath(), list);
  }

  public static String[] findSeriesPatterns(String base, String dir,
    String[] nameList)
  {
    String baseSuffix = base.substring(base.lastIndexOf(File.separator) + 1);
    int dot = baseSuffix.indexOf(".");
    if (dot < 0) baseSuffix = "";
    else baseSuffix = baseSuffix.substring(dot + 1);

    String absoluteBase = new Location(base).getAbsolutePath();

    ArrayList<String> patterns = new ArrayList<String>();
    int[] exclude = new int[] {AxisGuesser.S_AXIS};
    for (String name : nameList) {
      String pattern = findPattern(name, dir, nameList, exclude);
      if (pattern == null) continue;
      int start = pattern.lastIndexOf(File.separator) + 1;
      if (start < 0) start = 0;
      String patternSuffix = pattern.substring(start);
      dot = patternSuffix.indexOf(".");
      if (dot < 0) patternSuffix = "";
      else patternSuffix = patternSuffix.substring(dot + 1);

      String checkPattern = findPattern(name, dir, nameList);
      String[] checkFiles = new FilePattern(checkPattern).getFiles();

      // ensure that escaping is consistent with the base file
      // this is needed to make sure that file grouping works correctly
      // on Windows
      for (int q=0; q<checkFiles.length; q++) {
        checkFiles[q] = new Location(checkFiles[q]).getAbsolutePath();
      }

      if (!patterns.contains(pattern) && (!new Location(pattern).exists() ||
        absoluteBase.equals(pattern)) && patternSuffix.equals(baseSuffix) &&
        DataTools.indexOf(checkFiles, absoluteBase) >= 0)
      {
        patterns.add(pattern);
      }
    }
    String[] s = patterns.toArray(new String[patterns.size()]);
    Arrays.sort(s);
    return s;
  }

  // -- Utility helper methods --

  /** Recursive method for parsing a fixed-width numerical block. */
  private static String findPattern(String name,
    String[] nameList, int ndx, int end, String p)
  {
    if (ndx == end) return p;
    for (int i=end-ndx; i>=1; i--) {
      NumberFilter filter = new NumberFilter(
        name.substring(0, ndx), name.substring(ndx + i));
      String[] list = matchFiles(nameList, filter);
      BigInteger[] numbers = new BigInteger[list.length];
      for (int j=0; j<list.length; j++) {
        numbers[j] = new BigInteger(list[j].substring(ndx, ndx + i));
      }
      Arrays.sort(numbers);
      String bounds = getBounds(numbers, true);
      if (bounds == null) continue;
      String pat = findPattern(name, nameList, ndx + i, end, p + bounds);
      if (pat != null) return pat;
    }
    // no combination worked; this parse path is infeasible
    return null;
  }

  /**
   * Gets a string containing start, end and step values
   * for a sorted list of numbers.
   */
  private static String getBounds(BigInteger[] numbers, boolean fixed) {
    if (numbers.length < 2) return null;
    BigInteger b = numbers[0];
    BigInteger e = numbers[numbers.length - 1];
    BigInteger s = numbers[1].subtract(b);
    if (s.equals(BigInteger.ZERO)) {
      // step size must be positive
      return null;
    }
    for (int i=2; i<numbers.length; i++) {
      if (!numbers[i].subtract(numbers[i - 1]).equals(s)) {
        // step size is not constant
        return null;
      }
    }
    String sb = b.toString();
    String se = e.toString();
    StringBuffer bounds = new StringBuffer("<");
    if (fixed) {
      int zeroes = se.length() - sb.length();
      for (int i=0; i<zeroes; i++) bounds.append("0");
    }
    bounds.append(sb);
    bounds.append("-");
    bounds.append(se);
    if (!s.equals(BigInteger.ONE)) {
      bounds.append(":");
      bounds.append(s);
    }
    bounds.append(">");
    return bounds.toString();
  }

  /** Filters the given list of filenames according to the specified filter. */
  private static String[] matchFiles(String[] inFiles, NumberFilter filter) {
    List<String> list = new ArrayList<String>();
    for (int i=0; i<inFiles.length; i++) {
      if (filter.accept(inFiles[i])) list.add(inFiles[i]);
    }
    return list.toArray(new String[0]);
  }

  // -- Helper methods --

  /** Recursive method for building filenames for the file listing. */
  private void buildFiles(String prefix, int ndx, List<String> fileList) {
    if (blocks.length == 0) {
      // regex pattern

      if (new Location(pattern).exists()) {
        fileList.add(pattern);
        return;
      }

      isRegex = true;

      String[] files = null;
      String dir;

      int endRegex = pattern.indexOf(File.separator + "\\E") + 1;
      int endNotRegex = pattern.lastIndexOf(File.separator) + 1;
      int end;

      //Check if an escaped path has been defined as part of the regex.
      if (pattern.startsWith("\\Q") && endRegex > 0 && endRegex <= endNotRegex)
      {
        dir = pattern.substring(2, endRegex);
        end = endRegex + 2;
      }
      else {
        dir = pattern.substring(0, endNotRegex);
        end = endNotRegex;
      }
      if (dir.equals("") || !new Location(dir).exists()) {
        files = Location.getIdMap().keySet().toArray(new String[0]);
        if (files.length == 0) {
          dir = ".";
          files = getAllFiles(dir);
        }
      }
      else {
        files = getAllFiles(dir);
      }

      Arrays.sort(files);

      String basePattern = pattern.substring(end);
      Pattern regex = null;
      try {
        regex = Pattern.compile(basePattern);
      }
      catch (PatternSyntaxException e) {
        regex = Pattern.compile(pattern);
      }

      for (String f : files) {
        Location path = new Location(dir, f);
        if (regex.matcher(path.getName()).matches()) {
          if (path.exists()) fileList.add(path.getAbsolutePath());
          else fileList.add(f);
        }
      }
    }
    else {
      // compute bounds for constant (non-block) pattern fragment
      int num = startIndex.length;
      int n1 = ndx == 0 ? 0 : endIndex[ndx - 1];
      int n2 = ndx == num ? pattern.length() : startIndex[ndx];
      String pre = pattern.substring(n1, n2);

      if (ndx == 0) fileList.add(pre + prefix);
      else {
        FilePatternBlock block = blocks[--ndx];
        String[] blockElements = block.getElements();
        for (String element : blockElements) {
          buildFiles(element + pre + prefix, ndx, fileList);
        }
      }
    }
  }

  private String[] getAllFiles(String dir) {
    ArrayList<String> files = new ArrayList<String>();

    Location root = new Location(dir);
    String[] children = root.list();

    for (String child : children) {
      Location file = new Location(root, child);
      if (file.isDirectory()) {
        String[] grandchildren = getAllFiles(file.getAbsolutePath());
        for (String g : grandchildren) {
          files.add(g);
        }
      }
      else {
        files.add(file.getAbsolutePath());
      }
    }

    return files.toArray(new String[files.size()]);
  }

  // -- Main method --

  /** Method for testing file pattern logic. */
  public static void main(String[] args) {
    String pat = null;
    if (args.length > 0) {
      // test file pattern detection based on the given file on disk
      Location file = new Location(args[0]);
      LOGGER.info("File = {}", file.getAbsoluteFile());
      pat = findPattern(file);
    }
    else {
      // test file pattern detection from a virtual file list
      String[] nameList = new String[2 * 4 * 3 * 12 + 1];
      nameList[0] = "outlier.ext";
      int count = 1;
      for (int i=1; i<=2; i++) {
        for (int j=1; j<=4; j++) {
          for (int k=0; k<=2; k++) {
            for (int l=1; l<=12; l++) {
              String sl = (l < 10 ? "0" : "") + l;
              nameList[count++] =
                "hypothetical" + sl + k + j + "c" + i + ".ext";
            }
          }
        }
      }
      pat = findPattern(nameList[1], null, nameList);
    }
    if (pat == null) LOGGER.info("No pattern found.");
    else {
      LOGGER.info("Pattern = {}", pat);
      FilePattern fp = new FilePattern(pat);
      if (fp.isValid()) {
        LOGGER.info("Pattern is valid.");
        LOGGER.info("Files:");
        String[] ids = fp.getFiles();
        for (int i=0; i<ids.length; i++) {
          LOGGER.info("  #{}: {}", i, ids[i]);
        }
      }
      else LOGGER.info("Pattern is invalid: {}", fp.getErrorMessage());
    }
  }

  // -- Deprecated methods --

  /* @deprecated */
  public BigInteger[] getFirst() {
    BigInteger[] first = new BigInteger[blocks.length];
    for (int i=0; i<first.length; i++) {
      first[i] = blocks[i].getFirst();
    }
    return first;
  }

  /* @deprecated */
  public BigInteger[] getLast() {
    BigInteger[] last = new BigInteger[blocks.length];
    for (int i=0; i<last.length; i++) {
      last[i] = blocks[i].getLast();
    }
    return last;
  }

  /* @deprecated */
  public BigInteger[] getStep() {
    BigInteger[] step = new BigInteger[blocks.length];
    for (int i=0; i<step.length; i++) {
      step[i] = blocks[i].getStep();
    }
    return step;
  }

}

// -- Notes --

// Some patterns observed:
//
//   TAABA1.PIC TAABA2.PIC TAABA3.PIC ... TAABA45.PIC
//
//   0m.tiff 3m.tiff 6m.tiff ... 36m.tiff
//
//   cell-Z0.C0.tiff cell-Z1.C0.tiff cell-Z2.C0.tiff ... cell-Z39.C0.tiff
//   cell-Z0.C1.tiff cell-Z1.C1.tiff cell-Z2.C1.tiff ... cell-Z39.C1.tiff
//
//   CRG401.PIC
//
//   TST00101.PIC TST00201.PIC TST00301.PIC
//   TST00102.PIC TST00202.PIC TST00302.PIC
//
//   800102.pic 800202.pic 800302.pic ... 805902.pic
//   800103.pic 800203.pic 800303.pic ... 805903.pic
//
//   nd400102.pic nd400202.pic nd400302.pic ... nd406002.pic
//   nd400103.pic nd400203.pic nd400303.pic ... nd406003.pic
//
//   WTERZ2_Series13_z000_ch00.tif ... WTERZ2_Series13_z018_ch00.tif
//
// --------------------------------------------------------------------------
//
// The file pattern notation defined here encompasses all patterns above.
//
//   TAABA<1-45>.PIC
//   <0-36:3>m.tiff
//   cell-Z<0-39>.C<0-1>.tiff
//   CRG401.PIC
//   TST00<1-3>0<1-2>.PIC
//   80<01-59>0<2-3>.pic
//   nd40<01-60>0<2-3>.pic
//   WTERZ2_Series13_z0<00-18>_ch00.tif
//
// In general: <B-E:S> where B is the start number, E is the end number, and S
// is the step increment. If zero padding has been used, the start number B
// will have leading zeroes to indicate that. If the step increment is one, it
// can be omitted.
//
// --------------------------------------------------------------------------
//
// If file groups not limited to numbering need to be handled, we can extend
// the notation as follows:
//
// A pattern such as:
//
//   ABCR.PIC ABCG.PIC ABCB.PIC
//
// Could be represented as:
//
//   ABC<R|G|B>.PIC
//
// If such cases come up, they will need to be identified heuristically and
// incorporated into the detection algorithm.
//
// --------------------------------------------------------------------------
//
// Here is a sketch of the algorithm for determining the pattern from a given
// file within a particular group:
//
//   01 - Detect number blocks within the file name, marking them with stars.
//        For example:
//
//          xyz800303b.pic -> xyz<>b.pic
//
//        Where <> represents a numerical block with unknown properties.
//
//   02 - Get a file listing for all files matching the given pattern. In the
//        example above, we'd get:
//
//        xyz800102b.pic, xyz800202b.pic, ..., xyz805902b.pic,
//        xyz800103b.pic, xyz800203b.pic, ..., xyz805903b.pic
//
//   03 - There are two possibilities: "fixed width" and "variable width."
//
//        Variable width: Not all filenames are the same length in characters.
//        Assume the block only covers a single number. Extract that number
//        from each filename, sort them and analyze as described below.
//
//        Fixed width: All filenames are the same length in characters. The
//        block could represent more than one number.
//
//        First, for each character, determine if that character varies between
//        filenames. If not, lock it down, splitting the block as necessary
//        into fixed-width blocks. When finished, the above example looks like:
//
//          xyz80<2>0<1>b.pic
//
//        Where <N> represents a numerical block of width N.
//
//        For each remaining block, extract the numbers from each matching
//        filename, sort the lists, and analyze as described below.
//
//   04 - In either case, analyze each list of numbers. The first on the list
//        is B. The last one is E. And S is the second one minus B. But check
//        the list to make sure no numbers are missing for that step size.
//
// NOTE: The fixed width algorithm above is insufficient for patterns like
// "0101.pic" through "2531.pic," where no fixed constant pads the two
// numerical counts. An additional step is required, as follows:
//
//   05 - For each fixed-width block, recursively divide it into pieces, and
//        analyze the numerical scheme according to those pieces. For example,
//        in the problem case given above, we'd have:
//
//          <4>.pic
//
//        Recursively, we'd analyze:
//
//          <4>.pic
//          <3><R1>.pic
//          <2><R2>.pic
//          <1><R3>.pic
//
//        The <Rx> blocks represent recursive calls to analyze the remainder of
//        the width.
//
//        The function decides if a given combination of widths is valid by
//        determining if each individual width is valid. An individual width
//        is valid if the computed B, S and E properly cover the numerical set.
//
//        If no combination of widths is found to be valid, the file numbering
//        is screwy. Print an error message.
