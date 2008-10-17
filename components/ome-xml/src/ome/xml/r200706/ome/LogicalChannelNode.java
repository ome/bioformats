/*
 * ome.xml.r200706.ome.LogicalChannelNode
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via xsd-fu on 2008-10-16 05:38:13-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200706.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200706.ome.*;
import ome.xml.r200706.spw.*;

import org.w3c.dom.Element;

public class LogicalChannelNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a LogicalChannel node with an associated DOM element. */
	public LogicalChannelNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a LogicalChannel node with an associated DOM element beneath
	 * a given parent.
	 */
	public LogicalChannelNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a LogicalChannel node with an associated DOM element beneath
	 * a given parent.
	 */
	public LogicalChannelNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "LogicalChannel", attach));
	}

	// -- LogicalChannel API methods --

	// Attribute
	public String getPhotometricInterpretation()
	{
		return getStringAttribute("PhotometricInterpretation");
	}

	public void setPhotometricInterpretation(String photometricInterpretation)
	{
		setAttribute("PhotometricInterpretation", photometricInterpretation);
	}

	// Element which occurs more than once
	public int getChannelComponentCount()
	{
		return getChildCount("ChannelComponent");
	}

	public java.util.Vector getChannelComponentList()
	{
		return getChildNodes("ChannelComponent");
	}

	public ChannelComponentNode getChannelComponent(int index)
	{
		return (ChannelComponentNode) getChildNode("ChannelComponent", index);
	}

	// Attribute
	public Integer getPockelCellSetting()
	{
		return getIntegerAttribute("PockelCellSetting");
	}

	public void setPockelCellSetting(Integer pockelCellSetting)
	{
		setAttribute("PockelCellSetting", pockelCellSetting);
	}

	// Attribute which is an OME XML "ID"
	public FilterNode getSecondaryExcitationFilter()
	{
		return (FilterNode)
			getAttrReferencedNode("Filter", "SecondaryExcitationFilter");
	}

	// Attribute
	public Integer getSamplesPerPixel()
	{
		return getIntegerAttribute("SamplesPerPixel");
	}

	public void setSamplesPerPixel(Integer samplesPerPixel)
	{
		setAttribute("SamplesPerPixel", samplesPerPixel);
	}

	// Attribute
	public String getContrastMethod()
	{
		return getStringAttribute("ContrastMethod");
	}

	public void setContrastMethod(String contrastMethod)
	{
		setAttribute("ContrastMethod", contrastMethod);
	}

	// Element which is complex and is an OME XML "Ref"
	public FilterSetNode getFilterSet()
	{
		return (FilterSetNode)
			getReferencedNode("FilterSet", "FilterSetRef");
	}

	public FilterSetRefNode getFilterSetRef()
	{
		return (FilterSetRefNode)
			getChildNode("FilterSetRef", "FilterSetRef");
	}

	// Attribute which is an OME XML "ID"
	public FilterNode getSecondaryEmissionFilter()
	{
		return (FilterNode)
			getAttrReferencedNode("Filter", "SecondaryEmissionFilter");
	}

	// *** WARNING *** Unhandled or skipped property ID

	// Attribute
	public String getFluor()
	{
		return getStringAttribute("Fluor");
	}

	public void setFluor(String fluor)
	{
		setAttribute("Fluor", fluor);
	}

	// Attribute
	public Integer getPinholeSize()
	{
		return getIntegerAttribute("PinholeSize");
	}

	public void setPinholeSize(Integer pinholeSize)
	{
		setAttribute("PinholeSize", pinholeSize);
	}

	// Attribute
	public Float getNdFilter()
	{
		return getFloatAttribute("NdFilter");
	}

	public void setNdFilter(Float ndFilter)
	{
		setAttribute("NdFilter", ndFilter);
	}

	// Attribute
	public String getMode()
	{
		return getStringAttribute("Mode");
	}

	public void setMode(String mode)
	{
		setAttribute("Mode", mode);
	}

	// Element which is complex and is an OME XML "Ref"
	public DetectorNode getDetector()
	{
		return (DetectorNode)
			getReferencedNode("Detector", "DetectorRef");
	}

	public DetectorRefNode getDetectorRef()
	{
		return (DetectorRefNode)
			getChildNode("DetectorRef", "DetectorRef");
	}

	// Element which is complex and is an OME XML "Ref"
	public OTFNode getOTF()
	{
		return (OTFNode)
			getReferencedNode("OTF", "OTFRef");
	}

	public OTFRefNode getOTFRef()
	{
		return (OTFRefNode)
			getChildNode("OTFRef", "OTFRef");
	}

	// Attribute
	public Integer getEmWave()
	{
		return getIntegerAttribute("EmWave");
	}

	public void setEmWave(Integer emWave)
	{
		setAttribute("EmWave", emWave);
	}

	// Attribute
	public Integer getExWave()
	{
		return getIntegerAttribute("ExWave");
	}

	public void setExWave(Integer exWave)
	{
		setAttribute("ExWave", exWave);
	}

	// Element which is complex and is an OME XML "Ref"
	public LightSourceNode getLightSource()
	{
		return (LightSourceNode)
			getReferencedNode("LightSource", "LightSourceRef");
	}

	public LightSourceRefNode getLightSourceRef()
	{
		return (LightSourceRefNode)
			getChildNode("LightSourceRef", "LightSourceRef");
	}

	// Attribute
	public String getIlluminationType()
	{
		return getStringAttribute("IlluminationType");
	}

	public void setIlluminationType(String illuminationType)
	{
		setAttribute("IlluminationType", illuminationType);
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

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
