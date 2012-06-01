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

public class ChannelInfoNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a ChannelInfo node with an associated DOM element. */
	public ChannelInfoNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a ChannelInfo node with an associated DOM element beneath
	 * a given parent.
	 */
	public ChannelInfoNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a ChannelInfo node with an associated DOM element beneath
	 * a given parent.
	 */
	public ChannelInfoNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "ChannelInfo", attach));
	}

	// -- ChannelInfo API methods --

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
	public Integer getPinholeSize()
	{
		return getIntegerAttribute("PinholeSize");
	}

	public void setPinholeSize(Integer pinholeSize)
	{
		setAttribute("PinholeSize", pinholeSize);
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

	// Element which is complex and is an OME XML "Ref"
	public LightSourceNode getAuxLightSource()
	{
		return (LightSourceNode)
			getReferencedNode("LightSource", "AuxLightSourceRef");
	}

	public AuxLightSourceRefNode getAuxLightSourceRef()
	{
		return (AuxLightSourceRefNode)
			getChildNode("LightSourceRef", "AuxLightSourceRef");
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

	// *** WARNING *** Unhandled or skipped property ID

	// Element which is complex and is an OME XML "Ref"
	public FilterNode getFilter()
	{
		return (FilterNode)
			getReferencedNode("Filter", "FilterRef");
	}

	public FilterRefNode getFilterRef()
	{
		return (FilterRefNode)
			getChildNode("FilterRef", "FilterRef");
	}

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
	public String getPhotometricInterpretation()
	{
		return getStringAttribute("PhotometricInterpretation");
	}

	public void setPhotometricInterpretation(String photometricInterpretation)
	{
		setAttribute("PhotometricInterpretation", photometricInterpretation);
	}

	// Attribute
	public Double getNDfilter()
	{
		return getDoubleAttribute("NDfilter");
	}

	public void setNDfilter(Double ndfilter)
	{
		setAttribute("NDfilter", ndfilter);
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

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
