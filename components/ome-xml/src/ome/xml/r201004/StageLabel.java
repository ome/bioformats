/*
 * ome.xml.r201004.StageLabel
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

public class StageLabel extends Object
{
	// -- Instance variables --

	// Property
	private Double y;

	// Property
	private Double x;

	// Property
	private Double z;

	// Property
	private String name;

	// -- Constructors --

	/** Constructs a StageLabel. */
	public StageLabel()
	{
	}

	// -- StageLabel API methods --

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
	public Double getZ()
	{
		return z;
	}

	public void setZ(Double z)
	{
		this.z = z;
	}

	// Property
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for StageLabel
		Element StageLabel_element = document.createElement("StageLabel");
		if (y != null)
		{
			// Attribute property Y
			StageLabel_element.setAttribute("Y", y.toString());
		}
		if (x != null)
		{
			// Attribute property X
			StageLabel_element.setAttribute("X", x.toString());
		}
		if (z != null)
		{
			// Attribute property Z
			StageLabel_element.setAttribute("Z", z.toString());
		}
		if (name != null)
		{
			// Attribute property Name
			StageLabel_element.setAttribute("Name", name.toString());
		}
		return StageLabel_element;
	}
}
