//
// MakeLZW.java
//

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import loci.formats.MetadataTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.out.TiffWriter;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;

/** Converts the given image file to an LZW-compressed TIFF. */
public class MakeLZW {

  public static void main(String[] args) throws Exception {
    BufferedImageReader reader = new BufferedImageReader();
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

        IFD ifd = new IFD();
        if (b == 0) {
          // preserve OME-XML block
          ifd.putIFDValue(IFD.IMAGE_DESCRIPTION, xml);
        }

        // save with LZW
        ifd.putIFDValue(IFD.COMPRESSION, TiffCompression.LZW);
        ifd.putIFDValue(IFD.PREDICTOR, 2);

        // write file to disk
        writer.saveImage(img, ifd, b == blocks - 1);
      }
      System.out.println(" [done]");
    }
  }

}
