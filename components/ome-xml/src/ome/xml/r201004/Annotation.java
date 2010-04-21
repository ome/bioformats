/*
 * ome.xml.r201004.Annotation
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
 * Created by callan via xsd-fu on 2010-04-21 11:45:19+0100
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

public abstract class Annotation extends AbstractOMEModelObject
{
	// -- Instance variables --

	// Property
	private String namespace;

	// Property
	private String id;

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// Back reference Pixels_BackReference
	private List<Pixels> pixels_BackReferenceList = new ArrayList<Pixels>();

	// Back reference Plane_BackReference
	private List<Plane> plane_BackReferenceList = new ArrayList<Plane>();

	// Back reference Channel_BackReference
	private List<Channel> channel_BackReferenceList = new ArrayList<Channel>();

	// Back reference Project_BackReference
	private List<Project> project_BackReferenceList = new ArrayList<Project>();

	// Back reference Dataset_BackReference
	private List<Dataset> dataset_BackReferenceList = new ArrayList<Dataset>();

	// Back reference Experimenter_BackReference
	private List<Experimenter> experimenter_BackReferenceList = new ArrayList<Experimenter>();

	// Back reference Plate_BackReference
	private List<Plate> plate_BackReferenceList = new ArrayList<Plate>();

	// Back reference Reagent_BackReference
	private List<Reagent> reagent_BackReferenceList = new ArrayList<Reagent>();

	// Back reference Screen_BackReference
	private List<Screen> screen_BackReferenceList = new ArrayList<Screen>();

	// Back reference PlateAcquisition_BackReference
	private List<PlateAcquisition> plateAcquisition_BackReferenceList = new ArrayList<PlateAcquisition>();

	// Back reference Well_BackReference
	private List<Well> well_BackReferenceList = new ArrayList<Well>();

	// Back reference WellSample_BackReference
	private List<WellSample> wellSample_BackReferenceList = new ArrayList<WellSample>();

	// Back reference ROI_BackReference
	private List<ROI> roi_backReferenceList = new ArrayList<ROI>();

	// Back reference Shape_BackReference
	private List<Shape> shape_BackReferenceList = new ArrayList<Shape>();

	// Back reference ListAnnotation_BackReference
	private List<ListAnnotation> listAnnotation_BackReferenceList = new ArrayList<ListAnnotation>();

	// -- Constructors --

	/** Default constructor. */
	public Annotation()
	{
		super();
	}

	/** 
	 * Constructs Annotation recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Annotation(Element element) throws EnumerationException
	{
		super(element);
		if (element.hasAttribute("Namespace"))
		{
			// Attribute property Namespace
			setNamespace(String.valueOf(
					element.getAttribute("Namespace")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		// *** IGNORING *** Skipped back reference Image_BackReference
		// *** IGNORING *** Skipped back reference Pixels_BackReference
		// *** IGNORING *** Skipped back reference Plane_BackReference
		// *** IGNORING *** Skipped back reference Channel_BackReference
		// *** IGNORING *** Skipped back reference Project_BackReference
		// *** IGNORING *** Skipped back reference Dataset_BackReference
		// *** IGNORING *** Skipped back reference Experimenter_BackReference
		// *** IGNORING *** Skipped back reference Plate_BackReference
		// *** IGNORING *** Skipped back reference Reagent_BackReference
		// *** IGNORING *** Skipped back reference Screen_BackReference
		// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
		// *** IGNORING *** Skipped back reference Well_BackReference
		// *** IGNORING *** Skipped back reference WellSample_BackReference
		// *** IGNORING *** Skipped back reference ROI_BackReference
		// *** IGNORING *** Skipped back reference Shape_BackReference
		// *** IGNORING *** Skipped back reference ListAnnotation_BackReference
	}

	// -- Annotation API methods --

	// Property
	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
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

	// *** WARNING *** Unhandled or skipped property Image_BackReference

	// *** WARNING *** Unhandled or skipped property Pixels_BackReference

	// *** WARNING *** Unhandled or skipped property Plane_BackReference

	// *** WARNING *** Unhandled or skipped property Channel_BackReference

	// *** WARNING *** Unhandled or skipped property Project_BackReference

	// *** WARNING *** Unhandled or skipped property Dataset_BackReference

	// *** WARNING *** Unhandled or skipped property Experimenter_BackReference

	// *** WARNING *** Unhandled or skipped property Plate_BackReference

	// *** WARNING *** Unhandled or skipped property Reagent_BackReference

	// *** WARNING *** Unhandled or skipped property Screen_BackReference

	// *** WARNING *** Unhandled or skipped property PlateAcquisition_BackReference

	// *** WARNING *** Unhandled or skipped property Well_BackReference

	// *** WARNING *** Unhandled or skipped property WellSample_BackReference

	// *** WARNING *** Unhandled or skipped property ROI_BackReference

	// *** WARNING *** Unhandled or skipped property Shape_BackReference

	// *** WARNING *** Unhandled or skipped property ListAnnotation_BackReference

	protected Element asXMLElement(Document document, Element Annotation_element)
	{
		// Creating XML block for Annotation
		if (Annotation_element == null)
		{
			Annotation_element = document.createElement("Annotation");
		}
		Annotation_element = super.asXMLElement(document, Annotation_element);

		if (namespace != null)
		{
			// Attribute property Namespace
			Annotation_element.setAttribute("Namespace", namespace.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Annotation_element.setAttribute("ID", id.toString());
		}
		return Annotation_element;
	}
}
