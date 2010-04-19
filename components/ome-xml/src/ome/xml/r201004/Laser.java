/*
 * ome.xml.r201004.Laser
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
 * Created by callan via xsd-fu on 2010-04-19 19:23:58+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ome.xml.r201004.enums.*;

public class Laser extends Object
{
	// -- Instance variables --

	// Property
	private Boolean pockelCell;

	// Property
	private Pulse pulse;

	// Property
	private LaserMedium laserMedium;

	// Property
	private Boolean tuneable;

	// Property
	private Integer wavelength;

	// Property
	private Integer frequencyMultiplication;

	// Property
	private LaserType type;

	// Property
	private Double repetitionRate;

	// Property
	private Pump pump;

	// -- Constructors --

	/** Constructs a Laser. */
	public Laser()
	{
	}

	// -- Laser API methods --

	// Property
	public Boolean getPockelCell()
	{
		return pockelCell;
	}

	public void setPockelCell(Boolean pockelCell)
	{
		this.pockelCell = pockelCell;
	}

	// Property
	public Pulse getPulse()
	{
		return pulse;
	}

	public void setPulse(Pulse pulse)
	{
		this.pulse = pulse;
	}

	// Property
	public LaserMedium getLaserMedium()
	{
		return laserMedium;
	}

	public void setLaserMedium(LaserMedium laserMedium)
	{
		this.laserMedium = laserMedium;
	}

	// Property
	public Boolean getTuneable()
	{
		return tuneable;
	}

	public void setTuneable(Boolean tuneable)
	{
		this.tuneable = tuneable;
	}

	// Property
	public Integer getWavelength()
	{
		return wavelength;
	}

	public void setWavelength(Integer wavelength)
	{
		this.wavelength = wavelength;
	}

	// Property
	public Integer getFrequencyMultiplication()
	{
		return frequencyMultiplication;
	}

	public void setFrequencyMultiplication(Integer frequencyMultiplication)
	{
		this.frequencyMultiplication = frequencyMultiplication;
	}

	// Property
	public LaserType getType()
	{
		return type;
	}

	public void setType(LaserType type)
	{
		this.type = type;
	}

	// Property
	public Double getRepetitionRate()
	{
		return repetitionRate;
	}

	public void setRepetitionRate(Double repetitionRate)
	{
		this.repetitionRate = repetitionRate;
	}

	// Property
	public Pump getPump()
	{
		return pump;
	}

	public void setPump(Pump pump)
	{
		this.pump = pump;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Laser
		Element Laser_element = document.createElement("Laser");
		if (pockelCell != null)
		{
			// Attribute property PockelCell
			Laser_element.setAttribute("PockelCell", pockelCell.toString());
		}
		if (pulse != null)
		{
			// Attribute property Pulse
			Laser_element.setAttribute("Pulse", pulse.toString());
		}
		if (laserMedium != null)
		{
			// Attribute property LaserMedium
			Laser_element.setAttribute("LaserMedium", laserMedium.toString());
		}
		if (tuneable != null)
		{
			// Attribute property Tuneable
			Laser_element.setAttribute("Tuneable", tuneable.toString());
		}
		if (wavelength != null)
		{
			// Attribute property Wavelength
			Laser_element.setAttribute("Wavelength", wavelength.toString());
		}
		if (frequencyMultiplication != null)
		{
			// Attribute property FrequencyMultiplication
			Laser_element.setAttribute("FrequencyMultiplication", frequencyMultiplication.toString());
		}
		if (type != null)
		{
			// Attribute property Type
			Laser_element.setAttribute("Type", type.toString());
		}
		if (repetitionRate != null)
		{
			// Attribute property RepetitionRate
			Laser_element.setAttribute("RepetitionRate", repetitionRate.toString());
		}
		if (pump != null)
		{
			// Element property Pump which is complex (has
			// sub-elements)
			Laser_element.appendChild(pump.asXMLElement(document));
		}
		return Laser_element;
	}
}
