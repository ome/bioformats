//
// SystemControls.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import com.jgoodies.plaf.LookUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;

import loci.visbio.util.LAFUtil;
import loci.visbio.util.VisUtil;

import visad.VisADException;

import visad.data.qt.QTForm;

import visad.util.ReflectedUniverse;
import visad.util.Util;

/** SystemControls is the control panel for reporting system information. */
public class SystemControls extends ControlPanel
  implements ActionListener, Runnable
{

  // -- GUI components --

  /** Memory usage text field. */
  private JTextField memField;


  // -- Fields --

  /** Current memory usage in megabytes. */
  protected long memUsed;

  /** Current memory total in megabytes. */
  protected long memTotal;


  // -- Constructor --

  /** Constructs a control panel for viewing system information. */
  public SystemControls(LogicManager logic) {
    super(logic, "System", "Reports system information");
    VisBioFrame bio = lm.getVisBio();

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
    clean.setMnemonic('c');
    clean.setToolTipText(
      "Calls the Java garbage collector to free wasted memory");
    clean.setActionCommand("clean");
    clean.addActionListener(this);

    // memory maximum text field
    JTextField heapField = new JTextField(getMaximumMemory() + " MB maximum");
    heapField.setEditable(false);

    // memory maximum alteration button
    JButton heap = new JButton("Change");
    heap.setMnemonic('a');
    heap.setToolTipText(
      "Edits the maximum amount of memory available to VisBio");
    heap.setActionCommand("heap");
    heap.addActionListener(this);

    // Java3D library text field
    JTextField java3dField = new JTextField(
      getVersionString("javax.vecmath.Point3d"));
    java3dField.setEditable(false);

    // JPEG library text field
    JTextField jpegField = new JTextField(
      getVersionString("com.sun.image.codec.jpeg.JPEGCodec"));
    jpegField.setEditable(false);

    // QuickTime library text field
    String qtVersion = null;
    try {
      ReflectedUniverse r = QTForm.getUniverse();
      String qtMajor = r.exec("QTSession.getMajorVersion()").toString();
      String qtMinor = r.exec("QTSession.getMinorVersion()").toString();
      qtVersion = qtMajor + "." + qtMinor;
    }
    catch (VisADException exc) { qtVersion = "Missing"; }
    JTextField qtField = new JTextField(qtVersion);
    qtField.setEditable(false);

    // Python library text field
    JTextField pythonField = new JTextField(
      getVersionString("org.python.util.PythonInterpreter"));
    pythonField.setEditable(false);

    // JAI library text field
    JTextField jaiField = new JTextField(
      getVersionString("javax.media.jai.JAI"));
    jaiField.setEditable(false);

    // Look & Feel text field
    JTextField lafField = new JTextField(LAFUtil.getLookAndFeel()[0]);
    lafField.setEditable(false);

    // Look & Feel alteration button
    JButton laf = new JButton("Change");
    laf.setMnemonic('n');
    laf.setToolTipText("Edits VisBio's graphical Look & Feel");
    laf.setActionCommand("laf");
    laf.addActionListener(this);

    // Stereo configuration text field
    JTextField stereoField = new JTextField(
      VisUtil.getStereoConfiguration() == null ? "Not available" : "Enabled");
    stereoField.setEditable(false);

    // lay out components
    FormLayout layout = new FormLayout(
      "right:pref, 3dlu, pref:grow, 3dlu, pref",
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 9dlu, " +
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, " +
      "pref, 9dlu, pref, 3dlu, pref, 3dlu, pref");
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setDefaultDialogBorder();
    CellConstraints cc = new CellConstraints();

    builder.addSeparator("Properties", cc.xyw(1, 1, 5));

    builder.addLabel("&Operating system", cc.xy(1, 3)).setLabelFor(osField);
    builder.add(osField, cc.xyw(3, 3, 3));

    builder.addLabel("&Java version", cc.xy(1, 5)).setLabelFor(javaField);
    builder.add(javaField, cc.xyw(3, 5, 3));

    builder.addLabel("&Memory usage", cc.xy(1, 7)).setLabelFor(memField);
    builder.add(memField, cc.xy(3, 7));
    builder.add(clean, cc.xy(5, 7));

    builder.addLabel("&Memory maximum", cc.xy(1, 9)).setLabelFor(heapField);
    builder.add(heapField, cc.xy(3, 9));
    builder.add(heap, cc.xy(5, 9));

    builder.addSeparator("Libraries", cc.xyw(1, 11, 5));

    builder.addLabel("Java&3D", cc.xy(1, 13)).setLabelFor(java3dField);
    builder.add(java3dField, cc.xyw(3, 13, 3));

    builder.addLabel("JPE&G", cc.xy(1, 15)).setLabelFor(jpegField);
    builder.add(jpegField, cc.xyw(3, 15, 3));

    builder.addLabel("&QuickTime", cc.xy(1, 17)).setLabelFor(qtField);
    builder.add(qtField, cc.xyw(3, 17, 3));

    builder.addLabel("&Python", cc.xy(1, 19)).setLabelFor(pythonField);
    builder.add(pythonField, cc.xyw(3, 19, 3));

    builder.addLabel("JA&I", cc.xy(1, 21)).setLabelFor(jaiField);
    builder.add(jaiField, cc.xyw(3, 21, 3));

    builder.addSeparator("Configuration", cc.xyw(1, 23, 5));

    builder.addLabel("&Look && Feel", cc.xy(1, 25)).setLabelFor(lafField);
    builder.add(lafField, cc.xy(3, 25));
    builder.add(laf, cc.xy(5, 25));

    builder.addLabel("&Stereo", cc.xy(1, 27)).setLabelFor(stereoField);
    builder.add(stereoField, cc.xyw(3, 27, 3));

    controls.add(builder.getPanel());

    // update system information twice per second
    Timer t = new Timer(500, this);
    t.start();
  }


  // -- SystemControls API methods --

  /** Gets maximum amount of memory available to VisBio in megabytes. */
  public int getMaximumMemory() {
    // HACK: 8 MB seem to be reserved...
    return (int) (Runtime.getRuntime().maxMemory() / 1048376) + 8;
  }


  // -- ActionListener API methods --

  /** Handles action events. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("clean".equals(cmd)) Util.invoke(false, this);
    else if ("heap".equals(cmd)) {
      String max = "" + getMaximumMemory();
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
      writeScript(maxHeap, LAFUtil.getLookAndFeel()[1]);
    }
    else if ("laf".equals(cmd)) {
      String[] laf = LAFUtil.getLookAndFeel();
      final String[][] lafs = LAFUtil.getAvailableLookAndFeels();
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
      if (ndx < 0 || lafs[1][ndx].equals(laf[1])) return;
      writeScript(getMaximumMemory(), lafs[1][ndx]);
    }
    else {
      // update system information
      if (!lm.getVisBio().isVisible()) return;
      long total = Runtime.getRuntime().totalMemory();
      long free = Runtime.getRuntime().freeMemory();
      long used = total - free;
      long umeg = used >> 20;
      long tmeg = total >> 20;
      if (memUsed != umeg || memTotal != tmeg) {
        memUsed = umeg;
        memTotal = tmeg;
        memField.setText(umeg + " MB used (" + tmeg + " MB reserved)");
      }
    }
  }


  // -- Runnable API methods --

  /** Performs garbage collection, displaying a wait cursor while doing so. */
  public void run() {
    WindowManager wm = (WindowManager)
      lm.getVisBio().getManager(WindowManager.class);
    wm.setWaitCursor(true);
    SystemManager.gc();
    wm.setWaitCursor(false);
  }


  // -- Helper methods --

  /** Gets version information for the specified class. */
  private String getVersionString(String clas) {
    Class c = null;
    try { c = Class.forName(clas); }
    catch (ClassNotFoundException exc) { c = null; }
    return getVersionString(c);
  }

  /** Gets version information for the specified class. */
  private String getVersionString(Class c) {
    if (c == null) return "Missing";
    Package p = c.getPackage();
    if (p == null) return "No package";
    String vendor = p.getImplementationVendor();
    String version = p.getImplementationVersion();
    if (vendor == null && version == null) return "Installed";
    else if (vendor == null) return version;
    else if (version == null) return vendor;
    else return version + " (" + vendor + ")";
  }

  /**
   * Updates the VisBio launch script to specify the given
   * maximum heap and look and feel settings.
   */
  private void writeScript(int heap, String laf) {
    // a platform-dependent mess!
    String filename;
    if (LookUtils.IS_OS_WINDOWS) filename = "VisBio.lnk";
    else if (LookUtils.IS_OS_MAC) filename = "VisBio.app/Contents/Info.plist";
    else filename = "visbio";

    // read in the VisBio startup script
    Vector lines = new Vector();
    try {
      BufferedReader fin = new BufferedReader(new FileReader(filename));
      while (true) {
        String line = fin.readLine();
        if (line == null) break;
        lines.add(line);
      }
      fin.close();
    }
    catch (IOException exc) { exc.printStackTrace(); }

    // alter settings in VisBio startup script
    PrintWriter fout = null;
    int size = 0;
    try {
      fout = new PrintWriter(new FileWriter(filename));
      size = lines.size();
    }
    catch (IOException exc) { exc.printStackTrace(); }

    boolean heapChanged = false, lafChanged = false;

    for (int i=0; i<size; i++) {
      String line = (String) lines.elementAt(i);

      // check for maximum heap setting
      String heapString = "mx";
      int heapPos = line.indexOf(heapString);
      if (heapPos >= 0) {
        int space = line.indexOf(" ", heapPos);
        if (space >= 0) {
          line = line.substring(0, heapPos + heapString.length()) +
            heap + "m" + line.substring(space);
          heapChanged = true;
        }
      }

      // check for L&F setting
      String lafString = "swing.defaultlaf=";
      int lafPos = line.indexOf(lafString);
      if (lafPos >= 0) {
        int space = line.indexOf(" ", lafPos);
        if (space >= 0) {
          line = line.substring(0, lafPos + lafString.length()) +
            laf + line.substring(space);
          lafChanged = true;
        }
      }

      fout.println(line);
    }
    fout.close();

    if (!heapChanged) {
      System.err.println("Warning: no maximum heap setting found " +
        "in launch script " + filename + ".");
    }
    if (!lafChanged) {
      System.err.println("Warning: no Look & Feel setting found " +
        "in launch script " + filename + ".");
    }

    JOptionPane.showMessageDialog(this,
      "The change will take effect next time VisBio is run.",
      "VisBio", JOptionPane.INFORMATION_MESSAGE);
  }

}
