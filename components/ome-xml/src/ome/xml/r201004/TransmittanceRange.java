/*
 * ome.xml.r201004.TransmittanceRange
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

public class TransmittanceRange extends Object
{
	// -- Instance variables --

	// Property
	private Integer cutIn;

	// Property
	private Double transmittance;

	// Property
	private Integer cutOut;

	// Property
	private Integer cutInTolerance;

	// Property
	private Integer cutOutTolerance;

	// -- Constructors --

	/** Constructs a TransmittanceRange. */
	public TransmittanceRange()
	{
	}

	// -- TransmittanceRange API methods --

	// Property
	public Integer getCutIn()
	{
		return cutIn;
	}

	public void setCutIn(Integer cutIn)
	{
		this.cutIn = cutIn;
	}

	// Property
	public Double getTransmittance()
	{
		return transmittance;
	}

	public void setTransmittance(Double transmittance)
	{
		this.transmittance = transmittance;
	}

	// Property
	public Integer getCutOut()
	{
		return cutOut;
	}

	public void setCutOut(Integer cutOut)
	{
		this.cutOut = cutOut;
	}

	// Property
	public Integer getCutInTolerance()
	{
		return cutInTolerance;
	}

	public void setCutInTolerance(Integer cutInTolerance)
	{
		this.cutInTolerance = cutInTolerance;
	}

	// Property
	public Integer getCutOutTolerance()
	{
		return cutOutTolerance;
	}

	public void setCutOutTolerance(Integer cutOutTolerance)
	{
		this.cutOutTolerance = cutOutTolerance;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for TransmittanceRange
		Element TransmittanceRange_element = document.createElement("TransmittanceRange");
		if (cutIn != null)
		{
			// Attribute property CutIn
			TransmittanceRange_element.setAttribute("CutIn", cutIn.toString());
		}
		if (transmittance != null)
		{
			// Attribute property Transmittance
			TransmittanceRange_element.setAttribute("Transmittance", transmittance.toString());
		}
		if (cutOut != null)
		{
			// Attribute property CutOut
			TransmittanceRange_element.setAttribute("CutOut", cutOut.toString());
		}
		if (cutInTolerance != null)
		{
			// Attribute property CutInTolerance
			TransmittanceRange_element.setAttribute("CutInTolerance", cutInTolerance.toString());
		}
		if (cutOutTolerance != null)
		{
			// Attribute property CutOutTolerance
			TransmittanceRange_element.setAttribute("CutOutTolerance", cutOutTolerance.toString());
		}
		return TransmittanceRange_element;
	}
}
