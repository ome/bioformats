/*
 * ome.xml.r201004.LightSource
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2010 Open Microscopy Environment
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
 * Created by callan via xsd-fu on 2010-04-20 12:31:20+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.r201004.enums.*;

public class LightSource extends ManufacturerSpec
{
	// -- Instance variables --

	// Property
	private String id;

	// Property
	private Double power;

	// Property
	private Laser laser;

	// Property
	private Filament filament;

	// Property
	private Arc arc;

	// Property
	private LightEmittingDiode lightEmittingDiode;

	// -- Constructors --

	/** Constructs a LightSource. */
	public LightSource()
	{
	}

	// -- LightSource API methods --

	// Property
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property
	public Double getPower()
	{
		return power;
	}

	public void setPower(Double power)
	{
		this.power = power;
	}

	// Property
	public Laser getLaser()
	{
		return laser;
	}

	public void setLaser(Laser laser)
	{
		this.laser = laser;
	}

	// Property
	public Filament getFilament()
	{
		return filament;
	}

	public void setFilament(Filament filament)
	{
		this.filament = filament;
	}

	// Property
	public Arc getArc()
	{
		return arc;
	}

	public void setArc(Arc arc)
	{
		this.arc = arc;
	}

	// Property
	public LightEmittingDiode getLightEmittingDiode()
	{
		return lightEmittingDiode;
	}

	public void setLightEmittingDiode(LightEmittingDiode lightEmittingDiode)
	{
		this.lightEmittingDiode = lightEmittingDiode;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for LightSource
		Element LightSource_element = document.createElement("LightSource");
		if (id != null)
		{
			// Attribute property ID
			LightSource_element.setAttribute("ID", id.toString());
		}
		if (power != null)
		{
			// Attribute property Power
			LightSource_element.setAttribute("Power", power.toString());
		}
		if (laser != null)
		{
			// Element property Laser which is complex (has
			// sub-elements)
			LightSource_element.appendChild(laser.asXMLElement(document));
		}
		if (filament != null)
		{
			// Element property Filament which is complex (has
			// sub-elements)
			LightSource_element.appendChild(filament.asXMLElement(document));
		}
		if (arc != null)
		{
			// Element property Arc which is complex (has
			// sub-elements)
			LightSource_element.appendChild(arc.asXMLElement(document));
		}
		if (lightEmittingDiode != null)
		{
			// Element property LightEmittingDiode which is complex (has
			// sub-elements)
			LightSource_element.appendChild(lightEmittingDiode.asXMLElement(document));
		}
		return LightSource_element;
	}

	public static LightSource fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"LightSource".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of LightSource got %s",
					tagName));
		}
		LightSource instance = new LightSource();
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			instance.setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("Power"))
		{
			// Attribute property Power
			instance.setPower(Double.valueOf(
					element.getAttribute("Power")));
		}
		NodeList Laser_nodeList = element.getElementsByTagName("Laser");
		if (Laser_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Laser node list size %d != 1",
					Laser_nodeList.getLength()));
		}
		else if (Laser_nodeList.getLength() != 0)
		{
			// Element property Laser which is complex (has
			// sub-elements)
			instance.setLaser(Laser.fromXMLElement(
					(Element) Laser_nodeList.item(0)));
		}
		NodeList Filament_nodeList = element.getElementsByTagName("Filament");
		if (Filament_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Filament node list size %d != 1",
					Filament_nodeList.getLength()));
		}
		else if (Filament_nodeList.getLength() != 0)
		{
			// Element property Filament which is complex (has
			// sub-elements)
			instance.setFilament(Filament.fromXMLElement(
					(Element) Filament_nodeList.item(0)));
		}
		NodeList Arc_nodeList = element.getElementsByTagName("Arc");
		if (Arc_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Arc node list size %d != 1",
					Arc_nodeList.getLength()));
		}
		else if (Arc_nodeList.getLength() != 0)
		{
			// Element property Arc which is complex (has
			// sub-elements)
			instance.setArc(Arc.fromXMLElement(
					(Element) Arc_nodeList.item(0)));
		}
		NodeList LightEmittingDiode_nodeList = element.getElementsByTagName("LightEmittingDiode");
		if (LightEmittingDiode_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"LightEmittingDiode node list size %d != 1",
					LightEmittingDiode_nodeList.getLength()));
		}
		else if (LightEmittingDiode_nodeList.getLength() != 0)
		{
			// Element property LightEmittingDiode which is complex (has
			// sub-elements)
			instance.setLightEmittingDiode(LightEmittingDiode.fromXMLElement(
					(Element) LightEmittingDiode_nodeList.item(0)));
		}
		return instance;
	}
}
