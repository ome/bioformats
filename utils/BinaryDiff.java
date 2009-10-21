//
// BinaryDiff.java
//

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Compares two binary files, outputting their differences in human-readable
 * form. It is not as smart as diff in that it does not identify added or
 * missing bytes, just changes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/utils/BinaryDiff.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/utils/BinaryDiff.java">SVN</a></dd></dl>
 */
public class BinaryDiff {

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.out.println("Please specify two files to compare.");
      System.exit(1);
    }
    File f1 = new File(args[0]);
    File f2 = new File(args[1]);
    long len1 = f1.length();
    long len2 = f2.length();
    int min = (int) (len1 < len2 ? len1 : len2);
    if (len1 != len2) {
      System.out.println("File sizes differ; comparing only first " +
        min + " bytes.");
    }
    int width = ("" + min).length();
    DataInputStream fin1 = new DataInputStream(new FileInputStream(f1));
    DataInputStream fin2 = new DataInputStream(new FileInputStream(f2));
    int q = 0, size = 8192;
    byte[] buf1 = new byte[size];
    byte[] buf2 = new byte[size];
    while (q < min) {
      int num = q + size > min ? min - q : size;
      fin1.readFully(buf1, 0, num);
      fin2.readFully(buf2, 0, num);
      for (int i=0; i<num; i++) {
        if (buf1[i] != buf2[i]) {
          String s = "" + (q + i);
          while (s.length() < width) s = " " + s;
          int b1 = (buf1[i] & 0xff);
          int b2 = (buf2[i] & 0xff);
          System.out.println(s + ": " + b1 + " / " + b2);
        }
      }
      q += num;
    }
    fin1.close();
    fin2.close();
  }

}
