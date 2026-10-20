/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2020 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.internal;

import java.util.ArrayList;
import java.util.List;

import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.NonNegativeInteger;

/**
 * Represents a well in a plate.
 */
public abstract class WellContainer implements Comparable<WellContainer> {

  private int plateAcquisitionIndex;
  private int rowIndex;
  private int columnIndex;
  private transient int wellIndex;
  private int fieldCount;

  private List<String> files = new ArrayList<String>();

  public WellContainer() {
  }

  public WellContainer(int pa, int row, int col, int fields) {
    plateAcquisitionIndex = pa;
    rowIndex = row;
    columnIndex = col;
    fieldCount = fields;
  }

  public String getFile(int fieldIndex, int planeIndex) {
    String[] fieldFiles = getFiles(fieldIndex);
    if (fieldFiles != null && planeIndex < fieldFiles.length) {
      return fieldFiles[planeIndex];
    }
    return null;
  }

  public abstract String[] getFiles(int fieldIndex);

  public List<String> getAllFiles() {
    return files;
  }

  public void addFile(String file) {
    files.add(file);
  }

  public int getPlateAcquisition() {
    return plateAcquisitionIndex;
  }

  public void setPlateAcquisition(int pa) {
    plateAcquisitionIndex = pa;
  }

  public int getRowIndex() {
    return rowIndex;
  }

  public void setRowIndex(int newRow) {
    rowIndex = newRow;
  }

  public int getColumnIndex() {
    return columnIndex;
  }

  public void setColumnIndex(int newColumn) {
    columnIndex = newColumn;
  }

  public int getFieldCount() {
    return fieldCount;
  }

  public void setFieldCount(int fields) {
    fieldCount = fields;
  }

  public int getWellIndex() {
    return wellIndex;
  }

  public void setWellIndex(int index) {
    wellIndex = index;
  }

  public void fillMetadataStore(MetadataStore store, int plate, int plateAcq, int wellSampleStart, int nextImage) {
    fillMetadataStore(store, plate, plateAcq, wellIndex, wellSampleStart, nextImage);
  }

  public void fillMetadataStore(MetadataStore store, int plate, int plateAcq, int well, int wellSampleStart, int nextImage) {
    store.setWellID(MetadataTools.createLSID("Well", plate, well), plate, well);
    store.setWellRow(new NonNegativeInteger(getRowIndex()), plate, well);
    store.setWellColumn(new NonNegativeInteger(getColumnIndex()), plate, well);

    for (int field=0; field<getFieldCount(); field++) {
      int wellSampleIndex = field + wellSampleStart;
      String wellSample = MetadataTools.createLSID("WellSample", plate, well, wellSampleIndex);
      store.setWellSampleID(wellSample, plate, well, wellSampleIndex);

      int imageIndex = nextImage + field;
      store.setWellSampleIndex(new NonNegativeInteger(imageIndex), plate, well, wellSampleIndex);
      String imageID = MetadataTools.createLSID("Image", imageIndex);
      store.setImageID(imageID, imageIndex);
      store.setWellSampleImageRef(imageID, plate, well, wellSampleIndex);
      store.setPlateAcquisitionWellSampleRef(wellSample, plate, plateAcq, imageIndex);
    }
  }

  @Override
  public int compareTo(WellContainer w) {
    int paDiff = getPlateAcquisition() - w.getPlateAcquisition();
    if (paDiff != 0) {
      return paDiff;
    }
    int rowDiff = getRowIndex() - w.getRowIndex();
    if (rowDiff != 0) {
      return rowDiff;
    }
    return getColumnIndex() - w.getColumnIndex();
  }

}
