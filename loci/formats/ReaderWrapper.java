//
// ReaderWrapper.java
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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;

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

  public void setId(String id) throws FormatException, IOException {
    reader.setId(id);
  }

  public int getImageCount() throws FormatException, IOException {
    return reader.getImageCount();
  }

  public boolean isRGB() throws FormatException, IOException {
    return reader.isRGB();
  }

  public int getSizeX() throws FormatException, IOException {
    return reader.getSizeX();
  }

  public int getSizeY() throws FormatException, IOException {
    return reader.getSizeY();
  }

  public int getSizeZ() throws FormatException, IOException {
    return reader.getSizeZ();
  }

  public int getSizeC() throws FormatException, IOException {
    return reader.getSizeC();
  }

  public int getSizeT() throws FormatException, IOException {
    return reader.getSizeT();
  }

  public int getPixelType() throws FormatException, IOException {
    return reader.getPixelType();
  }

  public int getEffectiveSizeC() throws FormatException, IOException {
    return getImageCount() / (getSizeZ() * getSizeT());
  }

  public int getRGBChannelCount() throws FormatException, IOException {
    return getSizeC() / getEffectiveSizeC();
  }

  public int[] getChannelDimLengths() throws FormatException, IOException {
    return reader.getChannelDimLengths();
  }

  public String[] getChannelDimTypes() throws FormatException, IOException {
    return reader.getChannelDimTypes();
  }

  public int getThumbSizeX() throws FormatException, IOException {
    return reader.getThumbSizeX();
  }

  public int getThumbSizeY() throws FormatException, IOException {
    return reader.getThumbSizeY();
  }

  public boolean isLittleEndian() throws FormatException, IOException {
    return reader.isLittleEndian();
  }

  public String getDimensionOrder() throws FormatException, IOException {
    return reader.getDimensionOrder();
  }

  public boolean isOrderCertain() throws FormatException, IOException {
    return reader.isOrderCertain();
  }

  public boolean isInterleaved() throws FormatException, IOException {
    return reader.isInterleaved();
  }

  public boolean isInterleaved(int subC) throws FormatException, IOException {
    return reader.isInterleaved(subC);
  }

  public BufferedImage openImage(int no) throws FormatException, IOException {
    return reader.openImage(no);
  }

  public byte[] openBytes(int no) throws FormatException, IOException {
    return reader.openBytes(no);
  }

  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return reader.openBytes(no, buf);
  }

  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    return reader.openThumbImage(no);
  }

  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    return reader.openThumbBytes(no);
  }

  public void close(boolean fileOnly) throws FormatException, IOException {
    reader.close(fileOnly);
  }

  public void close() throws FormatException, IOException {
    reader.close();
  }

  public int getSeriesCount() throws FormatException, IOException {
    return reader.getSeriesCount();
  }

  public void setSeries(int no) throws FormatException, IOException {
    reader.setSeries(no);
  }

  public int getSeries() throws FormatException, IOException {
    return reader.getSeries();
  }

  public void setNormalized(boolean normalize) {
    reader.setNormalized(normalize);
  }

  public boolean isNormalized() { return reader.isNormalized(); }

  public void setMetadataCollected(boolean collect) {
    reader.setMetadataCollected(collect);
  }

  public boolean isMetadataCollected() { return reader.isMetadataCollected(); }

  public String[] getUsedFiles() throws FormatException, IOException {
    return reader.getUsedFiles();
  }

  public String getCurrentFile() { return reader.getCurrentFile(); }

  public int getIndex(int z, int c, int t) throws FormatException, IOException {
    return reader.getIndex(z, c, t);
  }

  public int[] getZCTCoords(int index) throws FormatException, IOException {
    return reader.getZCTCoords(index);
  }

  public Object getMetadataValue(String field)
    throws FormatException, IOException
  {
    return reader.getMetadataValue(field);
  }

  public Hashtable getMetadata() throws FormatException, IOException {
    return reader.getMetadata();
  }

  public CoreMetadata getCoreMetadata() throws FormatException, IOException {
    return reader.getCoreMetadata();
  }

  public void setMetadataFiltered(boolean filter) {
    reader.setMetadataFiltered(filter);
  }

  public boolean isMetadataFiltered() { return reader.isMetadataFiltered(); }

  public void setMetadataStore(MetadataStore store) {
    reader.setMetadataStore(store);
  }

  public MetadataStore getMetadataStore() throws FormatException, IOException {
    return reader.getMetadataStore();
  }

  public Object getMetadataStoreRoot() throws FormatException, IOException {
    return reader.getMetadataStoreRoot();
  }

  public boolean testRead(String[] args) throws FormatException, IOException {
    return reader.testRead(args);
  }

  // -- Deprecated IFormatReader API methods --

  public int getImageCount(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getImageCount();
  }

  public boolean isRGB(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.isRGB();
  }

  public int getSizeX(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getSizeX();
  }

  public int getSizeY(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getSizeY();
  }

  public int getSizeZ(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getSizeZ();
  }

  public int getSizeC(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getSizeC();
  }

  public int getSizeT(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getSizeT();
  }

  public int getPixelType(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getPixelType();
  }

  public int getEffectiveSizeC(String id) throws FormatException, IOException {
    reader.setId(id); 
    return getImageCount() / (getSizeZ() * getSizeT());
  }

  public int getRGBChannelCount(String id) throws FormatException, IOException {
    reader.setId(id); 
    return getSizeC() / getEffectiveSizeC();
  }

  public int[] getChannelDimLengths(String id)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.getChannelDimLengths();
  }

  public String[] getChannelDimTypes(String id)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.getChannelDimTypes();
  }

  public int getThumbSizeX(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getThumbSizeX();
  }

  public int getThumbSizeY(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getThumbSizeY();
  }

  public boolean isLittleEndian(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.isLittleEndian();
  }

  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.getDimensionOrder();
  }

  public boolean isOrderCertain(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.isOrderCertain();
  }

  public boolean isInterleaved(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.isInterleaved();
  }

  public boolean isInterleaved(String id, int subC)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.isInterleaved(subC);
  }

  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.openImage(no);
  }

  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.openBytes(no);
  }

  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.openBytes(no, buf);
  }

  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.openThumbImage(no);
  }

  public byte[] openThumbBytes(String id, int no)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.openThumbBytes(no);
  }

  public int getSeriesCount(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getSeriesCount();
  }

  public void setSeries(String id, int no) throws FormatException, IOException {
    reader.setId(id); 
    reader.setSeries(no);
  }

  public int getSeries(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getSeries();
  }

  public String[] getUsedFiles(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getUsedFiles();
  }

  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.getIndex(z, c, t);
  }

  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.getZCTCoords(index);
  }

  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.getMetadataValue(field);
  }

  public Hashtable getMetadata(String id) throws FormatException, IOException {
    reader.setId(id); 
    return reader.getMetadata();
  }

  public CoreMetadata getCoreMetadata(String id)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.getCoreMetadata();
  }

  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.getMetadataStore();
  }

  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException
  {
    reader.setId(id); 
    return reader.getMetadataStoreRoot();
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

  // -- StatusReporter API methods --

  public void addStatusListener(StatusListener l) {
    reader.addStatusListener(l);
  }

  public void removeStatusListener(StatusListener l) {
    reader.removeStatusListener(l);
  }

  public StatusListener[] getStatusListeners() {
    return reader.getStatusListeners();
  }

}
