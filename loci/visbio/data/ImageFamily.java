//
// ImageFamily.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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

import com.jgoodies.plaf.LookUtils;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import visad.*;
import visad.data.*;
import visad.data.avi.AVIForm;
import visad.data.bio.*;
import visad.data.ij.ImageJForm;
import visad.data.qt.PictForm;
import visad.data.qt.QTForm;
import visad.data.tiff.BaseTiffForm;
import visad.data.tiff.TiffForm;

/** A container for VisAD data types that provide data as image planes. */
public class ImageFamily extends FormNode implements FormBlockReader,
  FormFileInformer, FormProgressInformer, MetadataReader, OMEReader
{

  // -- Static fields --

  /** Form id count. */
  private static int id = 0;

  /** Index of form with last successful isThisType identification. */
  private static int lastGood = -1;


  // -- Fields --

  /** List of supported datatype forms. */
  private FormNode[] list;

  /** Current form index. */
  private int index;

  /** Filename of last operation. */
  private String lastId;

  /**
   * String describing the file format
   * corresponding to the most recent operation.
   */
  private String format;


  // -- Constructor --

  /** Construct a family of the supported forms. */
  public ImageFamily() {
    super("ImageFamily" + id++);
    list = new FormNode[] {
      new BioRadForm(), // proprietary
      new IPLabForm(), // proprietary
      new DeltavisionForm(), // proprietary
      new GatanForm(), // proprietary
      new LeicaForm(), // proprietary (companion file)
      new PerkinElmerForm(), // proprietary (companion files)
      new ZVIForm(), // proprietary (needs POIFS)
      new MetamorphForm(), // TIFF variant
      new ZeissForm(), // TIFF variant
      new FluoviewTiffForm(), // TIFF variant; isThisType(String) is slow
      new TiffForm(),
      new AVIForm(),
      new ImageJForm(),
      new QTForm(), // needs QTJ
      new PictForm(), // needs QTJ
      new OpenlabForm() // needs QTJ; isThisType(String) is slow
    };
    index = -1;
  }


  // -- ImageFamily methods --

  /**
   * Gets a string describing the file format
   * corresponding to the most recent operation.
   */
  public String getFormatString() { return format; }

  /** Gets whether QuickTime for Java is available to this JVM. */
  public boolean canDoQT() {
    for (int i=0; i<list.length; i++) {
      if (list[i] instanceof QTForm) {
        QTForm qtform = (QTForm) list[i];
        return qtform.canDoQT();
      }
    }
    return false;
  }

  /** Gets whether QuickTime movies can be saved on this JVM. */
  public boolean canSaveQT() { return canDoQT() && !LookUtils.IS_OS_MAC; }


  // -- FormNode methods --

  /**
   * Saves a VisAD Data object to one of the
   * supported formats at the given location.
   */
  public void save(String id, Data data, boolean replace)
    throws IOException, VisADException
  {
    if (!id.equals(lastId)) initId(id);
    list[index].save(id, data, replace);
  }

  /** Adds data to an existing file. */
  public void add(String id, Data data, boolean replace)
    throws BadFormException
  {
    if (!id.equals(lastId)) initId(id);
    list[index].add(id, data, replace);
  }

  /**
   * Opens an existing file from the given location.
   *
   * @return VisAD Data object containing data from the file.
   */
  public DataImpl open(String id) throws IOException, VisADException {
    if (!id.equals(lastId)) initId(id);
    return list[index].open(id);
  }

  /**
   * Opens an existing file from the given URL.
   *
   * @throws BadFormException Always thrown (method not implemented).
   */
  public DataImpl open(URL url) throws IOException, VisADException {
    throw new BadFormException("ImageFamily.open(URL) unsupported");
  }

  public FormNode getForms(Data data) { return null; }


  // -- FormBlockReader methods --

  /**
   * Obtains the specified block from the given file.
   * @param id The file from which to load data blocks.
   * @param block_number The block number of the block to load.
   * @throws VisADException If the block number is invalid.
   */
  public DataImpl open(String id, int block_number)
    throws IOException, VisADException
  {
    if (!id.equals(lastId)) initId(id);
    if (!(list[index] instanceof FormBlockReader)) return null;
    try { return ((FormBlockReader) list[index]).open(id, block_number); }
    catch (BadFormException exc) { checkLibraryException(exc); throw exc; }
  }

  /**
   * Determines the number of blocks in the given file.
   * @param id The file for which to get a block count.
   */
  public int getBlockCount(String id) throws IOException, VisADException {
    if (!id.equals(lastId)) initId(id);
    if (!(list[index] instanceof FormBlockReader)) return -1;
    try { return ((FormBlockReader) list[index]).getBlockCount(id); }
    catch (BadFormException exc) { checkLibraryException(exc); throw exc; }
  }

  /** Closes any open files. */
  public void close() throws IOException, VisADException {
    for (int i=0; i<list.length; i++) {
      if (!(list[i] instanceof FormBlockReader)) continue;
      FormBlockReader fbr = (FormBlockReader) list[i];
      fbr.close();
    }
  }


  // -- FormFileInformer methods --

  /**
   * Checks if the given string is a valid filename
   * for any of the supported formats.
   */
  public boolean isThisType(String name) {
    for (int i=0; i<list.length; i++) {
      if (!(list[i] instanceof FormFileInformer)) continue;
      FormFileInformer ffi = (FormFileInformer) list[i];
      if (ffi.isThisType(name)) return true;
    }
    return false;
  }

  /**
   * Checks if the given block is a valid header
   * for any of the supported formats.
   */
  public boolean isThisType(byte[] block) {
    for (int i=0; i<list.length; i++) {
      if (!(list[i] instanceof FormFileInformer)) continue;
      FormFileInformer ffi = (FormFileInformer) list[i];
      if (ffi.isThisType(block)) return true;
    }
    return false;
  }

  /** Returns the default file suffixes for any of the supported formats. */
  public String[] getDefaultSuffixes() {
    String[][] s = new String[list.length][];
    int count = 0;
    for (int i=0; i<list.length; i++) {
      if (!(list[i] instanceof FormFileInformer)) continue;
      FormFileInformer ffi = (FormFileInformer) list[i];
      s[i] = ffi.getDefaultSuffixes();
      count += s[i].length;
    }
    String[] suffixes = new String[count];
    int c = 0;
    for (int i=0; i<list.length; i++) {
      for (int j=0; j<s[i].length; j++) suffixes[c++] = s[i][j];
    }
    return suffixes;
  }


  // -- FormProgressInformer methods --

  /**
   * Get the percentage complete of the form's current operation.
   * @return The percentage complete (0.0 - 100.0), or Double.NaN
   *         if no operation is currently taking place.
   */
  public double getPercentComplete() {
    if (index < 0) return Double.NaN;
    else {
      if (!(list[index] instanceof FormProgressInformer)) return Double.NaN;
      FormProgressInformer fpi = (FormProgressInformer) list[index];
      return fpi.getPercentComplete();
    }
  }


  // -- MetadataReader methods --

  protected static final Hashtable EMPTY_TABLE = new Hashtable();

  /**
   * Obtains the specified metadata field's value for the given file.
   * @param field the name associated with the metadata field
   * @return the value, or null should the field not exist
   */
  public Object getMetadataValue(String id, String field)
    throws BadFormException, IOException, VisADException
  {
    if (!id.equals(lastId)) initId(id);
    if (!(list[index] instanceof MetadataReader)) return null;
    return ((MetadataReader) list[index]).getMetadataValue(id, field);
  }

  /**
   * Obtains a hashtable containing all metadata field/value pairs from
   * the given file.
   * @param id the filename
   * @return the hashtable containing all metadata associated with the file
   */
  public Hashtable getMetadata(String id)
    throws BadFormException, IOException, VisADException
  {
    if (!id.equals(lastId)) initId(id);
    if (!(list[index] instanceof MetadataReader)) return EMPTY_TABLE;
    return ((MetadataReader) list[index]).getMetadata(id);
  }


  // -- OMEReader methods --

  /**
   * Obtains a loci.ome.xml.OMENode object representing the
   * file's metadata as an OME-XML DOM structure.
   *
   * @throws BadFormException if the loci.ome.xml package is not present
   */
  public Object getOMENode(String id)
    throws BadFormException, IOException, VisADException
  {
    if (!id.equals(lastId)) initId(id);
    if (!(list[index] instanceof OMEReader)) return null;
    return ((OMEReader) list[index]).getOMENode(id);
  }


  // -- Helper methods --

  private void initId(String id) throws BadFormException {
    // HACK - try last known good type first, for efficiency
    for (int i=-1; i<list.length; i++) {
      if (i == lastGood) continue;
      int ii = i < 0 ? lastGood : i;
      FormFileInformer ffi = (FormFileInformer) list[ii];
      if (ffi.isThisType(id)) {
        index = ii;
        lastGood = ii;
        lastId = id;

        // compute format string
        if (list[ii] instanceof BioRadForm) format = "Bio-Rad PIC file";
        else if (list[ii] instanceof IPLabForm) format = "IPLab file";
        else if (list[ii] instanceof DeltavisionForm) {
          format = "Deltavision file";
        }
        else if (list[ii] instanceof GatanForm) format = "Gatan DM3 file";
        else if (list[ii] instanceof LeicaForm) format = "Leica file";
        else if (list[ii] instanceof PerkinElmerForm) {
          format = "PerkinElmer file";
        }
        else if (list[ii] instanceof ZVIForm) format = "Zeiss ZVI file";
        else if (list[ii] instanceof MetamorphForm) {
          format = "Metamorph STK file";
        }
        else if (list[ii] instanceof ZeissForm) format = "Zeiss LSM file";
        else if (list[ii] instanceof FluoviewTiffForm) {
          format = "Olympus Fluoview TIFF file";
        }
        else if (list[ii] instanceof TiffForm) format = "TIFF file";
        else if (list[ii] instanceof AVIForm) format = "AVI movie";
        else if (list[ii] instanceof ImageJForm) {
          int ndx = id.lastIndexOf(".");
          if (ndx < 0) format = "ImageJ file";
          else {
            String ext = id.substring(ndx + 1).toUpperCase();
            if (ext.equals("JPG")) ext = "JPEG";
            format = ext + " image";
          }
        }
        else if (list[ii] instanceof QTForm) format = "QuickTime movie";
        else if (list[ii] instanceof PictForm) format = "PICT image";
        else if (list[ii] instanceof OpenlabForm) {
          format = "Openlab LIFF file";
        }
        else {
          String s = list[ii].getClass().getName();
          int dot = s.lastIndexOf(".");
          if (dot >= 0) s = s.substring(dot + 1);
          format = "Unknown file (" + s + ")";
        }

        return;
      }
    }
    throw new BadFormException("Unknown file format: " + id);
  }

  /** Prints an error message if the exception indicates a missing library. */
  private void checkLibraryException(BadFormException exc) {
    String msg = exc.getMessage();
    if (msg.equals(QTForm.NO_QT_MSG)) {
      System.err.println("VisBio requires the QuickTime for Java library " +
        "to read this dataset, but QuickTime does not appear to be " +
        "installed. Please install QuickTime from:");
      System.err.println("    http://www.apple.com/quicktime/download/");
    }
    else if (msg.equals(QTForm.EXPIRED_QT_MSG)) {
      System.err.println("Your version of the QuickTime for Java library " +
        "appears to be expired. Please reinstall QuickTime from:");
      System.err.println("    http://www.apple.com/quicktime/download/");
    }
  }


  // -- Main method --

  /**
   * Run 'java loci.visbio.data.ImageFamily in_file'
   * to test read a data files in any format VisBio supports.
   */
  public static void main(String[] args) throws VisADException, IOException {
    BaseTiffForm.testRead(new ImageFamily(), "VisBio", args);
  }

}
