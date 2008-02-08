//
// TemplateTools.java
//

/*
OME Notes library for flexible organization and presentation of OME-XML
metadata. Copyright (C) 2006-@year@ Melissa Linkert and Christopher Peterson.

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

package loci.ome.notes;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import loci.formats.meta.AggregateMetadata;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/ome/notes/TemplateTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/ome/notes/TemplateTools.java">SVN</a></dd></dl>
 */
public class TemplateTools {

  /** Get the value corresponding to the given map. */
  public static String getString(AggregateMetadata store, String map,
    boolean value) throws Exception
  {
    if (map == null || store == null) return null;

    int openParentheses = map.indexOf("(");
    String method = null, params = null;
    if (openParentheses != -1) {
      method = map.substring(0, openParentheses);
      params = map.substring(openParentheses + 1, map.length() - 1);
    }
    else method = map;

    method = "get" + method;

    int[] indices = null;
    if (params != null) {
      StringTokenizer s = new StringTokenizer(params, ",");
      int count = s.countTokens();
      indices = new int[count];
      for (int i=0; i<count; i++) {
        indices[i] = Integer.parseInt(s.nextToken().trim());
      }
    }

    Class[] c = null;
    Object[] o = null;
    if (indices != null) {
      c = new Class[indices.length];
      Arrays.fill(c, Integer.TYPE);
      o = new Object[indices.length];
      for (int i=0; i<indices.length; i++) {
        o[i] = new Integer(indices[i]);
      }
    }

    Method m = AggregateMetadata.class.getMethod(method, c);
    Object r = m.invoke(store, o);

    return r == null ? null : r.toString();
  }

  /** Get the number of nodes matching this map. */
  public static int getNodeCount(AggregateMetadata store, String map)
    throws Exception
  {
    /*
    if (map == null || map.length() == 0 || root == null) return 0;
    map = map.substring(0, map.lastIndexOf(":"));
    map = map.substring(map.lastIndexOf(":") + 1);
    if (map.indexOf("-") != -1) map = map.substring(0, map.indexOf("-"));
    return DOMUtil.findElementList(map, root.getOMEDocument(true)).size();
    */
    return 1;
  }

  /** Get the value from the given component. */
  public static Object getComponentValue(JComponent c) {
    Object value = null;

    if (c instanceof JCheckBox) {
      value = new Boolean(((JCheckBox) c).isSelected());
    }
    else if (c instanceof JComboBox) {
      value = ((JComboBox) c).getSelectedItem();
    }
    else if (c instanceof JScrollPane) {
      JScrollPane scroll = (JScrollPane) c;
      JViewport view = scroll.getViewport();
      value = ((JTextArea) view.getView()).getText();
    }
    else if (c instanceof JSpinner) {
      value = ((JSpinner) c).getValue();
    }

    return value;
  }

}
