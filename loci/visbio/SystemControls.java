//
// SystemControls.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio;

import com.jgoodies.plaf.LookUtils;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Properties;
import javax.swing.*;
import loci.formats.*;
import loci.visbio.ext.MatlabUtil;
import loci.visbio.util.*;
import visad.util.Util;

/**
 * SystemControls is the control panel for reporting system information.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/SystemControls.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/SystemControls.java">SVN</a></dd></dl>
 */
public class SystemControls extends ControlPanel implements ActionListener {

  // -- GUI components --

  /** Memory usage text field. */
  private JTextField memField;

  /** Look &amp; Feel text field. */
  private JTextField lafField;

  // -- Fields --

  /** Current memory usage. */
  protected String memUsage;

  // -- Constructor --

  /** Constructs a control panel for viewing system information. */
  public SystemControls(LogicManager logic) {
    super(logic, "System", "Reports system information");
    VisBioFrame bio = lm.getVisBio();
    SystemManager sm = (SystemManager) lm;

    // dump properties button
    JButton dump = new JButton("Dump all");
    if (!LAFUtil.isMacLookAndFeel()) dump.setMnemonic('d');
    dump.setToolTipText("Dumps system property values to the output console");
    dump.setActionCommand("dump");
    dump.addActionListener(this);

    // operating system text field
    JTextField osField = new JTextField(System.getProperty("os.name") +
      " (" + System.getProperty("os.arch") + ")");
    osField.setEditable(false);

    // java version text field
    JTextField javaField = new JTextField(System.getProperty("java.version") +
      " (" + System.getProperty("java.vendor") + ")");
    javaField.setEditable(false);

    // memory usage text field
    memField = new JTextField("xxxx MB used (xxxx MB reserved)");
    memField.setEditable(false);

    // garbage collection button
    JButton clean = new JButton("Clean");
    if (!LAFUtil.isMacLookAndFeel()) clean.setMnemonic('c');
    clean.setToolTipText(
      "Calls the Java garbage collector to free wasted memory");
    clean.setActionCommand("clean");
    clean.addActionListener(this);

    // memory maximum text field
    JTextField heapField = new JTextField(
      sm.getMaximumMemory() + " MB maximum");
    heapField.setEditable(false);

    // memory maximum alteration button
    JButton heap = new JButton("Change...");
    if (!LAFUtil.isMacLookAndFeel()) heap.setMnemonic('a');
    if (sm.isJNLP()) heap.setEnabled(false);
    heap.setToolTipText(
      "Edits the maximum amount of memory available to VisBio");
    heap.setActionCommand("heap");
    heap.addActionListener(this);

    // Java3D library text field
    String j3dVersion = getVersionString("javax.vecmath.Point3d");
    JTextField java3dField = new JTextField(j3dVersion);
    java3dField.setEditable(false);

    // QuickTime library text field
    String qtVersion = new LegacyQTTools().getQTVersion();
    JTextField qtField = new JTextField(qtVersion);
    qtField.setEditable(false);

    // Python library text field
    JTextField pythonField = new JTextField(
      getVersionString("org.python.util.PythonInterpreter"));
    pythonField.setEditable(false);

    // MATLAB library text field
    String matlabVersion = MatlabUtil.getMatlabVersion();
    JTextField matlabField = new JTextField(
      matlabVersion == null ? "Missing" : matlabVersion);
    matlabField.setEditable(false);

    // Look & Feel text field
    lafField = new JTextField(LAFUtil.getLookAndFeel()[0]);
    lafField.setEditable(false);

    // Look & Feel alteration button
    JButton laf = new JButton("Change...");
    if (!LAFUtil.isMacLookAndFeel()) laf.setMnemonic('n');
    if (sm.isJNLP()) laf.setEnabled(false);
    laf.setToolTipText("Edits VisBio's graphical Look & Feel");
    laf.setActionCommand("laf");
    laf.addActionListener(this);

    // Renderer text field
    boolean j3d = j3dVersion != null && !j3dVersion.equals("Missing");
    // HACK - Util.canDoJava3D("1.3.2") does not work as expected
    //boolean j3d132 = Util.canDoJava3D("1.3.2");
    int ndx = j3dVersion.indexOf(" ");
    if (ndx < 0) ndx = j3dVersion.length();
    boolean j3d132 = Util.canDoJava3D("1.4") ||
      j3dVersion.substring(0, ndx).equals("1.3.2");
    boolean j3dWin132 = LookUtils.IS_OS_WINDOWS && j3d132;
    String rend = j3d ?  (j3dWin132 ? getJ3DString() : "Java3D") : "Java2D";
    JTextField renderField = new JTextField(rend);
    renderField.setEditable(false);

    // Renderer alteration button
    JButton render = new JButton("Change...");
    if (!LAFUtil.isMacLookAndFeel()) render.setMnemonic('g');
    if (!j3dWin132) render.setEnabled(false);
    render.setToolTipText("Changes the renderer used for visualization");
    render.setActionCommand("render");
    render.addActionListener(this);

    // Stereo configuration text field
    JTextField stereoField = new JTextField(
      DisplayUtil.getStereoConfiguration() == null ?
      "Not available" : "Enabled");
    stereoField.setEditable(false);

    // lay out components
    FormLayout layout = new FormLayout(
      "right:pref, 3dlu, pref:grow, 3dlu, pref",
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 9dlu, " +
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 9dlu, " +
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref");
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setDefaultDialogBorder();
    CellConstraints cc = new CellConstraints();
    int row = 1;

    builder.addSeparator("Properties", cc.xyw(1, row, 3));
    builder.add(dump, cc.xy(5, row));
    row += 2;
    builder.addLabel("&Operating system", cc.xy(1, row)).setLabelFor(osField);
    builder.add(osField, cc.xyw(3, row, 3));
    row += 2;
    builder.addLabel("&Java version", cc.xy(1, row)).setLabelFor(javaField);
    builder.add(javaField, cc.xyw(3, row, 3));
    row += 2;
    builder.addLabel("Memory &usage", cc.xy(1, row)).setLabelFor(memField);
    builder.add(memField, cc.xy(3, row));
    builder.add(clean, cc.xy(5, row));
    row += 2;
    builder.addLabel("Memory ma&ximum", cc.xy(1, row)).setLabelFor(heapField);
    builder.add(heapField, cc.xy(3, row));
    builder.add(heap, cc.xy(5, row));
    row += 2;
    builder.addSeparator("Libraries", cc.xyw(1, row, 5));
    row += 2;
    builder.addLabel("Java&3D", cc.xy(1, row)).setLabelFor(java3dField);
    builder.add(java3dField, cc.xyw(3, row, 3));
    row += 2;
    builder.addLabel("&QuickTime", cc.xy(1, row)).setLabelFor(qtField);
    builder.add(qtField, cc.xyw(3, row, 3));
    row += 2;
    builder.addLabel("&Python", cc.xy(1, row)).setLabelFor(pythonField);
    builder.add(pythonField, cc.xyw(3, row, 3));
    row += 2;
    builder.addLabel("&MATLAB", cc.xy(1, row)).setLabelFor(matlabField);
    builder.add(matlabField, cc.xyw(3, row, 3));
    row += 2;
    builder.addSeparator("Configuration", cc.xyw(1, row, 5));
    row += 2;
    builder.addLabel("&Look && Feel", cc.xy(1, row)).setLabelFor(lafField);
    builder.add(lafField, cc.xy(3, row));
    builder.add(laf, cc.xy(5, row));
    row += 2;
    builder.addLabel("&Renderer", cc.xy(1, row)).setLabelFor(renderField);
    builder.add(renderField, cc.xy(3, row));
    builder.add(render, cc.xy(5, row));
    row += 2;
    builder.addLabel("&Stereo", cc.xy(1, row)).setLabelFor(stereoField);
    builder.add(stereoField, cc.xyw(3, row, 3));
    row += 2;
    add(builder.getPanel());

    // update system information twice per second
    Timer t = new Timer(500, this);
    t.start();
  }

  // -- ActionListener API methods --

  /** Handles action events. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    SystemManager sm = (SystemManager) lm;
    if ("dump".equals(cmd)) {
      Properties properties = System.getProperties();
      // Properties.list() truncates the property values, so we iterate
      //properties.list(System.out);
      System.out.println("-- listing properties --");
      Enumeration list = properties.propertyNames();
      while (list.hasMoreElements()) {
        String key = (String) list.nextElement();
        String value = properties.getProperty(key);
        System.out.println(key + "=" + value);
      }
    }
    else if ("clean".equals(cmd)) sm.cleanMemory();
    else if ("heap".equals(cmd)) {
      String max = "" + sm.getMaximumMemory();
      String heapSize = (String) JOptionPane.showInputDialog(this,
        "New maximum memory value:", "VisBio", JOptionPane.QUESTION_MESSAGE,
        null, null, "" + max);
      if (heapSize == null || heapSize.equals(max)) return;
      int maxHeap = -1;
      try { maxHeap = Integer.parseInt(heapSize); }
      catch (NumberFormatException exc) { }
      if (maxHeap < 16) {
        JOptionPane.showMessageDialog(this,
          "Maximum memory value must be at least 16 MB.",
          "VisBio", JOptionPane.ERROR_MESSAGE);
        return;
      }
      sm.writeScript(maxHeap, null, null);
      JOptionPane.showMessageDialog(this,
        "The change will take effect next time you run VisBio.",
        "VisBio", JOptionPane.INFORMATION_MESSAGE);
    }
    else if ("laf".equals(cmd)) {
      String[] laf = LAFUtil.getLookAndFeel();
      String[][] lafs = LAFUtil.getAvailableLookAndFeels();
      String lafName = (String) JOptionPane.showInputDialog(this,
        "New Look & Feel:", "VisBio", JOptionPane.QUESTION_MESSAGE,
        null, lafs[0], laf[0]);
      if (lafName == null) return;
      int ndx = -1;
      for (int i=0; i<lafs[0].length; i++) {
        if (lafs[0][i].equals(lafName)) {
          ndx = i;
          break;
        }
      }
      if (ndx < 0 || lafs[1][ndx].equals(laf[1])) return; // cancel or same

      try {
        // update Look and Feel
        UIManager.setLookAndFeel(lafs[1][ndx]);
        WindowManager wm = (WindowManager)
          lm.getVisBio().getManager(WindowManager.class);
        Window[] w = wm.getWindows();
        for (int i=0; i<w.length; i++) {
          SwingUtilities.updateComponentTreeUI(w[i]);
          SwingUtil.repack(w[i]);
        }

        // save change to startup script
        sm.writeScript(-1, lafs[1][ndx], null);

        // update L&F field
        lafField.setText(LAFUtil.getLookAndFeel()[0]);
      }
      catch (ClassNotFoundException exc) { exc.printStackTrace(); }
      catch (IllegalAccessException exc) { exc.printStackTrace(); }
      catch (InstantiationException exc) { exc.printStackTrace(); }
      catch (UnsupportedLookAndFeelException exc) { exc.printStackTrace(); }
    }
    else if ("render".equals(cmd)) {
      String rend = getJ3DString();
      String[] renderers = {"Java3D (OpenGL)", "Java3D (Direct3D)"};
      String[] renderFlags = {"ogl", "d3d"};
      String renderName = (String) JOptionPane.showInputDialog(this,
        "New renderer:", "VisBio", JOptionPane.QUESTION_MESSAGE,
        null, renderers, renderers[0]);
      if (renderName == null) return;
      int ndx = -1;
      for (int i=0; i<renderers.length; i++) {
        if (renderers[i].equals(renderName)) {
          ndx = i;
          break;
        }
      }
      if (ndx < 0 || renderers[ndx].equals(rend)) return; // cancel or same
      sm.writeScript(-1, null, renderFlags[ndx]);
      JOptionPane.showMessageDialog(this,
        "The change will take effect next time you run VisBio.",
        "VisBio", JOptionPane.INFORMATION_MESSAGE);
    }
    else {
      // update system information
      if (!lm.getVisBio().isVisible()) return;
      String mem = ((SystemManager) lm).getMemoryUsage();
      if (!mem.equals(memUsage)) {
        memUsage = mem;
        memField.setText(mem);
      }
    }
  }

  // -- Utility methods --

  /** Gets version information for the specified class. */
  private static String getVersionString(String clas) {
    Class c = null;
    try { c = Class.forName(clas); }
    catch (ClassNotFoundException exc) { c = null; }
    return getVersionString(c);
  }

  /** Gets version information for the specified class. */
  private static String getVersionString(Class c) {
    if (c == null) return "Missing";
    Package p = c.getPackage();
    if (p == null) return "No package";
    String vendor = p.getImplementationVendor();
    String version = p.getImplementationVersion();
    if (vendor == null && version == null) return "Available";
    else if (vendor == null) return version;
    else if (version == null) return vendor;
    else return version + " (" + vendor + ")";
  }

  /** Gets a string representing the Java3D renderer currently in use. */
  private static String getJ3DString() {
    String rend = System.getProperty("j3d.rend");
    return (rend == null || rend.equals("ogl")) ? "Java3D (OpenGL)" :
      (rend.equals("d3d") ? "Java3D (Direct3D)" : "Java3D (" + rend + ")");
  }

}
