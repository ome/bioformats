//
// DumpClass.java
//

import java.lang.reflect.*;

/** Uses reflection to print a bunch of information about a class. */
public class DumpClass {

  public static void main(String[] args) throws Exception {
    for (int i=0; i<args.length; i++) {
      Class c = Class.forName(args[i]);
      System.out.println("Class = " + c);

      System.out.println();
      System.out.println("[Constructors]");
      Constructor[] con = c.getDeclaredConstructors();
      for (int j=0; j<con.length; j++) System.out.println("\t" + con[j]);

      System.out.println();
      System.out.println("[Fields]");
      Field[] f = c.getDeclaredFields();
      for (int j=0; j<f.length; j++) System.out.println("\t" + f[j]);

      System.out.println();
      System.out.println("[Methods]");
      Method[] m = c.getDeclaredMethods();
      for (int j=0; j<m.length; j++) System.out.println("\t" + m[j]);
    }
  }

}
