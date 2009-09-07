//
// TestMem.java
//

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** A little GUI for testing Java memory behavior. */
@SuppressWarnings("serial")
public class TestMem extends JFrame implements ActionListener {
  private static final int MEM = 32 * 1024 * 1024; // 32 MB

  @SuppressWarnings("unused")
  private byte[] data;
  private JLabel memTotal, memMax, memFree, memUsed;

  public TestMem() {
    super("TestMem");
    JPanel pane = new JPanel();
    setContentPane(pane);
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    pane.setBorder(new EmptyBorder(10, 10, 10, 10));

    JPanel buttons = new JPanel();
    buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

    JButton malloc = new JButton("malloc");
    JButton free = new JButton("free");

    malloc.setToolTipText("Allocates 32 MB onto the heap.");
    free.setToolTipText("Frees the allocated memory and garbage collects.");

    malloc.setActionCommand("malloc");
    free.setActionCommand("free");
    malloc.addActionListener(this);
    free.addActionListener(this);

    buttons.add(malloc);
    buttons.add(free);

    memTotal = new JLabel();
    memMax = new JLabel();
    memFree = new JLabel();
    memUsed = new JLabel();

    memTotal.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    memMax.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    memFree.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    memUsed.setAlignmentX(JLabel.CENTER_ALIGNMENT);

    refreshLabels();

    add(buttons);
    add(Box.createVerticalStrut(15));
    add(memTotal);
    add(memMax);
    add(memFree);
    add(memUsed);

    new Timer(50, this).start();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("malloc".equals(cmd)) {
      // allocate lots of memory
      data = new byte[MEM];
    }
    else if ("free".equals(cmd)) {
      // free reference & garbage collect
      data = null;
      System.gc();
    }
    else {
      // timer event -- update memory usage display
      refreshLabels();
    }
  }

  private void refreshLabels() {
    Runtime r = Runtime.getRuntime();
    long total = r.totalMemory();
    long max = r.maxMemory();
    long free = r.freeMemory();
    long used = total - free;
    memTotal.setText("Total: " + (total / 1024) + " KB");
    memMax.setText("Max: " + (max / 1024) + " KB");
    memFree.setText("Free: " + (free / 1024) + " KB");
    memUsed.setText("Used: " + (used / 1024) + " KB");
  }

  public static void main(String[] args) {
    new TestMem();
  }

}
