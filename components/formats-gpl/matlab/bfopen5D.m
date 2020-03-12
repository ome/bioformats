function [ResData,AllColorMaps,LabelList,FileInfo,OMEMetaData,seriesNumber] = bfopen5D(id, varargin)
% Open microscopy images using Bio-Formats.
% based on the bfopen function, but optimized for importing up to 5D data.
%
% SYNOPSIS [ResData,AllColorMaps,LabelList,FileInfo,OMEMetaData] = bfopen5D(id)
%          [ResData,AllColorMaps,LabelList,FileInfo,OMEMetaData] = bfopen5D(id, x, y, w, h)
%
% Input
%    r - the reader object (e.g. the output bfGetReader)
%
%    x - (Optional) A scalar giving the x-origin of the tile.
%    Default: 1
%
%    y - (Optional) A scalar giving the y-origin of the tile.
%    Default: 1
%
%    w - (Optional) A scalar giving the width of the tile.
%    Set to the width of the plane by default.
%
%    h - (Optional) A scalar giving the height of the tile.
%    Set to the height of the plane by default.
%
% Output
%
%    result - a cell array of cell arrays of (matrix, label) pairs,
%    with each matrix representing a single image plane, and each inner
%    list of matrices representing an image series.
%
% Portions of this code were adapted from:
% http://www.mathworks.com/support/solutions/en/data/1-2WPAYR/
%
% This method is ~1.5x-2.5x slower than Bio-Formats's command line
% showinf tool (MATLAB 7.0.4.365 R14 SP2 vs. java 1.6.0_20),
% due to overhead from copying arrays.
%
% Thanks to all who offered suggestions and improvements:
%     * Ville Rantanen
%     * Brett Shoelson
%     * Martin Offterdinger
%     * Tony Collins
%     * Cris Luengo
%     * Arnon Lieber
%     * Jimmy Fong
%     * Rainer Heintzmann
%
% NB: Internet Explorer sometimes erroneously renames the Bio-Formats library
%     to bioformats_package.zip. If this happens, rename it back to
%     bioformats_package.jar.
%
% For many examples of how to use the bfopen function, please see:
%     https://docs.openmicroscopy.org/latest/bio-formats/developers/matlab-dev.html

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2007 - 2017 Open Microscopy Environment:
%   - Board of Regents of the University of Wisconsin-Madison
%   - Glencoe Software, Inc.
%   - University of Dundee
%
% This program is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as
% published by the Free Software Foundation, either version 2 of the
% License, or (at your option) any later version.
%
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License along
% with this program; if not, write to the Free Software Foundation, Inc.,
% 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

% -- Configuration - customize this section to your liking --

% Toggle the autoloadBioFormats flag to control automatic loading
% of the Bio-Formats library using the javaaddpath command.
%
% For static loading, you can add the library to MATLAB's class path:
%     1. Type "edit classpath.txt" at the MATLAB prompt.
%     2. Go to the end of the file, and add the path to your JAR file
%        (e.g., C:/Program Files/MATLAB/work/bioformats_package.jar).
%     3. Save the file and restart MATLAB.
%
% There are advantages to using the static approach over javaaddpath:
%     1. If you use bfopen within a loop, it saves on overhead
%        to avoid calling the javaaddpath command repeatedly.
%     2. Calling 'javaaddpath' may erase certain global parameters.
% autoloadBioFormats = 1;

ResData=[];AllColorMaps=[];FileInfo=[];OMEMetaData=[];LabelList=[];

% Toggle the stitchFiles flag to control grouping of similarly
% named files into a single dataset based on file numbering.
stitchFiles = 0;
seriesNumber=1;

% To work with compressed Evotec Flex, fill in your LuraWave license code.
%lurawaveLicense = 'xxxxxx-xxxxxxx';

% -- Main function - no need to edit anything past this point --

% load the Bio-Formats library into the MATLAB environment
if (0)
   status = bfCheckJavaPath(autoloadBioFormats);
   assert(status, ['Missing Bio-Formats library. Either add bioformats_package.jar '...
   'to the static Java path or add it to the Matlab path.']);
