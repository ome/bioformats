/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;

import org.openmicroscopy.xml.OMENode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A class that emulates the login and experiment setup screen
 * found in the program WiscScan, which acquires laser data and
 * records various things in OMEXML. Many things in WiscScan
 * itself seem completely broken at the present time and so
 * many of the fields which are disabled fall into the category
 * of "broken in WiscScan, not my fault." This view was designed
 * to make our biologists at UW feel less overwhelmed with the
 * multiple scary fields found in the normal view. Also it is
 * not at all clear in many cases how the OMEXML was generated
 * in the first place.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/WiscScanPane.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/WiscScanPane.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class WiscScanPane extends JTabbedPane
  implements ActionListener, ItemListener, DocumentListener
{

  /** Holds types of experiments available. */
  public static final Object [] EX_CHOICES = {"Time-lapse","4-D+",
    "PGI/Documentation",
    "Photoablation","Fluorescense-Lifetime","Spectral-Imaging",
    "FP","FRET","Screen","Immunocytochemistry Immunofluorescence",
    "FISH","Electrophysiology","Ion-Imaging","Colocalization",
    "FRAP","Photoactivation","Uncaging","Optical-Trapping",
    "Other"};

  /** Holds types of filter wheels. */
  public static final Object [] WHEE_CHOICES = {"None","1 TFI 650SP"};

  /** Holds types of filter holders. */
  public static final Object [] HO_CHOICES = {"None","1 TFI 650SP"};

  /** The various GUI input components for this program. */
  protected JComboBox groupBox, exB, wheeB, hoB;

  /** The various GUI input components for this program. */
  protected JCheckBox tiC, phC, pmtC;

  /** The various GUI input components for this program. */
  protected JTextField firstField,lastField,omeField,passField,
    emailField,prT, tempT, pocT, tapT, tiT,pmtT;

  /** The various GUI input components for this program. */
  protected JTextArea desA;

  /** The XML elements associated with the GUI input components. */
  protected Element exrEle,exEle,prEle,desEle,tiEle,pmtEle,phEle;

  /**
   * A toggle that turns off the action listening for the GUI
   * components when we are initializing them so that we don't
   * needlessly change XML to what it already is.
   */
  protected boolean setup;

  /** The OMENode for the document being edited. */
  protected OMENode ome;

  /** Construct a new WiscScanPane. */
  public WiscScanPane() {
    setup = false;
    ome = null;
    exrEle = null;
    exEle = null;
    prEle = null;
    desEle = null;
    tiEle = null;
    pmtEle = null;
    phEle = null;

    setPreferredSize(new Dimension(700, 500));
    JPanel loginPanel = new JPanel();
    JPanel experimentPanel = new ScrollablePanel();
    JScrollPane jScroll = new JScrollPane(experimentPanel);
    addTab("WiscScan Login", (Icon) null, loginPanel,
      "Emulates the login screen of WiscScan.");
    addTab("Experiment Setup Information", (Icon) null,
      jScroll, "Emulates the Experiment Setup Information"
      + " screen of WiscScan.");

    //gui setup for login screen
    int w = 300, h = 20;
    JLabel firstLabel = new JLabel("First Name");
    firstLabel.setPreferredSize(new Dimension(w,h));
    firstField = new JTextField();
    firstField.setPreferredSize(new Dimension(w,h));
    firstField.getDocument().addDocumentListener(this);
    JLabel lastLabel = new JLabel("Last Name");
    lastLabel.setPreferredSize(new Dimension(w,h));
    lastField = new JTextField();
    lastField.setPreferredSize(new Dimension(w,h));
    lastField.getDocument().addDocumentListener(this);
    JLabel omeLabel = new JLabel("OME Name (not supported)");
    omeLabel.setPreferredSize(new Dimension(w,h));
    omeField = new JTextField();
    omeField.setEnabled(false);
    omeField.setPreferredSize(new Dimension(w,h));
    omeField.getDocument().addDocumentListener(this);
    JLabel passLabel = new JLabel("Password (not supported)");
    passLabel.setPreferredSize(new Dimension(w,h));
    passField = new JTextField();
    passField.setEnabled(false);
    passField.setPreferredSize(new Dimension(w,h));
    passField.getDocument().addDocumentListener(this);
    JLabel emailLabel = new JLabel("Email");
    emailLabel.setPreferredSize(new Dimension(w,h));
    emailField = new JTextField();
    emailField.setPreferredSize(new Dimension(w,h));
    emailField.getDocument().addDocumentListener(this);
    JLabel groupLabel = new JLabel("Group (not supported)");
    groupLabel.setPreferredSize(new Dimension(w,h));
    groupBox = new JComboBox();
    groupBox.setPreferredSize(new Dimension(w,h));
    groupBox.addActionListener(this);
    groupBox.setEnabled(false);

    JPanel subPanel = null;

    FormLayout layout = new FormLayout(
        "5dlu,center:pref,5dlu",
        "5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref," +
          "5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu");
    PanelBuilder build = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();

    build.add(firstLabel,cc.xy(2,2));
    build.add(firstField,cc.xy(2,4));
    build.add(lastLabel,cc.xy(2,6));
    build.add(lastField,cc.xy(2,8));
    build.add(omeLabel,cc.xy(2,10));
    build.add(omeField,cc.xy(2,12));
    build.add(passLabel,cc.xy(2,14));
    build.add(passField,cc.xy(2,16));
    build.add(emailLabel,cc.xy(2,18));
    build.add(emailField,cc.xy(2,20));
    build.add(groupLabel,cc.xy(2,22));
    build.add(groupBox,cc.xy(2,24));

    subPanel = build.getPanel();

    Border lineB = BorderFactory.createMatteBorder(1,
      1, 1, 1, Color.BLACK);
    EmptyBorder emptyB = new EmptyBorder(5,5,5,5);
    EmptyBorder empB = new EmptyBorder(5,5,5,5);
    CompoundBorder tempB = new CompoundBorder(lineB,emptyB);
    CompoundBorder finalB = new CompoundBorder(empB,lineB);
    subPanel.setBorder(finalB);

    JPanel holderP = new JPanel();
    holderP.add(subPanel);

    JLabel welcomeLabel = new JLabel("Welcome To WiscScan",
      JLabel.CENTER);
//    Font thisFont = welcomeLabel.getFont();
    Font thisFont = new Font("Serif",
      Font.PLAIN,64);
    welcomeLabel.setFont(thisFont);

    loginPanel.setLayout(new BorderLayout());
    loginPanel.add(welcomeLabel, BorderLayout.NORTH);
    loginPanel.add(holderP, BorderLayout.CENTER);

    //gui setup for experiment setup screen

    Border etchB = BorderFactory.createEtchedBorder(
      EtchedBorder.LOWERED);
    TitledBorder infoB = BorderFactory.createTitledBorder(
      etchB, "Experiment Information");
    TitledBorder filterB = BorderFactory.createTitledBorder(
      etchB, "Filter");
    TitledBorder laserB = BorderFactory.createTitledBorder(
      etchB, "Laser");
    TitledBorder detB = BorderFactory.createTitledBorder(
      etchB, "Detector");

    JPanel infoP, filterP, laserP, detP;

    w = 250;

    JLabel exL = new JLabel("Experiment Type");
    JLabel prL = new JLabel("Project Name");
    JLabel desL = new JLabel("Description");
    JLabel tempL = new JLabel("Temperature");
    JLabel pocL = new JLabel("Pockel Cell");
    JLabel tapL = new JLabel("Tap Settings");

    JLabel wheeL = new JLabel("Wheel");
    JLabel hoL = new JLabel("Holder");

    prT = new JTextField();
    prT.setPreferredSize(new Dimension(w,h));
    prT.getDocument().addDocumentListener(this);
    tempT = new JTextField();
    tempT.setPreferredSize(new Dimension(w,h));
    tempT.setEnabled(false);
    pocT = new JTextField();
    pocT.setPreferredSize(new Dimension(w,h));
    pocT.setEnabled(false);
    tapT = new JTextField();
    tapT.setPreferredSize(new Dimension(w,h));
    tapT.setEnabled(false);

    tiT = new JTextField();
    tiT.setPreferredSize(new Dimension(80,h));
    tiT.setEnabled(false);

    pmtT = new JTextField();
    pmtT.setPreferredSize(new Dimension(80,h));
    pmtT.getDocument().addDocumentListener(this);

    desA = new JTextArea("",4,1);
    desA.setLineWrap(true);
    desA.setWrapStyleWord(true);
    desA.getDocument().addDocumentListener(this);
    JScrollPane desS = new JScrollPane(desA);
    desS.setPreferredSize(new Dimension(w,h*4));
    desS.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    exB = new JComboBox(EX_CHOICES);
    exB.setPreferredSize(new Dimension(w,h));
    exB.addActionListener(this);
    wheeB = new JComboBox(WHEE_CHOICES);
    wheeB.setEnabled(false);
    hoB = new JComboBox(HO_CHOICES);
    hoB.setEnabled(false);

    tiC = new JCheckBox("Ti-Sapphire");
    tiC.addItemListener(this);
    phC = new JCheckBox("Photodiode Bio-Rad 1024TLD");
    phC.addItemListener(this);
    pmtC = new JCheckBox("PMT Hamamatsu H7422");
    pmtC.addItemListener(this);

    FormLayout layoutF = new FormLayout(
        "5dlu,pref,5dlu,pref:grow,5dlu",
        "5dlu,pref,5dlu,pref,5dlu");
    PanelBuilder buildF = new PanelBuilder(layoutF);
    CellConstraints ccF = new CellConstraints();

    buildF.add(wheeL, ccF.xy(2,2));
    buildF.add(wheeB, ccF.xy(4,2));
    buildF.add(hoL, ccF.xy(2,4));
    buildF.add(hoB, ccF.xy(4,4));

    filterP = buildF.getPanel();
    filterP.setBorder(filterB);

    FormLayout layoutL = new FormLayout(
        "5dlu,pref,5dlu,pref,pref:grow, 5dlu",
        "5dlu,pref,25dlu");
    PanelBuilder buildL = new PanelBuilder(layoutL);
    CellConstraints ccL = new CellConstraints();

    buildL.add(tiC, cc.xy(2,2));
    buildL.add(tiT, cc.xy(4,2));

    laserP = buildL.getPanel();
    laserP.setBorder(laserB);

    FormLayout layoutD = new FormLayout(
        "5dlu,pref,5dlu,pref,pref:grow, 5dlu",
        "5dlu,pref,5dlu,pref,20dlu");
    PanelBuilder buildD = new PanelBuilder(layoutD);
    CellConstraints ccD = new CellConstraints();

    buildD.add(phC, cc.xy(2,2));
    buildD.add(pmtC, cc.xy(2,4));
    buildD.add(pmtT, cc.xy(4,4));

    detP = buildD.getPanel();
    detP.setBorder(detB);

    FormLayout layoutE = new FormLayout(
        "5dlu,pref,5dlu,pref,5dlu,",
        "5dlu,pref,5dlu,pref,5dlu,top:pref,5dlu,pref,5dlu,pref,"
        + "5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu");
    PanelBuilder buildE = new PanelBuilder(layoutE);
    CellConstraints ccE = new CellConstraints();

    buildE.add(exL, cc.xy(2,2));
    buildE.add(exB, cc.xy(4,2));
    buildE.add(prL, cc.xy(2,4));
    buildE.add(prT, cc.xy(4,4));
    buildE.add(desL, cc.xy(2,6));
    buildE.add(desS, cc.xy(4,6));
    buildE.add(tempL, cc.xy(2,8));
    buildE.add(tempT, cc.xy(4,8));
    buildE.add(pocL, cc.xy(2,10));
    buildE.add(pocT, cc.xy(4,10));
    buildE.add(tapL, cc.xy(2,12));
    buildE.add(tapT, cc.xy(4,12));
    buildE.add(filterP, cc.xyw(2,14,3));
    buildE.add(laserP, cc.xyw(2,16,3));
    buildE.add(detP, cc.xyw(2,18,3));

    infoP = buildE.getPanel();
    infoP.setBorder(infoB);

    experimentPanel.add(infoP);
  }

  /**
   * Set whether or not the user should be able to edit the
   * OMEXML in any document.
   */
  public void setEditable(boolean editable) {
//      groupBox.setEditable(editable);
      exB.setEnabled(editable);
//      wheeB.setEnabled(editable);
//      hoB.setEnabled(editable);
      tiC.setEnabled(editable);
      phC.setEnabled(editable);
      pmtC.setEnabled(editable);
      firstField.setEditable(editable);
      lastField.setEditable(editable);
//      omeField.setEditable(editable);
//      passField.setEditable(editable);
      emailField.setEditable(editable);
      prT.setEditable(editable);
//      tempT.setEditable(editable);
//      pocT.setEditable(editable);
//      tapT.setEditable(editable);
//      tiT.setEditable(editable);
      pmtT.setEditable(editable);
      desA.setEditable(editable);
  }

  /**
  * Update the OMEXML that this document is displaying/editing.
  * @param omeNode The OMENode of the current OMEXML document.
  */
  public void setOMEXML(OMENode omeNode) {
    ome = omeNode;
    setup = true;

    exrEle = null;
    exEle = null;
    prEle = null;
    desEle = null;
    tiEle = null;
    pmtEle = null;
    phEle = null;

    Document omeDoc = null;
    try {
      omeDoc = ome.getOMEDocument(true);
    }
    catch (Exception exc) {}

    Vector exrVector = DOMUtil.findElementList("Experimenter",omeDoc);
    String firstName = "",lastName = "",emailName = "";
    for (int i = 0;i<exrVector.size();i++) {
      Element ele = (Element) exrVector.get(i);
      if (ele.hasAttribute("FirstName")) {
        if (exrEle==null || exrEle == ele) {
          firstName = ele.getAttribute("FirstName");
          exrEle = ele;
        }
      }
      if (ele.hasAttribute("LastName")) {
        if (exrEle==null || exrEle == ele) {
          lastName = ele.getAttribute("LastName");
          exrEle = ele;
        }
      }
      if (ele.hasAttribute("Email")) {
        if (exrEle==null || exrEle == ele) {
          emailName = ele.getAttribute("Email");
          exrEle = ele;
        }
      }
    }
    firstField.setText(firstName);
    lastField.setText(lastName);
    emailField.setText(emailName);
    if (exrEle == null && exrVector.size() != 0) {
      exrEle = (Element) exrVector.get(0);
    }

    Vector exVector = DOMUtil.findElementList("Experiment",omeDoc);
    String exType = "Other";
    for (int i = 0;i<exVector.size();i++) {
      Element ele = (Element) exVector.get(i);
      if (ele.hasAttribute("Type")) {
        if (exEle==null || exEle == ele) {
          exType = ele.getAttribute("Type");
          exEle = ele;
        }
      }
    }
    Arrays.sort(EX_CHOICES);
    if (Arrays.binarySearch(EX_CHOICES, exType) >= 0)
      exB.setSelectedItem(exType);
    else exB.setSelectedItem("Other");
    if (exEle == null && exVector.size() != 0) {
      exEle = (Element) exVector.get(0);
    }

    Vector prVector = DOMUtil.findElementList("Project",omeDoc);
    String prName = null;
    for (int i = 0;i<prVector.size();i++) {
      Element ele = (Element) prVector.get(i);
      if (ele.hasAttribute("Name")) {
        if (prEle==null || prEle == ele) {
          prName = ele.getAttribute("Name");
          prEle = ele;
        }
      }
    }
    prT.setText(prName);
    if (prEle == null && prVector.size() != 0) {
      prEle = (Element) prVector.get(0);
    }

    Vector desVector = exVector;
    String desText = null;
    for (int i = 0;i<desVector.size();i++) {
      Element ele = (Element) desVector.get(i);
      if (ele.hasAttribute("Description")) {
        if (desEle==null || desEle == ele) {
          desText = ele.getAttribute("Description");
          desEle = ele;
        }
      }
    }
    desA.setText(desText);
    if (desEle == null && desVector.size() != 0) {
      desEle = (Element) desVector.get(0);
    }

    Vector tiVector = DOMUtil.findElementList("Laser",omeDoc);
    boolean tiToggle = false;
    for (int i = 0;i<tiVector.size();i++) {
      Element ele = (Element) tiVector.get(i);
      if (ele.hasAttribute("Medium")) {
        String attr = ele.getAttribute("Medium");
        if (attr.equals("Ti-Sapphire")) {
          if (tiEle==null || tiEle == ele) {
            tiToggle = true;
            tiEle = ele;
          }
        }
      }
    }
    tiC.setSelected(tiToggle);

    Vector phVector = DOMUtil.findElementList("Detector",omeDoc);
    boolean phToggle = false;
    for (int i = 0;i<phVector.size();i++) {
      Element ele = (Element) phVector.get(i);
      if (ele.hasAttribute("Type")) {
        String attr = ele.getAttribute("Type");
        if (attr.equals("Photodiode")) {
          if (phEle==null || phEle==ele) {
            phEle = ele;
            phToggle = true;
          }
        }
      }
    }
    phC.setSelected(phToggle);

    Vector pmtVector = phVector;
    boolean pmtToggle = false;
    for (int i = 0;i<pmtVector.size();i++) {
      Element ele = (Element) pmtVector.get(i);
      if (ele.hasAttribute("Type")) {
        String attr = ele.getAttribute("Type");
        if (attr.equals("PMT")) {
          if (pmtEle==null || pmtEle==ele) {
            pmtToggle = true;
            pmtEle = ele;
          }
        }
      }
    }
    pmtC.setSelected(pmtToggle);
    pmtT.setEnabled(pmtToggle);

    if (pmtEle != null && pmtEle.hasAttribute("Gain")) {
      pmtT.setText(pmtEle.getAttribute("Gain"));
    }

    setup = false;
  }

  // -- ActionListener API methods --

  /** Handle selections in the JComboBoxes. */
  public void actionPerformed(ActionEvent e) {
    if (setup) return;
    if (e.getSource() instanceof JComboBox) {
      JComboBox src = (JComboBox) e.getSource();
      if (src==exB) {
        if (exEle==null) {
          exEle = MetadataPane.makeNode("Experiment",
            ome).getDOMElement();
        }
        exEle.setAttribute("Type",(String) exB.getSelectedItem());
        setOMEXML(ome);
      }
    }
  }

  // -- ItemListener API methods --

  /** Handle changes with selection in the JCheckBoxes. */
  public void itemStateChanged(ItemEvent e) {
    if (setup) return;
    JCheckBox src = (JCheckBox) e.getItem();
    if (src==tiC) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        OMEXMLNode newNode = MetadataPane.makeNode("Laser",ome);
        newNode.setAttribute("Medium", "Ti-Sapphire");
        newNode.setAttribute("Type", "Solid State");
        tiEle = newNode.getDOMElement();
      }
      else {
        if (ome.getChildNode("CustomAttributes") != null) {
          Node thisNode = (Node) ome.getChildNode(
            "CustomAttributes").getDOMElement();
          thisNode.removeChild((Node)tiEle);
          tiEle = null;
        }
      }
    }
    else if (src==phC) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        OMEXMLNode newNode = MetadataPane.makeNode("Detector",ome);
        newNode.setAttribute("Manufacturer", "Bio-Rad");
        newNode.setAttribute("Model", "1024LD");
        newNode.setAttribute("Type", "Photodiode");
        phEle = newNode.getDOMElement();
      }
      else {
        if (ome.getChildNode("CustomAttributes") != null) {
          Node thisNode = (Node) ome.getChildNode(
            "CustomAttributes").getDOMElement();
          thisNode.removeChild((Node)phEle);
          phEle = null;
        }
      }
    }
    else if (src==pmtC) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        OMEXMLNode newNode = MetadataPane.makeNode("Detector",ome);
        newNode.setAttribute("Manufacturer", "Hamamatsu");
        newNode.setAttribute("Model", "H7422");
        newNode.setAttribute("Type", "PMT");
        pmtEle = newNode.getDOMElement();
        pmtT.setEnabled(true);
      }
      else {
        if (ome.getChildNode("CustomAttributes") != null) {
          Node thisNode = (Node) ome.getChildNode(
            "CustomAttributes").getDOMElement();
          thisNode.removeChild((Node)pmtEle);
          pmtEle = null;
          pmtT.setEnabled(false);
          pmtT.setText("");
        }
      }
    }
  }

  // -- DocumentListener API methods --

  /** Calls changeNode(e). */
  public void insertUpdate(DocumentEvent e) {changeNode(e);}

  /** Calls changeNode(e). */
  public void removeUpdate(DocumentEvent e) {changeNode(e);}

  /** Calls changeNode(e). */
  public void changedUpdate(DocumentEvent e) {changeNode(e);}

  /** Change the OMEXMLNode tree appropriately for a given DocumentEvent. */
  private void changeNode(DocumentEvent e) {
    if (setup) return;
    setup = true;
    try {
      if (e.getDocument() == desA.getDocument()) {
        if (desEle==null) {
          desEle = MetadataPane.makeNode("Experiment",
            ome).getDOMElement();
        }
        desEle.setAttribute("Description", e.getDocument().getText(0,
          e.getDocument().getLength()));
        setOMEXML(ome);
      }
      else if (e.getDocument() == prT.getDocument()) {
        if (prEle==null) {
          prEle = MetadataPane.makeNode("Project",
            ome).getDOMElement();
        }
        prEle.setAttribute("Name", e.getDocument().getText(0,
          e.getDocument().getLength()));
        setOMEXML(ome);
      }
      else if (e.getDocument() == pmtT.getDocument()) {
        if (pmtC != null)
          pmtEle.setAttribute("Gain", e.getDocument().getText(0,
          e.getDocument().getLength()));
        else pmtT.setText("");
      }
      else if (e.getDocument() == firstField.getDocument()) {
        if (exrEle==null) {
          exrEle = MetadataPane.makeNode("Experimenter"
            ,ome).getDOMElement();
        }
        exrEle.setAttribute("FirstName", e.getDocument().getText(0,
          e.getDocument().getLength()));
        setOMEXML(ome);
      }
      else if (e.getDocument() == lastField.getDocument()) {
        if (exrEle==null) {
          exrEle = MetadataPane.makeNode("Experimenter"
            ,ome).getDOMElement();
        }
        exrEle.setAttribute("LastName", e.getDocument().getText(0,
          e.getDocument().getLength()));
        setOMEXML(ome);
      }
      else if (e.getDocument() == emailField.getDocument()) {
        if (exrEle==null) {
          exrEle = MetadataPane.makeNode("Experimenter",
            ome).getDOMElement();
        }
        exrEle.setAttribute("Email", e.getDocument().getText(0,
          e.getDocument().getLength()));
        setOMEXML(ome);
      }
    }
    catch (Exception exc) {}
    setup = false;
  }

  // -- Helper classes --

  /**
   * Fixes the annoyingness that happens with JPanel expansion
   * within a JScrollPane. Resizing should work appropriately now.
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

}
