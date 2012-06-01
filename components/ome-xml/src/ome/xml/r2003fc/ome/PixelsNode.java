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

public class PixelsNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a Pixels node with an associated DOM element. */
	public PixelsNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Pixels node with an associated DOM element beneath
	 * a given parent.
	 */
	public PixelsNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Pixels node with an associated DOM element beneath
	 * a given parent.
	 */
	public PixelsNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Pixels", attach));
	}

	// -- Pixels API methods --

	// Attribute
	public Integer getSizeT()
	{
		return getIntegerAttribute("SizeT");
	}

	public void setSizeT(Integer sizeT)
	{
		setAttribute("SizeT", sizeT);
	}

	// Attribute
	public String getDimensionOrder()
	{
		return getStringAttribute("DimensionOrder");
	}

	public void setDimensionOrder(String dimensionOrder)
	{
		setAttribute("DimensionOrder", dimensionOrder);
	}

	// Attribute
	public String getPixelType()
	{
		return getStringAttribute("PixelType");
	}

	public void setPixelType(String pixelType)
	{
		setAttribute("PixelType", pixelType);
	}

	// Attribute
	public Integer getSizeX()
	{
		return getIntegerAttribute("SizeX");
	}

	public void setSizeX(Integer sizeX)
	{
		setAttribute("SizeX", sizeX);
	}

	// Attribute
	public Integer getSizeY()
	{
		return getIntegerAttribute("SizeY");
	}

	public void setSizeY(Integer sizeY)
	{
		setAttribute("SizeY", sizeY);
	}

	// Attribute
	public Integer getSizeZ()
	{
		return getIntegerAttribute("SizeZ");
	}

	public void setSizeZ(Integer sizeZ)
	{
		setAttribute("SizeZ", sizeZ);
	}

	// Element which occurs more than once
	public int getTiffDataCount()
	{
		return getChildCount("TiffData");
	}

	public java.util.Vector getTiffDataList()
	{
		return getChildNodes("TiffData");
	}

	public TiffDataNode getTiffData(int index)
	{
		return (TiffDataNode) getChildNode("TiffData", index);
	}

	// Attribute
	public Boolean getBigEndian()
	{
		return getBooleanAttribute("BigEndian");
	}

	public void setBigEndian(Boolean bigEndian)
	{
		setAttribute("BigEndian", bigEndian);
	}

	// Element which occurs more than once
	public int getBinDataCount()
	{
		return getChildCount("BinData");
	}

	public java.util.Vector getBinDataList()
	{
		return getChildNodes("BinData");
	}

	public OMEXMLNode getBinData(int index)
	{
		return (OMEXMLNode) getChildNode("BinData", index);
	}

	// Attribute
	public Integer getSizeC()
	{
		return getIntegerAttribute("SizeC");
	}

	public void setSizeC(Integer sizeC)
	{
		setAttribute("SizeC", sizeC);
	}

	// *** WARNING *** Unhandled or skipped property ID

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
