//
// ITKBridgePipes.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.itk;
import java.io.IOException;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import java.io.FileOutputStream;

//import loci.formats.*;
import loci.formats.IFormatReader;
import loci.formats.FormatTools;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.in.DefaultMetadataOptions;
import loci.formats.in.MetadataLevel;
import java.io.BufferedOutputStream;

/**
 * ImageConverter is a utility class for converting a file between formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/itk/ITKBridgePipes.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/itk/ITKBridgePipes.java">SVN</a></dd></dl>
 */
public final class ITKBridgePipes {

  // -- Constructor --

  private ITKBridgePipes() { }

  // -- Utility methods --

  /** A utility method for converting a file from the command line. */
  public static boolean readImageInfo(String[] args)
    throws FormatException, IOException
  {
    IFormatReader reader = new ImageReader();
    reader = new ChannelSeparator(reader);

    reader.setMetadataFiltered(true);
    reader.setOriginalMetadataPopulated(true);
    MetadataStore store = MetadataTools.createOMEXMLMetadata();
    if (store == null) System.err.println("OME-Java library not found.");
    else reader.setMetadataStore(store);

    reader.setGroupFiles(false); // avoid grouping all the .lsm when a .mdb is there
    String fname = args[0];
    reader.setId(fname);
    reader.setSeries(0);

     store = reader.getMetadataStore();
    MetadataRetrieve meta = (MetadataRetrieve) store;

   // now print the informations

   // little endian?
    System.out.println( reader.isLittleEndian()? 1:0 );

    // component type
    // set ITK component type
    int pixelType = reader.getPixelType();
    int itkComponentType;
    if( pixelType == FormatTools.UINT8 )
      {
      itkComponentType = 1;
      }
    else if( pixelType == FormatTools.INT8 )
      {
      itkComponentType = 2;
      }
    else if( pixelType == FormatTools.UINT16 )
      {
      itkComponentType = 3;
      }
    else if( pixelType == FormatTools.INT16 )
      {
      itkComponentType = 4;
      }
    else if( pixelType == FormatTools.UINT32 )
      {
      itkComponentType = 5;
      }
    else if( pixelType == FormatTools.INT32 )
      {
      itkComponentType = 6;
      }
    else if( pixelType == FormatTools.FLOAT )
      {
      itkComponentType = 9;
      }
    else if( pixelType == FormatTools.DOUBLE )
      {
      itkComponentType = 10;
      }
    else
      {
      itkComponentType = 0;
      }
    System.out.println( itkComponentType );

    // x, y, z, t, c
    System.out.println( reader.getSizeX() );
    System.out.println( reader.getSizeY() );
    System.out.println( reader.getSizeZ() );
    System.out.println( reader.getSizeT() );
    System.out.println( reader.getEffectiveSizeC() ); // reader.getSizeC()

    // number of components
    System.out.println( reader.getRGBChannelCount() );

    // spacing
    System.out.println( meta.getPixelsPhysicalSizeX(0) );
    System.out.println( meta.getPixelsPhysicalSizeY(0) );
    System.out.println( meta.getPixelsPhysicalSizeZ(0) );
    System.out.println( meta.getPixelsTimeIncrement(0) );
    System.out.println( 1.0 ); // should we give something more useful for this one?

    return true;
  }
  
  /** A utility method for converting a file from the command line. */
  public static boolean read(String[] args)
    throws FormatException, IOException
  {
    IFormatReader reader = new ImageReader();
    reader.setMetadataOptions(new DefaultMetadataOptions(MetadataLevel.MINIMUM));
    reader.setGroupFiles(false); // avoid grouping all the .lsm when a .mdb is there
    String fname = args[0];
    reader.setId(fname);
    reader.setSeries(0);
    
    BufferedOutputStream out = new BufferedOutputStream(System.out);
    for( int c = 0; c < reader.getSizeC(); c++) {
    	for (int t = 0; t < reader.getSizeT(); t++) {
    	    for( int z=0; z < reader.getSizeZ(); z++ ) {
    	      byte[] image = reader.openBytes( reader.getIndex(z, c, t) );
    	      out.write(image);
    	     }
    	}
    }

    
    /* attempts at multidimensional reading
    int pixelType = reader.getPixelType();
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    int rgbChannelCount = reader.getRGBChannelCount();
    
    int xStart = Integer.valueOf(args[2]);
    int xCount = Integer.valueOf(args[3]);
    int yStart = Integer.valueOf(args[4]);
    int yCount = Integer.valueOf(args[5]);
    int zStart = Integer.valueOf(args[6]);
    int zCount = Integer.valueOf(args[7]);
    int tStart = Integer.valueOf(args[8]);
    int tCount = Integer.valueOf(args[9]);
    int cStart = Integer.valueOf(args[10]);
    int cCount = Integer.valueOf(args[11]);
    
    int imageCount = reader.getImageCount();
    
    int bytesPerPlane = xCount * yCount * bpp * rgbChannelCount;
    boolean isInterleaved = reader.isInterleaved();
    
    boolean canDoDirect = false;
    
    if( (rgbChannelCount == 1) || isInterleaved )
    	canDoDirect = true;
    
    byte[] tmpData = null;
    
    if(!canDoDirect)
    	tmpData = new byte[bytesPerPlane];

    BufferedOutputStream out = new BufferedOutputStream(System.out);
    
    byte[] buf = new byte[bytesPerPlane];

    for( int c = cStart; c < (cStart + cCount); c++ )
    {
      for (int t = tStart; t < (tStart + tCount); t++ )
      {
        for (int z = zStart; z < (zStart + zCount); z++ )
        {
        	int no = reader.getIndex(z, c, t);
        	reader.openBytes(no, buf, xStart, yStart, xCount, yCount);
        	
        	if (canDoDirect)
        	{
        		out.write(buf);
        	}
        	else
        	{
        		int pos = 0;
        		for(int x = 0; x < xCount; x++) {
        			for(int y = 0; y < yCount; y++) {
        				for(int i = 0; i < rgbChannelCount; i++) {
        					for(int b = 0; b < bpp; b++) {
        						int index = yCount * (xCount * (rgbChannelCount * b + i) + x) + y;
        						tmpData[pos++] = buf[index];
        					}
        				}
        					
        			}
        		}
        		out.write(tmpData);	
        	}
        }
      }
    }
    */
    out.close();
    System.out.close();
    return true;
  }


  /** A utility method to determine if a file can be read by BioFormats. */
  public static boolean canRead(String[] args)
    throws FormatException, IOException
  {
    boolean h = false;
    IFormatReader reader = new ImageReader();

    try {
      h = reader.isThisType(args[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println(h);
    System.out.close();
    return true;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
	  if(args[1].equals("info"))
	  {
	     if (!readImageInfo(args)) System.exit(1);
	  }
	  else if(args[1].equals("read"))
  	{
  		if (!read(args)) System.exit(1);
  	}
    else if(args[1].equals("canRead"))
    {
      if (!canRead(args)) System.exit(1);
    }
  }

}
