/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

// Adapted from: http://www.jguru.com/faq/view.jsp?EID=246569

import java.lang.reflect.Method;

import ome.scifio.common.ReflectedUniverse;

/**
 * A benchmark for Java reflection performance.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/utils/ReflectionPerformance.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/utils/ReflectionPerformance.java;hb=HEAD">Gitweb</a></dd></dl>
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
