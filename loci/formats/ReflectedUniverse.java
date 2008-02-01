//
// ReflectedUniverse.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A general-purpose reflection wrapper class.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ReflectedUniverse.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ReflectedUniverse.java">SVN</a></dd></dl>
 */
public class ReflectedUniverse {

  // -- Fields --

  /** Hashtable containing all variables present in the universe. */
  protected Hashtable variables;

  /** Class loader for imported classes. */
  protected ClassLoader loader;

  /** Whether to force our way past restrictive access modifiers. */
  protected boolean force;

  /** Debugging flag. */
  protected boolean debug;

  // -- Constructors --

  /** Constructs a new reflected universe. */
  public ReflectedUniverse() { this((ClassLoader) null); }

  /**
   * Constructs a new reflected universe, with the given URLs
   * representing additional search paths for imported classes
   * (in addition to the CLASSPATH).
   */
  public ReflectedUniverse(URL[] urls) {
    this(urls == null ? null : new URLClassLoader(urls));
  }

  /** Constructs a new reflected universe that uses the given class loader. */
  public ReflectedUniverse(ClassLoader loader) {
    variables = new Hashtable();
    this.loader = loader == null ? getClass().getClassLoader() : loader;
    debug = false;
  }

  // -- Utility methods --

  /**
   * Returns whether the given object is compatible with the
   * specified class for the purposes of reflection.
   */
  public static boolean isInstance(Class c, Object o) {
    return (o == null || c.isInstance(o) ||
      (c == byte.class && o instanceof Byte) ||
      (c == short.class && o instanceof Short) ||
      (c == int.class && o instanceof Integer) ||
      (c == long.class && o instanceof Long) ||
      (c == float.class && o instanceof Float) ||
      (c == double.class && o instanceof Double) ||
      (c == boolean.class && o instanceof Boolean) ||
      (c == char.class && o instanceof Character));
  }

  // -- ReflectedUniverse API methods --

