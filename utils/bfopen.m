function [result] = bfopen(id)

% A script for opening microscopy images in MATLAB using Bio-Formats.
%
% Portions of this code were adapted from:
% http://www.mathworks.com/support/solutions/data/1-2WPAYR.html?solution=1-2WPAYR
%
% Excluding figure creation, this method is ~1.5x-2.5x slower than
% Bio-Formats's command line showinf tool (MATLAB R14 vs. java 1.6.0_03),
% due to overhead from reshaping arrays and converting pixel types.
%
% Thanks to Ville Rantanen for his performance improvements and ideas.
% Thanks to Brett Shoelson of The MathWorks for his excellent suggestions.
%
% To install, download loci_tools.jar from:
%   http://www.loci.wisc.edu/ome/formats.html
% Internet Explorer sometimes erroneously renames the Bio-Formats library
% to loci_tools.zip. If this happens, rename it back to loci_tools.jar.
% Place loci_tools.jar and this script (bfopen.m) in your MATLAB work folder.

% load Bio-Formats library into MATLAB environment
javaaddpath('loci_tools.jar');
% Alternately, you can add the library to MATLAB's static class path:
%   1. Type "edit classpath.txt" at the MATLAB prompt.
%   2. Go to the end of the file, and add the path to your JAR file
%      (e.g., C:/Program Files/MATLAB/work/loci_tools.jar).
%   3. Save the file and restart MATLAB.

r = loci.formats.ChannelFiller();
r = loci.formats.ChannelSeparator(r);
r = loci.formats.FileStitcher(r);
tic
r.setId(id);
numSeries = r.getSeriesCount();
for s = 1:numSeries
    fprintf('Reading series #%d', s);
    r.setSeries(s - 1);
    w = r.getSizeX();
    h = r.getSizeY();
    numImages = r.getImageCount();
    for i = 1:numImages
        fprintf('.');
        img = r.openImage(i - 1);
        % convert Java BufferedImage to MATLAB image
        pix = img.getData.getPixels(0, 0, w, h, []);
        arr = reshape(pix, [w h])';
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
        % plot the image plane in a new figure
        figure('Name', label);
        imagesc(arr);
        % If you have the image processing toolbox, you could use:
        %imshow(arr, []);
        % You could do something else with each plane
        % here, rather than just overwriting the result.
        result = arr;
    end
    fprintf('\n');
end
toc
