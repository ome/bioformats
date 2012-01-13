/*
 * ome.xml.model.Union
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
 * Created by melissa via xsd-fu on 2012-01-12 20:06:01-0500
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

public class Union extends AbstractOMEModelObject
{
	// Base:  -- Name: Union -- Type: Union -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/ROI/2011-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Union.class);

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
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Union(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Union specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Union recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Union".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Union got {}", tagName);
		}
		// Element property Shape which is complex (has
		// sub-elements) and occurs more than once. The element's model
		// object type is also abstract so we need to have a handler for
		// each "subclass".
		List<Element> Shape_nodeList =
				getChildrenByTagName(element, "Shape");
		for (Element Shape_element : Shape_nodeList)
		{
			List<Element> Line_nodeList = 
					getChildrenByTagName(Shape_element, "Line");
			for (Element Line_element : Line_nodeList)
			{
				Line o = new Line(Shape_element, model);
				o.update(Line_element, model);
				addShape(o);
			}
			List<Element> Rectangle_nodeList = 
					getChildrenByTagName(Shape_element, "Rectangle");
			for (Element Rectangle_element : Rectangle_nodeList)
			{
				Rectangle o = new Rectangle(Shape_element, model);
				o.update(Rectangle_element, model);
				addShape(o);
			}
			List<Element> Mask_nodeList = 
					getChildrenByTagName(Shape_element, "Mask");
			for (Element Mask_element : Mask_nodeList)
			{
				Mask o = new Mask(Shape_element, model);
				o.update(Mask_element, model);
				addShape(o);
			}
			List<Element> Ellipse_nodeList = 
					getChildrenByTagName(Shape_element, "Ellipse");
			for (Element Ellipse_element : Ellipse_nodeList)
			{
				Ellipse o = new Ellipse(Shape_element, model);
				o.update(Ellipse_element, model);
				addShape(o);
			}
			List<Element> Point_nodeList = 
					getChildrenByTagName(Shape_element, "Point");
			for (Element Point_element : Point_nodeList)
			{
				Point o = new Point(Shape_element, model);
				o.update(Point_element, model);
				addShape(o);
			}
			List<Element> Polyline_nodeList = 
					getChildrenByTagName(Shape_element, "Polyline");
			for (Element Polyline_element : Polyline_nodeList)
			{
				Polyline o = new Polyline(Shape_element, model);
				o.update(Polyline_element, model);
				addShape(o);
			}
			List<Element> Path_nodeList = 
					getChildrenByTagName(Shape_element, "Path");
			for (Element Path_element : Path_nodeList)
			{
				Path o = new Path(Shape_element, model);
				o.update(Path_element, model);
				addShape(o);
			}
			List<Element> Text_nodeList = 
					getChildrenByTagName(Shape_element, "Text");
			for (Element Text_element : Text_nodeList)
			{
				Text o = new Text(Shape_element, model);
				o.update(Text_element, model);
				addShape(o);
			}
		}
	}

	// -- Union API methods --

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
