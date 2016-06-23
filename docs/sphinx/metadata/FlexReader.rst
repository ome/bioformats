*******************************************************************************
FlexReader
*******************************************************************************

This page lists supported metadata fields for the Bio-Formats Evotec Flex format reader.

These fields are from the :model_doc:`OME data model <>`.
Bio-Formats standardizes each format's original metadata to and from the OME
data model so that you can work with a particular piece of metadata (e.g.
physical width of the image in microns) in a format-independent way.

Of the 476 fields documented in the :doc:`metadata summary table </metadata-summary>`:
  * The file format itself supports 69 of them (14%).
  * Of those, Bio-Formats fully or partially converts 69 (100%).

Supported fields
===============================================================================

These fields are fully supported by the Bio-Formats Evotec Flex format reader:
  * :schema:`Channel : ID <OME-2016-06/ome_xsd.html#Channel_ID>`
  * :schema:`Channel : LightSourceSettingsID <OME-2016-06/ome_xsd.html#LightSourceSettings_ID>`
  * :schema:`Channel : Name <OME-2016-06/ome_xsd.html#Channel_Name>`
  * :schema:`Channel : SamplesPerPixel <OME-2016-06/ome_xsd.html#Channel_SamplesPerPixel>`
  * :schema:`Detector : ID <OME-2016-06/ome_xsd.html#Detector_ID>`
  * :schema:`Detector : Type <OME-2016-06/ome_xsd.html#Detector_Type>`
  * :schema:`DetectorSettings : Binning <OME-2016-06/ome_xsd.html#DetectorSettings_Binning>`
  * :schema:`DetectorSettings : ID <OME-2016-06/ome_xsd.html#DetectorSettings_ID>`
  * :schema:`Dichroic : ID <OME-2016-06/ome_xsd.html#Dichroic_ID>`
  * :schema:`Dichroic : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Filter : FilterWheel <OME-2016-06/ome_xsd.html#Filter_FilterWheel>`
  * :schema:`Filter : ID <OME-2016-06/ome_xsd.html#Filter_ID>`
  * :schema:`Filter : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Image : AcquisitionDate <OME-2016-06/ome_xsd.html#Image_AcquisitionDate>`
  * :schema:`Image : ID <OME-2016-06/ome_xsd.html#Image_ID>`
  * :schema:`Image : InstrumentRef <OME-2016-06/ome_xsd.html#InstrumentRef_ID>`
  * :schema:`Image : Name <OME-2016-06/ome_xsd.html#Image_Name>`
  * :schema:`Instrument : ID <OME-2016-06/ome_xsd.html#Instrument_ID>`
  * :schema:`Laser : ID <OME-2016-06/ome_xsd.html#LightSource_ID>`
  * :schema:`Laser : LaserMedium <OME-2016-06/ome_xsd.html#Laser_LaserMedium>`
  * :schema:`Laser : Type <OME-2016-06/ome_xsd.html#Laser_Type>`
  * :schema:`Laser : Wavelength <OME-2016-06/ome_xsd.html#Laser_Wavelength>`
  * :schema:`LightPath : DichroicRef <OME-2016-06/ome_xsd.html#DichroicRef_ID>`
  * :schema:`LightPath : EmissionFilterRef <OME-2016-06/ome_xsd.html#FilterRef_ID>`
  * :schema:`LightPath : ExcitationFilterRef <OME-2016-06/ome_xsd.html#FilterRef_ID>`
  * :schema:`Objective : CalibratedMagnification <OME-2016-06/ome_xsd.html#Objective_CalibratedMagnification>`
  * :schema:`Objective : Correction <OME-2016-06/ome_xsd.html#Objective_Correction>`
  * :schema:`Objective : ID <OME-2016-06/ome_xsd.html#Objective_ID>`
  * :schema:`Objective : Immersion <OME-2016-06/ome_xsd.html#Objective_Immersion>`
  * :schema:`Objective : LensNA <OME-2016-06/ome_xsd.html#Objective_LensNA>`
  * :schema:`ObjectiveSettings : ID <OME-2016-06/ome_xsd.html#ObjectiveSettings_ID>`
  * :schema:`Pixels : BigEndian <OME-2016-06/ome_xsd.html#Pixels_BigEndian>`
  * :schema:`Pixels : DimensionOrder <OME-2016-06/ome_xsd.html#Pixels_DimensionOrder>`
  * :schema:`Pixels : ID <OME-2016-06/ome_xsd.html#Pixels_ID>`
  * :schema:`Pixels : Interleaved <OME-2016-06/ome_xsd.html#Pixels_Interleaved>`
  * :schema:`Pixels : PhysicalSizeX <OME-2016-06/ome_xsd.html#Pixels_PhysicalSizeX>`
  * :schema:`Pixels : PhysicalSizeY <OME-2016-06/ome_xsd.html#Pixels_PhysicalSizeY>`
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
  * :schema:`Plate : ColumnNamingConvention <OME-2016-06/ome_xsd.html#Plate_ColumnNamingConvention>`
  * :schema:`Plate : ExternalIdentifier <OME-2016-06/ome_xsd.html#Plate_ExternalIdentifier>`
  * :schema:`Plate : ID <OME-2016-06/ome_xsd.html#Plate_ID>`
  * :schema:`Plate : Name <OME-2016-06/ome_xsd.html#Plate_Name>`
  * :schema:`Plate : RowNamingConvention <OME-2016-06/ome_xsd.html#Plate_RowNamingConvention>`
  * :schema:`PlateAcquisition : ID <OME-2016-06/ome_xsd.html#PlateAcquisition_ID>`
  * :schema:`PlateAcquisition : MaximumFieldCount <OME-2016-06/ome_xsd.html#PlateAcquisition_MaximumFieldCount>`
  * :schema:`PlateAcquisition : StartTime <OME-2016-06/ome_xsd.html#PlateAcquisition_StartTime>`
  * :schema:`PlateAcquisition : WellSampleRef <OME-2016-06/ome_xsd.html#WellSampleRef_ID>`
  * :schema:`Well : Column <OME-2016-06/ome_xsd.html#Well_Column>`
  * :schema:`Well : ID <OME-2016-06/ome_xsd.html#Well_ID>`
  * :schema:`Well : Row <OME-2016-06/ome_xsd.html#Well_Row>`
  * :schema:`WellSample : ID <OME-2016-06/ome_xsd.html#WellSample_ID>`
  * :schema:`WellSample : ImageRef <OME-2016-06/ome_xsd.html#ImageRef_ID>`
  * :schema:`WellSample : Index <OME-2016-06/ome_xsd.html#WellSample_Index>`
  * :schema:`WellSample : PositionX <OME-2016-06/ome_xsd.html#WellSample_PositionX>`
  * :schema:`WellSample : PositionY <OME-2016-06/ome_xsd.html#WellSample_PositionY>`

**Total supported: 69**

**Total unknown or missing: 407**
