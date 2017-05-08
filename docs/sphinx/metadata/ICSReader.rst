*******************************************************************************
ICSReader
*******************************************************************************

This page lists supported metadata fields for the Bio-Formats Image Cytometry Standard format reader.

These fields are from the :model_doc:`OME data model <>`.
Bio-Formats standardizes each format's original metadata to and from the OME
data model so that you can work with a particular piece of metadata (e.g.
physical width of the image in microns) in a format-independent way.

Of the 476 fields documented in the :doc:`metadata summary table </metadata-summary>`:
  * The file format itself supports 72 of them (15%).
  * Of those, Bio-Formats fully or partially converts 72 (100%).

Supported fields
===============================================================================

These fields are fully supported by the Bio-Formats Image Cytometry Standard format reader:
  * :schema:`Channel : EmissionWavelength <OME-2016-06/ome_xsd.html#Channel_EmissionWavelength>`
  * :schema:`Channel : ExcitationWavelength <OME-2016-06/ome_xsd.html#Channel_ExcitationWavelength>`
  * :schema:`Channel : ID <OME-2016-06/ome_xsd.html#Channel_ID>`
  * :schema:`Channel : Name <OME-2016-06/ome_xsd.html#Channel_Name>`
  * :schema:`Channel : PinholeSize <OME-2016-06/ome_xsd.html#Channel_PinholeSize>`
  * :schema:`Channel : SamplesPerPixel <OME-2016-06/ome_xsd.html#Channel_SamplesPerPixel>`
  * :schema:`Detector : ID <OME-2016-06/ome_xsd.html#Detector_ID>`
  * :schema:`Detector : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Detector : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Detector : Type <OME-2016-06/ome_xsd.html#Detector_Type>`
  * :schema:`DetectorSettings : Gain <OME-2016-06/ome_xsd.html#DetectorSettings_Gain>`
  * :schema:`DetectorSettings : ID <OME-2016-06/ome_xsd.html#DetectorSettings_ID>`
  * :schema:`Dichroic : ID <OME-2016-06/ome_xsd.html#Dichroic_ID>`
  * :schema:`Dichroic : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Experiment : ID <OME-2016-06/ome_xsd.html#Experiment_ID>`
  * :schema:`Experiment : Type <OME-2016-06/ome_xsd.html#Experiment_Type>`
  * :schema:`Experimenter : ID <OME-2016-06/ome_xsd.html#Experimenter_ID>`
  * :schema:`Experimenter : LastName <OME-2016-06/ome_xsd.html#Experimenter_LastName>`
  * :schema:`Filter : ID <OME-2016-06/ome_xsd.html#Filter_ID>`
  * :schema:`Filter : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`FilterSet : DichroicRef <OME-2016-06/ome_xsd.html#DichroicRef_ID>`
  * :schema:`FilterSet : EmissionFilterRef <OME-2016-06/ome_xsd.html#FilterRef_ID>`
  * :schema:`FilterSet : ExcitationFilterRef <OME-2016-06/ome_xsd.html#FilterRef_ID>`
  * :schema:`FilterSet : ID <OME-2016-06/ome_xsd.html#FilterSet_ID>`
  * :schema:`FilterSet : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Image : AcquisitionDate <OME-2016-06/ome_xsd.html#Image_AcquisitionDate>`
  * :schema:`Image : Description <OME-2016-06/ome_xsd.html#Image_Description>`
  * :schema:`Image : ID <OME-2016-06/ome_xsd.html#Image_ID>`
  * :schema:`Image : InstrumentRef <OME-2016-06/ome_xsd.html#InstrumentRef_ID>`
  * :schema:`Image : Name <OME-2016-06/ome_xsd.html#Image_Name>`
  * :schema:`Instrument : ID <OME-2016-06/ome_xsd.html#Instrument_ID>`
  * :schema:`Laser : ID <OME-2016-06/ome_xsd.html#LightSource_ID>`
  * :schema:`Laser : LaserMedium <OME-2016-06/ome_xsd.html#Laser_LaserMedium>`
  * :schema:`Laser : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Laser : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Laser : Power <OME-2016-06/ome_xsd.html#LightSource_Power>`
  * :schema:`Laser : RepetitionRate <OME-2016-06/ome_xsd.html#Laser_RepetitionRate>`
  * :schema:`Laser : Type <OME-2016-06/ome_xsd.html#Laser_Type>`
  * :schema:`Laser : Wavelength <OME-2016-06/ome_xsd.html#Laser_Wavelength>`
  * :schema:`Microscope : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Microscope : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Objective : CalibratedMagnification <OME-2016-06/ome_xsd.html#Objective_CalibratedMagnification>`
  * :schema:`Objective : Correction <OME-2016-06/ome_xsd.html#Objective_Correction>`
  * :schema:`Objective : ID <OME-2016-06/ome_xsd.html#Objective_ID>`
  * :schema:`Objective : Immersion <OME-2016-06/ome_xsd.html#Objective_Immersion>`
  * :schema:`Objective : LensNA <OME-2016-06/ome_xsd.html#Objective_LensNA>`
  * :schema:`Objective : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
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
  * :schema:`Plane : ExposureTime <OME-2016-06/ome_xsd.html#Plane_ExposureTime>`
  * :schema:`Plane : PositionX <OME-2016-06/ome_xsd.html#Plane_PositionX>`
  * :schema:`Plane : PositionY <OME-2016-06/ome_xsd.html#Plane_PositionY>`
  * :schema:`Plane : PositionZ <OME-2016-06/ome_xsd.html#Plane_PositionZ>`
  * :schema:`Plane : TheC <OME-2016-06/ome_xsd.html#Plane_TheC>`
  * :schema:`Plane : TheT <OME-2016-06/ome_xsd.html#Plane_TheT>`
  * :schema:`Plane : TheZ <OME-2016-06/ome_xsd.html#Plane_TheZ>`

**Total supported: 72**

**Total unknown or missing: 404**
