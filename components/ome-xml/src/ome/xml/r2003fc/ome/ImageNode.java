/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via xsd-fu on 2008-10-16 06:18:35-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r2003fc.ome.*;

import org.w3c.dom.Element;

public class ImageNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a Image node with an associated DOM element. */
	public ImageNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Image node with an associated DOM element beneath
	 * a given parent.
	 */
	public ImageNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Image node with an associated DOM element beneath
	 * a given parent.
	 */
	public ImageNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Image", attach));
	}

	// -- Image API methods --

	// Element which is complex (has sub-elements)
	public ImagingEnvironmentNode getImagingEnvironment()
	{
		return (ImagingEnvironmentNode)
			getChildNode("ImagingEnvironment", "ImagingEnvironment");
	}

	// Element which is complex and is an OME XML "Ref"
	public ExperimenterNode getExperimenter()
	{
		return (ExperimenterNode)
			getReferencedNode("Experimenter", "ExperimenterRef");
	}

	public ExperimenterRefNode getExperimenterRef()
	{
		return (ExperimenterRefNode)
			getChildNode("ExperimenterRef", "ExperimenterRef");
	}

	// Element which occurs more than once
	public int getFeatureCount()
	{
		return getChildCount("Feature");
	}

	public java.util.Vector getFeatureList()
	{
		return getChildNodes("Feature");
	}

	public FeatureNode getFeature(int index)
	{
		return (FeatureNode) getChildNode("Feature", index);
	}

	// Attribute which is an OME XML "ID"
	public PixelsNode getDefaultPixelsNode()
	{
		return (PixelsNode)
			getAttrReferencedNode("Pixels", "DefaultPixels");
	}

	public String getDefaultPixels()
	{
		return getStringAttribute("DefaultPixels");
	}

	public void setDefaultPixels(String defaultPixels)
	{
		setAttribute("DefaultPixels", defaultPixels);
	}

	// Element which is not complex (has only a text node)
	public String getCustomAttributes()
	{
		return getStringCData("CustomAttributes");
	}

	public void setCustomAttributes(String customAttributes)
	{
		setCData("CustomAttributes", customAttributes);
	}

	// Element which is complex (has sub-elements)
	public ThumbnailNode getThumbnail()
	{
		return (ThumbnailNode)
			getChildNode("Thumbnail", "Thumbnail");
	}

	// Element which is not complex (has only a text node)
	public String getDescription()
	{
		return getStringCData("Description");
	}

	public void setDescription(String description)
	{
		setCData("Description", description);
	}

	// Element which is complex (has sub-elements)
	public StageLabelNode getStageLabel()
	{
		return (StageLabelNode)
			getChildNode("StageLabel", "StageLabel");
	}

	// Element which is complex and is an OME XML "Ref"
	public GroupNode getGroup()
	{
		return (GroupNode)
			getReferencedNode("Group", "GroupRef");
	}

	public GroupRefNode getGroupRef()
	{
		return (GroupRefNode)
			getChildNode("GroupRef", "GroupRef");
	}

	// Element which is complex and is an OME XML "Ref"
	public InstrumentNode getInstrument()
	{
		return (InstrumentNode)
			getReferencedNode("Instrument", "InstrumentRef");
	}

	public InstrumentRefNode getInstrumentRef()
	{
		return (InstrumentRefNode)
			getChildNode("InstrumentRef", "InstrumentRef");
	}

	// Element which is complex and is an OME XML "Ref"
	public PlateNode getPlate()
	{
		return (PlateNode)
			getReferencedNode("Plate", "PlateRef");
	}

	public PlateRefNode getPlateRef()
	{
		return (PlateRefNode)
			getChildNode("PlateRef", "PlateRef");
	}

	// Element which is complex and is an OME XML "Ref"
	public ObjectiveNode getObjective()
	{
		return (ObjectiveNode)
			getReferencedNode("Objective", "ObjectiveRef");
	}

	public ObjectiveRefNode getObjectiveRef()
	{
		return (ObjectiveRefNode)
			getChildNode("ObjectiveRef", "ObjectiveRef");
	}

	// Element which occurs more than once and is an OME XML "Ref"
	public int getDatasetCount()
	{
		return getChildCount("DatasetRef");
	}

	public java.util.Vector getDatasetList()
	{
		return getReferencedNodes("Dataset", "DatasetRef");
	}

	public int getDatasetRefCount()
	{
		return getChildCount("DatasetRef");
	}

	public java.util.Vector getDatasetRefList()
	{
		return getChildNodes("DatasetRef");
	}

	// Element which occurs more than once
	public int getChannelInfoCount()
	{
		return getChildCount("ChannelInfo");
	}

	public java.util.Vector getChannelInfoList()
	{
		return getChildNodes("ChannelInfo");
	}

	public ChannelInfoNode getChannelInfo(int index)
	{
		return (ChannelInfoNode) getChildNode("ChannelInfo", index);
	}

	// Attribute
	public Double getTimeIncrement()
	{
		return getDoubleAttribute("TimeIncrement");
	}

	public void setTimeIncrement(Double timeIncrement)
	{
		setAttribute("TimeIncrement", timeIncrement);
	}

	// Attribute
	public String getName()
	{
		return getStringAttribute("Name");
	}

	public void setName(String name)
	{
		setAttribute("Name", name);
	}

	// Attribute
	public Integer getWaveIncrement()
	{
		return getIntegerAttribute("WaveIncrement");
	}

	public void setWaveIncrement(Integer waveIncrement)
	{
		setAttribute("WaveIncrement", waveIncrement);
	}

	// *** WARNING *** Unhandled or skipped property ID

	// Element which is complex and is an OME XML "Ref"
	public ExperimentNode getExperiment()
	{
		return (ExperimentNode)
			getReferencedNode("Experiment", "ExperimentRef");
	}

	public ExperimentRefNode getExperimentRef()
	{
		return (ExperimentRefNode)
			getChildNode("ExperimentRef", "ExperimentRef");
	}

	// Attribute
	public Integer getWaveStart()
	{
		return getIntegerAttribute("WaveStart");
	}

	public void setWaveStart(Integer waveStart)
	{
		setAttribute("WaveStart", waveStart);
	}

	// Attribute
	public Double getPixelSizeZ()
	{
		return getDoubleAttribute("PixelSizeZ");
	}

	public void setPixelSizeZ(Double pixelSizeZ)
	{
		setAttribute("PixelSizeZ", pixelSizeZ);
	}

	// Attribute
	public Double getPixelSizeY()
	{
		return getDoubleAttribute("PixelSizeY");
	}

	public void setPixelSizeY(Double pixelSizeY)
	{
		setAttribute("PixelSizeY", pixelSizeY);
	}

	// Attribute
	public Double getPixelSizeX()
	{
		return getDoubleAttribute("PixelSizeX");
	}

	public void setPixelSizeX(Double pixelSizeX)
	{
		setAttribute("PixelSizeX", pixelSizeX);
	}

	// Element which occurs more than once
	public int getPixelsCount()
	{
		return getChildCount("Pixels");
	}

	public java.util.Vector getPixelsList()
	{
		return getChildNodes("Pixels");
	}

	public PixelsNode getPixels(int index)
	{
		return (PixelsNode) getChildNode("Pixels", index);
	}

	// Element which is not complex (has only a text node)
	public String getCreationDate()
	{
		return getStringCData("CreationDate");
	}

	public void setCreationDate(String creationDate)
	{
		setCData("CreationDate", creationDate);
	}

	// Element which is complex (has sub-elements)
	public DisplayOptionsNode getDisplayOptions()
	{
		return (DisplayOptionsNode)
			getChildNode("DisplayOptions", "DisplayOptions");
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
