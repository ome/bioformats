//
// UnconfiguredFiles.java
//

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import loci.common.DataTools;

/**
 * Recursively searchs a directory for files that have not been configured for
 * testing.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://loci.wisc.edu/trac/java/browser/trunk/components/test-suite/utils/UnconfiguredFiles.java">Trac</a>,
 * <a href="http://loci.wisc.edu/svn/java/trunk/components/test-suite/utils/UnconfiguredFiles.java">SVN</a></dd></dl>
 */
public class UnconfiguredFiles {

  private ArrayList<String> unconfigured = new ArrayList<String>();

  public void buildUnconfiguredList(File root) throws IOException {
    if (!root.isDirectory()) return;
    String[] list = root.list();
    File configFile = new File(root, ".bioformats");
    String configData = null;
    if (configFile.exists()) {
      configData = DataTools.readFile(configFile.getAbsolutePath());
    }

    for (String file : list) {
      File child = new File(root, file).getAbsoluteFile();
      if (file.startsWith(".")) continue;
      else if (child.isDirectory()) buildUnconfiguredList(child);
      else if (!configFile.exists() ||
        configData.indexOf("\"" + child.getName() + "\"") < 0)
      {
        unconfigured.add(child.getAbsolutePath());
      }
    }
  }

  public void printList() {
    if (unconfigured.size() > 0) {
      System.out.println("Unconfigured files:");
      for (String file : unconfigured) {
        System.out.println("  " + file);
      }
    }
    else System.out.println("All files have been configured!");
  }

  public static void main(String[] args) throws IOException {
    UnconfiguredFiles f = new UnconfiguredFiles();
    f.buildUnconfiguredList(new File(args[0]));
    f.printList();
  }

}
