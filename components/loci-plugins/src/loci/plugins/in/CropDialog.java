//
// CropDialog.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser and Stack Slicer. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden and Christopher Peterson.

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

package loci.plugins.in;

import ij.gui.GenericDialog;

import loci.common.Region;
import loci.formats.IFormatReader;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer crop options dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/CropDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/CropDialog.java">SVN</a></dd></dl>
 */
public class CropDialog extends ImporterDialog {

  // -- Constructor --

  /** Creates a crop options dialog for the Bio-Formats Importer. */
  public CropDialog(ImportProcess process) {
    super(process);
  }
  
  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return !process.isWindowless() && options.doCrop();
  }
  
  @Override
  protected GenericDialog constructDialog() {
    int seriesCount = process.getSeriesCount();
    IFormatReader r = process.getReader();
    
    // construct dialog
    GenericDialog gd = new GenericDialog("Bio-Formats Crop Options");
    for (int s=0; s<seriesCount; s++) {
      if (!options.isSeriesOn(s)) continue;
      r.setSeries(s);

      Region region = process.getCropRegion(s);

      gd.addMessage(process.getSeriesLabel(s).replaceAll("_", " "));
      gd.addNumericField("X_Coordinate_" + (s + 1), region.x, 0);
      gd.addNumericField("Y_Coordinate_" + (s + 1), region.y, 0);
      gd.addNumericField("Width_" + (s + 1), region.width, 0);
      gd.addNumericField("Height_" + (s + 1), region.height, 0);
    }
    WindowTools.addScrollBars(gd);
 
    return gd;
  }
  
  @Override
  protected boolean harvestResults(GenericDialog gd) {
    int seriesCount = process.getSeriesCount();
    IFormatReader r = process.getReader();

    for (int s=0; s<seriesCount; s++) {
      if (!options.isSeriesOn(s)) continue;
      r.setSeries(s);

      Region region = new Region();
      region.x = (int) gd.getNextNumber();
      region.y = (int) gd.getNextNumber();
      region.width = (int) gd.getNextNumber();
      region.height = (int) gd.getNextNumber();

      if (region.x < 0) region.x = 0;
      if (region.y < 0) region.y = 0;
      if (region.x >= r.getSizeX()) region.x = r.getSizeX() - region.width - 1;
      if (region.y >= r.getSizeY()) region.y = r.getSizeY() - region.height - 1;
      if (region.width < 1) region.width = 1;
      if (region.height < 1) region.height = 1;
      if (region.width + region.x > r.getSizeX()) {
        region.width = r.getSizeX() - region.x;
      }
      if (region.height + region.y > r.getSizeY()) {
        region.height = r.getSizeY() - region.y;
      }

      options.setCropRegion(s, region);
    }

    return true;
  }
  
}
