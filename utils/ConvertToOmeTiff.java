//
// ConvertToOmeTiff.java
//

import java.awt.image.BufferedImage;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.out.OMETiffWriter;

/** Converts the given files to OME-TIFF format. */
public class ConvertToOmeTiff {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java ConvertToOmeTiff file1 file2 ...");
      return;
    }
    ImageReader reader = new ImageReader();
    OMETiffWriter writer = new OMETiffWriter();
    for (int i=0; i<args.length; i++) {
      String id = args[i];
      int dot = id.indexOf(".");
      String outId = (dot >= 0 ? id.substring(0, dot) : id) + ".ome.tif";
      System.out.print("Converting " + id + " to " + outId + " ");

      // record metadata to OME-XML format
      MetadataStore omexmlMeta = MetadataTools.createOMEXMLMetadata();
      reader.setMetadataStore(omexmlMeta);
      reader.setId(id);

      // configure OME-TIFF writer
      writer.setMetadataRetrieve((MetadataRetrieve) omexmlMeta);
      writer.setId(outId);

      // write out image planes
      int imageCount = reader.getImageCount();
      for (int j=0; j<imageCount; j++) {
        BufferedImage plane = reader.openImage(j);
        // write plane to output file
        writer.saveImage(plane, j == imageCount - 1);
        System.out.print(".");
      }
      writer.close();
      reader.close();
      System.out.println(" [done]");
    }
  }

}
