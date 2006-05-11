//
// EditOMETIFF.java
//

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;
import loci.formats.*;

/** Allows raw user OME-XML comment editing for the given OME-TIFF files. */
public class EditOMETIFF {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java EditOMETIFF file1 file2 ...");
      return;
    }
    TiffReader opener = new TiffReader();
    TiffWriter saver = new TiffWriter();
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    for (int i=0; i<args.length; i++) {
      String f = args[i];
      String nf = "new-" + f;
      System.out.print("Reading " + f + " ");
      DataInputStream fin = new DataInputStream(new FileInputStream(f));
      Hashtable[] ifds = TiffTools.getIFDs(fin);
      fin.close();
      String ome = (String)
        TiffTools.getIFDValue(ifds[0], TiffTools.IMAGE_DESCRIPTION);
      System.out.println("[done]");
      System.out.println("OME-XML block =");
      System.out.println(ome);
      System.out.println("Enter new OME-XML block (no line breaks):");
      String xml = in.readLine();
      System.out.print("Saving " + f);
      int num = opener.getImageCount(f);
      for (int j=0; j<num; j++) {
        System.out.print(".");
        BufferedImage img = opener.openImage(f, j);

        Hashtable ifd = new Hashtable();
        if (j == 0) {
          // update OME-XML block
          TiffTools.putIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION, xml);
        }

        // write file to disk
        saver.saveImage(nf, img, ifd, j == num - 1);
      }
      File oldFile = new File(f);
      File newFile = new File(nf);
      oldFile.delete();
      newFile.renameTo(oldFile);
      System.out.println(" [done]");
    }
  }

}
