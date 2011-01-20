import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * This class is used to generate a text file containing all of the
 * image file extensions supported by Bio-Formats.
 */
public class BioFormatsExtensionPrinter {

	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Generating list of Bio-Formats supported suffixes...");
		IFormatReader reader = new ImageReader();
		String[] suffixes = reader.getSuffixes();
		
		PrintWriter fo = null;

		fo = new PrintWriter(new FileWriter("build/BioFormatsSuffixes.txt"));
		
		for(String s : suffixes)
		{
			fo.println("*." + s);
		}
		
		fo.close();
		
		System.out.println(suffixes.length + " suffixes discovered.");
	}

}
