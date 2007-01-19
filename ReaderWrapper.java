//
// ReaderWrapper.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Abstract superclass of reader logic that wraps other readers.
 * All methods are simply delegated to the wrapped reader.
 */
public abstract class ReaderWrapper implements IFormatReader {

  // -- Fields --

  /** FormatReader used to read the file. */
  protected IFormatReader reader;

  // -- Constructors --

  /** Constructs a reader wrapper around a new image reader. */
  public ReaderWrapper() { this(new ImageReader()); }

  /** Constructs a reader wrapper around the given reader. */
  public ReaderWrapper(IFormatReader r) {
    if (r == null) {
      throw new IllegalArgumentException("Format reader cannot be null");
    }
    reader = r;
  }

  // -- ReaderWrapper API methods --

  /** Gets the wrapped reader. */
  public IFormatReader getReader() { return reader; }

  // -- IFormatReader API methods --

  public boolean isThisType(byte[] block) {
    return reader.isThisType(block);
  }

  public int getImageCount(String id) throws FormatException, IOException {
    return reader.getImageCount(id);
  }

  public boolean isRGB(String id) throws FormatException, IOException {
    return reader.isRGB(id);
  }

  public int getSizeX(String id) throws FormatException, IOException {
    return reader.getSizeX(id);
  }

  public int getSizeY(String id) throws FormatException, IOException {
    return reader.getSizeY(id);
  }

  public int getSizeZ(String id) throws FormatException, IOException {
    return reader.getSizeZ(id);
  }

  public int getSizeC(String id) throws FormatException, IOException {
    return reader.getSizeC(id);
  }

  public int getSizeT(String id) throws FormatException, IOException {
    return reader.getSizeT(id);
  }

  public int getPixelType(String id) throws FormatException, IOException {
    return reader.getPixelType(id);
  }

  public int getEffectiveSizeC(String id) throws FormatException, IOException {
    return FormatReader.getEffectiveSizeC(isRGB(id), getSizeC(id));
  }

  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    return reader.getChannelGlobalMinimum(id, theC);
  }

  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    return reader.getChannelGlobalMaximum(id, theC);
  }

  public int getThumbSizeX(String id) throws FormatException, IOException {
    return reader.getThumbSizeX(id);
  }

  public int getThumbSizeY(String id) throws FormatException, IOException {
    return reader.getThumbSizeY(id);
  }

  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return reader.isLittleEndian(id);
  }

  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    return reader.getDimensionOrder(id);
  }

  public boolean isOrderCertain(String id) throws FormatException, IOException {
    return reader.isOrderCertain(id);
  }

  public void setChannelStatCalculationStatus(boolean on) {
    reader.setChannelStatCalculationStatus(on);
  }

  public boolean getChannelStatCalculationStatus() {
    return reader.getChannelStatCalculationStatus();
  }

  public boolean isInterleaved(String id) throws FormatException, IOException {
    return reader.isInterleaved(id);
  }

  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return reader.openImage(id, no);
  }

  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    return reader.openBytes(id, no);
  }

  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    return reader.openBytes(id, no, buf);
  }

  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    return reader.openThumbImage(id, no);
  }

  public byte[] openThumbBytes(String id, int no)
    throws FormatException, IOException
  {
    return reader.openThumbBytes(id, no);
  }

  public void close() throws FormatException, IOException {
    reader.close();
  }

  public int getSeriesCount(String id) throws FormatException, IOException {
    return reader.getSeriesCount(id);
  }

  public void setSeries(String id, int no) throws FormatException, IOException {
    reader.setSeries(id, no);
  }

  public int getSeries(String id) throws FormatException, IOException {
    return reader.getSeries(id);
  }

  public void setColorTableIgnored(boolean ignore) {
    reader.setColorTableIgnored(ignore);
  }

  public boolean isColorTableIgnored() {
    return reader.isColorTableIgnored();
  }

  public void setNormalized(boolean normalize) {
    reader.setNormalized(normalize);
  }

  public boolean isNormalized() { return reader.isNormalized(); }

  public String[] getUsedFiles(String id) throws FormatException, IOException {
    return reader.getUsedFiles(id);
  }

  public String getCurrentFile() { return reader.getCurrentFile(); }

  public void swapDimensions(String id, String order)
    throws FormatException, IOException
  {
    reader.swapDimensions(id, order);
  }

  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    return reader.getIndex(id, z, c, t);
  }

  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return reader.getZCTCoords(id, index);
  }

  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    return reader.getMetadataValue(id, field);
  }

  public Hashtable getMetadata(String id) throws FormatException, IOException {
    return reader.getMetadata(id);
  }

  public void setMetadataFiltered(boolean filter) {
    reader.setMetadataFiltered(filter);
  }

  public boolean isMetadataFiltered() { return reader.isMetadataFiltered(); }

  public void setMetadataStore(MetadataStore store) {
    reader.setMetadataStore(store);
  }

  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    return reader.getMetadataStore(id);
  }

  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException
  {
    return reader.getMetadataStoreRoot(id);
  }

  public boolean testRead(String[] args) throws FormatException, IOException {
    return reader.testRead(args);
  }

  // -- IFormatHandler API methods --

  public boolean isThisType(String name) {
    return reader.isThisType(name);
  }

  public boolean isThisType(String name, boolean open) {
    return reader.isThisType(name, open);
  }

  public String getFormat() {
    return reader.getFormat();
  }

  public String[] getSuffixes() {
    return reader.getSuffixes();
  }

  public FileFilter[] getFileFilters() {
    return reader.getFileFilters();
  }

  public JFileChooser getFileChooser() {
    return reader.getFileChooser();
  }

}
