// LeicaConverter.java

// Coded 2006 Mar 3-4 by Curtis Rueden, for Julie Simpson.
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

  private JTextField input, output;
  private JRadioButton rgb, leica;
  private JProgressBar progress;

  public LeicaConverter() {
    super("Leica Converter");
    setLocation(100, 100);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel pane = new JPanel();
    pane.setBorder(new EmptyBorder(10, 10, 10, 10));
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

    JLabel formatLabel = new JLabel("Output format");
    inputLabel.setPreferredSize(formatLabel.getPreferredSize());
    outputLabel.setPreferredSize(formatLabel.getPreferredSize());
    rgb = new JRadioButton("merged RGB", true);
    leica = new JRadioButton("split Leica");
    ButtonGroup group = new ButtonGroup();
    group.add(rgb);
    group.add(leica);

    row3.add(formatLabel);
    row3.add(Box.createHorizontalStrut(3));
    row3.add(rgb);
    row3.add(Box.createHorizontalStrut(3));
    row3.add(leica);
    row3.add(Box.createHorizontalGlue());

    JPanel row4 = new JPanel();
    row4.setLayout(new BoxLayout(row4, BoxLayout.X_AXIS));
    pane.add(Box.createVerticalStrut(10));
    pane.add(row4);

    progress = new JProgressBar();
    progress.setStringPainted(true);
    progress.setString("");
    JButton convert = new JButton("Convert");
    convert.setActionCommand("convert");
    convert.addActionListener(this);
    JButton quit = new JButton("Quit");
    quit.setActionCommand("quit");
    quit.addActionListener(this);

    row4.add(progress);
    row4.add(Box.createHorizontalStrut(3));
    row4.add(convert);
    row4.add(Box.createHorizontalStrut(3));
    row4.add(quit);
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
      String s = file.getPath();
      if (!writer.isThisType(s)) s += ".tif";
      output.setText(s);
    }
    else if ("convert".equals(cmd)) {
      // TODO
    }
    else if ("quit".equals(cmd)) {
      setVisible(false);
      dispose();
    }
  }

  public static void main(String[] args) {
    LeicaConverter lc = new LeicaConverter();
    lc.pack();
    lc.show();
  }

}
