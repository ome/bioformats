/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package ome.jxr.datastream;

import java.io.IOException;

import ome.jxr.JXRException;
import ome.jxr.constants.Image;
import ome.jxr.image.JXRImagePlane;
import ome.jxr.metadata.IFDMetadata;
import ome.scifio.io.BitBuffer;
import ome.scifio.io.RandomAccessInputStream;

/**
 * Decodes the image data from a JPEG XR image file. The data has to be in the
 * form of an input stream and accompanying metadata has to be present to aid
 * the decompression process.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/datastream/JXRDecoder.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/datastream/JXRDecoder.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public class JXRDecoder {

  private RandomAccessInputStream stream;

  private IFDMetadata metadata;

  public JXRDecoder(RandomAccessInputStream stream, IFDMetadata metadata)
      throws IOException {
    if (stream == null || metadata == null) {
      throw new IllegalArgumentException("Input stream or metadata has not "
          + "been set.");
    }
    this.stream = stream;
    this.metadata = metadata;
  }

  public byte[] decode()
      throws IOException, JXRException {
    parseImageLayer();

    return null;
  }

  private void parseImageLayer() throws IOException, JXRException {
    stream.seek(metadata.getImageOffset());
    checkIfGDISignaturePresent();
    JXRImagePlane primaryImagePlane = extractPrimaryImagePlane();
  }

  private JXRImagePlane extractPrimaryImagePlane() throws IOException {
    byte[] bytes = new byte[4];

    stream.readFully(bytes);
    BitBuffer bits = new BitBuffer(bytes);

    int reservedB = bits.getBits(4);
    int hardTilingFlag = bits.getBits(1);
    int reservedC = bits.getBits(3);
    int tilingFlag = bits.getBits(1);
    int frequencyModeCodestreamFlag = bits.getBits(1);
    int spatialXfrmSubordinate = bits.getBits(3);
    int indexTablePresentFlag = bits.getBits(1);
    int overlapMode = bits.getBits(2);
    int shortHeaderFlag = bits.getBits(1);
    int longWordFlag = bits.getBits(1);
    int windowingFlag = bits.getBits(1);
    int trimFlexbitsFlag = bits.getBits(1);
    int reservedD = bits.getBits(1);
    int redBlueNotSwappedFlag = bits.getBits(1);
    int premultipliedAlphaFlag = bits.getBits(1);
    int alphaImagePlaneFlag = bits.getBits(1);
    int outputClrFmt = bits.getBits(4);
    int outputBitdepth = bits.getBits(4);

    return new JXRImagePlane();
  }

  private void checkIfGDISignaturePresent() throws IOException, JXRException {
    String signature = stream.readString(Image.GDI_SIGNATURE.length());
    if (!Image.GDI_SIGNATURE.equals(signature)) {
      throw new JXRException("Missing required image signature.");
    }
    stream.skipBytes(1);
  }

  public void close() throws IOException {
    stream.close();
  }
}
