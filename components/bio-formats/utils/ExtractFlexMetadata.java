//
// ExtractFlexMetadata.java
//

import java.io.File;
import java.io.FileWriter;

import loci.common.RandomAccessInputStream;
import loci.formats.in.FlexReader;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * Convenience method to extract the metadata from
 * all the Flex files present in a directory.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/utils/ExtractFlexMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/utils/ExtractFlexMetadata.java">SVN</a></dd></dl>
 */
public class ExtractFlexMetadata {

  public static void main(String[] args) throws Exception {
    File dir;
    if (args.length != 1 || !(dir = new File(args[0])).canRead()) {
      System.out.println("Usage: java ExtractFlexMetadata dir");
      return;
    }
    for (File file:dir.listFiles()) {
      if (file.getName().endsWith(".flex")) {
        String id = file.getPath();
        int dot = id.lastIndexOf(".");
        String outId = (dot >= 0 ? id.substring(0, dot) : id) + ".xml";
        RandomAccessInputStream in = new RandomAccessInputStream(id);
        TiffParser parser = new TiffParser(in);
        IFD firstIFD = parser.getIFDs().get(0);
        String xml = firstIFD.getIFDTextValue(FlexReader.FLEX);
        in.close();
        FileWriter writer = new FileWriter(new File(outId));
        writer.write(xml);
        writer.close();
        System.out.println("Writing header of: " + id);
      }
    }
    System.out.println("Done");
  }
}
