/*
 * ome.xml.r2003fc.ome.PixelsNode
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
 * Created by curtis via xsd-fu on 2008-10-15 21:58:36-0500
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
