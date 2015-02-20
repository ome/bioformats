/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import ome.xml.model.enums.*;
import ome.xml.model.enums.handlers.*;
import ome.xml.model.primitives.*;

public abstract class Shape extends AbstractOMEModelObject
{
	// Base:  -- Name: Shape -- Type: Shape -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/ROI/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Shape.class);

	// -- Instance variables --

	// StrokeDashArray property
	private String strokeDashArray;

	// StrokeWidth property
	private Length strokeWidth;

	// Locked property
	private Boolean locked;

	// FillRule property
	private FillRule fillRule;

	// Visible property
	private Boolean visible;

	// LineCap property
	private LineCap lineCap;

	// TheC property
	private NonNegativeInteger theC;

	// FontFamily property
	private FontFamily fontFamily;

	// FontStyle property
	private FontStyle fontStyle;

	// FontSize property
	private Length fontSize;

	// FillColor property
	private Color fillColor;

	// Text property
	private String text;

	// StrokeColor property
	private Color strokeColor;

	// TheT property
	private NonNegativeInteger theT;

	// ID property
	private String id;

	// TheZ property
	private NonNegativeInteger theZ;

	// *** WARNING *** Unhandled or skipped property Line

	// *** WARNING *** Unhandled or skipped property Rectangle

	// *** WARNING *** Unhandled or skipped property Mask

	// *** WARNING *** Unhandled or skipped property Ellipse

	// *** WARNING *** Unhandled or skipped property Point

	// *** WARNING *** Unhandled or skipped property Polyline

	// *** WARNING *** Unhandled or skipped property Polygon

	// *** WARNING *** Unhandled or skipped property Label

	// Transform property
	private AffineTransform transform;

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// Union_BackReference back reference
	private Union union;

	// -- Constructors --

	/** Default constructor. */
	public Shape()
	{
	}

