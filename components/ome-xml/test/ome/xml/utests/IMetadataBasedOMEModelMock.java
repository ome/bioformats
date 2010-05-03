//
// OMEModelMock.java
//

/*
 * ome.xml.utests
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007-2008 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.utests;

import ome.xml.OMEXMLMetadataImpl;
import ome.xml.r201004.BinaryFile;
import ome.xml.r201004.BooleanAnnotation;
import ome.xml.r201004.Channel;
import ome.xml.r201004.Detector;
import ome.xml.r201004.Dichroic;
import ome.xml.r201004.DoubleAnnotation;
import ome.xml.r201004.External;
import ome.xml.r201004.Filter;
import ome.xml.r201004.FilterSet;
import ome.xml.r201004.Image;
import ome.xml.r201004.Instrument;
import ome.xml.r201004.Laser;
import ome.xml.r201004.LongAnnotation;
import ome.xml.r201004.MetadataOnly;
import ome.xml.r201004.OME;
import ome.xml.r201004.OMEModel;
import ome.xml.r201004.OTF;
import ome.xml.r201004.Objective;
import ome.xml.r201004.ObjectiveSettings;
import ome.xml.r201004.Pixels;
import ome.xml.r201004.Plate;
import ome.xml.r201004.ROI;
import ome.xml.r201004.Rectangle;
import ome.xml.r201004.StringAnnotation;
import ome.xml.r201004.StructuredAnnotations;
import ome.xml.r201004.TimestampAnnotation;
import ome.xml.r201004.Union;
import ome.xml.r201004.Well;
import ome.xml.r201004.WellSample;
import ome.xml.r201004.XMLAnnotation;
import ome.xml.r201004.primitives.NonNegativeInteger;
import ome.xml.r201004.primitives.PositiveInteger;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/ome-xml/test/ome/xml/utests/OMEModelMock.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/ome-xml/test/ome/xml/utests/OMEModelMock.java">SVN</a></dd></dl>
 */
public class IMetadataBasedOMEModelMock implements OMEModelMock {

  private OMEXMLMetadataImpl store;

  public IMetadataBasedOMEModelMock() {
    store = new OMEXMLMetadataImpl();
    store.createRoot();
    makeImage();
  }

  public OME getRoot() {
    return (OME) store.getRoot();
  }

