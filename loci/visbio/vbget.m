function [result] = vbget(func, field, params)
%VisBio calls vbget to evaluate the given field using the specified function.
%
%  func   - name of function to execute
%           (must take 3D array of image pixels as argument)
%
%  field  - VisAD FlatField from which to extract pixels for evaluation
%
%  params - vector (1D) with any extra parameters to pass to the function
pix = loci.visbio.ext.MatlabUtil.getImagePixels(field);
result = feval(func, pix, params);
