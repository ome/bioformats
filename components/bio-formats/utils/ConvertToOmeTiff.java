//
// ConvertToOmeTiff.java
//

import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.out.OMETiffWriter;

/**
 * Converts the given files to OME-TIFF format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/utils/ConvertToOmeTiff.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/utils/ConvertToOmeTiff.java">SVN</a></dd></dl>
 */
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
      int dot = id.lastIndexOf(".");
      String outId = (dot >= 0 ? id.substring(0, dot) : id) + ".ome.tif";
      System.out.print("Converting " + id + " to " + outId + " ");

      // record metadata to OME-XML format
      IMetadata omexmlMeta = MetadataTools.createOMEXMLMetadata();
      reader.setMetadataStore(omexmlMeta);
      reader.setId(id);

      // configure OME-TIFF writer
      writer.setMetadataRetrieve(omexmlMeta);
      writer.setId(outId);
      //writer.setCompression("J2K");

      // write out image planes
      int seriesCount = reader.getSeriesCount();
      for (int s=0; s<seriesCount; s++) {
        reader.setSeries(s);
        int planeCount = reader.getImageCount();
        for (int p=0; p<planeCount; p++) {
          byte[] plane = reader.openBytes(p);
          // write plane to output file
          writer.saveBytes(plane, s, p == planeCount - 1,
            (p == planeCount - 1) && (s == seriesCount - 1));
          System.out.print(".");
        }
      }
      writer.close();
      reader.close();
      System.out.println(" [done]");
    }
  }

}
