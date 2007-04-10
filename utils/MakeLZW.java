//
// MakeLZW.java
//

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import org.openmicroscopy.xml.OMENode;
import loci.formats.*;
import loci.formats.ome.OMEXMLMetadataStore;
import loci.formats.out.TiffWriter;

/** Converts the given image file to an LZW-compressed TIFF. */
public class MakeLZW {

  public static void main(String[] args) throws Exception {
    ImageReader reader = new ImageReader();
    OMEXMLMetadataStore ms = new OMEXMLMetadataStore();
    reader.setMetadataStore(ms);
    TiffWriter writer = new TiffWriter();
    for (int i=0; i<args.length; i++) {
      String f = args[i];
      String nf = "lzw-" + f;
      System.out.print("Converting " + f + " to " + nf);
      reader.setId(f); 
      int blocks = reader.getImageCount();
      OMENode ome = (OMENode) ms.getRoot();
      for (int b=0; b<blocks; b++) {
        System.out.print(".");
        BufferedImage img = reader.openImage(b);

        Hashtable ifd = new Hashtable();
        if (b == 0) {
          // preserve OME-XML block
          TiffTools.putIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION,
            ome.writeOME(false));
        }

        // save with LZW
        TiffTools.putIFDValue(ifd, TiffTools.COMPRESSION, TiffTools.LZW);
        TiffTools.putIFDValue(ifd, TiffTools.PREDICTOR, 2);

        // write file to disk
        writer.saveImage(nf, img, ifd, b == blocks - 1);
      }
      System.out.println(" [done]");
    }
  }

}
