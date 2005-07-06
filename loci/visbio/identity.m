function [result] = identity(pix, params)
%identity returns the input pixels with no changes, for testing
%
%  pix    - a 3D array of pixels dimensioned (Y, X, N)
%  params - not used

if size(pix, 1) == 0
  if size(params, 1) == 0
    % return parameter list with default values
    % (no parameters for identity)
    result = {};
  else
    % input dimensions must be prepended to parameter list
    leny = params(1);
    lenx = params(2);
    num = params(3);

    % return output image dimensions for the given
    % input dimensions and parameter values
    result = [leny lenx num];
  end
else
  % process pixels (or not, in this case)
  result = pix;
end
