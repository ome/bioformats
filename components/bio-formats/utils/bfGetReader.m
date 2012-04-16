function r = bfGetReader(id,varargin)
% bfGetReader get a reader for a microscopy image using Bio-Formats
% 
% SYNOPSIS r=bfGetReader(path)
%
% Input 
%
%    id - the path to the microscopy image
%
%    stichFiles - Optional. Toggle the grouping of similarly
%    named files into a single dataset based on file numbering.
%    Default: false;
%
% Output
%
%    r - a reader object of class extending loci.formats.ReaderWrapper
%
% Adapted from bfopen.m

% Input check
ip=inputParser;
ip.addRequired('id',@ischar);
ip.addOptional('stitchFiles',false,@isscalar);
ip.parse(id,varargin{:});

% set LuraWave license code, if available
if exist('lurawaveLicense')
    path = fullfile(fileparts(mfilename('fullpath')), 'lwf_jsdk2.6.jar');
    javaaddpath(path);
    java.lang.System.setProperty('lurawave.license', lurawaveLicense);
end

% Check Bio-Formats is in the Java path
status = bfCheckJavaPath();
assert(status,['Missing Bio-Formats library. Either add loci_tools.jar '...
    'to the static Java path or add it to the Matlab path.']);

r = loci.formats.ChannelFiller();
r = loci.formats.ChannelSeparator(r);
if ip.Results.stitchFiles
    r = loci.formats.FileStitcher(r);
end

r.setMetadataStore(loci.formats.MetadataTools.createOMEXMLMetadata());
r.setId(id);