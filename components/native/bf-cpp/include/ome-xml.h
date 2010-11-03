//
// ome-xml.h
//

/*
OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
Copyright (c) 2008-2010, UW-Madison LOCI.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the UW-Madison LOCI nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY UW-MADISON LOCI ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL UW-MADISON LOCI BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
IMPORTANT NOTE: Although this software is distributed according to a
"BSD-style" license, it requires the Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via JaceHeaderAutogen on Nov 3, 2010 12:20:33 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

#ifndef OME_XML_H
#define OME_XML_H

#include "jace.h"

#include "jace/proxy/ome/xml/About.h"
#include "jace/proxy/ome/xml/CustomNode.h"
#include "jace/proxy/ome/xml/DOMUtil.h"
#include "jace/proxy/ome/xml/OMEXMLFactory.h"
#include "jace/proxy/ome/xml/OMEXMLNode.h"
//using namespace jace::proxy::ome::xml;

#include "jace/proxy/ome/xml/model/AbstractOMEModelObject.h"
#include "jace/proxy/ome/xml/model/Annotation.h"
#include "jace/proxy/ome/xml/model/AnnotationRef.h"
#include "jace/proxy/ome/xml/model/Arc.h"
#include "jace/proxy/ome/xml/model/BinData.h"
#include "jace/proxy/ome/xml/model/BinaryFile.h"
#include "jace/proxy/ome/xml/model/BooleanAnnotation.h"
#include "jace/proxy/ome/xml/model/Channel.h"
#include "jace/proxy/ome/xml/model/ChannelRef.h"
#include "jace/proxy/ome/xml/model/CommentAnnotation.h"
#include "jace/proxy/ome/xml/model/Contact.h"
#include "jace/proxy/ome/xml/model/Dataset.h"
#include "jace/proxy/ome/xml/model/DatasetRef.h"
#include "jace/proxy/ome/xml/model/Detector.h"
#include "jace/proxy/ome/xml/model/DetectorSettings.h"
#include "jace/proxy/ome/xml/model/Dichroic.h"
#include "jace/proxy/ome/xml/model/DichroicRef.h"
#include "jace/proxy/ome/xml/model/DoubleAnnotation.h"
#include "jace/proxy/ome/xml/model/Ellipse.h"
#include "jace/proxy/ome/xml/model/EmissionFilterRef.h"
#include "jace/proxy/ome/xml/model/ExcitationFilterRef.h"
#include "jace/proxy/ome/xml/model/Experiment.h"
#include "jace/proxy/ome/xml/model/ExperimentRef.h"
#include "jace/proxy/ome/xml/model/Experimenter.h"
#include "jace/proxy/ome/xml/model/ExperimenterRef.h"
#include "jace/proxy/ome/xml/model/External.h"
#include "jace/proxy/ome/xml/model/Filament.h"
#include "jace/proxy/ome/xml/model/FileAnnotation.h"
#include "jace/proxy/ome/xml/model/Filter.h"
#include "jace/proxy/ome/xml/model/FilterRef.h"
#include "jace/proxy/ome/xml/model/FilterSet.h"
#include "jace/proxy/ome/xml/model/FilterSetRef.h"
#include "jace/proxy/ome/xml/model/Group.h"
#include "jace/proxy/ome/xml/model/GroupRef.h"
#include "jace/proxy/ome/xml/model/Image.h"
#include "jace/proxy/ome/xml/model/ImageRef.h"
#include "jace/proxy/ome/xml/model/ImagingEnvironment.h"
#include "jace/proxy/ome/xml/model/Instrument.h"
#include "jace/proxy/ome/xml/model/InstrumentRef.h"
#include "jace/proxy/ome/xml/model/Laser.h"
#include "jace/proxy/ome/xml/model/Leader.h"
#include "jace/proxy/ome/xml/model/LightEmittingDiode.h"
#include "jace/proxy/ome/xml/model/LightPath.h"
#include "jace/proxy/ome/xml/model/LightSource.h"
#include "jace/proxy/ome/xml/model/LightSourceSettings.h"
#include "jace/proxy/ome/xml/model/Line.h"
#include "jace/proxy/ome/xml/model/ListAnnotation.h"
#include "jace/proxy/ome/xml/model/LongAnnotation.h"
#include "jace/proxy/ome/xml/model/ManufacturerSpec.h"
#include "jace/proxy/ome/xml/model/Mask.h"
#include "jace/proxy/ome/xml/model/MetadataOnly.h"
#include "jace/proxy/ome/xml/model/MicrobeamManipulation.h"
#include "jace/proxy/ome/xml/model/MicrobeamManipulationRef.h"
#include "jace/proxy/ome/xml/model/Microscope.h"
#include "jace/proxy/ome/xml/model/OME.h"
#include "jace/proxy/ome/xml/model/OMEModel.h"
#include "jace/proxy/ome/xml/model/OMEModelImpl.h"
#include "jace/proxy/ome/xml/model/OMEModelObject.h"
#include "jace/proxy/ome/xml/model/OTF.h"
#include "jace/proxy/ome/xml/model/OTFRef.h"
#include "jace/proxy/ome/xml/model/Objective.h"
#include "jace/proxy/ome/xml/model/ObjectiveSettings.h"
#include "jace/proxy/ome/xml/model/Path.h"
#include "jace/proxy/ome/xml/model/Pixels.h"
#include "jace/proxy/ome/xml/model/Plane.h"
#include "jace/proxy/ome/xml/model/Plate.h"
#include "jace/proxy/ome/xml/model/PlateAcquisition.h"
#include "jace/proxy/ome/xml/model/PlateRef.h"
#include "jace/proxy/ome/xml/model/Point.h"
#include "jace/proxy/ome/xml/model/Polyline.h"
#include "jace/proxy/ome/xml/model/Project.h"
#include "jace/proxy/ome/xml/model/ProjectRef.h"
#include "jace/proxy/ome/xml/model/Pump.h"
#include "jace/proxy/ome/xml/model/ROI.h"
#include "jace/proxy/ome/xml/model/ROIRef.h"
#include "jace/proxy/ome/xml/model/Reagent.h"
#include "jace/proxy/ome/xml/model/ReagentRef.h"
#include "jace/proxy/ome/xml/model/Rectangle.h"
#include "jace/proxy/ome/xml/model/Reference.h"
#include "jace/proxy/ome/xml/model/Screen.h"
#include "jace/proxy/ome/xml/model/ScreenRef.h"
#include "jace/proxy/ome/xml/model/Settings.h"
#include "jace/proxy/ome/xml/model/Shape.h"
#include "jace/proxy/ome/xml/model/StageLabel.h"
#include "jace/proxy/ome/xml/model/StructuredAnnotations.h"
#include "jace/proxy/ome/xml/model/TagAnnotation.h"
#include "jace/proxy/ome/xml/model/TermAnnotation.h"
#include "jace/proxy/ome/xml/model/Text.h"
#include "jace/proxy/ome/xml/model/TiffData.h"
#include "jace/proxy/ome/xml/model/TimestampAnnotation.h"
#include "jace/proxy/ome/xml/model/TransmittanceRange.h"
#include "jace/proxy/ome/xml/model/UUID.h"
#include "jace/proxy/ome/xml/model/Union.h"
#include "jace/proxy/ome/xml/model/Well.h"
#include "jace/proxy/ome/xml/model/WellSample.h"
#include "jace/proxy/ome/xml/model/WellSampleRef.h"
#include "jace/proxy/ome/xml/model/XMLAnnotation.h"
//using namespace jace::proxy::ome::xml::model;

#include "jace/proxy/ome/xml/model/enums/AcquisitionMode.h"
#include "jace/proxy/ome/xml/model/enums/ArcType.h"
#include "jace/proxy/ome/xml/model/enums/Binning.h"
#include "jace/proxy/ome/xml/model/enums/Compression.h"
#include "jace/proxy/ome/xml/model/enums/ContrastMethod.h"
#include "jace/proxy/ome/xml/model/enums/Correction.h"
#include "jace/proxy/ome/xml/model/enums/DetectorType.h"
#include "jace/proxy/ome/xml/model/enums/DimensionOrder.h"
#include "jace/proxy/ome/xml/model/enums/Enumeration.h"
#include "jace/proxy/ome/xml/model/enums/EnumerationException.h"
#include "jace/proxy/ome/xml/model/enums/ExperimentType.h"
#include "jace/proxy/ome/xml/model/enums/FilamentType.h"
#include "jace/proxy/ome/xml/model/enums/FillRule.h"
#include "jace/proxy/ome/xml/model/enums/FilterType.h"
#include "jace/proxy/ome/xml/model/enums/FontFamily.h"
#include "jace/proxy/ome/xml/model/enums/FontStyle.h"
#include "jace/proxy/ome/xml/model/enums/IlluminationType.h"
#include "jace/proxy/ome/xml/model/enums/Immersion.h"
#include "jace/proxy/ome/xml/model/enums/LaserMedium.h"
#include "jace/proxy/ome/xml/model/enums/LaserType.h"
#include "jace/proxy/ome/xml/model/enums/LineCap.h"
#include "jace/proxy/ome/xml/model/enums/Marker.h"
#include "jace/proxy/ome/xml/model/enums/Medium.h"
#include "jace/proxy/ome/xml/model/enums/MicrobeamManipulationType.h"
#include "jace/proxy/ome/xml/model/enums/MicroscopeType.h"
#include "jace/proxy/ome/xml/model/enums/NamingConvention.h"
#include "jace/proxy/ome/xml/model/enums/PixelType.h"
#include "jace/proxy/ome/xml/model/enums/Pulse.h"
//using namespace jace::proxy::ome::xml::model::enums;

#include "jace/proxy/ome/xml/model/enums/handlers/AcquisitionModeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/ArcTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/BinningEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/CompressionEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/ContrastMethodEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/CorrectionEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/DetectorTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/DimensionOrderEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/ExperimentTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/FilamentTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/FillRuleEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/FilterTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/FontFamilyEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/FontStyleEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/IEnumerationHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/IlluminationTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/ImmersionEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/LaserMediumEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/LaserTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/LineCapEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/MarkerEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/MediumEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/MicrobeamManipulationTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/MicroscopeTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/NamingConventionEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/PixelTypeEnumHandler.h"
#include "jace/proxy/ome/xml/model/enums/handlers/PulseEnumHandler.h"
//using namespace jace::proxy::ome::xml::model::enums::handlers;

#include "jace/proxy/ome/xml/model/primitives/NonNegativeInteger.h"
#include "jace/proxy/ome/xml/model/primitives/NonNegativeLong.h"
#include "jace/proxy/ome/xml/model/primitives/PercentFraction.h"
#include "jace/proxy/ome/xml/model/primitives/PositiveInteger.h"
#include "jace/proxy/ome/xml/model/primitives/PositiveLong.h"
#include "jace/proxy/ome/xml/model/primitives/PrimitiveType.h"
//using namespace jace::proxy::ome::xml::model::primitives;

#include "jace/proxy/ome/xml/r2003fc/ome/ArcNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/AuxLightSourceRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ChannelComponentNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ChannelInfoNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ChannelSpecTypeNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ContactNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/DatasetNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/DatasetRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/DescriptionNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/DetectorNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/DetectorRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/DichroicNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/DisplayOptionsNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/EmFilterNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ExFilterNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ExperimentNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ExperimentRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ExperimenterNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ExperimenterRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ExperimenterTypeNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/FeatureNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/FilamentNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/FilterNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/FilterRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/FilterSetNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/FilterSpecNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/GreyChannelNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/GroupNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/GroupRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ImageNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ImagingEnvironmentNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/InstrumentNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/InstrumentRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/LaserNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/LeaderNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/LightSourceNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/LightSourceRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ManufactSpecNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/MicroscopeNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/OMENode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/OTFNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/OTFRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ObjectiveNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ObjectiveRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/PixelsNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/PlateNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/PlateRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ProjectNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ProjectRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ProjectionNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/PumpNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ROINode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ReferenceNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ScreenNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ScreenRefNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/StageLabelNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/ThumbnailNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/TiffDataNode.h"
#include "jace/proxy/ome/xml/r2003fc/ome/TimeNode.h"
//using namespace jace::proxy::ome::xml::r2003fc::ome;

#endif
