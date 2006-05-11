//
// FixEOLSpaces.java
//

import java.io.*;

/** Scans text files for end-of-line spaces, and removes them. */
public class FixEOLSpaces {

  public static void main(String[] args) throws IOException {
    for (int i=0; i<args.length; i++) {
      File inFile = new File(args[i]);
      System.out.print(inFile + "  ");

      // check for EOL spaces and tabs
      BufferedReader in = new BufferedReader(new FileReader(inFile));
      boolean process = false;
      while (true) {
        String line = in.readLine();
        if (line == null) break;
        if (line.endsWith(" ") || line.endsWith("\t")) {
          process = true;
          break;
        }
      }
      in.close();

      if (process) {
        // remove EOL spaces and tabs
        in = new BufferedReader(new FileReader(inFile));
        File outFile = new File(args[i] + ".tmp");
        PrintWriter out = new PrintWriter(new FileWriter(outFile));
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
          out.println(line);
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
