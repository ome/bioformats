/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.utests;

import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Arc;
import ome.xml.model.BinaryFile;
import ome.xml.model.BooleanAnnotation;
import ome.xml.model.Channel;
import ome.xml.model.CommentAnnotation;
import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.DoubleAnnotation;
import ome.xml.model.External;
import ome.xml.model.Filament;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Image;
import ome.xml.model.Instrument;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.ListAnnotation;
import ome.xml.model.LongAnnotation;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.Pixels;
import ome.xml.model.Plate;
import ome.xml.model.ROI;
import ome.xml.model.Rectangle;
import ome.xml.model.StructuredAnnotations;
import ome.xml.model.TiffData;
import ome.xml.model.TimestampAnnotation;
import ome.xml.model.UUID;
import ome.xml.model.Union;
import ome.xml.model.Well;
import ome.xml.model.WellSample;
import ome.xml.model.XMLAnnotation;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/ObjectBasedOMEModelMock.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/ObjectBasedOMEModelMock.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ObjectBasedOMEModelMock implements OMEModelMock {

  private OMEXMLMetadataRoot ome;

  private StructuredAnnotations annotations;

  public ObjectBasedOMEModelMock() {
    ome = new OMEXMLMetadataRoot();
    annotations = new StructuredAnnotations();
    ome.addImage(makeImage());
    ome.addPlate(makePlate());
    ome.addInstrument(makeInstrument());
    ome.addROI(makeROI());
    ome.setStructuredAnnotations(annotations);
  }

  public OMEXMLMetadataRoot getRoot() {
    return ome;
  }

  private Image makeImage() {
    // Create <Image/>
    Image image = new Image();
    image.setID(InOut201004Test.IMAGE_ID);
    ListAnnotation listAnnotation = new ListAnnotation();
    listAnnotation.setID(InOut201004Test.IMAGE_LIST_ANNOTATION_ID);
    listAnnotation.setNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE);
    annotations.addListAnnotation(listAnnotation);
    BooleanAnnotation annotation = new BooleanAnnotation();
    annotation.setID(InOut201004Test.IMAGE_ANNOTATION_ID);
    annotation.setValue(InOut201004Test.IMAGE_ANNOTATION_VALUE);
    annotation.setNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE);
    listAnnotation.linkAnnotation(annotation);
    image.linkAnnotation(listAnnotation);
    annotations.addBooleanAnnotation(annotation);
    // Create <Pixels/>
    Pixels pixels = new Pixels();
    pixels.setID(InOut201004Test.PIXELS_ID);
    pixels.setSizeX(new PositiveInteger(InOut201004Test.SIZE_X));
    pixels.setSizeY(new PositiveInteger(InOut201004Test.SIZE_Y));
    pixels.setSizeZ(new PositiveInteger(InOut201004Test.SIZE_Z));
    pixels.setSizeC(new PositiveInteger(InOut201004Test.SIZE_C));
    pixels.setSizeT(new PositiveInteger(InOut201004Test.SIZE_T));
    pixels.setDimensionOrder(InOut201004Test.DIMENSION_ORDER);
    pixels.setType(InOut201004Test.PIXEL_TYPE);
    // Create <TiffData/>
    TiffData tiffData = new TiffData();
    // Create <UUID/>
    UUID uuid = new UUID();
    uuid.setValue(InOut201004Test.TIFF_DATA_UUID);
    tiffData.setUUID(uuid);
    pixels.addTiffData(tiffData);
    // Create <Channel/> under <Pixels/>
    for (int i = 0; i < InOut201004Test.SIZE_C; i++) {
      Channel channel = new Channel();
      channel.setID("Channel:" + i);
      if (i == 0) {
        XMLAnnotation channelAnnotation = new XMLAnnotation();
        channelAnnotation.setID(InOut201004Test.CHANNEL_ANNOTATION_ID);
        channelAnnotation.setValue(InOut201004Test.CHANNEL_ANNOTATION_VALUE);
        channelAnnotation.setNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE);
        channel.linkAnnotation(channelAnnotation);
        annotations.addXMLAnnotation(channelAnnotation);
      }
      pixels.addChannel(channel);
    }
    // create Annotation for Pixels
    DoubleAnnotation pixelsAnnotation = new DoubleAnnotation();
    pixelsAnnotation.setID(InOut201004Test.PIXELS_ANNOTATION_ID);
    pixelsAnnotation.setValue(InOut201004Test.PIXELS_ANNOTATION_VALUE);
    pixelsAnnotation.setNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE);
    pixels.linkAnnotation(pixelsAnnotation);
    annotations.addDoubleAnnotation(pixelsAnnotation);
    // Put <Pixels/> under <Image/>
    image.setPixels(pixels);
    return image;
  }

  private Instrument makeInstrument() {
    // Create <Instrument/>
    Instrument instrument = new Instrument();
    instrument.setID(InOut201004Test.INSTRUMENT_ID);
    // Create <Detector/> under <Instrument/>
    Detector detector = new Detector();
    detector.setID(InOut201004Test.DETECTOR_ID);
    detector.setModel(InOut201004Test.DETECTOR_MODEL);
    instrument.addDetector(detector);
    // Create <Laser/> under <Instrument/>
    Laser laser = new Laser();
    laser.setID(InOut201004Test.LIGHTSOURCE_LASER_ID);
    laser.setModel(InOut201004Test.LIGHTSOURCE_LASER_MODEL);
    laser.setType(InOut201004Test.LASER_TYPE);
    laser.setPower(InOut201004Test.LIGHTSOURCE_LASER_POWER);
    // with a <Pump/>
    Laser laserPump = new Laser();
    laserPump.setID(InOut201004Test.LIGHTSOURCE_PUMP_ID);
    laserPump.setModel(InOut201004Test.LIGHTSOURCE_PUMP_MODEL);
    laserPump.setType(InOut201004Test.LASER_TYPE);
    laserPump.setPower(InOut201004Test.LIGHTSOURCE_PUMP_POWER);
    
    laser.linkPump(laserPump);
    
    instrument.addLightSource(laser);
    instrument.addLightSource(laserPump);
    
    // Create <Arc/> under <Instrument/>
    Arc arc = new Arc();
    arc.setID(InOut201004Test.LIGHTSOURCE_ARC_ID);
    arc.setModel(InOut201004Test.LIGHTSOURCE_ARC_MODEL);
    arc.setType(InOut201004Test.ARC_TYPE);
    arc.setPower(InOut201004Test.LIGHTSOURCE_ARC_POWER);
    instrument.addLightSource(arc);
    
    // Create <Filament/> under <Instrument/>
    Filament filament = new Filament();
    filament.setID(InOut201004Test.LIGHTSOURCE_FILAMENT_ID);
    filament.setModel(InOut201004Test.LIGHTSOURCE_FILAMENT_MODEL);
    filament.setType(InOut201004Test.FILAMENT_TYPE);
    filament.setPower(InOut201004Test.LIGHTSOURCE_FILAMENT_POWER);
    instrument.addLightSource(filament);

    // Create <LightEmittingDiode/> under <Instrument/>
    LightEmittingDiode led = new LightEmittingDiode();
    led.setID(InOut201004Test.LIGHTSOURCE_LED_ID);
    led.setModel(InOut201004Test.LIGHTSOURCE_LED_MODEL);
    led.setPower(InOut201004Test.LIGHTSOURCE_LED_POWER);
    instrument.addLightSource(led);

    // Create <Dichroic/> under <Instrument/>
    Dichroic dichroic = new Dichroic();
    dichroic.setID(InOut201004Test.DICHROIC_ID);
    dichroic.setSerialNumber(InOut201004Test.DICHROIC_SN);
    // Create <FilterSet/> under <Dichroic/>
    FilterSet filterSet = new FilterSet();
    filterSet.setID(InOut201004Test.FILTERSET_ID);
    filterSet.setLotNumber(InOut201004Test.FILTERSET_LOT);
    filterSet.linkDichroic(dichroic);

    Filter emFilter = new Filter();
    Filter exFilter = new Filter();
    // Create <Objective/> under <Instrument/>
    Objective objective = new Objective();
    objective.setID(InOut201004Test.OBJECTIVE_ID);
    objective.setModel(InOut201004Test.OBJECTIVE_MODEL);

    emFilter.setID(InOut201004Test.EM_FILTER_ID);
    emFilter.setType(InOut201004Test.EM_FILTER_TYPE);
    exFilter.setID(InOut201004Test.EX_FILTER_ID);
    exFilter.setType(InOut201004Test.EX_FILTER_TYPE);

    instrument.addFilter(emFilter);
    instrument.addFilter(exFilter);
    instrument.addObjective(objective);

    filterSet.linkEmissionFilter(emFilter);
    filterSet.linkExcitationFilter(exFilter);
    filterSet.linkDichroic(dichroic);
    instrument.addFilterSet(filterSet);
    instrument.addDichroic(dichroic);

    // link Instrument to the first Image
    Image image = ome.getImage(0);
    image.linkInstrument(instrument);

    return instrument;
  }

  private Plate makePlate() {
    Plate plate = new Plate();
    plate.setID(InOut201004Test.PLATE_ID);
    plate.setRows(InOut201004Test.WELL_ROWS);
    plate.setColumns(InOut201004Test.WELL_COLS);
    plate.setRowNamingConvention(InOut201004Test.WELL_ROW);
    plate.setColumnNamingConvention(InOut201004Test.WELL_COL);

    TimestampAnnotation plateAnnotation = new TimestampAnnotation();
    plateAnnotation.setID(InOut201004Test.PLATE_ANNOTATION_ID);
    plateAnnotation.setValue(new Timestamp(InOut201004Test.PLATE_ANNOTATION_VALUE));
    plateAnnotation.setNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE);
    plate.linkAnnotation(plateAnnotation);
    annotations.addTimestampAnnotation(plateAnnotation);

    int wellSampleIndex = 0;
    for (int row=0; row<InOut201004Test.WELL_ROWS.getValue(); row++) {
      for (int col=0; col<InOut201004Test.WELL_COLS.getValue(); col++) {
        Well well = new Well();
        well.setID(String.format("Well:%d_%d", row, col));
        well.setRow(new NonNegativeInteger(row));
        well.setColumn(new NonNegativeInteger(col));

        if (row == 0 && col == 0) {
          LongAnnotation annotation = new LongAnnotation();
          annotation.setID(InOut201004Test.WELL_ANNOTATION_ID);
          annotation.setValue(InOut201004Test.WELL_ANNOTATION_VALUE);
          annotation.setNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE);
          well.linkAnnotation(annotation);
          annotations.addLongAnnotation(annotation);
        }

        WellSample sample = new WellSample();
        sample.setID(String.format("WellSample:%d_%d", row, col));
        sample.setIndex(new NonNegativeInteger(wellSampleIndex));
        sample.linkImage(ome.getImage(0));
        well.addWellSample(sample);
        plate.addWell(well);
        wellSampleIndex++;
      }
    }

    return plate;
  }

  private ROI makeROI() {
    ROI roi = new ROI();
    roi.setID(InOut201004Test.ROI_ID);

    CommentAnnotation roiAnnotation = new CommentAnnotation();
    roiAnnotation.setID(InOut201004Test.ROI_ANNOTATION_ID);
    roiAnnotation.setValue(InOut201004Test.ROI_ANNOTATION_VALUE);
    roiAnnotation.setNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE);
    roi.linkAnnotation(roiAnnotation);
    annotations.addCommentAnnotation(roiAnnotation);

    Union shapeUnion = new Union();
    Rectangle rect = new Rectangle();
    rect.setID(InOut201004Test.SHAPE_ID);
    rect.setX(InOut201004Test.RECTANGLE_X);
    rect.setY(InOut201004Test.RECTANGLE_Y);
    rect.setWidth(InOut201004Test.RECTANGLE_WIDTH);
    rect.setHeight(InOut201004Test.RECTANGLE_HEIGHT);

    shapeUnion.addShape(rect);
    roi.setUnion(shapeUnion);

    return roi;
  }
}
