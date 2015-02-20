/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import ome.xml.model.enums.*;
import ome.xml.model.enums.handlers.*;
import ome.xml.model.primitives.*;

public class Plate extends AbstractOMEModelObject
{
	// Base:  -- Name: Plate -- Type: Plate -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SPW/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Plate.class);

	// -- Instance variables --

	// Status property
	private String status;

	// Rows property
	private PositiveInteger rows;

	// ExternalIdentifier property
	private String externalIdentifier;

	// RowNamingConvention property
	private NamingConvention rowNamingConvention;

	// ColumnNamingConvention property
	private NamingConvention columnNamingConvention;

	// FieldIndex property
	private NonNegativeInteger fieldIndex;

	// Columns property
	private PositiveInteger columns;

	// WellOriginY property
	private Length wellOriginY;

	// WellOriginX property
	private Length wellOriginX;

	// ID property
	private String id;

	// Name property
	private String name;

	// Description property
	private String description;

	// Well property (occurs more than once)
	private List<Well> wells = new ArrayList<Well>();

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// PlateAcquisition property (occurs more than once)
	private List<PlateAcquisition> plateAcquisitions = new ArrayList<PlateAcquisition>();

	// Screen_BackReference back reference (occurs more than once)
	private List<Screen> screenLinks = new ReferenceList<Screen>();

	// -- Constructors --

	/** Default constructor. */
	public Plate()
	{
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

	/** Copy constructor. */
	public Plate(Plate orig)
	{
		status = orig.status;
		rows = orig.rows;
		externalIdentifier = orig.externalIdentifier;
		rowNamingConvention = orig.rowNamingConvention;
		columnNamingConvention = orig.columnNamingConvention;
		fieldIndex = orig.fieldIndex;
		columns = orig.columns;
		wellOriginY = orig.wellOriginY;
		wellOriginX = orig.wellOriginX;
		id = orig.id;
		name = orig.name;
		description = orig.description;
		wells = orig.wells;
		annotationLinks = orig.annotationLinks;
		plateAcquisitions = orig.plateAcquisitions;
		screenLinks = orig.screenLinks;
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
		if (element.hasAttribute("Columns"))
		{
			// Attribute property Columns
			setColumns(PositiveInteger.valueOf(
					element.getAttribute("Columns")));
		}
		if (element.hasAttribute("WellOriginY"))
		{
			// Attribute property WellOriginY with unit companion WellOriginYUnit
			String unitSymbol = element.getAttribute("WellOriginYUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getWellOriginYUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("WellOriginY"));
			if (baseValue != null) 
			{
				setWellOriginY(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
		}
		if (element.hasAttribute("WellOriginX"))
		{
			// Attribute property WellOriginX with unit companion WellOriginXUnit
			String unitSymbol = element.getAttribute("WellOriginXUnit");
			if ((unitSymbol == null) || (unitSymbol.isEmpty()))
			{
				// Use default value specified in the xsd model
				unitSymbol = getWellOriginXUnitXsdDefault();
			}
			UnitsLength modelUnit = 
				UnitsLength.fromString(unitSymbol);
			Double baseValue = Double.valueOf(
					element.getAttribute("WellOriginX"));
			if (baseValue != null) 
			{
				setWellOriginX(UnitsLengthEnumHandler.getQuantity(baseValue, modelUnit));
			}
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
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property Status
	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	// Property Rows
	public PositiveInteger getRows()
	{
		return rows;
	}

	public void setRows(PositiveInteger rows)
	{
		this.rows = rows;
	}

	// Property ExternalIdentifier
	public String getExternalIdentifier()
	{
		return externalIdentifier;
	}

	public void setExternalIdentifier(String externalIdentifier)
	{
		this.externalIdentifier = externalIdentifier;
	}

	// Property RowNamingConvention
	public NamingConvention getRowNamingConvention()
	{
		return rowNamingConvention;
	}

	public void setRowNamingConvention(NamingConvention rowNamingConvention)
	{
		this.rowNamingConvention = rowNamingConvention;
	}

	// Property WellOriginXUnit is a unit companion
	public static String getWellOriginXUnitXsdDefault()
	{
		return "reference frame";
	}

	// Property ColumnNamingConvention
	public NamingConvention getColumnNamingConvention()
	{
		return columnNamingConvention;
	}

	public void setColumnNamingConvention(NamingConvention columnNamingConvention)
	{
		this.columnNamingConvention = columnNamingConvention;
	}

	// Property FieldIndex
	public NonNegativeInteger getFieldIndex()
	{
		return fieldIndex;
	}

	public void setFieldIndex(NonNegativeInteger fieldIndex)
	{
		this.fieldIndex = fieldIndex;
	}

	// Property Columns
	public PositiveInteger getColumns()
	{
		return columns;
	}

	public void setColumns(PositiveInteger columns)
	{
		this.columns = columns;
	}

	// Property WellOriginY with unit companion WellOriginYUnit
	public Length getWellOriginY()
	{
		return wellOriginY;
	}

	public void setWellOriginY(Length wellOriginY)
	{
		this.wellOriginY = wellOriginY;
	}

	// Property WellOriginX with unit companion WellOriginXUnit
	public Length getWellOriginX()
	{
		return wellOriginX;
	}

	public void setWellOriginX(Length wellOriginX)
	{
		this.wellOriginX = wellOriginX;
	}

	// Property ID
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property WellOriginYUnit is a unit companion
	public static String getWellOriginYUnitXsdDefault()
	{
		return "reference frame";
	}

	// Property Name
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	// Property Description
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
		return annotationLinks.add(o);
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
		return screenLinks.add(o);
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
		if (columns != null)
		{
			// Attribute property Columns
			Plate_element.setAttribute("Columns", columns.toString());
		}
		if (wellOriginY != null)
		{
			// Attribute property WellOriginY with units companion prop.unitsCompanion.name
			if (wellOriginY.value() != null)
			{
				Plate_element.setAttribute("WellOriginY", wellOriginY.value().toString());
			}
			if (wellOriginY.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(wellOriginY.unit().getSymbol());
					Plate_element.setAttribute("WellOriginYUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Plate:WellOriginYUnit: {}", e.toString());
				}
			}
		}
		if (wellOriginX != null)
		{
			// Attribute property WellOriginX with units companion prop.unitsCompanion.name
			if (wellOriginX.value() != null)
			{
				Plate_element.setAttribute("WellOriginX", wellOriginX.value().toString());
			}
			if (wellOriginX.unit() != null)
			{
				try
				{
					UnitsLength enumUnits = UnitsLength.fromString(wellOriginX.unit().getSymbol());
					Plate_element.setAttribute("WellOriginXUnit", enumUnits.toString());
				} catch (EnumerationException e)
				{
					LOGGER.debug("Unable to create xml for Plate:WellOriginXUnit: {}", e.toString());
				}
			}
		}
		if (id != null)
		{
			// Attribute property ID
			Plate_element.setAttribute("ID", id.toString());
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
