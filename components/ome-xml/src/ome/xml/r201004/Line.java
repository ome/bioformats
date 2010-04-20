/*
 * ome.xml.r201004.Line
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

public class Line extends Object
{
	// -- Instance variables --

	// Property
	private Double y1;

	// Property
	private Double x2;

	// Property
	private Double x1;

	// Property
	private Double y2;

	// -- Constructors --

	/** Constructs a Line. */
	public Line()
	{
	}

	// -- Line API methods --

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
	public Double getX2()
	{
		return x2;
	}

	public void setX2(Double x2)
	{
		this.x2 = x2;
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
	public Double getY2()
	{
		return y2;
	}

	public void setY2(Double y2)
	{
		this.y2 = y2;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Line
		Element Line_element = document.createElement("Line");
		if (y1 != null)
		{
			// Attribute property Y1
			Line_element.setAttribute("Y1", y1.toString());
		}
		if (x2 != null)
		{
			// Attribute property X2
			Line_element.setAttribute("X2", x2.toString());
		}
		if (x1 != null)
		{
			// Attribute property X1
			Line_element.setAttribute("X1", x1.toString());
		}
		if (y2 != null)
		{
			// Attribute property Y2
			Line_element.setAttribute("Y2", y2.toString());
		}
		return Line_element;
	}

	public static Line fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"Line".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Line got %s",
					tagName));
		}
		Line instance = new Line();
		if (element.hasAttribute("Y1"))
		{
			// Attribute property Y1
			instance.setY1(Double.valueOf(
					element.getAttribute("Y1")));
		}
		if (element.hasAttribute("X2"))
		{
			// Attribute property X2
			instance.setX2(Double.valueOf(
					element.getAttribute("X2")));
		}
		if (element.hasAttribute("X1"))
		{
			// Attribute property X1
			instance.setX1(Double.valueOf(
					element.getAttribute("X1")));
		}
		if (element.hasAttribute("Y2"))
		{
			// Attribute property Y2
			instance.setY2(Double.valueOf(
					element.getAttribute("Y2")));
		}
		return instance;
	}
}
