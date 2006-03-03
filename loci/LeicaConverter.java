// LeicaConverter.java

// Coded on 2006 Mar 3 by Curtis Rueden, for Julie Simpson.
// Permission is granted to use this code for anything.

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import loci.formats.*;

public class LeicaConverter extends JFrame implements ActionListener {

  private ImageReader reader = new ImageReader();
  private TiffWriter writer = new TiffWriter();
  private JFileChooser rc = reader.getFileChooser();
  private JFileChooser wc = writer.getFileChooser();

  private JTextField input;
  private JTextField output;

  public LeicaConverter() {
    super("Leica Converter");

    JPanel pane = new JPanel();
    pane.setBorder(new EmptyBorder(5, 5, 5, 5));
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    setContentPane(pane);

    JPanel row1 = new JPanel();
    row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
    pane.add(row1);

    JLabel inputLabel = new JLabel("Input file(s)");
    input = new JTextField();
    input.setColumns(25);
    input.setMaximumSize(
      new Dimension(Integer.MAX_VALUE, input.getPreferredSize().height));
    JButton inputButton = new JButton("Choose");
    inputButton.setActionCommand("input");
    inputButton.addActionListener(this);

    row1.add(inputLabel);
    row1.add(Box.createHorizontalStrut(3));
    row1.add(input);
    row1.add(Box.createHorizontalStrut(3));
    row1.add(inputButton);

    JPanel row2 = new JPanel();
    row2.setBorder(new EmptyBorder(5, 0, 5, 0));
    row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
    pane.add(row2);

    JLabel outputLabel = new JLabel("Output file");
    outputLabel.setPreferredSize(inputLabel.getPreferredSize());
    output = new JTextField();
    output.setColumns(25);
    output.setMaximumSize(
      new Dimension(Integer.MAX_VALUE, output.getPreferredSize().height));
    JButton outputButton = new JButton("Choose");
    outputButton.setActionCommand("output");
    outputButton.addActionListener(this);

    row2.add(outputLabel);
    row2.add(Box.createHorizontalStrut(3));
    row2.add(output);
    row2.add(Box.createHorizontalStrut(3));
    row2.add(outputButton);

    JPanel row3 = new JPanel();
    row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
    pane.add(row3);

    JButton convert = new JButton("Convert");
    convert.setActionCommand("convert");
    convert.addActionListener(this);
    JButton quit = new JButton("Quit");
    quit.setActionCommand("quit");
    quit.addActionListener(this);

    row3.add(Box.createHorizontalGlue());
    row3.add(convert);
    row3.add(Box.createHorizontalStrut(3));
    row3.add(quit);
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("input".equals(cmd)) {
      int rval = rc.showOpenDialog(this);
      if (rval != JFileChooser.APPROVE_OPTION) return;
      File file = rc.getSelectedFile();
      input.setText(file.getPath());
    }
    else if ("output".equals(cmd)) {
      int rval = wc.showSaveDialog(this);
      if (rval != JFileChooser.APPROVE_OPTION) return;
      File file = wc.getSelectedFile();
      output.setText(file.getPath());
    }
    else if ("convert".equals(cmd)) {
      // TODO
    }
    else if ("quit".equals(cmd)) {
      hide();
      dispose();
    }
  }

  public static void main(String[] args) {
    LeicaConverter lc = new LeicaConverter();
    lc.pack();
    lc.show();
  }

}
