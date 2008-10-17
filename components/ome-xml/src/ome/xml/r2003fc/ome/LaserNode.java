/*
 * ome.xml.r2003fc.ome.LaserNode
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
 * Created by curtis via xsd-fu on 2008-10-16 05:38:12-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r2003fc.ome.*;

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
	public String getMedium()
	{
		return getStringAttribute("Medium");
	}

	public void setMedium(String medium)
	{
		setAttribute("Medium", medium);
	}

	// Attribute
	public Float getPower()
	{
		return getFloatAttribute("Power");
	}

	public void setPower(Float power)
	{
		setAttribute("Power", power);
	}

	// Attribute
	public Boolean getFrequencyDoubled()
	{
		return getBooleanAttribute("FrequencyDoubled");
	}

	public void setFrequencyDoubled(Boolean frequencyDoubled)
	{
		setAttribute("FrequencyDoubled", frequencyDoubled);
	}

	// Attribute
	public Boolean getTunable()
	{
		return getBooleanAttribute("Tunable");
	}

	public void setTunable(Boolean tunable)
	{
		setAttribute("Tunable", tunable);
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
	public String getType()
	{
		return getStringAttribute("Type");
	}

	public void setType(String type)
	{
		setAttribute("Type", type);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
