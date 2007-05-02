//
// TemplateGroup.java
//

/*
OME Metadata Notebook application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006-@year@ Christopher Peterson.

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

package loci.ome.notebook;

import java.awt.Point;
import java.util.*;

/** Stores information about a template group. */
public class TemplateGroup {

  // -- Fields --

  /** Number of times this group should be repeated. */
  private int repetitions;

  /** Name of this group. */
  private String name;

  /** List of fields in this group - indexed by a Point(repetition, index). */
  private Hashtable fields;

  /** Field count. */
  private int numFields = 0;

  // -- Constructor --

  public TemplateGroup() {
    fields = new Hashtable();
  }

  // -- TemplateGroup API methods --

  public int getRepetitions() { return repetitions; }

  public void setRepetitions(int repetitions) {
    int oldRepetitions = this.repetitions;
    this.repetitions = repetitions;

    if (oldRepetitions < repetitions) {
      for (int i=oldRepetitions; i<repetitions; i++) {
        for (int j=0; j<numFields; j++) {
          TemplateField f = null;
          if (oldRepetitions == 0) {
            f = ((TemplateField) fields.get(new Point(0, j))).copy();
          }
          else f = ((TemplateField) fields.get(new Point(i - 1, j))).copy();
          adjustMap(f, i);
          fields.put(new Point(i, j), f);
        }
      }
    }
    else if (oldRepetitions > repetitions) {
      for (int i=repetitions; i<oldRepetitions; i++) {
        for (int j=0; j<numFields; j++) {
          fields.remove(new Point(i, j));
        }
      }
    }
  }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }

  public int getNumFields() { return numFields; }

  public TemplateField getField(int repetition, int ndx) {
    return (TemplateField) fields.get(new Point(repetition, ndx));
  }

  public void addField(TemplateField t) {
    if (repetitions > 0) {
      for (int i=0; i<repetitions; i++) {
        TemplateField f = t.copy();
        adjustMap(f, i);
        fields.put(new Point(i, numFields), f);
      }
    }
    else fields.put(new Point(0, numFields), t.copy());
    numFields++;
  }

  public void removeField(int ndx) {
    for (int i=0; i<repetitions; i++) {
      fields.remove(new Point(i, ndx));
    }
  }

  // -- Helper methods --

  /**
   * Adjust the mapping so that the node index matches the repetition number.
   */
  private void adjustMap(TemplateField t, int repetition) {
    String map = t.getMap();
    if (map == null) return;

    if (map.lastIndexOf("-") != -1) {
      map = map.substring(0, map.lastIndexOf("-"));
    }

    map = map + "-" + repetition;
    t.setMap(map);
  }

}
