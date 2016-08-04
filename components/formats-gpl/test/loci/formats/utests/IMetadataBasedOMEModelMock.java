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

import loci.formats.ome.OMEXMLMetadataImpl;
import ome.xml.meta.MetadataRoot;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Power;
import ome.units.UNITS;

/**
 */
public class IMetadataBasedOMEModelMock implements OMEModelMock {

  private OMEXMLMetadataImpl store;

  public IMetadataBasedOMEModelMock() {
    store = new OMEXMLMetadataImpl();
    store.createRoot();
    makeImage();
    makeInstrument();
    makePlate();
    makeROI();
    store.resolveReferences();
  }

  @Override
  public MetadataRoot getRoot() {
    return store.getRoot();
  }

  private void makeImage() {
    // Create <Image/>
    store.setImageID(InOutCurrentTest.IMAGE_ID, 0);
    store.setListAnnotationID(InOutCurrentTest.IMAGE_LIST_ANNOTATION_ID, 0);
    store.setListAnnotationNamespace(
        InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setBooleanAnnotationID(InOutCurrentTest.IMAGE_ANNOTATION_ID, 0);
    store.setBooleanAnnotationNamespace(
        InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setListAnnotationAnnotationRef(
        InOutCurrentTest.IMAGE_ANNOTATION_ID, 0, 0);
    store.setBooleanAnnotationValue(InOutCurrentTest.IMAGE_ANNOTATION_VALUE, 0);
    store.setBooleanAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setBooleanAnnotationValue(InOutCurrentTest.IMAGE_ANNOTATION_VALUE, 0);
    store.setImageAnnotationRef(InOutCurrentTest.IMAGE_LIST_ANNOTATION_ID, 0, 0);
    // Create <Pixels/>
    store.setPixelsID(InOutCurrentTest.PIXELS_ID, 0);
    store.setPixelsSizeX(new PositiveInteger(InOutCurrentTest.SIZE_X), 0);
    store.setPixelsSizeY(new PositiveInteger(InOutCurrentTest.SIZE_Y), 0);
    store.setPixelsSizeZ(new PositiveInteger(InOutCurrentTest.SIZE_Z), 0);
    store.setPixelsSizeC(new PositiveInteger(InOutCurrentTest.SIZE_C), 0);
    store.setPixelsSizeT(new PositiveInteger(InOutCurrentTest.SIZE_T), 0);
    store.setPixelsDimensionOrder(InOutCurrentTest.DIMENSION_ORDER, 0);
    store.setPixelsType(InOutCurrentTest.PIXEL_TYPE, 0);
    store.setUUIDValue(InOutCurrentTest.TIFF_DATA_UUID, 0, 0);
    // Create <Channel/> under <Pixels/>
    store.setXMLAnnotationID(
        InOutCurrentTest.CHANNEL_ANNOTATION_ID, 0);
    store.setXMLAnnotationNamespace(
        InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setXMLAnnotationValue(
        InOutCurrentTest.CHANNEL_ANNOTATION_VALUE, 0);
    store.setXMLAnnotationNamespace(
        InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 0);
    for (int i = 0; i < InOutCurrentTest.SIZE_C; i++) {
      store.setChannelID("Channel:" + i, 0, i);
      if (i == 0) {
        store.setChannelAnnotationRef(
            InOutCurrentTest.CHANNEL_ANNOTATION_ID, 0, i, 0);
      }
    }
  }

  private void makeInstrument() {
    // Create <Instrument/>
    store.setInstrumentID(InOutCurrentTest.INSTRUMENT_ID, 0);

    // Create <Detector/> under <Instrument/>
    store.setDetectorID(InOutCurrentTest.DETECTOR_ID, 0, 0);
    store.setDetectorModel(InOutCurrentTest.DETECTOR_MODEL, 0, 0);
    // Create <Annotation/> under <Detector/>
    store.setCommentAnnotationID(InOutCurrentTest.DETECTOR_ANNOTATION_ID, 0);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setCommentAnnotationValue(InOutCurrentTest.DETECTOR_ANNOTATION_VALUE, 0);
    store.setDetectorAnnotationRef(InOutCurrentTest.DETECTOR_ANNOTATION_ID, 0, 0, 0);

    // Create <Laser/> under <Instrument/>
    store.setLaserID(InOutCurrentTest.LIGHTSOURCE_LASER_ID, 0, 0);
    store.setLaserModel(InOutCurrentTest.LIGHTSOURCE_LASER_MODEL, 0, 0);
    store.setLaserType(InOutCurrentTest.LASER_TYPE, 0, 0);
    store.setLaserPower(new Power(InOutCurrentTest.LIGHTSOURCE_LASER_POWER, UNITS.MW), 0, 0);
    // Create <Annotation/> under <Laser/>
    store.setCommentAnnotationID(InOutCurrentTest.LIGHTSOURCE_LASER_ANNOTATION_ID, 1);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 1);
    store.setCommentAnnotationValue(InOutCurrentTest.LIGHTSOURCE_LASER_ANNOTATION_VALUE, 1);
    store.setLightEmittingDiodeAnnotationRef(InOutCurrentTest.LIGHTSOURCE_LASER_ANNOTATION_ID, 0, 0, 0);

    // with a Pump>
    store.setLaserID(InOutCurrentTest.LIGHTSOURCE_PUMP_ID, 0, 1);
    store.setLaserModel(InOutCurrentTest.LIGHTSOURCE_PUMP_MODEL, 0, 1);
    store.setLaserType(InOutCurrentTest.LASER_TYPE, 0, 1);
    store.setLaserPower(new Power(InOutCurrentTest.LIGHTSOURCE_PUMP_POWER, UNITS.MW), 0, 1);
    // and link them
    store.setLaserPump(InOutCurrentTest.LIGHTSOURCE_PUMP_ID, 0, 0);

    // Create <Arc/> under <Instrument/>
    store.setArcID(InOutCurrentTest.LIGHTSOURCE_ARC_ID, 0, 2);
    store.setArcModel(InOutCurrentTest.LIGHTSOURCE_ARC_MODEL, 0, 2);
    store.setArcType(InOutCurrentTest.ARC_TYPE, 0, 2);
    store.setArcPower(new Power(InOutCurrentTest.LIGHTSOURCE_ARC_POWER, UNITS.MW), 0, 2);
    // Create <Annotation/> under <Arc/>
    store.setCommentAnnotationID(InOutCurrentTest.LIGHTSOURCE_ARC_ANNOTATION_ID, 2);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 2);
    store.setCommentAnnotationValue(InOutCurrentTest.LIGHTSOURCE_ARC_ANNOTATION_VALUE, 2);
    store.setLightEmittingDiodeAnnotationRef(InOutCurrentTest.LIGHTSOURCE_ARC_ANNOTATION_ID, 0, 2, 0);
    
    // Create <Filament/> under <Instrument/>
    store.setFilamentID(InOutCurrentTest.LIGHTSOURCE_FILAMENT_ID, 0, 3);
    store.setFilamentModel(InOutCurrentTest.LIGHTSOURCE_FILAMENT_MODEL, 0, 3);
    store.setFilamentType(InOutCurrentTest.FILAMENT_TYPE, 0, 3);
    store.setFilamentPower(new Power(InOutCurrentTest.LIGHTSOURCE_FILAMENT_POWER, UNITS.MW), 0, 3);
    // Create <Annotation/> under <Filament/>
    store.setCommentAnnotationID(InOutCurrentTest.LIGHTSOURCE_FILAMENT_ANNOTATION_ID, 3);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 3);
    store.setCommentAnnotationValue(InOutCurrentTest.LIGHTSOURCE_FILAMENT_ANNOTATION_VALUE, 3);
    store.setLightEmittingDiodeAnnotationRef(InOutCurrentTest.LIGHTSOURCE_FILAMENT_ANNOTATION_ID, 0, 3, 0);

    // Create <LightEmittingDiode/> under <Instrument/>
    store.setLightEmittingDiodeID(InOutCurrentTest.LIGHTSOURCE_LED_ID, 0, 4);
    store.setLightEmittingDiodeModel(InOutCurrentTest.LIGHTSOURCE_LED_MODEL, 0, 4);
    store.setLightEmittingDiodePower(new Power(InOutCurrentTest.LIGHTSOURCE_LED_POWER, UNITS.MW), 0, 4);
    // Create <Annotation/> under <LightEmittingDiode/>
    store.setCommentAnnotationID(InOutCurrentTest.LIGHTSOURCE_LED_ANNOTATION_ID, 4);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 4);
    store.setCommentAnnotationValue(InOutCurrentTest.LIGHTSOURCE_LED_ANNOTATION_VALUE, 4);
    store.setLightEmittingDiodeAnnotationRef(InOutCurrentTest.LIGHTSOURCE_LED_ANNOTATION_ID, 0, 4, 0);

