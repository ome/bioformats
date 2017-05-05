*******************************************************************************
BaseZeissReader
*******************************************************************************

This page lists supported metadata fields for the Bio-Formats BaseZeissReader.

These fields are from the :model_doc:`OME data model <>`.
Bio-Formats standardizes each format's original metadata to and from the OME
data model so that you can work with a particular piece of metadata (e.g.
physical width of the image in microns) in a format-independent way.

Of the 476 fields documented in the :doc:`metadata summary table </metadata-summary>`:
  * The file format itself supports 84 of them (17%).
  * Of those, Bio-Formats fully or partially converts 84 (100%).

Supported fields
===============================================================================

These fields are fully supported by the Bio-Formats BaseZeissReader:
  * :schema:`Channel : Color <OME-2016-06/ome_xsd.html#Channel_Color>`
  * :schema:`Channel : EmissionWavelength <OME-2016-06/ome_xsd.html#Channel_EmissionWavelength>`
  * :schema:`Channel : ExcitationWavelength <OME-2016-06/ome_xsd.html#Channel_ExcitationWavelength>`
  * :schema:`Channel : ID <OME-2016-06/ome_xsd.html#Channel_ID>`
  * :schema:`Channel : Name <OME-2016-06/ome_xsd.html#Channel_Name>`
  * :schema:`Channel : SamplesPerPixel <OME-2016-06/ome_xsd.html#Channel_SamplesPerPixel>`
  * :schema:`Detector : ID <OME-2016-06/ome_xsd.html#Detector_ID>`
  * :schema:`Detector : Type <OME-2016-06/ome_xsd.html#Detector_Type>`
  * :schema:`DetectorSettings : Gain <OME-2016-06/ome_xsd.html#DetectorSettings_Gain>`
  * :schema:`DetectorSettings : ID <OME-2016-06/ome_xsd.html#DetectorSettings_ID>`
  * :schema:`DetectorSettings : Offset <OME-2016-06/ome_xsd.html#DetectorSettings_Offset>`
  * :schema:`Ellipse : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Ellipse : RadiusX <OME-2016-06/ome_xsd.html#Ellipse_RadiusX>`
  * :schema:`Ellipse : RadiusY <OME-2016-06/ome_xsd.html#Ellipse_RadiusY>`
  * :schema:`Ellipse : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Ellipse : X <OME-2016-06/ome_xsd.html#Ellipse_X>`
  * :schema:`Ellipse : Y <OME-2016-06/ome_xsd.html#Ellipse_Y>`
  * :schema:`Experimenter : FirstName <OME-2016-06/ome_xsd.html#Experimenter_FirstName>`
  * :schema:`Experimenter : ID <OME-2016-06/ome_xsd.html#Experimenter_ID>`
  * :schema:`Experimenter : Institution <OME-2016-06/ome_xsd.html#Experimenter_Institution>`
  * :schema:`Experimenter : LastName <OME-2016-06/ome_xsd.html#Experimenter_LastName>`
  * :schema:`Image : AcquisitionDate <OME-2016-06/ome_xsd.html#Image_AcquisitionDate>`
  * :schema:`Image : Description <OME-2016-06/ome_xsd.html#Image_Description>`
  * :schema:`Image : ID <OME-2016-06/ome_xsd.html#Image_ID>`
  * :schema:`Image : InstrumentRef <OME-2016-06/ome_xsd.html#InstrumentRef_ID>`
  * :schema:`Image : Name <OME-2016-06/ome_xsd.html#Image_Name>`
  * :schema:`Image : ROIRef <OME-2016-06/ome_xsd.html#ROIRef_ID>`
  * :schema:`Instrument : ID <OME-2016-06/ome_xsd.html#Instrument_ID>`
  * :schema:`Label : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Label : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Label : X <OME-2016-06/ome_xsd.html#Label_X>`
  * :schema:`Label : Y <OME-2016-06/ome_xsd.html#Label_Y>`
  * :schema:`Line : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Line : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Line : X1 <OME-2016-06/ome_xsd.html#Line_X1>`
  * :schema:`Line : X2 <OME-2016-06/ome_xsd.html#Line_X2>`
  * :schema:`Line : Y1 <OME-2016-06/ome_xsd.html#Line_Y1>`
  * :schema:`Line : Y2 <OME-2016-06/ome_xsd.html#Line_Y2>`
  * :schema:`Objective : Correction <OME-2016-06/ome_xsd.html#Objective_Correction>`
  * :schema:`Objective : ID <OME-2016-06/ome_xsd.html#Objective_ID>`
  * :schema:`Objective : Immersion <OME-2016-06/ome_xsd.html#Objective_Immersion>`
  * :schema:`Objective : LensNA <OME-2016-06/ome_xsd.html#Objective_LensNA>`
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
  * :schema:`Pixels : Type <OME-2016-06/ome_xsd.html#Pixels_Type>`
  * :schema:`Plane : DeltaT <OME-2016-06/ome_xsd.html#Plane_DeltaT>`
  * :schema:`Plane : ExposureTime <OME-2016-06/ome_xsd.html#Plane_ExposureTime>`
  * :schema:`Plane : PositionX <OME-2016-06/ome_xsd.html#Plane_PositionX>`
  * :schema:`Plane : PositionY <OME-2016-06/ome_xsd.html#Plane_PositionY>`
  * :schema:`Plane : TheC <OME-2016-06/ome_xsd.html#Plane_TheC>`
  * :schema:`Plane : TheT <OME-2016-06/ome_xsd.html#Plane_TheT>`
  * :schema:`Plane : TheZ <OME-2016-06/ome_xsd.html#Plane_TheZ>`
  * :schema:`Point : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Point : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Point : X <OME-2016-06/ome_xsd.html#Point_X>`
  * :schema:`Point : Y <OME-2016-06/ome_xsd.html#Point_Y>`
  * :schema:`Polygon : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Polygon : Points <OME-2016-06/ome_xsd.html#Polygon_Points>`
  * :schema:`Polygon : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Polyline : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Polyline : Points <OME-2016-06/ome_xsd.html#Polyline_Points>`
  * :schema:`Polyline : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`ROI : ID <OME-2016-06/ome_xsd.html#ROI_ID>`
  * :schema:`ROI : Name <OME-2016-06/ome_xsd.html#ROI_Name>`
  * :schema:`Rectangle : Height <OME-2016-06/ome_xsd.html#Rectangle_Height>`
  * :schema:`Rectangle : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Rectangle : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Rectangle : Width <OME-2016-06/ome_xsd.html#Rectangle_Width>`
  * :schema:`Rectangle : X <OME-2016-06/ome_xsd.html#Rectangle_X>`
  * :schema:`Rectangle : Y <OME-2016-06/ome_xsd.html#Rectangle_Y>`

**Total supported: 84**

**Total unknown or missing: 392**
