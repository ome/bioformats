/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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

package loci.ome.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.common.ReflectedUniverse;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.in.OMEXMLReader;
import loci.formats.in.TiffReader;
import loci.formats.meta.IMetadata;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.out.TiffWriter;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffSaver;
import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.primitives.PositiveInteger;

import org.openmicroscopy.xml.AttributeNode;
import org.openmicroscopy.xml.CustomAttributesNode;
import org.openmicroscopy.xml.OMENode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A panel that displays OME-XML metadata.
 * Most of the gui code is in here.
 * If you want a panel instead of a window, instantiate this
 * instead of MetadataEditor.
 * Sadly you lose quite a bit of functionality such as the
 * various views if you choose to directly instatiate.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/MetadataPane.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/MetadataPane.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class MetadataPane extends JPanel
  implements ActionListener, Runnable
{

  // -- Constants --

  /**Defines the names of columns in the TablePanels.*/
  protected static final String[] TREE_COLUMNS = {"Attribute", "Value", "Goto"};

  /**An icon that signifies metadata is present.*/
  public static final ImageIcon DATA_BULLET =
    createImageIcon("Icons/bullet-green.gif",
      "An icon signifying that metadata is present.");

  /**An icon that signifies no metadata is present.*/
  public static final ImageIcon NO_DATA_BULLET =
    createImageIcon("Icons/bullet-red.gif",
      "An icon signifying that no metadata is present.");

  /**The color that signifies a button's operation is to add something.*/
  public static final Color ADD_COLOR =
    new Color(0, 100, 0);

  /**The color that signifies a button's operation is to delete something.*/
  public static final Color DELETE_COLOR =
    new Color(100, 0, 0);

  /**The main text color of most things.*/
  public static final Color TEXT_COLOR =
    new Color(0, 0, 50);

  // -- Fields --

  /** Pane containing XML tree. */
  protected JTabbedPane tabPane;

  /** TemplateParser object.*/
  protected TemplateParser tParse;

  /** Keeps track of the OMENode being operated on currently.*/
  protected OMENode thisOmeNode;

  /** A list of all TablePanel objects. */
  protected Vector panelList;

  /** A list of TablePanel objects that have ID attributes. */
  protected Vector panelsWithID;

  /** A list of external references to be added to the combobox cell editor.*/
  protected Vector addItems;

  /** A list of the TabPanels.*/
  protected Vector tabPanelList;

  /**
  * Hashtable containing internal semantic
  * type defs in current file.
  */
  public Hashtable internalDefs;

  /**
  * Signifies that the current file has
  * changed from the last saved version.
  */
  public boolean hasChanged;

  /** If true, the save button should be display in each TabPanel.*/
  protected boolean addSave;

  /**Whether or not the user should be able to edit metadata.*/
  protected boolean editable;

  /**Whether or not to display ID attributes. By default, is false.*/
  protected boolean showIDs;

  /** Holds the original file if it is of TIFF format.*/
  protected File originalTIFF;

  /** Holds the currently edited file, or null if none.*/
  protected File currentFile;

  /** Holds the first image of a tiff file.*/
  public BufferedImage img, thumb;

  public BufferedImage[] images, thumbs;

  /** Holds the image reader used to open image or null if none used. */
  protected BufferedImageReader reader;

  private int minPixNum;

  private boolean pixelsIDProblem, isOMETiff;

  protected String fileID;

  protected Hashtable tiffDataStore;

  protected OMEXMLMetadata ms;

  // -- Fields - raw panel --

  /** Panel containing raw XML dump. */
  protected JPanel rawPanel;

  /** Text area displaying raw XML. */
  protected JTextArea rawText;

  /** Whether XML is being displayed in raw form. */
  protected boolean raw;

  // -- Constructor --

  /** Constructs default widget for displaying OME-XML metadata. */
  public MetadataPane() { this((File) null, true); }

  /**
  * Constructs a pane to display the OME-XML metadata of a
  * given file.
  * @param file The file to be initially displayed.
  */
  public MetadataPane(File file) { this(file, true); }

  /**
  * Constructs a pane to display the OME-XML metadata of a
  * given file and with the given save policy.
  * @param file The file to be initially displayed.
  * @param save Whether saving should be allowed.
  */
  public MetadataPane(File file, boolean save) { this(file, save, true); }

  /**
   * Constructs a pane to display the OME-XML metadata of a
   * given file with the given save and editing policy.
   * @param file the file to open initially
   * @param save whether or not to display the save button
   * @param editMe whether or not the user should be able to edit metadata
   */
  public MetadataPane(File file, boolean save, boolean editMe) {
    // -- General Field Initialization --

    editable = editMe;
    panelList = new Vector();
    panelsWithID = new Vector();
    addItems = new Vector();
    tParse = new TemplateParser("Template.xml");
    thisOmeNode = null;
    internalDefs = null;
    hasChanged = false;
    currentFile = null;
    addSave = save;
    originalTIFF = null;
    img = null;
    thumb = null;
    showIDs = false;
    reader = null;

    // -- Tabbed Pane Initialization --

    tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
    setupTabs();
    setLayout(new CardLayout());
    add(tabPane, "tabs");
    setPreferredSize(new Dimension(700, 500));
    tabPane.setVisible(true);
    setVisible(true);

    // -- Raw panel --

    raw = false;
    rawPanel = new JPanel();
    rawPanel.setLayout(new BorderLayout());

    // label explaining what happened
    JLabel rawLabel = new JLabel("Metadata parsing failed. " +
      "Here is the raw info. Good luck!");
    rawLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
    rawPanel.add(rawLabel, BorderLayout.NORTH);

    // text area for displaying raw XML
    rawText = new JTextArea();
    rawText.setLineWrap(true);
    rawText.setColumns(50);
    rawText.setRows(30);
    rawText.setEditable(false);
    rawPanel.add(new JScrollPane(rawText), BorderLayout.CENTER);
    rawPanel.setVisible(false);
    add(rawPanel, "raw");

    //open initial file
    if (file != null) {
      setOMEXML(file);
      if (getTopLevelAncestor() instanceof MetadataEditor) {
        MetadataEditor mn = (MetadataEditor) getTopLevelAncestor();
        mn.setCurrentFile(file);
      }
    }
  }

  // -- MetadataPane API methods --

  /**
   * Retrieves the current document object
   * describing the whole OMEXMLNode tree.
   */
  public Document getDoc() {
    Document doc = null;
    try { doc = thisOmeNode.getOMEDocument(false); }
    catch (Exception e) { }
    return doc;
  }

  /**
  * Tells whether or not the XML has changed due to
  * user manipulation.
  */
  public boolean getState() { return hasChanged; }

  /**
  * Sets whether or not the XML has changed due to
  * user manipulation.
  * @param change Has the XML changed?
  */
  public void stateChanged(boolean change) {
    hasChanged = change;
    for (int i = 0; i < tabPanelList.size(); i++) {
      TabPanel thisTab = (TabPanel) tabPanelList.get(i);
      if (change) thisTab.saveButton.setForeground(ADD_COLOR);
      else thisTab.saveButton.setForeground(TEXT_COLOR);
    }
  }

  /** Get the OMENode currently being edited.*/
  public OMENode getRoot() { return thisOmeNode; }

  public boolean testThirdParty(File file) {
    String id = file.getPath();
    if (!file.exists()) return false;
    if (!isOMETiff) return false;
    ImageReader read = new ImageReader();
    try {
      if (read.getReader(id) instanceof TiffReader ||
        read.getReader(id) instanceof OMEXMLReader) return false;
      else return false;
    }
    catch (FormatException exc) {
      return true;
    }
    catch (IOException exc) {
      return true;
    }
  }

  public void askCompanionInstead(File file) {
    Object[] options = {"Sounds good", "Cancel"};

    int n = JOptionPane.showOptionDialog(getTopLevelAncestor(),
            "The file you are trying to save to is a third-party format."
            + " Currently only TIFF or OME-XML files can be saved."
            + " Would you like to save to a companion file instead?",
            "Can't Save to Third-Party Format",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            (javax.swing.Icon)null,
            options,
            options[0]);
    if (n == JOptionPane.YES_OPTION) saveCompanionFile(file);
  }

  /** Save to the given file.*/
  public void saveFile(File file) {
    try {
      //use the node tree in the MetadataPane to write flattened OMECA
      //to a given file
      if (testThirdParty(file)) {
        askCompanionInstead(file);
        return;
      }
      if (originalTIFF != null && !file.getPath().endsWith(".ome")) {
        String xml = thisOmeNode.writeOME(false);

        if (originalTIFF.equals(file)) {
          //just rewrite image description of original file.
          xml = addTiffData(xml, file);
          String path = file.getAbsolutePath();
          RandomAccessOutputStream out = new RandomAccessOutputStream(path);
          try {
            TiffSaver saver = new TiffSaver(out, path);
            RandomAccessInputStream in = new RandomAccessInputStream(path);
            saver.overwriteComment(in, xml);
            in.close();
          }
          finally {
            out.close();
          }
        }
        else {
          //create the new tiff file.
          saveTiffFile(file);
        }
      }
      else {
        thisOmeNode.writeOME(file, false);
        if (getTopLevelAncestor() instanceof MetadataEditor) {
          MetadataEditor mdn = (MetadataEditor) getTopLevelAncestor();
          mdn.setTitle("OME Metadata Editor - " + file);
        }
      }
    }
    catch (Exception e) {
      //if all hell breaks loose, display an error dialog
      JOptionPane.showMessageDialog(getTopLevelAncestor(),
        "Sadly, the file you specified is either write-protected\n" +
        "or in use by another program. Game over, man.",
        "Unable to Write to Specified File", JOptionPane.ERROR_MESSAGE);
      System.out.println("ERROR! Attempt failed to open file: " +
        file.getName());
    }
  }

  public void saveCompanionFile(File file) {
    File compFile = new File(file.getPath() + ".ome");
    if (compFile.exists()) compFile.delete();
    try {
      thisOmeNode.writeOME(compFile, false);
    }
    catch (Exception exc) {
      if (exc instanceof RuntimeException) throw (RuntimeException) exc;
      else exc.printStackTrace();
    }
  }

  public void saveTiffFile(File file) {
    if (originalTIFF != null && originalTIFF.equals(file)) saveFile(file);
    else {
      String id = currentFile.getPath();
      String outId = id + ".tif";
      File outFile = new File(outId);
      if (outFile.exists()) outFile.delete();
      if (reader == null) reader = new BufferedImageReader();
      TiffWriter writer = new TiffWriter();

      int imageCount = 0;
      String xml = null;

      try {
        xml = thisOmeNode.writeOME(false);
        xml = addTiffData(xml, file);
        reader.setId(id);
        imageCount = reader.getImageCount();
      }
      catch (Exception exc) {
        if (exc instanceof RuntimeException) throw (RuntimeException) exc;
        else exc.printStackTrace();
      }

      try {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service =
          (OMEXMLService) factory.getInstance(OMEXMLService.class);
        IMetadata meta = service.createOMEXMLMetadata();
        writer.setMetadataRetrieve(meta);
        meta.setPixelsBinDataBigEndian(
          new Boolean(!reader.isLittleEndian()), 0, 0);
        try {
          meta.setPixelsDimensionOrder(
            DimensionOrder.fromString(reader.getDimensionOrder()), 0);
        }
        catch (EnumerationException e) { }
        meta.setPixelsSizeX(
          new PositiveInteger(new Integer(reader.getSizeX())), 0);
        meta.setPixelsSizeY(
          new PositiveInteger(new Integer(reader.getSizeY())), 0);
        meta.setPixelsSizeZ(
          new PositiveInteger(new Integer(reader.getSizeZ())), 0);
        meta.setPixelsSizeC(
          new PositiveInteger(new Integer(reader.getSizeC())), 0);
        meta.setPixelsSizeT(
          new PositiveInteger(new Integer(reader.getSizeT())), 0);
        writer.setId(outId);
      }
      catch (Exception exc) {
        if (exc instanceof RuntimeException) throw (RuntimeException) exc;
        else exc.printStackTrace();
      }

      for(int i = 0; i < imageCount; i++) {
        byte[] plane = null;

        try {
          plane = reader.openBytes(i);
        }
        catch (Exception exc) {
          if (exc instanceof RuntimeException) throw (RuntimeException) exc;
          else exc.printStackTrace();
        }

        IFD ifd = null;
        if (i == 0) {
          // save OME-XML metadata to TIFF file's first IFD
          ifd = new IFD();
          ifd.putIFDValue(IFD.IMAGE_DESCRIPTION, xml);
        }
        // write plane to output file
        try {
          writer.saveBytes(i, plane, ifd);
        }
        catch (Exception exc) {
          if (exc instanceof RuntimeException) throw (RuntimeException) exc;
          else exc.printStackTrace();
        }
      }
      currentFile = new File(outId);
      if (getTopLevelAncestor() instanceof MetadataEditor) {
        MetadataEditor mn = (MetadataEditor) getTopLevelAncestor();
        mn.setCurrentFile(file);
      }
    }
  }

  public void saveTiffFile(File file, String outId) {
    String id = currentFile.getPath();
    File outFile = new File(outId);
    if (outFile.exists()) outFile.delete();
    if (reader == null) reader = new BufferedImageReader();
    TiffWriter writer = new TiffWriter();

    int imageCount = 0;
    String xml = null;

    try {
      xml = thisOmeNode.writeOME(false);
      xml = addTiffData(xml, file);
      reader.setId(id);
      imageCount = reader.getImageCount();
    }
    catch (Exception exc) {
      if (exc instanceof RuntimeException) throw (RuntimeException) exc;
      else exc.printStackTrace();
    }

    for(int i = 0; i < imageCount; i++) {
      byte[] plane = null;

      try {
        plane = reader.openBytes(i);
      }
      catch (Exception exc) {
        if (exc instanceof RuntimeException) throw (RuntimeException) exc;
        else exc.printStackTrace();
      }

      IFD ifd = null;
      if (i == 0) {
        // save OME-XML metadata to TIFF file's first IFD
        ifd = new IFD();
        ifd.putIFDValue(IFD.IMAGE_DESCRIPTION, xml);
      }
      // write plane to output file

      try {
        writer.setId(outId);
        writer.saveBytes(i, plane, ifd);
      }
      catch (Exception exc) {
        if (exc instanceof RuntimeException) throw (RuntimeException) exc;
        else exc.printStackTrace();
      }
    }
    currentFile = new File(outId);
    if (getTopLevelAncestor() instanceof MetadataEditor) {
      MetadataEditor mn = (MetadataEditor) getTopLevelAncestor();
      mn.setCurrentFile(file);
    }
  }

  public void merge() {
    if (currentFile != null) {
      String id = currentFile.getPath();
      ImageReader read = new ImageReader();
      OMEXMLMetadata oms =
        (OMEXMLMetadata) MetadataTools.createOMEXMLMetadata();
      read.setMetadataStore(oms);

      try {
        //just to repopulate the metadatastore to original state
        read.setId(id);
        int imageCount = read.getImageCount();
      }
      catch (Exception exc) {
        if (exc instanceof RuntimeException) throw (RuntimeException) exc;
        else exc.printStackTrace();
      }
      OMENode ome = (OMENode)oms.getRoot();

      File companion = new File(currentFile.getPath() + ".ome");
      if (companion.exists()) {
        Merger merge = new Merger(ome, companion, this);
        setOMEXML(merge.getRoot());
      }
      else {
        JOptionPane.showMessageDialog(this,
            "No companion file found to merge!!",
            "MetadataEditor Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else {
      JOptionPane.showMessageDialog(this,
            "You have not saved or opened a file to merge yet!",
            "MetadataEditor Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void storeTiffData(File file) {
    tiffDataStore = new Hashtable();
    Document doc;
    Vector pixList = new Vector();
    DocumentBuilderFactory docFact =
      DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder db = docFact.newDocumentBuilder();

      // get TIFF comment without parsing out TiffData Elements
      String comment = new TiffParser(currentFile.getPath()).getComment();
      ByteArrayInputStream bis = new ByteArrayInputStream(comment.getBytes());
      doc = db.parse((java.io.InputStream)bis);
      pixList = DOMUtil.findElementList("Pixels", doc);

    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
    catch (org.xml.sax.SAXException exc) {
      exc.printStackTrace();
    }
    catch (javax.xml.parsers.ParserConfigurationException exc) {
      exc.printStackTrace();
    }

    for(int i = 0; i<pixList.size(); i++) {
      Element thisEle = (Element) pixList.get(i);
      String thisID = DOMUtil.getAttribute("ID", thisEle);
      Vector dataList = DOMUtil.getChildElements("TiffData", thisEle);
      Vector tiffDataAttrs = new Vector();
      for(int j = 0; j<dataList.size(); j++) {
        Element thisData = (Element) dataList.get(j);
        String[] attrNames = DOMUtil.getAttributeNames(thisData);
        String[] attrValues = DOMUtil.getAttributeValues(thisData);
        Hashtable attrs = new Hashtable();
        for(int k= 0; k<attrNames.length; k++) {
          attrs.put(attrNames[k], attrValues[k]);
        }
        tiffDataAttrs.add(attrs);
      }

      tiffDataStore.put(thisID, tiffDataAttrs);
    }

  }

  public String addTiffData(String xml, File file) {
    Document doc = null;
    Vector pixList = new Vector();
    DocumentBuilderFactory docFact =
      DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder db = docFact.newDocumentBuilder();
      ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
      doc = db.parse((java.io.InputStream)bis);
      pixList = DOMUtil.findElementList("Pixels", doc);
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
    catch (org.xml.sax.SAXException exc) {
      exc.printStackTrace();
    }
    catch (javax.xml.parsers.ParserConfigurationException exc) {
      exc.printStackTrace();
    }

    //creating tiffData from non-OME-Tiff
    if (!isOMETiff) {
      for(int i = 0; i<pixList.size(); i++) {
        Element thisEle = (Element) pixList.get(i);
        DOMUtil.createChild(thisEle, "TiffData");
      }
    }
    //creating tiff from OMETiff file

    else if (isOMETiff) {
      boolean prompted = false;
      boolean addElements = false;
      for(int i = 0; i<pixList.size(); i++) {
        Element thisEle = (Element) pixList.get(i);
        String thisID = DOMUtil.getAttribute("ID", thisEle);
        Vector dataEles = (Vector) tiffDataStore.get(thisID);

        //fixes if TiffData Elements not in File but should be
        if (dataEles.size() == 0) {
          if (!prompted) {
            Object[] options =
              {"Sounds good", "Cancel (Nothing bad will happen)"};

            int n = JOptionPane.showOptionDialog(getTopLevelAncestor(),
              "We detected that an OME-xml companion file exists for"
                + " the file you just opened, \n would you like to merge these"
                + " files in some manner?",
              "Companion File Detected",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              (javax.swing.Icon)null,
              options,
              options[0]);

              if (n == JOptionPane.YES_OPTION)  addElements = true;
              prompted = true;
          }

          if (addElements) {
            DOMUtil.createChild(thisEle, "TiffData");
            continue;
          }
        }

        for(int j=0; j<dataEles.size(); j++) {
          Element thisData = DOMUtil.createChild(thisEle, "TiffData");
          Hashtable attrs = (Hashtable) dataEles.get(j);
          Object[] attrKeys = attrs.keySet().toArray();
          for(int k = 0; k<attrKeys.length; k++) {
            String name = (String)attrKeys[k];
            String value = (String)(attrs.get(name));
            if (value == null) value = "";
            DOMUtil.setAttribute(name, value, thisData);
          }
        }
      }
    }
    String result = null;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DOMUtil.writeXML(baos, doc);
      result = baos.toString();
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    return result;
  }

  public boolean checkOMETiff(File file) {
    try {
      String comment = new TiffParser(file.getPath()).getComment();
      OMENode testNode = new OMENode(comment);
    }
    catch (IOException exc) {
      return false;
    }
    catch (javax.xml.parsers.ParserConfigurationException exc) {
      return false;
    }
    catch (org.xml.sax.SAXException exc) {
      return false;
    }
    catch (javax.xml.transform.TransformerConfigurationException exc) {
      return false;
    }
    catch (javax.xml.transform.TransformerException exc) {
      return false;
    }
    return true;
  }

  /**
   * Sets the displayed OME-XML metadata to correspond
   * to the given character string of XML.
   */
  private void setOMEXML(String xml) {
    OMENode ome = null;
    try { ome = new OMENode(xml); }
    catch (Exception exc) { }
    raw = ome == null;
    if (raw) rawText.setText(xml);
    else setOMEXML(ome);
    SwingUtilities.invokeLater(this);
  }

  /**
   * Sets the displayed OME-XML metadata to correspond
   * to the given OME-XML or OME-TIFF file.
   * @return true if the operation was successful
   */
  public boolean setOMEXML(File file) {
    try {
      RandomAccessInputStream in =
        new RandomAccessInputStream(file.getAbsolutePath());
      TiffParser parser = new TiffParser(in);
      isOMETiff = false;

      if (parser.isValidHeader()) {
        // TIFF file
        originalTIFF = file;
      }
      else originalTIFF = null;
      in.close();

      OMENode ome = null;

      boolean doMerge = false;

      try {
        reader = new BufferedImageReader();
        ms = (OMEXMLMetadata) MetadataTools.createOMEXMLMetadata();

        // tell reader to write metadata as it's being
        // parsed to an OMENode (DOM in memory)
        reader.setMetadataStore(ms);
        String id = file.getPath();
        fileID = id;
        currentFile = file;
        File companionFile = new File(id + ".ome");
        if (companionFile.exists()) {
          Object[] options = {"Sounds good", "No, open original file"};

          int n = JOptionPane.showOptionDialog(getTopLevelAncestor(),
            "We detected that an OME-xml companion file exists for"
              + " the file you just opened, \n would you like to merge these"
              + " files in some manner?",
            "Companion File Detected",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            (javax.swing.Icon)null,
            options,
            options[0]);
          if (n == JOptionPane.YES_OPTION) doMerge = true;
        }

        //Set up thumbnails
        reader.setId(id);
        int numSeries = reader.getSeriesCount();

        images = new BufferedImage[numSeries+1];
        thumbs = new BufferedImage[numSeries+1];
        for(int i = 0; i<numSeries; i++) {
          if (numSeries > 1) reader.setSeries(i);
          int num = reader.getImageCount();
          if (num > 0) {
            // get middle image from the file
            img = reader.openImage(num / 2);
          }
          else img = null;
          images[i] = img;
          int width = 50, height = 50;
          thumb = AWTImageTools.scale(img, width, height, false);
          thumbs[i] = thumb;
        }
        ome = (OMENode) ms.getRoot();

        if (doMerge) {
          Merger merge = new Merger(ome, companionFile, this);
          ome = merge.getRoot();
        }

        //handle if tiff after reader has been constructed
        if (originalTIFF != null) {
          isOMETiff = checkOMETiff(file);
          storeTiffData(file);
        }

        //find minimum pixel ID, doesn't have to be zero, if not in
        //standard format, flag this, all thumbs will be the same
        minPixNum = 0;
        pixelsIDProblem = false;
        Vector pixList = new Vector();

        try {
          pixList = DOMUtil.findElementList("Pixels", ome.getOMEDocument(true));

          int lowestInt = -1;

          for(int i = 0; i<pixList.size(); i++) {
            Element thisPix = (Element) pixList.get(i);
            String thisID = thisPix.getAttribute("ID");
            int colonIndex = thisID.indexOf(":");
            if (colonIndex == -1) {
              pixelsIDProblem = true;
              break;
            }
            String pixNumString = thisID.substring(colonIndex + 1);
            int pixNum = -1;
            try {
              pixNum = Integer.parseInt(pixNumString);
            }
            catch (java.lang.NumberFormatException exc) {
              pixelsIDProblem = true;
              break;
            }
            if (lowestInt == -1) {
              lowestInt = pixNum;
              continue;
            }
            if (lowestInt > pixNum) lowestInt = pixNum;
          }

          minPixNum = lowestInt;
          if (minPixNum == -1) pixelsIDProblem = true;
        }
        catch (Exception exc) { exc.printStackTrace(); }

        if (pixList.size() == 1) pixelsIDProblem = false;

        setOMEXML(ome);
      }
      catch (FormatException exc) {
        if ("Unsupported ZCT index mapping".equals(exc.getMessage())) {
          JOptionPane.showMessageDialog(this,
             "This tiff file is corrupted. The ZCT index mapping is"
             + " unsupported by bioformats.\nYour metadata will not"
             + " populate correctly, our apologies.",
          "MetadataEditor Error", JOptionPane.ERROR_MESSAGE);
        }
        img = null;
        thumb = null;

        String xml = DataTools.readFile(file.getAbsolutePath());
        if (xml.startsWith("<?xml") || xml.startsWith("<OME")) {
          setOMEXML(xml);
        }
        else return false;
      }
      catch (IOException exc) { exc.printStackTrace(); };

      in.close();
      return true;
    }
    catch (IOException exc) { return false; }
  }

  /** Sets the displayed OME-XML metadata. */
  protected void setOMEXML(OMENode ome) {
    // test for document, then call the setup(OMENode ome) method
    Document doc = null;
    try { doc = ome == null ? null : ome.getOMEDocument(false); }
    catch (Exception exc) { }
    if (doc == null) {
      JOptionPane.showMessageDialog(this,
          "Document is NULL.",
          "MetadataEditor Error", JOptionPane.ERROR_MESSAGE);
      System.out.println("Document is NULL.");
      return;
    }
    internalDefs = new Hashtable();

    //time to parse internal semantic type defs in file
    //to handle appropriate reference types
    Element thisRoot = ome.getDOMElement();
    NodeList nl = thisRoot.getChildNodes();
    for (int j = 0; j < nl.getLength(); j++) {
      Node node = nl.item(j);
      if (!(node instanceof Element)) continue;
      Element someE = (Element) node;
      if (!someE.getTagName().equals("STD:SemanticTypeDefinitions")) {
        continue;
      }
      NodeList omeEleList = node.getChildNodes();
      for (int k = 0; k < omeEleList.getLength(); k++) {
        node = omeEleList.item(k);
        if (!(node instanceof Element)) continue;
        Element omeEle = (Element) node;
        if (!omeEle.getTagName().equals("SemanticType")) continue;
        NodeList omeAttrList = node.getChildNodes();
        Hashtable thisHash = new Hashtable(10);
        for (int l = 0; l < omeAttrList.getLength(); l++) {
          node = omeAttrList.item(l);
          if (!(node instanceof Element)) continue;
          Element omeAttr = (Element) node;
          if (!omeAttr.getTagName().equals("Element")) continue;
          if (!omeAttr.hasAttribute("DataType")) continue;
          String dType = omeAttr.getAttribute("DataType");
          if (!dType.equals("reference")) continue;
          String attrName = omeAttr.getAttribute("Name");
          String refType = omeAttr.getAttribute("RefersTo");
          thisHash.put(attrName, refType);
        }
        internalDefs.put(omeEle.getAttribute("Name"), thisHash);
      }
    }

    thisOmeNode = ome;
    stateChanged(false);
    setupTabs(ome);
  }

  /**
   * Sets up the JTabbedPane based on a template, assumes that no OMEXML
   * file is being compared to the template, so no data will be displayed.
   * should be used to initialize the application and to create new OMEXML
   * documents based on the template
   */
  public void setupTabs() {
    //make sure all old gui stuff is tossed when this called twice.
    //also clear our TablePanel lists
    tabPanelList = new Vector();
    panelList = new Vector();
    panelsWithID = new Vector();
    addItems = new Vector();
    tabPane.removeAll();
    currentFile = null;
    originalTIFF = null;
    img = null;
    thumb = null;
    internalDefs = new Hashtable();
    try { thisOmeNode = new OMENode(); }
    catch (Exception e) { e.printStackTrace(); }

    //use the list acquired from Template.xml to form the initial tabs
    Element[] tabList = tParse.getTabs();
    for (int i = 0; i< tabList.length; i++) {
      String thisName = tabList[i].getAttribute("Name");
      if (thisName.length() == 0) thisName = tabList[i].getAttribute("XMLName");
      //make a TabPanel Object that represents the panel
      //that displays data for a node
      TabPanel tPanel = new TabPanel(tabList[i]);

      //set the field oNode in TabPanel to reflect the structure of the xml
      //document being formed

      //do all the good stuff to flesh out the TabPanel's gui with the tables
      //and assorted stuff
      renderTab(tPanel);

      //set up a scrollpane to hold the TabPanel in case it's too large to fit
      JScrollPane scrollPane = new JScrollPane(tPanel);
      scrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      String desc = tabList[i].getAttribute("Description");
      if (desc.length() == 0) tabPane.addTab(thisName, NO_DATA_BULLET,
        scrollPane, null);
      else tabPane.addTab(thisName, NO_DATA_BULLET, scrollPane, desc);
      int keyNumber = getKey(i+1);
      if (keyNumber !=0) tabPane.setMnemonicAt(i, keyNumber);
    }

    //Makes sure that the external references do not mirror the internal ones
    //since there should be no intersection between the two sets
    for (int j = 0; j<panelsWithID.size(); j++) {
      TablePanel tempTP = (TablePanel) panelsWithID.get(j);
      String tryID = "(External) " + tempTP.id;
      if (addItems.indexOf(tryID) >= 0) addItems.remove(tryID);
    }

    //this part sets up the refTable's comboBox editor to have choices
    //corresponding to every TablePanel that has a valid ID attribute
    for (int i = 0; i<panelList.size(); i++) {
      TablePanel p = (TablePanel) panelList.get(i);
      p.setEditor();
    }

    //set the displayed tab to be by default the first image
    TabPanel firstImageTab = null;
    for (int i=0; i<tabPanelList.size(); i++) {
      TabPanel tabP = (TabPanel) tabPanelList.get(i);
      if (tabP.name.startsWith("Image")) {
        firstImageTab = tabP;
        break;
      }
    }
    if (firstImageTab != null) {
      Container anObj = (Container) firstImageTab;
      while (!(anObj instanceof JScrollPane)) {
        anObj = anObj.getParent();
      }
      JScrollPane jScr = (JScrollPane) anObj;
      tabPane.setSelectedComponent(jScr);
    }

    stateChanged(false);
  }

  /**
   * sets up the JTabbedPane given an OMENode from an OMEXML file.
   * the template will set which parts of the file are displayed.
   */
  public void setupTabs(OMENode ome) {
    //Get rid of old gui components and old TablePanel lists
    //when new file is opened
    tabPane.removeAll();
    tabPanelList = new Vector();
    panelList = new Vector();
    panelsWithID = new Vector();
    addItems = new Vector();

    //use the list acquired from Template.xml to form the initial tabs
    Element[] tabList = tParse.getTabs();
    Vector actualTabs = new Vector(2 * tabList.length);
    Vector oNodeList = new Vector(2 * tabList.length);
    for (int i = 0; i< tabList.length; i++) {
      //since we have an OMEXML file to compare to now, we have to worry about
      //repeat elements
      //inOmeList will hold all instances of a particular tagname on one level
      //of the node-tree
      Vector inOmeList = null;
      String aName = tabList[i].getAttribute("XMLName");
      //work-around checks if we need to look in CustomAttributes,
      //and subsequently ignore it
      if (aName.equals("Image") || aName.equals("Feature") ||
        aName.equals("Dataset") || aName.equals("Project"))
      {
        inOmeList = ome.getChildNodes(aName);
      }
      else {
        if (ome.getChildNode("CustomAttributes") != null)
          inOmeList = ome.getChildNode("CustomAttributes").getChildNodes(aName);
      }
      int vSize = 0;
      if (inOmeList != null) vSize = inOmeList.size();
      //check to see if one or more elements with the given tagname
      //(a.k.a. "XMLName") exist in the file
      if (vSize >0) {
        for (int j = 0; j<vSize; j++) {
          String thisName = tabList[i].getAttribute("Name");
          if (thisName.length() == 0) {
            thisName = tabList[i].getAttribute("XMLName");
          }
          //create our friend, the TabPanel, which holds the template Element
          //and the actual OMEXMLNode that either existed previously or has
          //been created by the ReflectedUniverse mumbojumbo
          TabPanel tPanel = new TabPanel(tabList[i]);
          tPanel.oNode = (OMEXMLNode) inOmeList.get(j);
          //call renderTab(TabPanel tp) to set up the TabPanel gui, create
          //TablePanels to display Elements, etc.
          renderTab(tPanel);
          //create a scrollpane to hold the tabpanel in case it is too large
          JScrollPane scrollPane = new JScrollPane(tPanel);
          scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
          //test if a description is associated with this tab in the template
          String desc = tabList[i].getAttribute("Description");
          thisName = getTreePathName(tPanel.el, tPanel.oNode);
          if (desc.length() == 0) {
            tabPane.addTab(thisName, DATA_BULLET, scrollPane, null);
          }
          else tabPane.addTab(thisName, DATA_BULLET, scrollPane, desc);
          actualTabs.add(tabList[i]);
          oNodeList.add(tPanel.oNode);
        }
      }
      //if no instances of tagname in file,
      //still set up a blank table based on the template
      else {
        //this section the same as above case, see comments there
        String thisName = tabList[i].getAttribute("Name");
        if (thisName.length() == 0) {
          thisName = tabList[i].getAttribute("XMLName");
        }
        TabPanel tPanel = new TabPanel(tabList[i]);

        renderTab(tPanel);
        JScrollPane scrollPane = new JScrollPane(tPanel);
        scrollPane.setVerticalScrollBarPolicy(
          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        String desc = tabList[i].getAttribute("Description");
        if (desc.length() == 0) {
          tabPane.addTab(thisName, NO_DATA_BULLET, scrollPane, null);
        }
        else tabPane.addTab(thisName, NO_DATA_BULLET, scrollPane, desc);
        actualTabs.add(tabList[i]);
        oNodeList.add(tPanel.oNode);
      }
    }
    //set up mnemonics, and form an array holding the names of the tabs
    String[] tabNames = new String[actualTabs.size()];
    for (int i = 0; i<actualTabs.size(); i++) {
      int keyNumber = getKey(i+1);
      if (keyNumber !=0) tabPane.setMnemonicAt(i, keyNumber);
      Element e = (Element) actualTabs.get(i);
      tabNames[i] = getTreePathName(e, (OMEXMLNode) oNodeList.get(i));
      if (tabNames[i] == null) tabNames[i] = getTreePathName(e);
    }
    //change the "Tabs" menu in the original window to reflect the actual tabs
    //created (duplicate tabs are the reason for this)
    if (getTopLevelAncestor() instanceof MetadataEditor) {
      MetadataEditor mn = (MetadataEditor) getTopLevelAncestor();
      mn.changeTabMenu(tabNames);
    }

    //Makes sure that the external references do not mirror the internal ones
    //since there should be no intersection between the two sets
    for (int j = 0; j<panelsWithID.size(); j++) {
      TablePanel tempTP = (TablePanel) panelsWithID.get(j);
      String tryID = "(External) " + tempTP.id;
      if (addItems.indexOf(tryID) >= 0) addItems.remove(tryID);
    }

    //this part sets up the refTable's comboBox editor to have choices
    //corresponding to every TablePanel that has a valid ID attribute
    for (int i = 0; i<panelList.size(); i++) {
      TablePanel p = (TablePanel) panelList.get(i);
      p.setEditor();
    }

    //set the displayed tab to be by default the first image
    TabPanel firstImageTab = null;
    for (int i=0; i<tabPanelList.size(); i++) {
      TabPanel tabP = (TabPanel) tabPanelList.get(i);
      if (tabP.name.startsWith("Image")) {
        firstImageTab = tabP;
        break;
      }
    }
    if (firstImageTab != null) {
      Container anObj = (Container) firstImageTab;
      while (!(anObj instanceof JScrollPane)) {
        anObj = anObj.getParent();
      }
      JScrollPane jScr = (JScrollPane) anObj;
      tabPane.setSelectedComponent(jScr);
    }
  }

  /**
   * Fleshes out the GUI of a given TabPanel, adding TablePanels appropriately.
   */
  public void renderTab(TabPanel tp) {
    if (tp.isRendered) return;
    tp.isRendered = true;
    tp.removeAll();

    Vector iHoldTables = new Vector();

    //add a title label to show which element
    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new GridLayout(2, 1));
    JLabel title = new JLabel();
    Font thisFont = title.getFont();
    Font newFont = new Font(thisFont.getFontName(), Font.BOLD, 18);
    title.setFont(newFont);
    if (tp.oNode != null) {
      title.setText(" " + getTreePathName(tp.el, tp.oNode) + ":");
    }
    else title.setText(" " + getTreePathName(tp.el) + ":");
    title.setForeground(new Color(255, 255, 255));

    tp.saveButton = new JButton("QuickSave");
    tp.saveButton.setPreferredSize(new Dimension(100, 17));
    tp.saveButton.setActionCommand("save");
    tp.saveButton.addActionListener(this);
    tp.saveButton.setOpaque(false);
    tp.saveButton.setForeground(TEXT_COLOR);
    if (getState()) tp.saveButton.setForeground(ADD_COLOR);
    if (!addSave) tp.saveButton.setVisible(false);

    Color aColor = getBackground();

    JTextArea descrip = new JTextArea();

    //if title has a description, add it in italics
    if (tp.el.hasAttribute("Description")) {
      if (tp.el.getAttribute("Description").length() != 0) {
        descrip.setEditable(false);
        descrip.setLineWrap(true);
        descrip.setWrapStyleWord(true);
        descrip.setBackground(aColor);
        newFont = new Font(thisFont.getFontName(),
          Font.ITALIC, thisFont.getSize());
        descrip.setFont(newFont);
        descrip.setText("     " + tp.el.getAttribute("Description"));
      }
    }

    FormLayout myLayout = new FormLayout(
      "pref, 5dlu, pref:grow:right, 5dlu",
      "5dlu, pref, 5dlu, pref");
    PanelBuilder build = new PanelBuilder(myLayout);
    CellConstraints cellC = new CellConstraints();

    build.add(title, cellC.xy(1, 2, "left, center"));
    build.add(tp.saveButton, cellC.xy(3, 2, "right, center"));
    build.add(descrip, cellC.xyw(1, 4, 4, "fill, center"));
    titlePanel = build.getPanel();
    titlePanel.setBackground(TEXT_COLOR);

    //this sets up titlePanel so we can access its height later to
    //use for "Goto" button scrollpane view setting purposes
    tp.titlePanel = titlePanel;

    //First instantiation of TablePanel. This one corresponds to the
    //actual "top-level" element, e.g. what the main Tab element is
    TablePanel pan = new TablePanel(tp.el, tp, tp.oNode);
    iHoldTables.add(pan);

    //make the nested elements have a further indent to distinguish them

    //look at the template to get the nested Elements we need to display
    //with their own TablePanels
    Vector theseElements = DOMUtil.getChildElements("OMEElement", tp.el);
    //will be a list of those nested elements that have their own nested
    //elements
    Vector branchElements = new Vector(theseElements.size());

    //check out each nested Element
    for (int i = 0; i<theseElements.size(); i++) {
      Element e = null;
      if (theseElements.get(i) instanceof Element) {
        e = (Element) theseElements.get(i);
      }
      if (DOMUtil.getChildElements("OMEElement", e).size() != 0) {
        branchElements.add(e);
      }
      else {
        if (tp.oNode != null) {
          Vector v = new Vector();
          String aName = e.getAttribute("XMLName");
          if (aName.equals("Image") || aName.equals("Feature") ||
            aName.equals("Dataset") || aName.equals("Project"))
          {
            v = DOMUtil.getChildElements(aName, tp.oNode.getDOMElement());
          }
          else if (tp.oNode.getChildNode("CustomAttributes") != null) {
            v = DOMUtil.getChildElements(aName,
              tp.oNode.getChildNode("CustomAttributes").getDOMElement());
          }

          if (v.size() == 0) {
            OMEXMLNode n = null;
            TablePanel p = new TablePanel(e, tp, n);
            iHoldTables.add(p);
          }
          else {
            for (int j = 0; j<v.size(); j++) {
              Element anEle = (Element) v.get(j);

              OMEXMLNode n = null;
              String unknownName = aName;

              try {
                ReflectedUniverse r = new ReflectedUniverse();
                if (unknownName.equals("Project") ||
                  unknownName.equals("Feature") ||
                  unknownName.equals("CustomAttributes") ||
                  unknownName.equals("Dataset") ||
                  unknownName.equals("Image"))
                {
                  r.exec("import org.openmicroscopy.xml." +
                    unknownName + "Node");
                  r.setVar("DOMElement", anEle);
                  r.exec("result = new " + unknownName + "Node(DOMElement)");
                  n = (OMEXMLNode) r.getVar("result");
                }
                else {
                    r.exec("import org.openmicroscopy.xml.st." +
                      unknownName + "Node");
                    r.setVar("DOMElement", anEle);
                    r.exec("result = new " + unknownName + "Node(DOMElement)");
                    n = (OMEXMLNode) r.getVar("result");
                }
              }
              catch (Exception exc) {
                //System.out.println(exc.toString());
              }
              if (n == null) {
                n = new AttributeNode(anEle);
              }

              TablePanel p = new TablePanel(e, tp, n);
              iHoldTables.add(p);
            }
          }
        }
        else {
          OMEXMLNode n = null;

          TablePanel p = new TablePanel(e, tp, n);
          iHoldTables.add(p);
        }
      }
    }

    String rowString = "pref, 10dlu, ";
    for (int i = 0; i<iHoldTables.size(); i++) {
      rowString = rowString + "pref, 5dlu, ";
    }
    rowString = rowString.substring(0, rowString.length() - 2);

    FormLayout layout = new FormLayout(
      "5dlu, 5dlu, pref:grow, 5dlu, 5dlu",
      rowString);
    tp.setLayout(layout);
    CellConstraints cc = new CellConstraints();

    tp.add(titlePanel, cc.xyw(1, 1, 5));

    int row = 1;
    for (int i = 0; i<iHoldTables.size(); i++) {
      row = row + 2;
      Component c = (Component) iHoldTables.get(i);
      tp.add(c, cc.xyw(i == 0 ? 2 : 3, row, 2, "fill, center"));
    }

    //Layout stuff distinguishes between the title and the data panels
  }

  /** changes the selected tab to tab of index i */
  public void tabChange(int i) {
    tabPane.setSelectedIndex(i);
  }

  /** resets all gui to reflect changes to the node tree*/
  public void reRender() {
    int tabIndex = tabPane.getSelectedIndex();
    setupTabs(thisOmeNode);
    tabPane.setSelectedIndex(tabIndex);
  }

  // -- Runnable API methods --

  /** Shows or hides the proper subpanes. */
  public void run() {
    tabPane.setVisible(!raw);
    rawPanel.setVisible(raw);
    validate();
    repaint();
  }

  // -- Event API methods --

  /** Handle the "QuickSave" button actions.*/
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("save")) {
      if (currentFile != null) {
        saveFile(currentFile);
        stateChanged(false);
      }
      else JOptionPane.showMessageDialog(getTopLevelAncestor(),
        "There is no current file specified, \n" +
        "so you cannot QuickSave.",
        "No Current File Found", JOptionPane.ERROR_MESSAGE);
    }
  }

  // -- Static methods --

  /** Returns an ImageIcon, or null if the path was invalid. */
  protected static ImageIcon createImageIcon(String path, String description) {
    java.net.URL imgURL = MetadataPane.class.getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL, description);
    }
    else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  /** Get a path name for an element in the template.*/
  public static String getTreePathName(Element e) {
    String thisName = null;
    if (e.hasAttribute("Name"))thisName = e.getAttribute("Name");
    else thisName = e.getAttribute("XMLName");

    Element aParent = DOMUtil.getAncestorElement("OMEElement", e);
    while (aParent != null) {
      if (aParent.hasAttribute("Name")) {
        thisName = aParent.getAttribute("Name") + thisName;
      }
      else thisName = aParent.getAttribute("XMLName") + ": " + thisName;
      aParent = DOMUtil.getAncestorElement("OMEElement", aParent);
    }
    return thisName;
  }

  /**
  * Get a path name for a given OMEXMLNode found in the current
  * file being edited based on the template. Only by calling this
  * method will duplicate OMEXMLNodes be accounted for, e.g. this
  * method adds Project (1) or Project (2) to signify multiple
  * elements.
  * @param el The template element that corresponds to this OMEXMLNode.
  * @param on The OMEXMLNode that we want the path name for.
  * @return A string representing the OMEXMLNode's path name.
  */
  public static String getTreePathName(Element el, OMEXMLNode on) {
    if (el != null && on != null) {
      Vector pathList = new Vector();
      Element aParent = on.getDOMElement();
      Vector pathNames = getTreePathList(el);
      pathNames.add("OME");
      pathList.add(aParent);

      for (int i = 1; i<pathNames.size(); i++) {
        String s = (String) pathNames.get(i);
        aParent = DOMUtil.getAncestorElement(s, aParent);
        pathList.add(0, aParent);
      }

      String result = "";

      for (int i = 0; i<pathList.size() - 1; i++) {
        aParent = (Element) pathList.get(i);
        Element aChild = (Element) pathList.get(i+1);
        String thisName = aChild.getTagName();

        NodeList nl = aParent.getElementsByTagName(thisName);

        if (nl.getLength() == 1) {
          Element e = (Element) nl.item(0);
          if (i == 0) result = result + e.getTagName();
          else result = result + ": " + e.getTagName();
        }
        else {
          for (int j = 0; j<nl.getLength(); j++) {
            Element e = (Element) nl.item(j);
            if (e == aChild) {
              Integer aInt = new Integer(j+1);
              if (!result.equals("")) {
                result += ": " + e.getTagName() + " (" + aInt + ")";
              }
              else result += e.getTagName() + " (" + aInt + ")";
            }
          }
        }
      }
      return result;
    }
    else return null;
  }

  /**
   * Tests if the given tagname should be
   * placed under a CustomAttributesNode.
   */
  public static boolean isInCustom(String tagName) {
    if (tagName.equals("Project") ||
      tagName.equals("Feature") ||
      tagName.equals("CustomAttributes") ||
      tagName.equals("Dataset") ||
      tagName.equals("Image"))
    {
      return false;
    }
    else return true;
  }

  /**
   * Return a new node of type specified by unknownName
   * with the specified parent.
   * N.B. The Parent can either be the direct parent or the
   * ancestor that has the CustomAttributesNode that
   * is the real parent.
   * @param unknownName The name this new OMEXMLNode should have.
   * @param parent The parent of the new OMEXMLNode. NEVER pass in
   * a CustomAttributesNode, but rather the parent of that CANode.
   */
  public static OMEXMLNode makeNode(String unknownName, OMEXMLNode parent) {
    OMEXMLNode n = null;
    CustomAttributesNode caNode = null;

    try {
      ReflectedUniverse r = new ReflectedUniverse();
      if (!isInCustom(unknownName)) {
        r.exec("import org.openmicroscopy.xml." +
          unknownName + "Node");
        r.setVar("parent", parent);
        r.exec("result = new " + unknownName + "Node(parent)");
        n = (OMEXMLNode) r.getVar("result");
      }
      else {
        caNode = (CustomAttributesNode) parent.getChildNode("CustomAttributes");
        if (caNode != null) {
          r.exec("import org.openmicroscopy.xml.CustomAttributesNode");
          r.exec("import org.openmicroscopy.xml.st." +
            unknownName + "Node");
          r.setVar("parent", caNode);
          r.exec("result = new " + unknownName + "Node(parent)");
          n = (OMEXMLNode) r.getVar("result");
        }
        else {
          Element cloneEle = DOMUtil.createChild(
            parent.getDOMElement(), "CustomAttributes");
          caNode = new CustomAttributesNode(cloneEle);
          r.exec("import org.openmicroscopy.xml.CustomAttributesNode");
          r.exec("import org.openmicroscopy.xml.st." +
            unknownName + "Node");
          r.setVar("parent", caNode);
          r.exec("result = new " + unknownName + "Node(parent)");
          n = (OMEXMLNode) r.getVar("result");
        }
      }
    }
    catch (Exception exc) {
      //System.out.println(exc.toString());
    }
    if (caNode != null && n == null) n = new AttributeNode(caNode, unknownName);
    return n;
  }

  /**
   * Returns a vector of Strings representing the XMLNames of the
   * template's ancestors in ascending order in the list.
   * @param e The template element we want the path list for.
   * @return A vector holding the tagnames of all parent elements.
   */
  public static Vector getTreePathList(Element e) {
    Vector thisPath = new Vector(10);
    thisPath.add(e.getAttribute("XMLName"));

    Element aParent = DOMUtil.getAncestorElement("OMEElement", e);
    while (aParent != null) {
      thisPath.add(aParent.getAttribute("XMLName"));
      aParent = DOMUtil.getAncestorElement("OMEElement", aParent);
    }
    return thisPath;
  }

  /**Converts a number into the KeyEvent for that number.*/
  public static int getKey(int i) {
    int keyNumber = 0;
    switch (i) {
      case 1:
        keyNumber = KeyEvent.VK_1;
        break;
      case 2:
        keyNumber = KeyEvent.VK_2;
        break;
      case 3:
        keyNumber = KeyEvent.VK_3;
        break;
      case 4:
        keyNumber = KeyEvent.VK_4;
        break;
      case 5:
        keyNumber = KeyEvent.VK_5;
        break;
      case 6:
        keyNumber = KeyEvent.VK_6;
        break;
      case 7:
        keyNumber = KeyEvent.VK_7;
        break;
      case 8:
        keyNumber = KeyEvent.VK_8;
        break;
      case 9:
        keyNumber = KeyEvent.VK_9;
        break;
      case 10:
        keyNumber = KeyEvent.VK_0;
        break;
      default:
        keyNumber = 0;
    }
    return keyNumber;
  }

  // -- Helper classes --

  /**
   * Helper class to make my life easier in the creation and use of tabs
   * associates a given xml template element and also an optional OMEXMLNode
   * with a JPanel that represents the content of a tab.
   */
  public class TabPanel extends JPanel
    implements Scrollable
  {
    /**The template element this TabPanel corresponds to.*/
    protected Element el;

    /**The name of this TabPanel.*/
    public String name;

    /**Whether or not this TabPanel has already been rendered.*/
    private boolean isRendered;

    /**
    * The OMEXMLNode in the current file that this TabPanel
    * corresponds to.
    */
    protected OMEXMLNode oNode;

    /** The OMENode associated with this file*/
    protected OMENode ome;

    /** The JPanel that holds this TabPanel's title header.*/
    protected JPanel titlePanel;

    /** The "QuickSave" button for this TabPanel*/
    protected JButton saveButton;

    /** Construct a TabPanel arround a given template Element.*/
    public TabPanel(Element el) {
      ome = thisOmeNode;
      isRendered = false;
      this.el = el;
      oNode = null;
      name = getTreePathName(el);
      titlePanel = null;
      tabPanelList.add(this);
      saveButton = null;
    }

    /** Convert this TabPanel into a String.*/
    public String toString() { return el == null ? "null" :
      "Name: " + name + " Element: " + el.getTagName(); }

    /** Implement these scrollable methods to make resizing behave.*/
    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
      int orientation, int direction)
    {
      return 5;
    }
    public int getScrollableBlockIncrement(Rectangle visibleRect,
      int orientation, int direction)
    {
      return visibleRect.height;
    }
    public boolean getScrollableTracksViewportWidth() {
      return true;
    }
    public boolean getScrollableTracksViewportHeight() {
      return false;
    }
  }

  /**
   * Helper class to handle the various TablePanels that will be created to
   * display the attributes of Elements that have no nested Elements
   */
  public class TablePanel extends JPanel
    implements ActionListener, MouseListener
  {
    /**The OMEXMLNode that this TablePanel is displaying.*/
    public OMEXMLNode oNode;

    /**The TabPanel that this TablePanel is a part of.*/
    public TabPanel tPanel;

    /**The NotePanel that holds the notes for this TablePanel.*/
    public NotePanel noteP;

    /**
    * The "ID" OMEXML attribute for the OMEXMLNode this TablePanel
    * displays.
    */
    public String id;

    /**The name of this TablePanel.*/
    public String name;

    /**
    * An String to hold misc data we want to display to distinguish
    * the name presented for this as an internal reference.
    */
    public String refDetails;

    /**The ClickableTable that displays this TablePanel's metadata.*/
    public ClickableTable table;

    /**The tableheader of this TablePanel's ClickableTable*/
    JTableHeader tHead;

    /**The template element that this TablePanel corresponds to.*/
    public Element el;

    /**Indicates whether or not this is a nested OMEXMLNode*/
    public boolean isTopLevel;

    /**
    * The notes, add table, and delete table buttons
    * of this TablePanel.
    */
    protected JButton noteButton, addButton, delButton;

    /**A list of non "Ref" type attributes.*/
    protected Vector attrList;

    /**A list of "Ref" type attributes.*/
    protected Vector refList;

    /**The JLabels for the title and the optional image.*/
    protected JLabel tableName, imageLabel;

    protected BufferedImage tableThumb;

    protected BufferedImage tableImage;

    /**
    * Construct a TablePanel to display the metadata of a
    * particular OMEXMLNode.
    * @param e The template element this TablePanel corresponds to.
    * @param tp The TabPanel this TablePanel is a part of.
    * @param on The OMEXMLNode to be displayed.
    */
    public TablePanel(Element e, TabPanel tp, OMEXMLNode on) {
      isTopLevel = false;

      //check if this TablePanel is "top level"
      if (tp.oNode == null) {
        Vector foundEles = DOMUtil.getChildElements("OMEElement",
          tParse.getRoot());

        for (int i = 0; i < foundEles.size(); i++) {
          Element thisNode = (Element) foundEles.get(i);
          if (thisNode == e) isTopLevel = true;
        }
      }
      else if (tp.oNode != null && tp.oNode == on) isTopLevel = true;

      el = e;
      oNode = on;
      tPanel = tp;
      id = null;
      JComboBox comboBox = null;
      if (on != null) name = getTreePathName(e, on);
      else name = getTreePathName(e);
      String thisName = name;
      panelList.add(this);

      //for debuging this simple parser
      final boolean debug = false;

      //Check which "types" the various template attributes are and
      //group them into Vectors.
      Vector fullList = DOMUtil.getChildElements("OMEAttribute", e);
      attrList = new Vector();
      refList = new Vector();
      for (int i = 0; i<fullList.size(); i++) {
        Element thisE = (Element) fullList.get(i);
        if (thisE.hasAttribute("Type")) {
          if (thisE.getAttribute("Type").equals("Ref")) {
            if (oNode != null) {
              String value = oNode.getAttribute(thisE.getAttribute("XMLName"));
              if (value != null && !value.equals("")) {
                if (addItems.indexOf("(External) " + value) < 0) {
                  addItems.add("(External) " + value);
                }
              }
            }
            refList.add(thisE);
          }
          else if (thisE.getAttribute("Type").equals("ID") && oNode != null
            && !showIDs) {
            if (oNode.getDOMElement().hasAttribute("ID")) {
              id = oNode.getAttribute("ID");
              panelsWithID.add(this);
            }
          }
          else if (thisE.getAttribute("Type").equals("ID") && oNode != null
            && showIDs) {
            if (oNode.getDOMElement().hasAttribute("ID")) {
              id = oNode.getAttribute("ID");
              panelsWithID.add(this);
              attrList.add(thisE);
            }
          }
          else attrList.add(thisE);
        }
        else attrList.add(thisE);
      }

      //Set up the details for internal reference names
      refDetails = e.getAttribute("RefVars");
      if (debug) System.out.println();
      if (debug)   System.out.println(name + " - " + refDetails);
      boolean noDetails = true;
      int openIndex = refDetails.indexOf('%');
      while (openIndex >= 0) {
        if (debug) {
          System.out.println(openIndex + " " + refDetails.charAt(openIndex));
        }
        int closeIndex = refDetails.indexOf('%', openIndex + 1);
        if (debug) {
          System.out.println(closeIndex + " " + refDetails.charAt(closeIndex));
        }
        String thisCommand = refDetails.substring(openIndex + 1, closeIndex);
        if (debug) {
          System.out.println("Command: " + thisCommand);
        }
        String processed = refDetails.substring(0, openIndex);
        if (debug) {
          System.out.println("Processed: " + processed);
        }
        String remnants = refDetails.substring(closeIndex + 1,
          refDetails.length());
        if (debug) System.out.println("Remnants: " + remnants);

        boolean addThisCommand = false;
        int varIndex = thisCommand.indexOf('$');
        while (varIndex >=0) {
          if (debug) {
            System.out.println("varIndex: " +
              varIndex + " " + thisCommand.charAt(varIndex));
          }
          int endIndex = thisCommand.indexOf(' ', varIndex + 1);
          if (endIndex < 0) endIndex = thisCommand.length();
          if (debug) System.out.println("endIndex: " + endIndex);
          String prefix = thisCommand.substring(0, varIndex);
          if (debug) System.out.println("Prefix: " + prefix);
          String thisVar = thisCommand.substring(varIndex+1, endIndex);
          if (debug) System.out.println("thisVar: " + thisVar);
          String suffix;
          if (endIndex != thisCommand.length())
            suffix = thisCommand.substring(endIndex + 1, thisCommand.length());
          else suffix = "";
          if (debug) System.out.println("Suffix: " + suffix);
          String value = null;
          if (oNode != null) {
            value = oNode.getAttribute(thisVar);
          }
          if (value != null) {
            if (!value.equals("")) {
              addThisCommand = true;
            }
          }
          else value = "";
          if (debug) System.out.println("Value: " + value);
          thisCommand = prefix + value + suffix;
          if (debug) System.out.println("thisCommand: " + thisCommand);

          varIndex = thisCommand.indexOf('$');
        }
        if (addThisCommand) {
          noDetails = false;
          refDetails = processed + " (" + thisCommand + ")" + remnants;
        }
        else refDetails = processed + remnants;
        if (debug) System.out.println("refDetails: " + refDetails);

        openIndex = refDetails.indexOf('%');
      }
      if (debug) System.out.println(name + " - " + refDetails);
      if (showIDs) refDetails = refDetails + " (ID: " + id + ")";

      Element cDataEl = DOMUtil.getChildElement("CData", e);
      if (cDataEl != null) attrList.add(0, cDataEl);

      tableName = null;
      if (oNode == null) tableName =
        new JLabel(thisName, NO_DATA_BULLET, JLabel.LEFT);
      else tableName = new JLabel(thisName, DATA_BULLET, JLabel.LEFT);
      Font thisFont = tableName.getFont();
      thisFont = new Font(thisFont.getFontName(),
        Font.BOLD, 12);
      tableName.setFont(thisFont);
      if (el.hasAttribute("ShortDesc"))
        tableName.setToolTipText(el.getAttribute("ShortDesc"));
      else if (el.hasAttribute("Description"))
        tableName.setToolTipText(el.getAttribute("Description"));
      tableName.setForeground(TEXT_COLOR);

      noteButton = new JButton("Notes");
//      noteButton.setPreferredSize(new Dimension(85, 17));
      noteButton.addActionListener(this);
      noteButton.setActionCommand("getNotes");
      noteButton.setToolTipText(
        "Display or hide the notes associated with this " + name + ".");
      noteButton.setForeground(TEXT_COLOR);

      imageLabel = null;
      if (name.endsWith("Pixels")) {
        if (pixelsIDProblem || on == null) {
          if (thumb != null && !pixelsIDProblem) {
            tableThumb = thumb;
            tableImage = img;
            imageLabel = new JLabel(new ImageIcon(tableThumb));
            imageLabel.setToolTipText("The middle image of these pixels." +
              " Click for full sized image.");
            imageLabel.addMouseListener(this);
          }
          if (pixelsIDProblem) JOptionPane.showMessageDialog(this,
            "Thumbnails disabled due to multiple pixels with unsupported"
              + " ID naming scheme.",
            "MetadataEditor Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
          String thisID = on.getAttribute("ID");
          int colonIndex = thisID.indexOf(":");
          String pixNumString = thisID.substring(colonIndex + 1);
          int pixNum = -1;
          try {
            pixNum = Integer.parseInt(pixNumString);
            int indexNum = pixNum - minPixNum;
            tableThumb = thumbs == null ? null : thumbs[indexNum];
            tableImage = images == null ? null : images[indexNum];
          }
          catch (java.lang.NumberFormatException exc) {
            //this happens when multiple pixels aren't present
            //so we want to show just the one thumb
            tableThumb = thumb;
            tableImage = img;
          }
          imageLabel = tableThumb == null ? new JLabel() :
            new JLabel(new ImageIcon(tableThumb));
          imageLabel.setToolTipText("The middle image of these pixels." +
            " Click for full sized image.");
          imageLabel.addMouseListener(this);
        }
      }

      DefaultTableModel myTableModel =
        new DefaultTableModel(TREE_COLUMNS, 0)
      {
        public boolean isCellEditable(int row, int col) {
          return col >= (editable ? 1 : 2);
        }
      };

      VariableComboEditor vcEdit = new VariableComboEditor(panelsWithID,
          addItems, this, internalDefs);

      table = new ClickableTable(myTableModel, this, vcEdit);
//      table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      table.getColumnModel().getColumn(0).setPreferredWidth(125);
      table.getColumnModel().getColumn(1).setPreferredWidth(430);
      table.getColumnModel().getColumn(2).setPreferredWidth(70);
      table.getColumnModel().getColumn(2).setMaxWidth(70);
      table.getColumnModel().getColumn(2).setMinWidth(70);
      tHead = table.getTableHeader();
      tHead.setResizingAllowed(true);
      tHead.setReorderingAllowed(false);
//      tHead.setBackground(NotePanel.BACK_COLOR);
//      tHead.setOpaque(false);
      myTableModel.setRowCount(attrList.size() + refList.size());

      String clippedName = name;
      if (name.endsWith(")")) {
        clippedName = name.substring(0, name.length() - 4);
      }

      addButton = new JButton("New Table");
//      addButton.setPreferredSize(new Dimension(130, 17));
      addButton.addActionListener(table);
      addButton.setActionCommand("bigAdd");
      addButton.setToolTipText("Create a new " + clippedName + " table.");
      if (!isTopLevel && tPanel.oNode == null) addButton.setEnabled(false);
      if (!editable) addButton.setEnabled(false);
      addButton.setForeground(ADD_COLOR);

      delButton = new JButton("Delete Table");
//      delButton.setPreferredSize(new Dimension(130, 17));
      delButton.addActionListener(table);
      delButton.setActionCommand("bigRem");
      delButton.setToolTipText("Delete this " + clippedName + " table.");
      if (oNode == null) delButton.setVisible(false);
      if (!editable) delButton.setEnabled(false);
      delButton.setForeground(DELETE_COLOR);

      noteP = new NotePanel(this);
      setNumNotes(noteP.getNumNotes());

      FormLayout layout = new FormLayout(
        "pref, 10dlu, pref, 10dlu, pref, pref:grow:right, 5dlu, pref",
        "pref, 2dlu, pref, pref, 3dlu, pref, 3dlu");
      setLayout(layout);
      CellConstraints cc = new CellConstraints();

      add(tableName, cc.xy(1, 1));
      add(noteButton, cc.xy(3, 1, "left, center"));
      if (imageLabel != null) {
        add(imageLabel, cc.xy(5, 1, "center, top"));
      }
      add(addButton, cc.xy(6, 1, "right, center"));
      add(delButton, cc.xy(8, 1, "right, center"));
      add(tHead, cc.xyw(1, 3, 8, "fill, center"));
      add(table, cc.xyw(1, 4, 8, "fill, center"));
      add(noteP, cc.xyw(1, 6, 8, "fill, center"));

      if (oNode == null) {
        tHead.setVisible(false);
        noteButton.setVisible(false);
        table.setVisible(false);
      }

      if (attrList.size() != 0) {
        // update OME-XML attributes table
        for (int i=0; i<attrList.size(); i++) {
          Element thisEle = null;
          if (attrList.get(i) instanceof Element) {
            thisEle = (Element) attrList.get(i);
          }
          if (thisEle != null) {
            String attrName = thisEle.getAttribute("XMLName");
            if (thisEle.hasAttribute("Name")) {
              myTableModel.setValueAt(thisEle.getAttribute("Name"), i, 0);
              if (oNode != null) {
                if (oNode.getDOMElement().hasAttribute(attrName)) {
                  myTableModel.setValueAt(oNode.getAttribute(attrName), i, 1);
                }

              }
            }
            else if (!thisEle.hasAttribute("Name") &&
              thisEle.hasAttribute("XMLName")) {
              myTableModel.setValueAt(thisEle.getAttribute("XMLName"), i, 0);
              if (oNode != null) {
                if (oNode.getDOMElement().hasAttribute(attrName)) {
                  myTableModel.setValueAt(oNode.getAttribute(attrName), i, 1);
                }

              }
            }
            else {
              if (e.hasAttribute("Name")) {
                myTableModel.setValueAt(e.getAttribute("Name") +
                  " CharData", i, 0);
              }
              else {
                myTableModel.setValueAt(e.getAttribute("XMLName") +
                  " CharData", i, 0);
              }
              if (oNode != null) {
                if (DOMUtil.getCharacterData(oNode.getDOMElement()) != null) {
                  myTableModel.setValueAt(
                    DOMUtil.getCharacterData(oNode.getDOMElement()), i, 1);
                }
              }
            }
          }
        }
      }

      if (refList.size() > 0) {
        for (int i=0; i<refList.size(); i++) {
          Element thisEle = null;
          if (refList.get(i) instanceof Element) {
            thisEle = (Element) refList.get(i);
          }
          if (thisEle != null) {
            if (thisEle.hasAttribute("Name")) {
              myTableModel.setValueAt(thisEle.getAttribute("Name"),
                i + attrList.size(), 0);
            }
            else if (thisEle.hasAttribute("XMLName")) {
              myTableModel.setValueAt(thisEle.getAttribute("XMLName"),
                i + attrList.size(), 0);
            }
          }
        }
      }
    }

    /**
    * A method to initialize the reference combo boxes to the value
    * found in the current file. First sets the Vectors of values
    * that are possible for the combobox.
    */
    public void setEditor() {
      if (table != null) {
        //This resets the possible values of the comboboxes.
        table.setDefs(panelsWithID, addItems);
        TableModel model = table.getModel();
        TableColumn refColumn = table.getColumnModel().getColumn(1);
        for (int i = 0; i < table.getRowCount(); i++) {
          if (i < attrList.size()) {
          }
          else {
            boolean isLocal = false;
            String attrName = (String) model.getValueAt(i, 0);
            String value = null;
            if (oNode != null) value = oNode.getAttribute(attrName);
            for (int j = 0; j < panelsWithID.size(); j++) {
              TablePanel tp = (TablePanel) panelsWithID.get(j);
              if (tp.id != null && value != null) {
                if (value.equals(tp.id)) {
                  isLocal = true;
                  if (tp.refDetails != null && !tp.refDetails.equals(""))
                    model.setValueAt(tp.name + " - " + tp.refDetails, i, 1);
                  else model.setValueAt(tp.name, i, 1);
                }
              }
            }
            if (!isLocal && value != null && !value.equals("")) {
              model.setValueAt("(External) " + value, i, 1);
            }
            //makes the initial value non-null to display the buttons
            model.setValueAt("foobar", i, 2);
          }
        }
      }
    }

    /**Set the number of notes displayed on the "Notes" button.*/
    public void setNumNotes(int n) {
      noteButton.setText("Notes (" + n + ")");
    }

    /**
    * Handles the actions of the "Goto" buttons and also
    * the "Notes" button.
    */
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof GotoEditor.TableButton) {
        GotoEditor.TableButton tb = (GotoEditor.TableButton) e.getSource();
        JTable jt = tb.table;

        TableModel model = jt.getModel();
        Object obj = model.getValueAt(tb.whichRow, 1);
        if (obj != null && !obj.toString().equals("")) {
          String aName = obj.toString();
          TablePanel aPanel = null;

          int whichNum = -23;

          for (int i = 0; i<panelsWithID.size(); i++) {
            aPanel = (TablePanel) panelsWithID.get(i);
            if (aName.startsWith(aPanel.name)) whichNum = i;
          }

          if (whichNum != -23) {
            TablePanel tablePan = (TablePanel) panelsWithID.get(whichNum);
            TabPanel tp = tablePan.tPanel;
            Container anObj = (Container) tp;
            while (!(anObj instanceof JScrollPane)) {
              anObj = anObj.getParent();
            }
            JScrollPane jScr = (JScrollPane) anObj;
            while (!(anObj instanceof JTabbedPane)) {
              anObj = anObj.getParent();
            }
            JTabbedPane jTabP = (JTabbedPane) anObj;
            jTabP.setSelectedComponent(jScr);
            Point loc = tablePan.getLocation();
            loc.x = 0;
            JViewport jView = jScr.getViewport();
            if (jView.getExtentSize().getHeight() <
              jView.getViewSize().getHeight())
                jScr.getViewport().setViewPosition(loc);
          }
          else {
            JOptionPane.showMessageDialog((Frame) getTopLevelAncestor(),
              "Since the ID in question refers to something\n" +
              "outside of this file, you cannot \"Goto\" it.",
              "External Reference Detected", JOptionPane.WARNING_MESSAGE);
          }
        }
        else {
          JOptionPane.showMessageDialog((Frame) getTopLevelAncestor(),
            "Since the ID in question is currently blank,\n" +
            "you cannot \"Goto\" it.",
            "Null Reference Detected", JOptionPane.WARNING_MESSAGE);
        }
      }
      else if (e.getActionCommand().equals("getNotes")) {
        if (noteP.isVisible()) noteP.setVisible(false);
        else noteP.setVisible(true);
        noteP.revalidate();
      }
    }

    /**
    * Handles the clicking of the icon of Image: Pixels.
    * If clicked, this icon will enlarge in a dialog.
    */
    public void mouseClicked(MouseEvent e) {
      if (e.getSource() instanceof JLabel) {
        JOptionPane.showMessageDialog(getTopLevelAncestor(), null,
          "(Full Sized) " + name, JOptionPane.PLAIN_MESSAGE,
          new ImageIcon(tableImage));
      }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    /** Call MetadataPane.reRender().*/
    public void callReRender() {
      reRender();
    }

    /** Call MetadataPane.stateChanged(boolean change)*/
    public void callStateChanged(boolean hasChanged) {
      stateChanged(true);
    }

    /** @return MetadataPane.currentFile*/
    public File getCurrentFile() { return currentFile; }

    /**
    * Checks whether this TablePanel should be editable.
    * @return MetadataPane.editable
    */
    public boolean isEditable() { return editable; }
  }

}
