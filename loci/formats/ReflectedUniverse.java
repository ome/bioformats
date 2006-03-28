//
// ReflectedUniverse.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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
import java.net.*;
import java.util.*;

/** A general-purpose reflection wrapper class. */
public class ReflectedUniverse {

  // -- Fields --

  /** Hashtable containing all variables present in the universe. */
  protected Hashtable variables;

  /** Class loader for imported classes. */
  protected ClassLoader loader;

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


  // -- API methods --

  /**
   * Executes a command in the universe. The following syntaxes are valid:
   * <li>import fully.qualified.package.ClassName
   * <li>var = new ClassName(param1, ..., paramN)
   * <li>var.method(param1, ..., paramN)
   * <li>var2 = var1.method(param1, ..., paramN)
   * <li>ClassName.method(param1, ..., paramN)
   * <p>
   * Important guidelines:
   * <li>Any referenced class must be imported first using "import".
   * <li>Variables can be exported from the universe with getVar().
   * <li>Variables can be imported to the universe with setVar().
   * <li>Each parameter must be either a variable in the universe
   *     or a static or instance field (i.e., no nested methods).
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
      catch (ClassNotFoundException exc) {
        if (debug) exc.printStackTrace();
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

    // parse parentheses
    int leftParen = command.indexOf("(");
    if (leftParen < 0 || leftParen != command.lastIndexOf("(") ||
      command.indexOf(")") != command.length() - 1)
    {
      throw new ReflectException("invalid parentheses");
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

    Object result;
    if (command.startsWith("new ")) {
      // command is a constructor call
      String className = command.substring(4).trim();
      Object var = getVar(className);
      if (!(var instanceof Class)) {
        throw new ReflectException("not a class: " + className);
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
      try {
        result = constructor.newInstance(args);
      }
      catch (Exception exc) {
        if (debug) exc.printStackTrace();
        throw new ReflectException("Cannot instantiate object", exc);
      }
    }
    else {
      // command is a method call
      int dot = command.indexOf(".");
      if (dot < 0) throw new ReflectException("syntax error");
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
      try {
        result = method.invoke(var, args);
      }
      catch (Exception exc) {
        if (debug) exc.printStackTrace();
        throw new ReflectException("Cannot execute method: " +
          methodName, exc);
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
    int dot = varName.indexOf(".");
    if (dot >= 0) {
      // get field value of variable
      Object var = variables.get(varName.substring(0, dot).trim());
      Class varClass = var instanceof Class ? (Class) var : var.getClass();
      String fieldName = varName.substring(dot + 1).trim();
      Field field;
      try {
        field = varClass.getField(fieldName);
      }
      catch (NoSuchFieldException exc) {
        if (debug) exc.printStackTrace();
        throw new ReflectException("No such field: " + varName, exc);
      }
      Object fieldVal;
      try {
        fieldVal = field.get(var);
      }
      catch (Exception exc) {
        if (debug) exc.printStackTrace();
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

  /** Enables or disables extended debugging output. */
  public void setDebug(boolean debug) { this.debug = debug; }

  /** Gets whether extended debugging output is enabled. */
  public boolean isDebug() { return debug; }

}
