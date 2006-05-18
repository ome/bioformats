//
// TiffComment.java
//

import loci.formats.RandomAccessStream;
import loci.formats.TiffTools;
import java.util.Hashtable;

/** Extracts the comment from the first IFD of the given TIFF file(s). */
public class TiffComment {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java TiffComment file1 file2 ...");
      return;
    }
    for (int i=0; i<args.length; i++) {
      // read IFDs
      RandomAccessStream in = new RandomAccessStream(args[i]);
      Hashtable ifd = TiffTools.getFirstIFD(in);
      in.close();

      // extract comment
      Object o = TiffTools.getIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION);
      String comment = null;
      if (o instanceof String) comment = (String) o;
      else if (o instanceof String[]) {
        String[] s = (String[]) o;
        if (s.length > 0) comment = s[0];
      }
      else if (o != null) comment = o.toString();

      if (comment != null) {
        // sanitize line feeds
        comment = comment.replaceAll("\r\n", "\n");
        comment = comment.replaceAll("\r", "\n");

        System.out.println(comment);
      }
    }
  }

}
