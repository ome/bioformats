//
// Simple_Read.java
//

import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import java.io.IOException;
import loci.formats.FormatException;
import loci.plugins.BF;

/**
 * A very simple example of using Bio-Formats in an ImageJ plugin.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/loci-plugins/utils/Simple_Read.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/loci-plugins/utils/Simple_Read.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Simple_Read implements PlugIn {

  public void run(String arg) {
    OpenDialog od = new OpenDialog("Open Image File...", arg);
    String dir = od.getDirectory();
    String name = od.getFileName();
    String id = dir + name;

    try {
      ImagePlus[] imps = BF.openImagePlus(id);
      for (ImagePlus imp : imps) imp.show();
    }
    catch (FormatException exc) {
      IJ.error("Sorry, an error occurred: " + exc.getMessage());
    }
    catch (IOException exc) {
      IJ.error("Sorry, an error occurred: " + exc.getMessage());
    }
  }

}
