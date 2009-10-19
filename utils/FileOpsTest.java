//
// FileOpsTest.java
//

import java.io.File;

/**
 * Tests performance of File.exists() vs File.listFiles().
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/utils/FileOpsTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/utils/FileOpsTest.java">SVN</a></dd></dl>
 */
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

/*
Some results on a MacPro 2 x 2.66 GHz Dual-Core Intel Xeon
Networked file system over SMB on local network (gigabit?)
Windows and Linux results via Parallels v2

LINUX - NETWORK
File.exists() for existing = 16 ms
File.exists() for non-exist = 12 ms
File.listFiles() = 1755 ms (2/file)

MAC OS X - NETWORK
curtis@monk:~/data/perkinelmer/koen/20061107165034$ java FileOpsTest
File.exists() for existing = 58 ms
File.exists() for non-exist = 602 ms
File.listFiles() = 4519 ms (6/file)

WIN XP - NETWORK
Z:/perkinelmer/koen/20061107165034>java -cp C:/svn/java/utils FileOpsTest
File.exists() for existing = 2043 ms
File.exists() for non-exist = 1763 ms
File.listFiles() = 40882 ms (60/file)

LINUX - LOCAL
File.exists() for existing = 10 ms
File.exists() for non-exist = 17 ms
File.listFiles() = 71 ms (2/file)

MAC OS X - LOCAL
curtis@monk:~/svn/java/jar$ java FileOpsTest
File.exists() for existing = 7 ms
File.exists() for non-exist = 12 ms
File.listFiles() = 149 ms (5/file)

WIN XP - LOCAL
C:/svn/java/jar>java -cp ../utils FileOpsTest
File.exists() for existing = 0 ms
File.exists() for non-exist = 0 ms
File.listFiles() = 91 ms (3/file)
*/
