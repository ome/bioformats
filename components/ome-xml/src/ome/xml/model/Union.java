/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:35.744474
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

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/ROI/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Union.class);

	// -- Instance variables --


	// Property which occurs more than once
	private List<Shape> shapes = new ArrayList<Shape>();

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
			List<Element> Polygon_nodeList = 
					getChildrenByTagName(Shape_element, "Polygon");
			for (Element Polygon_element : Polygon_nodeList)
			{
				Polygon o = new Polygon(Shape_element, model);
				o.update(Polygon_element, model);
				addShape(o);
			}
			List<Element> Label_nodeList = 
					getChildrenByTagName(Shape_element, "Label");
			for (Element Label_element : Label_nodeList)
			{
				Label o = new Label(Shape_element, model);
				o.update(Label_element, model);
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
		return shapes.size();
	}

	public List<Shape> copyShapeList()
	{
		return new ArrayList<Shape>(shapes);
	}

	public Shape getShape(int index)
	{
		return shapes.get(index);
	}

	public Shape setShape(int index, Shape shape)
	{
        shape.setUnion(this);
		return shapes.set(index, shape);
	}

	public void addShape(Shape shape)
	{
        shape.setUnion(this);
		shapes.add(shape);
	}

	public void removeShape(Shape shape)
	{
		shapes.remove(shape);
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

		if (shapes != null)
		{
			// Element property Shape which is complex (has
			// sub-elements) and occurs more than once
			for (Shape shapes_value : shapes)
			{
				Union_element.appendChild(shapes_value.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, Union_element);
	}
}
