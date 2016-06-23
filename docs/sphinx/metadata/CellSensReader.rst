*******************************************************************************
CellSensReader
*******************************************************************************

This page lists supported metadata fields for the Bio-Formats CellSens VSI format reader.

These fields are from the :model_doc:`OME data model <>`.
Bio-Formats standardizes each format's original metadata to and from the OME
data model so that you can work with a particular piece of metadata (e.g.
physical width of the image in microns) in a format-independent way.

Of the 476 fields documented in the :doc:`metadata summary table </metadata-summary>`:
  * The file format itself supports 46 of them (9%).
  * Of those, Bio-Formats fully or partially converts 46 (100%).

Supported fields
===============================================================================

These fields are fully supported by the Bio-Formats CellSens VSI format reader:
  * :schema:`Channel : EmissionWavelength <OME-2016-06/ome_xsd.html#Channel_EmissionWavelength>`
  * :schema:`Channel : ID <OME-2016-06/ome_xsd.html#Channel_ID>`
  * :schema:`Channel : Name <OME-2016-06/ome_xsd.html#Channel_Name>`
  * :schema:`Channel : SamplesPerPixel <OME-2016-06/ome_xsd.html#Channel_SamplesPerPixel>`
  * :schema:`Detector : Gain <OME-2016-06/ome_xsd.html#Detector_Gain>`
  * :schema:`Detector : ID <OME-2016-06/ome_xsd.html#Detector_ID>`
  * :schema:`Detector : Manufacturer <OME-2016-06/ome_xsd.html#ManufacturerSpec_Manufacturer>`
  * :schema:`Detector : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Detector : Offset <OME-2016-06/ome_xsd.html#Detector_Offset>`
  * :schema:`Detector : SerialNumber <OME-2016-06/ome_xsd.html#ManufacturerSpec_SerialNumber>`
  * :schema:`Detector : Type <OME-2016-06/ome_xsd.html#Detector_Type>`
  * :schema:`DetectorSettings : Binning <OME-2016-06/ome_xsd.html#DetectorSettings_Binning>`
  * :schema:`DetectorSettings : Gain <OME-2016-06/ome_xsd.html#DetectorSettings_Gain>`
  * :schema:`DetectorSettings : ID <OME-2016-06/ome_xsd.html#DetectorSettings_ID>`
  * :schema:`DetectorSettings : Offset <OME-2016-06/ome_xsd.html#DetectorSettings_Offset>`
  * :schema:`Image : AcquisitionDate <OME-2016-06/ome_xsd.html#Image_AcquisitionDate>`
  * :schema:`Image : ID <OME-2016-06/ome_xsd.html#Image_ID>`
  * :schema:`Image : InstrumentRef <OME-2016-06/ome_xsd.html#InstrumentRef_ID>`
  * :schema:`Image : Name <OME-2016-06/ome_xsd.html#Image_Name>`
  * :schema:`Instrument : ID <OME-2016-06/ome_xsd.html#Instrument_ID>`
  * :schema:`Objective : ID <OME-2016-06/ome_xsd.html#Objective_ID>`
  * :schema:`Objective : LensNA <OME-2016-06/ome_xsd.html#Objective_LensNA>`
  * :schema:`Objective : Model <OME-2016-06/ome_xsd.html#ManufacturerSpec_Model>`
  * :schema:`Objective : NominalMagnification <OME-2016-06/ome_xsd.html#Objective_NominalMagnification>`
  * :schema:`Objective : WorkingDistance <OME-2016-06/ome_xsd.html#Objective_WorkingDistance>`
  * :schema:`ObjectiveSettings : ID <OME-2016-06/ome_xsd.html#ObjectiveSettings_ID>`
  * :schema:`ObjectiveSettings : RefractiveIndex <OME-2016-06/ome_xsd.html#ObjectiveSettings_RefractiveIndex>`
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
  * :schema:`Plane : ExposureTime <OME-2016-06/ome_xsd.html#Plane_ExposureTime>`
  * :schema:`Plane : PositionX <OME-2016-06/ome_xsd.html#Plane_PositionX>`
  * :schema:`Plane : PositionY <OME-2016-06/ome_xsd.html#Plane_PositionY>`
  * :schema:`Plane : TheC <OME-2016-06/ome_xsd.html#Plane_TheC>`
  * :schema:`Plane : TheT <OME-2016-06/ome_xsd.html#Plane_TheT>`
  * :schema:`Plane : TheZ <OME-2016-06/ome_xsd.html#Plane_TheZ>`

**Total supported: 46**

**Total unknown or missing: 430**
