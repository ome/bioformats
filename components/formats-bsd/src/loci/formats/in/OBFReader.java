/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2014 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 *   - Max Planck Institute for Biophysical Chemistry, Goettingen
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

package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.DataFormatException;

import javax.xml.parsers.ParserConfigurationException;

import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

import ome.units.quantity.Length;
import ome.units.UNITS;
import ome.xml.model.primitives.PositiveInteger;

import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 * OBFReader is the file format reader for Imspector OBF files.
 *
 * @see <a href="https://imspectordocs.readthedocs.io/en/latest/fileformat.html#the-obf-file-format">The OBF File Format</a>
 * @author Bjoern Thiel bjoern.thiel at mpibpc.mpg.de
 */

public class OBFReader extends FormatReader {
  private static final boolean LITTLE_ENDIAN = true;

  private static final String FILE_MAGIC_STRING = "OMAS_BF\n";
  private static final String STACK_MAGIC_STRING = "OMAS_BF_STACK\n";
  private static final short MAGIC_NUMBER = (short) 0xFFFF;

  private static final int MAXIMAL_NUMBER_OF_DIMENSIONS = 15;

  private int file_version = - 1;

  private transient OMEXMLMetadata ome_meta_data;

  private class Stack {
    long position;
    long length;
    boolean compression;

    long samplesWritten = -1;
    int bytesPerSample = 0;
    List<Long> flushPoints;
    long flushBlockSize = 0;

    List<Long> chunkLogicalPositions;
    List<Long> chunkFilePositions;
  }

  private class State {
    State(int series) {
      this.series = series;
    }

    Inflater inflater;

    byte[] inflateInputBuffer;
    byte[] wholeStackBuffer;

    int series = -1;
    long nextReadPosition = -1;

    int currentChunk = -1;
    long chunkLogicalStart = 0;
    long chunkFileStart = 0;
    long chunkSize = 0;
  };

  private State state;

  private byte[] skipBuffer;

  private List<Stack> stacks = new ArrayList<Stack>();

  public OBFReader() {
    super("OBF", new String[] {"obf", "msr"});
    suffixNecessary = false;
    suffixSufficient = false;
    datasetDescription = "OBF file";
  }

