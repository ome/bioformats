/*
 * ome.xml.model.OME
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

public class OME extends AbstractOMEModelObject
{
	// Base:  -- Name: OME -- Type: OME -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2011-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(OME.class);

	// -- Instance variables --


	// Property
	private String uuid;

	// Property
	private String creator;

	// Property which occurs more than once
	private List<Project> projectList = new ArrayList<Project>();

	// Property which occurs more than once
	private List<Dataset> datasetList = new ArrayList<Dataset>();

	// Property which occurs more than once
	private List<Experiment> experimentList = new ArrayList<Experiment>();

	// Property which occurs more than once
	private List<Plate> plateList = new ArrayList<Plate>();

	// Property which occurs more than once
	private List<Screen> screenList = new ArrayList<Screen>();

	// Property which occurs more than once
	private List<Experimenter> experimenterList = new ArrayList<Experimenter>();

	// Property which occurs more than once
	private List<Group> groupList = new ArrayList<Group>();

	// Property which occurs more than once
	private List<Instrument> instrumentList = new ArrayList<Instrument>();

	// Property which occurs more than once
	private List<Image> imageList = new ArrayList<Image>();

	// Property
	private StructuredAnnotations structuredAnnotations;

	// Property which occurs more than once
	private List<ROI> roiList = new ArrayList<ROI>();

	// Property
	private BinaryOnly binaryOnly;

	// -- Constructors --

	/** Default constructor. */
	public OME()
	{
		super();
	}

	/** 
	 * Constructs OME recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public OME(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from OME specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates OME recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"OME".equals(tagName))
		{
			LOGGER.debug("Expecting node name of OME got {}", tagName);
		}
		if (element.hasAttribute("UUID"))
		{
			// Attribute property UUID
			setUUID(String.valueOf(
					element.getAttribute("UUID")));
		}
		if (element.hasAttribute("Creator"))
		{
			// Attribute property Creator
			setCreator(String.valueOf(
					element.getAttribute("Creator")));
		}
		// Element property Project which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Project_nodeList =
				getChildrenByTagName(element, "Project");
		for (Element Project_element : Project_nodeList)
		{
			addProject(
					new Project(Project_element, model));
		}
		// Element property Dataset which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Dataset_nodeList =
				getChildrenByTagName(element, "Dataset");
		for (Element Dataset_element : Dataset_nodeList)
		{
			addDataset(
					new Dataset(Dataset_element, model));
		}
		// Element property Experiment which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Experiment_nodeList =
				getChildrenByTagName(element, "Experiment");
		for (Element Experiment_element : Experiment_nodeList)
		{
			addExperiment(
					new Experiment(Experiment_element, model));
		}
		// Element property Plate which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Plate_nodeList =
				getChildrenByTagName(element, "Plate");
		for (Element Plate_element : Plate_nodeList)
		{
			addPlate(
					new Plate(Plate_element, model));
		}
		// Element property Screen which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Screen_nodeList =
				getChildrenByTagName(element, "Screen");
		for (Element Screen_element : Screen_nodeList)
		{
			addScreen(
					new Screen(Screen_element, model));
		}
		// Element property Experimenter which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Experimenter_nodeList =
				getChildrenByTagName(element, "Experimenter");
		for (Element Experimenter_element : Experimenter_nodeList)
		{
			addExperimenter(
					new Experimenter(Experimenter_element, model));
		}
		// Element property Group which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Group_nodeList =
				getChildrenByTagName(element, "Group");
		for (Element Group_element : Group_nodeList)
		{
			addGroup(
					new Group(Group_element, model));
		}
		// Element property Instrument which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Instrument_nodeList =
				getChildrenByTagName(element, "Instrument");
		for (Element Instrument_element : Instrument_nodeList)
		{
			addInstrument(
					new Instrument(Instrument_element, model));
		}
		// Element property Image which is complex (has
		// sub-elements) and occurs more than once
		List<Element> Image_nodeList =
				getChildrenByTagName(element, "Image");
		for (Element Image_element : Image_nodeList)
		{
			addImage(
					new Image(Image_element, model));
		}
		List<Element> StructuredAnnotations_nodeList =
				getChildrenByTagName(element, "StructuredAnnotations");
		if (StructuredAnnotations_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"StructuredAnnotations node list size %d != 1",
					StructuredAnnotations_nodeList.size()));
		}
		else if (StructuredAnnotations_nodeList.size() != 0)
		{
			// Element property StructuredAnnotations which is complex (has
			// sub-elements)
			setStructuredAnnotations(new StructuredAnnotations(
					(Element) StructuredAnnotations_nodeList.get(0), model));
		}
		// Element property ROI which is complex (has
		// sub-elements) and occurs more than once
		List<Element> ROI_nodeList =
				getChildrenByTagName(element, "ROI");
		for (Element ROI_element : ROI_nodeList)
		{
			addROI(
					new ROI(ROI_element, model));
		}
		List<Element> BinaryOnly_nodeList =
				getChildrenByTagName(element, "BinaryOnly");
		if (BinaryOnly_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"BinaryOnly node list size %d != 1",
					BinaryOnly_nodeList.size()));
		}
		else if (BinaryOnly_nodeList.size() != 0)
		{
			// Element property BinaryOnly which is complex (has
			// sub-elements)
			setBinaryOnly(new BinaryOnly(
					(Element) BinaryOnly_nodeList.get(0), model));
		}
	}

	// -- OME API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public String getUUID()
	{
		return uuid;
	}

	public void setUUID(String uuid)
	{
		this.uuid = uuid;
	}

	// Property
	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	// Property which occurs more than once
	public int sizeOfProjectList()
	{
		return projectList.size();
	}

	public List<Project> copyProjectList()
	{
		return new ArrayList<Project>(projectList);
	}

	public Project getProject(int index)
	{
		return projectList.get(index);
	}

	public Project setProject(int index, Project project)
	{
		return projectList.set(index, project);
	}

	public void addProject(Project project)
	{
		projectList.add(project);
	}

	public void removeProject(Project project)
	{
		projectList.remove(project);
	}

	// Property which occurs more than once
	public int sizeOfDatasetList()
	{
		return datasetList.size();
	}

	public List<Dataset> copyDatasetList()
	{
		return new ArrayList<Dataset>(datasetList);
	}

	public Dataset getDataset(int index)
	{
		return datasetList.get(index);
	}

	public Dataset setDataset(int index, Dataset dataset)
	{
		return datasetList.set(index, dataset);
	}

	public void addDataset(Dataset dataset)
	{
		datasetList.add(dataset);
	}

	public void removeDataset(Dataset dataset)
	{
		datasetList.remove(dataset);
	}

	// Property which occurs more than once
	public int sizeOfExperimentList()
	{
		return experimentList.size();
	}

	public List<Experiment> copyExperimentList()
	{
		return new ArrayList<Experiment>(experimentList);
	}

	public Experiment getExperiment(int index)
	{
		return experimentList.get(index);
	}

	public Experiment setExperiment(int index, Experiment experiment)
	{
		return experimentList.set(index, experiment);
	}

	public void addExperiment(Experiment experiment)
	{
		experimentList.add(experiment);
	}

	public void removeExperiment(Experiment experiment)
	{
		experimentList.remove(experiment);
	}

	// Property which occurs more than once
	public int sizeOfPlateList()
	{
		return plateList.size();
	}

	public List<Plate> copyPlateList()
	{
		return new ArrayList<Plate>(plateList);
	}

	public Plate getPlate(int index)
	{
		return plateList.get(index);
	}

	public Plate setPlate(int index, Plate plate)
	{
		return plateList.set(index, plate);
	}

	public void addPlate(Plate plate)
	{
		plateList.add(plate);
	}

	public void removePlate(Plate plate)
	{
		plateList.remove(plate);
	}

	// Property which occurs more than once
	public int sizeOfScreenList()
	{
		return screenList.size();
	}

	public List<Screen> copyScreenList()
	{
		return new ArrayList<Screen>(screenList);
	}

	public Screen getScreen(int index)
	{
		return screenList.get(index);
	}

	public Screen setScreen(int index, Screen screen)
	{
		return screenList.set(index, screen);
	}

	public void addScreen(Screen screen)
	{
		screenList.add(screen);
	}

	public void removeScreen(Screen screen)
	{
		screenList.remove(screen);
	}

	// Property which occurs more than once
	public int sizeOfExperimenterList()
	{
		return experimenterList.size();
	}

	public List<Experimenter> copyExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenterList);
	}

	public Experimenter getExperimenter(int index)
	{
		return experimenterList.get(index);
	}

	public Experimenter setExperimenter(int index, Experimenter experimenter)
	{
		return experimenterList.set(index, experimenter);
	}

	public void addExperimenter(Experimenter experimenter)
	{
		experimenterList.add(experimenter);
	}

	public void removeExperimenter(Experimenter experimenter)
	{
		experimenterList.remove(experimenter);
	}

	// Property which occurs more than once
	public int sizeOfGroupList()
	{
		return groupList.size();
	}

	public List<Group> copyGroupList()
	{
		return new ArrayList<Group>(groupList);
	}

	public Group getGroup(int index)
	{
		return groupList.get(index);
	}

	public Group setGroup(int index, Group group)
	{
		return groupList.set(index, group);
	}

	public void addGroup(Group group)
	{
		groupList.add(group);
	}

	public void removeGroup(Group group)
	{
		groupList.remove(group);
	}

	// Property which occurs more than once
	public int sizeOfInstrumentList()
	{
		return instrumentList.size();
	}

	public List<Instrument> copyInstrumentList()
	{
		return new ArrayList<Instrument>(instrumentList);
	}

	public Instrument getInstrument(int index)
	{
		return instrumentList.get(index);
	}

	public Instrument setInstrument(int index, Instrument instrument)
	{
		return instrumentList.set(index, instrument);
	}

	public void addInstrument(Instrument instrument)
	{
		instrumentList.add(instrument);
	}

	public void removeInstrument(Instrument instrument)
	{
		instrumentList.remove(instrument);
	}

	// Property which occurs more than once
	public int sizeOfImageList()
	{
		return imageList.size();
	}

	public List<Image> copyImageList()
	{
		return new ArrayList<Image>(imageList);
	}

	public Image getImage(int index)
	{
		return imageList.get(index);
	}

	public Image setImage(int index, Image image)
	{
		return imageList.set(index, image);
	}

	public void addImage(Image image)
	{
		imageList.add(image);
	}

	public void removeImage(Image image)
	{
		imageList.remove(image);
	}

	// Property
	public StructuredAnnotations getStructuredAnnotations()
	{
		return structuredAnnotations;
	}

	public void setStructuredAnnotations(StructuredAnnotations structuredAnnotations)
	{
		this.structuredAnnotations = structuredAnnotations;
	}

	// Property which occurs more than once
	public int sizeOfROIList()
	{
		return roiList.size();
	}

	public List<ROI> copyROIList()
	{
		return new ArrayList<ROI>(roiList);
	}

	public ROI getROI(int index)
	{
		return roiList.get(index);
	}

	public ROI setROI(int index, ROI roi)
	{
		return roiList.set(index, roi);
	}

	public void addROI(ROI roi)
	{
		roiList.add(roi);
	}

	public void removeROI(ROI roi)
	{
		roiList.remove(roi);
	}

	// Property
	public BinaryOnly getBinaryOnly()
	{
		return binaryOnly;
	}

	public void setBinaryOnly(BinaryOnly binaryOnly)
	{
		this.binaryOnly = binaryOnly;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element OME_element)
	{
		// Creating XML block for OME

		if (OME_element == null)
		{
			OME_element =
					document.createElementNS(NAMESPACE, "OME");
		}

		if (uuid != null)
		{
			// Attribute property UUID
			OME_element.setAttribute("UUID", uuid.toString());
		}
		if (creator != null)
		{
			// Attribute property Creator
			OME_element.setAttribute("Creator", creator.toString());
		}
		if (projectList != null)
		{
			// Element property Project which is complex (has
			// sub-elements) and occurs more than once
			for (Project projectList_value : projectList)
			{
				OME_element.appendChild(projectList_value.asXMLElement(document));
			}
		}
		if (datasetList != null)
		{
			// Element property Dataset which is complex (has
			// sub-elements) and occurs more than once
			for (Dataset datasetList_value : datasetList)
			{
				OME_element.appendChild(datasetList_value.asXMLElement(document));
			}
		}
		if (experimentList != null)
		{
			// Element property Experiment which is complex (has
			// sub-elements) and occurs more than once
			for (Experiment experimentList_value : experimentList)
			{
				OME_element.appendChild(experimentList_value.asXMLElement(document));
			}
		}
		if (plateList != null)
		{
			// Element property Plate which is complex (has
			// sub-elements) and occurs more than once
			for (Plate plateList_value : plateList)
			{
				OME_element.appendChild(plateList_value.asXMLElement(document));
			}
		}
		if (screenList != null)
		{
			// Element property Screen which is complex (has
			// sub-elements) and occurs more than once
			for (Screen screenList_value : screenList)
			{
				OME_element.appendChild(screenList_value.asXMLElement(document));
			}
		}
		if (experimenterList != null)
		{
			// Element property Experimenter which is complex (has
			// sub-elements) and occurs more than once
			for (Experimenter experimenterList_value : experimenterList)
			{
				OME_element.appendChild(experimenterList_value.asXMLElement(document));
			}
		}
		if (groupList != null)
		{
			// Element property Group which is complex (has
			// sub-elements) and occurs more than once
			for (Group groupList_value : groupList)
			{
				OME_element.appendChild(groupList_value.asXMLElement(document));
			}
		}
		if (instrumentList != null)
		{
			// Element property Instrument which is complex (has
			// sub-elements) and occurs more than once
			for (Instrument instrumentList_value : instrumentList)
			{
				OME_element.appendChild(instrumentList_value.asXMLElement(document));
			}
		}
		if (imageList != null)
		{
			// Element property Image which is complex (has
			// sub-elements) and occurs more than once
			for (Image imageList_value : imageList)
			{
				OME_element.appendChild(imageList_value.asXMLElement(document));
			}
		}
		if (structuredAnnotations != null)
		{
			// Element property StructuredAnnotations which is complex (has
			// sub-elements)
			OME_element.appendChild(structuredAnnotations.asXMLElement(document));
		}
		if (roiList != null)
		{
			// Element property ROI which is complex (has
			// sub-elements) and occurs more than once
			for (ROI roiList_value : roiList)
			{
				OME_element.appendChild(roiList_value.asXMLElement(document));
			}
		}
		if (binaryOnly != null)
		{
			// Element property BinaryOnly which is complex (has
			// sub-elements)
			OME_element.appendChild(binaryOnly.asXMLElement(document));
		}
		return super.asXMLElement(document, OME_element);
	}
}
