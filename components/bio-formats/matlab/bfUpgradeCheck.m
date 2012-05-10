function bfUpgradeCheck(varargin)
% Check for new version of Bio-Formats and update it if applicable
% 
% SYNOPSIS: bfUpgradeCheck(autoDownload, 'STABLE')
%
% Input 
%    autoDownload - Optional. A boolean specifying of the latest version
%    should be downloaded
%
%    versions -  Optional: a string sepecifying the version to fetch.
%    Should be either trunk, daily or stable (case insensitive)
%
% Output
%    none

% Check input
ip = inputParser;
ip.addOptional('autoDownload', false, @isscalar);
versions = {'stable', 'daily', 'trunk'};
ip.addOptional('version', 'STABLE', @(x) any(strcmpi(x, versions)))
ip.parse(varargin{:})

% Create UpgradeChecker
upgrader = loci.formats.UpgradeChecker();
if upgrader.alreadyChecked(), return; end

% Check for new version of Bio-Formats
canUpgrade = upgrader.newVersionAvailable('MATLAB');
if ~canUpgrade,
    fprintf('*** loci_tools.jar is up-to-date ***\n');
    return;
end

fprintf('*** A new stable version of Bio-Formats is available ***\n');
% If appliable, download new version of Bioformats
if ip.Results.autoDownload
    fprintf('*** Downloading... ***');
    path = fullfile(fileparts(mfilename('fullpath')), 'loci_tools.jar');
    buildName = [upper(ip.Results.version) '_BUILD'];
    upgrader.install(loci.formats.UpgradeChecker.(buildName), path);
    fprintf('*** Upgrade will be finished when MATLAB is restarted ***\n');
end