  private int getFileVersion(RandomAccessInputStream stream) throws IOException {
    stream.seek(0);

    stream.order(LITTLE_ENDIAN);

    try {
      final String magicString = stream.readString(FILE_MAGIC_STRING.length());
      final short magicNumber = stream.readShort();
      final int version = stream.readInt();

      if (magicString.equals(FILE_MAGIC_STRING) && magicNumber == MAGIC_NUMBER) {
        return version;
      }
    }
    catch(IOException exception) { }

    return - 1;
  }

  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return getFileVersion(stream) >= 0;
  }

  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessInputStream(id);

    file_version = getFileVersion(in);
    addGlobalMeta("Version", file_version);
    long stackPosition = in.readLong();
    final int lengthOfDescription = in.readInt();
    final String description = in.readString(lengthOfDescription);
    metadata.put("Description", description);

    if (file_version >= 2) {
      final long meta_data_position = in.readLong();
      final long current_position = in.getFilePointer();

      in.seek(meta_data_position);

      for (String key = readString(); key.length() > 0; key = readString()) {
        if (key.equals("ome_xml")) {
          final String ome_xml = readString();
          LOGGER.trace("OME-xml = {}", ome_xml);

          try {
            ServiceFactory factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);

            if (service.validateOMEXML(ome_xml)) {
              ome_meta_data = service.createOMEXMLMetadata(ome_xml);

              for (int image = 0; image != ome_meta_data.getImageCount(); ++ image) {
                if (ome_meta_data.getPixelsBigEndian(image) == null) {
                  ome_meta_data.setPixelsBigEndian(Boolean.FALSE, image);
                }
                int channels = ome_meta_data.getChannelCount(image);
                for (int channel = 0; channel != channels; ++ channel) {
                  if (ome_meta_data.getChannelSamplesPerPixel(image, channel) == null) {
                    ome_meta_data.setChannelSamplesPerPixel(new PositiveInteger(1), image, channel);
                  }
                }
              }

              service.convertMetadata(ome_meta_data, metadataStore);

              OMEXMLMetadata reference
                  = service.getOMEMetadata(service.asRetrieve(metadataStore));
              for (int image = 0; image != reference.getImageCount(); ++ image) {
                service.addMetadataOnly(reference, image);
              }
            }
          }
          catch (DependencyException exception) {
            throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, exception);
          }
          catch (ServiceException exception) {
            throw new FormatException(exception);
          }
          catch (Exception e) {
            LOGGER.warn("Could not parse OME-XML metadata", e);
          }

          break;
        }
        else {
          addGlobalMeta(key, readString());
        }
      }

      in.seek(current_position);
    }

    if (stackPosition != 0) {
      core.clear();
      do {
        stackPosition = initStack(stackPosition);
      }
      while (stackPosition != 0);
    }

    if (ome_meta_data == null) {
      MetadataTools.populatePixels(metadataStore, this);

      for (int image = 0; image != core.size(); ++ image) {
        CoreMetadata meta_data = core.get(image);

        final String name = meta_data.seriesMetadata.get("Name").toString();
        metadataStore.setImageName(name.trim(), image);

        @SuppressWarnings("unchecked")
        final List<Double> lengths = (List<Double>) meta_data.seriesMetadata.get("Lengths");

        if (lengths.size() > 0) {
          double lengthX = Math.abs(lengths.get(0));
          if (lengthX < 0.01) {
            lengthX *= 1000000;
          }
          if (lengthX > 0) {
            Length physicalSizeX = FormatTools.getPhysicalSizeX(lengthX / meta_data.sizeX, UNITS.MICROMETER);
            if (physicalSizeX != null) {
              metadataStore.setPixelsPhysicalSizeX(physicalSizeX, image);
            }
          }
        }
        if (lengths.size() > 1) {
          double lengthY = Math.abs(lengths.get(1));
          if (lengthY < 0.01) {
            lengthY *= 1000000;
          }
          if (lengthY > 0) {
            Length physicalSizeY = FormatTools.getPhysicalSizeY(lengthY / meta_data.sizeY, UNITS.MICROMETER);
            if (physicalSizeY != null) {
              metadataStore.setPixelsPhysicalSizeY(physicalSizeY, image);
            }
          }
        }
        if (lengths.size() > 2) {
          double lengthZ = Math.abs(lengths.get(2));
          if (lengthZ < 0.01) {
            lengthZ *= 1000000;
          }
          if (lengthZ > 0) {
            Length physicalSizeZ = FormatTools.getPhysicalSizeZ(lengthZ / meta_data.sizeZ, UNITS.MICROMETER);
            if (physicalSizeZ != null) {
              metadataStore.setPixelsPhysicalSizeZ(physicalSizeZ, image);
            }
          }
        }
      }
    }
  }

  private long initStack(long current) throws FormatException, IOException {
    in.seek(current);

    final String magicString = in.readString(STACK_MAGIC_STRING.length());
    final short magicNumber = in.readShort();
    final int stackVersion = in.readInt();
    addGlobalMeta("Stack version", stackVersion);

    if (magicString.equals(STACK_MAGIC_STRING) && magicNumber == MAGIC_NUMBER) {
      final int image = core.size();

      CoreMetadata meta_data = new CoreMetadata();
      core.add(meta_data);

      meta_data.littleEndian = LITTLE_ENDIAN;

      meta_data.thumbnail = false;

      final int numberOfDimensions = in.readInt();
      if (numberOfDimensions > 5) {
        throw new FormatException("Unsupported number of " + numberOfDimensions + " dimensions");
      }

      Stack stack = new Stack();
      stack.samplesWritten = 1;

      int[] sizes = new int[MAXIMAL_NUMBER_OF_DIMENSIONS];
      for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension) {
        final int size = in.readInt();

        if(dimension < numberOfDimensions) {
          stack.samplesWritten *= size;
        }

        sizes[dimension] = dimension < numberOfDimensions ? size : 1;
      }

      if (ome_meta_data != null) {
        meta_data.sizeX = ome_meta_data.getPixelsSizeX(image).getValue();
        meta_data.sizeY = ome_meta_data.getPixelsSizeY(image).getValue();
        meta_data.sizeZ = ome_meta_data.getPixelsSizeZ(image).getValue();
        meta_data.sizeC = ome_meta_data.getPixelsSizeC(image).getValue();
        meta_data.sizeT = ome_meta_data.getPixelsSizeT(image).getValue();
      }
      else {
        meta_data.sizeX = sizes[0];
        meta_data.sizeY = sizes[1];
        meta_data.sizeZ = sizes[2];
        meta_data.sizeC = sizes[3];
        meta_data.sizeT = sizes[4];
      }

      meta_data.imageCount = meta_data.sizeZ * meta_data.sizeC * meta_data.sizeT;

      if (ome_meta_data != null) {
        meta_data.dimensionOrder = ome_meta_data.getPixelsDimensionOrder(image).toString();
        meta_data.orderCertain = true;
      }
      else {
        meta_data.dimensionOrder = "XYZCT";
        meta_data.orderCertain = false;
      }

      List<Double> lengths = new ArrayList<Double>();
      for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension) {
        final double length = in.readDouble();
        if (dimension < numberOfDimensions) {
          lengths.add(new Double(length));
        }
      }
      meta_data.seriesMetadata.put("Lengths", lengths);

      List<Double> offsets = new ArrayList<Double>();
      for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension) {
        final double offset = in.readDouble();
        if (dimension < numberOfDimensions) {
          offsets.add(new Double(offset));
        }
      }
      meta_data.seriesMetadata.put("Offsets", offsets);

      final int type = in.readInt();
      meta_data.pixelType = getPixelType(type);
      meta_data.bitsPerPixel = getBitsPerPixel(type);

      stack.bytesPerSample = meta_data.bitsPerPixel / 8;

      meta_data.indexed = false;
      meta_data.rgb = false;
      meta_data.interleaved = false;

      final int compression = in.readInt();
      stack.compression = getCompression(compression);

      in.skipBytes(4);

      final int lengthOfName = in.readInt();
      final int lengthOfDescription = in.readInt();

      in.skipBytes(8);

      final long lengthOfData = in.readLong();
      stack.length = getLength(lengthOfData);

      final long next = in.readLong();

      final String name = in.readString(lengthOfName);
      meta_data.seriesMetadata.put("Name", name);
      String description = in.readString(lengthOfDescription);

      if (description != null && lengthOfDescription > 0) {
        description = XMLTools.sanitizeXML(description);

        // some XML node names may contain white space, which prevents parsing
        description = description.replaceAll("<Time Lapse ", "<TimeLapse ");
        description = description.replaceAll("</Time Lapse", "</TimeLapse");

        boolean xml = false;
        try {
          Element root = XMLTools.parseDOM(description).getDocumentElement();

          root = getChildNodes(root).get(0);

          ArrayList<Element> children = getChildNodes(root);
          for (Element child : children) {
            String nodeName = child.getNodeName();

            ArrayList<Element> grandchildren = getChildNodes(child);

            for (Element grandchild : grandchildren) {
              String key = grandchild.getNodeName();
              String value = grandchild.getTextContent().trim();
              if (!key.equals("doc") && !key.equals("hwr")) {
                addSeriesMeta(nodeName + " " + key, value);
              }
              else {
                ArrayList<Element> docs = getChildNodes(grandchild);

                for (Element doc : docs) {
                  key = doc.getNodeName();
                  value = doc.getTextContent().trim();
                  addSeriesMeta(nodeName + " " + key, value);
                }
              }
            }
          }
          xml = true;
        }
        catch (ParserConfigurationException e) {
          LOGGER.warn("Could not parse description as XML", e);
        }
        catch (SAXException e) {
          LOGGER.warn("Could not parse description as XML", e);
        }

        if (!xml) {
          meta_data.seriesMetadata.put("Description", description);
        }
      }

      stack.position = in.getFilePointer();

      if (stackVersion >= 1) {
        in.skip(lengthOfData);

        final long footer = in.getFilePointer();
        final int offset = in.readInt();

        List<Boolean> stepsPresent = new ArrayList<Boolean>();
        for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension) {
          final int present = in.readInt();
          if (dimension < numberOfDimensions) {
            stepsPresent.add(new Boolean(present != 0));
          }
        }
        List<Boolean> stepLabelsPresent = new ArrayList<Boolean>();
        for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension) {
          final int present = in.readInt();
          if (dimension < numberOfDimensions) {
            stepLabelsPresent.add(new Boolean(present != 0));
          }
        }

        int obsoleteMetadataLength = 0;
        long numFlushPoints = 0;

        if (stackVersion >=  3) {
          final int SI_UNIT_SIZE = 80;

          obsoleteMetadataLength = in.readInt();
          in.skipBytes(SI_UNIT_SIZE * (MAXIMAL_NUMBER_OF_DIMENSIONS + 1));
          numFlushPoints = in.readLong();
          stack.flushBlockSize = in.readLong();
        }

        long tagDictionaryLength = 0;
        long stackEndDisk = 0;
        int minFormatVersion = 0;

        if (stackVersion >= 4) {
          tagDictionaryLength = in.readLong();
          stackEndDisk = in.readLong();
          minFormatVersion = in.readInt();
        }

        long stackEndUsedDisk = 0;
        long numChunkPositions = 0;

        if (stackVersion >= 6) {
          stackEndUsedDisk = in.readLong();
          stack.samplesWritten = in.readLong();
          numChunkPositions = in.readLong();
        }

        in.seek(footer + offset);

        List<String> labels = new ArrayList<String>();
        for (int dimension = 0; dimension != numberOfDimensions; ++ dimension) {
          final int length = in.readInt();
          final String label = in.readString(length);
          labels.add(label);

          if ((label.endsWith("X") && meta_data.sizeX == 0) || (dimension == 1 && isFLIMLabel(labels.get(0)))) {
            meta_data.sizeX = sizes[dimension];
          }
          else if ((label.endsWith("Y") && meta_data.sizeY == 0) || (dimension == 2 && isFLIMLabel(labels.get(0)))) {
            meta_data.sizeY = sizes[dimension];
          }
          else if (isFLIMLabel(label)) {
            meta_data.sizeZ = sizes[dimension];
            meta_data.moduloZ.type = FormatTools.LIFETIME;
            meta_data.moduloZ.typeDescription = label;
            meta_data.moduloZ.start = 0;
            meta_data.moduloZ.step = 1;
            meta_data.moduloZ.end = meta_data.sizeZ - 1;
            meta_data.imageCount = meta_data.sizeZ * meta_data.sizeC * meta_data.sizeT;
          }
        }
        meta_data.seriesMetadata.put("Labels", labels);

        List<List<Double>> steps = new ArrayList<List<Double>>();
        for (int dimension = 0; dimension != numberOfDimensions; ++ dimension) {
          List<Double> list = new ArrayList<Double>();
          if (stepsPresent.get(dimension)) {
            for (int position = 0; position != sizes[dimension]; ++ position) {
              final double step = in.readDouble();
              list.add(new Double(step));
            }
          }
          steps.add(list);
        }
        meta_data.seriesMetadata.put("Steps", steps);

        List<List<String>> stepLabels = new ArrayList<List<String>>();
        for (int dimension = 0; dimension != numberOfDimensions; ++ dimension) {
          List<String> list = new ArrayList<String>();
          if (stepLabelsPresent.get(dimension)) {
            for (int position = 0; position != sizes[dimension]; ++ position) {
              final int length = in.readInt();
              final String label = in.readString(length);
              list.add(label);
            }
          }
          stepLabels.add(list);
        }
        meta_data.seriesMetadata.put("StepLabels", stepLabels);

        in.skipBytes(obsoleteMetadataLength);

        if (numFlushPoints > 0) {
            if (numFlushPoints > Integer.MAX_VALUE ) {
              throw new FormatException("The implementation can not currently " +
                "handle more than Integer.MAX_VALUE flush points");
            }

            List<Long> flushPoints = new ArrayList<>((int)numFlushPoints);
            for (int i = 0; i < numFlushPoints; ++i) {
                flushPoints.add(in.readLong());
            }
            stack.flushPoints = flushPoints;
        }

        in.skipBytes(tagDictionaryLength);

        if (numChunkPositions > 0) {
          if (numChunkPositions > Integer.MAX_VALUE ) {
              throw new FormatException("The implementation can not currently " +
                "handle more than Integer.MAX_VALUE chunk positions");
            }

          List<Long> logicalPositions = new ArrayList<>((int)numChunkPositions + 1);
          List<Long> filePositions = new ArrayList<>((int)numChunkPositions + 1);

          logicalPositions.add(Long.valueOf(0));
          filePositions.add(Long.valueOf(0));

          for (int i = 0; i < numChunkPositions; ++i) {
              logicalPositions.add(in.readLong());
              filePositions.add(in.readLong());
          }

          stack.chunkLogicalPositions = logicalPositions;
          stack.chunkFilePositions = filePositions;
        }
      }

      stacks.add(stack);

      return next;
    }
    else {
      throw new FormatException("Unsupported stack format");
    }
  }

  private boolean isFLIMLabel(String label) {
    return label.startsWith("SPCM");
  }

  private int getPixelType(int type) throws FormatException {
    switch (type) {
      case 0x01: return FormatTools.UINT8;
      case 0x02: return FormatTools.INT8;
      case 0x04: return FormatTools.UINT16;
      case 0x08: return FormatTools.INT16;
      case 0x10: return FormatTools.UINT32;
      case 0x20: return FormatTools.INT32;
      case 0x40: return FormatTools.FLOAT;
      case 0x80: return FormatTools.DOUBLE;
      default: throw new FormatException("Unsupported data type " + type);
    }
  }

  private int getBitsPerPixel(int type) throws FormatException {
    switch (type) {
      case 0x01:
      case 0x02: return 8;
      case 0x04:
      case 0x08: return 16;
      case 0x10:
      case 0x20: return 32;
      case 0x40: return 32;
      case 0x80: return 64;
      default: throw new FormatException("Unsupported data type " + type);
    }
  }

  private long getLength(long length) throws FormatException {
    if (length >= 0) {
      return length;
    }
    else {
      throw new FormatException("Negative stack length on disk");
    }
  }

  private boolean getCompression(int compression) throws FormatException {
    switch (compression) {
      case 0: return false;
      case 1: return true;
      default: throw new FormatException("Unsupported compression " + compression);
    }
  }

  private String readString() throws IOException {
    final int length = in.readInt();
    if (length > 0) {
      return in.readString(length);
    }
    else {
      return "";
    }
  }

  private long remainingBytesInChunk(Stack stack)
    throws IOException, FormatException
  {
    long result = state.chunkFileStart +
        state.chunkSize -
        in.getFilePointer();

    if (result < 0) {
      throw new FormatException("Negative remaining bytes in chunk; malformed file?");
    }

    return result;
  }

  private void readFromStackRaw(Stack stack, byte[] buffer, int bufferOffset, int bytes)
    throws IOException, FormatException
  {
    long remainingBytesInChunk = remainingBytesInChunk(stack);

    while (bytes > 0) {
      while (remainingBytesInChunk == 0) {
        switchChunk(stack, state.currentChunk + 1);
        in.seek(state.chunkFileStart);
        remainingBytesInChunk = remainingBytesInChunk(stack);
      }

      int bytesToRead = (int)Math.min(bytes, remainingBytesInChunk);

      if(buffer != null) {
        in.read(
          buffer,
          bufferOffset,
          bytesToRead
        );
      } else {
        in.skipBytes(bytesToRead);
      }

      bufferOffset += bytesToRead;
      remainingBytesInChunk -= bytesToRead;
      bytes -= bytesToRead;
    }
  }

  private void readFromStack(Stack stack, byte[] buffer, int bufferOffset, int bytes)
    throws IOException, FormatException
  {
    int remainingBytes = bytes;

    final long stackByteCount = stack.samplesWritten * stack.bytesPerSample;

    if (!stack.compression) {
      readFromStackRaw(stack, buffer, bufferOffset, bytes);

    } else if (stack.compression) {
      Inflater inflater = state.inflater;
      byte[] input = state.inflateInputBuffer;

      while (remainingBytes > 0) {
        if (inflater.needsInput()) {
          final long logicalOffset = state.chunkLogicalStart +
            (in.getFilePointer() - state.chunkFileStart);

          final long remainder = stackByteCount - logicalOffset;

          if (remainder > 0) {
            final int length = remainder > input.length ? input.length : (int) remainder;
            readFromStackRaw(stack, input, 0, length);
            inflater.setInput(input, 0, length);
          } else {
            throw new FormatException("Corrupted zlib compression");
          }
        } else if (inflater.needsDictionary()) {
          throw new FormatException("Unsupported zlib compression");
        }

        long bytesWrittenBefore = inflater.getBytesWritten();

        try {
          int decompressedBytes = inflater.inflate(buffer, bufferOffset, remainingBytes);
          bufferOffset += decompressedBytes;
          remainingBytes -= decompressedBytes;
        }
        catch (DataFormatException exception) {
          /* For older OBF files a zlib error may occur after all bytes have been decompressed.
           In this scenario we will attempt to ignore the error if no bytes remain and output a warning.
           This will only work for Java versions 11 and upwards, prior versions will still throw a FormatException.
           See https://github.com/openjdk/jdk/commit/883d41fefc2b5da40b159b24e7387f3bdbd22a5a for more details */

          long bytesWrittenAfter = inflater.getBytesWritten();

          long decompressedBytes = bytesWrittenAfter - bytesWrittenBefore;

          bufferOffset += decompressedBytes;
          remainingBytes -= decompressedBytes;

          if(remainingBytes != 0) {
            throw new FormatException(
                "Error while deflating stream, upgrading to Java 11 or later may help", exception);
          } else {
            LOGGER.warn("Error past the end of deflated stream", exception);
          }
        }
      }
    }

    state.nextReadPosition += bytes;
  }

  private void switchChunk(Stack stack, int chunkIndex)
    throws FormatException
  {
    if (chunkIndex >= stack.chunkLogicalPositions.size()) {
      throw new FormatException("Missing OBF data chunks");
    }

    state.currentChunk = chunkIndex;
    state.chunkLogicalStart = stack.chunkLogicalPositions.get(chunkIndex);

    state.chunkFileStart =
      stack.chunkFilePositions.get(chunkIndex) + stack.position;

    if (chunkIndex + 1 == stack.chunkLogicalPositions.size()) {
      state.chunkSize = (stack.samplesWritten * stack.bytesPerSample) -
        state.chunkLogicalStart;
    } else {
      state.chunkSize = stack.chunkLogicalPositions.get(chunkIndex + 1) -
        state.chunkLogicalStart;
    }
  }

  private void seekToFrameStart(Stack stack, long sampleOffset)
    throws IOException, FormatException
  {
    boolean hasChunks = stack.chunkLogicalPositions != null;
    long stackByteOffset = sampleOffset * stack.bytesPerSample;

    if (state.nextReadPosition == stackByteOffset) {
      return;
    }

    state.nextReadPosition = stackByteOffset;

    if (!hasChunks) {
      state.chunkLogicalStart = 0;
      state.chunkFileStart = stack.position;
      state.chunkSize = stack.samplesWritten * stack.bytesPerSample;

      if (!stack.compression) {
        in.seek(stack.position + stackByteOffset);
        return;
      }
    }

    long seekDestination = 0;
    long extraSkipBytes = stackByteOffset;

    if (stack.flushPoints != null && stack.flushBlockSize != 0) {
      int flushBlockIndex = (int)(stackByteOffset / stack.flushBlockSize);

      if (flushBlockIndex > 0) {
        seekDestination = stack.flushPoints.get(flushBlockIndex - 1);
        extraSkipBytes -= flushBlockIndex * stack.flushBlockSize;
      }
    }

    if (stack.compression) {
      state.inflater = new Inflater(seekDestination != 0);

      if (state.inflateInputBuffer == null) {
        state.inflateInputBuffer = new byte[8192];
      }
    }

    if (!hasChunks) {
      in.seek(stack.position + seekDestination);
      skipBytes(stack, extraSkipBytes);
      return;
    }

    switchChunk(stack, Collections.binarySearch(
      stack.chunkLogicalPositions, seekDestination));

    in.seek(state.chunkFileStart +
      seekDestination - state.chunkLogicalStart);

    skipBytes(stack, extraSkipBytes);
  }

  private void skipBytes(Stack stack, long byteCount)
    throws IOException, FormatException
  {
    boolean hasChunks = stack.chunkLogicalPositions != null;

    if (!stack.compression && !hasChunks) {
      in.skipBytes(byteCount);
      state.nextReadPosition += byteCount;
    } else if (stack.compression) {
      if (skipBuffer == null) {
        skipBuffer = new byte[8192];
      }

      while (byteCount > 0) {
        int readSize = (int)Math.min(skipBuffer.length, byteCount);
        readFromStack(stack, skipBuffer, 0, readSize);
        byteCount -= readSize;
      }
    } else {
      while(byteCount > 0) {
        int skipSize = (int)Math.min(byteCount, Integer.MAX_VALUE);
        readFromStackRaw(stack, null, 0, skipSize);
        byteCount -= skipSize;
      }
    }
  }

  private void readStackFrame(Stack stack, long sampleOffset, byte[] buffer, int bufferOffset, int w, int h)
    throws IOException, FormatException
  {
    final long columns = getSizeX();
    final long rows = getSizeY();

    long frameSamplesTotal = columns * rows;

    long frameBytesWritten =
      Math.max(
        Math.min(
          stack.samplesWritten - sampleOffset,
          frameSamplesTotal
        ) * stack.bytesPerSample,
        0
     );

    if (frameBytesWritten > 0) {
      seekToFrameStart(stack, sampleOffset);
    }

    long rowSkipBytes = (columns - w) * stack.bytesPerSample;

    int currentBufferOffset = bufferOffset;

    for (int y = 0; y < h; ++y) {
      if (y != 0 && rowSkipBytes > 0) {
        long writtenSkipBytes = Math.min(rowSkipBytes, frameBytesWritten);
        skipBytes(stack, writtenSkipBytes);
        frameBytesWritten -= writtenSkipBytes;
      }

      int totalRowBytes = w * stack.bytesPerSample;

      int writtenRowBytes = (int)Math.min(totalRowBytes, frameBytesWritten);

      int unwrittenRowBytes = Math.max(w - writtenRowBytes, 0);

      if(writtenRowBytes > 0) {
        readFromStack(
          stack,
          buffer,
          currentBufferOffset,
          writtenRowBytes
        );

        currentBufferOffset += writtenRowBytes;
        frameBytesWritten -= writtenRowBytes;
      }

      if(unwrittenRowBytes > 0) {
        Arrays.fill(
          buffer,
          currentBufferOffset,
          currentBufferOffset + unwrittenRowBytes,
          (byte)0
        );
      }
    }
  }

  private void readFlimFrame(Stack stack, int no, byte[] buffer, int x, int y, int w, int h)
    throws IOException, FormatException
  {
    if (state.wholeStackBuffer == null) {
      int wholeStackSize =
        getSizeX() * getSizeY() * getSizeZ() * stack.bytesPerSample;

      state.wholeStackBuffer = new byte[wholeStackSize];

      seekToFrameStart(stack, 0);

      readFromStack(
        stack,
        state.wholeStackBuffer,
        0,
        (int)stack.samplesWritten * stack.bytesPerSample
      );
    }

    byte[] wholeStackBuffer = state.wholeStackBuffer;

    int bytesPerSample = stack.bytesPerSample;
    int width = getSizeX();
    int lifetimeCount = getSizeZ();

    for (int yy=y; yy < y+h; ++yy) {
      for (int xx=x; xx < x+w; ++xx) {
        int sourcePosition =
          lifetimeCount * bytesPerSample * ((yy * width) + xx) +
          no * bytesPerSample;

        int destinationPosition =
          ((yy - y) * w * bytesPerSample) + ((xx - x) * bytesPerSample);

        System.arraycopy(
          wholeStackBuffer,
          sourcePosition,
          buffer,
          destinationPosition,
          bytesPerSample
        );
      }
    }
  }

  @Override
  public byte[] openBytes(int no, byte[] buffer, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buffer.length, x, y, w, h);

    final int series = getSeries();

    if (state == null || state.series != series) {
      state = new State(series);
    }

    final long rows = getSizeY();
    final long columns = getSizeX();

    boolean isFLIM = FormatTools.LIFETIME.equals(getModuloZ().type);

    final Stack stack = stacks.get(series);

    if (isFLIM) {
      readFlimFrame(stack, no, buffer, x, y, w, h);
    } else {
      long frameStartSampleOffset = (no * rows * columns) +
        y * columns +
        x;

      readStackFrame(stack, frameStartSampleOffset, buffer, 0, w, h);
    }

    return buffer;
  }

  @Override
  public void close(boolean fileOnly) throws IOException {
    if (! fileOnly) {
      state = null;
      skipBuffer = null;
      file_version = - 1;
      ome_meta_data = null;
      stacks.clear();
    }

    super.close(fileOnly);
  }

  private ArrayList<Element> getChildNodes(Element root) {
    ArrayList<Element> list = new ArrayList<Element>();
    NodeList children = root.getChildNodes();

    for (int i=0; i<children.getLength(); i++) {
      Object child = children.item(i);
      if (child instanceof Element) {
        list.add((Element) child);
      }
    }
    return list;
  }
}
