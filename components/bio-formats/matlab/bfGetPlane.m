function I = bfGetPlane(r, iPlane, varargin)
% Get the plane data from a dataset reader using bioformats tools
% 
% SYNOPSIS I = bfGetPlane(r, iPlane)
%
% Input 
%    r - the reader object (e.g. the output bfGetReader)
%
%    iPlane - a scalar giving the index of the plane to be retrieved.
%
%    x - (Optional) A scalar giving the x-coordinate of the tile origin.
%    Default: 1
%
%    y - (Optional) A scalar giving the y-coordinate of the tile origin.
%    Default: 1
%
%    w - (Optional) A scalar giving the width of the tile. 
%    Default: r.getSizeX()
%
%    h - (Optional) A scalar giving the height of the tile.
%    Default: r.getSizeY()
%
% Output
%
%    I - an array of size (width x height) containing the plane
%
% See also bfGetReader

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2013 Open Microscopy Environment:
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

% Input check
ip = inputParser;
ip.addRequired('r', @(x) isa(x, 'loci.formats.IFormatReader'));
ip.addRequired('iPlane', @isscalar);

% Define optional arguments to retrieve tile.
% No check is perform on this argument as they should already be checked in
% openBytes()
ip.addOptional('x', 1, @isscalar);
ip.addOptional('y', 1, @isscalar);
ip.addOptional('width', r.getSizeX(), @isscalar);
ip.addOptional('height', r.getSizeY(), @isscalar);
ip.parse(r, iPlane, varargin{:});

% check MATLAB version, since typecast function requires MATLAB 7.1+
canTypecast = versionCheck(version, 7, 1);

% Get pixel type
pixelType = r.getPixelType();
bpp = loci.formats.FormatTools.getBytesPerPixel(pixelType);
fp = loci.formats.FormatTools.isFloatingPoint(pixelType);
sgn = loci.formats.FormatTools.isSigned(pixelType);
bppMax = power(2, bpp * 8);
little = r.isLittleEndian();

plane = r.openBytes(iPlane - 1, ip.Results.x - 1, ip.Results.y - 1, ...
    ip.Results.width, ip.Results.height);
    
% convert byte array to MATLAB image
if sgn || ~canTypecast
    % can get the data directly to a matrix
    I = loci.common.DataTools.makeDataArray2D(plane, ...
        bpp, fp, little, ip.Results.height);
else
    % get the data as a vector, either because makeDataArray2D
    % is not available, or we need a vector for typecast
    I = loci.common.DataTools.makeDataArray(plane, ...
        bpp, fp, little);
end

% Java does not have explicitly unsigned data types;
% hence, we must inform MATLAB when the data is unsigned
if ~sgn
    if canTypecast
        % TYPECAST requires at least MATLAB 7.1
        % NB: arr will always be a vector here
        switch class(I)
            case 'int8'
                I = typecast(I, 'uint8');
            case 'int16'
                I = typecast(I, 'uint16');
            case 'int32'
                I = typecast(I, 'uint32');
            case 'int64'
                I = typecast(I, 'uint64');
        end
    else
        % adjust apparent negative values to actual positive ones
        % NB: arr might be either a vector or a matrix here
        mask = I < 0;
        adjusted = I(mask) + bppMax / 2;
        switch class(I)
            case 'int8'
                I = uint8(I);
                adjusted = uint8(adjusted);
            case 'int16'
                I = uint16(I);
                adjusted = uint16(adjusted);
            case 'int32'
                I = uint32(I);
                adjusted = uint32(adjusted);
            case 'int64'
                I = uint64(I);
                adjusted = uint64(adjusted);
        end
        adjusted = adjusted + bppMax / 2;
        I(mask) = adjusted;
    end
end

if isvector(I)
    % convert results from vector to matrix
    shape = [ip.Results.width ip.Results.height];
    I = reshape(I, shape)';
end


function [result] = versionCheck(v, maj, min)

tokens = regexp(v, '[^\d]*(\d+)[^\d]+(\d+).*', 'tokens');
majToken = tokens{1}(1);
minToken = tokens{1}(2);
major = str2double(majToken{1});
minor = str2double(minToken{1});
result = major > maj || (major == maj && minor >= min);