*******************************************************************************
ScreenReader
*******************************************************************************

This page lists supported metadata fields for the Bio-Formats Screen format reader.

These fields are from the :model_doc:`OME data model <>`.
Bio-Formats standardizes each format's original metadata to and from the OME
data model so that you can work with a particular piece of metadata (e.g.
physical width of the image in microns) in a format-independent way.

Of the 476 fields documented in the :doc:`metadata summary table </metadata-summary>`:
  * The file format itself supports 34 of them (7%).
  * Of those, Bio-Formats fully or partially converts 34 (100%).

Supported fields
===============================================================================

These fields are fully supported by the Bio-Formats Screen format reader:
  * :schema:`Channel : ID <OME-2016-06/ome_xsd.html#Channel_ID>`
  * :schema:`Channel : SamplesPerPixel <OME-2016-06/ome_xsd.html#Channel_SamplesPerPixel>`
  * :schema:`Image : AcquisitionDate <OME-2016-06/ome_xsd.html#Image_AcquisitionDate>`
  * :schema:`Image : ID <OME-2016-06/ome_xsd.html#Image_ID>`
  * :schema:`Image : Name <OME-2016-06/ome_xsd.html#Image_Name>`
  * :schema:`Pixels : BigEndian <OME-2016-06/ome_xsd.html#Pixels_BigEndian>`
  * :schema:`Pixels : DimensionOrder <OME-2016-06/ome_xsd.html#Pixels_DimensionOrder>`
  * :schema:`Pixels : ID <OME-2016-06/ome_xsd.html#Pixels_ID>`
  * :schema:`Pixels : Interleaved <OME-2016-06/ome_xsd.html#Pixels_Interleaved>`
  * :schema:`Pixels : SignificantBits <OME-2016-06/ome_xsd.html#Pixels_SignificantBits>`
  * :schema:`Pixels : SizeC <OME-2016-06/ome_xsd.html#Pixels_SizeC>`
  * :schema:`Pixels : SizeT <OME-2016-06/ome_xsd.html#Pixels_SizeT>`
  * :schema:`Pixels : SizeX <OME-2016-06/ome_xsd.html#Pixels_SizeX>`
  * :schema:`Pixels : SizeY <OME-2016-06/ome_xsd.html#Pixels_SizeY>`
  * :schema:`Pixels : SizeZ <OME-2016-06/ome_xsd.html#Pixels_SizeZ>`
  * :schema:`Pixels : Type <OME-2016-06/ome_xsd.html#Pixels_Type>`
  * :schema:`Plane : TheC <OME-2016-06/ome_xsd.html#Plane_TheC>`
  * :schema:`Plane : TheT <OME-2016-06/ome_xsd.html#Plane_TheT>`
  * :schema:`Plane : TheZ <OME-2016-06/ome_xsd.html#Plane_TheZ>`
  * :schema:`Plate : ColumnNamingConvention <OME-2016-06/ome_xsd.html#Plate_ColumnNamingConvention>`
  * :schema:`Plate : Columns <OME-2016-06/ome_xsd.html#Plate_Columns>`
  * :schema:`Plate : ID <OME-2016-06/ome_xsd.html#Plate_ID>`
  * :schema:`Plate : Name <OME-2016-06/ome_xsd.html#Plate_Name>`
  * :schema:`Plate : RowNamingConvention <OME-2016-06/ome_xsd.html#Plate_RowNamingConvention>`
  * :schema:`Plate : Rows <OME-2016-06/ome_xsd.html#Plate_Rows>`
  * :schema:`Screen : ID <OME-2016-06/ome_xsd.html#Screen_ID>`
  * :schema:`Screen : Name <OME-2016-06/ome_xsd.html#Screen_Name>`
  * :schema:`Screen : PlateRef <OME-2016-06/ome_xsd.html#Screen_Screen_PlateRef_ID>`
  * :schema:`Well : Column <OME-2016-06/ome_xsd.html#Well_Column>`
  * :schema:`Well : ID <OME-2016-06/ome_xsd.html#Well_ID>`
  * :schema:`Well : Row <OME-2016-06/ome_xsd.html#Well_Row>`
  * :schema:`WellSample : ID <OME-2016-06/ome_xsd.html#WellSample_ID>`
  * :schema:`WellSample : ImageRef <OME-2016-06/ome_xsd.html#ImageRef_ID>`
  * :schema:`WellSample : Index <OME-2016-06/ome_xsd.html#WellSample_Index>`

**Total supported: 34**

**Total unknown or missing: 442**
