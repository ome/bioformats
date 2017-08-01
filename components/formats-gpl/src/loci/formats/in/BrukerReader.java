/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import ome.xml.model.primitives.Timestamp;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * BrukerReader is the file format reader for Bruker MRI files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class BrukerReader extends FormatReader {

  // -- Constants --

  private static final String DATE_FORMAT = "HH:mm:ss  d MMM yyyy";

  // -- Fields --

  private ArrayList<String> pixelsFiles = new ArrayList<String>();
  private ArrayList<String> allFiles = new ArrayList<String>();

  private int lastSeries = -1;
  private RandomAccessInputStream seriesStream;

  // -- Constructor --

  /** Constructs a new Bruker reader. */
  public BrukerReader() {
    super("Bruker", "");
    suffixSufficient = false;
    domains = new String[] {FormatTools.MEDICAL_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One 'fid' and one 'acqp' plus several other " +
      "metadata files and a 'pdata' directory";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getRequiredDirectories(String[]) */
  @Override
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return 1;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    Location file = new Location(name).getAbsoluteFile();
    return file.getName().equals("fid") || file.getName().equals("acqp");
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (getSeries() != lastSeries) {
      if (seriesStream != null) {
        seriesStream.close();
      }
      seriesStream = new RandomAccessInputStream(pixelsFiles.get(getSeries()));
      lastSeries = getSeries();
    }

    seriesStream.seek((long) no * FormatTools.getPlaneSize(this));
    readPlane(seriesStream, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    String dir = pixelsFiles.get(getSeries());
    Location realDir = new Location(dir).getParentFile();
    if (realDir != null) {
      realDir = realDir.getParentFile();
      if (realDir != null) {
        realDir = realDir.getParentFile();
        if (realDir != null) {
          dir = realDir.getAbsolutePath();
          files.add(new Location(getCurrentFile()).getAbsolutePath());
      
          for (String f : allFiles) {
            if (f.startsWith(dir) && (!f.endsWith("2dseq") || !noPixels)) {
              if (!files.contains(f)) {
                files.add(f);
              }
            }
          }
        }
      }
    }

    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelsFiles.clear();
      allFiles.clear();
      lastSeries = -1;
      if (seriesStream != null) {
        seriesStream.close();
      }
      seriesStream = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Location originalFile = new Location(id).getAbsoluteFile();
    Location parent = originalFile.getParentFile().getParentFile();
    if (parent == null) {
      throw new FormatException("Unable to locate parent file to " + id);
    }

    String[] acquisitionDirs = parent.list(true);

    Comparator<String> comparator = new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        Integer i1 = 0;
        try {
          i1 = new Integer(s1);
        }
        catch (NumberFormatException e) { }
        Integer i2 = 0;
        try {
          i2 = new Integer(s2);
        }
        catch (NumberFormatException e) { }

        return i1.compareTo(i2);
      }
    };

    Arrays.sort(acquisitionDirs, comparator);

    ArrayList<String> acqpFiles = new ArrayList<String>();
    ArrayList<String> recoFiles = new ArrayList<String>();

    for (String f : acquisitionDirs) {
      Location dir = new Location(parent, f);
      if (dir.isDirectory()) {
        String[] files = dir.list(true);
        for (String file : files) {
          Location child = new Location(dir, file);
          if (!child.isDirectory()) {
            allFiles.add(child.getAbsolutePath());
            if (file.equals("acqp")) {
              acqpFiles.add(child.getAbsolutePath());
            }
          }
          else {
            Location grandchild = new Location(child, "1");
            if (grandchild.exists()) {
              String[] moreFiles = grandchild.list(true);
              for (String m : moreFiles) {
                Location ggc = new Location(grandchild, m);
                if (!ggc.isDirectory()) {
                  allFiles.add(ggc.getAbsolutePath());
                  if (m.equals("2dseq")) {
                    pixelsFiles.add(ggc.getAbsolutePath());
                  }
                  else if (m.equals("reco")) {
                    recoFiles.add(ggc.getAbsolutePath());
                  }
                }
              }
            }
          }
        }

        if (acqpFiles.size() > pixelsFiles.size()) {
          acqpFiles.remove(acqpFiles.size() - 1);
        }
        if (recoFiles.size() > pixelsFiles.size()) {
          recoFiles.remove(recoFiles.size() - 1);
        }
      }
    }


    String[] imageNames = new String[pixelsFiles.size()];
    String[] timestamps = new String[pixelsFiles.size()];
    String[] institutions = new String[pixelsFiles.size()];
    String[] users = new String[pixelsFiles.size()];

    core.clear();
    for (int series=0; series<pixelsFiles.size(); series++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      setSeries(series);

      String acqData = DataTools.readFile(acqpFiles.get(series));
      String[] lines = acqData.split("\n");

      String[] sizes = null;
      String[] ordering = null;
      int ni = 0, nr = 0, ns = 0;
      int bits = 0;
      boolean signed = false;
      boolean isFloat = false;

      for (int i=0; i<lines.length; i++) {
        String line = lines[i];
        int index = line.indexOf('=');
        if (index >= 0) {
          String key = line.substring(0, index);
          String value = line.substring(index + 1);

          if (value.startsWith("(")) {
            value = lines[i + 1].trim();
            if (value.startsWith("<")) {
              value = value.substring(1, value.length() - 1);
            }
          }
          if (key.length() < 4) {
            continue;
          }

          addSeriesMeta(key.substring(3), value);

          if (key.equals("##$NI")) {
            ni = Integer.parseInt(value);
          }
          else if (key.equals("##$NR")) {
            nr = Integer.parseInt(value);
          }
          else if (key.equals("##$ACQ_word_size")) {
            bits = Integer.parseInt(value.substring(1, value.lastIndexOf("_")));
          }
          else if (key.equals("##$BYTORDA")) {
            ms.littleEndian = value.toLowerCase().equals("little");
          }
          else if (key.equals("##$ACQ_size")) {
            sizes = value.split(" ");
          }
          else if (key.equals("##$ACQ_obj_order")) {
            ordering = value.split(" ");
          }
          else if (key.equals("##$ACQ_time")) {
            timestamps[series] = value;
          }
          else if (key.equals("##$ACQ_institution")) {
            institutions[series] = value;
          }
          else if (key.equals("##$ACQ_operator")) {
            users[series] = value;
          }
          else if (key.equals("##$ACQ_scan_name")) {
            imageNames[series] = value;
          }
          else if (key.equals("##$ACQ_ns_list_size")) {
            ns = Integer.parseInt(value);
          }
        }
      }

      String recoData = DataTools.readFile(recoFiles.get(series));
      lines = recoData.split("\n");

      for (int i=0; i<lines.length; i++) {
        String line = lines[i];
        int index = line.indexOf('=');
        if (index >= 0) {
          String key = line.substring(0, index);
          String value = line.substring(index + 1);

          if (value.startsWith("(")) {
            value = lines[i + 1].trim();
            if (value.startsWith("<")) {
              value = value.substring(1, value.length() - 1);
            }
          }
          if (key.length() < 4) {
            continue;
          }

          addSeriesMeta(key.substring(3), value);

          if (key.equals("##$RECO_size")) {
            sizes = value.split(" ");
          }
          else if (key.equals("##$RECO_wordtype")) {
            bits = Integer.parseInt(value.substring(1, value.indexOf("BIT")));
            signed = value.indexOf("_SGN_") >= 0;
            isFloat = !value.endsWith("_INT");
          }
        }
      }

      int td = Integer.parseInt(sizes[0]);
      int ys = sizes.length > 1 ? Integer.parseInt(sizes[1]) : 0;
      int zs = sizes.length > 2 ? Integer.parseInt(sizes[2]) : 0;

      if (sizes.length == 2) {
        if (ni == 1) {
          ms.sizeY = ys;
          ms.sizeZ = nr;
        }
        else {
          ms.sizeY = ys;
          ms.sizeZ = ni;
        }
      }
      else if (sizes.length == 3) {
        ms.sizeY = ni * ys;
        ms.sizeZ = nr * zs;
      }

      ms.sizeX = td;

      ms.sizeZ /= ns;
      ms.sizeT = ns * nr;
      ms.sizeC = 1;
      ms.imageCount = getSizeZ() * getSizeC() * getSizeT();
      ms.dimensionOrder = "XYCTZ";
      ms.rgb = false;
      ms.interleaved = false;
      ms.pixelType =
        FormatTools.pixelTypeFromBytes(bits / 8, signed, isFloat);
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    for (int series=0; series<getSeriesCount(); series++) {
      store.setImageName(imageNames[series] + " #" + (series + 1), series);
      String date = DateTools.formatDate(timestamps[series], DATE_FORMAT);
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(date), series);
      }

      String expID = MetadataTools.createLSID("Experimenter", series);
      store.setExperimenterID(expID, series);
      store.setExperimenterLastName(users[series], series);
      store.setExperimenterInstitution(institutions[series], series);

      store.setImageExperimenterRef(expID, series);
    }
  }

}