else
    p = javaclasspath('-all');
    % p = regexp(p,'loci_tools\.jar$','once');
    p = regexp(p,'bioformats_package\.jar$','once');
    p = [p{:}];
    if isempty(p)
       locifile = fullfile(fileparts(which('readim')),'private','loci_tools.jar');
       if ~exist(locifile,'file')
          error('Bio-Formats JAR file not installed.')
       end
       if strcmp(dipgetpref('DebugMode'),'on')
          warning('All your global variables are cleared! Due to "javaaddpath" very strange design choice.')
       end
    javaaddpath(locifile);
    % javaaddpath('Toolboxes\bfmatlab\bioformats_package.jar');
    % else: we don't want to change the Java Class Path if not necessary.
    end
end
% Prompt for a file if not input
if nargin == 0 || exist(id, 'file') == 0
  [file, path] = uigetfile(bfGetFileExtensions, 'Choose a file to open');
  id = [path file];
  if isequal(path, 0) || isequal(file, 0), return; end
end

% Initialize logging
bfInitLogging();

% Get the channel filler
r = bfGetReader(id, stitchFiles);

% Test plane size
if nargin >=4
    planeSize = javaMethod('getPlaneSize', 'loci.formats.FormatTools', ...
                           r, varargin{3}, varargin{4});
else
    planeSize = javaMethod('getPlaneSize', 'loci.formats.FormatTools', r);
end

if planeSize/(1024)^3 >= 2,
    error(['Image plane too large. Only 2GB of data can be extracted '...
        'at one time. You can workaround the problem by opening '...
        'the plane in tiles.']);
end

numSeries = r.getSeriesCount();
% result = cell(numSeries, 2);
AllColorMaps = cell(numSeries, 1);

globalMetadata = r.getGlobalMetadata();

qz=1;qc=1;qt=1;
zct=[0,0,0];

AllSizes={};AllNames={};
forceSelection=0;
for s=1:numSeries
    r.setSeries(s - 1);
    SizeX = r.getSizeX();SizeY = r.getSizeY();SizeZ = r.getSizeZ();SizeC = r.getSizeC();SizeT = r.getSizeT();
    AllSizes{s} = [SizeX,SizeY,SizeZ,SizeC,SizeT];
    try
        seriesName = char(r.getMetadataStore().getImageName(s - 1));
        AllNames{s} = sprintf('Series# %2.2d, [%d,%d,%d,%d,%d], %s',s,AllSizes{s},seriesName);
    catch
        AllNames{s} = sprintf('Series# %2.2d, [%d,%d,%d,%d,%d]',s,AllSizes{s});
    end
    if s>1
        if norm(AllSizes{s}(1:3) - AllSizes{1}(1:3)) > 0
            forceSelection=1;
        end
    end
end
if numSeries>1 % forceSelection
    [indx,tf] = listdlg('ListString',AllNames,'PromptString',{'Multiple Series found which are inconsistent in Sizes. Please select the ones to import (equal sizes only!)',''},'SelectionMode','single','ListSize',[650,250]);
    if (tf==0) || isempty(indx)
        return;
    end
else
    indx=[1:numSeries];
end

