//
// Binary.java
//

import java.io.*;

/**
 * Attempts to convert a string of zeroes and ones into readable text.
 * Try 'java Binary bits.txt' for an example.
 */
public class Binary {

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Usage: java Binary filename.txt");
      System.exit(2);
    }
    File file = new File(args[0]);
    BufferedReader fin = new BufferedReader(new FileReader(file));
    StringBuffer sb = new StringBuffer((int) file.length());
    sb.append(fin.readLine());
    String bits = sb.toString();
    System.out.println("bits: " + bits);
    System.out.println();
    for (int len=7; len<=8; len++) {
      for (int offset=0; offset<len; offset++) {
        System.out.print("len=" + len + ", offset=" + offset + ": ");
        decode(bits, len, offset);
      }
    }
  }

  public static void decode(String bits, int len, int offset) {
    int bitlen = bits.length();
    for (int i=offset; i<=bitlen-len; i+=len) {
      String s = bits.substring(i, i + len);
      int q = Integer.parseInt(s, 2);
      if (q >= 32 && q != 127) System.out.print((char) q);
    }
    System.out.println();
  }

}
