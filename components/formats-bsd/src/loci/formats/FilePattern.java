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

import java.io.File;
import java.math.BigInteger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import loci.common.DataTools;
import loci.common.Location;

/**
 * Represents a collection of files meant to be part of the same
 * data series.
 * <p>
 * A file pattern can be constructed from a pattern string, where
 * variable parts are represented by blocks delimited by angle
 * brackets. For instance, the pattern
 * <code>img_&lt;R,G,B&gt;.ext</code> expands to
 * <code>img_R.ext</code>, <code>img_G.ext</code> and
 * <code>img_B.ext</code>.
 * <p>
 * In addition to comma-separated series as the one shown above,
 * pattern blocks can contain a sequence expression in the
 * <code>START-STOP:STEP</code> format. For instance, the pattern
 * <code>&lt;0-36:3&gt;m.tiff</code> expands to <code>0m.tiff,
 * 3m.tiff, 6m.tiff ... 36m.tiff</code>. If the step increment is one,
 * it can be omitted: <code>TAABA&lt;1-3&gt;.PIC</code> expands to
 * <code>TAABA1.PIC, TAABA2.PIC, TAABA3.PIC</code>. The start number
 * can have leading zeroes to denote zero-padded numbers:
 * <code>img_&lt;08-10&gt;.ext</code> expands to <code>img_08.ext,
 * img_09.ext, img_10.ext</code>. Sequence expressions also support
 * alphabetic ranges: <code>img_&lt;C-E&gt;.ext</code> expands to
 * <code>img_C.ext, img_D.ext, img_E.ext</code>.
 * <p>
 * A multi-block pattern is expanded by substituting the blocks with
 * the tuples from the cartesian product of all block expansions. For
 * instance, <code>z&lt;1-2&gt;&lt;R,G,B&gt;.tiff</code> expands to
 * <code>z1R.tiff, z1G.tiff, z1B.tiff, z2R.tiff, z2G.tiff,
 * z2B.tiff</code>.
 * <p>
 * If a pattern has zero blocks, it will be treated as a regular
 * expression to be matched against the names of existing files. If
 * there are no matches, the pattern expands to itself. For instance,
 * <code>/tmp/z.*.tif</code> expands to <code>/tmp/z1.tif</code> if
 * that file exists (and it's the only matching file), otherwise it
 * expands to itself.  If the pattern contains no special regex
 * syntax, it also expands to itself (a single file is a special case
 * of file pattern).
 * <p>
 * A <code>FilePattern</code> can also be created from a {@link
 * Location} or from a basename and directory. In these cases, the
 * pattern string is inferred from the names of other files in the
 * same directory.
 * <p>
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FilePattern {

  // -- Constants --

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

  /**
   * Creates a pattern object using the given file as a template.
   *
   * @param file the file as a {@link Location} instance.
   */
  public FilePattern(Location file) {
    this(FilePattern.findPattern(file));
  }

  /**
   * Creates a pattern object using the given
   * filename and directory path as a template.
   *
   * @param name file basename.
   * @param dir directory path.
   */
  public FilePattern(String name, String dir) {
    this(FilePattern.findPattern(name, dir));
  }

  /**
   * Creates a pattern object for files with the given pattern string.
   *
   * @param pattern the pattern string.
   */
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
    try {
      buildFiles("", num, fileList);
    }
    catch (IOException e) {
      msg = "Error whilst listing files.";
      return;
    }

    files = fileList.toArray(new String[0]);

    if (files.length == 0) {
      files = new String[] {pattern};
    }

    valid = true;
  }

  // -- FilePattern API methods --

  /**
   * Returns whether or not this pattern is a regular expression.
   *
   * @return true if this pattern is a regex, false otherwise.
   */
  public boolean isRegex() {
    return isRegex;
  }

  /**
   * Gets the file pattern string.
   *
   * @return the pattern string.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Returns whether or not the file pattern is valid.
   *
   * @return true if the pattern is valid, false otherwise.
   */
  public boolean isValid() {
    return valid;
  }

  /**
   * Gets the file pattern error message, if any.
   *
   * @return the error message generated while parsing the pattern
   * string, or null if there was no error.
   */
  public String getErrorMessage() {
    return msg;
  }

  /**
   * Gets a listing of all files matching this file pattern.
   *
   * @return an array containing all file names that match this pattern.
   */
  public String[] getFiles() {
    return files;
  }

  /**
   * Gets individual elements for each pattern block.
   *
   * @return an array containing an array of individual elements for
   * each pattern block.
   */
  public String[][] getElements() {
    String[][] elements = new String[blocks.length][];
    for (int i=0; i<elements.length; i++) {
      elements[i] = blocks[i].getElements();
    }
    return elements;
  }

  /**
   * Gets the number of elements for each pattern block.
   *
   * @return an array containing the number of individual elements for
   * each pattern block.
   */
  public int[] getCount() {
    int[] count = new int[blocks.length];
    for (int i=0; i<count.length; i++) {
      count[i] = blocks[i].getElements().length;
    }
    return count;
  }

  /**
   * Gets the specified file pattern block (e.g., <0-9>).
   *
   * @param i block index
   * @return the ith pattern block.
   */
  public String getBlock(int i) {
    if (i < 0 || i >= startIndex.length) return null;
    return pattern.substring(startIndex[i], endIndex[i]);
  }

  /**
   * Gets all file pattern blocks.
   *
   * @return an array containing all pattern blocks.
   */
  public String[] getBlocks() {
    String[] s = new String[startIndex.length];
    for (int i=0; i<s.length; i++) s[i] = getBlock(i);
    return s;
  }

  /**
   * Gets the pattern's substring that comes before any block sections.
   * For instance, the prefix of "img_z<0-2>t<1-3>_gs.ext" is "img_z".
   *
   * @return the prefix string as described above.
   */
  public String getPrefix() {
    return getPrefix(0);
  }

  /**
   * Gets the pattern's substring that comes after all block sections.
   * For instance, the suffix of "img_z<0-2>t<1-3>_gs.ext" is "_gs.ext".
   *
   * @return the suffix string as described above.
   */
  public String getSuffix() {
    return endIndex.length > 0 ?
      pattern.substring(endIndex[endIndex.length - 1]) : pattern;
  }

  /**
   * Gets the pattern's substring between block i-1 (or the beginning
   * of the pattern, if i is 0) and block i. For instance, the second
   * (i = 1) prefix of "img_z<0-2>t<1-3>_gs.ext" is "t".
   *
   * @param i block index.
   * @return the ith prefix as defined above.
   */
  public String getPrefix(int i) {
    if (i < 0 || i >= startIndex.length) return null;
    int s = i > 0 ? endIndex[i - 1] : 0;
    int e = startIndex[i];
    return s <= e ? pattern.substring(s, e) : "";
  }

  /**
   * Gets all block prefixes. For instance, the prefixes of
   * "img_z<0-2>t<1-3>_gs.ext" are "img_z" and "t".
   *
   * @return an array containing all block prefixes.
   */
  public String[] getPrefixes() {
    String[] s = new String[startIndex.length];
    for (int i=0; i<s.length; i++) s[i] = getPrefix(i);
    return s;
  }

  // -- Utility methods --

  /**
   * Identifies the group pattern from a given file within that group.
   *
   * @param path The file path to use as a template for the match.
   * @return the identified pattern.
   */
  public static String findPattern(String path) {
    return findPattern(new Location(path));
  }

  /**
   * Identifies the group pattern from a given file within that group.
   *
   * @param file The {@link Location} to use as a template for the match.
   * @return the identified pattern.
   */
  public static String findPattern(Location file) {
    return findPattern(file.getName(), file.getAbsoluteFile().getParent());
  }

  /**
   * Identifies the group pattern from a given file within that group.
   *
   * @param file The file to use as a template for the match.
   * @return the identified pattern.
   */
  public static String findPattern(File file) {
    return findPattern(file.getName(), file.getAbsoluteFile().getParent());
  }

  /**
   * Identifies the group pattern from a given filename and directory.
   *
   * @param name The file basename to use as a template for the match.
   * @param dir The directory in which to search for matching files.
   * @return the identified pattern.
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
   * Identifies the group pattern from a given filename, directory and
   * list of candidate filenames.
   *
   * @param name The file basename to use as a template for the match.
   * @param dir The directory prefix to use for matching files.
   * @param nameList The names through which to search for matching files.
   * @return the identified pattern.
   */
  public static String findPattern(String name, String dir, String[] nameList) {
    return findPattern(name, dir, nameList, null);
  }

  /**
   * Identifies the group pattern from a given filename, directory and
   * list of candidate filenames.
   *
   * @param name The file basename to use as a template for the match.
   * @param dir The directory prefix to use for matching files.
   * @param nameList The names through which to search for matching files.
   * @param excludeAxes The list of axis types which should be
   * excluded from the pattern (see {@link AxisGuesser}).
   * @return the identified pattern.
   */
  public static String findPattern(String name, String dir, String[] nameList,
    int[] excludeAxes)
  {
    if (excludeAxes == null) excludeAxes = new int[0];

    if (dir == null) dir = ""; // current directory
    else if (!dir.equals("") && !dir.endsWith(File.separator)) {
      dir += File.separator;
    }

    // locate numerical blocks
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

    final StringBuilder sb = new StringBuilder(dir);

    for (int i=0; i<q; i++) {
      // Get the list of matching files. For instance, if name is
      // "z10c1.tif" and nameList is {"z9c1.tif", "z10c1.tif",
      // "z9c2.tif", "z10c2.tif", "foo.tif"}, the matching list for
      // the first block (i = 0) is {"z9c1.tif", "z10c1.tif"}.
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

      // fixed width block iff all matching filenames are the same length
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

        // for each character, determine if it varies between filenames
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
            // this character is the same in all filenames; lock it down
            sb.append(name.charAt(jx));
            j++;
          }
          else {
            // recursively split the block into variable prefix + const suffix
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
   *
   * @param names the list of filenames.
   * @return the generated pattern.
   */
  public static String findPattern(String[] names) {
    String dir =
      names[0].substring(0, names[0].lastIndexOf(File.separator) + 1);

    final StringBuilder pattern = new StringBuilder();
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

  /**
   * Works like {@link #findSeriesPatterns(String, String, String[])},
   * but dir and nameList are inferred from the given file's absolute
   * path.
   *
   * @param base The file basename to use as a template for the match.
   * @return an array containing all identified patterns.
   */
  public static String[] findSeriesPatterns(String base) {
    Location file = new Location(base).getAbsoluteFile();
    Location parent = file.getParentFile();
    String[] list = parent.list(true);
    return findSeriesPatterns(base, parent.getAbsolutePath(), list);
  }

  /**
   * Similar to {@link #findPattern(String, String, String[])}, but
   * this does not merge series indices into a pattern block. Instead,
   * it returns a separate pattern for each series index. For
   * instance, if the file names are:
   *
   *   "foo_s1_z1.ext", "foo_s1_z2.ext", "foo_s2_z1.ext", "foo_s2_z2.ext"
   *
   * Then {@link #findPattern(String, String, String[]) findPattern}
   * will find a single "foo_s<1-2>_z<1-2>.ext" pattern, whereas this
   * method will find "foo_s1_z<1-2>.ext" and "foo_s2_z<1-2>.ext".

   * @param base The file basename to use as a template for the match.
   * @param dir The directory prefix to use for matching files.
   * @param nameList The names through which to search for matching files.
   * @return an array containing all identified patterns.
   */
  public static String[] findSeriesPatterns(String base, String dir,
    String[] nameList)
  {
    String baseSuffix = base.substring(base.lastIndexOf(File.separator) + 1);
    int dot = baseSuffix.indexOf('.');
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
      dot = patternSuffix.indexOf('.');
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

  // recursive method for fixed-width numerical blocks
  private static String findPattern(String name,
      String[] nameList, int ndx, int end, String p) {
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
   *
   * @param numbers a sorted list of numbers
   * @param fixed whether the numbers to a fixed width block
   * @return block bounds as a &lt;START-STOP:STEP&gt; expression
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
    final StringBuilder bounds = new StringBuilder("<");
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

  // filters the given list of filenames according to the specified filter
  private static String[] matchFiles(String[] inFiles, NumberFilter filter) {
    List<String> list = new ArrayList<String>();
    for (int i=0; i<inFiles.length; i++) {
      if (filter.accept(inFiles[i])) list.add(inFiles[i]);
    }
    return list.toArray(new String[0]);
  }

  // -- Helper methods --

  // recursive method for building the list of matching filenames
  private void buildFiles(String prefix, int ndx, List<String> fileList) throws IOException {
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

  private String[] getAllFiles(String dir) throws IOException {
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

  // -- Deprecated methods --

  /**
   * Gets the START element for all blocks.
   *
   * @return an array containing all START elements.
   */
  public BigInteger[] getFirst() {
    BigInteger[] first = new BigInteger[blocks.length];
    for (int i=0; i<first.length; i++) {
      first[i] = blocks[i].getFirst();
    }
    return first;
  }

  /**
   * Gets the STOP element for all blocks.
   *
   * @return an array containing all STOP elements.
   */
  public BigInteger[] getLast() {
    BigInteger[] last = new BigInteger[blocks.length];
    for (int i=0; i<last.length; i++) {
      last[i] = blocks[i].getLast();
    }
    return last;
  }

  /**
   * Gets the STEP element for all blocks.
   *
   * @return an array containing all STEP elements.
   */
  public BigInteger[] getStep() {
    BigInteger[] step = new BigInteger[blocks.length];
    for (int i=0; i<step.length; i++) {
      step[i] = blocks[i].getStep();
    }
    return step;
  }

}
