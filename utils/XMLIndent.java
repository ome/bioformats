//
// XMLIndent.java
//

import java.io.*;
import java.util.StringTokenizer;

/** Indents XML to be more readable. Does not handle CDATA. */
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
      StringTokenizer st = new StringTokenizer(sb.toString(), "<>");
      int indent = 0;
      while (st.hasMoreTokens()) {
        String token = st.nextToken().trim();
        if (token.equals("")) continue;
        if (token.startsWith("/")) indent -= 2;
        for (int j=0; j<indent; j++) System.out.print(" ");
        System.out.println("<" + token + ">");
        if (!token.startsWith("/") && !token.endsWith("/")) indent += 2;
      }
      System.out.println();
    }
  }

}
