function [result] = bfopen(id)

%{
A script for opening microscopy images in MATLAB using Bio-Formats.

The function returns a list of image series; i.e., a cell array of cell
arrays of (matrix, label) pairs, with each matrix representing a single
image plane, and each inner list of matrices representing an image
series. See the bottom of this file below for examples of usage.

Portions of this code were adapted from:
http://www.mathworks.com/support/solutions/data/1-2WPAYR.html?solution=1-2WPAYR

This method is ~1.5x-2.5x slower than Bio-Formats's command line showinf
tool (MATLAB R14 vs. java 1.6.0_03), due to overhead from reshaping arrays
and converting pixel types.

Thanks to all who offered suggestions and improvements:
    * Ville Rantanen
    * Brett Shoelson
    * Martin Offterdinger
    * Tony Collins
    * Cris Luengo

Internet Explorer sometimes erroneously renames the Bio-Formats library
to loci_tools.zip. If this happens, rename it back to loci_tools.jar.
%}

% load the Bio-Formats library into the MATLAB environment
javaaddpath(fullfile(fileparts(mfilename('fullpath')),'loci_tools.jar'));
%{
Alternately, you can add the library to MATLAB's static class path:
    1. Type "edit classpath.txt" at the MATLAB prompt.
    2. Go to the end of the file, and add the path to your JAR file
       (e.g., C:/Program Files/MATLAB/work/loci_tools.jar).
    3. Save the file and restart MATLAB.
%}

% to work with Evotec Flex, fill in your LuraWave license code
%javaaddpath(fullfile(fileparts(mfilename('fullpath')),'lwf_jsdk2.6.jar'));
%java.lang.System.setProperty('lurawave.license', 'xxxxxx-xxxxxxx');

r = loci.formats.ChannelFiller();
r = loci.formats.ChannelSeparator(r);

% uncomment the following line to enable grouping of similarly
% named files into a single dataset based on file numbering
%r = loci.formats.FileStitcher(r);

tic
r.setId(id);
numSeries = r.getSeriesCount();
result = cell(numSeries, 2);
for s = 1:numSeries
    fprintf('Reading series #%d', s);
    r.setSeries(s - 1);
    w = r.getSizeX();
    h = r.getSizeY();
    shape = [w h];
    pixelType = r.getPixelType();
    bpp = loci.formats.FormatTools.getBytesPerPixel(pixelType);
    fp = loci.formats.FormatTools.isFloatingPoint(pixelType);
    little = r.isLittleEndian();
    numImages = r.getImageCount();
    imageList = cell(numImages, 2);
    colorMaps = cell(numImages);
    for i = 1:numImages
        fprintf('.');
        plane = r.openBytes(i - 1);

        % retrieve color map data
        nBytes = loci.formats.FormatTools.getBytesPerPixel(r.getPixelType());
        if nBytes == 1
          colorMaps{s, i} = r.get8BitLookupTable()';
        else
          colorMaps{s, i} = r.get16BitLookupTable()';
        end

        newMap = zeros(size(colorMaps{s, i}, 1), size(colorMaps{s, i}, 2));
        for (row = 1:size(colorMaps{s, i}, 1))
           for (col = 1:size(colorMaps{s, i}, 2))
              newMap(row, col) = colorMaps{s, i}(row, col);
              if newMap(row, col) < 0
                  newMap(row, col) = newMap(row, col) + power(2, nBytes * 8);
              end
              newMap(row, col) = newMap(row, col) / (power(2, nBytes * 8) - 1);
           end
        end
        colorMaps{s, i} = newMap;

        % convert byte array to MATLAB image
        pix = loci.common.DataTools.makeDataArray(plane, bpp, fp, little);
        arr = reshape(pix, shape)';
        % build an informative title for our figure
        label = id;
        if numSeries > 1
            qs = int2str(s);
            label = [label, '; series ', qs, '/', int2str(numSeries)];
        end
        if numImages > 1
            qi = int2str(i);
            label = [label, '; plane ', qi, '/', int2str(numImages)];
            if r.isOrderCertain()
                lz = 'Z';
                lc = 'C';
                lt = 'T';
            else
                lz = 'Z?';
                lc = 'C?';
                lt = 'T?';
            end
            zct = r.getZCTCoords(i - 1);
            sizeZ = r.getSizeZ();
            if sizeZ > 1
                qz = int2str(zct(1) + 1);
                label = [label, '; ', lz, '=', qz, '/', int2str(sizeZ)];
            end
            sizeC = r.getSizeC();
            if sizeC > 1
                qc = int2str(zct(2) + 1);
                label = [label, '; ', lc, '=', qc, '/', int2str(sizeC)];
            end
            sizeT = r.getSizeT();
            if sizeT > 1
                qt = int2str(zct(3) + 1);
                label = [label, '; ', lt, '=', qt, '/', int2str(sizeT)];
            end
        end
        % save image plane and label into the list
        imageList{i, 1} = arr;
        imageList{i, 2} = label;
    end
    % extract metadata table for this series
    metadataList = r.getMetadata();
    % save images and metadata into our master series list
    result{s, 1} = imageList;
    result{s, 2} = metadataList;
    result{s, 3} = colorMaps;
    fprintf('\n');
end
r.close();
toc

%{
Here are some examples of accessing data using the bfopen function:

% read the data using Bio-Formats
data = bfopen('C:/data/experiment.lif');

% unwrap some specific image planes from the result
numSeries = size(data, 1);
series1 = data{1, 1};
series2 = data{2, 1};
series3 = data{3, 1};
metadataList = data{1, 2};
% ...etc.
series1_numPlanes = size(series1, 1);
series1_plane1 = series1{1, 1};
series1_label1 = series1{1, 2};
series1_plane2 = series1{2, 1};
series1_label2 = series1{2, 2};
series1_plane3 = series1{3, 1};
series1_label3 = series1{3, 2};
% ...etc.

% plot the 1st series's 1st image plane in a new figure
series1_colorMaps = data{1, 3};
figure('Name', series1_label1);
if isempty(series1_colorMaps{1})
  colormap(gray);
else
  colormap(series1_colorMaps{1});
end
imagesc(series1_plane1);

% Or if you have the image processing toolbox, you could use:
% imshow(series1_plane1, []);

% Or animate as a movie (assumes 8-bit unsigned data)
v = linspace(0, 1, 256)';
cmap = [v v v];
for p = 1:series1_numPlanes
    M(p) = im2frame(uint8(series1{p, 1}), cmap);
end
movie(M);

% Query some metadata fields (keys are format-dependent)
subject = metadataList.get('Subject');
title = metadataList.get('Title');
%}
