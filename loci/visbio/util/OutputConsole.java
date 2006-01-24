//
// OutputConsole.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2006 Curtis Rueden.

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

package loci.visbio.util;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import visad.util.Util;

/**
 * OutputConsole provides an output stream that pipes to a JTextArea
 * Swing component in its own frame (instead of to a console window).
 */
public class OutputConsole extends OutputStream {

  // -- Constants --

  /** Monospaced font. */
  private static final Font MONO = new Font("Monospaced", Font.PLAIN, 11);


  // -- Fields --

  private JFrame frame;
  private Document doc;
  private JTextArea area;
  private String log;
  private Vector listeners;


  // -- Constructors --

  /** Constructs a new instance of OutputConsole. */
  public OutputConsole(String title) { this(title, null); }

  /**
   * Constructs a new instance of OutputConsole,
   * logging all output to the given log file.
   */
  public OutputConsole(String title, String logFile) {
    super();
    log = logFile;
    if (log != null && new File(log).exists()) {
      // clear log file content
      File f = new File(log);
      if (f.exists()) {
        try { f.delete(); }
        catch (SecurityException exc) {
          // delete access is denied; try clearing file contents
          try { new FileWriter(log).close(); }
          catch (IOException exc2) { }
        }
      }
    }
    frame = new JFrame(title);

    JPanel pane = new JPanel();
    pane.setLayout(new BorderLayout());
    frame.setContentPane(pane);

    area = new JTextArea(25, 100);
    area.setEditable(false);
    area.setFont(MONO);
    area.setLineWrap(true);
    JScrollPane scroll = new JScrollPane(area);
    SwingUtil.configureScrollPane(scroll);
    pane.add(scroll, BorderLayout.CENTER);
    doc = area.getDocument();
    listeners = new Vector();

    frame.pack();
  }


  // -- OutputConsole API methods --

  public void show() {
    if (!frame.isVisible()) Util.centerWindow(frame);
    frame.setVisible(true);
  }

  public JFrame getWindow() { return frame; }

  public void addOutputListener(OutputListener l) {
    synchronized (listeners) { listeners.addElement(l); }
  }

  public void removeOutputListener(OutputListener l) {
    synchronized (listeners) { listeners.removeElement(l); }
  }

  public void removeAllOutputListeners() {
    synchronized (listeners) { listeners.removeAllElements(); }
  }

  public void notifyListeners(OutputEvent e) {
    synchronized (listeners) {
      int size = listeners.size();
      for (int i=0; i<size; i++) {
        ((OutputListener) listeners.elementAt(i)).outputProduced(e);
      }
    }
  }


  // -- OutputStream API methods --

  public void write(int b) throws IOException {
    write(new byte[] {(byte) b}, 0, 1);
  }

  public void write(byte[] b, int off, int len) throws IOException {
    final OutputConsole out = this;
    final String s = new String(b, off, len);
    Util.invoke(false, new Runnable() {
      public void run() {
        try {
          if (log != null) {
            PrintWriter fout = new PrintWriter(new FileWriter(log, true));
            fout.print(s);
            fout.close();
          }
          doc.insertString(doc.getLength(), s, null);
        }
        catch (BadLocationException exc) { }
        catch (IOException exc) { }
        notifyListeners(new OutputEvent(out));
      }
    });
  }

}
