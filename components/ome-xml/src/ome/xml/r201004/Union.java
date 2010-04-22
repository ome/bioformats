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
 * Created by callan via xsd-fu on 2010-04-22 17:05:10+0100
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
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/ROI/2010-04";

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
		update(element);
	}

	/** 
	 * Updates Union recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Union".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of Union got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of Union got %s",
			//		tagName));
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		NodeList Shape_nodeList = element.getElementsByTagName("Shape");
		for (int i = 0; i < Shape_nodeList.getLength(); i++)
		{
			Element Shape_element = (Element) Shape_nodeList.item(i);
			NodeList Line_nodeList = 
					Shape_element.getElementsByTagName("Line");
			for (int j = 0; j < Line_nodeList.getLength(); j++)
			{
				Element Line_element = (Element) Line_nodeList.item(j);
				Line o = new Line(Line_element);
				o.update(Shape_element);
				addShape(o);
			}
			NodeList Rectangle_nodeList = 
					Shape_element.getElementsByTagName("Rectangle");
			for (int j = 0; j < Rectangle_nodeList.getLength(); j++)
			{
				Element Rectangle_element = (Element) Rectangle_nodeList.item(j);
				Rectangle o = new Rectangle(Rectangle_element);
				o.update(Shape_element);
				addShape(o);
			}
			NodeList Mask_nodeList = 
					Shape_element.getElementsByTagName("Mask");
			for (int j = 0; j < Mask_nodeList.getLength(); j++)
			{
				Element Mask_element = (Element) Mask_nodeList.item(j);
				Mask o = new Mask(Mask_element);
				o.update(Shape_element);
				addShape(o);
			}
			NodeList Ellipse_nodeList = 
					Shape_element.getElementsByTagName("Ellipse");
			for (int j = 0; j < Ellipse_nodeList.getLength(); j++)
			{
				Element Ellipse_element = (Element) Ellipse_nodeList.item(j);
				Ellipse o = new Ellipse(Ellipse_element);
				o.update(Shape_element);
				addShape(o);
			}
			NodeList Point_nodeList = 
					Shape_element.getElementsByTagName("Point");
			for (int j = 0; j < Point_nodeList.getLength(); j++)
			{
				Element Point_element = (Element) Point_nodeList.item(j);
				Point o = new Point(Point_element);
				o.update(Shape_element);
				addShape(o);
			}
			NodeList Polyline_nodeList = 
					Shape_element.getElementsByTagName("Polyline");
			for (int j = 0; j < Polyline_nodeList.getLength(); j++)
			{
				Element Polyline_element = (Element) Polyline_nodeList.item(j);
				Polyline o = new Polyline(Polyline_element);
				o.update(Shape_element);
				addShape(o);
			}
			NodeList Path_nodeList = 
					Shape_element.getElementsByTagName("Path");
			for (int j = 0; j < Path_nodeList.getLength(); j++)
			{
				Element Path_element = (Element) Path_nodeList.item(j);
				Path o = new Path(Path_element);
				o.update(Shape_element);
				addShape(o);
			}
			NodeList Text_nodeList = 
					Shape_element.getElementsByTagName("Text");
			for (int j = 0; j < Text_nodeList.getLength(); j++)
			{
				Element Text_element = (Element) Text_nodeList.item(j);
				Text o = new Text(Text_element);
				o.update(Shape_element);
				addShape(o);
			}
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
			Union_element =
					document.createElementNS(NAMESPACE, "Union");
		}

		if (shapeList != null)
		{
			// Element property Shape which is complex (has
			// sub-elements) and occurs more than once
			for (Shape shapeList_value : shapeList)
			{
				Union_element.appendChild(shapeList_value.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, Union_element);
	}
}
