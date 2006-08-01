package loci.ome.notebook;

import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import org.openmicroscopy.xml.*;
import org.w3c.dom.*;
import javax.swing.event.*;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NotePanel extends JPanel 
  implements ListSelectionListener {

  public static final ImageIcon NOTES_BULLET =
    MetadataPane.createImageIcon("Icons/Bullet1.gif",
      "An icon signifying that notes are present.");
      
  public static final ImageIcon NO_NOTES_BULLET =
    MetadataPane.createImageIcon("Icons/Bullet4.gif",
      "An icon signifying that no notes are present.");

  MetadataPane.TablePanel tableP;
  public ClickableList noteList;
  public JTextArea textArea;
  public JLabel nameLabel,noteLabel;
	
	public NotePanel(MetadataPane.TablePanel tp) {
	  super();
	  
	  tableP = tp;
	  
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
	  
//	  if (thisModel.getSize() > 0) noteList.setSelectedIndex(0);
	  
	  JScrollPane jScroll = new JScrollPane(noteList);
	  jScroll.setPreferredSize(new Dimension(150,100));
	  
	  textArea = new JTextArea();
	  textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.getDocument().addDocumentListener(noteList);
    JScrollPane jNoteScroll = new JScrollPane(textArea);
    jNoteScroll.setPreferredSize(new Dimension(465,100));
    jNoteScroll.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	  
    FormLayout layout = new FormLayout(
      "pref:grow, 10px, pref:grow",
      "pref,2dlu,pref");
    PanelBuilder builder = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();
    
    if (thisModel.size() == 0)
      nameLabel = new JLabel("Name", NO_NOTES_BULLET, JLabel.LEFT);
    else nameLabel = new JLabel("Name", NOTES_BULLET, JLabel.LEFT);
    noteLabel = new JLabel("Notes", NO_NOTES_BULLET, JLabel.LEFT);
    
    builder.add(nameLabel, cc.xy(1,1, "center,center"));
    builder.add(noteLabel, cc.xy(3,1, "center,center"));
    
    builder.add(jScroll, cc.xy(1,3, "fill,center"));
    builder.add(jNoteScroll, cc.xy(3,3, "fill,center"));
    
    add(builder.getPanel());
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
