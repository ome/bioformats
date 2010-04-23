//
// SysProps.java
//

import java.util.Properties;

/**
 * Dumps all Java system properties.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/utils/SysProps.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/utils/SysProps.java">SVN</a></dd></dl>
 */
public class SysProps {

  public static void main(String[] args) {
    printProperties(args);
  }

  public static void printProperties(String[] filters) {
    Properties props = System.getProperties();
    for (Object key : props.keySet()) {
      String name = key.toString();
      if (filterMatch(name, filters)) {
        String value = props.get(key).toString();
        printProperty(name, value);
      }
    }
  }

  public static boolean filterMatch(String s, String[] filters) {
    for (String filter : filters) {
      if (s.indexOf(filter) < 0) return false;
    }
    return true;
  }

  public static void printProperty(String name, String value) {
    if (name.endsWith(".path")) printPathProperty(name, value);
    else System.out.println(name + " = " + value);
  }

  public static void printPathProperty(String name, String value) {
    String sep = System.getProperty("path.separator");
    String[] tokens = value.split(sep);
    System.out.println(name + " =");
    for (String token : tokens) {
      System.out.println("\t" + token);
    }
  }

}
