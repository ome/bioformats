//
// DumpClass.java
//

import java.lang.reflect.*;

/**
 * Uses reflection to print a bunch of information about a class.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/utils/DumpClass.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/utils/DumpClass.java">SVN</a></dd></dl>
 */
public class DumpClass {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws Exception {
    for (int i=0; i<args.length; i++) {
      Class c = Class.forName(args[i]);
      System.out.println("Class = " + c);

      System.out.println();
      System.out.println("[Constructors]");
      Constructor[] con = c.getDeclaredConstructors();
      for (int j=0; j<con.length; j++) System.out.println(con[j]);

      System.out.println();
      System.out.println("[Fields]");
      Field[] f = c.getDeclaredFields();
      for (int j=0; j<f.length; j++) System.out.println(f[j]);

      System.out.println();
      System.out.println("[Methods]");
      Method[] m = c.getDeclaredMethods();
      for (int j=0; j<m.length; j++) System.out.println(m[j]);
    }
  }

}
