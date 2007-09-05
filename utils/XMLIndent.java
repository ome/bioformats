//
// XMLIndent.java
//

import java.io.BufferedReader;
import java.io.FileReader;
import loci.formats.MetadataTools;

/** Indents XML to be more readable. */
public class XMLIndent {

  public static void main(String[] args) throws Exception {
    for (int i=0; i<args.length; i++) {
      String f = args[i];
      BufferedReader in = new BufferedReader(new FileReader(f));
      StringBuffer sb = new StringBuffer();
      while (true) {
        String line = in.readLine();
        if (line == null) break;
        sb.append(line);
      }
      in.close();
      System.out.println(MetadataTools.indentXML(sb.toString()));
    }
  }

}
