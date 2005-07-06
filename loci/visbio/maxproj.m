function [result] = maxproj(pix, params)
%maxproj computes a maximum intensity projection of the input planes
%
%  pix    - a 3D array of pixels dimensioned (Y, X, N)
%  params - not used

if size(pix, 1) == 0
  if size(params, 1) == 0
    % return parameter list with default values
    % (no parameters for maxproj)
    result = {};
  else
    % input dimensions must be prepended to parameter list
    leny = params(1);
    lenx = params(2);
    num = params(3);

    % return output image dimensions for the given
    % input dimensions and parameter values
    result = [leny lenx 1];
  end
else
  % process pixels
  leny = size(pix, 1);
  lenx = size(pix, 2);
  num = size(pix, 3);
  for x = 1:lenx
    for y = 1:leny
      result(y,x,1) = max(pix(y,x,:));
    end
  end
end
