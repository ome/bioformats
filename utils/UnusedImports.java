//
// UnusedImports.java
//

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Scans Java source files for unused and package imports.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/utils/UnusedImports.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/utils/UnusedImports.java">SVN</a></dd></dl>
 */
public class UnusedImports {
  public static void checkFile(String filename) throws IOException {
    BufferedReader file =
      new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
    Vector<String> imports = new Vector<String>();
    Vector<String> packages = new Vector<String>();

    String line = file.readLine();
    int lineNumber = 0;
    while (line != null) {
      line = line.trim();
      if (line.startsWith("import ")) {
        if (line.endsWith(".*;")) {
          packages.add(line.replace("import ", ""));
        }
        else {
          String className =
            line.substring(line.lastIndexOf(".") + 1, line.length() - 1);
          imports.add(className);
        }
      }
      else {
        for (int i=0; i<imports.size(); i++) {
          String importedClass = imports.get(i);
          if (line.indexOf(importedClass) != -1 && !line.startsWith("//") &&
            !line.startsWith("*") && !line.startsWith("/*"))
          {
            imports.remove(importedClass);
            i--;
          }
        }
      }

      line = file.readLine();
      lineNumber++;
    }

    if (imports.size() > 0 || packages.size() > 0) {
      System.out.println(filename);
      if (imports.size() > 0) {
        System.out.println("  Unused imports:");
        for (String importedClass : imports) {
          System.out.println("    " + importedClass);
        }
      }
      if (packages.size() > 0) {
        System.out.println("  Package imports:");
        for (String packageName : packages) {
          System.out.println("    " + packageName);
        }
      }
    }

    file.close();
  }

  public static void main(String[] args) throws IOException {
    for (String arg : args) {
      checkFile(arg);
    }
  }
}
