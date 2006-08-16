//
// XMLObject.java
//

/*
OME Plugin for ImageJ plugin for transferring images to and from an OME
database. Copyright (C) 2004-2006 Philip Huettl and Melissa Linkert.

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

package loci.ome.ij;

/**
 * XMLObject is the class that handles display and
 * editing of a piece of metadata in the tree.
 *
 * @author Philip Huettl pmhuettl at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */

public class XMLObject {

  // -- Constants --

  public static final int IMAGE = 1;
  public static final int PROJECT = 2;
  public static final int DATASET = 3;
  public static final int ELEMENT = 4;
  public static final int CUSTOMHEADING = 5;
  public static final int FEATUREHEADING = 6;
  public static final int FEATURE = 7;
  public static final int DATASETHEADING = 8;
  public static final int IMAGEHEADING = 9;
  public static final int DATASETREF = 10;
  public static final int PROJECTHEADING = 11;
  public static final int PIXELHEADING = 12;
  public static final int PIXELS = 13;
  public static final int ATTRIBUTE = 0;
  public static final int READONLY = -1;

  // -- Fields --

  private String attr, value;
  private int type;

  /** Create a custom attribute node */
  public XMLObject(String attr, String value, int type) {
    this.type = type;
    this.attr = attr;
    this.value = value;
  }

  /** Create a custom element node */
  public XMLObject(String attr, int type) {
    this.type = type;
    this.attr = attr;
  }

  /** Create a heading node */
  public XMLObject(int type) {
    this.type = type;
    switch (type) {
      case IMAGEHEADING: this.attr = "Image"; break;
      case FEATUREHEADING: this.attr = "Feature"; break;
      case CUSTOMHEADING: this.attr = "Custom Attributes"; break;
      case DATASETREF: this.attr = "Dataset References"; break;
      case PROJECTHEADING: this.attr = "Project"; break;
      case DATASETHEADING: this.attr = "Dataset"; break;
      case PIXELHEADING: this.attr = "Pixels"; break;
    }
  }

  /** Create an ome heading node */
  public XMLObject(String attr) {
    this.attr = attr;
    type = READONLY;
  }

  public String toString() {
    return attr;
  }

  /** sets new value of this attr */
  public void setValue(String newValue) {
    if (type == ATTRIBUTE || type == IMAGE || type == PROJECT ||
      type == DATASET || type == FEATURE) value = newValue;
  }

  public String getValue() {
    return value;
  }

  public int getType() {
    return type;
  }

}
