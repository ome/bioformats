//
// LineTracker.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.auto;

import java.util.Vector;
import java.util.StringTokenizer;

/**
 * Manages line output with intelligent wrapping.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/auto/LineTracker.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/auto/LineTracker.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LineTracker {

  // -- Constants --

  private static final int MAX_LEN = 80;

  // -- Fields --

  private Vector lines = new Vector();
  private StringBuffer line = new StringBuffer();
  private boolean wrapped = false;

  // -- LineTracker API methods --

  public void newline() {
    lines.add(line.toString());
    line.setLength(0);
    wrapped = false;
  }

  public void add(String s) { add(s, ""); }

  public void add(String s, String lead) {
    if (line.length() + s.length() > MAX_LEN) {
      newline();
      wrapped = true;
      s = lead + s.trim();
    }
    line.append(s);
  }

  public void addTokens(String s, String lead) {
    StringTokenizer st = new StringTokenizer(s);
    String lastToken = st.hasMoreTokens() ? st.nextToken() : null;
    if (lastToken != null) add(lastToken);
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (!lastToken.endsWith("(")) token = " " + token;
      add(token, lead);
      lastToken = token;
    }
  }

  public boolean hasWrapped() { return wrapped; }

  // -- Object API methods --

  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<lines.size(); i++) sb.append(lines.get(i) + "\n");
    if (line.length() > 0) sb.append(line);
    return sb.toString();
  }

}
