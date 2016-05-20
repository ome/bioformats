/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) Max Planck Institute for Biophysical Chemistry, 
 * Goettingen, 2014 - 2015
 *
 * Copyright (C) 2014 - 2016 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;
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
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

import ome.units.quantity.Length;
import ome.units.UNITS;

import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 * OBFReader is the file format reader for Imspector OBF files.
 *
 * @author Bjoern Thiel bjoern.thiel at mpibpc.mpg.de
 */

public class OBFReader extends FormatReader
{
  private static final boolean LITTLE_ENDIAN = true;

  private static final String FILE_MAGIC_STRING = "OMAS_BF\n";
  private static final String STACK_MAGIC_STRING = "OMAS_BF_STACK\n";
  private static final short MAGIC_NUMBER = (short) 0xFFFF;

  private static final int FILE_VERSION = 2;
  private static final int STACK_VERSION = 5;

  private static final int MAXIMAL_NUMBER_OF_DIMENSIONS = 15;

  private class Stack
  {
    long position;
    long length;
    boolean compression;
  }
  private List<Stack> stacks = new ArrayList<Stack>();

  private class Frame
  {
    byte[] bytes;
    int series;
    int number;
  }
  private Frame currentInflatedFrame = new Frame();

  private transient Inflater inflater;

  public OBFReader()
  {
    super("OBF", new String[] {"obf", "msr"});
    suffixNecessary = false;
    suffixSufficient = false;
    datasetDescription = "OBF file";
  }

  private int getFileVersion(RandomAccessInputStream stream) throws IOException
  {
    stream.seek(0);

    stream.order(LITTLE_ENDIAN);

    try
    {
      final String magicString = stream.readString(FILE_MAGIC_STRING.length());
      final short magicNumber = stream.readShort();
      final int version = stream.readInt();

      if (magicString.equals(FILE_MAGIC_STRING) && magicNumber == MAGIC_NUMBER)
      {
        return version;
      }
    }
    catch(IOException exception) { }

    return -1;
  }

  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException
  {
    final int fileVersion = getFileVersion(stream);

    return fileVersion >= 0 && fileVersion <= FILE_VERSION;
  }

  @Override
  protected void initFile(String id) throws FormatException, IOException
  {
    super.initFile(id);

    currentInflatedFrame.series = -1;
    currentInflatedFrame.number = -1;

    in = new RandomAccessInputStream(id);

    final int fileVersion = getFileVersion(in);
    long stackPosition = in.readLong();
    final int lengthOfDescription = in.readInt();
    final String description = in.readString(lengthOfDescription);
    metadata.put("Description", description);
    String ome_xml = "" ;
    if (fileVersion > 1)
    {
      final long meta_data_position = in.readLong() ;
      final long current_position = in.getFilePointer() ;

      in.seek( meta_data_position ) ;

      for (String key = readString() ; key.length() > 0 ; key = readString())
      {
        if (key.equals( "ome_xml" ))
        {
          ome_xml = readString() ;
          break ;
        }
        else
        {
          addGlobalMeta( key, readString() ) ;
        }
      }

      in.seek( current_position ) ;
    }

    if (stackPosition != 0)
    {
      core.clear();
      do
      {
        stackPosition = initStack(stackPosition, fileVersion);
      }
      while (stackPosition != 0);
    }

    MetadataStore ome = makeFilterMetadata();

    boolean parsedXML = false;
    if (fileVersion > 1) {
     try
      {
        ServiceFactory factory = new ServiceFactory() ;
        OMEXMLService service = factory.getInstance( OMEXMLService.class ) ;

        if (service.validateOMEXML(ome_xml)) {
          service.convertMetadata( ome_xml, ome ) ;
          parsedXML = true;
          MetadataTools.populatePixels(ome, this, false, false);
        }
      }
      catch (DependencyException exception) 
      {
        throw new MissingLibraryException( OMEXMLServiceImpl.NO_OME_XML_MSG, exception ) ;
      }
      catch (ServiceException exception) 
      {
        throw new FormatException( exception ) ;
      }
      catch (Exception e) {
        LOGGER.warn("Could not parse OME-XML metadata", e);
      }
    }

    if (fileVersion <= 1 || !parsedXML)
    {
      MetadataTools.populatePixels(ome, this);
      for (int series = 0; series != core.size(); ++ series)
      {
        CoreMetadata obf = core.get(series);

        final String name = obf.seriesMetadata.get("Name").toString();
        ome.setImageName(name, series);

        @SuppressWarnings("unchecked")
        final List<Double> lengths = (List<Double>) obf.seriesMetadata.get("Lengths");

        if (lengths.size() > 0)
        {
          double lengthX = Math.abs(lengths.get(0));
          if (lengthX < 0.01)
          {
            lengthX *= 1000000;
          }
          if (lengthX > 0)
          {
            Length physicalSizeX = FormatTools.getPhysicalSizeX(lengthX / obf.sizeX, UNITS.MICROMETER);
            if (physicalSizeX != null) {
              ome.setPixelsPhysicalSizeX(physicalSizeX, series);
            }
          }
        }
        if (lengths.size() > 1)
        {
          double lengthY = Math.abs(lengths.get(1));
          if (lengthY < 0.01)
          {
            lengthY *= 1000000;
          }
          if (lengthY > 0)
          {
            Length physicalSizeY = FormatTools.getPhysicalSizeY(lengthY / obf.sizeY, UNITS.MICROMETER);
            if (physicalSizeY != null) {
              ome.setPixelsPhysicalSizeY(physicalSizeY, series);
            }
          }
        }
        if (lengths.size() > 2)
        {
          double lengthZ = Math.abs(lengths.get(2));
          if (lengthZ < 0.01)
          {
            lengthZ *= 1000000;
          }
          if (lengthZ > 0)
          {
            Length physicalSizeZ = FormatTools.getPhysicalSizeZ(lengthZ / obf.sizeZ, UNITS.MICROMETER);
            if (physicalSizeZ != null) {
              ome.setPixelsPhysicalSizeZ(physicalSizeZ, series);
            }
          }
        }
      }
    }
  }