	/**
	 * Constructs Shape recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Shape(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public Shape(Shape orig)
	{
		strokeDashArray = orig.strokeDashArray;
		strokeWidth = orig.strokeWidth;
		locked = orig.locked;
		fillRule = orig.fillRule;
		visible = orig.visible;
		lineCap = orig.lineCap;
		theC = orig.theC;
		fontFamily = orig.fontFamily;
		fontStyle = orig.fontStyle;
		fontSize = orig.fontSize;
		fillColor = orig.fillColor;
		text = orig.text;
		strokeColor = orig.strokeColor;
		theT = orig.theT;
		id = orig.id;
		theZ = orig.theZ;
		transform = orig.transform;
		annotationLinks = orig.annotationLinks;
		union = orig.union;
	}

	// -- Custom content from Shape specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates Shape recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (element.hasAttribute("StrokeDashArray"))
		{
			// Attribute property StrokeDashArray
			setStrokeDashArray(String.valueOf(
					element.getAttribute("StrokeDashArray")));
		}
		if (element.hasAttribute("StrokeWidth"))
		{
			// Attribute property StrokeWidth with unit companion StrokeWidthUnit
			String unitSymbol = element.getAttribute("StrokeWidthUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getStrokeWidthUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("StrokeWidth"));
			if (baseValue != null) 
			{
				setStrokeWidth(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("Locked"))
		{
			// Attribute property Locked
			setLocked(Boolean.valueOf(
					element.getAttribute("Locked")));
		}
		if (element.hasAttribute("FillRule"))
		{
			// Attribute property which is an enumeration FillRule
			setFillRule(FillRule.fromString(
					element.getAttribute("FillRule")));
		}
		if (element.hasAttribute("Visible"))
		{
			// Attribute property Visible
			setVisible(Boolean.valueOf(
					element.getAttribute("Visible")));
		}
		if (element.hasAttribute("LineCap"))
		{
			// Attribute property which is an enumeration LineCap
			setLineCap(LineCap.fromString(
					element.getAttribute("LineCap")));
		}
		if (element.hasAttribute("TheC"))
		{
			// Attribute property TheC
			setTheC(NonNegativeInteger.valueOf(
					element.getAttribute("TheC")));
		}
		if (element.hasAttribute("FontFamily"))
		{
			// Attribute property which is an enumeration FontFamily
			setFontFamily(FontFamily.fromString(
					element.getAttribute("FontFamily")));
		}
		if (element.hasAttribute("FontStyle"))
		{
			// Attribute property which is an enumeration FontStyle
			setFontStyle(FontStyle.fromString(
					element.getAttribute("FontStyle")));
		}
		if (element.hasAttribute("FontSize"))
		{
			// Attribute property FontSize with unit companion FontSizeUnit
			String unitSymbol = element.getAttribute("FontSizeUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getFontSizeUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			NonNegativeInteger baseValue = NonNegativeInteger.valueOf(
					element.getAttribute("FontSize"));
			if (baseValue != null) 
			{
				setFontSize(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("FillColor"))
		{
			// Attribute property FillColor
			setFillColor(Color.valueOf(
					element.getAttribute("FillColor")));
		}
		if (element.hasAttribute("Text"))
		{
			// Attribute property Text
			setText(String.valueOf(
					element.getAttribute("Text")));
		}
		if (element.hasAttribute("StrokeColor"))
		{
			// Attribute property StrokeColor
			setStrokeColor(Color.valueOf(
					element.getAttribute("StrokeColor")));
		}
		if (element.hasAttribute("TheT"))
		{
			// Attribute property TheT
			setTheT(NonNegativeInteger.valueOf(
					element.getAttribute("TheT")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Shape missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("TheZ"))
		{
			// Attribute property TheZ
			setTheZ(NonNegativeInteger.valueOf(
					element.getAttribute("TheZ")));
		}
		List<Element> Line_nodeList =
				getChildrenByTagName(element, "Line");
		if (Line_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Line node list size %d != 1",
					Line_nodeList.size()));
		}
		else if (Line_nodeList.size() != 0)
		{
		}
		List<Element> Rectangle_nodeList =
				getChildrenByTagName(element, "Rectangle");
		if (Rectangle_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Rectangle node list size %d != 1",
					Rectangle_nodeList.size()));
		}
		else if (Rectangle_nodeList.size() != 0)
		{
		}
		List<Element> Mask_nodeList =
				getChildrenByTagName(element, "Mask");
		if (Mask_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Mask node list size %d != 1",
					Mask_nodeList.size()));
		}
		else if (Mask_nodeList.size() != 0)
		{
		}
		List<Element> Ellipse_nodeList =
				getChildrenByTagName(element, "Ellipse");
		if (Ellipse_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Ellipse node list size %d != 1",
					Ellipse_nodeList.size()));
		}
		else if (Ellipse_nodeList.size() != 0)
		{
		}
		List<Element> Point_nodeList =
				getChildrenByTagName(element, "Point");
		if (Point_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Point node list size %d != 1",
					Point_nodeList.size()));
		}
		else if (Point_nodeList.size() != 0)
		{
		}
		List<Element> Polyline_nodeList =
				getChildrenByTagName(element, "Polyline");
		if (Polyline_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Polyline node list size %d != 1",
					Polyline_nodeList.size()));
		}
		else if (Polyline_nodeList.size() != 0)
		{
		}
		List<Element> Polygon_nodeList =
				getChildrenByTagName(element, "Polygon");
		if (Polygon_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Polygon node list size %d != 1",
					Polygon_nodeList.size()));
		}
		else if (Polygon_nodeList.size() != 0)
		{
		}
		List<Element> Label_nodeList =
				getChildrenByTagName(element, "Label");
		if (Label_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Label node list size %d != 1",
					Label_nodeList.size()));
		}
		else if (Label_nodeList.size() != 0)
		{
		}
		List<Element> Transform_nodeList =
				getChildrenByTagName(element, "Transform");
		if (Transform_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Transform node list size %d != 1",
					Transform_nodeList.size()));
		}
		else if (Transform_nodeList.size() != 0)
		{
		}
		// Element reference AnnotationRef
		List<Element> AnnotationRef_nodeList =
				getChildrenByTagName(element, "AnnotationRef");
		for (Element AnnotationRef_element : AnnotationRef_nodeList)
		{
			AnnotationRef annotationLinks_reference = new AnnotationRef();
			annotationLinks_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationLinks_reference);
		}
	}

	// -- Shape API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkShape(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property StrokeDashArray
	public String getStrokeDashArray()
	{
		return strokeDashArray;
	}

	public void setStrokeDashArray(String strokeDashArray)
	{
		this.strokeDashArray = strokeDashArray;
	}

	// Property StrokeWidth with unit companion StrokeWidthUnit
	public Length getStrokeWidth()
	{
		return strokeWidth;
	}

	public void setStrokeWidth(Length strokeWidth)
	{
		this.strokeWidth = strokeWidth;
	}

	// Property Locked
	public Boolean getLocked()
	{
		return locked;
	}

	public void setLocked(Boolean locked)
	{
		this.locked = locked;
	}

	// Property FillRule
	public FillRule getFillRule()
	{
		return fillRule;
	}

	public void setFillRule(FillRule fillRule)
	{
		this.fillRule = fillRule;
	}

	// Property Visible
	public Boolean getVisible()
	{
		return visible;
	}

	public void setVisible(Boolean visible)
	{
		this.visible = visible;
	}

	// Property LineCap
	public LineCap getLineCap()
	{
		return lineCap;
	}

	public void setLineCap(LineCap lineCap)
	{
		this.lineCap = lineCap;
	}

	// Property TheC
	public NonNegativeInteger getTheC()
	{
		return theC;
	}

	public void setTheC(NonNegativeInteger theC)
	{
		this.theC = theC;
	}

	// Property StrokeWidthUnit is a unit companion
	public static String getStrokeWidthUnitXsdDefault()
	{
		return "pixel";
	}

	// Property FontFamily
	public FontFamily getFontFamily()
	{
		return fontFamily;
	}

	public void setFontFamily(FontFamily fontFamily)
	{
		this.fontFamily = fontFamily;
	}

	// Property FontSizeUnit is a unit companion
	public static String getFontSizeUnitXsdDefault()
	{
		return "pt";
	}

	// Property FontStyle
	public FontStyle getFontStyle()
	{
		return fontStyle;
	}

	public void setFontStyle(FontStyle fontStyle)
	{
		this.fontStyle = fontStyle;
	}

	// Property FontSize with unit companion FontSizeUnit
	public Length getFontSize()
	{
		return fontSize;
	}

	public void setFontSize(Length fontSize)
	{
		this.fontSize = fontSize;
	}

	// Property FillColor
	public Color getFillColor()
	{
		return fillColor;
	}

	public void setFillColor(Color fillColor)
	{
		this.fillColor = fillColor;
	}

	// Property Text
	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	// Property StrokeColor
	public Color getStrokeColor()
	{
		return strokeColor;
	}

	public void setStrokeColor(Color strokeColor)
	{
		this.strokeColor = strokeColor;
	}

	// Property TheT
	public NonNegativeInteger getTheT()
	{
		return theT;
	}

	public void setTheT(NonNegativeInteger theT)
	{
		this.theT = theT;
	}

	// Property ID
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property TheZ
	public NonNegativeInteger getTheZ()
	{
		return theZ;
	}

	public void setTheZ(NonNegativeInteger theZ)
	{
		this.theZ = theZ;
	}









	// Property Transform
	public AffineTransform getTransform()
	{
		return transform;
	}

	public void setTransform(AffineTransform transform)
	{
		this.transform = transform;
	}

	// Reference which occurs more than once
	public int sizeOfLinkedAnnotationList()
	{
		return annotationLinks.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationLinks);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationLinks.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationLinks.set(index, o);
	}

	public boolean linkAnnotation(Annotation o)
	{

			o.linkShape(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkShape(this);
		return annotationLinks.remove(o);
	}

	// Property Union_BackReference
	public Union getUnion()
	{
		return union;
	}

	public void setUnion(Union union_BackReference)
	{
		this.union = union_BackReference;
	}

	protected Element asXMLElement(Document document, Element Shape_element)
	{
		// Creating XML block for Shape

		// Class is abstract so we may need to create its "container" element
		if (!"Shape".equals(Shape_element.getTagName()))
		{
			Element abstractElement =
					document.createElementNS(NAMESPACE, "Shape");
			abstractElement.appendChild(Shape_element);
			Shape_element = abstractElement;
		}
		if (Shape_element == null)
		{
			Shape_element =
					document.createElementNS(NAMESPACE, "Shape");
		}


		if (strokeDashArray != null)
		{
			// Attribute property StrokeDashArray
			Shape_element.setAttribute("StrokeDashArray", strokeDashArray.toString());
		}
		if (strokeWidth != null)
		{
			// Attribute property StrokeWidth with units companion prop.unitsCompanion.name
			if (strokeWidth.value() != null)
			{
				Shape_element.setAttribute("StrokeWidth", strokeWidth.value().toString());
			}
			if (strokeWidth.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(strokeWidth.unit().getSymbol());
					Shape_element.setAttribute("StrokeWidthUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Shape:StrokeWidthUnit: {}", e.toString());
				}
			}
		}
		if (locked != null)
		{
			// Attribute property Locked
			Shape_element.setAttribute("Locked", locked.toString());
		}
		if (fillRule != null)
		{
			// Attribute property FillRule
			Shape_element.setAttribute("FillRule", fillRule.toString());
		}
		if (visible != null)
		{
			// Attribute property Visible
			Shape_element.setAttribute("Visible", visible.toString());
		}
		if (lineCap != null)
		{
			// Attribute property LineCap
			Shape_element.setAttribute("LineCap", lineCap.toString());
		}
		if (theC != null)
		{
			// Attribute property TheC
			Shape_element.setAttribute("TheC", theC.toString());
		}
		if (fontFamily != null)
		{
			// Attribute property FontFamily
			Shape_element.setAttribute("FontFamily", fontFamily.toString());
		}
		if (fontStyle != null)
		{
			// Attribute property FontStyle
			Shape_element.setAttribute("FontStyle", fontStyle.toString());
		}
		if (fontSize != null)
		{
			// Attribute property FontSize with units companion prop.unitsCompanion.name
			if (fontSize.value() != null)
			{
				Shape_element.setAttribute("FontSize", fontSize.value().toString());
			}
			if (fontSize.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(fontSize.unit().getSymbol());
					Shape_element.setAttribute("FontSizeUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Shape:FontSizeUnit: {}", e.toString());
				}
			}
		}
		if (fillColor != null)
		{
			// Attribute property FillColor
			Shape_element.setAttribute("FillColor", fillColor.toString());
		}
		if (text != null)
		{
			// Attribute property Text
			Shape_element.setAttribute("Text", text.toString());
		}
		if (strokeColor != null)
		{
			// Attribute property StrokeColor
			Shape_element.setAttribute("StrokeColor", strokeColor.toString());
		}
		if (theT != null)
		{
			// Attribute property TheT
			Shape_element.setAttribute("TheT", theT.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Shape_element.setAttribute("ID", id.toString());
		}
		if (theZ != null)
		{
			// Attribute property TheZ
			Shape_element.setAttribute("TheZ", theZ.toString());
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Shape_element.appendChild(o.asXMLElement(document));
			}
		}

		return super.asXMLElement(document, Shape_element);
	}
}
