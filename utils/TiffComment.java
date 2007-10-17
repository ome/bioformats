//
// TiffComment.java
//

import loci.formats.TiffTools;

/** Extracts the comment from the first IFD of the given TIFF file(s). */
public class TiffComment {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java TiffComment file1 file2 ...");
      return;
    }
    for (int i=0; i<args.length; i++) {
      String comment = TiffTools.getComment(args[i]);
      System.out.println(comment == null ?
        args[i] + ": no TIFF comment found." : comment);
    }
  }

}
