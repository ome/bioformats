/*
 * #%L
 * The OME Data Model specification
 * %%
 * Copyright (C) 2003 - 2015 Open Microscopy Environment:
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
/*
 * unit.XMLMockFactory
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2014 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package ome.specification;

//Java imports
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Third-party libraries

//Application-internal dependencies
import ome.xml.model.AffineTransform;
import ome.xml.model.Arc;
import ome.xml.model.BinData;
import ome.xml.model.Channel;
import ome.xml.model.Annotation;
import ome.xml.model.BooleanAnnotation;
import ome.xml.model.BinaryFile;
import ome.xml.model.CommentAnnotation;
import ome.xml.model.Dataset;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Dichroic;
import ome.xml.model.Ellipse;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.Filament;
import ome.xml.model.FileAnnotation;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Instrument;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.Line;
import ome.xml.model.LongAnnotation;
import ome.xml.model.Mask;
import ome.xml.model.MicrobeamManipulation;
import ome.xml.model.Microscope;
import ome.xml.model.OMEModelObject;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.OME;
import ome.xml.model.Pixels;
import ome.xml.model.Plane;
import ome.xml.model.Plate;
import ome.xml.model.PlateAcquisition;
import ome.xml.model.Point;
import ome.xml.model.Polyline;
import ome.xml.model.Project;
import ome.xml.model.Reagent;
import ome.xml.model.Rectangle;
import ome.xml.model.Screen;
import ome.xml.model.Shape;
import ome.xml.model.StageLabel;
import ome.xml.model.StructuredAnnotations;
import ome.xml.model.Union;
import ome.xml.model.TagAnnotation;
import ome.xml.model.TermAnnotation;
import ome.xml.model.TransmittanceRange;
import ome.xml.model.ROI;
import ome.xml.model.Well;
import ome.xml.model.WellSample;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.Compression;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.ExperimentType;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.enums.Medium;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.MicrobeamManipulationType;
import ome.xml.model.enums.MicroscopeType;
import ome.xml.model.enums.NamingConvention;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.NonNegativeLong;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

/**
 * Creates XML objects for the 2010-06 schema.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Chris Allan &nbsp;&nbsp;&nbsp;&nbsp;
 * Chris Allan <callan at blackcat dot ca>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
public class XMLMockObjects
{

  /** The default color. */
  public static final Color DEFAULT_COLOR = new Color(100, 150, 200, 255);

  /** The default power of a light source. */
  public static final Double LIGHTSOURCE_POWER = 200.0;

  /** The default model of a component of a microscope. */
  public static final String COMPONENT_MODEL = "Model";

  /** The default manufacturer of a component of a microscope. */
  public static final String COMPONENT_MANUFACTURER = "Manufacturer";

  /** The default serial number of a component of a microscope. */
  public static final String COMPONENT_SERIAL_NUMBER = "0123456789";

  /** The default lot number of a component of a microscope. */
  public static final String COMPONENT_LOT_NUMBER = "9876543210";

  /** The default type of a laser. */
  public static final LaserType LASER_TYPE = LaserType.DYE;

  /** The default type of an arc. */
  public static final ArcType ARC_TYPE = ArcType.HGXE;

  /** The default type of a filament. */
  public static final FilamentType FILAMENT_TYPE = FilamentType.HALOGEN;

  /** The default type of a detector. */
  public static final DetectorType DETECTOR_TYPE = DetectorType.CCD;

  /** The default objective's correction. */
  public static final Correction CORRECTION = Correction.UV;

  /** The default objective's immersion. */
  public static final Immersion IMMERSION = Immersion.OIL;

  /** The default objective's immersion. */
  public static final FilterType FILTER_TYPE = FilterType.LONGPASS;

  /** The default type of a microscope. */
  public static final MicroscopeType MICROSCOPE_TYPE = MicroscopeType.UPRIGHT;

  /** The default type of a microscope. */
  public static final ExperimentType EXPERIMENT_TYPE = ExperimentType.FISH;

  /** The default type of a microbeam manipulation. */
  public static final MicrobeamManipulationType
    MICROBEAM_MANIPULATION_TYPE = MicrobeamManipulationType.FLIP;

  /** The default binning value. */
  public static final Binning BINNING = Binning.TWOXTWO;

  /** The default medium for the objective. */
  public static final Medium MEDIUM = Medium.AIR;

  /** The default number of pixels along the X-axis. */
  public static final Integer SIZE_X = 24;

  /** The default number of pixels along the Y-axis. */
  public static final Integer SIZE_Y = 24;

  /** The default number of z-sections. */
  public static final Integer SIZE_Z = 1;

  /** The default number of channels. */
  public static final Integer SIZE_C = 1;

  /** The default number of time-points. */
  public static final Integer SIZE_T = 1;

  /** The number of bytes per pixels. */
  public static final Integer BYTES_PER_PIXEL = 2;

  /** The default number of rows for a plate. */
  public static final int    ROWS = 16;

  /** The default number of columns for a plate. */
  public static final int    COLUMNS = 24;

  /** The default number of fields for a well. */
  public static final int    FIELDS = 3;

  /** The light sources to handle. */
  public static final String[] LIGHT_SOURCES = {Laser.class.getName(),
    Arc.class.getName(), Filament.class.getName(),
    LightEmittingDiode.class.getName(), Laser.class.getName()};

  /**
   * The shapes to handle.
   * Do not use Masks, as BinData is not appropriately supported.
   **/
  public static final String[] SHAPES = {Line.class.getName(),
    Point.class.getName(), Rectangle.class.getName(),
    Ellipse.class.getName(), Polyline.class.getName(),
    /*Mask.class.getName()*/};

  /** The supported types of annotations. */
  public static final String[] ANNOTATIONS = {
    BooleanAnnotation.class.getName(), CommentAnnotation.class.getName(),
    LongAnnotation.class.getName(), TermAnnotation.class.getName(),
    TagAnnotation.class.getName() };

  /** The default naming convention for rows. */
  public static final NamingConvention ROW_NAMING_CONVENTION =
    NamingConvention.LETTER;

  /** The default naming convention for columns. */
  public static final NamingConvention COLUMN_NAMING_CONVENTION =
    NamingConvention.NUMBER;

  /** The default dimension order. */
  public static final DimensionOrder DIMENSION_ORDER = DimensionOrder.XYZCT;

  /** The default pixels type. */
  public static final PixelType PIXEL_TYPE = PixelType.UINT16;

  /** The number of detectors created. */
  public static final int NUMBER_OF_DECTECTORS = 1;

  /** The number of objectives created. */
  public static final int NUMBER_OF_OBJECTIVES = 1;

  /** The number of filters created. */
  public static final int NUMBER_OF_FILTERS = 2;

  /** The number of dichroics created. */
  public static final int NUMBER_OF_DICHROICS = 1;

  /** Points used to create Polyline and Polygon shape. */
  public static final String POINTS = "0,0 10,10";

  /** The default time. */
  public static final String TIME = "2006-05-04T18:13:51.0Z";

  /** The default cut-in. */
  public static final int CUT_IN = 200;

  /** The default cut-out. */
  public static final int CUT_OUT = 300;

  /** Root of the file. */
  protected OME ome;

  /** The instrument used for the metadata. */
  protected Instrument instrument;

  /** Creates and populates the instrument. */
  private void populateInstrument()
  {
    if (instrument != null) return;
    instrument = createInstrument(true);
    ome.addInstrument(instrument);
  }

  /**
   * Creates a detector.
   *
   * @param index The index of the detector in the file.
   * @return See above.
   */
  public Detector createDetector(int index)
  {
    Detector detector = new Detector();
    detector.setID("Detector:"+index);
    detector.setModel(COMPONENT_MODEL);
    detector.setManufacturer(COMPONENT_MANUFACTURER);
    detector.setSerialNumber(COMPONENT_SERIAL_NUMBER);
    detector.setLotNumber(COMPONENT_LOT_NUMBER);
    detector.setAmplificationGain(0.0);
    detector.setGain(1.0);
    return detector;
  }

  /**
   * Creates a filter set.
   *
   * @param index The index of the filter set in the file.
   * @return See above.
   */
  public FilterSet createFilterSet(int index)
  {
    FilterSet set = new FilterSet();
    set.setID("FilterSet:"+index);
    set.setModel(COMPONENT_MODEL);
    set.setManufacturer(COMPONENT_MANUFACTURER);
    set.setSerialNumber(COMPONENT_SERIAL_NUMBER);
    set.setLotNumber(COMPONENT_LOT_NUMBER);
    return set;
  }

  /**
   * Creates a microscope.
   *
   * @return See above.
   */
  public Microscope createMicroscope()
  {
    Microscope microscope = new Microscope();
    microscope.setManufacturer(COMPONENT_MANUFACTURER);
    microscope.setModel(COMPONENT_MODEL);
    microscope.setSerialNumber(COMPONENT_SERIAL_NUMBER);
    microscope.setLotNumber(COMPONENT_LOT_NUMBER);
    microscope.setType(MICROSCOPE_TYPE);
    return microscope;
  }

  /**
   * Creates a dichroic.
   *
   * @param index The index of the dichroic in the file.
   * @return See above.
   */
  public Dichroic createDichroic(int index)
  {
    Dichroic dichroic = new Dichroic();
    dichroic.setID("Dichroic:"+index);
    dichroic.setModel(COMPONENT_MODEL);
    dichroic.setManufacturer(COMPONENT_MANUFACTURER);
    dichroic.setLotNumber(COMPONENT_LOT_NUMBER);
    dichroic.setSerialNumber(COMPONENT_SERIAL_NUMBER);
    return dichroic;
  }

  /**
   * Creates an objective.
   *
   * @param index The index of the objective in the file.
   * @return See above.
   */
  public Objective createObjective(int index)
  {
    Objective objective = new Objective();
    objective.setID("Objective:"+index);
    objective.setModel(COMPONENT_MODEL);
    objective.setManufacturer(COMPONENT_MANUFACTURER);
    objective.setSerialNumber(COMPONENT_SERIAL_NUMBER);
    objective.setLotNumber(COMPONENT_LOT_NUMBER);
    objective.setCalibratedMagnification(1.0);
    objective.setCorrection(CORRECTION);
    objective.setImmersion(IMMERSION);
    objective.setIris(true);
    objective.setLensNA(0.5);
    objective.setNominalMagnification(1.5);
    objective.setWorkingDistance(1.0);
    return objective;
  }

  /**
   * Creates a filter.
   *
   * @param index The index of the objective in the file.
   * @param cutIn The cut in value.
     * @param cutOut The cut out value.
   * @return See above.
   */
  public Filter createFilter(int index, int cutIn, int cutOut)
  {
    Filter filter = new Filter();
    filter.setID("Filter:"+index);
    filter.setModel(COMPONENT_MODEL);
    filter.setManufacturer(COMPONENT_MANUFACTURER);
    filter.setLotNumber(COMPONENT_LOT_NUMBER);
    filter.setSerialNumber(COMPONENT_SERIAL_NUMBER);
    filter.setType(FILTER_TYPE);

    TransmittanceRange transmittance = new TransmittanceRange();
    transmittance.setCutIn(new PositiveInteger(cutIn));
    transmittance.setCutOut(new PositiveInteger(cutOut));
    transmittance.setCutInTolerance(new NonNegativeInteger(1));
    transmittance.setCutOutTolerance(new NonNegativeInteger(1));
    filter.setTransmittanceRange(transmittance);
    return filter;
  }

  /**
   * Creates a light source of the specified type.
   *
   * @param type The type of light source to create.
   * @param index The index of the light source in the file.
   * @return See above.
   */
  public LightSource createLightSource(String type, int index)
  {
    if (Laser.class.getName().equals(type)) {
      Laser laser = new Laser();
      laser.setID("LightSource:"+index);
      laser.setModel(COMPONENT_MODEL);
      laser.setManufacturer(COMPONENT_MANUFACTURER);
      laser.setSerialNumber(COMPONENT_SERIAL_NUMBER);
      laser.setLotNumber(COMPONENT_LOT_NUMBER);
      laser.setPower(LIGHTSOURCE_POWER);
      laser.setType(LASER_TYPE);
      return laser;
    } else if (Arc.class.getName().equals(type)) {
      Arc arc = new Arc();
      arc.setID("LightSource:"+index);
      arc.setManufacturer(COMPONENT_MANUFACTURER);
      arc.setSerialNumber(COMPONENT_SERIAL_NUMBER);
      arc.setLotNumber(COMPONENT_LOT_NUMBER);
      arc.setModel(COMPONENT_MODEL);
      arc.setPower(LIGHTSOURCE_POWER);
      arc.setType(ARC_TYPE);
      return arc;
    } else if (Filament.class.getName().equals(type)) {
      Filament filament = new Filament();
      filament.setID("LightSource:"+index);
      filament.setManufacturer(COMPONENT_MANUFACTURER);
      filament.setSerialNumber(COMPONENT_SERIAL_NUMBER);
      filament.setLotNumber(COMPONENT_LOT_NUMBER);
      filament.setModel(COMPONENT_MODEL);
      filament.setPower(LIGHTSOURCE_POWER);
      filament.setType(FILAMENT_TYPE);
      return filament;
    } else if (LightEmittingDiode.class.getName().equals(type)) {
      LightEmittingDiode light = new LightEmittingDiode();
      light.setID("LightSource:"+index);
      light.setManufacturer(COMPONENT_MANUFACTURER);
      light.setSerialNumber(COMPONENT_SERIAL_NUMBER);
      light.setLotNumber(COMPONENT_LOT_NUMBER);
      light.setModel(COMPONENT_MODEL);
      light.setPower(LIGHTSOURCE_POWER);
      return light;
    }
    return null;
  }

  /**
   * Creates a new object.
   *
   * @param sizeX The number of pixels along the X-axis.
   * @param sizeY The number of pixels along the Y-axis.
   * @param bpp   The number of bytes per pixels.
   * @return See above.
   */
  public BinData createBinData(int sizeX, int sizeY, int bpp)
  {
    BinData data = new BinData();
    data.setBigEndian(false);
    data.setCompression(Compression.NONE);
    data.setLength(new NonNegativeLong((long) (sizeX*sizeY*bpp)));
    return data;
  }

  /**
   * Creates a light path.
   *
   * @return See above.
   */
  public LightPath createLightPath()
  {
    LightPath lp = new LightPath();
    if (NUMBER_OF_DICHROICS > 0) {
      lp.linkDichroic(instrument.getDichroic(0));
    }
    if (NUMBER_OF_FILTERS == 1) {
      lp.linkEmissionFilter(instrument.getFilter(0));
    }
    else if (NUMBER_OF_FILTERS >= 2) {
      lp.linkEmissionFilter(instrument.getFilter(0));
      lp.linkExcitationFilter(instrument.getFilter(1));
    }
    return lp;
  }

  /**
   * Creates a imaging environment.
   *
   * @return See above.
   */
  public ImagingEnvironment createImageEnvironment()
  {
    ImagingEnvironment env = new ImagingEnvironment();
    env.setAirPressure(1.0);
    env.setCO2Percent(new PercentFraction(1.0f));
    env.setHumidity(new PercentFraction(1.0f));
    env.setTemperature(1.0);
    return env;
  }

  /**
   * Creates a imaging environment.
   *
   * @param index The index of the environment in the file.
   * @return See above.
   */
  public StageLabel createStageLabel()
  {
    StageLabel label = new StageLabel();
    label.setName("StageLabel");
    label.setX(1.0);
    label.setY(1.0);
    label.setZ(1.0);
    return label;
  }

  /**
   * Creates a light source settings.
   *
   * @param ref Reference to the light source.
   * @return See above.
   */
  public LightSourceSettings createLightSourceSettings(int ref)
  {
    if (instrument == null) populateInstrument();
    LightSourceSettings settings = new LightSourceSettings();
    settings.setID("LightSource:"+ref);
    settings.setAttenuation(new PercentFraction(1.0f));
    settings.setWavelength(new PositiveInteger(200));
    settings.setLightSource(instrument.copyLightSourceList().get(0));
    return settings;
  }

  /**
   * Creates a microbeam manipulation.
   *
   * @param index The index in the file.
   * @return See above.
   */
  public MicrobeamManipulation createMicrobeamManipulation(int index)
  {
    LightSourceSettings lss = createLightSourceSettings(4);
    MicrobeamManipulation mm = new MicrobeamManipulation();
    mm.setID("MicrobeamManipulation:"+index);
    mm.setType(MICROBEAM_MANIPULATION_TYPE);
    mm.setDescription("Manipulation #" + index);

    ROI roi = createROI(index, 0, 0, 0);
    ome.addROI(roi);
    mm.linkROI(roi);

    Experimenter experimenter = createExperimenter(index);
    ome.addExperimenter(experimenter);
    mm.linkExperimenter(experimenter);

    lss.setMicrobeamManipulation(mm);
    mm.addLightSourceSettings(lss);
    return mm;
  }

  /**
   * Creates an experimenter.
   *
   * @param index The index of the Experimenter.
   * @return See above.
   */
  public Experimenter createExperimenter(int index)
  {
    Experimenter experimenter = new Experimenter();
    experimenter.setID("Experimenter:" + index);

    return experimenter;
  }

  /**
   * Creates an experiment.
   *
   * @param index The index in the file.
   * @return See above.
   */
  public Experiment createExperiment(int index)
  {
    Experiment exp = new Experiment();
    exp.setDescription("Experiment");
    exp.setType(ExperimentType.PHOTOBLEACHING);
    exp.setID("Experiment:"+index);
    return exp;
  }

  /**
   * Creates an experiment with a microbeam.
   *
   * @param index The index in the file.
   * @return See above.
   */
  public Experiment createExperimentWithMicrobeam(int index)
  {
    Experiment exp = new Experiment();
    exp.setDescription("Experiment");
    exp.setType(ExperimentType.PHOTOBLEACHING);
    exp.setID("Experiment:"+index);
    MicrobeamManipulation mm = createMicrobeamManipulation(index);
    exp.addMicrobeamManipulation(mm);
    return exp;
  }

  /**
   * Creates a detector settings.
   *
   * @param ref Reference to the detector.
   * @return See above.
   */
  public DetectorSettings createDetectorSettings(int ref)
  {
    DetectorSettings settings = new DetectorSettings();
    settings.setID("Detector:"+ref);
    settings.setBinning(BINNING);
    settings.setGain(1.0);
    settings.setOffset(1.0);
    settings.setReadOutRate(1.0);
    settings.setVoltage(1.0);
    return settings;
  }

  /**
   * Creates an objective settings.
   *
   * @param ref Reference to the objective.
   * @return See above.
   */
  public ObjectiveSettings createObjectiveSettings(int ref)
  {
    ObjectiveSettings settings = new ObjectiveSettings();
    settings.setID("Objective:"+ref);
    settings.setMedium(MEDIUM);
    settings.setCorrectionCollar(1.0);
    settings.setRefractiveIndex(1.0);
    return settings;
  }

  /**
   * Creates a binary file.
   *
   * @return See above.
   */
  public BinaryFile createBinaryFile()
  {
    BinaryFile bf = new BinaryFile();
    bf.setBinData(createBinData(SIZE_X, SIZE_Y, BYTES_PER_PIXEL));
    return bf;
  }

  /**
   * Creates the specified type of shape.
   *
   * @param index The index of the shape in the file.
   * @param type The type of shape to create.
   * @param z    The selected z-section.
   * @param c    The selected channel.
   * @param t    The selected time-point.
   * @return See above.
   */
  public Shape createShape(int index, String type, int z, int c, int t)
  {
    Shape shape = null;
    if (Line.class.getName().equals(type)) {
      Line line = new Line();
      line.setX1(0.0);
      line.setY1(0.0);
      line.setX2(1.0);
      line.setY2(1.0);
      shape = line;
    } else if (Rectangle.class.getName().equals(type)) {
      Rectangle r = new Rectangle();
      r.setX(0.0);
      r.setY(0.0);
      r.setWidth(10.0);
      r.setHeight(10.0);
      shape = r;
    } else if (Ellipse.class.getName().equals(type)) {
      Ellipse e = new Ellipse();
      e.setRadiusX(1.0);
      e.setRadiusY(1.0);
      e.setY(2.0);
      e.setX(2.0);
      shape = e;
    } else if (Point.class.getName().equals(type)) {
      Point p = new Point();
      p.setY(2.0);
      p.setX(2.0);
      shape = p;
    } else if (Polyline.class.getName().equals(type)) {
      Polyline pl = new Polyline();
      pl.setPoints(POINTS);
      shape = pl;
    } else if (Mask.class.getName().equals(type)) {
      Mask m = new Mask();
      m.setX(0.0);
      m.setY(0.0);
      m.setWidth(new Double(SIZE_X));
      m.setHeight(new Double(SIZE_Y));
      shape = m;
    }
    if (shape != null) {
      shape.setID("Shape:"+index);
      shape.setTheC(new NonNegativeInteger(c));
      shape.setTheZ(new NonNegativeInteger(z));
      shape.setTheT(new NonNegativeInteger(t));
      //shape.setTransform(createTransform());
      shape.setFillColor(new ome.xml.model.primitives.Color(100));
      shape.setStrokeColor(new ome.xml.model.primitives.Color(100));
    }
    return shape;
  }

  private AffineTransform createTransform()
  {
    AffineTransform at = new AffineTransform();

    return at;
  }

  /**
   * Creates an ROI.
   *
   * @param index The index of the ROI in the file.
   * @param z The selected z-section.
   * @param c The selected channel.
   * @param t The selected time-point.
   */
  public ROI createROI(int index, int z, int c, int t)
  {
    ROI roi = new ROI();
    roi.setName("ROI name:"+index);
    roi.setID("ROI:"+index);
    int n = SHAPES.length;
    int j = index;
    if (index > 0) j += n;
    Union union = new Union();
    for (int i = 0; i < n; i++) {
      j += i;
      Shape shape = createShape(j, SHAPES[i], z, c, t);
      shape.setID("Shape:" + index + ":" + j);
      union.addShape(shape);
    }
    roi.setUnion(union);
    return roi;
  }

  /** Creates a new instance. */
  public XMLMockObjects()
  {
    ome = new OME();
  }

  /**
   * Returns the root of the XML file.
   *
   * @return See above.
   */
  public OME getRoot() { return ome; }

  /**
   * Creates a project.
   *
   * @param index The index of the project.
   * @return See above.
   */
  public Project createProject(int index)
  {
    Project project = new Project();
    project.setID("Project:"+index);
    project.setName("Project Name "+index);
    project.setDescription("Project Description "+index);
    return project;
  }

  /**
   * Creates a dataset.
   *
   * @param index The index of the dataset.
   * @return See above.
   */
  public Dataset createDataset(int index)
  {
    Dataset dataset = new Dataset();
    dataset.setID("Dataset:"+index);
    dataset.setName("Dataset Name "+index);
    dataset.setDescription("Dataset Description "+index);
    return dataset;
  }

  /**
   * Creates a screen.
   *
   * @param index The index of the screen.
   * @return See above.
   */
  public Screen createScreen(int index)
  {
    Screen screen = new Screen();
    screen.setID("Screen:"+index);
    screen.setName("Screen Name "+index);
    screen.setDescription("Screen Description "+index);
    return screen;
  }

  /**
   * Creates a basic plate.
   *
   * @param index The index of the plate.
   * @return See above.
   */
  public Plate createBasicPlate(int index)
  {
    Plate plate = new Plate();
    plate.setID("Plate:"+index);
    plate.setName("Plate Name "+index);
    plate.setDescription("Plate Description "+index);
    return plate;
  }

  /**
   * Creates a default plate
   *
   * @param numberOfPlates The total number of plates.
   * @param index The index of the plate.
   * @param numberOfPlateAcquisition  The number of plate acquisition to add.
   * @return See above.
   */
  public Plate createPlate(int numberOfPlates, int index,
      int numberOfPlateAcquisition)
  {
    return createPlate(numberOfPlates, index, ROWS, COLUMNS, FIELDS,
        numberOfPlateAcquisition);
  }

  /**
   * Creates a populated plate with images.
   *
   * @param numberOfPlates The total number of plates.
   * @param index The index of the plate.
   * @param rows  The number of rows.
   * @param columns The number of columns.
   * @param fields  The number of fields.
   * @param numberOfPlateAcquisition  The number of plate acquisition to add.
   * @return See above.
   */
  public Plate createPlate(int numberOfPlates,
      int index, int rows, int columns, int fields,
      int numberOfPlateAcquisition)
  {
    // ticket:3102
    Experiment exp = createExperimentWithMicrobeam(index);
    ome.addExperiment(exp);

    if (numberOfPlateAcquisition < 0) {
      numberOfPlateAcquisition = 0;
    }
    Plate plate = new Plate();
    plate.setID("Plate:"+index);
    plate.setName("Plate Name "+index);
    plate.setDescription("Plate Description "+index);
    plate.setExternalIdentifier("External Identifier");
    plate.setRows(new PositiveInteger(rows));
    plate.setColumns(new PositiveInteger(columns));
    plate.setRowNamingConvention(ROW_NAMING_CONVENTION);
    plate.setColumnNamingConvention(COLUMN_NAMING_CONVENTION);
    plate.setWellOriginX(0.0);
    plate.setWellOriginY(1.0);
    plate.setStatus("Plate status");
    PlateAcquisition pa = null;
    List<PlateAcquisition> pas = new ArrayList<PlateAcquisition>();
    int v;
    if (numberOfPlateAcquisition > 0) {
      for (int i = 0; i < numberOfPlateAcquisition; i++) {
        pa = new PlateAcquisition();
        v = i+index*numberOfPlates;
        pa.setID("PlateAcquisition:"+v);
        pa.setName("PlateAcquisition Name "+v);
        pa.setDescription("PlateAcquisition Description "+v);
        pa.setEndTime(new Timestamp(TIME));
        pa.setStartTime(new Timestamp(TIME));
        plate.addPlateAcquisition(pa);
        pas.add(pa);
      }
    }
    //now populate the plate
    Well well;
    WellSample sample;
    Image image;
    int i = index*rows*columns*fields*numberOfPlateAcquisition;
    Iterator<PlateAcquisition> k;
    int kk = 0;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        well = new Well();
        well.setID(String.format("Well:%d_%d_%d", row, column, index));
        well.setRow(new NonNegativeInteger(row));
        well.setColumn(new NonNegativeInteger(column));
        well.setType("Transfection: done");
        well.setExternalDescription("External Description");
        well.setExternalIdentifier("External Identifier");
        well.setColor(new ome.xml.model.primitives.Color(255));
        if (pas.size() == 0) {
          for (int field = 0; field < fields; field++) {
            sample = new WellSample();
            sample.setPositionX(0.0);
            sample.setPositionY(1.0);
            sample.setTimepoint(new Timestamp(TIME));
            sample.setID(String.format("WellSample:%d_%d_%d_%d",
                index, row, column, field));
            sample.setIndex(new NonNegativeInteger(i));
            //create an image. and register it
            image = createImageWithExperiment(i, true, exp);
            //image = createImage(i, true);
            ome.addImage(image);
            sample.linkImage(image);
            well.addWellSample(sample);
            i++;
          }
        } else {
          k = pas.iterator();
          kk = 0;
          while (k.hasNext()) {
            pa = k.next();
            for (int field = 0; field < fields; field++) {
              v = kk+index*numberOfPlates;
              sample = new WellSample();
              sample.setPositionX(0.0);
              sample.setPositionY(1.0);
              sample.setTimepoint(new Timestamp(TIME));
              sample.setID(String.format("WellSample:%d_%d_%d_%d_%d",
                  index, row, column, field, v));
              sample.setIndex(new NonNegativeInteger(i));
              //create an image. and register it
              //image = createImage(i, true);
              image = createImageWithExperiment(i, true, exp);
              ome.addImage(image);
              sample.linkImage(image);
              pa.linkWellSample(sample);
              well.addWellSample(sample);
              i++;
            }
            kk++;
          }
        }

        plate.addWell(well);
      }
    }
    return plate;
  }

  /**
   * Creates a plane information object.
   *
   * @param z The z-section.
   * @param c The channel.
   * @param t The time-point.
   * @return See above.
   */
  public Plane createPlane(int z, int c, int t)
  {
    Plane plane = new Plane();
    plane.setDeltaT(0.1);
    plane.setExposureTime(10.0);
    plane.setPositionX(1.0);
    plane.setPositionY(1.0);
    plane.setPositionZ(1.0);
    plane.setTheZ(new NonNegativeInteger(z));
    plane.setTheC(new NonNegativeInteger(c));
    plane.setTheT(new NonNegativeInteger(z));
    plane.setHashSHA1("1234567890ABCDEF1234567890ABCDEF12345678");
    return plane;
  }

  /**
   * Creates a new image.
   *
   * @param index The identifier of the image.
   * @param metadata Pass <code>true</code> to create channel with acquisition
   *                  metadata, <code>false</code> otherwise.
   * @return See above.
   */
  public Image createImage(int index, boolean metadata)
  {
    if (metadata && instrument == null) {
      populateInstrument();
    }
    Image image = new Image();
    image.setID("Image:"+index);
    image.setName("Image Name "+index);
    image.setDescription("Image Description "+index);
    if (metadata) {
      image.setImagingEnvironment(createImageEnvironment());
      image.setStageLabel(createStageLabel());
    }
    Pixels pixels = new Pixels();
    pixels.setID("Pixels:"+index);
    pixels.setSizeX(new PositiveInteger(SIZE_X));
    pixels.setSizeY(new PositiveInteger(SIZE_Y));
    pixels.setSizeZ(new PositiveInteger(SIZE_Z));
    pixels.setSizeC(new PositiveInteger(SIZE_C));
    pixels.setSizeT(new PositiveInteger(SIZE_T));
    pixels.setDimensionOrder(DIMENSION_ORDER);
    pixels.setType(PIXEL_TYPE);
    BinData data;
    for (int i = 0; i < SIZE_Z*SIZE_C*SIZE_T; i++) {
      data = createBinData(SIZE_X, SIZE_Y, BYTES_PER_PIXEL);
      pixels.addBinData(data);
    }
    for (int z = 0; z < SIZE_Z; z++) {
      for (int t = 0; t < SIZE_T; t++) {
        for (int c = 0; c < SIZE_C; c++) {
          pixels.addPlane(createPlane(z, c, t));
        }
      }
    }
    Channel channel;
    int j = 0;
    int n = LIGHT_SOURCES.length-1;
    DetectorSettings ds = createDetectorSettings(0);
    for (int i = 0; i < SIZE_C; i++) {
      channel = createChannel(i);
      channel.setID("Channel:" + index + ":" + i);
      if (metadata) {
        if (j == n) j = 0;
        channel.setLightSourceSettings(createLightSourceSettings(j));
        channel.setLightPath(createLightPath());
        channel.setDetectorSettings(ds);
        //link the channel to the OTF
        //if (otf != null) otf.linkChannel(channel);
        j++;
      }
      pixels.addChannel(channel);
    }

    image.setPixels(pixels);
    return image;
  }

  /**
   * Creates a channel.
   *
   * @param index The index in the file.
   * @return See above.
   */
  public Channel createChannel(int index)
  {
    Channel channel = new Channel();
    channel.setID("Channel:"+index);
    channel.setAcquisitionMode(AcquisitionMode.FLUORESCENCELIFETIME);
    int argb = DEFAULT_COLOR.getRGB();
    int rgba = (argb << 8) | (argb >>> (32-8));
    channel.setColor(new ome.xml.model.primitives.Color(rgba));
    channel.setName("Name");
    channel.setIlluminationType(IlluminationType.OBLIQUE);
    channel.setPinholeSize(0.5);
    channel.setContrastMethod(ContrastMethod.BRIGHTFIELD);
    channel.setEmissionWavelength(new PositiveInteger(300));
    channel.setExcitationWavelength(new PositiveInteger(400));
    channel.setFluor("Fluor");
    channel.setNDFilter(1.0);
    channel.setPockelCellSetting(0);
    return channel;
  }

  /**
   * Creates a new image.
   *
   * @param index The identifier of the image.
   * @return See above.
   */
  public Image createImage(int index)
  {
    return createImage(index, false);
  }

  /**
   * Creates an instrument with filters, light sources etc.
   *
   * @param populate Pass <code>true</code> to populate the instrument,
   *                 <code>false</code> otherwise.
   * @return See above.
   */
  public Instrument createInstrument(boolean populate)
  {
    int index = 0;
    Instrument instrument = new Instrument();
    instrument.setID("Instrument:"+index);
    instrument.setMicroscope(createMicroscope());
    if (populate) {
      for (int i = 0; i < NUMBER_OF_OBJECTIVES; i++) {
        instrument.addObjective(createObjective(i));
      }
      for (int i = 0; i < NUMBER_OF_DECTECTORS; i++) {
        instrument.addDetector(createDetector(i));
      }
      instrument.addFilterSet(createFilterSet(index));
      for (int i = 0; i < NUMBER_OF_FILTERS; i++) {
        instrument.addFilter(createFilter(i, CUT_IN, CUT_OUT));
      }
      for (int i = 0; i < NUMBER_OF_DICHROICS; i++) {
        instrument.addDichroic(createDichroic(i));
      }
      for (int i = 0; i < LIGHT_SOURCES.length; i++) {
        instrument.addLightSource(createLightSource(LIGHT_SOURCES[i], i));
      }
    }
    return instrument;
  }

  /**
   * Creates a reagent.
   *
   * @param index The index in the file.
   * @return See above.
   */
  public Reagent createReagent(int index)
  {
    Reagent reagent = new Reagent();
    reagent.setID("Reagent:"+index);
    reagent.setDescription("Reagent Description");
    reagent.setName("Reagent Name");
    reagent.setReagentIdentifier("Reagent Identifier");
    return reagent;
  }

  //annotations
  /**
   * Create a comment annotation for the specified object.
   *
   * @param type The type of annotation to create.
   * @param object The object to link the annotation to.
   * @param index The index of the annotation.
   * @return See above.
   */
  public Annotation createAnnotation(String type, OMEModelObject object,
      int index)
  {
    Annotation annotation = null;
    if (object instanceof Image) {
      if (CommentAnnotation.class.getName().equals(type)) {
        CommentAnnotation c = new CommentAnnotation();
        c.setID("ImageCommentAnnotation:" + index);
        c.setValue("Image:"+index+" CommentAnnotation.");
        annotation = c;
      } else if (BooleanAnnotation.class.getName().equals(type)) {
        BooleanAnnotation b = new BooleanAnnotation();
        b.setID("ImageBooleanAnnotation:" + index);
        b.setValue(true);
        annotation = b;
      } else if (LongAnnotation.class.getName().equals(type)) {
        LongAnnotation l = new LongAnnotation();
        l.setID("ImageLongAnnotation:" + index);
        l.setValue(1L);
        annotation = l;
      } else if (TagAnnotation.class.getName().equals(type)) {
        TagAnnotation tag = new TagAnnotation();
        tag.setID("ImageTagAnnotation:" + index);
        tag.setValue("Image:"+index+" TagAnnotation.");
        annotation = tag;
      } else if (TermAnnotation.class.getName().equals(type)) {
        TermAnnotation term = new TermAnnotation();
        term.setID("ImageTermAnnotation:" + index);
        term.setValue("Image:"+index+" TermAnnotation.");
        annotation = term;
      } else if (FileAnnotation.class.getName().equals(type)) {
        FileAnnotation f = new FileAnnotation();
        f.setID("ImageFileAnnotation:" + index);
        f.setBinaryFile(createBinaryFile());
        annotation = f;
      }
      if (annotation != null) {
        ((Image) object).linkAnnotation(annotation);
      }
    } else if (object instanceof Plate) {
      if (CommentAnnotation.class.getName().equals(type)) {
        CommentAnnotation c = new CommentAnnotation();
        c.setID("PlateCommentAnnotation:" + index);
        c.setValue("Plate:"+index+" CommentAnnotation.");
        annotation = c;
      } else if (BooleanAnnotation.class.getName().equals(type)) {
        BooleanAnnotation b = new BooleanAnnotation();
        b.setID("PlateBooleanAnnotation:" + index);
        b.setValue(true);
        annotation = b;
      } else if (LongAnnotation.class.getName().equals(type)) {
        LongAnnotation l = new LongAnnotation();
        l.setID("PlateLongAnnotation:" + index);
        l.setValue(1L);
        annotation = l;
      } else if (TagAnnotation.class.getName().equals(type)) {
        TagAnnotation tag = new TagAnnotation();
        tag.setID("PlateTagAnnotation:" + index);
        tag.setValue("Plate:"+index+" TagAnnotation.");
        annotation = tag;
      } else if (TermAnnotation.class.getName().equals(type)) {
        TermAnnotation term = new TermAnnotation();
        term.setID("PlateTermAnnotation:" + index);
        term.setValue("Plate:"+index+" TermAnnotation.");
        annotation = term;
      } else if (FileAnnotation.class.getName().equals(type)) {
        FileAnnotation f = new FileAnnotation();
        f.setID("PlateFileAnnotation:" + index);
        f.setBinaryFile(createBinaryFile());
        annotation = f;
      }
      if (annotation != null) {
        ((Plate) object).linkAnnotation(annotation);
      }
    } else if (object instanceof Well) {
      if (CommentAnnotation.class.getName().equals(type)) {
        CommentAnnotation c = new CommentAnnotation();
        c.setID("WellCommentAnnotation:" + index);
        c.setValue("Well:"+index+" CommentAnnotation.");
        annotation = c;
      } else if (BooleanAnnotation.class.getName().equals(type)) {
        BooleanAnnotation b = new BooleanAnnotation();
        b.setID("WellBooleanAnnotation:" + index);
        b.setValue(true);
        annotation = b;
      } else if (LongAnnotation.class.getName().equals(type)) {
        LongAnnotation l = new LongAnnotation();
        l.setID("WellLongAnnotation:" + index);
        l.setValue(1L);
        annotation = l;
      } else if (TagAnnotation.class.getName().equals(type)) {
        TagAnnotation tag = new TagAnnotation();
        tag.setID("WellTagAnnotation:" + index);
        tag.setValue("Well:"+index+" TagAnnotation.");
        annotation = tag;
      } else if (TermAnnotation.class.getName().equals(type)) {
        TermAnnotation term = new TermAnnotation();
        term.setID("WellTermAnnotation:" + index);
        term.setValue("Well:"+index+" TermAnnotation.");
        annotation = term;
      } else if (FileAnnotation.class.getName().equals(type)) {
        FileAnnotation f = new FileAnnotation();
        f.setID("WellFileAnnotation:" + index);
        f.setBinaryFile(createBinaryFile());
        annotation = f;
      }
      if (annotation != null) {
        ((Well) object).linkAnnotation(annotation);
      }
    } else if (object instanceof WellSample) {
      if (CommentAnnotation.class.getName().equals(type)) {
        CommentAnnotation c = new CommentAnnotation();
        c.setID("WellSampleCommentAnnotation:" + index);
        c.setValue("WellSample:"+index+" CommentAnnotation.");
        annotation = c;
      } else if (BooleanAnnotation.class.getName().equals(type)) {
        BooleanAnnotation b = new BooleanAnnotation();
        b.setID("WellSampleBooleanAnnotation:" + index);
        b.setValue(true);
        annotation = b;
      } else if (LongAnnotation.class.getName().equals(type)) {
        LongAnnotation l = new LongAnnotation();
        l.setID("WellSampleLongAnnotation:" + index);
        l.setValue(1L);
        annotation = l;
      } else if (TagAnnotation.class.getName().equals(type)) {
        TagAnnotation tag = new TagAnnotation();
        tag.setID("WellSampleTagAnnotation:" + index);
        tag.setValue("WellSample:"+index+" TagAnnotation.");
        annotation = tag;
      } else if (TermAnnotation.class.getName().equals(type)) {
        TermAnnotation term = new TermAnnotation();
        term.setID("WellSampleTermAnnotation:" + index);
        term.setValue("WellSample:"+index+" TermAnnotation.");
        annotation = term;
      } else if (FileAnnotation.class.getName().equals(type)) {
        FileAnnotation f = new FileAnnotation();
        f.setID("WellSampleFileAnnotation:" + index);
        f.setBinaryFile(createBinaryFile());
        annotation = f;
      }
      if (annotation != null) {
        ((WellSample) object).linkAnnotation(annotation);
      }
    }
    return annotation;
  }

  //Collection of helper methods.

  /**
   * Creates and returns the root element. Creates an image w/o metadata.
   *
   * @return See above.
   */
  public OME createImage()
  {
    ome.addImage(createImage(0));
    return ome;
  }

  /**
   * Creates and returns the root element.
   *
   * @param metadata Pass <code>true</code> to create an image with metadata,
   *                 <code>false</code> w/o/
   * @return See above.
   */
  public OME createImage(boolean metadata)
  {
    ome.addImage(createImage(0, true));
    return ome;
  }

  /**
   * Creates and annotates an image.
   * The following types of annotations are added:
   * TagAnnotation, TermAnnotation, BooleanAnnotation, LongAnnotation,
   * CommentAnnotation.
   *
   * @return See above.
   */
  public OME createAnnotatedImage()
  {
    StructuredAnnotations annotations = new StructuredAnnotations();
    int index = 0;
    Image image = createImage(index);
    ome.addImage(image);

    annotations.addCommentAnnotation((CommentAnnotation) createAnnotation(
        CommentAnnotation.class.getName(), image, index));
    annotations.addBooleanAnnotation((BooleanAnnotation) createAnnotation(
        BooleanAnnotation.class.getName(), image, index));
    annotations.addLongAnnotation((LongAnnotation) createAnnotation(
        LongAnnotation.class.getName(), image, index));
    annotations.addTagAnnotation((TagAnnotation) createAnnotation(
        TagAnnotation.class.getName(), image, index));
    annotations.addTermAnnotation((TermAnnotation) createAnnotation(
        TermAnnotation.class.getName(), image, index));
    ome.setStructuredAnnotations(annotations);
    return ome;
  }

  /**
   * Creates an image with acquisition data.
   *
   * @return See above.
   */
  public OME createImageWithAcquisitionData()
  {
    populateInstrument();
    Image image = createImage(0, true);
    ObjectiveSettings settings = createObjectiveSettings(0);
    image.setObjectiveSettings(settings);

    //Add microbeam
    Experiment exp = createExperiment(0);
    ome.addExperiment(exp);
    MicrobeamManipulation mm = createMicrobeamManipulation(0);
    exp.addMicrobeamManipulation(mm);
    Pixels pixels = image.getPixels();
    image.linkExperiment(exp);
    image.linkInstrument(instrument);
    ome.addImage(image);
    return ome;
  }

  /**
   * Creates an image with a given experiment. The Image is not added to ome.
   *
   * @return See above.
   */
  public Image createImageWithExperiment(int index, boolean metadata,
    Experiment exp)
  {
    Image image = createImage(index, metadata);
    image.linkExperiment(exp);
    return image;
  }

  /**
   * Creates an image with ROI.
   *
   * @return See above.
   */
  public OME createImageWithROI()
  {
    int index = 0;
    Image image = createImage(index, false);
    ome.addImage(image);
    ROI roi;
    for (int i = 0; i < SIZE_C; i++) {
      roi = createROI(i, 0, i, 0);
      image.linkROI(roi);
      ome.addROI(roi);
    }
    return ome;
  }

  /**
   * Creates a plate with <code>1</code> row, <code>1</code> column
   * and <code>1</code>field.
   * The plate will have images with acquisition data but no plate acquisition
   * data if the passed value is <code>0</code> otherwise will have
   * <code>n</code> plate acquisitions.
   *
   * @param n The number of plate acquisition.
   * @return See above
   */
  public OME createPopulatedPlate(int n)
  {
    populateInstrument();
    ome.addPlate(createPlate(1, 0, 1, 1, 1, n));
    return ome;
  }

  /**
   * Creates a plate with <code>1</code> row, <code>1</code> column
   * and <code>fields</code>field.
   * The plate will have images with acquisition data but no plate acquisition
   * data if the passed value is <code>0</code> otherwise will have
   * <code>n</code> plate acquisitions.
   *
   * @param n The number of plate acquisition.
   * @param fields The number of fields.
   * @return See above
   */
  public OME createPopulatedPlate(int n, int fields)
  {
    if (fields < 1) fields = 1;
    populateInstrument();
    ome.addPlate(createPlate(1, 0, 1, 1, fields, n));
    return ome;
  }

  /**
   * Creates a screen with several plates.
   *
   * @param plates The number of plates to create.
   * @param rows   The number of rows for plate.
   * @param cols   The number of columns for plate.
   * @param fields The number of fields.
   * @param acqs   The number of plate acquisitions.
   * @return See above.
   */
  public OME createPopulatedScreen(int plates, int rows, int cols, int fields,
      int acqs)
  {
    Screen screen = createScreen(0);
    Plate plate;
    for (int p = 0; p < plates; p++) {
      plate = createPlate(plates, p, rows, cols, fields, acqs);
      screen.linkPlate(plate);
      ome.addPlate(plate);
    }
    ome.addScreen(screen);
    return ome;
  }

  /**
   * Creates one 2x2 plate of with a single well sample per well and one
   * plate acquisition.
   */
  public OME createPopulatedScreen()
  {
    return createPopulatedScreen(1, 2, 2, 2, 2); //1, 2, 2, 1, 1
  }

  /**
   * Creates a plate with <code>1</code> row, <code>1</code> column
   * and <code>1</code> field. This plate will be added to a screen
   * and the well linked to a reagent.
   *
   * @return See above
   */
  public OME createBasicPlateWithReagent()
  {
    populateInstrument();
    Plate plate = createPlate(1, 0, 1, 1, 1, 0);
    Reagent r = createReagent(0);
    plate.getWell(0).linkReagent(r);
    Screen screen = createScreen(0);
    screen.addReagent(r);
    screen.linkPlate(plate);
    ome.addPlate(plate);
    ome.addScreen(screen);
    return ome;
  }

  /**
   * Creates a plate with {@link #ROWS} rows, {@link #COLUMNS} columns
   * and {@link #FIELDS} field.
   * The plate will have images with acquisition data but no plate acquisition
   * data.
   *
   * @param n The number of plate acquisition.
   * @return See above
   */
  public OME createFullPopulatedPlate(int n)
  {
    populateInstrument();
    ome.addPlate(createPlate(1, 0, n));
    return ome;
  }

}
