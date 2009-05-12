//
// ExtractFlexMetadata.java
//

import java.io.File;
import java.io.FileWriter;

import loci.common.RandomAccessInputStream;
import loci.formats.TiffTools;

/** Convenience method to extract the metadata from all the Flex files present in a directory. */
public class ExtractFlexMetadata {

  public static void main(String[] args) throws Exception {
    File dir;
    if (args.length != 1 || !(dir = new File(args[0])).canRead()) {
      System.out.println("Usage: java ExtractFlexMetdata dir");
      return;
    }
    for (File file:dir.listFiles()) {
      if (file.getName().endsWith(".flex")) {
        String id = file.getPath();
        int dot = id.lastIndexOf(".");
        String outId = (dot >= 0 ? id.substring(0, dot) : id) + ".xml";
        RandomAccessInputStream in = new RandomAccessInputStream(id);
        String xml = (String) TiffTools.getIFDValue(TiffTools.getIFDs(in)[0],
          65200, true, String.class);
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
