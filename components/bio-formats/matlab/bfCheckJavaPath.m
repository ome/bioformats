function status = bfCheckJavaPath(varargin)
% bfCheckJavaPath check Bio-Formats is included in the Java class path
% 
% SYNOPSIS  bfCheckJavaPath
%           status = bfCheckJavaPath(autoloadBioFormats)
%
% Input 
%
%    autoloadBioFormats - Optional. A boolean specifying the action to take
%    if loci_tools is not in the java path. If true, add loci_tools to the
%    dynamic java path. Default - true
%
% Output
%
%    status - Boolean. True if loci_tools is part of the Java library.

% Input check
ip = inputParser;
ip.addOptional('autoloadBioFormats', true, @isscalar);
ip.parse(varargin{:});

% Check if loci_tools is in the Java class path (static or dynamic)
jPath = javaclasspath('-all');
isLociTools = cellfun(@(x) ~isempty(regexp(x, '.*loci_tools.jar$', 'once')),...
    jPath);
status = any(isLociTools);

% Return if loci_tools is in the java path or autoloadBioFormats is off
if status || ~ip.Results.autoloadBioFormats, return; end   

% Assume the jar is in Matlab path or under the same folder as this file
path = which('loci_tools.jar');
if isempty(path)
    path = fullfile(fileparts(mfilename('fullpath')), 'loci_tools.jar');
end
assert(exist(path, 'file') == 2, 'Cannot automatically locate loci_tools.jar');

% Add loci_tools to dynamic Java class path
javaaddpath(path);    
status = true;