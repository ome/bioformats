//
// ImageFamily.java
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

import java.io.IOException;

import java.net.URL;

import java.util.Hashtable;

import visad.Data;
import visad.DataImpl;
import visad.VisADException;

import visad.data.*;

import visad.data.avi.AVIForm;

import visad.data.bio.*;

import visad.data.ij.ImageJForm;

import visad.data.qt.PictForm;
import visad.data.qt.QTForm;

import visad.data.tiff.TiffForm;

/**
 * A container for VisAD data types that provide data as image planes.
 * These formats currently include Bio-Rad PIC, TIFF, Fluoview TIFF,
 * QuickTime, AVI and ImageJ formats.
 */
public class ImageFamily extends FormNode implements FormBlockReader,
  FormFileInformer, FormProgressInformer, MetadataReader
{

  // -- Static fields --

  /** Form id count. */
  private static int id = 0;


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
      new BioRadForm(),
      new MetamorphForm(),
      new OpenlabForm(),
      new FluoviewTiffForm(),
      new ZeissForm(),
      new ZVIForm(),
      new TiffForm(),
      new ImageJForm(),
      new AVIForm(),
      new QTForm(),
      new PictForm()
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
    return ((FormBlockReader) list[index]).open(id, block_number);
  }

  /**
   * Determines the number of blocks in the given file.
   * @param id The file for which to get a block count.
   */
  public int getBlockCount(String id) throws IOException, VisADException {
    if (!id.equals(lastId)) initId(id);
    if (!(list[index] instanceof FormBlockReader)) return -1;
    return ((FormBlockReader) list[index]).getBlockCount(id);
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


  // -- Helper methods --

  private void initId(String id) throws BadFormException {
    for (int i=0; i<list.length; i++) {
      FormFileInformer ffi = (FormFileInformer) list[i];
      if (ffi.isThisType(id)) {
        index = i;
        lastId = id;

        // compute format string
        if (list[i] instanceof BioRadForm) format = "Bio-Rad PIC file";
        else if (list[i] instanceof MetamorphForm) {
          format = "Metamorph STK file";
        }
        else if (list[i] instanceof OpenlabForm) {
          format = "Openlab LIFF file";
        }
        else if (list[i] instanceof FluoviewTiffForm) {
          format = "Olympus Fluoview TIFF file";
        }
        else if (list[i] instanceof ZeissForm) format = "Zeiss LSM file";
        else if (list[i] instanceof ZVIForm) format = "Zeiss ZVI file";
        else if (list[i] instanceof TiffForm) format = "TIFF file";
        else if (list[i] instanceof ImageJForm) {
          int ndx = id.lastIndexOf(".");
          if (ndx < 0) format = "ImageJ file";
          else {
            String ext = id.substring(ndx + 1).toUpperCase();
            if (ext.equals("JPG")) ext = "JPEG";
            format = ext + " image file";
          }
        }
        else if (list[i] instanceof AVIForm) format = "AVI movie";
        else if (list[i] instanceof QTForm) format = "QuickTime movie";
        else format = "Unknown format";

        return;
      }
    }
    throw new BadFormException("Unknown file format: " + id);
  }


  // -- Main method --

  /**
   * Run 'java loci.visbio.ImageFamily in_file out_file' to convert
   * in_file to out_file in any of the supported formats.
   */
  public static void main(String[] args) throws VisADException, IOException {
    if (args == null || args.length < 1 || args.length > 2) {
      System.out.println("To convert a file, run:");
      System.out.println(
        "  java loci.visbio.ImageFamily in_file out_file");
      System.out.println("To test read an image file, run:");
      System.out.println("  java loci.visbio.ImageFamily in_file");
      System.exit(2);
    }

    if (args.length == 1) {
      // Test read image file
      ImageFamily form = new ImageFamily();
      System.out.print("Reading " + args[0] + " ");
      Data data = form.open(args[0]);
      System.out.println("[done]");
      System.out.println("MathType =\n" + data.getType().prettyString());
    }
    else if (args.length == 2) {
      // Convert file
      System.out.print(args[0] + " -> " + args[1] + " ");
      DefaultFamily loader = new DefaultFamily("loader");
      DataImpl data = loader.open(args[0]);
      loader = null;
      ImageFamily form = new ImageFamily();
      form.save(args[1], data, true);
      System.out.println("[done]");
    }
    System.exit(0);
  }

}