  /**
   * Executes a command in the universe. The following syntaxes are valid:
   * <li>import fully.qualified.package.ClassName
   * <li>var = new ClassName(param1, ..., paramN)
   * <li>var.method(param1, ..., paramN)
   * <li>var2 = var.method(param1, ..., paramN)
   * <li>ClassName.method(param1, ..., paramN)
   * <li>var2 = ClassName.method(param1, ..., paramN)
   * <li>var2 = var
   * <p>
   * Important guidelines:
   * <li>Any referenced class must be imported first using "import".
   * <li>Variables can be exported from the universe with getVar().
   * <li>Variables can be imported to the universe with setVar().
   * <li>Each parameter must be either:
   *     1) a variable in the universe;
   *     2) a static or instance field (i.e., no nested methods);
   *     3) a string literal (remember to escape the double quotes);
   *     4) an integer literal;
   *     6) a long literal (ending in L);
   *     7) a double literal (containing a decimal point);
   *     8) a boolean literal (true or false);
   *     or 9) the null keyword.
   */
  public Object exec(String command) throws ReflectException {
    command = command.trim();
    if (command.startsWith("import ")) {
      // command is an import statement
      command = command.substring(7).trim();
      int dot = command.lastIndexOf(".");
      String varName = dot < 0 ? command : command.substring(dot + 1);
      Class c;
      try {
        c = Class.forName(command, true, loader);
      }
      catch (NoClassDefFoundError err) {
        if (debug) LogTools.trace(err);
        throw new ReflectException("No such class: " + command, err);
      }
      catch (ClassNotFoundException exc) {
        if (debug) LogTools.trace(exc);
        throw new ReflectException("No such class: " + command, exc);
      }
      catch (RuntimeException exc) {
        // HACK: workaround for bug in Apache Axis2
        String msg = exc.getMessage();
        if (msg != null && msg.indexOf("ClassNotFound") < 0) throw exc;
        if (debug) LogTools.trace(exc);
        throw new ReflectException("No such class: " + command, exc);
      }
      setVar(varName, c);
      return null;
    }

    // get variable where results of command should be stored
    int eqIndex = command.indexOf("=");
    String target = null;
    if (eqIndex >= 0) {
      target = command.substring(0, eqIndex).trim();
      command = command.substring(eqIndex + 1).trim();
    }

    Object result = null;

    // parse parentheses
    int leftParen = command.indexOf("(");
    if (leftParen < 0) {
      // command is a simple assignment
      result = getVar(command);
      if (target != null) setVar(target, result);
      return result;
    }
    else if (leftParen != command.lastIndexOf("(") ||
      command.indexOf(")") != command.length() - 1)
    {
      throw new ReflectException("Invalid parentheses");
    }

    // parse arguments
    String arglist = command.substring(leftParen + 1);
    StringTokenizer st = new StringTokenizer(arglist, "(,)");
    int len = st.countTokens();
    Object[] args = new Object[len];
    for (int i=0; i<len; i++) {
      String arg = st.nextToken().trim();
      args[i] = getVar(arg);
    }
    command = command.substring(0, leftParen);

    if (command.startsWith("new ")) {
      // command is a constructor call
      String className = command.substring(4).trim();
      Object var = getVar(className);
      if (var == null) {
        throw new ReflectException("Class not found: " + className);
      }
      else if (!(var instanceof Class)) {
        throw new ReflectException("Not a class: " + className);
      }
      Class cl = (Class) var;

      // Search for a constructor that matches the arguments. Unfortunately,
      // calling cl.getConstructor(argClasses) does not work, because
      // getConstructor() is not flexible enough to detect when the arguments
      // are subclasses of the constructor argument classes, making a brute
      // force search through all public constructors necessary.
      Constructor constructor = null;
      Constructor[] c = cl.getConstructors();
      for (int i=0; i<c.length; i++) {
        if (force) c[i].setAccessible(true);
        Class[] params = c[i].getParameterTypes();
        if (params.length == args.length) {
          boolean match = true;
          for (int j=0; j<params.length; j++) {
            if (!isInstance(params[j], args[j])) {
              match = false;
              break;
            }
          }
          if (match) {
            constructor = c[i];
            break;
          }
        }
      }
      if (constructor == null) {
        StringBuffer sb = new StringBuffer(command);
        for (int i=0; i<args.length; i++) {
          sb.append(i == 0 ? "(" : ", ");
          sb.append(args[i].getClass().getName());
        }
        sb.append(")");
        throw new ReflectException("No such constructor: " + sb.toString());
      }

      // invoke constructor
      Exception exc = null;
      try { result = constructor.newInstance(args); }
      catch (InstantiationException e) { exc = e; }
      catch (IllegalAccessException e) { exc = e; }
      catch (InvocationTargetException e) { exc = e; }
      if (exc != null) {
        if (debug) LogTools.trace(exc);
        throw new ReflectException("Cannot instantiate object", exc);
      }
    }
    else {
      // command is a method call
      int dot = command.indexOf(".");
      if (dot < 0) throw new ReflectException("Syntax error");
      String varName = command.substring(0, dot).trim();
      String methodName = command.substring(dot + 1).trim();
      Object var = getVar(varName);
      if (var == null) {
        throw new ReflectException("No such variable: " + varName);
      }
      Class varClass = var instanceof Class ? (Class) var : var.getClass();

      // Search for a method that matches the arguments. Unfortunately,
      // calling varClass.getMethod(methodName, argClasses) does not work,
      // because getMethod() is not flexible enough to detect when the
      // arguments are subclasses of the method argument classes, making a
      // brute force search through all public methods necessary.
      Method method = null;
      Method[] m = varClass.getMethods();
      for (int i=0; i<m.length; i++) {
        if (force) m[i].setAccessible(true);
        if (methodName.equals(m[i].getName())) {
          Class[] params = m[i].getParameterTypes();
          if (params.length == args.length) {
            boolean match = true;
            for (int j=0; j<params.length; j++) {
              if (!isInstance(params[j], args[j])) {
                match = false;
                break;
              }
            }
            if (match) {
              method = m[i];
              break;
            }
          }
        }
      }
      if (method == null) {
        throw new ReflectException("No such method: " + methodName);
      }

      // invoke method
      Exception exc = null;
      try { result = method.invoke(var, args); }
      catch (IllegalAccessException e) { exc = e; }
      catch (InvocationTargetException e) { exc = e; }
      if (exc != null) {
        if (debug) LogTools.trace(exc);
        throw new ReflectException("Cannot execute method: " + methodName, exc);
      }
    }

    // assign result to proper variable
    if (target != null) setVar(target, result);
    return result;
  }

  /** Registers a variable in the universe. */
  public void setVar(String varName, Object obj) {
    if (obj == null) variables.remove(varName);
    else variables.put(varName, obj);
  }

