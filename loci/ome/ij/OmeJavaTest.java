/*
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
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
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.ds.tests;

import java.net.MalformedURLException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import org.openmicroscopy.ds.Criteria;
import org.openmicroscopy.ds.DataFactory;
import org.openmicroscopy.ds.DataServer;
import org.openmicroscopy.ds.DataServices;
import org.openmicroscopy.ds.RemoteCaller;
import org.openmicroscopy.ds.ServerVersion;

import org.openmicroscopy.ds.dto.DataColumn;
import org.openmicroscopy.ds.dto.Dataset;
import org.openmicroscopy.ds.dto.SemanticType;
import org.openmicroscopy.ds.dto.Image;

import org.openmicroscopy.ds.st.Experimenter;
import org.openmicroscopy.ds.st.Pixels;
import org.openmicroscopy.ds.st.Repository;

import org.openmicroscopy.is.PixelsFactory;
import org.openmicroscopy.is.ImageServerException;

/**
 * 
 * This example file illustrates some of the very basics of connecting to OME
 * vs. the OME-JAVA library. In addition to making a connection, this code retrieves 
 * a data column, a dataset, and an image, along with some additional information
 * about each.
 * 
 * This file is obviously far from complete documentation of ome-java.  It's provided 
 * merely as a quick introduction that might help developers get started.
 * 
 * The following jar files will be needed to run this code:
 * 	ome-java.jar
 *  xmlrpc-1.2-b1.jar
 *  commons-httpclient-2.0-rc2.jar
 * 
 *  All of these files are included in the OME-JAVA distribution
 * 
 * @author <br>Harry Hochheiser &nbsp;&nbsp;&nbsp;
 * 	<A HREF="mailto:hsh@nih.gov">hsh@nih.gov</A>
 *
 *  @version 2.2
 * <small>
 * </small>
 * @since OME2.2
 */
public class OmeJavaTest {

	/** 
	 * Update the following variables as appropriate for your local installation.
	 */
	private static final String SHOOLA_URL="http://ome.grc.nia.nih.gov/shoola";
	private static final String USER ="hsh";
	private static final String PASS = "foobar";
	
	private static final String IMG_TYPE="png";
	private static final String IMG_FILE="foo.png";
	
	/** 
	 * Update the following variables as appropriate for your local installation.
	 */
	private static final int COLUMN_WITH_REF_ST=4;
	private static final int DATASET_ID=1;
	private static final int IMAGE_ID=1;
	
	private DataServices services;
	private DataFactory factory;
	
	public OmeJavaTest() {
		super();
		
		try {
			// construct connection to omeds
			services = DataServer.getDefaultServices(SHOOLA_URL);
			// doe the basic login
			factory  = initializeFactory(services);
			if (factory != null) {
				getDataColumn();
				getDataset();
				getImage();
			}
		}
		catch (MalformedURLException e) {
			System.err.println("Improperly specified Shoola URL: "+SHOOLA_URL);
			System.exit(0);
		}	
	}
	
	private DataFactory initializeFactory(DataServices services) {
		System.err.println("trying to get data...");
		//  login 
		RemoteCaller remote = services.getRemoteCaller();
		remote.login(USER,PASS);
		// test with server version
		ServerVersion ver = remote.getServerVersion();
		System.err.println("Server Version "+ ver);
		
		// retrieve the DataFactory which is used for data requests
		return (DataFactory) services.getService(DataFactory.class);
			
	}
	
	private void getDataColumn() {
		
		// get data column - build up criteria, and grab it.
		// these criteria specify which fields are retrieved: essentially a 
		// project off of a join (on reference_semantic_type).
		Criteria c = new Criteria();
		c.addWantedField("id");
		c.addWantedField("data_table");
		c.addWantedField("column_name");
		c.addWantedField("reference_semantic_type");
		c.addWantedField("reference_semantic_type","id");
		c.addWantedField("reference_semantic_type","name");
		
		// this value might needto be changed for other installations.
		c.addFilter(	"id", new Integer(COLUMN_WITH_REF_ST));
		
		// Standard DataFactory call for retrieving object of a given class,
		// according to certain criteria
		DataColumn dc = (DataColumn) factory.retrieve(DataColumn.class,c);
		
		System.err.println("data column name is "+dc.getColumnName());
		// get st 
		SemanticType st = dc.getReferenceType();
		
		// if st is not null, print details.
		if (st != null) {
			System.err.println("Semantic type is "+st.getID()+","+st.getName());
		}

	}
	
	private void getDataset() {

		// Criteria for the dataset
		Criteria c = new Criteria();
		c.addWantedField("id");
		c.addWantedField("name");
		c.addWantedField("images");
		c.addWantedField("description");
		c.addWantedField("owner");
		c.addWantedField("owner","FirstName");
		c.addWantedField("owner","LastName");
		
		// restrict to only the desired dataset.
		c.addFilter(	"id", new Integer(DATASET_ID));
		
		Dataset d = (Dataset) factory.retrieve(Dataset.class,c);
		System.err.println("Dataset: "+d.getName());
		System.err.println("Description: "+d.getDescription());
		System.err.println("# of images .. "+d.countImages());
		
		// The owner is an instance of Semantic Type Experimenter. See
		// Dataset or DatasetDTO for details.
		Experimenter exp = d.getOwner();
		System.err.println("owner: "+exp.getFirstName()+" "+exp.getLastName());
		
	}
	
	public void getImage() {

		Criteria c = new Criteria();
		c.addWantedField("id");
		c.addWantedField("name");
		
		// The default pixels for an image provide the thumbnail.
		c.addWantedField("default_pixels");
		c.addWantedField("default_pixels","id");
		
		// we must get the URL of the repository for retrieval to work.
		c.addWantedField("default_pixels","ImageServerID");
		c.addWantedField("default_pixels","Repository");
		c.addWantedField("default_pixels.Repository","ImageServerURL");
	
		c.addFilter(	"id", new Integer(IMAGE_ID));
		
		// get the image
		Image i = (Image) factory.retrieve(Image.class,c);
		System.err.println("Image: "+i.getID()+", "+i.getName());
		
		// get the pixels.
		Pixels p = i.getDefaultPixels();
		System.err.println("default pixel id ..."+p.getID());
	
		Repository rep = p.getRepository();
		System.err.println("Repository URL is "+rep.getImageServerURL());
		
		// ok. turn this into an local file.
		
		// we need to get a PixelsFactory objct to get a thumbnail.
		PixelsFactory pf = (PixelsFactory) services.getService(PixelsFactory.class);
		try {
			System.err.println("getting thumbnail.");
			// get the thumbnail.
			BufferedImage im  = pf.getThumbnail(p);
			File f = new File(IMG_FILE);
			System.err.println("Saving thumbnail in file "+IMG_FILE);
			ImageIO.write(im,IMG_TYPE,f);
			System.err.println("Thumbnail saved");
		} catch(ImageServerException ise) {
			System.err.println("Can't retrieve pixels for image "+IMAGE_ID);
			System.exit(0);
		}
		catch(IOException ioe) {
			System.err.println("Can't write file "+IMG_FILE);
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		new OmeJavaTest();
	}
}
