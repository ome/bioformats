/*
 * ome.xml.r201004.Microscope
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

public class Microscope extends ManufacturerSpec
{
	// -- Instance variables --

	// Property
	private MicroscopeType type;

	// -- Constructors --

	/** Constructs a Microscope. */
	public Microscope()
	{
	}

	// -- Microscope API methods --

	// Property
	public MicroscopeType getType()
	{
		return type;
	}

	public void setType(MicroscopeType type)
	{
		this.type = type;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Microscope
		Element Microscope_element = document.createElement("Microscope");
		if (type != null)
		{
			// Attribute property Type
			Microscope_element.setAttribute("Type", type.toString());
		}
		return Microscope_element;
	}
}
