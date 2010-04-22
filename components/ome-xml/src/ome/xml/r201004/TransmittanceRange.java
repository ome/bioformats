/*
 * ome.xml.r201004.TransmittanceRange
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) @year@ Open Microscopy Environment
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
 * Created by callan via xsd-fu on 2010-04-22 16:29:38+0100
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

public class TransmittanceRange extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

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

	/** Default constructor. */
	public TransmittanceRange()
	{
		super();
	}

	/** 
	 * Constructs TransmittanceRange recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public TransmittanceRange(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates TransmittanceRange recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element) throws EnumerationException
	{	
		super.update(element);
		String tagName = element.getTagName();
		if (!"TransmittanceRange".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of TransmittanceRange got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of TransmittanceRange got %s",
			//		tagName));
		}
		if (element.hasAttribute("CutIn"))
		{
			// Attribute property CutIn
			setCutIn(Integer.valueOf(
					element.getAttribute("CutIn")));
		}
		if (element.hasAttribute("Transmittance"))
		{
			// Attribute property Transmittance
			setTransmittance(Double.valueOf(
					element.getAttribute("Transmittance")));
		}
		if (element.hasAttribute("CutOut"))
		{
			// Attribute property CutOut
			setCutOut(Integer.valueOf(
					element.getAttribute("CutOut")));
		}
		if (element.hasAttribute("CutInTolerance"))
		{
			// Attribute property CutInTolerance
			setCutInTolerance(Integer.valueOf(
					element.getAttribute("CutInTolerance")));
		}
		if (element.hasAttribute("CutOutTolerance"))
		{
			// Attribute property CutOutTolerance
			setCutOutTolerance(Integer.valueOf(
					element.getAttribute("CutOutTolerance")));
		}
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
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element TransmittanceRange_element)
	{
		// Creating XML block for TransmittanceRange
		if (TransmittanceRange_element == null)
		{
			TransmittanceRange_element =
					document.createElementNS(NAMESPACE, "TransmittanceRange");
		}

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
		return super.asXMLElement(document, TransmittanceRange_element);
	}
}
