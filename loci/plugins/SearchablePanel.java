//
// SearchablePanel.java
//

package loci.plugins;

import com.jgoodies.forms.layout.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import ij.text.*;

/** Text panel with search capabilities. */
public class SearchablePanel extends TextPanel implements KeyListener {

  protected int index;

  // -- KeyListener API methods --

  public void keyPressed(KeyEvent e) {
    // Ctrl+f is the command to open the search box 
    if (e.isControlDown() && KeyEvent.getKeyText(e.getKeyCode()).equals("F")) {
      new SearchBox(this); 
      return; 
    }
    super.keyPressed(e); 
  }

  public void keyReleased(KeyEvent e) { }
  public void keyTyped(KeyEvent e) { }

  // -- SearchablePanel API methods --

  public void selectLine(int index) {
    int ys = getFontMetrics(getFont()).getHeight() + 2; // height of each row 
    int y = ys * (index + 1) + 2;  // absolute y coordinate
    int totalHeight = ys * getLineCount();

    Scrollbar ss = null;
    Component[] components = getComponents();
    for (int i=0; i<components.length; i++) {
      if (components[i] instanceof Scrollbar) {
        Scrollbar s = (Scrollbar) components[i];
        if (s.getOrientation() == Scrollbar.VERTICAL) {
          ss = s; 
        }
      }
    }

    int height = getHeight();

    // convert absolute y value to scrollbar and relative y coordinates
    int min = ss.getMinimum();
    int scrollValue = min + index;
    ss.setValue(scrollValue);
    adjustmentValueChanged(null);

    int linesPerPanel = height / ys;
    if (scrollValue >= linesPerPanel) {
      int ticks = scrollValue;
      if (ticks + linesPerPanel > getLineCount()) {
        ticks = getLineCount() - linesPerPanel;
      }
      
      y -= ys * (ticks + 2);
    }

    MouseEvent event = new MouseEvent(this, MouseEvent.MOUSE_PRESSED, 
      System.currentTimeMillis(), InputEvent.BUTTON1_DOWN_MASK, 0, y, 1, false); 
    mousePressed(event); 
  }

  // -- Helper class --

  class SearchBox extends JDialog implements ActionListener, ChangeListener {
    private JTextField searchBox;
    private JCheckBox ignore;
    private boolean ignoreCase; 
    private SearchablePanel searchPane;

    public SearchBox(SearchablePanel searchPane) {
      setTitle("Search..."); 
      this.searchPane = searchPane;
      FormLayout layout = new FormLayout("pref,pref:grow,pref,pref:grow,pref",
        "pref,pref:grow,pref,pref:grow,pref,pref:grow,pref");
      JPanel panel = new JPanel(layout);
      CellConstraints cc = new CellConstraints();

      searchBox = new JTextField();

      ignore = new JCheckBox("Ignore Case", false);
      ignore.addChangeListener(this); 
      JButton next = new JButton("Find Next");
      next.setActionCommand("next");
      next.addActionListener(this);
      JButton previous = new JButton("Find Previous");
      previous.setActionCommand("previous");
      previous.addActionListener(this);
      JButton cancel = new JButton("Cancel");
      cancel.setActionCommand("cancel");
      cancel.addActionListener(this);
      
      panel.add(searchBox, cc.xy(2, 2));
      panel.add(ignore, cc.xy(2, 4));
      panel.add(next, cc.xy(4, 2));
      panel.add(previous, cc.xy(4, 4));
      panel.add(cancel, cc.xywh(2, 6, 3, 1));

      panel.setSize(new Dimension(350, 200));
      setContentPane(panel);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setSize(new Dimension(350, 200)); 
      setVisible(true);
    }

    // -- ActionListener API methods --

    public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      if (cmd.equals("next")) {
        searchPane.resetSelection(); 
        String text = searchBox.getText(); 
        boolean found = false;
        int original = searchPane.index; 
        while (!found) {
          searchPane.index++;
          if (searchPane.index >= searchPane.getLineCount()) {
            searchPane.index = 0; 
          } 
          if (searchPane.index == original) return;
          String line = searchPane.getLine(searchPane.index);
          found = ignoreCase ? line.toLowerCase().indexOf(
            text.toLowerCase()) >= 0 : line.indexOf(text) >= 0; 
        }
        searchPane.selectLine(searchPane.index); 
      }
      else if (cmd.equals("previous")) {
        searchPane.resetSelection(); 
        String text = searchBox.getText();
        boolean found = false;
        int original = searchPane.index; 
        while (!found) {
          searchPane.index--;
          if (searchPane.index < 0) {
            searchPane.index = searchPane.getLineCount() - 1;
          }
          if (searchPane.index == original) return;
          String line = searchPane.getLine(searchPane.index);
          found = ignoreCase ? line.toLowerCase().indexOf(
            text.toLowerCase()) >= 0 : line.indexOf(text) >= 0; 
        }
        searchPane.selectLine(searchPane.index); 
      }
      else if (cmd.equals("cancel")) {
        dispose();
      }
    }
 
    // -- ChangeListener API methods --

    public void stateChanged(ChangeEvent e) {
      if (e.getSource().equals(ignore)) {
        ignoreCase = ignore.isSelected();
      }
    }

  }

}
