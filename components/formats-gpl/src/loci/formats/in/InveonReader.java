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
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;

/**
 * InveonReader is the file format reader for Inveon files.
 */
public class InveonReader extends FormatReader {

  // -- Constants --

  private static final String HEADER = "Header file for data file";

  // -- Fields --

  private String datFile;
  private ArrayList<Long> dataPointers = new ArrayList<Long>();

  // -- Constructor --

  /** Constructs a new Inveon reader. */
  public InveonReader() {
    super("Inveon", new String[] {"hdr"});
    domains = new String[] {FormatTools.MEDICAL_DOMAIN};
    suffixSufficient = false;
    hasCompanionFiles = true;
    datasetDescription = "One .hdr file plus one similarly-named file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "hdr")) {
      return super.isThisType(name, open);
    }

    Location file = new Location(name + ".hdr");

    if (!file.exists()) {
      return false;
    }
    return super.isThisType(file.getAbsolutePath(), open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 128;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(HEADER) >= 0;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) return new String[] {currentId};
    return new String[] {currentId, datFile};
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    long planeSize = (long) FormatTools.getPlaneSize(this);
    int index = getCoreIndex();

    RandomAccessInputStream dat = new RandomAccessInputStream(datFile);
    try {
      dat.order(isLittleEndian());
      dat.seek(dataPointers.get(index) + no * planeSize);
      readPlane(dat, x, y, w, h, buf);
    }
    finally {
      dat.close();
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      datFile = null;
      dataPointers.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    if (!checkSuffix(id, "hdr")) {
      id += ".hdr";
    }

    super.initFile(id);

    String headerData = DataTools.readFile(id);
    String[] lines = headerData.split("\n");

    String date = null;
    String institution = null, investigator = null;
    String model = null;
    String description = null;
    Double pixelSizeX = null;
    Double pixelSizeY = null;
    Double pixelSizeZ = null;
    int frames = 0;

    for (String line : lines) {
      line = line.trim();

      if (!line.startsWith("#")) {
        int space = line.indexOf(' ');
        if (space < 0) {
          continue;
        }

        String key = line.substring(0, space);
        String value = line.substring(space + 1);

        if (key.equals("institution")) {
          institution = value;
        }
        else if (key.equals("investigator")) {
          investigator = value;
        }
        else if (key.equals("study")) {
          description = value;
        }
        else if (key.equals("model")) {
          value = transformModel(value);
          model = value;
        }
        else if (key.equals("modality")) {
          value = transformModality(value);
        }
        else if (key.equals("modality_configuration")) {
          value = transformModalityConfiguration(value);
        }
        else if (key.equals("file_type")) {
          value = transformFileType(value);
        }
        else if (key.equals("acquisition_mode")) {
          value = transformAcquisitionMode(value);
        }
        else if (key.equals("bed_control")) {
          value = transformBedControl(value);
        }
        else if (key.equals("bed_motion")) {
          value = transformBedMotion(value);
        }
        else if (key.equals("registration_available")) {
          value = transformRegistrationAvailable(value);
        }
        else if (key.equals("normalization_applied")) {
          value = transformNormalizationApplied(value);
        }
        else if (key.equals("recon_algorithm")) {
          value = transformReconAlgorithm(value);
        }
        else if (key.equals("x_filter")) {
          value = transformFilter(value);
        }
        else if (key.equals("y_filter")) {
          value = transformFilter(value);
        }
        else if (key.equals("z_filter")) {
          value = transformFilter(value);
        }
        else if (key.equals("subject_orientation")) {
          value = transformSubjectOrientation(value);
        }
        else if (key.equals("subject_length_units")) {
          value = transformSubjectLengthUnits(value);
        }
        else if (key.equals("subject_weight_units")) {
          value = transformSubjectWeightUnits(value);
        }
        else if (key.equals("gantry_rotation")) {
          value = transformGantryRotation(value);
        }
        else if (key.equals("rotation_direction")) {
          value = transformRotationDirection(value);
        }
        else if (key.equals("ct_warping")) {
          value = transformCTWarping(value);
        }
        else if (key.equals("ct_projection_interpolation")) {
          value = transformCTProjectionInterpolation(value);
        }
        else if (key.equals("event_type")) {
          value = transformEventType(value);
        }
        else if (key.equals("projection") ||
          key.equals("ct_projection_center_offset") ||
          key.equals("ct_projection_horizontal_bed_offset"))
        {
          space = value.indexOf(' ');
          int index = Integer.parseInt(value.substring(0, space));
          value = value.substring(space + 1);
          key += " " + index;
        }
        else if (key.equals("user")) {
          space = value.indexOf(' ');
          key = value.substring(0, space);
          value = value.substring(space + 1);
        }
        else if (key.equals("file_name")) {
          // remove path from stored file name, if present
          value = value.replace('/', File.separatorChar);
          value = value.replace('\\', File.separatorChar);
          value = value.substring(value.lastIndexOf(File.separator) + 1);

          Location header = new Location(currentId).getAbsoluteFile();
          Location dat = new Location(header.getParent(), value);
          if (dat.exists()) {
            datFile = dat.getAbsolutePath();
          }
          else {
            // usually this means that the files were renamed
            String[] allFiles = header.getParentFile().list(true);
            for (String file : allFiles) {
              if (header.getName().startsWith(file)) {
                datFile = new Location(header.getParent(), file).getAbsolutePath();
              }
            }
          }
        }
        else if (key.equals("time_frames")) {
          int sizeT = Integer.parseInt(value);
          for (int i=0; i<core.size(); i++) {
            core.get(i).sizeT = sizeT;
          }
        }
        else if (key.equals("total_frames")) {
          frames = Integer.parseInt(value);
        }
        else if (key.equals("number_of_bed_positions")) {
          int nPos = (int) Math.min(frames, Integer.parseInt(value));
          if (nPos > 1) {
            CoreMetadata original = core.get(0);
            core.clear();
            for (int i=0; i<nPos; i++) {
              core.add(original);
            }
          }
        }
        else if (key.equals("data_type")) {
          setDataType(value);
        }
        else if (key.equals("x_dimension")) {
          int sizeX = Integer.parseInt(value);
          for (int i=0; i<core.size(); i++) {
            core.get(i).sizeX = sizeX;
          }
        }
        else if (key.equals("y_dimension")) {
          int sizeY = Integer.parseInt(value);
          for (int i=0; i<core.size(); i++) {
            core.get(i).sizeY = sizeY;
          }
        }
        else if (key.equals("z_dimension")) {
          int sizeZ = Integer.parseInt(value);
          for (int i=0; i<core.size(); i++) {
            core.get(i).sizeZ = sizeZ;
          }
        }
        else if (key.equals("scan_time")) {
          date = value;
        }
        else if (key.equals("data_file_pointer")) {
          String[] values = value.split(" ");
          int[] ints = new int[values.length];
          for (int i=0; i<ints.length; i++) {
            ints[i] = Integer.parseInt(values[i]);
          }
          byte[] b = DataTools.intsToBytes(ints, false);
          dataPointers.add(DataTools.bytesToLong(b, false));
        }
        // pixel sizes stored in mm
        else if (key.equals("pixel_size_x")) {
          pixelSizeX = new Double(value) * 1000;
        }
        else if (key.equals("pixel_size_y")) {
          pixelSizeY = new Double(value) * 1000;
        }
        else if (key.equals("pixel_size_z")) {
          pixelSizeZ = new Double(value) * 1000;
        }

        addGlobalMeta(key, value);
      }
    }

    for (int i=0; i<core.size(); i++) {
      CoreMetadata ms = core.get(i);
      if (ms.sizeZ == 0) {
        ms.sizeZ = 1;
      }
      if (ms.sizeT == 0) {
        ms.sizeT = 1;
      }

      ms.sizeC = 1;
      ms.rgb = false;
      ms.interleaved = false;
      ms.indexed = false;
      ms.dimensionOrder = "XYZCT";

      ms.imageCount = ms.sizeZ * ms.sizeC * ms.sizeT;
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String experimenter = null, instrument = null;
    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      experimenter = MetadataTools.createLSID("Experimenter", 0);
      store.setExperimenterID(experimenter, 0);
      store.setExperimenterUserName(investigator, 0);
      store.setExperimenterInstitution(institution, 0);

      instrument = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrument, 0);
      store.setMicroscopeModel(model, 0);
    }

