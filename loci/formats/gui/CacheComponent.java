//
// CacheComponent.java
//

package loci.formats.gui;

import com.jgoodies.forms.layout.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import loci.formats.*;
import loci.formats.cache.*;

/** GUI component for managing a cache. */
public class CacheComponent extends JPanel 
  implements ActionListener, ChangeListener
{

  // -- Fields --

  /** The cache that this component controls. */
  private Cache cache; 

  /** File that the cache is working with. */
  private String file;

  /** Length of each axis. */
  private int[] lengths;

  /** Spinners for choosing number of slices to cache ahead. */
  private Vector forward;

  /** Spinners for choosing number of slices to cache behind. */
  private Vector backward;

  /** Buttons for choosing axis priority. */
  private JRadioButton[][] priority;

  // -- Constructor --

  public CacheComponent(Cache cache, boolean doSource, String[] axisLabels,
    String file, int[] lengths) 
  {
    super();
    this.cache = cache;
    this.file = file;
    this.lengths = lengths;

    BoxLayout thisLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
    setLayout(thisLayout);

    CellConstraints cc = new CellConstraints();

    forward = new Vector();
    backward = new Vector();

    JPanel top = new JPanel();
    FormLayout layout = new FormLayout("pref,pref:grow,pref,pref:grow,pref",
      doSource ? "pref,pref:grow,pref,pref:grow,pref" : "pref,pref:grow,pref");
    top.setLayout(layout);

    // add source choices, if desired 
    JComboBox sourceChooser = null; 
    if (doSource) {
      JLabel label = new JLabel("Objects to cache: ");
      top.add(label, cc.xy(2, 2));
      String[] sources = 
        new String[] {"byte arrays", "BufferedImages", "ImageProcessors"};
      sourceChooser = new JComboBox(sources);
      sourceChooser.setActionCommand("source");
      sourceChooser.addActionListener(this);
      top.add(sourceChooser, cc.xy(4, 2));
    }

    // add strategy choices

    JLabel label = new JLabel("Caching strategy: ");
    top.add(label, cc.xy(2, doSource ? 4 : 2));
    String[] strategies = new String[] {"Crosshair - forward first", 
      "Crosshair - backward first", "Rectangle - forward first", 
      "Rectangle - backward first"};
    JComboBox strategyChooser = new JComboBox(strategies); 
    strategyChooser.setActionCommand("strategy"); 
    strategyChooser.addActionListener(this); 
    top.add(strategyChooser, cc.xy(4, doSource ? 4 : 2));

    add(top);

    // add cache size choices

    JPanel middle = new JPanel();
    String rowString = "pref,pref:grow,pref,";
    for (int i=0; i<axisLabels.length; i++) {
      rowString += "pref:grow,pref,";
    }
    layout = new FormLayout("pref,pref:grow,pref,pref:grow,pref,pref:grow,pref",
      rowString);
    middle.setLayout(layout);

    JLabel header = new JLabel("Axis");
    middle.add(header, cc.xy(2, 2));
    header = new JLabel("Forward");
    middle.add(header, cc.xy(4, 2));
    header = new JLabel("Backward");
    middle.add(header, cc.xy(6, 2));

    for (int i=0; i<axisLabels.length; i++) {
      JLabel l = new JLabel(axisLabels[i]);
      middle.add(l, cc.xy(2, i*2 + 4));
      JSpinner f = new JSpinner(new SpinnerNumberModel(1, 0, lengths[i], 1));
      middle.add(f, cc.xy(4, i*2 + 4));
      JSpinner b = new JSpinner(new SpinnerNumberModel(1, 0, lengths[i], 1));
      middle.add(b, cc.xy(6, i*2 + 4)); 
      forward.add(f);
      backward.add(b);
    }

    add(middle);

    if (sourceChooser != null) updateSource(sourceChooser);
    updateStrategy(strategyChooser);

    // add priority choices

    JPanel bottom = new JPanel();
    String colString = "pref,pref:grow,pref,";
    for (int i=0; i<axisLabels.length; i++) {
      colString += "pref:grow,pref,";
    }
    layout = new FormLayout(colString, rowString);
    bottom.setLayout(layout);

    priority = new JRadioButton[axisLabels.length][axisLabels.length];
    String[] priorities = new String[] {
      "High priority", "Medium priority", "Low priority"
    };
    int skip = axisLabels.length / 3;

    for (int i=0; i<axisLabels.length; i++) {
      JLabel l = new JLabel(""); 
      if (i % skip == 0) {
        l = new JLabel(priorities[i / skip]);
      }
      bottom.add(l, cc.xy(2, (i + 1) * 2));

      ButtonGroup g = new ButtonGroup();
      for (int j=0; j<axisLabels.length; j++) {
        JRadioButton button = new JRadioButton(axisLabels[j], i == j);
        priority[i][j] = button; 
        button.addChangeListener(this); 
        g.add(button); 
        bottom.add(button, cc.xy((j + 2) * 2, (i + 1) * 2)); 
      }
    }

    add(bottom);

    JButton reset = new JButton("Reset");
    reset.setActionCommand("reset"); 
    reset.addActionListener(this);
    add(reset); 
  }

  // -- CacheComponent API methods --

  public Cache getCache() {
    return cache;
  }

  // -- ActionListener API methods -- 

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals("reset")) {
      // TODO - reset to reasonable defaults
    }
    else if (cmd.equals("source")) {
      updateSource((JComboBox) e.getSource()); 
    }
    else if (cmd.equals("strategy")) {
      updateStrategy((JComboBox) e.getSource()); 
    }
  }

  // -- ChangeListener API methods --

  public void stateChanged(ChangeEvent e) {
    // make sure each axis is chosen only once 
    for (int ct=0; ct<priority.length; ct++) {
      for (int col=0; col<priority.length; col++) {
        int chosenNdx = -1;
        
        for (int row=0; row<priority.length; row++) {
          if (priority[row][col].equals(e.getSource()) &&
            priority[row][col].isSelected()) 
          {
            chosenNdx = row;
            break;
          }
          else if (priority[row][col].isSelected() && chosenNdx == -1) {
            chosenNdx = row;
          }
        } 
        
        for (int row=0; row<priority.length; row++) {
          if (priority[row][col].isSelected() && row != chosenNdx) {
            priority[row][(col + 1) % priority.length].setSelected(true);
          }
        }
      }
    } 
  
    // reset the cache's priorities 
    ICacheStrategy strategy = cache.getStrategy();

    for (int row=0; row<priority.length; row++) {
      for (int col=0; col<priority.length; col++) {
        if (priority[row][col].isSelected()) {
          strategy.setPriority(row, col); 
        }
      }
    }
   
    for (int i=0; i<forward.size(); i++) {
      JSpinner f = (JSpinner) forward.get(i);
      JSpinner b = (JSpinner) backward.get(i);
      strategy.setForward(((Integer) f.getValue()).intValue(), i);
      strategy.setBackward(((Integer) b.getValue()).intValue(), i);
    }
  }

  // -- Helper methods --

  private int[] getForward() {
    int[] n = new int[forward.size()];
    for (int i=0; i<n.length; i++) {
      n[i] = ((Integer) ((JSpinner) forward.get(i)).getValue()).intValue();
    }
    return n; 
  }

  private int[] getBackward() {
    int[] n = new int[backward.size()];
    for (int i=0; i<n.length; i++) {
      n[i] = ((Integer) ((JSpinner) backward.get(i)).getValue()).intValue();
    }
    return n; 
  } 

  private void updateSource(JComboBox box) {
    String s = (String) box.getSelectedItem(); 

    CacheSource source = null;
    try {
      if (s.equals("byte arrays")) {
        source = new ByteArraySource(file);  
      }
      else if (s.equals("BufferedImages")) {
        source = new BufferedImageSource(file);  
      }
      else if (s.equals("ImageProcessors")) {
        source = new ImageProcessorSource(file);  
      }
      cache.setSource(source);
    }
    catch (CacheException exc) {
      LogTools.trace(exc);
    }
  }

  private void updateStrategy(JComboBox box) {
    String s = (String) box.getSelectedItem();
    CacheStrategy strategy = null;

    boolean forwardFirst = s.indexOf("forward") != -1;

    try {
      if (s.startsWith("Crosshair")) {
        strategy = new CrosshairStrategy(lengths);
      }
      else if (s.startsWith("Rectangle")) {
        strategy = new RectangleStrategy(lengths);
      }
      strategy.setForwardFirst(forwardFirst);
      int[] fwd = getForward();
      for (int i=0; i<fwd.length; i++) strategy.setForward(fwd[i], i);
      int[] bwd = getBackward();
      for (int i=0; i<bwd.length; i++) strategy.setBackward(bwd[i], i);
      cache.setStrategy(strategy); 
    }
    catch (CacheException exc) {
      LogTools.trace(exc);
    }
  }

}
