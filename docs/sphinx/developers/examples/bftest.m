bfCheckJavaPath();

% Create local file for testing
java_tmpdir = char(java.lang.System.getProperty('java.io.tmpdir'));
uuid = char(java.util.UUID.randomUUID());
tmpdir = fullfile(java_tmpdir, uuid);
mkdir(tmpdir);

% Create fake file for testing purposes
pathToFile = fullfile(tmpdir, 'test.fake');
pathToIniFile = fullfile(tmpdir, 'test.fake.ini');
fid = fopen(pathToFile, 'w');
fwrite(fid, '');
fclose(fid);
fid = fopen(pathToIniFile, 'w');
fwrite(fid, sprintf('physicalSizeX=.1\n'));
fwrite(fid, sprintf('physicalSizeY=.1\n'));
fwrite(fid, sprintf('physicalSizeZ=.2\n'));
fwrite(fid, sprintf('series=3\n'));
fwrite(fid, sprintf('sizeT=3\n'));
fclose(fid);

% bfopen-start
data = bfopen(pathToFile);
% bfopen-end

% Reading Images
% accessing-planes-start
seriesCount = size(data, 1);
series1 = data{1, 1};
series2 = data{2, 1};
series3 = data{3, 1};
metadataList = data{1, 2};
% etc
series1_planeCount = size(series1, 1);
series1_plane1 = series1{1, 1};
series1_label1 = series1{1, 2};
series1_plane2 = series1{2, 1};
series1_label2 = series1{2, 2};
series1_plane3 = series1{3, 1};
series1_label3 = series1{3, 2};
% accessing-planes-end

%Displaying images
% displaying-images-start
series1_colorMap = data{1, 3}{1, 1};
figure('Name', series1_label1);
if isempty(series1_colorMap)
  colormap(gray);
else
  colormap(series1_colorMap);
end
imagesc(series1_plane1);
% displaying-images-end

% animated-movie-start
v = linspace(0, 1, 256)';
cmap = [v v v];
for p = 1 : size(series1, 1)
  M(p) = im2frame(uint8(series1{p, 1}), cmap);
end
if feature('ShowFigureWindows')
  movie(M);
end
% animated-movie-end


% read-original-metadata-by-key-start
% Query some metadata fields (keys are format-dependent)
metadata = data{1, 2};
subject = metadata.get('Subject');
title = metadata.get('Title');
% read-original-metadata-by-key-end

% read-original-metadata-start
metadataKeys = metadata.keySet().iterator();
for i=1:metadata.size()
  key = metadataKeys.nextElement();
  value = metadata.get(key);
  fprintf('%s = %s\n', key, value)
end
% read-original-metadata-end

% read-ome-metadata-start
omeMeta = data{1, 4};
stackSizeX = omeMeta.getPixelsSizeX(0).getValue(); % image width, pixels
stackSizeY = omeMeta.getPixelsSizeY(0).getValue(); % image height, pixels
stackSizeZ = omeMeta.getPixelsSizeZ(0).getValue(); % number of Z slices

voxelSizeXdefaultValue = omeMeta.getPixelsPhysicalSizeX(0).value();           % returns value in default unit
voxelSizeXdefaultUnit = omeMeta.getPixelsPhysicalSizeX(0).unit().getSymbol(); % returns the default unit type
voxelSizeX = omeMeta.getPixelsPhysicalSizeX(0).value(ome.units.UNITS.MICROM); % in µm
voxelSizeXdouble = voxelSizeX.doubleValue();                                  % The numeric value represented by this object after conversion to type double
voxelSizeY = omeMeta.getPixelsPhysicalSizeY(0).value(ome.units.UNITS.MICROM); % in µm
voxelSizeYdouble = voxelSizeY.doubleValue();                                  % The numeric value represented by this object after conversion to type double
voxelSizeZ = omeMeta.getPixelsPhysicalSizeZ(0).value(ome.units.UNITS.MICROM); % in µm
voxelSizeZdouble = voxelSizeZ.doubleValue();                                  % The numeric value represented by this object after conversion to type double
% read-ome-metadata-end

omeXML = char(omeMeta.dumpXML());

% bfsave-plane-start
plane = zeros(64, 64, 'uint8');
bfsave(plane, 'single-plane.ome.tiff');
% bfsave-plane-end
delete('single-plane.ome.tiff');

% bfsave-multiple-planes-start
plane = zeros(64, 64, 1, 2, 2, 'uint8');
bfsave(plane, 'multiple-planes.ome.tiff');
% bfsave-multiple-planes-end
delete('multiple-planes.ome.tiff');

% bfsave-metadata-start
plane = zeros(64, 64, 1, 2, 2, 'uint8');
metadata = createMinimalOMEXMLMetadata(plane);
pixelSize = ome.units.quantity.Length(java.lang.Double(.05), ome.units.UNITS.MICROM);
metadata.setPixelsPhysicalSizeX(pixelSize, 0);
metadata.setPixelsPhysicalSizeY(pixelSize, 0);
pixelSizeZ = ome.units.quantity.Length(java.lang.Double(.2), ome.units.UNITS.MICROM);
metadata.setPixelsPhysicalSizeZ(pixelSizeZ, 0);
bfsave(plane, 'metadata.ome.tiff', 'metadata', metadata);
% bfsave-metadata-end
delete('metadata.ome.tiff');

% memoizer-start
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
% memoizer-end

% memoizer-parfor-start
% Construct a Bio-Formats reader decorated with the Memoizer wrapper
r = loci.formats.Memoizer(bfGetReader(), 0);
% Initialize the reader with an input file to cache the reader
r.setId(pathToFile);
% Close reader
r.close()

nWorkers = 4;

% Enter parallel loop
parfor i = 1 : nWorkers
    % Initialize a new reader per worker as Bio-Formats is not thread safe
    r2 = javaObject('loci.formats.Memoizer', bfGetReader(), 0);
    % Initialization should use the memo file cached before entering the
    % parallel loop
    r2.setId(pathToFile);

    % Perform work

    % Close the reader
    r2.close()
end
% memoizer-parfor-end

% Clean directory
rmdir(tmpdir, 's');