didReadFirst=0;
for s = 1:numSeries
    if ~ismember(s,indx)
        fprintf('Skipping series #%2.2d\n', s);
        continue;
    end
    seriesNumber=s;
    fprintf('Reading series #%2.2d', s);
    r.setSeries(s - 1);
    pixelType = r.getPixelType();
    bpp = javaMethod('getBytesPerPixel', 'loci.formats.FormatTools', ...
                     pixelType);
    bppMax = power(2, bpp * 8);
    numImages = r.getImageCount();
    colorMaps = cell(numImages);
    for i = 1:numImages
        if mod(i, 72) == 1
            fprintf('\n    ');
        end
        fprintf('.');
        if mod(i,32)==0
           fprintf(', %2.0f percent\n',100*i/(numImages*numSeries));
        end
        arr = bfGetPlane(r, i, varargin{:});
        [SizeX,SizeY] = size(arr);
        SizeZ = r.getSizeZ();
        SizeC = r.getSizeC();
        SizeT = r.getSizeT();
        TotalSize = [SizeX,SizeY,SizeZ,SizeC,SizeT];
        if i==1
           fprintf('Series %d, datatype: %s. Casted to uint16.\n',s,class(arr));
        end
        if i==1 && didReadFirst==0 % first plane read
            ResData = zeros([SizeX,SizeY,SizeZ,SizeC,SizeT],'uint16');  % class(arr)
            LabelList = cell(1,SizeC);
            seriesMetadata = r.getSeriesMetadata();
            javaMethod('merge', 'loci.formats.MetadataTools', ...
               globalMetadata, seriesMetadata, 'Global ');
            didReadFirst=1;
            % reshape(ResData,TotalSize);
            OMEMetaData = r.getMetadataStore();            
        end
        if ~equalsizes(size(ResData),TotalSize)
            if i == 1
                fprintf('Problem reading data. Size Variation in series! Slice %d plane %d. Ignoring...\n',s,i);   
            end
            break;  % finishes this for loop and continues with the next series
        end

        % retrieve color map data
        if bpp == 1
            colorMaps{s, i} = r.get8BitLookupTable()';
        else
            colorMaps{s, i} = r.get16BitLookupTable()';
        end

        warning_state = warning ('off');
        if ~isempty(colorMaps{s, i})
            newMap = single(colorMaps{s, i});
            newMap(newMap < 0) = newMap(newMap < 0) + bppMax;
            colorMaps{s, i} = newMap / (bppMax - 1);
        end
        warning (warning_state);


        % build an informative title for our figure
        label = id;
        if numSeries > 1
            seriesName = char(r.getMetadataStore().getImageName(s - 1));
            if ~isempty(seriesName)
%                label = [label, '; ', seriesName];
                label = seriesName;
            else
                qs = int2str(s);
                label = [label, '; series ', qs, '/', int2str(numSeries)];
            end
        end
        if numImages > 1
            qi = int2str(i);
%            label = [label, '; plane ', qi, '/', int2str(numImages)];
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
%             if SizeZ > 1
%                 qz = int2str(zct(1) + 1);
%                 label = [label, '; ', lz, '=', qz, '/', int2str(SizeZ)];
%             end
%             if SizeC > 1  % label each color
%                 qc = int2str(zct(2) + 1);
%                 label = [label, '; ', lc, '=', qc, '/', int2str(SizeC)];
%             end
%             if SizeT > 1
%                 qt = int2str(zct(3) + 1);
%                 label = [label, '; ', lt, '=', qt, '/', int2str(SizeT)];
%             end
        end

        if isempty(LabelList{zct(2)+1})
            LabelList{zct(2)+1} = label;
        else
        end
        % save image plane and label into the list
        ResData(:,:,zct(1)+1,zct(2)+1,zct(3)+1) = arr;
%        imageList{i, 2} = label;
    end

    % save images and metadata into our master series list
 %   result{s, 1} = imageList;

    % extract metadata table for this series
%    result{s, 2} = seriesMetadata;
    AllColorMaps{s} = colorMaps;
    fprintf('\n');
end

pixelsize = [1,1,1,1,1];
dimensionUnits={'px','px','px','col','time'};
Objective = [];
try
    if size(ResData,1) > 1
        ps = OMEMetaData.getPixelsPhysicalSizeX(0);
        if ~isempty(ps)
            pixelsize(1) = double(ps.value);
            dimensionUnits{1}= char(ps.unit().getSymbol());
        end
    end
    if size(ResData,2) > 1
        ps = OMEMetaData.getPixelsPhysicalSizeY(0);
        if ~isempty(ps)
            pixelsize(2) = double(ps.value);
            dimensionUnits{2}=char(ps.unit().getSymbol());
        end
    end
    if size(ResData,3) > 1
        ps = OMEMetaData.getPixelsPhysicalSizeZ(0);
        if ~isempty(ps)
            pixelsize(3) = double(ps.value);
            dimensionUnits{13}=char(ps.unit().getSymbol());
        end
    end
    % dimensionUnits={'µm','µm','µm','col','time'};
catch exception
    fprintf('Problem with detecting Pixelsize: %s\n',exception.message)
end
astruct = struct('dimensions',[pixelsize(:)],'dimensionUnits',{dimensionUnits});
FileInfo=struct('physDims',astruct);
% OMEMetaData.getLightSourceType(0,0)
% OMEMetaData.getLaserWavelength(0,7)

r.close();
