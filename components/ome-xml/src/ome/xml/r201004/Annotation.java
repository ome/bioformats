/*
 * ome.xml.r201004.Annotation
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

public class Annotation extends Object
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

	/** Constructs a Annotation. */
	public Annotation()
	{
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

	// Back reference Image_BackReference
	public int sizeOfLinkedImageList()
	{
		return image_BackReferenceList.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(image_BackReferenceList);
	}

	public Image getLinkedImage(int index)
	{
		return image_BackReferenceList.get(index);
	}

	public Image setLinkedImage(int index, Image image_BackReference)
	{
		return image_BackReferenceList.set(index, image_BackReference);
	}

	public void linkImage(Image image_BackReference)
	{
		this.image_BackReferenceList.add(image_BackReference);
	}

	public void unlinkImage(Image image_BackReference)
	{
		this.image_BackReferenceList.add(image_BackReference);
	}

	// Back reference Pixels_BackReference
	public int sizeOfLinkedPixelsList()
	{
		return pixels_BackReferenceList.size();
	}

	public List<Pixels> copyLinkedPixelsList()
	{
		return new ArrayList<Pixels>(pixels_BackReferenceList);
	}

	public Pixels getLinkedPixels(int index)
	{
		return pixels_BackReferenceList.get(index);
	}

	public Pixels setLinkedPixels(int index, Pixels pixels_BackReference)
	{
		return pixels_BackReferenceList.set(index, pixels_BackReference);
	}

	public void linkPixels(Pixels pixels_BackReference)
	{
		this.pixels_BackReferenceList.add(pixels_BackReference);
	}

	public void unlinkPixels(Pixels pixels_BackReference)
	{
		this.pixels_BackReferenceList.add(pixels_BackReference);
	}

	// Back reference Plane_BackReference
	public int sizeOfLinkedPlaneList()
	{
		return plane_BackReferenceList.size();
	}

	public List<Plane> copyLinkedPlaneList()
	{
		return new ArrayList<Plane>(plane_BackReferenceList);
	}

	public Plane getLinkedPlane(int index)
	{
		return plane_BackReferenceList.get(index);
	}

	public Plane setLinkedPlane(int index, Plane plane_BackReference)
	{
		return plane_BackReferenceList.set(index, plane_BackReference);
	}

	public void linkPlane(Plane plane_BackReference)
	{
		this.plane_BackReferenceList.add(plane_BackReference);
	}

	public void unlinkPlane(Plane plane_BackReference)
	{
		this.plane_BackReferenceList.add(plane_BackReference);
	}

	// Back reference Channel_BackReference
	public int sizeOfLinkedChannelList()
	{
		return channel_BackReferenceList.size();
	}

	public List<Channel> copyLinkedChannelList()
	{
		return new ArrayList<Channel>(channel_BackReferenceList);
	}

	public Channel getLinkedChannel(int index)
	{
		return channel_BackReferenceList.get(index);
	}

	public Channel setLinkedChannel(int index, Channel channel_BackReference)
	{
		return channel_BackReferenceList.set(index, channel_BackReference);
	}

	public void linkChannel(Channel channel_BackReference)
	{
		this.channel_BackReferenceList.add(channel_BackReference);
	}

	public void unlinkChannel(Channel channel_BackReference)
	{
		this.channel_BackReferenceList.add(channel_BackReference);
	}

	// Back reference Project_BackReference
	public int sizeOfLinkedProjectList()
	{
		return project_BackReferenceList.size();
	}

	public List<Project> copyLinkedProjectList()
	{
		return new ArrayList<Project>(project_BackReferenceList);
	}

	public Project getLinkedProject(int index)
	{
		return project_BackReferenceList.get(index);
	}

	public Project setLinkedProject(int index, Project project_BackReference)
	{
		return project_BackReferenceList.set(index, project_BackReference);
	}

	public void linkProject(Project project_BackReference)
	{
		this.project_BackReferenceList.add(project_BackReference);
	}

	public void unlinkProject(Project project_BackReference)
	{
		this.project_BackReferenceList.add(project_BackReference);
	}

	// Back reference Dataset_BackReference
	public int sizeOfLinkedDatasetList()
	{
		return dataset_BackReferenceList.size();
	}

	public List<Dataset> copyLinkedDatasetList()
	{
		return new ArrayList<Dataset>(dataset_BackReferenceList);
	}

	public Dataset getLinkedDataset(int index)
	{
		return dataset_BackReferenceList.get(index);
	}

	public Dataset setLinkedDataset(int index, Dataset dataset_BackReference)
	{
		return dataset_BackReferenceList.set(index, dataset_BackReference);
	}

	public void linkDataset(Dataset dataset_BackReference)
	{
		this.dataset_BackReferenceList.add(dataset_BackReference);
	}

	public void unlinkDataset(Dataset dataset_BackReference)
	{
		this.dataset_BackReferenceList.add(dataset_BackReference);
	}

	// Back reference Experimenter_BackReference
	public int sizeOfLinkedExperimenterList()
	{
		return experimenter_BackReferenceList.size();
	}

	public List<Experimenter> copyLinkedExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenter_BackReferenceList);
	}

	public Experimenter getLinkedExperimenter(int index)
	{
		return experimenter_BackReferenceList.get(index);
	}

	public Experimenter setLinkedExperimenter(int index, Experimenter experimenter_BackReference)
	{
		return experimenter_BackReferenceList.set(index, experimenter_BackReference);
	}

	public void linkExperimenter(Experimenter experimenter_BackReference)
	{
		this.experimenter_BackReferenceList.add(experimenter_BackReference);
	}

	public void unlinkExperimenter(Experimenter experimenter_BackReference)
	{
		this.experimenter_BackReferenceList.add(experimenter_BackReference);
	}

	// Back reference Plate_BackReference
	public int sizeOfLinkedPlateList()
	{
		return plate_BackReferenceList.size();
	}

	public List<Plate> copyLinkedPlateList()
	{
		return new ArrayList<Plate>(plate_BackReferenceList);
	}

	public Plate getLinkedPlate(int index)
	{
		return plate_BackReferenceList.get(index);
	}

	public Plate setLinkedPlate(int index, Plate plate_BackReference)
	{
		return plate_BackReferenceList.set(index, plate_BackReference);
	}

	public void linkPlate(Plate plate_BackReference)
	{
		this.plate_BackReferenceList.add(plate_BackReference);
	}

	public void unlinkPlate(Plate plate_BackReference)
	{
		this.plate_BackReferenceList.add(plate_BackReference);
	}

	// Back reference Reagent_BackReference
	public int sizeOfLinkedReagentList()
	{
		return reagent_BackReferenceList.size();
	}

	public List<Reagent> copyLinkedReagentList()
	{
		return new ArrayList<Reagent>(reagent_BackReferenceList);
	}

	public Reagent getLinkedReagent(int index)
	{
		return reagent_BackReferenceList.get(index);
	}

	public Reagent setLinkedReagent(int index, Reagent reagent_BackReference)
	{
		return reagent_BackReferenceList.set(index, reagent_BackReference);
	}

	public void linkReagent(Reagent reagent_BackReference)
	{
		this.reagent_BackReferenceList.add(reagent_BackReference);
	}

	public void unlinkReagent(Reagent reagent_BackReference)
	{
		this.reagent_BackReferenceList.add(reagent_BackReference);
	}

	// Back reference Screen_BackReference
	public int sizeOfLinkedScreenList()
	{
		return screen_BackReferenceList.size();
	}

	public List<Screen> copyLinkedScreenList()
	{
		return new ArrayList<Screen>(screen_BackReferenceList);
	}

	public Screen getLinkedScreen(int index)
	{
		return screen_BackReferenceList.get(index);
	}

	public Screen setLinkedScreen(int index, Screen screen_BackReference)
	{
		return screen_BackReferenceList.set(index, screen_BackReference);
	}

	public void linkScreen(Screen screen_BackReference)
	{
		this.screen_BackReferenceList.add(screen_BackReference);
	}

	public void unlinkScreen(Screen screen_BackReference)
	{
		this.screen_BackReferenceList.add(screen_BackReference);
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

	// Back reference Well_BackReference
	public int sizeOfLinkedWellList()
	{
		return well_BackReferenceList.size();
	}

	public List<Well> copyLinkedWellList()
	{
		return new ArrayList<Well>(well_BackReferenceList);
	}

	public Well getLinkedWell(int index)
	{
		return well_BackReferenceList.get(index);
	}

	public Well setLinkedWell(int index, Well well_BackReference)
	{
		return well_BackReferenceList.set(index, well_BackReference);
	}

	public void linkWell(Well well_BackReference)
	{
		this.well_BackReferenceList.add(well_BackReference);
	}

	public void unlinkWell(Well well_BackReference)
	{
		this.well_BackReferenceList.add(well_BackReference);
	}

	// Back reference WellSample_BackReference
	public int sizeOfLinkedWellSampleList()
	{
		return wellSample_BackReferenceList.size();
	}

	public List<WellSample> copyLinkedWellSampleList()
	{
		return new ArrayList<WellSample>(wellSample_BackReferenceList);
	}

	public WellSample getLinkedWellSample(int index)
	{
		return wellSample_BackReferenceList.get(index);
	}

	public WellSample setLinkedWellSample(int index, WellSample wellSample_BackReference)
	{
		return wellSample_BackReferenceList.set(index, wellSample_BackReference);
	}

	public void linkWellSample(WellSample wellSample_BackReference)
	{
		this.wellSample_BackReferenceList.add(wellSample_BackReference);
	}

	public void unlinkWellSample(WellSample wellSample_BackReference)
	{
		this.wellSample_BackReferenceList.add(wellSample_BackReference);
	}

	// Back reference ROI_BackReference
	public int sizeOfLinkedROIList()
	{
		return roi_backReferenceList.size();
	}

	public List<ROI> copyLinkedROIList()
	{
		return new ArrayList<ROI>(roi_backReferenceList);
	}

	public ROI getLinkedROI(int index)
	{
		return roi_backReferenceList.get(index);
	}

	public ROI setLinkedROI(int index, ROI roi_backReference)
	{
		return roi_backReferenceList.set(index, roi_backReference);
	}

	public void linkROI(ROI roi_backReference)
	{
		this.roi_backReferenceList.add(roi_backReference);
	}

	public void unlinkROI(ROI roi_backReference)
	{
		this.roi_backReferenceList.add(roi_backReference);
	}

	// Back reference Shape_BackReference
	public int sizeOfLinkedShapeList()
	{
		return shape_BackReferenceList.size();
	}

	public List<Shape> copyLinkedShapeList()
	{
		return new ArrayList<Shape>(shape_BackReferenceList);
	}

	public Shape getLinkedShape(int index)
	{
		return shape_BackReferenceList.get(index);
	}

	public Shape setLinkedShape(int index, Shape shape_BackReference)
	{
		return shape_BackReferenceList.set(index, shape_BackReference);
	}

	public void linkShape(Shape shape_BackReference)
	{
		this.shape_BackReferenceList.add(shape_BackReference);
	}

	public void unlinkShape(Shape shape_BackReference)
	{
		this.shape_BackReferenceList.add(shape_BackReference);
	}

	// Back reference ListAnnotation_BackReference
	public int sizeOfLinkedListAnnotationList()
	{
		return listAnnotation_BackReferenceList.size();
	}

	public List<ListAnnotation> copyLinkedListAnnotationList()
	{
		return new ArrayList<ListAnnotation>(listAnnotation_BackReferenceList);
	}

	public ListAnnotation getLinkedListAnnotation(int index)
	{
		return listAnnotation_BackReferenceList.get(index);
	}

	public ListAnnotation setLinkedListAnnotation(int index, ListAnnotation listAnnotation_BackReference)
	{
		return listAnnotation_BackReferenceList.set(index, listAnnotation_BackReference);
	}

	public void linkListAnnotation(ListAnnotation listAnnotation_BackReference)
	{
		this.listAnnotation_BackReferenceList.add(listAnnotation_BackReference);
	}

	public void unlinkListAnnotation(ListAnnotation listAnnotation_BackReference)
	{
		this.listAnnotation_BackReferenceList.add(listAnnotation_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Annotation
		Element Annotation_element = document.createElement("Annotation");
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
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		if (pixels_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Pixels_BackReference
		}
		if (plane_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Plane_BackReference
		}
		if (channel_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Channel_BackReference
		}
		if (project_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Project_BackReference
		}
		if (dataset_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Dataset_BackReference
		}
		if (experimenter_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Experimenter_BackReference
		}
		if (plate_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Plate_BackReference
		}
		if (reagent_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Reagent_BackReference
		}
		if (screen_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Screen_BackReference
		}
		if (plateAcquisition_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
		}
		if (well_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Well_BackReference
		}
		if (wellSample_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference WellSample_BackReference
		}
		if (roi_backReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference ROI_BackReference
		}
		if (shape_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Shape_BackReference
		}
		if (listAnnotation_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference ListAnnotation_BackReference
		}
		return Annotation_element;
	}
}