  private long initStack(long current, int fileVersion) throws FormatException, IOException
  {
    in.seek(current);

    final String magicString = in.readString(STACK_MAGIC_STRING.length());
    final short magicNumber = in.readShort();
    final int version = in.readInt();

    if (magicString.equals(STACK_MAGIC_STRING) && magicNumber == MAGIC_NUMBER && version <= STACK_VERSION)
    {
      CoreMetadata obf = new CoreMetadata();
      core.add(obf);

      obf.littleEndian = LITTLE_ENDIAN;

      obf.thumbnail = false;

      final int numberOfDimensions = in.readInt();
      if (numberOfDimensions > 5)
      {
        throw new FormatException("Unsupported number of " + numberOfDimensions + " dimensions");
      }

      int[] sizes = new int[MAXIMAL_NUMBER_OF_DIMENSIONS];
      for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension)
      {
        final int size = in.readInt();
        sizes[dimension] = dimension < numberOfDimensions ? size : 1;
      }

      obf.sizeX = sizes[0];
      obf.sizeY = sizes[1];
      obf.sizeZ = sizes[2];
      obf.sizeC = sizes[3];
      obf.sizeT = sizes[4];

      obf.imageCount = sizes[2] * sizes[3] * sizes[4];
      obf.dimensionOrder = "XYZCT";
      obf.orderCertain = false;

      List<Double> lengths = new ArrayList<Double>();
      for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension)
      {
        final double length = in.readDouble();
        if (dimension < numberOfDimensions)
        {
          lengths.add(new Double(length));
        }
      }
      obf.seriesMetadata.put("Lengths", lengths);

