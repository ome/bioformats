//
// FixProxies.java
//

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * FixProxies is a program to post-process the Jace generated proxies.
 *
 * This step is necessary to avoid potential global namespace name clashes
 * with various Java constants. See conflicts.txt for a list.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/native/bf-cpp/src/FixProxies.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/native/bf-cpp/src/FixProxies.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FixProxies {

  // -- Constants --

  private static final String CONFLICTS_FILE = "conflicts.txt";

  private static final String PATCH_PREFIX = "JACE";
  private static final String CONSTANT_TOKEN = "CONSTANT";

  private static final String HEADER_INPUT =
    "(^.*static.* )(" + CONSTANT_TOKEN + ")\\(\\);$";
  private static final String HEADER_OUTPUT = "$1" + PATCH_PREFIX + "_$2();";
  private static final String SOURCE_INPUT =
    "(JFieldProxy.*::)(" + CONSTANT_TOKEN + ")\\(\\)$";
  private static final String SOURCE_OUTPUT = "$1" + PATCH_PREFIX + "_$2()";

  // -- Fields --

  private String pathPrefix;
  private ArrayList<String> constants;

  // -- Constructor --

  public FixProxies(String pathPrefix) throws IOException {
    this.pathPrefix = pathPrefix;

    // parse list of conflicting constants
    constants = new ArrayList<String>();
    BufferedReader in = new BufferedReader(new InputStreamReader(
      FixProxies.class.getResourceAsStream(CONFLICTS_FILE)));
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      line = line.trim();
      if (line.startsWith("#")) continue; // comment
      if (line.equals("")) continue; // blank line
      constants.add(line);
    }
    in.close();
  }

  // -- FixProxies methods --

  public void fixProxies() {
    for (String entry : constants) {
      int dot = entry.lastIndexOf(".");
      if (dot < 0) {
        System.err.println("Warning: invalid constant: " + entry);
        continue;
      }
      String path = entry.substring(0, dot).replaceAll("\\.", "/");
      String constant = entry.substring(dot + 1);

      // fix header file
      String headerInput = HEADER_INPUT.replaceAll(CONSTANT_TOKEN, constant);
      String headerOutput = HEADER_OUTPUT.replaceAll(CONSTANT_TOKEN, constant);
      String headerPath = pathPrefix + "/include/jace/proxy/" + path + ".h";
      new StringReplace(headerInput, headerOutput).processFile(headerPath);

      // fix source file
      String sourceInput = SOURCE_INPUT.replaceAll(CONSTANT_TOKEN, constant);
      String sourceOutput = SOURCE_OUTPUT.replaceAll(CONSTANT_TOKEN, constant);
      String sourcePath = pathPrefix + "/source/jace/proxy/" + path + ".cpp";
      new StringReplace(sourceInput, sourceOutput).processFile(sourcePath);
    }
  }

  // -- Main method --

  public static void main(String[] args) throws IOException {
    if (args == null || args.length < 1) {
      System.out.println("Usage: java FixProxies /path/to/proxies");
      return;
    }
    new FixProxies(args[0]).fixProxies();
  }

}
