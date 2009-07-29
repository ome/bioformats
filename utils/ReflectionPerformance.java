//
// ReflectionPerformance.java
//

// Adapted from: http://www.jguru.com/faq/view.jsp?EID=246569

import java.lang.reflect.Method;

import loci.common.ReflectedUniverse;

/** A benchmark for Java reflection performance. */
public class ReflectionPerformance {

  public static void slowMethod() {
    for (int i=0; i<1000000; i++);
  }

  public static void benchmarkFast() throws Exception {
    System.out.println("--== toString() benchmark ==--");
    Object object = new Object();
    Class c = Object.class;
    Method method = c.getMethod("toString");

    ReflectedUniverse r = new ReflectedUniverse();
    r.exec("import java.lang.Object");
    r.setVar("object", object);

    int loops = 100000;
    for (int outer=0; outer<3; outer++) {
      long start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) object.toString();
      System.out.println(loops + " regular method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) method.invoke(object);
      System.out.println(loops + " reflective method calls without lookup: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        method = c.getMethod("toString");
        method.invoke(object);
      }
      System.out.println(loops + " reflective method calls with lookup: " +
        (System.currentTimeMillis() - start) + " ms");

      start = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
        r.exec("object.toString()");
      }
      System.out.println(loops + " reflected universe method calls: " +
        (System.currentTimeMillis() - start) + " ms");

      loops *= 10;
    }
  }

  public static void benchmarkSlow() throws Exception {
    System.out.println("--== slowMethod() benchmark ==--");
    Class c = ReflectionPerformance.class;
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

  public static void main(String[] args) throws Exception {
    benchmarkFast();
    benchmarkSlow();
  }

}
