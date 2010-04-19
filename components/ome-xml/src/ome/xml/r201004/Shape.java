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
}
