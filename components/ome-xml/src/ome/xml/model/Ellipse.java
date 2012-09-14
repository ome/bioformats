/*
 * ome.xml.model.Ellipse
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

public class Ellipse extends Shape
{
	// Base:  -- Name: Ellipse -- Type: Ellipse -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/ROI/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Ellipse.class);

	// -- Instance variables --


	// Property
	private Double y;

	// Property
	private Double x;

	// Property
	private Double radiusY;

	// Property
	private Double radiusX;

	// -- Constructors --

	/** Default constructor. */
	public Ellipse()
	{
		super();
	}

	/** 
	 * Constructs Ellipse recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Ellipse(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Ellipse specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Ellipse recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Ellipse".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Ellipse got {}", tagName);
		}
		if (element.hasAttribute("Y"))
		{
			// Attribute property Y
			setY(Double.valueOf(
					element.getAttribute("Y")));
		}
		if (element.hasAttribute("X"))
		{
			// Attribute property X
			setX(Double.valueOf(
					element.getAttribute("X")));
		}
		if (element.hasAttribute("RadiusY"))
		{
			// Attribute property RadiusY
			setRadiusY(Double.valueOf(
					element.getAttribute("RadiusY")));
		}
		if (element.hasAttribute("RadiusX"))
		{
			// Attribute property RadiusX
			setRadiusX(Double.valueOf(
					element.getAttribute("RadiusX")));
		}
	}

	// -- Ellipse API methods --

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
	public Double getY()
	{
		return y;
	}

	public void setY(Double y)
	{
		this.y = y;
	}

	// Property
	public Double getX()
	{
		return x;
	}

	public void setX(Double x)
	{
		this.x = x;
	}

	// Property
	public Double getRadiusY()
	{
		return radiusY;
	}

	public void setRadiusY(Double radiusY)
	{
		this.radiusY = radiusY;
	}

	// Property
	public Double getRadiusX()
	{
		return radiusX;
	}

	public void setRadiusX(Double radiusX)
	{
		this.radiusX = radiusX;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Ellipse_element)
	{
		// Creating XML block for Ellipse

		if (Ellipse_element == null)
		{
			Ellipse_element =
					document.createElementNS(NAMESPACE, "Ellipse");
		}

		if (y != null)
		{
			// Attribute property Y
			Ellipse_element.setAttribute("Y", y.toString());
		}
		if (x != null)
		{
			// Attribute property X
			Ellipse_element.setAttribute("X", x.toString());
		}
		if (radiusY != null)
		{
			// Attribute property RadiusY
			Ellipse_element.setAttribute("RadiusY", radiusY.toString());
		}
		if (radiusX != null)
		{
			// Attribute property RadiusX
			Ellipse_element.setAttribute("RadiusX", radiusX.toString());
		}
		return super.asXMLElement(document, Ellipse_element);
	}
}
