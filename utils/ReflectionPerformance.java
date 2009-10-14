//
// ReflectionPerformance.java
//

// Adapted from: http://www.jguru.com/faq/view.jsp?EID=246569

import java.lang.reflect.Method;

import loci.common.ReflectedUniverse;

/**
 * A benchmark for Java reflection performance.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/utils/ReflectionPerformance.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/utils/ReflectionPerformance.java">SVN</a></dd></dl>
 */
public class ReflectionPerformance {

  private static int val = 0;

  public static void fastMethod() {
    val++;
  }

  public static void fastMethod(int arg) {
    val += arg;
  }

  public static void slowMethod() {
    for (int i=0; i<1000000; i++) val++;
  }

  public static void slowMethod(int arg) {
    for (int i=0; i<1000000; i++) val += arg;
  }

  public static void benchmarkFast() throws Exception {
    System.out.println();
    System.out.println("--== Fast method benchmark, no args ==--");
    Class<ReflectionPerformance> c = ReflectionPerformance.class;
    Method method = c.getMethod("fastMethod");

    ReflectedUniverse r = new ReflectedUniverse();
    r.exec("import ReflectionPerformance");

    int loops = 100000;
    for (int outer=0; outer<3; outer++) {
      long start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) ReflectionPerformance.fastMethod();
      System.out.println(loops + " regular method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) method.invoke(null);

      System.out.println(loops + " reflective method calls without lookup: " +
        (System.currentTimeMillis() - start) + " ms");
      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        method = c.getMethod("fastMethod");
        method.invoke(null);
      }
      System.out.println(loops + " reflective method calls with lookup: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        r.exec("ReflectionPerformance.fastMethod()");
      }
      System.out.println(loops + " reflected universe method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      loops *= 10;
    }
  }

  public static void benchmarkFastWithArg() throws Exception {
    System.out.println();
    System.out.println("--== Fast method benchmark, w/ arg ==--");
    Class<ReflectionPerformance> c = ReflectionPerformance.class;
    Method method = c.getMethod("fastMethod", int.class);

    ReflectedUniverse r = new ReflectedUniverse();
    r.exec("import ReflectionPerformance");

    int arg = 1;
    r.setVar("arg", arg);

    int loops = 100000;
    for (int outer=0; outer<3; outer++) {
      long start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) ReflectionPerformance.fastMethod(arg);
      System.out.println(loops + " regular method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) method.invoke(null, arg);

      System.out.println(loops + " reflective method calls without lookup: " +
        (System.currentTimeMillis() - start) + " ms");
      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        method = c.getMethod("fastMethod", int.class);
        method.invoke(null, arg);
      }
      System.out.println(loops + " reflective method calls with lookup: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        r.exec("ReflectionPerformance.fastMethod(arg)");
      }
      System.out.println(loops + " reflected universe method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      loops *= 10;
    }
  }

  public static void benchmarkSlow() throws Exception {
    System.out.println();
    System.out.println("--== Slow method benchmark, no args ==--");
    Class<ReflectionPerformance> c = ReflectionPerformance.class;
    Method method = c.getMethod("slowMethod");

    ReflectedUniverse r = new ReflectedUniverse();
    r.exec("import ReflectionPerformance");

    int loops = 100;
    for (int outer=0; outer<3; outer++) {
      long start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) ReflectionPerformance.slowMethod();
      System.out.println(loops + " regular method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) method.invoke(null);

      System.out.println(loops + " reflective method calls without lookup: " +
        (System.currentTimeMillis() - start) + " ms");
      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        method = c.getMethod("slowMethod");
        method.invoke(null);
      }
      System.out.println(loops + " reflective method calls with lookup: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        r.exec("ReflectionPerformance.slowMethod()");
      }
      System.out.println(loops + " reflected universe method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      loops *= 10;
    }
  }

  public static void benchmarkSlowWithArg() throws Exception {
    System.out.println();
    System.out.println("--== Slow method benchmark, w/ arg ==--");
    Class<ReflectionPerformance> c = ReflectionPerformance.class;
    Method method = c.getMethod("slowMethod", int.class);

    ReflectedUniverse r = new ReflectedUniverse();
    r.exec("import ReflectionPerformance");

    int arg = 1;
    r.setVar("arg", arg);

    int loops = 100;
    for (int outer=0; outer<3; outer++) {
      long start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) ReflectionPerformance.slowMethod(arg);
      System.out.println(loops + " regular method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) method.invoke(null, arg);

      System.out.println(loops + " reflective method calls without lookup: " +
        (System.currentTimeMillis() - start) + " ms");
      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        method = c.getMethod("slowMethod", int.class);
        method.invoke(null, arg);
      }
      System.out.println(loops + " reflective method calls with lookup: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        r.exec("ReflectionPerformance.slowMethod(arg)");
      }
      System.out.println(loops + " reflected universe method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      loops *= 10;
    }
  }


  public static void main(String[] args) throws Exception {
    benchmarkFast();
    benchmarkFastWithArg();
    benchmarkSlow();
    benchmarkSlowWithArg();
  }

}