  /** Registers a variable of primitive type boolean in the universe. */
  public void setVar(String varName, boolean b) {
    setVar(varName, new Boolean(b));
  }

  /** Registers a variable of primitive type byte in the universe. */
  public void setVar(String varName, byte b) {
    setVar(varName, new Byte(b));
  }

  /** Registers a variable of primitive type char in the universe. */
  public void setVar(String varName, char c) {
    setVar(varName, new Character(c));
  }

  /** Registers a variable of primitive type double in the universe. */
  public void setVar(String varName, double d) {
    setVar(varName, new Double(d));
  }

  /** Registers a variable of primitive type float in the universe. */
  public void setVar(String varName, float f) {
    setVar(varName, new Float(f));
  }

  /** Registers a variable of primitive type int in the universe. */
  public void setVar(String varName, int i) {
    setVar(varName, new Integer(i));
  }

  /** Registers a variable of primitive type long in the universe. */
  public void setVar(String varName, long l) {
    setVar(varName, new Long(l));
  }

  /** Registers a variable of primitive type short in the universe. */
  public void setVar(String varName, short s) {
    setVar(varName, new Short(s));
  }

  /**
   * Returns the value of a variable or field in the universe.
   * Primitive types will be wrapped in their Java Object wrapper classes.
   */
  public Object getVar(String varName) throws ReflectException {
    if (varName.equals("null")) {
      // variable is a null value
      return null;
    }
    else if (varName.equals("true")) {
      // variable is a boolean literal
      return new Boolean(true);
    }
    else if (varName.equals("false")) {
      // variable is a boolean literal
      return new Boolean(false);
    }
    else if (varName.startsWith("\"") && varName.endsWith("\"")) {
      // variable is a string literal
      return varName.substring(1, varName.length() - 1);
    }
    try {
      if (varName.matches("-?\\d+")) {
        // variable is an int literal
        return new Integer(varName);
      }
      else if (varName.matches("-?\\d+L")) {
        // variable is a long literal
        return new Long(varName);
      }
      else if (varName.matches("-?\\d*\\.\\d*")) {
        // variable is a double literal
        return new Double(varName);
      }
    }
    catch (NumberFormatException exc) {
      throw new ReflectException("Invalid literal: " + varName, exc);
    }
    int dot = varName.indexOf(".");
    if (dot >= 0) {
      // get field value of variable
      String className = varName.substring(0, dot).trim();
      Object var = variables.get(className);
      if (var == null) {
        throw new ReflectException("No such class: " + className);
      }
      Class varClass = var instanceof Class ? (Class) var : var.getClass();
      String fieldName = varName.substring(dot + 1).trim();
      Field field;
      try {
        field = varClass.getField(fieldName);
        if (force) field.setAccessible(true);
      }
      catch (NoSuchFieldException exc) {
        if (debug) LogTools.trace(exc);
        throw new ReflectException("No such field: " + varName, exc);
      }
      Object fieldVal;
      try { fieldVal = field.get(var); }
      catch (IllegalAccessException exc) {
        if (debug) LogTools.trace(exc);
        throw new ReflectException("Cannot get field value: " + varName, exc);
      }
      return fieldVal;
    }
    else {
      // get variable
      Object var = variables.get(varName);
      return var;
    }
  }

  /** Sets whether access modifiers (protected, private, etc.) are ignored. */
  public void setAccessibilityIgnored(boolean ignore) { force = ignore; }

  /** Gets whether access modifiers (protected, private, etc.) are ignored. */
  public boolean isAccessibilityIgnored() { return force; }

  /** Enables or disables extended debugging output. */
  public void setDebug(boolean debug) { this.debug = debug; }

  /** Gets whether extended debugging output is enabled. */
  public boolean isDebug() { return debug; }

  // -- Main method --

  /**
   * Allows exploration of a reflected universe in an interactive environment.
   */
  public static void main(String[] args) throws IOException {
    ReflectedUniverse r = new ReflectedUniverse();
    LogTools.println("Reflected universe test environment. " +
      "Type commands, or press ^D to quit.");
    if (args.length > 0) {
      r.setAccessibilityIgnored(true);
      LogTools.println("Ignoring accessibility modifiers.");
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      LogTools.print("> ");
      String line = in.readLine();
      if (line == null) break;
      try { r.exec(line); }
      catch (ReflectException exc) { LogTools.trace(exc); }
    }
    LogTools.println();
  }

}
