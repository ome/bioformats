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

public class Plane extends AbstractOMEModelObject
{
	// Base:  -- Name: Plane -- Type: Plane -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Plane.class);

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
	private NonNegativeInteger theC;

	// Property
	private NonNegativeInteger theZ;

	// Property
	private NonNegativeInteger theT;

	// Property
	private String hashSHA1;

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

	// Back reference Pixels_BackReference
	private Pixels pixels;

	// -- Constructors --

	/** Default constructor. */
	public Plane()
	{
		super();
	}

	/** 
	 * Constructs Plane recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Plane(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Plane specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Plane recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Plane".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Plane got {}", tagName);
		}
		if (element.hasAttribute("ExposureTime"))
		{
			// Attribute property ExposureTime
			setExposureTime(Double.valueOf(
					element.getAttribute("ExposureTime")));
		}
		if (element.hasAttribute("PositionZ"))
		{
			// Attribute property PositionZ
			setPositionZ(Double.valueOf(
					element.getAttribute("PositionZ")));
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
		if (element.hasAttribute("DeltaT"))
		{
			// Attribute property DeltaT
			setDeltaT(Double.valueOf(
					element.getAttribute("DeltaT")));
		}
		if (element.hasAttribute("TheC"))
		{
			// Attribute property TheC
			setTheC(NonNegativeInteger.valueOf(
					element.getAttribute("TheC")));
		}
		if (element.hasAttribute("TheZ"))
		{
			// Attribute property TheZ
			setTheZ(NonNegativeInteger.valueOf(
					element.getAttribute("TheZ")));
		}
		if (element.hasAttribute("TheT"))
		{
			// Attribute property TheT
			setTheT(NonNegativeInteger.valueOf(
					element.getAttribute("TheT")));
		}
		List<Element> HashSHA1_nodeList =
				getChildrenByTagName(element, "HashSHA1");
		if (HashSHA1_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"HashSHA1 node list size %d != 1",
					HashSHA1_nodeList.size()));
		}
		else if (HashSHA1_nodeList.size() != 0)
		{
			// Element property HashSHA1 which is not complex (has no
			// sub-elements)
			setHashSHA1(
					String.valueOf(HashSHA1_nodeList.get(0).getTextContent()));
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
		// *** IGNORING *** Skipped back reference Pixels_BackReference
	}

	// -- Plane API methods --

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
			o_casted.linkPlane(this);
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


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
	public NonNegativeInteger getTheC()
	{
		return theC;
	}

	public void setTheC(NonNegativeInteger theC)
	{
		this.theC = theC;
	}

	// Property
	public NonNegativeInteger getTheZ()
	{
		return theZ;
	}

	public void setTheZ(NonNegativeInteger theZ)
	{
		this.theZ = theZ;
	}

	// Property
	public NonNegativeInteger getTheT()
	{
		return theT;
	}

	public void setTheT(NonNegativeInteger theT)
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

			o.linkPlane(this);
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkPlane(this);
		return annotationLinks.remove(o);
	}

	// Property
	public Pixels getPixels()
	{
		return pixels;
	}

	public void setPixels(Pixels pixels_BackReference)
	{
		this.pixels = pixels_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Plane_element)
	{
		// Creating XML block for Plane

		if (Plane_element == null)
		{
			Plane_element =
					document.createElementNS(NAMESPACE, "Plane");
		}

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
			Element hashSHA1_element = 
					document.createElementNS(NAMESPACE, "HashSHA1");
			hashSHA1_element.setTextContent(hashSHA1.toString());
			Plane_element.appendChild(hashSHA1_element);
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Plane_element.appendChild(o.asXMLElement(document));
			}
		}
		if (pixels != null)
		{
			// *** IGNORING *** Skipped back reference Pixels_BackReference
		}
		return super.asXMLElement(document, Plane_element);
	}
}
