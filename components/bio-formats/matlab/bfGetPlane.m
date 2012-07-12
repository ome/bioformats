function I = bfGetPlane(r, iPlane)
% Get the plane data from a dataset reader using bioformats tools
% 
% SYNOPSIS I = bfGetPlane(r, iPlane)
%
% Input 
%    r - the reader object (e.g. the output bfGetReader)
%
%    iPlane - a scalar giving the index of the plane to be retrieved.
%
% Output
%
%    I - an array of size (width x height) containing the plane
%
% See also bfGetReader

% Input check
ip = inputParser;
ip.addRequired('r', @(x) isa(x, 'loci.formats.ReaderWrapper'));
ip.addRequired('iPlane', @isscalar);
ip.parse(r, iPlane);

% check MATLAB version, since typecast function requires MATLAB 7.1+
canTypecast = versionCheck(version, 7, 1);
bioFormatsVersion = char(loci.formats.FormatTools.VERSION);
isBioFormatsTrunk = versionCheck(bioFormatsVersion, 5, 0);

width = r.getSizeX();
height = r.getSizeY();
pixelType = r.getPixelType();
bpp = loci.formats.FormatTools.getBytesPerPixel(pixelType);
fp = loci.formats.FormatTools.isFloatingPoint(pixelType);
sgn = loci.formats.FormatTools.isSigned(pixelType);
bppMax = power(2, bpp * 8);
little = r.isLittleEndian();

plane = r.openBytes(iPlane - 1);
    
% convert byte array to MATLAB image
if isBioFormatsTrunk && (sgn || ~canTypecast)
    % can get the data directly to a matrix
    arr = loci.common.DataTools.makeDataArray2D(plane, ...
        bpp, fp, little, height);
else
    % get the data as a vector, either because makeDataArray2D
    % is not available, or we need a vector for typecast
    arr = loci.common.DataTools.makeDataArray(plane, ...
        bpp, fp, little);
end
%     if ~strcmp(class(I),class(arr)), I= cast(I,['u' class(arr)]); end

% Java does not have explicitly unsigned data types;
% hence, we must inform MATLAB when the data is unsigned
if ~sgn
    if canTypecast
        % TYPECAST requires at least MATLAB 7.1
        % NB: arr will always be a vector here
        switch class(arr)
            case 'int8'
                arr = typecast(arr, 'uint8');
            case 'int16'
                arr = typecast(arr, 'uint16');
            case 'int32'
                arr = typecast(arr, 'uint32');
            case 'int64'
                arr = typecast(arr, 'uint64');
        end
    else
        % adjust apparent negative values to actual positive ones
        % NB: arr might be either a vector or a matrix here
        mask = arr < 0;
        adjusted = arr(mask) + bppMax / 2;
        switch class(arr)
            case 'int8'
                arr = uint8(arr);
                adjusted = uint8(adjusted);
            case 'int16'
                arr = uint16(arr);
                adjusted = uint16(adjusted);
            case 'int32'
                arr = uint32(arr);
                adjusted = uint32(adjusted);
            case 'int64'
                arr = uint64(arr);
                adjusted = uint64(adjusted);
        end
        adjusted = adjusted + bppMax / 2;
        arr(mask) = adjusted;
    end
end

if isvector(arr)
    % convert results from vector to matrix
    shape = [width height];
    I = reshape(arr, shape)';
end


function [result] = versionCheck(v, maj, min)

tokens = regexp(v, '[^\d]*(\d+)[^\d]+(\d+).*', 'tokens');
majToken = tokens{1}(1);
minToken = tokens{1}(2);
major = str2double(majToken{1});
minor = str2double(minToken{1});
result = major > maj || (major == maj && minor >= min);