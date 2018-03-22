*******************************************************************************
FakeReader
*******************************************************************************

This page lists supported metadata fields for the Bio-Formats Simulated data format reader.

These fields are from the :model_doc:`OME data model <>`.
Bio-Formats standardizes each format's original metadata to and from the OME
data model so that you can work with a particular piece of metadata (e.g.
physical width of the image in microns) in a format-independent way.

Of the 476 fields documented in the :doc:`metadata summary table </metadata-summary>`:
  * The file format itself supports 87 of them (18%).
  * Of those, Bio-Formats fully or partially converts 87 (100%).

Supported fields
===============================================================================

These fields are fully supported by the Bio-Formats Simulated data format reader:
  * :schema:`BooleanAnnotation : ID <OME-2016-06/ome_xsd.html#Annotation_ID>`
  * :schema:`BooleanAnnotation : Namespace <OME-2016-06/ome_xsd.html#Annotation_Namespace>`
  * :schema:`BooleanAnnotation : Value <OME-2016-06/ome_xsd.html#BooleanAnnotation_Value>`
  * :schema:`Channel : Color <OME-2016-06/ome_xsd.html#Channel_Color>`
  * :schema:`Channel : ID <OME-2016-06/ome_xsd.html#Channel_ID>`
  * :schema:`Channel : SamplesPerPixel <OME-2016-06/ome_xsd.html#Channel_SamplesPerPixel>`
  * :schema:`CommentAnnotation : ID <OME-2016-06/ome_xsd.html#Annotation_ID>`
  * :schema:`CommentAnnotation : Namespace <OME-2016-06/ome_xsd.html#Annotation_Namespace>`
  * :schema:`CommentAnnotation : Value <OME-2016-06/ome_xsd.html#CommentAnnotation_Value>`
  * :schema:`DoubleAnnotation : ID <OME-2016-06/ome_xsd.html#Annotation_ID>`
  * :schema:`DoubleAnnotation : Namespace <OME-2016-06/ome_xsd.html#Annotation_Namespace>`
  * :schema:`DoubleAnnotation : Value <OME-2016-06/ome_xsd.html#DoubleAnnotation_Value>`
  * :schema:`Ellipse : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Ellipse : RadiusX <OME-2016-06/ome_xsd.html#Ellipse_RadiusX>`
  * :schema:`Ellipse : RadiusY <OME-2016-06/ome_xsd.html#Ellipse_RadiusY>`
  * :schema:`Ellipse : X <OME-2016-06/ome_xsd.html#Ellipse_X>`
  * :schema:`Ellipse : Y <OME-2016-06/ome_xsd.html#Ellipse_Y>`
  * :schema:`Image : AcquisitionDate <OME-2016-06/ome_xsd.html#Image_AcquisitionDate>`
  * :schema:`Image : AnnotationRef <OME-2016-06/ome_xsd.html#AnnotationRef_ID>`
  * :schema:`Image : ID <OME-2016-06/ome_xsd.html#Image_ID>`
  * :schema:`Image : Name <OME-2016-06/ome_xsd.html#Image_Name>`
  * :schema:`Image : ROIRef <OME-2016-06/ome_xsd.html#ROIRef_ID>`
  * :schema:`Label : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Label : Text <OME-2016-06/ome_xsd.html#Shape_Text>`
  * :schema:`Label : X <OME-2016-06/ome_xsd.html#Label_X>`
  * :schema:`Label : Y <OME-2016-06/ome_xsd.html#Label_Y>`
  * :schema:`Line : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Line : X1 <OME-2016-06/ome_xsd.html#Line_X1>`
  * :schema:`Line : X2 <OME-2016-06/ome_xsd.html#Line_X2>`
  * :schema:`Line : Y1 <OME-2016-06/ome_xsd.html#Line_Y1>`
  * :schema:`Line : Y2 <OME-2016-06/ome_xsd.html#Line_Y2>`
  * :schema:`LongAnnotation : ID <OME-2016-06/ome_xsd.html#Annotation_ID>`
  * :schema:`LongAnnotation : Namespace <OME-2016-06/ome_xsd.html#Annotation_Namespace>`
  * :schema:`LongAnnotation : Value <OME-2016-06/ome_xsd.html#LongAnnotation_Value>`
  * :schema:`Mask : BinData <OME-2016-06/ome_xsd.html#BinData>`
  * :schema:`Mask : BinDataBigEndian <OME-2016-06/ome_xsd.html#BinData_BigEndian>`
  * :schema:`Mask : Height <OME-2016-06/ome_xsd.html#Mask_Height>`
  * :schema:`Mask : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Mask : Width <OME-2016-06/ome_xsd.html#Mask_Width>`
  * :schema:`Mask : X <OME-2016-06/ome_xsd.html#Mask_X>`
  * :schema:`Mask : Y <OME-2016-06/ome_xsd.html#Mask_Y>`
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
  * :schema:`Plane : ExposureTime <OME-2016-06/ome_xsd.html#Plane_ExposureTime>`
  * :schema:`Plane : PositionX <OME-2016-06/ome_xsd.html#Plane_PositionX>`
  * :schema:`Plane : PositionY <OME-2016-06/ome_xsd.html#Plane_PositionY>`
  * :schema:`Plane : PositionZ <OME-2016-06/ome_xsd.html#Plane_PositionZ>`
  * :schema:`Plane : TheC <OME-2016-06/ome_xsd.html#Plane_TheC>`
  * :schema:`Plane : TheT <OME-2016-06/ome_xsd.html#Plane_TheT>`
  * :schema:`Plane : TheZ <OME-2016-06/ome_xsd.html#Plane_TheZ>`
  * :schema:`Point : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Point : X <OME-2016-06/ome_xsd.html#Point_X>`
  * :schema:`Point : Y <OME-2016-06/ome_xsd.html#Point_Y>`
  * :schema:`Polygon : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Polygon : Points <OME-2016-06/ome_xsd.html#Polygon_Points>`
  * :schema:`Polyline : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Polyline : Points <OME-2016-06/ome_xsd.html#Polyline_Points>`
  * :schema:`ROI : ID <OME-2016-06/ome_xsd.html#ROI_ID>`
  * :schema:`Rectangle : Height <OME-2016-06/ome_xsd.html#Rectangle_Height>`
  * :schema:`Rectangle : ID <OME-2016-06/ome_xsd.html#Shape_ID>`
  * :schema:`Rectangle : Width <OME-2016-06/ome_xsd.html#Rectangle_Width>`
  * :schema:`Rectangle : X <OME-2016-06/ome_xsd.html#Rectangle_X>`
  * :schema:`Rectangle : Y <OME-2016-06/ome_xsd.html#Rectangle_Y>`
  * :schema:`TagAnnotation : ID <OME-2016-06/ome_xsd.html#Annotation_ID>`
  * :schema:`TagAnnotation : Namespace <OME-2016-06/ome_xsd.html#Annotation_Namespace>`
  * :schema:`TagAnnotation : Value <OME-2016-06/ome_xsd.html#TagAnnotation_Value>`
  * :schema:`TermAnnotation : ID <OME-2016-06/ome_xsd.html#Annotation_ID>`
  * :schema:`TermAnnotation : Namespace <OME-2016-06/ome_xsd.html#Annotation_Namespace>`
  * :schema:`TermAnnotation : Value <OME-2016-06/ome_xsd.html#TermAnnotation_Value>`
  * :schema:`TimestampAnnotation : ID <OME-2016-06/ome_xsd.html#Annotation_ID>`
  * :schema:`TimestampAnnotation : Namespace <OME-2016-06/ome_xsd.html#Annotation_Namespace>`
  * :schema:`TimestampAnnotation : Value <OME-2016-06/ome_xsd.html#TimestampAnnotation_Value>`
  * :schema:`XMLAnnotation : ID <OME-2016-06/ome_xsd.html#Annotation_ID>`
  * :schema:`XMLAnnotation : Namespace <OME-2016-06/ome_xsd.html#Annotation_Namespace>`
  * :schema:`XMLAnnotation : Value <OME-2016-06/ome_xsd.html#XMLAnnotation_Value>`

**Total supported: 87**

**Total unknown or missing: 389**
