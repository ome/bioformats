/*
 * ome.xml.model.Plate
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
 * Created by melissa via xsd-fu on 2012-01-12 20:06:01-0500
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

public class Plate extends AbstractOMEModelObject
{
	// Base:  -- Name: Plate -- Type: Plate -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2011-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Plate.class);

	// -- Instance variables --


	// Property
	private String status;

	// Property
	private PositiveInteger rows;

	// Property
	private String externalIdentifier;

	// Property
	private NamingConvention rowNamingConvention;

	// Property
	private NamingConvention columnNamingConvention;

	// Property
	private Double wellOriginY;

	// Property
	private Double wellOriginX;

	// Property
	private String id;

	// Property
	private PositiveInteger columns;

	// Property
	private String name;

	// Property
	private String description;

	// Reference ScreenRef
	private List<Screen> screenList = new ArrayList<Screen>();

	// Property which occurs more than once
	private List<Well> wellList = new ArrayList<Well>();

	// Reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Property which occurs more than once
	private List<PlateAcquisition> plateAcquisitionList = new ArrayList<PlateAcquisition>();

	// -- Constructors --

	/** Default constructor. */
	public Plate()
	{
		super();
	}

	/** 
	 * Constructs Plate recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Plate(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Plate specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Plate recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Plate".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Plate got {}", tagName);
		}
		if (element.hasAttribute("Status"))
		{
			// Attribute property Status
			setStatus(String.valueOf(
					element.getAttribute("Status")));
		}
		if (element.hasAttribute("Rows"))
		{
			// Attribute property Rows
			setRows(PositiveInteger.valueOf(
					element.getAttribute("Rows")));
		}
		if (element.hasAttribute("ExternalIdentifier"))
		{
			// Attribute property ExternalIdentifier
			setExternalIdentifier(String.valueOf(
					element.getAttribute("ExternalIdentifier")));
		}
		if (element.hasAttribute("RowNamingConvention"))
		{
			// Attribute property which is an enumeration RowNamingConvention
			setRowNamingConvention(NamingConvention.fromString(
					element.getAttribute("RowNamingConvention")));
		}
		if (element.hasAttribute("ColumnNamingConvention"))
		{
			// Attribute property which is an enumeration ColumnNamingConvention
			setColumnNamingConvention(NamingConvention.fromString(
					element.getAttribute("ColumnNamingConvention")));
		}
		if (element.hasAttribute("WellOriginY"))
		{
			// Attribute property WellOriginY
			setWellOriginY(Double.valueOf(
					element.getAttribute("WellOriginY")));
		}
		if (element.hasAttribute("WellOriginX"))
		{
			// Attribute property WellOriginX
			setWellOriginX(Double.valueOf(
					element.getAttribute("WellOriginX")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Plate missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		if (element.hasAttribute("Columns"))
		{
			// Attribute property Columns
			setColumns(PositiveInteger.valueOf(
					element.getAttribute("Columns")));
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		List<Element> Description_nodeList =
				getChildrenByTagName(element, "Description");
		if (Description_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Description node list size %d != 1",
					Description_nodeList.size()));
		}
		else if (Description_nodeList.size() != 0)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			setDescription(
					String.valueOf(Description_nodeList.get(0).getTextContent()));
		}
		// Element reference ScreenRef
		List<Element> ScreenRef_nodeList =
				getChildrenByTagName(element, "ScreenRef");
		for (Element ScreenRef_element : ScreenRef_nodeList)
		{
			ScreenRef screenList_reference = new ScreenRef();
			screenList_reference.setID(ScreenRef_element.getAttribute("ID"));
			model.addReference(this, screenList_reference);
		}
		// Element property Well which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Well_nodeList =
				getChildrenByTagName(element, "Well");
		for (Element Well_element : Well_nodeList)
		{
			addWell(
					new Well(Well_element, model));
		}
		// Element reference AnnotationRef
		List<Element> AnnotationRef_nodeList =
				getChildrenByTagName(element, "AnnotationRef");
		for (Element AnnotationRef_element : AnnotationRef_nodeList)
		{
			AnnotationRef annotationList_reference = new AnnotationRef();
			annotationList_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationList_reference);
		}
		// Element property PlateAcquisition which is complex (has
		// sub-elements) and occurs more than once
		List<Element> PlateAcquisition_nodeList =
				getChildrenByTagName(element, "PlateAcquisition");
		for (Element PlateAcquisition_element : PlateAcquisition_nodeList)
		{
			addPlateAcquisition(
					new PlateAcquisition(PlateAcquisition_element, model));
		}
	}

	// -- Plate API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof ScreenRef)
		{
			Screen o_casted = (Screen) o;
			if (!screenList.contains(o_casted)) {
				screenList.add(o_casted);
			}
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkPlate(this);
			if (!annotationList.contains(o_casted)) {
				annotationList.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	// Property
	public PositiveInteger getRows()
	{
		return rows;
	}

	public void setRows(PositiveInteger rows)
	{
		this.rows = rows;
	}

	// Property
	public String getExternalIdentifier()
	{
		return externalIdentifier;
	}

	public void setExternalIdentifier(String externalIdentifier)
	{
		this.externalIdentifier = externalIdentifier;
	}

	// Property
	public NamingConvention getRowNamingConvention()
	{
		return rowNamingConvention;
	}

	public void setRowNamingConvention(NamingConvention rowNamingConvention)
	{
		this.rowNamingConvention = rowNamingConvention;
	}

	// Property
	public NamingConvention getColumnNamingConvention()
	{
		return columnNamingConvention;
	}

	public void setColumnNamingConvention(NamingConvention columnNamingConvention)
	{
		this.columnNamingConvention = columnNamingConvention;
	}

	// Property
	public Double getWellOriginY()
	{
		return wellOriginY;
	}

	public void setWellOriginY(Double wellOriginY)
	{
		this.wellOriginY = wellOriginY;
	}

	// Property
	public Double getWellOriginX()
	{
		return wellOriginX;
	}

	public void setWellOriginX(Double wellOriginX)
	{
		this.wellOriginX = wellOriginX;
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
	public PositiveInteger getColumns()
	{
		return columns;
	}

	public void setColumns(PositiveInteger columns)
	{
		this.columns = columns;
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
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	// Reference which occurs more than once
	public int sizeOfLinkedScreenList()
	{
		return screenList.size();
	}

	public List<Screen> copyLinkedScreenList()
	{
		return new ArrayList<Screen>(screenList);
	}

	public Screen getLinkedScreen(int index)
	{
		return screenList.get(index);
	}

	public Screen setLinkedScreen(int index, Screen o)
	{
		return screenList.set(index, o);
	}

	public boolean linkScreen(Screen o)
	{
		if (!screenList.contains(o)) {
			return screenList.add(o);
		}
		return false;
	}

	public boolean unlinkScreen(Screen o)
	{
		return screenList.remove(o);
	}

	// Property which occurs more than once
	public int sizeOfWellList()
	{
		return wellList.size();
	}

	public List<Well> copyWellList()
	{
		return new ArrayList<Well>(wellList);
	}

	public Well getWell(int index)
	{
		return wellList.get(index);
	}

	public Well setWell(int index, Well well)
	{
		return wellList.set(index, well);
	}

	public void addWell(Well well)
	{
		wellList.add(well);
	}

	public void removeWell(Well well)
	{
		wellList.remove(well);
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
		o.linkPlate(this);
		if (!annotationList.contains(o)) {
			return annotationList.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{
		o.unlinkPlate(this);
		return annotationList.remove(o);
	}

	// Property which occurs more than once
	public int sizeOfPlateAcquisitionList()
	{
		return plateAcquisitionList.size();
	}

	public List<PlateAcquisition> copyPlateAcquisitionList()
	{
		return new ArrayList<PlateAcquisition>(plateAcquisitionList);
	}

	public PlateAcquisition getPlateAcquisition(int index)
	{
		return plateAcquisitionList.get(index);
	}

	public PlateAcquisition setPlateAcquisition(int index, PlateAcquisition plateAcquisition)
	{
		return plateAcquisitionList.set(index, plateAcquisition);
	}

	public void addPlateAcquisition(PlateAcquisition plateAcquisition)
	{
		plateAcquisitionList.add(plateAcquisition);
	}

	public void removePlateAcquisition(PlateAcquisition plateAcquisition)
	{
		plateAcquisitionList.remove(plateAcquisition);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Plate_element)
	{
		// Creating XML block for Plate

		if (Plate_element == null)
		{
			Plate_element =
					document.createElementNS(NAMESPACE, "Plate");
		}

		if (status != null)
		{
			// Attribute property Status
			Plate_element.setAttribute("Status", status.toString());
		}
		if (rows != null)
		{
			// Attribute property Rows
			Plate_element.setAttribute("Rows", rows.toString());
		}
		if (externalIdentifier != null)
		{
			// Attribute property ExternalIdentifier
			Plate_element.setAttribute("ExternalIdentifier", externalIdentifier.toString());
		}
		if (rowNamingConvention != null)
		{
			// Attribute property RowNamingConvention
			Plate_element.setAttribute("RowNamingConvention", rowNamingConvention.toString());
		}
		if (columnNamingConvention != null)
		{
			// Attribute property ColumnNamingConvention
			Plate_element.setAttribute("ColumnNamingConvention", columnNamingConvention.toString());
		}
		if (wellOriginY != null)
		{
			// Attribute property WellOriginY
			Plate_element.setAttribute("WellOriginY", wellOriginY.toString());
		}
		if (wellOriginX != null)
		{
			// Attribute property WellOriginX
			Plate_element.setAttribute("WellOriginX", wellOriginX.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Plate_element.setAttribute("ID", id.toString());
		}
		if (columns != null)
		{
			// Attribute property Columns
			Plate_element.setAttribute("Columns", columns.toString());
		}
		if (name != null)
		{
			// Attribute property Name
			Plate_element.setAttribute("Name", name.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
			Plate_element.appendChild(description_element);
		}
		if (screenList != null)
		{
			// Reference property ScreenRef which occurs more than once
			for (Screen screenList_value : screenList)
			{
				ScreenRef o = new ScreenRef();
				o.setID(screenList_value.getID());
				Plate_element.appendChild(o.asXMLElement(document));
			}
		}
		if (wellList != null)
		{
			// Element property Well which is complex (has
			// sub-elements) and occurs more than once
			for (Well wellList_value : wellList)
			{
				Plate_element.appendChild(wellList_value.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationList_value.getID());
				Plate_element.appendChild(o.asXMLElement(document));
			}
		}
		if (plateAcquisitionList != null)
		{
			// Element property PlateAcquisition which is complex (has
			// sub-elements) and occurs more than once
			for (PlateAcquisition plateAcquisitionList_value : plateAcquisitionList)
			{
				Plate_element.appendChild(plateAcquisitionList_value.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, Plate_element);
	}
}
