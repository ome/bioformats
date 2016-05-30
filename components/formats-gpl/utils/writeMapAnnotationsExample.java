/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2015 - 2016 Open Microscopy Environment. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */

/**
 * 
 *
 * @author Balaji Ramalingam &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:b.ramalingam@dundee.ac.uk">b.ramalingam@dundee.ac.uk</a>
 * @since 5.1
 */

import java.util.ArrayList;

import loci.formats.FormatTools;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLServiceImpl;
import loci.formats.services.OMEXMLService;
import loci.common.services.ServiceFactory;
import loci.formats.meta.IMetadata;


public class writeMapAnnotationsExample {

    public static void main(String[] args) throws Exception{

        if (args.length < 1) {
            System.out.println("Please specify an output file name.");
            System.exit(1);
        }
        String id = args[0];

        // create blank 512x512 image
        System.out.println("Creating random image...");
        int w = 512, h = 512, c = 1;
        int pixelType = FormatTools.UINT16;
        byte[] img = new byte[w * h * c * FormatTools.getBytesPerPixel(pixelType)];

        // fill with random data
        for (int i=0; i<img.length; i++) img[i] = (byte) (256 * Math.random());

        // Create MapPair Object and add to List
        java.util.List<ome.xml.model.MapPair> mapList = new java.util.ArrayList<ome.xml.model.MapPair>();
        mapList.add(new ome.xml.model.MapPair("Example Key","Example Value"));
        mapList.add(new ome.xml.model.MapPair("Bio-Formats Version", FormatTools.VERSION));

        // create metadata object with minimum required metadata fields
        System.out.println("Populating metadata...");
        //add (minimum+Map)Annotations to the metadata object
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        IMetadata metadata = service.createOMEXMLMetadata();
        metadata.createRoot();
        MetadataTools.populateMetadata(metadata, 0, null, false, "XYZCT",
                FormatTools.getPixelTypeString(pixelType), w, h, 1, c, 1, c);
        
        int mapAnnotationIndex = 0;
        int annotationRefIndex = 0;
        String mapAnnotationID = MetadataTools.createLSID("MapAnnotation", 0, mapAnnotationIndex);
        
        metadata.setMapAnnotationID(mapAnnotationID, mapAnnotationIndex);
        metadata.setMapAnnotationValue(mapList, mapAnnotationIndex);
        metadata.setMapAnnotationAnnotator("Example Map Annotation", mapAnnotationIndex);
        metadata.setMapAnnotationDescription("Example Description", mapAnnotationIndex);
        metadata.setMapAnnotationNamespace("Example NameSpace", mapAnnotationIndex);
        metadata.setImageAnnotationRef(mapAnnotationID,0, annotationRefIndex);

        mapAnnotationIndex = 1;
        annotationRefIndex = 1;
        mapAnnotationID = MetadataTools.createLSID("MapAnnotation", 0, mapAnnotationIndex);
        metadata.setMapAnnotationID(mapAnnotationID, mapAnnotationIndex);
        metadata.setMapAnnotationValue(mapList, mapAnnotationIndex);
        metadata.setMapAnnotationAnnotator("Example Map Annotation 1", mapAnnotationIndex);
        metadata.setMapAnnotationDescription("Example Description 1", mapAnnotationIndex);
        metadata.setMapAnnotationNamespace("Example NameSpace 1", mapAnnotationIndex);
        metadata.setImageAnnotationRef(mapAnnotationID,0, annotationRefIndex);
        
        //Initialize writer and save file
        ImageWriter writer = new ImageWriter();
        writer.setMetadataRetrieve(metadata);
        writer.setId(id);
        writer.saveBytes(0, img);
        writer.close();

        System.out.println("Done.");

    }

}
