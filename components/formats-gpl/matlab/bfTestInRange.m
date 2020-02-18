function test = bfTestInRange(x,name,maxValue)
%bfTestInRange A validation function that tests if the argument
% is scalar integer between 1 and maxValue
%
% This should be faster than ismember(x, 1:maxValue) while also producing
% more readable errors.

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2017 Open Microscopy Environment:
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


    % Check to see if x is a single value
    test = isscalar(x);
    if(~test)
        error('bfTestInRange:notScalar', ...
            [name ' value, [' num2str(x) '], is not scalar']);
    end

    % Check to see if x is a whole number
    test = mod(x,1) == 0;
    if(~test)
        error('bfTestInRange:notAnInteger', ...
            [name ' value, ' num2str(x) ', is not an integer']);
    end

    % Check to see if x is between 1 and maxValue
    test = x >= 1 && x <= maxValue;
    if(~test)
        error('bfTestInRange:notInSequence', ...
            [name ' value, ' num2str(x) ', is not between 1 and ', ...
            num2str(maxValue)]);
    end
end
