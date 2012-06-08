/*
 * #%L
 * OME database I/O package for communicating with OME and OMERO servers.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.ome.io;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import loci.common.Constants;

/**
 * Stores credentials for logging into an OME/OMERO server.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-io/src/loci/ome/io/OMECredentials.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-io/src/loci/ome/io/OMECredentials.java;hb=HEAD">Gitweb</a></dd></dl>
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

  /**
   * Get credentials from a string. The following two formats are recognized:
   * <code>ip.address?port=54321&username=login&password=secret&id=12345</code>
   * or:
   * <pre>
   * server=ip.address
   * port=54321
   * user=login
   * password=secret
   * id=12345
   * </pre>
   * Strings are assumed to be encoded with the HTML form encoding scheme,
   * and will be decoded accordingly.
   */
  public OMECredentials(String s) {
    final String invalidMsg = "Invalid credentials string";
    if (s == null) {
      throw new IllegalArgumentException(invalidMsg);
    }

    String split = s.indexOf("\n") < 0 ? "?&" : "\n";
    StringTokenizer st = new StringTokenizer(s, split);
    while (st.hasMoreTokens()) {
      String token = st.nextToken();

      int equals = token.indexOf("=");
      String key = equals < 0 ? "server" : token.substring(0, equals);
      String value = token.substring(equals + 1);

      try {
        key = URLDecoder.decode(key, Constants.ENCODING).trim();
        value = URLDecoder.decode(value, Constants.ENCODING).trim();
      }
      catch (UnsupportedEncodingException exc) {
        throw new IllegalArgumentException(invalidMsg, exc);
      }

      if (key.equals("server")) server = value;
      else if (key.equals("username")) username = value;
      else if (key.equals("port")) port = value;
      else if (key.equals("password")) password = value;
      else if (key.equals("id")) {
        try {
          imageID = Long.parseLong(value);
        }
        catch (NumberFormatException exc) {
          throw new IllegalArgumentException(invalidMsg, exc);
        }
      }
    }
  }

}
