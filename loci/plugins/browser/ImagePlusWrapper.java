//
// ImagePlusWrapper.java
//

/*
LOCI 4D Data Browser plugin for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Christopher Peterson, Francis Wong, Curtis Rueden
and Melissa Linkert.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.browser;

import ij.*;
import ij.process.*;
import java.io.IOException;
import loci.formats.*;
import loci.formats.ome.OMEXMLMetadata;
import loci.plugins.Util;

public class ImagePlusWrapper {

  // -- Fields --

  protected MetadataStore store;
  private ImagePlus imp;
  private int numTotal; // total number of images

  // -- Constructor --

  public ImagePlusWrapper(String name, IFormatReader r)
    throws IOException, FormatException
  {
    store = new OMEXMLMetadata();
    synchronized (r) {
      r.setMetadataStore(store);

      // retrieve core metadata

      r.setId(name);
      r.setSeries(r.getSeries());

      numTotal = r.getImageCount();
      String dim = r.getDimensionOrder();
      int sizeX = r.getSizeX();
      int sizeY = r.getSizeY();
      int sizeZ = r.getSizeZ();
      int sizeT = r.getSizeT();
      int sizeC = r.getSizeC();

      if (LociDataBrowser.DEBUG) {
        LogTools.println("numTotal = "+numTotal);
      }

      int num = r.getImageCount();
      ImageStack stackB = null, stackS = null, stackF = null, stackO = null;
      long start = System.currentTimeMillis();
      long time = start;

      for (int i=0; i<num; i++) {
        long clock = System.currentTimeMillis();
        if (clock - time >= 50) {
          IJ.showStatus("Reading plane "+(i+1)+"/"+num);
          time = clock;
        }
        IJ.showProgress((double) i / num);
        ImageProcessor ip = Util.openProcessor(r, i);

        int[] zct = r.getZCTCoords(i);
        StringBuffer sb = new StringBuffer();
        sb.append("ch:");
        sb.append(zct[1] + 1);
        sb.append("/");
        sb.append(r.getSizeC());
        sb.append("; z:");
        sb.append(zct[0] + 1);
        sb.append("/");
        sb.append(r.getSizeZ());
        sb.append("; t:");
        sb.append(zct[2] + 1);
        sb.append("/");
        sb.append(r.getSizeT());
        String label = sb.toString();

        if (ip instanceof ByteProcessor) {
          if (stackB == null) stackB = new ImageStack(sizeX, sizeY);
          stackB.addSlice(label, ip);
        }
        else if (ip instanceof ShortProcessor) {
          if (stackS == null) stackB = new ImageStack(sizeX, sizeY);
          stackS.addSlice(label, ip);
        }
        else if (ip instanceof FloatProcessor) {
          if (stackB != null) stackB.addSlice(label, ip.convertToByte(true));
          else if (stackS != null) {
            stackS.addSlice(label, ip.convertToShort(true));
          }
          else {
            if (stackF == null) stackF = new ImageStack(sizeX, sizeY);
            stackF.addSlice(label, ip);
          }
        }
        else if (ip instanceof ColorProcessor) {
          if (stackO == null) stackO = new ImageStack(sizeX, sizeY);
          stackO.addSlice(label, ip);
        }
      }
      IJ.showStatus("Creating image");
      IJ.showProgress(1);
      if (stackB != null) imp = new ImagePlus(name, stackB);
      if (stackS != null) imp = new ImagePlus(name, stackS);
      if (stackF != null) imp = new ImagePlus(name, stackF);
      if (stackO != null) imp = new ImagePlus(name, stackO);

      long end = System.currentTimeMillis();
      double elapsed = (end - start) / 1000.0;
      if (num == 1) IJ.showStatus(elapsed + " seconds");
      else {
        long average = (end - start) / num;
        IJ.showStatus("LOCI Bio-Formats : " + elapsed + " seconds (" +
          average + " ms per plane)");
      }
    }
  }

  // -- ImagePlusWrapper API methods --

  public ImagePlus getImagePlus() { return imp; }

  public int getNumTotal() { return numTotal; }

}
