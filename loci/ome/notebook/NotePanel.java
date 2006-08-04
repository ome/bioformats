package loci.ome.notebook;

import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import org.openmicroscopy.xml.*;
import org.w3c.dom.*;
import javax.swing.event.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NotePanel extends JPanel 
  implements ListSelectionListener {

  public static final Color BACK_COLOR = 
    new Color(175,175,175);

  public static final ImageIcon NOTES_BULLET =
    MetadataPane.createImageIcon("Icons/Bullet3.gif",
      "An icon signifying that notes are present.");
      
  public static final ImageIcon NO_NOTES_BULLET =
    MetadataPane.createImageIcon("Icons/Bullet2.gif",
      "An icon signifying that no notes are present.");

  MetadataPane.TablePanel tableP;
  public ClickableList noteList;
  public JTextArea textArea;
  public JLabel nameLabel,noteLabel;
	
	public NotePanel(MetadataPane.TablePanel tp) {
	  super();
	  
	  tableP = tp;
	  setBorder(new EmptyBorder(5,5,5,5));
	  setBackground(BACK_COLOR);
	  
	  Vector noteEleList = getNoteElements();
	  DefaultListModel thisModel = new DefaultListModel();
	  noteList = new ClickableList(thisModel,this);
	  noteList.addListSelectionListener(this);
	  
	  if (noteEleList != null) {
	    for(int i = 0;i<noteEleList.size();i++) {
	      Element e = (Element) noteEleList.get(i);
	      thisModel.addElement(e.getAttribute("Name"));
	    }
	  }	  
	  
// buggy...
//	  if (thisModel.getSize() > 0) noteList.setSelectedIndex(0);
	  
	  JScrollPane jScroll = new JScrollPane(noteList);
	  jScroll.setPreferredSize(new Dimension(150,100));
	  
	  textArea = new JTextArea();
	  textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.getDocument().addDocumentListener(noteList);
    JScrollPane jNoteScroll = new JScrollPane(textArea);
    jNoteScroll.setPreferredSize(new Dimension(450,100));
    jNoteScroll.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	  
    FormLayout layout = new FormLayout(
      "pref, 5dlu, pref, 5dlu, pref:grow:right, 5dlu, pref",
      "pref,2dlu,pref");
    setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    if (thisModel.size() == 0)
      nameLabel = new JLabel("Name", NO_NOTES_BULLET, JLabel.LEFT);
    else nameLabel = new JLabel("Name", NOTES_BULLET, JLabel.LEFT);
    noteLabel = new JLabel("Notes", NO_NOTES_BULLET, JLabel.LEFT);
    Font thisFont = nameLabel.getFont();
    thisFont = new Font(thisFont.getFontName(),
      Font.BOLD,thisFont.getSize());
    nameLabel.setFont(thisFont);
    thisFont = noteLabel.getFont();
    thisFont = new Font(thisFont.getFontName(),
      Font.BOLD,thisFont.getSize());
    noteLabel.setFont(thisFont);
    
    JButton addBTN = new JButton("New Note");
    addBTN.setPreferredSize(new Dimension(100,17));
    addBTN.setActionCommand("add");
    addBTN.addActionListener(noteList);
    addBTN.setToolTipText("Add a new note to the \"Name\" list.");
    addBTN.setForeground(MetadataPane.ADD_COLOR);
    addBTN.setOpaque(false);
    
    JButton delBTN = new JButton("Delete Note");
    delBTN.setPreferredSize(new Dimension(100,17));
    delBTN.setActionCommand("remove");
    delBTN.addActionListener(noteList);
    delBTN.setToolTipText("Delete the note selected in the \"Name\" list.");
    delBTN.setForeground(MetadataPane.DELETE_COLOR);
    delBTN.setOpaque(false);
    
    add(nameLabel, cc.xy(1,1, "left,center"));
    add(noteLabel, cc.xy(3,1, "left,center"));
    
    add(addBTN, cc.xy(5,1, "right,center"));
    add(delBTN, cc.xy(7,1, "right,center"));
    
    add(jScroll, cc.xy(1,3, "fill,center"));
    add(jNoteScroll, cc.xyw(3,3,5, "fill,center"));
    setBackground(BACK_COLOR);
    setVisible(false);
    if(noteEleList != null) setVisible(true);
	}
	
	public Vector getNoteElements() {
	  if(tableP.tPanel.oNode != null && tableP.oNode != null) {
		  Vector results = new Vector();
		  CustomAttributesNode caNode = null;
	    Element currentCA = DOMUtil.getChildElement("CustomAttributes", tableP.tPanel.oNode.getDOMElement());
	    if (currentCA != null) caNode = new CustomAttributesNode(currentCA);
	    
	    if (caNode != null) { 
	  	  Vector eleList = DOMUtil.getChildElements(tableP.el.getAttribute("XMLName") + "Annotation",
		      caNode.getDOMElement());
		    if (eleList != null && eleList.size() != 0) {
		    	for (int i = 0;i<eleList.size();i++) {
		    	  Element anEle = (Element) eleList.get(i);
		    	  if ( anEle.getAttribute("NoteFor").equals(tableP.oNode.getAttribute("ID")) ) {
		    	    results.add(anEle);
		    	  }
		    	}
		    	return results;
		    }
		    else return null;
		  }
		  else return null;
		}
		else return null;
	}
	
	public int getNumNotes() {
	  Vector thisVector = getNoteElements();
	  if (thisVector == null) return 0;
	  else return thisVector.size();
	}
	
	public void setNameLabel(boolean hasElements) {
	  if (hasElements) nameLabel.setIcon(NOTES_BULLET);
	  else nameLabel.setIcon(NO_NOTES_BULLET);
	}
	
	public void setNotesLabel(boolean hasElements) {
	  if (hasElements) noteLabel.setIcon(NOTES_BULLET);
	  else noteLabel.setIcon(NO_NOTES_BULLET);
	}
	
	public void valueChanged(ListSelectionEvent e) {
	  String thisName = (String) noteList.getSelectedValue();
  	if ( thisName != null ) {
	    Element currentCA = DOMUtil.getChildElement("CustomAttributes", tableP.tPanel.oNode.getDOMElement());
	    Vector childList = DOMUtil.getChildElements(tableP.tPanel.el.getAttribute("XMLName") +
	      "Annotation", currentCA);
	    Element childEle = null;
	    for(int i = 0;i<childList.size();i++) {
	      Element thisEle = (Element) childList.get(i);
	      if(thisEle.getAttribute("Name").equals(thisName)) childEle = thisEle;
	    }
	    if (childEle != null && childEle.getAttribute("Value") != null) 
	      textArea.setText(childEle.getAttribute("Value"));
	  }
	  else textArea.setText("");
	}
}
