function r = bfGetReader(varargin)
% BFGETREADER return a reader for a microscopy image using Bio-Formats
% 
% SYNOPSIS  r = bfGetReader()
%           r = bfGetReader(path)
%
% Input 
%
%    id - (Optional - string) A valid path to the microscopy image
%
%    stichFiles (Optional - scalar). Toggle the grouping of similarly
%    named files into a single dataset based on file numbering.
%    Default: false;
%
% Output
%
%    r - A reader object of class extending loci.formats.ReaderWrapper
%
% Adapted from bfopen.m

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2015 Open Microscopy Environment:
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
ip.addOptional('id', '', @ischar);
ip.addOptional('stitchFiles', false, @isscalar);
ip.parse(varargin{:});
id = ip.Results.id;

% verify that enough memory is allocated
bfCheckJavaMemory();

% load the Bio-Formats library into the MATLAB environment
status = bfCheckJavaPath();
assert(status, ['Missing Bio-Formats library. Either add bioformats_package.jar '...
    'to the static Java path or add it to the Matlab path.']);

% Check if input is a fake string
isFake = strcmp(id(max(1, end - 4):end), '.fake');

if ~isFake
    % Check file existence using fileattrib
    [status, f] = fileattrib(id);
    isFile = status && f.directory == 0;
    if isFile, id = f.Name; end
end

% Prompt for a file via the UI
if (~isFake && ~isFile)
    [file, path] = uigetfile(bfGetFileExtensions, 'Choose a file to open');
    id = [path file];
    if isequal(path, 0) || isequal(file, 0), return; end
end

% set LuraWave license code, if available
if exist('lurawaveLicense', 'var')
    path = fullfile(fileparts(mfilename('fullpath')), 'lwf_jsdk2.6.jar');
    javaaddpath(path);
    java.lang.System.setProperty('lurawave.license', lurawaveLicense);
end

r = loci.formats.ChannelFiller();
r = loci.formats.ChannelSeparator(r);
if ip.Results.stitchFiles
    r = loci.formats.FileStitcher(r);
end

OMEXMLService = loci.formats.services.OMEXMLServiceImpl();
r.setMetadataStore(OMEXMLService.createOMEXMLMetadata());
r.setId(id);
