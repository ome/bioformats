/*
 * ome.xml.r201004.XMLAnnotation
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

public class XMLAnnotation extends Annotation
{
	// -- Instance variables --

	// Property
	private String value;

	// -- Constructors --

	/** Constructs a XMLAnnotation. */
	public XMLAnnotation()
	{
	}

	// -- XMLAnnotation API methods --

	// Property
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for XMLAnnotation
		Element XMLAnnotation_element = document.createElement("XMLAnnotation");
		if (value != null)
		{
			// Element property Value which is not complex (has no
			// sub-elements)
			Element value_element = document.createElement("Value");
			value_element.setTextContent(value);
			XMLAnnotation_element.appendChild(value_element);
		}
		return XMLAnnotation_element;
	}
}
