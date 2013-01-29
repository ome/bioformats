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

public class Plate extends AbstractOMEModelObject
{
	// Base:  -- Name: Plate -- Type: Plate -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2012-06";

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
	private NonNegativeInteger fieldIndex;

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

	// Property which occurs more than once
	private List<Well> wells = new ArrayList<Well>();

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

	// Property which occurs more than once
	private List<PlateAcquisition> plateAcquisitions = new ArrayList<PlateAcquisition>();

	// Back reference Screen_BackReference
	private List<Screen> screenLinks = new ArrayList<Screen>();

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
		if (element.hasAttribute("FieldIndex"))
		{
			// Attribute property FieldIndex
			setFieldIndex(NonNegativeInteger.valueOf(
					element.getAttribute("FieldIndex")));
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
			AnnotationRef annotationLinks_reference = new AnnotationRef();
			annotationLinks_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationLinks_reference);
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
		// *** IGNORING *** Skipped back reference Screen_BackReference
	}

	// -- Plate API methods --

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
			o_casted.linkPlate(this);
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
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
	public NonNegativeInteger getFieldIndex()
	{
		return fieldIndex;
	}

	public void setFieldIndex(NonNegativeInteger fieldIndex)
	{
		this.fieldIndex = fieldIndex;
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

	// Property which occurs more than once
	public int sizeOfWellList()
	{
		return wells.size();
	}

	public List<Well> copyWellList()
	{
		return new ArrayList<Well>(wells);
	}

	public Well getWell(int index)
	{
		return wells.get(index);
	}

	public Well setWell(int index, Well well)
	{
        well.setPlate(this);
		return wells.set(index, well);
	}

	public void addWell(Well well)
	{
        well.setPlate(this);
		wells.add(well);
	}

	public void removeWell(Well well)
	{
		wells.remove(well);
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

			o.linkPlate(this);
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkPlate(this);
		return annotationLinks.remove(o);
	}

	// Property which occurs more than once
	public int sizeOfPlateAcquisitionList()
	{
		return plateAcquisitions.size();
	}

	public List<PlateAcquisition> copyPlateAcquisitionList()
	{
		return new ArrayList<PlateAcquisition>(plateAcquisitions);
	}

	public PlateAcquisition getPlateAcquisition(int index)
	{
		return plateAcquisitions.get(index);
	}

	public PlateAcquisition setPlateAcquisition(int index, PlateAcquisition plateAcquisition)
	{
        plateAcquisition.setPlate(this);
		return plateAcquisitions.set(index, plateAcquisition);
	}

	public void addPlateAcquisition(PlateAcquisition plateAcquisition)
	{
        plateAcquisition.setPlate(this);
		plateAcquisitions.add(plateAcquisition);
	}

	public void removePlateAcquisition(PlateAcquisition plateAcquisition)
	{
		plateAcquisitions.remove(plateAcquisition);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedScreenList()
	{
		return screenLinks.size();
	}

	public List<Screen> copyLinkedScreenList()
	{
		return new ArrayList<Screen>(screenLinks);
	}

	public Screen getLinkedScreen(int index)
	{
		return screenLinks.get(index);
	}

	public Screen setLinkedScreen(int index, Screen o)
	{
		return screenLinks.set(index, o);
	}

	public boolean linkScreen(Screen o)
	{
		if (!screenLinks.contains(o)) {
			return screenLinks.add(o);
		}
		return false;
	}

	public boolean unlinkScreen(Screen o)
	{
		return screenLinks.remove(o);
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
		if (fieldIndex != null)
		{
			// Attribute property FieldIndex
			Plate_element.setAttribute("FieldIndex", fieldIndex.toString());
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
		if (wells != null)
		{
			// Element property Well which is complex (has
			// sub-elements) and occurs more than once
			for (Well wells_value : wells)
			{
				Plate_element.appendChild(wells_value.asXMLElement(document));
			}
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Plate_element.appendChild(o.asXMLElement(document));
			}
		}
		if (plateAcquisitions != null)
		{
			// Element property PlateAcquisition which is complex (has
			// sub-elements) and occurs more than once
			for (PlateAcquisition plateAcquisitions_value : plateAcquisitions)
			{
				Plate_element.appendChild(plateAcquisitions_value.asXMLElement(document));
			}
		}
		if (screenLinks != null)
		{
			// *** IGNORING *** Skipped back reference Screen_BackReference
		}
		return super.asXMLElement(document, Plate_element);
	}
}
