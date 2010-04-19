/*
 * ome.xml.r201004.WellSample
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

public class WellSample extends Object
{
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

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference PlateAcquisition_BackReference
	private List<PlateAcquisition> plateAcquisition_BackReferenceList = new ArrayList<PlateAcquisition>();

	// -- Constructors --

	/** Constructs a WellSample. */
	public WellSample()
	{
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

	// Property
	public Image getImage()
	{
		return image;
	}

	public void setImage(Image image)
	{
		this.image = image;
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

	// Back reference PlateAcquisition_BackReference
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

	public PlateAcquisition setLinkedPlateAcquisition(int index, PlateAcquisition plateAcquisition_BackReference)
	{
		return plateAcquisition_BackReferenceList.set(index, plateAcquisition_BackReference);
	}

	public void linkPlateAcquisition(PlateAcquisition plateAcquisition_BackReference)
	{
		this.plateAcquisition_BackReferenceList.add(plateAcquisition_BackReference);
	}

	public void unlinkPlateAcquisition(PlateAcquisition plateAcquisition_BackReference)
	{
		this.plateAcquisition_BackReferenceList.add(plateAcquisition_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for WellSample
		Element WellSample_element = document.createElement("WellSample");
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
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				WellSample_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		if (plateAcquisition_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
		}
		return WellSample_element;
	}
}
