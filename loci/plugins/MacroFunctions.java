//
// MacroFunctions.java
//

package loci.plugins;

import ij.IJ;
import ij.macro.Functions;
import ij.macro.MacroExtension;
import ij.macro.ExtensionDescriptor;
import ij.plugin.PlugIn;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MacroFunctions implements PlugIn, MacroExtension {

  // -- Fields --

  protected ExtensionDescriptor[] extensions = buildExtensions();

  // -- PlugIn API methods --

  public void run(String arg) {
    if (IJ.versionLessThan("1.39c")) return;
    if (!IJ.macroRunning()) {
      IJ.error("Cannot install extensions from outside a macro.");
      return;
    }
    Functions.registerExtensions(this);
  }

  // -- MacroExtension API methods --

  public ExtensionDescriptor[] getExtensionFunctions() {
    return extensions;
  }

  public String handleExtension(String name, Object[] args) {
    Class[] c = null;
    if (args != null) {
      c = new Class[args.length];
      for (int i=0; i<args.length; i++) c[i] = args[i].getClass();
    }
    try {
      getClass().getMethod(name, c).invoke(this, args);
    }
    catch (NoSuchMethodException exc) { exc.printStackTrace(); }
    catch (IllegalAccessException exc) { exc.printStackTrace(); }
    catch (InvocationTargetException exc) { exc.printStackTrace(); }
    return null;
  }

  // -- Helper methods --

  /**
   * Builds the list of extensions, using reflection,
   * from public methods of this class.
   */
  protected ExtensionDescriptor[] buildExtensions() {
    Method[] m = getClass().getMethods();
    ExtensionDescriptor[] desc = new ExtensionDescriptor[m.length];
    for (int i=0; i<m.length; i++) {
      Class[] c = m[i].getParameterTypes();
      int[] types = new int[c.length];
      for (int j=0; j<c.length; j++) {
        if (c[j] == String.class) types[j] = ARG_STRING;
        else if (c[j] == Double.class) types[j] = ARG_NUMBER;
        else if (c[j] == Object[].class) types[j] = ARG_ARRAY;
        else if (c[j] == String[].class) types[j] = ARG_OUTPUT + ARG_STRING;
        else if (c[j] == Double[].class) types[j] = ARG_OUTPUT + ARG_NUMBER;
      }
      desc[i] = ExtensionDescriptor.newDescriptor(m[i].getName(), this, types);
    }
    return desc;
  }

}