  private void makeImage() {
    // Create <Image/>
    store.setImageID(InOut201004Test.IMAGE_ID, 0);
    store.setBooleanAnnotationID(InOut201004Test.IMAGE_ANNOTATION_ID, 0);
    store.setBooleanAnnotationValue(InOut201004Test.IMAGE_ANNOTATION_VALUE, 0);
    store.setImageAnnotationRef(InOut201004Test.IMAGE_ANNOTATION_ID, 0, 0);
    // Create <Pixels/>
    store.setPixelsID(InOut201004Test.PIXELS_ID, 0);
    store.setPixelsSizeX(new PositiveInteger(InOut201004Test.SIZE_X), 0);
    store.setPixelsSizeY(new PositiveInteger(InOut201004Test.SIZE_Y), 0);
    store.setPixelsSizeZ(new PositiveInteger(InOut201004Test.SIZE_Z), 0);
    store.setPixelsSizeC(new PositiveInteger(InOut201004Test.SIZE_C), 0);
    store.setPixelsSizeT(new PositiveInteger(InOut201004Test.SIZE_T), 0);
    store.setPixelsDimensionOrder(InOut201004Test.DIMENSION_ORDER, 0);
    store.setPixelsType(InOut201004Test.PIXEL_TYPE, 0);
    // Create <Channel/> under <Pixels/>
    store.setXMLAnnotationID(
        InOut201004Test.CHANNEL_ANNOTATION_ID, 0);
    store.setXMLAnnotationValue(
        InOut201004Test.CHANNEL_ANNOTATION_VALUE, 0);
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
    store.setDoubleAnnotationValue(
        InOut201004Test.PIXELS_ANNOTATION_VALUE, 0);
    store.setPixelsAnnotationRef(InOut201004Test.PIXELS_ANNOTATION_ID, 0, 0);
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
    laser.setID(InOut201004Test.LIGHTSOURCE_ID);
    laser.setModel(InOut201004Test.LIGHTSOURCE_MODEL);
    laser.setType(InOut201004Test.LASER_TYPE);
    laser.setPower(InOut201004Test.LIGHTSOURCE_POWER);
    instrument.addLightSource(laser);

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
    OTF otf = new OTF();
    // Create <Objective/> under <Instrument/>
    Objective objective = new Objective();
    objective.setID(InOut201004Test.OBJECTIVE_ID);
    objective.setModel(InOut201004Test.OBJECTIVE_MODEL);

    emFilter.setID(InOut201004Test.EM_FILTER_ID);
    emFilter.setType(InOut201004Test.EM_FILTER_TYPE);
    exFilter.setID(InOut201004Test.EX_FILTER_ID);
    exFilter.setType(InOut201004Test.EX_FILTER_TYPE);
    otf.setID(InOut201004Test.OTF_ID);
    otf.setType(InOut201004Test.OTF_PIXELTYPE);
    otf.setSizeX(new PositiveInteger(InOut201004Test.OTF_SIZE_X));
    otf.setSizeY(new PositiveInteger(InOut201004Test.OTF_SIZE_Y));
    otf.setOpticalAxisAveraged(InOut201004Test.OTF_OPTICAL_AXIS_AVERAGED);
    // Create <ObjectiveSettings/> under <OTF/>
    ObjectiveSettings otfObjectiveSettings = new ObjectiveSettings();
    otfObjectiveSettings.setID(objective.getID());
    otf.setObjectiveSettings(otfObjectiveSettings);
    // Create <BinaryFile/> under <OTF/>
    BinaryFile otfBinaryFile = new BinaryFile();
    otfBinaryFile.setFileName(InOut201004Test.OTF_BINARY_FILE_NAME);
    otfBinaryFile.setSize(InOut201004Test.OTF_BINARY_FILE_SIZE);
    External otfBinaryFileExternal = new External();
    otfBinaryFileExternal.sethref(InOut201004Test.OTF_BINARY_FILE_EXTERNAL_HREF);
    otfBinaryFileExternal.setSHA1(InOut201004Test.OTF_BINARY_FILE_EXTERNAL_SHA1);
    otfBinaryFile.setExternal(otfBinaryFileExternal);
    otf.setBinaryFile(otfBinaryFile);
    otf.linkFilterSet(filterSet);

    instrument.addFilter(emFilter);
    instrument.addFilter(exFilter);
    instrument.addOTF(otf);
    instrument.addObjective(objective);

    filterSet.linkEmissionFilter(emFilter);
    filterSet.linkExcitationFilter(exFilter);
    filterSet.linkDichroic(dichroic);
    instrument.addFilterSet(filterSet);
    instrument.addDichroic(dichroic);

    // link Instrument to the first Image
    //Image image = ome.getImage(0);
    //image.linkInstrument(instrument);

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
    plateAnnotation.setValue(InOut201004Test.PLATE_ANNOTATION_VALUE);
    plate.linkAnnotation(plateAnnotation);
    //annotations.addTimestampAnnotation(plateAnnotation);

    int wellSampleIndex = 0;
    for (int row=0; row<InOut201004Test.WELL_ROWS; row++) {
      for (int col=0; col<InOut201004Test.WELL_COLS; col++) {
        Well well = new Well();
        well.setID(String.format("Well:%d_%d", row, col));
        well.setRow(new NonNegativeInteger(row));
        well.setColumn(new NonNegativeInteger(col));

        if (row == 0 && col == 0) {
          LongAnnotation annotation = new LongAnnotation();
          annotation.setID(InOut201004Test.WELL_ANNOTATION_ID);
          annotation.setValue(InOut201004Test.WELL_ANNOTATION_VALUE);
          well.linkAnnotation(annotation);
          //annotations.addLongAnnotation(annotation);
        }

        WellSample sample = new WellSample();
        sample.setID(String.format("WellSample:%d_%d", row, col));
        sample.setIndex(new NonNegativeInteger(wellSampleIndex));
        //sample.linkImage(ome.getImage(0));
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

    StringAnnotation roiAnnotation = new StringAnnotation();
    roiAnnotation.setID(InOut201004Test.ROI_ANNOTATION_ID);
    roiAnnotation.setValue(InOut201004Test.ROI_ANNOTATION_VALUE);
    roi.linkAnnotation(roiAnnotation);
    //annotations.addStringAnnotation(roiAnnotation);

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
