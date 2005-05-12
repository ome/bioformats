//
// DataTransform.java
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

package loci.visbio.data;

import java.awt.Font;
import java.math.BigInteger;
import java.util.Vector;
import javax.swing.JComponent;
import loci.visbio.state.*;
import loci.visbio.util.*;
import org.w3c.dom.Element;
import visad.*;

/** DataTransform is the superclass of all data transform types. */
public abstract class DataTransform implements Dynamic, Saveable {

  // -- Static fields --

  /** Next free transform id number. */
  protected static int nextId = 0;


  // -- Fields --

  /** Parent transform from which this transform obtains its data. */
  protected DataTransform parent;

  /** Name of this transform. */
  protected String name;

  /** ID number for this data transform. */
  protected int transformId;

  // Note: All subclasses must populate "lengths" and "dims" fields,
  //       then call makeLabels to populate dimensional axis labels list.

  /** Length of each dimension in the multidimensional structure. */
  protected int[] lengths;

  /**
   * A list of what each dimension in the multidimensional structure means.
   * Common values include Time, Slice, Channel and Lifetime.
   */
  protected String[] dims;

  /** Label for each value at each dimensional axis. */
  protected String[][] labels;

  /** Handles logic for creating thumbnails from transform data. */
  protected ThumbnailHandler thumbs;

  /** Font used for this transform's text mappings. */
  protected Font font = FontChooserPane.DEFAULT_FONT;

  /** List of transform listeners. */
  protected Vector listeners = new Vector();


  // -- Constructors --

  /** Constructs an uninitialized data transform. */
  public DataTransform() { this(null, null); }

  /** Creates a data transform with the given transform as its parent. */
  public DataTransform(DataTransform parent, String name) {
    this.parent = parent;
    this.name = name;
    transformId = nextId++;
  }


  // -- DataTransform API methods --

  // Note: all subclasses should implement:
  //
  //   public static DataTransform makeTransform(DataManager dm)
  //   public static boolean isValidParent(DataTransform data)
  //   public static boolean isParentRequired()
  //
  // See Dataset.java for an example.

  /**
   * Retrieves the data corresponding to the given dimensional position,
   * for the given display dimensionality.
   *
   * @return null if the transform does not provide data of that dimensionality
   */
  public abstract Data getData(int[] pos, int dim, DataCache cache);

  /** Gets whether this transform provides data of the given dimensionality. */
  public abstract boolean isValidDimension(int dim);

  /** Retrieves a set of mappings for displaying this transform effectively. */
  public abstract ScalarMap[] getSuggestedMaps();

  /**
   * Gets a string id uniquely describing this data transform at the given
   * dimensional position, for the purposes of thumbnail caching.
   * If global flag is true, the id is suitable for use in the default,
   * global cache file.
   */
  public abstract String getCacheId(int[] pos, boolean global);

  /**
   * Gets whether this transform should be drawn immediately, or "burned in"
   * at full resolution after the usual delay by a separate thread. It is
   * recommended that most transforms not be immediate, unless they are fast
   * to render and require frequent, high-priority updates (see
   * {@link loci.visbio.overlays.OverlayTransform} for one such example).
   */
  public abstract boolean isImmediate();

  /** Gets the parent of this transform. */
  public DataTransform getParent() { return parent; }

  /** Gets the name of this transform. */
  public String getName() { return name; }

  /** Gets the data transform ID. */
  public int getTransformId() { return transformId; }

  /** Gets length of each dimensional axis. */
  public int[] getLengths() { return ObjectUtil.copy(lengths); }

  /**
   * Gets string descriptors for each dimensional axis type.
   * Common values include Time, Slice, Channel and Lifetime.
   */
  public String[] getDimTypes() { return (String[]) ObjectUtil.copy(dims); }

  /** Gets thumbnail handler for thumbnail-related operations. */
  public ThumbnailHandler getThumbHandler() { return thumbs; }

  /** Gets associated GUI controls for this transform. */
  public JComponent getControls() { return null; }

