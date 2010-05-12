import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

/**
 * Writes each Z section in a dataset to a separate file.
 */
public class MultiFileExportExample {
  public static void main(String[] args) throws FormatException, IOException {
    if (args.length < 2) {
      System.out.println(
        "Usage: java MultiFileExportExample <infile> <output file extension");
      System.exit(1);
    }

    ImageReader reader = new ImageReader();
    IMetadata metadata = MetadataTools.createOMEXMLMetadata();
    reader.setMetadataStore(metadata);
    reader.setId(args[0]);

    ImageWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(metadata);
    String baseFile = args[0].substring(0, args[0].lastIndexOf("."));
    writer.setId(baseFile + "_s0_z0" + args[1]);

    for (int series=0; series<reader.getSeriesCount(); series++) {
      reader.setSeries(series);
      writer.setSeries(series);

      int planesPerFile = reader.getImageCount() / reader.getSizeZ();
      for (int z=0; z<reader.getSizeZ(); z++) {
        String file = baseFile + "_s" + series + "_z" + z + args[1];
        writer.changeOutputFile(file);
        for (int image=0; image<planesPerFile; image++) {
          writer.saveBytes(image, reader.openBytes(image));
        }
      }
    }

    reader.close();
    writer.close();
  }
}
