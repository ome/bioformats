/*
 * ome.xml.r201004.Ellipse
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

public class Ellipse extends Object
{
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

	/** Constructs a Ellipse. */
	public Ellipse()
	{
	}

	// -- Ellipse API methods --

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
		// Creating XML block for Ellipse
		Element Ellipse_element = document.createElement("Ellipse");
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
		return Ellipse_element;
	}
}
