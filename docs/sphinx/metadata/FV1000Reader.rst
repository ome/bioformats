*******************************************************************************
FV1000Reader
*******************************************************************************

This page lists supported metadata fields for the Bio-Formats Olympus FV1000 format reader.

These fields are from the :model_doc:`OME data model <>`.
Bio-Formats standardizes each format's original metadata to and from the OME
data model so that you can work with a particular piece of metadata (e.g.
physical width of the image in microns) in a format-independent way.

Of the 476 fields documented in the :doc:`metadata summary table </metadata-summary>`:
  * The file format itself supports 113 of them (23%).
  * Of those, Bio-Formats fully or partially converts 113 (100%).

Supported fields
===============================================================================

These fields are fully supported by the Bio-Formats Olympus FV1000 format reader:
  * :schema:`Channel : EmissionWavelength <OME-2016-06/ome_xsd.html#Channel_EmissionWavelength>`
  * :schema:`Channel : ExcitationWavelength <OME-2016-06/ome_xsd.html#Channel_ExcitationWavelength>`
  * :schema:`Channel : ID <OME-2016-06/ome_xsd.html#Channel_ID>`
  * :schema:`Channel : IlluminationType <OME-2016-06/ome_xsd.html#Channel_IlluminationType>`
  * :schema:`Channel : LightSourceSettingsID <OME-2016-06/ome_xsd.html#LightSourceSettings_ID>`
  * :schema:`Channel : LightSourceSettingsWavelength <OME-2016-06/ome_xsd.html#LightSourceSettings_Wavelength>`
  * :schema:`Channel : Name <OME-2016-06/ome_xsd.html#Channel_Name>`
  * :schema:`Channel : SamplesPerPixel <OME-2016-06/ome_xsd.html#Channel_SamplesPerPixel>`
  * :schema:`Detector : Gain <OME-2016-06/ome_xsd.html#Detector_Gain>`
  * :schema:`Detector : ID <OME-2016-06/ome_xsd.html#Detector_ID>`
  * :schema:`Detector : Type <OME-2016-06/ome_xsd.html#Detector_Type>`
  * :schema:`Detector : Voltage <OME-2016-06/ome_xsd.html#Detector_Voltage>`
  * :schema:`DetectorSettings : ID <OME-2016-06/ome_xsd.html#DetectorSettings_ID>`
  * :schema:`Dichroic : ID <OME-2016-06/ome_xsd.html#Dichroic_ID>`
  * :schema:`Dichroic : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Ellipse : FontSize <OME-2016-06/ome_xsd.html#Shape_FontSize>`
  * :schema:`Ellipse : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Ellipse : RadiusX <OME-2016-06/ome_xsd.html#Ellipse_RadiusX>`
  * :schema:`Ellipse : RadiusY <OME-2016-06/ome_xsd.html#Ellipse_RadiusY>`
  * :schema:`Ellipse : StrokeWidth <OME-2016-06/ome_xsd.html#Shape_StrokeWidth>`
  * :schema:`Ellipse : TheT <OME-2016-06/ome_xsd.html#Shape_TheT>`
  * :schema:`Ellipse : TheZ <OME-2016-06/ome_xsd.html#Shape_TheZ>`
  * :schema:`Ellipse : Transform <OME-2016-06/ome_xsd.html#Shape_Transform>`
  * :schema:`Ellipse : X <OME-2016-06/ome_xsd.html#Ellipse_X>`
  * :schema:`Ellipse : Y <OME-2016-06/ome_xsd.html#Ellipse_Y>`
  * :schema:`Filter : ID <OME-2016-06/ome_xsd.html#Filter_ID>`
  * :schema:`Filter : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Image : AcquisitionDate <OME-2016-06/ome_xsd.html#Image_AcquisitionDate>`
  * :schema:`Image : ID <OME-2016-06/ome_xsd.html#Image_ID>`
  * :schema:`Image : InstrumentRef <OME-2016-06/ome_xsd.html#InstrumentRef_ID>`
  * :schema:`Image : Name <OME-2016-06/ome_xsd.html#Image_Name>`
  * :schema:`Image : ROIRef <OME-2016-06/ome_xsd.html#ROIRef_ID>`
  * :schema:`Instrument : ID <OME-2016-06/ome_xsd.html#Instrument_ID>`
  * :schema:`Laser : ID <OME-2016-06/ome_xsd.html#LightSource_ID>`
  * :schema:`Laser : LaserMedium <OME-2016-06/ome_xsd.html#Laser_LaserMedium>`
  * :schema:`Laser : Type <OME-2016-06/ome_xsd.html#Laser_Type>`
  * :schema:`Laser : Wavelength <OME-2016-06/ome_xsd.html#Laser_Wavelength>`
  * :schema:`LightPath : DichroicRef <OME-2016-06/ome_xsd.html#DichroicRef_ID>`
  * :schema:`LightPath : EmissionFilterRef <OME-2016-06/ome_xsd.html#FilterRef_ID>`
  * :schema:`Line : FontSize <OME-2016-06/ome_xsd.html#Shape_FontSize>`
  * :schema:`Line : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Line : StrokeWidth <OME-2016-06/ome_xsd.html#Shape_StrokeWidth>`
  * :schema:`Line : TheT <OME-2016-06/ome_xsd.html#Shape_TheT>`
  * :schema:`Line : TheZ <OME-2016-06/ome_xsd.html#Shape_TheZ>`
  * :schema:`Line : Transform <OME-2016-06/ome_xsd.html#Shape_Transform>`
  * :schema:`Line : X1 <OME-2016-06/ome_xsd.html#Line_X1>`
  * :schema:`Line : X2 <OME-2016-06/ome_xsd.html#Line_X2>`
  * :schema:`Line : Y1 <OME-2016-06/ome_xsd.html#Line_Y1>`
  * :schema:`Line : Y2 <OME-2016-06/ome_xsd.html#Line_Y2>`
  * :schema:`Objective : Correction <OME-2016-06/ome_xsd.html#Objective_Correction>`
  * :schema:`Objective : ID <OME-2016-06/ome_xsd.html#Objective_ID>`
  * :schema:`Objective : Immersion <OME-2016-06/ome_xsd.html#Objective_Immersion>`
  * :schema:`Objective : LensNA <OME-2016-06/ome_xsd.html#Objective_LensNA>`
  * :schema:`Objective : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Objective : NominalMagnification <OME-2016-06/ome_xsd.html#Objective_NominalMagnification>`
  * :schema:`Objective : WorkingDistance <OME-2016-06/ome_xsd.html#Objective_WorkingDistance>`
  * :schema:`ObjectiveSettings : ID <OME-2016-06/ome_xsd.html#ObjectiveSettings_ID>`
  * :schema:`Pixels : BigEndian <OME-2016-06/ome_xsd.html#Pixels_BigEndian>`
  * :schema:`Pixels : DimensionOrder <OME-2016-06/ome_xsd.html#Pixels_DimensionOrder>`
  * :schema:`Pixels : ID <OME-2016-06/ome_xsd.html#Pixels_ID>`
  * :schema:`Pixels : Interleaved <OME-2016-06/ome_xsd.html#Pixels_Interleaved>`
  * :schema:`Pixels : PhysicalSizeX <OME-2016-06/ome_xsd.html#Pixels_PhysicalSizeX>`
  * :schema:`Pixels : PhysicalSizeY <OME-2016-06/ome_xsd.html#Pixels_PhysicalSizeY>`
  * :schema:`Pixels : PhysicalSizeZ <OME-2016-06/ome_xsd.html#Pixels_PhysicalSizeZ>`
  * :schema:`Pixels : SignificantBits <OME-2016-06/ome_xsd.html#Pixels_SignificantBits>`
  * :schema:`Pixels : SizeC <OME-2016-06/ome_xsd.html#Pixels_SizeC>`
  * :schema:`Pixels : SizeT <OME-2016-06/ome_xsd.html#Pixels_SizeT>`
  * :schema:`Pixels : SizeX <OME-2016-06/ome_xsd.html#Pixels_SizeX>`
  * :schema:`Pixels : SizeY <OME-2016-06/ome_xsd.html#Pixels_SizeY>`
  * :schema:`Pixels : SizeZ <OME-2016-06/ome_xsd.html#Pixels_SizeZ>`
  * :schema:`Pixels : TimeIncrement <OME-2016-06/ome_xsd.html#Pixels_TimeIncrement>`
  * :schema:`Pixels : Type <OME-2016-06/ome_xsd.html#Pixels_Type>`
  * :schema:`Plane : DeltaT <OME-2016-06/ome_xsd.html#Plane_DeltaT>`
  * :schema:`Plane : PositionX <OME-2016-06/ome_xsd.html#Plane_PositionX>`
  * :schema:`Plane : PositionY <OME-2016-06/ome_xsd.html#Plane_PositionY>`
  * :schema:`Plane : PositionZ <OME-2016-06/ome_xsd.html#Plane_PositionZ>`
  * :schema:`Plane : TheC <OME-2016-06/ome_xsd.html#Plane_TheC>`
  * :schema:`Plane : TheT <OME-2016-06/ome_xsd.html#Plane_TheT>`
  * :schema:`Plane : TheZ <OME-2016-06/ome_xsd.html#Plane_TheZ>`
  * :schema:`Point : FontSize <OME-2016-06/ome_xsd.html#Shape_FontSize>`
  * :schema:`Point : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Point : StrokeWidth <OME-2016-06/ome_xsd.html#Shape_StrokeWidth>`
  * :schema:`Point : TheT <OME-2016-06/ome_xsd.html#Shape_TheT>`
  * :schema:`Point : TheZ <OME-2016-06/ome_xsd.html#Shape_TheZ>`
  * :schema:`Point : X <OME-2016-06/ome_xsd.html#Point_X>`
  * :schema:`Point : Y <OME-2016-06/ome_xsd.html#Point_Y>`
  * :schema:`Polygon : FontSize <OME-2016-06/ome_xsd.html#Shape_FontSize>`
  * :schema:`Polygon : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Polygon : Points <OME-2016-06/ome_xsd.html#Polygon_Points>`
  * :schema:`Polygon : StrokeWidth <OME-2016-06/ome_xsd.html#Shape_StrokeWidth>`
  * :schema:`Polygon : TheT <OME-2016-06/ome_xsd.html#Shape_TheT>`
  * :schema:`Polygon : TheZ <OME-2016-06/ome_xsd.html#Shape_TheZ>`
  * :schema:`Polygon : Transform <OME-2016-06/ome_xsd.html#Shape_Transform>`
  * :schema:`Polyline : FontSize <OME-2016-06/ome_xsd.html#Shape_FontSize>`
  * :schema:`Polyline : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Polyline : Points <OME-2016-06/ome_xsd.html#Polyline_Points>`
  * :schema:`Polyline : StrokeWidth <OME-2016-06/ome_xsd.html#Shape_StrokeWidth>`
  * :schema:`Polyline : TheT <OME-2016-06/ome_xsd.html#Shape_TheT>`
  * :schema:`Polyline : TheZ <OME-2016-06/ome_xsd.html#Shape_TheZ>`
  * :schema:`Polyline : Transform <OME-2016-06/ome_xsd.html#Shape_Transform>`
  * :schema:`ROI : ID <OME-2016-06/ome_xsd.html#ROI_ID>`
  * :schema:`Rectangle : FontSize <OME-2016-06/ome_xsd.html#Shape_FontSize>`
  * :schema:`Rectangle : Height <OME-2016-06/ome_xsd.html#Rectangle_Height>`
  * :schema:`Rectangle : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Rectangle : StrokeWidth <OME-2016-06/ome_xsd.html#Shape_StrokeWidth>`
  * :schema:`Rectangle : TheT <OME-2016-06/ome_xsd.html#Shape_TheT>`
  * :schema:`Rectangle : TheZ <OME-2016-06/ome_xsd.html#Shape_TheZ>`
  * :schema:`Rectangle : Transform <OME-2016-06/ome_xsd.html#Shape_Transform>`
  * :schema:`Rectangle : Width <OME-2016-06/ome_xsd.html#Rectangle_Width>`
  * :schema:`Rectangle : X <OME-2016-06/ome_xsd.html#Rectangle_X>`
  * :schema:`Rectangle : Y <OME-2016-06/ome_xsd.html#Rectangle_Y>`
  * :schema:`TransmittanceRange : CutIn <OME-2016-06/ome_xsd.html#TransmittanceRange_CutIn>`
  * :schema:`TransmittanceRange : CutOut <OME-2016-06/ome_xsd.html#TransmittanceRange_CutOut>`

**Total supported: 113**

**Total unknown or missing: 363**