      List<Double> offsets = new ArrayList<Double>();
      for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension)
      {
        final double offset = in.readDouble();
        if (dimension < numberOfDimensions)
        {
          offsets.add(new Double(offset));
        }
      }
      obf.seriesMetadata.put("Offsets", offsets);

      final int type = in.readInt();
      obf.pixelType = getPixelType(type);
      obf.bitsPerPixel = getBitsPerPixel(type);

      obf.indexed = false;
      obf.rgb = false;
      obf.interleaved = false;

      Stack stack = new Stack();

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
      obf.seriesMetadata.put("Name", name);
      String description = in.readString(lengthOfDescription);

      if (description != null) {
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
          obf.seriesMetadata.put("Description", description);
        }
      }

      stack.position = in.getFilePointer();

      stacks.add(stack);

      if (fileVersion >= 1)
      {
        in.skip(lengthOfData);

        final long footer = in.getFilePointer();
        final int offset = in.readInt();

        List<Boolean> stepsPresent = new ArrayList<Boolean>();
        for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension)
        {
          final int present = in.readInt();
          if (dimension < numberOfDimensions)
          {
            stepsPresent.add(new Boolean(present != 0));
          }
        }
        List<Boolean> stepLabelsPresent = new ArrayList<Boolean>();
        for (int dimension = 0; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS; ++ dimension)
        {
          final int present = in.readInt();
          if (dimension < numberOfDimensions)
          {
            stepLabelsPresent.add(new Boolean(present != 0));
          }
        }

        in.seek(footer + offset);

        List<String> labels = new ArrayList<String>();
        for (int dimension = 0; dimension != numberOfDimensions; ++ dimension)
        {
          final int length = in.readInt();
          final String label = in.readString(length);
          labels.add(label);
        }
        obf.seriesMetadata.put("Labels", labels);

        List<List<Double>> steps = new ArrayList<List<Double>>();
        for (int dimension = 0; dimension != numberOfDimensions; ++ dimension)
        {
          List<Double> list = new ArrayList<Double>();
          if (stepsPresent.get(dimension))
          {
            for (int position = 0; position != sizes[dimension]; ++ position)
            {
              final double step = in.readDouble();
              list.add(new Double(step));
            }
          }
          steps.add(list);
        }
        obf.seriesMetadata.put("Steps", steps);

        List<List<String>> stepLabels = new ArrayList<List<String>>();
        for (int dimension = 0; dimension != numberOfDimensions; ++ dimension)
        {
          List<String> list = new ArrayList<String>();
          if (stepLabelsPresent.get(dimension))
          {
            for (int position = 0; position != sizes[dimension]; ++ position)
            {
              final int length = in.readInt();
              final String label = in.readString(length);
              list.add(label);
            }
          }
          stepLabels.add(list);
        }
        obf.seriesMetadata.put("StepLabels", stepLabels);
      }
      return next;
    }
    else
    {
      throw new FormatException("Unsupported stack format");
    }
  }

  private int getPixelType(int type) throws FormatException
  {
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

  private int getBitsPerPixel(int type) throws FormatException
  {
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

  private long getLength(long length) throws FormatException
  {
    if (length >= 0)
    {
      return length;
    }
    else
    {
      throw new FormatException("Negative stack length on disk");
    }
  }

  private boolean getCompression(int compression) throws FormatException
  {
    switch (compression) {
      case 0: return false;
      case 1: return true;
      default: throw new FormatException("Unsupported compression " + compression);
    }
  }

  private String readString() throws IOException
  {
    final int length = in.readInt() ;
    if (length > 0)
    {
      return in.readString( length ) ;
    }
    else
    {
      return "" ;
    }
  }

  @Override
  public byte[] openBytes(int no, byte[] buffer, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buffer.length, x, y, w, h);

    final int rows = getSizeY();
    final int columns = getSizeX();
    final int bytesPerPixel = getBitsPerPixel() / 8;

    final int series = getSeries();
    final Stack stack = stacks.get(series);
    if (stack.compression)
    {
      if (series != currentInflatedFrame.series)
      {
        currentInflatedFrame.bytes = new byte[rows * columns * bytesPerPixel];
        currentInflatedFrame.series = series;
        currentInflatedFrame.number = - 1;
      }

      if (inflater == null) {
        inflater = new Inflater();
      }

      byte[] bytes = currentInflatedFrame.bytes;
      if (no != currentInflatedFrame.number)
      {
        if (no < currentInflatedFrame.number)
        {
          currentInflatedFrame.number = - 1;
        }
        if (currentInflatedFrame.number == - 1)
        {
          in.seek(stack.position);
          inflater.reset();
        }

        byte[] input = new byte[8192];
        while (no != currentInflatedFrame.number)
        {
          int offset = 0;
          while (offset != bytes.length)
          {
            if (inflater.needsInput())
            {
              final long remainder = stack.position + stack.length - in.getFilePointer();
              if (remainder > 0)
              {
                final int length = remainder > input.length ? input.length : (int) remainder;

                in.read(input, 0, length);
                inflater.setInput(input, 0, length);
              }
              else
              {
                throw new FormatException("Corrupted zlib compression");
              }
            }
            else if (inflater.needsDictionary())
            {
              throw new FormatException("Unsupported zlib compression");
            }
            try
            {
              offset += inflater.inflate(bytes, offset, bytes.length - offset);
            }
            catch (DataFormatException exception)
            {
              throw new FormatException(exception.getMessage());
            }
          }
          ++ currentInflatedFrame.number;
        }
      }
      for (int row = 0; row != h; ++ row)
      {
        System.arraycopy(bytes, ((row + y) * columns + x) * bytesPerPixel, buffer, row * w * bytesPerPixel, w * bytesPerPixel);
      }
    }
    else
    {
      for (int row = 0; row != h; ++ row)
      {
        in.seek(stack.position + ((no * rows + row + y) * columns + x) * bytesPerPixel);
        in.read(buffer, row * w * bytesPerPixel, w * bytesPerPixel);
      }
    }

    return buffer;
  }

  @Override
  public void close(boolean fileOnly) throws IOException
  {
    super.close(fileOnly);
    if (!fileOnly) {
      stacks.clear();
      currentInflatedFrame = new Frame();
      inflater = null;
    }
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
