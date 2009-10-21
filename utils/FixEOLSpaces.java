//
// FixEOLSpaces.java
//

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Scans text files for end-of-line spaces or tabs, and
 * multiple consecutive blank lines, and removes them.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/utils/FixEOLSpaces.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/utils/FixEOLSpaces.java">SVN</a></dd></dl>
 */
public class FixEOLSpaces {

  public static void main(String[] args) throws IOException {
    for (int i=0; i<args.length; i++) {
      File inFile = new File(args[i]);
      System.out.print(inFile + "  ");

      // check for EOL spaces and tabs
      BufferedReader in = new BufferedReader(new FileReader(inFile));
      boolean process = false;
      boolean lastBlank = false;
      while (true) {
        String line = in.readLine();
        if (line == null) break;
        if (line.endsWith(" ") || line.endsWith("\t")) {
          process = true;
          break;
        }
        boolean blank = line.trim().equals("");
        if (blank && lastBlank) {
          // found consecutive blank lines
          process = true;
          break;
        }
        lastBlank = blank;
      }
      in.close();

      if (process) {
        // remove EOL spaces and tabs
        in = new BufferedReader(new FileReader(inFile));
        File outFile = new File(args[i] + ".tmp");
        PrintWriter out = new PrintWriter(new FileWriter(outFile));
        lastBlank = false;
        while (true) {
          String line = in.readLine();
          if (line == null) break;
          if (line.endsWith(" ") || line.endsWith("\t")) {
            char[] c = line.toCharArray();
            int n;
            for (n=c.length-1; n>=0; n--) {
              if (c[n] != ' ' && c[n] != '\t') break;
            }
            line = new String(c, 0, n + 1);
          }
          boolean blank = line.equals("");
          if (!blank || !lastBlank) out.println(line);
          lastBlank = blank;
        }
        out.close();
        in.close();
        inFile.delete();
        outFile.renameTo(inFile);
        System.out.println("OK");
      }
      else System.out.println("skipped");
    }
  }

}
