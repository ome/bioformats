/*
 * ome.xml.r201004.Detector
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

public class Detector extends ManufacturerSpec
{
	// -- Instance variables --

	// Property
	private Double zoom;

	// Property
	private Double amplificationGain;

	// Property
	private Double gain;

	// Property
	private Double offset;

	// Property
	private DetectorType type;

	// Property
	private String id;

	// Property
	private Double voltage;

	// -- Constructors --

	/** Constructs a Detector. */
	public Detector()
	{
	}

	// -- Detector API methods --

	// Property
	public Double getZoom()
	{
		return zoom;
	}

	public void setZoom(Double zoom)
	{
		this.zoom = zoom;
	}

	// Property
	public Double getAmplificationGain()
	{
		return amplificationGain;
	}

	public void setAmplificationGain(Double amplificationGain)
	{
		this.amplificationGain = amplificationGain;
	}

	// Property
	public Double getGain()
	{
		return gain;
	}

	public void setGain(Double gain)
	{
		this.gain = gain;
	}

	// Property
	public Double getOffset()
	{
		return offset;
	}

	public void setOffset(Double offset)
	{
		this.offset = offset;
	}

	// Property
	public DetectorType getType()
	{
		return type;
	}

	public void setType(DetectorType type)
	{
		this.type = type;
	}

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
	public Double getVoltage()
	{
		return voltage;
	}

	public void setVoltage(Double voltage)
	{
		this.voltage = voltage;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Detector
		Element Detector_element = document.createElement("Detector");
		if (zoom != null)
		{
			// Attribute property Zoom
			Detector_element.setAttribute("Zoom", zoom.toString());
		}
		if (amplificationGain != null)
		{
			// Attribute property AmplificationGain
			Detector_element.setAttribute("AmplificationGain", amplificationGain.toString());
		}
		if (gain != null)
		{
			// Attribute property Gain
			Detector_element.setAttribute("Gain", gain.toString());
		}
		if (offset != null)
		{
			// Attribute property Offset
			Detector_element.setAttribute("Offset", offset.toString());
		}
		if (type != null)
		{
			// Attribute property Type
			Detector_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Detector_element.setAttribute("ID", id.toString());
		}
		if (voltage != null)
		{
			// Attribute property Voltage
			Detector_element.setAttribute("Voltage", voltage.toString());
		}
		return Detector_element;
	}

	public static Detector fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"Detector".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Detector got %s",
					tagName));
		}
		Detector instance = new Detector();
		if (element.hasAttribute("Zoom"))
		{
			// Attribute property Zoom
			instance.setZoom(Double.valueOf(
					element.getAttribute("Zoom")));
		}
		if (element.hasAttribute("AmplificationGain"))
		{
			// Attribute property AmplificationGain
			instance.setAmplificationGain(Double.valueOf(
					element.getAttribute("AmplificationGain")));
		}
		if (element.hasAttribute("Gain"))
		{
			// Attribute property Gain
			instance.setGain(Double.valueOf(
					element.getAttribute("Gain")));
		}
		if (element.hasAttribute("Offset"))
		{
			// Attribute property Offset
			instance.setOffset(Double.valueOf(
					element.getAttribute("Offset")));
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			instance.setType(DetectorType.fromString(
					element.getAttribute("Type")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			instance.setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("Voltage"))
		{
			// Attribute property Voltage
			instance.setVoltage(Double.valueOf(
					element.getAttribute("Voltage")));
		}
		return instance;
	}
}
