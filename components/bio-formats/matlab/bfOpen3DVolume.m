function volume = bfOpen3DVolume(filename)
% bfOpen3DVolume loads a stack of images using Bio-Formats and transforms them
% into a 3D volume
%
% SYNPOSIS  bfOpen3DVolume
%           V = bfOpen3DVolume(filename)
%
% Input
%
%   filename - Optional.  A path to the file to be opened.  If not specified,
%   then a file chooser window will appear.
%
% Output
%
%   volume - 3D array containing all images in the file.

% load the Bio-Formats library into the MATLAB environment
status = bfCheckJavaPath();
assert(status, ['Missing Bio-Formats library. Either add loci_tools.jar '...
    'to the static Java path or add it to the Matlab path.']);

% Prompt for a file if not input
if nargin == 0 || exist(filename, 'file') == 0
  [file, path] = uigetfile(bfGetFileExtensions, 'Choose a file to open');
  filename = [path file];
  if isequal(path, 0) || isequal(file, 0), return; end
end

volume = bfopen(filename);
vaux{1} = cat(3, volume{1}{:, 1});
vaux{2} = filename;
volume{1} = vaux;
end