/*
 * ome.xml.r201004.Shape
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

public class Shape extends Object
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

	// Property
	private Line line;

	// Property
	private Rectangle rectangle;

	// Property
	private Mask mask;

	// Property
	private Ellipse ellipse;

	// Property
	private Point point;

	// Property
	private Polyline polyline;

	// Property
	private Path path;

	// Property
	private Text text;

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Property
	private String description;

	// -- Constructors --

	/** Constructs a Shape. */
	public Shape()
	{
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

	// Property
	public Line getLine()
	{
		return line;
	}

	public void setLine(Line line)
	{
		this.line = line;
	}

	// Property
	public Rectangle getRectangle()
	{
		return rectangle;
	}

	public void setRectangle(Rectangle rectangle)
	{
		this.rectangle = rectangle;
	}

	// Property
	public Mask getMask()
	{
		return mask;
	}

	public void setMask(Mask mask)
	{
		this.mask = mask;
	}

	// Property
	public Ellipse getEllipse()
	{
		return ellipse;
	}

	public void setEllipse(Ellipse ellipse)
	{
		this.ellipse = ellipse;
	}

	// Property
	public Point getPoint()
	{
		return point;
	}

	public void setPoint(Point point)
	{
		this.point = point;
	}

	// Property
	public Polyline getPolyline()
	{
		return polyline;
	}

	public void setPolyline(Polyline polyline)
	{
		this.polyline = polyline;
	}

	// Property
	public Path getPath()
	{
		return path;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}

	// Property
	public Text getText()
	{
		return text;
	}

	public void setText(Text text)
	{
		this.text = text;
	}

	// Property which occurs more than once
	public int sizeOfAnnotationList()
	{
		return annotationList.size();
	}

	public List<Annotation> copyAnnotationList()
	{
		return new ArrayList<Annotation>(annotationList);
	}

	public Annotation getAnnotation(int index)
	{
		return annotationList.get(index);
	}

	public Annotation setAnnotation(int index, Annotation annotation)
	{
		return annotationList.set(index, annotation);
	}

	public void addAnnotation(Annotation annotation)
	{
		annotationList.add(annotation);
	}

	public void removeAnnotation(Annotation annotation)
	{
		annotationList.remove(annotation);
	}

	// Property
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Shape
		Element Shape_element = document.createElement("Shape");
		if (strokeDashArray != null)
		{
			// Attribute property StrokeDashArray
			Shape_element.setAttribute("StrokeDashArray", strokeDashArray.toString());
		}
		if (strokeWidth != null)
		{
			// Attribute property StrokeWidth
			Shape_element.setAttribute("StrokeWidth", strokeWidth.toString());
		}
		if (fillRule != null)
		{
			// Attribute property FillRule
			Shape_element.setAttribute("FillRule", fillRule.toString());
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
		if (theT != null)
		{
			// Attribute property TheT
			Shape_element.setAttribute("TheT", theT.toString());
		}
		if (transform != null)
		{
			// Attribute property Transform
			Shape_element.setAttribute("Transform", transform.toString());
		}
		if (label != null)
		{
			// Attribute property Label
			Shape_element.setAttribute("Label", label.toString());
		}
		if (fontFamily != null)
		{
			// Attribute property FontFamily
			Shape_element.setAttribute("FontFamily", fontFamily.toString());
		}
		if (stroke != null)
		{
			// Attribute property Stroke
			Shape_element.setAttribute("Stroke", stroke.toString());
		}
		if (fontStyle != null)
		{
			// Attribute property FontStyle
			Shape_element.setAttribute("FontStyle", fontStyle.toString());
		}
		if (markerEnd != null)
		{
			// Attribute property MarkerEnd
			Shape_element.setAttribute("MarkerEnd", markerEnd.toString());
		}
		if (theZ != null)
		{
			// Attribute property TheZ
			Shape_element.setAttribute("TheZ", theZ.toString());
		}
		if (fontSize != null)
		{
			// Attribute property FontSize
			Shape_element.setAttribute("FontSize", fontSize.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Shape_element.setAttribute("ID", id.toString());
		}
		if (fill != null)
		{
			// Attribute property Fill
			Shape_element.setAttribute("Fill", fill.toString());
		}
		if (markerStart != null)
		{
			// Attribute property MarkerStart
			Shape_element.setAttribute("MarkerStart", markerStart.toString());
		}
		if (name != null)
		{
			// Attribute property Name
			Shape_element.setAttribute("Name", name.toString());
		}
		if (line != null)
		{
			// Element property Line which is complex (has
			// sub-elements)
			Shape_element.appendChild(line.asXMLElement(document));
		}
		if (rectangle != null)
		{
			// Element property Rectangle which is complex (has
			// sub-elements)
			Shape_element.appendChild(rectangle.asXMLElement(document));
		}
		if (mask != null)
		{
			// Element property Mask which is complex (has
			// sub-elements)
			Shape_element.appendChild(mask.asXMLElement(document));
		}
		if (ellipse != null)
		{
			// Element property Ellipse which is complex (has
			// sub-elements)
			Shape_element.appendChild(ellipse.asXMLElement(document));
		}
		if (point != null)
		{
			// Element property Point which is complex (has
			// sub-elements)
			Shape_element.appendChild(point.asXMLElement(document));
		}
		if (polyline != null)
		{
			// Element property Polyline which is complex (has
			// sub-elements)
			Shape_element.appendChild(polyline.asXMLElement(document));
		}
		if (path != null)
		{
			// Element property Path which is complex (has
			// sub-elements)
			Shape_element.appendChild(path.asXMLElement(document));
		}
		if (text != null)
		{
			// Element property Text which is complex (has
			// sub-elements)
			Shape_element.appendChild(text.asXMLElement(document));
		}
		if (annotationList != null)
		{
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Shape_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			Shape_element.appendChild(description_element);
		}
		return Shape_element;
	}

	public static Shape fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"Shape".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Shape got %s",
					tagName));
		}
		Shape instance = new Shape();
		if (element.hasAttribute("StrokeDashArray"))
		{
			// Attribute property StrokeDashArray
			instance.setStrokeDashArray(String.valueOf(
					element.getAttribute("StrokeDashArray")));
		}
		if (element.hasAttribute("StrokeWidth"))
		{
			// Attribute property StrokeWidth
			instance.setStrokeWidth(Double.valueOf(
					element.getAttribute("StrokeWidth")));
		}
		if (element.hasAttribute("FillRule"))
		{
			// Attribute property which is an enumeration FillRule
			instance.setFillRule(FillRule.fromString(
					element.getAttribute("FillRule")));
		}
		if (element.hasAttribute("LineCap"))
		{
			// Attribute property which is an enumeration LineCap
			instance.setLineCap(LineCap.fromString(
					element.getAttribute("LineCap")));
		}
		if (element.hasAttribute("TheC"))
		{
			// Attribute property TheC
			instance.setTheC(Integer.valueOf(
					element.getAttribute("TheC")));
		}
		if (element.hasAttribute("TheT"))
		{
			// Attribute property TheT
			instance.setTheT(Integer.valueOf(
					element.getAttribute("TheT")));
		}
		if (element.hasAttribute("Transform"))
		{
			// Attribute property Transform
			instance.setTransform(String.valueOf(
					element.getAttribute("Transform")));
		}
		if (element.hasAttribute("Label"))
		{
			// Attribute property Label
			instance.setLabel(String.valueOf(
					element.getAttribute("Label")));
		}
		if (element.hasAttribute("FontFamily"))
		{
			// Attribute property which is an enumeration FontFamily
			instance.setFontFamily(FontFamily.fromString(
					element.getAttribute("FontFamily")));
		}
		if (element.hasAttribute("Stroke"))
		{
			// Attribute property Stroke
			instance.setStroke(Integer.valueOf(
					element.getAttribute("Stroke")));
		}
		if (element.hasAttribute("FontStyle"))
		{
			// Attribute property which is an enumeration FontStyle
			instance.setFontStyle(FontStyle.fromString(
					element.getAttribute("FontStyle")));
		}
		if (element.hasAttribute("MarkerEnd"))
		{
			// Attribute property which is an enumeration MarkerEnd
			instance.setMarkerEnd(Marker.fromString(
					element.getAttribute("MarkerEnd")));
		}
		if (element.hasAttribute("TheZ"))
		{
			// Attribute property TheZ
			instance.setTheZ(Integer.valueOf(
					element.getAttribute("TheZ")));
		}
		if (element.hasAttribute("FontSize"))
		{
			// Attribute property FontSize
			instance.setFontSize(Integer.valueOf(
					element.getAttribute("FontSize")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			instance.setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("Fill"))
		{
			// Attribute property Fill
			instance.setFill(Integer.valueOf(
					element.getAttribute("Fill")));
		}
		if (element.hasAttribute("MarkerStart"))
		{
			// Attribute property which is an enumeration MarkerStart
			instance.setMarkerStart(Marker.fromString(
					element.getAttribute("MarkerStart")));
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			instance.setName(String.valueOf(
					element.getAttribute("Name")));
		}
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
			// Element property Line which is complex (has
			// sub-elements)
			instance.setLine(Line.fromXMLElement(
					(Element) Line_nodeList.item(0)));
		}
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
			// Element property Rectangle which is complex (has
			// sub-elements)
			instance.setRectangle(Rectangle.fromXMLElement(
					(Element) Rectangle_nodeList.item(0)));
		}
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
			// Element property Mask which is complex (has
			// sub-elements)
			instance.setMask(Mask.fromXMLElement(
					(Element) Mask_nodeList.item(0)));
		}
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
			// Element property Ellipse which is complex (has
			// sub-elements)
			instance.setEllipse(Ellipse.fromXMLElement(
					(Element) Ellipse_nodeList.item(0)));
		}
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
			// Element property Point which is complex (has
			// sub-elements)
			instance.setPoint(Point.fromXMLElement(
					(Element) Point_nodeList.item(0)));
		}
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
			// Element property Polyline which is complex (has
			// sub-elements)
			instance.setPolyline(Polyline.fromXMLElement(
					(Element) Polyline_nodeList.item(0)));
		}
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
			// Element property Path which is complex (has
			// sub-elements)
			instance.setPath(Path.fromXMLElement(
					(Element) Path_nodeList.item(0)));
		}
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
			// Element property Text which is complex (has
			// sub-elements)
			instance.setText(Text.fromXMLElement(
					(Element) Text_nodeList.item(0)));
		}
		// Element property AnnotationRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList AnnotationRef_nodeList = element.getElementsByTagName("AnnotationRef");
		for (int i = 0; i < AnnotationRef_nodeList.getLength(); i++)
		{
			instance.addAnnotation(Annotation.fromXMLElement(
					(Element) AnnotationRef_nodeList.item(i)));
		}
		NodeList Description_nodeList = element.getElementsByTagName("Description");
		if (Description_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Description node list size %d != 1",
					Description_nodeList.getLength()));
		}
		else if (Description_nodeList.getLength() != 0)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			instance.setDescription(Description_nodeList.item(0).getTextContent());
		}
		return instance;
	}
}
