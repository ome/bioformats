//
// StringReplace.java
//

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * A program to filter and replace strings in a file.
 *
 * The only reason this program exists is because sed is
 * not available by default on Windows systems.
 */
public class StringReplace {

  // -- Fields --

  private String input, output;

  // -- Constructor --

  public StringReplace(String inputPattern, String outputPattern) {
    input = inputPattern;
    output = outputPattern;
  }

  // -- StringReplace methods --

  public void processFile(String path) {
    System.out.println("Processing file: " + path);

    // read data from file
    Vector<String> lines = null;
    try {
      lines = readFile(path);
    }
    catch (IOException exc) {
      System.err.println("Error: cannot read file: " + path);
      return;
    }

    // replace patterns
    int changed = 0;
    for (int i=0; i<lines.size(); i++) {
      String line = lines.get(i);
      String newLine = line.replaceAll(input, output);
      if (!line.equals(newLine)) {
        lines.set(i, newLine);
        changed++;
      }
    }

    // write data back to file
    try {
      writeFile(path, lines);
      System.out.println(changed + " lines updated.");
    }
    catch (IOException exc) {
      System.err.println("Error: cannot write file: " + path);
      return;
    }
  }

  public Vector<String> readFile(String path) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(path));
    Vector<String> lines = new Vector<String>();
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      lines.add(line);
    }
    in.close();
    return lines;
  }

  public void writeFile(String path, Vector<String> lines) throws IOException {
    File destFile = new File(path);
    File tempFile = new File(path + ".tmp");
    PrintWriter out = new PrintWriter(new FileWriter(tempFile));
    for (String line : lines) out.println(line);
    out.close();
    destFile.delete();
    tempFile.renameTo(destFile);
  }

  // -- Helper utility methods --

  public static String fixEscaped(String s) {
    s = s.replaceAll("\\\\n", "\n");
    s = s.replaceAll("\\\\r", "\r");
    s = s.replaceAll("\\\\t", "\t");
    return s;
  }

  // -- Main method --

  public static void main(String[] args) {
    if (args == null || args.length < 3) {
      System.out.println("Usage: java StringReplace " +
        "inputPattern outputPattern file [file2 file3 ...]");
    }
    String inputPattern = fixEscaped(args[0]);
    String outputPattern = fixEscaped(args[1]);
    StringReplace sr = new StringReplace(inputPattern, outputPattern);
    for (int i=2; i<args.length; i++) sr.processFile(args[i]);
  }

}
