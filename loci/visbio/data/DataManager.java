//
// DataManager.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.data;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import loci.ome.xml.CAElement;
import loci.ome.xml.OMEElement;
import loci.visbio.*;
import loci.visbio.help.HelpManager;
import loci.visbio.state.SaveException;
import loci.visbio.state.StateManager;
import loci.visbio.util.SwingUtil;

/** DataManager is the manager encapsulating VisBio's data transform logic. */
public class DataManager extends LogicManager {

  // -- Control panel --

  /** Datasets control panel. */
  protected DataControls dataControls;


  // -- Other fields --

  /** List of registered data transform type classes. */
  protected Vector transformTypes;

  /** List of registered data transform type labels. */
  protected Vector transformLabels;


  // -- Constructor --

  /** Constructs a dataset manager. */
  public DataManager(VisBioFrame bio) { super(bio); }


  // -- DataManager API methods --

  /** Adds a data object to the list. */
  public void addData(DataTransform data) {
    dataControls.addData(data);
    bio.generateEvent(this, "add data", true);
  }

  /** Removes a data object from the list. */
  public void removeData(DataTransform data) {
    dataControls.removeData(data);
    bio.generateEvent(this, "remove data", true);
  }

  /** Gets the root node of the data object tree. */
  public DefaultMutableTreeNode getDataRoot() {
    return dataControls.getDataRoot();
  }

  /** Gets the currently selected data object. */
  public DataTransform getSelectedData() {
    return dataControls.getSelectedData();
  }

  /** Shows dialog containing controls for the given data object. */
  public void showControls(DataTransform data) {
    dataControls.showControls(data);
  }

  /**
   * Registers the given subclass of DataTransform with the data manager,
   * using the given label as a description.
   */
  public void registerDataType(Class c, String label) {
    transformTypes.add(c);
    transformLabels.add(label);
  }

  /** Gets list of registered data transform types. */
  public Class[] getRegisteredDataTypes() {
    Class[] types = new Class[transformTypes.size()];
    transformTypes.copyInto(types);
    return types;
  }

  /** Gets list of regitered data transform labels. */
  public String[] getRegisteredDataLabels() {
    String[] labels = new String[transformLabels.size()];
    transformLabels.copyInto(labels);
    return labels;
  }

  /** Gets data control panel. */
  public DataControls getControlPanel() { return dataControls; }

  /** Gets a list of data transforms present in the tree. */
  public Vector getDataList() {
    Vector v = new Vector();
    buildDataList(dataControls.getDataRoot(), v);
    return v;
  }

  /** Imports a dataset. */
  public void importData() { importData(bio); }

  /** Imports a dataset, using the given parent component for user dialogs. */
  public void importData(Component parent) {
    DataTransform dt = Dataset.makeTransform(this, null, parent);
    if (dt != null) addData(dt);
  }

  /** Exports the selected data object to disk. */
  public void exportData() {
    DataTransform data = dataControls.getSelectedData();
    if (data instanceof ImageTransform) exportData((ImageTransform) data);
  }

