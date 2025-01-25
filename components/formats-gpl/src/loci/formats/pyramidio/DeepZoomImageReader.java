/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package loci.formats.pyramidio;


import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DZI pyramid reader. Thread safe.
 *
 * @author Antoine Vandecreme
 *
 * reused and adapted for PreciPoint .vmic WSI files reader
 *
 */
public class DeepZoomImageReader {
    public static final String DZI_FILE = "dzc_output.xml";
    public static final String DZI_FILES = "dzc_output_files";

    private final FileSystem zipfs;
    private final File filesFolder;
    private final int tileSize;
    private final int overlap;
    private final String format;
    private final int width;
    private final int height;
    private final int maxLevel;
    private final ImageTypeSpecifier rawImageType;

    public DeepZoomImageReader(FileSystem dziFile) throws IOException {
        this(dziFile, null);
    }

    public DeepZoomImageReader(FileSystem zipfs, File tileExample) throws IOException {
        this.zipfs = zipfs;
        this.filesFolder = new File(DZI_FILES);

        Path entry = zipfs.getPath(DZI_FILE);
        InputStream is = Files.newInputStream(entry);
        DziFile df = new DziFile(is);

        tileSize = df.getTileSize();
        overlap = df.getOverlap();
        format = df.getFormat();
        width = df.getWidth();
        height = df.getHeight();

        if (tileExample == null) {
            tileExample = getFilesOfLevel(0).get(0);
        }
        try {
            String s = tileExample.getPath();
            entry = zipfs.getPath(s);
            is = Files.newInputStream(entry);
            ImageInputStream iis = ImageIO.createImageInputStream(is);
            ImageReader reader = getImageReader(iis);
            reader.setInput(iis);
            this.rawImageType = reader.getRawImageType(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int maxDim = Math.max(width, height);
        maxLevel = (int) Math.ceil(Math.log(maxDim) / Math.log(2));
    }


    public File getFilesFolder() {
        return filesFolder;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getOverlap() {
        return overlap;
    }

    public String getFormat() {
        return format;
    }

    //@Override
    public int getWidth() {
        return width;
    }

    //@Override
    public int getHeight() {
        return height;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    //@Override
    public BufferedImage read() throws IOException {
        return getWholeImage(1);
    }

    //@Override
    public BufferedImage read(Rectangle rectangle) throws IOException {
        return getRegion(rectangle, 1);
    }

    /**
     * Get the whole image at the specified zoom level.
     *
     * @param zoom The desired zoom level
     * @return
     * @throws IOException
     */
    public BufferedImage getWholeImage(double zoom) throws IOException {
        return getRegion(new Rectangle(width, height), zoom);
    }

    /**
     * Get the region specified. Pixels outside of the image are filled in
     * black.
     *
     * @param region
     * @param zoom
     * @return
     * @throws IOException
     */
    public BufferedImage getRegion(Rectangle region, double zoom) throws IOException {
        if (region == null || region.isEmpty()) {
            throw new IllegalArgumentException("Region cannot be empty.");
        }

        int resultWidth = (int) Math.round(region.width * zoom);
        int resultHeight = (int) Math.round(region.height * zoom);
        if (resultWidth < 1 || resultHeight < 1) {
            throw new IllegalArgumentException("Zoom too small for width or height.");
        }

        Rectangle imageArea = new Rectangle(width, height);
        Rectangle intersection = imageArea.intersection(region);
        if (region.equals(intersection)) {
            return getSubImage(region, zoom);
        }

        BufferedImage result;
        synchronized (this) {
            result = rawImageType.createBufferedImage(
                    resultWidth, resultHeight);
        }

        if (!intersection.isEmpty()) {
            BufferedImage subimage = getSubImage(intersection, zoom);
            int intersectionX = region.x < 0
                    ? (int) Math.round(-region.x * zoom) : 0;
            int intersectionY = region.y < 0
                    ? (int) Math.round(-region.y * zoom) : 0;
            result.getRaster().setRect(intersectionX, intersectionY,
                    subimage.getRaster());
            subimage.flush();
        }
        return result;
    }

    /**
     * Get the sub image specified by the region. The region must be entirely
     * inside the image.
     *
     * @param region
     * @param zoom
     * @return
     * @throws IOException
     */
    public BufferedImage getSubImage(Rectangle region, double zoom)
            throws IOException {
        if (region == null || region.isEmpty()) {
            throw new IllegalArgumentException("Region cannot be empty.");
        }

        Rectangle wholeImage = new Rectangle(width, height);
        if (!wholeImage.contains(region)) {
            throw new IllegalArgumentException("Region outside image.");
        }

        int resultWidth = (int) Math.round(region.width * zoom);
        int resultHeight = (int) Math.round(region.height * zoom);

        if (resultWidth < 1 || resultHeight < 1) {
            throw new IllegalArgumentException("Zoom too small for width or height.");
        }

        int level = getClosestLevel(zoom);
        double zoomOfLevel = getZoomOfLevel(level);
        int x = (int) Math.round(region.x * zoomOfLevel);
        int y = (int) Math.round(region.y * zoomOfLevel);
        int w = (int) Math.round(region.width * zoomOfLevel);
        int h = (int) Math.round(region.height * zoomOfLevel);

        BufferedImage image = readRegionOfLevel(new Rectangle(x, y, w, h), level);
        return ImageResizingHelper.resizeImage(image, resultWidth, resultHeight);
    }

    public BufferedImage readRegionOfLevel(Rectangle region, int level)
            throws IOException {
        int firstTileColumn = region.x / tileSize;
        if (tileSize * (firstTileColumn + 1) - overlap <= region.x) {
            firstTileColumn++;
        }
        int firstTileRow = region.y / tileSize;
        if (tileSize * (firstTileRow + 1) - overlap <= region.y) {
            firstTileRow++;
        }
        int lastTileColumn = (region.x + region.width) / tileSize;
        if (tileSize * lastTileColumn + overlap >= region.x + region.width
                && lastTileColumn != 0) {
            lastTileColumn--;
        }
        int lastTileRow = (region.y + region.height) / tileSize;
        if (tileSize * lastTileRow + overlap >= region.y + region.height
                && lastTileRow != 0) {
            lastTileRow--;
        }

        BufferedImage result = null;
        WritableRaster raster = null;
        int dx = 0;
        for (int i = firstTileColumn; i <= lastTileColumn; i++) {
            int x;
            int w;
            if (i == firstTileColumn) {
                x = region.x - firstTileColumn * tileSize;
                if (i == lastTileColumn) {
                    w = region.width;
                } else {
                    w = tileSize - x;
                }
                if (firstTileColumn != 0) {
                    x += overlap;
                }
            } else {
                x = overlap;
                if (i == lastTileColumn) {
                    w = region.width - dx;
                } else {
                    w = tileSize;
                }
            }

            int dy = 0;
            for (int j = firstTileRow; j <= lastTileRow; j++) {
                int y;
                int h;
                if (j == firstTileRow) {
                    y = region.y - firstTileRow * tileSize;
                    if (j == lastTileRow) {
                        h = region.height;
                    } else {
                        h = tileSize - y;
                    }
                    if (firstTileRow != 0) {
                        y += overlap;
                    }
                } else {
                    y = overlap;
                    if (j == lastTileRow) {
                        h = region.height - dy;
                    } else {
                        h = tileSize;
                    }
                }

                Rectangle area = new Rectangle(x, y, w, h);
                BufferedImage tile = readRegionOfTile(area, level, i, j);
                if (i == firstTileColumn && j == firstTileRow) {
                    result = BufferedImageHelper.createBufferedImage(
                            region.width, region.height, tile);
                    raster = result.getRaster();
                }
                raster.setRect(dx, dy, tile.getRaster());
                tile.flush();
                dy += h;
            }
            dx += w;
        }
        return result;
    }

    private BufferedImage readRegionOfTile(Rectangle region, int level,
            int column, int row) throws IOException {
        File levelFolder = new File(filesFolder, Integer.toString(level));
        File tile = new File(levelFolder, column + "_" + row + "." + format);

        String s = tile.getPath();
        Path entry = zipfs.getPath(s);

        if (Files.exists(entry)) {
            InputStream is = Files.newInputStream(entry);
            try (ImageInputStream iis = ImageIO.createImageInputStream(is)) {
                ImageReader reader = getImageReader(iis);
                reader.setInput(iis);
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(region);
                return reader.read(0, param);
            }

        } else {
            BufferedImage image = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, tileSize, tileSize);
            g2d.dispose();
            return image;
        }
    }

    private int getClosestLevel(double zoom) {
        if (zoom > 0.5) {
            return maxLevel;
        }
        return maxLevel + (int) Math.ceil(Math.log(zoom) / Math.log(2));
    }

    public double getZoomOfLevel(int level) {
        return Math.pow(2, level - maxLevel);
    }

    public List<File> getFilesOfLevel(int level) {
        int widthOfLevel = 1;
        int heightOfLevel = 1;

        if (level != 0) {
            widthOfLevel = (int) Math.round(width * getZoomOfLevel(level)); //Math.min(2 * level, width);
            heightOfLevel = (int) Math.round(height * getZoomOfLevel(level)); //Math.min(2 * level, height);
        }

        int numColumns = (int) Math.ceil(widthOfLevel / (float) tileSize);
        int numRows = (int) Math.ceil(heightOfLevel / (float) tileSize);

        File levelFolder = new File(filesFolder, Integer.toString(level));
        ArrayList<File> result = new ArrayList<>(numColumns * numRows);
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                File file = new File(levelFolder, i + "_" + j + "." + format);
                result.add(file);
            }
        }
        return result;
    }

    private static ImageReader getImageReader(ImageInputStream iis)
            throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        if (!readers.hasNext()) {
            throw new IOException("No compatible image reader found.");
        }
        return readers.next();
    }
}