    for (int i=0; i<core.size(); i++) {
      if (date != null) {
        String newDate = DateTools.formatDate(date, "EEE MMM dd HH:mm:ss yyyy");
        if (newDate != null) {
          store.setImageAcquisitionDate(new Timestamp(newDate), i);
        }
      }

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        if (experimenter != null) {
          store.setImageExperimenterRef(experimenter, i);
        }
        if (instrument != null) {
          store.setImageInstrumentRef(instrument, i);
        }

        store.setImageDescription(description, i);

        Length sizeX = FormatTools.getPhysicalSizeX(pixelSizeX);
        Length sizeY = FormatTools.getPhysicalSizeY(pixelSizeY);
        Length sizeZ = FormatTools.getPhysicalSizeZ(pixelSizeZ);

        if (sizeX != null) {
          store.setPixelsPhysicalSizeX(sizeX, i);
        }
        if (sizeY != null) {
          store.setPixelsPhysicalSizeY(sizeY, i);
        }
        if (sizeZ != null) {
          store.setPixelsPhysicalSizeZ(sizeZ, i);
        }
      }
    }

  }

  // -- Helper methods --

  // Enumeration data is taken from the comments of the .hdr files.

  private String transformModel(String value) {
    int model = Integer.parseInt(value);
    switch (model) {
      case 2000:
        return "Primate";
      case 2001:
        return "Rodent";
      case 2002:
        return "microPET2";
      case 2500:
        return "Focus_220";
      case 2501:
        return "Focus_120";
      case 3000:
        return "mCAT";
      case 3500:
        return "mCATII";
      case 4000:
        return "mSPECT";
      case 5000:
        return "Inveon_Dedicated_PET";
      case 5001:
        return "Inveon_MM_Platform";
      case 6000:
        return "MR_PET_Head_Insert";
      case 8000:
        return "Tuebingen_PET_MR";
    }
    return "Unknown";
  }

  private String transformModality(String value) {
    int modality = Integer.parseInt(value);
    switch (modality) {
      case 0:
        return "PET acquisition";
      case 1:
        return "CT acquisition";
      case 2:
        return "SPECT acquisition";
    }
    return "Unknown";
  }

  private String transformModalityConfiguration(String value) {
    int modalityConfiguration = Integer.parseInt(value);
    switch (modalityConfiguration) {
      case 3000:
        return "mCAT";
      case 3500:
        return "mCATII";
      case 3600:
        return "Inveon_MM_Std_CT";
      case 3601:
        return "Inveon_MM_HiRes_Std_CT";
      case 3602:
        return "Inveon_MM_Std_LFOV_CT";
      case 3603:
        return "Inveon_MM_HiRes_LFOV_CT";
    }
    return "Unknown";
  }

  private String transformFileType(String value) {
    int type = Integer.parseInt(value);
    switch (type) {
      case 1:
        return "List mode";
      case 2:
        return "Sinogram";
      case 3:
        return "Normalization";
      case 4:
        return "Attenuation correction";
      case 5:
        return "Image data";
      case 6:
        return "Blank data";
      // 7 omitted intentionally
      case 8:
        return "Mu map";
      case 9:
        return "Scatter correction";
      case 10:
        return "Crystal efficiency";
      case 11:
        return "Crystal interference correction";
      case 12:
        return "Transaxial geometric correction";
      case 13:
        return "Axial geometric correction";
      case 14:
        return "CT projection";
      case 15:
        return "SPECT raw projection";
      case 16:
        return "SPECT energy data from projections";
      case 17:
        return "SPECT normalization";
    }
    return "Unknown";
  }

  private String transformAcquisitionMode(String value) {
    int mode = Integer.parseInt(value);
    switch (mode) {
      case 1:
        return "Blank";
      case 2:
        return "Emission";
      case 3:
        return "Dynamic";
      case 4:
        return "Gated";
      case 5:
        return "Continuous bed motion";
      case 6:
        return "Singles transmission";
      case 7:
        return "Windowed coincidence transmission";
      case 8:
        return "Non-windowed coincidence transmission";
      case 9:
        return "CT projection";
      case 10:
        return "CT calibration";
      case 11:
        return "SPECT planar projection";
      case 12:
        return "SPECT multi-projection";
      case 13:
        return "SPECT calibration";
      case 14:
        return "SPECT normalization";
      case 15:
        return "SPECT detector setup";
      case 16:
        return "SPECT scout view";
    }
    return "Unknown";
  }

  private String transformBedControl(String value) {
    int control = Integer.parseInt(value);
    switch (control) {
      case 1:
        return "Dedicated PET";
      case 2:
        return "microCAT II";
      case 3:
        return "Multimodality bed control";
      case 4:
        return "microPET bed control";
    }
    return "Unknown";
  }

  private String transformBedMotion(String value) {
    int motion = Integer.parseInt(value);
    switch (motion) {
      case 1:
        return "Continuous";
      case 2:
        return "Multiple bed positions";
    }
    return "Unknown";
  }

  private String transformRegistrationAvailable(String value) {
    int available = Integer.parseInt(value);
    switch (available) {
      case 1:
        return "CT";
      case 2:
        return "PET";
    }
    return "None";
  }

  private String transformNormalizationApplied(String value) {
    int normalization = Integer.parseInt(value);
    switch (normalization) {
      case 1:
        return "Point source inversion";
      case 2:
        return "Point source component based";
      case 3:
        return "Cylinder source inversion";
      case 4:
        return "Cylinder source component based";
      case 5:
        return "Dark/bright field log normalization (CT)";
      case 6:
        return "SPECT flood inversion based";
    }
    return "None";
  }

  private String transformReconAlgorithm(String value) {
    int algorithm = Integer.parseInt(value);
    switch (algorithm) {
      case 1:
        return "Filtered Backprojection";
      case 2:
        return "OSEM2d";
      case 3:
        return "OSEM3d";
      // 4 and 5 omitted intentionally
      case 6:
        return "OSEM3D followed by MAP or FastMAP";
      case 7:
        return "MAPTR for transmission image";
      case 8:
        return "MAP 3D reconstruction";
      case 9:
        return "Feldkamp cone beam";
    }
    return "Unknown";
  }

  private String transformFilter(String value) {
    int space = value.indexOf(' ');
    int filter = Integer.parseInt(value.substring(0, space));
    String cutoff = " (cutoff = " + value.substring(space + 1) + ")";

    String filterType = "Unknown";
    switch (filter) {
      case 0:
        return "None";
      case 1:
        return "Ramp filter (backprojection)";
      case 2:
        return "First-order Butterworth window";
      case 3:
        return "Hanning window";
      case 4:
        return "Hamming window";
      case 5:
        return "Parzen window";
      case 6:
        return "Shepp filter";
      case 7:
        return "Second-order Butterworth window";
    }
    return filterType + cutoff;
  }

  private String transformSubjectOrientation(String value) {
    int orientation = Integer.parseInt(value);
    switch (orientation) {
      case 1:
        return "Feet first, prone";
      case 2:
        return "Head first, prone";
      case 3:
        return "Feet first, supine";
      case 4:
        return "Head first, supine";
      case 5:
        return "Feet first, right";
      case 6:
        return "Head first, right";
      case 7:
        return "Feet first, left";
      case 8:
        return "Head first, left";
    }
    return "Unknown";
  }

  private String transformSubjectLengthUnits(String value) {
    int units = Integer.parseInt(value);
    switch (units) {
      case 1:
        return "millimeters";
      case 2:
        return "centimeters";
      case 3:
        return "inches";
    }
    return "Unknown";
  }

  private String transformSubjectWeightUnits(String value) {
    int units = Integer.parseInt(value);
    switch (units) {
      case 1:
        return "grams";
      case 2:
        return "ounces";
      case 3:
        return "kilograms";
      case 4:
        return "pounds";
    }
    return "Unknown";
  }

  private String transformGantryRotation(String value) {
    int rotation = Integer.parseInt(value);
    switch (rotation) {
      case 0:
        return "No gantry rotation";
      case 1:
        return "Rotation with discrete steps";
      case 2:
        return "Continuous rotation";
    }
    return "Unknown";
  }

  private String transformRotationDirection(String value) {
    return value.equals("0") ? "Clockwise" : "Counterclockwise";
  }

  private String transformCTWarping(String value) {
    int warping = Integer.parseInt(value);
    switch (warping) {
      case 1:
        return "None";
      case 2:
        return "Bilinear";
      case 3:
        return "Nearest neighbor";
    }
    return "Unknown";
  }

  private String transformCTProjectionInterpolation(String value) {
    int interpolation = Integer.parseInt(value);
    switch (interpolation) {
      case 1:
        return "Bilinear";
      case 2:
        return "Nearest neighbor";
    }
    return "Unknown";
  }

  private String transformEventType(String value) {
    int type = Integer.parseInt(value);
    switch (type) {
      case 1:
        return "Singles";
      case 2:
        return "Prompt events (coincidences)";
      case 3:
        return "Delay events";
      case 4:
        return "Trues (prompts - delays)";
      case 5:
        return "Energy spectrum data";
    }
    return "Unknown";
  }

  private void setDataType(String value) {
    int type = Integer.parseInt(value);
    int pixelType = FormatTools.INT8;
    boolean littleEndian = true;
    switch (type) {
      case 2:
        pixelType = FormatTools.INT16;
        break;
      case 3:
        pixelType = FormatTools.INT32;
        break;
      case 4:
        pixelType = FormatTools.FLOAT;
        break;
      case 5:
        pixelType = FormatTools.FLOAT;
        littleEndian = false;
        break;
      case 6:
        pixelType = FormatTools.INT16;
        littleEndian = false;
        break;
      case 7:
        pixelType = FormatTools.INT32;
        littleEndian = false;
        break;
    }

    for (int i=0; i<core.size(); i++) {
      CoreMetadata ms = core.get(i);
      ms.pixelType = pixelType;
      ms.littleEndian = littleEndian;
    }
  }

}
