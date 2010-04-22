/*
 * ome.xml.r201004.WellSample
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
 * Created by callan via xsd-fu on 2010-04-22 16:50:50+0100
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

public class WellSample extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2010-04";

	// -- Instance variables --

	// Property
	private Integer index;

	// Property
	private Double positionX;

	// Property
	private Double positionY;

	// Property
	private String id;

	// Property
	private Integer timepoint;

	// Property
	private Image image;

	// Reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference PlateAcquisition_BackReference
	private List<PlateAcquisition> plateAcquisition_BackReferenceList = new ArrayList<PlateAcquisition>();

	// -- Constructors --

	/** Default constructor. */
	public WellSample()
	{
		super();
	}

	/** 
	 * Constructs WellSample recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public WellSample(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates WellSample recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"WellSample".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of WellSample got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of WellSample got %s",
			//		tagName));
		}
		if (element.hasAttribute("Index"))
		{
			// Attribute property Index
			setIndex(Integer.valueOf(
					element.getAttribute("Index")));
		}
		if (element.hasAttribute("PositionX"))
		{
			// Attribute property PositionX
			setPositionX(Double.valueOf(
					element.getAttribute("PositionX")));
		}
		if (element.hasAttribute("PositionY"))
		{
			// Attribute property PositionY
			setPositionY(Double.valueOf(
					element.getAttribute("PositionY")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("Timepoint"))
		{
			// Attribute property Timepoint
			setTimepoint(Integer.valueOf(
					element.getAttribute("Timepoint")));
		}
		// *** IGNORING *** Skipped back reference ImageRef
		// *** IGNORING *** Skipped back reference AnnotationRef
		// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
	}

	// -- WellSample API methods --


	// Property
	public Integer getIndex()
	{
		return index;
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}

	// Property
	public Double getPositionX()
	{
		return positionX;
	}

	public void setPositionX(Double positionX)
	{
		this.positionX = positionX;
	}

	// Property
	public Double getPositionY()
	{
		return positionY;
	}

	public void setPositionY(Double positionY)
	{
		this.positionY = positionY;
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
	public Integer getTimepoint()
	{
		return timepoint;
	}

	public void setTimepoint(Integer timepoint)
	{
		this.timepoint = timepoint;
	}

	// Reference
	public Image getLinkedImage()
	{
		return image;
	}

	public void linkImage(Image o)
	{
		image = o;
	}

	public void unlinkImage(Image o)
	{
		if (image == o)
		{
			image = null;
		}
	}

	// Reference which occurs more than once
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

	public boolean linkAnnotation(Annotation o)
	{
		o.linkWellSample(this);
		return annotationList.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{
		o.unlinkWellSample(this);
		return annotationList.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedPlateAcquisitionList()
	{
		return plateAcquisition_BackReferenceList.size();
	}

	public List<PlateAcquisition> copyLinkedPlateAcquisitionList()
	{
		return new ArrayList<PlateAcquisition>(plateAcquisition_BackReferenceList);
	}

	public PlateAcquisition getLinkedPlateAcquisition(int index)
	{
		return plateAcquisition_BackReferenceList.get(index);
	}

	public PlateAcquisition setLinkedPlateAcquisition(int index, PlateAcquisition o)
	{
		return plateAcquisition_BackReferenceList.set(index, o);
	}

	public boolean linkPlateAcquisition(PlateAcquisition o)
	{
		return plateAcquisition_BackReferenceList.add(o);
	}

	public boolean unlinkPlateAcquisition(PlateAcquisition o)
	{
		return plateAcquisition_BackReferenceList.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element WellSample_element)
	{
		// Creating XML block for WellSample
		if (WellSample_element == null)
		{
			WellSample_element =
					document.createElementNS(NAMESPACE, "WellSample");
		}

		if (index != null)
		{
			// Attribute property Index
			WellSample_element.setAttribute("Index", index.toString());
		}
		if (positionX != null)
		{
			// Attribute property PositionX
			WellSample_element.setAttribute("PositionX", positionX.toString());
		}
		if (positionY != null)
		{
			// Attribute property PositionY
			WellSample_element.setAttribute("PositionY", positionY.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			WellSample_element.setAttribute("ID", id.toString());
		}
		if (timepoint != null)
		{
			// Attribute property Timepoint
			WellSample_element.setAttribute("Timepoint", timepoint.toString());
		}
		if (image != null)
		{
			// Element property ImageRef which is complex (has
			// sub-elements)
			WellSample_element.appendChild(image.asXMLElement(document));
		}
		if (annotationList != null)
		{
			// Reference property AnnotationRef
			for (Annotation o : annotationList)
			{
				Element annotationList_element = 
						document.createElementNS(NAMESPACE, "AnnotationRefRef");
				annotationList_element.setAttribute("ID", o.getID());
				WellSample_element.appendChild(annotationList_element);
			}
		}
		if (plateAcquisition_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
		}
		return super.asXMLElement(document, WellSample_element);
	}
}
