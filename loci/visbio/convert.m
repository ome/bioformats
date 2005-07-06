function [result] = convert(pix, params)
%convert applies a multiplier and an offset to the image planes
%
%  pix    - a 3D array of pixels dimensioned (Y, X, N)
%  params - multiplier, offset

if size(pix, 1) == 0
  if size(params, 1) == 0
    % return parameter list with default values
    result{1} = {'Multiplier', 1.0};
    result{2} = {'Offset', 0.0};
  else
    % input dimensions must be prepended to parameter list
    leny = params(1);
    lenx = params(2);
    num = params(3);

    % mult and offset do not affect output dimensions,
    % but if they did, it would be permitted
    mult = params(4);
    offset = params(5);

    % return output image dimensions for the given
    % input dimensions and parameter values
    result = [leny lenx num];
  end
else
  % assign parameters
  mult = params(1);
  offset = params(2);

  % process pixels
  leny = size(pix, 1);
  lenx = size(pix, 2);
  num = size(pix, 3);
  result(:,:,:) = mult * pix(:,:,:) + offset;
end