    // Create <Dichroic/> under <Instrument/>
    store.setDichroicID(InOutCurrentTest.DICHROIC_ID, 0, 0);
    store.setDichroicSerialNumber(InOutCurrentTest.DICHROIC_SN, 0, 0);
    // Create <Annotation/> under <Dichroic/>
    store.setCommentAnnotationID(InOutCurrentTest.DICHROIC_ANNOTATION_ID, 5);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 5);
    store.setCommentAnnotationValue(InOutCurrentTest.DICHROIC_ANNOTATION_VALUE, 5);
    store.setDichroicAnnotationRef(InOutCurrentTest.DICHROIC_ANNOTATION_ID, 0, 0, 0);

    // Create <FilterSet/> under <Instrument/>
    store.setFilterSetID(InOutCurrentTest.FILTERSET_ID, 0, 0);
    store.setFilterSetLotNumber(InOutCurrentTest.FILTERSET_LOT, 0, 0);
    store.setFilterSetDichroicRef(InOutCurrentTest.DICHROIC_ID, 0, 0);
    store.setFilterSetEmissionFilterRef(InOutCurrentTest.EM_FILTER_ID, 0, 0, 0);
    store.setFilterSetExcitationFilterRef(InOutCurrentTest.EX_FILTER_ID, 0, 0, 0);

    // Create <Filter/>s under <Instrument/>
    store.setFilterID(InOutCurrentTest.EM_FILTER_ID, 0, 0);
    store.setFilterType(InOutCurrentTest.EM_FILTER_TYPE, 0, 0);
    // Create <Annotation/> under first <Filter/>
    store.setCommentAnnotationID(InOutCurrentTest.EM_FILTER_ANNOTATION_ID, 6);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 6);
    store.setCommentAnnotationValue(InOutCurrentTest.EM_FILTER_ANNOTATION_VALUE, 6);
    store.setFilterAnnotationRef(InOutCurrentTest.EM_FILTER_ANNOTATION_ID, 0, 0, 0);

    store.setFilterID(InOutCurrentTest.EX_FILTER_ID, 0, 1);
    store.setFilterType(InOutCurrentTest.EX_FILTER_TYPE, 0, 1);

    // Create <Objective/> under <Instrument/>
    store.setObjectiveID(InOutCurrentTest.OBJECTIVE_ID, 0, 0);
    store.setObjectiveModel(InOutCurrentTest.OBJECTIVE_MODEL, 0, 0);
    // Create <Annotation/> under <Objective/>
    store.setCommentAnnotationID(InOutCurrentTest.OBJECTIVE_ANNOTATION_ID, 7);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 7);
    store.setCommentAnnotationValue(InOutCurrentTest.OBJECTIVE_ANNOTATION_VALUE, 7);
    store.setObjectiveAnnotationRef(InOutCurrentTest.OBJECTIVE_ANNOTATION_ID, 0, 0, 0);

    // Create <Annotation/> under <Instrument/>
    store.setCommentAnnotationID(InOutCurrentTest.INSTRUMENT_ANNOTATION_ID, 8);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 8);
    store.setCommentAnnotationValue(InOutCurrentTest.INSTRUMENT_ANNOTATION_VALUE, 8);

    store.setInstrumentAnnotationRef(InOutCurrentTest.INSTRUMENT_ANNOTATION_ID, 0, 0);

    // link Instrument to the first Image
    store.setImageInstrumentRef(InOutCurrentTest.INSTRUMENT_ID, 0);
  }

  private void makePlate() {
    store.setPlateID(InOutCurrentTest.PLATE_ID, 0);
    store.setPlateRows(InOutCurrentTest.WELL_ROWS, 0);
    store.setPlateColumns(InOutCurrentTest.WELL_COLS, 0);
    store.setPlateRowNamingConvention(InOutCurrentTest.WELL_ROW, 0);
    store.setPlateColumnNamingConvention(InOutCurrentTest.WELL_COL, 0);

    store.setPlateAnnotationRef(InOutCurrentTest.PLATE_ANNOTATION_ID, 0, 0);

    store.setTimestampAnnotationID(InOutCurrentTest.PLATE_ANNOTATION_ID, 0);
    store.setTimestampAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setTimestampAnnotationValue(new Timestamp(InOutCurrentTest.PLATE_ANNOTATION_VALUE), 0);

    int wellSampleIndex = 0;
    int wellCount = 0;
    for (int row=0; row<InOutCurrentTest.WELL_ROWS.getValue(); row++) {
      for (int col=0; col<InOutCurrentTest.WELL_COLS.getValue(); col++) {

        store.setWellID(String.format("Well:%d_%d", row, col), 0, wellCount);
        store.setWellRow(new NonNegativeInteger(row), 0, wellCount);
        store.setWellColumn(new NonNegativeInteger(col), 0, wellCount);

        if (row == 0 && col == 0) {
          store.setLongAnnotationID(InOutCurrentTest.WELL_ANNOTATION_ID, 0);
          store.setLongAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 0);
          store.setLongAnnotationValue(InOutCurrentTest.WELL_ANNOTATION_VALUE, 0);
          store.setWellAnnotationRef(InOutCurrentTest.WELL_ANNOTATION_ID, 0, wellCount, 0);
        }

        store.setWellSampleID(String.format("WellSample:%d_%d", row, col), 0, wellCount, 0);
        store.setWellSampleIndex(new NonNegativeInteger(wellSampleIndex), 0, wellCount, 0);
        store.setWellSampleImageRef(InOutCurrentTest.IMAGE_ID, 0, wellCount, 0);

        wellSampleIndex++;
        wellCount++;
      }
    }
  }

  private void makeROI() {
    store.setROIID(InOutCurrentTest.ROI_ID, 0);

    store.setCommentAnnotationID(InOutCurrentTest.ROI_ANNOTATION_ID, 9);
    store.setCommentAnnotationNamespace(InOutCurrentTest.GENERAL_ANNOTATION_NAMESPACE, 9);
    store.setCommentAnnotationValue(InOutCurrentTest.ROI_ANNOTATION_VALUE, 9);

    store.setROIAnnotationRef(InOutCurrentTest.ROI_ANNOTATION_ID, 0, 0);

    store.setRectangleID(InOutCurrentTest.SHAPE_ID, 0, 0);
    store.setRectangleX(InOutCurrentTest.RECTANGLE_X, 0, 0);
    store.setRectangleY(InOutCurrentTest.RECTANGLE_Y, 0, 0);
    store.setRectangleWidth(InOutCurrentTest.RECTANGLE_WIDTH, 0, 0);
    store.setRectangleHeight(InOutCurrentTest.RECTANGLE_HEIGHT, 0, 0);
  }
}
