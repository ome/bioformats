//
// FileOpsTest.java
//

import java.io.File;

/** Tests performance of File.exists() vs File.listFiles(). */
public class FileOpsTest {
  public static void main(String[] args) {
    int iter = 1000;
    if (args.length > 0) iter = Integer.parseInt(args[0]);
    long t1, t2;

    File dot = new File(".");
    File[] list = dot.listFiles();

    // time File.exists with an existing file
    t1 = System.currentTimeMillis();
    for (int i=0; i<iter; i++) list[i % list.length].exists();
    t2 = System.currentTimeMillis();
    System.out.println("File.exists() for existing = " + (t2 - t1) + " ms");

    // time File.exists with a non-existent file
    File non = new File("this-file-is-unlikely-to-exist");
    t1 = System.currentTimeMillis();
    for (int i=0; i<iter; i++) non.exists();
    t2 = System.currentTimeMillis();
    System.out.println("File.exists() for non-exist = " + (t2 - t1) + " ms");

    // time File.listFiles
    t1 = System.currentTimeMillis();
    for (int i=0; i<iter; i++) dot.listFiles();
    t2 = System.currentTimeMillis();
    System.out.println("File.listFiles() = " + (t2 - t1) +
      " ms (" + ((t2 - t1) / list.length) + "/file)");
  }
}
