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

    volume = bfopen(filename);
    vaux{1} = zeros([size(volume{1}, 1), size(volume{1}{1})]);
    for k = 1:size(vaux{1}, 1)
        vaux{1}(:,:,k) = volume{1}{k};
    end
    vaux{2} = filename;
    volume{1} = vaux;
end
