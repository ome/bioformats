//
// OMECredentials.java
//

/*
OME database I/O package for communicating with OME and OMERO servers.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Philip Huettl.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.ome.io;

import java.util.StringTokenizer;

/**
 * Stores credentials for logging into an OME/OMERO server.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/ome/io/OMECredentials.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/ome/io/OMECredentials.java">SVN</a></dd></dl>
 */
public class OMECredentials {

  // -- Fields --

  public String server;
  public String port;
  public String username;
  public String password;
  public long imageID;
  public boolean isOMERO;

  // -- Constructor --

  public OMECredentials(String server, String username, String password) {
    this.server = server;
    this.username = username;
    this.password = password;
  }

  /** Get credentials from a string. */
  public OMECredentials(String s) {
    if (s == null || s.trim().equals("")) {
      throw new IllegalArgumentException("Invalid credentials string");
    }

    if (s.indexOf("\n") != -1) {
      StringTokenizer st = new StringTokenizer(s, "\n");
      String token = null;
      while (st.hasMoreTokens()) {
        token = st.nextToken();
        String key = token.substring(0, token.indexOf("=")).trim();
        String value = token.substring(token.indexOf("=") + 1).trim();
        if (key.equals("server")) server = value;
        else if (key.equals("username")) username = value;
        else if (key.equals("port")) port = value;
        else if (key.equals("password")) password = value;
        else if (key.equals("id")) imageID = Long.parseLong(value);
      }
    }
    else {
      server = s.substring(0, s.lastIndexOf("?"));

      int first = server.indexOf(":");
      int last = server.lastIndexOf(":");
      if (server.indexOf("http://") == -1) {
        first = 0;
        if (last < 0) last = 0;
      }
      if (first != last) {
        port = server.substring(last + 1);
        server = server.substring(0, last);
      }

      int ndx = s.indexOf("&");
      username = s.substring(s.lastIndexOf("?") + 6, ndx);
      int end = s.indexOf("&", ndx + 1);
      if (end == -1) end = s.length();
      password = s.substring(ndx + 10, end);
      ndx = s.indexOf("&", ndx + 1);
      if (ndx > 0) imageID = Long.parseLong(s.substring(ndx + 4));
    }
  }

}
