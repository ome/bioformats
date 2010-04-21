/*
 * ome.xml.r201004.Union
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
 * Created by callan via xsd-fu on 2010-04-21 11:45:19+0100
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

public class Union extends AbstractOMEModelObject
{
	// -- Instance variables --

	// Property which occurs more than once
	private List<Shape> shapeList = new ArrayList<Shape>();

	// -- Constructors --

	/** Default constructor. */
	public Union()
	{
		super();
	}

	/** 
	 * Constructs Union recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Union(Element element) throws EnumerationException
	{
		super(element);
		String tagName = element.getTagName();
		if (!"Union".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Union got %s",
					tagName));
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList Line_nodeList = element.getElementsByTagName("Line");
		for (int i = 0; i < Line_nodeList.getLength(); i++)
		{
			Element Line_element = (Element) Line_nodeList.item(i);
			addShape(
					new Line(Line_element));
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList Rectangle_nodeList = element.getElementsByTagName("Rectangle");
		for (int i = 0; i < Rectangle_nodeList.getLength(); i++)
		{
			Element Rectangle_element = (Element) Rectangle_nodeList.item(i);
			addShape(
					new Rectangle(Rectangle_element));
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList Mask_nodeList = element.getElementsByTagName("Mask");
		for (int i = 0; i < Mask_nodeList.getLength(); i++)
		{
			Element Mask_element = (Element) Mask_nodeList.item(i);
			addShape(
					new Mask(Mask_element));
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList Ellipse_nodeList = element.getElementsByTagName("Ellipse");
		for (int i = 0; i < Ellipse_nodeList.getLength(); i++)
		{
			Element Ellipse_element = (Element) Ellipse_nodeList.item(i);
			addShape(
					new Ellipse(Ellipse_element));
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList Point_nodeList = element.getElementsByTagName("Point");
		for (int i = 0; i < Point_nodeList.getLength(); i++)
		{
			Element Point_element = (Element) Point_nodeList.item(i);
			addShape(
					new Point(Point_element));
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList Polyline_nodeList = element.getElementsByTagName("Polyline");
		for (int i = 0; i < Polyline_nodeList.getLength(); i++)
		{
			Element Polyline_element = (Element) Polyline_nodeList.item(i);
			addShape(
					new Polyline(Polyline_element));
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList Path_nodeList = element.getElementsByTagName("Path");
		for (int i = 0; i < Path_nodeList.getLength(); i++)
		{
			Element Path_element = (Element) Path_nodeList.item(i);
			addShape(
					new Path(Path_element));
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList Text_nodeList = element.getElementsByTagName("Text");
		for (int i = 0; i < Text_nodeList.getLength(); i++)
		{
			Element Text_element = (Element) Text_nodeList.item(i);
			addShape(
					new Text(Text_element));
		}
	}

	// -- Union API methods --

	// Property which occurs more than once
	public int sizeOfShapeList()
	{
		return shapeList.size();
	}

	public List<Shape> copyShapeList()
	{
		return new ArrayList<Shape>(shapeList);
	}

	public Shape getShape(int index)
	{
		return shapeList.get(index);
	}

	public Shape setShape(int index, Shape shape)
	{
		return shapeList.set(index, shape);
	}

	public void addShape(Shape shape)
	{
		shapeList.add(shape);
	}

	public void removeShape(Shape shape)
	{
		shapeList.remove(shape);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Union_element)
	{
		// Creating XML block for Union
		if (Union_element == null)
		{
			Union_element = document.createElement("Union");
		}
		Union_element = super.asXMLElement(document, Union_element);

		if (shapeList != null)
		{
			// Element property Shape which is complex (has
			// sub-elements) and occurs more than once
			for (Shape shapeList_value : shapeList)
			{
				Union_element.appendChild(shapeList_value.asXMLElement(document));
			}
		}
		return Union_element;
	}
}
