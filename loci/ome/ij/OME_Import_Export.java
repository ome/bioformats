/**
 * OME_Import_Export is the ImageJ Plugin
 * that allows image import and exports from
 * the OME database.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OME_Import_Export implements ij.plugin.PlugIn{
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
      
}//end of OME_Import_Export class