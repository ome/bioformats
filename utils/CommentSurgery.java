//
// CommentSurgery.java
//

import java.io.*;
import loci.formats.DataTools;
import loci.formats.TiffTools;

/**
 * Performs "surgery" on a TIFF ImageDescription comment, particularly the
 * OME-XML comment found in OME-TIFF files. Note that this code must be
 * tailored to a specific need by editing the commented out code below to
 * make desired alterations to the comment.
 */
public class CommentSurgery {
  public static void main(String[] args) throws Exception {
    for (int i=0; i<args.length; i++) {
      String f = args[i];
      System.out.println("Processing file " + f);
      RandomAccessFile in = new RandomAccessFile(f, "rw");
      byte[] four = new byte[4];
      in.readFully(four);
      boolean little = (four[0] == 'I');
      int first = DataTools.read4SignedBytes(in, little);
      in.seek(first);
      long fp = first;
      int numEntries = DataTools.read2SignedBytes(in, little);
      fp += 2;
      int oldCount = -1; // length of old comment
      long countOffset = -1; // offset to comment count field
      long commentOffset = -1; // offset to comment itself
      String comment = null;
      for (int j=0; j<numEntries; j++) {
        int tag = DataTools.read2SignedBytes(in, little);
        int type = DataTools.read2SignedBytes(in, little);
        fp += 4;
        countOffset = fp;
        oldCount = DataTools.read4SignedBytes(in, little);
        commentOffset = DataTools.read4SignedBytes(in, little);
        fp += 8;
        if (tag != TiffTools.IMAGE_DESCRIPTION) continue;
        in.seek(commentOffset);
        byte[] b = new byte[oldCount];
        in.readFully(b);
        long len = new File(f).length();
        if (in.getFilePointer() != len) {
          System.out.println("ERROR: Comment is not at end of file.");
        //  return;
        }
        comment = new String(b);
        if (comment.length() != oldCount) {
          System.out.println("ERROR: Comment length doesn't match count.");
          return;
        }
        break;
      }
      if (comment == null) {
        System.out.println("ERROR: No OME-XML comment.");
        return;
      }
      else {
        // comment is altered here; tailor for whatever is needed
        System.out.println("Old comment =\n" + comment);
        /* Example 1:
        comment =
          comment.replaceAll("LogicalChannel:OWS", "LogicalChannel:OWS347-");
        */
        /* Example 2:
        int ndx = comment.indexOf("<Pixels");
        ndx = comment.indexOf(" />", ndx);
        String tiffData = "<TiffData";
        int tndx = f.indexOf("_TP");
        int tval = -1;
        if (tndx >= 0) {
          String s = f.substring(tndx + 3, tndx + 5);
          char c = s.charAt(1);
          if (c < '0' || c > '9') s = s.substring(0, 1);
          int t = Integer.parseInt(s) - 1;
          tiffData += " FirstT=\"" + t + "\"";
        }
        int cndx = f.indexOf("_C");
        int cval = -1;
        if (cndx >= 0) {
          int c = Integer.parseInt(f.substring(cndx + 2, cndx + 3)) - 1;
          tiffData += " FirstC=\"" + c + "\"";
        }
        tiffData += " />";
        comment = comment.substring(0, ndx) + ">" + tiffData +
          "</Pixels><CustomAttributes /></Image><CustomAttributes /></OME>";
        comment = comment.replaceAll("SizeC=\"0\"", "SizeC=\"2\"");
        //comment = comment.replaceAll("SizeT=\"200\"", "SizeT=\"43\"");
        comment = comment.replaceAll("Maimoon ", "Maimoon");
        */
        System.out.println("New comment =\n" + comment);

        // splice in new comment
        byte[] bb = comment.getBytes();
        if (bb[bb.length - 1] != 0) {
          byte[] bb2 = new byte[bb.length + 1];
          System.arraycopy(bb, 0, bb2, 0, bb.length);
          bb = bb2;
          System.out.println("WARNING: no null terminator (added one)");
        }
        byte[] countBytes = new byte[4];
        if (little) {
          countBytes[0] = (byte) (bb.length % 256);
          countBytes[1] = (byte) ((bb.length >> 8) % 256);
          countBytes[2] = (byte) ((bb.length >> 16) % 256);
          countBytes[3] = (byte) ((bb.length >> 24) % 256);
        }
        else {
          countBytes[0] = (byte) ((bb.length >> 24) % 256);
          countBytes[1] = (byte) ((bb.length >> 16) % 256);
          countBytes[2] = (byte) ((bb.length >> 8) % 256);
          countBytes[3] = (byte) (bb.length % 256);
        }
        long oldLength = in.length();
        long newLength = oldLength - oldCount + bb.length;
        in.setLength(newLength);
        in.seek(countOffset);
        in.write(countBytes);
        in.seek(commentOffset);
        in.write(bb);
      }
      in.close();
    }
  }
}
