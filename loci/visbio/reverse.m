function [result] = reverse(pix, params)
%reverse is a function that reverses the order of the input planes
%
%  pix    - a 3D array of pixels dimensioned (Y, X, N)
%  params - not used
%
%For example, an RGB image would be passed in with R at (:,:,1), G at (:,:,2)
%and B at (:,:,3). The reverse function returns a 3D array with the same
%dimensions, but ordered BGR -- B at (:,:,1), G at (:,:,2) and R at (:,:,3).

if size(pix, 1) == 0
  if size(params, 1) == 0
    % return parameter list with default values
    % (no parameters for reverse)
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
  % process pixels
  leny = size(pix, 1);
  lenx = size(pix, 2);
  num = size(pix, 3);
  for n = 1:num
    result(:,:,num-n+1) = pix(:,:,n);
  end
end
