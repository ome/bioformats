// CompareTrees.java

import java.io.File;
import java.util.Arrays;

/**
 * Compares the paths specified as command line arguments, outputting all
 * differences between them (i.e., files and directories present in one path
 * but not in the other). This implementation does note when two files do not
 * have matching file sizes, but does not check byte-for-byte equality when
 * the file sizes match.
 */
public class CompareTrees {

  public static final void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Please specify two paths to compare.");
      System.exit(1);
    }
    String path1 = args[0];
    String path2 = args[1];
    File dir1 = new File(path1);
    File dir2 = new File(path2);
    if (!dir1.exists() || !dir1.isDirectory()) {
      System.out.println(path1 + " is invalid.");
      System.exit(2);
    }
    if (!dir2.exists() || !dir2.isDirectory()) {
      System.out.println(path2 + " is invalid.");
      System.exit(3);
    }

    compare(dir1, dir2);
  }

  public static final void compare(File dir1, File dir2) {
    File[] list1 = dir1.listFiles();
    File[] list2 = dir2.listFiles();
    Arrays.sort(list1);
    Arrays.sort(list2);
    int ndx1 = 0, ndx2 = 0;
    while (ndx1 < list1.length && ndx2 < list2.length) {
      boolean d1 = list1[ndx1].isDirectory();
      boolean d2 = list2[ndx2].isDirectory();
      int c = list1[ndx1].getName().compareToIgnoreCase(list2[ndx2].getName());
      if (c < 0) {
        System.out.println("<<< " + list1[ndx1++] + (d1 ? " **" : ""));
      }
      else if (c > 0) {
        System.out.println(">>> " + list2[ndx2++] + (d2 ? " **" : ""));
      }
      else {
        if ((d1 && !d2) || (!d1 && d2)) {
          System.out.println("!D! " + list1[ndx1]);
        }
        else if (d1 && d2) compare(list1[ndx1], list2[ndx2]);
        else {
          if (list1[ndx1].length() != list2[ndx2].length()) {
            System.out.println("!S! " + list1[ndx1]);
          }
        }
        ndx1++;
        ndx2++;
      }
    }
    if (ndx1 < list1.length) {
      for (int i=ndx1; i<list1.length; i++) {
        boolean d1 = list1[i].isDirectory();
        System.out.println("<<< " + list1[i] + (d1 ? " **" : ""));
      }
    }
    if (ndx2 < list2.length) {
      for (int i=ndx2; i<list2.length; i++) {
        boolean d2 = list2[i].isDirectory();
        System.out.println(">>> " + list2[i] + (d2 ? " **" : ""));
      }
    }
  }

}
