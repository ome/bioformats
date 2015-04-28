clear all;close all;
pathToFile = '/Users/bramalingam/Downloads/data_repo_good/leica-lif/Beta Catenin.lif';
%Reading Images
data = bfopen(pathToFile);
seriesCount = size(data, 1);
series1 = data{1, 1};
series1_planeCount = size(series1, 1);

%Displaying images
series1_plane1 = series1{1, 1};
series1_label1 = series1{1, 2};
series1_colorMaps = data{1, 3};
figure('Name', series1_label1);
if (isempty(series1_colorMaps{1}))
  colormap(gray);
else
  colormap(series1_colorMaps{1}(1,:));
end
imagesc(series1_plane1);

v = linspace(0, 1, 256)';
cmap = [v v v];
for p = 1 : size(series1, 1)
  M(p) = im2frame(uint8(series1{p, 1}), cmap);
end
movie(M);

% Query some metadata fields (keys are format-dependent)
metadata = data{1, 2};
subject = metadata.get('Subject');
title = metadata.get('Title');

metadataKeys = metadata.keySet().iterator();
for i=1:metadata.size()
  key = metadataKeys.nextElement();
  value = metadata.get(key);
  fprintf('%s = %s\n', key, value)
end

omeMeta = data{1, 4};
stackSizeX = omeMeta.getPixelsSizeX(0).getValue(); % image width, pixels
stackSizeY = omeMeta.getPixelsSizeY(0).getValue(); % image height, pixels
stackSizeZ = omeMeta.getPixelsSizeZ(0).getValue(); % number of Z slices

voxelSizeXdefaultValue = omeMeta.getPixelsPhysicalSizeX(0).value();           % returns value in default unit
voxelSizeXdefaultUnit = omeMeta.getPixelsPhysicalSizeX(0).unit().getSymbol(); % returns the default unit type
voxelSizeX = omeMeta.getPixelsPhysicalSizeX(0).value(ome.units.UNITS.MICROM); % in µm
voxelSizeY = omeMeta.getPixelsPhysicalSizeY(0).value(ome.units.UNITS.MICROM); % in µm
voxelSizeZ = omeMeta.getPixelsPhysicalSizeZ(0).value(ome.units.UNITS.MICROM); % in µm

omeXML = char(omeMeta.dumpXML());

plane = zeros(64, 64, 'uint8');
bfsave(plane, 'my-file.ome.tiff');

plane = zeros(64, 64, 1, 2, 2, 'uint8');
bfsave(plane, 'my-file.ome.tiff');

plane = zeros(64, 64, 1, 2, 2, 'uint8');
metadata = createMinimalOMEXMLMetadata(plane);
pixelSize = ome.xml.model.primitives.PositiveFloat(java.lang.Double(.05));
metadata.setPixelsPhysicalSizeX(pixelSize, 0);
metadata.setPixelsPhysicalSizeY(pixelSize, 0);
pixelSizeZ = ome.xml.model.primitives.PositiveFloat(java.lang.Double(.2));
metadata.setPixelsPhysicalSizeZ(pixelSizeZ, 0);
bfsave(plane, 'my-file.ome.tiff', 'metadata', metadata);

% Construct an empty Bio-Formats reader
r = bfGetReader();
% Decorate the reader with the Memoizer wrapper
r = loci.formats.Memoizer(r);
% Initialize the reader with an input file
% If the call is longer than a minimal time, the initialized reader will
% be cached in a file under the same directory as the initial file
% name .large_file.bfmemo
r.setId(pathToFile);

% Perform work using the reader

% Close the reader
r.close()

% If the reader has been cached in the call above, re-initializing the
% reader will use the memo file and complete much faster especially for
% large data
r.setId(pathToFile);

% Perform additional work

% Close the reader
r.close()

% Construct a Bio-Formats reader decorated with the Memoizer wrapper
r = loci.formats.Memoizer(bfGetReader(), 0);
% Initialize the reader with an input file to cache the reader
r.setId(pathToFile);
% Close reader
r.close()

nWorkers = 4;

% Enter parallel loop
for i = 1 : nWorkers
    % Initialize a new reader per worker as Bio-Formats is not thread safe
    r2 = loci.formats.Memoizer(bfGetReader(), 0)
    % Initialization should use the memo file cached before entering the
    % parallel loop
    r2.setId(pathToFile);

    % Perform work

    % Close the reader
    r2.close()
end