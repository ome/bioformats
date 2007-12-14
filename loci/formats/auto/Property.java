//
// Property.java
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

import java.util.Hashtable;

/**
 * CTR TODO Property javadoc.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/auto/Property.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/auto/Property.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class Property {

  // -- Fields --

  private String name;
  private String type;
  private String desc;
  private String prefix;
  private String varName;

  private Hashtable nameMap;
  private Entity entity;

  // -- Constructor --

  public Property(String name, String type, String desc,
    Entity entity, boolean index)
  {
    this.name = name;
    this.type = type;
    this.desc = desc;
    this.entity = entity;
    if (!index) entity.addProperty(this);

    nameMap = new Hashtable();

    // strip off any lower case prefix
    char[] c = name.toCharArray();
    int i = 0;
    while (i < c.length && c[i] >= 'a' && c[i] <= 'z') i++;
    if (i > 0) {
      prefix = name.substring(0, i);
      name = name.substring(i);
    }
    else prefix = "get";

    varName = entity.toVarName(new String(c));
  }

  // -- Property API methods --

  public String name() { return name; }

  public String mappedName(String version) {
    String s = (String) nameMap.get(version);
    return s == null ? name : s;
  }

  public String mappedNameVar(String version) {
    return entity.toVarName(mappedName(version));
  }

  public String desc() { return desc; }

  public String type() { return type; }

  public String prefix() { return prefix; }

  public String varName() { return varName; }

  public void addMappedName(String version, String name) {
    nameMap.put(version, name);
  }

}
