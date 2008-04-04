//
// DownloadURL.java
//

import java.io.*;
import java.net.URL;

public class DownloadURL {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Please specify a URL as an argument.");
      System.exit(1);
    }
    String urlPath = args[0];
    URL url = new URL(urlPath);
    String filename = null;
    if (args.length > 1) filename = args[1];
    else {
      int slash = urlPath.lastIndexOf("/");
      int end = urlPath.lastIndexOf("?");
      if (end < 0) end = urlPath.length();
      filename = urlPath.substring(slash + 1, end);
      if (filename.equals("")) filename = "index.html";
    }
    System.out.println("Downloading " + urlPath + " to " + filename);
    InputStream in = url.openStream();
    FileOutputStream out = new FileOutputStream(filename);
    byte[] buf = new byte[65536];
    while (true) {
      int r = in.read(buf);
      if (r <= 0) break;
      out.write(buf, 0, r);
    }
    out.close();
    in.close();
  }

}
