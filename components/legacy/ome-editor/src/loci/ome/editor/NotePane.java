/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
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

package loci.ome.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.w3c.dom.Element;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A class that makes a comprehensive list of all the
 * notes added to a particular OMEXML File by using the
 * preexisting NotePanels made by MetadataPane.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/NotePane.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/NotePane.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class NotePane extends JScrollPane implements ActionListener {

  /** The main text color for this view. */
  public static final Color TEXT_COLOR =
    new Color(0,35,0);

  /** The panel that holds the title header for this view. */
  private JPanel titlePanel;

  /** The filechooser for this view. */
  protected JFileChooser chooser;

  /** A list of all TablePanels in the MetadataPane. */
  protected Vector tPanels;

  /** Construct the default NotePane object. */
  public NotePane() {
    super();

    tPanels = null;

    titlePanel = new JPanel();
    titlePanel.setLayout(new GridLayout(2,1));
    JLabel title = new JLabel();
    Font thisFont = title.getFont();
    Font newFont = new Font(thisFont.getFontName(),Font.BOLD,18);
    title.setFont(newFont);
    title.setText(" Note List:");
    title.setForeground(new Color(255,255,255));

    JButton saveButton = new JButton("Export Notes");
    saveButton.setPreferredSize(new Dimension(120,17));
    saveButton.setActionCommand("save");
    saveButton.addActionListener(this);
    saveButton.setOpaque(false);
    saveButton.setForeground(TEXT_COLOR);

    Color aColor = getBackground();

    JTextArea descrip = new JTextArea();
    descrip.setEditable(false);
    descrip.setLineWrap(true);
    descrip.setWrapStyleWord(true);
    descrip.setBackground(aColor);
    newFont = new Font(thisFont.getFontName(),
      Font.ITALIC,thisFont.getSize());
    descrip.setFont(newFont);
    descrip.setText("     A comprehensive list of all notes in this file.");

    FormLayout myLayout = new FormLayout(
      "pref, 5dlu, pref:grow:right, 5dlu",
      "5dlu, pref, 5dlu, pref");
    PanelBuilder build = new PanelBuilder(myLayout);
    CellConstraints cellC = new CellConstraints();

    build.add( title, cellC.xy(1, 2, "left,center"));
    build.add( saveButton, cellC.xy(3, 2, "right,center"));
    build.add( descrip, cellC.xyw(1, 4, 4, "fill,center"));
    titlePanel = build.getPanel();
    titlePanel.setBackground(TEXT_COLOR);

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setDialogTitle("Export Notes to Text File");
        chooser.setApproveButtonText("Save");
        chooser.setApproveButtonToolTipText("Export notes to " +
          "selected file.");
        chooser.setFileFilter(new TextFileFilter());
      }
    });
  }

  /**
  * Reset the TablePanel list acquired from MetadataPane, get
  * the NotePanels for these TablePanels, display them.
  */
  public void setPanels(Vector tablePanels) {
    tPanels = tablePanels;

    String rowString = "pref";
    for (int i = 0; i < tablePanels.size();i++) {
      rowString = rowString + ", 5dlu, pref";
    }

    ScrollablePanel contentPanel = new ScrollablePanel();
    FormLayout panelLayout = new FormLayout(
      "5dlu, pref:grow, 5dlu",
      rowString);
    contentPanel.setLayout(panelLayout);
    CellConstraints cc = new CellConstraints();

    contentPanel.add(titlePanel, cc.xyw(1,1,3));

    for (int i = 0;i < tablePanels.size();i++) {
      MetadataPane.TablePanel tableP = (MetadataPane.TablePanel)
        tablePanels.get(i);
      tableP.tableName.setForeground(TEXT_COLOR);
      tableP.addButton.setVisible(false);
      tableP.delButton.setVisible(false);
      tableP.tHead.setVisible(false);
      tableP.table.setVisible(false);
      if (tableP.imageLabel != null) tableP.imageLabel.setVisible(false);

      contentPanel.add( tableP, cc.xy(2,(2*i)+3));
    }

    setViewportView(contentPanel);
  }

  /**
   * Call up the necessary dialogs and then output the selected notes
   * to the selected text file.
   */
  public void exportNotes() {
    if (tPanels != null) {
      Hashtable topHash = new Hashtable();
      Vector names = new Vector();
      for (int i = 0;i < tPanels.size();i++) {
        MetadataPane.TablePanel thisPanel =
          (MetadataPane.TablePanel) tPanels.get(i);
        Hashtable noteHash = thisPanel.noteP.getNoteHash();
        topHash.put(thisPanel.name, noteHash);

        Vector noteElements = thisPanel.noteP.getNoteElements();
        if (noteElements!=null) {
          for (int j=0;j<noteElements.size();j++) {
            Element thisEle = (Element) noteElements.get(j);
            String suffix = thisEle.getAttribute("Name");
            names.add(thisPanel.name + "  >>>" + suffix);
          }
        }
      }

      Object[] values = names.toArray();

      Object[] toExport = ExportDialog.showDialog(
        (Component) getTopLevelAncestor(), (Component) getTopLevelAncestor(),
        "Select the notes you wish to export:", "Note Chooser", values,
        (Object[]) null,
        "Image (23): LaserCoordinates (23)  >>>Long Note Name");

      if (toExport != null && toExport.length > 0) {
        int rval = chooser.showOpenDialog(this);
        if (rval == JFileChooser.APPROVE_OPTION) {
          String pathName = chooser.getSelectedFile().getPath();
          if (!pathName.endsWith(".txt")) pathName = pathName + ".txt";

          File file = new File(pathName);

          try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("** Metadata Notes from " +
              ((MetadataPane.TablePanel)tPanels.get(0))
              .getCurrentFile().getName() + " **");
            bw.newLine();
            bw.newLine();
            Calendar rightNow = Calendar.getInstance();
            bw.write("** Date Exported: " + rightNow.get(Calendar.MONTH) +
              "/" + rightNow.get(Calendar.DAY_OF_MONTH) + "/" +
              rightNow.get(Calendar.YEAR) + " " +
              rightNow.get(Calendar.HOUR_OF_DAY) + ":" +
              rightNow.get(Calendar.MINUTE) + " **");
            bw.newLine();
            bw.newLine();

            Vector alreadyUsed = new Vector();
            int placeMark = 1;

            for (int i = 0;i < toExport.length;i++) {
              String thisNoteName = (String) toExport[i];

              int index = thisNoteName.indexOf(">");
              String tableName = thisNoteName.substring(0,index-2);

              Hashtable subHash = (Hashtable) topHash.get(tableName);

              if (!alreadyUsed.contains(tableName)) {
                alreadyUsed.add(tableName);

                bw.write(placeMark + ") " + tableName);
                placeMark++;
                bw.newLine();
                bw.newLine();
              }

              int lIndex = thisNoteName.lastIndexOf(">");
              String noteName = thisNoteName.substring(lIndex+1,
                thisNoteName.length());
              String noteValue = (String) subHash.get(noteName);

              bw.write("  ->" + noteName + ":");
              bw.newLine();
              int newLine = noteValue.indexOf("\n");
              //handle newline characters found in the note's value
              while (newLine != -1) {
                String subValue = noteValue.substring(0,newLine);
                noteValue = noteValue.substring(newLine+1,
                  noteValue.length());
                bw.write("     " + subValue);
                bw.newLine();
                newLine = noteValue.indexOf("\n");
              }
              if (noteValue != null) bw.write("     " + noteValue);
              bw.newLine();
              bw.newLine();
            }
            bw.close();
          }
          catch (Exception exc) {exc.printStackTrace();}
        }
        else JOptionPane.showMessageDialog(getTopLevelAncestor(),
              "No notes were selected to export!",
              "Unable to Export Nothingness", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /** Handle the "Export Notes" button action, call exportNotes(). */
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("save")) exportNotes();
  }

  // -- Helper classes --

  /**
   * A subclass of JPanel that gets around the annoying resize width
   * problems inherent in a JPanel that is the View of a JScrollPane.
   */
  public class ScrollablePanel extends JPanel implements Scrollable {
    public ScrollablePanel() { super(); }

    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
      int orientation, int direction) {return 5;}
    public int getScrollableBlockIncrement(Rectangle visibleRect,
      int orientation, int direction) {return visibleRect.height;}
    public boolean getScrollableTracksViewportWidth() {return true;}
    public boolean getScrollableTracksViewportHeight() {return false;}
  }

  /** A file filter to display only text files and directories. */
  public class TextFileFilter extends FileFilter {
    public boolean accept(File f) {
      if (f.getPath().endsWith(".txt") || f.isDirectory()) return true;
      else return false;
    }

    public String getDescription() {
      return "Text Files";
    }
  }

}
