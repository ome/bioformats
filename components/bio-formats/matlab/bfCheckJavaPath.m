function [status, version] = bfCheckJavaPath(varargin)
% bfCheckJavaPath check Bio-Formats is included in the Java class path
% 
% SYNOPSIS  bfCheckJavaPath()
%           status = bfCheckJavaPath(autoloadBioFormats)
%           [status, version] = bfCheckJavaPath()
%
% Input 
%
%    autoloadBioFormats - Optional. A boolean specifying the action to take
%    if loci_tools is not in the Java class path. If true, add loci_tools 
%    to the dynamic Java path. Default - true
%
% Output
%
%    status - Boolean. True if loci_tools.jar is in the Java class path.
%
%    version - String specifying the current version of Bio-Formats if 
%    loci_tools.jar is in the Java class path. Empty string else.

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
ip.addOptional('autoloadBioFormats', true, @isscalar);
ip.parse(varargin{:});

% Check if loci_tools is in the Java class path (static or dynamic)
jPath = javaclasspath('-all');
isLociTools = cellfun(@(x) ~isempty(regexp(x, '.*loci_tools.jar$', 'once')),...
    jPath);
status = any(isLociTools);

if ~status && ip.Results.autoloadBioFormats,
    % Assume the jar is in Matlab path or under the same folder as this file
    path = which('loci_tools.jar');
    if isempty(path)
        path = fullfile(fileparts(mfilename('fullpath')), 'loci_tools.jar');
    end
    assert(exist(path, 'file') == 2, 'Cannot automatically locate loci_tools.jar');
    
    % Add loci_tools to dynamic Java class path
    javaaddpath(path);
    status = true;
end

if status
    % Read Bio-Formats version
    version = char(loci.formats.FormatTools.VERSION);
else
    version = '';
end