  /** Exports the given data object to disk. */
  public void exportData(ImageTransform data) {
    dataControls.exportData(data);
  }


  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      LogicManager lm = (LogicManager) evt.getSource();
      if (lm == this) doGUI();
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 4; }


  // -- Saveable API methods --

  protected static final String DATA_MANAGER = "VisBio_DataManager";

  /** Writes the current state to the given XML object. */
  public void saveState(OMEElement ome) throws SaveException {
    Vector v = getDataList();
    int len = v.size();

    // save number of transforms
    CAElement custom = ome.getCustomAttr();
    custom.createElement(DATA_MANAGER);
    custom.setAttribute("count", "" + len);

    // save transform class names
    for (int i=0; i<len; i++) {
      custom.setAttribute("class" + i, v.elementAt(i).getClass().getName());
    }

    // save all transforms
    for (int i=0; i<len; i++) {
      DataTransform data = (DataTransform) v.elementAt(i);
      data.saveState(ome, i, v);
    }
  }

  /** Restores the current state from the given XML object. */
  public void restoreState(OMEElement ome) throws SaveException {
    CAElement custom = ome.getCustomAttr();

    // read number of transforms
    String[] dtCount = custom.getAttributes(DATA_MANAGER, "count");
    int count = -1;
    if (dtCount != null && dtCount.length > 0) {
      try { count = Integer.parseInt(dtCount[0]); }
      catch (NumberFormatException exc) { }
    }
    if (count < 0) {
      System.err.println("Failed to restore transform count.");
      count = 0;
    }

    Vector vn = new Vector();
    for (int i=0; i<count; i++) {
      // read transform class name
      String[] dtClass = custom.getAttributes(DATA_MANAGER, "class" + i);
      if (dtClass == null || dtClass.length == 0) {
        System.err.println("Failed to read transform #" + i + " class");
        continue;
      }

      // locate transform class
      Class c = null;
      try { c = Class.forName(dtClass[0]); }
      catch (ClassNotFoundException exc) { }
      if (c == null) {
        System.err.println("Failed to identify transform #" + i + " class");
        continue;
      }

      // construct transform
      Object o = null;
      try { o = c.newInstance(); }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (o == null) {
        System.err.println("Failed to instantiate transform #" + i);
        continue;
      }
      if (!(o instanceof DataTransform)) {
        System.err.println("Transform #" + i + " is not valid (" +
          o.getClass().getName() + ")");
        continue;
      }

      // restore transform state
      DataTransform data = (DataTransform) o;
      data.restoreState(ome, i, vn);
      vn.add(data);
    }

    // merge old and new transform lists
    Vector vo = getDataList();
    StateManager.mergeStates(vo, vn);

    // add new transforms to tree structure
    int nlen = vn.size();
    for (int i=0; i<nlen; i++) {
      DataTransform data = (DataTransform) vn.elementAt(i);
      if (!vo.contains(data)) addData(data);
    }

    // purge old transforms from tree structure
    int olen = vo.size();
    for (int i=0; i<olen; i++) {
      DataTransform data = (DataTransform) vo.elementAt(i);
      if (!vn.contains(data)) removeData(data);
    }
  }


  // -- Helper methods --

  /** Adds data-related GUI components to VisBio. */
  private void doGUI() {
    // control panel
    bio.setSplashStatus("Initializing data logic");
    dataControls = new DataControls(this);
    PanelManager pm = (PanelManager) bio.getManager(PanelManager.class);
    pm.addPanel(dataControls);

    // data transform registration
    bio.setSplashStatus(null);
    transformTypes = new Vector();
    transformLabels = new Vector();
    registerDataType(Dataset.class, "Dataset");
    registerDataType(DataSampling.class, "Subsampling");
    registerDataType(ProjectionTransform.class,
      "Maximum intensity projection");
    registerDataType(CollapseTransform.class, "Dimensional collapse");
    registerDataType(SpectralTransform.class, "Spectral mapping");
    registerDataType(ArbitrarySlice.class, "Arbitrary slice");

    // menu items
    bio.setSplashStatus(null);
    bio.addMenuItem("File", "Import data...",
      "loci.visbio.data.DataManager.importData", 'i');
    bio.addMenuItem("File", "Export data...",
      "loci.visbio.data.DataManager.exportData", 'e').setEnabled(false);
    SwingUtil.setMenuShortcut(bio, "File", "Import data...", KeyEvent.VK_O);

    // help window
    bio.setSplashStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    String s = "File formats";
    hm.addHelpTopic(s, "formats.html");
    hm.addHelpTopic(s + "/Bio-Rad PIC", "biorad.html");
    hm.addHelpTopic(s + "/Olympus Fluoview TIFF", "fluoview.html");
    hm.addHelpTopic(s + "/Metamorph STK", "metamorph.html");
    hm.addHelpTopic(s + "/Openlab LIFF", "openlab.html");
    hm.addHelpTopic(s + "/QuickTime movies", "quicktime.html");
    hm.addHelpTopic(s + "/TIFF stacks", "tiff.html");
    hm.addHelpTopic(s + "/Zeiss LSM", "zeiss_lsm.html");
    hm.addHelpTopic(s + "/Zeiss ZVI (Axiovision)", "zeiss_zvi.html");
    hm.addHelpTopic(s + "/Other formats (JPEG, BMP, etc.)",
      "other_formats.html");
    s = "Data transforms";
    hm.addHelpTopic(s, "data_transforms.html");
    hm.addHelpTopic(s + "/Datasets", "dataset.html");
    hm.addHelpTopic(s + "/Subsamplings", "subsampling.html");
    hm.addHelpTopic(s + "/Maximum intensity projections",
      "max_intensity.html");
    hm.addHelpTopic(s + "/Dimensional collapse transforms", "collapse.html");
    hm.addHelpTopic(s + "/Spectral mappings", "spectral.html");
    hm.addHelpTopic(s + "/Arbitrary slices", "arbitrary_slice.html");
    s = "Control panels/Data panel";
    hm.addHelpTopic(s, "data_panel.html");
    hm.addHelpTopic(s + "/Importing a dataset from disk", "import_data.html");
    hm.addHelpTopic(s + "/Adding a data object", "add_data.html");
    hm.addHelpTopic(s + "/Generating thumbnails", "thumbnails.html");
    hm.addHelpTopic(s + "/Displaying a data object", "display_data.html");
    hm.addHelpTopic(s + "/Exporting data to disk", "export_data.html");
    hm.addHelpTopic(s + "/Uploading to an OME database", "upload_ome.html");
  }

  /** Recursively creates a list of data transforms below the given node. */
  private void buildDataList(DefaultMutableTreeNode node, Vector v) {
    Object o = node.getUserObject();
    if (o instanceof DataTransform) v.add(o);

    int count = node.getChildCount();
    for (int i=0; i<count; i++) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode)
        node.getChildAt(i);
      buildDataList(child, v);
    }
  }

}
