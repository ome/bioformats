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

package loci.common;

import java.io.IOException;
import java.net.URL;

/**
 * A legacy delegator class for ome.scifio.common.ReflectedUniverse 
 * 
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/ReflectedUniverse.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/ReflectedUniverse.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ReflectedUniverse {

  // -- Constants --

  // -- Fields --

  private ome.scifio.common.ReflectedUniverse  unv;
  
  // -- Constructors --

  /** Constructs a new reflected universe. */
  public ReflectedUniverse() { 
    unv = new ome.scifio.common.ReflectedUniverse();
  }

  /**
   * Constructs a new reflected universe, with the given URLs
   * representing additional search paths for imported classes
   * (in addition to the CLASSPATH).
   */
  public ReflectedUniverse(URL[] urls) {
    unv = new ome.scifio.common.ReflectedUniverse (urls);
  }

  /** Constructs a new reflected universe that uses the given class loader. */
  public ReflectedUniverse(ClassLoader loader) {
    unv = new ome.scifio.common.ReflectedUniverse (loader);
  }

  // -- Utility methods --

  /**
   * Returns whether the given object is compatible with the
   * specified class for the purposes of reflection.
   */
  public static boolean isInstance(Class<?> c, Object o) {
    return ome.scifio.common.ReflectedUniverse .isInstance(c, o);
  }

  // -- ReflectedUniverse API methods --

  /**
   * Executes a command in the universe. The following syntaxes are valid:
   * <ul>
   *   <li>import fully.qualified.package.ClassName</li>
   *   <li>var = new ClassName(param1, ..., paramN)</li>
   *   <li>var.method(param1, ..., paramN)</li>
   *   <li>var2 = var.method(param1, ..., paramN)</li>
   *   <li>ClassName.method(param1, ..., paramN)</li>
   *   <li>var2 = ClassName.method(param1, ..., paramN)</li>
   *   <li>var2 = var</li>
   * </ul>
   * Important guidelines:
   * <ul>
   *   <li>Any referenced class must be imported first using "import".</li>
   *   <li>Variables can be exported from the universe with getVar().</li>
   *   <li>Variables can be imported to the universe with setVar().</li>
   *   <li>Each parameter must be either:
   *     <ol>
   *       <li>a variable in the universe</li>
   *       <li>a static or instance field (i.e., no nested methods)</li>
   *       <li>a string literal (remember to escape the double quotes)</li>
   *       <li>an integer literal</li>
   *       <li>a long literal (ending in L)</li>
   *       <li>a double literal (containing a decimal point)</li>
   *       <li>a boolean literal (true or false)</li>
   *       <li>the null keyword</li>
   *     </ol>
   *   </li>
   * </ul>
   */
  public Object exec(String command) throws ReflectException {
    try {
      return unv.exec(command);
    }
    catch (ome.scifio.common.ReflectException e) {
      throw new ReflectException(e.getCause());
    }
  }

  /** Registers a variable in the universe. */
  public void setVar(String varName, Object obj) {
    unv.setVar(varName, obj);
  }

  /** Registers a variable of primitive type boolean in the universe. */
  public void setVar(String varName, boolean b) {
    unv.setVar(varName, b);
  }

  /** Registers a variable of primitive type byte in the universe. */
  public void setVar(String varName, byte b) {
    unv.setVar(varName, b);
  }

  /** Registers a variable of primitive type char in the universe. */
  public void setVar(String varName, char c) {
    unv.setVar(varName, c);
  }

  /** Registers a variable of primitive type double in the universe. */
  public void setVar(String varName, double d) {
    unv.setVar(varName, d);
  }

  /** Registers a variable of primitive type float in the universe. */
  public void setVar(String varName, float f) {
    unv.setVar(varName, f);
  }

  /** Registers a variable of primitive type int in the universe. */
  public void setVar(String varName, int i) {
    unv.setVar(varName, i);
  }

  /** Registers a variable of primitive type long in the universe. */
  public void setVar(String varName, long l) {
    unv.setVar(varName, l);
  }

  /** Registers a variable of primitive type short in the universe. */
  public void setVar(String varName, short s) {
    unv.setVar(varName, s);
  }

  /**
   * Returns the value of a variable or field in the universe.
   * Primitive types will be wrapped in their Java Object wrapper classes.
   */
  public Object getVar(String varName) throws ReflectException {
    try {
      return unv.getVar(varName);
    }
    catch (ome.scifio.common.ReflectException e) {
       throw (ReflectException)e;
    }
  }

  /** Sets whether access modifiers (protected, private, etc.) are ignored. */
  public void setAccessibilityIgnored(boolean ignore) { 
    unv.setAccessibilityIgnored(ignore);
  }

  /** Gets whether access modifiers (protected, private, etc.) are ignored. */
  public boolean isAccessibilityIgnored() { 
    return unv.isAccessibilityIgnored();
  }

  // -- Main method --

  /**
   * Allows exploration of a reflected universe in an interactive environment.
   */
  public static void main(String[] args) throws IOException {
    ome.scifio.common.ReflectedUniverse.main(args);
  }

  // -- Object delegators --

  @Override
  public boolean equals(Object obj) {
    return unv.equals(obj);
  }
  
  @Override
  public int hashCode() {
    return unv.hashCode();
  }
  
  @Override
  public String toString() {
    return unv.toString();
  }
}