  /** Gets a description of this transform, with HTML markup. */
  public String getHTMLDescription() {
    StringBuffer sb = new StringBuffer();
    int[] len = getLengths();
    String[] dimTypes = getDimTypes();

    // name
    sb.append(getName());
    sb.append("<p>\n\n");

    if (this instanceof ImageTransform) {
      ImageTransform it = (ImageTransform) this;
      int width = it.getImageWidth();
      int height = it.getImageHeight();
      int rangeCount = it.getRangeCount();

      // list of dimensional axes
      sb.append("Dimensionality: ");
      sb.append(len.length + 2);
      sb.append("D\n");
      sb.append("<ul>\n");
      BigInteger images = BigInteger.ONE;
      if (len.length > 0) {
        for (int i=0; i<len.length; i++) {
          images = images.multiply(new BigInteger("" + len[i]));
          sb.append("<li>");
          sb.append(len[i]);
          sb.append(" ");
          sb.append(getUnitDescription(dimTypes[i]));
          if (len[i] != 1) sb.append("s");
          sb.append("</li>\n");
        }
      }

      // image resolution
      sb.append("<li>");
      sb.append(width);
      sb.append(" x ");
      sb.append(height);
      sb.append(" pixel");
      if (width * height != 1) sb.append("s");
      sb.append("</li>\n");

      // range component count
      sb.append("<li>");
      sb.append(rangeCount);
      sb.append(" range component");
      if (rangeCount != 1) sb.append("s");
      sb.append("</li>\n");
      sb.append("</ul>\n");

      // image and pixel counts
      BigInteger pixels = images.multiply(new BigInteger("" + width));
      pixels = pixels.multiply(new BigInteger("" + height));
      pixels = pixels.multiply(new BigInteger("" + rangeCount));
      sb.append(images);
      sb.append(" image");
      if (!images.equals(BigInteger.ONE)) sb.append("s");
      sb.append(" totaling ");
      sb.append(MathUtil.getValueWithUnit(pixels, 2));
      sb.append("pixel");
      if (!pixels.equals(BigInteger.ONE)) sb.append("s");
      sb.append(".<p>\n");
    }
    else {
      // list of dimensional axes
      sb.append("Dimensional axes: ");
      sb.append(len.length);
      sb.append("\n");
      sb.append("<ul>\n");
      BigInteger count = BigInteger.ONE;
      if (len.length > 0) {
        for (int i=0; i<len.length; i++) {
          count = count.multiply(new BigInteger("" + len[i]));
          sb.append("<li>");
          sb.append(len[i]);
          sb.append(" ");
          sb.append(getUnitDescription(dimTypes[i]));
          if (len[i] != 1) sb.append("s");
          sb.append("</li>\n");
        }
      }
      sb.append("</ul>\n");

      // data count
      sb.append(count.toString());
      sb.append(" dimensional position");
      if (!count.equals(BigInteger.ONE)) sb.append("s");
      sb.append(" total.<p>\n");
    }

    return sb.toString();
  }

  /** Gets label for each dimensional position. */
  public String[][] getLabels() { return labels; }

  /** Sets the font used for this transform's text mappings. */
  public void setFont(Font font) {
    this.font = font;
    notifyListeners(new TransformEvent(this, TransformEvent.FONT_CHANGED));
  }

  /** Gets the font used for this transform's text mappings. */
  public Font getFont() { return font; }

  /**
   * Whenever a display to which this transform is linked changes, the
   * DisplayEvent is passed to this method so that the transform can
   * adjust itself (i.e., is interactive).
   */
  public void displayChanged(DisplayEvent e) { }

  /** Adds the given listener to the list of listeners. */
  public void addTransformListener(TransformListener l) {
    synchronized (listeners) { listeners.add(l); }
  }

  /** Removes the given listener from the list of listeners. */
  public void removeTransformListener(TransformListener l) {
    synchronized (listeners) { listeners.remove(l); }
  }

  /** Notifies transform listeners of a parameter change. */
  public void notifyListeners(TransformEvent e) {
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        TransformListener l = (TransformListener) listeners.elementAt(i);
        l.transformChanged(e);
      }
    }
  }


  // -- Internal DataTransform API methods --

  /** Creates labels based on data transform parameters. */
  protected void makeLabels() {
    labels = new String[lengths.length][];
    for (int i=0; i<lengths.length; i++) {
      labels[i] = new String[lengths[i]];
      for (int j=0; j<lengths[i]; j++) labels[i][j] = "" + (j + 1);
    }
  }


  // -- Object API methods --

  /** Gets a string representation of this transform. */
  public String toString() { return name; }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!isCompatible(dyn)) return false;
    DataTransform data = (DataTransform) dyn;

    return (parent == null ? data.parent == null :
      parent.matches(data.parent)) &&
      ObjectUtil.objectsEqual(name, data.name) &&
      ObjectUtil.arraysEqual(lengths, data.lengths) &&
      ObjectUtil.arraysEqual(dims, data.dims);
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof DataTransform;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (!isCompatible(dyn)) return;
    DataTransform data = (DataTransform) dyn;

    parent = data.parent;
    name = data.name;
    lengths = data.lengths;
    dims = data.dims;
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() {
    notifyListeners(new TransformEvent(this, TransformEvent.DATA_REMOVED));
    listeners.removeAllElements();
  }


  // -- Saveable API methods --

  /**
   * Writes the current state to the given DOM element
   * (a child of "DataTransforms").
   */
  public void saveState(Element el) throws SaveException {
    el.setAttribute("class", getClass().getName());
    el.setAttribute("id", "" + transformId);
    if (parent != null) el.setAttribute("parent", "" + parent.transformId);
    el.setAttribute("name", name);
    el.setAttribute("lengths", ObjectUtil.arrayToString(lengths));
    el.setAttribute("dims", ObjectUtil.arrayToString(dims));
  }

  /**
   * Restores the current state from the given DOM element
   * (a child of "DataTransforms").
   */
  public void restoreState(Element el) throws SaveException {
    transformId = Integer.parseInt(el.getAttribute("id"));
    name = el.getAttribute("name");
    // NB: parent reference is restored in DataManager.restoreState,
    // since individual DataTransforms are not aware of each other
    lengths = ObjectUtil.stringToIntArray(el.getAttribute("lengths"));
    dims = ObjectUtil.stringToStringArray(el.getAttribute("dims"));
  }


  // -- Utility methods --

  /**
   * Gets the unit description for a dimensional type. For example,
   * for dimensional type "Slice" the unit would be "focal plane,"
   * and for type "Channel" the unit would be "spectral channel."
   */
  public static String getUnitDescription(String dimType) {
    if (dimType.equals("Time")) return "time point";
    else if (dimType.equals("Slice")) return "focal plane";
    else if (dimType.equals("Channel")) return "spectral channel";
    else if (dimType.equals("Lifetime")) return "lifetime bin";
    else return "sample";
  }

}
