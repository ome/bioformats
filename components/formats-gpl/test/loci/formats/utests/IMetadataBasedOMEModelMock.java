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

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/IMetadataBasedOMEModelMock.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/IMetadataBasedOMEModelMock.java;hb=HEAD">Gitweb</a></dd></dl>
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

  public MetadataRoot getRoot() {
    return store.getRoot();
  }

  private void makeImage() {
    // Create <Image/>
    store.setImageID(InOut201004Test.IMAGE_ID, 0);
    store.setListAnnotationID(InOut201004Test.IMAGE_LIST_ANNOTATION_ID, 0);
    store.setListAnnotationNamespace(
        InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setBooleanAnnotationID(InOut201004Test.IMAGE_ANNOTATION_ID, 0);
    store.setBooleanAnnotationNamespace(
        InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setListAnnotationAnnotationRef(
        InOut201004Test.IMAGE_ANNOTATION_ID, 0, 0);
    store.setBooleanAnnotationValue(InOut201004Test.IMAGE_ANNOTATION_VALUE, 0);
    store.setBooleanAnnotationNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setBooleanAnnotationValue(InOut201004Test.IMAGE_ANNOTATION_VALUE, 0);
    store.setImageAnnotationRef(InOut201004Test.IMAGE_LIST_ANNOTATION_ID, 0, 0);
    // Create <Pixels/>
    store.setPixelsID(InOut201004Test.PIXELS_ID, 0);
    store.setPixelsSizeX(new PositiveInteger(InOut201004Test.SIZE_X), 0);
    store.setPixelsSizeY(new PositiveInteger(InOut201004Test.SIZE_Y), 0);
    store.setPixelsSizeZ(new PositiveInteger(InOut201004Test.SIZE_Z), 0);
    store.setPixelsSizeC(new PositiveInteger(InOut201004Test.SIZE_C), 0);
    store.setPixelsSizeT(new PositiveInteger(InOut201004Test.SIZE_T), 0);
    store.setPixelsDimensionOrder(InOut201004Test.DIMENSION_ORDER, 0);
    store.setPixelsType(InOut201004Test.PIXEL_TYPE, 0);
    store.setUUIDValue(InOut201004Test.TIFF_DATA_UUID, 0, 0);
    // Create <Channel/> under <Pixels/>
    store.setXMLAnnotationID(
        InOut201004Test.CHANNEL_ANNOTATION_ID, 0);
    store.setXMLAnnotationNamespace(
        InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setXMLAnnotationValue(
        InOut201004Test.CHANNEL_ANNOTATION_VALUE, 0);
    store.setXMLAnnotationNamespace(
        InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
    for (int i = 0; i < InOut201004Test.SIZE_C; i++) {
      store.setChannelID("Channel:" + i, 0, i);
      if (i == 0) {
        store.setChannelAnnotationRef(
            InOut201004Test.CHANNEL_ANNOTATION_ID, 0, i, 0);
      }
    }
    // create Annotation for Pixels
    store.setDoubleAnnotationID(
        InOut201004Test.PIXELS_ANNOTATION_ID, 0);
    store.setDoubleAnnotationNamespace(
        InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setDoubleAnnotationValue(
        InOut201004Test.PIXELS_ANNOTATION_VALUE, 0);
    store.setDoubleAnnotationNamespace(
        InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setPixelsAnnotationRef(InOut201004Test.PIXELS_ANNOTATION_ID, 0, 0);
  }

  private void makeInstrument() {
    // Create <Instrument/>
    store.setInstrumentID(InOut201004Test.INSTRUMENT_ID, 0);

    // Create <Detector/> under <Instrument/>
    store.setDetectorID(InOut201004Test.DETECTOR_ID, 0, 0);
    store.setDetectorModel(InOut201004Test.DETECTOR_MODEL, 0, 0);

    // Create <Laser/> under <Instrument/>
    store.setLaserID(InOut201004Test.LIGHTSOURCE_LASER_ID, 0, 0);
    store.setLaserModel(InOut201004Test.LIGHTSOURCE_LASER_MODEL, 0, 0);
    store.setLaserType(InOut201004Test.LASER_TYPE, 0, 0);
    store.setLaserPower(InOut201004Test.LIGHTSOURCE_LASER_POWER, 0, 0);

    // with a Pump>
    store.setLaserID(InOut201004Test.LIGHTSOURCE_PUMP_ID, 0, 1);
    store.setLaserModel(InOut201004Test.LIGHTSOURCE_PUMP_MODEL, 0, 1);
    store.setLaserType(InOut201004Test.LASER_TYPE, 0, 1);
    store.setLaserPower(InOut201004Test.LIGHTSOURCE_PUMP_POWER, 0, 1);
    // and link them
    store.setLaserPump(InOut201004Test.LIGHTSOURCE_PUMP_ID, 0, 0);

    // Create <Arc/> under <Instrument/>
    store.setArcID(InOut201004Test.LIGHTSOURCE_ARC_ID, 0, 2);
    store.setArcModel(InOut201004Test.LIGHTSOURCE_ARC_MODEL, 0, 2);
    store.setArcType(InOut201004Test.ARC_TYPE, 0, 2);
    store.setArcPower(InOut201004Test.LIGHTSOURCE_ARC_POWER, 0, 2);
    
    // Create <Filament/> under <Instrument/>
    store.setFilamentID(InOut201004Test.LIGHTSOURCE_FILAMENT_ID, 0, 3);
    store.setFilamentModel(InOut201004Test.LIGHTSOURCE_FILAMENT_MODEL, 0, 3);
    store.setFilamentType(InOut201004Test.FILAMENT_TYPE, 0, 3);
    store.setFilamentPower(InOut201004Test.LIGHTSOURCE_FILAMENT_POWER, 0, 3);

    // Create <LightEmittingDiode/> under <Instrument/>
    store.setLightEmittingDiodeID(InOut201004Test.LIGHTSOURCE_LED_ID, 0, 4);
    store.setLightEmittingDiodeModel(InOut201004Test.LIGHTSOURCE_LED_MODEL, 0, 4);
    store.setLightEmittingDiodePower(InOut201004Test.LIGHTSOURCE_LED_POWER, 0, 4);

    // Create <Dichroic/> under <Instrument/>
    store.setDichroicID(InOut201004Test.DICHROIC_ID, 0, 0);
    store.setDichroicSerialNumber(InOut201004Test.DICHROIC_SN, 0, 0);

    // Create <FilterSet/> under <Instrument/>
    store.setFilterSetID(InOut201004Test.FILTERSET_ID, 0, 0);
    store.setFilterSetLotNumber(InOut201004Test.FILTERSET_LOT, 0, 0);
    store.setFilterSetDichroicRef(InOut201004Test.DICHROIC_ID, 0, 0);
    store.setFilterSetEmissionFilterRef(InOut201004Test.EM_FILTER_ID, 0, 0, 0);
    store.setFilterSetExcitationFilterRef(InOut201004Test.EX_FILTER_ID, 0, 0, 0);
    
    // Create <Filter/>s under <Instrument/>
    store.setFilterID(InOut201004Test.EM_FILTER_ID, 0, 0);
    store.setFilterType(InOut201004Test.EM_FILTER_TYPE, 0, 0);
    
    store.setFilterID(InOut201004Test.EX_FILTER_ID, 0, 1);
    store.setFilterType(InOut201004Test.EX_FILTER_TYPE, 0, 1);

    // Create <Objective/> under <Instrument/>
    store.setObjectiveID(InOut201004Test.OBJECTIVE_ID, 0, 0);
    store.setObjectiveModel(InOut201004Test.OBJECTIVE_MODEL, 0, 0);
    
    // link Instrument to the first Image
    store.setImageInstrumentRef(InOut201004Test.INSTRUMENT_ID, 0);
  }

  private void makePlate() {
    store.setPlateID(InOut201004Test.PLATE_ID, 0);
    store.setPlateRows(InOut201004Test.WELL_ROWS, 0);
    store.setPlateColumns(InOut201004Test.WELL_COLS, 0);
    store.setPlateRowNamingConvention(InOut201004Test.WELL_ROW, 0);
    store.setPlateColumnNamingConvention(InOut201004Test.WELL_COL, 0);
    
    store.setPlateAnnotationRef(InOut201004Test.PLATE_ANNOTATION_ID, 0, 0);

    store.setTimestampAnnotationID(InOut201004Test.PLATE_ANNOTATION_ID, 0);
    store.setTimestampAnnotationNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setTimestampAnnotationValue(new Timestamp(InOut201004Test.PLATE_ANNOTATION_VALUE), 0);

    int wellSampleIndex = 0;
    int wellCount = 0;
    for (int row=0; row<InOut201004Test.WELL_ROWS.getValue(); row++) {
      for (int col=0; col<InOut201004Test.WELL_COLS.getValue(); col++) {

        store.setWellID(String.format("Well:%d_%d", row, col), 0, wellCount);
        store.setWellRow(new NonNegativeInteger(row), 0, wellCount);
        store.setWellColumn(new NonNegativeInteger(col), 0, wellCount);

        if (row == 0 && col == 0) {
          store.setLongAnnotationID(InOut201004Test.WELL_ANNOTATION_ID, 0);
          store.setLongAnnotationNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
          store.setLongAnnotationValue(InOut201004Test.WELL_ANNOTATION_VALUE, 0);
          store.setWellAnnotationRef(InOut201004Test.WELL_ANNOTATION_ID, 0, wellCount, 0);
        }

        store.setWellSampleID(String.format("WellSample:%d_%d", row, col), 0, wellCount, 0);
        store.setWellSampleIndex(new NonNegativeInteger(wellSampleIndex), 0, wellCount, 0);
        store.setWellSampleImageRef(InOut201004Test.IMAGE_ID, 0, wellCount, 0);

        wellSampleIndex++;
        wellCount++;
      }
    }
  }

  private void makeROI() {
    store.setROIID(InOut201004Test.ROI_ID, 0);

    store.setCommentAnnotationID(InOut201004Test.ROI_ANNOTATION_ID, 0);
    store.setCommentAnnotationNamespace(InOut201004Test.GENERAL_ANNOTATION_NAMESPACE, 0);
    store.setCommentAnnotationValue(InOut201004Test.ROI_ANNOTATION_VALUE, 0);
    
    store.setROIAnnotationRef(InOut201004Test.ROI_ANNOTATION_ID, 0, 0);

    store.setRectangleID(InOut201004Test.SHAPE_ID, 0, 0);
    store.setRectangleX(InOut201004Test.RECTANGLE_X, 0, 0);
    store.setRectangleY(InOut201004Test.RECTANGLE_Y, 0, 0);
    store.setRectangleWidth(InOut201004Test.RECTANGLE_WIDTH, 0, 0);
    store.setRectangleHeight(InOut201004Test.RECTANGLE_HEIGHT, 0, 0);
    
    
  }
}
