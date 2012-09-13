/*
 * ome.xml.model.Polyline
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

public class Polyline extends Shape
{
	// Base:  -- Name: Polyline -- Type: Polyline -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/ROI/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Polyline.class);

	// -- Instance variables --


	// Property
	private String points;

	// Property
	private Marker markerEnd;

	// Property
	private Marker markerStart;

	// -- Constructors --

	/** Default constructor. */
	public Polyline()
	{
		super();
	}

	/** 
	 * Constructs Polyline recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Polyline(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Polyline specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Polyline recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Polyline".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Polyline got {}", tagName);
		}
		if (element.hasAttribute("Points"))
		{
			// Attribute property Points
			setPoints(String.valueOf(
					element.getAttribute("Points")));
		}
		if (element.hasAttribute("MarkerEnd"))
		{
			// Attribute property which is an enumeration MarkerEnd
			setMarkerEnd(Marker.fromString(
					element.getAttribute("MarkerEnd")));
		}
		if (element.hasAttribute("MarkerStart"))
		{
			// Attribute property which is an enumeration MarkerStart
			setMarkerStart(Marker.fromString(
					element.getAttribute("MarkerStart")));
		}
	}

	// -- Polyline API methods --

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
	public String getPoints()
	{
		return points;
	}

	public void setPoints(String points)
	{
		this.points = points;
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

	protected Element asXMLElement(Document document, Element Polyline_element)
	{
		// Creating XML block for Polyline

		if (Polyline_element == null)
		{
			Polyline_element =
					document.createElementNS(NAMESPACE, "Polyline");
		}

		if (points != null)
		{
			// Attribute property Points
			Polyline_element.setAttribute("Points", points.toString());
		}
		if (markerEnd != null)
		{
			// Attribute property MarkerEnd
			Polyline_element.setAttribute("MarkerEnd", markerEnd.toString());
		}
		if (markerStart != null)
		{
			// Attribute property MarkerStart
			Polyline_element.setAttribute("MarkerStart", markerStart.toString());
		}
		return super.asXMLElement(document, Polyline_element);
	}
}
