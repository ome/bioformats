/*
 * ome.xml.r201004.DetectorSettings
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

public class DetectorSettings extends Settings
{
	// -- Instance variables --

	// Property
	private Binning binning;

	// Property
	private Double readOutRate;

	// Property
	private Double gain;

	// Property
	private Double offset;

	// Property
	private String id;

	// Property
	private Double voltage;

	// -- Constructors --

	/** Constructs a DetectorSettings. */
	public DetectorSettings()
	{
	}

	// -- DetectorSettings API methods --

	// Property
	public Binning getBinning()
	{
		return binning;
	}

	public void setBinning(Binning binning)
	{
		this.binning = binning;
	}

	// Property
	public Double getReadOutRate()
	{
		return readOutRate;
	}

	public void setReadOutRate(Double readOutRate)
	{
		this.readOutRate = readOutRate;
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
		// Creating XML block for DetectorSettings
		Element DetectorSettings_element = document.createElement("DetectorSettings");
		if (binning != null)
		{
			// Attribute property Binning
			DetectorSettings_element.setAttribute("Binning", binning.toString());
		}
		if (readOutRate != null)
		{
			// Attribute property ReadOutRate
			DetectorSettings_element.setAttribute("ReadOutRate", readOutRate.toString());
		}
		if (gain != null)
		{
			// Attribute property Gain
			DetectorSettings_element.setAttribute("Gain", gain.toString());
		}
		if (offset != null)
		{
			// Attribute property Offset
			DetectorSettings_element.setAttribute("Offset", offset.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			DetectorSettings_element.setAttribute("ID", id.toString());
		}
		if (voltage != null)
		{
			// Attribute property Voltage
			DetectorSettings_element.setAttribute("Voltage", voltage.toString());
		}
		return DetectorSettings_element;
	}
}
