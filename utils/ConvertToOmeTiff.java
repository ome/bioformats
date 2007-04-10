//
// ConvertToOmeTiff.java
//

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import loci.formats.*;
import loci.formats.ome.OMEXMLMetadataStore;
import loci.formats.out.TiffWriter;

/** Converts the given files to OME-TIFF format. */
public class ConvertToOmeTiff {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java ConvertToOmeTiff file1 file2 ...");
      return;
    }
    ImageReader reader = new ImageReader();
    TiffWriter writer = new TiffWriter();
    // record metadata to OME-XML format
    OMEXMLMetadataStore store = new OMEXMLMetadataStore();
    reader.setMetadataStore(store);
    for (int i=0; i<args.length; i++) {
      String id = args[i];
      String outId = id + ".tif";
      System.out.print("Converting " + id + " to " + outId + " ");
      reader.setId(id); 
      int imageCount = reader.getImageCount();
      // insert TiffData element into OME-XML
      // currently handles only single series (single Image, single Pixels)
      String xml = store.dumpXML();
      int pix = xml.indexOf("<Pixels ");
      int end = xml.indexOf("/>", pix);
      xml = xml.substring(0, end) +
        "><TiffData/></Pixels>" + xml.substring(end + 2);
      // write out image planes
      for (int j=0; j<imageCount; j++) {
        BufferedImage plane = reader.openImage(j);
        Hashtable ifd = null;
        if (j == 0) {
          // save OME-XML metadata to TIFF file's first IFD
          ifd = new Hashtable();
          TiffTools.putIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION, xml);
        }
        // write plane to output file
        writer.saveImage(outId, plane, ifd, j == imageCount - 1);
        System.out.print(".");
      }
      System.out.println(" [done]");
    }
  }

}
