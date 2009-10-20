/*
 * ome.xml.r200809.ome.LaserNode
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

public class LaserNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a Laser node with an associated DOM element. */
	public LaserNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Laser node with an associated DOM element beneath
	 * a given parent.
	 */
	public LaserNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Laser node with an associated DOM element beneath
	 * a given parent.
	 */
	public LaserNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Laser", attach));
	}

	// -- Laser API methods --

	// Attribute
	public Boolean getPockelCell()
	{
		return getBooleanAttribute("PockelCell");
	}

	public void setPockelCell(Boolean pockelCell)
	{
		setAttribute("PockelCell", pockelCell);
	}

	// Attribute
	public Boolean getTuneable()
	{
		return getBooleanAttribute("Tuneable");
	}

	public void setTuneable(Boolean tuneable)
	{
		setAttribute("Tuneable", tuneable);
	}

	// Attribute
	public String getLaserMedium()
	{
		return getStringAttribute("LaserMedium");
	}

	public void setLaserMedium(String laserMedium)
	{
		setAttribute("LaserMedium", laserMedium);
	}

	// Element which is complex (has sub-elements)
	public PumpNode getPump()
	{
		return (PumpNode)
			getChildNode("Pump", "Pump");
	}

	// Attribute
	public String getPulse()
	{
		return getStringAttribute("Pulse");
	}

	public void setPulse(String pulse)
	{
		setAttribute("Pulse", pulse);
	}

	// Attribute
	public Integer getWavelength()
	{
		return getIntegerAttribute("Wavelength");
	}

	public void setWavelength(Integer wavelength)
	{
		setAttribute("Wavelength", wavelength);
	}

	// Attribute
	public Integer getFrequencyMultiplication()
	{
		return getIntegerAttribute("FrequencyMultiplication");
	}

	public void setFrequencyMultiplication(Integer frequencyMultiplication)
	{
		setAttribute("FrequencyMultiplication", frequencyMultiplication);
	}

	// Attribute
	public String getType()
	{
		return getStringAttribute("Type");
	}

	public void setType(String type)
	{
		setAttribute("Type", type);
	}

	// Attribute
	public Double getRepetitionRate()
	{
		return getDoubleAttribute("RepetitionRate");
	}

	public void setRepetitionRate(Double repetitionRate)
	{
		setAttribute("RepetitionRate", repetitionRate);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
