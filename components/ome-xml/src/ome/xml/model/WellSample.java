/*
 * ome.xml.model.WellSample
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
 * Created by melissa via xsd-fu on 2012-09-10 13:40:21-0400
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

public class WellSample extends AbstractOMEModelObject
{
	// Base:  -- Name: WellSample -- Type: WellSample -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(WellSample.class);

	// -- Instance variables --


	// Property
	private NonNegativeInteger index;

	// Property
	private Double positionX;

	// Property
	private Double positionY;

	// Property
	private String id;

	// Property
	private Timestamp timepoint;

	// Property
	private Image image;

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

	// Back reference PlateAcquisition_BackReference
	private List<PlateAcquisition> plateAcquisitions = new ArrayList<PlateAcquisition>();

	// Back reference Well_BackReference
	private Well well;

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
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public WellSample(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from WellSample specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates WellSample recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"WellSample".equals(tagName))
		{
			LOGGER.debug("Expecting node name of WellSample got {}", tagName);
		}
		if (element.hasAttribute("Index"))
		{
			// Attribute property Index
			setIndex(NonNegativeInteger.valueOf(
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
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"WellSample missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("Timepoint"))
		{
			// Attribute property Timepoint
			setTimepoint(Timestamp.valueOf(
					element.getAttribute("Timepoint")));
		}
		// Element reference ImageRef
		List<Element> ImageRef_nodeList =
				getChildrenByTagName(element, "ImageRef");
		for (Element ImageRef_element : ImageRef_nodeList)
		{
			ImageRef image_reference = new ImageRef();
			image_reference.setID(ImageRef_element.getAttribute("ID"));
			model.addReference(this, image_reference);
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
		// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
		// *** IGNORING *** Skipped back reference Well_BackReference
	}

	// -- WellSample API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof ImageRef)
		{
			Image o_casted = (Image) o;
			o_casted.linkWellSample(this);
			image = o_casted;
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkWellSample(this);
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public NonNegativeInteger getIndex()
	{
		return index;
	}

	public void setIndex(NonNegativeInteger index)
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
	public Timestamp getTimepoint()
	{
		return timepoint;
	}

	public void setTimepoint(Timestamp timepoint)
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

			o.linkWellSample(this);
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkWellSample(this);
		return annotationLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedPlateAcquisitionList()
	{
		return plateAcquisitions.size();
	}

	public List<PlateAcquisition> copyLinkedPlateAcquisitionList()
	{
		return new ArrayList<PlateAcquisition>(plateAcquisitions);
	}

	public PlateAcquisition getLinkedPlateAcquisition(int index)
	{
		return plateAcquisitions.get(index);
	}

	public PlateAcquisition setLinkedPlateAcquisition(int index, PlateAcquisition o)
	{
		return plateAcquisitions.set(index, o);
	}

	public boolean linkPlateAcquisition(PlateAcquisition o)
	{
		if (!plateAcquisitions.contains(o)) {
			return plateAcquisitions.add(o);
		}
		return false;
	}

	public boolean unlinkPlateAcquisition(PlateAcquisition o)
	{
		return plateAcquisitions.remove(o);
	}

	// Property
	public Well getWell()
	{
		return well;
	}

	public void setWell(Well well_BackReference)
	{
		this.well = well_BackReference;
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
			// Reference property ImageRef
			ImageRef o = new ImageRef();
			o.setID(image.getID());
			WellSample_element.appendChild(o.asXMLElement(document));
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				WellSample_element.appendChild(o.asXMLElement(document));
			}
		}
		if (plateAcquisitions != null)
		{
			// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
		}
		if (well != null)
		{
			// *** IGNORING *** Skipped back reference Well_BackReference
		}
		return super.asXMLElement(document, WellSample_element);
	}
}
