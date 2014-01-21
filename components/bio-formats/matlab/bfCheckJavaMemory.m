function [] = bfCheckJavaMemory()
% bfCheckJavaMemory warn if too little memory is allocated to Java
% 
% SYNOPSIS  bfCheckJavaMemory()
%
% Input 
%
%    n/a
%
% Output
%
%    A warning message is printed if too little memory is allocated.

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2014 Open Microscopy Environment:
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

runtime = java.lang.Runtime.getRuntime();
maxMemory = runtime.maxMemory() / (1024 * 1024);

if maxMemory < 512
  fprintf('*** Insufficient memory detected. ***\n');
  fprintf('*** %dm found ***\n', maxMemory);
  fprintf('*** 512m or greater is recommended ***\n');
  fprintf('*** See http://www.mathworks.com/matlabcentral/answers/92813 ***\n');
  fprintf('*** for instructions on increasing memory allocation. ***\n');
end
