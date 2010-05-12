import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

public class TiledExportExample {
  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println("Usage: java TiledExportExample <infile> <outfile>");
      System.exit(1);
    }

    ImageReader reader = new ImageReader();
    ImageWriter writer = new ImageWriter();

    IMetadata meta = MetadataTools.createOMEXMLMetadata();
    reader.setMetadataStore(meta);

    reader.setId(args[0]);
    writer.setMetadataRetrieve(meta);
    writer.setId(args[1]);

    for (int series=0; series<reader.getSeriesCount(); series++) {
      reader.setSeries(series);
      writer.setSeries(series);

      for (int image=0; image<reader.getImageCount(); image++) {
        for (int row=0; row<2; row++) {
          for (int col=0; col<2; col++) {
            int w = reader.getSizeX() / 2;
            int h = reader.getSizeY() / 2;
            int x = col * w;
            int y = row * h;
            /* debug */ System.out.println("[" + x + ", " + y + ", " + w + ", " + h + "]");
            byte[] buf = reader.openBytes(image, x, y, w, h);
            writer.saveBytes(image, buf, x, y, w, h);
          }
        }
      }
    }

    reader.close();
    writer.close();
  }
}
