*******************************************************************************
ZeissCZIReader
*******************************************************************************

This page lists supported metadata fields for the Bio-Formats Zeiss CZI format reader.

These fields are from the :model_doc:`OME data model <>`.
Bio-Formats standardizes each format's original metadata to and from the OME
data model so that you can work with a particular piece of metadata (e.g.
physical width of the image in microns) in a format-independent way.

Of the 476 fields documented in the :doc:`metadata summary table </metadata-summary>`:
  * The file format itself supports 158 of them (33%).
  * Of those, Bio-Formats fully or partially converts 158 (100%).

Supported fields
===============================================================================

These fields are fully supported by the Bio-Formats Zeiss CZI format reader:
  * :schema:`Arc : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`Arc : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Arc : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Arc : Power <OME-2016-06/ome_xsd.html#LightSource_Power>`
  * :schema:`Arc : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Channel : AcquisitionMode <OME-2016-06/ome_xsd.html#Channel_AcquisitionMode>`
  * :schema:`Channel : Color <OME-2016-06/ome_xsd.html#Channel_Color>`
  * :schema:`Channel : EmissionWavelength <OME-2016-06/ome_xsd.html#Channel_EmissionWavelength>`
  * :schema:`Channel : ExcitationWavelength <OME-2016-06/ome_xsd.html#Channel_ExcitationWavelength>`
  * :schema:`Channel : FilterSetRef <OME-2016-06/ome_xsd.html#FilterSetRef_ID>`
  * :schema:`Channel : Fluor <OME-2016-06/ome_xsd.html#Channel_Fluor>`
  * :schema:`Channel : ID <OME-2016-06/ome_xsd.html#Channel_ID>`
  * :schema:`Channel : IlluminationType <OME-2016-06/ome_xsd.html#Channel_IlluminationType>`
  * :schema:`Channel : Name <OME-2016-06/ome_xsd.html#Channel_Name>`
  * :schema:`Channel : PinholeSize <OME-2016-06/ome_xsd.html#Channel_PinholeSize>`
  * :schema:`Channel : SamplesPerPixel <OME-2016-06/ome_xsd.html#Channel_SamplesPerPixel>`
  * :schema:`Detector : AmplificationGain <OME-2016-06/ome_xsd.html#Detector_AmplificationGain>`
  * :schema:`Detector : Gain <OME-2016-06/ome_xsd.html#Detector_Gain>`
  * :schema:`Detector : ID <OME-2016-06/ome_xsd.html#Detector_ID>`
  * :schema:`Detector : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`Detector : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Detector : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Detector : Offset <OME-2016-06/ome_xsd.html#Detector_Offset>`
  * :schema:`Detector : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Detector : Type <OME-2016-06/ome_xsd.html#Detector_Type>`
  * :schema:`Detector : Zoom <OME-2016-06/ome_xsd.html#Detector_Zoom>`
  * :schema:`DetectorSettings : Binning <OME-2016-06/ome_xsd.html#DetectorSettings_Binning>`
  * :schema:`DetectorSettings : Gain <OME-2016-06/ome_xsd.html#DetectorSettings_Gain>`
  * :schema:`DetectorSettings : ID <OME-2016-06/ome_xsd.html#DetectorSettings_ID>`
  * :schema:`Dichroic : ID <OME-2016-06/ome_xsd.html#Dichroic_ID>`
  * :schema:`Dichroic : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`Dichroic : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Dichroic : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Dichroic : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Ellipse : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Ellipse : RadiusX <OME-2016-06/ome_xsd.html#Ellipse_RadiusX>`
  * :schema:`Ellipse : RadiusY <OME-2016-06/ome_xsd.html#Ellipse_RadiusY>`
  * :schema:`Ellipse : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Ellipse : X <OME-2016-06/ome_xsd.html#Ellipse_X>`
  * :schema:`Ellipse : Y <OME-2016-06/ome_xsd.html#Ellipse_Y>`
  * :schema:`Experimenter : Email <OME-2016-06/ome_xsd.html#Experimenter_Email>`
  * :schema:`Experimenter : FirstName <OME-2016-06/ome_xsd.html#Experimenter_FirstName>`
  * :schema:`Experimenter : ID <OME-2016-06/ome_xsd.html#Experimenter_ID>`
  * :schema:`Experimenter : Institution <OME-2016-06/ome_xsd.html#Experimenter_Institution>`
  * :schema:`Experimenter : LastName <OME-2016-06/ome_xsd.html#Experimenter_LastName>`
  * :schema:`Experimenter : MiddleName <OME-2016-06/ome_xsd.html#Experimenter_MiddleName>`
  * :schema:`Experimenter : UserName <OME-2016-06/ome_xsd.html#Experimenter_UserName>`
  * :schema:`Filament : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`Filament : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Filament : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Filament : Power <OME-2016-06/ome_xsd.html#LightSource_Power>`
  * :schema:`Filament : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Filter : FilterWheel <OME-2016-06/ome_xsd.html#Filter_FilterWheel>`
  * :schema:`Filter : ID <OME-2016-06/ome_xsd.html#Filter_ID>`
  * :schema:`Filter : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`Filter : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Filter : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Filter : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Filter : Type <OME-2016-06/ome_xsd.html#Filter_Type>`
  * :schema:`FilterSet : DichroicRef <OME-2016-06/ome_xsd.html#DichroicRef_ID>`
  * :schema:`FilterSet : EmissionFilterRef <OME-2016-06/ome_xsd.html#FilterRef_ID>`
  * :schema:`FilterSet : ExcitationFilterRef <OME-2016-06/ome_xsd.html#FilterRef_ID>`
  * :schema:`FilterSet : ID <OME-2016-06/ome_xsd.html#FilterSet_ID>`
  * :schema:`FilterSet : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`FilterSet : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`FilterSet : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`FilterSet : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Image : AcquisitionDate <OME-2016-06/ome_xsd.html#Image_AcquisitionDate>`
  * :schema:`Image : Description <OME-2016-06/ome_xsd.html#Image_Description>`
  * :schema:`Image : ExperimenterRef <OME-2016-06/ome_xsd.html#ExperimenterRef_ID>`
  * :schema:`Image : ID <OME-2016-06/ome_xsd.html#Image_ID>`
  * :schema:`Image : InstrumentRef <OME-2016-06/ome_xsd.html#InstrumentRef_ID>`
  * :schema:`Image : Name <OME-2016-06/ome_xsd.html#Image_Name>`
  * :schema:`Image : ROIRef <OME-2016-06/ome_xsd.html#ROIRef_ID>`
  * :schema:`ImagingEnvironment : AirPressure <OME-2016-06/ome_xsd.html#ImagingEnvironment_AirPressure>`
  * :schema:`ImagingEnvironment : CO2Percent <OME-2016-06/ome_xsd.html#ImagingEnvironment_CO2Percent>`
  * :schema:`ImagingEnvironment : Humidity <OME-2016-06/ome_xsd.html#ImagingEnvironment_Humidity>`
  * :schema:`ImagingEnvironment : Temperature <OME-2016-06/ome_xsd.html#ImagingEnvironment_Temperature>`
  * :schema:`Instrument : ID <OME-2016-06/ome_xsd.html#Instrument_ID>`
  * :schema:`Laser : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`Laser : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Laser : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Laser : Power <OME-2016-06/ome_xsd.html#LightSource_Power>`
  * :schema:`Laser : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`LightEmittingDiode : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`LightEmittingDiode : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`LightEmittingDiode : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`LightEmittingDiode : Power <OME-2016-06/ome_xsd.html#LightSource_Power>`
  * :schema:`LightEmittingDiode : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Line : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Line : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Line : X1 <OME-2016-06/ome_xsd.html#Line_X1>`
  * :schema:`Line : X2 <OME-2016-06/ome_xsd.html#Line_X2>`
  * :schema:`Line : Y1 <OME-2016-06/ome_xsd.html#Line_Y1>`
  * :schema:`Line : Y2 <OME-2016-06/ome_xsd.html#Line_Y2>`
  * :schema:`Microscope : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`Microscope : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Microscope : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Microscope : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Microscope : Type <OME-2016-06/ome_xsd.html#Microscope_Type>`
  * :schema:`Objective : CalibratedMagnification <OME-2016-06/ome_xsd.html#Objective_CalibratedMagnification>`
  * :schema:`Objective : Correction <OME-2016-06/ome_xsd.html#Objective_Correction>`
  * :schema:`Objective : ID <OME-2016-06/ome_xsd.html#Objective_ID>`
  * :schema:`Objective : Immersion <OME-2016-06/ome_xsd.html#Objective_Immersion>`
  * :schema:`Objective : Iris <OME-2016-06/ome_xsd.html#Objective_Iris>`
  * :schema:`Objective : LensNA <OME-2016-06/ome_xsd.html#Objective_LensNA>`
  * :schema:`Objective : LotNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_LotNumber>`
  * :schema:`Objective : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Objective : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Objective : NominalMagnification <OME-2016-06/ome_xsd.html#Objective_NominalMagnification>`
  * :schema:`Objective : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Objective : WorkingDistance <OME-2016-06/ome_xsd.html#Objective_WorkingDistance>`
  * :schema:`ObjectiveSettings : CorrectionCollar <OME-2016-06/ome_xsd.html#ObjectiveSettings_CorrectionCollar>`
  * :schema:`ObjectiveSettings : ID <OME-2016-06/ome_xsd.html#ObjectiveSettings_ID>`
  * :schema:`ObjectiveSettings : Medium <OME-2016-06/ome_xsd.html#ObjectiveSettings_Medium>`
  * :schema:`ObjectiveSettings : RefractiveIndex <OME-2016-06/ome_xsd.html#ObjectiveSettings_RefractiveIndex>`
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
  * :schema:`Pixels : Type <OME-2016-06/ome_xsd.html#Pixels_Type>`
  * :schema:`Plane : DeltaT <OME-2016-06/ome_xsd.html#Plane_DeltaT>`
  * :schema:`Plane : ExposureTime <OME-2016-06/ome_xsd.html#Plane_ExposureTime>`
  * :schema:`Plane : PositionX <OME-2016-06/ome_xsd.html#Plane_PositionX>`
  * :schema:`Plane : PositionY <OME-2016-06/ome_xsd.html#Plane_PositionY>`
  * :schema:`Plane : PositionZ <OME-2016-06/ome_xsd.html#Plane_PositionZ>`
  * :schema:`Plane : TheC <OME-2016-06/ome_xsd.html#Plane_TheC>`
  * :schema:`Plane : TheT <OME-2016-06/ome_xsd.html#Plane_TheT>`
  * :schema:`Plane : TheZ <OME-2016-06/ome_xsd.html#Plane_TheZ>`
  * :schema:`Polygon : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Polygon : Points <OME-2016-06/ome_xsd.html#Polygon_Points>`
  * :schema:`Polygon : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Polyline : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Polyline : Points <OME-2016-06/ome_xsd.html#Polyline_Points>`
  * :schema:`Polyline : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`ROI : Description <OME-2016-06/ome_xsd.html#ROI_Description>`
  * :schema:`ROI : ID <OME-2016-06/ome_xsd.html#ROI_ID>`
  * :schema:`ROI : Name <OME-2016-06/ome_xsd.html#ROI_Name>`
  * :schema:`Rectangle : Height <OME-2016-06/ome_xsd.html#Rectangle_Height>`
  * :schema:`Rectangle : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Rectangle : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Rectangle : Width <OME-2016-06/ome_xsd.html#Rectangle_Width>`
  * :schema:`Rectangle : X <OME-2016-06/ome_xsd.html#Rectangle_X>`
  * :schema:`Rectangle : Y <OME-2016-06/ome_xsd.html#Rectangle_Y>`
  * :schema:`TransmittanceRange : CutIn <OME-2016-06/ome_xsd.html#TransmittanceRange_CutIn>`
  * :schema:`TransmittanceRange : CutInTolerance <OME-2016-06/ome_xsd.html#TransmittanceRange_CutInTolerance>`
  * :schema:`TransmittanceRange : CutOut <OME-2016-06/ome_xsd.html#TransmittanceRange_CutOut>`
  * :schema:`TransmittanceRange : CutOutTolerance <OME-2016-06/ome_xsd.html#TransmittanceRange_CutOutTolerance>`
  * :schema:`TransmittanceRange : Transmittance <OME-2016-06/ome_xsd.html#TransmittanceRange_Transmittance>`

**Total supported: 158**

**Total unknown or missing: 318**
