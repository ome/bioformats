
/*
 * ome.xml.r201004.Shape
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
 * Created by callan via xsd-fu on 2010-04-20 18:27:32+0100
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

public abstract class Shape extends AbstractOMEModelObject
{
	// -- Instance variables --

	// Property
	private String strokeDashArray;

	// Property
	private Double strokeWidth;

	// Property
	private FillRule fillRule;

	// Property
	private LineCap lineCap;

	// Property
	private Integer theC;

	// Property
	private Integer theT;

	// Property
	private String transform;

	// Property
	private String label;

	// Property
	private FontFamily fontFamily;

	// Property
	private Integer stroke;

	// Property
	private FontStyle fontStyle;

	// Property
	private Marker markerEnd;

	// Property
	private Integer theZ;

	// Property
	private Integer fontSize;

	// Property
	private String id;

	// Property
	private Integer fill;

	// Property
	private Marker markerStart;

	// Property
	private String name;

	// *** WARNING *** Unhandled or skipped property Line

	// *** WARNING *** Unhandled or skipped property Rectangle

	// *** WARNING *** Unhandled or skipped property Mask

	// *** WARNING *** Unhandled or skipped property Ellipse

	// *** WARNING *** Unhandled or skipped property Point

	// *** WARNING *** Unhandled or skipped property Polyline

	// *** WARNING *** Unhandled or skipped property Path

	// *** WARNING *** Unhandled or skipped property Text

	// Back reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// *** WARNING *** Unhandled or skipped property Description

	// -- Constructors --

	/** Default constructor. */
	public Shape()
	{
		super();
	}

	/** 
	 * Constructs Shape recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Shape(Element element) throws EnumerationException
	{
		super(element);
		String tagName = element.getTagName();
		if (!"Shape".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Shape got %s",
					tagName));
		}
		// Model object: None
		if (element.hasAttribute("StrokeDashArray"))
		{
			// Attribute property StrokeDashArray
			setStrokeDashArray(String.valueOf(
					element.getAttribute("StrokeDashArray")));
		}
		// Model object: None
		if (element.hasAttribute("StrokeWidth"))
		{
			// Attribute property StrokeWidth
			setStrokeWidth(Double.valueOf(
					element.getAttribute("StrokeWidth")));
		}
		// Model object: None
		if (element.hasAttribute("FillRule"))
		{
			// Attribute property which is an enumeration FillRule
			setFillRule(FillRule.fromString(
					element.getAttribute("FillRule")));
		}
		// Model object: None
		if (element.hasAttribute("LineCap"))
		{
			// Attribute property which is an enumeration LineCap
			setLineCap(LineCap.fromString(
					element.getAttribute("LineCap")));
		}
		// Model object: None
		if (element.hasAttribute("TheC"))
		{
			// Attribute property TheC
			setTheC(Integer.valueOf(
					element.getAttribute("TheC")));
		}
		// Model object: None
		if (element.hasAttribute("TheT"))
		{
			// Attribute property TheT
			setTheT(Integer.valueOf(
					element.getAttribute("TheT")));
		}
		// Model object: None
		if (element.hasAttribute("Transform"))
		{
			// Attribute property Transform
			setTransform(String.valueOf(
					element.getAttribute("Transform")));
		}
		// Model object: None
		if (element.hasAttribute("Label"))
		{
			// Attribute property Label
			setLabel(String.valueOf(
					element.getAttribute("Label")));
		}
		// Model object: None
		if (element.hasAttribute("FontFamily"))
		{
			// Attribute property which is an enumeration FontFamily
			setFontFamily(FontFamily.fromString(
					element.getAttribute("FontFamily")));
		}
		// Model object: None
		if (element.hasAttribute("Stroke"))
		{
			// Attribute property Stroke
			setStroke(Integer.valueOf(
					element.getAttribute("Stroke")));
		}
		// Model object: None
		if (element.hasAttribute("FontStyle"))
		{
			// Attribute property which is an enumeration FontStyle
			setFontStyle(FontStyle.fromString(
					element.getAttribute("FontStyle")));
		}
		// Model object: None
		if (element.hasAttribute("MarkerEnd"))
		{
			// Attribute property which is an enumeration MarkerEnd
			setMarkerEnd(Marker.fromString(
					element.getAttribute("MarkerEnd")));
		}
		// Model object: None
		if (element.hasAttribute("TheZ"))
		{
			// Attribute property TheZ
			setTheZ(Integer.valueOf(
					element.getAttribute("TheZ")));
		}
		// Model object: None
		if (element.hasAttribute("FontSize"))
		{
			// Attribute property FontSize
			setFontSize(Integer.valueOf(
					element.getAttribute("FontSize")));
		}
		// Model object: None
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		// Model object: None
		if (element.hasAttribute("Fill"))
		{
			// Attribute property Fill
			setFill(Integer.valueOf(
					element.getAttribute("Fill")));
		}
		// Model object: None
		if (element.hasAttribute("MarkerStart"))
		{
			// Attribute property which is an enumeration MarkerStart
			setMarkerStart(Marker.fromString(
					element.getAttribute("MarkerStart")));
		}
		// Model object: None
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		// Model object: None
		NodeList Line_nodeList = element.getElementsByTagName("Line");
		if (Line_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Line node list size %d != 1",
					Line_nodeList.getLength()));
		}
		else if (Line_nodeList.getLength() != 0)
		{
		}
		// Model object: None
		NodeList Rectangle_nodeList = element.getElementsByTagName("Rectangle");
		if (Rectangle_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Rectangle node list size %d != 1",
					Rectangle_nodeList.getLength()));
		}
		else if (Rectangle_nodeList.getLength() != 0)
		{
		}
		// Model object: None
		NodeList Mask_nodeList = element.getElementsByTagName("Mask");
		if (Mask_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Mask node list size %d != 1",
					Mask_nodeList.getLength()));
		}
		else if (Mask_nodeList.getLength() != 0)
		{
		}
		// Model object: None
		NodeList Ellipse_nodeList = element.getElementsByTagName("Ellipse");
		if (Ellipse_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Ellipse node list size %d != 1",
					Ellipse_nodeList.getLength()));
		}
		else if (Ellipse_nodeList.getLength() != 0)
		{
		}
		// Model object: None
		NodeList Point_nodeList = element.getElementsByTagName("Point");
		if (Point_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Point node list size %d != 1",
					Point_nodeList.getLength()));
		}
		else if (Point_nodeList.getLength() != 0)
		{
		}
		// Model object: None
		NodeList Polyline_nodeList = element.getElementsByTagName("Polyline");
		if (Polyline_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Polyline node list size %d != 1",
					Polyline_nodeList.getLength()));
		}
		else if (Polyline_nodeList.getLength() != 0)
		{
		}
		// Model object: None
		NodeList Path_nodeList = element.getElementsByTagName("Path");
		if (Path_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Path node list size %d != 1",
					Path_nodeList.getLength()));
		}
		else if (Path_nodeList.getLength() != 0)
		{
		}
		// Model object: None
		NodeList Text_nodeList = element.getElementsByTagName("Text");
		if (Text_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Text node list size %d != 1",
					Text_nodeList.getLength()));
		}
		else if (Text_nodeList.getLength() != 0)
		{
		}
		// Model object: None
		// *** IGNORING *** Skipped back reference AnnotationRef
		// Model object: None
		// *** WARNING *** Unhandled or skipped property Description
	}

	// -- Shape API methods --

	// Property
	public String getStrokeDashArray()
	{
		return strokeDashArray;
	}

	public void setStrokeDashArray(String strokeDashArray)
	{
		this.strokeDashArray = strokeDashArray;
	}

	// Property
	public Double getStrokeWidth()
	{
		return strokeWidth;
	}

	public void setStrokeWidth(Double strokeWidth)
	{
		this.strokeWidth = strokeWidth;
	}

	// Property
	public FillRule getFillRule()
	{
		return fillRule;
	}

	public void setFillRule(FillRule fillRule)
	{
		this.fillRule = fillRule;
	}

	// Property
	public LineCap getLineCap()
	{
		return lineCap;
	}

	public void setLineCap(LineCap lineCap)
	{
		this.lineCap = lineCap;
	}

	// Property
	public Integer getTheC()
	{
		return theC;
	}

	public void setTheC(Integer theC)
	{
		this.theC = theC;
	}

	// Property
	public Integer getTheT()
	{
		return theT;
	}

	public void setTheT(Integer theT)
	{
		this.theT = theT;
	}

	// Property
	public String getTransform()
	{
		return transform;
	}

	public void setTransform(String transform)
	{
		this.transform = transform;
	}

	// Property
	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	// Property
	public FontFamily getFontFamily()
	{
		return fontFamily;
	}

	public void setFontFamily(FontFamily fontFamily)
	{
		this.fontFamily = fontFamily;
	}

	// Property
	public Integer getStroke()
	{
		return stroke;
	}

	public void setStroke(Integer stroke)
	{
		this.stroke = stroke;
	}

	// Property
	public FontStyle getFontStyle()
	{
		return fontStyle;
	}

	public void setFontStyle(FontStyle fontStyle)
	{
		this.fontStyle = fontStyle;
	}

	// Property
	public Marker getMarkerEnd()
	{
		return markerEnd;
	}

	public void setMarkerEnd(Marker markerEnd)
	{
		this.markerEnd = markerEnd;
	}

	// Property
	public Integer getTheZ()
	{
		return theZ;
	}

	public void setTheZ(Integer theZ)
	{
		this.theZ = theZ;
	}

	// Property
	public Integer getFontSize()
	{
		return fontSize;
	}

	public void setFontSize(Integer fontSize)
	{
		this.fontSize = fontSize;
	}

	// Property
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property
	public Integer getFill()
	{
		return fill;
	}

	public void setFill(Integer fill)
	{
		this.fill = fill;
	}

	// Property
	public Marker getMarkerStart()
	{
		return markerStart;
	}

	public void setMarkerStart(Marker markerStart)
	{
		this.markerStart = markerStart;
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

	// *** WARNING *** Unhandled or skipped property Line

	// *** WARNING *** Unhandled or skipped property Rectangle

	// *** WARNING *** Unhandled or skipped property Mask

	// *** WARNING *** Unhandled or skipped property Ellipse

	// *** WARNING *** Unhandled or skipped property Point

	// *** WARNING *** Unhandled or skipped property Polyline

	// *** WARNING *** Unhandled or skipped property Path

	// *** WARNING *** Unhandled or skipped property Text

	// Reference AnnotationRef
	public int sizeOfLinkedAnnotationList()
	{
		return annotationList.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationList);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationList.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationList.set(index, o);
	}

	public void linkAnnotation(Annotation o)
	{
		this.annotationList.add(o);
	}

	public void unlinkAnnotation(Annotation o)
	{
		this.annotationList.add(o);
	}

	// *** WARNING *** Unhandled or skipped property Description
}
