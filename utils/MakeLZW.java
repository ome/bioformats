//
// MakeLZW.java
//

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.TiffTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.out.TiffWriter;

/** Converts the given image file to an LZW-compressed TIFF. */
public class MakeLZW {

  public static void main(String[] args) throws Exception {
    ImageReader reader = new ImageReader();
    MetadataStore omexmlMeta = MetadataTools.createOMEXMLMetadata();
    reader.setMetadataStore(omexmlMeta);
    TiffWriter writer = new TiffWriter();
    for (int i=0; i<args.length; i++) {
      String inFile = args[i];
      String outFile = "lzw-" + inFile;
      System.out.print("Converting " + inFile + " to " + outFile);
      reader.setId(inFile);
      writer.setId(outFile);
      int blocks = reader.getImageCount();
      String xml = MetadataTools.getOMEXML((MetadataRetrieve) omexmlMeta);
      for (int b=0; b<blocks; b++) {
        System.out.print(".");
        BufferedImage img = reader.openImage(b);

        Hashtable ifd = new Hashtable();
        if (b == 0) {
          // preserve OME-XML block
          TiffTools.putIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION, xml);
        }

        // save with LZW
        TiffTools.putIFDValue(ifd, TiffTools.COMPRESSION, TiffTools.LZW);
        TiffTools.putIFDValue(ifd, TiffTools.PREDICTOR, 2);

        // write file to disk
        writer.saveImage(img, ifd, b == blocks - 1);
      }
      System.out.println(" [done]");
    }
  }

}
