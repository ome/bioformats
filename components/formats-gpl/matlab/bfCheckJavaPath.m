function [status, version] = bfCheckJavaPath(varargin)
% bfCheckJavaPath check Bio-Formats is included in the Java class path
%
% SYNOPSIS  bfCheckJavaPath()
%           status = bfCheckJavaPath(autoloadBioFormats)
%           [status, version] = bfCheckJavaPath()
%
% Input
%
%    autoloadBioFormats - Optional. A boolean specifying the action
%    to take if Bio-Formats is not in the javaclasspath.  If true,
%    tries to find a Bio-Formats jar file and adds it to the java
%    class path.
%    Default - true
%
% Output
%
%    status - Boolean. True if the Bio-Formats classes are in the Java
%    class path.
%
%
%    version - String specifying the current version of Bio-Formats if
%    Bio-Formats is in the Java class path. Empty string otherwise.

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
ip.addOptional('autoloadBioFormats', true, @isscalar);
ip.parse(varargin{:});

[status, version] = has_working_bioformats();

if ~status && ip.Results.autoloadBioFormats,
    jarPaths = getJarPaths();
    % Try each of the found jar files until we find something that works
    for i = 1:numel(jarPaths)
      javaaddpath(jarPaths{i});
      [status, version] = has_working_bioformats();
      if (status)
          break
      else
          javarmpath(jarPaths{i});
      end
    end
end

% Return true if bioformats java interface is working, false otherwise.
% Not working will probably mean that the classes are not in
% the javaclasspath.
function [status, version] = has_working_bioformats()

status = true;
version = '';
try
    % If the following fails for any reason, then bioformats is not working.
    % Getting the version number and creating a reader is the bare minimum.
    reader = javaObject('loci.formats.in.FakeReader');
    if is_octave()
        version = char(java_get('loci.formats.FormatTools', 'VERSION'));
    else
        version = char(loci.formats.FormatTools.VERSION);
    end
catch
    status = false;
end

% Return cell array of possible jar files with bioformats, ordered
% by preference.
function paths = getJarPaths()

unversion_jar_regexp = '^(?:bioformats(_package)?|loci_tools).jar$';
version_jar_regexp = '^(?:bioformats(_package)?|loci_tools)(-[\d\.]+(-(SNAPSHOT|DEV))?).jar$';

% If there is a bioformats jar packaged with the toolbox
dirs = {fileparts(mfilename('fullpath'))};

if isunix()
    % XDG Base Directory Specification

    xdg_data_home = getenv('XDG_DATA_HOME');
    if isempty(xdg_data_home)
        xdg_data_home = fullfile(getenv('HOME'), '.local', 'share', 'java');
    else
        xdg_data_home = fullfile(xdg_data_home, 'java');
    end

    xdg_data_dirs = getenv('XDG_DATA_DIRS');
    if isempty(xdg_data_dirs)
        xdg_data_dirs = {'/usr/local/share/', '/usr/share/'};
    else
        xdg_data_dirs = strsplit(xdg_data_dirs, ':');
    end
    xdg_data_dirs(cellfun(@isempty, xdg_data_dirs)) = [];
    xdg_data_dirs = cellfun(@fullfile, xdg_data_dirs, ...
                            repmat({'java'}, size(xdg_data_dirs)), ...
                            'UniformOutput', false);

    dirs = {dirs{:}, xdg_data_home, xdg_data_dirs{:}};

elseif ismac()
    % Who knows where is OSX keeps jar files by default.

elseif ispc()
    % Who knows where Windows keeps jar files by default.

end

paths = {};
for i = 1 : numel(dirs)
    files = dir(dirs{i});
    fnames = {files.name};
    fpaths = cellfun(@fullfile, repmat(dirs(i), size(fnames)), fnames, ...
                     'UniformOutput', false);
    % Favour unversioned jar files (it is common to have multiple versions
    % installed and have a default by creating a symlink to it without the
    % version number) so add then first.
    matches = ~ cellfun(@isempty, regexp ({files.name}, unversion_jar_regexp));
    paths(end+1:end+nnz(matches)) = fpaths(matches);
    matches = ~ cellfun(@isempty, regexp ({files.name}, version_jar_regexp));
    paths(end+1:end+nnz(matches)) = fpaths(matches);
end
