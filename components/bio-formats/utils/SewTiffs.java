//
// SewTiffs.java
//

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FilePattern;
import loci.formats.MetadataTools;
import loci.formats.in.TiffReader;
import loci.formats.meta.IMetadata;
import loci.formats.out.TiffWriter;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * Stitches the first plane from a collection of TIFFs into a single file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/utils/SewTiffs.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/utils/SewTiffs.java">SVN</a></dd></dl>
 */
public class SewTiffs {

  private static final int DOTS = 50;

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println(
        "Usage: java SewTiffs base_name channel_num [time_count]");
      System.exit(1);
    }
    String base = args[0];
    int c = Integer.parseInt(args[1]);
    int num;
    if (args.length < 3) {
      FilePattern fp = new FilePattern(
        new Location(base + "_C" + c + "_TP1.tiff"));
      int[] count = fp.getCount();
      num = count[count.length - 1];
    }
    else num = Integer.parseInt(args[2]);
    System.out.println("Fixing " + base + "_C" + c + "_TP<1-" + num + ">.tiff");
    TiffReader in = new TiffReader();
    TiffWriter out = new TiffWriter();
    String outId = base + "_C" + c + ".tiff";
    System.out.println("Writing " + outId);
    out.setId(outId);
    System.out.print("   ");
    boolean comment = false;

    for (int t=0; t<num; t++) {
      String inId = base + "_C" + c + "_TP" + (t + 1) + ".tiff";
      IMetadata meta = MetadataTools.createOMEXMLMetadata();
      in.setMetadataStore(meta);
      in.setId(inId);
      out.setMetadataRetrieve(meta);

      // read first image plane
      byte[] image = in.openBytes(0);
      in.close();

      if (t == 0) {
        // read first IFD
        RandomAccessInputStream ras = new RandomAccessInputStream(inId);
        TiffParser parser = new TiffParser(ras);
        IFD ifd = parser.getFirstIFD();
        ras.close();

        // preserve TIFF comment
        String desc = ifd.getComment();

        if (desc != null) {
          ifd = new IFD();
          ifd.putIFDValue(IFD.IMAGE_DESCRIPTION, desc);
          comment = true;
          out.saveBytes(image, ifd, t == num - 1);
          System.out.print(".");
          continue;
        }
      }

      // write image plane
      out.saveBytes(image, t == num - 1);

      // update status
      System.out.print(".");
      if (t % DOTS == DOTS - 1) {
        System.out.println(" " + (t + 1));
        System.out.print("   ");
      }
    }
    System.out.println();
    if (comment) System.out.println("OME-TIFF comment saved.");
    else System.out.println("No OME-TIFF comment found.");
  }

}
