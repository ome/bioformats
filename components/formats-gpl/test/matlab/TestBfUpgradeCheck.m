% Integration tests for the bfUpgradeCheck utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

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

classdef TestBfUpgradeCheck < ReaderTest
    
    properties
        upgrader
    end
    
    methods
        
        function self = TestBfUpgradeCheck(name)
            self = self@ReaderTest(name);
        end
        
        function tearDown(self)
            self.upgrader = [];
            upgrader = [];
            tearDown@ReaderTest(self);
        end
        
        function testJavaMethod(self)
            self.upgrader = javaObject('loci.formats.UpgradeChecker');
            upgrader = loci.formats.UpgradeChecker();
            assertEqual( self.upgrader.getClass, upgrader.getClass);
        end
        
    end
    
end