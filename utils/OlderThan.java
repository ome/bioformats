//
// OlderThan.java
//

import java.io.File;
import java.io.IOException;

/**
 * Returns true if the datestamp of the most recently changed file among its
 * arguments (including files recursively beneath directory arguments) is not
 * within the number of seconds given.
 */
public class OlderThan {

  /** Usage: java OlderThan num_seconds file1 file2 file3 ... */
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.err.println("Usage: java OlderThan " +
        "num_seconds file1 file2 file3 ...");
      return;
    }
    long minTime = Long.parseLong(args[0]);
    File[] f = new File[args.length - 1];
    for (int i=1; i<args.length; i++) f[i - 1] = new File(args[i]);
    long last = lastModified(f);
    long since = System.currentTimeMillis() - last;
    System.out.println(since > 1000 * minTime ? "true" : "false");
  }

  public static long lastModified(File[] f) throws IOException {
    long last = 0;
    for (int i=0; i<f.length; i++) {
      if (!f[i].exists()) continue;
      long lm = f[i].lastModified();
      if (lm > last) last = lm;
      if (f[i].isDirectory()) {
        File[] list = f[i].listFiles();
        lm = lastModified(list);
        if (lm > last) last = lm;
      }
    }
    return last;
  }

}
