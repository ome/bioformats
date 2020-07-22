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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.meta.MetadataConverter;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Image;
import ome.xml.model.Instrument;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;
import ome.units.UNITS;
import ome.units.quantity.Length;

/**
 * MetaxpressTiffReader is the file format reader for MetaXpress .htd + TIFF files.
 */
public class MetaxpressTiffReader extends CellWorxReader {

  // -- Fields --

  // -- Constructor --

  /** Constructs a new MetaXpress TIFF reader. */
  public MetaxpressTiffReader() {
    super("MetaXpress TIFF", new String[] {"htd", "tif"});
    datasetDescription = "One .htd file plus one or more .tif files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "htd")) {
      return true;
    }
    if (!open) {
      return false;
    }
    return foundHTDFile(name);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();
    files.add(currentId);

    int row = getWellRow(getSeries());
    int col = getWellColumn(getSeries());

    if (!noPixels) {
      for (String f : wellFiles[row][col]) {
        if (new Location(f).exists()) {
          files.add(f);
        }
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    String[] files = super.getUsedFiles(noPixels);

    List<String> allFiles = new ArrayList<String>();
    for (String f : files) {
      allFiles.add(f);
    }
    if (directoryList != null) {
      Location root = new Location(currentId).getParentFile();
      for (String f : directoryList) {
        if (f.toLowerCase().indexOf("_thumb") > 0) {
          String path = new Location(root, f).getAbsolutePath();
          if (!allFiles.contains(path)) {
            allFiles.add(path);
          }
        }
      }
    }
    return allFiles.toArray(new String[allFiles.size()]);
  }

  // -- Internal FormatReader API methods --

  // -- Helper methods --

  protected void findPixelsFiles() throws FormatException {
    // find pixels files
    String plateName = getPlateName(currentId);
    for (int row=0; row<wellFiles.length; row++) {
      for (int col=0; col<wellFiles[row].length; col++) {
        if (wellFiles[row][col] != null) {
          wellCount++;
          char rowLetter = (char) (row + 'A');
          wellFiles[row][col] = getTiffFiles(
            plateName, rowLetter, col, wavelengths.length, nTimepoints, zSteps);
        }
      }
    }
  }

  protected void parseWellLogFile(int wellIndex, MetadataStore store)
    throws IOException
  {
    return;
  }

  protected IFormatReader getReader(String file, boolean omexml)
    throws FormatException, IOException
  {
    IFormatReader reader = new MetamorphReader();
    initReader(reader, file, omexml);
    return reader;
  }

  private String[] getTiffFiles(String plateName, char rowLetter, int col,
    int channels, int nTimepoints, int zSteps)
    throws FormatException
  {
    String well = rowLetter + String.format("%02d", col + 1);
    String base = plateName + well;

    String[] files = new String[fieldCount * channels * nTimepoints * zSteps];

    int nextFile = 0;
    for (int field=0; field<fieldCount; field++) {
      for (int channel=0; channel<channels; channel++) {
        for (int t=0; t<nTimepoints; t++, nextFile++) {
          String file = base;
          if (fieldCount > 1) {
           file += "_s" + (field + 1);
          }
          if (doChannels || channels > 1) {
            file += "_w" + (channel + 1);
          }
          if (nTimepoints > 1) {
            file += "_t" + nTimepoints;
          }
          files[nextFile] = file + ".tif";

          if (!new Location(files[nextFile]).exists()) {
            files[nextFile] = file + ".TIF";
          }
        }
      }
    }

    boolean noneExist = true;
    for (String file : files) {
      if (file != null && new Location(file).exists()) {
        noneExist = false;
        break;
      }
    }

    if (noneExist) {
      nextFile = 0;
      Location parent =
        new Location(currentId).getAbsoluteFile().getParentFile();
      if (directoryList == null) {
        directoryList = parent.list(true);
        Arrays.sort(directoryList);
      }
      for (String f : directoryList) {
        if (checkSuffix(f, new String [] {"tif", "tiff"})) {
          String path = new Location(parent, f).getAbsolutePath();
          if (path.startsWith(base) && path.toLowerCase().indexOf("_thumb") < 0)
          {
            files[nextFile++] = path;
            noneExist = false;
          }
        }
      }

      if (noneExist) {
        subdirectories = true;

        // if all else fails, look for a directory structure:
        //  * file.htd
        //  * TimePoint_<t>
        //    * ZStep_<z>
        //      * file_<...>.tif
        base = base.substring(base.lastIndexOf(File.separator) + 1);
        LOGGER.debug("expected file prefix = {}", base);
        nextFile = 0;
        for (int i=0; i<nTimepoints; i++) {
          Location dir = new Location(parent, "TimePoint_" + (i + 1));
          if (dir.exists() && dir.isDirectory()) {
            for (int z=0; z<zSteps; z++) {
              Location file = new Location(dir, "ZStep_" + (z + 1));
              String[] zList = null;
              if (file.exists() && file.isDirectory()) {
                zList = file.list(true);
              }
              else if (zSteps == 1) {
                // if SizeZ == 1, the TIFF files may be in the
                // TimePoint_<t> directory
                file = dir;
                zList = file.list(true);
              }
              LOGGER.debug("parent directory = {}", file);

              if (zList != null) {
                Arrays.sort(zList);
                for (String f : zList) {
                  LOGGER.debug("  checking relative path = {}", f);
                  String path = new Location(file, f).getAbsolutePath();
                  if (f.startsWith(base) && path.indexOf("_thumb") < 0) {
                    if (nextFile < files.length) {
                      files[nextFile] = path;
                    }
                    nextFile++;
                  }
                }
              }
            }
          }
        }
        if (nextFile != files.length) {
          LOGGER.warn("Well {} expected {} files; found {}",
            well, files.length, nextFile);
        }
      }
    }

    return files;
  }

}
