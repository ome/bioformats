package loci.plugins.in;


import ij.IJ;
//import net.imagej.ImageJ;
import ij.ImageJ;
import ij.ImagePlus;
import loci.common.DebugTools;
import loci.formats.in.ZeissCZIReader;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;

public class ImageJOpen {

    public static void main(String... arg) throws Exception {
        //Long.parseLong()
        //Long l1 = Long.parseUnsignedLong("5329794269271856694");
        //System.out.println(l1/(1024*1024*1024)+" Gb");
        //DebugTools.setRootLevel("TRACE");
        /*long start = System.currentTimeMillis();
        //File fTest = new File("N:\\temp-Nico\\TL-03.czi");
        File fTest = new File("C:\\Users\\nicol\\Dropbox\\Romain-Experiment-10-Airyscan Processing-05.czi");
        System.out.println("Start reading file "+ fTest.getAbsolutePath());

        IFormatReader fr;
        ZeissCZIFastReader reader = new ZeissCZIFastReader();
        reader.setFlattenedResolutions(true);
        reader.setId(fTest.getAbsolutePath());

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Reading file "+ fTest.getAbsolutePath()+" done in "+(timeElapsed/1000.0)+"s");
        reader.close();
        System.out.println("Reader closed");*/

        String pathfczi = "C:\\Users\\nicol\\Dropbox\\Romain-Experiment-10-Airyscan Processing-05.fczi";
        String pathczi = "C:\\Users\\nicol\\Dropbox\\Romain-Experiment-10-Airyscan Processing-05.czi";

        pathfczi = "N:\\temp-Nico\\TL-03.czi";
        pathfczi = "N:\\temp-Nico\\2023-Clearing\\LocalData\\2023-02-10\\LSM980\\20220628-ClearedOrganoid.czi";
        pathfczi = "N:\\temp-Nico\\2023-Clearing\\LocalData\\2023-02-10\\LSM980\\AKS-40x-LSM980-ImmersolG-Fine.czi";
        pathfczi= "N:\\temp-Nico\\PCB03\\Pos-100x1800-0p010-60-170-Lattice Lightsheet-Deskew.czi";
        pathfczi = "N:\\temp-Romain\\2022_Spirochrome\\LSM980\\20220121\\Experiment-08-Airyscan Processing-04.czi";
        pathfczi = "N:\\temp-Romain\\2022_Spirochrome\\LSM980\\20220121\\Experiment-08.czi"; // FAIL! <- Airyscan non processed
        pathfczi = "Z:\\Data\\Marine\\2023-03-17\\Cfra_LS2-01.czi";
        pathfczi = "Z:\\Data\\Marine\\2023-03-16\\Cfra_LS2-03.czi";
        pathfczi = "Z:\\Data\\Marine\\2023-03-16\\Cfra_LS2-03_MIP.czi";
        pathfczi = "F:\\230321_21008.czi";
        //pathfczi = "F:\\230316_stitched.czi";
        //pathfczi = "F:\\Experiment-488.czi";
        //pathfczi = "C:\\Users\\nicol\\Dropbox\\Experiment-08.czi";
        //pathfczi = "C:\\Users\\nicol\\Dropbox\\230321_21008.czi";
        pathfczi = "C:\\Users\\nicol\\Dropbox\\230316_stitched.czi";
        //pathfczi="Z:\\Data\\Omaya Dudin\\2022-11-22\\HighRes\\FM4-64-03.czi";
        //pathfczi="Z:\\Data\\2023-02-16_Marine\\2023-02-16\\New-02.czi";
        //pathfczi="Z:\\Data\\2023-02-16_Marine\\2023-02-16\\New-02-Lattice Lightsheet-03.czi";
        //pathfczi="Z:\\Data\\2023-02-16_Marine\\2023-02-16\\New-02_MIP.czi";
        //pathfczi="Z:\\Data\\2022-11-07-Omaya\\2022-11-08\\New-05-Lattice Lightsheet-06.czi"; // Multiresolution after LLS processing


        //IJ.run("Bio-Formats Importer", "open=["+path+"] open_all_series");
        final ImageJ ij = new ImageJ();
        DebugTools.enableLogging("DEBUG");
        //SwingUtilities.invokeAndWait(() ->);
        ij.setVisible(true);
        //MemoryMonitor mm = new MemoryMonitor();
        System.gc();
        System.gc();
        System.out.println("Start:"+IJ.currentMemory()/(1024*1024));

        //Thread.sleep(2000);
        System.out.println("Reader start");
        ZeissCZIReader reader = new ZeissCZIReader();
        reader.setFlattenedResolutions(false);
        reader.setId(pathfczi);
        System.out.println("nSeries="+reader.getSeriesCount());
        for (int i = 0; i<reader.getSeriesCount();i++) {
            reader.setSeries(i);
            System.out.println("nRes="+reader.getResolutionCount());
        }
        System.out.println("Reader end");
        System.gc();
        System.gc();
        System.out.println("One reader:"+IJ.currentMemory()/(1024*1024));

        //Thread.sleep(2000);
        System.out.println("Reader start");
        ZeissCZIReader reader2 = new ZeissCZIReader();
        reader2.setFlattenedResolutions(true);
        reader2.setId(pathfczi);
        System.out.println("nSeries="+reader2.getSeriesCount());
        for (int i = 0; i<reader2.getSeriesCount();i++) {
            reader2.setSeries(i);
            System.out.println("nRes="+reader2.getResolutionCount());
        }
        System.out.println("Reader end");
        System.gc();
        System.gc();
        System.out.println("Two readers:"+IJ.currentMemory()/(1024*1024));



        tic();
        ImporterOptions options = new ImporterOptions();
        options.setAutoscale(false);
        options.setId(pathfczi);
        options.setOpenAllSeries(true);
        //options.setSeriesOn(0,true);
        //options.setSeriesOn(4,true);
        //options.setConcatenate(true);
        options.setShowOMEXML(true);
        //options.setStitchTiles(false);
        //ZeissCZIFastReader.ALLOW_AUTOSTITCHING_DEFAULT = true;
        //options.getShowOMEXMLInfo();
        options.setVirtual(false);
        System.out.println("BF Open... ");
        ImagePlus[] imps = BF.openImagePlus(options);
        System.out.println("nImages = "+imps.length);
        /*for (ImagePlus imp: imps) {
            imp.show();
            //Thread.sleep(1000);
        }*/
        /*imps[0].show();
        imps[1].show();
        imps[2].show();
        imps[3].show();
        imps[4].show();
        imps[5].show();*/
        System.out.println("nImages = "+imps.length);
        toc("fczi");
        /*tic();
        options.setId(pathczi);
        imps = BF.openImagePlus(options);
        imps[0].show();
        toc("czi");*/
        /*tic();
        options.setId(pathczi);
        imps = BF.openImagePlus(options);
        imps[0].show();
        toc("czi");
        tic();
        options.setId(pathfczi);
        imps = BF.openImagePlus(options);
        imps[0].show();
        toc("fczi");
        tic();
        options.setId(pathczi);
        imps = BF.openImagePlus(options);
        imps[0].show();
        toc("czi");*/
    }
    static long start;
    public static void tic() {
        start = System.currentTimeMillis();
    }

    public static void toc(String message) {
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(message +": "+(timeElapsed/1000.0)+"s");
    }

    public static void toc() {
        toc("");
    }

}

