/**
 * OME_Plugin is the ImageJ Plugin
 * that allows image import and exports from
 * the OME database.  This plugin also views
 * OME-XML metadata present in OME-Tiff files.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OME_Plugin implements ij.plugin.PlugIn{
  private static OMESidePanel omeSidePanel;
  //Methods

  /**shows and retrieves info from the SidePanel*/
  public void run(java.lang.String arg){
    if ( omeSidePanel == null ) {
      // side panel not initialized; initialize now
      omeSidePanel = new OMESidePanel(ij.IJ.getInstance());
    }
    omeSidePanel.showIt();
  }//end of run method
      
}//end of OME_Plugin class