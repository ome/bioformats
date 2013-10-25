package loci.formats.in;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CellVoyagerReader extends FormatReader
{

	private static final String NAMESPACE = "http://www.yokogawa.co.jp/BTS/BTSSchema/1.0";

	private static final String XML_FILE = "MeasurementSetting.xml";

	private Location measurementSettingFile;

	private Location imageIndexFile;

	private Map< Integer, Map< Integer, Map< Integer, Map< Integer, String >>> > imageList;

	private List< ChannelInfo > channelInfos;

	private List< Integer > timePoints;

	private Location measurementFolder;

	private ChannelInfo ci;

	public CellVoyagerReader()
	{
		super( "CellVoyager", new String[] { "tif", "tiff", "xml" } );
		this.suffixNecessary = false;
		this.suffixSufficient = false;
		this.hasCompanionFiles = true;
		this.datasetDescription = "Directory with two master files 'MeasurementSetting.xml' and 'ImageIndex.xml' , used to stich together several tiff files.";
		this.domains = new String[] { FormatTools.HISTOLOGY_DOMAIN, FormatTools.LM_DOMAIN, FormatTools.HCS_DOMAIN };
	}

	@Override
	public byte[] openBytes( final int no, final byte[] buf, final int x, final int y, final int w, final int h ) throws FormatException, IOException
	{
		FormatTools.checkPlaneParameters( this, no, buf.length, x, y, w, h );

		final CoreMetadata cm = core.get( 0 );

		final int nImagesPerTimepoint = cm.sizeC * cm.sizeZ;
		final int targetTindex = no / nImagesPerTimepoint;

		final int rem = no % nImagesPerTimepoint;
		final int targetZindex = rem / cm.sizeC;
		final int targetCindex = rem % cm.sizeC;

		// Careful: everything is 1-based in the file format.
		final Map< Integer, Map< Integer, Map< Integer, String >>> tFilenames = imageList.get( Integer.valueOf( targetTindex + 1 ) );
		final Map< Integer, Map< Integer, String >> cFilenames = tFilenames.get( Integer.valueOf( targetCindex + 1 ) );
		final Map< Integer, String > zFilenames = cFilenames.get( Integer.valueOf( targetZindex + 1 ) );
		final MinimalTiffReader tiffReader = new MinimalTiffReader();
		for ( final Integer fieldIndex : zFilenames.keySet() )
		{
			String filename = zFilenames.get( fieldIndex );
			filename = filename.replace( '\\', File.separatorChar );
			final Location image = new Location( measurementFolder, filename );
			if ( !image.exists() ) { throw new IOException( "Could not find required file: " + image ); }

			tiffReader.setId( image.getAbsolutePath() );

			final long[] offset = ci.offsets.get( fieldIndex - 1 );

			// Tile size
			final int tw = ci.tileWidth;
			final int th = ci.tileHeight;

			// Field bounds in full final image, full width, full height
			// (referential named '0', as if x=0 and y=0).
			final int xbs0 = ( int ) ( offset[ 0 ] );
			final int ybs0 = ( int ) ( offset[ 1 ] );

			// Subimage bounds in full final image is simply x, y, x+w, y+h

			// Do they intersect?
			if ( x + w < xbs0 || xbs0 + tw < x || y + h < ybs0 || ybs0 + th < y )
			{
				continue;
			}

			// Common rectangle in reconstructed image referential.
			final int xs0 = Math.max( xbs0 - x, 0 );
			final int ys0 = Math.max( ybs0 - y, 0 );

			// Common rectangle in tile referential (named with '1').
			final int xs1 = Math.max( x - xbs0, 0 );
			final int ys1 = Math.max( y - ybs0, 0 );
			final int xe1 = Math.min( tw, x + w - xbs0 );
			final int ye1 = Math.min( th, y + h - ybs0 );
			final int w1 = xe1 - xs1;
			final int h1 = ye1 - ys1;

			if ( w1 <= 0 || h1 <= 0 )
			{
				continue;
			}

			// Get corresponding data.
			final byte[] bytes = tiffReader.openBytes( 0, xs1, ys1, w1, h1 );
			final int nbpp = cm.bitsPerPixel / 8;

			for ( int row1 = 0; row1 < h1; row1++ )
			{
				// Line index in tile coords
				final int ls1 = nbpp * ( row1 * w1 );
				final int length = nbpp * w1;

				// Line index in reconstructed image coords
				final int ls0 = nbpp * ( ( ys0 + row1 ) * w + xs0 );

				// Transfer
				System.arraycopy( bytes, ls1, buf, ls0, length );
			}
			tiffReader.close();
		}

		return buf;
	}

	@Override
	public int fileGroupOption( final String id ) throws FormatException, IOException
	{
		return FormatTools.MUST_GROUP;
	}

	@Override
	public int getRequiredDirectories( final String[] files ) throws FormatException, IOException
	{
		/*
		 * We only need the directory where there is the two xml files. The
		 * parent durectory seems to contain only hardware macros to load and
		 * eject the plate or slide.
		 */
		return 0;
	}

	@Override
	public boolean isSingleFile( final String id ) throws FormatException, IOException
	{
		return false;
	}

	@Override
	public boolean isThisType( final String name, final boolean open )
	{
		/*
		 * We want to be pointed to any file in the directory that contains
		 * 'MeasurementSetting.xml'.
		 */
		final String localName = new Location( name ).getName();
		if ( localName.equals( XML_FILE ) ) { return true; }
		final Location parent = new Location( name ).getAbsoluteFile().getParentFile();
		final Location xml = new Location( parent, XML_FILE );
		if ( !xml.exists() ) { return false; }

		return super.isThisType( name, open );
	}

	@Override
	protected void initFile( final String id ) throws FormatException, IOException
	{
		super.initFile( id );

		measurementFolder = new Location( id );
		if ( !measurementFolder.exists() ) { throw new IOException( "File " + id + " does not exist." ); }
		if ( !measurementFolder.isDirectory() )
		{
			measurementFolder = measurementFolder.getParentFile();
		}

		measurementSettingFile = new Location( measurementFolder, "MeasurementSetting.xml" );
		if ( !measurementSettingFile.exists() ) { throw new IOException( "Could not find " + measurementSettingFile + " in folder." ); }

		imageIndexFile = new Location( measurementFolder, "ImageIndex.xml" );
		if ( !imageIndexFile.exists() ) { throw new IOException( "Could not find " + imageIndexFile + " in folder." ); }

		/*
		 * Open MeasurementSettings file
		 */

		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
		}
		catch ( final ParserConfigurationException e )
		{
			e.printStackTrace();
		}
		Document msDocument = null;
		try
		{
			msDocument = dBuilder.parse( measurementSettingFile.getAbsolutePath() );
		}
		catch ( final SAXException e )
		{
			e.printStackTrace();
		}

		msDocument.getDocumentElement().normalize();

		/*
		 * Open OME metadata file
		 */

		final Location omeXmlFile = new Location( measurementFolder, "MeasurementResult.ome.xml" );
		Document omeDocument = null;
		try
		{
			omeDocument = dBuilder.parse( omeXmlFile.getAbsolutePath() );
		}
		catch ( final SAXException e )
		{
			e.printStackTrace();
		}

		omeDocument.getDocumentElement().normalize();

		/*
		 * Extract channel Metadata from MeasurementSetting.xml & OME xml file
		 */

		channelInfos = readInfo( msDocument, omeDocument );
		timePoints = readTimePoints( msDocument );

		/*
		 * Populate CORE metadata accordingly.
		 */

		final CoreMetadata coreMetadata = core.get( 0 );
		ci = channelInfos.get( 0 ); // Get common data from first channel

		// Bit depth.
		final int inferredBD = Integer.parseInt( ci.bitDepth );
		switch ( inferredBD )
		{
		case 8:
			coreMetadata.pixelType = FormatTools.UINT8;
			coreMetadata.bitsPerPixel = 8;
			break;
		case 16:
			coreMetadata.pixelType = FormatTools.UINT16;
			coreMetadata.bitsPerPixel = 16;
			break;
		case 32:
			coreMetadata.pixelType = FormatTools.UINT32;
			coreMetadata.bitsPerPixel = 32;
			break;
		default:
			throw new FormatException( "Unknown bit-depth: " + ci.bitDepth );
		}

		// Sub Channel info? I don't understand that actually?
		coreMetadata.cLengths = new int[] { coreMetadata.sizeC };
		coreMetadata.cTypes = new String[] { FormatTools.CHANNEL };
		coreMetadata.rgb = false; // 1 image = 1 color

		// Channel order
		coreMetadata.dimensionOrder = "XYCZT";
		coreMetadata.orderCertain = true;

		// Indexed color. I guess it's true since we can read the LUT
		// separately.
		coreMetadata.indexed = true;
		coreMetadata.falseColor = true;

		// Total number of images
		int activeChannels = 0;
		for ( final ChannelInfo channelInfo : channelInfos )
		{
			if ( channelInfo.isEnabled )
			{
				activeChannels++;
			}
		}
		final int nImages = activeChannels * ci.nZSlices * timePoints.size();
		coreMetadata.imageCount = nImages;

		// Interleaved. Maybe irrelevant for us?
		coreMetadata.interleaved = true;

		// Dimensions
		coreMetadata.sizeX = ci.width;
		coreMetadata.sizeY = ci.height;
		coreMetadata.sizeZ = ci.nZSlices;
		coreMetadata.sizeC = activeChannels;
		coreMetadata.sizeT = timePoints.size();

		// Are we a thumbnail? No, we are the real thing.
		coreMetadata.thumbnail = false;

		/*
		 * Build image list
		 */

		final DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
		dbFactory2.setNamespaceAware( true );
		DocumentBuilder dBuilder2 = null;
		try
		{
			dBuilder2 = dbFactory2.newDocumentBuilder();
		}
		catch ( final ParserConfigurationException e )
		{
			e.printStackTrace();
		}
		Document document2 = null;
		try
		{
			document2 = dBuilder2.parse( imageIndexFile.getAbsolutePath() );
		}
		catch ( final SAXException e )
		{
			e.printStackTrace();
		}

		document2.getDocumentElement().normalize();
		imageList = buildImageList( document2 );

	}

	@Override
	public String[] getSeriesUsedFiles( final boolean noPixels )
	{
		FormatTools.assertId( currentId, true, 1 );

		if ( noPixels )
		{
			return new String[] { measurementSettingFile.getAbsolutePath(), imageIndexFile.getAbsolutePath() };
		}
		else
		{
			final int nFields = channelInfos.get( 0 ).offsets.size();
			final String[] images = new String[ getImageCount() * nFields + 2 ];
			int index = 0;
			images[ index++ ] = measurementSettingFile.getAbsolutePath();
			images[ index++ ] = imageIndexFile.getAbsolutePath();

			for ( final Integer timepoint : imageList.keySet() )
			{
				final Map< Integer, Map< Integer, Map< Integer, String >>> timeImages = imageList.get( timepoint );
				for ( final Integer channel : timeImages.keySet() )
				{
					final Map< Integer, Map< Integer, String >> channelImages = timeImages.get( channel );
					for ( final Integer zindex : channelImages.keySet() )
					{
						final Map< Integer, String > tilesAtZ = channelImages.get( zindex );
						for ( final Integer fieldIndex : tilesAtZ.keySet() )
						{
							final String filename = tilesAtZ.get( fieldIndex );
							images[ index++ ] = filename;
						}
					}
				}

			}

			return images;
		}

	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * @return a Map of
	 *         <code>timepoint (int) -> channel (int) -> Z (index) -> field index (int) -> file name (String)</code>
	 */
	private Map< Integer, Map< Integer, Map< Integer, Map< Integer, String >>> > buildImageList( final Document document )
	{
		final Map< Integer, Map< Integer, Map< Integer, Map< Integer, String > > > > allFilenames = new HashMap< Integer, Map< Integer, Map< Integer, Map< Integer, String > > > >();

		final Element root = document.getDocumentElement();

		final NodeList measurements = root.getElementsByTagNameNS( NAMESPACE, "MeasurementRecord" );

		for ( int i = 0; i < measurements.getLength(); i++ )
		{
			final Element element = ( Element ) measurements.item( i );


			final int field = Integer.parseInt( element.getAttributeNS( NAMESPACE, "FieldIndex" ) );
			final int timepoint = Integer.parseInt( element.getAttributeNS( NAMESPACE, "TimePoint" ) );
			final int zindex = Integer.parseInt( element.getAttributeNS( NAMESPACE, "ZIndex" ) );
			final int channel = Integer.parseInt( element.getAttributeNS( NAMESPACE, "Ch" ) );
			final boolean isMIP = Boolean.parseBoolean( element.getAttributeNS( NAMESPACE, "Mip" ) );

			if ( isMIP )
			{
				continue;
			}

			final String filename = element.getTextContent();

			// Extract time
			Map< Integer, Map< Integer, Map< Integer, String >>> timeFilenames = allFilenames.get( Integer.valueOf( timepoint ) );
			if ( null == timeFilenames )
			{
				timeFilenames = new TreeMap< Integer, Map< Integer, Map< Integer, String >>>();
				allFilenames.put( Integer.valueOf( timepoint ), timeFilenames );
			}

			// Extract channel
			Map< Integer, Map< Integer, String >> channelFilenames = timeFilenames.get( Integer.valueOf( channel ) );
			if ( null == channelFilenames )
			{
				channelFilenames = new TreeMap< Integer, Map< Integer, String > >();
				timeFilenames.put( Integer.valueOf( channel ), channelFilenames );
			}

			// Extract Z
			Map< Integer, String > tilesAtZ = channelFilenames.get( Integer.valueOf( zindex ) );
			if ( null == tilesAtZ )
			{
				tilesAtZ = new TreeMap< Integer, String >();
				channelFilenames.put( Integer.valueOf( zindex ), tilesAtZ );
			}

			// Extract field
			tilesAtZ.put( field, filename );

		}

		return allFilenames;
	}

	private List< ChannelInfo > readInfo( final Document msDocument, final Document omeDocument )
	{

		/*
		 * Read the ome.xml file. Since it is malformed, we need to parse all
		 * nodes, and add an "ID" attribute to those who do not have it.
		 */

		final NodeList nodeList = omeDocument.getElementsByTagName( "*" );
		for ( int i = 0; i < nodeList.getLength(); i++ )
		{
			final Node node = nodeList.item( i );
			if ( node.getNodeType() == Node.ELEMENT_NODE )
			{
				final NamedNodeMap atts = node.getAttributes();

				final Node namedItem = atts.getNamedItem( "ID" );
				if ( namedItem == null )
				{
					( ( Element ) node ).setAttribute( "ID", "none" );
				}
			}
		}

		/*
		 * Now that the XML document is properly formed, we can build a metadata
		 * object from it.
		 */

		OMEXMLService service = null;
		String xml = null;
		try
		{
			xml = XMLTools.getXML( omeDocument );
		}
		catch ( final TransformerConfigurationException e2 )
		{
			e2.printStackTrace();
		}
		catch ( final TransformerException e2 )
		{
			e2.printStackTrace();
		}
		try
		{
			service = new ServiceFactory().getInstance( OMEXMLService.class );
		}
		catch ( final DependencyException e1 )
		{
			e1.printStackTrace();
		}
		OMEXMLMetadata omeMD = null;
		if ( service != null )
		{
			try
			{
				omeMD = service.createOMEXMLMetadata( xml );
			}
			catch ( final ServiceException e )
			{
				e.printStackTrace();
			}
		}

		// Pass it to the reader, populate the MetadataStore
		final MetadataStore store = makeFilterMetadata();
		MetadataTools.populatePixels( store, this, true );
		service.convertMetadata( omeMD, store );

		System.out.println( prettyPrintXML( omeMD.dumpXML() ) );// DEBUG


		/*
		 * The rest
		 */

		final List< ChannelInfo > channels = new ArrayList< ChannelInfo >();

		final Element msRoot = msDocument.getDocumentElement();

		/*
		 * Magnification
		 */

		final double objectiveMagnification = Double.parseDouble( getChildText( msRoot, new String[] { "SelectedObjectiveLens", "Magnification" } ) );
		final double zoomLensMagnification = Double.parseDouble( getChildText( msRoot, new String[] { "ZoomLens", "Magnification", "Value" } ) );
		final double magnification = objectiveMagnification * zoomLensMagnification;

		/*
		 * Channels
		 */
		final Element channelsEl = getChild( msRoot, "Channels" );
		final NodeList channelElements = channelsEl.getElementsByTagName( "Channel" );
		for ( int i = 0; i < channelElements.getLength(); i++ )
		{
			final Element channelElement = ( Element ) channelElements.item( i );
			final boolean isEnabled = Boolean.parseBoolean( getChildText( channelElement, "IsEnabled" ) );
			if ( !isEnabled )
			{
				continue;
			}

			final ChannelInfo ci = new ChannelInfo();
			channels.add( ci );

			ci.isEnabled = true;

			ci.channelNumber = Integer.parseInt( getChildText( channelElement, "Number" ) );

			final Element acquisitionSettings = getChild( channelElement, "AcquisitionSetting" );

			final Element cameraEl = getChild( acquisitionSettings, "Camera" );
			ci.tileWidth = Integer.parseInt( getChildText( cameraEl, "EffectiveHorizontalPixels_pixel" ) );
			ci.tileHeight = Integer.parseInt( getChildText( cameraEl, "EffectiveVerticalPixels_pixel" ) );

			ci.unmagnifiedPixelWidth = Double.parseDouble( getChildText( cameraEl, "HorizonalCellSize_um" ) );
			ci.unmagnifiedPixelHeight = Double.parseDouble( getChildText( cameraEl, "VerticalCellSize_um" ) );

			final Element colorElement = getChild( channelElement, new String[] { "ContrastEnhanceParam", "Color" } );
			final int r = Integer.parseInt( getChildText( colorElement, "R" ) );
			final int g = Integer.parseInt( getChildText( colorElement, "G" ) );
			final int b = Integer.parseInt( getChildText( colorElement, "B" ) );
			final int a = Integer.parseInt( getChildText( colorElement, "A" ) );
			ci.channelColor = new Color( r, g, b, a );

			ci.bitDepth = getChildText( channelElement, new String[] { "ContrastEnhanceParam", "BitDepth" } );
			ci.pixelWidth = ci.unmagnifiedPixelWidth / magnification;
			ci.pixelHeight = ci.unmagnifiedPixelWidth / magnification;

			// Detector gain
			final Element gainElement = getChild( acquisitionSettings, "Gain_percent" );
			ci.gainPercent = Double.parseDouble( getChildText( gainElement, "Value" ) );

			// // Camera key
			ci.cameraKey = getChildText( acquisitionSettings, "CameraParameterKey" );

			/*
			 * Fields, for each channel
			 */

			for ( final ChannelInfo channelInfo : channels )
			{

				// TODO handles each well & area as a series here
				final Element fieldsEl = getChild( msRoot, new String[] { "Wells", "Well", "Areas", "Area", "Fields" } );
				final NodeList fieldElements = fieldsEl.getElementsByTagName( "Field" );

				// Read field position in um
				double xmin = Double.POSITIVE_INFINITY;
				double ymin = Double.POSITIVE_INFINITY;
				double xmax = Double.NEGATIVE_INFINITY;
				double ymax = Double.NEGATIVE_INFINITY;
				final ArrayList< double[] > offsetsUm = new ArrayList< double[] >();

				for ( int j = 0; j < fieldElements.getLength(); j++ )
				{
					final Element fieldElement = ( Element ) fieldElements.item( j );

					final double xum = Double.parseDouble( getChildText( fieldElement, "StageX_um" ) );
					if ( xum < xmin )
					{
						xmin = xum;
					}
					if ( xum > xmax )
					{
						xmax = xum;
					}

					/*
					 * Careful! For the fields to be padded correctly, we need
					 * to invert their Y position, so that it matches the pixel
					 * orientation.
					 */
					final double yum = -Double.parseDouble( getChildText( fieldElement, "StageY_um" ) );
					if ( yum < ymin )
					{
						ymin = yum;
					}
					if ( yum > ymax )
					{
						ymax = yum;
					}

					offsetsUm.add( new double[] { xum, yum } );
				}

				// Convert in pixel position
				final List< long[] > offsets = new ArrayList< long[] >();
				for ( final double[] offsetUm : offsetsUm )
				{
					final long x = Math.round( ( offsetUm[ 0 ] - xmin ) / ( channelInfo.unmagnifiedPixelWidth / magnification ) );
					final long y = Math.round( ( offsetUm[ 1 ] - ymin ) / ( channelInfo.unmagnifiedPixelHeight / magnification ) );

					offsets.add( new long[] { x, y } );
				}

				channelInfo.offsets = offsets;

				final int width = 1 + ( int ) ( ( xmax - xmin ) / ( channelInfo.unmagnifiedPixelWidth / magnification ) );
				final int height = 1 + ( int ) ( ( ymax - ymin ) / ( channelInfo.unmagnifiedPixelWidth / magnification ) );
				channelInfo.width = width + channelInfo.tileWidth;
				channelInfo.height = height + channelInfo.tileHeight;

			}

			/*
			 * Z range
			 */

			final int nZSlices = Integer.parseInt( getChildText( msRoot, new String[] { "ZRange", "NumberOfSlices" } ) );
			final double zStroke = Double.parseDouble( getChildText( msRoot, new String[] { "ZRange", "Stroke_um" } ) );
			final double pixelDepth = zStroke / ( nZSlices - 1 );

			for ( final ChannelInfo channelInfo : channels )
			{
				channelInfo.nZSlices = nZSlices;
				channelInfo.pixelDepth = pixelDepth;
				channelInfo.spaceUnits = "µm";
			}

		}

		return channels;
	}

	private List< Integer > readTimePoints( final Document document )
	{
		final Element root = document.getDocumentElement();
		final int nTimePoints = Integer.parseInt( getChildText( root, new String[] { "TimelapsCondition", "Iteration" } ) );
		//
		final List< Integer > timepoints = new ArrayList< Integer >( nTimePoints );
		for ( int i = 0; i < nTimePoints; i++ )
		{
			timepoints.add( Integer.valueOf( i ) );
		}
		return timepoints;
	}

	@SuppressWarnings( "unused" )
	private double readFrameInterval( final Document document )
	{
		final Element root = document.getDocumentElement();
		final double dt = Double.parseDouble( getChildText( root, new String[] { "TimelapsCondition", "Interval" } ) );
		return dt;
	}

	/*
	 * INNER CLASSES
	 */

	private static final class ChannelInfo
	{

		public String cameraKey;

		public double gainPercent;

		public int height;

		public int width;

		public int nZSlices;

		public String spaceUnits;

		public double pixelHeight;

		public double pixelWidth;

		public double pixelDepth;

		public List< long[] > offsets = Collections.emptyList();

		public boolean isEnabled;

		public String bitDepth;

		public Color channelColor;

		public double unmagnifiedPixelHeight;

		public double unmagnifiedPixelWidth;

		public int tileHeight;

		public int tileWidth;

		public int channelNumber;

		@Override
		public String toString()
		{
			final StringBuffer str = new StringBuffer();
			str.append( "Channel " + channelNumber + ": \n" );
			str.append( " - isEnabled: " + isEnabled + "\n" );
			str.append( " - width: " + width + "\n" );
			str.append( " - height: " + height + "\n" );
			str.append( " - tile width: " + tileWidth + "\n" );
			str.append( " - tile height: " + tileHeight + "\n" );
			str.append( " - NZSlices: " + nZSlices + "\n" );
			str.append( " - unmagnifiedPixelWidth: " + unmagnifiedPixelWidth + "\n" );
			str.append( " - unmagnifiedPixelHeight: " + unmagnifiedPixelHeight + "\n" );
			str.append( " - color: " + channelColor + "\n" );
			str.append( " - camera: " + cameraKey + "\n" );
			str.append( " - bitDepth: " + bitDepth + "\n" );
			str.append( " - detectorGain: " + gainPercent + "%\n" );
			str.append( " - has " + offsets.size() + " fields:\n" );
			int index = 1;
			for ( final long[] offset : offsets )
			{
				str.append( "    " + index++ + ": x = " + offset[ 0 ] + ", y = " + offset[ 1 ] + "\n" );
			}
			str.append( " - spatial calibration:\n" );
			str.append( "    dx = " + pixelWidth + " " + spaceUnits + "\n" );
			str.append( "    dy = " + pixelHeight + " " + spaceUnits + "\n" );
			str.append( "    dz = " + pixelDepth + " " + spaceUnits + "\n" );
			return str.toString();
		}

	}

	/*
	 * MAIN METHOD
	 */

	public static void main( final String[] args ) throws IOException, FormatException
	{
		final String id = "/Users/tinevez/Projects/EArena/Data/30um sections at 40x - last round/1_3_1_2_1/20130731T133016/MeasurementSetting.xml";

		final CellVoyagerReader r = new CellVoyagerReader();
		r.setId( id );
		r.close();

	}

	private static final Element getChild( final Element parent, final String childName )
	{
		final NodeList childNodes = parent.getChildNodes();
		for ( int i = 0; i < childNodes.getLength(); i++ )
		{
			final Node item = childNodes.item( i );
			if ( item.getNodeName().equals( childName ) ) { return ( Element ) item; }
		}
		return null;
	}

	private static final Element getChild( final Element parent, final String[] path )
	{
		if ( path.length == 1 ) { return getChild( parent, path[ 0 ] ); }

		final NodeList childNodes = parent.getChildNodes();
		for ( int i = 0; i < childNodes.getLength(); i++ )
		{
			final Node item = childNodes.item( i );
			if ( item.getNodeName().equals( path[ 0 ] ) ) { return getChild( ( Element ) item, Arrays.copyOfRange( path, 1, path.length ) ); }
		}
		return null;
	}

	private static final String getChildText( final Element parent, final String[] path )
	{
		if ( path.length == 1 ) { return getChildText( parent, path[ 0 ] ); }

		final NodeList childNodes = parent.getChildNodes();
		for ( int i = 0; i < childNodes.getLength(); i++ )
		{
			final Node item = childNodes.item( i );
			if ( item.getNodeName().equals( path[ 0 ] ) ) { return getChildText( ( Element ) item, Arrays.copyOfRange( path, 1, path.length ) ); }
		}
		return null;
	}

	private static final String getChildText( final Element parent, final String childName )
	{
		final NodeList childNodes = parent.getChildNodes();
		for ( int i = 0; i < childNodes.getLength(); i++ )
		{
			final Node item = childNodes.item( i );
			if ( item.getNodeName().equals( childName ) ) { return item.getTextContent(); }
		}
		return null;
	}

	private static final String prettyPrintXML(final String xml) {
		try
		{
			final Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();

			serializer.setOutputProperty( OutputKeys.INDENT, "yes" );

			serializer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );
			final Source xmlSource = new SAXSource( new InputSource( new ByteArrayInputStream( xml.getBytes( "utf-8" ) ) ) );
			final StreamResult res = new StreamResult( new ByteArrayOutputStream() );

			serializer.transform( xmlSource, res );

			return new String( ( ( ByteArrayOutputStream ) res.getOutputStream() ).toByteArray() );
		}
		catch ( final Exception e )
		{
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings( "unused" )
	private static final String prettyPrintXML( final Document doc )
	{
		Transformer transformer = null;
		try
		{
			transformer = TransformerFactory.newInstance().newTransformer();
		}
		catch ( final TransformerConfigurationException e )
		{
			e.printStackTrace();
		}
		catch ( final TransformerFactoryConfigurationError e )
		{
			e.printStackTrace();
		}
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		final StreamResult result = new StreamResult( new StringWriter() );
		final DOMSource source = new DOMSource( doc );
		try
		{
			transformer.transform( source, result );
		}
		catch ( final TransformerException e )
		{
			e.printStackTrace();
		}
		final String xmlString = result.getWriter().toString();
		return xmlString;
	}
}
