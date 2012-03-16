function r = bfGetReader(id,varargin)
% Get the reader associated with a given dataset using Bio-Formats
% 
% SYNOPSIS r=bfGetReader(path)
%
% Input 
%
%    id - the path of a proprietary file
%
%    stichFiles - Optional. Toggle the grouping of similarly
%    named files into a single dataset based on file numbering.
%    Default: false;
%
% Output
%
%    r - object of class loci.formats.ChannelSeparator
%
% Adapted from bfopen.m

% Input check
ip=inputParser;
ip.addRequired('id',@ischar);
ip.addOptional('stitchFiles',false,@isscalar);
ip.parse(id,varargin{:});

% initialize logging
loci.common.DebugTools.enableLogging('INFO');

r = loci.formats.ChannelFiller();
r = loci.formats.ChannelSeparator(r);
if ip.Results.stitchFiles
    r = loci.formats.FileStitcher(r);
end

r.setMetadataStore(loci.formats.MetadataTools.createOMEXMLMetadata());
r.setId(id);