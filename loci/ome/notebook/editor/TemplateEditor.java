//
// TemplateEditor.java
//

/*
OME Metadata Notebook application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006-@year@ Christopher Peterson.
  
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

package loci.ome.notebook.editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.*;

/** Graphical application for creating OME Notebook template files. */
public class TemplateEditor extends JFrame implements ActionListener {

  // -- Constants --

  /** Original window width. */
  private static final int WINDOW_WIDTH = 800;

  /** Original window height. */
  private static final int WINDOW_HEIGHT = 700;

  // -- Fields --

  // -- Constructor --

  public TemplateEditor() {
    super("OME Notebook Template Editor"); 
  
    JTabbedPane tabPane = new JTabbedPane();
   
    // set up general options tab

    JScrollPane general = new JScrollPane();
    JPanel content = new JPanel(new GridLayout(10, 2)); 

    // get available fonts
    GraphicsEnvironment local = 
      GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fonts = local.getAvailableFontFamilyNames();

    // prompt for font properties 

    content.add(new JLabel("Font: "));
    JComboBox fontNames = new JComboBox(fonts);
    content.add(fontNames);

    content.add(new JLabel("Font Size: "));
    JSpinner size = new JSpinner(new SpinnerNumberModel(12, 1, 50, 1));
    content.add(size);

    content.add(new JLabel("Font Color: "));
    ImageIcon ii = new ImageIcon();
    BufferedImage font = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
    setPixels(font, 0); 
    ii.setImage(font); 

    JButton fontColor = new JButton("Black", ii);
    fontColor.setActionCommand("fontColor");
    fontColor.addActionListener(this);
    content.add(fontColor);

    // prompt for background color

    content.add(new JLabel("Background Color: "));
    ImageIcon back = new ImageIcon();
    BufferedImage background = new BufferedImage(40, 40, 
      BufferedImage.TYPE_INT_RGB);
    setPixels(background, 16777215);
    back.setImage(background);
    JButton backgroundColor = new JButton("White", back);
    backgroundColor.setActionCommand("backgroundColor");
    backgroundColor.addActionListener(this);
    content.add(backgroundColor);

    // prompt for default window dimensions

    content.add(new JLabel("Default Window Width: "));
    JSpinner width = new JSpinner(new SpinnerNumberModel(800, 1, 1024, 1));
    content.add(width);

    content.add(new JLabel("Default Window Height: "));
    JSpinner height = new JSpinner(new SpinnerNumberModel(700, 1, 768, 1));
    content.add(height);

    // prompt for boolean options

    content.add(new JLabel("Can Edit Fields?"));
    JCheckBox edit = new JCheckBox("", true);
    content.add(edit);
    content.add(new JLabel("Prefer Companion File?"));
    JCheckBox companion = new JCheckBox("", true);
    content.add(companion);
    content.add(new JLabel("Can Dynamically Edit Template?"));
    JCheckBox editTemplate = new JCheckBox("", true);
    content.add(editTemplate);
    content.add(new JLabel("Can Dynamically Edit OME-CA Mappings?"));
    JCheckBox editMap = new JCheckBox("", true);
    content.add(editMap);

    general.getViewport().add(content);
    tabPane.add("General Options", general); 

    // set up tab options tab

    JScrollPane tab = new JScrollPane();
    tab.getViewport().add(new JPanel());
    tabPane.add("Tab Options", tab); 
   
    // set up group options tab

    JScrollPane group = new JScrollPane();
    group.getViewport().add(new JPanel());
    tabPane.add("Group Options", group); 
  
    // set up field options tab

    JScrollPane field = new JScrollPane();
    field.getViewport().add(new JPanel());
    tabPane.add("Field Options", field); 

    getContentPane().add(tabPane);
 
    setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
    setVisible(true);
  }

  // -- TemplateEditor API methods --

  /** Sets all of the pixels in the given image to the specified RGB value. */
  public static void setPixels(BufferedImage bi, int value) {
    for (int x=0; x<bi.getWidth(); x++) {
      for (int y=0; y<bi.getHeight(); y++) {
        bi.setRGB(x, y, value);
      }
    }
  } 

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals("fontColor")) {
      new RGBWindow();
    }
    else if (cmd.equals("backgroundColor")) {
      new RGBWindow();
    }
  }

  // -- Helper classes --

  /** Displays a window for interactively choosing a color. */
  public class RGBWindow extends JFrame implements ChangeListener {

    // -- Fields --

    /** Red, green, and blue sliders. */
    private JSlider red, green, blue;

    /** Image showing what color is selected. */
    private BufferedImage current;

    private JPanel content;
    private JLabel imageLabel;

    // -- Constructor --

    public RGBWindow() {
      super("Color Picker");
      
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      setLayout(gridbag);

      current = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
      TemplateEditor.setPixels(current, 0);

      // set up color sliders  
   
      red = new JSlider(0, 255, 0);
      red.addChangeListener(this); 
      green = new JSlider(0, 255, 0);
      green.addChangeListener(this); 
      blue = new JSlider(0, 255, 0);
      blue.addChangeListener(this); 

      c.gridwidth = GridBagConstraints.RELATIVE;

      content = new JPanel();
      imageLabel = new JLabel(new ImageIcon(current));
      gridbag.setConstraints(imageLabel, c); 
      content.add(imageLabel);
      
      c.gridwidth = GridBagConstraints.REMAINDER; 
      
      gridbag.setConstraints(red, c); 
      content.add(red);
      gridbag.setConstraints(green, c); 
      content.add(green);

      c.gridheight = GridBagConstraints.REMAINDER;
      gridbag.setConstraints(blue, c); 
      content.add(blue);
      setContentPane(content);

      setPreferredSize(new Dimension(214, 230)); 
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      pack();
      setVisible(true);
    }

    // -- ChangeListener API methods --

    public void stateChanged(ChangeEvent e) {
      // update the image color 
      int r = red.getValue();
      int g = green.getValue();
      int b = blue.getValue();
      TemplateEditor.setPixels(current, (r << 16) | (g << 8) | b); 
      imageLabel.setIcon(new ImageIcon(current));
    }

  }

  // -- Main method --

  public static void main(String[] args) {
    new TemplateEditor();
  } 

}
