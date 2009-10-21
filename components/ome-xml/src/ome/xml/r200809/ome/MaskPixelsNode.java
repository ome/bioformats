/*
 * ome.xml.r200809.ome.MaskPixelsNode
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
 * Created by curtis via xsd-fu on 2008-10-17 01:41:39-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200809.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200809.ome.*;
import ome.xml.r200809.spw.*;

import org.w3c.dom.Element;

public class MaskPixelsNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a MaskPixels node with an associated DOM element. */
	public MaskPixelsNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a MaskPixels node with an associated DOM element beneath
	 * a given parent.
	 */
	public MaskPixelsNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a MaskPixels node with an associated DOM element beneath
	 * a given parent.
	 */
	public MaskPixelsNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "MaskPixels", attach));
	}

	// -- MaskPixels API methods --

	// Attribute
	public String getExtendedPixelType()
	{
		return getStringAttribute("ExtendedPixelType");
	}

	public void setExtendedPixelType(String extendedPixelType)
	{
		setAttribute("ExtendedPixelType", extendedPixelType);
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

	// Element which is complex (has sub-elements)
	public TiffDataNode getTiffData()
	{
		return (TiffDataNode)
			getChildNode("TiffData", "TiffData");
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

	// Element which is not complex (has only a text node)
	public String getBinData()
	{
		return getStringCData("BinData");
	}

	public void setBinData(String binData)
	{
		setCData("BinData", binData);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
