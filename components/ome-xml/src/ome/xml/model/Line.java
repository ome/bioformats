/*
 * ome.xml.model.Line
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
 * Created by melissa via xsd-fu on 2012-09-10 13:40:21-0400
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

public class Line extends Shape
{
	// Base:  -- Name: Line -- Type: Line -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/ROI/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Line.class);

	// -- Instance variables --


	// Property
	private Double y2;

	// Property
	private Double x2;

	// Property
	private Marker markerEnd;

	// Property
	private Double y1;

	// Property
	private Double x1;

	// Property
	private Marker markerStart;

	// -- Constructors --

	/** Default constructor. */
	public Line()
	{
		super();
	}

	/** 
	 * Constructs Line recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Line(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Line specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Line recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element, OMEModel model)
	    throws EnumerationException
	{
		super.update(element, model);
		String tagName = element.getTagName();
		if (!"Line".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Line got {}", tagName);
		}
		if (element.hasAttribute("Y2"))
		{
			// Attribute property Y2
			setY2(Double.valueOf(
					element.getAttribute("Y2")));
		}
		if (element.hasAttribute("X2"))
		{
			// Attribute property X2
			setX2(Double.valueOf(
					element.getAttribute("X2")));
		}
		if (element.hasAttribute("MarkerEnd"))
		{
			// Attribute property which is an enumeration MarkerEnd
			setMarkerEnd(Marker.fromString(
					element.getAttribute("MarkerEnd")));
		}
		if (element.hasAttribute("Y1"))
		{
			// Attribute property Y1
			setY1(Double.valueOf(
					element.getAttribute("Y1")));
		}
		if (element.hasAttribute("X1"))
		{
			// Attribute property X1
			setX1(Double.valueOf(
					element.getAttribute("X1")));
		}
		if (element.hasAttribute("MarkerStart"))
		{
			// Attribute property which is an enumeration MarkerStart
			setMarkerStart(Marker.fromString(
					element.getAttribute("MarkerStart")));
		}
	}

	// -- Line API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public Double getY2()
	{
		return y2;
	}

	public void setY2(Double y2)
	{
		this.y2 = y2;
	}

	// Property
	public Double getX2()
	{
		return x2;
	}

	public void setX2(Double x2)
	{
		this.x2 = x2;
	}

	// Property
	public Marker getMarkerEnd()
	{
		return markerEnd;
	}

	public void setMarkerEnd(Marker markerEnd)
	{
		this.markerEnd = markerEnd;
	}

	// Property
	public Double getY1()
	{
		return y1;
	}

	public void setY1(Double y1)
	{
		this.y1 = y1;
	}

	// Property
	public Double getX1()
	{
		return x1;
	}

	public void setX1(Double x1)
	{
		this.x1 = x1;
	}

	// Property
	public Marker getMarkerStart()
	{
		return markerStart;
	}

	public void setMarkerStart(Marker markerStart)
	{
		this.markerStart = markerStart;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Line_element)
	{
		// Creating XML block for Line

		if (Line_element == null)
		{
			Line_element =
					document.createElementNS(NAMESPACE, "Line");
		}

		if (y2 != null)
		{
			// Attribute property Y2
			Line_element.setAttribute("Y2", y2.toString());
		}
		if (x2 != null)
		{
			// Attribute property X2
			Line_element.setAttribute("X2", x2.toString());
		}
		if (markerEnd != null)
		{
			// Attribute property MarkerEnd
			Line_element.setAttribute("MarkerEnd", markerEnd.toString());
		}
		if (y1 != null)
		{
			// Attribute property Y1
			Line_element.setAttribute("Y1", y1.toString());
		}
		if (x1 != null)
		{
			// Attribute property X1
			Line_element.setAttribute("X1", x1.toString());
		}
		if (markerStart != null)
		{
			// Attribute property MarkerStart
			Line_element.setAttribute("MarkerStart", markerStart.toString());
		}
		return super.asXMLElement(document, Line_element);
	}
}
