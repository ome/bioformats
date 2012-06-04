/*
 * #%L
 * OME Notes library for flexible organization and presentation of OME-XML
 * metadata.
 * %%
 * Copyright (C) 2007 - 2012 Open Microscopy Environment:
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

package loci.ome.notes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import loci.formats.meta.AggregateMetadata;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-notes/src/loci/ome/notes/TemplateTools.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-notes/src/loci/ome/notes/TemplateTools.java;hb=HEAD">Gitweb</a></dd></dl>
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
