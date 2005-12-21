import ij.plugin.PlugIn;

/**
 * OME_Plugin is the ImageJ Plugin
 * that allows image import and exports from
 * the OME database.  This plugin also views
 * OME-XML metadata present in OME-TIFF files.
 * @author Philip Huettl pmhuettl@wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OME_Plugin implements PlugIn {
  private static OMESidePanel omeSidePanel;

  /** shows and retrieves info from the SidePanel */
  public void run(java.lang.String arg) {
    if (omeSidePanel == null) {
      omeSidePanel = new OMESidePanel(ij.IJ.getInstance());
    }
    omeSidePanel.showIt();
  }
      
}
