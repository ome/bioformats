function bfsave(I, outputPath, varargin)
% Save a 5D matrix into an OME-TIFF using Bio-Formats library
%
% SYNOPSIS bfsave(I, outputPath)
%          bfsave(I, outputPath, dimensionsOrder)
%
% Input:
%       I - a 5D stack containing the pixels data
%
%       outputPath - a string containing the location of the path where to
%       save the resulting OME-TIFF
%
%       dimensionOrder - optional. A string representing the dimension order
%       of type XYxxx where xxx are CZT in any order. Default XYZCT.
% Output
% 

% Not using the inputParser for first argument as it copies data
assert(isnumeric(I), 'First argument must be numeri');

% Input check
ip = inputParser;
ip.addRequired('outputPath', @ischar);
dimensionOrderValidator = @(x) strcmp(sort(x), 'CTXYZ') && strcmp(x(1:2), 'XY');
ip.addOptional('dimensionOrder', 'XYZCT', dimensionOrderValidator)
ip.parse(outputPath, varargin{:});

% Check loci=tools jar is in the Java path
bfCheckJavaPath();

% Read dimnensions from array
sizeX = size(I, 1);
sizeY = size(I, 2);
sizeZ = size(I, find(ip.Results.dimensionOrder == 'Z'));
sizeC = size(I, find(ip.Results.dimensionOrder == 'C'));
sizeT = size(I, find(ip.Results.dimensionOrder == 'T'));

% Create metadata
toInt = @(x) ome.xml.model.primitives.PositiveInteger(java.lang.Integer(x));
metadata = loci.formats.MetadataTools.createOMEXMLMetadata();
metadata.createRoot();
metadata.setImageID('Image:0', 0);
metadata.setPixelsID('Pixels:0', 0);
metadata.setPixelsBinDataBigEndian(java.lang.Boolean.TRUE, 0, 0);
d = ome.xml.model.enums.DimensionOrder.(ip.Results.dimensionOrder);
metadata.setPixelsDimensionOrder(d, 0);
metadata.setPixelsType(ome.xml.model.enums.PixelType.UINT8, 0);
metadata.setPixelsSizeX(toInt(sizeX), 0);
metadata.setPixelsSizeY(toInt(sizeY), 0);
metadata.setPixelsSizeZ(toInt(sizeZ), 0);
metadata.setPixelsSizeC(toInt(sizeC), 0);
metadata.setPixelsSizeT(toInt(sizeT), 0);
for i = 1: sizeC
    metadata.setChannelID(['Channel:0:' num2str(i-1)], 0, i-1);
    metadata.setChannelSamplesPerPixel(toInt(1), 0, i-1);
end

% Create ImageWriter
writer = loci.formats.ImageWriter();
writer.setMetadataRetrieve(metadata);
writer.setId(outputPath);

% Save planes to the writer
nPlanes = sizeZ * sizeC * sizeT;
for index = 1 : nPlanes 
    [i, j, k] = ind2sub([size(I, 3) size(I, 4) size(I, 5)],index);
    plane = I(:, :, i, j, k)';
    writer.saveBytes(index-1, plane(:));
end
writer.close();

end