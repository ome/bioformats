//
// LineLength.java
//

import java.io.*;
import java.util.Arrays;
import java.util.Vector;

/**
 * Scans text files for lines longer than a specified length.
 * Also checks for tabs and end-of-line spaces.
 */
public class LineLength implements FileFilter {

  private static final int LENGTH = 80;
  private static final int TABWIDTH = 2;

  private static final String[] LOCI_HACKS = {
    " * <dd><a href=\"https://skyking.microscopy.wisc.edu/trac/",
    " * <a href=\"https://skyking.microscopy.wisc.edu/svn/java/trunk/"
  };

  public static void main(String[] args) throws IOException {
    int tabWidth = TABWIDTH;
    boolean checkTabs = true, checkEndSpaces = true;
    boolean lociHacks = false;
    Vector v = new Vector();
    for (int i=0; i<args.length; i++) {
      if (args[i].startsWith("-")) {
        // argument is a flag
        if (args[i].equals("-tabwidth")) {
          int q = -1;
          try { q = Integer.parseInt(args[++i]); }
          catch (Exception exc) { }
          if (q <= 0) System.out.println("Ignoring bogus tab width.");
          else tabWidth = q;
        }
        else if (args[i].equals("-notabs")) checkTabs = false;
        else if (args[i].equals("-noendspaces")) checkEndSpaces = false;
        else if (args[i].equals("-locihacks")) lociHacks = true;
        else System.out.println("Unknown flag: " + args[i]);
        continue;
      }
      // argument is a filename
      String dir;
      int slash = args[i].lastIndexOf(File.separator);
      if (slash < 0) dir = ".";
      else dir = args[i].substring(0, slash);
      File[] files = new File(dir).listFiles(new LineLength(args[i]));
      if (files != null) {
        for (int j=0; j<files.length; j++) v.add(strip(files[j]));
      }
    }

    char[] spaces = new char[tabWidth];
    Arrays.fill(spaces, ' ');
    String tabSpaces = new String(spaces);

    File[] files = new File[v.size()];
    v.copyInto(files);
    if (files.length == 0) {
      System.out.println("No matching files found.");
      System.exit(2);
    }
    int numOk = 0;
    for (int i=0; i<files.length; i++) {
      BufferedReader fin = new BufferedReader(new FileReader(files[i]));
      String line;
      int num = 0;
      boolean okay = true;
      while (true) {
        line = fin.readLine();
        num++;
        if (line == null) break;
        boolean hasTabs = checkTabs && line.indexOf("\t") >= 0;
        line = line.replaceAll("\t", tabSpaces);
        int len = line.length();
        boolean tooLong = len > LENGTH;
        if (lociHacks) {
          for (int j=0; j<LOCI_HACKS.length; j++) {
            if (line.startsWith(LOCI_HACKS[j])) {
              tooLong = false;
              break;
            }
          }
        }
        boolean endSpace = checkEndSpaces &&
          len > 0 && line.charAt(len - 1) == ' ';
        if (!tooLong && !hasTabs && !endSpace) continue;
        if (okay) {
          okay = false;
          System.out.print(files[i].getPath() + ": ");
        }
        else System.out.print(", ");
        System.out.print(num);
        if (tooLong) System.out.print("-" + len);
        if (hasTabs) System.out.print("-T");
        if (endSpace) System.out.print("-S");
      }
      if (okay) numOk++;
      else System.out.println();
    }
    System.out.println(numOk + " of " + files.length + " files OK.");
  }

  private static File strip(File file) {
    String s = file.getPath();
    return s.startsWith("." + File.separator) ?
      new File(s.substring(1 + File.separator.length())) : file;
  }

  private String exp;
  private boolean suffix;
  private File expFile;

  public LineLength(String exp) {
    this.exp = exp.trim();
    suffix = this.exp.startsWith("*.");
    if (suffix) this.exp = this.exp.substring(1);
    else expFile = new File(this.exp);
  }

  public boolean accept(File pathName) {
    pathName = strip(pathName);
    return suffix ?
      pathName.getName().endsWith(exp) : expFile.equals(pathName);
  }

}
