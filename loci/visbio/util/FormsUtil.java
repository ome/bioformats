//
// FormsUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.util;

import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

/** FormsUtil contains useful JGoodies Forms functions. */
public abstract class FormsUtil {

  /** Creates a panel with the given components in one row. */
  public static JPanel makeRow(Object o1, Object o2) {
    return makeRow(new Object[] {o1, o2});
  }

  /** Creates a panel with the given components in one row. */
  public static JPanel makeRow(Object o1, Object o2, Object o3) {
    return makeRow(new Object[] {o1, o2, o3});
  }

  /** Creates a panel with the given components in one row. */
  public static JPanel makeRow(Object[] o) {
    return makeRow(o, null, null, false);
  }

  /** Creates a panel with the given components in one row. */
  public static JPanel makeRow(Object[] o, boolean[] grow) {
    return makeRow(o, grow, null, false);
  }

  /** Creates a panel with the given components in one row. */
  public static JPanel makeRow(Object[] o, String row, boolean border) {
    return makeRow(o, null, row, border);
  }

  /** Creates a panel with the given components in one row. */
  public static JPanel makeRow(Object[] o,
    boolean[] grow, String row, boolean border)
  {
    if (o.length < 1) return null;

    StringBuffer sb = new StringBuffer();
    if (grow == null) sb.append("pref");
    else sb.append(grow[0] ? "pref:grow" : "pref");
    for (int i=1; i<o.length; i++) {
      if (grow == null) sb.append(", 3dlu, pref");
      else sb.append(grow[i] ? ", 3dlu, pref:grow" : ", 3dlu, pref");
    }
    PanelBuilder builder = new PanelBuilder(
      new FormLayout(sb.toString(), row == null ? "pref" : row));
    if (border) builder.setDefaultDialogBorder();
    CellConstraints cc = new CellConstraints();

    for (int i=0; i<o.length; i++) {
      if (o[i] instanceof String) {
        JLabel l = builder.addLabel((String) o[i], cc.xy(2 * i + 1, 1));
        if (i < o.length - 1 && o[i + 1] instanceof Component) {
          l.setLabelFor((Component) o[i + 1]);
        }
      }
      else if (o[i] instanceof Component) {
        builder.add((Component) o[i], cc.xy(2 * i + 1, 1));
      }
      else {
        System.err.println("Cannot lay out object #" +
          i + ": " + o[i].getClass().getName());
      }
    }

    return builder.getPanel();
  }

  /** Creates a panel with the given components in one column. */
  public static JPanel makeColumn(Object o1, Object o2) {
    return makeColumn(new Object[] {o1, o2});
  }

  /** Creates a panel with the given components in one column. */
  public static JPanel makeColumn(Object o1, Object o2, Object o3) {
    return makeColumn(new Object[] {o1, o2, o3});
  }

  /** Creates a panel with the given components in one column. */
  public static JPanel makeColumn(Object[] o) {
    return makeColumn(o, null, null, false);
  }

  /** Creates a panel with the given components in one column. */
  public static JPanel makeColumn(Object[] o, boolean[] grow) {
    return makeColumn(o, grow, null, false);
  }

  /** Creates a panel with the given components in one column. */
  public static JPanel makeColumn(Object[] o, String col, boolean border) {
    return makeColumn(o, null, col, border);
  }

  /** Creates a panel with the given components in one column. */
  public static JPanel makeColumn(Object[] o,
    boolean[] grow, String col, boolean border)
  {
    if (o.length < 1) return null;

    StringBuffer sb = new StringBuffer("pref");
    for (int i=1; i<o.length; i++) {
      sb.append(o[i] instanceof String ? ", 9dlu, pref" : ", 3dlu, pref");
    }
    PanelBuilder builder = new PanelBuilder(
      new FormLayout(col == null ? "pref" : col, sb.toString()));
    if (border) builder.setDefaultDialogBorder();
    CellConstraints cc = new CellConstraints();

    for (int i=0; i<o.length; i++) {
      if (o[i] instanceof String) {
        builder.addSeparator((String) o[i], cc.xy(1, 2 * i + 1));
      }
      else if (o[i] instanceof Component) {
        builder.add((Component) o[i], cc.xy(1, 2 * i + 1));
      }
      else {
        System.err.println("Cannot lay out object #" +
          i + ": " + o[i].getClass().getName());
      }
    }

    return builder.getPanel();
  }

}
