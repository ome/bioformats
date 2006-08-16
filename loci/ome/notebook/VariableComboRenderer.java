package loci.ome.notebook;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.border.EmptyBorder;

import java.awt.Component;

import java.util.Vector;

import org.openmicroscopy.xml.DOMUtil;
import org.w3c.dom.Element;

public class VariableComboRenderer
  implements TableCellRenderer
{
  public static final ImageIcon TEXT_BULLET = 
    MetadataPane.createImageIcon("Icons/TextBullet.GIF",
      "An icon signifying that a textfield will edit this cell.");

  protected Element tEle;

  public VariableComboRenderer(Element e) {
    tEle = e;
  }

  public Component getTableCellRendererComponent(
                          JTable table, Object value,
                          boolean isSelected, boolean hasFocus,
                          int row, int column) {
    TableModel tModel = table.getModel();
  
    Vector fullList = DOMUtil.getChildElements("OMEAttribute",tEle);
    Element templateE = null;
    for (int i = 0;i<fullList.size();i++) {
      Element thisE = (Element) fullList.get(i);
      String nameAttr = thisE.getAttribute("XMLName");
      if(thisE.hasAttribute("Name")) nameAttr = thisE.getAttribute("Name");
      if(nameAttr.equals((String) tModel.getValueAt(row, 0))) templateE = thisE;      
    }
    
    String cellType = null;
    if(templateE.hasAttribute("Type")) cellType = templateE.getAttribute("Type");                      
    if(cellType != null) { 
	    if(cellType.equals("Ref")) {
	      Object [] myObjects = {value};
	      JComboBox jcb = new JComboBox(myObjects);
	      jcb.setBorder((EmptyBorder) null);
	      return jcb;
	    }
	    else if(cellType.equals("Desc")) {
	      JLabel jl = new JLabel(" " + (String) value, TEXT_BULLET, JLabel.LEFT);
	      return jl;
	    }
	    else {
	      JLabel jl = new JLabel(" " + (String) value);
	      return jl;
	    } 
    }
    else {
      JLabel jl = new JLabel(" " + (String) value);
      return jl;
    } 
  }
}
