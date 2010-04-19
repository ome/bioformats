/*
 * ome.xml.r201004.Plane
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

public class Plane extends Object
{
	// -- Instance variables --

	// Property
	private Double exposureTime;

	// Property
	private Double positionZ;

	// Property
	private Double positionX;

	// Property
	private Double positionY;

	// Property
	private Double deltaT;

	// Property
	private Integer theC;

	// Property
	private Integer theZ;

	// Property
	private Integer theT;

	// Property
	private String hashSHA1;

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// -- Constructors --

	/** Constructs a Plane. */
	public Plane()
	{
	}

	// -- Plane API methods --

	// Property
	public Double getExposureTime()
	{
		return exposureTime;
	}

	public void setExposureTime(Double exposureTime)
	{
		this.exposureTime = exposureTime;
	}

	// Property
	public Double getPositionZ()
	{
		return positionZ;
	}

	public void setPositionZ(Double positionZ)
	{
		this.positionZ = positionZ;
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
	public Double getDeltaT()
	{
		return deltaT;
	}

	public void setDeltaT(Double deltaT)
	{
		this.deltaT = deltaT;
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
	public Integer getTheZ()
	{
		return theZ;
	}

	public void setTheZ(Integer theZ)
	{
		this.theZ = theZ;
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
	public String getHashSHA1()
	{
		return hashSHA1;
	}

	public void setHashSHA1(String hashSHA1)
	{
		this.hashSHA1 = hashSHA1;
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

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Plane
		Element Plane_element = document.createElement("Plane");
		if (exposureTime != null)
		{
			// Attribute property ExposureTime
			Plane_element.setAttribute("ExposureTime", exposureTime.toString());
		}
		if (positionZ != null)
		{
			// Attribute property PositionZ
			Plane_element.setAttribute("PositionZ", positionZ.toString());
		}
		if (positionX != null)
		{
			// Attribute property PositionX
			Plane_element.setAttribute("PositionX", positionX.toString());
		}
		if (positionY != null)
		{
			// Attribute property PositionY
			Plane_element.setAttribute("PositionY", positionY.toString());
		}
		if (deltaT != null)
		{
			// Attribute property DeltaT
			Plane_element.setAttribute("DeltaT", deltaT.toString());
		}
		if (theC != null)
		{
			// Attribute property TheC
			Plane_element.setAttribute("TheC", theC.toString());
		}
		if (theZ != null)
		{
			// Attribute property TheZ
			Plane_element.setAttribute("TheZ", theZ.toString());
		}
		if (theT != null)
		{
			// Attribute property TheT
			Plane_element.setAttribute("TheT", theT.toString());
		}
		if (hashSHA1 != null)
		{
			// Element property HashSHA1 which is not complex (has no
			// sub-elements)
			Element hashSHA1_element = document.createElement("HashSHA1");
			hashSHA1_element.setTextContent(hashSHA1);
			Plane_element.appendChild(hashSHA1_element);
		}
		if (annotationList != null)
		{
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Plane_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		return Plane_element;
	}
}
