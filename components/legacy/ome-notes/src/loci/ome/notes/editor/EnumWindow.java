/*
 * #%L
 * OME Notes library for flexible organization and presentation of OME-XML
 * metadata.
 * %%
 * Copyright (C) 2007 - 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.ome.notes.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Window for choosing which values to put in a JComboBox.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-notes/src/loci/ome/notes/editor/EnumWindow.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-notes/src/loci/ome/notes/editor/EnumWindow.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class EnumWindow extends JPopupMenu implements ActionListener {

  // -- Constants --

  private static final CellConstraints CC = new CellConstraints();

  // -- Fields --

  /** Current list of options. */
  private Vector options;

  private JPanel content;
  private JTextField field;
  private TemplateEditor parent;

  // -- Constructor --

  public EnumWindow(TemplateEditor parent, String[] options) {
    super();

    this.parent = parent;

    FormLayout layout = new FormLayout("pref,pref:grow,pref,pref:grow," +
      "pref:grow,pref,pref:grow,pref:grow,pref:grow,pref",
      "pref,pref:grow,pref,pref:grow,pref");
    content = new JPanel(layout);

    this.options = new Vector();
    if (options != null) {
      for (int i=0; i<options.length; i++) {
        addOption(options[i]);
      }
    }

    field = new JTextField();
    content.add(field, CC.xywh(2, 2, 4, 1));

    JButton add = new JButton("Add choice");
    add.setActionCommand("add");
    add.addActionListener(this);
    content.add(add, CC.xywh(8, 2, 2, 1));

    JButton ok = new JButton("OK");
    ok.setActionCommand("ok");
    ok.addActionListener(parent);
    content.add(ok, CC.xy(5, 4));

    add(content);
    setPreferredSize(new Dimension(512, 256));
    pack();
  }

  // -- EnumWindow API methods --

  public String[] getOptions() {
    return (String[]) options.toArray(new String[0]);
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals("add")) {
      addOption(field.getText());
    }
    else if (cmd.equals("remove")) {
      Component[] c = content.getComponents();
      for (int i=3; i<c.length; i++) {
        if (c[i].equals(e.getSource())) {
          remove(content);
          content.remove(c[i - 1]);
          content.remove(c[i]);
          options.removeElementAt((i - 3) / 2);
          add(content);
          break;
        }
      }
      pack();
      parent.repaint();
    }
  }

  // -- Helper methods --

  private void addOption(String name) {
    remove(content);
    FormLayout layout = (FormLayout) content.getLayout();
    layout.insertRow(layout.getRowCount() - 2, new RowSpec("pref:grow"));
    layout.insertRow(layout.getRowCount() - 2, new RowSpec("pref"));
    JLabel label = new JLabel(name);
    JButton remove = new JButton("Remove");
    remove.setActionCommand("remove");
    remove.addActionListener(this);

    options.add(name);

    int last = layout.getRowCount() - 4;
    content.add(label, CC.xy(2, last));
    content.add(remove, CC.xywh(7, last, 2, 1));
    add(content);
    pack();
    parent.repaint();
  }

}
